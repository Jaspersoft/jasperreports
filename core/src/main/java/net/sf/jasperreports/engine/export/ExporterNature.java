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

/*
 * Contributors:
 * Greg Hilton 
 */

package net.sf.jasperreports.engine.export;

import java.util.Map;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface ExporterNature extends ExporterFilter
{

	/**
	 * Specified whether to include in the grid sub elements of a given {@link JRPrintFrame frame} element.
	 */
	public abstract boolean isDeep(JRPrintFrame frame);

	/**
	 * Specifies whether the exporter handles cells span
	 */
	public abstract boolean isSpanCells();

	public abstract boolean isIgnoreLastRow();
	
	/**
	 * Specifies whether empty page margins should be ignored
	 */
	public abstract boolean isIgnorePageMargins();

	/**
	 *
	 */
	public boolean isBreakBeforeRow(JRPrintElement element);

	/**
	 *
	 */
	public boolean isBreakAfterRow(JRPrintElement element);

	/**
	 * Flag that specifies that empty cells are to be horizontally merged.
	 * <p>
	 * If the flag is set and this nature is {@link #isDeep(JRPrintFrame) deep}, the nature is required
	 * to {@link #isToExport(JRPrintElement) export} {@link JRPrintFrame frames}.
	 * </p>
	 * 
	 * @return whether empty cells are to be horizontally merged
	 */
	public boolean isHorizontallyMergeEmptyCells();
	
	public void setXProperties(CutsInfo xCuts, JRPrintElement element, int row1, int col1, int row2, int col2);
	
	public void setXProperties(Map<String,Object> xCutsProperties, JRPrintElement element);
	
	public void setYProperties(CutsInfo yCuts, JRPrintElement element, int row1, int col1, int row2, int col2);
	
	public void setYProperties(Map<String,Object> yCutsProperties, JRPrintElement element);
}
