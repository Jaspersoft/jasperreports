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

import java.util.function.Supplier;

import org.openpdf.text.Chunk;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfAction;
import org.openpdf.text.pdf.PdfAnnotation;
import org.openpdf.text.pdf.PdfArray;
import org.openpdf.text.pdf.PdfBorderArray;
import org.openpdf.text.pdf.PdfDestination;
import org.openpdf.text.pdf.PdfDictionary;
import org.openpdf.text.pdf.PdfName;
import org.openpdf.text.pdf.PdfNumber;
import org.openpdf.text.pdf.PdfObject;
import org.openpdf.text.pdf.PdfString;
import org.openpdf.text.pdf.PdfStructureElement;
import org.openpdf.text.pdf.PdfStructureTreeRoot;

import net.sf.jasperreports.pdf.common.PdfChunk;
import net.sf.jasperreports.pdf.common.PdfStructureEntry;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardChunk implements PdfChunk
{

	private StandardPdfProducer pdfProducer;
	protected Chunk chunk;

	private PdfStructureEntry linkTag;
	private float linkLlx;
	private float linkLly;
	private float linkUrx;
	private float linkUry;
	private String linkContents;

	public StandardChunk(StandardPdfProducer pdfProducer, Chunk chunk)
	{
		this.pdfProducer = pdfProducer;
		this.chunk = chunk;
	}

	public Chunk getChunk()
	{
		return chunk;
	}
	
	@Override
	public void setLocalDestination(String anchorName, PdfStructureEntry structureEntry)
	{
		chunk.setLocalDestination(anchorName);
		if (structureEntry != null
				&& pdfProducer.isPdf2())
		{
			chunk.setLocalDestinationStructElement(
					((StandardStructureEntry) structureEntry).getElement().getReference());
		}
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
	public void setLocalGotoPage(int page, float top, Supplier<PdfStructureEntry> targetStructureEntry)
	{
		PdfDestination dest = new PdfDestination(PdfDestination.XYZ, 0, top, 0);
		PdfAction action = PdfAction.gotoLocalPage(page, dest, pdfProducer.getPdfWriter());
		if (targetStructureEntry != null
				&& pdfProducer.isPdf2())
		{
			StandardStructureEntry targetStructure = (StandardStructureEntry) targetStructureEntry.get();
			if (targetStructure != null)
			{
				PdfStructureElement element = targetStructure.getElement();
				PdfArray sd = new PdfArray();
				sd.add(element.getReference());
				sd.add(dest.getPdfObject(1));
				sd.add(dest.getPdfObject(2));
				sd.add(dest.getPdfObject(3));
				sd.add(dest.getPdfObject(4));
				action.put(new PdfName("SD"), sd);
			}
		}
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

		PdfStructureElement element = ((StandardStructureEntry) linkTag).getElement();

		PdfStructureTreeRoot treeRoot = pdfProducer.getPdfWriter().getStructureTreeRoot();
		int structParent = treeRoot.addExistingObject(element.getReference());
		annotation.put(PdfName.STRUCTPARENT, new PdfNumber(structParent));

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
