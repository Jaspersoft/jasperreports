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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.component.ComponentDesignConverter;
import net.sf.jasperreports.engine.convert.ReportConverter;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRExpressionUtil;
import net.sf.jasperreports.okapi.OkapiElementType.ExpressionProperty;


/**
 * Draws the design preview of an Okapi barcode element, from the literal expression
 * values or else a default code.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
class OkapiDesignConverter implements ComponentDesignConverter
{

	private static final Log log = LogFactory.getLog(OkapiDesignConverter.class);

	@Override
	public JRPrintElement convert(ReportConverter reportConverter,
			JRComponentElement element)
	{
		OkapiComponent component = (OkapiComponent) element.getComponent();
		JRBasePrintImage printImage = new JRBasePrintImage(
				reportConverter.getDefaultStyleProvider());
		reportConverter.copyBaseAttributes(element, printImage);
		printImage.setRotation(OkapiImageFactory.getRotation(element));
		printImage.setHorizontalImageAlign(component.getHorizontalImageAlign());
		printImage.setVerticalImageAlign(component.getVerticalImageAlign());
		printImage.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);

		try
		{
			OkapiLayout layout = OkapiImageFactory.createImage(
					reportConverter.getJasperReportsContext(), element,
					getPreviewValues(OkapiElementTypes.getType(component), component),
					reportConverter.getReport().getDpi(), reportConverter.getLocale());
			printImage.setScaleImage(layout.getScaleImage());
			printImage.setRenderer(new OkapiRenderer(layout.getSpec(), layout.getSymbol()));
		}
		catch (Exception e)
		{
			if (log.isWarnEnabled())
			{
				log.warn("Failed to create barcode preview", e);
			}
		}

		return printImage;
	}

	private <C extends OkapiComponent> Object[] getPreviewValues(OkapiElementType<C> type, OkapiComponent component)
	{
		@SuppressWarnings("unchecked")
		C typedComponent = (C) component;
		List<ExpressionProperty<C>> expressions = type.getExpressions();
		Object[] values = new Object[expressions.size()];
		for (int i = 0; i < values.length; i++)
		{
			ExpressionProperty<C> expression = expressions.get(i);
			String text = JRExpressionUtil.getSimpleExpressionText(expression.get(typedComponent));
			try
			{
				values[i] = OkapiFillComponent.toValue(text, expression.getValueClass());
			}
			catch (NumberFormatException e)
			{
				// not a literal number: left out of the preview
				values[i] = null;
			}
			if (values[i] == null && i > 0)
			{
				values[i] = type.getDefaultPreviewValue(typedComponent, expression.getName());
			}
		}
		if (values[0] == null)
		{
			values[0] = type.getDefaultPreviewCode(typedComponent);
		}
		return values;
	}
}
