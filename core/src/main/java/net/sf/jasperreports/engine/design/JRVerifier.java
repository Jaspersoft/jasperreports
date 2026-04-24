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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.JRCrosstabBucket;
import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabDataset;
import net.sf.jasperreports.crosstabs.JRCrosstabGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.JRCrosstabParameter;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.fill.JRPercentageCalculator;
import net.sf.jasperreports.crosstabs.fill.JRPercentageCalculatorFactory;
import net.sf.jasperreports.crosstabs.type.CrosstabPercentageEnum;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.CommonReturnValue;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRGenericElementParameter;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRQueryChunk;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.VariableReturnValue;
import net.sf.jasperreports.engine.analytics.dataset.MultiAxisData;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentCompiler;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.fill.JRExtendedIncrementerFactory;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartComponentCompiler;
import net.sf.jasperreports.engine.part.PartComponentManager;
import net.sf.jasperreports.engine.part.PartComponentsEnvironment;
import net.sf.jasperreports.engine.part.PartEvaluationTime;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.PartEvaluationTimeType;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.util.FormatFactory;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRQueryExecuterUtils;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * A report verifier.
 *
 * <p>
 * The verifier checks that a report design meets certain rules in order to pass
 * report compilation.  
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRVerifier
{

	private static final Log log = LogFactory.getLog(JRVerifier.class);
	
	/**
	 * A property that determines whether elements are allowed to overlap.
	 * 
	 * <p>
	 * If this value is set to <code>false</code>, the report is verified not
	 * to contain elements that overlap.  This is useful when the report is
	 * meant to be exported to grid-based formats such as HTML, XLS or CSV.
	 * Setting this property to <code>false</code> ensures that element overlap
	 * issues are caught at report compile time.
	 * 
	 * <p>
	 * Additionally, when this property is set to false <code>false</code>, the
	 * report is verified not to have any content in the background section as
	 * this content would likely be overlapped by other sections and would not
	 * show in grid-based exporters.
	 * 
	 * <p>
	 * By default, the property is set to <code>true</code> which means that
	 * no element overlap checks are performed.
	 * 
	 * <p>
	 * The property can be set at the following levels:
	 * <ul>
	 * 	<li>At global level (in jasperreports.properties) to provide a default
	 * value.</li>
	 * 	<li>At report level, to indicate whether element overlap checks are to
	 * be performed for the report.  If not set, the global property value is
	 * used.</li>
	 * 	<li>At report element level to specify that the particular element is
	 * allowed to overlap or be overlapped by other elements, when the report
	 * or global property determines report element overlap verification.
	 * The element level property is only effective when set to <code>true</code>;
	 * setting the property to <code>false</code> does not make the verifier
	 * check for overlaps when the report is not set to be checked for element
	 * overlaps.</li>
	 * </ul>
	 * 
	 * <p>
	 * Note that print when expressions or export filters cannot be taken into
	 * consideration while checking for overlapping elements as this check is
	 * performed at report compilation time.
	 * If a report contains two elements that overlap but have print when
	 * expressions that guarantee that only one of them will be printed,
	 * or if export filters are in place to exclude one of the elements,
	 * one of them should be explicitly marked to allow element overlap
	 * when the report is configured to check for overlaps.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_5_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_ALLOW_ELEMENT_OVERLAP = 
		JRPropertiesUtil.PROPERTY_PREFIX + "allow.element.overlap";
	
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_7_1
			)
	public static final String PROPERTY_ALLOW_ELEMENT_NEGATIVE_WIDTH =
		JRPropertiesUtil.PROPERTY_PREFIX + "allow.element.negative.width";
	
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_1_1
			)
	public static final String PROPERTY_ALLOW_ELEMENT_NEGATIVE_X =
			JRPropertiesUtil.PROPERTY_PREFIX + "allow.element.negative.x";

	/**
	 * Property that determines whether elements positioned at negative Y offsets 
	 * on bands, frames and other element containers are allowed in a report.
	 *
	 * <p>
	 * Elements placed at negative Y offsets can cause unexpected problems in
	 * grid-based exporters where they can overlap elements from previous
	 * bands/element containers.
	 * </p>
	 * 
	 * <p>
	 * If the property is set to <code>false</code>, elements in the report are 
	 * verified to have positive Y offsets.  Otherwise, no check is performed
	 * on element Y offsets.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at element, report and global levels.
	 * By default the property is set to <code>true</code>.
	 * </p>
	 * 
	 * @see JRElement#getY()
	 * @since 3.7.3
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DESIGN,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_ALLOW_ELEMENT_NEGATIVE_Y =
		JRPropertiesUtil.PROPERTY_PREFIX + "allow.element.negative.y";

	/**
	 *
	 */
	private JasperReportsContext jasperReportsContext;
	private JasperDesign jasperDesign;
	private SectionTypeEnum sectionType;
	private Collection<JRValidationFault> brokenRules;

	private JRExpressionCollector expressionCollector;

	private LinkedList<JRComponentElement> currentComponentElementStack = new LinkedList<>();
	private LinkedList<String> datasetContextStack = new LinkedList<>();
	
	private boolean allowElementNegativeWidth;
	private final boolean allowElementNegativeX;
	private final boolean allowElementNegativeY;

	private JRVerifierVisitor verifierVisitor = new JRVerifierVisitor(this);

	/**
	 * 
	 */
	protected JRVerifier(
		JasperReportsContext jasperReportsContext,
		JasperDesign jasperDesign, 
		JRExpressionCollector expressionCollector
		)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.jasperDesign = jasperDesign;
		this.sectionType = SectionTypeEnum.getValueOrDefault(jasperDesign.getSectionType());
		brokenRules = new ArrayList<>();

		if (expressionCollector != null)
		{
			this.expressionCollector = expressionCollector;
		}
		else
		{
			this.expressionCollector = JRExpressionCollector.collector(jasperReportsContext, jasperDesign);
		}
		
		allowElementNegativeWidth = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(jasperDesign, PROPERTY_ALLOW_ELEMENT_NEGATIVE_WIDTH, false);
		allowElementNegativeX = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(jasperDesign, 
				PROPERTY_ALLOW_ELEMENT_NEGATIVE_X, true);
		allowElementNegativeY = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(jasperDesign, 
				PROPERTY_ALLOW_ELEMENT_NEGATIVE_Y, true);
	}

	public JasperDesign getReportDesign()
	{
		return jasperDesign;
	}
	
	/**
	 * Logs a broken rule for the report.
	 * 
	 * @param message the message
	 * @param source the source object to which the rule applies; can be null
	 * if not available
	 */
	public void addBrokenRule(String message, Object source)
	{
		addBrokenRule(brokenRules, message, source);
	}

	protected static void addBrokenRule(Collection<JRValidationFault> brokenRules, 
			String message, Object source)
	{
		JRValidationFault fault = new JRValidationFault();
		fault.setMessage(message);
		fault.setSource(source);
		brokenRules.add(fault);
	}

	/**
	 * Logs a broken report rule which was caused by an exception.
	 * 
	 * @param e the exception that caused the broken rule
	 * @param source the source object if available
	 */
	public void addBrokenRule(Exception e, Object source)
	{
		JRValidationFault fault = new JRValidationFault();
		fault.setMessage(e.getMessage());
		fault.setSource(source);
		brokenRules.add(fault);
	}

	/**
	 * Validates a {@link JasperDesign report design}.
	 *
	 * @param jasperDesign the report design
	 * @param expressionCollector a collector which was used to collect expressions from the report design;
	 * 	if null, a new collector will be created and used to collect the expressions
	 *
	 * @return a list of {@link JRValidationFault design faults};
	 * 	the report design is valid if and only if the list is empty
	 */
	public static Collection<JRValidationFault> verifyDesign(
		JasperReportsContext jasperReportsContext,
		JasperDesign jasperDesign, 
		JRExpressionCollector expressionCollector
		)
	{
		JRVerifier verifier = new JRVerifier(jasperReportsContext, jasperDesign, expressionCollector);
		return verifier.verifyDesign();
	}

	/**
	 * Validates a {@link JasperDesign report design}.
	 *
	 * @param jasperDesign the report design
	 *
	 * @return a list of {@link JRValidationFault design faults};
	 * 	the report design is valid if and only if the list is empty
	 */
	public static Collection<JRValidationFault> verifyDesign(JasperDesign jasperDesign)
	{
		return verifyDesign(DefaultJasperReportsContext.getInstance(), jasperDesign, null);
	}

	/**
	 *
	 */
	protected Collection<JRValidationFault> verifyDesign()
	{
		/*   */
		jasperDesign.preprocess();//FIXME either calculate twice or use change listeners

		/*   */
		verifyDesignAttributes();

		verifyReportTemplates();

		verifyDataset(jasperDesign.getMainDesignDataset());

		verifyDatasets();

		/*   */
		verifyStyles();

		if (toVerifyElementOverlap())
		{
			verifyEmptyBackground();
		}
		
		/*   */
		verifyBand(jasperDesign.getBackground());
		verifyBand(jasperDesign.getTitle());
		verifyBand(jasperDesign.getPageHeader());
		verifyBand(jasperDesign.getColumnHeader());
		verifySection(jasperDesign.getDetailSection());
		verifyBand(jasperDesign.getColumnFooter());
		verifyBand(jasperDesign.getPageFooter());
		verifyBand(jasperDesign.getLastPageFooter());
		verifyBand(jasperDesign.getSummary());
		verifyBand(jasperDesign.getNoData());

		return brokenRules;
	}


	protected void verifyEmptyBackground()
	{
		// if element overlapping checks are on, do not allow any content
		// in the background band
		JRBand background = jasperDesign.getBackground();
		if (background != null 
				&& background.getHeight() > 0)
		{
			JRElement[] elements = background.getElements();
			if (elements != null && elements.length > 0)
			{
				boolean foundContent = false;
				for (int i = 0; i < elements.length; i++)
				{
					if (elements[i].getWidth() > 0 && elements[i].getHeight() > 0)
					{
						foundContent = true;
						break;
					}
				}
				
				if (foundContent)
				{
					addBrokenRule("Use of the background section is not recommended " +
							"for reports that are supposed to be exported using grid exporters such as HTML and XLS " +
							"because the background content would likely be overlapped by other sections " +
							"resulting in it not showing up.", 
							background);
				}
			}
		}
	}


	/**
	 *
	 */
	private void verifyDesignAttributes()
	{
		if (jasperDesign.getName() == null || jasperDesign.getName().trim().length() == 0)
		{
			addBrokenRule("Report name is missing.", jasperDesign);
		}

		int columnCount = jasperDesign.getColumnCount() == null ? 1 : jasperDesign.getColumnCount();
		if (columnCount <= 0)
		{
			addBrokenRule("Column count must be greater than zero.", jasperDesign);
		}

		if (jasperDesign.getPageWidth() < 0)
		{
			addBrokenRule("Page width must be positive.", jasperDesign);
		}

		if (jasperDesign.getPageHeight() < 0)
		{
			addBrokenRule("Page height must be positive.", jasperDesign);
		}

		if (jasperDesign.getColumnWidth() < 0)
		{
			addBrokenRule("Column width must be positive.", jasperDesign);
		}

		if (jasperDesign.getColumnSpacing() < 0)
		{
			addBrokenRule("Column spacing must be positive.", jasperDesign);
		}

		if (jasperDesign.getLeftMargin() < 0)
		{
			addBrokenRule("Left margin must be positive.", jasperDesign);
		}

		if (jasperDesign.getRightMargin() < 0)
		{
			addBrokenRule("Right margin must be positive.", jasperDesign);
		}

		if (jasperDesign.getTopMargin() < 0)
		{
			addBrokenRule("Top margin must be positive.", jasperDesign);
		}

		if (jasperDesign.getBottomMargin() < 0)
		{
			addBrokenRule("Bottom margin must be positive.", jasperDesign);
		}

		if (
			jasperDesign.getLeftMargin() +
			columnCount * jasperDesign.getColumnWidth() +
			(columnCount - 1) * jasperDesign.getColumnSpacing() +
			jasperDesign.getRightMargin() >
			jasperDesign.getPageWidth()
			)
		{
			addBrokenRule("The columns and the margins do not fit the page width.", jasperDesign);
		}

		verifyBandHeights(brokenRules, jasperDesign,
				jasperDesign.getPageHeight(), 
				jasperDesign.getTopMargin(), jasperDesign.getBottomMargin());

		verifyFormatFactoryClass();
	}


	/**
	 * Validates that the report band heights fit on a page of certain size.
	 * 
	 * @param brokenRules the list of rules to which
	 * the validation failures are to be added
	 * @param report the report whose bands are to be validated
	 * @param pageHeight the height of the page
	 * @param topMargin the page top margin
	 * @param bottomMargin the page bottom margin
	 */
	public static void verifyBandHeights(Collection<JRValidationFault> brokenRules, JRReport report,
			int pageHeight, int topMargin, int bottomMargin)
	{
		if (
			topMargin +
			(report.getBackground() != null ? report.getBackground().getHeight() : 0) +
			bottomMargin >
			pageHeight
			)
		{
			addBrokenRule(brokenRules, 
					"The background section and the margins do not fit the page height.", 
					report);
		}

		if (report.isTitleNewPage())
		{
			if (
				topMargin +
				getBreakHeight(report.getTitle()) +
				bottomMargin >
				pageHeight
				)
			{
				addBrokenRule(brokenRules, 
						"The title section and the margins do not fit the page height.", 
						report);
			}
		}
		else
		{
			if (
				topMargin +
				getBreakHeight(report.getTitle()) +
				(report.getPageHeader() != null ? report.getPageHeader().getHeight() : 0) +
				(report.getColumnHeader() != null ? report.getColumnHeader().getHeight() : 0) +
				(report.getColumnFooter() != null ? report.getColumnFooter().getHeight() : 0) +
				(report.getPageFooter() != null ? report.getPageFooter().getHeight() : 0) +
				bottomMargin >
				pageHeight
				)
			{
				addBrokenRule(brokenRules, 
						"The title section, the page and column headers and footers and the margins do not fit the page height.", 
						report);
			}
		}

		if (
			topMargin +
			(report.getPageHeader() != null ? report.getPageHeader().getHeight() : 0) +
			(report.getColumnHeader() != null ? report.getColumnHeader().getHeight() : 0) +
			(report.getColumnFooter() != null ? report.getColumnFooter().getHeight() : 0) +
			(report.getPageFooter() != null ? report.getPageFooter().getHeight() : 0) +
			bottomMargin >
			pageHeight
			)
		{
			addBrokenRule(brokenRules, 
					"The page and column headers and footers and the margins do not fit the page height.", 
					report);
		}

		if (
			topMargin +
			(report.getPageHeader() != null ? report.getPageHeader().getHeight() : 0) +
			(report.getColumnHeader() != null ? report.getColumnHeader().getHeight() : 0) +
			(report.getColumnFooter() != null ? report.getColumnFooter().getHeight() : 0) +
			(report.getLastPageFooter() != null ? report.getLastPageFooter().getHeight() : 0) +
			bottomMargin >
			pageHeight
			)
		{
			addBrokenRule(brokenRules,
					"The page and column headers and footers and the margins do not fit the last page height.", 
					report);
		}

		if (
			topMargin +
			getBreakHeight(report.getSummary()) +
			bottomMargin >
			pageHeight
			)
		{
			addBrokenRule(brokenRules,
					"The summary section and the margins do not fit the page height.", 
					report);
		}

		JRSection detailSection = report.getDetailSection();
		if (detailSection != null)
		{
			JRBand[] detailBands = detailSection.getBands();
			if (detailBands != null && detailBands.length > 0)
			{
				for(int i = 0; i< detailBands.length; i++)
				{
					JRBand detailBand = detailBands[i];
					if (
						topMargin +
						(report.getPageHeader() != null ? report.getPageHeader().getHeight() : 0) +
						(report.getColumnHeader() != null ? report.getColumnHeader().getHeight() : 0) +
						getBreakHeight(detailBand) +
						(report.getColumnFooter() != null ? report.getColumnFooter().getHeight() : 0) +
						(report.getPageFooter() != null ? report.getPageFooter().getHeight() : 0) +
						bottomMargin >
						pageHeight
						)
					{
						addBrokenRule(brokenRules,
								"The detail section, the page and column headers and footers and the margins do not fit the page height.", 
								report);
					}
				}
			}
		}

		if (
				topMargin +
				getBreakHeight(report.getNoData()) +
				bottomMargin >
				pageHeight
				)
		{
			addBrokenRule(brokenRules,
					"The noData section and the margins do not fit the page height.", 
					report);
		}
	}


	protected void verifyFormatFactoryClass()
	{
		String formatFactoryClassName = jasperDesign.getFormatFactoryClass();
		if (formatFactoryClassName != null)
		{
			try
			{
				Class<?> formatFactoryClass = JRClassLoader.loadClassForName(formatFactoryClassName);
				if (!FormatFactory.class.isAssignableFrom(formatFactoryClass))
				{
					addBrokenRule("The report format factory class is not compatible with " + FormatFactory.class.getName(), jasperDesign);
				}
			}
			catch (ClassNotFoundException e)
			{
				addBrokenRule(e.toString(), jasperDesign);
			}
		}
	}


	/**
	 *
	 */
	private void verifyQuery(JRDesignDataset dataset)
	{
		JRQuery query = dataset.getQuery();
		if (query != null)
		{
			String language = query.getLanguage();
			QueryExecuterFactory queryExecuterFactory = null;
			if (language == null || language.length() == 0)
			{
				addBrokenRule("Query language not set.", query);
			}
			else
			{
				try
				{
					queryExecuterFactory = JRQueryExecuterUtils.getInstance(jasperReportsContext).getExecuterFactory(query.getLanguage());
				}
				catch (JRException e1)
				{
					addBrokenRule("Query executer factory for " + language + " cannot be created.", query);
				}
			}


			JRQueryChunk[] chunks = query.getChunks();
			if (chunks != null && chunks.length > 0)
			{
				Map<String, JRParameter> parametersMap = dataset.getParametersMap();

				for(int j = 0; j < chunks.length; j++)
				{
					JRQueryChunk queryChunk = chunks[j];
					switch (queryChunk.getType())
					{
						case JRQueryChunk.TYPE_PARAMETER :
						{
							JRParameter parameter = parametersMap.get(queryChunk.getText());
							if ( parameter == null )
							{
								addBrokenRule("Query parameter not found : " + queryChunk.getText(), query);
							}
							else if (queryExecuterFactory != null)
							{
								String parameterType = null;
								try
								{
									parameterType = parameter.getValueClassName();
								}
								catch (JRRuntimeException e)
								{
									// ignore, already added when the parameter got verified
								}
								if (parameterType != null && !queryExecuterFactory.supportsQueryParameterType(parameterType))
								{
									addBrokenRule("Parameter type not supported in query : " + queryChunk.getText() + " class " + parameterType, query);
								}
							}

							break;
						}
						case JRQueryChunk.TYPE_PARAMETER_CLAUSE :
						{
							if (!parametersMap.containsKey(queryChunk.getText()))
							{
								addBrokenRule("Query parameter not found : " + queryChunk.getText(), query);
							}
							break;
						}
						case JRQueryChunk.TYPE_TEXT :
						default :
						{
						}
					}
				}
			}
		}
	}


	/**
	 *
	 */
	private void verifyExpressions(
			List<JRExpression> expressions, 
			Map<String, ? extends JRParameter> parametersMap, 
			Map<String, JRField> fieldsMap, 
			Map<String, JRVariable> variablesMap
			)
	{
		if (expressions != null && expressions.size() > 0)
		{
			for(Iterator<JRExpression> it = expressions.iterator(); it.hasNext();)
			{
				JRExpression expression = it.next();
				verifyExpression(expression, parametersMap, fieldsMap, variablesMap, brokenRules);
			}
		}
	}


	/**
	 *
	 */
	public static void verifyExpression(
			JRExpression expression, 
			Map<String, ? extends JRParameter> parametersMap, 
			Map<String, JRField> fieldsMap, 
			Map<String, JRVariable> variablesMap,
			Collection<JRValidationFault> brokenRules
			)
	{
		JRExpressionChunk[] chunks = expression.getChunks();
		if (chunks != null && chunks.length > 0)
		{
			for(int j = 0; j < chunks.length; j++)
			{
				JRExpressionChunk expressionChunk = chunks[j];
				switch (expressionChunk.getType())
				{
					case JRExpressionChunk.TYPE_VARIABLE :
					{
						if ( !variablesMap.containsKey(expressionChunk.getText()) )
						{
							addBrokenRule(brokenRules, "Variable not found : " + expressionChunk.getText(), expression);
						}
						break;
					}
					case JRExpressionChunk.TYPE_FIELD :
					{
						if ( !fieldsMap.containsKey(expressionChunk.getText()) )
						{
							addBrokenRule(brokenRules, "Field not found : " + expressionChunk.getText(), expression);
						}
						break;
					}
					case JRExpressionChunk.TYPE_PARAMETER :
					{
						if ( !parametersMap.containsKey(expressionChunk.getText()) )
						{
							addBrokenRule(brokenRules, "Parameter not found : " + expressionChunk.getText(), expression);
						}
						break;
					}
					case JRExpressionChunk.TYPE_RESOURCE :
					case JRExpressionChunk.TYPE_TEXT :
					default :
					{
					}
				}
			}
		}
	}


	private void verifyExpressions(JRDesignDataset dataset)
	{
		verifyExpressions(
				expressionCollector.getExpressions(dataset),
				dataset.getParametersMap(),
				dataset.getFieldsMap(),
				dataset.getVariablesMap());
	}


	protected void verifyReportTemplates()
	{
		JRReportTemplate[] templates = jasperDesign.getTemplates();
		if (templates != null)
		{
			for (int i = 0; i < templates.length; i++)
			{
				JRReportTemplate template = templates[i];
				verifyTemplate(template);
			}
		}
	}


	protected void verifyTemplate(JRReportTemplate template)
	{
		JRExpression sourceExpression = template.getSourceExpression();
		if (sourceExpression == null)
		{
			addBrokenRule("Template source expression missing.", template);
		}
	}


	/**
	 *
	 */
	private void verifyStyles()
	{
		JRStyle[] styles = jasperDesign.getStyles();
		if (styles != null && styles.length > 0)
		{
			for(int index = 0; index < styles.length; index++)
			{
				JRStyle style = styles[index];

				if (style.getName() == null || style.getName().trim().length() == 0)
				{
					addBrokenRule("Report style name missing.", style);
				}
				
				verifyConditionalStyles(style);
			}
		}
	}

	/**
	 *
	 */
	private void verifyConditionalStyles(JRStyle style)
	{
		JRConditionalStyle[] condStyles = style.getConditionalStyles();
		if (condStyles != null && condStyles.length > 0)
		{
			for(int index = 0; index < condStyles.length; index++)
			{
				JRConditionalStyle condStyle = condStyles[index];

				if (log.isWarnEnabled())
				{
					if (condStyle.getName() != null)
					{
						log.warn("Conditional style should not have a name.");
					}

					if (condStyle.isDefault())
					{
						log.warn("Conditional style can't be the default style.");
					}
				}
			}
		}
	}


	/**
	 *
	 */
	private void verifyParameters(JRDesignDataset dataset)
	{
		JRParameter[] parameters = dataset.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for(int index = 0; index < parameters.length; index++)
			{
				JRParameter parameter = parameters[index];

				Object errorSource = parameter;
				if (parameter.isSystemDefined())
				{
					errorSource = jasperDesign;
				}

				if (parameter.getName() == null || parameter.getName().trim().length() == 0)
				{
					addBrokenRule("Parameter name missing.", errorSource);
				}

				if (parameter.getValueClassName() == null)
				{
					addBrokenRule("Class not set for parameter : " + parameter.getName(), errorSource);
				}
			}
		}
	}


	/**
	 *
	 */
	private void verifyFields(JRDesignDataset dataset)
	{
		JRField[] fields = dataset.getFields();
		if (fields != null && fields.length > 0)
		{
			for(int index = 0; index < fields.length; index++)
			{
				JRField field = fields[index];

				if (field.getName() == null || field.getName().trim().length() == 0)
				{
					addBrokenRule("Field name missing.", field);
				}

				try
				{
					Class<?> fieldType = field.getValueClass();
					if (fieldType == null)
					{
						addBrokenRule("Class not set for field : " + field.getName(), field);
					}
				}
				catch (JRRuntimeException e)
				{
					addBrokenRule(e, field);
				}
				
				verifyProperyExpressions(field.getPropertyExpressions());
			}
		}
	}


	/**
	 *
	 */
	private void verifySortFields(JRDesignDataset dataset)
	{
		JRField[] fields = dataset.getFields();
		JRVariable[] variables = dataset.getVariables();
		JRSortField[] sortFields = dataset.getSortFields();
		if (sortFields != null && sortFields.length > 0)
		{
			for(int index = 0; index < sortFields.length; index++)
			{
				JRSortField sortField = sortFields[index];
				String sortFieldName = sortField.getName();

				if (sortFieldName == null || sortFieldName.trim().length() == 0)
				{
					addBrokenRule("Sort field name missing.", sortField);
				}
				else
				{
					boolean isFound = false;

					if (sortField.getType() == SortFieldTypeEnum.VARIABLE)
					{
						if (variables != null)
						{
							int j = 0;
							while (!isFound && j < variables.length)
							{
								isFound = sortFieldName.equals(variables[j].getName());
								j++;
							}
						}
					}
					else
					{
						if (fields != null)
						{
							int j = 0;
							while (!isFound && j < fields.length)
							{
								isFound = sortFieldName.equals(fields[j].getName());
								j++;
							}
						}
					}

					if (!isFound)
					{
						addBrokenRule("Sort " + SortFieldTypeEnum.getValueOrDefault(sortField.getType()).getName().toLowerCase() + " \'" + sortFieldName + "\' not found in dataset.", sortField);
					}
				}
			}
		}
	}


	/**
	 *
	 */
	private void verifyVariables(JRDesignDataset dataset) throws JRRuntimeException
	{
		JRVariable[] variables = dataset.getVariables();
		if (variables != null && variables.length > 0)
		{
			for(int index = 0; index < variables.length; index++)
			{
				JRVariable variable = variables[index];

				if (variable.getName() == null || variable.getName().trim().length() == 0)
				{
					addBrokenRule("Variable name missing.", variable);
				}

				try
				{
					Class<?> valueClass = variable.getValueClass();
					if (valueClass == null)
					{
						addBrokenRule("Class not set for variable : " + variable.getName(), variable);
					}
				}
				catch (JRRuntimeException e)
				{
					addBrokenRule(e, variable);
				}

				ResetTypeEnum resetType = variable.getResetType();
				if (resetType == ResetTypeEnum.GROUP)
				{
					if (variable.getResetGroup() == null)
					{
						addBrokenRule("Reset group missing for variable : " + variable.getName(), variable);
					}
					else
					{
						Map<String,JRGroup> groupsMap = dataset.getGroupsMap();

						if (!groupsMap.containsKey(variable.getResetGroup()))
						{
							addBrokenRule("Reset group \"" + variable.getResetGroup() + "\" not found for variable : " + variable.getName(), variable);
						}
					}
				}

				IncrementTypeEnum incrementType = variable.getIncrementType();
				if (incrementType == IncrementTypeEnum.GROUP)
				{
					if (variable.getIncrementGroup() == null)
					{
						addBrokenRule("Increment group missing for variable : " + variable.getName(), variable);
					}
					else
					{
						Map<String,JRGroup> groupsMap = dataset.getGroupsMap();

						if (!groupsMap.containsKey(variable.getIncrementGroup()))
						{
							addBrokenRule("Increment group \"" + variable.getIncrementGroup() + "\" not found for variable : " + variable.getName(), variable);
						}
					}
				}
			}
		}
	}


	/**
	 *
	 */
	private void verifyGroups(JRDesignDataset dataset)
	{
		JRGroup[] groups = dataset.getGroups();
		if (groups != null && groups.length > 0)
		{
			boolean isMainDataset = dataset.isMainDataset();
			for(int index = 0; index < groups.length; index++)
			{
				JRGroup group = groups[index];

				if (group.getName() == null || group.getName().trim().length() == 0)
				{
					addBrokenRule("Group name missing.", group);
				}

				if (isMainDataset)
				{
					verifyGroupHeaderAndFooter(group);
				}
				else
				{
					if (
						(group.getGroupHeaderSection() != null 
						&& group.getGroupHeaderSection().getBands() != null 
						&& group.getGroupHeaderSection().getBands().length > 0) 
						|| (group.getGroupFooterSection() != null
						&& group.getGroupFooterSection().getBands() != null
						&& group.getGroupFooterSection().getBands().length > 0)
						)
					{
						addBrokenRule("Group " + group.getName() + " cannot have header or footer sections.", group);
					}
				}

				if (isMainDataset)
				{
					verifySection(group.getGroupHeaderSection());
					verifySection(group.getGroupFooterSection());
				}
			}
		}
	}


	private void verifyGroupHeaderAndFooter(JRGroup group)
	{
		if (jasperDesign.isTitleNewPage())
		{
			JRSection groupHeaderSection = group.getGroupHeaderSection();
			if (groupHeaderSection != null)
			{
				JRBand[] groupHeaderBands = groupHeaderSection.getBands();
				if (groupHeaderBands != null && groupHeaderBands.length > 0)
				{
					for(int i = 0; i< groupHeaderBands.length; i++)
					{
						JRBand groupHeaderBand = groupHeaderBands[i];
						if (
							jasperDesign.getTopMargin() +
							(jasperDesign.getPageHeader() != null ? jasperDesign.getPageHeader().getHeight() : 0) +
							(jasperDesign.getColumnHeader() != null ? jasperDesign.getColumnHeader().getHeight() : 0) +
							getBreakHeight(groupHeaderBand) +
							(jasperDesign.getColumnFooter() != null ? jasperDesign.getColumnFooter().getHeight() : 0) +
							(jasperDesign.getPageFooter() != null ? jasperDesign.getPageFooter().getHeight() : 0) +
							jasperDesign.getBottomMargin() >
							jasperDesign.getPageHeight()
							)
						{
							addBrokenRule("The '" + group.getName() + "' group header section, the page and column headers and footers and the margins do not fit the page height.", groupHeaderBand);
						}
					}
				}
			}

			JRSection groupFooterSection = group.getGroupFooterSection();
			if (groupFooterSection != null)
			{
				JRBand[] groupFooterBands = groupFooterSection.getBands();
				if (groupFooterBands != null && groupFooterBands.length > 0)
				{
					for(int i = 0; i< groupFooterBands.length; i++)
					{
						JRBand groupFooterBand = groupFooterBands[i];
						if (
							jasperDesign.getTopMargin() +
							(jasperDesign.getPageHeader() != null ? jasperDesign.getPageHeader().getHeight() : 0) +
							(jasperDesign.getColumnHeader() != null ?  jasperDesign.getColumnHeader().getHeight() : 0) +
							getBreakHeight(groupFooterBand) +
							(jasperDesign.getColumnFooter() != null ? jasperDesign.getColumnFooter().getHeight() : 0) +
							(jasperDesign.getPageFooter() != null ? jasperDesign.getPageFooter().getHeight() : 0) +
							jasperDesign.getBottomMargin() >
							jasperDesign.getPageHeight()
							)
						{
							addBrokenRule("The '" + group.getName() + "' group footer section, the page and column headers and footers and the margins do not fit the page height.", groupFooterBand);
						}
					}
				}
			}
		}
		else
		{
			JRSection groupHeaderSection = group.getGroupHeaderSection();
			if (groupHeaderSection != null)
			{
				JRBand[] groupHeaderBands = groupHeaderSection.getBands();
				if (groupHeaderBands != null && groupHeaderBands.length > 0)
				{
					for(int i = 0; i< groupHeaderBands.length; i++)
					{
						JRBand groupHeaderBand = groupHeaderBands[i];
						if (
							jasperDesign.getTopMargin() +
							(jasperDesign.getTitle() != null ? jasperDesign.getTitle().getHeight() : 0) +
							(jasperDesign.getPageHeader() != null ? jasperDesign.getPageHeader().getHeight() : 0) +
							(jasperDesign.getColumnHeader() != null ? jasperDesign.getColumnHeader().getHeight() : 0) +
							getBreakHeight(groupHeaderBand) +
							(jasperDesign.getColumnFooter() != null ? jasperDesign.getColumnFooter().getHeight() : 0) +
							(jasperDesign.getPageFooter() != null ? jasperDesign.getPageFooter().getHeight() : 0) +
							jasperDesign.getBottomMargin() >
							jasperDesign.getPageHeight()
							)
						{
							addBrokenRule("The '" + group.getName() + "' group header section, the title, the page and column headers and footers and the margins do not fit the first page height.", groupHeaderBand);
						}
					}
				}
			}

			JRSection groupFooterSection = group.getGroupFooterSection();
			if (groupFooterSection != null)
			{
				JRBand[] groupFooterBands = groupFooterSection.getBands();
				if (groupFooterBands != null && groupFooterBands.length > 0)
				{
					for(int i = 0; i< groupFooterBands.length; i++)
					{
						JRBand groupFooterBand = groupFooterBands[i];
						if (
							jasperDesign.getTopMargin() +
							(jasperDesign.getTitle() != null ? jasperDesign.getTitle().getHeight() : 0) +
							(jasperDesign.getPageHeader() != null ? jasperDesign.getPageHeader().getHeight() : 0) +
							(jasperDesign.getColumnHeader() != null ? jasperDesign.getColumnHeader().getHeight() : 0) +
							getBreakHeight(groupFooterBand) +
							(jasperDesign.getColumnFooter() != null ? jasperDesign.getColumnFooter().getHeight() : 0) +
							(jasperDesign.getPageFooter() != null ? jasperDesign.getPageFooter().getHeight() : 0) +
							jasperDesign.getBottomMargin() >
							jasperDesign.getPageHeight()
							)
						{
							addBrokenRule("The '" + group.getName() + "' group footer section, the title, the page and column headers and footers and the margins do not fit the first page height.", groupFooterBand);
						}
					}
				}
			}
		}
	}

	protected boolean toVerifyElementOverlap()
	{
		return !JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(jasperDesign, 
				PROPERTY_ALLOW_ELEMENT_OVERLAP, 
				true);
	}

	protected boolean isAllowedToOverlap(JRElement element)
	{
		// check whether the element has been marked to allow to overwrite
		return element.hasProperties()
			&& JRPropertiesUtil.asBoolean(element.getPropertiesMap().getProperty(
							PROPERTY_ALLOW_ELEMENT_OVERLAP));
	}
	

	protected void verifyElementOverlap(JRElement element1, JRElement element2)
	{
		if (element1.getWidth() <= 0 || element1.getHeight() <= 0
				|| element2.getWidth() <= 0 || element2.getHeight() <= 0)
		{
			// no-space element -> no overlap
			return;
		}
		
		if ((element1.getX() < element2.getX() + element2.getWidth()
				&& element2.getX() < element1.getX() + element1.getWidth())
				&& (element1.getY() < element2.getY() + element2.getHeight()
						&& element2.getY() < element1.getY() + element1.getHeight()))
		{
			// we have an overlap
			StringBuilder message = new StringBuilder();
			message.append("Element ");
			if (element2.getKey() != null)
			{
				message.append("\"");
				message.append(element2.getKey());
				message.append("\" ");
			}
			message.append("at ");
			message.append(getElementPositionText(element2));
			message.append(" overlaps element ");
			if (element1.getKey() != null)
			{
				message.append("\"");
				message.append(element1.getKey());
				message.append("\" ");
			}
			message.append("at ");
			message.append(getElementPositionText(element1));
			
			// using the element on top (in z-order) as source 
			addBrokenRule(message.toString(), element2);
		}
	}

	protected String getElementPositionText(JRElement element)
	{
		return "[x = " + element.getX()
			+ ", y = " + element.getY()
			+ ", width = " + element.getWidth()
			+ ", height = " + element.getHeight()
			+ "]";
	}
	
	protected void verifyElementsOverlap(JRElement[] elements)
	{
		if (!toVerifyElementOverlap())
		{
			return;
		}
		
		for(int index = 1; index < elements.length; index++)
		{
			JRElement element = elements[index];
			if (!isAllowedToOverlap(element))
			{
				for (int overlapIndex = 0; overlapIndex < index; ++overlapIndex)
				{
					if (!isAllowedToOverlap(elements[overlapIndex]))
					{
						verifyElementOverlap(elements[overlapIndex], element);
					}
				}
			}
		}
	}

	
	/**
	 *
	 */
	private void verifySection(JRSection section)
	{
		if (section != null)
		{
			JRBand[] bands = section.getBands();
			if (bands != null && bands.length > 0)
			{
				if (sectionType == SectionTypeEnum.PART)
				{
					addBrokenRule("Part reports cannot contain bands", section);
				}
				else
				{
					for(int i = 0; i< bands.length; i++)
					{
						verifyBand(bands[i]);
					}
				}
			}
			
			JRPart[] parts = section.getParts();
			if (parts != null && parts.length > 0)
			{
				if (sectionType == SectionTypeEnum.BAND)
				{
					addBrokenRule("Band reports cannot contain parts", section);
				}
				else
				{
					for (int i = 0; i < parts.length; i++)
					{
						verifyPart(parts[i]);
					}
				}
			}
		}
	}


	/**
	 *
	 */
	private void verifyBand(JRBand band)
	{
		if (band != null)
		{
			if (sectionType == SectionTypeEnum.PART)
			{
				addBrokenRule("Part reports cannot contain bands", band);
				return;
			}
			
			JRElement[] elements = band.getElements();
			if (elements != null && elements.length > 0)
			{
				for(int index = 0; index < elements.length; index++)
				{
					JRElement element = elements[index];

					/*
					if (element.getY() < 0)
					{
//						if (log.isWarnEnabled())
//							log.warn(
								"Warning : Element placed outside band area : y=" + element.getY()
								);
						//addBrokenRule("Element placed outside band area.");
					}
					else if (element.getY() + element.getHeight() > band.getHeight())
					*/
					if (element.getY() + element.getHeight() > band.getHeight())
					{
//						if (log.isWarnEnabled())
//							log.warn(
//								"Warning : Element bottom reaches outside band area : y=" + element.getY() +
//								" height=" + element.getHeight() +
//								" band-height=" + band.getHeight()
//								);
						addBrokenRule(
							"Warning : Element bottom reaches outside band area : y=" + element.getY() +
							" height=" + element.getHeight() +
							" band-height=" + band.getHeight(),
							element
							);
					}

					verifyElement(element);
				}
				
				verifyElementsOverlap(elements);
			}

			List<ExpressionReturnValue> returnValues = band.getReturnValues();
			if (returnValues != null && !returnValues.isEmpty())
			{
				for (ExpressionReturnValue returnValue : returnValues)
				{
					verifyReturnValue(returnValue);
				}
			}
		}
	}


	public void verifyElement(JRElement element)
	{
		element.visit(verifierVisitor);
	}


	/**
	 *
	 */
	protected void verifyStaticText(JRStaticText staticText)
	{
		verifyReportElement(staticText);
	}


	/**
	 *
	 */
	protected void verifyTextField(JRTextField textField)
	{
		verifyReportElement(textField);
		verifyAnchor(textField);
		verifyHyperlink(textField);
	}


	/**
	 *
	 */
	private void verifyAnchor(JRAnchor anchor)
	{
		if (anchor != null)
		{
			if (anchor.getBookmarkLevel() != JRAnchor.NO_BOOKMARK && anchor.getBookmarkLevel() < 1)
			{
				addBrokenRule("Bookmark level should be " + JRAnchor.NO_BOOKMARK + " or greater than 0", anchor);
			}
		}
	}


	/**
	 *
	 */
	public void verifyHyperlink(JRHyperlink hyperlink)
	{
		if (hyperlink != null)
		{
			JRHyperlinkParameter[] parameters = hyperlink.getHyperlinkParameters();
			if (parameters != null)
			{
				for (int i = 0; i < parameters.length; i++)
				{
					JRHyperlinkParameter parameter = parameters[i];
					verifyHyperlinkParameter(parameter);
				}
			}
		}
	}


	protected void verifyHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		if (parameter != null)
		{
			String name = parameter.getName();
			if (name == null || name.length() == 0)
			{
				addBrokenRule("Hyperlink parameter name missing.", parameter);
			}
		}
	}


	/**
	 *
	 */
	protected void verifyImage(JRImage image)
	{
		verifyReportElement(image);
		verifyAnchor(image);
		verifyHyperlink(image);
	}


	/**
	 *
	 */
	protected void verifySubreport(JRSubreport subreport)
	{
		if (subreport != null)
		{
			verifyReportElement(subreport);
			
			JRSubreportParameter[] parameters = subreport.getParameters();
			if (parameters != null && parameters.length > 0)
			{
				for(int index = 0; index < parameters.length; index++)
				{
					JRSubreportParameter parameter = parameters[index];

					if (parameter.getName() == null || parameter.getName().trim().length() == 0)
					{
						addBrokenRule("Subreport parameter name missing.", parameter);
					}
				}
			}

			if (
				subreport.getConnectionExpression() != null &&
				subreport.getDataSourceExpression() != null
				)
			{
				addBrokenRule("Subreport cannot have both connection expresion and data source expression.", subreport);
			}

			JRSubreportReturnValue[] returnValues = subreport.getReturnValues();
			if (returnValues != null && returnValues.length > 0)
			{
				for (int i = 0; i < returnValues.length; i++)
				{
					JRSubreportReturnValue returnValue = returnValues[i];

					if (returnValue.getFromVariable() == null || returnValue.getFromVariable().trim().length() == 0)
					{
						addBrokenRule("Subreport return value variable name missing.", returnValue);
					}

					if (returnValue.getToVariable() == null || returnValue.getToVariable().trim().length() == 0)
					{
						addBrokenRule("Subreport return value to variable name missing.", returnValue);
					}

					JRDesignDataset dataset = currentDataset();
					if (dataset == null || !dataset.getVariablesMap().containsKey(returnValue.getToVariable()))
					{
						addBrokenRule("Subreport return value to variable not found.", returnValue);
					}
				}
			}
		}
	}

	protected void verifyReturnValue(VariableReturnValue returnValue)
	{
		if (returnValue.getFromVariable() == null || returnValue.getFromVariable().trim().length() == 0)
		{
			addBrokenRule("Return value source variable name missing.", returnValue);
		}

		verifyCommonReturnValue(returnValue);
	}


	protected void verifyReturnValue(ExpressionReturnValue returnValue)
	{
		if (returnValue.getExpression() == null)
		{
			addBrokenRule("Return value expression missing.", returnValue);
		}

		verifyCommonReturnValue(returnValue);
	}


	protected void verifyCommonReturnValue(CommonReturnValue returnValue)
	{
		if (returnValue.getToVariable() == null || returnValue.getToVariable().trim().length() == 0)
		{
			addBrokenRule("Return value destination variable name missing.", returnValue);
		}

		JRDesignDataset dataset = currentDataset();
		if (dataset == null || !dataset.getVariablesMap().containsKey(returnValue.getToVariable()))
		{
			addBrokenRule("Return value destination variable not found.", returnValue);
		}
	}


	protected void verifyCrosstab(JRDesignCrosstab crosstab)
	{
		verifyReportElement(crosstab);
		verifyParameters(crosstab);

		JRCrosstabDataset dataset = crosstab.getDataset();
		if (dataset == null)
		{
			addBrokenRule("Crosstab dataset missing.", crosstab);
		}
		else
		{
			verifyElementDataset(dataset);
		}

		if (crosstab.getTitleCell() != null)
		{
			verifyCellContents(crosstab.getTitleCell().getCellContents(), "crosstab title cell");
		}
		
		verifyCellContents(crosstab.getHeaderCell(), "crosstab cell");

		JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
		if (rowGroups == null || rowGroups.length == 0)
		{
			addBrokenRule("Crosstab should have at least one row group.", crosstab);
		}
		else
		{
			for (int i = 0; i < rowGroups.length; i++)
			{
				verifyCrosstabRowGroup(rowGroups[i]);
				
				if (i + 1 < rowGroups.length)
				{
					verifyCrosstabNextGroup(rowGroups[i], rowGroups[i + 1]);
				}
			}
		}

		JRCrosstabColumnGroup[] colGroups = crosstab.getColumnGroups();
		if (colGroups == null || colGroups.length == 0)
		{
			addBrokenRule("Crosstab should have at least one column group.", crosstab);
		}
		else
		{
			for (int i = 0; i < colGroups.length; i++)
			{
				verifyCrosstabColumnGroup(colGroups[i]);
				
				if (i + 1 < colGroups.length)
				{
					verifyCrosstabNextGroup(colGroups[i], colGroups[i + 1]);
				}
			}
		}

		JRCrosstabMeasure[] measures = crosstab.getMeasures();
		if (measures == null || measures.length == 0)
		{
			addBrokenRule("Crosstab should have at least one measure.", crosstab);
		}
		else
		{
			for (int i = 0; i < measures.length; i++)
			{
				verifyCrosstabMeasure(measures[i]);
			}
		}

		verifyCrosstabCells(crosstab);

		verifyCellContents(crosstab.getWhenNoDataCell(), "when no data cell");

		verifyExpressions(crosstab);
	}

	protected void verifyCrosstabNextGroup(JRCrosstabGroup group, JRCrosstabGroup nextGroup)
	{
		if (Boolean.FALSE.equals(group.getMergeHeaderCells())
				&& CrosstabTotalPositionEnum.getValueOrDefault(nextGroup.getTotalPosition()) != CrosstabTotalPositionEnum.NONE)
		{
			addBrokenRule("Row crosstab group has repeating header cells but the next group has a total row",
					group);
		}
	}

	private void verifyParameters(JRDesignCrosstab crosstab)
	{
		JRCrosstabParameter[] parameters = crosstab.getParameters();
		if (parameters != null)
		{
			for (int i = 0; i < parameters.length; i++)
			{
				JRCrosstabParameter parameter = parameters[i];

				String paramName = parameter.getName();
				if (paramName == null || paramName.length() == 0)
				{
					addBrokenRule("Missing parameter name for crosstab.", parameter);
				}

				try
				{
					Class<?> valueClass = parameter.getValueClass();
					if (valueClass == null)
					{
						addBrokenRule("Class not set for crosstab parameter " + paramName + ".", parameter);
					}
				}
				catch (Exception e)
				{
					addBrokenRule(e, parameter);
				}
			}
		}
	}


	private void verifyCrosstabRowGroup(JRCrosstabRowGroup group)
	{
		verifyCrosstabGroup(group);
	}


	private void verifyCrosstabColumnGroup(JRCrosstabColumnGroup group)
	{
		verifyCrosstabGroup(group);
		
		verifyCellContents(group.getCrosstabHeader(), group.getName() + " crosstab header");
	}


	private void verifyCrosstabGroup(JRCrosstabGroup group)
	{
		String groupName = group.getName();
		if (groupName == null || groupName.length() == 0)
		{
			addBrokenRule("Crosstab group name missing.", group);
		}

		verifyCrosstabBucket(group);
		verifyCellContents(group.getHeader(), groupName + " header");
		if (group.hasTotal())
		{
			verifyCellContents(group.getTotalHeader(), groupName + " total header");
		}
	}


	private void verifyCrosstabBucket(JRCrosstabGroup group)
	{
		JRCrosstabBucket bucket = group.getBucket();

		try
		{
			Class<?> valueClass = bucket.getValueClass();
			if (valueClass == null)
			{
				addBrokenRule("Class not set for bucket : " + group.getName(), bucket);
			}
		}
		catch (JRRuntimeException e)
		{
			addBrokenRule(e, bucket);
		}

		JRExpression expression = bucket.getExpression();
		if (expression == null)
		{
			addBrokenRule("Crosstab bucket expression missing for group " + group.getName() + ".", bucket);
		}
	}


	private void verifyCrosstabCells(JRDesignCrosstab crosstab)
	{
		JRCrosstabCell[][] cells = crosstab.getCells();
		JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
		JRCrosstabColumnGroup[] columnGroups = crosstab.getColumnGroups();

		JRCrosstabCell baseCell = cells[rowGroups.length][columnGroups.length];
		if(baseCell == null || baseCell.getWidth() == null)
		{
			addBrokenRule("Crosstab base cell width not specified.", crosstab);
		}

		if(baseCell == null || baseCell.getHeight() == null)
		{
			addBrokenRule("Crosstab base cell height not specified.", crosstab);
		}

		for (int i = rowGroups.length; i >= 0 ; --i)
		{
			for (int j = columnGroups.length; j >= 0 ; --j)
			{
				JRCrosstabCell cell = cells[i][j];

				String cellText = getCrosstabCellText(rowGroups, columnGroups, i, j);

				if (cell != null)
				{
					JRCellContents contents = cell.getContents();

					if (i < rowGroups.length)
					{
						JRCrosstabCell colCell = cells[rowGroups.length][j];
						if (colCell != null && colCell.getContents().getWidth() != contents.getWidth())
						{
							addBrokenRule("Crosstab " + cellText + " width should be " + colCell.getContents().getWidth() + ".", cell);
						}
					}

					if (j < columnGroups.length)
					{
						JRCrosstabCell rowCell = cells[i][columnGroups.length];
						if (rowCell != null && rowCell.getContents().getHeight() != contents.getHeight())
						{
							addBrokenRule("Crosstab " + cellText + " height should be " + rowCell.getContents().getHeight() + ".", cell);
						}
					}

					verifyCellContents(contents, cellText);
				}
			}
		}
	}

	private String getCrosstabCellText(JRCrosstabRowGroup[] rowGroups, JRCrosstabColumnGroup[] columnGroups,
			int rowIndex, int columnIndex)
	{
		String text;

		if (rowIndex == rowGroups.length)
		{
			if (columnIndex == columnGroups.length)
			{
				text = "cell";
			}
			else
			{
				text = columnGroups[columnIndex].getName() + " total cell";
			}
		}
		else
		{
			if (columnIndex == columnGroups.length)
			{
				text = rowGroups[rowIndex].getName() + " total cell";
			}
			else
			{
				text = rowGroups[rowIndex].getName() + "," + columnGroups[columnIndex].getName() + " total cell";
			}
		}

		return text;
	}

	private void verifyCrosstabMeasure(JRCrosstabMeasure measure)
	{
		String measureName = measure.getName();
		if (measureName == null || measureName.trim().length() == 0)
		{
			addBrokenRule("Measure name missing.", measure);
		}

		CalculationEnum calculation = measure.getCalculation();
		if (calculation == CalculationEnum.SYSTEM)
		{
			addBrokenRule("Crosstab mesures cannot have system calculation", measure);
		}

		JRExpression valueExpression = measure.getValueExpression();
		if (valueExpression == null)
		{
			addBrokenRule("Missing expression for measure " + measureName, measure);
		}

		try
		{
			Class<?> valueClass = measure.getValueClass();
			if (valueClass == null)
			{
				addBrokenRule("Measure value class missing.", measure);
			}
			if (CrosstabPercentageEnum.getValueOrDefault(measure.getPercentageType()) != CrosstabPercentageEnum.NONE)
			{
				Class<?> percentageCalculatorClass = measure.getPercentageCalculatorClass();
				if (percentageCalculatorClass == null)
				{
					if (valueClass != null && !JRPercentageCalculatorFactory.hasBuiltInCalculator(valueClass))
					{
						addBrokenRule("Percentage calculator class needs to be specified for measure " + measureName + ".", measure);
					}
				}
				else
				{
					if (!JRPercentageCalculator.class.isAssignableFrom(percentageCalculatorClass))
					{
						addBrokenRule("Incompatible percentage calculator class for measure " + measureName + ".", measure);
					}
				}
			}
		}
		catch (JRRuntimeException e)
		{
			addBrokenRule(e, measure);
		}

		try
		{
			Class<?> incrementerFactoryClass = measure.getIncrementerFactoryClass();
			if (incrementerFactoryClass != null && !JRExtendedIncrementerFactory.class.isAssignableFrom(incrementerFactoryClass))
			{
				addBrokenRule("Crosstab measures need extended incrementers (net.sf.jasperreports.engine.fill.JRExtendedIncrementerFactory).", measure);
			}
		}
		catch (JRRuntimeException e)
		{
			addBrokenRule(e, measure);
		}
	}


	private void verifyExpressions(JRDesignCrosstab crosstab)
	{
		verifyExpressions(expressionCollector.getExpressions(crosstab),
				crosstab.getParametersMap(),
				new HashMap<>(),
				crosstab.getVariablesMap());
	}


	private void verifyCellContents(JRCellContents contents, String cellText)
	{
		if (contents != null)
		{
			JRElement[] elements = contents.getElements();
			if (elements != null && elements.length > 0)
			{
				int topPadding = 0;
				int leftPadding = 0;
				int bottomPadding = 0;
				int rightPadding = 0;

				JRLineBox box = contents.getLineBox();
				if (box != null)
				{
					topPadding = box.getTopPadding();
					leftPadding = box.getLeftPadding();
					bottomPadding = box.getBottomPadding();
					rightPadding = box.getRightPadding();
				}

				int cellWidth = contents.getWidth();
				boolean widthCalculated = cellWidth != JRCellContents.NOT_CALCULATED;
				int avlblWidth = cellWidth - leftPadding - rightPadding;
				int cellHeight = contents.getHeight();
				boolean heightCalculated = cellHeight != JRCellContents.NOT_CALCULATED;
				int avlblHeight = cellHeight - topPadding - bottomPadding;

				for (int i = 0; i < elements.length; i++)
				{
					JRElement element = elements[i];

					if (widthCalculated && element.getX() + element.getWidth() > avlblWidth)
					{
						addBrokenRule("Element reaches outside " + cellText + " width: x=" + element.getX() + ", width="
								+ element.getWidth() + ", available width=" + avlblWidth + ".",
								element);
					}

					if (heightCalculated && element.getY() + element.getHeight() > avlblHeight)
					{
						addBrokenRule("Element reaches outside " + cellText + " height: y=" + element.getY() + ", height="
								+ element.getHeight() + ", available height=" + avlblHeight + ".",
								element);
					}

					if (element instanceof JRStaticText)
					{
						verifyStaticText((JRStaticText)element);
					}
					else if (element instanceof JRTextField)
					{
						JRTextField textField = (JRTextField) element;

						if (EvaluationTimeEnum.getValueOrDefault(textField.getEvaluationTime()) != EvaluationTimeEnum.NOW)
						{
							addBrokenRule("Elements with delayed evaluation time are not supported inside crosstab cells.", textField);
						}

						verifyTextField(textField);
					}
					else if (element instanceof JRImage)
					{
						JRImage image = (JRImage) element;

						if (EvaluationTimeEnum.getValueOrDefault(image.getEvaluationTime()) != EvaluationTimeEnum.NOW)
						{
							addBrokenRule("Elements with delayed evaluation time are not supported inside crosstab cells.", image);
						}

						verifyImage(image);
					}
					else if (element instanceof JRFrame)
					{
						verifyFrame((JRFrame) element);
					}
					else if (element instanceof JRSubreport)
					{
						verifySubreport((JRSubreport) element);
					}
					else if (element instanceof JRCrosstab)
					{
						addBrokenRule("Crosstabs are not allowed inside crosstab cells.", element);
					}
					else if (element.getClass().getName().equals("net.sf.jasperreports.charts.JRChart"))
					{
						addBrokenRule("Charts are not allowed inside crosstab cells.", element);
					}
				}
				
				verifyElementsOverlap(elements);
			}
		}
	}


	public void verifyElementDataset(JRElementDataset dataset)
	{
		JRDatasetRun datasetRun = dataset.getDatasetRun();

		if (datasetRun != null)
		{
			IncrementTypeEnum incrementType = dataset.getIncrementType();
			if (incrementType == IncrementTypeEnum.PAGE || incrementType == IncrementTypeEnum.COLUMN)
			{
				addBrokenRule("Chart datasets with dataset run cannont have Column or Page increment type.", dataset);
			}

			DatasetResetTypeEnum resetType = dataset.getDatasetResetType();
			if (resetType == DatasetResetTypeEnum.PAGE || resetType == DatasetResetTypeEnum.COLUMN)
			{
				addBrokenRule("Chart datasets with dataset run cannont have Column or Page reset type.", dataset);
			}
//			else if (resetType != ResetTypeEnum.REPORT)
//			{
//				//doesn't make sense, but let it go
//			}

			verifyDatasetRun(datasetRun);
		}
	}


	/**
	 * Verifies a subdataset run object.
	 * 
	 * @param datasetRun the subdataset run
	 */
	public void verifyDatasetRun(JRDatasetRun datasetRun)
	{
		JRDesignDataset dataset = null;

		String datasetName = datasetRun.getDatasetName();
		if (datasetName == null || datasetName.length() == 0)
		{
			addBrokenRule("Dataset name is missing for dataset run.", datasetRun);
		}
		else
		{
			dataset = (JRDesignDataset) jasperDesign.getDatasetMap().get(datasetName);

			if (dataset == null)
			{
				addBrokenRule("Unknown dataset name " + datasetName + ".", datasetRun);
			}
		}

		JRDatasetParameter[] parameters = datasetRun.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for(int index = 0; index < parameters.length; index++)
			{
				JRDatasetParameter parameter = parameters[index];

				String paramName = parameter.getName();
				if (paramName == null || paramName.trim().length() == 0)
				{
					addBrokenRule("Dataset " + datasetName + " parameter name missing.", parameter);
				}

				JRParameter datasetParam = null;
				if (dataset != null)
				{
					datasetParam = dataset.getParametersMap().get(paramName);

					if (datasetParam == null)
					{
						addBrokenRule("Unknown parameter " + paramName + " in dataset " + datasetName + ".", parameter);
					}
				}
			}
		}

		JRExpression connectionExpression = datasetRun.getConnectionExpression();
		JRExpression dataSourceExpression = datasetRun.getDataSourceExpression();

		if (connectionExpression != null && dataSourceExpression != null)
		{
			addBrokenRule("Dataset " + datasetName + " cannot have both connection expresion and data source expression.", datasetRun);
		}

		List<ReturnValue> returnValues = datasetRun.getReturnValues();
		if (returnValues != null && !returnValues.isEmpty())
		{
			for (ReturnValue returnValue : returnValues)
			{
				verifyReturnValue(returnValue);
			}
		}
	}


	private void verifyDatasets()
	{
		JRDataset[] datasets = jasperDesign.getDatasets();
		if (datasets != null && datasets.length > 0)
		{
			for (int i = 0; i < datasets.length; ++i)
			{
				JRDesignDataset dataset = (JRDesignDataset) datasets[i];

				if (dataset.getName() == null || dataset.getName().trim().length() == 0)
				{
					addBrokenRule("Dataset name is missing.", dataset);
				}

				verifyDataset(dataset);
			}
		}
	}


	private void verifyDataset(JRDesignDataset dataset)
	{
		verifyExpressions(dataset);

		verifyProperyExpressions(dataset.getPropertyExpressions());

		verifyParameters(dataset);

		verifyQuery(dataset);

		verifyFields(dataset);

		verifySortFields(dataset);

		verifyVariables(dataset);

		verifyGroups(dataset);
	}


	protected void verifyFrame(JRFrame frame)
	{
		verifyReportElement(frame);
		
		JRElement[] elements = frame.getElements();
		if (elements != null && elements.length > 0)
		{
			int leftPadding = frame.getLineBox().getLeftPadding();
			int rightPadding = frame.getLineBox().getRightPadding();

			int avlblWidth = frame.getWidth() - leftPadding - rightPadding;

			for (int i = 0; i < elements.length; i++)
			{
				JRElement element = elements[i];

				if (element.getX() + element.getWidth() > avlblWidth)
				{
					addBrokenRule("Element reaches outside frame width: x=" + element.getX() + ", width="
							+ element.getWidth() + ", available width=" + avlblWidth + ".", element);
				}

				verifyElement(element);
			}
			
			verifyElementsOverlap(elements);
		}
	}


	public void verifyReportElement(JRElement element)
	{
		if (element.getWidth() < 0)
		{
			if (allowElementNegativeWidth)
			{
				if (log.isWarnEnabled())
				{
					log.warn("Element has negative width: " + element.getWidth());
				}
			}
			else
			{
				addBrokenRule("Element cannot have negative width.", element);
			}
		}
		
		if (element.getX() < 0 && !allowElementNegativeX(element))
		{
			addBrokenRule("Element negative X " + element.getX() + " not allowed", 
					element);
		}

		if (element.getY() < 0 && !allowElementNegativeY(element))
		{
			addBrokenRule("Element negative Y " + element.getY() + " not allowed", 
					element);
		}

		verifyProperyExpressions(element.getPropertyExpressions());
	}

	protected boolean allowElementNegativeX(JRElement element)
	{
		// default to report/global property
		boolean allow = allowElementNegativeX;
		if (element.hasProperties())
		{
			JRPropertiesMap properties = element.getPropertiesMap();
			if (properties.containsProperty(PROPERTY_ALLOW_ELEMENT_NEGATIVE_X))
			{
				// use element level property
				allow = JRPropertiesUtil.asBoolean(properties.getProperty(
						PROPERTY_ALLOW_ELEMENT_NEGATIVE_X));
			}
		}
		return allow;
	}

	protected boolean allowElementNegativeY(JRElement element)
	{
		// default to report/global property
		boolean allow = allowElementNegativeY;
		if (element.hasProperties())
		{
			JRPropertiesMap properties = element.getPropertiesMap();
			if (properties.containsProperty(PROPERTY_ALLOW_ELEMENT_NEGATIVE_Y))
			{
				// use element level property
				allow = JRPropertiesUtil.asBoolean(properties.getProperty(
						PROPERTY_ALLOW_ELEMENT_NEGATIVE_Y));
			}
		}
		return allow;
	}
	
	protected void verifyProperyExpressions(
			JRPropertyExpression[] propertyExpressions)
	{
		if (propertyExpressions != null)
		{
			for (int i = 0; i < propertyExpressions.length; i++)
			{
				verifyPropertyExpression(propertyExpressions[i]);
			}
		}
	}

	protected void verifyPropertyExpression(JRPropertyExpression propertyExpression)
	{
		String name = propertyExpression.getName();
		if (name == null)
		{
			addBrokenRule("Property name missing.", propertyExpression);
		}
		
		JRExpression expr = propertyExpression.getValueExpression();
		if (expr == null)
		{
			addBrokenRule("Property value expression missing.", propertyExpression);
		}
	}


	protected void verifyComponentElement(JRComponentElement element)
	{
		verifyReportElement(element);
		
		Component component = element.getComponent();
		if (component == null)
		{
			addBrokenRule("No component set for component element", element);
		}
		
		if (component != null)
		{
			ComponentCompiler compiler = 
				ComponentsEnvironment.getInstance(jasperReportsContext).getManager(component).getComponentCompiler(jasperReportsContext);
			pushCurrentComponentElement(element);
			try
			{
				compiler.verify(component, this);
			}
			finally
			{
				popCurrentComponentElement();
			}
		}
	}
	
	/**
	 * Returns the component element which is currently verified, if any.
	 *
	 * <p>
	 * This method can be used in the {@link ComponentCompiler#verify(Component, JRVerifier)}
	 * method to get a handle of the wrapping componenet element.
	 * </p>
	 * 
	 * @return the currently verified component element
	 */
	public JRComponentElement getCurrentComponentElement()
	{
		if (currentComponentElementStack.isEmpty())
		{
			return null;
		}
		return currentComponentElementStack.getFirst();
	}

	protected void pushCurrentComponentElement(JRComponentElement element)
	{
		currentComponentElementStack.addFirst(element);
	}
	
	protected void popCurrentComponentElement()
	{
		currentComponentElementStack.removeFirst();
	}

	protected void verifyGenericElement(JRGenericElement element)
	{
		verifyReportElement(element);

		if (element.getEvaluationTime() == EvaluationTimeEnum.GROUP)
		{
			String groupName = element.getEvaluationGroup();
			if (groupName == null)
			{
				addBrokenRule("Evaluation group not set for generic element", element);
			}
			else
			{
				if (!jasperDesign.getGroupsMap().containsKey(groupName))
				{
					addBrokenRule("Generic element evaluation group " + groupName 
							+ " not found in report", element);
				}
			}
		}
		
		JRGenericElementType type = element.getGenericType();
		if (type == null)
		{
			addBrokenRule("No type set for generic element", element);
		}
		else
		{
			if (type.getNamespace() == null)
			{
				addBrokenRule("No namespace set for generic element type", type);
			}
			
			if (type.getName() == null)
			{
				addBrokenRule("No name set for generic element type", type);
			}
		}
		
		JRGenericElementParameter[] parameters = element.getParameters();
		for (int i = 0; i < parameters.length; i++)
		{
			JRGenericElementParameter parameter = parameters[i];
			
			if (parameter.getName() == null)
			{
				addBrokenRule("No name set for generic element parameter", parameter);
			}
		}
	}

	private static int getBreakHeight(JRBand band)
	{
		int breakHeight = 0;

		if (band != null)
		{
			breakHeight = band.getHeight();
			JRElement[] elements = band.getElements();
			if (
				SplitTypeEnum.IMMEDIATE == band.getSplitType()
				&& elements != null && elements.length > 0
				)
			{
				for(int i = 0; i < elements.length; i++)
				{
					JRElement element = elements[i];
					int bottom = element.getY() + element.getHeight();
					breakHeight = bottom < breakHeight ? bottom : breakHeight;
				}
			}
		}

		return breakHeight;
	}
	
	public void verifyExpression(JRExpression expression, Object parent, String mandatoryMessage)
	{
		if (expression == null)
		{
			if (mandatoryMessage != null)
			{
				addBrokenRule(mandatoryMessage, parent);
			}
		}
	}
	
	public void verify(MultiAxisData data)
	{
		// TODO lucianc 
	}


	protected void verifyPart(JRPart part)
	{
		PartEvaluationTime evaluationTime = part.getEvaluationTime();
		if (evaluationTime != null && evaluationTime.getEvaluationTimeType() == PartEvaluationTimeType.GROUP)
		{
			String evaluationGroup = evaluationTime.getEvaluationGroup();
			if (evaluationGroup == null)
			{
				addBrokenRule("Evaluation group not set for part", part);
			}
			else
			{
				Map<String, JRGroup> groups = jasperDesign.getGroupsMap();
				if (!groups.containsKey(evaluationGroup))
				{
					addBrokenRule("Part evaluation group \"" + evaluationGroup + "\" not found in report", part);
				}
			}
		}

		PartComponent component = part.getComponent();
		if (component == null)
		{
			addBrokenRule("No component set for part", part);
		}
		
		if (component != null)
		{
			PartComponentManager manager = PartComponentsEnvironment.getInstance(jasperReportsContext).getManager(component);
			PartComponentCompiler compiler = manager.getComponentCompiler(jasperReportsContext);
			compiler.verify(component, this);
		}
	}
	
	public void pushSubdatasetContext(String subdatasetName)
	{
		datasetContextStack.addFirst(subdatasetName);
	}
	
	public void popSubdatasetContext()
	{
		datasetContextStack.removeFirst();
	}
	
	protected JRDesignDataset currentDataset()
	{
		String subdatasetName = datasetContextStack.isEmpty() ? null : datasetContextStack.getFirst();
		JRDesignDataset dataset = subdatasetName == null ? jasperDesign.getMainDesignDataset()
				: (JRDesignDataset) jasperDesign.getDatasetMap().get(subdatasetName);
		return dataset;
	}
	
}
