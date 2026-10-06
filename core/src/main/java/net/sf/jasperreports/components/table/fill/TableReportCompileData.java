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

import java.io.Serializable;
import java.util.Map;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRReportCompileData;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableReportCompileData extends JRReportCompileData
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private final JasperReport originialReport;

	public TableReportCompileData(JasperReport originialReport)
	{
		this.originialReport = originialReport;
	}
	
	@Override
	public String getUnitName(JasperReport jasperReport, JRDataset dataset)
	{
		String unitName;
		if (dataset.isMainDataset())
		{
			unitName = super.getUnitName(jasperReport, dataset);
		}
		else
		{
			unitName = super.getUnitName(originialReport, dataset);
		}
		return unitName;
	}

	@Override
	public String getUnitName(JasperReport jasperReport, JRCrosstab crosstab)
	{
		return super.getUnitName(originialReport, crosstab);
	}

	public void copyCrosstabCompileData(JRReportCompileData compileData)
	{
		Map<Integer, Serializable> crosstabCompileData = compileData.getCrosstabsCompileData();
		if (crosstabCompileData != null)
		{
			for (Map.Entry<Integer, Serializable> entry : crosstabCompileData.entrySet())
			{
				setCrosstabCompileData(entry.getKey(), entry.getValue());
			}
		}
	}
}
