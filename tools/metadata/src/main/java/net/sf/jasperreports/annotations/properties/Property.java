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
package net.sf.jasperreports.annotations.properties;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.jasperreports.metadata.properties.PropertyMetadataConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@Retention(RetentionPolicy.SOURCE)
@Target(FIELD)
public @interface Property
{
	
	String name() default "";
	
	String category() default PropertyMetadataConstants.CATEGORY_OTHER;
	
	String defaultValue() default "N/A";//TODO lucianc
	
	PropertyScope[] scopes();
	
	String sinceVersion() default "";//TODO lucianc
	
	Class<?> valueType() default String.class;
	
	String[] scopeQualifications() default {};
	
}
