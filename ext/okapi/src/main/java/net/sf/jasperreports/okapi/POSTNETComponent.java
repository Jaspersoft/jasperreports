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
 * USPS POSTNET or PLANET barcode. Encodes digits; the check digit is added.
 * <p>
 * It draws no human-readable text unless a text location is set.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(POSTNETComponent.COMPONENT_NAME)
public class POSTNETComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "POSTNET";

	public static final String PROPERTY_MODE = "mode";
	public static final String PROPERTY_MODULE_WIDTH_RATIO = "moduleWidthRatio";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private POSTNETModeEnum mode;
	private Double moduleWidthRatio;

	public POSTNETComponent()
	{
	}

	/**
	 * POSTNET or PLANET; when not set, POSTNET.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public POSTNETModeEnum getMode()
	{
		return mode;
	}

	public void setMode(POSTNETModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(PROPERTY_MODE,
				old, this.mode);
	}

	/**
	 * The width of the spaces as a multiple of the bars; when not set, 1.5.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getModuleWidthRatio()
	{
		return moduleWidthRatio;
	}

	public void setModuleWidthRatio(Double moduleWidthRatio)
	{
		Object old = this.moduleWidthRatio;
		this.moduleWidthRatio = moduleWidthRatio;
		getEventSupport().firePropertyChange(PROPERTY_MODULE_WIDTH_RATIO,
				old, this.moduleWidthRatio);
	}
}
