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
package net.sf.jasperreports.barbecue;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleContainer;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.util.StyleResolver;

/**
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public final class BarbecueStyleResolver {
	
	/**
	 * The barcode background color used for transparent elements, which leaves the background unpainted.
	 */
	private static final Color TRANSPARENT_BACKGROUND = new Color(0, 0, 0, 0);
	
	private BarbecueStyleResolver()
	{
	}
	
	/**
	 * 
	 */
	public static StyleResolver getStyleResolver(JRComponentElement element)
	{
		if (element != null)
		{
			JRDefaultStyleProvider defaultStyleProvider = element.getDefaultStyleProvider();
			if (defaultStyleProvider != null)
			{
				return defaultStyleProvider.getStyleResolver();
			}
		}
		return StyleResolver.getInstance();
	}

	/**
	 * 
	 */
	public static RotationEnum getRotation(JRComponentElement element)
	{
		RotationEnum ownRotation = ((BarbecueComponent)element.getComponent()).getOwnRotation();
		if (ownRotation != null) {
			return ownRotation;
		}
		JRStyle style = getStyleResolver(element).getBaseStyle(element);
		if (style != null) {
			RotationEnum rotation = style.getRotation();
			if (rotation != null) {
				return rotation;
			}
		}
		return RotationEnum.NONE;
	}

	/**
	 * Returns the AWT font for the barcode text, as set or inherited through style by the given element,
	 * resolved through the font extensions.
	 */
	public static Font getFont(JasperReportsContext jasperReportsContext, JRStyleContainer styleContainer, Locale locale)
	{
		return FontUtil.getInstance(jasperReportsContext).getAwtFont(new JRBaseFont(styleContainer), locale);
	}

	/**
	 * Returns the barcode background color for the given element, which is its backcolor when the element is opaque,
	 * and a fully transparent color otherwise, so that whatever lies behind the element shows through.
	 */
	public static Color getBackground(JRPrintElement element)
	{
		return element.getMode() == ModeEnum.OPAQUE ? element.getBackcolor() : TRANSPARENT_BACKGROUND;
	}

}
