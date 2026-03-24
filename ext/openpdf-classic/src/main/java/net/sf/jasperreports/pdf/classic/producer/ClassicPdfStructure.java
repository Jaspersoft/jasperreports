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

import java.util.HashMap;
import java.util.Map;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfStructureElement;
import com.lowagie.text.pdf.PdfStructureTreeRoot;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.pdf.common.PdfStructure;
import net.sf.jasperreports.pdf.common.PdfStructureEntry;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicPdfStructure implements PdfStructure
{

	private ClassicPdfProducer pdfProducer;
	
	private Map<String, PdfName> pdfNames;

	public ClassicPdfStructure(ClassicPdfProducer pdfProducer)
	{
		this.pdfProducer = pdfProducer;
		this.pdfNames = new HashMap<>();
	}

	@Override
	public PdfStructureEntry createAllTag(String language)
	{
		PdfWriter pdfWriter = pdfProducer.getPdfWriter();
		PdfStructureTreeRoot root = pdfWriter.getStructureTreeRoot();
		
		PdfName pdfNameALL = new PdfName("All");
		root.mapRole(pdfNameALL, PdfName.SECT);
		root.mapRole(PdfName.IMAGE, PdfName.FIGURE);
		root.mapRole(PdfName.TEXT, PdfName.P);
		PdfStructureElement allTag = new PdfStructureElement(root, pdfNameALL);
		if(pdfWriter.getPDFXConformance() == PdfWriter.PDFA1A)
		{
			root.mapRole(new PdfName("Anchor"), PdfName.NONSTRUCT);
			root.mapRole(PdfName.TEXT, PdfName.SPAN);
		}
		else
		{
			root.mapRole(new PdfName("Anchor"), PdfName.TEXT);
		}
		
		if (language != null)
		{
			allTag.put(PdfName.LANG, new PdfString(language));
		}
		
		return new ClassicStructureEntry(this, allTag);
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

	protected ClassicStructureEntry createElement(PdfStructureEntry parent, String name)
	{
		PdfStructureElement parentElement = ((ClassicStructureEntry) parent).getElement();
		PdfStructureElement element = new PdfStructureElement(parentElement, pdfName(name));
		return new ClassicStructureEntry(this, element);
	}

	@Override
	public PdfStructureEntry createTag(PdfStructureEntry parent, String name)
	{
		return createElement(parent, name);
	}

	@Override
	public PdfStructureEntry beginTag(PdfStructureEntry parent, String name)
	{
		ClassicStructureEntry tag = createElement(parent, name);
		pdfProducer.getPdfContentByte().beginMarkedContentSequence(tag.getElement());
		return tag;
	}

	@Override
	public PdfStructureEntry beginTag(PdfStructureEntry parent, String name, String text)
	{
		PdfDictionary markedContentProps = new PdfDictionary();
		markedContentProps.put(PdfName.ACTUALTEXT, new PdfString(text, PdfObject.TEXT_UNICODE));
		
		ClassicStructureEntry tag = createElement(parent, name);
		pdfProducer.getPdfContentByte().beginMarkedContentSequence(tag.getElement(), 
				markedContentProps);
		return tag;
	}

	@Override
	public void endTag()
	{
		pdfProducer.getPdfContentByte().endMarkedContentSequence();
	}

}
