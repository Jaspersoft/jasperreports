/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.crosstabs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.type.CrosstabRowPositionEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.jackson.util.JRXmlSince;


/**
 * Crosstab row group interface.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonDeserialize(as = JRDesignCrosstabRowGroup.class)
public interface JRCrosstabRowGroup extends JRCrosstabGroup
{
	/**
	 * Returns the width of the group headers.
	 * 
	 * @return the width of the group headers
	 * @see JRCrosstabGroup#getHeader()
	 * @see JRCrosstabGroup#getTotalHeader()
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getWidth();
	

	/**
	 * Returns the position of the header contents for header stretching.
	 * <p>
	 * The row group headers stretch vertically when there are multiple sub group entries.
	 * The header contents will be adjusted to the new height depending on this attribute:
	 * <ul>
	 * 	<li>{@link CrosstabRowPositionEnum#TOP CrosstabRowPositionEnum.TOP} - the contents will be rendered at the top of of the header</li>
	 * 	<li>{@link CrosstabRowPositionEnum#MIDDLE CrosstabRowPositionEnum.MIDDLE} - the contents will be rendered on the center of the header</li>
	 * 	<li>{@link CrosstabRowPositionEnum#BOTTOM JCrosstabRowPositionEnum.BOTTOM} - the contents will be rendered at the bottom of the header</li>
	 * 	<li>{@link CrosstabRowPositionEnum#STRETCH CrosstabRowPositionEnum.STRETCH} - the contents will be proportionally stretched to the new header size</li>
	 * </ul>
	 * 
	 * @return the position of the header contents for header stretching
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public CrosstabRowPositionEnum getPosition();


	/**
	 * Determines whether the row group should be kept together on a single page.
	 * <p>
	 * When set to <code>true</code>, all rows belonging to the same row group instance
	 * will be kept together on a single page. If there is not enough space on the current page,
	 * the entire row group will be moved to the next page.
	 *
	 * @return whether the row group should be kept together
	 */
	@JRXmlSince(JRConstants.VERSION_7_0_7)
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isKeepTogether();
}
