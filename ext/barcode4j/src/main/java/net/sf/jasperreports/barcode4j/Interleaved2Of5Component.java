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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName("barcode4j:Interleaved2Of5")
public class Interleaved2Of5Component extends Barcode4jComponent
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_CHECKSUM_MODE = "checksumMode";
	public static final String PROPERTY_DISPLAY_CHECKSUM = "displayChecksum";
	public static final String PROPERTY_WIDE_FACTOR = "wideFactor";

	private String checksumMode;
	private Boolean displayChecksum;
	private Double wideFactor;

	@JacksonXmlProperty(isAttribute = true)
	public String getChecksumMode()
	{
		return checksumMode;
	}

	public void setChecksumMode(String checksumMode)
	{
		Object old = this.checksumMode;
		this.checksumMode = checksumMode;
		getEventSupport().firePropertyChange(PROPERTY_CHECKSUM_MODE, old, this.checksumMode);
	}

	public void setChecksumMode(ChecksumMode checksumMode)
	{
		setChecksumMode(checksumMode == null ? null : checksumMode.getName());
	}

	@JacksonXmlProperty(isAttribute = true)
	public Boolean isDisplayChecksum()
	{
		return displayChecksum;
	}

	/**
	 * Enables or disables the use of the checksum in the human-readable message.
	 */
	public void setDisplayChecksum(Boolean displayChecksum)
	{
		Boolean old = this.displayChecksum;
		this.displayChecksum = displayChecksum;
		getEventSupport().firePropertyChange(PROPERTY_DISPLAY_CHECKSUM, old, this.displayChecksum);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Double getWideFactor()
	{
		return wideFactor;
	}

	/**
	 * Sets the factor by which wide bars are broader than narrow bars.
	 */
	public void setWideFactor(Double wideFactor)
	{
		Double old = this.wideFactor;
		this.wideFactor = wideFactor;
		getEventSupport().firePropertyChange(PROPERTY_WIDE_FACTOR, old, this.wideFactor);
	}

	@Override
	public void receive(BarcodeVisitor visitor)
	{
		visitor.visitInterleaved2Of5(this);
	}

}
