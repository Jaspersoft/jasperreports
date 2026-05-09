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
package net.sf.jasperreports.crosstabs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;
import net.sf.jasperreports.engine.JRCloneable;

/**
 * Crosstab data cell interface.
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see net.sf.jasperreports.crosstabs.JRCrosstab#getCells()
 */
@JsonPropertyOrder({
	"width",
	"height",
	"rowTotalGroup",
	"columnTotalGroup",
	"contents"
	})
@JsonDeserialize(as = JRDesignCrosstabCell.class)
public interface JRCrosstabCell extends JRCloneable
{
	/**
	 * Returns the width of the cell.
	 * <p>
	 * The width of the cell can be unspecified.
	 * The width used for the cell is computed base on the rules described
	 * {@link net.sf.jasperreports.crosstabs.JRCrosstab#getCells() here}.
	 * 
	 * @return the width of the cell
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Integer getWidth();
	
	
	/**
	 * Returns the height of the cell.
	 * <p>
	 * The height of the cell can be unspecified.
	 * The height used for the cell is computed base on the rules described
	 * {@link net.sf.jasperreports.crosstabs.JRCrosstab#getCells() here}.
	 * 
	 * @return the width of the cell
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Integer getHeight();
	
	
	/**
	 * Returns the name of the row group if the crosstab cell corresponds to a total row
	 * and <code>null</code> otherwise.
	 * 
	 * @return the name of the total row group this cell corresponds to
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getRowTotalGroup();
	
	
	/**
	 * Returns the name of the column group if the crosstab cell corresponds to a total column
	 * and <code>null</code> otherwise.
	 * 
	 * @return the name of the total column group this cell corresponds to
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getColumnTotalGroup();
	
	
	/**
	 * Returns the cell contents.
	 * <p>
	 * Should never return null, but empty cell contents instead.
	 * 
	 * @return the cell contents
	 */
	public JRCellContents getContents();
}
