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
package net.sf.jasperreports.engine.design;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.DatasetPropertyExpression;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseReport;
import net.sf.jasperreports.engine.design.events.PropagationChangeListener;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRVisitorSupport;


/**
 * JasperDesign is used for in-memory representation of a report design. 
 * <p>
 * The class contains all report properties and report elements inherited from the 
 * {@link net.sf.jasperreports.engine.JRReport} interface
 * in their design time state. 
 * </p><p>
 * All its instances are subject to compilation before being used for filling and report 
 * generation. These instances are the raw material that the JasperReports library uses to generate 
 * reports. Such instances are usually obtained:</p>
 * <ul>
 * <li>by parsing the JRXML report template files 
 * using the library's internal XML-parsing utility classes.</li>
 * <li>by applications that use JasperReports through API calls if 
 * working with JRXML files is not an option.</li>
 * </ul>
 * <p>
 * The first option for creating report designs relies on editing the JRXML files and using them 
 * with the {@link net.sf.jasperreports.engine.JasperCompileManager} in order to prepare them
 * for filling with data. Because they are well structured and are validated against a public
 * XSD when parsed, these files can be easily edited using simple editors or specialized
 * XML editors.
 * </p><p>
 * The second option is recommended only in case the parent application that uses
 * JasperReports inside the reporting module needs to create report templates at runtime. In
 * most cases this is not needed because the report templates do not need to change with
 * every report execution, and hence static report templates could be used. Only the data
 * used to fill these static report templates is dynamic.
 * </p><p>
 * However, there are cases when the actual report template is the result of some user input.
 * The parent application might supply its users with a set of options when launching the
 * reports that might take the form of some simplified report designer or wizard. In such
 * cases, the actual report layout is not known or is not complete at design time, and can
 * only be put together after the user's input is received.
 * </p><p>
 * The most common use case scenario that requires dynamically built or ad hoc report
 * templates (as we call them) is one in which the columns that are going to be present in a
 * table-like report layout are not known at design time. Instead, the user will give the
 * number of columns and their order inside the desired report at runtime.
 * </p><p>
 * Developers have to make sure that the applications they create really need ad hoc reports
 * and cannot rely solely on static report templates. Since dynamically built report
 * templates have to be compiled on the fly at runtime, they can result in a certain loss of
 * performance.
 * </p>
 *
 * @see net.sf.jasperreports.engine.JRReport
 * @see net.sf.jasperreports.engine.base.JRBaseReport
 * @see net.sf.jasperreports.engine.xml.JRXmlLoader
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JasperDesign extends JRBaseReport
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_DATASET = "design.duplicate.dataset";
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_REPORT_STYLE = "design.duplicate.report.style";

	public static final String PROPERTY_BACKGROUND = "background";

	public static final String PROPERTY_BOTTOM_MARGIN = "bottomMargin";

	public static final String PROPERTY_COLUMN_COUNT = "columnCount";

	public static final String PROPERTY_COLUMN_FOOTER = "columnFooter";

	public static final String PROPERTY_COLUMN_HEADER = "columnHeader";

	public static final String PROPERTY_COLUMN_SPACING = "columnSpacing";

	public static final String PROPERTY_COLUMN_WIDTH = "columnWidth";

	public static final String PROPERTY_DATASETS = "datasets";

	public static final String PROPERTY_DEFAULT_FONT = "defaultFont";

	public static final String PROPERTY_DEFAULT_STLYE = "defaultStyle";

	public static final String PROPERTY_DETAIL = "detail";

	public static final String PROPERTY_FLOAT_COLUMN_FOOTER = "isFloatColumnFooter";

	public static final String PROPERTY_FONTS = "fonts";

	public static final String PROPERTY_FORMAT_FACTORY_CLASS = "formatFactoryClass";

	public static final String PROPERTY_IGNORE_PAGINATION = "isIgnorePagination";

	public static final String PROPERTY_IMPORTS = "imports";

	public static final String PROPERTY_LANGUAGE = "language";

	public static final String PROPERTY_LAST_PAGE_FOOTER = "lastPageFooter";

	public static final String PROPERTY_LEFT_MARGIN = "leftMargin";

	public static final String PROPERTY_MAIN_DATASET = "mainDataset";
	
	public static final String PROPERTY_NAME = "name";

	public static final String PROPERTY_NO_DATA = "noData";

	public static final String PROPERTY_ORIENTATION = "orientation";

	public static final String PROPERTY_PAGE_FOOTER = "pageFooter";

	public static final String PROPERTY_PAGE_HEADER = "pageHeader";

	public static final String PROPERTY_PAGE_HEIGHT = "pageHeight";

	public static final String PROPERTY_PAGE_WIDTH = "pageWidth";

	public static final String PROPERTY_PRINT_ORDER = "printOrder";

	public static final String PROPERTY_COLUMN_DIRECTION = "columnDirection";

	public static final String PROPERTY_RIGHT_MARGIN = "rightMargin";

	public static final String PROPERTY_STYLES = "styles";

	public static final String PROPERTY_SUMMARY = "summary";

	public static final String PROPERTY_SUMMARY_NEW_PAGE = "isSummaryNewPage";

	public static final String PROPERTY_SUMMARY_WITH_PAGE_HEADER_AND_FOOTER = "isSummaryWithPageHeaderAndFooter";

	public static final String PROPERTY_TEMPLATES = "templates";

	public static final String PROPERTY_TITLE = "title";

	public static final String PROPERTY_TITLE_NEW_PAGE = "isTitleNewPage";

	public static final String PROPERTY_TOP_MARGIN = "topMargin";
	
	private static final ThreadLocal<JasperReportsContext> THREAD_JASPER_REPORTS_CONTEXT_INSTANCE = new ThreadLocal<JasperReportsContext>();
	private static final ThreadLocal<JasperDesign> THREAD_INSTANCE = new ThreadLocal<JasperDesign>();

	/**
	 * Report templates.
	 */
	private List<JRReportTemplate> templateList = new ArrayList<>();

	private Map<String, JRStyle> stylesMap = new HashMap<>();
	private List<JRStyle> stylesList = new ArrayList<>();

	/**
	 * Main report dataset.
	 */
	private JRDesignDataset mainDesignDataset;

	/**
	 * Report sub datasets indexed by name.
	 */
	private Map<String, JRDataset> datasetMap = new HashMap<>();
	private List<JRDataset> datasetList = new ArrayList<>();
	
	/**
	 * Constructs a JasperDesign object and fills it with the default variables and parameters.
	 */
	@JsonCreator // not actually needed; used for clarity
	public JasperDesign()
	{
		this(
			THREAD_JASPER_REPORTS_CONTEXT_INSTANCE.get() == null
			? DefaultJasperReportsContext.getInstance()
			: THREAD_JASPER_REPORTS_CONTEXT_INSTANCE.get()
			);
		THREAD_INSTANCE.set(this);
	}

	public JasperDesign(JasperReportsContext context)
	{
		setMainDataset(new JRDesignDataset(context, true));
		
		detailSection = new JRDesignSection(new JROrigin(BandTypeEnum.DETAIL));
	}
	
	
	/**
	 * 
	 */
	public static void setThreadJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		THREAD_JASPER_REPORTS_CONTEXT_INSTANCE.set(jasperReportsContext);
	}

	/**
	 * 
	 */
	public static void removeThreadJasperReportsContext()
	{
		THREAD_JASPER_REPORTS_CONTEXT_INSTANCE.remove();
	}

	/**
	 * 
	 */
	public static JasperDesign getThreadInstance()
	{
		return THREAD_INSTANCE.get();
	}

	/**
	 * 
	 */
	public static void removeThreadInstance()
	{
		THREAD_INSTANCE.remove();
	}


	/**
	 * Sets the report name. It is strongly recommended that the report name matches the .jrxml file name, since report compilers
	 * usually use this name for the compiled .jasper file.
	 */
	public void setName(String name)
	{
		Object old = this.name;
		this.name = name;
		this.mainDesignDataset.setName(name);
		getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
	}


	/**
	 * Specifies the language used for report expressions (Java or Groovy). The default is Java.
	 */
	public void setLanguage(String language)
	{
		Object old = this.language;
		this.language = language;
		getEventSupport().firePropertyChange(PROPERTY_LANGUAGE, old, this.language);
	}


	/**
	 * Specifies the number of report columns.
	 */
	public void setColumnCount(Integer columnCount)
	{
		Integer old = this.columnCount;
		this.columnCount = columnCount;
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_COUNT, old, this.columnCount);
	}


	/**
	 * Sets the print order. In case of multiple column reports, the engine can perform vertical or horizontal fill.
	 * @see net.sf.jasperreports.engine.type.PrintOrderEnum VERTICAL,
	 * @see net.sf.jasperreports.engine.type.PrintOrderEnum HORIZONTAL
	 */
	public void setPrintOrder(PrintOrderEnum printOrder)
	{
		Object old = this.printOrder;
		this.printOrder = printOrder;
		getEventSupport().firePropertyChange(PROPERTY_PRINT_ORDER, old, this.printOrder);
	}


	/**
	 * Sets the column direction.
	 */
	public void setColumnDirection(RunDirectionEnum columnDirection)
	{
		Object old = this.columnDirection;
		this.columnDirection = columnDirection;
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_DIRECTION, old, this.columnDirection);
	}


	/**
	 * Sets page width (including margins etc.). Default is 595.
	 */
	public void setPageWidth(int pageWidth)
	{
		int old = this.pageWidth;
		this.pageWidth = pageWidth;
		getEventSupport().firePropertyChange(PROPERTY_PAGE_WIDTH, old, this.pageWidth);
	}


	/**
	 * Sets page height (including margins etc.). Default is 842.
	 */
	public void setPageHeight(int pageHeight)
	{
		int old = this.pageHeight;
		this.pageHeight = pageHeight;
		getEventSupport().firePropertyChange(PROPERTY_PAGE_HEIGHT, old, this.pageHeight);
	}


	/**
	 * Sets the report orientation.
	 * @see net.sf.jasperreports.engine.JRReport ORIENTATION_PORTRAIT,
	 * @see net.sf.jasperreports.engine.JRReport ORIENTATION_LANDSCAPE
	 */
	public void setOrientation(OrientationEnum orientation)
	{
		Object old = this.orientation;
		this.orientation = orientation;
		getEventSupport().firePropertyChange(PROPERTY_ORIENTATION, old, this.orientation);
	}


	/**
	 * Sets the column width.
	 */
	public void setColumnWidth(int columnWidth)
	{
		int old = this.columnWidth;
		this.columnWidth = columnWidth;
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_WIDTH, old, this.columnWidth);
	}


	/**
	 * Sets the spacing between columns.
	 */
	public void setColumnSpacing(int columnSpacing)
	{
		int old = this.columnSpacing;
		this.columnSpacing = columnSpacing;
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_SPACING, old, this.columnSpacing);
	}


	/**
	 * Sets the left margin. The working space is calculated by subtracting the margins from the page width.
	 */
	public void setLeftMargin(int leftMargin)
	{
		int old = this.leftMargin;
		this.leftMargin = leftMargin;
		getEventSupport().firePropertyChange(PROPERTY_LEFT_MARGIN, old, this.leftMargin);
	}


	/**
	 * Sets the right margin. The working space is calculated by subtracting the margins from the page width.
	 */
	public void setRightMargin(int rightMargin)
	{
		int old = this.rightMargin;
		this.rightMargin = rightMargin;
		getEventSupport().firePropertyChange(PROPERTY_RIGHT_MARGIN, old, this.rightMargin);
	}


	/**
	 * Sets the top margin. The working space is calculated by subtracting the margins from the page height.
	 */
	public void setTopMargin(int topMargin)
	{
		int old = this.topMargin;
		this.topMargin = topMargin;
		getEventSupport().firePropertyChange(PROPERTY_TOP_MARGIN, old, this.topMargin);
	}


	/**
	 * Sets the top margin. The working space is calculated by subtracting the margins from the page height.
	 */
	public void setBottomMargin(int bottomMargin)
	{
		int old = this.bottomMargin;
		this.bottomMargin = bottomMargin;
		getEventSupport().firePropertyChange(PROPERTY_BOTTOM_MARGIN, old, this.bottomMargin);
	}


	/**
	 * Sets the background band.
	 */
	public void setBackground(JRBand background)
	{
		Object old = this.background;
		this.background = background;
		setBandOrigin(this.background, BandTypeEnum.BACKGROUND);
		getEventSupport().firePropertyChange(PROPERTY_BACKGROUND, old, this.background);
	}


	/**
	 * Sets the title band.
	 */
	public void setTitle(JRBand title)
	{
		Object old = this.title;
		this.title = title;
		setBandOrigin(this.title, BandTypeEnum.TITLE);
		getEventSupport().firePropertyChange(PROPERTY_TITLE, old, this.title);
	}


	/**
	 * Flag used to specify if the title section should be printed on a separate initial page.
	 *
	 * @param isTitleNewPage true if the title section should be displayed on a separate initial page, false if
	 * it will be displayed on the first page along with other sections.
	 */
	public void setTitleNewPage(boolean isTitleNewPage)
	{
		boolean old = this.isTitleNewPage;
		this.isTitleNewPage = isTitleNewPage;
		getEventSupport().firePropertyChange(PROPERTY_TITLE_NEW_PAGE, old, this.isTitleNewPage);
	}


	/**
	 * Sets the summary band.
	 */
	public void setSummary(JRBand summary)
	{
		Object old = this.summary;
		this.summary = summary;
		setBandOrigin(this.summary, BandTypeEnum.SUMMARY);
		getEventSupport().firePropertyChange(PROPERTY_SUMMARY, old, this.summary);
	}

	/**
	 * Sets the noData band.
	 */
	public void setNoData(JRBand noData)
	{
		Object old = this.noData;
		this.noData = noData;
		setBandOrigin(this.noData, BandTypeEnum.NO_DATA);
		getEventSupport().firePropertyChange(PROPERTY_NO_DATA, old, this.noData);
	}


	/**
	 * Flag used to specify if the summary section should be printed on a separate last page.
	 *
	 * @param isSummaryNewPage true if the summary section should be displayed on a separate last page, false if
	 * it will be displayed on the last page along with other sections, if there is enough space.
	 */
	public void setSummaryNewPage(boolean isSummaryNewPage)
	{
		boolean old = this.isSummaryNewPage;
		this.isSummaryNewPage = isSummaryNewPage;
		getEventSupport().firePropertyChange(PROPERTY_SUMMARY_NEW_PAGE, old, this.isSummaryNewPage);
	}


	/**
	 * Flag used to specify if the summary section should be accompanied by the page header and footer.
	 *
	 * @param isSummaryWithPageHeaderAndFooter true if the summary section should be displayed on pages that have the page header and footer, 
	 * false if it will be displayed on pages without header and footer.
	 */
	public void setSummaryWithPageHeaderAndFooter(boolean isSummaryWithPageHeaderAndFooter)
	{
		boolean old = this.isSummaryWithPageHeaderAndFooter;
		this.isSummaryWithPageHeaderAndFooter = isSummaryWithPageHeaderAndFooter;
		getEventSupport().firePropertyChange(PROPERTY_SUMMARY_WITH_PAGE_HEADER_AND_FOOTER, old, this.isSummaryWithPageHeaderAndFooter);
	}


	/**
	 * Flag used to specify if the column footer section should be printed at the bottom of the column or if it
	 * should immediately follow the last detail or group footer printed on the current column.
	 */
	public void setFloatColumnFooter(boolean isFloatColumnFooter)
	{
		boolean old = this.isFloatColumnFooter;
		this.isFloatColumnFooter = isFloatColumnFooter;
		getEventSupport().firePropertyChange(PROPERTY_FLOAT_COLUMN_FOOTER, old, this.isFloatColumnFooter);
	}


	/**
	 * Sets the page header band.
	 */
	public void setPageHeader(JRBand pageHeader)
	{
		Object old = this.pageHeader;
		this.pageHeader = pageHeader;
		setBandOrigin(this.pageHeader, BandTypeEnum.PAGE_HEADER);
		getEventSupport().firePropertyChange(PROPERTY_PAGE_HEADER, old, this.pageHeader);
	}


	/**
	 * Sets the page footer band.
	 */
	public void setPageFooter(JRBand pageFooter)
	{
		Object old = this.pageFooter;
		this.pageFooter = pageFooter;
		setBandOrigin(this.pageFooter, BandTypeEnum.PAGE_FOOTER);
		getEventSupport().firePropertyChange(PROPERTY_PAGE_FOOTER, old, this.pageFooter);
	}


	/**
	 * Sets the last page footer band.
	 */
	public void setLastPageFooter(JRBand lastPageFooter)
	{
		Object old = this.lastPageFooter;
		this.lastPageFooter = lastPageFooter;
		setBandOrigin(this.lastPageFooter, BandTypeEnum.LAST_PAGE_FOOTER);
		getEventSupport().firePropertyChange(PROPERTY_LAST_PAGE_FOOTER, old, this.lastPageFooter);
	}


	/**
	 * Sets the column header band.
	 */
	public void setColumnHeader(JRBand columnHeader)
	{
		Object old = this.columnHeader;
		this.columnHeader = columnHeader;
		setBandOrigin(this.columnHeader, BandTypeEnum.COLUMN_HEADER);
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_HEADER, old, this.columnHeader);
	}


	/**
	 * Sets the column footer band.
	 */
	public void setColumnFooter(JRBand columnFooter)
	{
		Object old = this.columnFooter;
		this.columnFooter = columnFooter;
		setBandOrigin(this.columnFooter, BandTypeEnum.COLUMN_FOOTER);
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_FOOTER, old, this.columnFooter);
	}


	/**
	 *
	 */
	public void setScriptletClass(String scriptletClass)
	{
		mainDesignDataset.setScriptletClass(scriptletClass);
	}


	/**
	 *
	 */
	public void setFormatFactoryClass(String formatFactoryClass)
	{
		Object old = this.formatFactoryClass;
		this.formatFactoryClass = formatFactoryClass;
		getEventSupport().firePropertyChange(PROPERTY_FORMAT_FACTORY_CLASS, old, this.formatFactoryClass);
	}


	/**
	 * Sets the base name of the report associated resource bundle.
	 */
	public void setResourceBundle(String resourceBundle)
	{
		mainDesignDataset.setResourceBundle(resourceBundle);
	}


	/**
	 * Adds an import (needed if report expression require additional classes in order to compile).
	 */
	public void addImport(String value)
	{
		if (importsSet == null)
		{
			importsSet = new HashSet<String>();//FIXME maintain order
		}
		
		if (importsSet.add(value))
		{
			getEventSupport().fireCollectionElementAddedEvent(PROPERTY_IMPORTS, value, importsSet.size() - 1);
		}
	}

	
	@JsonSetter
	private void setImports(List<String> imports)
	{
		if (imports != null)
		{
			for (String imp : imports)
			{
				addImport(imp);
			}
		}
	}


	/**
	 * Removes an import.
	 */
	public void removeImport(String value)
	{
		if (importsSet != null)
		{
			if (importsSet.remove(value))
			{
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_IMPORTS, value, -1);
			}
		}
	}


	/**
	 *
	 */
	public void resetDefaultStyle()
	{
		for (int i = stylesList.size() - 1; i >= 0; i--)
		{
			JRStyle style = stylesList.get(i);
			if (style.isDefault())
			{
				setDefaultStyle(style);
				return;
			}
		}
		setDefaultStyle(null);
	}


	/**
	 *
	 */
	public void setDefaultStyle(JRStyle style)
	{
		Object old = this.defaultStyle;
		this.defaultStyle = style;
		getEventSupport().firePropertyChange(PROPERTY_DEFAULT_STLYE, old, this.defaultStyle);
	}


	/**
	 * Gets an array of report level styles. These styles can be referenced by report elements.
	 */
	@Override
	public JRStyle[] getStyles()
	{
		JRStyle[] stylesArray = new JRStyle[stylesList.size()];

		stylesList.toArray(stylesArray);

		return stylesArray;
	}


	/**
	 * Gets a list of report level styles. These styles can be referenced by report elements.
	 */
	@JsonIgnore
	public List<JRStyle> getStylesList()
	{
		return stylesList;
	}


	@JsonSetter
	private void setStyles(List<JRDesignStyle> styles) throws JRException
	{
		if (styles != null)
		{
			for (JRDesignStyle style : styles)
			{
				String styleName = style.getStyleNameReference();
				if (styleName != null)
				{
					Map<String,JRStyle> stylesMap = getStylesMap();

					if (stylesMap.containsKey(styleName))
					{
						JRStyle parent = stylesMap.get(styleName);
						style.setParentStyle(parent);
						style.setParentStyleNameReference(null);
					}
				}
				addStyle(style);
			}
		}
	}


	/**
	 *
	 */
	@JsonIgnore
	public Map<String, JRStyle> getStylesMap()
	{
		return stylesMap;
	}


	/**
	 * Adds a report style, that can be referenced by report elements.
	 */
	public void addStyle(JRStyle style) throws JRException
	{
		addStyle(stylesList.size(), style);
	}


	/**
	 * Inserts a report style, that can be referenced by report elements, at specified position.
	 */
	public void addStyle(int index, JRStyle style) throws JRException
	{
		if (stylesMap.containsKey(style.getName()))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_REPORT_STYLE,
					new Object[]{style.getName()});
		}

		stylesList.add(index, style);
		stylesMap.put(style.getName(), style);

		if (style.isDefault())
		{
			resetDefaultStyle();
		}
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_STYLES, style, index);
	}


	/**
	 * Removes a report style from the list, based on the style name.
	 */
	public JRStyle removeStyle(String styleName)
	{
		return removeStyle(stylesMap.get(styleName));
	}


	/**
	 * Removes a report style from the list.
	 */
	public JRStyle removeStyle(JRStyle style)
	{
		if (style != null)
		{
			if (style.isDefault())
			{
				resetDefaultStyle();
			}

			int idx = stylesList.indexOf(style);
			if (idx >= 0)
			{
				stylesList.remove(idx);
				stylesMap.remove(style.getName());
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_STYLES, style, idx);
			}
		}

		return style;
	}


	/**
	 * Gets a list of report scriptlets (excluding the one specified by scriptletClass).
	 */
	@JsonIgnore
	public List<JRScriptlet> getScriptletsList()
	{
		return mainDesignDataset.getScriptletsList();
	}


	@JsonSetter
	private void setScriptlets(List<JRScriptlet> scriptlets) throws JRException
	{
		mainDesignDataset.setScriptlets(scriptlets);
	}

	
	/**
	 * Gets a map of report scriptlets (excluding the one specified by scriptletClass).
	 */
	@JsonIgnore
	public Map<String, JRScriptlet> getScriptletsMap()
	{
		return mainDesignDataset.getScriptletsMap();
	}


	/**
	 * Adds a report scriplet.
	 */
	public void addScriptlet(JRScriptlet scriptlet) throws JRException
	{
		mainDesignDataset.addScriptlet(scriptlet);
	}


	/**
	 * Removes a report scriptlet, based on its name.
	 */
	public JRScriptlet removeScriptlet(String scriptletName)
	{
		return mainDesignDataset.removeScriptlet(scriptletName);
	}


	@JsonSetter
	private void setPropertyExpressions(List<DatasetPropertyExpression> properties) throws JRException
	{
		mainDesignDataset.setPropertyExpressions(properties);
	}


	/**
	 *
	 */
	public void addPropertyExpression(DatasetPropertyExpression propertyExpression)
	{
		mainDesignDataset.addPropertyExpression(propertyExpression);
	}


	/**
	 *
	 */
	public void removePropertyExpression(DatasetPropertyExpression propertyExpression)
	{
		mainDesignDataset.removePropertyExpression(propertyExpression);
	}

	
	/**
	 *
	 */
	public DatasetPropertyExpression removePropertyExpression(String name)
	{
		return mainDesignDataset.removePropertyExpression(name);
	}

	
	/**
	 *
	 */
	@JsonIgnore
	public List<DatasetPropertyExpression> getPropertyExpressionsList()
	{
		return mainDesignDataset.getPropertyExpressionsList();
	}


	/**
	 * Removes a report scriptlet.
	 */
	public JRScriptlet removeScriptlet(JRScriptlet scriptlet)
	{
		return mainDesignDataset.removeScriptlet(scriptlet);
	}


	/**
	 * Gets a list of report parameters (including built-in ones).
	 */
	@JsonIgnore
	public List<JRParameter> getParametersList()
	{
		return mainDesignDataset.getParametersList();
	}


	@JsonSetter
	private void setParameters(List<JRParameter> parameters) throws JRException
	{
		mainDesignDataset.setParameters(parameters);
	}


	/**
	 * Gets a map of report parameters (including built-in ones).
	 */
	@JsonIgnore
	public Map<String, JRParameter> getParametersMap()
	{
		return mainDesignDataset.getParametersMap();
	}


	/**
	 * Adds a report parameter.
	 */
	public void addParameter(JRParameter parameter) throws JRException
	{
		mainDesignDataset.addParameter(parameter);
	}


	/**
	 * Removes a report parameter, based on its name.
	 */
	public JRParameter removeParameter(String parameterName)
	{
		return mainDesignDataset.removeParameter(parameterName);
	}


	/**
	 * Removes a report parameter.
	 */
	public JRParameter removeParameter(JRParameter parameter)
	{
		return mainDesignDataset.removeParameter(parameter);
	}


	/**
	 * Specifies the report query.
	 */
	public void setQuery(JRDesignQuery query)
	{
		mainDesignDataset.setQuery(query);
	}


	/**
	 * Gets a list of report fields.
	 */
	@JsonIgnore
	public List<JRField> getFieldsList()
	{
		return mainDesignDataset.getFieldsList();
	}


	@JsonSetter
	private void setFields(List<JRField> fields) throws JRException
	{
		mainDesignDataset.setFields(fields);
	}


	/**
	 * Gets a map of report fields.
	 */
	@JsonIgnore
	public Map<String, JRField> getFieldsMap()
	{
		return mainDesignDataset.getFieldsMap();
	}


	/**
	 *
	 */
	public void addField(JRField field) throws JRException
	{
		mainDesignDataset.addField(field);
	}


	/**
	 *
	 */
	public JRField removeField(String fieldName)
	{
		return mainDesignDataset.removeField(fieldName);
	}


	/**
	 *
	 */
	public JRField removeField(JRField field)
	{
		return mainDesignDataset.removeField(field);
	}


	/**
	 * Gets a list of sort report fields.
	 */
	@JsonIgnore
	public List<JRSortField> getSortFieldsList()
	{
		return mainDesignDataset.getSortFieldsList();
	}


	@JsonSetter
	private void setSortFields(List<JRSortField> sortFields) throws JRException
	{
		mainDesignDataset.setSortFields(sortFields);
	}


	/**
	 *
	 */
	public void addSortField(JRSortField sortField) throws JRException
	{
		mainDesignDataset.addSortField(sortField);
	}


	/**
	 *
	 */
	public JRSortField removeSortField(JRSortField sortField)
	{
		return mainDesignDataset.removeSortField(sortField);
	}


	/**
	 * Gets a list of report variables.
	 */
	@JsonIgnore
	public List<JRVariable> getVariablesList()
	{
		return mainDesignDataset.getVariablesList();
	}


	@JsonSetter
	private void setVariables(List<JRDesignVariable> variables) throws JRException
	{
		mainDesignDataset.setVariables(variables);
	}


	/**
	 * Gets a map of report variables.
	 */
	@JsonIgnore
	public Map<String, JRVariable> getVariablesMap()
	{
		return mainDesignDataset.getVariablesMap();
	}


	/**
	 *
	 */
	public void addVariable(JRDesignVariable variable) throws JRException
	{
		mainDesignDataset.addVariable(variable);
	}


	/**
	 *
	 */
	public JRVariable removeVariable(String variableName)
	{
		return mainDesignDataset.removeVariable(variableName);
	}


	/**
	 *
	 */
	public JRVariable removeVariable(JRVariable variable)
	{
		return mainDesignDataset.removeVariable(variable);
	}


	/**
	 * Gets an array of report groups.
	 */
	@JsonIgnore
	public List<JRGroup> getGroupsList()
	{
		return mainDesignDataset.getGroupsList();
	}


	@JsonSetter
	private void setGroups(List<JRDesignGroup> groups) throws JRException
	{
		mainDesignDataset.setGroups(groups);
	}


	/**
	 * Gets a list of report groups.
	 */
	@JsonIgnore
	public Map<String, JRGroup> getGroupsMap()
	{
		return mainDesignDataset.getGroupsMap();
	}


	/**
	 * Gets a map of report groups.
	 */
	public void addGroup(JRDesignGroup group) throws JRException
	{
		mainDesignDataset.addGroup(group);
	}


	/**
	 * Removes a new group from the report design.
	 */
	public JRGroup removeGroup(String groupName)
	{
		return mainDesignDataset.removeGroup(groupName);
	}


	/**
	 *
	 */
	public JRGroup removeGroup(JRGroup group)
	{
		return mainDesignDataset.removeGroup(group);
	}


	/**
	 * Returns a collection of all report expressions.
	 */
	@JsonIgnore
	public Collection<JRExpression> getExpressions()
	{
		return JRExpressionCollector.collectExpressions(DefaultJasperReportsContext.getInstance(), this);
	}


	@Override
	public JRDataset[] getDatasets()
	{
		JRDataset[] datasetArray = new JRDataset[datasetList.size()];
		datasetList.toArray(datasetArray);
		return datasetArray;
	}


	@JsonSetter
	private void setDatasets(List<JRDesignDataset> datasets) throws JRException
	{
		if (datasets != null)
		{
			for (JRDesignDataset dataset : datasets)
			{
				addDataset(dataset);
			}
		}
	}


	/**
	 * Returns the list of report sub datasets.
	 *
	 * @return list of {@link JRDesignDataset JRDesignDataset} objects
	 */
	@JsonIgnore
	public List<JRDataset> getDatasetsList()
	{
		return datasetList;
	}


	/**
	 * Returns the sub datasets of the report indexed by name.
	 *
	 * @return the sub datasets of the report indexed by name
	 */
	@JsonIgnore
	public Map<String, JRDataset> getDatasetMap()
	{
		return datasetMap;
	}


	/**
	 * Adds a sub dataset to the report.
	 *
	 * @param dataset the dataset
	 * @throws JRException
	 */
	public void addDataset(JRDesignDataset dataset) throws JRException
	{
		addDataset(datasetList.size(), dataset);
	}


	/**
	 * Inserts a sub dataset at specified position into the report.
	 *
	 * @param index the position
	 * @param dataset the dataset
	 * @throws JRException
	 */
	public void addDataset(int index, JRDesignDataset dataset) throws JRException
	{
		if (datasetMap.containsKey(dataset.getName()))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_DATASET,
					new Object[]{dataset.getName()});
		}

		datasetList.add(index, dataset);
		datasetMap.put(dataset.getName(), dataset);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_DATASETS, dataset, index);
	}


	/**
	 * Removes a sub dataset from the report.
	 *
	 * @param datasetName the dataset name
	 * @return the removed dataset
	 */
	public JRDataset removeDataset(String datasetName)
	{
		return removeDataset(datasetMap.get(datasetName));
	}


	/**
	 * Removes a sub dataset from the report.
	 *
	 * @param dataset the dataset to be removed
	 * @return the dataset
	 */
	public JRDataset removeDataset(JRDataset dataset)
	{
		if (dataset != null)
		{
			int idx = datasetList.indexOf(dataset);
			if (idx >= 0)
			{
				datasetList.remove(idx);
				datasetMap.remove(dataset.getName());
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_DATASETS, dataset, idx);
			}
		}

		return dataset;
	}


	/**
	 * Returns the main report dataset.
	 *
	 * @return the main report dataset
	 */
	@JsonIgnore
	public JRDesignDataset getMainDesignDataset()
	{
		return mainDesignDataset;
	}


	/**
	 * Sets the main report dataset.
	 * <p>
	 * This method can be used as an alternative to setting the parameters, fields, etc directly on the report.
	 *
	 * @param dataset the dataset
	 */
	public void setMainDataset(JRDesignDataset dataset)
	{
		Object old = this.background;
		this.mainDataset = this.mainDesignDataset = dataset;
		this.mainDesignDataset.setName(getName());
		this.mainDesignDataset.getEventSupport().addPropertyChangeListener(new PropagationChangeListener(getEventSupport()));
		getEventSupport().firePropertyChange(PROPERTY_MAIN_DATASET, old, this.mainDataset);
	}


	/**
	 * Performs preliminary processing and calculations prior to compilation.
	 */
	public void preprocess()
	{
		for (Iterator<JRCrosstab> it = getCrosstabs().iterator(); it.hasNext();)
		{
			JRDesignCrosstab crosstab = (JRDesignCrosstab) it.next();
			crosstab.preprocess();
		}
	}

	protected List<JRCrosstab> getCrosstabs()
	{
		final List<JRCrosstab> crosstabs = new ArrayList<>();
		JRElementsVisitor.visitReport(this, new JRVisitorSupport()
		{
			@Override
			public void visitCrosstab(JRCrosstab crosstab)
			{
				crosstabs.add(crosstab);
			}
		});
		return crosstabs;
	}


	/**
	 * Sets the value of the ignore pagination flag.
	 *
	 * @param ignorePagination whether to ignore pagination when generating the report
	 * @see net.sf.jasperreports.engine.JRReport#isIgnorePagination()
	 */
	public void setIgnorePagination(boolean ignorePagination)
	{
		boolean old = this.ignorePagination;
		this.ignorePagination = ignorePagination;
		getEventSupport().firePropertyChange(PROPERTY_IGNORE_PAGINATION, old, this.ignorePagination);
	}


	/**
	 * Returns the main dataset filter expression.
	 *
	 * @return the main dataset filter expression
	 * @see JRDataset#getFilterExpression()
	 */
	public JRExpression getFilterExpression()
	{
		return mainDesignDataset.getFilterExpression();
	}


	/**
	 * Sets the main dataset filter expression.
	 *
	 * @param expression the boolean expression to use as main dataset filter expression
	 * @see JRDesignDataset#setFilterExpression(JRExpression)
	 * @see JRDataset#getFilterExpression()
	 */
	public void setFilterExpression(JRExpression expression)
	{
		mainDesignDataset.setFilterExpression(expression);
	}

	/**
	 * Adds a report template.
	 *
	 * @param template the template to add.
	 * @see #getTemplates()
	 */
	public void addTemplate(JRReportTemplate template)
	{
		addTemplate(templateList.size(), template);
	}

	/**
	 * Inserts a report template at specified position.
	 *
	 * @param index the template position.
	 * @param template the template to insert.
	 * @see #getTemplates()
	 */
	public void addTemplate(int index, JRReportTemplate template)
	{
		templateList.add(index, template);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_TEMPLATES, template, index);
	}

	/**
	 * Removes a report template.
	 *
	 * @param template the template to remove
	 * @return <code>true</code> if and only if the template has been found and removed
	 */
	public boolean removeTemplate(JRReportTemplate template)
	{
		int idx = templateList.indexOf(template);
		if (idx >= 0)
		{
			templateList.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_TEMPLATES, template, idx);
			return true;
		}
		return false;
	}

	@Override
	public JRReportTemplate[] getTemplates()
	{
		return templateList.toArray(new JRReportTemplate[templateList.size()]);
	}

	/**
	 * Returns the list of report templates defined in the report.
	 * 
	 * @return the list of {@link JRReportTemplate} objects for the report
	 * @see #getTemplates()
	 * @see #addTemplate(JRReportTemplate)
	 */
	@JsonIgnore
	public List<JRReportTemplate> getTemplatesList()
	{
		return templateList;
	}
	
	@JsonSetter
	private void setTemplates(List<JRReportTemplate> templates)
	{
		if (templates != null)
		{
			for (JRReportTemplate template : templates)
			{
				addTemplate(template);
			}
		}
	}
	
	protected void setBandOrigin(JRBand band, BandTypeEnum type)
	{
		if (band instanceof JRDesignBand)
		{
			JROrigin origin = new JROrigin(type);
			((JRDesignBand) band).setOrigin(origin);
		}
	}
	
	protected void setSectionOrigin(JRSection section, BandTypeEnum type)
	{
		if (section instanceof JRDesignSection)
		{
			JROrigin origin = new JROrigin(type);
			((JRDesignSection) section).setOrigin(origin);
		}
	}
	
	/**
	 * Sets the unique identifier for the report.
	 * 
	 * @param uuid the identifier
	 */
	public void setUUID(UUID uuid)
	{
		mainDesignDataset.setUUID(uuid);
	}
	
	/**
	 * Determines whether the report has an existing unique identifier.
	 * 
	 * Note that when no existing identifier is set, {@link #getUUID()} would generate and return
	 * an identifier.
	 * 
	 * @return whether the report has an externally set unique identifier
	 * @see #setUUID(UUID)
	 */
	public boolean hasUUID()
	{
		return mainDesignDataset.hasUUID();
	}
	
}
