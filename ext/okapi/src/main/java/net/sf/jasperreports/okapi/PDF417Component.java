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
 * PDF417 barcode, also truncated, and MicroPDF417.
 * <p>
 * A message split over several symbols (Macro PDF417) is described by the position, total,
 * file ID and file name expressions, evaluated for each symbol.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(PDF417Component.COMPONENT_NAME)
public class PDF417Component extends OkapiSizedComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "PDF417";

	public static final String PROPERTY_MODE = "mode";
	public static final String PROPERTY_DATA_COLUMNS = "dataColumns";
	public static final String PROPERTY_ROWS = "rows";
	public static final String PROPERTY_PREFERRED_ECC_LEVEL = "preferredEccLevel";
	public static final String PROPERTY_VARIANT = "variant";
	public static final String PROPERTY_FORCE_BYTE_COMPACTION = "forceByteCompaction";
	public static final String PROPERTY_BAR_HEIGHT = "barHeight";
	public static final String PROPERTY_DATA_TYPE = "dataType";
	public static final String PROPERTY_ECI_MODE = "eciMode";
	public static final String PROPERTY_READER_INIT = "readerInit";
	public static final String PROPERTY_STRUCTURED_APPEND_POSITION_EXPRESSION = "structuredAppendPositionExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_TOTAL_EXPRESSION = "structuredAppendTotalExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_FILE_ID_EXPRESSION = "structuredAppendFileIdExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_FILE_NAME_EXPRESSION = "structuredAppendFileNameExpression";
	public static final String PROPERTY_STRUCTURED_APPEND_INCLUDE_SEGMENT_COUNT = "structuredAppendIncludeSegmentCount";
	public static final String PROPERTY_STRUCTURED_APPEND_FORCED = "structuredAppendForced";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private PDF417ModeEnum mode;
	private Integer dataColumns;
	private Integer rows;
	private Integer preferredEccLevel;
	private Integer variant;
	private Boolean forceByteCompaction;
	private Double barHeight;
	private PDF417DataTypeEnum dataType;
	private Integer eciMode;
	private Boolean readerInit;
	private JRExpression structuredAppendPositionExpression;
	private JRExpression structuredAppendTotalExpression;
	private JRExpression structuredAppendFileIdExpression;
	private JRExpression structuredAppendFileNameExpression;
	private Boolean structuredAppendIncludeSegmentCount;
	private Boolean structuredAppendForced;

	public PDF417Component()
	{
	}

	/**
	 * The PDF417 variant; when not set, normal.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public PDF417ModeEnum getMode()
	{
		return mode;
	}

	public void setMode(PDF417ModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(PROPERTY_MODE,
				old, this.mode);
	}

	/**
	 * The number of data columns, 1 to 30, or 1 to 4 for MicroPDF417; when not set, chosen for the data.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getDataColumns()
	{
		return dataColumns;
	}

	public void setDataColumns(Integer dataColumns)
	{
		Object old = this.dataColumns;
		this.dataColumns = dataColumns;
		getEventSupport().firePropertyChange(PROPERTY_DATA_COLUMNS,
				old, this.dataColumns);
	}

	/**
	 * The number of rows, 3 to 90, or 4 to 44 for MicroPDF417; when not set, chosen for the data.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getRows()
	{
		return rows;
	}

	public void setRows(Integer rows)
	{
		Object old = this.rows;
		this.rows = rows;
		getEventSupport().firePropertyChange(PROPERTY_ROWS,
				old, this.rows);
	}

	/**
	 * The error correction level, 0 to 8, which adds 2 to the power of the level plus one
	 * error correction codewords; when not set, chosen for the data. It isn't allowed for MicroPDF417.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredEccLevel()
	{
		return preferredEccLevel;
	}

	public void setPreferredEccLevel(Integer preferredEccLevel)
	{
		Object old = this.preferredEccLevel;
		this.preferredEccLevel = preferredEccLevel;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_ECC_LEVEL,
				old, this.preferredEccLevel);
	}

	/**
	 * The MicroPDF417 variant, 1 to 34, which sets the columns and rows; only allowed for MicroPDF417,
	 * and not with dataColumns or rows.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getVariant()
	{
		return variant;
	}

	public void setVariant(Integer variant)
	{
		Object old = this.variant;
		this.variant = variant;
		getEventSupport().firePropertyChange(PROPERTY_VARIANT,
				old, this.variant);
	}

	/**
	 * Whether all the data is encoded in byte compaction mode.
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
	 * The height of each row, in report units, rounded to whole modules. When set, the rows are never
	 * taller; they are lower when the element height doesn't fit them, but never below 3 modules (2 for MicroPDF417),
	 * the image then shrinks the symbol. When not set, the rows share the element height, with the same minimum.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getBarHeight()
	{
		return barHeight;
	}

	public void setBarHeight(Double barHeight)
	{
		Object old = this.barHeight;
		this.barHeight = barHeight;
		getEventSupport().firePropertyChange(PROPERTY_BAR_HEIGHT,
				old, this.barHeight);
	}

	/**
	 * How the content is interpreted; when not set, as plain data, in the character set of {@link #getEciMode()}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public PDF417DataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(PDF417DataTypeEnum dataType)
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
	 * The position (1 to 99999) of this symbol in a Macro PDF417 series.
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
	 * The number of symbols (2 to 99999) in a Macro PDF417 series.
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
	 * The file ID of a Macro PDF417 series, the same in every symbol of the series:
	 * triplets of digits, each from 000 to 899. When not set, 000.
	 */
	public JRExpression getStructuredAppendFileIdExpression()
	{
		return structuredAppendFileIdExpression;
	}

	public void setStructuredAppendFileIdExpression(JRExpression structuredAppendFileIdExpression)
	{
		Object old = this.structuredAppendFileIdExpression;
		this.structuredAppendFileIdExpression = structuredAppendFileIdExpression;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_FILE_ID_EXPRESSION,
				old, this.structuredAppendFileIdExpression);
	}

	/**
	 * The optional file name of a Macro PDF417 series, the same in every symbol of the series.
	 */
	public JRExpression getStructuredAppendFileNameExpression()
	{
		return structuredAppendFileNameExpression;
	}

	public void setStructuredAppendFileNameExpression(JRExpression structuredAppendFileNameExpression)
	{
		Object old = this.structuredAppendFileNameExpression;
		this.structuredAppendFileNameExpression = structuredAppendFileNameExpression;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_FILE_NAME_EXPRESSION,
				old, this.structuredAppendFileNameExpression);
	}

	/**
	 * Whether the number of symbols of a Macro PDF417 series is also written in its control block.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getStructuredAppendIncludeSegmentCount()
	{
		return structuredAppendIncludeSegmentCount;
	}

	public void setStructuredAppendIncludeSegmentCount(Boolean structuredAppendIncludeSegmentCount)
	{
		Object old = this.structuredAppendIncludeSegmentCount;
		this.structuredAppendIncludeSegmentCount = structuredAppendIncludeSegmentCount;
		getEventSupport().firePropertyChange(PROPERTY_STRUCTURED_APPEND_INCLUDE_SEGMENT_COUNT,
				old, this.structuredAppendIncludeSegmentCount);
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
		PDF417Component clone = (PDF417Component) super.clone();
		clone.structuredAppendPositionExpression = JRCloneUtils.nullSafeClone(structuredAppendPositionExpression);
		clone.structuredAppendTotalExpression = JRCloneUtils.nullSafeClone(structuredAppendTotalExpression);
		clone.structuredAppendFileIdExpression = JRCloneUtils.nullSafeClone(structuredAppendFileIdExpression);
		clone.structuredAppendFileNameExpression = JRCloneUtils.nullSafeClone(structuredAppendFileNameExpression);
		return clone;
	}
}
