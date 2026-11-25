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
 * QR Code barcode.
 * <p>
 * A message split over several symbols (structured append) is described by the
 * position, total and parity expressions, evaluated for each symbol.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(QRCodeComponent.COMPONENT_NAME)
public class QRCodeComponent extends OkapiSizedComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "QRCode";

	public static final String PROPERTY_PREFERRED_VERSION = "preferredVersion";
	public static final String PROPERTY_PREFERRED_ECC_LEVEL = "preferredEccLevel";
	public static final String PROPERTY_FORCE_BYTE_COMPACTION = "forceByteCompaction";
	public static final String PROPERTY_DATA_TYPE = "dataType";
	public static final String PROPERTY_ECI_MODE = "eciMode";
	public static final String PROPERTY_STRUCTURED_APPEND_POSITION_EXPRESSION = "structuredAppendPositionExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_TOTAL_EXPRESSION = "structuredAppendTotalExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_PARITY_EXPRESSION = "structuredAppendParityExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_FORCED = "structuredAppendForced";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Integer preferredVersion;
	private QRCodeEccLevelEnum preferredEccLevel;
	private Boolean forceByteCompaction;
	private QRCodeDataTypeEnum dataType;
	private Integer eciMode;
	private JRExpression structuredAppendPositionExpression;
	private JRExpression structuredAppendTotalExpression;
	private JRExpression structuredAppendParityExpression;
	private Boolean structuredAppendForced;

	public QRCodeComponent()
	{
	}

	/**
	 * The smallest symbol version (1 to 40) to use; a larger one is used when the data needs it.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredVersion()
	{
		return preferredVersion;
	}

	public void setPreferredVersion(Integer preferredVersion)
	{
		Object old = this.preferredVersion;
		this.preferredVersion = preferredVersion;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_VERSION,
				old, this.preferredVersion);
	}

	/**
	 * The lowest error correction level to use; a higher one is used when it fits the same symbol size.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public QRCodeEccLevelEnum getPreferredEccLevel()
	{
		return preferredEccLevel;
	}

	public void setPreferredEccLevel(QRCodeEccLevelEnum preferredEccLevel)
	{
		Object old = this.preferredEccLevel;
		this.preferredEccLevel = preferredEccLevel;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_ECC_LEVEL,
				old, this.preferredEccLevel);
	}

	/**
	 * Whether all the data is encoded in byte mode.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getForceByteCompaction()
	{
		return forceByteCompaction;
	}

	public void setForceByteCompaction(Boolean forceByteCompaction)
	{
		Object old = this.forceByteCompaction;
		this.forceByteCompaction = forceByteCompaction;
		getEventSupport().firePropertyChange(PROPERTY_FORCE_BYTE_COMPACTION,
				old, this.forceByteCompaction);
	}

	/**
	 * How the content is interpreted; when not set, as plain data, in the character set of {@link #getEciMode()}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public QRCodeDataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(QRCodeDataTypeEnum dataType)
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
	 * The position (1 to 16) of this symbol in a structured append series.
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
	 * The number of symbols (2 to 16) in a structured append series.
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
	 * The parity (0 to 255) of the whole message of a structured append series,
	 * the same in every symbol of the series.
	 */
	public JRExpression getStructuredAppendParityExpression()
	{
		return structuredAppendParityExpression;
	}

	public void setStructuredAppendParityExpression(JRExpression structuredAppendParityExpression)
	{
		Object old = this.structuredAppendParityExpression;
		this.structuredAppendParityExpression = structuredAppendParityExpression;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_PARITY_EXPRESSION,
				old, this.structuredAppendParityExpression);
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
		QRCodeComponent clone = (QRCodeComponent) super.clone();
		clone.structuredAppendPositionExpression = JRCloneUtils.nullSafeClone(structuredAppendPositionExpression);
		clone.structuredAppendTotalExpression = JRCloneUtils.nullSafeClone(structuredAppendTotalExpression);
		clone.structuredAppendParityExpression = JRCloneUtils.nullSafeClone(structuredAppendParityExpression);
		return clone;
	}
}
