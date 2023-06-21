/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
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
package net.sf.jasperreports.view;

import java.util.Locale;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Stefan Bischof
 */
public interface JRSaveContributorFactory 
{
	
	/**
	 * @see #create(JasperReportsContext, Locale, ResourceBundle)
	 */
	default JRSaveContributor create()
	{
		return	create(null, null);
	}
	
	/**
	 * @see #create(JasperReportsContext, Locale, ResourceBundle)
	 */
	default JRSaveContributor create(Locale locale, ResourceBundle resBundle)
	{
		return create(DefaultJasperReportsContext.getInstance(), locale, resBundle);
	}
	
	/**
	 * creates a JRSaveContributor
	 */
	JRSaveContributor create(JasperReportsContext jasperReportsContext, Locale locale, ResourceBundle resBundle);

}
