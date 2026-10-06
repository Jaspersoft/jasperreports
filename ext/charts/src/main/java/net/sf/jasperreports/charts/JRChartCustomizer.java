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
 * Michael McMahon - Michael.McMahon@activewire.net
 */

package net.sf.jasperreports.charts;

import org.jfree.chart.JFreeChart;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * This interface allows users to provide pluggable chart customizers. A class that implements this interface can
 * be defined and its name must be specified in the report template. At fill time, the corresponding <i>customize()</i>
 * method of this class will be called, if advanced chart customization is needed.
 * <p/>
 * If values of report parameters, variables or fields are needed in the customization, the customizer implementation
 * should extend {@link net.sf.jasperreports.charts.JRAbstractChartCustomizer JRAbstractChartCustomizer}
 * to gain access to such values.
 *
 * @author Michael McMahon (Michael.McMahon@activewire.net)
 */
public interface JRChartCustomizer 
{
	@Property(
			name = "net.sf.jasperreports.customizer.{arbitrary_name}.{property_name}",
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String CUSTOMIZER_PROPERTY_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "customizer.";
	
	@Property(
			name = "net.sf.jasperreports.customizer.class.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_3_1
			)
	public static final String CUSTOMIZER_CLASS_PROPERTY_PREFIX = CUSTOMIZER_PROPERTY_PREFIX + "class.";

	/**
	 * This method is called at fill time, before the chart is rendered.
 	 * @param chart the JFreeChart object, which can be accessed and modified
	 * @param jasperChart the JasperReports version of the chart, containing data that might be needed in
	 * customization
	 */
	public void customize(JFreeChart chart, JRChart jasperChart);

}

