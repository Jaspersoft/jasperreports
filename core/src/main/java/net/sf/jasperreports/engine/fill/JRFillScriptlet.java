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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRScriptlet;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillScriptlet implements JRScriptlet
{


	/**
	 *
	 */
	protected JRScriptlet parent;

	/**
	 *
	 */
	private JRAbstractScriptlet scriptlet;


	/**
	 *
	 */
	protected JRFillScriptlet(
		JRScriptlet parent, 
		JRFillObjectFactory factory
		)
	{
		factory.put(parent, this);

		this.parent = parent;
	}


	@Override
	public String getName()
	{
		return parent.getName();
	}
		
	@Override
	public String getDescription()
	{
		return parent.getDescription();
	}
		
	@Override
	public void setDescription(String description)
	{
	}
	
	@Override
	public Class<?> getValueClass()
	{
		return parent.getValueClass();
	}
	
	@Override
	public String getValueClassName()
	{
		return parent.getValueClassName();
	}
	
	/**
	 *
	 */
	public JRAbstractScriptlet getScriptlet()
	{
		return scriptlet;
	}
		
	/**
	 *
	 */
	public void setScriptlet(JRAbstractScriptlet scriptlet)
	{
		this.scriptlet = scriptlet;
	}

	
	@Override
	public boolean hasProperties()
	{
		return parent.hasProperties();
	}


	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return parent.getPropertiesMap();
	}

	
	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}
		

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}


	@Override
	public JRPropertyExpression[] getPropertyExpressions()
	{
		return parent.getPropertyExpressions();
	}

}
