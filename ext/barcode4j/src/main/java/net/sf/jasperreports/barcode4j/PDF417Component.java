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
package net.sf.jasperreports.barcode4j;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonTypeName("barcode4j:PDF417")
public class PDF417Component extends Barcode4jComponent
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_MIN_COLUMNS = "minColumns";
	public static final String PROPERTY_MAX_COLUMNS = "maxColumns";
	public static final String PROPERTY_MIN_ROWS = "minRows";
	public static final String PROPERTY_MAX_ROWS = "maxRows";
	public static final String PROPERTY_WIDTH_TO_HEIGHT_RATIO = "widthToHeightRatio";
	public static final String PROPERTY_ERROR_CORRECTION_LEVEL = "errorCorrectionLevel";
	
	private Integer minColumns;
	private Integer maxColumns;
	private Integer minRows;
	private Integer maxRows;
	private Double widthToHeightRatio;
	private Integer errorCorrectionLevel;
	
	@Override
	public void receive(BarcodeVisitor visitor)
	{
		visitor.visitPDF417(this);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Integer getMinColumns()
	{
		return minColumns;
	}

	public void setMinColumns(Integer minColumns)
	{
		Object old = this.minColumns;
		this.minColumns = minColumns;
		getEventSupport().firePropertyChange(PROPERTY_MIN_COLUMNS, 
				old, this.minColumns);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Integer getMaxColumns()
	{
		return maxColumns;
	}

	public void setMaxColumns(Integer maxColumns)
	{
		Object old = this.maxColumns;
		this.maxColumns = maxColumns;
		getEventSupport().firePropertyChange(PROPERTY_MAX_COLUMNS, 
				old, this.maxColumns);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Integer getMinRows()
	{
		return minRows;
	}

	public void setMinRows(Integer minRows)
	{
		Object old = this.minRows;
		this.minRows = minRows;
		getEventSupport().firePropertyChange(PROPERTY_MIN_ROWS, 
				old, this.minRows);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Integer getMaxRows()
	{
		return maxRows;
	}

	public void setMaxRows(Integer maxRows)
	{
		Object old = this.maxRows;
		this.maxRows = maxRows;
		getEventSupport().firePropertyChange(PROPERTY_MAX_ROWS, 
				old, this.maxRows);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Double getWidthToHeightRatio()
	{
		return widthToHeightRatio;
	}

	public void setWidthToHeightRatio(Double widthToHeightRatio)
	{
		Object old = this.widthToHeightRatio;
		this.widthToHeightRatio = widthToHeightRatio;
		getEventSupport().firePropertyChange(PROPERTY_WIDTH_TO_HEIGHT_RATIO, 
				old, this.widthToHeightRatio);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Integer getErrorCorrectionLevel()
	{
		return errorCorrectionLevel;
	}

	public void setErrorCorrectionLevel(Integer errorCorrectionLevel)
	{
		Object old = this.errorCorrectionLevel;
		this.errorCorrectionLevel = errorCorrectionLevel;
		getEventSupport().firePropertyChange(PROPERTY_ERROR_CORRECTION_LEVEL, 
				old, this.errorCorrectionLevel);
	}

}
