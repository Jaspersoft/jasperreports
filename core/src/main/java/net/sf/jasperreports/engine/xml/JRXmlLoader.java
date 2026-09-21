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
 * Artur Biesiadowski - abies@users.sourceforge.net 
 */
package net.sf.jasperreports.engine.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;


/**
 * Utility class that helps parsing a JRXML file into a 
 * {@link net.sf.jasperreports.engine.design.JasperDesign} object.
 * <p>
 * This can be done using one of the <code>load(...)</code> or <code>loadXml</code> 
 * methods published by this class. Applications might need to do this in cases where report
 * templates kept in their source form (JRXML) must be modified at runtime based on
 * some user input and then compiled on the fly for filling with data.
 * </p>
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRXmlLoader
{
	
	private static final String EXCEPTION_MESSAGE_KEY_NO_LOADER = "xml.loader.unknown.subdataset";

	/**
	 *
	 */
	private final JasperReportsContext jasperReportsContext;

	private boolean ignoreConsistencyProblems;
		
	/**
	 *
	 */
	public JRXmlLoader(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}

	/**
	 *
	 */
	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}


	/**
	 * @see #load(JasperReportsContext, String)
	 */
	public static JasperDesign load(String sourceFileName) throws JRException//FIXMEREPO consider renaming
	{
		return load(DefaultJasperReportsContext.getInstance(),  sourceFileName);
	}


	/**
	 *
	 */
	public static JasperDesign load(JasperReportsContext jasperReportsContext, String sourceFileName) throws JRException//FIXMEREPO consider renaming
	{
		return load(jasperReportsContext, new File(sourceFileName));
	}


	/**
	 * @see #load(JasperReportsContext, File)
	 */
	public static JasperDesign load(File file) throws JRException
	{
		return load(DefaultJasperReportsContext.getInstance(), file);
	}


	/**
	 *
	 */
	public static JasperDesign load(JasperReportsContext jasperReportsContext, File file) throws JRException
	{
		JasperDesign jasperDesign = null;

		try (FileInputStream fis = new FileInputStream(file))
		{
			jasperDesign = JRXmlLoader.load(jasperReportsContext, fis);
		}
		catch (IOException e)
		{
			throw new JRException(e);
		}

		return jasperDesign;
	}


	/**
	 * @see #load(JasperReportsContext, InputStream)
	 */
	public static JasperDesign load(InputStream is) throws JRException
	{
		return load(DefaultJasperReportsContext.getInstance(), is);
	}


	/**
	 *
	 */
	public static JasperDesign load(JasperReportsContext jasperReportsContext, InputStream is) throws JRException
	{
		JasperDesign jasperDesign = null;

		JRXmlLoader xmlLoader = new JRXmlLoader(jasperReportsContext);
		
		jasperDesign = xmlLoader.loadXML(is);

		return jasperDesign;
	}


	/**
	 *
	 */
	public JasperDesign loadXML(InputStream is) throws JRException
	{
		byte[] data = JRLoader.loadBytes(is);
		List<ReportLoader> loaders = jasperReportsContext.getExtensions(ReportLoader.class);
		for (ReportLoader reportLoader : loaders)
		{
			//TODO legacyxml ignoreConsistencyProblems
			Optional<JasperDesign> report = reportLoader.loadReport(jasperReportsContext, data);
			if (report.isPresent())
			{
				return report.get();
			}
		}
		//TODO legacyxml 
		throw new JRException("Unable to load report");
	}
	
	/**
	 * Returns true if the loader is set to ignore consistency problems
	 * @return the ignoreConsistencyProblems flag.
	 */
	public boolean isIgnoreConsistencyProblems() {
		return ignoreConsistencyProblems;
	}
	
	/**
	 * Allows to enable or disable the reporting of consistency problems. Consistency 
	 * problems are problems in the logical structure of the report such as references
	 * to missing groups and fonts.
	 * 
	 * @param ignoreConsistencyProblems The ignoreConsistencyProblems value to set.
	 */
	public void setIgnoreConsistencyProblems(boolean ignoreConsistencyProblems) {
		this.ignoreConsistencyProblems = ignoreConsistencyProblems;
	}
}
