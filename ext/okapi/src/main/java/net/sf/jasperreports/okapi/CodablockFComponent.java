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
package net.sf.jasperreports.okapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;


/**
 * Codablock F barcode, stacked rows of Code 128. Encodes any ASCII text; GS1 and HIBC data.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(CodablockFComponent.COMPONENT_NAME)
public class CodablockFComponent extends OkapiSizedComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "CodablockF";

	public static final String PROPERTY_BAR_HEIGHT = "barHeight";
	public static final String PROPERTY_DATA_TYPE = "dataType";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Double barHeight;
	private CodablockFDataTypeEnum dataType;

	public CodablockFComponent()
	{
	}

	/**
	 * The height of each row, in report units, rounded to whole modules. When set, the rows are never
	 * taller; they are lower when the element height doesn't fit them, but never below the minimum of AIM ISS-X-24, 8 modules or more for wide symbols,
	 * the image then shrinks the symbol. When not set, the rows share the element height, with the same minimum.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getBarHeight()
	{
		return barHeight;
	}

	public void setBarHeight(Double barHeight)
	{
		Object old = this.barHeight;
		this.barHeight = barHeight;
		getEventSupport().firePropertyChange(PROPERTY_BAR_HEIGHT,
				old, this.barHeight);
	}

	/**
	 * How the content is interpreted; when not set, as plain data.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public CodablockFDataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(CodablockFDataTypeEnum dataType)
	{
		Object old = this.dataType;
		this.dataType = dataType;
		getEventSupport().firePropertyChange(PROPERTY_DATA_TYPE,
				old, this.dataType);
	}
}
