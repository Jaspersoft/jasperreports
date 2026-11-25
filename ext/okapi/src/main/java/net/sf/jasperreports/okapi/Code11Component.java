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
 * Code 11 barcode. Encodes digits and the dash.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(Code11Component.COMPONENT_NAME)
public class Code11Component extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "Code11";

	public static final String PROPERTY_MODULE_WIDTH_RATIO = "moduleWidthRatio";
	public static final String PROPERTY_CHECK_DIGIT_COUNT = "checkDigitCount";
	public static final String PROPERTY_START_DELIMITER = "startDelimiter";
	public static final String PROPERTY_STOP_DELIMITER = "stopDelimiter";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Double moduleWidthRatio;
	private Integer checkDigitCount;
	private Character startDelimiter;
	private Character stopDelimiter;

	public Code11Component()
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
	 * The number of check digits added to the data, 1 or 2; when not set, 2.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getCheckDigitCount()
	{
		return checkDigitCount;
	}

	public void setCheckDigitCount(Integer checkDigitCount)
	{
		Object old = this.checkDigitCount;
		this.checkDigitCount = checkDigitCount;
		getEventSupport().firePropertyChange(PROPERTY_CHECK_DIGIT_COUNT,
				old, this.checkDigitCount);
	}

	/**
	 * A character drawn before the data in the human-readable text; when not set, none.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Character getStartDelimiter()
	{
		return startDelimiter;
	}

	public void setStartDelimiter(Character startDelimiter)
	{
		Object old = this.startDelimiter;
		this.startDelimiter = startDelimiter;
		getEventSupport().firePropertyChange(PROPERTY_START_DELIMITER,
				old, this.startDelimiter);
	}

	/**
	 * A character drawn after the data in the human-readable text; when not set, none.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Character getStopDelimiter()
	{
		return stopDelimiter;
	}

	public void setStopDelimiter(Character stopDelimiter)
	{
		Object old = this.stopDelimiter;
		this.stopDelimiter = stopDelimiter;
		getEventSupport().firePropertyChange(PROPERTY_STOP_DELIMITER,
				old, this.stopDelimiter);
	}
}
