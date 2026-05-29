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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.Writer;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.LengthUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DocxHeaderHelper extends DocxDocumentHelper
{

	/**
	 * 
	 */
	public DocxHeaderHelper(JasperReportsContext jasperReportsContext, Writer writer, int dpi)
	{
		super(jasperReportsContext, writer, dpi);
	}
	
	@Override
	public void exportHeader(PrintPageFormat pageFormat)
	{
		int pageWidth = LengthUtil.emu(pageFormat.getPageWidth() - pageFormat.getLeftMargin() - pageFormat.getRightMargin(), dpi);
		int pageHeight = LengthUtil.emu(pageFormat.getPageHeight() - pageFormat.getTopMargin() - pageFormat.getBottomMargin(), dpi);
		
		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		write("<w:hdr\n");
		write(" xmlns:ve=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"\n");
		write(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n");
		write(" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n");
		write(" xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"\n");
		write(" xmlns:v=\"urn:schemas-microsoft-com:vml\"\n");
		write(" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n");
		write(" xmlns:w10=\"urn:schemas-microsoft-com:office:word\"\n");
		write(" xmlns:wp14=\"http://schemas.microsoft.com/office/word/2010/wordml\"\n");
		write(" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n");
		write(" xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"\n");
		write(" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n");
		write(" xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\"\n"); 
		write(" xmlns:wps=\"http://schemas.microsoft.com/office/word/2010/wordprocessingShape\">\n");


		write("    <w:p>\n"
				+ "        <w:pPr>\n"
				+ "            <w:pStyle w:val=\"Header\" />\n"
				+ "        </w:pPr>\n"
				+ "        <w:r>\n"
				+ "            <w:rPr>\n"
				+ "                <w:noProof />\n"
				+ "            </w:rPr>\n"
				+ "                    <w:drawing>\n"
				+ "                        <wp:anchor distT=\"0\" distB=\"0\" distL=\"0\" distR=\"0\" simplePos=\"0\"\n"
				+ "                            relativeHeight=\"251659264\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"1\"\n"
				+ "                            allowOverlap=\"1\">\n"
				+ "                            <wp:simplePos x=\"0\" y=\"0\" />\n"
				+ "                            <wp:positionH relativeFrom=\"margin\">\n"
				+ "                                <wp:posOffset>0</wp:posOffset>\n"
				+ "                            </wp:positionH>\n"
				+ "                            <wp:positionV relativeFrom=\"margin\">\n"
				+ "                                <wp:posOffset>0</wp:posOffset>\n"
				+ "                            </wp:positionV>\n"
				+ "                            <wp:extent cx=\"" + pageWidth + "\" cy=\"" + pageHeight + "\" />\n"
				+ "                            <wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\" />\n"
				+ "                            <wp:wrapNone />\n"
				+ "                            <wp:docPr id=\"1\" name=\"Text Box 1\" />\n"
				+ "                            <wp:cNvGraphicFramePr />\n"
				+ "                            <a:graphic\n"
				+ "                                xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">\n"
				+ "                                <a:graphicData\n"
				+ "                                    uri=\"http://schemas.microsoft.com/office/word/2010/wordprocessingShape\">\n"
				+ "                                    <wps:wsp>\n"
				+ "                                        <wps:cNvSpPr txBox=\"1\" />\n"
				+ "                                        <wps:spPr>\n"
				+ "                                            <a:xfrm>\n"
				+ "                                                <a:off x=\"0\" y=\"0\" />\n"
				+ "                                                <a:ext cx=\"" + pageWidth + "\" cy=\"" + pageHeight + "\" />\n"
				+ "                                            </a:xfrm>\n"
				+ "                                            <a:prstGeom prst=\"rect\">\n"
				+ "                                                <a:avLst />\n"
				+ "                                            </a:prstGeom>\n"
				+ "                                            <a:ln w=\"6350\">\n"
				+ "                                                <a:noFill/>\n"
				+ "                                            </a:ln>\n"
				+ "                                        </wps:spPr>\n"
				+ "                                        <wps:txbx>\n"
				+ "                                            <w:txbxContent>\n"
				+ "");		
	}

	@Override
	public void exportFooter()
	{
		write("                                                <w:p/>\n"
				+ "                                            </w:txbxContent>\n"
				+ "                                        </wps:txbx>\n"
				+ "                                        <wps:bodyPr rot=\"0\" spcFirstLastPara=\"0\"\n"
				+ "                                            vertOverflow=\"overflow\" horzOverflow=\"overflow\"\n"
				+ "                                            vert=\"horz\" wrap=\"square\" lIns=\"0\" tIns=\"0\"\n"
				+ "                                            rIns=\"0\" bIns=\"0\" numCol=\"1\" spcCol=\"0\"\n"
				+ "                                            rtlCol=\"0\" fromWordArt=\"0\" anchor=\"t\" anchorCtr=\"0\"\n"
				+ "                                            forceAA=\"0\" compatLnSpc=\"1\">\n"
				+ "                                            <a:prstTxWarp prst=\"textNoShape\">\n"
				+ "                                                <a:avLst />\n"
				+ "                                            </a:prstTxWarp>\n"
				+ "                                            <a:noAutofit />\n"
				+ "                                        </wps:bodyPr>\n"
				+ "                                    </wps:wsp>\n"
				+ "                                </a:graphicData>\n"
				+ "                            </a:graphic>\n"
				+ "                            <wp14:sizeRelH relativeFrom=\"margin\">\n"
				+ "                                <wp14:pctWidth>0</wp14:pctWidth>\n"
				+ "                            </wp14:sizeRelH>\n"
				+ "                            <wp14:sizeRelV relativeFrom=\"margin\">\n"
				+ "                                <wp14:pctHeight>0</wp14:pctHeight>\n"
				+ "                            </wp14:sizeRelV>\n"
				+ "                        </wp:anchor>\n"
				+ "                    </w:drawing>\n"
				+ "        </w:r>\n"
				+ "    </w:p>");

		write("</w:hdr>\n");
	}

}
