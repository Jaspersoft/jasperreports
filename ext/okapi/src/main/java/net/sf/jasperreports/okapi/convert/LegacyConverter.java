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
package net.sf.jasperreports.okapi.convert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRVisitorSupport;
import net.sf.jasperreports.engine.util.ReportUpdater;
import net.sf.jasperreports.okapi.OkapiComponent;


/**
 * Base of the report updaters that convert the elements of a legacy barcode extension
 * to <code>okapi:*</code> elements.
 * <p>
 * The element itself is kept, with its position, size, style, key and properties, and only its
 * component is replaced. An element whose conversion could encode different data is left as it is,
 * so that its data can be reviewed first; so is an element of a type that has no <code>okapi:*</code>
 * element yet. Every element left unconverted and every legacy setting dropped is logged as a warning.
 * <p>
 * A single instance converts several reports at once when run by the Maven plugin, so the state of
 * a conversion is kept in local objects only.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
abstract class LegacyConverter implements ReportUpdater
{

	private static final Log log = LogFactory.getLog(LegacyConverter.class);


	@Override
	public JasperDesign update(JasperDesign jasperDesign)
	{
		ReportConversion report = new ReportConversion(jasperDesign);

		JRElementsVisitor.visitReport(jasperDesign, new JRVisitorSupport()
		{
			@Override
			public void visitComponentElement(JRComponentElement componentElement)
			{
				Component component = componentElement.getComponent();
				// elements of a report loaded from JRXML are design elements
				if (componentElement instanceof JRDesignComponentElement && isLegacy(component))
				{
					convertElement(report, (JRDesignComponentElement) componentElement);
				}
			}
		});

		if (report.converted > 0 || report.unconverted > 0)
		{
			log.info("Report " + report.name + ": " + report.converted + " " + getExtensionName()
				+ " barcodes converted, " + report.unconverted + " left unconverted.");
		}

		return jasperDesign;
	}


	private void convertElement(ReportConversion report, JRDesignComponentElement element)
	{
		ElementConversion conversion = new ElementConversion(report, element);
		OkapiComponent converted = convert(element.getComponent(), conversion);
		if (converted == null)
		{
			report.unconverted++;
		}
		else
		{
			element.setComponent(converted);
			report.converted++;
		}
	}


	/**
	 * The name of the legacy extension, for the log.
	 */
	protected abstract String getExtensionName();

	/**
	 * Tells whether the component belongs to the legacy extension this updater converts.
	 */
	protected abstract boolean isLegacy(Component component);

	/**
	 * Converts a legacy component, or returns <code>null</code> after calling
	 * {@link ElementConversion#notConverted(String)} when the element is to be left as it is.
	 */
	protected abstract OkapiComponent convert(Component component, ElementConversion conversion);


	/**
	 * Copies the settings that every barcode component has.
	 */
	protected static <C extends OkapiComponent> C common(C component, JRExpression codeExpression,
			EvaluationTimeEnum evaluationTime, String evaluationGroup)
	{
		component.setCodeExpression(codeExpression);
		component.setEvaluationTime(evaluationTime);
		component.setEvaluationGroup(evaluationGroup);
		return component;
	}


	private static class ReportConversion
	{
		final JasperDesign design;
		final String name;
		int converted;
		int unconverted;

		ReportConversion(JasperDesign design)
		{
			this.design = design;
			this.name = design.getName();
		}
	}


	/**
	 * The conversion of one element, which reports what it leaves out.
	 */
	protected static class ElementConversion
	{
		private final ReportConversion report;
		private final JRDesignComponentElement element;

		private ElementConversion(ReportConversion report, JRDesignComponentElement element)
		{
			this.report = report;
			this.element = element;
		}

		public JRDesignComponentElement getElement()
		{
			return element;
		}

		public JasperDesign getReport()
		{
			return report.design;
		}

		/**
		 * Reports that the element is left unconverted, and why.
		 */
		public OkapiComponent notConverted(String reason)
		{
			log.warn(describe() + " not converted: " + reason);
			return null;
		}

		/**
		 * Reports a legacy setting that has no <code>okapi:*</code> equivalent and doesn't change the data.
		 */
		public void dropped(String setting, String reason)
		{
			log.warn(describe() + ": " + setting + " dropped, " + reason);
		}

		private String describe()
		{
			// named by key, or by UUID when it has none, as fill errors name okapi elements
			String id = element.getKey() != null ? "key " + element.getKey() : "UUID " + element.getUUID();
			return "Report " + report.name + ", element with " + id
				+ " (" + element.getComponent().getClass().getSimpleName() + ")";
		}
	}
}
