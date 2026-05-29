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

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.type.OrientationEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StyleBuilder
{
	/**
	 * 
	 */
	private WriterHelper writer;
	private int reportDpi;
	
	/**
	 * 
	 */
	public StyleBuilder(WriterHelper writer, int reportDpi)
	{
		this.writer = writer;
		this.reportDpi = reportDpi;
	}

	/**
	 * 
	 */
	public void buildBeforeAutomaticStyles(JasperPrint jasperPrint)
	{
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		writer.write("<office:document-styles");
		writer.write(" xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\"");
		writer.write(" xmlns:style=\"urn:oasis:names:tc:opendocument:xmlns:style:1.0\"");
		writer.write(" xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"");
		writer.write(" xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\"");
		writer.write(" xmlns:draw=\"urn:oasis:names:tc:opendocument:xmlns:drawing:1.0\"");
		writer.write(" xmlns:fo=\"urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0\"");
		writer.write(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"");
		writer.write(" xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
		writer.write(" xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\"");
		writer.write(" xmlns:number=\"urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0\"");
		writer.write(" xmlns:svg=\"urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0\"");
		writer.write(" xmlns:chart=\"urn:oasis:names:tc:opendocument:xmlns:chart:1.0\"");
		writer.write(" xmlns:dr3d=\"urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0\"");
		writer.write(" xmlns:math=\"http://www.w3.org/1998/Math/MathML\"");
		writer.write(" xmlns:form=\"urn:oasis:names:tc:opendocument:xmlns:form:1.0\"");
		writer.write(" xmlns:script=\"urn:oasis:names:tc:opendocument:xmlns:script:1.0\"");
		writer.write(" xmlns:ooo=\"http://openoffice.org/2004/office\"");
		writer.write(" xmlns:ooow=\"http://openoffice.org/2004/writer\"");
		writer.write(" xmlns:oooc=\"http://openoffice.org/2004/calc\"");
		writer.write(" xmlns:dom=\"http://www.w3.org/2001/xml-events\"");
		writer.write(" xmlns:xforms=\"http://www.w3.org/2002/xforms\"");
		writer.write(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
		writer.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		writer.write(" office:version=\"");		
		writer.write(ContentBuilder.VERSION);		
		writer.write("\">\n");		

		writer.write(" <office:styles>\n");	
		writer.write("<draw:stroke-dash draw:name=\"Dashed\" draw:display-name=\"Dashed\" " +
			"draw:style=\"rect\" draw:dots1=\"1\" draw:dots1-length=\"0.05cm\" draw:dots2=\"1\" " +
			"draw:dots2-length=\"0.05cm\" draw:distance=\"0.05cm\"/>");
		writer.write(" <style:default-style style:family=\"paragraph\"><style:paragraph-properties style:tab-stop-distance=\"" +
				LengthUtil.inchFloor4Dec(new JRBasePrintText(jasperPrint.getDefaultStyleProvider()).getParagraph().getTabStopWidth(), reportDpi) +
				"in\"/></style:default-style>\n");	
		writer.write(" </office:styles>\n");	
		writer.write(" <office:automatic-styles>\n");	
	}
	
	/**
	 * 
	 */
	public void buildPageLayout(int pageFormatIndex, PrintPageFormat pageFormat) 
	{
			writer.write("<style:page-layout");
			writer.write(" style:name=\"page_" + pageFormatIndex + "\">\n");
			
			writer.write("<style:page-layout-properties");
			writer.write(" fo:page-width=\"" + LengthUtil.inchRound4Dec(pageFormat.getPageWidth(), reportDpi) +"in\"");
			writer.write(" fo:page-height=\"" + LengthUtil.inchRound4Dec(pageFormat.getPageHeight(), reportDpi) +"in\"");//FIXMEODT we probably need some actualHeight trick
			writer.write(" fo:margin-top=\"0in\"");//FIXMEODT if first cell on page is for frame (nested table), this forcing of margins to zero does not work
			writer.write(" fo:margin-bottom=\"0in\"");
			writer.write(" fo:margin-left=\"0in\"");
			writer.write(" fo:margin-right=\"0in\"");

			if (pageFormat.getOrientation() == OrientationEnum.LANDSCAPE)
			{
				writer.write(" style:print-orientation=\"landscape\"");
			}
			else
			{
				writer.write(" style:print-orientation=\"portrait\"");
			}
			
			writer.write("/>\n");
			writer.write("</style:page-layout>\n");
	}

	/**
	 * 
	 */
	public void buildMasterPages(int pageFormatCount) 
	{
		writer.write(" </office:automatic-styles>\n");
		writer.write(" <office:master-styles>\n");	

		for (int i = 0; i <= pageFormatCount; i++)
		{
			writer.write("<style:master-page style:name=\"master_");
			writer.write(String.valueOf(i));
			writer.write("\" style:page-layout-name=\"page_");
			writer.write(String.valueOf(i));
			writer.write("\"/>\n");
		}

		writer.write(" </office:master-styles>\n");	
		writer.write("</office:document-styles>\n");
	}

}
