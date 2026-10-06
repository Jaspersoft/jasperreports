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
package net.sf.jasperreports.engine.query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OracleProcedureCallHandlerFactory implements ProcedureCallHandlerFactory
{
	private static final String ORACLE_PROCEDURE_CALL_HANDLER_CLASS = "com.jaspersoft.jasperreports.plsql.OracleProcedureCallHandler";
	
	@Override
	public ProcedureCallHandler createProcedureCallHandler() throws JRException
	{
		ProcedureCallHandler procedureCallHandler = null;
		
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends ProcedureCallHandler> procedureCallHandlerClass = 
				(Class<? extends ProcedureCallHandler>) JRClassLoader.loadClassForName(ORACLE_PROCEDURE_CALL_HANDLER_CLASS);
			Constructor<? extends ProcedureCallHandler> constructor = procedureCallHandlerClass.getConstructor();
			procedureCallHandler = constructor.newInstance();
		}
		catch (InvocationTargetException  e)
		{
			throw new JRException(e.getTargetException());
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException e)
		{
			throw new JRException(e);
		}
		catch (ClassNotFoundException e)
		{
			// silently ignore missing Oracle implementation class
		}
		
		return procedureCallHandler;
	}

}
