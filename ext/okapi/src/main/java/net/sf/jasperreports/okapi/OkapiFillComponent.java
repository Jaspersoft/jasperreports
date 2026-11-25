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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.component.BaseFillComponent;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.fill.JRTemplateImage;
import net.sf.jasperreports.engine.fill.JRTemplatePrintImage;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.export.HtmlReportConfiguration;
import net.sf.jasperreports.okapi.OkapiElementType.ExpressionProperty;


/**
 * Fills an Okapi barcode element as an image.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
class OkapiFillComponent extends BaseFillComponent
{

	private final OkapiComponent component;

	// the scale mode depends on whether the symbol fits, so templates are kept per scale mode too
	private final Map<ScaleImageEnum, Map<JRStyle, JRTemplateImage>> printTemplates = new EnumMap<>(ScaleImageEnum.class);
	private OkapiLayout layout;

	OkapiFillComponent(OkapiComponent component)
	{
		this.component = component;
	}

	OkapiFillComponent(OkapiFillComponent fillComponent)
	{
		this.component = fillComponent.component;
	}

	@Override
	public void evaluate(byte evaluation) throws JRException
	{
		if (isEvaluateNow())
		{
			evaluateBarcode(evaluation);
		}
	}

	protected boolean isEvaluateNow()
	{
		return EvaluationTimeEnum.getValueOrDefault(component.getEvaluationTime()) == EvaluationTimeEnum.NOW;
	}

	protected void evaluateBarcode(byte evaluation) throws JRException
	{
		OkapiElementType<?> type = OkapiElementTypes.getType(component);
		Object[] values = evaluateValues(type, evaluation);
		layout = isMissingRequiredValue(type, values)
				? null
				: OkapiImageFactory.createImage(fillContext.getFiller().getJasperReportsContext(),
						fillContext.getComponentElement(), values,
						fillContext.getFiller().getDpi(), fillContext.getReportLocale());
	}

	private <C extends OkapiComponent> Object[] evaluateValues(OkapiElementType<C> type, byte evaluation) throws JRException
	{
		@SuppressWarnings("unchecked")
		C typedComponent = (C) component;
		List<ExpressionProperty<C>> expressions = type.getExpressions();
		Object[] values = new Object[expressions.size()];
		for (int i = 0; i < values.length; i++)
		{
			ExpressionProperty<C> expression = expressions.get(i);
			values[i] = toValue(fillContext.evaluate(expression.get(typedComponent), evaluation), expression.getValueClass());
		}
		return values;
	}

	/**
	 * Whether the value of a required expression, the code among them, is null: then no barcode is drawn.
	 */
	private static boolean isMissingRequiredValue(OkapiElementType<?> type, Object[] values)
	{
		List<? extends ExpressionProperty<?>> expressions = type.getExpressions();
		for (int i = 0; i < values.length; i++)
		{
			if (values[i] == null && expressions.get(i).isRequired())
			{
				return true;
			}
		}
		return false;
	}

	static Object toValue(Object value, Class<?> valueClass)
	{
		if (value == null)
		{
			return null;
		}
		if (valueClass == Integer.class)
		{
			return value instanceof Number ? ((Number) value).intValue() : Integer.valueOf(value.toString().trim());
		}
		return JRStringUtil.getString(value);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FillPrepareResult prepare(int availableHeight)
	{
		return prepare(availableHeight, true);
	}

	@Override
	public FillPrepareResult prepare(int availableHeight, boolean isOverflowAllowed)
	{
		if (isEvaluateNow() && layout == null)
		{
			return FillPrepareResult.NO_PRINT_NO_OVERFLOW;
		}

		return FillPrepareResult.PRINT_NO_STRETCH;
	}

	@Override
	public JRPrintElement fill()
	{
		// a delayed barcode gets its template again once its scale mode is known
		JRTemplateImage templateImage = getTemplateImage(
				isEvaluateNow() ? layout.getScaleImage() : ScaleImageEnum.RETAIN_SHAPE);

		JRTemplatePrintImage image = new JRTemplatePrintImage(templateImage, printElementOriginator);
		JRComponentElement element = fillContext.getComponentElement();
		image.setUUID(element.getUUID());
		image.setX(element.getX());
		image.setY(fillContext.getElementPrintY());
		image.setWidth(element.getWidth());
		image.setHeight(element.getHeight());
		HtmlReportConfiguration.forceEmbedImage(fillContext.getFiller().getPropertiesUtil(),
				fillContext.getComponentElement(), image);

		if (isEvaluateNow())
		{
			setBarcodeImage(image);
		}
		else
		{
			fillContext.registerDelayedEvaluation(image,
					component.getEvaluationTime(),
					component.getEvaluationGroup());
		}

		return image;
	}

	protected JRTemplateImage getTemplateImage(ScaleImageEnum scaleImage)
	{
		JRStyle elementStyle = fillContext.getElementStyle();
		Map<JRStyle, JRTemplateImage> templates = printTemplates.computeIfAbsent(scaleImage, key -> new HashMap<>());
		JRTemplateImage templateImage = templates.get(elementStyle);
		if (templateImage == null)
		{
			JRComponentElement element = fillContext.getComponentElement();
			templateImage = new JRTemplateImage(
					fillContext.getElementOrigin(),
					fillContext.getDefaultStyleProvider());
			templateImage.setElement(element);
			templateImage.setStyle(elementStyle);//already set by setElement, but keeping for safety
			templateImage.setScaleImage(scaleImage);
			// the barcode is drawn upright and the image is rotated and aligned,
			// by the component's own settings or else by those of the style
			templateImage.setRotation(OkapiImageFactory.getRotation(element));
			if (component.getHorizontalImageAlign() != null)
			{
				templateImage.setHorizontalImageAlign(component.getHorizontalImageAlign());
			}
			if (component.getVerticalImageAlign() != null)
			{
				templateImage.setVerticalImageAlign(component.getVerticalImageAlign());
			}
			templateImage.setUsingCache(false);
			templateImage = deduplicate(templateImage);
			templates.put(elementStyle, templateImage);
		}
		return templateImage;
	}

	protected void setBarcodeImage(JRTemplatePrintImage image)
	{
		if (layout != null)
		{
			image.setRenderer(new OkapiRenderer(layout.getSpec(), layout.getSymbol()));
		}
	}

	@Override
	public void evaluateDelayedElement(JRPrintElement element, byte evaluation)
			throws JRException
	{
		evaluateBarcode(evaluation);
		JRTemplatePrintImage image = (JRTemplatePrintImage) element;
		if (layout != null)
		{
			image.setTemplate(getTemplateImage(layout.getScaleImage()));
		}
		setBarcodeImage(image);
	}
}
