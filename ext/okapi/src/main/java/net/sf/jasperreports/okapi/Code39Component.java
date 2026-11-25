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
 * Code 39 barcode, also used for HIBC data. Encodes digits, upper-case letters, space and - . $ / + %.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(Code39Component.COMPONENT_NAME)
public class Code39Component extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "Code39";

	public static final String PROPERTY_MODULE_WIDTH_RATIO = "moduleWidthRatio";
	public static final String PROPERTY_CHECK_DIGIT = "checkDigit";
	public static final String PROPERTY_DATA_TYPE = "dataType";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Double moduleWidthRatio;
	private Code39CheckDigitEnum checkDigit;
	private Code39DataTypeEnum dataType;

	public Code39Component()
	{
	}

	/**
	 * The width of the wide bars as a multiple of the narrow bars; when not set, 2.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getModuleWidthRatio()
	{
		return moduleWidthRatio;
	}

	public void setModuleWidthRatio(Double moduleWidthRatio)
	{
		Object old = this.moduleWidthRatio;
		this.moduleWidthRatio = moduleWidthRatio;
		getEventSupport().firePropertyChange(PROPERTY_MODULE_WIDTH_RATIO,
				old, this.moduleWidthRatio);
	}

	/**
	 * The check digit added to the data; when not set, none.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Code39CheckDigitEnum getCheckDigit()
	{
		return checkDigit;
	}

	public void setCheckDigit(Code39CheckDigitEnum checkDigit)
	{
		Object old = this.checkDigit;
		this.checkDigit = checkDigit;
		getEventSupport().firePropertyChange(PROPERTY_CHECK_DIGIT,
				old, this.checkDigit);
	}

	/**
	 * How the content is interpreted; when not set, as plain data.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Code39DataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(Code39DataTypeEnum dataType)
	{
		Object old = this.dataType;
		this.dataType = dataType;
		getEventSupport().firePropertyChange(PROPERTY_DATA_TYPE,
				old, this.dataType);
	}
}
