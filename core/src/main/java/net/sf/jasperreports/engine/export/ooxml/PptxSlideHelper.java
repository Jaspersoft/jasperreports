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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.Writer;

import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PptxSlideHelper extends BaseHelper
{

	
	/**
	 *
	 */
	private PptxSlideRelsHelper slideRelsHelper;

	/**
	 * 
	 */
	public PptxSlideHelper(JasperReportsContext jasperReportsContext, Writer writer, PptxSlideRelsHelper slideRelsHelper)
	{
		super(jasperReportsContext, writer);
		
		this.slideRelsHelper = slideRelsHelper;
	}

	
	/**
	 *
	 */
	public void exportHeader(boolean isSlideMaster, boolean hideSlideMaster)
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		write("<p:sld" + (isSlideMaster ? "Master" : "") + (hideSlideMaster ? " showMasterSp=\"0\"" : "") + "\n");
		write("xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n"); 
		write("xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n"); 
		write("xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\">\n");
		write("<p:cSld>\n");
		write("<p:spTree>\n");
		write("<p:nvGrpSpPr><p:cNvPr id=\"1\" name=\"\"/><p:cNvGrpSpPr/><p:nvPr/></p:nvGrpSpPr>\n");
		write("<p:grpSpPr><a:xfrm><a:off x=\"0\" y=\"0\"/><a:ext cx=\"0\" cy=\"0\"/><a:chOff x=\"0\" y=\"0\"/><a:chExt cx=\"0\" cy=\"0\"/></a:xfrm></p:grpSpPr>\n");
//		write("<p:sp><p:nvSpPr><p:cNvPr id=\"2\" name=\"Title 1\"/><p:cNvSpPr><a:spLocks noGrp=\"1\"/></p:cNvSpPr><p:nvPr><p:ph type=\"ctrTitle\"/></p:nvPr></p:nvSpPr><p:spPr><a:xfrm><a:off x=\"0\" y=\"0\"/><a:ext cx=\"2209800\" cy=\"1219199\"/></a:xfrm></p:spPr><p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:r><a:rPr lang=\"en-US\" dirty=\"0\" err=\"1\" smtClean=\"0\"/><a:t>Dede</a:t></a:r><a:endParaRPr lang=\"en-US\" dirty=\"0\"/></a:p></p:txBody></p:sp>\n");
//		write("<p:sp><p:nvSpPr><p:cNvPr id=\"3\" name=\"Subtitle 2\"/><p:cNvSpPr><a:spLocks noGrp=\"1\"/></p:cNvSpPr><p:nvPr><p:ph type=\"subTitle\" idx=\"1\"/></p:nvPr></p:nvSpPr><p:spPr><a:xfrm><a:off x=\"2209800\" y=\"1219199\"/><a:ext cx=\"2209800\" cy=\"1219199\"/></a:xfrm></p:spPr><p:txBody><a:bodyPr/><a:lstStyle/><a:p><a:r><a:rPr lang=\"en-US\" smtClean=\"0\"/><a:t>coco</a:t></a:r><a:endParaRPr lang=\"en-US\" dirty=\"0\"/></a:p></p:txBody></p:sp>\n");
	}
	

	/**
	 *
	 */
	public void exportHyperlink(String href) 
	{
		write("<a:hlinkClick r:id=\"rIdLnk" + slideRelsHelper.getHyperlink(href) + "\"/>\n");
	}


	/**
	 *
	 */
	public void exportFooter(boolean isSlideMaster)
	{
		write("</p:spTree>\n");
		write("</p:cSld>\n");
		if (isSlideMaster)
		{
			write("<p:clrMap bg1=\"lt1\" tx1=\"dk1\" bg2=\"lt2\" tx2=\"dk2\" accent1=\"accent1\" accent2=\"accent2\" accent3=\"accent3\" accent4=\"accent4\" accent5=\"accent5\" accent6=\"accent6\" hlink=\"hlink\" folHlink=\"folHlink\"/>\n");
			write("<p:sldLayoutIdLst>\n");
			write("<p:sldLayoutId id=\"2147483649\" r:id=\"rIdSl\"/>\n");
			write("</p:sldLayoutIdLst>\n");
			write("</p:sldMaster>\n");		
		}
		else
		{
			write("<p:clrMapOvr><a:masterClrMapping/></p:clrMapOvr>\n");
			write("</p:sld>\n");
		}
	}


}
