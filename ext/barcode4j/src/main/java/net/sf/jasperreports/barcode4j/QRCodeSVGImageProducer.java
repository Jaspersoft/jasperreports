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
package net.sf.jasperreports.barcode4j;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.SimpleRenderToImageAwareDataRenderer;


/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class QRCodeSVGImageProducer implements QRCodeImageProducer
{

	public static final int DEFAULT_MARGIN = 4;//same as zxing's QRCodeWriter.QUIET_ZONE_SIZE
	
	@Override
	public Renderable createImage(
		JasperReportsContext jasperReportsContext,
		JRComponentElement componentElement,
		QRCodeBean qrCodeBean, 
		String message
		)
	{
		Map<EncodeHintType,Object> hints = new HashMap<>();
		
		String encoding = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(
				componentElement, QRCodeComponent.PROPERTY_QRCODE_CHARACTER_ENCODING, QRCodeComponent.PROPERTY_DEFAULT_ENCODING);
		if (!encoding.isEmpty())
		{
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		
		ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevelEnum.getValueOrDefault(qrCodeBean.getErrorCorrectionLevel()).getErrorCorrectionLevel();
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

		Integer qrVersion = qrCodeBean.getQrVersion();
		if (qrVersion != null)
		{
			hints.put(EncodeHintType.QR_VERSION, qrVersion);
		}

		ByteMatrix matrix = null;
		SVGCanvasProvider provider = null;
		try
		{
			QRCode qrCode = Encoder.encode(message, errorCorrectionLevel, hints);
			matrix = qrCode.getMatrix();
			
			provider = new SVGCanvasProvider(false, OrientationEnum.UP.getValue());
		}
		catch (WriterException | BarcodeCanvasSetupException e)
		{
			throw new JRRuntimeException(e);
		}

		Document svgDoc = provider.getDOM();
		Element svg = svgDoc.getDocumentElement();
		int codeWidth = matrix.getWidth();
		int codeHeight = matrix.getHeight();
		
		JRStyle elementStyle = componentElement.getStyle();
		int elementWidth = componentElement.getWidth() - (elementStyle == null ? 0
				: (elementStyle.getLineBox().getLeftPadding() + elementStyle.getLineBox().getRightPadding()));
		int elementHeight = componentElement.getHeight() - (elementStyle == null ? 0
				: (elementStyle.getLineBox().getTopPadding() + elementStyle.getLineBox().getBottomPadding()));
		
		int margin = qrCodeBean.getMargin() == null ? DEFAULT_MARGIN : qrCodeBean.getMargin();
		int matrixWidth = codeWidth + 2 * margin;
		int matrixHeight = codeHeight + 2 * margin;
		
		// scaling to match the image size as closely as possible so that it looks good in html.
		// the resolution is taken into account because the html exporter rasterizes to a png 
		// that has the same size as the svg.
		int resolution = JRPropertiesUtil.getInstance(jasperReportsContext).getIntegerProperty(
				componentElement, BarcodeRasterizedImageProducer.PROPERTY_RESOLUTION, 300);
		int imageWidth = (int) Math.ceil(elementWidth * (resolution / 72d));
		int imageHeight = (int) Math.ceil(elementHeight * (resolution / 72d));
		
		double horizontalScale = ((double) imageWidth) / matrixWidth;
		double verticalScale = ((double) imageHeight) / matrixHeight;
		
		// we are scaling with integer units, not considering fractional coordinates for now
		int scale = Math.max(1, (int) Math.min(Math.ceil(horizontalScale), Math.ceil(verticalScale)));
		int qrWidth = scale * matrixWidth;
		int qrHeight = scale * matrixHeight;

		// scaling again because of the integer units
		double qrScale = Math.max(1d, Math.max(((double) qrWidth) / imageWidth, ((double) qrHeight) / imageHeight));
		int svgWidth = (int) Math.ceil(qrScale * imageWidth);
		int svgHeight = (int) Math.ceil(qrScale * imageHeight);
		svg.setAttribute("width", String.valueOf(svgWidth));
		svg.setAttribute("height",String.valueOf(svgHeight));
		svg.setAttribute("viewBox", "0 0 " + svgWidth + " " + svgHeight);

		int xOffset = Math.max(0, (svgWidth - qrWidth) / 2);
		int yOffset = Math.max(0, (svgHeight - qrHeight) / 2);
		
		Color color = componentElement.getForecolor();
		String fill = "#" + JRColorUtil.getColorHexa(color);
		String fillOpacity = color.getAlpha() < 255 ? Float.toString(((float) color.getAlpha()) / 255) : null;
		String rectangleSize = Integer.toString(scale);
		for (int x = 0; x < codeWidth; x++) {
			for (int y = 0; y < codeHeight; y++) {
				if (matrix.get(x,y) == 1) {
					Element element = svgDoc.createElementNS(svg.getNamespaceURI(), "rect");
					element.setAttribute("x", String.valueOf(xOffset + scale * (x + margin)));
					element.setAttribute("y", String.valueOf(yOffset + scale * (y + margin)));
					element.setAttribute("width", rectangleSize);
					element.setAttribute("height", rectangleSize);
					element.setAttribute("fill", fill);
					if (fillOpacity != null) {
						element.setAttribute("fill-opacity", fillOpacity);
					}
					svgDoc.getFirstChild().appendChild(element);
				}
			}
		}        

		Source source = new DOMSource(svgDoc);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Result output = new StreamResult(baos);

		try
		{
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(source, output);

		}
		catch (TransformerException e)
		{
			throw new JRRuntimeException(e);
		}

		return SimpleRenderToImageAwareDataRenderer.getInstance(baos.toByteArray());
	}

}
