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
 * EAN-13 or EAN-8 barcode. A 2 or 5 digit add-on follows the code in the content,
 * separated by a + sign.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(EANComponent.COMPONENT_NAME)
public class EANComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "EAN";

	public static final String PROPERTY_MODE = "mode";
	public static final String PROPERTY_GUARD_PATTERN_EXTRA_HEIGHT = "guardPatternExtraHeight";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private EANModeEnum mode;
	private Double guardPatternExtraHeight;

	public EANComponent()
	{
	}

	/**
	 * The EAN variant; when not set, EAN-13.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public EANModeEnum getMode()
	{
		return mode;
	}

	public void setMode(EANModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(PROPERTY_MODE,
				old, this.mode);
	}

	/**
	 * How much longer the guard bars are than the other bars, in report units, rounded to whole modules;
	 * when not set, the Okapi default applies.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getGuardPatternExtraHeight()
	{
		return guardPatternExtraHeight;
	}

	public void setGuardPatternExtraHeight(Double guardPatternExtraHeight)
	{
		Object old = this.guardPatternExtraHeight;
		this.guardPatternExtraHeight = guardPatternExtraHeight;
		getEventSupport().firePropertyChange(PROPERTY_GUARD_PATTERN_EXTRA_HEIGHT,
				old, this.guardPatternExtraHeight);
	}
}
