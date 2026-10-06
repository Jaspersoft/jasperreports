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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.dataadapters.repo.DataAdapterResource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.ParameterContributor;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.ParameterContributorFactory;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryResourceContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.ResourceInfo;
import net.sf.jasperreports.repo.SimpleRepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryResourceContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class DataAdapterParameterContributorFactory implements ParameterContributorFactory
{

	private static final Log log = LogFactory.getLog(DataAdapterParameterContributorFactory.class);
	
	/**
	 * A report/dataset level property that provides the location of a data adapter resource 
	 * to be used for the dataset.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			valueType = DataAdapter.class,//for JSS
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_4_1_1
			)
	public static final String PROPERTY_DATA_ADAPTER_LOCATION = JRPropertiesUtil.PROPERTY_PREFIX + "data.adapter";

	private static final DataAdapterParameterContributorFactory INSTANCE = new DataAdapterParameterContributorFactory();
	
	private DataAdapterParameterContributorFactory()
	{
	}
	
	/**
	 * 
	 */
	public static DataAdapterParameterContributorFactory getInstance()
	{
		return INSTANCE;
	}

	@Override
	public List<ParameterContributor> getContributors(ParameterContributorContext context) throws JRException
	{
		List<ParameterContributor> contributors = new ArrayList<>();
		if (!context.isToPrepareData())
		{
			return contributors;
		}

		String dataAdapterUri = JRPropertiesUtil.getOwnProperty(context.getDataset(), PROPERTY_DATA_ADAPTER_LOCATION);
		if (dataAdapterUri == null)
		{
			dataAdapterUri = JRPropertiesUtil.getInstance(context.getJasperReportsContext()).getProperty(
					PROPERTY_DATA_ADAPTER_LOCATION);
		}

		if (dataAdapterUri != null)
		{
			RepositoryUtil repository = RepositoryUtil.getInstance(context.getRepositoryContext());
			ResourceInfo resourceInfo = repository.getResourceInfo(dataAdapterUri);
			
			String resourceLocation = dataAdapterUri;
			String contextLocation = null;
			if (resourceInfo != null)
			{
				resourceLocation = resourceInfo.getRepositoryResourceLocation();
				contextLocation = resourceInfo.getRepositoryContextLocation();
				if (log.isDebugEnabled())
				{
					log.debug("data adapter " + dataAdapterUri + " resolved to " + resourceLocation
							+ ", context " + contextLocation);
				}
			}
			
			DataAdapterResource dataAdapterResource = repository.getResourceFromLocation(resourceLocation, DataAdapterResource.class);
			
			RepositoryResourceContext currentContext = context.getRepositoryContext().getResourceContext();
			RepositoryResourceContext adapterResourceContext = SimpleRepositoryResourceContext.of(contextLocation,
					currentContext == null ? null : currentContext.getDerivedContextFallback());
			RepositoryContext adapterRepositoryContext = SimpleRepositoryContext.of(context.getJasperReportsContext(), 
					adapterResourceContext);
			ParameterContributorContext adapterContext = context.withRepositoryContext(adapterRepositoryContext);
			
			ParameterContributor dataAdapterService = DataAdapterServiceUtil.getInstance(adapterContext).getService(dataAdapterResource.getDataAdapter());
			
			return Collections.singletonList(dataAdapterService);
		}

		return contributors;
	}
	
}
