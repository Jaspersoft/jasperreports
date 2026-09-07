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
package net.sf.jasperreports.engine.design;

import java.io.File;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * Base class that can be used by single source file compilers to implement multiple compilation.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRAbstractSingleClassCompiler extends JRAbstractClassCompiler
{
	/**
	 * 
	 */
	public JRAbstractSingleClassCompiler(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}

	@Override
	public String compileClasses(File[] sourceFiles, String classpath) throws JRException
	{
		if (sourceFiles.length == 1)
		{
			return compileClass(sourceFiles[0], classpath);
		}
		
		StringBuilder errors = new StringBuilder();
		for (File sourceFile : sourceFiles)
		{
			String classErrors = compileClass(sourceFile, classpath);
			if (classErrors != null)
			{
				errors.append(classErrors);
			}
		}
		
		return errors.length() > 0 ? errors.toString() : null;
	}
	
}
