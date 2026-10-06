/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.renderers.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.w3c.dom.svg.SVGDocument;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.BatikUserAgent;


/**
 * Utility class that uses <a href="http://xmlgraphics.apache.org/batik/">Batik</a> to check if the provided byte data
 * is a valid SVG image.
 * It does this by attempting to parse the data into an SVG document.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SvgDataSniffer
{
	/**
	 * 
	 */
	private final SVGDocumentFactory documentFactory;
	
	/**
	 * 
	 */
	private SvgDataSniffer(JasperReportsContext jasperReportsContext)
	{
		UserAgent userAgent = new BatikUserAgent(jasperReportsContext);
		documentFactory =
			new SAXSVGDocumentFactory(userAgent.getXMLParserClassName(), true);
		documentFactory.setValidating(userAgent.isXMLParserValidating());
	}
	
	/**
	 * 
	 */
	public static SvgDataSniffer getInstance(JasperReportsContext jasperReportsContext)
	{
		return new SvgDataSniffer(jasperReportsContext);
	}
	
	/**
	 * 
	 */
	public boolean isSvgData(byte[] data)
	{
		return getSvgInfo(data) != null;
	}
	
	/**
	 * 
	 */
	public SvgInfo getSvgInfo(byte[] data)
	{
		try
		{
			SVGDocument document = 
				documentFactory.createSVGDocument(
					null,
					new ByteArrayInputStream(data)
					);
			
			return new SvgInfo(document.getInputEncoding());
		}
		catch (IOException e)
		{
			return null;
		}
	}
	
	/**
	 * 
	 */
	public static final class SvgInfo
	{
		private final String encoding;
		
		private SvgInfo(String encoding)
		{
			this.encoding = encoding;
		}
		
		public String getEncoding()
		{
			return encoding;
		}
	}
}
