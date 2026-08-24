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
package net.sf.jasperreports.dataadapters.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.components.table.Cell;
import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.data.DataFile;
import net.sf.jasperreports.dataadapters.DataAdapter;
import net.sf.jasperreports.dataadapters.DataAdapterService;
import net.sf.jasperreports.dataadapters.DataAdapterServiceUtil;
import net.sf.jasperreports.dataadapters.DataFileServiceFactory;
import net.sf.jasperreports.dataadapters.FileDataAdapter;
import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.engine.util.Designated;
import net.sf.jasperreports.engine.util.Designator;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRQueryExecuterUtils;
import net.sf.jasperreports.properties.PropertyMetadata;
import net.sf.jasperreports.properties.ResourcePropertiesMetadataReader;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DataAdapterPropertiesMetadataUtil
{
	
	public static DataAdapterPropertiesMetadataUtil getInstance(JasperReportsContext context)
	{
		return DataAdapterPropertiesMetadataUtil.getInstance(context, Locale.getDefault());
	}
	
	public static DataAdapterPropertiesMetadataUtil getInstance(JasperReportsContext context, Locale locale)
	{
		return new DataAdapterPropertiesMetadataUtil(context, locale);
	}
	
	private JasperReportsContext context;
	private Locale locale;
	
	private volatile Map<String, PropertyMetadata> loadedProperties;

	protected DataAdapterPropertiesMetadataUtil(JasperReportsContext context, Locale locale)
	{
		this.context = context;
		this.locale = locale;
	}
	
	protected Collection<PropertyMetadata> allProperties()
	{
		Map<String, PropertyMetadata> allProperties = loadedProperties;
		if (allProperties == null)
		{
			loadedProperties = allProperties = ResourcePropertiesMetadataReader.instance().readProperties(context, locale);
		}
		return allProperties.values();
	}
	
	public List<PropertyMetadata> getProperties()
	{
		Collection<PropertyMetadata> allProperties = allProperties();
		return new ArrayList<>(allProperties);
	}
	
	public List<PropertyMetadata> getProperties(PropertyScope scope)
	{
		return allProperties().stream().filter(primaryScopePredicate(scope)).collect(Collectors.toList());
	}
	
	public List<PropertyMetadata> getQueryExecuterFieldProperties(String queryLanguage) throws JRException
	{
		String qualification = queryExecuterQualification(queryLanguage);
		if (qualification == null)
		{
			return Collections.emptyList();
		}
		
		List<PropertyMetadata> properties = filterQualifiedProperties(PropertyScope.FIELD, qualification);
		return properties;
	}
	
	protected String queryExecuterQualification(String queryLanguage) throws JRException
	{
		QueryExecuterFactory queryExecuterFactory = JRQueryExecuterUtils.getInstance(context).getExecuterFactory(queryLanguage);
		if (!(queryExecuterFactory instanceof Designated))
		{
			return null;
		}
		String queryExecuterName = ((Designated) queryExecuterFactory).getDesignation();
		return queryExecuterName;
	}

	protected List<PropertyMetadata> filterQualifiedProperties(PropertyScope primaryScope, String... qualifications)
	{
		return qualifiedProperties(primaryScope, qualifications).collect(Collectors.toList());
	}

	protected Stream<PropertyMetadata> qualifiedProperties(PropertyScope primaryScope, String... qualifications)
	{
		return allProperties().stream().filter(scopeQualificationsPredicate(primaryScope, qualifications));
	}

	protected Predicate<PropertyMetadata> primaryScopePredicate(PropertyScope scope)
	{
		return property -> property.getScopes().contains(scope);
	}

	protected Predicate<PropertyMetadata> scopeQualificationsPredicate(PropertyScope scope, String... qualifications)
	{
		Set<String> qualificationSet = Stream.of(qualifications).filter(v -> v != null).collect(Collectors.toSet());
		return primaryScopePredicate(scope).and(property -> 
		{
			List<String> propertyQualifications = property.getScopeQualifications();
			return propertyQualifications == null || propertyQualifications.isEmpty()
					|| !Collections.disjoint(propertyQualifications, qualificationSet);
		});
	}
	
	public List<PropertyMetadata> getParameterProperties(DataAdapter dataAdapter)
	{
		String qualification = dataFileQualification(dataAdapter);
		if (qualification == null)
		{
			return Collections.emptyList();
		}
		
		List<PropertyMetadata> properties = filterQualifiedProperties(PropertyScope.PARAMETER, qualification);
		return properties;
	}
	
	@SuppressWarnings("unchecked")
	protected String dataFileQualification(DataAdapter dataAdapter)
	{
		if (!(dataAdapter instanceof FileDataAdapter))
		{
			return null;
		}
		
		DataFile dataFile = ((FileDataAdapter) dataAdapter).getDataFile();
		String name = null;
		List<DataFileServiceFactory> factories = context.getExtensions(DataFileServiceFactory.class);
		if (factories != null)
		{
			for (DataFileServiceFactory factory : factories)
			{
				if (factory instanceof Designator<?>)
				{
					name = ((Designator<DataFile>) factory).getName(dataFile);
					if (name != null)
					{
						break;
					}
				}
			}
		}
		return name;
	}
	
	public List<PropertyMetadata> getElementProperties(JRElement element)
	{
		return allProperties().stream().filter(v -> inScope(v, element)).collect(Collectors.toCollection(ArrayList::new));
	}
	
	protected boolean inScope(PropertyMetadata property, JRElement element)
	{
		List<PropertyScope> scopes = property.getScopes();
		if (scopes.contains(PropertyScope.ELEMENT))
		{
			return true;
		}
		
		if (element instanceof JRTextElement && scopes.contains(PropertyScope.TEXT_ELEMENT))
		{
			return true;
		}
		
		if (element instanceof JRImage && scopes.contains(PropertyScope.IMAGE_ELEMENT))
		{
			return true;
		}
		
//FIXME7
//		if (element instanceof JRChart && scopes.contains(PropertyScope.CHART_ELEMENT))
//		{
//			return true;
//		}
		
		if (element instanceof JRCrosstab && scopes.contains(PropertyScope.CROSSTAB))
		{
			return true;
		}
		
		if (element instanceof JRFrame && scopes.contains(PropertyScope.FRAME))
		{
			return true;
		}
		
		if (element instanceof JRSubreport && scopes.contains(PropertyScope.SUBREPORT))
		{
			return true;
		}
		
		//TODO lucianc generic element
		
		if (element instanceof JRComponentElement && scopes.contains(PropertyScope.COMPONENT))
		{
			List<String> qualifications = property.getScopeQualifications();
			if (qualifications == null || qualifications.isEmpty())
			{
				//assuming all components
				return true;
			}
			
			JRComponentElement componentElement = (JRComponentElement) element;
			String qualification;
			if (componentElement.getComponent() instanceof Designated)
			{
				qualification = ((Designated) componentElement.getComponent()).getDesignation();
			}
			else
			{
				Component component = componentElement.getComponent();
				if (component == null)
				{
					//key is missing
					qualification = null;
				}
				else
				{
					qualification = ComponentsEnvironment.getInstance(context).getComponentName(component.getClass());
				}
			}
			
			return qualification != null && qualifications.contains(qualification);
		}
		
		return false;
	}
	
	public List<PropertyMetadata> getReportProperties(JRReport report)
	{
		return getProperties(PropertyScope.REPORT);
	}

	protected String datasetQueryQualification(JRDataset dataset) throws JRException
	{
		String queryLanguage = dataset.getQuery() == null ? null : dataset.getQuery().getLanguage();
		String queryQualification = queryLanguage == null ? null : queryExecuterQualification(queryLanguage);
		return queryQualification;
	}
	
	@SuppressWarnings("unchecked")
	protected String dataAdapterQualification(JRDataset dataset, DataAdapter dataAdapter)
	{
		ParameterContributorContext contributorContext = new ParameterContributorContext(context,
				dataset, Collections.<String, Object>emptyMap());
		DataAdapterServiceUtil serviceUtil = DataAdapterServiceUtil.getInstance(contributorContext);
		DataAdapterService service = serviceUtil.getService(dataAdapter);
		if (service instanceof Designated)
		{
			return ((Designated) service).getDesignation();
		}
		if (service instanceof Designator<?>)
		{
			return ((Designator<DataAdapter>) service).getName(dataAdapter);
		}
		return null;
	}
	
	public List<PropertyMetadata> getDatasetProperties(JRDataset dataset, DataAdapter dataAdapter) throws JRException
	{
		String queryQualification = datasetQueryQualification(dataset);
		String dataAdapterQualification = dataAdapter == null ? null : dataAdapterQualification(dataset, dataAdapter);
		String dataFileQualification = dataAdapter == null ? null : dataFileQualification(dataAdapter);
		return filterQualifiedProperties(PropertyScope.DATASET, queryQualification, dataAdapterQualification, dataFileQualification);
	}
	
	public List<PropertyMetadata> getFieldProperties(JRDataset dataset, DataAdapter dataAdapter) throws JRException
	{
		String queryQualification = datasetQueryQualification(dataset);
		String dataAdapterQualification = dataAdapter == null ? null : dataAdapterQualification(dataset, dataAdapter);
		return filterQualifiedProperties(PropertyScope.FIELD, queryQualification, dataAdapterQualification);
	}
	
	public List<PropertyMetadata> getContainerProperties(JRElementGroup container)
	{
		return allProperties().stream().filter(p -> inScope(p, container)).collect(Collectors.toCollection(ArrayList::new));
	}

	protected boolean inScope(PropertyMetadata propertyMetadata, JRElementGroup container)
	{
		if (container instanceof JRFrame)
		{
			return inScope(propertyMetadata, (JRElement) container);
		}
		
		List<PropertyScope> scopes = propertyMetadata.getScopes();
		
		if (container instanceof JRBand)
		{
			return scopes.contains(PropertyScope.BAND);
		}
		
		if (container instanceof Cell)
		{
			return scopes.contains(PropertyScope.TABLE_CELL);
		}
		
		if (container instanceof JRCellContents)
		{
			return scopes.contains(PropertyScope.CROSSTAB_CELL);
		}
		
		return false;
	}

	public List<PropertyMetadata> getScriptletProperties(String scriptletClassName) throws JRException
	{
		Class<?> scriptletClass;
		try
		{
			scriptletClass = JRClassLoader.loadClassForName(scriptletClassName);
		}
		catch (ClassNotFoundException e)
		{
			throw new JRException("Could not load scriptlet class " + scriptletClassName, e);
		}

		Stream<PropertyMetadata> properties = Stream.empty();
		do
		{
			String qualification = scriptletClass.getName();
			Stream<PropertyMetadata> qualifiedProperties = qualifiedProperties(PropertyScope.SCRIPTLET, qualification);
			properties = Stream.concat(properties, qualifiedProperties);
			
			scriptletClass = scriptletClass.getSuperclass();
		}
		while (scriptletClass != null && JRAbstractScriptlet.class.isAssignableFrom(scriptletClass));
		return properties.collect(Collectors.toList());
	}

}
