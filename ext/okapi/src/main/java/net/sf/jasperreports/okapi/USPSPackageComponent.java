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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;


/**
 * USPS Intelligent Mail Package Barcode (IMpb), a GS1-128 symbol with the (420) application identifier
 * and a banner. Encodes digits as GS1 data, with the application identifiers in square brackets;
 * the content length, brackets included, is even.
 * <p>
 * The banner and the number are always drawn, above and below the bars; only their alignment can be set.
 * The text uses the font of the element style. Module width has no effect on this barcode.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(USPSPackageComponent.COMPONENT_NAME)
public class USPSPackageComponent extends OkapiComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "USPSPackage";

	public static final String PROPERTY_HUMAN_READABLE_ALIGNMENT = "humanReadableAlignment";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private HumanReadableAlignmentEnum humanReadableAlignment;

	public USPSPackageComponent()
	{
	}

	/**
	 * How the banner and the number are aligned; when not set, they are centered.
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
