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
package net.sf.jasperreports.openpdf.producer;

import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openpdf.text.DocumentException;
import org.openpdf.text.pdf.PdfArray;
import org.openpdf.text.pdf.PdfBoolean;
import org.openpdf.text.pdf.PdfDictionary;
import org.openpdf.text.pdf.PdfICCBased;
import org.openpdf.text.pdf.PdfName;
import org.openpdf.text.pdf.PdfString;
import org.openpdf.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.openpdf.PdfXmpCreator;
import net.sf.jasperreports.pdf.PdfExporterConfiguration;
import net.sf.jasperreports.pdf.common.PdfDocumentWriter;
import net.sf.jasperreports.pdf.type.PdfPermissionsEnum;
import net.sf.jasperreports.pdf.type.PdfPrintScalingEnum;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;
import net.sf.jasperreports.pdf.type.PdfaConformanceEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPdfWriter implements PdfDocumentWriter
{

	private static final Log log = LogFactory.getLog(StandardPdfWriter.class);

	/**
	 * Integer property that contains all permissions for the generated PDF document
	 */
	public static final int ALL_PERMISSIONS = 
			PdfWriter.ALLOW_ASSEMBLY 
			| PdfWriter.ALLOW_COPY
			| PdfWriter.ALLOW_DEGRADED_PRINTING
			| PdfWriter.ALLOW_FILL_IN
			| PdfWriter.ALLOW_MODIFY_ANNOTATIONS
			| PdfWriter.ALLOW_MODIFY_CONTENTS
			| PdfWriter.ALLOW_PRINTING
			| PdfWriter.ALLOW_SCREENREADERS;
	
	private StandardPdfProducer pdfProducer;
	private PdfWriter pdfWriter;
	private PdfaConformanceEnum pdfaConformance;

	// initialized to OpenPDF's default (PDF 2.0)
	private PdfVersionEnum pdfVersion = PdfVersionEnum.VERSION_2_0;

	public StandardPdfWriter(StandardPdfProducer pdfProducer, PdfWriter pdfWriter)
	{
		this.pdfProducer = pdfProducer;
		this.pdfWriter = pdfWriter;
	}

	public PdfWriter getPdfWriter()
	{
		return pdfWriter;
	}

	public boolean isPdf2()
	{
		// true when the effective version is 2.0 or later
		return pdfVersion.isAtLeast(PdfVersionEnum.VERSION_2_0);
	}

	@Override
	public void setPdfVersion(PdfVersionEnum pdfVersion)
	{
		this.pdfVersion = pdfVersion;
		pdfWriter.setPdfVersion(toPdfVersion(pdfVersion));
	}

	@Override
	public void setMinimalPdfVersion(PdfVersionEnum minimalVersion)
	{
		// mirror OpenPDF's setAtLeastPdfVersion which only raises the version
		// (assigning an equal version is a harmless no-op)
		if (minimalVersion.isAtLeast(pdfVersion))
		{
			pdfVersion = minimalVersion;
		}
		pdfWriter.setAtLeastPdfVersion(toPdfVersion(minimalVersion));
	}
	
	protected String toPdfVersion(PdfVersionEnum pdfVersion)
	{
		return pdfVersion.getName();
	}

	@Override
	public void setFullCompression()
	{
		pdfWriter.setFullCompression();
	}

	@Override
	public void setEncryption(PdfExporterConfiguration configuration) throws JRException
	{
		int permissions = getIntegerPermissions(configuration.getAllowedPermissions()) 
				& (~getIntegerPermissions(configuration.getDeniedPermissions()));
		
		int perms = configuration.isOverrideHints() == null || configuration.isOverrideHints()
				? (configuration.getPermissions() != null 
					? (Integer)configuration.getPermissions() 
					: permissions) 
				: (permissions != 0 
					? permissions 
					:(configuration.getPermissions() != null 
						? (Integer)configuration.getPermissions() 
						: 0));

		try
		{
			pdfWriter.setEncryption(
					PdfWriter.getISOBytes(configuration.getUserPassword()),
					PdfWriter.getISOBytes(configuration.getOwnerPassword()),
					perms,
					configuration.is128BitKey() ? PdfWriter.STANDARD_ENCRYPTION_128 : PdfWriter.STANDARD_ENCRYPTION_40
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
		else if (PdfaConformanceEnum.PDFA_2A == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA2A);
		}
		else if (PdfaConformanceEnum.PDFA_2B == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA2B);
		}
		else if (PdfaConformanceEnum.PDFA_2U == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA2U);
		}
		else if (PdfaConformanceEnum.PDFA_3A == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA3A);
		}
		else if (PdfaConformanceEnum.PDFA_3B == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA3B);
		}
		else if (PdfaConformanceEnum.PDFA_3U == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA3U);
		}
		else if (PdfaConformanceEnum.PDFA_4 == pdfaConformance)
		{
			pdfWriter.setPDFXConformance(PdfWriter.PDFA4);
		}
	}

	@Override
	public void createXmpMetadata(String title, String subject, String keywords, boolean isTagged)
	{
		if (PdfXmpCreator.supported())
		{
			byte[] metadata = PdfXmpCreator.createXmpMetadata(pdfWriter, pdfaConformance, isTagged, isPdf2());
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
	
	public static int getIntegerPermissions(String permissions)
	{
		int permission = 0;
		if (permissions != null && permissions.length() > 0)
		{
			String[] perms = permissions.split("\\|");
			for (String perm : perms)
			{
				if (PdfPermissionsEnum.ALL.equals(PdfPermissionsEnum.getByName(perm)))
				{
					permission = ALL_PERMISSIONS;
					break;
				}
				if (perm != null && perm.length()>0)
				{
					permission |= toIntegerPermission(PdfPermissionsEnum.getByName(perm));
				}
			}
		}
		return permission;
	}
	
	protected static int toIntegerPermission(PdfPermissionsEnum permission)
	{
		int intPermission;
		switch (permission)
		{
			case ASSEMBLY:
				intPermission = PdfWriter.ALLOW_ASSEMBLY;
				break;
			case COPY:
				intPermission = PdfWriter.ALLOW_COPY;
				break;
			case DEGRADED_PRINTING:
				intPermission = PdfWriter.ALLOW_DEGRADED_PRINTING;
				break;
			case FILL_IN:
				intPermission = PdfWriter.ALLOW_FILL_IN;
				break;
			case MODIFY_ANNOTATIONS:
				intPermission = PdfWriter.ALLOW_MODIFY_ANNOTATIONS;
				break;
			case MODIFY_CONTENTS:
				intPermission = PdfWriter.ALLOW_MODIFY_CONTENTS;
				break;
			case PRINTING:
				intPermission = PdfWriter.ALLOW_PRINTING;
				break;
			case SCREENREADERS:
				intPermission = PdfWriter.ALLOW_SCREENREADERS;
				break;
			case ALL:
				intPermission = ALL_PERMISSIONS;
				break;
			default:
				throw new JRRuntimeException("Unexpected permission: " + permission);
		}
		return intPermission;
	}

}
