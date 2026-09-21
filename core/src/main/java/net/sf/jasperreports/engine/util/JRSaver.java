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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * Utility class that can be used when serializable objects must be saved on disk 
 * or sent over the network through an output stream.
 * <p>
 * Both the {@link net.sf.jasperreports.engine.design.JasperDesign} and
 * {@link net.sf.jasperreports.engine.JasperReport} classes implement the
 * <code>java.io.Serializable</code> interface. This allows users to store their report templates as
 * serialized objects either in their fully modifiable state 
 * ({@link net.sf.jasperreports.engine.design.JasperDesign} objects) or in their
 * compiled form ({@link net.sf.jasperreports.engine.JasperReport} objects), using 
 * various methods exposed by this class. 
 * </p>
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRSaver
{
	private static final String EXCEPTION_MESSAGE_KEY_EXPRESSIONS_CLASS_FILE_SAVE_ERROR = "util.saver.expressions.class.file.save.error";
	private static final String EXCEPTION_MESSAGE_KEY_FILE_SAVE_ERROR = "util.saver.file.save.error";
	private static final String EXCEPTION_MESSAGE_KEY_OUTPUT_STREAM_SAVE_ERROR = "util.saver.output.stream.save.error";

	/**
	 *
	 */
	public static void saveObject(
		Object obj, 
		String fileName
		) throws JRException
	{
		saveObject( obj, new File(fileName) );
	}


	/**
	 *
	 */
	public static void saveObject(
		Object obj, 
		File file
		) throws JRException
	{
		try (
			ObjectOutputStream oos = 
				new ObjectOutputStream(
					new BufferedOutputStream(
						new FileOutputStream(file)
						)
					)
			)
		{
			oos.writeObject(obj);
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_FILE_SAVE_ERROR,
					new Object[]{file},
					e);
		}
	}


	/**
	 *
	 */
	public static void saveObject(
		Object obj, 
		OutputStream os
		) throws JRException
	{
		ObjectOutputStream oos = null;

		try
		{
			oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
			oos.flush();
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_OUTPUT_STREAM_SAVE_ERROR,
					null,
					e);
		}
	}
		

	/**
	 *
	 */
	public static void saveClassSource(
		String source, 
		File file
		) throws JRException
	{
		try (
			BufferedWriter writer = 
				new BufferedWriter(
					new FileWriter(file)
					)
			)
		{
			writer.write(source);
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_EXPRESSIONS_CLASS_FILE_SAVE_ERROR,
					new Object[]{file},
					e);
		}
	}


	/**
	 * 
	 */
	public static void saveResource(String resource, File file)
	{
		try (FileOutputStream fos = new FileOutputStream(file))
		{
			fos.write(JRLoader.loadBytesFromResource(resource));
		}
		catch (JRException | IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	
	private JRSaver()
	{
	}
}
