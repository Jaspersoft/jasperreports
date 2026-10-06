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
package net.sf.jasperreports.poi.components.iconlabel;

import net.sf.jasperreports.components.iconlabel.IconLabelElement;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.poi.export.GenericElementXlsHandler;
import net.sf.jasperreports.poi.export.JRXlsExporter;
import net.sf.jasperreports.poi.export.JRXlsExporterContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class IconLabelElementXlsHandler implements GenericElementXlsHandler
{
	private static final IconLabelElementXlsHandler INSTANCE = new IconLabelElementXlsHandler();
	
	public static IconLabelElementXlsHandler getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void exportElement(
		JRXlsExporterContext exporterContext,
		JRGenericPrintElement element, JRExporterGridCell gridCell,
		int colIndex, int rowIndex, int emptyCols, int yCutsRow,
		JRGridLayout layout
		)
	{
		JRPrintText labelPrintText = (JRPrintText)element.getParameterValue(IconLabelElement.PARAMETER_LABEL_TEXT_ELEMENT);
		if (labelPrintText != null)
		{
			try
			{
				JRXlsExporter exporter = (JRXlsExporter)exporterContext.getExporterRef();
				exporter.exportText(labelPrintText, gridCell, colIndex, rowIndex);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public boolean toExport(JRGenericPrintElement element) 
	{
		return true;
	}
	
}
