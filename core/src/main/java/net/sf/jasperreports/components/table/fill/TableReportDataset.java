/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.components.table.fill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.sf.jasperreports.engine.DatasetPropertyExpression;
import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignScriptlet;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableReportDataset implements JRDataset
{

	private final JRDataset tableSubdataset;
	private final String name;
	private final TableReportGroup[] tableGroups;
	private final List<JRGroup> groups;
	private final JRPropertiesMap properties;
	
	private final List<JRScriptlet> scriptlets;
	private final List<JRParameter> parameters;
	
	public TableReportDataset(JRDataset tableSubdataset, String name)
	{
		this.tableSubdataset = tableSubdataset;
		this.name = name;
		
		JRGroup[] datasetGroups = tableSubdataset.getGroups();
		groups = new ArrayList<>();
		if (datasetGroups == null)
		{
			tableGroups = null;
		}
		else
		{
			tableGroups = new TableReportGroup[datasetGroups.length];
			for (int i = 0; i < datasetGroups.length; i++)
			{
				tableGroups[i] = new TableReportGroup(datasetGroups[i]);
				groups.add(tableGroups[i]);
			}
		}
		
		properties = tableSubdataset.getPropertiesMap().cloneProperties();
		
		scriptlets = new ArrayList<>();
		JRScriptlet[] datasetScriptlets = tableSubdataset.getScriptlets();
		if (datasetScriptlets != null)
		{
			Collections.addAll(scriptlets, datasetScriptlets);
		}
		
		JRParameter[] datasetParameters = tableSubdataset.getParameters();
		parameters = new ArrayList<>();
		if (datasetParameters != null)
		{
			Collections.addAll(parameters, datasetParameters);
		}
	}
	
	@Override
	public JRField[] getFields()
	{
		return tableSubdataset.getFields();
	}

	@Override
	public JRExpression getFilterExpression()
	{
		return tableSubdataset.getFilterExpression();
	}
	
	public TableReportGroup[] getTableGroups()
	{
		return tableGroups;
	}

	@Override
	public JRGroup[] getGroups()
	{
		return groups.toArray(new JRGroup[groups.size()]);
	}
	
	public void addFirstGroup(JRGroup group)
	{
		groups.add(0, group);
	}

	@Override
	public UUID getUUID()
	{
		return tableSubdataset.getUUID();
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public JRParameter[] getParameters()
	{
		return parameters.toArray(new JRParameter[parameters.size()]);
	}

	@Override
	public JRQuery getQuery()
	{
		return tableSubdataset.getQuery();
	}

	@Override
	public String getResourceBundle()
	{
		return tableSubdataset.getResourceBundle();
	}

	@Override
	public String getScriptletClass()
	{
		return tableSubdataset.getScriptletClass();
	}

	@Override
	public JRScriptlet[] getScriptlets()
	{
		return scriptlets.toArray(new JRScriptlet[scriptlets.size()]);
	}

	@Override
	public JRSortField[] getSortFields()
	{
		return tableSubdataset.getSortFields();
	}

	@Override
	public JRVariable[] getVariables()
	{
		return tableSubdataset.getVariables();
	}

	@Override
	public WhenResourceMissingTypeEnum getWhenResourceMissingType()
	{
		return tableSubdataset.getWhenResourceMissingType();
	}

	@Override
	public boolean isMainDataset()
	{
		// used as main dataset
		return true;
	}

	@Override
	public void setWhenResourceMissingType(
			WhenResourceMissingTypeEnum whenResourceMissingType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return tableSubdataset.getParentProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return properties;
	}

	@Override
	public boolean hasProperties()
	{
		return properties.hasProperties();
	}

	@Override
	public DatasetPropertyExpression[] getPropertyExpressions()
	{
		return tableSubdataset.getPropertyExpressions();
	}
	
	@Override
	public Object clone()
	{
		throw new UnsupportedOperationException();
	}
	
	public void addScriptlet(String name, Class<? extends JRAbstractScriptlet> type)
	{
		JRDesignScriptlet scriptlet = new JRDesignScriptlet();
		scriptlet.setName(name);
		scriptlet.setValueClass(type);
		
		JRDesignParameter parameter = new JRDesignParameter();
		parameter.setName(name + JRScriptlet.SCRIPTLET_PARAMETER_NAME_SUFFIX);
		parameter.setValueClass(scriptlet.getValueClass());
		parameter.setSystemDefined(true);
		parameter.setForPrompting(false);
		
		scriptlets.add(scriptlet);
		parameters.add(parameter);
	}
	
}
