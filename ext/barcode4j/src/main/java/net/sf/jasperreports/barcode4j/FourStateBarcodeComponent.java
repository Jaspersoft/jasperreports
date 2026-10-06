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
package net.sf.jasperreports.barcode4j;

import org.krysalis.barcode4j.ChecksumMode;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class FourStateBarcodeComponent extends Barcode4jComponent
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_ASCENDER_HEIGHT = "ascenderHeight";
	public static final String PROPERTY_CHECKSUM_MODE = "checksumMode";
	public static final String PROPERTY_INTERCHAR_GAP_WIDTH = "intercharGapWidth";
	public static final String PROPERTY_TRACK_HEIGHT = "trackHeight";

	private Double ascenderHeight;
	private String checksumMode;
	private Double intercharGapWidth;
	private Double trackHeight;

	@JacksonXmlProperty(isAttribute = true)
	public Double getAscenderHeight()
	{
		return ascenderHeight;
	}

	public void setAscenderHeight(Double ascenderHeight)
	{
		Object old = this.ascenderHeight;
		this.ascenderHeight = ascenderHeight;
		getEventSupport().firePropertyChange(PROPERTY_ASCENDER_HEIGHT, 
				old, this.ascenderHeight);
	}

	@JacksonXmlProperty(isAttribute = true)
	public String getChecksumMode()
	{
		return checksumMode;
	}

	public void setChecksumMode(String checksumMode)
	{
		Object old = this.checksumMode;
		this.checksumMode = checksumMode;
		getEventSupport().firePropertyChange(PROPERTY_CHECKSUM_MODE, 
				old, this.checksumMode);
	}

	public void setChecksumMode(ChecksumMode checksumMode)
	{
		setChecksumMode(checksumMode == null ? null : checksumMode.getName());
	}

	@JacksonXmlProperty(isAttribute = true)
	public Double getIntercharGapWidth()
	{
		return intercharGapWidth;
	}

	public void setIntercharGapWidth(Double intercharGapWidth)
	{
		Object old = this.intercharGapWidth;
		this.intercharGapWidth = intercharGapWidth;
		getEventSupport().firePropertyChange(PROPERTY_INTERCHAR_GAP_WIDTH, 
				old, this.intercharGapWidth);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Double getTrackHeight()
	{
		return trackHeight;
	}

	public void setTrackHeight(Double trackHeight)
	{
		Object old = this.trackHeight;
		this.trackHeight = trackHeight;
		getEventSupport().firePropertyChange(PROPERTY_TRACK_HEIGHT, 
				old, this.trackHeight);
	}

}
