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

import java.util.function.Function;
import java.util.function.Supplier;

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
import com.lowagie.text.pdf.PdfWriter;

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
	private PdfStructureEntry styledTextLinkTag;
	private StandardStructureEntry markedContentTag;

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
	public void setLocalDestination(String anchorName,
			// structure entry not supported by the classic producer
			PdfStructureEntry structureEntry)
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
	public void setStyledTextLinkTag(PdfStructureEntry linkTag, String linkContents)
	{
		if (!StandardPdfUtils.isCustomStructureTreeRootSupported())
		{
			// without the annotation /StructParent support the Link tag would be incomplete
			// anyway; leave the annotation to be created by the library, untagged
			return;
		}

		this.styledTextLinkTag = linkTag;
		this.linkContents = linkContents;
	}

	@Override
	public void setMarkedContentTag(PdfStructureEntry markedContentTag)
	{
		this.markedContentTag = (StandardStructureEntry) markedContentTag;
		pdfProducer.registerChunkMarkedContent(chunk, this.markedContentTag);
	}

	public StandardStructureEntry getMarkedContentTag()
	{
		return markedContentTag;
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
		else if (
			!deferStyledTextLinkAnnotation(
				rect -> PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					rect,
					PdfAnnotation.HIGHLIGHT_INVERT,
					PdfAction.javaScript(script, pdfProducer.getPdfWriter())
					)
				)
			)
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
		else if (
			!deferStyledTextLinkAnnotation(
				rect -> new PdfAnnotation(
					pdfProducer.getPdfWriter(),
					rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop(),
					new PdfAction(reference)
					)
				)
			)
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
		else if (
			!deferStyledTextLinkAnnotation(
				rect -> PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					rect,
					PdfAnnotation.HIGHLIGHT_INVERT,
					anchor
					)
				)
			)
		{
			chunk.setLocalGoto(anchor);
		}
	}

	@Override
	public void setLocalGotoPage(int page, float top, Supplier<PdfStructureEntry> targetStructureEntry)
	{
		// targetStructureEntry not supported by the classic producer
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
		else if (
			!deferStyledTextLinkAnnotation(
				rect -> PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					rect,
					PdfAnnotation.HIGHLIGHT_INVERT,
					action
					)
				)
			)
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
		else if (
			!deferStyledTextLinkAnnotation(
				rect -> PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					rect,
					PdfAnnotation.HIGHLIGHT_INVERT,
					new PdfAction(reference, anchor)
					)
				)
			)
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
		else if (
			!deferStyledTextLinkAnnotation(
				rect -> PdfAnnotation.createLink(
					pdfProducer.getPdfWriter(),
					rect,
					PdfAnnotation.HIGHLIGHT_INVERT,
					new PdfAction(reference, page)
					)
				)
			)
		{
			chunk.setRemoteGoto(reference, page);
		}
	}

	/**
	 * Defers the creation of the hyperlink annotation of a styled text hyperlink to the moment
	 * the text layout places this chunk on the page, as the position of the annotation is only
	 * known then.
	 *
	 * @return whether the annotation creation was taken over; when it was not, the annotation is
	 * left to be created by the PDF library out of the chunk action
	 */
	protected boolean deferStyledTextLinkAnnotation(Function<Rectangle, PdfAnnotation> annotationFactory)
	{
		if (styledTextLinkTag == null)
		{
			return false;
		}

		pdfProducer.deferChunkAnnotation(
			chunk,
			rect -> addAnnotationToTag(styledTextLinkTag, annotationFactory.apply(rect))
			);
		return true;
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

		if (linkTag == null)
		{
			// the Link tag could not be created, add the annotation on its own
			pdfProducer.getPdfWriter().addAnnotation(annotation);
			return;
		}

		PdfStructureElement element = ((StandardStructureEntry) linkTag).getElement();

		if (StandardPdfUtils.isCustomStructureTreeRootSupported())
		{
			StandardPdfStructureTreeRoot treeRoot = (StandardPdfStructureTreeRoot) pdfProducer.getPdfWriter().getStructureTreeRoot();
			treeRoot.addAnnotationParent(annotation, element.getReference());
		}

		if (element.get(PdfName.PG) == null)
		{
			// the tag holds no marked content of its own, hence it has not been associated with a
			// page yet
			PdfWriter pdfWriter = pdfProducer.getPdfWriter();
			element.put(PdfName.PG, pdfWriter.getPageReference(pdfWriter.getCurrentPageNumber()));
		}

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
		else if (kObj == null)
		{
			PdfArray ar = new PdfArray();
			ar.add(objr);
			element.put(PdfName.K, ar);

			// the object reference became the first kid of the structure element, and the PDF
			// library refuses to place marked content in an element whose first kid is not marked
			// content; this only occurs for a tag whose chunks did not write any text
			((StandardStructureEntry) linkTag).setMarkedContentDisallowed();
		}
	}

}
