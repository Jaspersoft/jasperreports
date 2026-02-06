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
package net.sf.jasperreports.ant;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.RegexpPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRApiWriter;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.ReportCreator;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;


/**
 * Ant task for batch-generating the Java source file that uses the JR API to create the report design, 
 * from compiled report template files or from source JRXML files.
 * Works like the built-in <code>javac</code> Ant task.
 * <p>
 * This task can take the following arguments:
 * <ul>
 * <li>src
 * <li>destdir
 * </ul>
 * Of these arguments, the <code>src</code> and <code>destdir</code> are required.
 * When this task executes, it will recursively scan the <code>src</code> and 
 * <code>destdir</code> looking for compiled report template files or for source 
 * JRXML report template files and it will recreate the Java source file for each of them. 
 * This task makes its file creation decision based on timestamp and only input files 
 * that have no corresponding file in the target directory or where the destination Java file 
 * is older than the input file will be processed.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRAntApiWriteTask extends JRBaseAntTask
{


	/**
	 *
	 */
	private Path src;
	private File destdir;
	private Path classpath;
	private boolean runApi;

	private Map<String, String> reportFilesMap;


	/**
	 * Sets the source directories to find the source files.
	 * 
	 * @param srcdir source path
	 */
	public void setSrcdir(Path srcdir)
	{
		if (src == null) 
		{
			src = srcdir;
		}
		else
		{
			src.append(srcdir);
		}
	}


	/**
	 * Adds a path for source report templates.
	 * 
	 * @return source path
	 */
	public Path createSrc()
	{
		if (src == null)
		{
			src = new Path(getProject());
		}
		
		return src.createPath();
	}
	
	
	/**
	 * Sets the destination directory into which the Java report design files should be generated.
	 * 
	 * @param destdir destination directory
	 */
	public void setDestdir(File destdir)
	{
		this.destdir = destdir;
	}


	/**
	 * Adds a path to the classpath.
	 * 
	 * @return classpath to use when updating the report
	 */
	public Path createClasspath()
	{
		if (classpath == null)
		{
			classpath = new Path(getProject());
		}
		
		return classpath.createPath();
	}
	
	
	/**
	 * If set to true, the task will run the API writer generated classes and produce JRXML source files. 
	 */
	public void setRunApi(boolean runApi)
	{
		this.runApi = runApi;
	}

	
	/**
	 * Executes the task.
	 */
	@Override
	public void execute() throws BuildException
	{
		checkParameters();

		reportFilesMap = new HashMap<>();

		AntClassLoader classLoader = null;
		if (classpath != null)
		{
			jasperReportsContext.setProperty(JRCompiler.COMPILER_CLASSPATH, String.valueOf(classpath));
			
			ClassLoader parentClassLoader = getClass().getClassLoader();
			classLoader = new AntClassLoader(parentClassLoader, getProject(), classpath, true);
			classLoader.setThreadContextLoader();
		}

		try
		{
			/*   */
			scanSrc();
			
			if (runApi)
			{
				/*   */
				runApi();
			}
			else
			{
				/*   */
				writeApi();
			}
		}
		finally
		{
			if (classLoader != null)
			{
				classLoader.resetThreadContextLoader();
			}				
		}			
	}
	
	
	/**
	 * Checks that all required attributes have been set and that the supplied values are valid. 
	 */
	protected void checkParameters() throws BuildException 
	{
		if (src == null || src.size() == 0)
		{
			throw 
				new BuildException(
					"The srcdir attribute must be set.", 
					getLocation()
					);
		}
		
		if (destdir != null && !destdir.isDirectory()) 
		{
			throw 
				new BuildException(
					"The destination directory \"" 
						+ destdir 
						+ "\" does not exist "
						+ "or is not a directory.", 
					getLocation()
					);
		}
	}
	
	
	/**
	 * Scans the source directories looking for source report design files to be processed. 
	 */
	protected void scanSrc() throws BuildException
	{
		for(Iterator<Resource> it = src.iterator(); it.hasNext();)
		{
			Resource resource = it.next();
			FileResource fileResource = resource instanceof FileResource ? (FileResource)resource : null;
			if (fileResource != null)
			{
				File file = fileResource.getFile();
				if (file.isDirectory())
				{
					DirectoryScanner ds = getDirectoryScanner(file);
					String[] files = ds.getIncludedFiles();
					
					scanDir(file, destdir != null ? destdir : file, files);
				}
				else
				{
					String[] files = new String[]{fileResource.getName()};

					scanDir(fileResource.getBaseDir(), destdir != null ? destdir : fileResource.getBaseDir(), files);
				}
			}
//			else
//			{
//				//FIXME what to do?
//			}
		}
	}
	
	
	/**
	 * Scans the directory looking for source report design files to be processed. 
	 * The results are returned in the instance variable <code>reportFilesMap</code>.
	 * 
	 * @param srcdir source directory
	 * @param destdir destination directory
	 * @param files included file names
	 */
	protected void scanDir(File srcdir, File destdir, String[] files) 
	{
		RegexpPatternMapper mapper = new RegexpPatternMapper();
		if (runApi)
		{
			mapper.setFrom("^(.*)\\.(.*)$");
			mapper.setTo("\\1.api.jrxml");
		}
		else
		{
			mapper.setFrom("^(.*)\\.(.*)$");
			mapper.setTo("\\1.java");
		}

		SourceFileScanner scanner = new SourceFileScanner(this);
		String[] newFiles = scanner.restrict(files, srcdir, destdir, mapper);
		
		if (newFiles != null && newFiles.length > 0) 
		{
			for (int i = 0; i < newFiles.length; i++)
			{
				reportFilesMap.put(
					(new File(srcdir, newFiles[i])).getAbsolutePath(), 
					(new File(destdir, mapper.mapFileName(newFiles[i])[0])).getAbsolutePath()
					);
			}
		}
	}
	
	
	/**
	 * Performs the API code generation for the selected report design files.
	 */
	protected void writeApi() throws BuildException
	{
		Collection<String> files = reportFilesMap.keySet();

		if (files != null && files.size() > 0)
		{
			boolean isError = false;
		
			log("Processing " + files.size() + " report design files.");

			for (Iterator<String> it = files.iterator(); it.hasNext();)
			{
				String srcFileName = it.next();
				String destFileName = reportFilesMap.get(srcFileName);
				File destFileParent = new File(destFileName).getParentFile();
				if (!destFileParent.exists())
				{
					destFileParent.mkdirs();
				}

				String srcFileExtension = null;
				int srcFileExtensionStart = srcFileName.lastIndexOf('.');
				if (srcFileExtensionStart >= 0)
				{
					srcFileExtension = srcFileName.substring(srcFileExtensionStart);
				}
				
				try
				{
					JRReport report = null;

					if ("jrxml".equalsIgnoreCase(srcFileExtension))
					{
						report = JRXmlLoader.load(srcFileName);
					}
					else if ("jasper".equalsIgnoreCase(srcFileExtension))
					{
						report = (JRReport)JRLoader.loadObjectFromFile(srcFileName);
					}
					else
					{
						try
						{
							report = (JRReport)JRLoader.loadObjectFromFile(srcFileName);
						}
						catch (JRException e)
						{
							report = JRXmlLoader.load(srcFileName);
						}
					}
					
					new JRApiWriter(jasperReportsContext).write(report, destFileName);
					
					log("File : " + srcFileName);
				}
				catch (JRException e)
				{
					log("Error generating API report design : " + srcFileName);
					log(e, Project.MSG_ERR);
					isError = true;
				}
			}
		
			if (isError)
			{
				throw new BuildException("Errors were encountered when generating API report designs.");
			}
		}
	}
	
	
	/**
	 * Runs the generated code and produces the JRXML image of the report.
	 */
	protected void runApi() throws BuildException
	{
		Collection<String> files = reportFilesMap.keySet();

		if (files != null && files.size() > 0)
		{
			boolean isError = false;
		
			log("Running " + files.size() + " API report design files.");

			for (Iterator<String> it = files.iterator(); it.hasNext();)
			{
				String srcFileName = it.next();
				String destFileName = reportFilesMap.get(srcFileName);
				File destFileParent = new File(destFileName).getParentFile();
				if (!destFileParent.exists())
				{
					destFileParent.mkdirs();
				}

				try
				{
					Class<?> reportCreatorClass = JRClassLoader.loadClassFromFile(null, new File(srcFileName));
					ReportCreator reportCreator = (ReportCreator)reportCreatorClass.getDeclaredConstructor().newInstance();
					JasperDesign jasperDesign = reportCreator.create();
					new JRXmlWriter(jasperReportsContext).write(jasperDesign, destFileName, "UTF-8");

					log("File : " + srcFileName);
				}
				catch (JRException | IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e)
				{
					log("Error running API report design class : " + srcFileName);
					log(e, Project.MSG_ERR);
					isError = true;
				}
			}
		
			if (isError)
			{
				throw new BuildException("Errors were encountered when running API report designs classes.");
			}
		}
	}
	
	
}
