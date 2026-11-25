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
 * Code 93 barcode. Encodes the full ASCII character set; the two check characters are added.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(Code93Component.COMPONENT_NAME)
public class Code93Component extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "Code93";

	public static final String PROPERTY_SHOW_CHECK_DIGITS = "showCheckDigits";
	public static final String PROPERTY_START_STOP_DELIMITER = "startStopDelimiter";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Boolean showCheckDigits;
	private Character startStopDelimiter;

	public Code93Component()
	{
	}

	/**
	 * Whether the check characters are drawn in the human-readable text; when not set, they are.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getShowCheckDigits()
	{
		return showCheckDigits;
	}

	public void setShowCheckDigits(Boolean showCheckDigits)
	{
		Object old = this.showCheckDigits;
		this.showCheckDigits = showCheckDigits;
		getEventSupport().firePropertyChange(PROPERTY_SHOW_CHECK_DIGITS,
				old, this.showCheckDigits);
	}

	/**
	 * A character drawn before and after the data in the human-readable text; when not set, none.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Character getStartStopDelimiter()
	{
		return startStopDelimiter;
	}

	public void setStartStopDelimiter(Character startStopDelimiter)
	{
		Object old = this.startStopDelimiter;
		this.startStopDelimiter = startStopDelimiter;
		getEventSupport().firePropertyChange(PROPERTY_START_STOP_DELIMITER,
				old, this.startStopDelimiter);
	}
}
