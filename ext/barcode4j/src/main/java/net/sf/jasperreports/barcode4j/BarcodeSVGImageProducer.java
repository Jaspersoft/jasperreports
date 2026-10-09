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
package net.sf.jasperreports.barcode4j;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.w3c.dom.Document;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.SimpleRenderToImageAwareDataRenderer;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarcodeSVGImageProducer implements BarcodeImageProducer
{

	@Override
	public Renderable createImage(
		JasperReportsContext jasperReportsContext,
		JRComponentElement componentElement,
		BarcodeGenerator barcode, 
		String message
		)
	{
		try
		{
			SVGCanvasProvider provider = 
				new SVGCanvasProvider(
					false, 
					OrientationEnum.getValueOrDefault(
						((Barcode4jComponent)componentElement.getComponent()).getOrientation()
						).getValue()
					);
			barcode.generateBarcode(provider, message);
			Document svgDoc = provider.getDOM();

			Source source = new DOMSource(svgDoc);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Result output = new StreamResult(baos);
			Transformer transformer = JRXmlUtils.createTransformerFactory()
					.newTransformer();
			transformer.transform(source, output);

			return SimpleRenderToImageAwareDataRenderer.getInstance(baos.toByteArray());
		}
		catch (Exception e)
		{
			throw new JRRuntimeException(e);
		}
	}

}
