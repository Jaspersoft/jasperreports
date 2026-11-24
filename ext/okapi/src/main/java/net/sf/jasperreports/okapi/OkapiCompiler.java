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

import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentCompiler;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.okapi.OkapiElementType.ExpressionProperty;


/**
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
class OkapiCompiler implements ComponentCompiler
{

	@Override
	public void collectExpressions(Component component, JRExpressionCollector collector)
	{
		collectExpressions(OkapiElementTypes.getType((OkapiComponent) component), (OkapiComponent) component, collector);
	}

	private <C extends OkapiComponent> void collectExpressions(OkapiElementType<C> type, OkapiComponent component,
			JRExpressionCollector collector)
	{
		@SuppressWarnings("unchecked")
		C typedComponent = (C) component;
		for (ExpressionProperty<C> expression : type.getExpressions())
		{
			collector.addExpression(expression.get(typedComponent));
		}
	}

	@Override
	public Component toCompiledComponent(Component component,
			JRBaseObjectFactory baseFactory)
	{
		return toCompiledComponent(OkapiElementTypes.getType((OkapiComponent) component), (OkapiComponent) component, baseFactory);
	}

	private <C extends OkapiComponent> C toCompiledComponent(OkapiElementType<C> type, OkapiComponent component,
			JRBaseObjectFactory baseFactory)
	{
		@SuppressWarnings("unchecked")
		C typedComponent = (C) component;
		@SuppressWarnings("unchecked")
		C compiled = (C) typedComponent.clone();
		for (ExpressionProperty<C> expression : type.getExpressions())
		{
			expression.set(compiled, baseFactory.getExpression(expression.get(typedComponent)));
		}
		return compiled;
	}

	@Override
	public void verify(Component component, JRVerifier verifier)
	{
		OkapiComponent barcode = (OkapiComponent) component;

		if (barcode.getCodeExpression() == null)
		{
			verifier.addBrokenRule("Barcode expression is null", barcode);
		}

		EvaluationTimeEnum evaluationTime = barcode.getEvaluationTime();
		if (evaluationTime == EvaluationTimeEnum.AUTO)
		{
			verifier.addBrokenRule("Auto evaluation time is not supported for barcodes", barcode);
		}
		else if (evaluationTime == EvaluationTimeEnum.GROUP)
		{
			String evaluationGroup = barcode.getEvaluationGroup();
			if (evaluationGroup == null || evaluationGroup.length() == 0)
			{
				verifier.addBrokenRule("No evaluation group set for barcode", barcode);
			}
			else if (!verifier.getReportDesign().getGroupsMap().containsKey(evaluationGroup))
			{
				verifier.addBrokenRule("Barcode evaluation group \""
						+ evaluationGroup + "\" not found", barcode);
			}
		}

		verifyNotNegative(verifier, barcode, OkapiComponent.PROPERTY_QUIET_ZONE_HORIZONTAL, barcode.getQuietZoneHorizontal());
		verifyNotNegative(verifier, barcode, OkapiComponent.PROPERTY_QUIET_ZONE_VERTICAL, barcode.getQuietZoneVertical());
		if (barcode instanceof OkapiSizedComponent)
		{
			verifyPositive(verifier, barcode, OkapiSizedComponent.PROPERTY_MODULE_WIDTH,
					((OkapiSizedComponent) barcode).getModuleWidth());
		}
		OkapiElementType<OkapiComponent> type = OkapiElementTypes.getType(barcode);
		type.verify(barcode, verifier);

		// Okapi validates the static settings in its setters
		try
		{
			type.createSymbol(barcode);
		}
		catch (RuntimeException e)
		{
			verifier.addBrokenRule(e, barcode);
		}
	}

	static void verifyNotNegative(JRVerifier verifier, OkapiComponent barcode, String property, Double value)
	{
		if (value != null && !(value >= 0))
		{
			verifier.addBrokenRule("Barcode " + property + " must not be negative: " + value, barcode);
		}
	}

	static void verifyPositive(JRVerifier verifier, OkapiComponent barcode, String property, Double value)
	{
		if (value != null && !(value > 0))
		{
			verifier.addBrokenRule("Barcode " + property + " must be positive: " + value, barcode);
		}
	}
}
