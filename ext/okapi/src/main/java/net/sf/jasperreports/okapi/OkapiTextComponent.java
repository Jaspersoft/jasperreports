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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;


/**
 * Base class of the Okapi barcode components that draw human-readable text, adding
 * the text location and alignment. The text uses the font of the element style.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class OkapiTextComponent extends OkapiSizedComponent
{

	public static final String PROPERTY_HUMAN_READABLE_LOCATION = "humanReadableLocation";
	public static final String PROPERTY_HUMAN_READABLE_ALIGNMENT = "humanReadableAlignment";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private HumanReadableLocationEnum humanReadableLocation;
	private HumanReadableAlignmentEnum humanReadableAlignment;

	public OkapiTextComponent()
	{
	}

	/**
	 * Where the human-readable text is drawn; when not set, it is drawn below the bars.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public HumanReadableLocationEnum getHumanReadableLocation()
	{
		return humanReadableLocation;
	}

	public void setHumanReadableLocation(HumanReadableLocationEnum humanReadableLocation)
	{
		Object old = this.humanReadableLocation;
		this.humanReadableLocation = humanReadableLocation;
		getEventSupport().firePropertyChange(PROPERTY_HUMAN_READABLE_LOCATION,
				old, this.humanReadableLocation);
	}

	/**
	 * How the human-readable text is aligned; when not set, it is centered.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public HumanReadableAlignmentEnum getHumanReadableAlignment()
	{
		return humanReadableAlignment;
	}

	public void setHumanReadableAlignment(HumanReadableAlignmentEnum humanReadableAlignment)
	{
		Object old = this.humanReadableAlignment;
		this.humanReadableAlignment = humanReadableAlignment;
		getEventSupport().firePropertyChange(PROPERTY_HUMAN_READABLE_ALIGNMENT,
				old, this.humanReadableAlignment);
	}
}
