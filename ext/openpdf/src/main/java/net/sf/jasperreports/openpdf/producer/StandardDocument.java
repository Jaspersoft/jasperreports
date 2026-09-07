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

import org.openpdf.text.Document;

import net.sf.jasperreports.pdf.common.PdfDocument;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardDocument implements PdfDocument
{

	private Document document;

	public StandardDocument(Document document)
	{
		this.document = document;
	}

	public Document getDocument()
	{
		return document;
	}

	@Override
	public void addTitle(String title)
	{
		document.addTitle(title);
	}

	@Override
	public void addAuthor(String author)
	{
		document.addAuthor(author);
	}

	@Override
	public void addSubject(String subject)
	{
		document.addSubject(subject);
	}

	@Override
	public void addKeywords(String keywords)
	{
		document.addKeywords(keywords);
	}

	@Override
	public void addCreator(String creator)
	{
		document.addCreator(creator);
	}

	@Override
	public void addProducer(String producer)
	{
		document.addProducer(producer);
	}

	@Override
	public void open()
	{
		document.open();
	}
	
}
