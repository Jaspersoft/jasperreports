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
package net.sf.jasperreports.engine;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Implementations of this interface represent various bands in the report template. 
 * <h3>Report Sections</h3>
 * JasperReports works with templates that are structured into multiple sections, like any
 * traditional reporting tool. A report can contain the following
 * sections: background, title, summary, page header, page footer, last page footer, 
 * column header and column footer.
 * <p/>
 * At report-filling time, the engine iterates through the virtual
 * records of the supplied report data source and renders each report section when
 * appropriate, depending on each section's defined behavior.
 * <p/>
 * For instance, the detail section is rendered for each record in the data source. When page
 * breaks occur, the page header and page footer sections are rendered as needed.
 * <p/>
 * Sections are made of one or more bands. Bands are portions of the report template that
 * have a specified height and width and can contain report elements like lines, rectangles,
 * images, and text fields. These bands are filled repeatedly at report-generating time and
 * make up the final document.
 * <p/>
 * When declaring the content and layout of a report section, in an JRXML report design,
 * use the generic element <code>&lt;band&gt;</code>.
 * <p/>
 * Report sections, sometimes referred to as report bands, represent a feature and
 * functionality common to almost all reporting tools.
 * <h3>Band Height</h3>
 * The <code>height</code> attribute in a report band declaration specifies the height in pixels for that
 * particular band and is very important in the overall report design.
 * <p/>
 * The elements contained by a certain report band should always fit the band's dimensions;
 * this will prevent potentially bad results when generating the reports. The engine issues a
 * warning if it finds elements outside the band borders when compiling report designs.
 * <h3>Preventing Band Split</h3>
 * In some cases it is desirable to keep the whole contents of a given band in one piece to
 * prevent page breaks when the band stretches beyond its initial specified height. To do
 * this, use the <code>splitType</code> attribute, as follows:
 * <dl>
 * <dt><code>Stretch</code></dt>
 * <dd>The band never splits within its declared height. The band
 * will not start rendering on the current page if the remaining available space is not at
 * least equal to the band's declared height. However, if the band stretches on the
 * current page, the region that is added to the original height is allowed to split onto
 * the next page.</dd>
 * <dt><code>Prevent</code></dt>
 * <dd>The band starts to render normally, but if the bottom
 * of the page is reached without finishing the band, the whole contents of the band
 * that are already being laid out are moved to the next page. If the band does not fit
 * on the next page, the split occurs normally, as band split prevention is effective
 * only on the first split attempt</dd>
 * <dt><code>Immediate</code></dt>
 * <dd>The band is allowed to split anywhere except above its topmost element</dd>
 * </dl>
 * If a split type is not specified, the default is given by the
 * {@link #PROPERTY_SPLIT_TYPE net.sf.jasperreports.band.split.type} configuration property.
 * <h3>Skipping Bands</h3>
 * All the report sections allow users to define a report expression that will be evaluated at
 * runtime to decide if that section should be generated or skipped when producing the
 * document.
 * This expression is introduced by the <code>&lt;printWhenExpression&gt;</code> tag, which is available
 * in any <code>&lt;band&gt;</code> element of the JRXML report design and should always return a
 * <code>java.lang.Boolean</code> object or null.
 * @see net.sf.jasperreports.engine.JRSection
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_height,
	JRXmlConstants.ATTRIBUTE_splitType,
	JRXmlConstants.ELEMENT_printWhenExpression,
	JRXmlConstants.ELEMENT_returnValue
	})
@JsonTypeInfo(use = Id.NONE) // this is needed because JRBand extends JRChild, which has subtypes annotations
@JsonDeserialize(as = JRDesignBand.class)
public interface JRBand extends JRElementGroup, JRPropertiesHolder
{
	

	/**
	 * 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			valueType = SplitTypeEnum.class,
			defaultValue = "Stretch",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_5_2
			)
	public static final String PROPERTY_SPLIT_TYPE = JRPropertiesUtil.PROPERTY_PREFIX + "band.split.type";


	/**
	 *
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public int getHeight();

	/**
	 * Specifies the band split behavior.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public SplitTypeEnum getSplitType();

	/**
	 *
	 */
	public void setSplitType(SplitTypeEnum splitType);

	/**
	 * Returns the boolean expression that specifies if the band will be displayed.
	 */
	public JRExpression getPrintWhenExpression();

	/**
	 * Returns the list of values to increment report variables with.
	 *
	 * @return the list of returned values.
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_returnValue)
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<ExpressionReturnValue> getReturnValues();

}
