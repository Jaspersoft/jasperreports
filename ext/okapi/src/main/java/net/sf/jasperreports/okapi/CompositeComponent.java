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
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * GS1 Composite barcode: a 2D component (CC-A, CC-B or CC-C) above a linear component.
 * <p>
 * The code expression gives the 2D component and the linear content expression the linear one,
 * both GS1 data with the application identifiers in square brackets (UPC and EAN take digits).
 * When either is null, no barcode is drawn.
 * </p>
 * <p>
 * The text is the linear component's: the stacked DataBar variants draw none, and then
 * the text settings have no effect.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(CompositeComponent.COMPONENT_NAME)
public class CompositeComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "Composite";

	public static final String PROPERTY_SYMBOLOGY = "symbology";
	public static final String PROPERTY_PREFERRED_MODE = "preferredMode";
	public static final String PROPERTY_LINEAR_CONTENT_EXPRESSION = "linearContentExpression";
	public static final String PROPERTY_BAR_HEIGHT = "barHeight";
	public static final String PROPERTY_SEPARATOR_HEIGHT = "separatorHeight";
	public static final String PROPERTY_GUARD_PATTERN_EXTRA_HEIGHT = "guardPatternExtraHeight";
	public static final String PROPERTY_PREFERRED_COLUMNS = "preferredColumns";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private CompositeSymbologyEnum symbology;
	private CompositeModeEnum preferredMode;
	private JRExpression linearContentExpression;
	private Double barHeight;
	private Double separatorHeight;
	private Double guardPatternExtraHeight;
	private Integer preferredColumns;

	public CompositeComponent()
	{
	}

	/**
	 * The linear component; when not set, {@link CompositeSymbologyEnum#CODE128 GS1-128}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public CompositeSymbologyEnum getSymbology()
	{
		return symbology;
	}

	public void setSymbology(CompositeSymbologyEnum symbology)
	{
		Object old = this.symbology;
		this.symbology = symbology;
		getEventSupport().firePropertyChange(PROPERTY_SYMBOLOGY,
				old, this.symbology);
	}

	/**
	 * The smallest 2D component to use; a larger one is used when the data doesn't fit.
	 * When not set, {@link CompositeModeEnum#CC_A CC-A}. {@link CompositeModeEnum#CC_C CC-C}
	 * is only allowed with a {@link CompositeSymbologyEnum#CODE128 GS1-128} linear component.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public CompositeModeEnum getPreferredMode()
	{
		return preferredMode;
	}

	public void setPreferredMode(CompositeModeEnum preferredMode)
	{
		Object old = this.preferredMode;
		this.preferredMode = preferredMode;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_MODE,
				old, this.preferredMode);
	}

	/**
	 * The data of the linear component; it is required.
	 */
	public JRExpression getLinearContentExpression()
	{
		return linearContentExpression;
	}

	public void setLinearContentExpression(JRExpression linearContentExpression)
	{
		Object old = this.linearContentExpression;
		this.linearContentExpression = linearContentExpression;
		getEventSupport().firePropertyChange(PROPERTY_LINEAR_CONTENT_EXPRESSION,
				old, this.linearContentExpression);
	}

	/**
	 * The height of the linear component's bars, in report units, rounded to whole modules.
	 * When set, the bars are never taller; they are lower when the element height doesn't fit them.
	 * When not set, they take the element height left by the 2D component.
	 * It has no effect on {@link CompositeSymbologyEnum#DATABAR_STACKED DataBar Stacked}, whose rows have a fixed height.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getBarHeight()
	{
		return barHeight;
	}

	public void setBarHeight(Double barHeight)
	{
		Object old = this.barHeight;
		this.barHeight = barHeight;
		getEventSupport().firePropertyChange(PROPERTY_BAR_HEIGHT,
				old, this.barHeight);
	}

	/**
	 * The height of the separator between the 2D and the linear component, in report units,
	 * rounded to whole modules and at least one module; when not set, one module.
	 * It is not allowed with UPC and EAN.
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

	/**
	 * How much longer the guard bars are than the other bars, in report units, rounded to whole modules;
	 * when not set, the Okapi default applies. It is only allowed with UPC and EAN.
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

	/**
	 * The number of columns (symbol segments) in each row of the linear component, 1 to 10; when not set, 2.
	 * It is only allowed with {@link CompositeSymbologyEnum#DATABAR_EXPANDED_STACKED DataBar Expanded Stacked}.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredColumns()
	{
		return preferredColumns;
	}

	public void setPreferredColumns(Integer preferredColumns)
	{
		Object old = this.preferredColumns;
		this.preferredColumns = preferredColumns;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_COLUMNS,
				old, this.preferredColumns);
	}

	@Override
	public Object clone()
	{
		CompositeComponent clone = (CompositeComponent) super.clone();
		clone.linearContentExpression = JRCloneUtils.nullSafeClone(linearContentExpression);
		return clone;
	}
}
