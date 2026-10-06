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
package net.sf.jasperreports.functions;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRClassLoader;


/**
 * A functions bundle contains a list of classes which have static methods that can be used as functions in report expressions.
 * 
 * The registry may return one or more instances of this classes.
 * 
 * @author Giulio Toffoli (gt78@users.sourceforge.net)
 */
public class FunctionsBundle 
{

	private List<Class<?>> functionClasses = new ArrayList<>();

	
	/**
	 *
	 */
	public FunctionsBundle(List<String> functionClassNames) 
	{
		for (String className : functionClassNames)
		{
			addFunctionClass(className);
		}
	}

	
	/**
	 * 
	 */
	public FunctionsBundle() 
	{
		super();
	}


	/**
	 *
	 */
	public void addFunctionClass(Class<?> clazz) 
	{
		functionClasses.add(clazz);
	}


	/**
	 *
	 */
	public void addFunctionClass(String className) 
	{
		try 
		{
			Class<?> clazz = JRClassLoader.loadClassForName(className);
			addFunctionClass(clazz);
		}
		catch (ClassNotFoundException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	
	/**
	 *
	 */
	public List<Class<?>> getFunctionClasses() 
	{
		return functionClasses;
	}


	/**
	 *
	 */
	public void setFunctionClasses(List<Class<?>> functionClasses) 
	{
		this.functionClasses = functionClasses;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionsBundle && functionClasses!=null) {
			return functionClasses.equals(
					((FunctionsBundle) obj).getFunctionClasses());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		if (functionClasses != null) {
			return functionClasses.hashCode();
		}
		return super.hashCode();
	}	
	
}
