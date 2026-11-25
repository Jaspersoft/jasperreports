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
 * GS1 DataBar Omnidirectional barcode, also stacked. Encodes a GTIN of up to 13 digits,
 * without the (01) application identifier and the check digit, which is added.
 * <p>
 * Only the {@link DataBarModeEnum#LINEAR linear} mode draws human-readable text; in the other modes the text
 * settings have no effect. With Okapi Barcode 0.5.7, text at the top clips the other modes.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(DataBarComponent.COMPONENT_NAME)
public class DataBarComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "DataBar";

	public static final String PROPERTY_MODE = "mode";
	public static final String PROPERTY_SEPARATOR_HEIGHT = "separatorHeight";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private DataBarModeEnum mode;
	private Double separatorHeight;

	public DataBarComponent()
	{
	}

	/**
	 * The DataBar variant; when not set, linear.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public DataBarModeEnum getMode()
	{
		return mode;
	}

	public void setMode(DataBarModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(PROPERTY_MODE,
				old, this.mode);
	}

	/**
	 * The height of the separator between the rows of the stacked variants, in report units,
	 * rounded to whole modules and at least one module; when not set, one module. It has no effect in the linear mode.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getSeparatorHeight()
	{
		return separatorHeight;
	}

	public void setSeparatorHeight(Double separatorHeight)
	{
		Object old = this.separatorHeight;
		this.separatorHeight = separatorHeight;
		getEventSupport().firePropertyChange(PROPERTY_SEPARATOR_HEIGHT,
				old, this.separatorHeight);
	}
}
