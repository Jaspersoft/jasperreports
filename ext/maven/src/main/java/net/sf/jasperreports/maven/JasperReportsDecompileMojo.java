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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;


/**
 * Decompiles JasperReports compiled report design *.jasper files to source report design *.jrxml files.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@Mojo(name = "decompile", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class JasperReportsDecompileMojo extends AbstractJasperReportsMojo
{
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;
	
	/**
	 * Flag to skip the report decompilation goal.
	 */
	@Parameter(property = "jasperreports.decompile.skip", defaultValue = "false")
	private boolean skip;
	
	/**
	 * The directory where the compiled report design *.jasper files are found.
	 */
	@Parameter(alias = "sourceDirectoryDecompile")
	private File sourceDirectory;

	/**
	 * The directory where the source report design *.jrxml files will be generated.
	 */
	@Parameter(alias = "outputDirectoryDecompile")
	private File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException
	{
		if (skip)
		{
			getLog().info("Skipping report design files decompilation.");
			return;
		}

		SourceInclusionScanner scanner = new StaleSourceScanner();
		SourceMapping mapping = new SuffixMapping(".jasper", ".jrxml");
		scanner.addSourceMapping(mapping);
		
		Set<File> sources = null;
		
		try
		{
			sources = scanner.getIncludedSources(sourceDirectory, outputDirectory);
		}
		catch (InclusionScanException e)
		{
			throw new MojoExecutionException("Error scanning source files in folder : " + sourceDirectory.getAbsolutePath(), e);
		}
		
		if (sources == null || sources.size() == 0)
		{
			getLog().info("No report design files to decompile.");
		}
		else
		{
			getLog().info("Decompiling " + sources.size() + " report design files.");
			
			ClassLoader newClassLoader = null;
			
			try
			{
				@SuppressWarnings("rawtypes")
				List classpathElements = project.getRuntimeClasspathElements();
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
				throw new MojoExecutionException("Errors were encountered when decompiling report designs.", e);
			}
			
			ClassLoader oldThreadClassLoader = Thread.currentThread().getContextClassLoader();
				
			try
			{
				if (newClassLoader != null)
				{
					Thread.currentThread().setContextClassLoader(newClassLoader);
				}

				jasperReportsContext = new SimpleJasperReportsContext();

				decompile(sources, mapping);
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

	
	private void decompile(Set<File> sources, SourceMapping mapping) throws MojoExecutionException
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
                		outputDirectory, 
                		sourceDirectory.toPath().relativize(srcFile.toPath()).toString()
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

			futures.add(executorService.submit(new DecompileTask(srcFile.getAbsolutePath(), destFile.getAbsolutePath())));
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
			throw new MojoExecutionException("Errors were encountered when decompiling report designs.");
		}
	}
	
	
	class DecompileTask implements Callable<Void>
	{
		private String srcFileName;
		private String destFileName;
		
		public DecompileTask(String srcFileName, String destFileName)
		{
			this.srcFileName = srcFileName;
			this.destFileName = destFileName;
		}
		
		@Override
		public Void call() throws Exception
		{
			try
			{
				new JRXmlWriter(jasperReportsContext).write(
					(JasperReport)JRLoader.loadObjectFromFile(srcFileName), 
					destFileName, 
					"UTF-8"
					);
				getLog().debug("File : " + srcFileName);
			}
			catch (JRException e)
			{
				getLog().error("Error decompiling report design : " + srcFileName, e);
				error();
			}
			
			return null;
		}
	}
}
