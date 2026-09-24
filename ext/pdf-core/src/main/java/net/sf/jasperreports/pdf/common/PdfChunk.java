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
package net.sf.jasperreports.pdf.common;

import java.util.function.Supplier;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PdfChunk
{

	void setLocalDestination(String anchorName, PdfStructureEntry structureEntry);

	void setJavaScriptAction(String script);

	void setAnchor(String reference);

	void setLocalGoto(String anchor);

	void setLocalGotoPage(int page, float top, Supplier<PdfStructureEntry> targetStructureEntry);

	void setRemoteGoto(String reference, String anchor);

	void setRemoteGoto(String reference, int page);

	void setLinkTag(PdfStructureEntry linkTag, float llx, float lly, float urx, float ury, String linkContents);

	/**
	 * Sets the Link structure element that the hyperlink annotation of this chunk needs to be
	 * attached to, for a hyperlink that only covers a part of the text element, as specified in
	 * the styled text of the element.
	 *
	 * <p>
	 * Unlike {@link #setLinkTag(PdfStructureEntry, float, float, float, float, String)}, the
	 * position of the annotation is not known when this method is called, as it depends on where
	 * the chunk ends up when the text is laid out. The annotation is therefore created while the
	 * text is written.
	 * </p>
	 *
	 * <p>
	 * The Link structure element is the one that the text of the chunk goes into as well, as set
	 * by {@link #setMarkedContentTag(PdfStructureEntry)}, so that it holds both the text of the
	 * hyperlink and the reference to its annotation.
	 * </p>
	 *
	 * <p>
	 * Producers that do not support this leave the hyperlink annotation to be created by the PDF
	 * library, in which case the annotation is not referenced from the structure tree.
	 * </p>
	 *
	 * @param linkTag the Link structure element to attach the annotation to
	 * @param linkContents the alternate description of the link
	 */
	void setStyledTextLinkTag(PdfStructureEntry linkTag, String linkContents);

	/**
	 * Sets the structure element that the text of this chunk belongs to, for a chunk that is part
	 * of a text paragraph whose marked content is created by its chunks.
	 *
	 * <p>
	 * The marked content sequence that the text of the chunk goes into can only be opened and
	 * closed while the text is laid out, because that is when the position of the chunk inside the
	 * content stream is decided. Consecutive chunks that were given the same structure element end
	 * up in a single marked content sequence.
	 * </p>
	 *
	 * <p>
	 * Producers that do not support this leave the text of the chunk untagged.
	 * </p>
	 *
	 * @param markedContentTag the structure element to add the marked content of the chunk to
	 */
	void setMarkedContentTag(PdfStructureEntry markedContentTag);

}
