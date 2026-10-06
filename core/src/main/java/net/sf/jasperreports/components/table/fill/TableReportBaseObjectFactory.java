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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.base.JRBaseGroup;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableReportBaseObjectFactory extends JRBaseObjectFactory
{

	private Map<JRGroup, TableReportGroup> tableGroupMap = new HashMap<>();
	
	public TableReportBaseObjectFactory(TableReportDataset reportDataset)
	{
		super((JRExpressionCollector) null);
		
		TableReportGroup[] tableGroups = reportDataset.getTableGroups();
		if (tableGroups != null)
		{
			for (TableReportGroup tableReportGroup : tableGroups)
			{
				tableGroupMap.put(tableReportGroup.getOriginalGroup(), tableReportGroup);
			}
		}
	}
	
	@Override
	public JRExpression getExpression(JRExpression expression,
			boolean assignNotUsedId)
	{
		// same expressions are used in the table report
		return expression;
	}
	
	@Override
	public JRBaseGroup getGroup(JRGroup group)
	{
		JRGroup origGroup;
		if (tableGroupMap.containsKey(group))
		{
			origGroup = tableGroupMap.get(group);
		}
		else
		{
			origGroup = group;
		}
		
		return super.getGroup(origGroup);
	}

	@Override
	protected int resolveCrosstabId(JRCrosstab crosstab)
	{
		// crosstab Ids are already assigned
		return crosstab.getId();
	}
}
