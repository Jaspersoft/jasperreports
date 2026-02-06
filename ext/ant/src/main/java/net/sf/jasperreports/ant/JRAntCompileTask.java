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

/*
 * Contributors:
 * Henri Chen - henrichen@users.sourceforge.net
 * Kees Kuip  - keeskuip@users.sourceforge.net
 */
package net.sf.jasperreports.ant;

import java.io.File;
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
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JRCompiler;


/**
 * Ant task for batch-compiling XML report design files.
 * Works like the built-in <code>javac</code> Ant task.
 * <p>
 * This task can take the following arguments:
 * <ul>
 * <li>src
 * <li>destdir
 * <li>compiler
 * <li>classpath
 * <li>tempdir
 * <li>keepjava
 * <li>xmlvalidation
 * </ul>
 * Of these arguments, the <code>src</code> and <code>destdir</code> are required.
 * When this task executes, it will recursively scan the <code>src</code> and 
 * <code>destdir</code> looking for XML report design files to compile. 
 * This task makes its compile decision based on timestamp and only XML files 
 * that have no corresponding .jasper file or where the compiled report design file 
 * is older than the XML file will be compiled.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRAntCompileTask extends JRBaseAntTask
{


	/**
	 *
	 */
	private Path src;
	private File destdir;
	private File tempdir;
	private Boolean keepjava;
	private String compiler;
	private Path classpath;
	private boolean xmlvalidation = true;

	private Map<String, String> reportFilesMap;


	/**
	 * Sets the source directories to find the XML report design files.
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
	 * Adds a path for source compilation.
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
	 * Sets the destination directory into which the XML report design files should be compiled.
	 * 
	 * @param destdir destination directory
	 */
	public void setDestdir(File destdir)
	{
		this.destdir = destdir;
	}


	/**
	 * Sets the temporary working directory into which to store the temporary files 
	 * generated during XML report design file compilation. This is only used by the
	 * Java bytecode report compilers that need to have the Java source files stored 
	 * on disk in order to compile them.
	 * <p>
	 * If not set, the temporary working directory will be the temporary working directory,
	 * as specified by the <code>java.io.tmpdir</code> system property.
	 * 
	 * @param tempdir temporary working directory
	 */
	public void setTempdir(File tempdir)
	{
		this.tempdir = tempdir;
	}


	/**
	 * Sets a boolean flag that will instruct the Java bytecode report compilers
	 * to avoid deletion of the Java source files generated in the temporary working 
	 * directory during report generation. This is useful when debugging.
	 * 
	 * @param keepjava flag for preventing the deletion of generated Java source files
	 */
	public void setKeepjava(Boolean keepjava)
	{
		this.keepjava = keepjava;
	}


	/**
	 * Sets the name of the report compiler class to use when compiling the XML
	 * report design files.
	 * <p>
	 * The specified class should be an implementation of the 
	 * {@link net.sf.jasperreports.engine.design.JRCompiler} interface.
	 * When specified, this value will temporarily override the value of the
	 * {@link net.sf.jasperreports.engine.design.JRCompiler#COMPILER_CLASS net.sf.jasperreports.compiler.class} system property.
	 * 
	 * @param compiler report compiler class name
	 */
	public void setCompiler(String compiler)
	{
		this.compiler = compiler;
	}


	/**
	 * Adds a path to the classpath.
	 * 
	 * @return classpath to use when compiling the report associated Java expressions class
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
	 * Instructs the XML parser to validate the XML report design file during compilation. 
	 * 
	 * @param xmlvalidation flag for enabling/disabling the validation feature of the XML parser 
	 */
	public void setXmlvalidation(boolean xmlvalidation)
	{
		this.xmlvalidation = xmlvalidation;
	}


	/**
	 * Executes the task.
	 */
	@Override
	public void execute() throws BuildException
	{
		checkParameters();

		reportFilesMap = new HashMap<>();

		if (tempdir != null)
		{
			jasperReportsContext.setProperty(JRCompiler.COMPILER_TEMP_DIR, String.valueOf(tempdir));
		}

		if (keepjava != null)
		{
			jasperReportsContext.setProperty(JRCompiler.COMPILER_KEEP_JAVA_FILE, String.valueOf(keepjava));
		}

		setCompilerClass(compiler);

		//TODO legacyxml
		jasperReportsContext.setProperty("net.sf.jasperreports.compiler.xml.validation", String.valueOf(xmlvalidation));

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
			/*   */
			compile();
		}
		finally
		{
			if (classLoader != null)
			{
				classLoader.resetThreadContextLoader();
			}				
		}			
	}
	
	
	@SuppressWarnings("deprecation")
	private void setCompilerClass(String compile)
	{
		if (compiler != null)
		{
			jasperReportsContext.setProperty(JRCompiler.COMPILER_CLASS, compiler);
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

		if (tempdir != null && !tempdir.isDirectory()) 
		{
			throw 
				new BuildException(
					"The temporary directory \"" 
						+ tempdir 
						+ "\" does not exist "
						+ "or is not a directory.", 
					getLocation()
					);
		}
	}
	
	
	/**
	 * Scans the source directories looking for source files to be compiled. 
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
	 * Scans the directory looking for source files to be compiled. 
	 * The results are returned in the instance variable <code>reportFilesMap</code>.
	 * 
	 * @param srcdir source directory
	 * @param destdir destination directory
	 * @param files included file names
	 */
	protected void scanDir(File srcdir, File destdir, String[] files) 
	{
		RegexpPatternMapper mapper = new RegexpPatternMapper();
		mapper.setFrom("^(.*)\\.(.*)$");
		mapper.setTo("\\1.jasper");

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
	 * Performs the compilation of the selected report design files.
	 */
	protected void compile() throws BuildException
	{
		Collection<String> files = reportFilesMap.keySet();

		if (files != null && files.size() > 0)
		{
			boolean isError = false;
		
			log("Compiling " + files.size() + " report design files.");

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
					JasperCompileManager.getInstance(jasperReportsContext).compileToFile(srcFileName, destFileName);
					log("File : " + srcFileName);
				}
				catch (Exception e) // for some reason, jackson jrxml parsing was made to throw runtime exception
				{
					log("Error compiling report design : " + srcFileName);
					log(e, Project.MSG_ERR);
					isError = true;
				}
			}
		
			if (isError)
			{
				throw new BuildException("Errors were encountered when compiling report designs.");
			}
		}
	}
	
	
}
