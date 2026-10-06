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
package net.sf.jasperreports.engine.type;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DatasetPropertyExpression;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Defines specific moments in time when the property expression of a dataset is supposed to be evaluated.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum PropertyEvaluationTimeEnum implements NamedEnum
{
	/**
	 * Evaluate the property expression before parameter contributors.
	 */
	EARLY("Early"),
	/**
	 * Evaluate the property expression after parameter contributors.
	 */
	LATE("Late"),
	/**
	 * Evaluate the property expression at the end of the report.
	 */
	REPORT("Report");
	
	/**
	 * Provides a default value for the {@link DatasetPropertyExpression#getEvaluationTime()} setting of dataset property expressions and can be set either globally or at dataset level.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "Early",
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_3_1,
			valueType = PropertyEvaluationTimeEnum.class
			)
	public static final String PROPERTY_EVALUATION_TIME = JRPropertiesUtil.PROPERTY_PREFIX + "property.evaluation.time";

	private final String name;
	
	private PropertyEvaluationTimeEnum(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public static PropertyEvaluationTimeEnum byName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
