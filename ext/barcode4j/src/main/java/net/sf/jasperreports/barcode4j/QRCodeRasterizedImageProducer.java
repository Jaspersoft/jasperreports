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
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.util.RendererUtil;


/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class QRCodeRasterizedImageProducer implements QRCodeImageProducer
{
	
	@Override
	public Renderable createImage(
		JasperReportsContext jasperReportsContext,
		JRComponentElement componentElement, 
		QRCodeBean qrCodeBean, 
		String message
		)
	{
		QRCodeWriter writer = new QRCodeWriter();

		Map<EncodeHintType,Object> hints = new HashMap<>();
		
		String encoding = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(
				componentElement, QRCodeComponent.PROPERTY_QRCODE_CHARACTER_ENCODING, QRCodeComponent.PROPERTY_DEFAULT_ENCODING);
		if (!encoding.isEmpty())
		{
			hints.put(EncodeHintType.CHARACTER_SET, encoding);
		}
		
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevelEnum.getValueOrDefault(qrCodeBean.getErrorCorrectionLevel()).getErrorCorrectionLevel());
		
		int margin = qrCodeBean.getMargin() == null ? QRCodeSVGImageProducer.DEFAULT_MARGIN : qrCodeBean.getMargin();
		hints.put(EncodeHintType.MARGIN, margin);
		
		Integer qrVersion = qrCodeBean.getQrVersion();
		if (qrVersion != null)
		{
			hints.put(EncodeHintType.QR_VERSION, qrVersion);
		}

		int resolution = JRPropertiesUtil.getInstance(jasperReportsContext).getIntegerProperty(
				componentElement, BarcodeRasterizedImageProducer.PROPERTY_RESOLUTION, 300);
		try
		{
			BitMatrix matrix = 
				writer.encode(
					message,
					BarcodeFormat.QR_CODE,
//					(int)((72f / 2.54f) * componentElement.getWidth()), 
//					(int)((72f / 2.54f) * componentElement.getHeight()), 
					(int)((resolution / 72f) * componentElement.getWidth()), 
					(int)((resolution / 72f) * componentElement.getHeight()), 
					hints
					);
			BufferedImage image = getImage(matrix, componentElement.getForecolor());
			return RendererUtil.getInstance(jasperReportsContext).getRenderable(image, ImageTypeEnum.PNG, OnErrorTypeEnum.ERROR);
		}
		catch (WriterException | JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	public BufferedImage getImage(BitMatrix matrix, Color onColor) 
	{
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int onArgb = JRColorUtil.getOpaqueArgb(onColor, Color.BLACK);//not actually opaque
		for (int x = 0; x < width; x++) 
		{
			for (int y = 0; y < height; y++) 
			{
				if (matrix.get(x, y))
				{
					image.setRGB(x, y, onArgb);
				}
			}
		}
		return image;
	}
}
