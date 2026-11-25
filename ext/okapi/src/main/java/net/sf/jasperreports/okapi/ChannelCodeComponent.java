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
 * Channel Code barcode. Encodes up to 7 digits; the number of channels, 3 to 8, sets the largest value.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(ChannelCodeComponent.COMPONENT_NAME)
public class ChannelCodeComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "ChannelCode";

	public static final String PROPERTY_PREFERRED_NUMBER_OF_CHANNELS = "preferredNumberOfChannels";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Integer preferredNumberOfChannels;

	public ChannelCodeComponent()
	{
	}

	/**
	 * The preferred number of channels, 3 to 8; more are used when the data needs them.
	 * When not set, 3.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredNumberOfChannels()
	{
		return preferredNumberOfChannels;
	}

	public void setPreferredNumberOfChannels(Integer preferredNumberOfChannels)
	{
		Object old = this.preferredNumberOfChannels;
		this.preferredNumberOfChannels = preferredNumberOfChannels;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_NUMBER_OF_CHANNELS,
				old, this.preferredNumberOfChannels);
	}
}
