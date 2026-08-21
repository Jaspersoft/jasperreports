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
 * Special thanks to Google 'Summer of Code 2005' program for supporting this development
 * 
 * Contributors:
 * Majid Ali Khan - majidkk@users.sourceforge.net
 * Frank Schönheit - Frank.Schoenheit@Sun.COM
 */
package net.sf.jasperreports.engine.export.oasis;

import java.awt.Color;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.engine.util.StyledTextWriteContext;
import net.sf.jasperreports.export.OdtReportConfiguration;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class TableBuilder 
{
	/**
	 *
	 */
	private final DocumentBuilder documentBuilder;
	protected String tableName;
	private final JasperPrint jasperPrint;
	private int pageFormatIndex;
	private final WriterHelper bodyWriter;
	private final WriterHelper styleWriter;
	private final StyleCache styleCache;
	private boolean isFrame;
	private boolean isPageBreak;
	private String tableStyleName;
	private Map<Integer, String> rowStyles;
	private Map<Integer, String> columnStyles;
	private Color tabColor;
	private boolean rowTagOpen;
	

	protected TableBuilder(
		DocumentBuilder documentBuilder,
		JasperPrint jasperPrint,
		String name, 
		WriterHelper bodyWriter,
		WriterHelper styleWriter,
		StyleCache styleCache,
		Map<Integer, String> rowStyles,
		Map<Integer, String> columnStyles,
		Color tabColor
		) 
	{
		this.documentBuilder = documentBuilder;
		this.jasperPrint = jasperPrint;

		isFrame = true;
		isPageBreak = false;
		
		this.bodyWriter = bodyWriter;
		this.styleWriter = styleWriter;
		this.styleCache = styleCache;

		this.tableName = "TBL_" + name;
		this.rowStyles = rowStyles == null ? new HashMap<>() : rowStyles;
		this.columnStyles = columnStyles == null ? new HashMap<>() : columnStyles;
		this.tabColor = tabColor;
	}
	
	protected TableBuilder(
			DocumentBuilder documentBuilder,
			JasperPrint jasperPrint,
			String name, 
			WriterHelper bodyWriter,
			WriterHelper styleWriter,
			StyleCache styleCache,
			Map<Integer, String> rowStyles,
			Map<Integer, String> columnStyles
			) 
		{
			//used in ODT exporter only
			this(documentBuilder, jasperPrint, name, bodyWriter, styleWriter, styleCache, rowStyles, columnStyles, null);
		}
	

	protected TableBuilder(
		DocumentBuilder documentBuilder,
		JasperPrint jasperPrint,
		int pageFormatIndex,
		int pageIndex,
		WriterHelper bodyWriter,
		WriterHelper styleWriter,
		StyleCache styleCache,
		Map<Integer, String> rowStyles,
		Map<Integer, String> columnStyles,
		Color tabColor
		) 
	{
		this.documentBuilder = documentBuilder;
		this.jasperPrint = jasperPrint;

		isFrame = false;
		isPageBreak = (pageFormatIndex != 0 || pageIndex != 0);
		
		this.pageFormatIndex = pageFormatIndex;
		this.bodyWriter = bodyWriter;
		this.styleWriter = styleWriter;
		this.styleCache = styleCache;

		this.tableName = "TBL_" + pageFormatIndex + "_" + pageIndex;
		this.rowStyles = rowStyles == null ? new HashMap<>() : rowStyles;
		this.columnStyles = columnStyles == null ? new HashMap<>() : columnStyles;
		this.tabColor = tabColor;
	}


	protected TableBuilder(
			DocumentBuilder documentBuilder,
			JasperPrint jasperPrint,
			int pageFormatIndex,
			int pageIndex,
			WriterHelper bodyWriter,
			WriterHelper styleWriter,
			StyleCache styleCache,
			Map<Integer, String> rowStyles,
			Map<Integer, String> columnStyles
			) 
		{
			//used in ODT exporter only
			this(documentBuilder, jasperPrint, pageFormatIndex, pageIndex, bodyWriter, styleWriter, styleCache, rowStyles, columnStyles, null);
		}


	public void buildTableStyle(int width) 
	{
		try {
			  this.tableStyleName = styleCache.getTableStyle(width, pageFormatIndex, isFrame, isPageBreak, tabColor);
		} catch (IOException e) {
			throw new JRRuntimeException(e);
		}
	}
	
	public void buildTableHeader() 
	{
		bodyWriter.write("<table:table");
		if (isFrame)
		{
			bodyWriter.write(" is-subtable=\"true\"");
		}
		bodyWriter.write(" table:name=\"");
		bodyWriter.write(tableName);
		bodyWriter.write("\"");
		bodyWriter.write(" table:style-name=\"");
		bodyWriter.write(tableStyleName);
		bodyWriter.write("\"");
		bodyWriter.write(">\n");
	}
	
	public void buildTableFooter() 
	{
		bodyWriter.write("</table:table>\n");
	}
	
	public void buildRowStyle(int rowIndex, int rowHeight) 
	{
		try {
			this.rowStyles.put(rowHeight, styleCache.getRowStyle(rowHeight));
		} catch (IOException e) {
			throw new JRRuntimeException(e);
		}
	}

	public void buildRowHeader(int rowHeight) 
	{
		bodyWriter.write("<table:table-row");
		bodyWriter.write(" table:style-name=\"" + rowStyles.get(rowHeight) + "\"");
		bodyWriter.write(">\n");
		rowTagOpen = true;
	}
	
	public void buildRowFooter() 
	{
		if (rowTagOpen)
		{
			bodyWriter.write("</table:table-row>\n");
			rowTagOpen = false;
		}
	}
	
	public void buildRow(int rowIndex, int rowHeight) 
	{
		if (rowIndex > 0)
		{
			buildRowFooter();
		}
		buildRowHeader(rowHeight);
	}
	
	public void buildColumnStyle(int colIndex, int colWidth) 
	{
		try {
			this.columnStyles.put(colWidth, styleCache.getColumnStyle(colWidth));
		} catch (IOException e) {
			throw new JRRuntimeException(e);
		}
	}

	public void buildColumnHeader(int colWidth) 
	{
		bodyWriter.write("<table:table-column");		
		bodyWriter.write(" table:style-name=\"" + columnStyles.get(colWidth) + "\"");
		bodyWriter.write(">\n");
	}

	public void buildColumnFooter() 
	{
		bodyWriter.write("</table:table-column>\n");		
	}

	public void buildCellHeader(String cellStyleName, int colSpan, int rowSpan) 
	{
		//FIXMEODT officevalue bodyWriter.write("<table:table-cell office:value-type=\"string\"");
		bodyWriter.write("<table:table-cell");
		if (cellStyleName != null)
		{
			bodyWriter.write(" table:style-name=\"" + cellStyleName + "\"");
		}
		if (colSpan > 1)
		{
			bodyWriter.write(" table:number-columns-spanned=\"" + colSpan + "\"");
		}
		if (rowSpan > 1)
		{
			bodyWriter.write(" table:number-rows-spanned=\"" + rowSpan + "\"");
		}
		
		bodyWriter.write(">\n");
	}

	public void buildCellFooter()
	{
		bodyWriter.write("</table:table-cell>\n");
	}
	

	/**
	 *
	 */
	public void exportRectangle(JRPrintGraphicElement rectangle, JRExporterGridCell gridCell)
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = box.getPen();
		pen.setLineColor(rectangle.getLinePen().getLineColor());
		pen.setLineStyle(rectangle.getLinePen().getLineStyle());
		pen.setLineWidth(rectangle.getLinePen().getLineWidth());

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box

		buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());
		buildCellFooter();
	}

	
	/**
	 *
	 */
	public void exportLine(JRPrintLine line, JRExporterGridCell gridCell)
	{
		buildCellHeader(null, gridCell.getColSpan(), gridCell.getRowSpan());

		double x1, y1, x2, y2;

		if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)
		{
			x1 = 0;
			y1 = 0;
			x2 = line.getWidth() - 1;
			y2 = line.getHeight() - 1;
		}
		else
		{
			x1 = 0;
			y1 = line.getHeight() - 1;
			x2 = line.getWidth() - 1;
			y2 = 0;
		}

		bodyWriter.write("<text:p>");
		documentBuilder.insertPageAnchor(this);
		bodyWriter.write(
				"<draw:line text:anchor-type=\"paragraph\" "
				+ "draw:style-name=\"" + styleCache.getGraphicStyle(line) + "\" "
				+ "svg:x1=\"" + LengthUtil.inchFloor4Dec(x1) + "in\" "
				+ "svg:y1=\"" + LengthUtil.inchFloor4Dec(y1) + "in\" "
				+ "svg:x2=\"" + LengthUtil.inchFloor4Dec(x2) + "in\" "
				+ "svg:y2=\"" + LengthUtil.inchFloor4Dec(y2) + "in\">"
				//+ "</draw:line>"
				+ "<text:p/></draw:line>"
				+ "</text:p>"
				);
		buildCellFooter();
	}

	
	/**
	 *
	 */
	public void exportEllipse(JRPrintEllipse ellipse, JRExporterGridCell gridCell)
	{
		buildCellHeader(null, gridCell.getColSpan(), gridCell.getRowSpan());
		bodyWriter.write("<text:p>");
		documentBuilder.insertPageAnchor(this);
		bodyWriter.write(
			"<draw:ellipse text:anchor-type=\"paragraph\" "
			+ "draw:style-name=\"" + styleCache.getGraphicStyle(ellipse) + "\" "
			+ "svg:width=\"" + LengthUtil.inchFloor4Dec(ellipse.getWidth()) + "in\" "
			+ "svg:height=\"" + LengthUtil.inchFloor4Dec(ellipse.getHeight()) + "in\" "
			+ "svg:x=\"0in\" "
			+ "svg:y=\"0in\">"
			+ "<text:p/></draw:ellipse></text:p>"
			);
		buildCellFooter();
	}


	/**
	 *
	 */
	public void exportText(JRPrintText text, JRExporterGridCell gridCell, boolean shrinkToFit, boolean wrapText, boolean isIgnoreTextFormatting)
	{
		buildCellHeader((isIgnoreTextFormatting ? null : styleCache.getCellStyle(gridCell, shrinkToFit, wrapText)), gridCell.getColSpan(), gridCell.getRowSpan());
		
		bodyWriter.write("<text:p text:style-name=\"");
		bodyWriter.write(styleCache.getParagraphStyle(text, isIgnoreTextFormatting));
		bodyWriter.write("\">");
		documentBuilder.insertPageAnchor(this);
		if (text.getAnchorName() != null)
		{
			exportAnchor(JRStringUtil.xmlEncode(text.getAnchorName()));
		}

		exportTextContents(text);

		bodyWriter.write("</text:p>\n");

		buildCellFooter();
	}


	/**
	 *
	 */
	protected void exportTextContents(JRPrintText text)
	{
		boolean startedHyperlink = startHyperlink(text, true);

		exportStyledText(text, startedHyperlink, false);

		if (startedHyperlink)
		{
			endHyperlink(true);
		}
	}


	/**
	 *
	 */
	protected void exportStyledText(JRPrintText text, boolean startedHyperlink, boolean isIgnoreTextFormatting)
	{
		JRStyledText styledText = documentBuilder.getStyledText(text);
		if (styledText != null && styledText.length() > 0)
		{
			exportStyledText(styledText, documentBuilder.getTextLocale(text), startedHyperlink, isIgnoreTextFormatting);
		}
	}


	/**
	 *
	 */
	protected void exportStyledText(JRStyledText styledText, Locale locale, boolean startedHyperlink, boolean isIgnoreTextFormatting)
	{
		StyledTextWriteContext context = new StyledTextWriteContext();
		
		String text = styledText.getText();

		int runLimit = 0;

		AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();

		while (runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length())
		{
			Map<Attribute,Object> attributes = iterator.getAttributes();

			String runText = text.substring(iterator.getIndex(), runLimit);

			context.next(attributes, runText);

			//if (context.listItemStartsWithNewLine() && !context.isListItemStart() && (context.isListItemEnd() || context.isListStart() || context.isListEnd()))
			//{
			//	runText = runText.substring(1);
			//}

			if (runText.length() > 0)
			{
				String bulletText = JRStyledTextUtil.getIndentedBulletText(context);
				
				exportStyledTextRun(
					attributes, 
					(bulletText == null ? "" : bulletText) + runText,
					locale,
					startedHyperlink,
					isIgnoreTextFormatting
					);
			}

			iterator.setIndex(runLimit);
		}
	}


	/**
	 *
	 */
	protected void exportStyledTextRun(
			Map<AttributedCharacterIterator.Attribute, Object> attributes, 
			String text, 
			Locale locale, 
			boolean startedHyperlink,
			boolean isIgnoreTextFormatting
			)
	{
		startTextSpan(attributes, text, locale, isIgnoreTextFormatting);

		boolean localHyperlink = false;

		if (!startedHyperlink)
		{
			JRPrintHyperlink hyperlink = (JRPrintHyperlink)attributes.get(JRTextAttribute.HYPERLINK);
			if (hyperlink != null)
			{
				localHyperlink = startHyperlink(hyperlink, true);
			}
		}
		
		writeText(text);

		if (localHyperlink)
		{
			endHyperlink(true);
		}

		endTextSpan();
	}


	/**
	 *
	 */
	protected void startTextSpan(Map<AttributedCharacterIterator.Attribute, Object> attributes, String text, Locale locale, boolean isIgnoreTextFormatting)
	{
		bodyWriter.write("<text:span");
		if (attributes != null)
		{
			String textSpanStyleName = styleCache.getTextSpanStyle(attributes, text, locale, isIgnoreTextFormatting);
			bodyWriter.write(" text:style-name=\"" + textSpanStyleName + "\"");
		}
		bodyWriter.write(">");
	}

	
	/**
	 *
	 */
	protected void endTextSpan()
	{
		bodyWriter.write("</text:span>");
	}

	
	/**
	 *
	 */
	protected void writeText(String text)
	{
		if (text != null)
		{
			bodyWriter.write(Utility.replaceNewLineWithLineBreak(JRStringUtil.xmlEncode(text, documentBuilder.getInvalidCharReplacement())));//FIXMEODT try something nicer for replace
		}
	}


	/**
	 *
	 */
	protected void exportAnchor(String anchorName)
	{
		bodyWriter.write("<text:bookmark text:name=\"");
		bodyWriter.write(anchorName);
		bodyWriter.write("\"/>");
	}

	
	/**
	 *
	 */
	protected String getIgnoreHyperlinkProperty()
	{
		return OdtReportConfiguration.PROPERTY_IGNORE_HYPERLINK;
	}

	
	/**
	 *
	 */
	protected boolean startHyperlink(JRPrintHyperlink link, boolean isText)
	{
		return startHyperlink(link, isText, true);
	}
	
	
	/**
	 *
	 */
	protected boolean startHyperlink(JRPrintHyperlink link, boolean isText, boolean isOnePagePerSheet)
	{
		String href = null;

		String ignLnkPropName = getIgnoreHyperlinkProperty();
		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(ignLnkPropName, link);
		if (ignoreHyperlink == null)
		{
			ignoreHyperlink = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(jasperPrint, ignLnkPropName, false);
		}

		if (!ignoreHyperlink)
		{
			href = documentBuilder.getHyperlinkURL(link, isOnePagePerSheet);
		}
		
		if (href != null)
		{
			writeHyperlink(link, href, isText);
		}

		return href != null;
	}


	/**
	 *
	 */
	protected void writeHyperlink(JRPrintHyperlink link, String href, boolean isText)
	{
		if (isText)
		{
			bodyWriter.write("<text:a xlink:href=\"");
		}
		else
		{
			bodyWriter.write("<draw:a xlink:type=\"simple\" xlink:href=\"");
		}
		bodyWriter.write(JRStringUtil.xmlEncode(href));
		bodyWriter.write("\"");


		String target = getHyperlinkTarget(link);//FIXMETARGET
		if (target != null)
		{
			bodyWriter.write(" office:target-frame-name=\"");
			bodyWriter.write(target);
			bodyWriter.write("\"");
			if (target.equals("_blank"))
			{
				bodyWriter.write(" xlink:show=\"new\"");
			}
		}
/*
 * tooltips are unavailable for the moment
 *
		if (link.getHyperlinkTooltip() != null)
		{
			bodyWriter.write(" xlink:title=\"");
			bodyWriter.write(JRStringUtil.xmlEncode(link.getHyperlinkTooltip()));
			bodyWriter.write("\"");
		}
*/
		bodyWriter.write(">");
	}


	/**
	 *
	 */
	protected void endHyperlink(boolean isText)
	{
		if (isText)
		{
			bodyWriter.write("</text:a>");
		}
		else
		{
			bodyWriter.write("</draw:a>");
		}
	}


	/**
	 *
	 */
	protected String getHyperlinkTarget(JRPrintHyperlink link)
	{
		String target = null;
		switch (link.getHyperlinkTarget())
		{
			case SELF :
			{
				target = "_self";
				break;
			}
			case BLANK :
			default :
			{
				target = "_blank";
				break;
			}
		}
		return target;
	}

	
	/**
	 *
	 */
	protected JasperReportsContext getJasperReportsContext()
	{
		return documentBuilder.getJasperReportsContext();
	}

	public String getTableName() {
		return tableName;
	}
}