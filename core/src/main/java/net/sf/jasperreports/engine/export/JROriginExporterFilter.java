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
 * Greg Hilton 
 */

package net.sf.jasperreports.engine.export;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * An exporter filter that excludes elements based on their origin.
 * <p/>
 * In case no filter instance is passed to the exporter as the
 * {@link net.sf.jasperreports.export.ReportExportConfiguration#getExporterFilter()} exporter 
 * configuration, the exporter searches for some
 * configuration properties with a given prefix, both at report level (exporter hints) and
 * globally, in order to decide if a default exporter filter instance should be created on-the-fly
 * and used internally, when exporting the current document.
 * <p/>
 * If created, this default exporter filter will filter out content from the exported document
 * based on element origin information. Elements present in JasperReports generated
 * documents keep information about their origin. The origin of an element is defined by its
 * parent section in the initial report template, and optionally the name of the group and/or
 * subreport that the element originated from.
 * <p/>
 * Removing page headers and page footers from the document when exporting to XLS can
 * be achieved by putting these custom properties in the report template:
 * <pre>
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.band.1" value="pageHeader"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.band.2" value="pageFooter"/&gt;
 * </pre>
 * If you want to remove page headers and page footers, but keep the first page header in
 * place (useful when all pages are exported to the same sheet, in a flow layout) the
 * following properties have to be used in the report template:
 * <pre>
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.1" value="pageHeader"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.band.2" value="pageFooter"/&gt;
 * </pre>
 * Note that there is no property prefix available to keep the last occurrence of a band. If
 * you would want to keep the last page footer, then the best solution is to actually use the
 * <code>&lt;lastPageFooter&gt;</code> section of the report template.
 * <p/>
 * If you want to remove both page headers and page footers and also the group footers of a
 * group called <code>ProductGroup</code>, that comes from a subreport called <code>ProductReport</code>
 * these custom properties are needed:
 * <pre>
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.band.1" value="pageHeader"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.band.2" value="pageFooter"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.band.3" value="groupHeader"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.group.3" value="ProductGroup"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.report.3" value="ProductReport"/&gt;
 * </pre>
 * Note that the number at the end of the properties names is just an arbitrary suffix. The
 * only thing that counts is that the suffix be the same for properties referring to the same
 * filter. The last three properties in the above example define the filter that will exclude
 * group header of ProductGroup from ProductReport subreport. Instead of the
 * numeric suffix, you could put any suffix, as long as it does not coincide with suffixes
 * from other filters. The following example will exclude the same group header while
 * keeping its first occurrence:
 * <pre>
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.myGroupFilter" value="groupHeader"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.keep.first.group.myGroupFilter" value="ProductGroup"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.xls.exclude.origin.keep.first.report.myGroupFilter" value="ProductReport"/&gt;
 * </pre>
 * The <code>xls</code> token inside the properties prefixes refer to the particular export format that is
 * targeted and the general syntax of the origin exporter filter properties is:
 * <pre>
 *   net.sf.jasperreports.export.{format}.exclude.origin.{suffix}.{arbitrary_name}
 *   net.sf.jasperreports.export.{format}.exclude.origin.keep.first.{suffix}.{arbitrary_name}
 * </pre>
 * Other supported format tokens are <code>pdf</code>, <code>html</code>, <code>rtf</code>, <code>odt</code>, <code>xml</code>, 
 * <code>txt</code>, <code>csv</code> and <code>graphics2d</code>, while the only accepted suffixes are <code>band</code>, 
 * <code>group</code> and <code>report</code>.
 * <p/>
 * These properties make best sense when placed inside a report, to filter out specific
 * portions of that particular document, but they also work globally, if placed in the
 * <code>jasperreports.properties</code> file. This would allow removing the page headers
 * and page footers from all reports, when exporting to XLS, for example.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JROriginExporterFilter implements ResetableExporterFilter
{

	/**
	 * The prefix of origin exclusion properties.
	 */
	@Property(
			name = "net.sf.jasperreports.export.{format}.exclude.origin.{suffix}.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_3
			)
	public static final String PROPERTY_EXCLUDE_ORIGIN_PREFIX = "exclude.origin.";
	
	@Property(
			name = "net.sf.jasperreports.export.{format}.exclude.origin.keep.first.{suffix}.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_3
			)
	public static final String KEEP_FIRST_PREFIX = "keep.first.";
	private static final String BAND_PREFIX = "band.";
	private static final String GROUP_PREFIX = "group.";
	private static final String REPORT_PREFIX = "report.";
	
	private Map<JROrigin,Boolean> originsToExclude = new HashMap<>();
	private Map<Integer,JRPrintElement> firstOccurrences = new HashMap<>();
	private Map<JROrigin,Boolean> matchedOrigins = new HashMap<>();
	
	public void addOrigin(JROrigin origin)
	{
		addOrigin(origin, false);
	}
	
	public void addOrigin(JROrigin origin, boolean keepFirst)
	{
		originsToExclude.put(origin, keepFirst);
	}
	
	public void removeOrigin(JROrigin origin)
	{
		originsToExclude.remove(origin);
	}
	
	@Override
	public void reset()
	{
		firstOccurrences = new HashMap<>();
	}
	
	@Override
	public boolean isToExport(JRPrintElement element)
	{
		JROrigin origin = element.getOrigin();

		Boolean keepFirst = null;
		
		if (origin != null)
		{
			keepFirst = matchedOrigins.get(origin);
			
			if (keepFirst == null)
			{
				for (Entry<JROrigin, Boolean> entry : originsToExclude.entrySet())
				{
					JROrigin originToExclude = entry.getKey();
					if (match(originToExclude, origin))
					{
						keepFirst = entry.getValue();
						matchedOrigins.put(origin, keepFirst);
						break;
					}
				}
			}
		}
		
		boolean originMatched = keepFirst != null;

		return
			!originMatched 
			|| (keepFirst 
				&& isFirst(element));
	}
	
	public boolean match(JROrigin originToExclude, JROrigin origin)
	{
		String groupName1 = originToExclude.getGroupName();
		String reportName1 = originToExclude.getReportName();
		String groupName2 = origin.getGroupName();
		String reportName2 = origin.getReportName();
		return (
			originToExclude.getBandType() == origin.getBandType()
			&& (("*".equals(groupName1) && groupName2 != null) || (groupName1 == null ? groupName2 == null : groupName2 != null && groupName1.equals(groupName2)))
			&& (("*".equals(reportName1) && reportName2 != null) || (reportName1 == null ? reportName2 == null : reportName2 != null && reportName1.equals(reportName2)))
			);
	}
	
	private boolean isFirst(JRPrintElement element)
	{
		int elementId = element.getSourceElementId();
		if (elementId == JRPrintElement.UNSET_SOURCE_ELEMENT_ID)
		{
			// for some reason the element doesn't have a valid source Id.
			// do not exclude the element in that case.
			return true;
		}
		
		// FIXME this doesn't work well with batch exporting
		JRPrintElement firstElement = firstOccurrences.get(elementId);
		if (firstElement == null || firstElement == element)
		{
			firstOccurrences.put(elementId, element);
			return true;
		}
		
		// == does not work for virtualized elements, checking print Ids
		if (firstElement.getPrintElementId() != JRPrintElement.UNSET_PRINT_ELEMENT_ID
				&& firstElement.getPrintElementId() == element.getPrintElementId())
		{
			return true;
		}
		
		return false;
	}
	
	public static JROriginExporterFilter getFilter(
		JasperReportsContext jasperReportsContext,
		JRPropertiesMap propertiesMap, 
		String originFilterPrefix
		)
	{
		JROriginExporterFilter filter = null;
		
		filter = addOriginsToFilter(jasperReportsContext, filter, propertiesMap, originFilterPrefix, false);
		filter = addOriginsToFilter(jasperReportsContext, filter, propertiesMap, originFilterPrefix + KEEP_FIRST_PREFIX, true);
		
		return filter;
	}
	
	private static JROriginExporterFilter addOriginsToFilter(
		JasperReportsContext jasperReportsContext,
		JROriginExporterFilter filter, 
		JRPropertiesMap propertiesMap, 
		String originFilterPrefix,
		boolean keepFirst
		)
	{
		JRPropertiesUtil propUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
		List<PropertySuffix> properties = propUtil.getProperties(originFilterPrefix + BAND_PREFIX);
		properties.addAll(JRPropertiesUtil.getProperties(propertiesMap, originFilterPrefix + BAND_PREFIX));
		
		if (!properties.isEmpty())
		{
			filter = (filter == null ? new JROriginExporterFilter(): filter);
				
			for (Iterator<PropertySuffix> it = properties.iterator(); it.hasNext();)
			{
				PropertySuffix propertySuffix = it.next();
				String suffix = propertySuffix.getSuffix();
				String propValue = propUtil.getProperty(propertiesMap, propertySuffix.getKey());
				BandTypeEnum bandType = BandTypeEnum.getByName(propValue == null ? null : propValue.trim());
				if (bandType != null)
				{
					filter.addOrigin(
						new JROrigin(
							propUtil.getProperty(propertiesMap, originFilterPrefix + REPORT_PREFIX + suffix),
							propUtil.getProperty(propertiesMap, originFilterPrefix + GROUP_PREFIX + suffix),
							bandType
							),
						keepFirst
						);
				}
			}
		}
		
		return filter;
	}
	
}
