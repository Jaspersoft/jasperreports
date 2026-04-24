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
package net.sf.jasperreports.engine.base;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;

import net.sf.jasperreports.engine.DatasetPropertyExpression;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;
import net.sf.jasperreports.engine.util.StyleResolver;


/**
 * Base class that implements the {@link net.sf.jasperreports.engine.JRReport} interface.
 * 
 * @see net.sf.jasperreports.engine.JRReport
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * 
 */
public class JRBaseReport implements JRReport, Serializable, JRChangeEventsSupport
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_WHEN_NO_DATA_TYPE = "whenNoDataType";
	public static final String PROPERTY_SECTION_TYPE = "sectionType";

	/**
	 *
	 */
	protected String name;
	protected String language;
	protected Integer columnCount;
	protected PrintOrderEnum printOrder;
	protected RunDirectionEnum columnDirection;
	protected int pageWidth = 595;
	protected int pageHeight = 842;
	protected OrientationEnum orientation;
	protected WhenNoDataTypeEnum whenNoDataType;
	protected SectionTypeEnum sectionType;
	protected int columnWidth = 555;
	protected int columnSpacing;
	protected int leftMargin = 20;
	protected int rightMargin = 20;
	protected int topMargin = 30;
	protected int bottomMargin = 30;
	protected boolean isTitleNewPage;
	protected boolean isSummaryNewPage;
	protected boolean isSummaryWithPageHeaderAndFooter;
	protected boolean isFloatColumnFooter;
	protected boolean ignorePagination;//FIXMEBOOK remove default

	/**
	 *
	 */
	protected String formatFactoryClass;

	/**
	 *
	 */
	protected Set<String> importsSet;

	/**
	 * Report templates.
	 */
	protected JRReportTemplate[] templates;

	protected JRStyle defaultStyle;
	protected JRStyle[] styles;

	protected transient StyleResolver styleResolver = StyleResolver.getInstance();

	/**
	 * The main dataset of the report.
	 */
	protected JRDataset mainDataset;

	/**
	 * Sub datasets of the report.
	 */
	protected JRDataset[] datasets;

	protected JRBand background;
	protected JRBand title;
	protected JRBand pageHeader;
	protected JRBand columnHeader;
	protected JRSection detailSection;
	protected JRBand columnFooter;
	protected JRBand pageFooter;
	protected JRBand lastPageFooter;
	protected JRBand summary;
	protected JRBand noData;


	protected JRBaseReport()
	{
	}

	public JRBaseReport(JRReport report, JRExpressionCollector expressionCollector)
	{
		this(report, new JRBaseObjectFactory(expressionCollector));
	}
	
	/**
	 * Constructs a copy of a report.
	 */
	public JRBaseReport(JRReport report, JRBaseObjectFactory factory)
	{
		/*   */
		name = report.getName();
		language = report.getLanguage();
		columnCount = report.getColumnCount();
		printOrder = report.getPrintOrder();
		columnDirection = report.getColumnDirection();
		pageWidth = report.getPageWidth();
		pageHeight = report.getPageHeight();
		orientation = report.getOrientation();
		whenNoDataType = report.getWhenNoDataType();
		sectionType = report.getSectionType();
		columnWidth = report.getColumnWidth();
		columnSpacing = report.getColumnSpacing();
		leftMargin = report.getLeftMargin();
		rightMargin = report.getRightMargin();
		topMargin = report.getTopMargin();
		bottomMargin = report.getBottomMargin();
		isTitleNewPage = report.isTitleNewPage();
		isSummaryNewPage = report.isSummaryNewPage();
		isSummaryWithPageHeaderAndFooter = report.isSummaryWithPageHeaderAndFooter();
		isFloatColumnFooter = report.isFloatColumnFooter();
		ignorePagination = report.isIgnorePagination();

		formatFactoryClass = report.getFormatFactoryClass();

		/*   */
		String[] imports = report.getImports();
		if (imports != null && imports.length > 0)
		{
			importsSet = new HashSet<>(imports.length);
			importsSet.addAll(Arrays.asList(imports));
		}

		/*   */
		factory.setDefaultStyleProvider(this);

		copyTemplates(report, factory);

		/*   */
		defaultStyle = factory.getStyle(report.getDefaultStyle());

		/*   */
		JRStyle[] jrStyles = report.getStyles();
		if (jrStyles != null && jrStyles.length > 0)
		{
			styles = new JRStyle[jrStyles.length];
			for(int i = 0; i < styles.length; i++)
			{
				styles[i] = factory.getStyle(jrStyles[i]);
			}
		}

		mainDataset = factory.getDataset(report.getMainDataset());

		JRDataset[] datasetArray = report.getDatasets();
		if (datasetArray != null && datasetArray.length > 0)
		{
			datasets = new JRDataset[datasetArray.length];
			for (int i = 0; i < datasets.length; i++)
			{
				datasets[i] = factory.getDataset(datasetArray[i]);
			}
		}

		background = factory.getBand(report.getBackground());
		title = factory.getBand(report.getTitle());
		pageHeader = factory.getBand(report.getPageHeader());
		columnHeader = factory.getBand(report.getColumnHeader());
		detailSection = factory.getSection(report.getDetailSection());
		columnFooter = factory.getBand(report.getColumnFooter());
		pageFooter = factory.getBand(report.getPageFooter());
		lastPageFooter = factory.getBand(report.getLastPageFooter());
		summary = factory.getBand(report.getSummary());
		noData = factory.getBand(report.getNoData());
	}


	protected void copyTemplates(JRReport report, JRBaseObjectFactory factory)
	{
		JRReportTemplate[] reportTemplates = report.getTemplates();
		if (reportTemplates == null || reportTemplates.length == 0)
		{
			templates = null;
		}
		else
		{
			templates = new JRReportTemplate[reportTemplates.length];
			for (int i = 0; i < reportTemplates.length; i++)
			{
				templates[i] = factory.getReportTemplate(reportTemplates[i]);
			}
		}
	}

	public JRBaseReport(JRReport report)
	{
		this(report, (JRExpressionCollector) null);
	}


	/**
	 *
	 */
	public synchronized void setJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		styleResolver = new StyleResolver(jasperReportsContext);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getLanguage()
	{
		return language;
	}

	@Override
	public Integer getColumnCount()
	{
		return columnCount;
	}

	@Override
	public PrintOrderEnum getPrintOrder()
	{
		return printOrder;
	}

	@Override
	public RunDirectionEnum getColumnDirection()
	{
		return columnDirection;
	}

	@Override
	public int getPageWidth()
	{
		return pageWidth;
	}

	@Override
	public int getPageHeight()
	{
		return pageHeight;
	}

	@Override
	public OrientationEnum getOrientation()
	{
		return orientation;
	}

	@Override
	public WhenNoDataTypeEnum getWhenNoDataType()
	{
		return whenNoDataType;
	}

	@Override
	public void setWhenNoDataType(WhenNoDataTypeEnum whenNoDataType)
	{
		Object old = this.whenNoDataType;
		this.whenNoDataType = whenNoDataType;
		getEventSupport().firePropertyChange(PROPERTY_WHEN_NO_DATA_TYPE, old, whenNoDataType);
	}

	@Override
	public SectionTypeEnum getSectionType()
	{
		return sectionType;
	}

	/**
	 *
	 */
	public void setSectionType(SectionTypeEnum sectionType)
	{
		Object old = this.sectionType;
		this.sectionType = sectionType;
		getEventSupport().firePropertyChange(PROPERTY_SECTION_TYPE, old, sectionType);
	}

	@Override
	public int getColumnWidth()
	{
		return columnWidth;
	}

	@Override
	public int getColumnSpacing()
	{
		return columnSpacing;
	}

	@Override
	public int getLeftMargin()
	{
		return leftMargin;
	}

	@Override
	public int getRightMargin()
	{
		return rightMargin;
	}

	@Override
	public int getTopMargin()
	{
		return topMargin;
	}

	@Override
	public int getBottomMargin()
	{
		return bottomMargin;
	}

	@Override
	public boolean isTitleNewPage()
	{
		return isTitleNewPage;
	}

	@Override
	public boolean isSummaryNewPage()
	{
		return isSummaryNewPage;
	}

	@Override
	public boolean isSummaryWithPageHeaderAndFooter()
	{
		return isSummaryWithPageHeaderAndFooter;
	}

	@Override
	public boolean isFloatColumnFooter()
	{
		return isFloatColumnFooter;
	}

	@Override
	public String getScriptletClass()
	{
		return mainDataset.getScriptletClass();
	}

	@Override
	public String getFormatFactoryClass()
	{
		return formatFactoryClass;
	}

	@Override
	public String getResourceBundle()
	{
		return mainDataset.getResourceBundle();
	}

	@Override
	public String[] getPropertyNames()
	{
		return mainDataset.getPropertiesMap().getPropertyNames();
	}

	@Override
	public String getProperty(String propName)
	{
		return mainDataset.getPropertiesMap().getProperty(propName);
	}

	@Override
	public void setProperty(String propName, String value)
	{
		mainDataset.getPropertiesMap().setProperty(propName, value);
	}

	@Override
	public void removeProperty(String propName)
	{
		mainDataset.getPropertiesMap().removeProperty(propName);
	}

	@Override
	public DatasetPropertyExpression[] getPropertyExpressions()
	{
		return mainDataset.getPropertyExpressions();
	}

	@Override
	public String[] getImports()
	{
		if (importsSet != null)
		{
			return importsSet.toArray(new String[importsSet.size()]);
		}
		return null;
	}

	@Override
	public JRStyle getDefaultStyle()
	{
		return defaultStyle;
	}

	@Override
	public JRStyle[] getStyles()
	{
		return styles;
	}

	@Override
	public StyleResolver getStyleResolver()
	{
		return styleResolver;
	}

	/**
	 * Gets an array of report scriptlets (excluding the scriptletClass one).
	 */
	@Override
	public JRScriptlet[] getScriptlets()
	{
		return mainDataset.getScriptlets();
	}

	/**
	 * Gets an array of report parameters (including built-in ones).
	 */
	@Override
	public JRParameter[] getParameters()
	{
		return mainDataset.getParameters();
	}

	@Override
	public JRQuery getQuery()
	{
		return mainDataset.getQuery();
	}

	/**
	 *  Gets an array of report fields.
	 */
	@Override
	public JRField[] getFields()
	{
		return mainDataset.getFields();
	}

	/**
	 *  Gets an array of sort report fields.
	 */
	@Override
	public JRSortField[] getSortFields()
	{
		return mainDataset.getSortFields();
	}

	/**
	 * Gets an array of report variables.
	 */
	@Override
	public JRVariable[] getVariables()
	{
		return mainDataset.getVariables();
	}

	@Override
	public JRGroup[] getGroups()
	{
		return mainDataset.getGroups();
	}

	@Override
	public JRBand getBackground()
	{
		return background;
	}

	@Override
	public JRBand getTitle()
	{
		return title;
	}

	@Override
	public JRBand getPageHeader()
	{
		return pageHeader;
	}

	@Override
	public JRBand getColumnHeader()
	{
		return columnHeader;
	}

	@Override
	@JsonMerge
	public JRSection getDetailSection()
	{
		return detailSection;
	}

	@Override
	public JRBand getColumnFooter()
	{
		return columnFooter;
	}

	@Override
	public JRBand getPageFooter()
	{
		return pageFooter;
	}

	@Override
	public JRBand getLastPageFooter()
	{
		return lastPageFooter;
	}

	@Override
	public JRBand getSummary()
	{
		return summary;
	}


	@Override
	public WhenResourceMissingTypeEnum getWhenResourceMissingType()
	{
		return mainDataset.getWhenResourceMissingType();
	}

	@Override
	public void setWhenResourceMissingType(WhenResourceMissingTypeEnum whenResourceMissingType)
	{
		mainDataset.setWhenResourceMissingType(whenResourceMissingType);
	}


	@Override
	public JRDataset getMainDataset()
	{
		return mainDataset;
	}


	@Override
	public JRDataset[] getDatasets()
	{
		return datasets;
	}


	@Override
	public boolean isIgnorePagination()
	{
		return ignorePagination;
	}

	@Override
	public boolean hasProperties()
	{
		return mainDataset.hasProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return mainDataset.getPropertiesMap();
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

	@Override
	public JRReportTemplate[] getTemplates()
	{
		return templates;
	}

	/**
	 * @return the noData
	 */
	@Override
	public JRBand getNoData() {
		return noData;
	}
	
	/**
	 *
	 */
	@JsonIgnore
	public JRBand[] getAllBands()
	{
		List<JRBand> bands = new ArrayList<>();
		
		addBand(title, bands);
		addBand(pageHeader, bands);
		addBand(columnHeader, bands);

		JRGroup[] groups = mainDataset.getGroups();
		if (groups != null)
		{
			for (JRGroup group : groups)
			{
				addBands(group.getGroupHeaderSection(), bands);
				addBands(group.getGroupFooterSection(), bands);
			}
		}

		addBands(detailSection, bands);
		
		addBand(columnFooter, bands);
		addBand(pageFooter, bands);
		addBand(lastPageFooter, bands);
		addBand(summary, bands);
		addBand(noData, bands);
		
		return bands.toArray(new JRBand[bands.size()]);
	}

	/**
	 *
	 */
	private void addBand(JRBand band, List<JRBand> bands)
	{
		if (band != null)
		{
			bands.add(band);
		}
	}

	/**
	 *
	 */
	private void addBands(JRSection section, List<JRBand> bands)
	{
		if (section != null)
		{
			JRBand[] sectionBands = section.getBands();
			if (sectionBands != null)
			{
				for (JRBand band : sectionBands)
				{
					addBand(band, bands);
				}
			}
		}
	}

	@Override
	public UUID getUUID()
	{
		return mainDataset.getUUID();
	}
	
	private transient JRPropertyChangeSupport eventSupport;//FIXMECLONE cloneable for reset?
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		styleResolver = StyleResolver.getInstance();
	}
}
