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
 * Interleaved 2 of 5 barcode. Digits are encoded in pairs, so a leading zero is added
 * when their number, with the check digit, is odd.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(Interleaved2Of5Component.COMPONENT_NAME)
public class Interleaved2Of5Component extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "Interleaved2Of5";

	public static final String PROPERTY_MODULE_WIDTH_RATIO = "moduleWidthRatio";
	public static final String PROPERTY_CHECK_DIGIT = "checkDigit";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Double moduleWidthRatio;
	private Interleaved2Of5CheckDigitEnum checkDigit;

	public Interleaved2Of5Component()
	{
	}

	/**
	 * The width of the wide bars as a multiple of the narrow bars; when not set, 3.
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
	public Interleaved2Of5CheckDigitEnum getCheckDigit()
	{
		return checkDigit;
	}

	public void setCheckDigit(Interleaved2Of5CheckDigitEnum checkDigit)
	{
		Object old = this.checkDigit;
		this.checkDigit = checkDigit;
		getEventSupport().firePropertyChange(PROPERTY_CHECK_DIGIT,
				old, this.checkDigit);
	}
}
