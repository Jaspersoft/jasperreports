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
 * Australia Post customer barcode. Encodes the 8 digit Delivery Point Identifier,
 * followed in the standard mode by customer information; the format control code is added.
 * <p>
 * Its size is fixed: there is no module width. It fills the element, keeping its shape.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(AustraliaPostComponent.COMPONENT_NAME)
public class AustraliaPostComponent extends OkapiComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "AustraliaPost";

	public static final String PROPERTY_MODE = "mode";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private AustraliaPostModeEnum mode;

	public AustraliaPostComponent()
	{
	}

	/**
	 * The Australia Post barcode type; when not set, the standard customer barcode.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public AustraliaPostModeEnum getMode()
	{
		return mode;
	}

	public void setMode(AustraliaPostModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(PROPERTY_MODE,
				old, this.mode);
	}
}
