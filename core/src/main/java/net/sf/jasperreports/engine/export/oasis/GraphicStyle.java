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
package net.sf.jasperreports.engine.export.oasis;

import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRPenUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class GraphicStyle extends Style
{
	/**
	 * The ratio of 72 dpi to 96 dpi
	 */
	public static final Double DPI_RATIO = 72.0/96.0;
	
	/**
	 *
	 */
	private String backcolor;
	private String forecolor;
	private String style;
	private double width;
	private String hAlign;
	private String vAlign;
	private double cropTop;
	private double cropLeft;
	private double cropBottom;
	private double cropRight;

	private String clip;
	private int reportDpi;


	/**
	 *
	 */
	public GraphicStyle(
			WriterHelper styleWriter, 
			JRPrintGraphicElement element, 
			double cropTop,
			double cropLeft,
			double cropBottom,
			double cropRight,
			int reportDpi
			)
	{
		super(styleWriter);
		this.cropTop = cropTop;
		this.cropLeft = cropLeft;
		this.cropBottom = cropBottom;
		this.cropRight = cropRight;
		this.reportDpi = reportDpi;
		
		if (element.getMode() == ModeEnum.OPAQUE)
		{
			//fill = "solid";
			backcolor = JRColorUtil.getColorHexa(element.getBackcolor());
		}
//		else
//		{
//			//fill = "none";
//		}

		forecolor = JRColorUtil.getColorHexa(element.getLinePen().getLineColor());

		width = JRPenUtil.getLineWidth(element, reportDpi);
		if (width <= 0)
		{
			style = "none";
		}
		else
		{
			switch (element.getLinePen().getLineStyle())
			{
				case DOTTED : //FIXMEBORDER
				case DASHED :
				{
					style = "dash";
					break;
				}
				case SOLID :
				default :
				{
					style = "solid";
					break;
				}
			}
		}

		if (element instanceof JRPrintImage)
		{
			clip = " fo:clip=\"rect("
				+ LengthUtil.inchFloor4Dec(cropTop * DPI_RATIO, reportDpi)
				+ "in,"
				+ LengthUtil.inchFloor4Dec(cropRight * DPI_RATIO, reportDpi)
				+ "in,"
				+ LengthUtil.inchFloor4Dec(cropBottom * DPI_RATIO, reportDpi)
				+ "in,"
				+ LengthUtil.inchFloor4Dec(cropLeft * DPI_RATIO, reportDpi)
				+ "in)\"";
		}
	}

	@Override
	public String getId()
	{
		//return fill + "|" + backcolor
		StringBuilder id = new StringBuilder();
		id.append(backcolor);
		id.append("|");
		id.append(forecolor);
		id.append("|");
		id.append(style);
		id.append("|");
		id.append(width);
		id.append("|");
		id.append(hAlign);
		id.append("|");
		id.append(vAlign);
		id.append("|");
		id.append(cropTop);
		id.append("|");
		id.append(cropLeft);
		id.append("|");
		id.append(cropBottom);
		id.append("|");
		id.append(cropRight);
		return id.toString();
	}

	@Override
	public void write(String lineStyleName)
	{
		styleWriter.write(" <style:style style:name=\"" + lineStyleName + "\"");
		styleWriter.write(" style:family=\"graphic\" style:parent-style-name=\"Graphics\">\n");
		styleWriter.write("   <style:graphic-properties");
		if (backcolor != null)
		{
			styleWriter.write(" draw:fill-color=\"#" + backcolor + "\"");
		}
		styleWriter.write(" style:horizontal-pos=\"from-left\" style:horizontal-rel=\"frame\"");
		styleWriter.write(" style:vertical-pos=\"from-top\" style:vertical-rel=\"frame\"");
		if (clip != null)
		{
			styleWriter.write(clip);
		}
		styleWriter.write(" svg:stroke-color=\"#" + forecolor + "\"");
		styleWriter.write(" draw:stroke=\"" + style + "\"");//FIXMENOW dashed borders do not work; only dashed lines and ellipses seem to work
		styleWriter.write(" draw:stroke-dash=\"Dashed\"");
		styleWriter.write(" svg:stroke-width=\"" + LengthUtil.inchFloor4Dec(width, reportDpi) + "in\"");
		styleWriter.write("/>\n");
		styleWriter.write("</style:style>\n");
	}

}

