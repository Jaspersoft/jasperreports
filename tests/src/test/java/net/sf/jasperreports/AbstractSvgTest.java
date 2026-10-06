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
package net.sf.jasperreports;

import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleGraphics2DReportConfiguration;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractSvgTest extends AbstractTest
{
	@Override
	protected void export(JasperPrint print, OutputStream out) throws JRException, IOException
	{
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		Document document = domImpl.createDocument(null, "svg", null);
		SVGGraphics2D grx = 
			new SVGGraphics2D(
				SVGGeneratorContext.createDefault(document), 
				false // this is for textAsShapes, but does not seem to have effect in our case
				);
		
		JRGraphics2DExporter exporter = new JRGraphics2DExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(print));
		SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
		Graphics2D g = (Graphics2D)grx.create();
		output.setGraphics2D(g);
		exporter.setExporterOutput(output);
		
		for (int pageIndex = 0; pageIndex < print.getPages().size(); pageIndex++)
		{
			g.translate(0,  pageIndex * print.getPageHeight());
			
			SimpleGraphics2DReportConfiguration configuration = new SimpleGraphics2DReportConfiguration();
			configuration.setPageIndex(pageIndex);
			exporter.setConfiguration(configuration);

			exporter.exportReport();
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// use OutputStreamWriter instead of StringWriter so that we have "encoding" attribute in <xml> header tag.
		grx.stream(new OutputStreamWriter(baos, "UTF-8"), true);
		
		SVGTranscoder transcoder = new SVGTranscoder();
		transcoder.addTranscodingHint(SVGTranscoder.KEY_NEWLINE, SVGTranscoder.VALUE_NEWLINE_LF);
		try
		{
			transcoder.transcode(
				new TranscoderInput(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), "UTF-8")), 
				new TranscoderOutput(new OutputStreamWriter(out, "UTF-8"))
				);
		}
		catch (TranscoderException e)
		{
			throw new JRException(e);
		}
		
		out.close();
	}

	@Override
	protected String getExportFileExtension()
	{
		return "svg";
	}
}
