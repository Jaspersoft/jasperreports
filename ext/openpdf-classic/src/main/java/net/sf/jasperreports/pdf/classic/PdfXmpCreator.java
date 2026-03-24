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

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.PropertyOptions;
import com.adobe.internal.xmp.options.SerializeOptions;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.xmp.DublinCoreSchema;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.type.PdfaConformanceEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PdfXmpCreator
{

	private static final Log log = LogFactory.getLog(PdfXmpCreator.class);
	
	private static final String XMP_LIBARY_CLASS_NAME = "com.adobe.internal.xmp.XMPMetaFactory";
	private static final boolean XMP_LIBRARY;

	static
	{
		XMP_LIBRARY = determineXmpLibrary();
	}

	private static boolean determineXmpLibrary()
	{
		try
		{
			Class.forName(XMP_LIBARY_CLASS_NAME);
			return true;
		} catch (ClassNotFoundException e)
		{
			log.info("Adobe XMP library not found");
			return false;
		}
	}

	public static boolean supported()
	{
		return XMP_LIBRARY;
	}

	public static byte[] createXmpMetadata(PdfWriter pdfWriter, PdfaConformanceEnum conformance)
	{
		XmpWriter writer = new XmpWriter(pdfWriter, conformance);
		return writer.createXmpMetadata();
	}

}


class XmpWriter
{
	private static final String FORMAT_PDF = "application/pdf";

	private static final String PDF_PRODUCER = "Producer";

	private static final String PDF_KEYWORDS = "Keywords";

	private static final String PDFA_PART = "part";

	private static final String PDFA_CONFORMANCE = "conformance";

	private static final String PDFA_PART_1 = "1";
	
	private static final String PDFA_PART_2 = "2";
	
	private static final String PDFA_PART_3 = "3";

	private static final String PDFA_CONFORMANCE_A = "A";

	private static final String PDFA_CONFORMANCE_B = "B";
	
	private static final String PDFA_CONFORMANCE_U = "U";

	private static final String XMP_CREATE_DATE = "CreateDate";

	private static final String XMP_MODIFY_DATE = "ModifyDate";

	private static final String XMP_CREATOR_TOOL = "CreatorTool";
	
	private final PdfWriter pdfWriter;
	private final PdfDictionary info;
	private final PdfaConformanceEnum conformance;
	
	XmpWriter(PdfWriter pdfWriter, PdfaConformanceEnum conformance)
	{
		this.pdfWriter = pdfWriter;
		this.info = pdfWriter.getInfo();
		this.conformance = conformance;
	}
	
	byte[] createXmpMetadata()
	{
		try
		{
			XMPMeta xmp = XMPMetaFactory.create();
			xmp.setObjectName("");

			xmp.setProperty(XMPConst.NS_DC, DublinCoreSchema.FORMAT, FORMAT_PDF);
			
			String producer = extractInfo(PdfName.PRODUCER);
			if (producer != null)
			{
				xmp.setProperty(XMPConst.NS_PDF, PDF_PRODUCER, producer);
			}

			if (pdfWriter.getPDFXConformance() == PdfWriter.PDFA1A || this.conformance == PdfaConformanceEnum.PDFA_1A)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_1);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_A);
			}
			else if (pdfWriter.getPDFXConformance() == PdfWriter.PDFA1B || this.conformance == PdfaConformanceEnum.PDFA_1B)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_1);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_B);
			}
			else if (this.conformance == PdfaConformanceEnum.PDFA_2A)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_2);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_A);
			}
			else if (this.conformance == PdfaConformanceEnum.PDFA_2B)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_2);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_B);
			}
			else if (this.conformance == PdfaConformanceEnum.PDFA_2U)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_2);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_U);
			}
			else if (this.conformance == PdfaConformanceEnum.PDFA_3A)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_3);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_A);
			}
			else if (this.conformance == PdfaConformanceEnum.PDFA_3B)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_3);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_B);
			}
			else if (this.conformance == PdfaConformanceEnum.PDFA_3U)
			{
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_PART, PDFA_PART_3);
				xmp.setProperty(XMPConst.NS_PDFA_ID, PDFA_CONFORMANCE, PDFA_CONFORMANCE_U);
			}

			xmp.setProperty(XMPConst.NS_XMP, XMP_CREATE_DATE, ((PdfDate) info.get(PdfName.CREATIONDATE)).getW3CDate());
			PdfDate modifiedDate = (PdfDate) info.get(PdfName.MODDATE);
			if (modifiedDate != null)
			{
				xmp.setProperty(XMPConst.NS_XMP, XMP_MODIFY_DATE, modifiedDate.getW3CDate());
			}

			String title = extractInfo(PdfName.TITLE);
			if (title != null)
			{
				xmp.setLocalizedText(XMPConst.NS_DC, DublinCoreSchema.TITLE, 
						//FIXME use the tag language?
						XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, title);
			}

			String author = extractInfo(PdfName.AUTHOR);
			if (author != null)
			{
				//FIXME cache the options?
				PropertyOptions arrayOrdered = new PropertyOptions().setArrayOrdered(true);
				xmp.appendArrayItem(XMPConst.NS_DC, DublinCoreSchema.CREATOR, arrayOrdered, author, null);
			}

			String subject = extractInfo(PdfName.SUBJECT);
			if (subject != null)
			{
				PropertyOptions array = new PropertyOptions().setArray(true);
				xmp.appendArrayItem(XMPConst.NS_DC, DublinCoreSchema.SUBJECT, array, subject, null);
				xmp.setLocalizedText(XMPConst.NS_DC, DublinCoreSchema.DESCRIPTION, 
						XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, subject);
			}

			String keywords = extractInfo(PdfName.KEYWORDS);
			if (keywords != null)
			{
				xmp.setProperty(XMPConst.NS_PDF, PDF_KEYWORDS, keywords);
			}

			String creator = extractInfo(PdfName.CREATOR);
			if (creator != null)
			{
				xmp.setProperty(XMPConst.NS_XMP, XMP_CREATOR_TOOL, creator);
			}

			SerializeOptions options = new SerializeOptions();
			options.setUseCanonicalFormat(true);

			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			XMPMetaFactory.serialize(xmp, out, options);
			return out.toByteArray();
		}
		catch (XMPException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	private String extractInfo(PdfName key)
	{
		PdfString value = (PdfString) info.get(key);
		return value == null ? null : value.toUnicodeString();
	}
}