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
package net.sf.jasperreports.okapi;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.util.StyleResolver;


/**
 * Creates the image of an Okapi barcode element, at fill time and for the design preview.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
final class OkapiImageFactory
{

	public static final String EXCEPTION_MESSAGE_KEY_ENCODING_FAILED = "components.okapi.encoding.failed";

	private OkapiImageFactory()
	{
	}

	/**
	 * The rotation of the barcode image: the component's own, else the element style's, else none.
	 */
	static RotationEnum getRotation(JRComponentElement element)
	{
		RotationEnum rotation = ((OkapiComponent) element.getComponent()).getRotation();
		if (rotation == null)
		{
			JRDefaultStyleProvider defaultStyleProvider = element.getDefaultStyleProvider();
			StyleResolver styleResolver = defaultStyleProvider == null
					? StyleResolver.getInstance()
					: defaultStyleProvider.getStyleResolver();
			JRStyle style = styleResolver.getBaseStyle(element);
			rotation = style == null ? null : style.getRotation();
		}
		return rotation == null ? RotationEnum.NONE : rotation;
	}

	/**
	 * Encodes and sizes the barcode of an element.
	 *
	 * @param values the evaluated expression values, the code at index 0, not null
	 * @throws JRRuntimeException naming the element, when Okapi can't encode the data
	 */
	static OkapiLayout createImage(JasperReportsContext jasperReportsContext, JRComponentElement element,
			Object[] values, int reportDpi, Locale locale)
	{
		OkapiComponent component = (OkapiComponent) element.getComponent();
		try
		{
			double unitsToPoints = reportDpi == JasperPrint.DEFAULT_REPORT_DPI
					? 1 : (double) JasperPrint.DEFAULT_REPORT_DPI / reportDpi;

			Font font = OkapiElementTypes.getType(component).hasText()
					? FontUtil.getInstance(jasperReportsContext).getAwtFont(new JRBaseFont(element), locale)
					: null;
			Color paper = element.getMode() == ModeEnum.OPAQUE ? element.getBackcolor() : null;
			OkapiSymbolSpec spec = new OkapiSymbolSpec(component, values, unitsToPoints,
					font, locale, element.getForecolor(), paper);

			// the barcode is drawn upright and the image is rotated
			RotationEnum rotation = getRotation(element);
			boolean sideways = rotation == RotationEnum.LEFT || rotation == RotationEnum.RIGHT;
			double width = (sideways ? element.getHeight() : element.getWidth()) * unitsToPoints;
			double height = (sideways ? element.getWidth() : element.getHeight()) * unitsToPoints;

			return OkapiLayout.layout(jasperReportsContext, spec, width, height, unitsToPoints);
		}
		catch (JRRuntimeException e)
		{
			throw e;
		}
		catch (RuntimeException e)
		{
			// Okapi reports invalid data and settings with runtime exceptions, not all of them OkapiInputException
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_ENCODING_FAILED,
					new Object[]{getElementName(element), e.getMessage()}, e);
		}
	}

	private static String getElementName(JRComponentElement element)
	{
		return element.getKey() == null ? String.valueOf(element.getUUID()) : element.getKey();
	}
}
