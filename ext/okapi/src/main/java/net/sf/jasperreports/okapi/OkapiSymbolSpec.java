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
import java.io.Serializable;
import java.util.Locale;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.okapi.OkapiElementType.ExpressionProperty;
import uk.org.okapibarcode.backend.Symbol;


/**
 * Everything needed to encode one barcode again: the component settings, the evaluated
 * expression values and the sizes computed by {@link OkapiLayout}.
 * <p>
 * Okapi symbols are not serializable, so a {@link OkapiRenderer} keeps this instead and
 * encodes the symbol again after deserialization, with the same settings and values.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
class OkapiSymbolSpec implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private final OkapiComponent component;
	private final Object[] values;
	private final double unitsToPoints;
	private final String fontName;
	private final int fontStyle;
	private final float fontSize;
	private final Locale locale;
	private final Color ink;
	private final Color paper;

	// set by OkapiLayout
	double modulePt = 1;
	int quietZoneHorizontal;
	int quietZoneVertical;
	int barHeight;

	/**
	 * @param component the component; a copy without expressions is kept
	 * @param values the evaluated expression values, in the order of {@link OkapiElementType#getExpressions()}
	 * @param unitsToPoints the factor from report units to points
	 * @param font the font of the human-readable text, sized in points; null for types without text
	 * @param ink the bar colour
	 * @param paper the background colour, or null for transparent
	 */
	<C extends OkapiComponent> OkapiSymbolSpec(C component, Object[] values, double unitsToPoints,
			Font font, Locale locale, Color ink, Color paper)
	{
		this.component = withoutExpressions(component);
		this.values = values.clone();
		this.unitsToPoints = unitsToPoints;
		this.fontName = font == null ? null : font.getName();
		this.fontStyle = font == null ? Font.PLAIN : font.getStyle();
		this.fontSize = font == null ? 0 : font.getSize2D();
		this.locale = locale;
		this.ink = ink;
		this.paper = paper;
	}

	private static <C extends OkapiComponent> C withoutExpressions(C component)
	{
		OkapiElementType<C> type = OkapiElementTypes.getType(component);
		@SuppressWarnings("unchecked")
		C copy = (C) component.clone();
		for (ExpressionProperty<C> expression : type.getExpressions())
		{
			expression.set(copy, null);
		}
		return copy;
	}

	public OkapiComponent getComponent()
	{
		return component;
	}

	public String getCode()
	{
		return (String) values[0];
	}

	/**
	 * The size of one module, in points.
	 */
	public double getModulePt()
	{
		return modulePt;
	}

	public Color getInk()
	{
		return ink;
	}

	public Color getPaper()
	{
		return paper;
	}

	/**
	 * Encodes the symbol. Okapi reports invalid data with runtime exceptions, which are
	 * passed on.
	 */
	public Symbol createSymbol(JasperReportsContext jasperReportsContext)
	{
		return createSymbol(OkapiElementTypes.getType(component), component, jasperReportsContext);
	}

	private <C extends OkapiComponent> Symbol createSymbol(OkapiElementType<C> type, OkapiComponent component,
			JasperReportsContext jasperReportsContext)
	{
		@SuppressWarnings("unchecked")
		C typedComponent = (C) component;
		Symbol symbol = type.createSymbol(typedComponent);
		// one Okapi unit is one module, so the module width stays 1 and the renderer scales units to modulePt
		symbol.setQuietZoneHorizontal(quietZoneHorizontal);
		symbol.setQuietZoneVertical(quietZoneVertical);
		if (type.getLayout() == OkapiElementType.Layout.LINEAR)
		{
			symbol.setBarHeight(Math.max(1, barHeight));
		}
		type.applySizes(typedComponent, symbol, modulePt, unitsToPoints);
		if (type.hasText() && fontName != null)
		{
			symbol.setFont(getFont(jasperReportsContext, (float) (fontSize / modulePt)));
		}
		type.applyValues(typedComponent, symbol, values);
		// last, as Okapi encodes when the content is set
		symbol.setContent(getCode());
		return symbol;
	}

	/**
	 * The text font in Okapi units, from the font extensions when it is there.
	 */
	private Font getFont(JasperReportsContext jasperReportsContext, float size)
	{
		Font font = FontUtil.getInstance(jasperReportsContext).getAwtFontFromBundles(
				fontName, fontStyle, size, locale, true);
		if (font == null)
		{
			font = new Font(fontName, fontStyle, 1).deriveFont(size);
		}
		return font;
	}
}
