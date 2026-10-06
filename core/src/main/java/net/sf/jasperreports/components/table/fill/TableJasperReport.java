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

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableJasperReport extends JasperReport
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private final JasperReport parentReport;
	private final TableReport tableReport;

	public TableJasperReport(JasperReport parentReport, TableReport baseReport, 
			Serializable compileData, JRBaseObjectFactory factory,
			String compileNameSuffix)
	{
		super(baseReport, parentReport.getCompilerClass(), compileData, factory, compileNameSuffix);
		
		this.parentReport = parentReport;
		this.tableReport = baseReport;
	}

	public JasperReport getParentReport()
	{
		return parentReport;
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		// the subreport created for the table inherits all properties from the containing report
		return parentReport;
	}

	public TableReport getBaseReport()
	{
		return tableReport;
	}

}
