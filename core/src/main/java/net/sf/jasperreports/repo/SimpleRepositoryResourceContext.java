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
package net.sf.jasperreports.repo;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleRepositoryResourceContext implements RepositoryResourceContext
{

	public static SimpleRepositoryResourceContext of(String contextLocation)
	{
		SimpleRepositoryResourceContext context = new SimpleRepositoryResourceContext();
		context.setContextLocation(contextLocation);
		return context;
	}
	
	public static SimpleRepositoryResourceContext of(String contextLocation, RepositoryResourceContext fallbackContext)
	{
		SimpleRepositoryResourceContext context = new SimpleRepositoryResourceContext();
		context.setContextLocation(contextLocation);
		context.setFallbackContext(fallbackContext);
		return context;
	}
	
	private RepositoryResourceContext fallbackContext;
	
	private String contextLocation;
	
	private boolean selfAsDerivedFallback;

	@Override
	public RepositoryResourceContext getFallbackContext()
	{
		return fallbackContext;
	}

	public void setFallbackContext(RepositoryResourceContext fallbackContext)
	{
		this.fallbackContext = fallbackContext;
	}
	
	@Override
	public String getContextLocation()
	{
		return contextLocation;
	}

	public void setContextLocation(String contextLocation)
	{
		this.contextLocation = contextLocation;
	}

	@Override
	public RepositoryResourceContext getDerivedContextFallback()
	{
		return selfAsDerivedFallback ? this : fallbackContext;
	}

	public boolean isSelfAsDerivedFallback()
	{
		return selfAsDerivedFallback;
	}

	public void setSelfAsDerivedFallback(boolean selfAsDerivedFallback)
	{
		this.selfAsDerivedFallback = selfAsDerivedFallback;
	}

}
