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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.type.FooterPositionEnum;
import net.sf.jasperreports.jackson.util.SectionSerializer;


/**
 * Groups represent a flexible way to organize data on a report. A report group is represented by sequence
 * of consecutive records in the data source that have something in common, like the value of a certain report
 * field for example.
 * <p/>
 * A report group has three components:
 * <ul>
 * <li>Group expression</li>
 * <li>Group header section</li>
 * <li>Group footer section</li>
 * </ul>
 * The value of the associated group expression is what makes group records stick together. This value is the
 * thing that they have in common. When the value of the group expression
 * changes during the iteration through the data source at report-filling time, a group
 * rupture occurs and the corresponding <code>&lt;groupFooter&gt;</code> and <code>&lt;groupHeader&gt;</code> 
 * sections are inserted in the resulting document.
 * <p/>
 * You can have as many groups as you want on a report. The order of groups declared in a
 * report template is important because groups contain each other. One group contains the
 * following group, and so on. When a larger group encounters a rupture, all subsequent
 * groups are reinitialized.
 * <p/>
 * <b>Note:</b> Data grouping works as expected only when the records in the data source are already ordered
 * according to the group expressions used in the report. For example, if you want to group some products by 
 * the country and city of the manufacturer, the engine expects to find the records in the data source already 
 * ordered by country and city.
 * <h3>Group Name</h3>
 * The name unequivocally identifies the group and can be used in other JRXML attributes
 * when you want to refer a particular report group. The name of a group is mandatory and
 * obeys the same naming convention that we mentioned for the report parameters, fields,
 * and report variables.
 * <h3>Staring a New Page or a New Column When a Group Breaks</h3>
 * Sometimes it is useful to introduce a page or column break when a new group starts,
 * usually because that particular group is more important and should start on a page or
 * column of its own.
 * <p/>
 * To instruct the engine to start a new page or column for a certain group instead of
 * printing it on the remaining space at the bottom of the page or column, one must set
 * either the <code>isStartNewPage</code> or <code>isStartNewColumn</code> attribute to true.
 * These two attributes represent one of the most common ways to control page and column
 * breaks in a report. The other one is by using the special break element. In all other
 * situations, the reporting engine introduces page breaks automatically if content overflows
 * onto a new page or column during the report-filling process.
 * <p/>
 * In some report templates, you may want to introduce page breaks on purpose when a
 * report section is larger than one page. Using the break element would not help, as the
 * report template, having a band larger than the page size, would not get past the report
 * validation process. To do this, you would need to introduce special dummy groups, as
 * explained in the FAQs section of the freely available documentation published on the
 * JasperReports web site (<a href="http://community.jaspersoft.com/wiki/jasperreports-library-faqs">http://community.jaspersoft.com/wiki/jasperreports-library-faqs</a>).
 * However, if you don't want to consistently introduce page or column breaks for a
 * particular group, but prefer to do that only if the remaining space at the bottom of the
 * page or column is too small, use the <code>minHeightToStartNewPage</code> attribute. This
 * attribute specifies the minimum remaining vertical space that prevents the group from
 * starting a new page of its own. It is measured in pixels.
 * <h3>Resetting Page Number</h3>
 * If required, report groups have the power to reset the built-in report variable that contains
 * the current page number (variable {@link net.sf.jasperreports.engine.JRVariable#PAGE_NUMBER PAGE_NUMBER}). 
 * To do this, set the <code>isResetPageNumber</code> attribute to true.
 * <h3>Group Header</h3>
 * This section marks the start of a new group in the resulting document. It is inserted in the
 * document every time the value of the group expression changes during the iteration
 * through the data source. The group header section is a multi-band section.
 * <h3>Group Footer</h3>
 * Every time a report group changes, the engine adds the corresponding group footer
 * section before starting the new group or when the report ends. The group footer section
 * is also a multi-band section.
 * <p/>
 * The rendering position of the group footer on the page, as well as its behavior in relation
 * to the report sections that follow it, is controlled by the <code>footerPosition</code> attribute, as
 * follows:
 * <ul>
 * <li><code>Normal</code> - The group footer section is rendered immediately after the previous section.</li>
 * <li><code>StackAtBottom</code> - The group footer section appears at the bottom of
 * the current page. Remaining space on the page appears above it. The group footer
 * section of the outer groups is pushed to the bottom of the current page, as well, in
 * case the current group is a nested inner group. So both the current group footer and
 * the outer group footers stack at the bottom of the current page</li>
 * <li><code>ForceAtBottom</code> - The group footer section is forced to render at the
 * very bottom of the page and is followed only by the page footer section. All
 * sections following this type of group footer are forced to render on the next page</li>
 * <li><code>CollateAtBottom</code> - The collate setting is a weak setting. If all outer
 * group footers are configured to render at the bottom of the page, the group footer
 * section will also appear at the bottom and any remaining white space will appear
 * above it. However, if at least one outer group footer has normal rendering position
 * and its positioning is not overridden by another inner group, the current group
 * footer renders at the normal position.</li>
 * </ul>
 * Without specifying a footer position for the group, the group footer is rendered 
 * in normal position.
 * <h3>Preventing Group Split</h3>
 * Sometimes it is useful to keep the content of a group together and prevent it from
 * spanning pages or columns. In such cases, it is often advisable to start the group on a
 * new page or column and leave some unused space on the current page/column rather
 * than having the group split in the middle.
 * <p/>
 * This behavior can be controlled with the <code>keepTogether</code> flag available at group level.
 * When this flag is turned on, we prevent the group from splitting on its first break
 * attempt. If a group is long, it will certainly need to break at some point. So, with a first
 * break only, we avoid a split only when the group attempts to split for the first time, while
 * subsequent breaks during the current group are allowed.
 * <p/>
 * Note that this a purely visual feature of the engine, because it does not involve reverting
 * any of the calculations made during the current group iteration . It is only about moving
 * already-generated content to a new page, making it appear as if the group started there in
 * the first place. Be advised that in cases where group-, page- or column-related
 * information is displayed in the group, such as the current page number, their values 
 * might be wrong after they are moved.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_name,
	"startNewColumn",
	"startNewPage",
	"resetPageNumber",
	"reprintHeaderOnEachPage",
	"reprintHeaderOnEachColumn",
	JRXmlConstants.ATTRIBUTE_minHeightToStartNewPage,
	JRXmlConstants.ATTRIBUTE_minDetailsToStartFromTop,
	JRXmlConstants.ATTRIBUTE_footerPosition,
	JRXmlConstants.ATTRIBUTE_keepTogether,
	JRXmlConstants.ATTRIBUTE_preventOrphanFooter,
	JRXmlConstants.ELEMENT_expression,
	JRXmlConstants.ELEMENT_groupHeader,
	JRXmlConstants.ELEMENT_groupFooter
	})
@JsonDeserialize(as = JRDesignGroup.class)
public interface JRGroup extends JRCloneable
{


	/**
	 * Gets the group name
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();
	
	/**
	 * Gets the flag that signals if the group header should be printed always on a new column.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isStartNewColumn();
		
	/**
	 * Sets the flag that signals if the group header should be printed always on a new column.
	 */
	public void setStartNewColumn(boolean isStart);
		
	/**
	 * Gets the flag that signals if the group header should be printed always on a new page.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isStartNewPage();
		
	/**
	 * Sets the flag that signals if the group header should be printed always on a new page.
	 */
	public void setStartNewPage(boolean isStart);
		
	/**
	 * Gets the flag that signals if the group header should be printed always on a new page, along with the
	 * re-initialization of the page number.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isResetPageNumber();
		
	/**
	 * Sets the flag that signals if the group header should be printed always on a new page, along with the
	 * re-initialization of the page number.
	 */
	public void setResetPageNumber(boolean isReset);
		
	/**
	 * Gets the flag that signals if the group header should be reprinted at the beginning of each page.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isReprintHeaderOnEachPage();
		
	/**
	 * Gets the flag that signals if the group header should be reprinted at the beginning of each column, in vertically filled reports only.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isReprintHeaderOnEachColumn();
		
	/**
	 * Sets the flag that signals if the group header should be reprinted at the beginning of each page.
	 */
	public void setReprintHeaderOnEachPage(boolean isReprint);
		
	/**
	 * Sets the flag that signals if the group header should be reprinted at the beginning of each column, in vertically filled reports only.
	 */
	public void setReprintHeaderOnEachColumn(boolean isReprint);
		
	/**
	 * Gets the minimum amount of vertical space needed at the bottom of the column in order to place the
	 * group header on the current column.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public int getMinHeightToStartNewPage();

	/**
	 * Sets the minimum amount of vertical space needed at the bottom of the column in order to place the
	 * group header on the current column.
	 */
	public void setMinHeightToStartNewPage(int minHeight);
		
	/**
	 * Gets the minimum number of details to be rendered on the current column, to avoid starting the group on a new column.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public int getMinDetailsToStartFromTop();

	/**
	 * Sets the minimum number of details to be rendered on the current column, to avoid starting the group on a new column.
	 */
	public void setMinDetailsToStartFromTop(int minDetails);
		
	/**
	 * Specifies how the group footer section behaves with regards to its position on the current page.
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public FooterPositionEnum getFooterPosition();

	/**
	 * Specifies the group footer section behavior with regards to its position on the current page.
	 */
	public void setFooterPosition(FooterPositionEnum footerPosition);
		
	/**
	 * Gets the flag that signals if the group should be prevented from splitting on first break attempt.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isKeepTogether();
		
	/**
	 * Sets the flag that signals if the group should be prevented from splitting on first break attempt.
	 */
	public void setKeepTogether(boolean keepTogether);
	
	/**
	 * Gets the flag that signals if the group footer should be prevented from appearing without a detail at the top of a new page/column.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isPreventOrphanFooter();
		
	/**
	 * Sets the flag that signals if the group should be prevented from appearing without a detail at the top of a new page/column.
	 */
	public void setPreventOrphanFooter(boolean preventOrphanFooter);
	
	/**
	 * Gets the expression that defines what records in the group have in common.
	 */
	public JRExpression getExpression();
	
	/**
	 * Gets the header section created for this group.
	 */
	@JsonProperty("groupHeader")
	@JsonInclude(Include.NON_EMPTY)
	@JsonSerialize(using = SectionSerializer.class) // JACKSON-TIP: cannot be moved to JRSection
	public JRSection getGroupHeaderSection();
		
	/**
	 * Gets the footer section created for this group.
	 */
	@JsonProperty("groupFooter")
	@JsonInclude(Include.NON_EMPTY)
	@JsonSerialize(using = SectionSerializer.class)
	public JRSection getGroupFooterSection();

	/**
	 *
	 */
	@JsonIgnore
	public JRVariable getCountVariable();


}
