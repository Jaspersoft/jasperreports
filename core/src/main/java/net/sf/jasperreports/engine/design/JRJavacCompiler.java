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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRJavacCompiler extends JRAbstractMultiClassCompiler
{

	/**
	 *
	 */
	public JRJavacCompiler(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}
	
	@Override
	public String compileClasses(File[] sourceFiles, String classpath) throws JRException 
	{
		String[] source = new String[sourceFiles.length + 3];
		source[0] = "javac";
		source[1] = "-classpath";
		source[2] = classpath;
		for (int i = 0; i < sourceFiles.length; i++)
		{
			source[i + 3] = sourceFiles[i].getPath();
		}
		
		try 
		{
			// Compile the source file and arrange to read the errors if any.
			Process compile = Runtime.getRuntime().exec(source);
			InputStream errFile = compile.getErrorStream();
			
			// Read the error messages (if any) into the ByteArrayOutputStream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = 0;
			do 
			{
				count = errFile.read(buffer);
				if (count > 0)
				{
					baos.write(buffer, 0, count);
				}
			} while (count >= 0);
			
			if ( baos.toString().indexOf("error") != -1 ) 
			{
				return baos.toString();
			}
			
			return null;
		}
		catch (Exception e) 
		{
			StringBuilder files = new StringBuilder();
			for (File sourceFile : sourceFiles)
			{
				files.append(sourceFile.getPath());
				files.append(' ');
			}
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_JAVA_SOURCE_COMPILE_ERROR,
					new Object[]{files}, 
					e);
		}
	}


}
