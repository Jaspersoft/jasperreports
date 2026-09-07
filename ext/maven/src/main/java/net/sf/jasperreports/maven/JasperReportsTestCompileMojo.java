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
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;


/**
 * Compiles JasperReports source test report design *.jrxml files to compiled report design *.jasper files.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@Mojo(name = "testCompile", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true)
public class JasperReportsTestCompileMojo extends JasperReportsCompileMojo
{
	/**
	 * Flag to skip the test reports compilation goal.
	 */
	@Parameter(property = "jasperreports.testCompile.skip", defaultValue = "false")
	private boolean skip;
	
	/**
	 * The directory where the source test report design *.jrxml files are found.
	 */
	@Parameter(defaultValue = "${project.basedir}/src/test/reports")
	private File sourceDirectory;

	/**
	 * The directory where the compiled test report design *.jasper files will be generated.
	 */
	@Parameter(defaultValue = "${project.build.directory}/test-reports")
	private File outputDirectory;
	
	
	@Override
	protected File getSourceDirectory()
	{
		return sourceDirectory;
	}

	@Override
	protected File getOutputDirectory()
	{
		return outputDirectory;
	}
	
	@Override
	protected List<String> getClasspathElements() throws DependencyResolutionRequiredException
	{
		return project.getTestClasspathElements();
	}
}
