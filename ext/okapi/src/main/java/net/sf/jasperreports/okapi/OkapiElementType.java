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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRVerifier;
import uk.org.okapibarcode.backend.Symbol;


/**
 * What the engine needs to know about one Okapi component class: its expressions,
 * how its settings map onto an Okapi symbol, and its defaults.
 * <p>
 * This replaces a public visitor: each component class has one instance, registered in
 * {@link OkapiElementTypes}, so that adding a barcode type adds no method to a shared interface.
 * All the Okapi API use of a type is here.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
abstract class OkapiElementType<C extends OkapiComponent>
{

	/**
	 * How the symbol is sized against the element, see {@link OkapiLayout}.
	 */
	enum Layout
	{
		/** width depends on the data; bars take the element height */
		LINEAR,
		/** width and height depend on the data, in whole modules */
		MATRIX
	}

	/**
	 * An expression of the component, with the type its value is converted to.
	 */
	static final class ExpressionProperty<C>
	{
		private final String name;
		private final Function<C, JRExpression> getter;
		private final BiConsumer<C, JRExpression> setter;
		private final Class<?> valueClass;

		ExpressionProperty(String name, Function<C, JRExpression> getter, BiConsumer<C, JRExpression> setter, Class<?> valueClass)
		{
			this.name = name;
			this.getter = getter;
			this.setter = setter;
			this.valueClass = valueClass;
		}

		public String getName()
		{
			return name;
		}

		public JRExpression get(C component)
		{
			return getter.apply(component);
		}

		public void set(C component, JRExpression expression)
		{
			setter.accept(component, expression);
		}

		/**
		 * The class of the converted value: {@link String} or {@link Integer}.
		 */
		public Class<?> getValueClass()
		{
			return valueClass;
		}
	}

	private final Class<C> componentClass;
	private final Layout layout;
	private final List<ExpressionProperty<C>> expressions = new ArrayList<>();

	protected OkapiElementType(Class<C> componentClass, Layout layout)
	{
		this.componentClass = componentClass;
		this.layout = layout;
		addExpression(OkapiComponent.PROPERTY_CODE_EXPRESSION,
				OkapiComponent::getCodeExpression, OkapiComponent::setCodeExpression, String.class);
	}

	protected final void addExpression(String name, Function<C, JRExpression> getter, BiConsumer<C, JRExpression> setter, Class<?> valueClass)
	{
		expressions.add(new ExpressionProperty<>(name, getter, setter, valueClass));
	}

	public Class<C> getComponentClass()
	{
		return componentClass;
	}

	public Layout getLayout()
	{
		return layout;
	}

	/**
	 * The expressions of the component, the code expression first; values are evaluated in this order.
	 */
	public List<ExpressionProperty<C>> getExpressions()
	{
		return Collections.unmodifiableList(expressions);
	}

	/**
	 * Whether the component has the human-readable text settings.
	 */
	public abstract boolean hasText();

	/**
	 * Creates the Okapi symbol with the component's own static settings applied.
	 * Okapi validates settings in its setters, so this throws on invalid values;
	 * the compile-time verifier relies on it.
	 */
	public abstract Symbol createSymbol(C component);

	/**
	 * Applies the evaluated values of the expressions after the code expression,
	 * in the order of {@link #getExpressions()}; the code itself is set by the caller.
	 *
	 * @param values the evaluated values, the code at index 0
	 */
	public void applyValues(C component, Symbol symbol, Object[] values)
	{
		// most types have no expressions besides the code
	}

	/**
	 * The horizontal quiet zone, in modules, when the component sets none.
	 */
	public abstract int getDefaultQuietZoneHorizontal(C component);

	/**
	 * The vertical quiet zone, in modules, when the component sets none.
	 */
	public int getDefaultQuietZoneVertical(C component)
	{
		return 0;
	}

	/**
	 * The set height of the rows that take the bar height, in report units, or null when the
	 * component sets none and the rows share the element height.
	 */
	public Double getBarHeight(C component)
	{
		// most types have no row height setting
		return null;
	}

	/**
	 * The lowest height, in modules, of the rows that take the bar height: the layout never draws them lower,
	 * and the image shrinks a symbol that then overflows the element.
	 *
	 * @param bare the symbol encoded with a bar height of 1 and no quiet zones
	 */
	public int getMinBarHeight(C component, Symbol bare)
	{
		// most types need no more than one module
		return 1;
	}

	/**
	 * The code drawn in the design preview when the code expression is not a literal.
	 */
	public abstract String getDefaultPreviewCode(C component);

	/**
	 * The value drawn in the design preview for an expression after the code when it is not a literal,
	 * or null to leave it out.
	 *
	 * @param expressionName the name of the expression, as in {@link #getExpressions()}
	 */
	public Object getDefaultPreviewValue(C component, String expressionName)
	{
		// most types draw without their other expressions
		return null;
	}

	/**
	 * Checks the settings of the type that Okapi doesn't validate in its setters, at compile time.
	 */
	public void verify(C component, JRVerifier verifier)
	{
		// most types have none
	}

	/**
	 * Applies settings in report units that need the module size: called by {@link OkapiLayout}
	 * after the module size is known.
	 *
	 * @param modulePt the size of one module in points
	 * @param unitsToPoints the factor from report units to points
	 */
	public void applySizes(C component, Symbol symbol, double modulePt, double unitsToPoints)
	{
		// most types have no other sizes
	}
}
