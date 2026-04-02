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

import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfAction;

import net.sf.jasperreports.pdf.common.PdfChunk;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardChunk implements PdfChunk
{

	private StandardPdfProducer pdfProducer;
	protected Chunk chunk;

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
	public void setLocalDestination(String anchorName)
	{
		chunk.setLocalDestination(anchorName);
	}

	@Override
	public void setJavaScriptAction(String script)
	{
		chunk.setAction(PdfAction.javaScript(script, pdfProducer.getPdfWriter()));
	}

	@Override
	public void setAnchor(String reference)
	{
		chunk.setAnchor(reference);
	}

	@Override
	public void setLocalGoto(String anchor)
	{
		chunk.setLocalGoto(anchor);
	}

	@Override
	public void setRemoteGoto(String reference, String anchor)
	{
		chunk.setRemoteGoto(reference, anchor);
	}

	@Override
	public void setRemoteGoto(String reference, int page)
	{
		chunk.setRemoteGoto(reference, page);
	}

}
