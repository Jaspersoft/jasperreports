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
 * John Bindel - jbindel@users.sourceforge.net
 */

package net.sf.jasperreports.engine.fill;

import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleSetter;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.JRTemplateReference;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRVirtualPrintPage;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.PropertyEvaluationTimeEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;
import net.sf.jasperreports.engine.util.FormatUtils;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRStyledTextParser;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryResourceContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryResourceContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRBaseFiller extends BaseReportFiller implements JRDefaultStyleProvider
{

	private static final Log log = LogFactory.getLog(JRBaseFiller.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_INFINITE_LOOP_CREATING_NEW_PAGE = "fill.common.filler.infinite.loop.creating.new.page";
	public static final String EXCEPTION_MESSAGE_KEY_COLUMN_HEADER_OVERFLOW_INFINITE_LOOP = "fill.common.filler.column.header.overflow.infinite.loop";
	public static final String EXCEPTION_MESSAGE_KEY_CIRCULAR_DEPENDENCY_FOUND = "fill.base.filler.circular.dependency.found";
	public static final String EXCEPTION_MESSAGE_KEY_EXTERNAL_STYLE_NAME_NOT_SET = "fill.base.filler.external.style.name.not.set";
	public static final String EXCEPTION_MESSAGE_KEY_NO_SUCH_GROUP = "fill.base.filler.no.such.group";
	public static final String EXCEPTION_MESSAGE_KEY_PAGE_HEADER_OVERFLOW_INFINITE_LOOP = "fill.common.filler.page.header.overflow.infinite.loop";
	public static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_REPORT_SECTION_TYPE = "fill.base.filler.unsupported.report.section.type";
	public static final String EXCEPTION_MESSAGE_KEY_KEEP_TOGETHER_CONTENT_DOES_NOT_FIT = "fill.common.filler.keep.together.content.does.not.fit";
	
	private static final int PAGE_HEIGHT_PAGINATION_IGNORED = 0x7d000000;//less than Integer.MAX_VALUE to avoid 
	private static final int PAGE_WIDTH_IGNORED = 0x7d000000;
	
	private JRStyledTextParser styledTextParser = JRStyledTextParser.getInstance();

	/**
	 *
	 */
	protected String name;

	protected int columnCount;

	protected PrintOrderEnum printOrder;

	protected RunDirectionEnum columnDirection;

	protected int pageWidth;
	protected int maxPageWidth;

	protected int pageHeight;

	protected OrientationEnum orientation;

	private WhenNoDataTypeEnum whenNoDataType;

	protected int columnWidth;

	protected int columnSpacing;

	protected int leftMargin;

	protected int rightMargin;

	protected int topMargin;

	protected int bottomMargin;

	protected boolean isTitleNewPage;

	protected boolean isSummaryNewPage;

	protected boolean isSummaryWithPageHeaderAndFooter;

	protected boolean isFloatColumnFooter;

	protected JRFillReportTemplate[] reportTemplates;
	
	protected List<ReportTemplateSource> templates;

	protected JRStyle defaultStyle;
	
	protected StyleResolver styleResolver;

	protected JRStyle[] styles;

	protected JRFillGroup[] groups;

	protected JRFillSection missingFillSection;
	protected JRFillBand missingFillBand;

	protected JRFillBand background;

	protected JRFillBand title;

	protected JRFillBand pageHeader;

	protected JRFillBand columnHeader;

	protected JRFillSection detailSection;

	protected JRFillBand columnFooter;

	protected JRFillBand pageFooter;

	protected JRFillBand lastPageFooter;

	protected JRFillBand summary;

	protected JRFillBand noData;

	protected JRPrintPage printPage;
	protected int printPageContentsWidth;

	/**
	 * List of {@link JRFillBand JRFillBand} objects containing all bands of the
	 * report.
	 */
	protected List<JRBand> bands;

	/**
	 * Collection of subfillers
	 */
	protected Map<Integer, JRBaseFiller> subfillers;

	private boolean bandOverFlowAllowed;

	/**
	 * @deprecated To be removed.
	 */
	private boolean isLegacyTextMeasuring;

	/**
	 *
	 */
	protected Map<String,Format> dateFormatCache = new HashMap<>();
	protected Map<String,Format> numberFormatCache = new HashMap<>();

	protected GroupFooterElementRange groupFooterPositionElementRange;
	// we need to keep detail element range separate from orphan group footer element range
	// because it is created in advance, as by the time we need to create the element range 
	// for the current detail, we do not know if any group will break and footers would be filled
	protected ElementRange detailElementRange;
	// we use this element range to keep the detail element range once orphan footers start to render;
	// this is more like a flag to signal that we are currently dealing with orphan group footers
	protected ElementRange orphanGroupFooterDetailElementRange;
	// we keep the content of orphan group footers bands in separate element range because in horizontal
	// filler, the detail element range can have a different columnIndex and thus be moved with a different X offset
	protected ElementRange orphanGroupFooterElementRange;
	// keep the content of floating column footer so that it can be moved up in case content is moved
	// from one page/column to the next due to keep together, min details or orphan footer groups
	protected ElementRange floatColumnFooterElementRange;
	

	/**
	 *
	 */
	protected boolean isCreatingNewPage;
	protected boolean isNewPage;
	protected boolean isNewColumn;
	protected boolean isFirstPageBand;
	protected boolean isFirstColumnBand;
	protected boolean atLeastOneElementIsToPrint;
	// indicates if values from current record have already appeared on current page through a band being evaluated with JRExpression.EVALUATION_DEFAULT
	protected boolean isCrtRecordOnPage;
	protected boolean isCrtRecordOnColumn;
	// we call it min level because footers print in reverse order and lower level means outer footer
	protected Integer preventOrphanFootersMinLevel;
	protected int crtGroupFootersLevel;

	protected int columnIndex;

	protected int offsetX;
	protected int offsetY;
	protected int columnHeaderOffsetY;
	protected int columnFooterOffsetY;
	protected int lastPageColumnFooterOffsetY;

	protected boolean isLastPageFooter;

	protected boolean isReorderBandElements;
	
	protected int usedPageHeight = 0;

	/**
	 *
	 */
	protected JRBaseFiller(
		JasperReportsContext jasperReportsContext, 
		JasperReportSource reportSource,  
		BandReportFillerParent parent 
		) throws JRException
	{
		super(jasperReportsContext, reportSource, parent);
		
		groups = mainDataset.groups;

		createReportTemplates(factory);

		String reportName = getBandReportParent() == null ? null : getBandReportParent().getReportName();
		
		background = createFillBand(jasperReport.getBackground(), reportName, BandTypeEnum.BACKGROUND);
		title = createFillBand(jasperReport.getTitle(), reportName, BandTypeEnum.TITLE);
		pageHeader = createFillBand(jasperReport.getPageHeader(), reportName, BandTypeEnum.PAGE_HEADER);
		columnHeader = createFillBand(jasperReport.getColumnHeader(), reportName, BandTypeEnum.COLUMN_HEADER);
		
		detailSection = factory.getSection(jasperReport.getDetailSection());
		if (detailSection != missingFillSection)
		{
			detailSection.setOrigin(
				new JROrigin(
					reportName,
					BandTypeEnum.DETAIL
					)
				);
		}
		
		columnFooter = createFillBand(jasperReport.getColumnFooter(), reportName, BandTypeEnum.COLUMN_FOOTER);
		pageFooter = createFillBand(jasperReport.getPageFooter(), reportName, BandTypeEnum.PAGE_FOOTER);
		lastPageFooter = createFillBand(jasperReport.getLastPageFooter(), reportName, BandTypeEnum.LAST_PAGE_FOOTER);
		
		summary = createFillBand(jasperReport.getSummary(), reportName, BandTypeEnum.SUMMARY);
		if (summary != missingFillBand && summary.isEmpty())
		{
			summary = missingFillBand;
		}
		
		noData = createFillBand(jasperReport.getNoData(), reportName, BandTypeEnum.NO_DATA);

		initDatasets();
		initBands();
	}

	@Override
	protected void jasperReportSet()
	{
		SectionTypeEnum sectionType = SectionTypeEnum.getValueOrDefault(jasperReport.getSectionType());
		if (sectionType != SectionTypeEnum.BAND)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNSUPPORTED_REPORT_SECTION_TYPE,  
					new Object[]{sectionType} 
					);
		}

		/*   */
		name = jasperReport.getName();
		columnCount = jasperReport.getColumnCount() == null ? 1 : jasperReport.getColumnCount();
		printOrder = PrintOrderEnum.getValueOrDefault(jasperReport.getPrintOrder());
		columnDirection = jasperReport.getColumnDirection();
		pageWidth = jasperReport.getPageWidth();
		pageHeight = jasperReport.getPageHeight();
		orientation = jasperReport.getOrientation();
		whenNoDataType = jasperReport.getWhenNoDataType();
		columnWidth = jasperReport.getColumnWidth();
		columnSpacing = jasperReport.getColumnSpacing();
		leftMargin = jasperReport.getLeftMargin();
		rightMargin = jasperReport.getRightMargin();
		topMargin = jasperReport.getTopMargin();
		bottomMargin = jasperReport.getBottomMargin();
		isTitleNewPage = jasperReport.isTitleNewPage();
		isSummaryNewPage = jasperReport.isSummaryNewPage();
		isSummaryWithPageHeaderAndFooter = jasperReport.isSummaryWithPageHeaderAndFooter();
		isFloatColumnFooter = jasperReport.isFloatColumnFooter();
	}

	@Override
	protected JRFillObjectFactory initFillFactory()
	{
		JRFillObjectFactory fillFactory = new JRFillObjectFactory(this);
		
		// needed when creating group bands
		defaultStyleListeners = new ArrayList<>();
		
		missingFillBand = new JRFillBand(this, null, fillFactory);
		missingFillSection = new JRFillSection(this, null, fillFactory);

		return fillFactory;
	}
	
	private JRFillBand createFillBand(JRBand reportBand, String reportName, BandTypeEnum bandType)
	{
		JRFillBand fillBand = factory.getBand(reportBand);
		if (fillBand != missingFillBand)
		{
			JROrigin origin = new JROrigin(reportName, bandType);
			fillBand.setOrigin(origin);
		}
		return fillBand;
	}

	@Override
	protected void setJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		super.setJasperReportsContext(jasperReportsContext);

		this.styleResolver = new StyleResolver(jasperReportsContext);
	}

	/**
	 * Returns the report fields indexed by name.
	 *
	 * @return the report fields map
	 */
	protected Map<String,JRFillField> getFieldsMap()
	{
		return mainDataset.fieldsMap;
	}


	/**
	 * Returns the report variables indexed by name.
	 *
	 * @return the report variables map
	 */
	protected Map<String,JRFillVariable> getVariablesMap()
	{
		return mainDataset.variablesMap;
	}


	/**
	 * Returns a report field.
	 *
	 * @param fieldName the field name
	 * @return the field
	 */
	protected JRFillField getField(String fieldName)
	{
		return mainDataset.getFillField(fieldName);
	}

	private void initBands()
	{
		bands = new ArrayList<>(8 + (groups == null ? 0 : (2 * groups.length)));
		bands.add(title);
		bands.add(summary);
		bands.add(pageHeader);
		bands.add(pageFooter);
		bands.add(lastPageFooter);
		bands.add(columnHeader);
		bands.add(columnFooter);
		if (detailSection.getBands() != null)
		{
			bands.addAll(Arrays.asList(detailSection.getBands()));
		}
		bands.add(noData);

		if (groups != null && groups.length > 0)
		{
			for (int i = 0; i < groups.length; i++)
			{
				JRFillGroup group = groups[i];
				if (group.getGroupHeaderSection().getBands() != null)
				{
					bands.addAll(Arrays.asList(group.getGroupHeaderSection().getBands()));
				}
				if (group.getGroupFooterSection().getBands() != null)
				{
					bands.addAll(Arrays.asList(group.getGroupFooterSection().getBands()));
				}
			}
		}

		initBandsNowEvaluationTimes();
	}


	private void initBandsNowEvaluationTimes()
	{
		JREvaluationTime[] groupEvaluationTimes;
		if (groups == null)
		{
			groupEvaluationTimes = new JREvaluationTime[0];
		}
		else
		{
			groupEvaluationTimes = new JREvaluationTime[groups.length];
			for (int i = 0; i < groups.length; i++)
			{
				groupEvaluationTimes[i] = JREvaluationTime.getGroupEvaluationTime(groups[i].getName());
			}

			for (int i = 0; i < groups.length; i++)
			{
				JRGroup group = groups[i];
				JRFillSection footer = (JRFillSection) group.getGroupFooterSection();

				for (int j = i; j < groupEvaluationTimes.length; ++j)
				{
					footer.addNowEvaluationTime(groupEvaluationTimes[j]);
				}
			}
		}

		columnFooter.addNowEvaluationTime(JREvaluationTime.EVALUATION_TIME_COLUMN);

		pageFooter.addNowEvaluationTime(JREvaluationTime.EVALUATION_TIME_COLUMN);
		pageFooter.addNowEvaluationTime(JREvaluationTime.EVALUATION_TIME_PAGE);

		summary.addNowEvaluationTimes(groupEvaluationTimes);
		noData.addNowEvaluationTimes(groupEvaluationTimes);
	}

	protected BandReportFillerParent getBandReportParent()
	{
		return (BandReportFillerParent)parent;
	}

	protected List<String> getPrintTransferPropertyPrefixes()
	{
		return printTransferPropertyPrefixes;
	}

	/**
	 *
	 */
	public JRStyledTextParser getStyledTextParser()
	{
		return styledTextParser;
	}
	
	protected JRStyledTextUtil getStyledTextUtil()
	{
		return fillContext.getStyledTextUtil();
	}

	/**
	 * Returns the number of generated master print pages.
	 * 
	 * @return the number of generated master print pages
	 */
	public int getCurrentPageCount()
	{
		return getMasterFiller().jasperPrint.getPages().size();
	}
	
	@Override
	public JRStyle getDefaultStyle()
	{
		return defaultStyle;
	}

	@Override
	public StyleResolver getStyleResolver()
	{
		return styleResolver;
	}

	protected boolean isSubreportRunToBottom()
	{
		return getBandReportParent() != null && getBandReportParent().isRunToBottom();
	}
	
	/**
	 *
	 */
	public JRPrintPage getCurrentPage()
	{
		return printPage;
	}
	
	protected int getCurrentPageContentsWidth()
	{
		return printPageContentsWidth;
	}

	/**
	 *
	 */
	protected abstract void setPageHeight(int pageHeight);


	@Override
	@continuable
	public JasperPrint fill(Map<String,Object> parameterValues) throws JRException
	{
		if (parameterValues == null)
		{
			parameterValues = new HashMap<>();
		}

		if (log.isDebugEnabled())
		{
			log.debug("Fill " + fillerId + ": filling report");
		}

		setParametersToContext(parameterValues);

		fillingThread = Thread.currentThread();
		
		JRResourcesFillUtil.ResourcesFillContext resourcesContext = 
			JRResourcesFillUtil.setResourcesFillContext(parameterValues);
		
		boolean success = false;
		try
		{
			createBoundElemementMaps();

			if (parent != null)
			{
				getBandReportParent().registerSubfiller(this);
			}

			setParameters(parameterValues);

			setBookmarkHelper();
			
			isLegacyTextMeasuring = propertiesUtil.getBooleanProperty(mainDataset, 
					JRFillTextElement.PROPERTY_LEGACY_TEXT_MEASURING, false);
			
			loadStyles();

			jasperPrint.setName(name);
			jasperPrint.setPageWidth(pageWidth);
			jasperPrint.setPageHeight(pageHeight);
			jasperPrint.setTopMargin(topMargin);
			jasperPrint.setLeftMargin(leftMargin);
			jasperPrint.setBottomMargin(bottomMargin);
			jasperPrint.setRightMargin(rightMargin);
			jasperPrint.setOrientation(OrientationEnum.getValueOrDefault(orientation));

			jasperPrint.setFormatFactoryClass(jasperReport.getFormatFactoryClass());
			jasperPrint.setLocaleCode(JRDataUtils.getLocaleCode(getLocale()));
			jasperPrint.setTimeZoneId(JRDataUtils.getTimeZoneId(getTimeZone()));

			propertiesUtil.transferProperties(mainDataset, jasperPrint, 
				JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);

			jasperPrint.setDefaultStyle(defaultStyle);

			/*   */
			if (styles != null && styles.length > 0)
			{
				for (int i = 0; i < styles.length; i++)
				{
					addPrintStyle(styles[i]);
				}
			}

			/*   */
			mainDataset.start();

			/*   */
			fillReport();
			
			mainDataset.evaluateProperties(PropertyEvaluationTimeEnum.REPORT);
			
			propertiesUtil.transferProperties(
				mainDataset, 
				jasperPrint, 
				JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX
				);

			// add consolidates styles as normal styles in the print object
//			for (Iterator it = consolidatedStyles.values().iterator(); it.hasNext();)
//			{
//				jasperPrint.addStyle((JRStyle) it.next());
//			}

			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": ended");
			}

			success = true;
			return jasperPrint;
		}
		finally
		{
			mainDataset.closeDatasource();
			mainDataset.disposeParameterContributors();
			
			if (success && parent == null)
			{
				// commit the cached data
				fillContext.cacheDone();
			}

			if (parent != null)
			{
				getBandReportParent().unregisterSubfiller(this);
			}
			
			delayedActions.dispose();

			clearFillingThread();

			//kill the subreport filler threads
			abortSubfillers();
			
			if (parent == null)
			{
				fillContext.dispose();
			}

			JRResourcesFillUtil.revertResourcesFillContext(resourcesContext);
		}
	}
		
	public void addPrintStyle(JRStyle style) throws JRException
	{
		jasperPrint.addStyle(style, true);
	}
	
	protected static interface DefaultStyleListener
	{
		void defaultStyleSet(JRStyle style);
	}

	private List<DefaultStyleListener> defaultStyleListeners;

	protected void addDefaultStyleListener(DefaultStyleListener listener)
	{
		defaultStyleListeners.add(listener);
	}

	protected void setDefaultStyle(JRStyle style)
	{
		defaultStyle = style;

		for (Iterator<DefaultStyleListener> it = defaultStyleListeners.iterator(); it.hasNext();)
		{
			DefaultStyleListener listener = it.next();
			listener.defaultStyleSet(style);
		}
	}

	protected void loadStyles() throws JRException
	{
		List<JRStyle> styleList = collectStyles();
		JRStyle reportDefaultStyle = jasperReport.getDefaultStyle();
		if (reportDefaultStyle == null)
		{
			lookupExternalDefaultStyle(styleList);
		}

		List<JRStyle> includedStyles = factory.setStyles(styleList);
		if (getBandReportParent() != null)
		{
			getBandReportParent().registerReportStyles(includedStyles);
		}

		styles = includedStyles.toArray(new JRStyle[includedStyles.size()]);

		if (reportDefaultStyle != null)
		{
			setDefaultStyle(factory.getStyle(reportDefaultStyle));
		}
	}

	public void registerReportStyles(UUID id, List<JRStyle> styles)
	{
		if (getBandReportParent() == null)
		{
			fillContext.registerReportStyles(jasperReport, id, styles);
		}
		else
		{
			String reportLocation = getBandReportParent().getReportLocation();
			if (reportLocation != null)
			{
				fillContext.registerReportStyles(reportLocation, id, styles);
			}
		}
		
	}

	private static final JRStyleSetter DUMMY_STYLE_SETTER = new JRStyleSetter()
	{
		@Override
		public void setStyle(JRStyle style)
		{
		}

		@Override
		public void setStyleNameReference(String name)
		{
		}
	};

	protected List<JRStyle> collectStyles() throws JRException
	{
		List<JRStyle> styleList = collectTemplateStyles();

		JRStyle[] reportStyles = jasperReport.getStyles();
		if (reportStyles != null)
		{
			styles = new JRStyle[reportStyles.length];//FIXME remove this

			for (int i = 0; i < reportStyles.length; i++)
			{
				JRStyle style = reportStyles[i];
				styleList.add(style);

				//add dummy style requester so that report styles are always included
				//in the final list
				factory.registerDelayedStyleSetter(DUMMY_STYLE_SETTER, style.getName());
			}
		}

		return styleList;
	}

	protected void collectTemplates() throws JRException
	{
		templates = new ArrayList<>();

		if (reportTemplates != null)
		{
			for (JRFillReportTemplate reportTemplate : reportTemplates)
			{
				ReportTemplateSource template = reportTemplate.evaluate();
				if (template != null)
				{
					templates.add(template);
				}
			}
		}

		Collection<JRTemplate> paramTemplates = (Collection<JRTemplate>) mainDataset.getParameterValue(
				JRParameter.REPORT_TEMPLATES, true);
		if (paramTemplates != null)
		{
			for (JRTemplate template : paramTemplates)
			{
				templates.add(ReportTemplateSource.of(template));
			}
		}
	}

	public List<JRTemplate> getTemplates()
	{
		return templates.stream().map(source -> source.getTemplate()).collect(Collectors.toList());
	}
	
	protected List<JRStyle> collectTemplateStyles() throws JRException
	{
		collectTemplates();
		
		List<JRStyle> externalStyles = new ArrayList<>();
		HashSet<String> loadedLocations = new HashSet<>();
		for (ReportTemplateSource template : templates)
		{
			collectStyles(template, externalStyles, loadedLocations);
		}
		return externalStyles;
	}

	protected void collectStyles(ReportTemplateSource template, List<JRStyle> externalStyles, Set<String> loadedLocations) throws JRException
	{
		HashSet<String> parentLocations = new HashSet<>();
		collectStyles(template, externalStyles, loadedLocations, parentLocations);
	}
	
	protected void collectStyles(ReportTemplateSource templateSource, List<JRStyle> externalStyles, 
			Set<String> loadedLocations, Set<String> templateParentLocations) throws JRException
	{
		String templateLocation = templateSource.getTemplateResourceInfo() == null ? null
				: templateSource.getTemplateResourceInfo().getRepositoryResourceLocation();
		if (templateLocation != null && !templateParentLocations.add(templateLocation))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CIRCULAR_DEPENDENCY_FOUND,  
					new Object[]{templateLocation} 
					);
		}
		
		if (templateLocation != null && !loadedLocations.add(templateLocation))
		{
			//already loaded
			return;
		}
		
		collectIncludedTemplates(templateSource, externalStyles, 
				loadedLocations, templateParentLocations);

		JRStyle[] templateStyles = templateSource.getTemplate().getStyles();
		if (templateStyles != null)
		{
			for (int i = 0; i < templateStyles.length; i++)
			{
				JRStyle style = templateStyles[i];
				String styleName = style.getName();
				if (styleName == null)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_EXTERNAL_STYLE_NAME_NOT_SET,  
							(Object[])null 
							);
				}

				externalStyles.add(style);
			}
		}
	}

	protected void collectIncludedTemplates(ReportTemplateSource templateSource, List<JRStyle> externalStyles, 
			Set<String> loadedLocations, Set<String> templateParentLocations) throws JRException
	{
		JRTemplateReference[] includedTemplates = templateSource.getTemplate().getIncludedTemplates();
		if (includedTemplates != null && includedTemplates.length > 0)
		{
			RepositoryResourceContext currentContext = repositoryContext.getResourceContext();
			String contextLocation = templateSource.getTemplateResourceInfo() == null ? null 
					: templateSource.getTemplateResourceInfo().getRepositoryContextLocation();
			RepositoryResourceContext templateResourceContext = SimpleRepositoryResourceContext.of(contextLocation,
					currentContext == null ? null : currentContext.getDerivedContextFallback());
			RepositoryContext templateRepositoryContext = SimpleRepositoryContext.of(repositoryContext.getJasperReportsContext(), 
					templateResourceContext);
			
			for (int i = 0; i < includedTemplates.length; i++)
			{
				JRTemplateReference reference = includedTemplates[i];
				String location = reference.getLocation();
				
				ReportTemplateSource includedTemplate = JRFillReportTemplate.loadTemplate(
						location, this, templateRepositoryContext);
				collectStyles(includedTemplate, externalStyles, 
						loadedLocations, templateParentLocations);
			}
		}
	}

	protected void lookupExternalDefaultStyle(Collection<JRStyle> styleList)
	{
		JRStyle defStyle = null;
		for (Iterator<JRStyle> it = styleList.iterator(); it.hasNext();)
		{
			JRStyle style = it.next();
			if (style.isDefault())
			{
				defStyle = style;
			}
		}

		if (defStyle != null)
		{
			factory.registerDelayedStyleSetter(new JRStyleSetter()
			{
				@Override
				public void setStyle(JRStyle style)
				{
					if (style.isDefault())
					{
						setDefaultStyle(style);
					}
				}

				@Override
				public void setStyleNameReference(String name)
				{
				}
			}, defStyle.getName());
		}
	}


	private void createBoundElemementMaps()
	{
		createBoundElementMaps(JREvaluationTime.EVALUATION_TIME_MASTER);
		createBoundElementMaps(JREvaluationTime.EVALUATION_TIME_REPORT);
		createBoundElementMaps(JREvaluationTime.EVALUATION_TIME_PAGE);
		createBoundElementMaps(JREvaluationTime.EVALUATION_TIME_COLUMN);

		if (groups != null)
		{
			for (int i = 0; i < groups.length; i++)
			{
				createBoundElementMaps(JREvaluationTime.getGroupEvaluationTime(groups[i].getName()));
			}
		}

		for (Iterator<JRBand> i = bands.iterator(); i.hasNext();)
		{
			JRFillBand band = (JRFillBand) i.next();
			createBoundElementMaps(JREvaluationTime.getBandEvaluationTime(band));
		}
	}


	private void abortSubfillers()
	{
		if (subfillers != null && !subfillers.isEmpty())
		{
			for (JRBaseFiller subfiller : subfillers.values())
			{
				if (subfiller.getBandReportParent() != null)
				{
					if (log.isDebugEnabled())
					{
						log.debug("Fill " + fillerId + ": Aborting subfiller " + subfiller.fillerId);
					}
					
					subfiller.getBandReportParent().abortSubfiller(subfiller);
				}
			}
		}
	}


	/**
	 *
	 */
	@continuable
	protected abstract void fillReport() throws JRException;

	@Override
	protected void ignorePaginationSet(Map<String, Object> parameterValues)
	{
		if (ignorePagination)
		{
			isTitleNewPage = false;
			isSummaryNewPage = false;
			if (groups != null)
			{
				for (int i = 0; i < groups.length; i++)
				{
					groups[i].setStartNewPage(false);
					groups[i].setResetPageNumber(false);
					groups[i].setStartNewColumn(false);
				}
			}
			
			if (isMasterReport() || !getBandReportParent().isParentPagination())//subreport page height is already set by band master
			{
				int maxPageHeight = getMaxPageHeight(parameterValues);
				setPageHeight(maxPageHeight);
			}
		}
		
		maxPageWidth = getMaxPageWidth(parameterValues);
	}
	
	protected int getMaxPageHeight(Map<String,Object> parameterValues)
	{
		Integer maxPageHeightParam = (Integer) parameterValues.get(JRParameter.MAX_PAGE_HEIGHT);
		int maxPageHeight = maxPageHeightParam != null ? maxPageHeightParam : PAGE_HEIGHT_PAGINATION_IGNORED;
		if (maxPageHeight < pageHeight)
		{
			if (log.isDebugEnabled())
			{
				log.debug("max page height " + maxPageHeight + " smaller than report page height " + pageHeight);
			}
			
			//use the report page height
			maxPageHeight = pageHeight;
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("max page height is " + maxPageHeight);
		}
		
		return maxPageHeight;
	}
	
	protected int getMaxPageWidth(Map<String,Object> parameterValues)
	{
		Integer maxPageWidthParam = (Integer) parameterValues.get(JRParameter.MAX_PAGE_WIDTH);
		int maxPageWidth = maxPageWidthParam != null ?  maxPageWidthParam : PAGE_WIDTH_IGNORED;
		
		if (maxPageWidth < pageWidth)
		{
			if (log.isDebugEnabled())
			{
				log.debug("max page width " + maxPageWidth + " smaller than report page width " + pageWidth);
			}
			
			//use the report page width
			maxPageWidth = pageWidth;
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("max page width is " + maxPageWidth);
		}
		
		return maxPageWidth;
	}
	
	protected void recordUsedPageHeight(int pageHeight)
	{
		if (pageHeight > usedPageHeight)
		{
			usedPageHeight = pageHeight;
		}
	}


	/**
	 * Returns the report resource bundle.
	 *
	 * @return the report resource bundle
	 */
	protected ResourceBundle getResourceBundle()
	{
		return mainDataset.resourceBundle;
	}


	/**
	 *
	 */
	protected WhenNoDataTypeEnum getWhenNoDataType()
	{
		WhenNoDataTypeEnum result = whenNoDataType;
		
		if (result == null)
		{
			result = 
				WhenNoDataTypeEnum.getByName(
					propertiesUtil.getProperty(mainDataset, JRReport.CONFIG_PROPERTY_WHEN_NO_DATA_TYPE)
					);
		}
		
		return result;
	}


	/**
	 *
	 */
	public Format getDateFormat(String pattern)
	{
		return getDateFormat(pattern, null);
	}
	
	protected Format getDateFormat(String pattern, TimeZone timeZone)
	{
		Locale lc = getLocale();
		TimeZone tz = timeZone == null ? getTimeZone() : timeZone;// default to filler timezone
		String key = pattern + "|" + JRDataUtils.getLocaleCode(lc) + "|" + JRDataUtils.getTimeZoneId(tz);
		Format format = dateFormatCache.get(key);
		if (format == null)
		{
			format = getFormatFactory().createDateFormat(pattern, lc, tz);
			if (format != null)
			{
				dateFormatCache.put(key, format);
			}
		}
		return format;
	}


	/**
	 *
	 */
	public Format getNumberFormat(String pattern)
	{
		Locale lc = getLocale();
		String key = pattern + "|" + JRDataUtils.getLocaleCode(lc);
		Format format = numberFormatCache.get(key);
		if (format == null)
		{
			format = FormatUtils.getIcu4jNumberFormat(pattern, lc).orElseGet(
					() -> getFormatFactory().createNumberFormat(pattern, lc));

			if (log.isDebugEnabled())
			{
				log.debug("format is of type: " + format.getClass().getName());
			}

			if (format != null)
			{
				numberFormatCache.put(key, format);
			}
		}
		return format;
	}


	protected boolean hasMasterFormatFactory()
	{
		return
			!isSubreport()
			|| getFormatFactory().getClass().getName().equals(
				fillContext.getMasterFormatFactory().getClass().getName()
				);
	}


	protected boolean hasMasterLocale()
	{
		return !isSubreport() || getLocale().equals(fillContext.getMasterLocale());
	}


	protected boolean hasMasterTimeZone()
	{
		return !isSubreport() || getTimeZone().equals(fillContext.getMasterTimeZone());
	}


	/**
	 * Sets a parameter's value.
	 *
	 * @param parameterName the parameter name
	 * @param value the value
	 * @throws JRException
	 */
	protected void setParameter(String parameterName, Object value) throws JRException
	{
		mainDataset.setParameter(parameterName, value);
	}


	/**
	 * Sets a parameter's value.
	 *
	 * @param parameter the parameter
	 * @param value the value
	 * @throws JRException
	 */
	protected void setParameter(JRFillParameter parameter, Object value) throws JRException
	{
		mainDataset.setParameter(parameter, value);
	}

	/**
	 *
	 */
	protected boolean next() throws JRException
	{
		isCrtRecordOnPage = false;
		isCrtRecordOnColumn = false;
		
		return mainDataset.next();
	}

	/**
	 * Resolves elements which are to be evaluated at report level.
	 */
	protected void resolveReportBoundElements() throws JRException
	{
		resolveBoundElements(JREvaluationTime.EVALUATION_TIME_REPORT, JRExpression.EVALUATION_DEFAULT);
	}

	/**
	 * Resolves elements which are to be evaluated at page level.
	 *
	 * @param evaluation
	 *            the evaluation type
	 */
	protected void resolvePageBoundElements(byte evaluation) throws JRException
	{
		resolveBoundElements(JREvaluationTime.EVALUATION_TIME_PAGE, evaluation);
	}

	/**
	 * Resolves elements which are to be evaluated at column level.
	 *
	 * @param evaluation
	 *            the evaluation type
	 */
	protected void resolveColumnBoundElements(byte evaluation) throws JRException
	{
		resolveBoundElements(JREvaluationTime.EVALUATION_TIME_COLUMN, evaluation);
	}

	/**
	 * Resolves elements which are to be evaluated at group level.
	 *
	 * @param evaluation
	 *            the evaluation type
	 * @param isFinal
	 */
	protected void resolveGroupBoundElements(byte evaluation, boolean isFinal) throws JRException
	{
		if (groups != null && groups.length > 0)
		{
			for (int i = 0; i < groups.length; i++)
			{
				JRFillGroup group = groups[i];

				if ((group.hasChanged() && group.isFooterPrinted()) || isFinal)
				{
					String groupName = group.getName();

					resolveBoundElements(JREvaluationTime.getGroupEvaluationTime(groupName), evaluation);
				}
			}
		}
	}

	protected JRPrintPage newPage()
	{
		JRPrintPage page;

		if (fillContext.isUsingVirtualizer())
		{
			JRVirtualPrintPage virtualPage = new JRVirtualPrintPage(virtualizationContext);
			page = virtualPage;
		}
		else
		{
			page = new JRBasePrintPage();
		}

		return page;
	}

	/**
	 * Resloves elements which are to be evaluated at band level.
	 *
	 * @param band
	 *            the band
	 * @param evaluation
	 *            the evaluation type
	 * @throws JRException
	 */
	protected void resolveBandBoundElements(JRFillBand band, byte evaluation) throws JRException
	{
		resolveBoundElements(JREvaluationTime.getBandEvaluationTime(band), evaluation);
	}


	protected void registerSubfiller(JRBaseFiller subfiller)
	{
		if (subfillers == null)
		{
			subfillers = new ConcurrentHashMap<>(16, 0.75f, 1);
		}

		subfillers.put(subfiller.fillerId, subfiller);
	}

	protected void unregisterSubfiller(JRBaseFiller subfiller)
	{
		if (subfillers != null)
		{
			subfillers.remove(subfiller.fillerId);
		}
	}


	/**
	 *
	 */
	protected void fillBand(JRPrintBand band)
	{
		Consumer<JRPrintElement> offsetter = element ->
		{
			element.setX(element.getX() + offsetX);
			element.setY(element.getY() + offsetY);
		};
		
		if (isReorderBandElements())
		{
			List<JRPrintElement> elements = new ArrayList<>();
			band.consumeElement(offsetter.andThen(elements::add));
			Collections.sort(elements, new JRYXComparator());//FIXME make singleton comparator; same for older comparator
			for (JRPrintElement element : elements)
			{
				printPage.addElement(element);
				recordUsedWidth(element);
			}
		}
		else
		{
			band.consumeElement(offsetter.andThen(element ->
			{
				printPage.addElement(element);
				recordUsedWidth(element);
			}));
		}
		
		int contentsWidth = band.getContentsWidth();
		if (offsetX + contentsWidth + rightMargin > printPageContentsWidth)
		{
			printPageContentsWidth = offsetX + contentsWidth + rightMargin;
		}
	}
	
	protected void recordUsedWidth(JRPrintElement element)
	{
		recordUsedPageWidth(element.getX() + element.getWidth() + rightMargin);
	}


	protected void addPage(JRPrintPage page)
	{
		if (!isSubreport())
		{
			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": adding page " + (jasperPrint.getPages().size() + 1));
			}

			//TODO do these two in a single traversal
			addLastPageBookmarks();
			detectPart();
			
			// notify that the previous page was generated
			int pageCount = jasperPrint.getPages().size();
			if (pageCount > 0 && fillListener != null)
			{
				fillListener.pageGenerated(jasperPrint, pageCount - 1);
			}

			jasperPrint.addPage(page);
			fillContext.setPrintPage(page);
		}
	}
	
	@continuable
	protected void addPageToParent(final boolean ended) throws JRException
	{
		if (printPage == null)
		{
			return;
		}
		
		FillerPageAddedEvent pageAdded = new FillerPageAddedEvent()
		{
			@Override
			public JasperPrint getJasperPrint()
			{
				return jasperPrint;
			}
			
			@Override
			public JRPrintPage getPage()
			{
				return printPage;
			}

			@Override
			public boolean hasReportEnded()
			{
				return ended;
			}

			@Override
			public int getPageStretchHeight()
			{
				return offsetY + bottomMargin;
			}

			@Override
			public int getPageIndex()
			{
				Number pageNumber = (Number) calculator.getPageNumber().getValue();
				if (pageNumber == null)//this happens when whenNoDataType="BlankPage" //FIXMEBOOK maybe we should set the variable?
				{
					return 0;
				}
				return pageNumber.intValue() - 1;
			}

			@Override
			public JRBaseFiller getFiller()
			{
				return JRBaseFiller.this;
			}

			@Override
			public DelayedFillActions getDelayedActions()
			{
				return delayedActions;
			}
		};
		
		//FIXMEBOOK use a fill listener instead of this?
		getBandReportParent().addPage(pageAdded);
	}

	protected void setMasterPageVariables(int currentPageIndex, int totalPages)
	{
		JRFillVariable masterCurrentPage = getVariable(JRVariable.MASTER_CURRENT_PAGE);
		if (masterCurrentPage != null)
		{
			masterCurrentPage.setValue(currentPageIndex + 1);
		}
		
		JRFillVariable masterTotalPages = getVariable(JRVariable.MASTER_TOTAL_PAGES);
		if (masterTotalPages != null)
		{
			masterTotalPages.setValue(totalPages);
		}
	}

	protected WhenResourceMissingTypeEnum getWhenResourceMissingType()
	{
		return mainDataset.getWhenResourceMissingType();
	}


	protected boolean isBandOverFlowAllowed()
	{
		return bandOverFlowAllowed;
	}


	protected void setBandOverFlowAllowed(boolean splittableBand)
	{
		this.bandOverFlowAllowed = splittableBand;
	}


	protected boolean isReorderBandElements()
	{
		return isReorderBandElements;
	}


	protected void setReorderBandElements(boolean isReorderBandElements)
	{
		this.isReorderBandElements = isReorderBandElements;
	}


	/**
	 * @deprecated To be removed.
	 */
	protected boolean isLegacyTextMeasuring()
	{
		return isLegacyTextMeasuring;
	}


	@Override
	public boolean isPageBreakInhibited()
	{
		boolean isPageBreakInhibited = isFirstPageBand && !atLeastOneElementIsToPrint;
		
		if (isPageBreakInhibited && isSubreport())
		{
			isPageBreakInhibited = parent.getFiller().isPageBreakInhibited();
		}
		
		return isPageBreakInhibited;
	}
	

	protected int getMasterColumnCount()
	{
		FillerParent fillerParent = parent;
		int colCount = 1;

		while (fillerParent != null)
		{
			BaseReportFiller filler = fillerParent.getFiller();
			if (filler instanceof JRBaseFiller)//FIXMEBOOK
			{
				colCount *= ((JRBaseFiller) filler).columnCount;
			}
			fillerParent = filler.parent;
		}

		return colCount;
	}

	/**
	 * Returns the top-level (master) filler object.
	 * 
	 * @return the master filler object
	 */
	public BaseReportFiller getMasterFiller()
	{
		BaseReportFiller filler = this;
		while (filler.parent != null)
		{
			filler = filler.parent.getFiller();
		}
		return filler;
	}


	public void addBoundElement(JRFillElement element, JRPrintElement printElement, 
			EvaluationTimeEnum evaluationType, String groupName, JRFillBand band)
	{
		JRFillGroup group = groupName == null ? null : getGroup(groupName);
		JREvaluationTime evaluationTime = JREvaluationTime.getEvaluationTime(evaluationType, group, band);
		
		addBoundElement(element, printElement, evaluationTime);
	}

	protected void addBoundElement(JRFillElement element, JRPrintElement printElement, JREvaluationTime evaluationTime)
	{
		// the key contains the page and its index; the index is only stored so that we have it in resolveBoundElements
		int pageIndex = currentPageIndex();
		FillPageKey pageKey = new FillPageKey(printPage, pageIndex);
		
		addBoundElement(element, printElement, evaluationTime, pageKey);
	}

	protected int currentPageIndex()
	{
		int pageIndex = ((Number) calculator.getPageNumber().getValue()).intValue() - 1;
		return pageIndex;
	}

	protected void subreportPageFilled(JRPrintPage subreportPage)
	{
		FillPageKey subreportKey = new FillPageKey(subreportPage);
		
		// this method is only called when the parent is a band report
		JRBaseFiller parentFiller = (JRBaseFiller) parent.getFiller();
		//FIXMEBOOK the index is only correct when the parent is the master, see fillListener.pageUpdated
		int parentPageIndex = parentFiller.getJasperPrint().getPages().size() - 1;
		FillPageKey parentKey = new FillPageKey(parentFiller.printPage, parentPageIndex);
		
		// move all delayed elements from the subreport page to the master page
		moveBoundActions(subreportKey, parentKey);
		// move all master evaluations to the parent
		parent.getFiller().delayedActions.moveMasterEvaluations(delayedActions, parentKey);
	}

	protected void moveBoundActions(FillPageKey subreportKey, FillPageKey parentKey)
	{
		delayedActions.moveActions(subreportKey, parentKey);
		
		if (subfillers != null)//recursive
		{
			for (JRBaseFiller subfiller : subfillers.values())
			{
				subfiller.moveBoundActions(subreportKey, parentKey);
			}
		}
	}

	@Override
	public boolean isPageFinal(int pageIdx)
	{
		JRPrintPage page = jasperPrint.getPages().get(pageIdx);
		return !hasBoundActions(page);
	}

	public boolean isPageFinal(JRPrintPage page)
	{
		return !hasBoundActions(page);
	}
	
	protected boolean hasBoundActions(JRPrintPage page)
	{
		boolean hasActions = delayedActions.hasDelayedActions(page);
		if (hasActions)
		{
			return true;
		}
		
		if (subfillers != null)
		{
			for (JRBaseFiller subfiller : subfillers.values())
			{
				// recursive
				if (subfiller.hasBoundActions(page))
				{
					return true;
				}
			}
		}

		return false;
	}

	protected JRFillGroup getGroup(String groupName)
	{
		JRFillGroup group = null;
		if (groups != null)
		{
			for (int i = 0; i < groups.length; i++)
			{
				if (groups[i].getName().equals(groupName))
				{
					group = groups[i];
					break;
				}
			}
		}
		
		if (group == null)
		{
			throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_NO_SUCH_GROUP,  
				new Object[]{groupName} 
				);
		}
		return group;
	}


	/**
	 *
	 */
	protected JRFillGroup getKeepTogetherGroup()
	{
		Integer keepTogetherGroupLevel = null;

		if (groups != null)
		{
			// check to see if there is any group that needs to be kept together or does not have the required min details
			for (int i = 0; i <  groups.length; i++)
			{
				JRFillGroup group = groups[i];
				if (
					group.getKeepTogetherElementRange() != null
					&& (group.isKeepTogether() || !group.hasMinDetails())
					)
				{
					keepTogetherGroupLevel = i;
					break;
				}
			}
		}
		
		if (
			keepTogetherGroupLevel != null
			|| orphanGroupFooterDetailElementRange != null
			)
		{
			// if there was a group that was going to be moved, either because it had keepTogether or less than required min details,
			// or if there is an orphan to be moved, we need to check if the outer groups still meet their required min details or need 
			// to be moved themselves, possibly instead of the inner group
			
			int detailsToMove = Math.max(
				keepTogetherGroupLevel == null ? 0 : groups[keepTogetherGroupLevel].getDetailsCount(),
				orphanGroupFooterDetailElementRange == null ? 0 : 1
				);
			
			int lcMaxGrpIdx = keepTogetherGroupLevel == null ? groups.length : keepTogetherGroupLevel;
			
			for (int i = 0; i <  lcMaxGrpIdx; i++)
			{
				JRFillGroup group = groups[i];
				if (
					group.getKeepTogetherElementRange() != null
					&& !group.hasMinDetails(detailsToMove)
					)
				{
					keepTogetherGroupLevel = i;
					break;
				}
			}
		}
		
		return keepTogetherGroupLevel == null ? null : groups[keepTogetherGroupLevel];
	}


//	protected JRStyle getConsolidatedStyle(String consolidatedStyleName)
//	{
//		return (JRStyle) consolidatedStyles.get(consolidatedStyleName);
//	}
//
//
//	protected void putConsolidatedStyle(JRStyle consolidatedStyle)
//	{
//		consolidatedStyles.put(consolidatedStyle.getName(), consolidatedStyle);
//	}


	protected void createReportTemplates(JRFillObjectFactory factory)
	{
		JRReportTemplate[] templates = jasperReport.getTemplates();
		if (templates != null)
		{
			reportTemplates = new JRFillReportTemplate[templates.length];

			for (int i = 0; i < templates.length; i++)
			{
				JRReportTemplate template = templates[i];
				reportTemplates[i] = factory.getReportTemplate(template);
			}
		}
	}


	protected int getFillerId()
	{
		return fillerId;
	}

	protected PrintElementOriginator assignElementId(JRFillElement fillElement)
	{
		int id = getFillContext().generateFillElementId();
		DefaultPrintElementOriginator originator = new DefaultPrintElementOriginator(id);
		return originator;
	}
}
