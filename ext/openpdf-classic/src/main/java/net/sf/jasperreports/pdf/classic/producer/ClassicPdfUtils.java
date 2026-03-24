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
package net.sf.jasperreports.pdf.classic.producer;

import java.awt.Color;
import java.awt.color.ColorSpace;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.CMYKColor;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.common.PdfTextAlignment;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicPdfUtils
{

	public ClassicPdfUtils()
	{
	}
	
	public static int toPdfAlignment(PdfTextAlignment alignment)
	{
		int pdfAlign;
		switch (alignment)
		{
		case LEFT:
			pdfAlign = Element.ALIGN_LEFT;
			break;
		case RIGHT:
			pdfAlign = Element.ALIGN_RIGHT;
			break;
		case CENTER:
			pdfAlign = Element.ALIGN_CENTER;
			break;
		case JUSTIFIED:
			pdfAlign = Element.ALIGN_JUSTIFIED;
			break;
		case JUSTIFIED_ALL:
			pdfAlign = Element.ALIGN_JUSTIFIED_ALL;
			break;
		default:
			throw new JRRuntimeException("Unknown paragraph alignment " + alignment);
		}
		return pdfAlign;
	}

	public static Color convertColor(ColorSpace targetColorSpace, Color color)
	{
		if (color != null && targetColorSpace != null)
		{
//			ColorSpace rgbColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
//			float[] ciexyzColor = rgbColorSpace.fromRGB(color.getColorComponents(null));
//			float[] cmykColor = targetColorSpace.fromCIEXYZ(ciexyzColor);
			float[] cmykColor = targetColorSpace.fromRGB(color.getRGBComponents(null));
			color = new CMYKColor(cmykColor[0], cmykColor[1], cmykColor[2], cmykColor[3]);
		}
		
		return color;
	}
}
