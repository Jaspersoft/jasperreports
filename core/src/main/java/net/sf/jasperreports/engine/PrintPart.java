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
package net.sf.jasperreports.engine;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface PrintPart extends JRPropertiesHolder
{
	
	@Property(
			name = "net.sf.jasperreports.part.print.transfer.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_20_2
			)
	public static final String PROPERTIES_TRANSFER_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "part.print.transfer.";
	
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_20_2
			)
	public static final String ELEMENT_PROPERTY_PART_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "print.part.name";
	
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,//TODO part?
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.PART},
			sinceVersion = PropertyConstants.VERSION_6_20_2
			)
	public static final String PROPERTY_VISIBLE = JRPropertiesUtil.PROPERTY_PREFIX + "print.part.visible";
	
	public String getName();
	
	public PrintPageFormat getPageFormat();
}
