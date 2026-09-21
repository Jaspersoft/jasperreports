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
package net.sf.jasperreports.engine.util;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRGraphEnvInitializer
{
	private static final String EXCEPTION_MESSAGE_KEY_INITIALIZATION_ERROR = "util.graphic.environment.initialization.error";

	/**
	 *
	 */
	private static Set<String> AVAILABLE_FONT_FACE_NAMES; //NOPMD

	/**
	 *
	 */
	public static synchronized void initializeGraphEnv()
	{
		if (AVAILABLE_FONT_FACE_NAMES == null)
		{
			AVAILABLE_FONT_FACE_NAMES = new HashSet<>();

			try
			{
				AVAILABLE_FONT_FACE_NAMES.addAll(
					Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
					);
			}
			catch(Exception e)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INITIALIZATION_ERROR,
						(Object[])null,
						e);
			}
		}
	}

	/**
	 *
	 */
	public static boolean isAwtFontAvailable(String font)
	{
		initializeGraphEnv();
		
		return AVAILABLE_FONT_FACE_NAMES.contains(font);//FIXMEFONT not sure if we should check families or fonts
	}

	
	private JRGraphEnvInitializer()
	{
	}
}
