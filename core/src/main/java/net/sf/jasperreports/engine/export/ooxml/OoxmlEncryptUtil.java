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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.export.zip.AbstractZip;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class OoxmlEncryptUtil
{
	private static final String EXCEPTION_MESSAGE_KEY_MISSING_EXTENSION_EXCEL_POI = "extensions.missing.extension.excelpoi";

	/**
	 *
	 */
	public static void zipEntries(AbstractZip zip, OutputStream os, String password) throws IOException
	{
		try
		{
			Class clazz  = Class.forName("net.sf.jasperreports.poi.export.PoiEncryptUtil");
			Method method = clazz.getMethod("zipEntries", AbstractZip.class, OutputStream.class, String.class);
			method.invoke(method, zip, os, password);
		}
		catch (ClassNotFoundException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_MISSING_EXTENSION_EXCEL_POI,  
					(Object[])null 
					);
		}
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
		{
			throw new JRRuntimeException(e);
		}
	}
}
