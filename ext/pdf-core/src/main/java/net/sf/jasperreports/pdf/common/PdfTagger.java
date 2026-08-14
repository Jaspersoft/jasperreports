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

/*
 * Contributors:
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 * Ling Li - lonecatz@users.sourceforge.net
 * Martin Clough - mtclough@users.sourceforge.net
 */
package net.sf.jasperreports.pdf.common;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.util.StyledTextListWriter;
import net.sf.jasperreports.export.AccessibilityUtil;
import net.sf.jasperreports.export.type.AccessibilityTagEnum;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Provides support for tagged PDF documents.
 * <p/>
 * PDF files can contain hidden tags that describe the structure of the document. Some of
 * the tags are used by the automated reader tool that reads PDF documents aloud to people
 * with disabilities.
 * <h3>Marking Headings</h3>
 * JasperReports currently supports specifying type 1, 2 and 3 level headings.
 * In order to mark a text field as a level 1 heading, the following custom element property
 * should be used in JRXML:
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.h1" value="full"/&gt;</code>
 * <p/>
 * Value full means that a full <code>&lt;H1&gt;</code> tag will be embedded in the PDF wrapping the
 * current text element.
 * If two or more text fields make up a single level 1 heading, there are two ways to mark
 * the heading:
 * <ul>
 * <li>In the first, the text elements making up the heading are placed inside a frame and
 * the frame is marked with the following custom property:
 * <br/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.h1" value="full"/&gt;</code></li>
 * <li>In the second, the first element of the heading (respective to the Z-Order, or the
 * order in which the elements appear in JRXML) is tagged with:
 * <br/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.h1" value="start"/&gt;</code>
 * <br/>
 * and the last element from the heading (respective to the same order) is marked with
 * <br/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.h1" value="end"/&gt;</code></li>
 * </ul>
 * <p/>
 * Level 2 and level 3 headings are marked the same way, except that the properties are:
 * <p/>
 * <code>net.sf.jasperreports.export.pdf.tag.h2</code>
 * <p/>
 * and
 * <p/>
 * <code>net.sf.jasperreports.export.pdf.tag.h3</code>.
 * <h3>Marking Tables</h3>
 * Tables are comprised of column headers, row headers, and a data section. Each table
 * section is made of cells. Marking table structures in PDF is similar to the way tables are
 * described in HTML and uses the same techniques as those for marking headings.
 * <p/>
 * When marking a table, the user has to indicate in the report template where the table
 * starts and where it ends.
 * <p/>
 * If the entire table is placed in a container, such as a frame element, marking the table
 * requires only marking the parent frame with the following custom element property:
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.table" value="full"/&gt;</code>
 * <p/>
 * However, most of the time, tables cannot be isolated in a frame unless they are
 * subreports, because they generally span multiple report sections and bands. In such
 * cases, marking a table requires marking in JRXML the first and last element making up
 * the table structure.
 * <p/>
 * The first element of the table (probably the first element in the table header) should be
 * marked with the following custom property:
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.table" value="start"/&gt;</code>
 * <p/>
 * The last element of the table should be marked with:
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.table" value="end"/&gt;</code>
 * <p/>
 * Tables are made of rows, and each row has to be precisely delimited within the table
 * structure. This includes the column header rows at the top of the table. Similar to the
 * headings and table marking, a table row can be identified in two ways:
 * <ul>
 * <li>If the entire content that makes up the row is isolated within a frame, the frame can be
 * marked with the following custom property:
 * <br/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.tr" value="full"/&gt;</code>
 * </li>
 * <li>If the content of the row is not grouped in a container frame, its first and last elements
 * (respective to the Z-order or the order in which they appear in JRXML) have to be
 * marked with the following custom properties:
 * <br/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.tr" value="start"/&gt;</code>
 * <br/>
 * for the first element and
 * <br/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.tr" value="start"/&gt;</code>
 * <br/>
 * for the last element.</li>
 * </ul>
 * <p/>
 * Each table row can contain header cells or data cells. Regardless of their type, and
 * similar to headings, tables, and table rows, cells can be marked either by marking a
 * single element representing the cell content (this single cell element can actually be a
 * frame element), or by marking the first and last element from the cell content.
 * <p/>
 * Header cells made of a single element (this single element can actually be a frame) are
 * marked with
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.th" value="full"/&gt;</code>
 * <p/>
 * A header cell made of multiple elements is marked with
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.th" value="start"/&gt;</code>
 * on its first element and
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.th" value="end"/&gt;</code>
 * on its last element.
 * <p/>
 * Normal data cells made of a single element (that can be frame) are marked with
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.td" value="full"/&gt;</code>
 * <p/>
 * Normal data cells made of multiple elements are marked with
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.td" value="start"/&gt;</code>
 * on their first element and
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.td" value="end"/&gt;</code>
 * on their last element.
 * <p/>
 * Just as in HTML tables, cells can span multiple rows and/or columns. Column span and
 * row span values for the current table cell can be specified using the following custom
 * properties on the same element where the cell start was marked (the element with the
 * full or start property marking the cell):
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.colspan" value="&lt;number&gt;"/&gt;</code>
 * <p/>
 * <code>&lt;property name="net.sf.jasperreports.export.pdf.tag.rowspan" value="&lt;number&gt;"/&gt;</code>
 * <h3>PDF Content Reading Order</h3>
 * JasperReports uses the Z-order of the elements as present in the report template
 * (JRXML) to control reading order in the resulting PDF files. This is usually the intended
 * way for the documents to be read, so no specific modifications were required in order to
 * achieve it.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface PdfTagger
{
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_L = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.l";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_LI = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.li";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_21_4
			)
	public static final String PROPERTY_TAG_LBL = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.lbl";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_21_4
			)
	public static final String PROPERTY_TAG_LBODY = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.lbody";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_21_4
			)
	public static final String PROPERTY_TAG_REFERENCE = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.reference";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_21_4
			)
	public static final String PROPERTY_TAG_NOTE = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.note";
	/**
	 * @deprecated Replaced by {@link AccessibilityUtil#PROPERTY_ACCESSIBILITY_TAG} and {@link AccessibilityTagEnum#H1}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_H1 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h1";
	/**
	 * @deprecated Replaced by {@link AccessibilityUtil#PROPERTY_ACCESSIBILITY_TAG} and {@link AccessibilityTagEnum#H2}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_H2 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h2";
	/**
	 * @deprecated Replaced by {@link AccessibilityUtil#PROPERTY_ACCESSIBILITY_TAG} and {@link AccessibilityTagEnum#H3}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_H3 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h3";
	/**
	 * @deprecated Replaced by {@link AccessibilityUtil#PROPERTY_ACCESSIBILITY_TAG} and {@link AccessibilityTagEnum#H4}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_H4 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h4";
	/**
	 * @deprecated Replaced by {@link AccessibilityUtil#PROPERTY_ACCESSIBILITY_TAG} and {@link AccessibilityTagEnum#H5}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_H5 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h5";
	/**
	 * @deprecated Replaced by {@link AccessibilityUtil#PROPERTY_ACCESSIBILITY_TAG} and {@link AccessibilityTagEnum#H6}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_H6 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h6";

	void setLanguage(String language);

	void setPdfProducer(PdfProducer pdfProducer);

	void init();

	void startPage();

	void endPage();

	void startElement(JRPrintElement element);

	void endElement(JRPrintElement element);

	void beginArtifact();

	void endArtifact();

	void startImage(JRPrintImage printImage, float llx, float lly, float urx, float ury);

	void endImage();

	void startText(JRPrintText textElement);

	void startText(JRPrintText textElement, String actualText);

	void endText();

	boolean isFirstLinkParagraph();

	PdfStructureEntry getCurrentLinkTag();
	
	StyledTextListWriter getListWriter();

	PdfStructureEntry getCurrentContentEntry();

	PdfStructureEntry getPageStructureEntry(int pdfPage);

}
