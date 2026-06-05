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

import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfICCBased;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.pdf.PdfXmpCreator;
import net.sf.jasperreports.pdf.common.PdfDocumentWriter;
import net.sf.jasperreports.pdf.type.PdfPrintScalingEnum;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;
import net.sf.jasperreports.pdf.type.PdfaConformanceEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicPdfWriter implements PdfDocumentWriter
{

	private static final Log log = LogFactory.getLog(ClassicPdfWriter.class);
	
	private ClassicPdfProducer pdfProducer;
	private PdfWriter pdfWriter;
	private PdfaConformanceEnum pdfaConformance;

	public ClassicPdfWriter(ClassicPdfProducer pdfProducer, PdfWriter pdfWriter)
	{
		this.pdfProducer = pdfProducer;
		this.pdfWriter = pdfWriter;
	}

	public PdfWriter getPdfWriter()
	{
		return pdfWriter;
	}
	
	@Override
	public void setPdfVersion(PdfVersionEnum pdfVersion)
	{
		pdfWriter.setPdfVersion(pdfVersion.getName().charAt(0));
	}

	@Override
	public void setMinimalPdfVersion(PdfVersionEnum minimalVersion)
	{
		pdfWriter.setAtLeastPdfVersion(minimalVersion.getName().charAt(0));
	}

	@Override
	public void setFullCompression()
	{
		pdfWriter.setFullCompression();
	}

	@Override
	public void setEncryption(String userPassword, String ownerPassword, 
			int permissions, boolean is128BitKey) throws JRException
	{
		try
		{
			pdfWriter.setEncryption(
					PdfWriter.getISOBytes(userPassword),
					PdfWriter.getISOBytes(ownerPassword),
					permissions,
					is128BitKey ? PdfWriter.STANDARD_ENCRYPTION_128 : PdfWriter.STANDARD_ENCRYPTION_40
					);
		}
		catch (DocumentException e)
		{
			throw pdfProducer.getContext().handleDocumentException(e);
		}
	}

	@Override
	public void setPrintScaling(PdfPrintScalingEnum printScaling)
	{
		if (PdfPrintScalingEnum.DEFAULT == printScaling)
		{
			pdfWriter.addViewerPreference(PdfName.PRINTSCALING, PdfName.APPDEFAULT);
		}
		else if (PdfPrintScalingEnum.NONE == printScaling)
		{
			pdfWriter.addViewerPreference(PdfName.PRINTSCALING, PdfName.NONE);
		}
	}

	@Override
	public void setNoSpaceCharRatio()
	{
		pdfWriter.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO);
	}

	@Override
	public void setTabOrderStructure()
	{
		pdfWriter.setTabs(PdfName.S);
	}

	@Override
	public void setLanguage(String language)
	{
		pdfWriter.getExtraCatalog().put(PdfName.LANG, new PdfString(language));
	}

	@Override
	public void setPdfaConformance(PdfaConformanceEnum pdfaConformance)
	{
		this.pdfaConformance = pdfaConformance;
		
		// there is an incompatibility regarding font handling, 
		// specifically concerning the CIDSet entry for subsetted CIDFonts, 
		// when attempting to comply with PDF/A-1a and PDF/UA-1 simultaneously.
		if (PdfaConformanceEnum.PDFA_1A == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA1A);
		}
		else if (PdfaConformanceEnum.PDFA_1B == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA1B);
		}
	}

	@Override
	public void createXmpMetadata(String title, String subject, String keywords, boolean isTagged)
	{
		if (PdfXmpCreator.supported())
		{
			byte[] metadata = PdfXmpCreator.createXmpMetadata(pdfWriter, pdfaConformance, isTagged);
			pdfWriter.setXmpMetadata(metadata);
		}
		else
		{
			if ((title != null || subject != null || keywords != null) && log.isWarnEnabled())
			{
				//TODO check whether OpenPDF properly writes localized properties and keywords
				log.warn("XMP metadata might be non conforming, include the Adobe XMP library to correct");
			}
			
			pdfWriter.createXmpMetadata();
		}
	}

	@Override
	public void setRgbTransparencyBlending(boolean rgbTransparencyBlending)
	{
		pdfWriter.setRgbTransparencyBlending(rgbTransparencyBlending);
	}

	@Override
	public void setIccProfilePath(String iccProfilePath, InputStream iccIs) throws IOException
	{
		PdfDictionary pdfDictionary = new PdfDictionary(PdfName.OUTPUTINTENT);
		pdfDictionary.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("sRGB IEC61966-2.1"));
		pdfDictionary.put(PdfName.INFO, new PdfString("sRGB IEC61966-2.1"));
		pdfDictionary.put(PdfName.S, PdfName.GTS_PDFA1);
		PdfICCBased pdfICCBased = new PdfICCBased(ICC_Profile.getInstance(iccIs));
		pdfICCBased.remove(PdfName.ALTERNATE);
		pdfDictionary.put(PdfName.DESTOUTPUTPROFILE, pdfWriter.addToBody(pdfICCBased).getIndirectReference());

		pdfWriter.getExtraCatalog().put(PdfName.OUTPUTINTENTS, new PdfArray(pdfDictionary));
	}

	@Override
	public void addJavaScript(String pdfJavaScript)
	{
		pdfWriter.addJavaScript(pdfJavaScript);
	}

	@Override
	public void setDisplayMetadataTitle()
	{
		pdfWriter.addViewerPreference(PdfName.DISPLAYDOCTITLE, new PdfBoolean(true));
	}

}
