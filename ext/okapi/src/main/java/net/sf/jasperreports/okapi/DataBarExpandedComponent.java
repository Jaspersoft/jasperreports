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
 * GS1 DataBar Expanded barcode, stacked by default. Encodes GS1 data, with the application identifiers
 * in square brackets.
 * <p>
 * Only the variant that is not {@link #getStacked() stacked} draws human-readable text; when stacked,
 * the text settings have no effect.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(DataBarExpandedComponent.COMPONENT_NAME)
public class DataBarExpandedComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "DataBarExpanded";

	public static final String PROPERTY_PREFERRED_COLUMNS = "preferredColumns";
	public static final String PROPERTY_STACKED = "stacked";
	public static final String PROPERTY_BAR_HEIGHT = "barHeight";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Integer preferredColumns;
	private Boolean stacked;
	private Double barHeight;

	public DataBarExpandedComponent()
	{
	}

	/**
	 * The number of columns (symbol segments) in each row of the stacked variant, 1 to 10; when not set, 2.
	 * It has no effect when not stacked.
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

	/**
	 * Whether the symbol is stacked in rows; when not set, it is.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getStacked()
	{
		return stacked;
	}

	public void setStacked(Boolean stacked)
	{
		Object old = this.stacked;
		this.stacked = stacked;
		getEventSupport().firePropertyChange(PROPERTY_STACKED,
				old, this.stacked);
	}

	/**
	 * The height of each row of the stacked variant, in report units, rounded to whole modules.
	 * When set, the rows are never taller; they are lower when the element height doesn't fit them.
	 * When not set, the rows share the element height. It has no effect when not stacked.
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
}
