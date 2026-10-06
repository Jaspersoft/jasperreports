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
package net.sf.jasperreports.dataadapters;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.repo.RepositoryUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class RepositoryDataLocationService implements DataFileService
{
	
	private static final Log log = LogFactory.getLog(RepositoryDataLocationService.class);
	
	private final RepositoryUtil repository;
	private final RepositoryDataLocation dataLocation;
	
	public RepositoryDataLocationService(ParameterContributorContext context, RepositoryDataLocation dataLocation)
	{
		this.repository = RepositoryUtil.getInstance(context.getRepositoryContext());
		this.dataLocation = dataLocation;
	}

	@Override
	public DataFileConnection getDataFileConnection(Map<String, Object> parameters) throws JRException
	{
		String location = dataLocation.getLocation();
		if (log.isDebugEnabled())
		{
			log.debug("loading from the repository " + location);
		}
		
		InputStream dataStream = repository.getInputStreamFromLocation(location);
		return new DataFileStreamConnection(dataStream);
	}

}
