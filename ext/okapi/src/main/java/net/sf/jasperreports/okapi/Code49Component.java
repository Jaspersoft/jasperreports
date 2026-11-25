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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;


/**
 * Code 49 barcode, stacked rows. Encodes any ASCII text; GS1 data.
 * <p>
 * Its size is fixed: there is no module width. It fills the element, keeping its shape.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(Code49Component.COMPONENT_NAME)
public class Code49Component extends OkapiComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "Code49";

	public static final String PROPERTY_DATA_TYPE = "dataType";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Code49DataTypeEnum dataType;

	public Code49Component()
	{
	}

	/**
	 * How the content is interpreted; when not set, as plain data.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Code49DataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(Code49DataTypeEnum dataType)
	{
		Object old = this.dataType;
		this.dataType = dataType;
		getEventSupport().firePropertyChange(PROPERTY_DATA_TYPE,
				old, this.dataType);
	}
}
