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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.convert;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRExpressionUtil;
import net.sf.jasperreports.renderers.ResourceRenderer;
import net.sf.jasperreports.renderers.util.RendererUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class ImageConverter extends ElementConverter
{
	/**
	 *
	 */
	private final static ImageConverter INSTANCE = new ImageConverter();
	
	/**
	 *
	 */
	private ImageConverter()
	{
	}

	/**
	 *
	 */
	public static ImageConverter getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public JRPrintElement convert(ReportConverter reportConverter, JRElement element)
	{
		JRBasePrintImage printImage = new JRBasePrintImage(reportConverter.getDefaultStyleProvider());
		JRImage image = (JRImage)element;

		copyGraphicElement(reportConverter, image, printImage);

		printImage.copyBox(image.getLineBox());
		
		printImage.setAnchorName(JRExpressionUtil.getExpressionText(image.getAnchorNameExpression()));
		printImage.setBookmarkLevel(image.getBookmarkLevel());
		printImage.setHorizontalImageAlign(image.getOwnHorizontalImageAlign());
		printImage.setLinkType(image.getLinkType());
		printImage.setOnErrorType(OnErrorTypeEnum.ICON);
		printImage.setVerticalImageAlign(image.getOwnVerticalImageAlign());
		
		String location = JRExpressionUtil.getSimpleExpressionText(image.getExpression());
		if (location == null)
		{
			// no static location to load from; show the no-image icon at its natural size
			printImage.setRenderer(RendererUtil.NO_IMAGE_RENDERER);
			printImage.setScaleImage(ScaleImageEnum.CLIP);
		}
		else
		{
			printImage.setRenderer(ResourceRenderer.getInstance(location, image.isLazy()));
			printImage.setScaleImage(image.getOwnScaleImage());
		}

		printImage.setRotation(image.getOwnRotation());
		
		return printImage;
	}

}
