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
package net.sf.jasperreports.engine.xml.print;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.ResourceRenderer;
import net.sf.jasperreports.renderers.SimpleDataRenderer;
import net.sf.jasperreports.util.Base64Util;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ImageLoader
{
	
	private static final String EXCEPTION_MESSAGE_KEY_DECODING_ERROR = "xml.print.image.decoding.error";
	
	private static final ImageLoader INSTANCE = new ImageLoader();
	
	public static ImageLoader instance()
	{
		return INSTANCE;
	}

	public void loadImage(XmlLoader xmlLoader, JasperPrint jasperPrint, Consumer<? super JRPrintImage> consumer)
	{
		JRBasePrintImage image = new JRBasePrintImage(jasperPrint.getDefaultStyleProvider());
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_scaleImage, ScaleImageEnum::getByName, image::setScaleImage);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_rotation, RotationEnum::getByName, image::setRotation);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_hAlign, HorizontalImageAlignEnum::getByName, image::setHorizontalImageAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_vAlign, VerticalImageAlignEnum::getByName, image::setVerticalImageAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_onErrorType, OnErrorTypeEnum::getByName, image::setOnErrorType);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkType, image::setLinkType);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTarget, image::setLinkTarget);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_anchorName, image::setAnchorName);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkReference, image::setHyperlinkReference);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkAnchor, image::setHyperlinkAnchor);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkPage, image::setHyperlinkPage);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTooltip, image::setHyperlinkTooltip);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_bookmarkLevel, image::setBookmarkLevel);
		Boolean lazyAttribute = xmlLoader.getBooleanAttribute(JRXmlConstants.ATTRIBUTE_isLazy);
		boolean lazy = lazyAttribute != null ? lazyAttribute : false;
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_reportElement:
				ReportElementLoader.instance().loadReportElement(xmlLoader, jasperPrint, image);
				break;
			case JRXmlConstants.ELEMENT_box:
				BoxLoader.instance().loadBox(xmlLoader, image.getLineBox());
				break;
			case JRXmlConstants.ELEMENT_graphicElement:
				ReportElementLoader.instance().loadGraphicElement(xmlLoader, image);
				break;
			case JRXmlConstants.ELEMENT_imageSource:
				setImageSource(xmlLoader, image, lazy);
				break;
			case JRXmlConstants.ELEMENT_hyperlinkParameter:
				HyperlinkLoader.instance().loadHyperlinkParameter(xmlLoader, image::addHyperlinkParameter);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		consumer.accept(image);
	}

	protected void setImageSource(XmlLoader xmlLoader, JRBasePrintImage image, boolean lazy)
	{
  		Boolean embeddedAttribute = xmlLoader.getBooleanAttribute(JRXmlConstants.ATTRIBUTE_isEmbedded);
  		boolean embedded = embeddedAttribute != null ? embeddedAttribute : false;
  		String imageSource = xmlLoader.loadText(true);
  		
		Renderable renderable = null;
		if (lazy)
		{
			renderable = ResourceRenderer.getInstance(imageSource, true);
		}
		else
		{
			if (embedded)
			{
				try
				{
					ByteArrayInputStream bais = new ByteArrayInputStream(imageSource.getBytes("UTF-8"));//UTF-8 is fine here as Base64 only has ASCII characters anyway
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					
					Base64Util.decode(bais, baos);
					
					renderable = SimpleDataRenderer.getInstance(baos.toByteArray());//, JRImage.ON_ERROR_TYPE_ERROR));
				}
				catch (Exception e)
				{
					throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_DECODING_ERROR, null, e);
				}
			}
			else
			{
				renderable = ResourceRenderer.getInstance(imageSource, false);
			}
		}

		image.setRenderer(renderable);
	}

}
