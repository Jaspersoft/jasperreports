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

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import uk.org.okapibarcode.backend.Symbol;


/**
 * Sizes a barcode against its element.
 * <p>
 * One Okapi unit is one module. A size setting is used as set when the symbol fits the
 * element, and the image then uses {@link ScaleImageEnum#CLIP}, drawing it at exactly that size.
 * Otherwise the module size is computed so that the symbol fills the element, or, when the
 * set size doesn't fit, the image uses {@link ScaleImageEnum#RETAIN_SHAPE} and shrinks it.
 * A symbol is never drawn larger than its set size.
 * </p><p>
 * Linear codes have no bar height setting: the bars take the element height less the text
 * and the vertical quiet zones, in whole modules.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
final class OkapiLayout
{

	private static final double EPSILON = 1e-6;

	private final OkapiSymbolSpec spec;
	private final Symbol symbol;
	private final ScaleImageEnum scaleImage;

	private OkapiLayout(OkapiSymbolSpec spec, Symbol symbol, ScaleImageEnum scaleImage)
	{
		this.spec = spec;
		this.symbol = symbol;
		this.scaleImage = scaleImage;
	}

	public OkapiSymbolSpec getSpec()
	{
		return spec;
	}

	/**
	 * The symbol encoded with the final sizes.
	 */
	public Symbol getSymbol()
	{
		return symbol;
	}

	public ScaleImageEnum getScaleImage()
	{
		return scaleImage;
	}

	/**
	 * Computes the module size, quiet zones and bar height of the spec.
	 *
	 * @param width the width available to the upright symbol, in points (the element height when rotated by 90 or 270 degrees)
	 * @param height the height available to the upright symbol, in points
	 */
	static OkapiLayout layout(JasperReportsContext jasperReportsContext, OkapiSymbolSpec spec,
			double width, double height, double unitsToPoints)
	{
		OkapiComponent component = spec.getComponent();
		OkapiElementType<OkapiComponent> type = OkapiElementTypes.getType(component);

		Double setModule = component instanceof OkapiSizedComponent
				? toPoints(((OkapiSizedComponent) component).getModuleWidth(), unitsToPoints) : null;
		Double setQuietH = toPoints(component.getQuietZoneHorizontal(), unitsToPoints);
		Double setQuietV = toPoints(component.getQuietZoneVertical(), unitsToPoints);
		int defaultQuietH = type.getDefaultQuietZoneHorizontal(component);
		int defaultQuietV = type.getDefaultQuietZoneVertical(component);
		boolean linear = type.getLayout() == OkapiElementType.Layout.LINEAR;

		// the symbol size in modules without quiet zones
		spec.quietZoneHorizontal = 0;
		spec.quietZoneVertical = 0;
		spec.modulePt = 1;
		spec.barHeight = 1;
		Symbol bare = spec.createSymbol(jasperReportsContext);
		int modulesH = bare.getWidth();
		int modulesV = bare.getHeight();

		double module;
		boolean clip;
		if (setModule != null
				&& (modulesH + 2 * quietModules(setQuietH, defaultQuietH, setModule)) * setModule <= width + EPSILON
				&& (linear || (modulesV + 2 * quietModules(setQuietV, defaultQuietV, setModule)) * setModule <= height + EPSILON))
		{
			module = setModule;
			clip = true;
		}
		else if (setModule != null && !linear)
		{
			// a 2D code keeps the set size and the image shrinks it
			module = setModule;
			clip = false;
		}
		else
		{
			// fill the element: linear codes across their width, 2D codes in both directions
			module = fitModule(width, modulesH, setQuietH, defaultQuietH);
			if (!linear)
			{
				module = Math.min(module, fitModule(height, modulesV, setQuietV, defaultQuietV));
			}
			clip = false;
		}

		spec.modulePt = module;
		spec.quietZoneHorizontal = quietModules(setQuietH, defaultQuietH, module);
		spec.quietZoneVertical = quietModules(setQuietV, defaultQuietV, module);

		Symbol symbol;
		if (linear)
		{
			// bars take what the text and the vertical quiet zones leave, in whole modules
			int available = (int) Math.floor(height / module + EPSILON);
			spec.barHeight = Math.max(1, available);
			symbol = spec.createSymbol(jasperReportsContext);
			int other = symbol.getHeight() - symbol.getBarHeight();
			int barHeight = Math.max(1, available - other);
			if (barHeight != spec.barHeight)
			{
				spec.barHeight = barHeight;
				symbol = spec.createSymbol(jasperReportsContext);
			}
		}
		else
		{
			symbol = spec.createSymbol(jasperReportsContext);
		}

		// the bar height floor of one module or rounded quiet zones can still overflow the element
		if (clip && (symbol.getWidth() * module > width + EPSILON || symbol.getHeight() * module > height + EPSILON))
		{
			clip = false;
		}

		return new OkapiLayout(spec, symbol, clip ? ScaleImageEnum.CLIP : ScaleImageEnum.RETAIN_SHAPE);
	}

	private static Double toPoints(Double units, double unitsToPoints)
	{
		return units == null ? null : units * unitsToPoints;
	}

	/**
	 * A quiet zone set in points, in whole modules, rounded up so that it is never narrower than set.
	 */
	private static int quietModules(Double setPoints, int defaultModules, double module)
	{
		return setPoints == null ? defaultModules : (int) Math.ceil(setPoints / module - EPSILON);
	}

	/**
	 * The module size that makes a symbol of the given modules and quiet zones exactly the available length.
	 */
	private static double fitModule(double available, int modules, Double setQuietPoints, int defaultQuietModules)
	{
		if (setQuietPoints == null)
		{
			return available / (modules + 2 * defaultQuietModules);
		}
		double module = (available - 2 * setQuietPoints) / modules;
		// quiet zones as wide as the element leave no room: size the symbol alone,
		// the quiet zones then overflow and the image shrinks it all (RETAIN_SHAPE)
		return module > 0 ? module : available / modules;
	}
}
