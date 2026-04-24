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
package net.sf.jasperreports.engine.fill;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.components.iconlabel.IconLabelComponent;
import net.sf.jasperreports.components.iconlabel.IconLabelComponentUtil;
import net.sf.jasperreports.components.table.fill.TableReport;
import net.sf.jasperreports.crosstabs.CrosstabColumnCell;
import net.sf.jasperreports.crosstabs.CrosstabConstants;
import net.sf.jasperreports.crosstabs.CrosstabDeepVisitor;
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
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.fill.BucketExpressionOrderer;
import net.sf.jasperreports.crosstabs.fill.BucketOrderer;
import net.sf.jasperreports.crosstabs.fill.IconLabelFillObjectFactory;
import net.sf.jasperreports.crosstabs.fill.JRCrosstabExpressionEvaluator;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabCell;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabGroup;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabMeasure;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabObjectFactory;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabParameter;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketDefinition;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketDefinition.Bucket;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketingServiceContext;
import net.sf.jasperreports.crosstabs.fill.calculation.ColumnValueInfo;
import net.sf.jasperreports.crosstabs.fill.calculation.CrosstabBucketingService;
import net.sf.jasperreports.crosstabs.fill.calculation.CrosstabCell;
import net.sf.jasperreports.crosstabs.fill.calculation.HeaderCell;
import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition;
import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition.MeasureValue;
import net.sf.jasperreports.crosstabs.fill.calculation.OrderByColumnInfo;
import net.sf.jasperreports.crosstabs.fill.calculation.OrderByColumnOrderer;
import net.sf.jasperreports.crosstabs.interactive.DataColumnInfo;
import net.sf.jasperreports.crosstabs.interactive.RowGroupInteractiveInfo;
import net.sf.jasperreports.crosstabs.type.CrosstabColumnPositionEnum;
import net.sf.jasperreports.crosstabs.type.CrosstabPercentageEnum;
import net.sf.jasperreports.crosstabs.type.CrosstabRowPositionEnum;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.analytics.dataset.BucketOrder;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.MatcherExporterFilter;
import net.sf.jasperreports.engine.type.HorizontalPosition;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;
import net.sf.jasperreports.engine.util.JRValueStringUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.export.AccessibilityUtil;
import net.sf.jasperreports.export.type.AccessibilityTagEnum;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.util.JsonLoader;

