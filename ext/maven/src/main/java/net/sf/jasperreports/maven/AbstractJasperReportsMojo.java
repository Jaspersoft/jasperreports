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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import net.sf.jasperreports.engine.SimpleJasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractJasperReportsMojo extends AbstractMojo
{
	/**
	 * Sets the number of threads to use for executing the goal on multiple files in parallel.
	 * <p>
	 * The value should be a positive integer representing the number of threads, or a float number representing a multiplier 
	 * of the number of CPU cores, when followed by the letter C.
	 * For example, 2C means twice the number of CPU cores, while 0.5C means half the number of CPU cores.
	 * The default is 1.5C.
	 */
	@Parameter(defaultValue = "1.5C")
	private String threads;
	
	private boolean isError;
	
	protected SimpleJasperReportsContext jasperReportsContext;

	
	protected synchronized void error()
	{
		this.isError = true;
	}
	
	protected boolean isError()
	{
		return isError;
	}

	protected int getThreads() throws MojoExecutionException
	{
		int numberOfThreads = 1;
		
		if (threads.endsWith("C")) 
		{
			float multiplier = Float.parseFloat(threads.substring(0, threads.length() - 1));
			if (multiplier <= 0.0f)
			{
				throw new MojoExecutionException("The threads multiplier must be a positive float number followed by 'C'.");
			}
			numberOfThreads = (int) (Runtime.getRuntime().availableProcessors() * multiplier);
			numberOfThreads = numberOfThreads == 0 ? 1 : numberOfThreads;
		}
		else
		{
			numberOfThreads = Integer.parseInt(threads);
			if (numberOfThreads <= 0)
			{
				throw new MojoExecutionException("The number of threads must be a positive integer or a float number followed by 'C' representing a multiplier of the number of CPU cores.");
			}
		}
		
		return numberOfThreads;
	}
}
