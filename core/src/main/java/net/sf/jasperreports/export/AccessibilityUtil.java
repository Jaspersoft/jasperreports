/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.export;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.export.type.AccessibilityTagEnum;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class AccessibilityUtil
{
	/**
	 * Property that specifies the type of accessible content introduced by the element.
	 * Possible values are described by the {@link AccessibilityTagEnum}.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_19_0,
			valueType = String.class
			)
	public static final String PROPERTY_ACCESSIBILITY_TAG = JRPropertiesUtil.PROPERTY_PREFIX + "export.accessibility.tag";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_7_0_6
			)
	public static final String PROPERTY_TAG_ATTRIBUTE_ACTUAL_TEXT = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.tag.attribute.ActualText";

	/**
	 *
	 */
	private AccessibilityUtil()
	{
	}
}
