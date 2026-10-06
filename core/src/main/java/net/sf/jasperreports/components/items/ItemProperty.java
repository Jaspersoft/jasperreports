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
package net.sf.jasperreports.components.items;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.jackson.util.ItemPropertyDeserializer;
import net.sf.jasperreports.jackson.util.ItemPropertySerializer;

/**
 * The ItemProperty interface. An item property has a name (required) and a 
 * value provided either in a static way, using the <code>value</code> attribute, 
 * or dynamically, using a value expression.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonSerialize(using = ItemPropertySerializer.class)
@JsonDeserialize(using = ItemPropertyDeserializer.class)
public interface ItemProperty extends JRCloneable
{

	/**
	 * Returns the name of the item property (required).
	 * @return the property name
	 */
	String getName();
	
	/**
	 * Returns the <code>value</code> attribute of the item property. Only 
	 * <code>java.lang.String</code> values are allowed for this attribute.
	 * @return the <code>value</code> attribute
	 */
	String getValue();
	
	/**
	 * Returns a {@link net.sf.jasperreports.engine.JRExpression JRExpression} representing 
	 * the value object for the item property. If present, it overrides the value given by 
	 * the <code>value</code> attribute.
	 * 
	 * @return the value expression
	 */
	JRExpression getValueExpression();
	
}