/**
 * Fill-time implementation of a {@link net.sf.jasperreports.crosstabs.JRCrosstab crosstab}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillCrosstab extends JRFillElement implements JRCrosstab, JROriginProvider, BucketingServiceContext
{
	private final static Log log = LogFactory.getLog(JRFillCrosstab.class); 

	public static final String EXCEPTION_MESSAGE_KEY_BUCKETING_SERVICE_ERROR = "crosstabs.bucketing.service.error";
	public static final String EXCEPTION_MESSAGE_KEY_EVALUATOR_LOADING_ERROR = "crosstabs.evaluator.loading.error";
	public static final String EXCEPTION_MESSAGE_KEY_INFINITE_LOOP = "crosstabs.infinite.loop";
	public static final String EXCEPTION_MESSAGE_KEY_NOT_ENOUGH_SPACE = "crosstabs.not.enough.space";

	/**
	 *	Property that enables/disables the interactivity in the crosstab component.
	 *
	 * <p>
	 * It can be set:
	 * <ul>
	 * 	<li>globally</li>
	 * 	<li>at report level</li>
	 * 	<li>at component level</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * The default global value of this property is <code>true</code>
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_CROSSTAB,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.CROSSTAB},
			sinceVersion = PropertyConstants.VERSION_5_5_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_INTERACTIVE = JRPropertiesUtil.PROPERTY_PREFIX + "crosstab.interactive";

	/**
	 *	Property that enables/disables the floating headers in the crosstab component when scrolling.
	 *	If the interactivity has been disabled by setting {@link #PROPERTY_INTERACTIVE} to <code>false</code>, then
	 *	setting this property will have no effect.
	 *
	 * <p>
	 * It can be set:
	 * <ul>
	 * 	<li>globally</li>
	 * 	<li>at report level</li>
	 * 	<li>at component level</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * The default global value of this property is <code>true</code>
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_CROSSTAB,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.CROSSTAB},
			sinceVersion = PropertyConstants.VERSION_6_0_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FLOATING_HEADERS = JRPropertiesUtil.PROPERTY_PREFIX + "crosstab.floating.headers";

	public static final String PROPERTY_ORDER_BY_COLUMN = JRPropertiesUtil.PROPERTY_PREFIX + "crosstab.order.by.column";
	
	@Property(
			category = PropertyConstants.CATEGORY_CROSSTAB,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_5_5_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_ROW_GROUP_COLUMN_HEADER = JRPropertiesUtil.PROPERTY_PREFIX + "crosstab.row.group.column.header";
	
	@Property(
			category = PropertyConstants.CATEGORY_CROSSTAB,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_5_5_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_COLUMN_HEADER_SORT_MEASURE_INDEX = JRPropertiesUtil.PROPERTY_PREFIX + "crosstab.column.header.sort.measure.index";
	
	public static final String CROSSTAB_INTERACTIVE_ELEMENT_NAME = "crosstabInteractiveElement";
	
	public static final String HTML_CLASS_COLUMN_FLOATING = "jrxtcolfloating";
	
	public static final String HTML_CLASS_ROW_FLOATING = "jrxtrowfloating";
	
	public static final String HTML_CLASS_CROSS_FLOATING = "jrxtcrossfloating";
	
	public static final JRGenericElementType CROSSTAB_INTERACTIVE_ELEMENT_TYPE = 
			new JRGenericElementType(JRXmlConstants.JASPERREPORTS_NAMESPACE, CROSSTAB_INTERACTIVE_ELEMENT_NAME);

	protected static final String FILL_CACHE_KEY_CROSSTAB_CHUNK_COUNTER = JRFillCrosstab.class.getName() + "#chunkCounter";

	private final JRFillObjectFactory fillFactory;
	
	final protected JRCrosstab parentCrosstab;
	
	protected final JRLineBox lineBox;

	protected JRFillCrosstabDataset dataset;

	protected JRFillCrosstabRowGroup[] rowGroups;

	protected Map<String,Integer> rowGroupsMap;

	protected JRFillCrosstabColumnGroup[] columnGroups;

	protected Map<String,Integer> columnGroupsMap;

	protected JRFillCrosstabMeasure[] measures;

	private OrderByColumnInfo orderByColumnInfo;
	private List<Bucket> orderByColumnBucketValues;
	
	protected CrosstabBucketingService bucketingService;

	protected JRFillVariable[] variables;

	protected Map<String,JRFillVariable> variablesMap;
	
	protected JRFillVariable[][][] totalVariables;
	protected boolean[][] retrieveTotal;

	protected JRFillCrosstabParameter[] parameters;

	protected Map<String,JRFillParameter> parametersMap;
	
	protected boolean ignoreWidth;

	protected JRCrosstabExpressionEvaluator crosstabEvaluator;

	protected JRFillCrosstabCell[][] crossCells;
	protected JRFillCellContents titleCellContents;
	protected JRFillCellContents headerCell;
	protected JRFillCellContents whenNoDataCell;

	protected boolean hasData;
	protected HeaderCell[][] columnHeadersData;
	protected HeaderCell[][] rowHeadersData;
	protected CrosstabCell[][] cellData;
	protected MeasureValue[] grandTotals;

	private boolean percentage;

	private CrosstabFiller crosstabFiller;
	private int overflowStartPage;
	
	private List<JRTemplatePrintFrame> printFrames;
	private int printFramesMaxWidth;
	
	private boolean interactive;
	private boolean floatingHeaders;
	private int lastColumnGroupWithHeaderIndex = -1;
	
	public JRFillCrosstab(JRBaseFiller filler, JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		super(filler, crosstab, factory);

		this.fillFactory = factory;
		
		parentCrosstab = crosstab;
		lineBox = crosstab.getLineBox().clone(this);

		loadEvaluator(filler.getJasperReport());

		JRFillCrosstabObjectFactory crosstabFactory = new JRFillCrosstabObjectFactory(
				factory, crosstabEvaluator);
		crosstabFactory.setParentOriginProvider(this);
		
		if (crosstab.getTitleCell() != null && crosstab.getTitleCell().getCellContents() != null)
		{
			titleCellContents = crosstabFactory.getCell(crosstab.getTitleCell().getCellContents(), 
					JRCellContents.TYPE_CROSSTAB_TITLE);
		}
		
		headerCell = crosstabFactory.getCell(crosstab.getHeaderCell(), 
				JRCellContents.TYPE_CROSSTAB_HEADER);

		copyRowGroups(crosstab, crosstabFactory);
		copyColumnGroups(crosstab, crosstabFactory);
		
		copyMeasures(crosstab, crosstabFactory);
		copyCells(crosstab, crosstabFactory);
		whenNoDataCell = crosstabFactory.getCell(crosstab.getWhenNoDataCell(),
				JRCellContents.TYPE_NO_DATA_CELL);
		
		dataset = factory.getCrosstabDataset(crosstab.getDataset(), this);
		crosstabEvaluator.setFillDataset(dataset.getFillDataset());

		copyParameters(crosstab, factory);
		copyVariables(crosstab, crosstabFactory);
		
		lastColumnGroupWithHeaderIndex = determineLastColumnGroupWithHeaderIndex();
		
		crosstabFiller = new CrosstabFiller();
	}

	@Override
	protected void setBand(JRFillBand band)
	{
		super.setBand(band);
		
		dataset.setBand(band);
	}

	private boolean isIgnoreWidth(JRBaseFiller filler, JRCrosstab crosstab)
	{
		Boolean crosstabIgnoreWidth = crosstab.getIgnoreWidth();
		// crosstab attribute overrides all 
		if (crosstabIgnoreWidth != null)
		{
			return crosstabIgnoreWidth;
		}
		
		// report level property
		String reportProperty = JRPropertiesUtil.getOwnProperty(filler.getMainDataset(), PROPERTY_IGNORE_WIDTH); // because we read the "own" property here, without inheritance,
		// it is most likely that this would not work for crosstab in table component because the filler.getMainDataset() would be the one from TableJasperReport (subreport),
		// and not the master report dataset where the property would be set
		if (reportProperty != null)
		{
			return JRPropertiesUtil.asBoolean(reportProperty);
		}
		
		// report pagination flag from the filler
		if (filler.isIgnorePagination())
		{
			return true;
		}
		
		// global property
		return filler.getPropertiesUtil().getBooleanProperty(PROPERTY_IGNORE_WIDTH);
	}

	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}

	private void copyRowGroups(JRCrosstab crosstab, JRFillCrosstabObjectFactory factory)
	{
		JRCrosstabRowGroup[] groups = crosstab.getRowGroups();
		rowGroups = new JRFillCrosstabRowGroup[groups.length];
		rowGroupsMap = new HashMap<>();
		for (int i = 0; i < groups.length; ++i)
		{
			JRFillCrosstabRowGroup group = factory.getCrosstabRowGroup(groups[i]);
			group.getFillHeader().setVerticalPositionType(groups[i].getPosition());

			rowGroups[i] = group;
			rowGroupsMap.put(group.getName(), i);
		}
	}

	private void copyColumnGroups(JRCrosstab crosstab, JRFillCrosstabObjectFactory factory)
	{
		JRCrosstabColumnGroup[] groups = crosstab.getColumnGroups();
		columnGroups = new JRFillCrosstabColumnGroup[groups.length];
		columnGroupsMap = new HashMap<>();
		for (int i = 0; i < groups.length; ++i)
		{
			JRFillCrosstabColumnGroup group = factory.getCrosstabColumnGroup(groups[i]);
			columnGroups[i] = group;
			columnGroupsMap.put(group.getName(), i);
		}
	}

	private void copyMeasures(JRCrosstab crosstab, JRFillCrosstabObjectFactory factory)
	{
		JRCrosstabMeasure[] crossMeasures = crosstab.getMeasures();
		measures = new JRFillCrosstabMeasure[crossMeasures.length];
		for (int i = 0; i < crossMeasures.length; i++)
		{
			measures[i] = factory.getCrosstabMeasure(crossMeasures[i]);
		}
	}

	private void copyParameters(JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		JRCrosstabParameter[] crossParams = crosstab.getParameters();
		parameters = new JRFillCrosstabParameter[crossParams.length];
		parametersMap = new HashMap<>();
		for (int i = 0; i < crossParams.length; i++)
		{
			parameters[i] = factory.getCrosstabParameter(crossParams[i]);
			parametersMap.put(parameters[i].getName(), parameters[i]);
		}
	}

	private void copyCells(JRCrosstab crosstab, JRFillCrosstabObjectFactory factory)
	{
		JRCrosstabCell[][] crosstabCells = crosstab.getCells();		
		crossCells = new JRFillCrosstabCell[rowGroups.length + 1][columnGroups.length + 1];
		for (int i = 0; i <= rowGroups.length; ++i)
		{
			for (int j = 0; j <= columnGroups.length; ++j)
			{
				if (crosstabCells[i][j] != null)
				{
					crossCells[i][j] = factory.getCrosstabCell(crosstabCells[i][j]);
				}
			}
		}
	}

	private void copyVariables(JRCrosstab crosstab, JRFillObjectFactory factory)
	{
		JRVariable[] vars = crosstab.getVariables();
		variables = new JRFillVariable[vars.length];
		variablesMap = new HashMap<>();
		for (int i = 0; i < variables.length; i++)
		{
			variables[i] = factory.getVariable(vars[i]);
			variablesMap.put(variables[i].getName(), variables[i]);
		}
		
		Map<String,int[]> totalVarPos = new HashMap<>();
		totalVariables = new JRFillVariable[rowGroups.length + 1][columnGroups.length + 1][measures.length];
		for (int row = 0; row <= rowGroups.length; ++row)
		{
			JRCrosstabRowGroup rowGroup = row == rowGroups.length ? null : rowGroups[row];
			for (int col = 0; col <= columnGroups.length; ++col)
			{
				JRCrosstabColumnGroup colGroup = col == columnGroups.length ? null : columnGroups[col];
				
				if (row < rowGroups.length || col < columnGroups.length)
				{
					for (int m = 0; m < measures.length; m++)
					{
						String totalVariableName = JRDesignCrosstab.getTotalVariableName(measures[m], rowGroup, colGroup);
						totalVariables[row][col][m] = variablesMap.get(totalVariableName);
						totalVarPos.put(totalVariableName, new int[]{row, col});
					}
				}
			}
		}
		
		Set<String> measureVars = new HashSet<>();
		for (JRFillCrosstabMeasure measure : measures)
		{
			measureVars.add(measure.getFillVariable().getName());
		}

		retrieveTotal = new boolean[rowGroups.length + 1][columnGroups.length + 1];
		
		//FIXME avoid this
		JRExpressionCollector collector = JRExpressionCollector.collector(filler.getJasperReportsContext(), filler.getJasperReport(), crosstab);
		List<JRExpression> expressions = collector.getExpressions(crosstab);
		for (Iterator<JRExpression> iter = expressions.iterator(); iter.hasNext();)
		{
			JRExpression expression = iter.next();
			Object expressionContext = collector.getExpressionContext(expression);
			boolean groupHeaderExpression = expressionContext instanceof JRCrosstabGroup;
			JRExpressionChunk[] chunks = expression.getChunks();
			if (chunks != null)
			{
				for (int i = 0; i < chunks.length; i++)
				{
					JRExpressionChunk chunk = chunks[i];
					if (chunk.getType() == JRExpressionChunk.TYPE_VARIABLE)
					{
						String varName = chunk.getText();
						int[] pos = totalVarPos.get(varName);
						if (pos != null)
						{
							retrieveTotal[pos[0]][pos[1]] = true;
						}
						
						// if a measure variable is used inside a group header, compute all totals.
						// in theory we could have a finer grained rule here, but it complicates
						// the logic without a singnificant gain.
						if (groupHeaderExpression && (pos != null || measureVars.contains(varName)))
						{
							retrieveTotal[0][0] = true;
						}
					}
				}
			}
		}
	}

	@Override
	public JRBaseFiller getFiller()
	{
		return filler;
	}

	protected void loadEvaluator(JasperReport jasperReport)
	{
		try
		{
			JREvaluator evaluator = JasperCompileManager.getInstance(filler.getJasperReportsContext()).getEvaluator(jasperReport, parentCrosstab);
			crosstabEvaluator = new JRCrosstabExpressionEvaluator(evaluator);
		}
		catch (JRException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_EVALUATOR_LOADING_ERROR,
					(Object[])null,
					e);
		}
	}

	private CrosstabBucketingService createService(byte evaluation) throws JRException
	{
		boolean hasOrderByExpression = false;
		List<BucketDefinition> rowBuckets = new ArrayList<>(rowGroups.length);
		for (int i = 0; i < rowGroups.length; ++i)
		{
			JRFillCrosstabRowGroup group = rowGroups[i];
			rowBuckets.add(createServiceBucket(group, i, evaluation));
			hasOrderByExpression |= group.getBucket().getOrderByExpression() != null;
		}

		List<BucketDefinition> colBuckets = new ArrayList<>(columnGroups.length);
		for (int i = 0; i < columnGroups.length; ++i)
		{
			JRFillCrosstabColumnGroup group = columnGroups[i];
			colBuckets.add(createServiceBucket(group, i, evaluation));
			hasOrderByExpression |= group.getBucket().getOrderByExpression() != null;
		}

		percentage = false;
		List<MeasureDefinition> measureList = new ArrayList<>(measures.length);
		for (int i = 0; i < measures.length; ++i)
		{
			measureList.add(createServiceMeasure(measures[i]));
			percentage |= measures[i].getPercentageType() == CrosstabPercentageEnum.GRAND_TOTAL;
		}

		// if a group has order by expression, compute totals as they might be used
		// in the expression
		// TODO refine this
		if (percentage || hasOrderByExpression)
		{
			rowBuckets.get(0).setComputeTotal();
			colBuckets.get(0).setComputeTotal();
		}
		
		return new CrosstabBucketingService(this, rowBuckets, colBuckets, measureList, dataset.isDataPreSorted(), retrieveTotal);
	}

	private BucketDefinition createServiceBucket(JRCrosstabGroup group, int groupIndex, byte evaluation) throws JRException
	{
		JRCrosstabBucket bucket = group.getBucket();

		Comparator<Object> comparator = null;
		JRExpression comparatorExpression = bucket.getComparatorExpression();
		if (comparatorExpression != null)
		{
			comparator = (Comparator<Object>) evaluateExpression(comparatorExpression, evaluation);
		}
		
		BucketOrderer orderer = createOrderer(group, groupIndex, comparator);
		BucketDefinition bucketDefinition = new BucketDefinition(bucket.getValueClass(),
				orderer, comparator, BucketOrder.getValueOrDefault(bucket.getOrder()), 
				group.getTotalPosition());
		
		Boolean mergeHeaderCells = group.getMergeHeaderCells();
		// by default the header cells are merged
		bucketDefinition.setMergeHeaderCells(mergeHeaderCells == null || mergeHeaderCells);
		
		return bucketDefinition;
	}

	protected BucketOrderer createOrderer(JRCrosstabGroup group, int groupIndex, Comparator<Object> bucketComparator)
	{
		BucketOrderer orderer = null;
		BucketOrder bucketOrder = BucketOrder.getValueOrDefault(group.getBucket().getOrder());
		
		if (group instanceof JRCrosstabRowGroup
				&& orderByColumnInfo != null && orderByColumnInfo.getOrder() != null
				// ordering by column only applies to nesting groups is they are not already ordered
				&& (groupIndex == rowGroups.length - 1 || bucketOrder == BucketOrder.NONE))
		{
			orderer = new OrderByColumnOrderer(orderByColumnInfo);
		}
		
		if (orderer == null)
		{
			JRCrosstabBucket bucket = group.getBucket();
			JRExpression orderByExpression = bucket.getOrderByExpression();
			if (orderByExpression != null && bucketOrder != BucketOrder.NONE)
			{
				if (log.isDebugEnabled())
				{
					log.debug("using order by expression to order group " + group.getName());
				}
				
				// when we have an order by expression, the comparator is applied to order values
				Comparator<Object> orderValueComparator = BucketDefinition.createOrderComparator(bucketComparator, bucketOrder);
				orderer = new BucketExpressionOrderer(orderByExpression, orderValueComparator);
			}
		}
		
		return orderer;
	}

	private MeasureDefinition createServiceMeasure(JRFillCrosstabMeasure measure)
	{
		return new MeasureDefinition(
				measure.getValueClass(), 
				measure.getCalculation(), 
				measure.getIncrementerFactory()); 
	}

	@Override
	public Object evaluateMeasuresExpression(JRExpression expression, MeasureValue[] measureValues)
			throws JRException
	{
		for (int i = 0; i < measures.length; i++)
		{
			Object value = measureValues[i].getValue();
			measures[i].getFillVariable().setValue(value);
		}
		
		return crosstabEvaluator.evaluate(expression, JRExpression.EVALUATION_DEFAULT);
	}
	
	@Override
	protected void reset()
	{
		super.reset();

		for (int i = 0; i < variables.length; i++)
		{
			variables[i].setValue(null);
			variables[i].setInitialized(true);
		}
		
		printFrames = null;
		printFramesMaxWidth = 0;
	}

	@Override
	protected void evaluate(byte evaluation) throws JRException
	{
		reset();

		evaluatePrintWhenExpression(evaluation);

		if (isPrintWhenExpressionNull() || isPrintWhenTrue())
		{
			evaluateProperties(evaluation);
			evaluateStyle(evaluation);

			dataset.evaluateDatasetRun(evaluation);

			initEvaluator(evaluation);

			bucketingService.processData();
			bucketingService.createCrosstab();

			hasData = bucketingService.hasData();
			
			if (hasData)
			{
				columnHeadersData = bucketingService.getColumnHeaders();
				rowHeadersData = bucketingService.getRowHeaders();
				cellData = bucketingService.getCrosstabCells();
				if (percentage)
				{
					grandTotals = bucketingService.getGrandTotals();
				}

				crosstabFiller.initCrosstab();
			}
			
			overflowStartPage = 0;

			ignoreWidth = isIgnoreWidth(filler, parentCrosstab);
			
			interactive = filler.getPropertiesUtil().getBooleanProperty(this, PROPERTY_INTERACTIVE, true);

			floatingHeaders = filler.getPropertiesUtil().getBooleanProperty(this, PROPERTY_FLOATING_HEADERS, true);
		}
	}

	protected void initEvaluator(byte evaluation) throws JRException
	{
		Map<String,Object> parameterValues = 
			JRFillSubreport.getParameterValues(
				filler, 
				getParametersMapExpression(), 
				getParameters(), 
				evaluation, 
				true, 
				false,//hasResourceBundle
				false//hasFormatFactory
				);
		
		ResourceBundle resBdl = (ResourceBundle) parameterValues.get(JRParameter.REPORT_RESOURCE_BUNDLE);
		if (resBdl == null)
		{
			JRFillParameter resourceBundleParam = filler.getParametersMap().get(JRParameter.REPORT_RESOURCE_BUNDLE);
			parameterValues.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundleParam.getValue());
		}
		
		parameterValues.put(JRParameter.REPORT_PARAMETERS_MAP, parameterValues);

		for (int i = 0; i < parameters.length; i++)
		{
			Object value = parameterValues.get(parameters[i].getName());
			parameters[i].setValue(value);
		}

		boolean ignoreNPE = filler.getPropertiesUtil().getBooleanProperty(this,	JREvaluator.PROPERTY_IGNORE_NPE, true);
		crosstabEvaluator.init(parametersMap, variablesMap, filler.getWhenResourceMissingType(), ignoreNPE);
	}

	protected void initBucketingService()
	{
		if (bucketingService == null)
		{
			setOrderByColumnInfo();
			
			try
			{
				bucketingService = createService(JRExpression.EVALUATION_DEFAULT);
			}
			catch (JRException e)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_BUCKETING_SERVICE_ERROR,
						(Object[])null,
						e);
			}
			
			setOrderByColumnBucketValues();
		}
		else
		{
			bucketingService.clear();
		}
	}
	
	protected void setOrderByColumnInfo()
	{
		orderByColumnInfo = null;
		
		// should we read this from evaluated properties?
		String orderByProperty = parentCrosstab.getPropertiesMap().getProperty(PROPERTY_ORDER_BY_COLUMN);
		if (orderByProperty == null || orderByProperty.isEmpty())
		{
			return;
		}
		
		orderByColumnInfo = JsonLoader.getInstance(filler.getJasperReportsContext()).loadObject(
				orderByProperty, "net.sf.jasperreports.interactivity.crosstabs.OrderByColumnInfoImpl");
	}
	
	protected void setOrderByColumnBucketValues()
	{
		if (orderByColumnInfo != null && orderByColumnInfo.getOrder() != null)
		{
			// creating an orderer for convenience
			OrderByColumnOrderer orderer = new OrderByColumnOrderer(orderByColumnInfo);
			orderer.init(bucketingService);
			orderByColumnBucketValues = orderer.getBucketValues();
		}
	}

	@Override
	protected boolean prepare(
		int availableHeight, 
		boolean isOverflow,
		boolean isOverflowAllowed
		) throws JRException
	{
		super.prepare(availableHeight, isOverflow, isOverflowAllowed);

		if (!isToPrint())
		{
			return false;
		}

		if (availableHeight < getRelativeY() + getHeight())
		{
			setToPrint(false);
			return true;
		}

		if (isOverflow && crosstabFiller.ended() && isAlreadyPrinted())
		{
			if (isPrintWhenDetailOverflows())
			{
				rewind();
				setReprinted(true);
			}
			else
			{
				setPrepareHeight(getHeight());
				setToPrint(false);

				return false;
			}
		}

		if (isOverflow && isPrintWhenDetailOverflows())
		{
			setReprinted(true);
		}

		printFrames = new ArrayList<>();
		printFramesMaxWidth = 0;
		crosstabFiller.fill(availableHeight - getRelativeY());

		if (!printFrames.isEmpty())
		{
			// crosstab content has been filled, reset overflowPage
			overflowStartPage = 0;
		}
		else
		{
			int pageCount = filler.getCurrentPageCount();
			if (overflowStartPage == 0)
			{
				// first empty page
				overflowStartPage = pageCount;
			}
			else if (pageCount >= overflowStartPage + 2)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INFINITE_LOOP,  
						(Object[])null 
						);
			}
		}
		
		boolean willOverflow = crosstabFiller.willOverflow();
		if (willOverflow)
		{
			setPrepareHeight(availableHeight - getRelativeY());
		}
		else if (!printFrames.isEmpty())
		{
			JRTemplatePrintFrame lastFrame = printFrames.get(printFrames.size() - 1);
			int usedHeight = lastFrame.getY() + lastFrame.getHeight();
			setPrepareHeight(usedHeight);
		}
		
		return willOverflow;
	}
	
	protected void addCrosstabChunk(List<JRPrintElement> elements, int yOffset)
	{
		JRTemplatePrintFrame printFrame = new JRTemplatePrintFrame(getTemplateFrame(), printElementOriginator);
		//printFrame.setUUID(getUUID());
		printFrame.setX(0);
		printFrame.setY(yOffset);
		
		Collections.sort(elements, new JRYXComparator());//FIXME make singleton comparator; same for older comparator

		int xLimit = Integer.MIN_VALUE;
		int yLimit = Integer.MIN_VALUE;
		for (Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
		{
			JRPrintElement element = it.next();
			if (element.getX() + element.getWidth() > xLimit)
			{
				xLimit = element.getX() + element.getWidth();
			}
			if (element.getY() + element.getHeight() > yLimit)
			{
				yLimit = element.getY() + element.getHeight();
			}
		}
		
		JRLineBox lineBox = getLineBox();
		int width = xLimit + lineBox.getLeftPadding() + lineBox.getRightPadding();
		printFrame.setWidth(width);
		
		HorizontalPosition position = concreteHorizontalPosition();
		switch (position)
		{
		case RIGHT:
			// the position does not apply when the crosstab is bigger than the element (ignoreWidth is set)
			// still, it applies if the crosstab is RTL
			if (width < getWidth() || getRunDirection() == RunDirectionEnum.RTL)
			{
				// move to the right
				printFrame.setX(getWidth() - width);
			}
			break;
		case CENTER:
			// the position does not apply when the crosstab is bigger than the element (ignoreWidth is set)
			if (width < getWidth())
			{
				int centeredX = (getWidth() - width) / 2;
				printFrame.setX(centeredX);
			}
			break;
		case LEFT:
		default:
			// x = 0 already set
			break;
		}
		
		int height = yLimit + lineBox.getTopPadding() + lineBox.getBottomPadding();
		printFrame.setHeight(height);
		
		if (getRunDirection() == RunDirectionEnum.RTL)
		{
			mirrorPrintElements(elements, xLimit);
		}
		
		int chunkIndex = getChunkIndex();
		String chunkId = getUUID().toString() + "." + chunkIndex;
		
		printFrame.getPropertiesMap().setProperty(AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG, AccessibilityTagEnum.TABLE.getName());

		if (interactive)
		{
			printFrame.getPropertiesMap().setProperty(CrosstabConstants.PROPERTY_CROSSTAB_ID, 
					chunkId);

			JRTemplateGenericPrintElement genericElement = createInteractiveElement(chunkId, floatingHeaders);
			printFrame.addElement(genericElement);
		}

		// dump all elements into the print frame
		printFrame.addElements(elements);
		
		// add this frame to the list to the list of crosstab chunks
		printFrames.add(printFrame);
		if (printFrame.getX() + printFrame.getWidth() > printFramesMaxWidth)
		{
			printFramesMaxWidth = printFrame.getX() + printFrame.getWidth();
		}
	}

	protected int getChunkIndex()
	{
		JRFillContext fillerContext = filler.getFillContext();
		AtomicInteger counter = fillerContext.getFillCache(FILL_CACHE_KEY_CROSSTAB_CHUNK_COUNTER,
				AtomicInteger::new);
		
		int chunkIndex = counter.getAndIncrement();
		return chunkIndex;
	}
	
	protected HorizontalPosition concreteHorizontalPosition()
	{
		HorizontalPosition position = getHorizontalPosition();
		if (position == null)
		{
			position = getRunDirection() == RunDirectionEnum.RTL 
					? HorizontalPosition.RIGHT : HorizontalPosition.LEFT;
		}
		return position;
	}

	protected JRTemplateGenericPrintElement createInteractiveElement(String chunkId, boolean floatingHeaders)
	{
		// TODO lucianc cache
		JRTemplateGenericElement genericElementTemplate = new JRTemplateGenericElement(
				getElementOrigin(), defaultStyleProvider, CROSSTAB_INTERACTIVE_ELEMENT_TYPE);
		JRTemplateGenericPrintElement genericElement = new JRTemplateGenericPrintElement(
				genericElementTemplate, printElementOriginator);//use a different source id?
		genericElement.setX(0);
		genericElement.setY(0);
		genericElement.setWidth(1);
		genericElement.setHeight(1);
		
		genericElement.setParameterValue(CrosstabConstants.ELEMENT_PARAMETER_CROSSTAB_ID, getUUID().toString());
		genericElement.setParameterValue(CrosstabConstants.ELEMENT_PARAMETER_CROSSTAB_FRAGMENT_ID, chunkId);
		genericElement.setParameterValue(CrosstabConstants.ELEMENT_PARAMETER_START_COLUMN_INDEX, crosstabFiller.startColumnIndex);
		genericElement.setParameterValue(CrosstabConstants.ELEMENT_PARAMETER_FLOATING_HEADERS, floatingHeaders);

		BucketDefinition[] rowBuckets = bucketingService.getRowBuckets();
		List<RowGroupInteractiveInfo> rowGroups = new ArrayList<>(rowBuckets.length);
		for (BucketDefinition bucket : rowBuckets)
		{
			RowGroupInteractiveInfo groupInfo = new RowGroupInteractiveInfo();
			groupInfo.setSortable(true);// TODO lucianc
			BucketOrder order = bucketOrder(bucket);
			groupInfo.setOrder(order == null ? null : order.name());
			rowGroups.add(groupInfo);
		}
		genericElement.setParameterValue(CrosstabConstants.ELEMENT_PARAMETER_ROW_GROUPS, rowGroups);
		
		int dataColumnCount = crosstabFiller.lastColumnIndex - crosstabFiller.startColumnIndex;
		List<DataColumnInfo> dataColumns = new ArrayList<>(dataColumnCount);
		for (int colIdx = crosstabFiller.startColumnIndex; colIdx < crosstabFiller.lastColumnIndex; ++colIdx)
		{
			List<Bucket> bucketValues = null;
			// getting the values from the most detailed column header
			for (int level = columnHeadersData.length - 1; level >= 0 && bucketValues == null; --level)
			{
				HeaderCell header = columnHeadersData[level][colIdx];
				bucketValues = header == null ? null : bucketValuesList(header);
			}
			
			DataColumnInfo dataColumn = new DataColumnInfo();
			int sortMeasureIndex = measures.length <= 1 ? 0 
					: (crosstabFiller.dataColumnSortMeasures == null ? 0 
							: crosstabFiller.dataColumnSortMeasures[colIdx - crosstabFiller.startColumnIndex]);
			if (sortMeasureIndex >= 0)
			{
				// the column is sortable
				// TODO lucianc do not repeat this if not necessary + do not serialize nulls
				dataColumn.setSortMeasureIndex(sortMeasureIndex);
				
				List<ColumnValueInfo> columnValues = toColumnValues(bucketValues);
				dataColumn.setColumnValues(columnValues);
			}
			
			BucketOrder columnOrder = null;
			if (orderByColumnBucketValues != null && orderByColumnBucketValues.equals(bucketValues))
			{
				columnOrder = BucketOrder.fromSortOrderEnum(orderByColumnInfo.getOrder());
			}
			dataColumn.setOrder(columnOrder == null ? null : columnOrder.name());

			dataColumns.add(dataColumn);
		}
		genericElement.setParameterValue(CrosstabConstants.ELEMENT_PARAMETER_DATA_COLUMNS, dataColumns);

		return genericElement;
	}

	protected BucketOrder bucketOrder(BucketDefinition bucket)
	{
		return (dataset.isDataPreSorted() || bucket.getOrderer() != null) 
				? BucketOrder.NONE : bucket.getOrder();
	}
	
	protected boolean matchesOrderByColumn(HeaderCell cell)
	{
		if (orderByColumnBucketValues == null)
		{
			return false;
		}
		
		List<Bucket> cellValues = bucketValuesList(cell);
		if (cellValues.size() > orderByColumnBucketValues.size())
		{
			return false;
		}
		
		// when the last column group is empty, we will only match a part of orderByColumnBucketValues
		Iterator<Bucket> orderValueIt = orderByColumnBucketValues.iterator();
		for (Bucket cellValue : cellValues)
		{
			Bucket orderValue = orderValueIt.next();
			if (!cellValue.equals(orderValue))
			{
				return false;
			}
		}
		return true;
	}
	
	protected List<Bucket> bucketValuesList(HeaderCell cell)
	{
		Bucket[] values = cell.getBucketValues();
		ArrayList<Bucket> valuesList = new ArrayList<>(values.length);
		for (Bucket bucket : values)
		{
			if (bucket != null)
			{
				valuesList.add(bucket);
			}
		}
		return valuesList;
	}

	protected List<ColumnValueInfo> toColumnValues(List<Bucket> bucketValues)
	{
		List<ColumnValueInfo> columnValues = new ArrayList<>(bucketValues.size());
		for (Bucket bucket : bucketValues)
		{
			if (bucket != null)
			{
				ColumnValueInfo columnValue = new ColumnValueInfo();
				if (bucket.isTotal())
				{
					columnValue.setTotal(true);
				}
				else
				{
					Object value = bucket.getValue();
					if (value != null)
					{
						String valueType = value.getClass().getName();
						columnValue.setValueType(valueType);
						columnValue.setValue(JRValueStringUtils.serialize(valueType, value));
					}
				}
				
				columnValues.add(columnValue);
			}
		}
		return columnValues;
	}
	
	@Override
	protected JRPrintElement fill()
	{
		// don't return anything, see getPrintElements()
		return null;
	}

	protected JRTemplateFrame getTemplateFrame()
	{
		return (JRTemplateFrame) getElementTemplate();
	}

	@Override
	protected JRTemplateElement createElementTemplate()
	{
		JRTemplateFrame template = new JRTemplateFrame(getElementOrigin(), 
				filler.getJasperPrint().getDefaultStyleProvider());
		template.setElement(this);
		template.copyBox(getLineBox());
		return template;
	}

	@Override
	protected void rewind()
	{
		crosstabFiller.initCrosstab();
		
		overflowStartPage = 0;
	}

	protected List<? extends JRPrintElement> getPrintElements()
	{
		return printFrames;
	}
	
	protected int getPrintElementsWidth()
	{
		return printFramesMaxWidth;
	}

	protected void mirrorPrintElements(List<JRPrintElement> printElements, int width)
	{
		for (Iterator<JRPrintElement> it = printElements.iterator(); it.hasNext();)
		{
			JRPrintElement element = it.next();
			int mirrorX = width - element.getX() - element.getWidth();
			element.setX(mirrorX);
		}
	}

	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation)
	{
		// nothing
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitCrosstab(this);
		
		if (ElementsVisitorUtils.visitDeepElements(visitor))
		{
			new CrosstabDeepVisitor(visitor).deepVisitCrosstab(this);
		}
	}

	@Override
	public int getId()
	{
		return parentCrosstab.getId();
	}

	@Override
	public JRCrosstabDataset getDataset()
	{
		return dataset;
	}

	@Override
	public JRCrosstabRowGroup[] getRowGroups()
	{
		return rowGroups;
	}

	@Override
	public JRCrosstabColumnGroup[] getColumnGroups()
	{
		return columnGroups;
	}

	@Override
	public JRCrosstabMeasure[] getMeasures()
	{
		return measures;
	}
	
	private int determineLastColumnGroupWithHeaderIndex()
	{
		int colGroupIdx = columnGroups.length -1;
		while (colGroupIdx >= 0)
		{
			JRCellContents header = columnGroups[colGroupIdx].getHeader();
			if (header != null && !header.getChildren().isEmpty())
			{
				break;
			}
			
			--colGroupIdx;
		}
		
		int lastGroupIndex = colGroupIdx >= 0 ? colGroupIdx : 0;
		if (log.isDebugEnabled())
		{
			log.debug("last column group with header is " + lastGroupIndex);
		}
		return lastGroupIndex;
	}

	
	/**
	 * Fill-time crosstab input dataset implementation.
	 *  
	 * @author Lucian Chirita (lucianc@users.sourceforge.net)
	 */
	public class JRFillCrosstabDataset extends JRFillElementDataset implements JRCrosstabDataset
	{
		public static final String EXCEPTION_MESSAGE_KEY_DATASET_INCREMENTING_ERROR = "crosstabs.dataset.incrementing.error";

		private Object[] bucketValues;

		private Object[] measureValues;

		public JRFillCrosstabDataset(JRCrosstabDataset dataset, JRFillObjectFactory factory)
		{
			super(dataset, factory);

			this.bucketValues = new Object[rowGroups.length + columnGroups.length];
			this.measureValues = new Object[measures.length];
		}

		@Override
		protected void customInitialize()
		{
			initBucketingService();
		}

		@Override
		protected void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException
		{
			for (int i = 0; i < rowGroups.length; i++)
			{
				bucketValues[i] = calculator.evaluate(rowGroups[i].getBucket().getExpression());
			}

			for (int i = 0; i < columnGroups.length; ++i)
			{
				bucketValues[i + rowGroups.length] = calculator.evaluate(columnGroups[i].getBucket().getExpression());
			}

			for (int i = 0; i < measures.length; i++)
			{
				measureValues[i] = calculator.evaluate(measures[i].getValueExpression());
			}
		}

		@Override
		protected void customIncrement()
		{
			try
			{
				bucketingService.addData(bucketValues, measureValues);
			}
			catch (JRException e)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_DATASET_INCREMENTING_ERROR,
						(Object[])null,
						e);
			}
		}

		@Override
		public void collectExpressions(JRExpressionCollector collector)
		{
		}

		@Override
		public boolean isDataPreSorted()
		{
			return ((JRCrosstabDataset) parent).isDataPreSorted();
		}
	}
	
	/**
	 * Crosstab filler class.
	 *  
	 * @author Lucian Chirita (lucianc@users.sourceforge.net)
	 */
	protected class CrosstabFiller
	{
		private int yOffset;
		private int yChunkOffset;
		private boolean willOverflow;

		private int[] rowHeadersXOffsets;
		
		private boolean[] columnBreakable;
		private boolean[] rowBreakable;
		private int[] columnCount;
		private int[] rowCount;
		private int[] columnXOffsets;
		
		private boolean noDataCellPrinted;
		private boolean titlePrinted;
		
		private int startRowIndex;
		private int startColumnIndex;
		private int lastColumnIndex;
		private List<HeaderCell[]> columnHeaders;

		private List<List<JRPrintElement>> printRows;
		private int[] dataColumnSortMeasures;

		private HeaderCell[] spanHeaders;
		private int[] spanHeadersStart;

		private List<Integer> rowYs = new ArrayList<>();
		private int rowIdx;
		
		private List<JRFillCellContents> preparedRow = new ArrayList<>();
		private int preparedRowHeight;
		
		private boolean printRowHeaders;
		private boolean printColumnHeaders;
		
		private JRFillVariable rowCountVar;
		private JRFillVariable evenRowVar;
		private JRFillVariable colCountVar;
		private JRFillVariable evenColVar;

		protected CrosstabFiller()
		{
			setRowHeadersXOffsets();

			printRows = new ArrayList<>();
			
			rowCountVar = variablesMap.get(JRCrosstab.VARIABLE_ROW_COUNT);
			evenRowVar = variablesMap.get(JRCrosstab.VARIABLE_IS_EVEN_ROW);
			colCountVar = variablesMap.get(JRCrosstab.VARIABLE_COLUMN_COUNT);
			evenColVar = variablesMap.get(JRCrosstab.VARIABLE_IS_EVEN_COLUMN);
		}
		
		protected void initCrosstab()
		{
			columnXOffsets = computeOffsets(columnHeadersData, columnGroups, true);
			columnBreakable = computeBreakableHeaders(columnHeadersData, columnGroups, columnXOffsets, true, true);
			columnCount = computeCounts(columnHeadersData);
			
			int[] rowYOffsets = computeOffsets(rowHeadersData, rowGroups, false);
			rowBreakable = computeBreakableHeaders(rowHeadersData, rowGroups, rowYOffsets, false, false);
			applyKeepTogether(rowHeadersData, rowBreakable);
			rowCount = computeCounts(rowHeadersData);
			
			spanHeaders = new HeaderCell[rowGroups.length - 1];
			spanHeadersStart = new int[rowGroups.length - 1];
			
			startRowIndex = 0;
			startColumnIndex = 0;
			lastColumnIndex = 0;
			noDataCellPrinted = false;
			titlePrinted = false;
		}

		protected void setRowHeadersXOffsets()
		{
			rowHeadersXOffsets = new int[rowGroups.length + 1];
			rowHeadersXOffsets[0] = 0;
			for (int i = 0; i < rowGroups.length; i++)
			{
				rowHeadersXOffsets[i + 1] = rowHeadersXOffsets[i] + rowGroups[i].getWidth();
			}
		}

		protected int[] computeOffsets(HeaderCell[][] headersData, JRFillCrosstabGroup[] groups, boolean width)
		{
			int[] offsets = new int[headersData[0].length + 1];
			offsets[0] = 0;
			for (int i = 0; i < headersData[0].length; i++)
			{
				int size = 0;
				for (int j = groups.length - 1; j >= 0; --j)
				{
					if (headersData[j][i] != null)
					{
						JRFillCellContents cell = headersData[j][i].isTotal() ? groups[j].getFillTotalHeader() : groups[j].getFillHeader();
						size = cell == null ? 0 : (width ? cell.getWidth() : cell.getHeight());
						break;
					}
				}

				offsets[i + 1] = offsets[i] + size;
			}

			return offsets;
		}

		protected boolean[] computeBreakableHeaders(HeaderCell[][] headersData, JRFillCrosstabGroup[] groups, int[] offsets, boolean width, boolean startHeaders)
		{
			boolean[] breakable = new boolean[headersData[0].length];
			for (int i = 0; i < breakable.length; i++)
			{
				breakable[i] = true;
			}

			for (int j = 0; j < groups.length; ++j)
			{
				JRFillCellContents fillHeader = groups[j].getFillHeader();
				
				if (fillHeader != null)
				{
					int size = width ? fillHeader.getWidth() : fillHeader.getHeight();
					
					for (int i = 0; i < headersData[0].length; i++)
					{
						HeaderCell header = headersData[j][i];
						if (header != null && !header.isTotal() && header.getLevelSpan() > 1)
						{
							int span = header.getLevelSpan();
							
							if (startHeaders)
							{
								for (int k = i + 1; k < i + span && offsets[k] - offsets[i] < size; ++k)
								{
									breakable[k] = false;
								}
							}
							
							for (int k = i + span - 1; k > i && offsets[i + span] - offsets[k] < size; --k)
							{
								breakable[k] = false;
							}
						}
					}
				}
			}

			return breakable;
		}

		/**
		 * Applies the keepTogether flag for row groups that have it set.
		 * This method only sets breakable entries to false, never to true,
		 * so it does not interfere with the values already computed by
		 * computeBreakableHeaders; it only adds further restrictions on
		 * top of the existing ones.
		 */
		protected void applyKeepTogether(HeaderCell[][] headersData, boolean[] breakable)
		{
			for (int j = 0; j < rowGroups.length; ++j)
			{
				if (rowGroups[j].isKeepTogether())
				{
					for (int i = 0; i < headersData[0].length; i++)
					{
						HeaderCell header = headersData[j][i];
						if (header != null && !header.isTotal() && header.getLevelSpan() > 1)
						{
							int span = header.getLevelSpan();
							for (int k = i + 1; k < i + span; ++k)
							{
								breakable[k] = false;
							}
						}
					}
				}
			}
		}

		private int[] computeCounts(HeaderCell[][] headersData)
		{
			int[] counts = new int[headersData[0].length];
			
			HeaderCell[] lastHeaders = headersData[headersData.length - 1];
			for (int i = 0, c = 0; i < counts.length; ++i)
			{
				HeaderCell lastHeader = lastHeaders[i];
				if (lastHeader != null && !lastHeader.isTotal())
				{
					++c;
				}
				
				counts[i] = c;
			}
			
			return counts;
		}

		protected void fill(int availableHeight) throws JRException
		{
			printRows.clear();

			yOffset = 0;
			yChunkOffset = 0;
			willOverflow = false;
			
			fillVerticalCrosstab(availableHeight);
		}
		
		protected boolean willOverflow()
		{
			return willOverflow;
		}
		
		protected boolean ended()
		{
			return hasData ? (startRowIndex >= rowHeadersData[0].length && startColumnIndex >= columnHeadersData[0].length) : noDataCellPrinted;
		}
		
		protected void fillVerticalCrosstab(int availableHeight) throws JRException
		{
			JRLineBox lineBox = getLineBox();
			int padding = lineBox.getTopPadding() + lineBox.getBottomPadding();
			int contentsHeight = availableHeight - padding;
			
			if (contentsHeight < 0)
			{
				willOverflow = true;
				return;
			}

			if (!hasData)
			{
				fillNoDataCell(contentsHeight);			
				if (!printRows.isEmpty())
				{
					addFilledRows();
				}
				return;
			}
			
			printRowHeaders = startColumnIndex == 0 || isRepeatRowHeaders();
			int rowHeadersXOffset = printRowHeaders ? rowHeadersXOffsets[rowGroups.length] : 0;

			if (startColumnIndex == lastColumnIndex)
			{
				int availableWidth;
				if (ignoreWidth)
				{
					//FIXME lucianc this assumes that the crosstab is not placed in a subreport or table
					availableWidth = filler.maxPageWidth - filler.leftMargin - filler.rightMargin
							- getX() - lineBox.getLeftPadding() - lineBox.getRightPadding();
				}
				else
				{
					availableWidth = getWidth() - lineBox.getLeftPadding() - lineBox.getRightPadding();
				}

				columnHeaders = getGroupHeaders(availableWidth - rowHeadersXOffset, columnXOffsets, columnBreakable, startColumnIndex, columnHeadersData, columnGroups);
				lastColumnIndex = startColumnIndex + columnHeaders.size();

				if (startColumnIndex == lastColumnIndex)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_NOT_ENOUGH_SPACE,  
							(Object[])null 
							);
				}
			}
			
			List<JRPrintElement> titleRow = null;
			if (startRowIndex == 0 && startColumnIndex == 0 && titleCellContents != null && !titlePrinted)
			{
				titleRow = fillTitleCell(contentsHeight - yOffset);
				if (willOverflow)
				{
					return;
				}
				titlePrinted = true;
			}
			
			if (interactive && measures.length > 1)
			{
				dataColumnSortMeasures = new int[lastColumnIndex - startColumnIndex];
				Arrays.fill(dataColumnSortMeasures, -1);
			}
			
			printColumnHeaders = startRowIndex == 0 || isRepeatColumnHeaders();
			List<List<JRPrintElement>> columnHeaderRows = null;
			if (printColumnHeaders)
			{
				columnHeaderRows = fillColumnHeaders(rowHeadersXOffset, contentsHeight - yOffset);
				if (willOverflow)
				{
					//FIXME avoid repeating this
					if (titleRow != null)
					{
						addPrintRow(titleRow);
						addFilledRows();
					}
					
					return;
				}
			}

			int lastRowIndex = fillRows(rowHeadersXOffset, contentsHeight - yOffset);

			if (lastRowIndex == startRowIndex)
			{
				willOverflow = true;
				
				if (titleRow != null)
				{
					addPrintRow(titleRow);
					addFilledRows();
				}
				
				return;
			}

			if (titleRow != null)
			{
				addPrintRow(titleRow);
			}
			
			if (columnHeaderRows != null)
			{
				printRows.addAll(columnHeaderRows);
			}
			
			if (!printRows.isEmpty())
			{
				addFilledRows();
			}
			
			if (lastRowIndex >= rowHeadersData[0].length)
			{
				startColumnIndex = lastColumnIndex;

				if (startColumnIndex < columnHeadersData[0].length)
				{
					startRowIndex = lastRowIndex = 0;

					// set the chunk offset and compute the remaining height
					int yAdvance = yOffset + (getColumnBreakOffset() == null ? DEFAULT_COLUMN_BREAK_OFFSET : getColumnBreakOffset()) + padding;
					yChunkOffset += yAdvance;
					int remainingHeight = availableHeight - yAdvance;
					
					// reset the elements offset
					yOffset = 0;
					
					// fill a new chunk
					fillVerticalCrosstab(remainingHeight);
					return;
				}
			}

			//FIXME is this needed?  we do setPrepareHeight in prepare()
			boolean fillEnded = lastRowIndex >= rowHeadersData[0].length && lastColumnIndex >= columnHeadersData[0].length;
			if (fillEnded)
			{
				setPrepareHeight(yOffset);
			}
			else
			{
				setPrepareHeight(availableHeight);
			}

			startRowIndex = lastRowIndex;

			willOverflow = !fillEnded;
		}

		protected void addFilledRows()
		{
			List<JRPrintElement> prints = new ArrayList<>();
			for (Iterator<List<JRPrintElement>> it = printRows.iterator(); it.hasNext();)
			{
				List<JRPrintElement> rowPrints = it.next();
				prints.addAll(rowPrints);
			}
			
			// add the crosstab chunk to the element
			addCrosstabChunk(prints, yChunkOffset);
			
			// clear the added rows
			printRows.clear();
		}
		
		protected List<HeaderCell[]> getGroupHeaders(
				int available, 
				int[] offsets, 
				boolean[] breakable, 
				int firstIndex, 
				HeaderCell[][] headersData, 
				JRFillCrosstabGroup[] groups
				)
		{
			List<HeaderCell[]> headers = new ArrayList<>();

			int maxOffset = available + offsets[firstIndex];
			int lastIndex;
			for (lastIndex = firstIndex; 
				lastIndex < headersData[0].length 
					&& offsets[lastIndex + 1] <= maxOffset; 
				++lastIndex)
			{
				HeaderCell[] groupHeaders = new HeaderCell[groups.length];

				for (int j = 0; j < groups.length; ++j)
				{
					groupHeaders[j] = headersData[j][lastIndex];
				}

				headers.add(groupHeaders);
			}

			
			if (lastIndex < headersData[0].length)
			{
				while(lastIndex > firstIndex && !breakable[lastIndex])
				{
					--lastIndex;
					headers.remove(headers.size() - 1);
				}
			}
			
			if (lastIndex > firstIndex)
			{
				if (firstIndex > 0)
				{
					HeaderCell[] firstHeaders = headers.get(0);

					for (int j = 0; j < groups.length; ++j)
					{
						HeaderCell header = headersData[j][firstIndex];

						if (header == null)
						{
							int spanIndex = getSpanIndex(firstIndex, j, headersData);
							if (spanIndex >= 0)
							{
								HeaderCell spanCell = headersData[j][spanIndex];
								int headerEndIdx = spanCell.getLevelSpan() + spanIndex;
								if (headerEndIdx > lastIndex)
								{
									headerEndIdx = lastIndex;
								}
								firstHeaders[j] = HeaderCell.createLevelSpanCopy(spanCell, headerEndIdx - firstIndex);
							}
						}
					}
				}

				if (lastIndex < headersData[0].length)
				{
					for (int j = 0; j < groups.length; ++j)
					{
						HeaderCell header = headersData[j][lastIndex];

						if (header == null)
						{
							int spanIndex = getSpanIndex(lastIndex, j, headersData);
							if (spanIndex >= firstIndex)
							{
								HeaderCell spanCell = headersData[j][spanIndex];
								HeaderCell[] headerCells = headers.get(spanIndex - firstIndex);
								headerCells[j] = HeaderCell.createLevelSpanCopy(spanCell, lastIndex - spanIndex);
							}
						}
					}
				}
			}

			return headers;
		}

		
		protected int getSpanIndex(int i, int j, HeaderCell[][] headersData)
		{
			int spanIndex = i - 1;
			while (spanIndex >= 0 && headersData[j][spanIndex] == null)
			{
				--spanIndex;
			}

			if (spanIndex >= 0)
			{
				HeaderCell spanCell = headersData[j][spanIndex];
				int span = spanCell.getLevelSpan();

				if (span > i - spanIndex)
				{
					return spanIndex;
				}
			}

			return -1;
		}
		
		
		protected void fillNoDataCell(int availableHeight) throws JRException
		{
			if (whenNoDataCell == null)
			{
				noDataCellPrinted = true;
			}
			else
			{
				if (availableHeight < whenNoDataCell.getHeight())
				{
					willOverflow = true;
				}
				else
				{
					whenNoDataCell.evaluate(JRExpression.EVALUATION_DEFAULT);
					whenNoDataCell.prepare(availableHeight);
					
					willOverflow = whenNoDataCell.willOverflow();
					
					if (!willOverflow)
					{
						whenNoDataCell.setX(0);
						whenNoDataCell.setY(0);
						
						JRPrintFrame printCell = whenNoDataCell.fill();
						List<JRPrintElement> noDataRow = new ArrayList<>(1);
						noDataRow.add(printCell);
						addPrintRow(noDataRow);
						
						yOffset += whenNoDataCell.getPrintHeight();
						noDataCellPrinted = true;
					}
				}
			}
		}
		

		protected List<List<JRPrintElement>> fillColumnHeaders(int rowHeadersXOffset, int availableHeight) throws JRException
		{
			JRFillCellContents[][] columnHeaderRows = new JRFillCellContents[columnGroups.length][lastColumnIndex - startColumnIndex + 2];
			
			rowYs.clear();
			rowYs.add(0);
			
			if (printRowHeaders && headerCell != null)
			{
				JRFillCellContents contents = fillHeader(availableHeight);

				if (willOverflow)
				{
					return null;
				}

				columnHeaderRows[columnGroups.length - 1][0] = contents;
			}
			
			rows:
			for (rowIdx = 0; rowIdx < columnGroups.length; rowIdx++)
			{
				if (printRowHeaders)
				{
					JRFillCellContents cell = prepareColumnCrosstabHeader(availableHeight);
					columnHeaderRows[rowIdx][1] = cell;
					if (willOverflow)
					{
						break rows;
					}
				}
				
				for (int columnIdx = startColumnIndex; columnIdx < lastColumnIndex; ++columnIdx)
				{
					HeaderCell[] headers = columnHeaders.get(columnIdx - startColumnIndex);
					HeaderCell cell = headers[rowIdx];
					
					if (cell != null)
					{
						JRFillCellContents contents = prepareColumnHeader(cell, rowIdx, columnIdx, rowHeadersXOffset, availableHeight);
						columnHeaderRows[rowIdx + cell.getDepthSpan() - 1][columnIdx - startColumnIndex + 2] = contents;
						
						if (willOverflow)
						{
							break rows;
						}
					}
				}

				int rowStretchHeight = stretchColumnHeadersRow(columnHeaderRows[rowIdx]);
				rowYs.add(rowYs.get(rowIdx) + rowStretchHeight);
			}
			
			List<List<JRPrintElement>> headerRows;
			if (willOverflow)
			{
				headerRows = null;				
				releaseColumnHeaderCells(columnHeaderRows);
			}
			else
			{
				headerRows = fillColumnHeaders(columnHeaderRows);
				// reversing so that the overall header cell comes before the column crosstab header
				Collections.reverse(headerRows);

				yOffset += rowYs.get(columnGroups.length);
			}

			resetVariables();
			
			return headerRows;
		}

		
		private void setCountVars(int rowIdx, int colIdx)
		{
			if (rowIdx == -1)
			{
				rowCountVar.setValue(null);
				if (evenRowVar != null)
				{
					evenRowVar.setValue(null);
				}
			}
			else
			{
				rowCountVar.setValue(rowCount[rowIdx]);
				if (evenRowVar != null)
				{
					evenRowVar.setValue(rowCount[rowIdx] % 2 == 0);
				}
			}
			
			if (colIdx == -1)
			{
				colCountVar.setValue(null);
				if (evenColVar != null)
				{
					evenColVar.setValue(null);
				}
			}
			else
			{
				colCountVar.setValue(columnCount[colIdx]);
				if (evenColVar != null)
				{
					evenColVar.setValue(columnCount[colIdx] % 2 == 0);
				}
			}
			
		}

		private List<JRPrintElement> fillTitleCell(int availableHeight) throws JRException
		{
			setCountVars(-1, -1);
			
			JRFillCellContents cell = titleCellContents;
			int width = rowHeadersXOffsets[rowGroups.length] + columnXOffsets[lastColumnIndex] - columnXOffsets[startColumnIndex];
			cell = 
				cell.getTransformedContents(
					width, 
					cell.getHeight(), 
					getTitleCell().getContentsPosition() == null ? CrosstabColumnPositionEnum.LEFT : getTitleCell().getContentsPosition(), 
					CrosstabRowPositionEnum.TOP
					);
			
			JRFillCellContents contents = cell.getWorkingClone();
			contents.evaluate(JRExpression.EVALUATION_DEFAULT);
			contents.prepare(availableHeight);
			
			willOverflow = contents.willOverflow();
			if (willOverflow)
			{
				return null;
			}
			
			contents.setX(0);
			contents.setY(yOffset);
			contents.setHorizontalSpan(rowGroups.length + lastColumnIndex - startColumnIndex);
			contents.setVerticalSpan(1);
			
			JRPrintFrame printCell = contents.fill();
			List<JRPrintElement> titleRow = new ArrayList<>(1);
			titleRow.add(printCell);
			
			yOffset += contents.getPrintHeight();
			
			return titleRow;
		}
		
		private JRFillCellContents fillHeader(int availableHeight) throws JRException
		{
			setCountVars(-1, -1);
			
			JRFillCellContents cell = headerCell;
			if (!headerCell.getChildren().isEmpty() && interactive)
			{
				// look for row group column headers
				cell = decorateCellWithRowGroupIconLabel(cell);
			}
			
			JRFillCellContents contents = cell.getWorkingClone();
			contents.evaluate(JRExpression.EVALUATION_DEFAULT);
			contents.prepare(availableHeight);
			
			if (interactive)
			{
				contents.addHtmlClass(HTML_CLASS_CROSS_FLOATING);
			}
			
			willOverflow = contents.willOverflow();
			
			if (!willOverflow)
			{
				contents.setX(0);
				contents.setY(yOffset);
				contents.setVerticalSpan(columnGroups.length);
				contents.setHorizontalSpan(rowGroups.length);
			}
			return contents;
		}

		private JRFillCellContents decorateCellWithRowGroupIconLabel(JRFillCellContents cell)
		{
			List<JRChild> cellElements = cell.getChildren();
			BucketDefinition[] rowBuckets = bucketingService.getRowBuckets();
			int[] headerTextIndices = new int[rowBuckets.length];
			Arrays.fill(headerTextIndices, -1);
			boolean[] alignedText = new boolean[rowBuckets.length];
			boolean foundHeader = false;
			for (int bucketIdx = 0, bucketXOffset = 0; bucketIdx < rowBuckets.length; 
					bucketXOffset += rowGroups[bucketIdx].getWidth(), bucketIdx++)
			{
				int headerIndex = findRowGroupColumHeaderElementIndex(bucketIdx, cellElements);
				if (headerIndex < 0)
				{
					continue;
				}
				
				JRElement headerElement = (JRElement) cellElements.get(headerIndex);
				boolean aligned = headerElement.getX() == bucketXOffset 
						&& headerElement.getWidth() == rowGroups[bucketIdx].getWidth();
				
				BucketDefinition rowBucket = rowBuckets[bucketIdx];
				if (bucketOrder(rowBucket) == BucketOrder.NONE && !aligned)
				{
					continue;
				}
				
				headerTextIndices[bucketIdx] = headerIndex;
				alignedText[bucketIdx] = aligned;
				foundHeader = true;
			}
			
			if (!foundHeader)
			{
				return cell;
			}
			
			JRFillCellContents decoratedCell = (JRFillCellContents) cell.createClone();
			List<JRChild> clonedElements = decoratedCell.getChildren();
			
			BuiltinExpressionEvaluatorFactory builtinExpressions = new BuiltinExpressionEvaluatorFactory();
			JRFillExpressionEvaluator decoratedEvaluator = builtinExpressions.decorate(cell.expressionEvaluator);
			
			NavigableMap<Integer, JRDesignComponentElement> iconLabelElements = new TreeMap<>();
			for (int bucketIdx = 0; bucketIdx < headerTextIndices.length; bucketIdx++)
			{
				int textElementIndex = headerTextIndices[bucketIdx];
				if (textElementIndex >= 0)
				{
					JRFillTextElement textElement = (JRFillTextElement) clonedElements.get(textElementIndex);
					BucketOrder bucketOrder = bucketOrder(rowBuckets[bucketIdx]);
					if (bucketOrder == BucketOrder.NONE)
					{
						if (alignedText[bucketIdx])
						{
							// adding properties so that the text is selected as part of the row group column
							textElement.setExpressionEvaluator(decoratedEvaluator);
							textElement.addDynamicProperty(CrosstabConstants.PROPERTY_COLUMN_INDEX, 
									builtinExpressions.createConstantExpression(Integer.toString(bucketIdx)));
							textElement.addDynamicProperty(HtmlExporter.PROPERTY_HTML_CLASS, 
									builtinExpressions.createConstantExpression("jrxtrowheader jrxtinteractive " + HTML_CLASS_ROW_FLOATING));
						}
					}
					else
					{
						textElement.setExpressionEvaluator(decoratedEvaluator);
						textElement.addDynamicProperty(MatcherExporterFilter.PROPERTY_MATCHER_EXPORT_FILTER_KEY, 
								builtinExpressions.createConstantExpression(TableReport.TABLE_HEADER_LABEL_MATCHER_EXPORT_KEY));
						
						SortOrderEnum order = BucketOrder.toSortOrderEnum(bucketOrder);
						if (log.isDebugEnabled())
						{
							log.debug("wrapping header element " + textElement.getUUID() + " in iconLabel for row group " + bucketIdx);
						}
						
						JRDesignComponentElement iconLabelElement = createIconLabelElement(order, 
								textElement, builtinExpressions);
						if (alignedText[bucketIdx])
						{
							iconLabelElement.getPropertiesMap().setProperty(CrosstabConstants.PROPERTY_COLUMN_INDEX, 
									Integer.toString(bucketIdx));
							iconLabelElement.getPropertiesMap().setProperty(HtmlExporter.PROPERTY_HTML_CLASS, "jrxtrowheader jrxtinteractive " + HTML_CLASS_ROW_FLOATING);
						}
						
						iconLabelElements.put(textElementIndex, iconLabelElement);
					}
				}
			}
			
			IconLabelFillObjectFactory factory = new IconLabelFillObjectFactory(fillFactory, decoratedEvaluator);
			for (Entry<Integer, JRDesignComponentElement> entry : iconLabelElements.descendingMap().entrySet())
			{
				int elementIndex = entry.getKey();
				JRDesignComponentElement iconLabelElement = entry.getValue();
				JRFillComponentElement fillIconLabelElement = new JRFillComponentElement(filler, iconLabelElement, factory);
				decoratedCell.addElement(elementIndex + 1, fillIconLabelElement);
			}
			
			return decoratedCell;
		}
		
		private int findRowGroupColumHeaderElementIndex(int rowGroupIndex, List<JRChild> cellElements)
		{
			String rowGropIndexStr = Integer.toString(rowGroupIndex);
			int colHeaderTextIndex = -1;
			for (ListIterator<JRChild> elemIt = cellElements.listIterator(cellElements.size()); elemIt.hasPrevious();)
			{
				JRChild child = elemIt.previous();
				if (child instanceof JRTextElement)
				{
					JRFillTextElement textElement = (JRFillTextElement) child;
					JRElement parentElement = textElement.getParent();
					String prop = parentElement.hasProperties() 
							? parentElement.getPropertiesMap().getProperty(PROPERTY_ROW_GROUP_COLUMN_HEADER) : null;
					if (prop != null && prop.equals(rowGropIndexStr))
					{
						// found it
						colHeaderTextIndex = elemIt.nextIndex();
						break;
					}
				}
			}
			return colHeaderTextIndex;
		}
		
		private JRFillCellContents prepareColumnHeader(HeaderCell cell, int rowIdx, int columnIdx, int xOffset, int availableHeight) throws JRException
		{
			JRFillCrosstabColumnGroup group = columnGroups[rowIdx];
			JRFillCellContents contents = cell.isTotal() ? group.getFillTotalHeader() : group.getFillHeader();

			int width = columnXOffsets[columnIdx + cell.getLevelSpan()] - columnXOffsets[columnIdx];
			int height = contents.getHeight();
			
			if (width <= 0 || height <= 0)
			{
				return null;
			}
			
			// column header is a cell that is on the last level with a column header and has no row span 
			boolean headerLabel = rowIdx + cell.getDepthSpan() == lastColumnGroupWithHeaderIndex + 1
					&& cell.getLevelSpan() == 1;
			// a column header is a cell that comes after the last level and has no row span
			boolean header = rowIdx + cell.getDepthSpan() >= lastColumnGroupWithHeaderIndex + 1
					&& cell.getLevelSpan() == 1;
			
			JRFillCellContents preparedContents = null;
			
			int rowY = rowYs.get(rowIdx);
			
			if (availableHeight >=  rowY + height)
			{
				setCountVars(-1, columnIdx);
				setGroupVariables(columnGroups, cell.getBucketValues());
				setGroupMeasureVariables(cell, false);
				
				contents = contents.getTransformedContents(width, height, group.getPosition(), CrosstabRowPositionEnum.TOP);
				boolean firstOnRow = columnIdx == startColumnIndex && (!printRowHeaders || headerCell == null);
				contents = contents.getBoxContents(
						firstOnRow && getRunDirection() == RunDirectionEnum.LTR,
						firstOnRow && getRunDirection() == RunDirectionEnum.RTL,
						false);
				
				// check if the column is sorted
				if (interactive && headerLabel && matchesOrderByColumn(cell))
				{
					contents = decorateWithSortIcon(contents, orderByColumnInfo.getOrder());
				}
				
				contents = contents.getWorkingClone();

				contents.evaluate(JRExpression.EVALUATION_DEFAULT);
				contents.prepare(availableHeight - rowY);
				
				if (interactive)
				{
					contents.addHtmlClass(HTML_CLASS_COLUMN_FLOATING);

					boolean sortHeader = false;
					if (headerLabel && measures.length > 1)
					{
						if (measures.length <= 1)
						{
							sortHeader = true;
						}
						else
						{
							// looking for the sorting measure index property in the column header
							int sortMeasureIdx = determineColumnSortMeasure(contents);
							dataColumnSortMeasures[columnIdx - startColumnIndex] = sortMeasureIdx;
							sortHeader = sortMeasureIdx >= 0;
						}
					}

					if (header)
					{
						contents.setPrintProperty(CrosstabConstants.PROPERTY_COLUMN_INDEX, Integer.toString(columnIdx));
						contents.addHtmlClass("jrxtcolheader");
						if (sortHeader)
						{
							contents.addHtmlClass("jrxtinteractive");
						}
					}
				}

				if (contents.willOverflow())
				{
					willOverflow = true;
				}
				else
				{
					contents.setX(columnXOffsets[columnIdx] - columnXOffsets[startColumnIndex] + xOffset);
					contents.setY(rowY + yOffset);
					contents.setVerticalSpan(cell.getDepthSpan());
					contents.setHorizontalSpan(cell.getLevelSpan());
					
					preparedContents = contents;
				}
			}
			else
			{
				willOverflow = true;
			}
			
			return preparedContents;
		}

		private int determineColumnSortMeasure(JRFillCellContents contents)
		{
			int sortMeasureIdx = -1;
			JRElement[] cellElements = contents.getElements();
			for (JRElement element : cellElements)
			{
				String sortMeasureIdxProp = element.hasProperties() 
						? element.getPropertiesMap().getProperty(PROPERTY_COLUMN_HEADER_SORT_MEASURE_INDEX) : null;
				if (sortMeasureIdxProp != null)
				{
					sortMeasureIdx = JRPropertiesUtil.asInteger(sortMeasureIdxProp);
					break;
				}
			}
			return sortMeasureIdx;
		}

		private JRFillCellContents decorateWithSortIcon(JRFillCellContents cell, SortOrderEnum order)
		{
			// check whether the contents contain a single text element
			JRElement[] elements = cell.getElements();
			if (elements.length != 1)
			{
				if (log.isDebugEnabled())
				{
					log.debug("order by column header has " + elements.length + " elements");
				}
				
				return cell;
			}
			
			if (!(elements[0] instanceof JRTextElement))
			{
				if (log.isDebugEnabled())
				{
					log.debug("order by column header has element " + elements[0].getClass().getName());
				}
				
				return cell;
			}
			
			// TODO lucianc cache
			JRFillTextElement textElement = (JRFillTextElement) elements[0];
			if (log.isDebugEnabled())
			{
				log.debug("wrapping column header element " + textElement.getUUID() + " in iconLabel");
			}
			
			BuiltinExpressionEvaluatorFactory builtinExpressions = new BuiltinExpressionEvaluatorFactory();
			JRDesignComponentElement iconLabelElement = createIconLabelElement(order, textElement, builtinExpressions);

			JRFillExpressionEvaluator decoratedEvaluator = builtinExpressions.decorate(cell.expressionEvaluator);
			IconLabelFillObjectFactory factory = new IconLabelFillObjectFactory(fillFactory, decoratedEvaluator);
			JRFillComponentElement fillIconLabelElement = new JRFillComponentElement(filler, iconLabelElement, factory);
			
			JRFillCellContents clonedCell = (JRFillCellContents) cell.createClone();
			clonedCell.addElement(1, fillIconLabelElement);
			
			JRFillElement clonedTextElement = (JRFillElement) clonedCell.getElements()[0];
			clonedTextElement.setExpressionEvaluator(decoratedEvaluator);
			clonedTextElement.addDynamicProperty(MatcherExporterFilter.PROPERTY_MATCHER_EXPORT_FILTER_KEY, 
					builtinExpressions.createConstantExpression(TableReport.TABLE_HEADER_LABEL_MATCHER_EXPORT_KEY));
			
			return clonedCell;
		}

		protected JRDesignComponentElement createIconLabelElement(SortOrderEnum order, JRFillTextElement textElement,
				BuiltinExpressionEvaluatorFactory builtinExpressions)
		{
			JRTextElement parentElement = (JRTextElement) textElement.getParent();
			
			JRDesignComponentElement designIconLabelElement = 
				IconLabelComponentUtil.getInstance(filler.getJasperReportsContext()).createIconLabelComponentElement(parentElement, textElement);
			IconLabelComponent iconLabelComponent = (IconLabelComponent)designIconLabelElement.getComponent();

			JRDesignTextField labelTextField = (JRDesignTextField)iconLabelComponent.getLabelTextField();
			JRDesignTextField iconTextField = (JRDesignTextField)iconLabelComponent.getIconTextField();

			designIconLabelElement.setStyle(textElement.getInitStyle());
			labelTextField.setStyle(textElement.getInitStyle());
			iconTextField.setStyle(textElement.getInitStyle());

			if (textElement instanceof JRTextField) 
			{
				labelTextField.setExpression(((JRTextField) textElement).getExpression());
			}
			else if (textElement instanceof JRStaticText) 
			{
				String text = ((JRStaticText) textElement).getText();
				labelTextField.setExpression(builtinExpressions.createConstantExpression(text));
			}
			
			String iconText =
					order == SortOrderEnum.ASCENDING ? filler.getPropertiesUtil().getProperty(TableReport.PROPERTY_UP_ARROW_CHAR)
					: (order == SortOrderEnum.DESCENDING ? filler.getPropertiesUtil().getProperty(TableReport.PROPERTY_DOWN_ARROW_CHAR) : "");
			iconTextField.setExpression(builtinExpressions.createConstantExpression(" " + iconText)); 
			
			designIconLabelElement.getPropertiesMap().setProperty(
					MatcherExporterFilter.PROPERTY_MATCHER_EXPORT_FILTER_KEY, 
					TableReport.TABLE_HEADER_ICON_LABEL_MATCHER_EXPORT_KEY
					);
			
			return designIconLabelElement;
		}

		protected JRFillCellContents prepareColumnCrosstabHeader(int availableHeight) throws JRException
		{
			JRFillCellContents header = columnGroups[rowIdx].getFillCrosstabHeader();
			if (header == null)
			{
				return null;
			}
			
			int width = header.getWidth();
			int height = header.getHeight();
			if (width <= 0 || height <= 0)
			{
				return null;
			}
			
			int rowY = rowYs.get(rowIdx);
			if (availableHeight <  rowY + height)
			{
				willOverflow = true;
				return null;
			}
			
			if (rowIdx == columnGroups.length - 1 && !header.getChildren().isEmpty() && interactive)
			{
				// look for row group column headers in the last column group's header
				header = decorateCellWithRowGroupIconLabel(header);
			}
			
			JRFillCellContents preparedCell = header.getWorkingClone();
			setCountVars(-1, -1);
			preparedCell.evaluate(JRExpression.EVALUATION_DEFAULT);
			preparedCell.prepare(availableHeight - rowY);
			
			if (interactive)
			{
				preparedCell.addHtmlClass(HTML_CLASS_CROSS_FLOATING);
			}
			
			if (preparedCell.willOverflow())
			{
				willOverflow = true;
				return null;
			}

			preparedCell.setX(0);
			preparedCell.setY(rowY + yOffset);
			preparedCell.setHorizontalSpan(rowGroups.length);
			preparedCell.setVerticalSpan(1);
			
			return preparedCell;
		}
		
		private int stretchColumnHeadersRow(JRFillCellContents[] headers)
		{
			int rowY = rowYs.get(rowIdx);
			
			int rowStretchHeight = 0;
			for (int j = 0; j < headers.length; j++)
			{
				JRFillCellContents contents = headers[j];
				
				if (contents != null)
				{
					int startRowY = rowY;
					if (contents.getVerticalSpan() > 1)
					{
						startRowY = rowYs.get(rowIdx - contents.getVerticalSpan() + 1);
					}
					
					int height = contents.getPrintHeight() - rowY + startRowY;
					
					if (height > rowStretchHeight)
					{
						rowStretchHeight = height;
					}
				}
			}
			
			for (int j = 0; j < headers.length; j++)
			{
				JRFillCellContents contents = headers[j];
				
				if (contents != null)
				{
					int startRowY = rowY;
					if (contents.getVerticalSpan() > 1)
					{
						startRowY = rowYs.get(rowIdx - contents.getVerticalSpan() + 1);
					}
					
					contents.stretchTo(rowStretchHeight + rowY - startRowY);
				}
			}
			
			return rowStretchHeight;
		}

		
		private List<List<JRPrintElement>> fillColumnHeaders(JRFillCellContents[][] columnHeaderRows) throws JRException
		{
			List<List<JRPrintElement>> headerRows = new ArrayList<>(columnGroups.length);
			
			for (int i = 0; i < columnHeaderRows.length; ++i)
			{
				List<JRPrintElement> headerRow = new ArrayList<>(lastColumnIndex - startColumnIndex);
				headerRows.add(headerRow);
				
				for (int j = 0; j < columnHeaderRows[i].length; j++)
				{
					JRFillCellContents contents = columnHeaderRows[i][j];
					
					if (contents != null)
					{
						headerRow.add(contents.fill());
						contents.releaseWorkingClone();
					}
				}
			}
			
			return headerRows;
		}

		private void releaseColumnHeaderCells(JRFillCellContents[][] columnHeaderRows) throws JRException
		{
			for (int i = 0; i < columnHeaderRows.length; ++i)
			{
				for (int j = 0; j < columnHeaderRows[i].length; j++)
				{
					JRFillCellContents contents = columnHeaderRows[i][j];
					
					if (contents != null)
					{
						contents.rewind();
						contents.releaseWorkingClone();
					}
				}
			}
		}
		
		protected int fillRows(int xOffset, int availableHeight) throws JRException
		{
			rowYs.clear();			
			rowYs.add(0);

			for (rowIdx = 0; rowIdx < cellData.length - startRowIndex; ++rowIdx)
			{
				initPreparedRow();
				
				prepareRow(xOffset, availableHeight);

				if (willOverflow)
				{
					break;
				}
				
				fillRow();
				
				rowYs.add(rowYs.get(rowIdx) + preparedRowHeight);
			}
			
			if (rowIdx < cellData.length - startRowIndex)//overflow
			{
				releasePreparedRow();
				
				if (printRowHeaders)
				{
					fillContinuingRowHeaders(xOffset, availableHeight);
				}
			}
			
			yOffset += rowYs.get(rowIdx);

			return rowIdx + startRowIndex;
		}

		private void initPreparedRow()
		{
			preparedRow.clear();
			preparedRowHeight = 0;
		}

		private void removeFilledRows(int rowsToRemove)
		{
			if (rowsToRemove > 0)
			{
				for (int i = 0; i < rowsToRemove; ++i)
				{
					printRows.remove(printRows.size() - 1);
					rowYs.remove(rowYs.size() - 1);
				}
				
				rowIdx -= rowsToRemove;
			}
		}

		private void releasePreparedRow() throws JRException
		{
			for (Iterator<JRFillCellContents> it = preparedRow.iterator(); it.hasNext();)
			{
				JRFillCellContents cell = it.next();
				cell.rewind();
				cell.releaseWorkingClone();
			}
			
			preparedRow.clear();
		}

		private void fillRow() throws JRException
		{
			int rowY = rowYs.get(rowIdx);
			
			List<JRPrintElement> rowPrints = new ArrayList<>(preparedRow.size());
			for (Iterator<JRFillCellContents> it = preparedRow.iterator(); it.hasNext();)
			{
				JRFillCellContents cell = it.next();
				
				int spanHeight = 0;
				if (cell.getVerticalSpan() > 1)
				{
					spanHeight = rowY - rowYs.get(rowIdx - cell.getVerticalSpan() + 1);
				}
				
				cell.stretchTo(preparedRowHeight + spanHeight);
				rowPrints.add(cell.fill());
				
				cell.releaseWorkingClone();
			}
			
			addPrintRow(rowPrints);
		}
		
		private void prepareRow(int xOffset, int availableHeight) throws JRException
		{
			for (int col = startColumnIndex; col < lastColumnIndex; ++col)
			{
				CrosstabCell data = cellData[rowIdx + startRowIndex][col];
				boolean overflow = prepareDataCell(data, col, availableHeight, xOffset);
				
				if (overflow)
				{
					willOverflow = true;
					return;
				}
			}
			
			resetVariables();
			
			if (printRowHeaders)
			{
				for (int j = 0; j < rowGroups.length; j++)
				{
					HeaderCell cell = rowHeadersData[j][rowIdx + startRowIndex];
					int vSpan = 0;
					if (cell == null)
					{
						// if we have a span header
						if (toCloseRowHeader(j))
						{
							cell = spanHeaders[j];
							vSpan = cell.getLevelSpan();
							if (spanHeadersStart[j] < startRowIndex)//continuing from the prev page
							{
								vSpan += spanHeadersStart[j] - startRowIndex;
							}
						}
					}
					else
					{
						if (cell.getLevelSpan() > 1)
						{
							spanHeaders[j] = cell;
							spanHeadersStart[j] = rowIdx + startRowIndex;
							continue;
						}
						
						vSpan = 1;
					}
					
					if (cell != null)
					{
						boolean overflow = prepareRowHeader(j, cell, vSpan, availableHeight);						
						if (overflow)
						{
							willOverflow = true;
							return;
						}
					}
				}
				
				// successfully prepared a row, reset the span headers
				for (int j = 0; j < rowGroups.length; j++)
				{
					// if we have a span header
					if (rowHeadersData[j][rowIdx + startRowIndex] == null 
							&& toCloseRowHeader(j))
					{
						// reset it
						spanHeaders[j] = null;
					}
				}
				
				resetVariables();
			}
		}
		
		private boolean prepareDataCell(CrosstabCell data, int column, int availableHeight, int xOffset) throws JRException
		{
			int rowY = rowYs.get(rowIdx);
			
			JRFillCrosstabCell cell = crossCells[data.getRowTotalGroupIndex()][data.getColumnTotalGroupIndex()];
			JRFillCellContents contents = cell == null ? null : cell.getFillContents();
			if (contents == null || contents.getWidth() <= 0 || contents.getHeight() <= 0)
			{
				return false;
			}
			
			boolean overflow = availableHeight < rowY + contents.getHeight();
			if (!overflow)
			{
				boolean leftEmpty = startColumnIndex != 0 && !isRepeatRowHeaders();
				boolean topEmpty = startRowIndex != 0 && !isRepeatColumnHeaders();
				
				setCountVars(rowIdx + startRowIndex, column);
				setGroupVariables(rowGroups, data.getRowBucketValues());
				setGroupVariables(columnGroups, data.getColumnBucketValues());
				setMeasureVariables(data);
				
				boolean firstOnRow = leftEmpty && column == startColumnIndex;
				contents = contents.getBoxContents(
						firstOnRow && getRunDirection() == RunDirectionEnum.LTR,
						firstOnRow && getRunDirection() == RunDirectionEnum.RTL,
						topEmpty && rowIdx == 0);
				contents = contents.getWorkingClone();
				
				contents.evaluate(JRExpression.EVALUATION_DEFAULT);
				contents.prepare(availableHeight - rowY);
				
				if (interactive)
				{
					contents.setPrintProperty(CrosstabConstants.PROPERTY_COLUMN_INDEX, Integer.toString(column));
					contents.addHtmlClass("jrxtdatacell");
				}
								
				preparedRow.add(contents);
				
				overflow = contents.willOverflow();
				
				if (!overflow)
				{
					contents.setX(columnXOffsets[column] - columnXOffsets[startColumnIndex] + xOffset);
					contents.setY(rowY + yOffset);

					int rowCellHeight = Math.max(contents.getPrintHeight(), contents.getHeight());
					if (rowCellHeight > preparedRowHeight)
					{
						preparedRowHeight = rowCellHeight;
					}
				}
			}
			
			return overflow;
		}

		private boolean prepareRowHeader(int rowGroup, HeaderCell cell, int vSpan, int availableHeight) throws JRException
		{
			JRFillCrosstabRowGroup group = rowGroups[rowGroup];
			JRFillCellContents contents = cell.isTotal() ? group.getFillTotalHeader() : group.getFillHeader();

			if (contents.getWidth() <= 0 || contents.getHeight() <= 0)
			{
				return false;
			}
			
			int spanHeight = 0;
			int headerY = rowYs.get(rowIdx - vSpan + 1);
			if (vSpan > 1)
			{
				spanHeight += rowYs.get(rowIdx) - headerY;
			}
			int rowHeight = spanHeight + preparedRowHeight;
			
			boolean stretchContents = group.getPosition() == CrosstabRowPositionEnum.STRETCH;
			int contentsHeight = stretchContents ? rowHeight : contents.getHeight();
			
			boolean headerOverflow = availableHeight <  headerY + contentsHeight || rowHeight < contents.getHeight();
			
			if (!headerOverflow)
			{
				setCountVars(rowIdx + startRowIndex - vSpan + 1, -1);
				setGroupVariables(rowGroups, cell.getBucketValues());
				setGroupMeasureVariables(cell, true);

				if (stretchContents)
				{
					contents = contents.getTransformedContents(contents.getWidth(), rowHeight, CrosstabColumnPositionEnum.LEFT, CrosstabRowPositionEnum.STRETCH);
				}
				contents = contents.getBoxContents(false, false, rowIdx + 1 == vSpan && (!printColumnHeaders || headerCell == null));
				contents.getWorkingClone();

				contents.evaluate(JRExpression.EVALUATION_DEFAULT);
				contents.prepare(availableHeight - headerY);
				
				if (interactive)
				{
					contents.addHtmlClass(HTML_CLASS_ROW_FLOATING);

					contents.setPrintProperty(CrosstabConstants.PROPERTY_COLUMN_INDEX, Integer.toString(rowGroup));
					if (cell.getDepthSpan() == 1)
					{
						// marking only unspanned headers for HTML selection 
						contents.addHtmlClass("jrxtrowheader");
					}
				}
				
				preparedRow.add(contents);

				headerOverflow = contents.willOverflow();
				
				if (!headerOverflow)
				{
					contents.setX(rowHeadersXOffsets[rowGroup]);
					contents.setY(headerY + yOffset);
					contents.setVerticalSpan(vSpan);
					contents.setHorizontalSpan(cell.getDepthSpan());
					
					int rowCellHeight = contents.getPrintHeight() - spanHeight;
					if (rowCellHeight > preparedRowHeight)
					{
						preparedRowHeight = rowCellHeight;
					}
				}
			}
			
			if (headerOverflow)
			{
				removeFilledRows(vSpan - 1);
			}
			
			return headerOverflow;
		}

		protected boolean toCloseRowHeader(int rowGroup)
		{
			return rowGroup < rowGroups.length - 1 && 
					spanHeaders[rowGroup] != null && 
					spanHeaders[rowGroup].getLevelSpan() + spanHeadersStart[rowGroup] == 
						rowIdx + startRowIndex + 1;
		}

		private void removeExceedingSpanHeaders()
		{
			for (int j = rowGroups.length - 2; j >= 0; --j)
			{
				if (spanHeaders[j] != null && spanHeadersStart[j] >= rowIdx + startRowIndex)
				{
					spanHeaders[j] = null;
				}
			}
		}

		private void setBackSpanHeaders()
		{
			for (int j = rowGroups.length - 2; j >= 0 && spanHeaders[j] == null; --j)
			{
				int spanIndex = getSpanIndex(rowIdx + startRowIndex, j, rowHeadersData);
				
				if (spanIndex >= 0)
				{
					spanHeaders[j] = rowHeadersData[j][spanIndex];
					spanHeadersStart[j] = spanIndex;
				}
			}
		}

		private void fillContinuingRowHeaders(int xOffset, int availableHeight) throws JRException
		{
			boolean done = false;
			breakCrosstab:
			do
			{
				removeExceedingSpanHeaders();
				
				if (!rowBreakable[rowIdx + startRowIndex])
				{
					removeFilledRows(1);
					setBackSpanHeaders();
					continue;
				}

				initPreparedRow();
				
				//fill continuing headers
				for (int j = 0; j < rowGroups.length - 1; ++j)
				{
					if (spanHeaders[j] != null)
					{
						boolean headerOverflow = prepareContinuingRowHeader(j, availableHeight);
						
						if (headerOverflow)
						{
							releasePreparedRow();
							continue breakCrosstab;
						}
					}
				}

				if (!preparedRow.isEmpty())
				{
					int lastRowHeight = rowYs.get(rowIdx) - rowYs.get(rowIdx - 1);
					
					if (preparedRowHeight > lastRowHeight)//need to stretch already filled row by refilling
					{
						refillLastRow(xOffset, availableHeight);
					}
					else
					{
						fillContinuingHeaders(lastRowHeight);
					}
				}
				
				done = true;
			}
			while (!done && rowIdx > 0);
		}

		private void fillContinuingHeaders(int lastRowHeight) throws JRException
		{
			int nextToLastHeaderY = rowYs.get(rowIdx - 1);
			List<JRPrintElement> lastPrintRow = getLastPrintRow();
			
			for (int j = 0; j < preparedRow.size(); ++j)
			{
				JRFillCellContents contents = preparedRow.get(j);
				
				int headerY = rowYs.get(rowIdx - contents.getVerticalSpan());
				
				contents.stretchTo(nextToLastHeaderY - headerY + lastRowHeight);
				lastPrintRow.add(contents.fill());
				contents.releaseWorkingClone();
			}
		}

		private void refillLastRow(int xOffset, int availableHeight) throws JRException
		{
			removeFilledRows(1);
			setBackSpanHeaders();
			
			prepareRow(xOffset, availableHeight);
			fillRow();
			
			rowYs.add(rowYs.get(rowIdx) + preparedRowHeight);
			++rowIdx;
		}

		private boolean prepareContinuingRowHeader(int rowGroup, int availableHeight) throws JRException
		{
			HeaderCell cell = spanHeaders[rowGroup];
			int vSpan = rowIdx + startRowIndex - spanHeadersStart[rowGroup];

			if (spanHeadersStart[rowGroup] < startRowIndex)//continuing from the prev page
			{
				vSpan += spanHeadersStart[rowGroup] - startRowIndex;
			}

			int headerY = rowYs.get(rowIdx - vSpan);
			int lastHeaderY = rowYs.get(rowIdx);
			int headerHeight = lastHeaderY - headerY;
			int nextToLastHeaderY = rowYs.get(rowIdx - 1);
			int stretchHeight = nextToLastHeaderY - headerY;
			
			JRFillCrosstabRowGroup group = rowGroups[rowGroup];
			JRFillCellContents contents = cell.isTotal() ? group.getFillTotalHeader() : group.getFillHeader();
			
			boolean stretchContents = group.getPosition() == CrosstabRowPositionEnum.STRETCH;
			int contentsHeight = stretchContents ? headerHeight : contents.getHeight();
			
			boolean headerOverflow = availableHeight < headerY + contentsHeight || headerHeight < contents.getHeight();
			if (!headerOverflow)
			{
				setCountVars(rowIdx + startRowIndex - vSpan, -1);
				setGroupVariables(rowGroups, cell.getBucketValues());
				setGroupMeasureVariables(cell, true);

				if (stretchContents)
				{
					contents = contents.getTransformedContents(contents.getWidth(), headerHeight, CrosstabColumnPositionEnum.LEFT, CrosstabRowPositionEnum.STRETCH);
				}
				
				contents = contents.getBoxContents(false, false, rowIdx == vSpan && (!printColumnHeaders || headerCell == null));
				contents.getWorkingClone();

				contents.evaluate(JRExpression.EVALUATION_DEFAULT);
				contents.prepare(availableHeight - headerY);
				
				if (interactive)
				{
					contents.addHtmlClass(HTML_CLASS_ROW_FLOATING);

					contents.setPrintProperty(CrosstabConstants.PROPERTY_COLUMN_INDEX, Integer.toString(rowGroup));
					if (cell.getDepthSpan() == 1)
					{
						// marking only unspanned headers for HTML selection 
						contents.addHtmlClass("jrxtrowheader");
					}
				}
				
				preparedRow.add(contents);

				headerOverflow = contents.willOverflow();

				if (!headerOverflow)
				{
					contents.setX(rowHeadersXOffsets[rowGroup]);
					contents.setY(headerY + yOffset);
					contents.setVerticalSpan(vSpan);//FIXME is this correct?
					contents.setHorizontalSpan(cell.getDepthSpan());
					
					int rowHeight = contents.getPrintHeight() - stretchHeight;
					if (rowHeight > preparedRowHeight)
					{
						preparedRowHeight = rowHeight;
					}
				}
			}

			if (headerOverflow)
			{
				removeFilledRows(vSpan);
			}
			
			return headerOverflow;
		}
		
		protected void addPrintRow(List<JRPrintElement> printRow)
		{
			printRows.add(printRow);
		}
		
		protected List<JRPrintElement> getLastPrintRow()
		{
			return printRows.get(printRows.size() - 1);
		}

		protected void setGroupVariables(JRFillCrosstabGroup[] groups, Bucket[] bucketValues)
		{
			for (int i = 0; i < groups.length; i++)
			{
				Object value = null;
				if (bucketValues[i] != null && !bucketValues[i].isTotal())
				{
					value = bucketValues[i].getValue();
				}
				groups[i].getFillVariable().setValue(value);
			}
		}

		protected void setGroupMeasureVariables(HeaderCell cell, boolean rowGroup)
		{
			MeasureValue[][] totals = cell.getTotals();
			for (int m = 0; m < measures.length; m++)
			{
				for (int row = 0; row <= rowGroups.length; row++)
				{
					for (int col = 0; col <= columnGroups.length; col++)
					{
						// setting the same values for all row/column groups when filling column/row headers
						MeasureValue[] vals = rowGroup ? totals[row] : totals[col];
						if (row == rowGroups.length && col == columnGroups.length)
						{
							// setting the base measure variable
							Object value = measureValue(vals, m);
							measures[m].getFillVariable().setValue(value);
						}
						else if (retrieveTotal[row][col])
						{
							JRFillVariable totalVar = totalVariables[row][col][m];
							Object value = measureValue(vals, m);
							totalVar.setValue(value);
						}
					}
				}
			}
		}

		protected void setMeasureVariables(CrosstabCell cell)
		{
			MeasureValue[] values = cell.getMesureValues();
			for (int i = 0; i < measures.length; i++)
			{
				Object value = measureValue(values, i);
				measures[i].getFillVariable().setValue(value);
			}
			
			MeasureValue[][][] totals = cell.getTotals();
			for (int row = 0; row <= rowGroups.length; row++)
			{
				for (int col = 0; col <= columnGroups.length; col++)
				{
					MeasureValue[] vals = totals[row][col];
					if (retrieveTotal[row][col])
					{
						for (int m = 0; m < measures.length; m++)
						{
							JRFillVariable totalVar = totalVariables[row][col][m];
							Object value = measureValue(vals, m);
							totalVar.setValue(value);
						}
					}
				}
			}
		}

		
		protected Object measureValue(MeasureValue[] values, int measureIdx)
		{
			if (values == null)
			{
				return null;
			}
			
			Object value;
			if (measures[measureIdx].getPercentageType() == CrosstabPercentageEnum.GRAND_TOTAL)
			{
				if (values[measureIdx].isInitialized())
				{
					value = values[measureIdx].getValue();
				}
				else
				{
					value = measures[measureIdx].getPercentageCalculator().calculatePercentage(values[measureIdx], grandTotals[measureIdx]);
				}
			}
			else
			{
				value = values[measureIdx].getValue();
			}
			return value;
		}

		
		protected void resetVariables()
		{
			for (int i = 0; i < rowGroups.length; i++)
			{
				rowGroups[i].getFillVariable().setValue(null);
			}
			
			for (int i = 0; i < columnGroups.length; i++)
			{
				columnGroups[i].getFillVariable().setValue(null);
			}
			
			for (int i = 0; i < measures.length; i++)
			{
				measures[i].getFillVariable().setValue(null);
			}
			
			for (int row = 0; row <= rowGroups.length; ++row)
			{
				for (int col = 0; col <= columnGroups.length; ++col)
				{
					if (retrieveTotal[row][col])
					{
						for (int i = 0; i < measures.length; i++)
						{
							totalVariables[row][col][i].setValue(null);
						}
					}
				}
			}
		}
	}

	@Override
	public Integer getColumnBreakOffset()
	{
		return parentCrosstab.getColumnBreakOffset();
	}

	@Override
	public boolean isRepeatColumnHeaders()
	{
		return parentCrosstab.isRepeatColumnHeaders();
	}

	@Override
	public boolean isRepeatRowHeaders()
	{
		return parentCrosstab.isRepeatRowHeaders();
	}

	@Override
	public JRCrosstabCell[][] getCells()
	{
		return crossCells;
	}

	@Override
	public JRCellContents getWhenNoDataCell()
	{
		return whenNoDataCell;
	}

	@Override
	public JRCrosstabParameter[] getParameters()
	{
		return parameters;
	}

	@Override
	public JRExpression getParametersMapExpression()
	{
		return parentCrosstab.getParametersMapExpression();
	}

	
	@Override
	public JRElement getElementByKey(String elementKey)
	{
		return JRBaseCrosstab.getElementByKey(this, elementKey);
	}

	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		//not needed
		return null;
	}

	@Override
	public CrosstabColumnCell getTitleCell()
	{
		return parentCrosstab.getTitleCell();
	}

	@Override
	public JRCellContents getHeaderCell()
	{
		return headerCell;
	}

	@Override
	public JRVariable[] getVariables()
	{
		return variables;
	}

	@Override
	public RunDirectionEnum getRunDirection()
	{
		return parentCrosstab.getRunDirection();
	}

	@Override
	public void setRunDirection(RunDirectionEnum runDirection)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public HorizontalPosition getHorizontalPosition()
	{
		return parentCrosstab.getHorizontalPosition();
	}

	@Override
	public void setHorizontalPosition(HorizontalPosition horizontalPosition)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JROrigin getOrigin()
	{
		return getElementOrigin();
	}

	@Override
	public Boolean getIgnoreWidth()
	{
		return parentCrosstab.getIgnoreWidth();
	}

	@Override
	public void setIgnoreWidth(Boolean ignoreWidth)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getDefaultLineColor()
	{
		return parentCrosstab.getDefaultLineColor();
	}

	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	@Override
	public JasperReportsContext getJasperReportsContext()
	{
		return filler.getJasperReportsContext();
	}

	@Override
	public JRFillExpressionEvaluator getExpressionEvaluator()
	{
		return expressionEvaluator;
	}

}
