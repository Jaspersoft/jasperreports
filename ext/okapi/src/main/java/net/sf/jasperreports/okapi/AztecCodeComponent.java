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
 * Aztec Code barcode.
 * <p>
 * A message split over several symbols (structured append) is described by the
 * position, total and message ID expressions, evaluated for each symbol.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(AztecCodeComponent.COMPONENT_NAME)
public class AztecCodeComponent extends OkapiSizedComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "AztecCode";

	public static final String PROPERTY_MODE = "mode";
	public static final String PROPERTY_PREFERRED_SIZE = "preferredSize";
	public static final String PROPERTY_PREFERRED_ECC_PERCENTAGE = "preferredEccPercentage";
	public static final String PROPERTY_DATA_TYPE = "dataType";
	public static final String PROPERTY_ECI_MODE = "eciMode";
	public static final String PROPERTY_READER_INIT = "readerInit";
	public static final String PROPERTY_STRUCTURED_APPEND_POSITION_EXPRESSION = "structuredAppendPositionExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_TOTAL_EXPRESSION = "structuredAppendTotalExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_MESSAGE_ID_EXPRESSION = "structuredAppendMessageIdExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_FORCED = "structuredAppendForced";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private AztecCodeModeEnum mode;
	private Integer preferredSize;
	private Integer preferredEccPercentage;
	private AztecCodeDataTypeEnum dataType;
	private Integer eciMode;
	private Boolean readerInit;
	private JRExpression structuredAppendPositionExpression;
	private JRExpression structuredAppendTotalExpression;
	private JRExpression structuredAppendMessageIdExpression;
	private Boolean structuredAppendForced;

	public AztecCodeComponent()
	{
	}

	/**
	 * The symbol sizes allowed; when not set, both compact and full-range ones.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public AztecCodeModeEnum getMode()
	{
		return mode;
	}

	public void setMode(AztecCodeModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(PROPERTY_MODE,
				old, this.mode);
	}

	/**
	 * The symbol size, 1 to 36: 1 to 4 are the compact sizes, 5 to 36 the full-range ones.
	 * When not set, the smallest size that holds the data with its error correction.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredSize()
	{
		return preferredSize;
	}

	public void setPreferredSize(Integer preferredSize)
	{
		Object old = this.preferredSize;
		this.preferredSize = preferredSize;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_SIZE,
				old, this.preferredSize);
	}

	/**
	 * The share of the symbol, 1 to 99 percent, used for error correction, beyond the
	 * 3 codewords always used; when not set, 23. It has no effect when the size is set.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredEccPercentage()
	{
		return preferredEccPercentage;
	}

	public void setPreferredEccPercentage(Integer preferredEccPercentage)
	{
		Object old = this.preferredEccPercentage;
		this.preferredEccPercentage = preferredEccPercentage;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_ECC_PERCENTAGE,
				old, this.preferredEccPercentage);
	}

	/**
	 * How the content is interpreted; when not set, as plain data, in the character set of {@link #getEciMode()}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public AztecCodeDataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(AztecCodeDataTypeEnum dataType)
	{
		Object old = this.dataType;
		this.dataType = dataType;
		getEventSupport().firePropertyChange(PROPERTY_DATA_TYPE,
				old, this.dataType);
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

	/**
	 * The position (1 to 26) of this symbol in a structured append series.
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
	 * The number of symbols (2 to 26) in a structured append series.
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
	 * The message ID of a structured append series, the same in every symbol of the series:
	 * printable ASCII characters, with no spaces. When not set, none.
	 */
	public JRExpression getStructuredAppendMessageIdExpression()
	{
		return structuredAppendMessageIdExpression;
	}

	public void setStructuredAppendMessageIdExpression(JRExpression structuredAppendMessageIdExpression)
	{
		Object old = this.structuredAppendMessageIdExpression;
		this.structuredAppendMessageIdExpression = structuredAppendMessageIdExpression;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_MESSAGE_ID_EXPRESSION,
				old, this.structuredAppendMessageIdExpression);
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
		AztecCodeComponent clone = (AztecCodeComponent) super.clone();
		clone.structuredAppendPositionExpression = JRCloneUtils.nullSafeClone(structuredAppendPositionExpression);
		clone.structuredAppendTotalExpression = JRCloneUtils.nullSafeClone(structuredAppendTotalExpression);
		clone.structuredAppendMessageIdExpression = JRCloneUtils.nullSafeClone(structuredAppendMessageIdExpression);
		return clone;
	}
}
