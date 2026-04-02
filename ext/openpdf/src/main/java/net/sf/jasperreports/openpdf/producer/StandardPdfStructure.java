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

import java.util.HashMap;
import java.util.Map;

import org.openpdf.text.pdf.PdfDictionary;
import org.openpdf.text.pdf.PdfName;
import org.openpdf.text.pdf.PdfObject;
import org.openpdf.text.pdf.PdfString;
import org.openpdf.text.pdf.PdfStructureElement;
import org.openpdf.text.pdf.PdfStructureTreeRoot;
import org.openpdf.text.pdf.PdfStructureTreeRootUtil;
import org.openpdf.text.pdf.PdfWriter;

import net.sf.jasperreports.pdf.common.PdfStructure;
import net.sf.jasperreports.pdf.common.PdfStructureEntry;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPdfStructure implements PdfStructure
{

	private StandardPdfProducer pdfProducer;
	
	private Map<String, PdfName> pdfNames;

	public StandardPdfStructure(StandardPdfProducer pdfProducer)
	{
		this.pdfProducer = pdfProducer;
		this.pdfNames = new HashMap<>();
	}

	@Override
	public PdfStructureEntry createDocumentTag(String language)
	{
		PdfWriter pdfWriter = pdfProducer.getPdfWriter();
		PdfStructureTreeRootUtil.install(pdfWriter);
		PdfStructureTreeRoot root = pdfWriter.getStructureTreeRoot();
		
		root.mapRole(PdfName.TEXT, PdfName.P);
		PdfStructureElement documentTag = new PdfStructureElement(root, PdfName.DOCUMENT);
		if(pdfWriter.getPDFXConformance() == PdfWriter.PDFA1A)
		{
			root.mapRole(new PdfName("Anchor"), PdfName.NONSTRUCT);
		}
		else
		{
			root.mapRole(new PdfName("Anchor"), PdfName.TEXT);
		}
		
		if (language != null)
		{
			documentTag.put(PdfName.LANG, new PdfString(language));
		}
		
		return new StandardStructureEntry(this, documentTag);
	}
	
	protected PdfName pdfName(String name)
	{
		PdfName pdfName = pdfNames.get(name);
		if (pdfName == null)
		{
			pdfName = new PdfName(name);
			pdfNames.put(name, pdfName);
		}
		return pdfName;
	}

	protected StandardStructureEntry createElement(PdfStructureEntry parent, String name)
	{
		PdfStructureElement parentElement = ((StandardStructureEntry) parent).getElement();
		PdfStructureElement element = new PdfStructureElement(parentElement, pdfName(name));
		return new StandardStructureEntry(this, element);
	}

	@Override
	public PdfStructureEntry createTag(PdfStructureEntry parent, String name)
	{
		return createElement(parent, name);
	}

	@Override
	public PdfStructureEntry beginTag(PdfStructureEntry parent, String name)
	{
		StandardStructureEntry tag = null;
		
		if (name == null)
		{
			tag = ((StandardStructureEntry) parent);
		}
		else
		{
			tag = createElement(parent, name);
		}
		
		pdfProducer.getPdfContentByte().beginMarkedContentSequence(tag.getElement());
		return tag;
	}

	@Override
	public PdfStructureEntry beginTag(PdfStructureEntry parent, String name, String text)
	{
		PdfDictionary markedContentProps = new PdfDictionary();
		markedContentProps.put(PdfName.ACTUALTEXT, new PdfString(text, PdfObject.TEXT_UNICODE));
		
		StandardStructureEntry tag = null;
		
		if (name == null)
		{
			tag = ((StandardStructureEntry) parent);
		}
		else
		{
			tag = createElement(parent, name);
		}
		
		pdfProducer.getPdfContentByte().beginMarkedContentSequence(tag.getElement(), 
				markedContentProps);
		return tag;
	}

	@Override
	public void endTag()
	{
		pdfProducer.getPdfContentByte().endMarkedContentSequence();
	}

	@Override
	public void beginArtifact()
	{
		pdfProducer.getPdfContentByte().beginMarkedContentSequence(new PdfName("Artifact"));
	}

	@Override
	public void endArtifact()
	{
		pdfProducer.getPdfContentByte().endMarkedContentSequence();
	}

	@Override
	public void beginSpan(String actualText)
	{
		PdfDictionary markedContentProps = new PdfDictionary();
		markedContentProps.put(PdfName.ACTUALTEXT, new PdfString(actualText, PdfObject.TEXT_UNICODE));
		pdfProducer.getPdfContentByte().beginMarkedContentSequence(PdfName.SPAN, markedContentProps, true);
	}

	@Override
	public void endSpan()
	{
		pdfProducer.getPdfContentByte().endMarkedContentSequence();
	}

}
