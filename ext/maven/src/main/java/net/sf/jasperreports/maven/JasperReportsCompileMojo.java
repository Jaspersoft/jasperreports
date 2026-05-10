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
package net.sf.jasperreports.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.StaleSourceScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;


/**
 * Compiles JasperReports source report design *.jrxml files to compiled report design *.jasper files.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@Mojo(name = "compile", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class JasperReportsCompileMojo extends AbstractJasperReportsMojo
{
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	protected MavenProject project;
	
	/**
	 * Flag to skip the reports compilation goal.
	 */
	@Parameter(property = "jasperreports.compile.skip", defaultValue = "false")
	private boolean skip;
	
	/**
	 * The directory where the source report design *.jrxml files are found.
	 */
	@Parameter(defaultValue = "${project.basedir}/src/main/reports")
	private File sourceDirectory;

	/**
	 * The directory where the compiled report design *.jasper files will be generated.
	 */
	@Parameter(defaultValue = "${project.build.directory}/reports")
	private File outputDirectory;
	
	
	protected File getSourceDirectory()
	{
		return sourceDirectory;
	}

	
	protected File getOutputDirectory()
	{
		return outputDirectory;
	}
	
	
	protected List<String> getClasspathElements() throws DependencyResolutionRequiredException
	{
		return project.getRuntimeClasspathElements();
	}

	
	@Override
	public void execute() throws MojoExecutionException
	{
		if (skip)
		{
			getLog().info("Skipping report design files compilation.");
			return;
		}

		SourceInclusionScanner scanner = new StaleSourceScanner();
		SourceMapping mapping = new SuffixMapping(".jrxml", ".jasper");
		scanner.addSourceMapping(mapping);
		
		Set<File> sources = null;
		
		try
		{
			sources = scanner.getIncludedSources(getSourceDirectory(), getOutputDirectory());
		}
		catch (InclusionScanException e)
		{
			throw new MojoExecutionException("Error scanning source files in folder : " + getSourceDirectory().getAbsolutePath(), e);
		}
		
		if (sources == null || sources.size() == 0)
		{
			getLog().info("No report design files to compile.");
		}
		else
		{
			getLog().info("Compiling " + sources.size() + " report design files.");
			
			ClassLoader newClassLoader = null;
			
			try
			{
				@SuppressWarnings("rawtypes")
				List classpathElements = getClasspathElements();
				if (classpathElements != null && !classpathElements.isEmpty())
				{
					getLog().debug("URLs added to classpath:");
					List<URL> urls = new ArrayList<URL>(classpathElements.size());
					for (Object element : classpathElements)
					{
						urls.add(new File(element.toString()).toURI().toURL());
						getLog().debug(element.toString());
					}
					newClassLoader = new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
				}
			}
			catch (DependencyResolutionRequiredException | MalformedURLException e)
			{
				throw new MojoExecutionException("Errors were encountered when compiling report designs.", e);
			}
			
			ClassLoader oldThreadClassLoader = Thread.currentThread().getContextClassLoader();
				
			try
			{
				if (newClassLoader != null)
				{
					Thread.currentThread().setContextClassLoader(newClassLoader);
				}

				jasperReportsContext = new SimpleJasperReportsContext();

				compile(sources, mapping);
			}
			finally
			{
				if (newClassLoader != null)
				{
					Thread.currentThread().setContextClassLoader(oldThreadClassLoader);
				}
			}
		}
	}

	
	private void compile(Set<File> sources, SourceMapping mapping) throws MojoExecutionException
	{
		ExecutorService executorService = Executors.newFixedThreadPool(getThreads());
		
		List<Future<Void>> futures = new ArrayList<>(sources.size());

		for (File srcFile : sources)
		{
            File destFile = null;
            try
            {
                destFile =
                	mapping.getTargetFiles(
                		getOutputDirectory(), 
                		getSourceDirectory().toPath().relativize(srcFile.toPath()).toString()
                		).iterator().next();
            }
            catch (InclusionScanException e)
            {
            	throw new MojoExecutionException("Error determining destination file for source file : " + srcFile.getAbsolutePath(), e);
            }
			File destFileParent = destFile.getParentFile();
			if (!destFileParent.exists())
			{
				destFileParent.mkdirs();
			}

			futures.add(executorService.submit(new CompileTask(srcFile.getAbsolutePath(), destFile.getAbsolutePath())));
		}

		for (Future<Void> future : futures)
		{
			try
			{
				future.get();
			}
			catch (ExecutionException | InterruptedException e)
			{
				getLog().error(e);
				error();
			}
		}
		
		executorService.shutdown();
		try 
		{
		    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) 
		    {
		        executorService.shutdownNow();
		    } 
		}
		catch (InterruptedException e) 
		{
		    executorService.shutdownNow();
		}
		
		if (isError())
		{
			throw new MojoExecutionException("Errors were encountered when compiling report designs.");
		}
	}
	
	
	class CompileTask implements Callable<Void>
	{
		private String srcFileName;
		private String destFileName;
		
		public CompileTask(String srcFileName, String destFileName)
		{
			this.srcFileName = srcFileName;
			this.destFileName = destFileName;
		}
		
		@Override
		public Void call() throws Exception
		{
			try
			{
				JasperCompileManager.getInstance(jasperReportsContext).compileToFile(srcFileName, destFileName);
				getLog().debug("File : " + srcFileName);
			}
			catch (Exception e) // for some reason, jackson jrxml parsing was made to throw runtime exception
			{
				getLog().error("Error compiling report design : " + srcFileName, e);
				error();
			}
			
			return null;
		}
	}
}
