/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.view;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRSaveContributor extends FileFilter
{
	private JasperReportsContext jasperReportsContext;
	private Locale locale;
	private ResourceBundle resourceBundle;
	
	/**
	 * @see #JRSaveContributor(JasperReportsContext, Locale, ResourceBundle)
	 * @deprecated To be removed.
	 */
	public JRSaveContributor()
	{
		this(null, null);
	}
	
	/**
	 * @see #JRSaveContributor(JasperReportsContext, Locale, ResourceBundle)
	 * @deprecated To be removed.
	 */
	public JRSaveContributor(Locale locale, ResourceBundle resBundle)
	{
		this(DefaultJasperReportsContext.getInstance(), locale, resBundle);
	}
	
	/**
	 * 
	 */
	public JRSaveContributor(
		JasperReportsContext jasperReportsContext,
		Locale locale, 
		ResourceBundle resBundle
		)
	{
		this.jasperReportsContext = jasperReportsContext;
		
		if (locale != null)
		{
			this.locale = locale;
		}
		else
		{
			this.locale = Locale.getDefault();
		}

		if (resBundle == null)
		{
			this.resourceBundle = ResourceBundle.getBundle("net/sf/jasperreports/view/viewer", this.locale);
		}
		else
		{
			this.resourceBundle = resBundle;
		}
	}
	
	
	/**
	 * 
	 */
	protected JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}

	
	/**
	 * 
	 */
	protected String getBundleString(String key)
	{
		return resourceBundle.getString(key);
	}

	
	/**
	 * 
	 */
	public abstract void save(JasperPrint jasperPrint, File file) throws JRException;


}
