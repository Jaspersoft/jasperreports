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
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * MaxiCode barcode, of a fixed size, about 28 x 27 mm in ISO/IEC 16023.
 * <p>
 * It always fills the element, keeping its shape: there is no module width.
 * The structured carrier modes take the postal code, country and service in the primary expression.
 * </p>
 * <p>
 * A message split over several symbols (structured append) is described by the
 * position and total expressions, evaluated for each symbol.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(MaxiCodeComponent.COMPONENT_NAME)
public class MaxiCodeComponent extends OkapiComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "MaxiCode";

	public static final String PROPERTY_MODE = "mode";
	public static final String PROPERTY_PRIMARY_EXPRESSION = "primaryExpression";
	public static final String PROPERTY_ECI_MODE = "eciMode";
	public static final String PROPERTY_STRUCTURED_APPEND_POSITION_EXPRESSION = "structuredAppendPositionExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_TOTAL_EXPRESSION = "structuredAppendTotalExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_FORCED = "structuredAppendForced";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private MaxiCodeModeEnum mode;
	private JRExpression primaryExpression;
	private Integer eciMode;
	private JRExpression structuredAppendPositionExpression;
	private JRExpression structuredAppendTotalExpression;
	private Boolean structuredAppendForced;

	public MaxiCodeComponent()
	{
	}

	/**
	 * The MaxiCode mode; when not set, {@link MaxiCodeModeEnum#MODE4 mode 4}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public MaxiCodeModeEnum getMode()
	{
		return mode;
	}

	public void setMode(MaxiCodeModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(PROPERTY_MODE,
				old, this.mode);
	}

	/**
	 * The primary message of the structured carrier modes 2 and 3, 15 characters: the postal code
	 * (9 digits in mode 2, 6 characters in mode 3, padded with spaces), the 3 digit ISO 3166 country code
	 * and the 3 digit service code. It is required in modes 2 and 3, and only allowed in them.
	 */
	public JRExpression getPrimaryExpression()
	{
		return primaryExpression;
	}

	public void setPrimaryExpression(JRExpression primaryExpression)
	{
		Object old = this.primaryExpression;
		this.primaryExpression = primaryExpression;
		getEventSupport().firePropertyChange(PROPERTY_PRIMARY_EXPRESSION,
				old, this.primaryExpression);
	}

	/**
	 * The ECI mode, which names the character set of the content; when not set, Okapi chooses one.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getEciMode()
	{
		return eciMode;
	}

	public void setEciMode(Integer eciMode)
	{
		Object old = this.eciMode;
		this.eciMode = eciMode;
		getEventSupport().firePropertyChange(PROPERTY_ECI_MODE,
				old, this.eciMode);
	}

	/**
	 * The position (1 to 8) of this symbol in a structured append series.
	 */
	public JRExpression getStructuredAppendPositionExpression()
	{
		return structuredAppendPositionExpression;
	}

	public void setStructuredAppendPositionExpression(JRExpression structuredAppendPositionExpression)
	{
		Object old = this.structuredAppendPositionExpression;
		this.structuredAppendPositionExpression = structuredAppendPositionExpression;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_POSITION_EXPRESSION,
				old, this.structuredAppendPositionExpression);
	}

	/**
	 * The number of symbols (2 to 8) in a structured append series.
	 */
	public JRExpression getStructuredAppendTotalExpression()
	{
		return structuredAppendTotalExpression;
	}

	public void setStructuredAppendTotalExpression(JRExpression structuredAppendTotalExpression)
	{
		Object old = this.structuredAppendTotalExpression;
		this.structuredAppendTotalExpression = structuredAppendTotalExpression;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_TOTAL_EXPRESSION,
				old, this.structuredAppendTotalExpression);
	}

	/**
	 * Whether the structured append header is written even for a series of one symbol.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getStructuredAppendForced()
	{
		return structuredAppendForced;
	}

	public void setStructuredAppendForced(Boolean structuredAppendForced)
	{
		Object old = this.structuredAppendForced;
		this.structuredAppendForced = structuredAppendForced;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_FORCED,
				old, this.structuredAppendForced);
	}

	@Override
	public Object clone()
	{
		MaxiCodeComponent clone = (MaxiCodeComponent) super.clone();
		clone.primaryExpression = JRCloneUtils.nullSafeClone(primaryExpression);
		clone.structuredAppendPositionExpression = JRCloneUtils.nullSafeClone(structuredAppendPositionExpression);
		clone.structuredAppendTotalExpression = JRCloneUtils.nullSafeClone(structuredAppendTotalExpression);
		return clone;
	}
}
