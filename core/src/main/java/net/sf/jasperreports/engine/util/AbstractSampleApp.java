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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractSampleApp
{
	/**
	 *
	 */
	protected String usage()
	{
		StringBuilder sb = new StringBuilder();
		
		String appName = this.getClass().getName(); 
		
		sb.append(appName + " usage:" + "\n\tjava " + appName + " task" + "\n\tTasks : ");
		
		TreeSet<String> tasks = new TreeSet<>();
		Method[] methods = getClass().getMethods();
		for (Method method:methods)
		{
			if (
				method.getDeclaringClass().getName().endsWith("App")
				&& ((method.getModifiers() & Modifier.STATIC) == 0)
				&& ((method.getModifiers() & Modifier.PUBLIC) == 1)
				)
			{
				tasks.add(method.getName());
			}
		}
		for (String task:tasks)
		{
			sb.append(task).append(" | ");
		}
		
		return sb.toString().substring(0, sb.length() - 3);
	}

	
	/**
	 * 
	 * @throws Throwable 
	 */
	protected void executeTask(String taskName) throws Throwable
	{
		try
		{
			Method method = getClass().getMethod(taskName, new Class[]{});
			method.invoke(this, new Object[]{});
		}
		catch (NoSuchMethodException e)
		{
			System.out.println(usage());
		}
		catch (IllegalAccessException e)
		{
			throw e;
		}
		catch (InvocationTargetException e)
		{
			throw e.getCause();
		}
	}
	

	/**
	 *
	 */
	protected File[] getFiles(File parentFile, String extension)
	{
		List<File> fileList = new ArrayList<>();
		String[] fileNames = parentFile.list();
		if (fileNames != null)
		{
			for (String fileName : fileNames)
			{
				File file = new File(parentFile, fileName);
				if (file.isDirectory())
				{
					fileList.addAll(List.of(getFiles(file, extension)));
				}
				else
				{
					if (fileName.endsWith("." + extension))
					{
						fileList.add(file); 
					}
				}
			}
		}
		return fileList.toArray(new File[fileList.size()]);
	}
	
	
	/**
	 *
	 */
	protected Connection getDemoHsqldbConnection() throws JRException
	{
		Connection conn;

		try
		{
			//Change these settings according to your local configuration
			String driver = "org.hsqldb.jdbcDriver";
			String hsqldbServerPort = System.getProperty("hsqldb.server.port");
			String connectString = "jdbc:hsqldb:hsql://localhost" + (hsqldbServerPort != null ? (":" + hsqldbServerPort) : "");
			String user = "sa";
			String password = "";

			Class.forName(driver);
			conn = DriverManager.getConnection(connectString, user, password);
		}
		catch (ClassNotFoundException | SQLException e)
		{
			throw new JRException(e);
		}

		return conn;
	}

	
	/**
	 *
	 */
	public static void main(AbstractSampleApp app, String[] args)
	{
		try
		{
			if (args.length > 0)
			{
				for (String arg : args)
				{
					app.executeTask(arg);
				}
			}
			else
			{
				System.out.println(app.usage());
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}


	/**
	 *
	 */
	public abstract void test() throws JRException;


	/**
	 *
	 */
	public void writeApi() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jasper");
		if (files.length > 0)
		{
			File destFileParent = new File("target/reports");
			if (!destFileParent.exists())
			{
				destFileParent.mkdirs();
			}

			System.out.println("Writing API for " + files.length + " report design files.");

			for (int i = 0; i < files.length; i++)
			{
				File srcFile = files[i];
				String srcFileName = srcFile.getName();
				String destFileName = srcFileName.substring(0, srcFileName.lastIndexOf(".jasper")) + ".java";

				System.out.print("Writing API for: " + srcFileName + " ... ");

				JRReport report = (JRReport)JRLoader.loadObjectFromFile(srcFile.getAbsolutePath());

				new JRApiWriter(DefaultJasperReportsContext.getInstance()).write(
					report, 
					new File(destFileParent, destFileName).getAbsolutePath()
					);
				
				System.out.println("OK.");
			}
		}
		else
		{
			System.out.println("No report design files found to write API for.");
		}
	}


	/**
	 *
	 */
	public void writeApiXml() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jasper");
		if (files.length > 0)
		{
			File destFileParent = new File("target/reports");
			if (!destFileParent.exists())
			{
				destFileParent.mkdirs();
			}

			System.out.println("Running " + files.length + " API report design files.");

			for (int i = 0; i < files.length; i++)
			{
				File srcFile = files[i];
				String srcFileName = srcFile.getName();
				String srcClassName = srcFileName.substring(0, srcFileName.lastIndexOf(".jasper"));
				String destFileName = srcFileName.substring(0, srcFileName.lastIndexOf(".jasper")) + ".api.jrxml";

				System.out.print("Running: " + srcFileName + " ... ");

				try
				{
					Class<?> reportCreatorClass = JRClassLoader.loadClassForName(srcClassName);
					ReportCreator reportCreator = (ReportCreator)reportCreatorClass.getDeclaredConstructor().newInstance();
					JasperDesign jasperDesign = reportCreator.create();
					new JRXmlWriter(DefaultJasperReportsContext.getInstance()).write(
						jasperDesign, 
						new File(destFileParent, destFileName).getAbsolutePath(), 
						"UTF-8"
						);

					System.out.println("OK.");
				}
				catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e)
				{
					throw new JRException(e);
				}
			}
		}
		else
		{
			System.out.println("No API report design files found to run.");
		}
	}
}
