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
 * Code 16K barcode, stacked rows of Code 128 characters. Encodes any ASCII text; GS1 data.
 * <p>
 * Its size is fixed: there is no module width. It fills the element, keeping its shape.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(Code16kComponent.COMPONENT_NAME)
public class Code16kComponent extends OkapiComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "Code16k";

	public static final String PROPERTY_DATA_TYPE = "dataType";
	public static final String PROPERTY_READER_INIT = "readerInit";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Code16kDataTypeEnum dataType;
	private Boolean readerInit;

	public Code16kComponent()
	{
	}

	/**
	 * How the content is interpreted; when not set, as plain data.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Code16kDataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(Code16kDataTypeEnum dataType)
	{
		Object old = this.dataType;
		this.dataType = dataType;
		getEventSupport().firePropertyChange(PROPERTY_DATA_TYPE,
				old, this.dataType);
	}

	/**
	 * Whether the symbol is marked as a reader programming symbol.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getReaderInit()
	{
		return readerInit;
	}

	public void setReaderInit(Boolean readerInit)
	{
		Object old = this.readerInit;
		this.readerInit = readerInit;
		getEventSupport().firePropertyChange(PROPERTY_READER_INIT,
				old, this.readerInit);
	}
}
