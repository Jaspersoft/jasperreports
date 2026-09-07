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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Implementations of this interface represent various sections in the report template. 
 * <h3>Report Sections</h3>
 * JasperReports works with templates that are structured into multiple sections, like any
 * traditional reporting tool. A report can contain the following
 * sections: background, title, summary, page header, page footer, last page footer, 
 * column header and column footer.  For each group defined in the report, there is a 
 * corresponding group header section and group footer section.
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
 * The entire structure of the report template is based on the following sections:
 * <code>&lt;title&gt;</code>, <code>&lt;pageHeader&gt;</code>, <code>&lt;columnHeader&gt;</code>, 
 * <code>&lt;groupHeader&gt;</code>, <code>&lt;detail&gt;</code>,
 * <code>&lt;groupFooter&gt;</code>, <code>&lt;columnFooter&gt;</code>, <code>&lt;pageFooter&gt;</code>, 
 * <code>&lt;lastPageFooter&gt;</code>, <code>&lt;summary&gt;</code>, &lt;background&gt;</code> and &lt;noData&gt;</code>.
 * All report sections are optional, but of course all useful templates must have at least 
 * one such section.
 * <h3>Title</h3>
 * This is the first section of the report. It is generated only once during the report-filling
 * process and represents the beginning of the resulting document.
 * <p/>
 * The title section precedes even the page header section. To print the page header before
 * the title section, put the elements on the page header at the beginning of the title section
 * as well. One can suppress the actual page header on the first page using the
 * <code>&lt;printWhenExpression&gt;</code>, based on the 
 * {@link net.sf.jasperreports.engine.JRVariable#PAGE_NUMBER PAGE_NUMBER} report variable.
 * <p/>
 * The title section can be followed by a page break if the <code>isTitleNewPage</code> attribute 
 * at report level is set to true.
 * <h3>Page Header</h3>
 * This section appears at the top of each page in the generated document.
 * <h3>Column Header</h3>
 * This section appears at the top of each column in the generated document.
 * <h3>Detail</h3>
 * For each record in the data source, the engine tries to generate this section. The detail
 * section can be made of multiple bands.
 * <h3>Column Footer</h3>
 * This section appears at the bottom of each column in the generated document. It never
 * stretches downward to acquire the content of its containing text fields. Its rendering
 * position is controlled by the <code>isFloatColumnFooter</code> flag declared at report template
 * level.
 * <h3>Page Footer</h3>
 * This section appears at the bottom of each page in the generated document. Just like the
 * column footer section, the page footer never stretches downwards to acquire the content
 * of its containing text fields and always retains the declared fixed height.
 * <h3>Summary</h3>
 * This section is generated only once per report and appears at the end of the generated
 * document, but is not necessarily the last section generated. This is because in some cases
 * the column footer and/or page footer of the last page follows it.
 * <p/>
 * One can have the summary
 * section start a new page of its own by setting the <code>isSummaryNewPage</code> attribute 
 * to true at report template level.
 * Even if this attribute remains false, the summary section always starts a new page if it
 * does not fit on the remaining space of the last page, or if the report has more than one
 * column and it has already started a second column on the last page.
 * <h3>Last Page Footer</h3>
 * If present, this section replaces the normal page footer section, but only on the last
 * occurrence of the page footer, which might not be the last page if the summary is present
 * and it overflows on multiple pages or it is rendered alone on its own last page. So it
 * behaves more like <i>the last</i> page footer than the footer of the <i>last page</i>.
 * <h3>Background</h3>
 * This is a special section that is rendered on all pages and its content placed underneath
 * all other report sections. Normal report sections are rendered one after the other, but the
 * background section does not interfere with the other report sections and can be used to
 * achieve watermark effects or to create the same background for all pages.
 * <h3>No Data</h3>
 * This is another special section that is generated only once per report and, under certain
 * conditions, its content will replace all the ordinary report's content.
 * <p/>
 * Sometimes when the report data source is empty, is very useful to generate an equivalent
 * content, a notice, maybe, or an image, in order to replace all the empty zone. Especially
 * in the case of subreports, more useful is to replace all the subreport's content with an
 * equivalent one.
 * <p/>
 * If the <code>&lt;noData&gt;</code> section is defined in the report template, and if the data source is empty,
 * then the <code>&lt;noData&gt;</code> section will be the only one taken into account at fill time, and its
 * content will produce the report output.
 * <h3>Group Header</h3>
 * This section marks the start of a new group in the resulting document. It is inserted in the
 * document every time the value of the group expression changes during the iteration
 * through the data source. The group header section is a multi-band section.
 * <h3>Group Footer</h3>
 * Every time a report group changes, the engine adds the corresponding group footer
 * section before starting the new group or when the report ends. The group footer section
 * is also a multi-band section.
 * @see JRBand
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ELEMENT_band,
	JRXmlConstants.ELEMENT_part
	})
@JsonDeserialize(as = JRDesignSection.class)
public interface JRSection extends JRCloneable
{
	
	/**
	 * Returns the bands within the current section.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_band)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRBand[] getBands();
	
	/**
	 * Returns the parts within the current section.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_part)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRPart[] getParts();
		
}
