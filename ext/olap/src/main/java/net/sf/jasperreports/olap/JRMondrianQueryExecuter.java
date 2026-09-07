/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.olap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mondrian.olap.Connection;
import mondrian.olap.Query;
import mondrian.olap.Result;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.sf.jasperreports.olap.mondrian.JRMondrianResult;
import net.sf.jasperreports.olap.result.JROlapHierarchy;
import net.sf.jasperreports.olap.result.JROlapHierarchyLevel;
import net.sf.jasperreports.olap.result.JROlapMember;
import net.sf.jasperreports.olap.result.JROlapMemberTuple;
import net.sf.jasperreports.olap.result.JROlapResultAxis;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRMondrianQueryExecuter extends JRAbstractQueryExecuter
{
	private static final Log log = LogFactory.getLog(JRMondrianQueryExecuter.class);
	
	private Connection connection;
	private Result result;

	/**
	 * 
	 */
	public JRMondrianQueryExecuter(
		JasperReportsContext jasperReportsContext, 
		JRDataset dataset, 
		Map<String,? extends JRValueParameter> parametersMap
		)
	{
		super(jasperReportsContext, dataset, parametersMap);
		
		connection = (Connection) getParameterValue(JRMondrianQueryExecuterFactory.PARAMETER_MONDRIAN_CONNECTION);

		if (connection == null)
		{
			log.warn("The supplied mondrian.olap.Connection object is null.");
		}
		
		parseQuery();
	}

	@Override
	protected String getCanonicalQueryLanguage()
	{
		return JRMdxQueryExecuterFactory.CANONICAL_LANGUAGE;
	}

	@Override
	protected String getParameterReplacement(String parameterName)
	{
		return String.valueOf(getParameterValue(parameterName));
	}

	@Override
	public JRDataSource createDatasource() throws JRException
	{
		JRDataSource dataSource = null;
		
		String queryStr = getQueryString();
		if (connection != null && queryStr != null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("MDX query: " + queryStr);
			}
			
			Query query = connection.parseQuery(queryStr);
			result = connection.execute(query);
			logResult();
			
			dataSource = new JRMondrianDataSource(dataset, result);
		}

		return dataSource;
	}

	@Override
	public void close()
	{
		if (result != null)
		{
			result.close();
			result = null;
		}
	}

	@Override
	public boolean cancelQuery() throws JRException
	{
		return false;
	}

	public Result getResult()
	{
		return result;
	}

	protected void logResult()
	{
		if (!log.isDebugEnabled())//FIXME use a separate logging category
		{
			return;
		}
		
		JRMondrianResult monResult = new JRMondrianResult(result);
		Set<String> measureNames = new HashSet<>();
		List<List<String>> allLevelExpressions = new ArrayList<>();
		int axisCount = 0;
		int levelCount = 0;
		for (JROlapResultAxis axis : monResult.getAxes())
		{
			log.debug("Axis: " + axisCount);
			String prefix = (axisCount == 0) ? "Columns" : "Rows";
			
			for (JROlapHierarchy hier: axis.getHierarchiesOnAxis())
			{
				log.debug("\t Hierarchy: " + hier.getDimensionName() + " - " + hier.getHierarchyUniqueName());
				log.debug("\t\tLevels");
				
				/*
				 * create a "Rows/Columns" for each level - but not Measures
				 */
				boolean foundMeasuresLevel = false;
				boolean emittedLevel = false;
				for (JROlapHierarchyLevel level : hier.getLevels())
				{
					log.debug("\t\t\t" + (level == null ? "null" : level.getName() + ", depth: " + level.getDepth()));
					if (level != null)
					{
						if (level.getName().equalsIgnoreCase("MeasuresLevel"))
						{
							foundMeasuresLevel = true;
							//log.info("\t\t\t\t" + hier.getHierarchyUniqueName().replace(" ", "") + level.getName().replace(" ", "") + "- Data(" + hier.getHierarchyUniqueName() + "[" + level.getName() + "],?)");
						} 
						else
						{
							emittedLevel = true;
							String rowsExpression = prefix + makeOlapExpression(hier.getDimensionName()) + "[" + level.getName() + "]";
							List<String> thisLevelExpressions = null;
							if (levelCount >= allLevelExpressions.size())
							{
								thisLevelExpressions = new ArrayList<>();
								allLevelExpressions.add(thisLevelExpressions);
							} 
							else
							{
								thisLevelExpressions = allLevelExpressions.get(levelCount);
							}
							thisLevelExpressions.add(rowsExpression);
							log.debug("\t\t\t\t" + makeJRFieldName(hier.getDimensionName() + level.getName()) + "- " + rowsExpression);
						}
					}
				}
				
				if (emittedLevel)
				{
					levelCount++;
				}
				
				/*
				 * Pick up the measure names
				 */
				
				for (int i = 0; i < axis.getTupleCount(); i++)
				{
					JROlapMemberTuple memberTuple = axis.getTuple(i);
					for (JROlapMember member : memberTuple.getMembers())
					{
						if (
							foundMeasuresLevel 
							&& isMeasureMember(member.getUniqueName()) 
							&& !measureNames.contains(member.getUniqueName())
							) 
						{
							measureNames.add(member.getUniqueName());
						}
					}
				}
			}
			axisCount++;
		}
		
		for (String measureName : measureNames)
		{
				log.debug("\t\t\t\t" + makeJRFieldName(measureName) 
						+ "- Data(" + measureName + ",?)");
		}

		/*
		for (List<String> allLevelsExpression : allLevelExpressions) {
			for (String measureName : measureNames) {
					log.info("\t\t\t\t" + makeJRFieldName(rowExpression + measureName) + "- Data(" + measureName + ",?)");
					//hier.getHierarchyUniqueName() + "[" + member.getName() + "]
					//log.info("\t\t\t" + sb.toString());
			}
		}
		*/
	}
	
	private String makeJRFieldName(String s)
	{
		String out = s.replace(" ", "");
		out = out.replace("[", "");
		out = out.replace("]", "");
		out = out.replace("(", "");
		out = out.replace(")", "");
		out = out.replace(".", "");
		return out;
	}
	
	private String makeOlapExpression(String s)
	{
		/*
		 * Could be:
		 * - "hierarchy name"
		 * - "[hierarchy name]"
		 * - "[dimension].[hierarchy name]"
		 * 
		 * want "[hierarchy name]"
		 */
		String out = s.trim();
		int pos = out.indexOf("].[");
		if (pos != -1)
		{
			out = out.substring(pos + 2);
		}
		if (out.charAt(0) != '[') 
		{
			out = "[" + out;
		}
		if (out.charAt(out.length() - 1) != ']') 
		{
			out = out + "]";
		}
		return out;
	}

	private boolean isMeasureMember(String uniqueName) 
	{
		/*
		 * should be "[Measures].[measure name]"
		 */
		return uniqueName.startsWith("[Measures].");
	}
}
