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
 * MSI Plessey barcode. Encodes digits.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(MSIPlesseyComponent.COMPONENT_NAME)
public class MSIPlesseyComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "MSIPlessey";

	public static final String PROPERTY_CHECK_DIGIT = "checkDigit";
	public static final String PROPERTY_CHECK_DIGIT_IN_HUMAN_READABLE_TEXT = "checkDigitInHumanReadableText";
	public static final String PROPERTY_MODULE_WIDTH_RATIO = "moduleWidthRatio";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private MSIPlesseyCheckDigitEnum checkDigit;
	private Boolean checkDigitInHumanReadableText;
	private Double moduleWidthRatio;

	public MSIPlesseyComponent()
	{
	}

	/**
	 * The check digits added to the data; when not set, none.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public MSIPlesseyCheckDigitEnum getCheckDigit()
	{
		return checkDigit;
	}

	public void setCheckDigit(MSIPlesseyCheckDigitEnum checkDigit)
	{
		Object old = this.checkDigit;
		this.checkDigit = checkDigit;
		getEventSupport().firePropertyChange(PROPERTY_CHECK_DIGIT,
				old, this.checkDigit);
	}

	/**
	 * Whether the check digits are drawn in the human-readable text; when not set, they are.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getCheckDigitInHumanReadableText()
	{
		return checkDigitInHumanReadableText;
	}

	public void setCheckDigitInHumanReadableText(Boolean checkDigitInHumanReadableText)
	{
		Object old = this.checkDigitInHumanReadableText;
		this.checkDigitInHumanReadableText = checkDigitInHumanReadableText;
		getEventSupport().firePropertyChange(PROPERTY_CHECK_DIGIT_IN_HUMAN_READABLE_TEXT,
				old, this.checkDigitInHumanReadableText);
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
}
