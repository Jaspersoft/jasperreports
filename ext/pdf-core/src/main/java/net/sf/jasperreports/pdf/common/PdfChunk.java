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
	 * text is written, and the Link structure element is only requested from the supplier at that
	 * moment, so that no empty Link tag is created for a chunk that does not get rendered.
	 * </p>
	 *
	 * <p>
	 * Producers that do not support this leave the hyperlink annotation to be created by the PDF
	 * library, in which case the annotation is not referenced from the structure tree.
	 * </p>
	 *
	 * @param linkTagSupplier supplier of the Link structure element to attach the annotation to
	 * @param linkContents the alternate description of the link
	 */
	void setStyledTextLinkTag(Supplier<PdfStructureEntry> linkTagSupplier, String linkContents);

}
