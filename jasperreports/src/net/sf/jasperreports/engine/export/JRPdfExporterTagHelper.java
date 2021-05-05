/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.engine.export;

import java.util.Stack;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfStructureTreeRoot;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintImage;
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
public class JRPdfExporterTagHelper
{
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_TABLE = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.table";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_TR = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.tr";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_TH = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.th";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_TD = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.td";
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
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_H1 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h1";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_H2 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h2";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_H3 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h3";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_H4 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h4";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_H5 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h5";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_2_0
			)
	public static final String PROPERTY_TAG_H6 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h6";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_COLSPAN = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.colspan";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_2
			)
	public static final String PROPERTY_TAG_ROWSPAN = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.rowspan";
	public static final String TAG_START = "start";
	public static final String TAG_END = "end";
	public static final String TAG_FULL = "full";
	
	protected JRPdfExporter exporter;

	protected PdfContentByte pdfContentByte;
	protected PdfWriter pdfWriter;

	protected PdfStructureElement allTag;
	protected Stack<PdfStructureElement> tagStack;
	protected boolean isTagEmpty = true;
	protected int crtCrosstabRowY = -1;
	protected boolean insideCrosstabCellFrame;
	protected boolean isDataCellPrinted;

	protected boolean isTagged;
	protected String language;

	/**
	 *
	 */
	protected JRPdfExporterTagHelper(JRPdfExporter exporter)
	{
		this.exporter = exporter;
	}
	
	/**
	 *
	 */
	protected void setTagged(boolean isTagged)
	{
		this.isTagged = isTagged;
	}
	
	/**
	 *
	 */
	protected void setLanguage(String language)
	{
		this.language = language;
	}
	
	/**
	 *
	 */
	protected void setPdfWriter(PdfWriter pdfWriter)
	{
		this.pdfWriter = pdfWriter;
		
		if (isTagged)
		{
			pdfWriter.setTagged();
		}
	}
	
	/**
	 *
	 */
	protected void init(PdfContentByte pdfContentByte)
	{
		this.pdfContentByte = pdfContentByte;
		
		if (isTagged)
		{
			PdfStructureTreeRoot root = pdfWriter.getStructureTreeRoot();
			
			PdfName pdfNameALL = new PdfName("All");
			root.mapRole(pdfNameALL, PdfName.SECT);
			root.mapRole(PdfName.IMAGE, PdfName.FIGURE);
			root.mapRole(PdfName.TEXT, PdfName.SPAN);
			allTag = new PdfStructureElement(root, pdfNameALL);
			if(pdfWriter.getPDFXConformance() == PdfWriter.PDFA1A)
			{
				root.mapRole(new PdfName("Anchor"), PdfName.NONSTRUCT);
				root.mapRole(PdfName.TEXT, PdfName.SPAN);
			}
			else
			{
				root.mapRole(new PdfName("Anchor"), PdfName.TEXT);
			}
			
			if (language != null)
			{
				allTag.put(PdfName.LANG, new PdfString(language));
			}
			tagStack = new Stack<PdfStructureElement>();
			tagStack.push(allTag);
		}
	}
	
	protected void startPageAnchor()
	{
		if (isTagged)
		{
			PdfStructureElement textTag = new PdfStructureElement(allTag, new PdfName("Anchor"));
			pdfContentByte.beginMarkedContentSequence(textTag);
		}
	}
	
	protected void endPageAnchor()
	{
		if (isTagged)
		{
			pdfContentByte.endMarkedContentSequence();
		}
	}

	protected void startPage()
	{
		crtCrosstabRowY = -1;
		insideCrosstabCellFrame = false;
		isDataCellPrinted = false;
		
	}
	
	protected void endPage()
	{
		if (isTagged)
		{
			if (crtCrosstabRowY >= 0) //crosstab still open
			{
				//end the current row
				//pdfContentByte.endMarkedContentSequence();
				tagStack.pop();
				
				//end the table
				//pdfContentByte.endMarkedContentSequence();
				tagStack.pop();
			}
		}
	}

	protected void startElement(JRPrintElement element)
	{
		if (isTagged)
		{
			JRPrintFrame frame = element instanceof JRPrintFrame ? (JRPrintFrame) element : null;
			
			boolean isCellContentsFrame =
				frame != null
				&& frame.getPropertiesMap().hasProperties()
				&& frame.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE) != null;
			
			if (isCellContentsFrame)
			{
				insideCrosstabCellFrame = true;

				if (crtCrosstabRowY >= 0) //crosstab already started
				{
					if (JRCellContents.TYPE_DATA.equals(frame.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE)))
					{
						isDataCellPrinted = true;
					}

					if (crtCrosstabRowY != frame.getY())
					{
						//this is the first cell on a new row

						//end the current row
						//pdfContentByte.endMarkedContentSequence();
						tagStack.pop();
						
						if (
							isDataCellPrinted
							&& (JRCellContents.TYPE_CROSSTAB_HEADER.equals(frame.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE))
								|| JRCellContents.TYPE_COLUMN_HEADER.equals(frame.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE)))
							)
						{
							//end the table
							//pdfContentByte.endMarkedContentSequence();
							tagStack.pop();

							//start table
							createTableStartTag();
							//start crosstab
							isDataCellPrinted = false;
						}

						//start the new row
						createTrStartTag();

						//keep crosstab open, but mark new row position and frame depth
						crtCrosstabRowY = frame.getY();
					}
				}
				else
				{
					//start table and first row
					createTableStartTag();
					createTrStartTag();
					//start crosstab
					crtCrosstabRowY = frame.getY();
					isDataCellPrinted = false;
				}
			}
			else
			{
				if (crtCrosstabRowY >= 0) //crosstab already started
				{
					if (!insideCrosstabCellFrame)
					{
						//normal element outside crosstab
						
						//end the current row
						//pdfContentByte.endMarkedContentSequence();
						tagStack.pop();
						
						//end the table
						//pdfContentByte.endMarkedContentSequence();
						tagStack.pop();
						
						//end crosstab
						crtCrosstabRowY = -1;
					}
				}
//				else
//				{
//						//normal element outside crosstab
//				}
			}
			
			createStartTags(element);
		}
	}
	
	protected void endElement(JRPrintElement element)
	{
		if (isTagged)
		{
			JRPrintFrame frame = element instanceof JRPrintFrame ? (JRPrintFrame) element : null;
			
			boolean isCellContentsFrame =
				frame != null
				&& frame.getPropertiesMap().hasProperties()
				&& frame.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE) != null;
				
			if (isCellContentsFrame)
			{
				insideCrosstabCellFrame = false;
			}
			
			createEndTags(element);
		}
	}

	protected void startImage(JRPrintImage printImage)
	{
		if (isTagged)
		{
			PdfStructureElement imageTag = new PdfStructureElement(allTag, PdfName.IMAGE);
			pdfContentByte.beginMarkedContentSequence(imageTag);
			if (printImage.getHyperlinkTooltip() != null)
			{
				imageTag.put(PdfName.ALT, new PdfString(printImage.getHyperlinkTooltip()));
			}
		}
	}

	protected void endImage()
	{
		if (isTagged)
		{
			pdfContentByte.endMarkedContentSequence();
		}
	}

	protected void startText(boolean isHyperlink)
	{
		if (isTagged)
		{
//			PdfStructureElement parentTag = tableCellTag == null ? (tableHeaderTag == null ? allTag : tableHeaderTag): tableCellTag;
//			PdfStructureElement textTag = new PdfStructureElement(parentTag, PdfName.TEXT);
			PdfStructureElement textTag = new PdfStructureElement(tagStack.peek(), isHyperlink ? PdfName.LINK : PdfName.TEXT);
			pdfContentByte.beginMarkedContentSequence(textTag);
		}
	}

	protected void startText(String text, boolean isHyperlink)
	{
		if (isTagged)
		{
			PdfDictionary markedContentProps = new PdfDictionary();
			markedContentProps.put(PdfName.ACTUALTEXT, new PdfString(text, PdfObject.TEXT_UNICODE));
			PdfStructureElement textTag = new PdfStructureElement(tagStack.peek(), isHyperlink ? PdfName.LINK : PdfName.TEXT);
			// the following method is part of the patched iText
			pdfContentByte.beginMarkedContentSequence(textTag, markedContentProps);
		}
	}

	protected void endText()
	{
		if (isTagged)
		{
			pdfContentByte.endMarkedContentSequence();
			isTagEmpty = false;
		}
	}

	protected void createStartTags(JRPrintElement element)
	{
		if (element.hasProperties())
		{
			String prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TABLE);
			if (TAG_START.equals(prop) || TAG_FULL.equals(prop))
			{
				createTableStartTag();
			}
			
			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TR);
			if (TAG_START.equals(prop) || TAG_FULL.equals(prop))
			{
				createTrStartTag();
			}
			
			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TH);
			if (TAG_START.equals(prop) || TAG_FULL.equals(prop))
			{
				createThStartTag(element);
			}

			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TD);
			if (TAG_START.equals(prop) || TAG_FULL.equals(prop))
			{
				createTdStartTag(element);
			}
			
			prop = element.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE);
			if (
				JRCellContents.TYPE_CROSSTAB_HEADER.equals(prop) 
				|| JRCellContents.TYPE_COLUMN_HEADER.equals(prop)
				|| JRCellContents.TYPE_ROW_HEADER.equals(prop)
				)
			{
				createThStartTag(element);
			}
			if (JRCellContents.TYPE_DATA.equals(prop))
			{
				createTdStartTag(element);
			}
			
			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_L);
			if (TAG_START.equals(prop) || TAG_FULL.equals(prop))
			{
				createListStartTag();
			}
			
			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_LI);
			if (TAG_START.equals(prop) || TAG_FULL.equals(prop))
			{
				createListItemStartTag(element);
			}

			createStartHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H1), PdfName.H1);
			createStartHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H2), PdfName.H2);
			createStartHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H3), PdfName.H3);
			createStartHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H4), PdfName.H4);
			createStartHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H5), PdfName.H5);
			createStartHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H6), PdfName.H6);
		}
	}


	protected void createStartHeadingTags(String prop, PdfName pdfName)
	{
		if (TAG_START.equals(prop) || TAG_FULL.equals(prop))
		{
			PdfStructureElement headingTag = new PdfStructureElement(tagStack.peek(), pdfName);
			//pdfContentByte.beginMarkedContentSequence(headingTag);
			headingTag.put(PdfName.K, new PdfArray());
			tagStack.push(headingTag);
			isTagEmpty = true;
		}
	}


	protected void createTableStartTag()
	{
		PdfStructureElement tableTag = new PdfStructureElement(allTag, PdfName.TABLE);
		//pdfContentByte.beginMarkedContentSequence(tableTag);
		tableTag.put(PdfName.K, new PdfArray());
		tagStack.push(tableTag);
	}
		
	
	protected void createTrStartTag()
	{
		PdfStructureElement tableRowTag = new PdfStructureElement(tagStack.peek(), PdfName.TABLEROW);
		//pdfContentByte.beginMarkedContentSequence(tableRowTag);
		tableRowTag.put(PdfName.K, new PdfArray());
		tagStack.push(tableRowTag);
	}
		
	
	protected void createThStartTag(JRPrintElement element)
	{
		PdfStructureElement tableHeaderTag = new PdfStructureElement(tagStack.peek(), PdfName.TH);
		//pdfContentByte.beginMarkedContentSequence(tableHeaderTag);
		tableHeaderTag.put(PdfName.K, new PdfArray());
		tagStack.push(tableHeaderTag);
		isTagEmpty = true;
		
		createSpanTags(element, tableHeaderTag);
	}

	
	protected void createTdStartTag(JRPrintElement element)
	{
		PdfStructureElement tableCellTag = new PdfStructureElement(tagStack.peek(), PdfName.TD);
		//pdfContentByte.beginMarkedContentSequence(tableCellTag);
		tableCellTag.put(PdfName.K, new PdfArray());
		tagStack.push(tableCellTag);
		isTagEmpty = true;
		
		createSpanTags(element, tableCellTag);
	}
		

	protected void createSpanTags(JRPrintElement element, PdfStructureElement parentTag)
	{
		Integer colSpan = null;
		try	{
			String colSpanProp = element.getPropertiesMap().getProperty(PROPERTY_TAG_COLSPAN);
			if (colSpanProp != null) {
				colSpan = Integer.valueOf(colSpanProp);
			}
		} catch (NumberFormatException e) {}
		
		if (colSpan == null) {
			try	{
				String colSpanProp = element.getPropertiesMap().getProperty(JRCellContents.PROPERTY_COLUMN_SPAN);
				if (colSpanProp != null) {
					colSpan = Integer.valueOf(colSpanProp);
				}
			} catch (NumberFormatException ex) {}
		}
		
		Integer rowSpan = null;
		try {
			String rowSpanProp = element.getPropertiesMap().getProperty(PROPERTY_TAG_ROWSPAN);
			if (rowSpanProp != null) {
				rowSpan = Integer.valueOf(rowSpanProp);
			}
		} catch (NumberFormatException e) {}
		
		if (rowSpan == null) {
			try {
				String rowSpanProp = element.getPropertiesMap().getProperty(JRCellContents.PROPERTY_ROW_SPAN);
				if (rowSpanProp != null) {
					rowSpan = Integer.valueOf(rowSpanProp);
				}
			} catch (NumberFormatException ex) {}			
		}
		
		if (colSpan != null && colSpan > 1 || rowSpan != null && rowSpan > 1)
		{
			PdfArray a = new PdfArray();
			PdfDictionary dict = new PdfDictionary();
			if (colSpan != null && colSpan > 1)
			{
				dict.put(new PdfName("ColSpan"), new PdfNumber(colSpan));
			}
			if (rowSpan != null && rowSpan > 1)
			{
				dict.put(new PdfName("RowSpan"), new PdfNumber(rowSpan));
			}
			dict.put(PdfName.O, PdfName.TABLE);
			a.add(dict);
			parentTag.put(PdfName.A, a);
		}
	}


	protected void createListStartTag()
	{
		PdfStructureElement listTag = new PdfStructureElement(allTag, PdfName.L);
		//pdfContentByte.beginMarkedContentSequence(tableTag);
		listTag.put(PdfName.K, new PdfArray());
		tagStack.push(listTag);
	}
		
	
	protected void createListItemStartTag(JRPrintElement element)
	{
		PdfStructureElement listItemTag = new PdfStructureElement(tagStack.peek(), PdfName.LI);
		//pdfContentByte.beginMarkedContentSequence(tableHeaderTag);
		listItemTag.put(PdfName.K, new PdfArray());
		tagStack.push(listItemTag);
		isTagEmpty = true;
	}

	
	protected void createEndTags(JRPrintElement element)// throws DocumentException, IOException, JRException
	{
		if (element.hasProperties())
		{
			createEndHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H6));
			createEndHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H5));
			createEndHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H4));
			createEndHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H3));
			createEndHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H2));
			createEndHeadingTags(element.getPropertiesMap().getProperty(PROPERTY_TAG_H1));

			String prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_LI);
			if (TAG_END.equals(prop) || TAG_FULL.equals(prop))
			{
				//pdfContentByte.endMarkedContentSequence();
				
				if (isTagEmpty)
				{
					pdfContentByte.beginMarkedContentSequence(new PdfStructureElement(tagStack.peek(), PdfName.SPAN));
					pdfContentByte.endMarkedContentSequence();
				}
				
				tagStack.pop();
			}

			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_L);
			if (TAG_END.equals(prop) || TAG_FULL.equals(prop))
			{
				//pdfContentByte.endMarkedContentSequence();
				tagStack.pop();
			}

			prop = element.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE);
			if (
				prop != null 
				&& (JRCellContents.TYPE_CROSSTAB_HEADER.equals(prop) 
					|| JRCellContents.TYPE_COLUMN_HEADER.equals(prop)
					|| JRCellContents.TYPE_ROW_HEADER.equals(prop)
					|| JRCellContents.TYPE_DATA.equals(prop)))
			{
				//pdfContentByte.endMarkedContentSequence();
				
				if (isTagEmpty)
				{
					pdfContentByte.beginMarkedContentSequence(new PdfStructureElement(tagStack.peek(), PdfName.SPAN));
					pdfContentByte.endMarkedContentSequence();
				}

				tagStack.pop();
			}
			
			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TD);
			if (TAG_END.equals(prop) || TAG_FULL.equals(prop))
			{
				//pdfContentByte.endMarkedContentSequence();
				
				if (isTagEmpty)
				{
					pdfContentByte.beginMarkedContentSequence(new PdfStructureElement(tagStack.peek(), PdfName.SPAN));
					pdfContentByte.endMarkedContentSequence();
				}

				tagStack.pop();
			}
			
			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TH);
			if (TAG_END.equals(prop) || TAG_FULL.equals(prop))
			{
				//pdfContentByte.endMarkedContentSequence();
				
				if (isTagEmpty)
				{
					pdfContentByte.beginMarkedContentSequence(new PdfStructureElement(tagStack.peek(), PdfName.SPAN));
					pdfContentByte.endMarkedContentSequence();
				}
				
				tagStack.pop();
			}

			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TR);
			if (TAG_END.equals(prop) || TAG_FULL.equals(prop))
			{
				//pdfContentByte.endMarkedContentSequence();
				tagStack.pop();
			}

			prop = element.getPropertiesMap().getProperty(PROPERTY_TAG_TABLE);
			if (TAG_END.equals(prop) || TAG_FULL.equals(prop))
			{
				//pdfContentByte.endMarkedContentSequence();
				tagStack.pop();
			}
		}
//		else
//		{
//			//check to see if this is the first element without tag properties after a table
//			if (tagStack.size() > 2) // check to see if we are at least inside a table row (table/tr)
//			{
//				PdfStructureElement tag = tagStack.peek();
//				if (
//					PdfName.TABLEROW.equals(tag.get(PdfName.S))
//					&& PdfName.TABLE.equals(tag.getParent().get(PdfName.S))
//					)
//				{
//					// take out both row and table tags
//					tagStack.pop();
//					tagStack.pop();
//				}
//			}
//		}
	}


	protected void createEndHeadingTags(String prop)
	{
		if (TAG_END.equals(prop) || TAG_FULL.equals(prop))
		{
			//pdfContentByte.endMarkedContentSequence(); 

			if (isTagEmpty)
			{
				pdfContentByte.beginMarkedContentSequence(new PdfStructureElement(tagStack.peek(), PdfName.SPAN));
				pdfContentByte.endMarkedContentSequence();
			}

			tagStack.pop();
		}
	}
}
