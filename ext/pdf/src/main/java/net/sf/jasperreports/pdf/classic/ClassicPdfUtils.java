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
package net.sf.jasperreports.pdf.classic;

import java.awt.Color;
import java.awt.color.ColorSpace;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Element;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfStructureTreeRoot;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.common.PdfTextAlignment;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicPdfUtils
{

	private static final Log log = LogFactory.getLog(ClassicPdfUtils.class);

	/**
	 * Whether the OpenPDF library on the classpath allows installing a custom
	 * {@link PdfStructureTreeRoot}, which is required for the PDF/UA link annotation
	 * <code>/StructParent</code> tagging performed by {@link ClassicPdfStructureTreeRoot}.
	 *
	 * <p>
	 * This is supported by the Jaspersoft build of OpenPDF (which exposes
	 * {@link PdfWriter#setStructureTreeRoot(PdfStructureTreeRoot)}), but not by stock OpenPDF.
	 * When unavailable, the PDF exporter still produces valid tagged PDFs, only without the
	 * annotation <code>/StructParent</code> back-references.
	 * </p>
	 */
	private static final boolean CUSTOM_STRUCTURE_TREE_ROOT_SUPPORTED = determineCustomStructureTreeRootSupported();

	private static boolean determineCustomStructureTreeRootSupported()
	{
		try
		{
			PdfWriter.class.getMethod("setStructureTreeRoot", PdfStructureTreeRoot.class);
			return true;
		}
		catch (NoSuchMethodException e)
		{
			log.warn("Stock OpenPDF detected, link annotation /StructParent tagging is disabled");
			return false;
		}
		catch (SecurityException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	/**
	 * Returns whether a custom {@link PdfStructureTreeRoot} can be installed on the OpenPDF
	 * {@link PdfWriter}, enabling the link annotation <code>/StructParent</code> tagging.
	 *
	 * @see ClassicPdfStructureTreeRoot
	 */
	public static boolean isCustomStructureTreeRootSupported()
	{
		return CUSTOM_STRUCTURE_TREE_ROOT_SUPPORTED;
	}

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
