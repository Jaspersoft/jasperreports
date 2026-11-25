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
 * USPS Intelligent Mail barcode (IMb). Encodes the 20 digit tracking code, followed by a dash
 * and the 5, 9 or 11 digit routing (ZIP) code when there is one.
 * <p>
 * It draws no human-readable text unless a text location is set.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(USPSIntelligentMailComponent.COMPONENT_NAME)
public class USPSIntelligentMailComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "USPSIntelligentMail";

	public static final String PROPERTY_MODULE_WIDTH_RATIO = "moduleWidthRatio";
	public static final String PROPERTY_SHORT_HEIGHT_PERCENTAGE = "shortHeightPercentage";
	public static final String PROPERTY_LONG_HEIGHT_PERCENTAGE = "longHeightPercentage";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Double moduleWidthRatio;
	private Double shortHeightPercentage;
	private Double longHeightPercentage;

	public USPSIntelligentMailComponent()
	{
	}

	/**
	 * The width of the spaces as a multiple of the bars; when not set, 1.43.
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

	/**
	 * The height of the tracker bars as a fraction of the full bar height, 0 to 1; when not set, 0.25.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getShortHeightPercentage()
	{
		return shortHeightPercentage;
	}

	public void setShortHeightPercentage(Double shortHeightPercentage)
	{
		Object old = this.shortHeightPercentage;
		this.shortHeightPercentage = shortHeightPercentage;
		getEventSupport().firePropertyChange(PROPERTY_SHORT_HEIGHT_PERCENTAGE,
				old, this.shortHeightPercentage);
	}

	/**
	 * The height of the ascender and descender bars as a fraction of the full bar height, 0 to 1;
	 * when not set, 0.625.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getLongHeightPercentage()
	{
		return longHeightPercentage;
	}

	public void setLongHeightPercentage(Double longHeightPercentage)
	{
		Object old = this.longHeightPercentage;
		this.longHeightPercentage = longHeightPercentage;
		getEventSupport().firePropertyChange(PROPERTY_LONG_HEIGHT_PERCENTAGE,
				old, this.longHeightPercentage);
	}
}
