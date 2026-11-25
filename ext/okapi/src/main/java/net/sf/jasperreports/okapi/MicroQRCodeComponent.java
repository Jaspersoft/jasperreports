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
 * Micro QR Code barcode.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(MicroQRCodeComponent.COMPONENT_NAME)
public class MicroQRCodeComponent extends OkapiSizedComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "MicroQRCode";

	public static final String PROPERTY_PREFERRED_VERSION = "preferredVersion";
	public static final String PROPERTY_ECC_MODE = "eccMode";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Integer preferredVersion;
	private MicroQRCodeEccModeEnum eccMode;

	public MicroQRCodeComponent()
	{
	}

	/**
	 * The smallest symbol version, 1 to 4 (M1 to M4); a larger one is used when the data needs it.
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
	 * The error correction level; when not set, L.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public MicroQRCodeEccModeEnum getEccMode()
	{
		return eccMode;
	}

	public void setEccMode(MicroQRCodeEccModeEnum eccMode)
	{
		Object old = this.eccMode;
		this.eccMode = eccMode;
		getEventSupport().firePropertyChange(PROPERTY_ECC_MODE,
				old, this.eccMode);
	}
}
