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

import com.lowagie.text.Chunk;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBorderArray;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfStructureElement;

import net.sf.jasperreports.pdf.common.PdfChunk;
import net.sf.jasperreports.pdf.common.PdfStructureEntry;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicChunk implements PdfChunk
{

	private ClassicPdfProducer pdfProducer;
	protected Chunk chunk;

	private PdfStructureEntry linkTag;
	private float linkLlx;
	private float linkLly;
	private float linkUrx;
	private float linkUry;
	private String linkContents;

	public ClassicChunk(ClassicPdfProducer pdfProducer, Chunk chunk)
	{
		this.pdfProducer = pdfProducer;
		this.chunk = chunk;
	}

	public Chunk getChunk()
	{
		return chunk;
	}
	
	@Override
	public void setLocalDestination(String anchorName)
	{
		chunk.setLocalDestination(anchorName);
	}

	@Override
	public void setLinkTag(PdfStructureEntry linkTag, float llx, float lly, float urx, float ury, String linkContents)
	{
		this.linkTag = linkTag;
		this.linkLlx = llx;
		this.linkLly = lly;
		this.linkUrx = urx;
		this.linkUry = ury;
		this.linkContents = linkContents;
	}

	@Override
	public void setJavaScriptAction(String script)
	{
		if (linkTag != null)
		{
			addAnnotationToTag(
				linkTag,
				PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					new Rectangle(linkLlx, linkLly, linkUrx, linkUry),
					PdfAnnotation.HIGHLIGHT_INVERT,
					PdfAction.javaScript(script, pdfProducer.getPdfWriter())
					)
				);
		}
		else
		{
			chunk.setAction(PdfAction.javaScript(script, pdfProducer.getPdfWriter()));
		}
	}

	@Override
	public void setAnchor(String reference)
	{
		if (linkTag != null)
		{
			addAnnotationToTag(
				linkTag,
				new PdfAnnotation(pdfProducer.getPdfWriter(), linkLlx, linkLly, linkUrx, linkUry, new PdfAction(reference))
				);
		}
		else
		{
			chunk.setAnchor(reference);
		}
	}

	@Override
	public void setLocalGoto(String anchor)
	{
		if (linkTag != null)
		{
			addAnnotationToTag(
				linkTag,
				PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					new Rectangle(linkLlx, linkLly, linkUrx, linkUry),
					PdfAnnotation.HIGHLIGHT_INVERT,
					anchor
					)
				);
		}
		else
		{
			chunk.setLocalGoto(anchor);
		}
	}

	@Override
	public void setLocalGotoPage(int page, float top)
	{
		PdfAction action = PdfAction.gotoLocalPage(page, new PdfDestination(PdfDestination.XYZ, 0, top, 0), pdfProducer.getPdfWriter());
		if (linkTag != null)
		{
			addAnnotationToTag(
				linkTag,
				PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					new Rectangle(linkLlx, linkLly, linkUrx, linkUry),
					PdfAnnotation.HIGHLIGHT_INVERT,
					action
					)
				);
		}
		else
		{
			chunk.setAction(action);
		}
	}

	@Override
	public void setRemoteGoto(String reference, String anchor)
	{
		if (linkTag != null)
		{
			addAnnotationToTag(
				linkTag,
				PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					new Rectangle(linkLlx, linkLly, linkUrx, linkUry),
					PdfAnnotation.HIGHLIGHT_INVERT,
					new PdfAction(reference, anchor)
					)
				);
		}
		else
		{
			chunk.setRemoteGoto(reference, anchor);
		}
	}

	@Override
	public void setRemoteGoto(String reference, int page)
	{
		if (linkTag != null)
		{
			addAnnotationToTag(
				linkTag,
				PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					new Rectangle(linkLlx, linkLly, linkUrx, linkUry),
					PdfAnnotation.HIGHLIGHT_INVERT,
					new PdfAction(reference, page)
					)
				);
		}
		else
		{
			chunk.setRemoteGoto(reference, page);
		}
	}

	protected void addAnnotationToTag(PdfStructureEntry linkTag, PdfAnnotation annotation)
	{
		annotation.put(PdfName.BORDER, new PdfBorderArray(0, 0, 0));
		annotation.remove(PdfName.C);
		annotation.put(PdfName.F, new PdfNumber(PdfAnnotation.FLAGS_PRINT));

		if (linkContents != null && linkContents.trim().length() > 0)
		{
			annotation.put(PdfName.CONTENTS, new PdfString(linkContents));
		}

		PdfStructureElement element = ((ClassicStructureEntry) linkTag).getElement();

		ClassicPdfStructureTreeRoot treeRoot = (ClassicPdfStructureTreeRoot) pdfProducer.getPdfWriter().getStructureTreeRoot();
		treeRoot.addAnnotationParent(annotation, element.getReference());

		pdfProducer.getPdfWriter().addAnnotation(annotation);

		PdfDictionary objr = new PdfDictionary(PdfName.OBJR);
		objr.put(PdfName.OBJ, annotation.getIndirectReference());

		PdfObject kObj = element.get(PdfName.K);
		if (kObj instanceof PdfArray)
		{
			((PdfArray) kObj).add(objr);
		}
		else if (kObj instanceof PdfNumber)
		{
			PdfArray ar = new PdfArray();
			ar.add(kObj);
			ar.add(objr);
			element.put(PdfName.K, ar);
		}
	}

}
