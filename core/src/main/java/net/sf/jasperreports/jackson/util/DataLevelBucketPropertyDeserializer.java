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
package net.sf.jasperreports.jackson.util;

import net.sf.jasperreports.components.items.ItemProperty;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.analytics.dataset.DesignDataLevelBucketProperty;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DataLevelBucketPropertyDeserializer extends ItemPropertyDeserializer
{
	private static final long serialVersionUID = 1L;

	public DataLevelBucketPropertyDeserializer()
	{
		this(null);
	}
	
	public DataLevelBucketPropertyDeserializer(Class<?> vc)
	{
		super(vc);
	}
	
	@Override
	protected ItemProperty getItemProperty()
	{
		return new DesignDataLevelBucketProperty();
	}
	
	@Override
	protected void setName(ItemProperty itemProperty, String name)
	{
		((DesignDataLevelBucketProperty)itemProperty).setName(name);
	}
	
	@Override
	protected void setValue(ItemProperty itemProperty, String value)
	{
		((DesignDataLevelBucketProperty)itemProperty).setValue(value);
	}
	
	@Override
	protected void setValueExpression(ItemProperty itemProperty, JRExpression expression)
	{
		((DesignDataLevelBucketProperty)itemProperty).setValueExpression(expression);
	}
}
