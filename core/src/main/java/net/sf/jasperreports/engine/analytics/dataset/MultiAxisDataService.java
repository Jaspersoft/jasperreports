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
package net.sf.jasperreports.engine.analytics.dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.crosstabs.fill.calculation.BucketDefinition;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketDefinition.Bucket;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketingService;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketingService.BucketMap;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketingServiceContext;
import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition;
import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition.MeasureValue;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.analytics.data.Axis;
import net.sf.jasperreports.engine.analytics.data.AxisLevel;
import net.sf.jasperreports.engine.analytics.data.AxisLevel.Type;
import net.sf.jasperreports.engine.analytics.data.AxisLevelNode;
import net.sf.jasperreports.engine.analytics.data.MappedPropertyValues;
import net.sf.jasperreports.engine.analytics.data.Measure;
import net.sf.jasperreports.engine.analytics.data.MultiAxisDataSource;
import net.sf.jasperreports.engine.analytics.data.PropertyValues;
import net.sf.jasperreports.engine.analytics.data.StandardAxisLevel;
import net.sf.jasperreports.engine.analytics.data.StandardMeasure;
import net.sf.jasperreports.engine.analytics.data.StandardMeasureValue;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRDefaultIncrementerFactory;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRExtendedIncrementerFactory;
import net.sf.jasperreports.engine.fill.JRFillExpressionEvaluator;
import net.sf.jasperreports.engine.fill.JRIncrementerFactoryCache;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.util.SingleValue;
import net.sf.jasperreports.engine.util.ValuePropertiesWrapper;
import net.sf.jasperreports.engine.util.ValuePropertiesWrapperComparator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class MultiAxisDataService
{
	
	protected static final Log log = LogFactory.getLog(MultiAxisDataService.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_INCREMENT_BIDIMENSIONAL_DATASET_ERROR = "engine.analytics.dataset.increment.bidimensional.dataset.error";

	private final BucketingServiceContext serviceContext;
	private final MultiAxisData data;
	private final MultiAxisBucketingService bucketingService;
	
	private final Map<DataAxisLevel, Map<String, Integer>> axisLevelBucketPropertyIndexes = new HashMap<>();
	
	private final List<List<AxisLevel>> axisLevels;
	private final Bucket[] axisRoots;
	private final List<Measure> measures;
	
	private final Object[] bucketValues;
	private final Object[] measureValues;

	public MultiAxisDataService(JasperReportsContext jasperReportsContext,
			JRFillExpressionEvaluator expressionEvaluator, 
			MultiAxisData data, byte evaluation) throws JRException
	{
		this.serviceContext = new ServiceContext(jasperReportsContext, expressionEvaluator);
		this.data = data;
		
		if (log.isDebugEnabled())
		{
			log.debug("creating multi axis data service for " + data);
		}
		
		this.axisLevels = new ArrayList<>(Axis.axisCount());
		this.axisLevels.addAll(Collections.<List<AxisLevel>>nCopies(Axis.axisCount(), null));
		
		List<List<BucketDefinition>> axisBuckets = new ArrayList<>(Axis.axisCount());
		axisBuckets.addAll(Collections.<List<BucketDefinition>>nCopies(Axis.axisCount(), null));
		
		this.axisRoots = new Bucket[Axis.axisCount()];
		
		for (Axis axis : Axis.values())
		{
			DataAxis dataAxis = data.getDataAxis(axis);
			List<DataAxisLevel> dataLevels = dataAxis == null ? Collections.<DataAxisLevel>emptyList() 
					: dataAxis.getLevels();
			
			int levelCount = axisLevels.size();
			
			List<AxisLevel> levels = new ArrayList<>(levelCount + 1);
			levels.add(createRootLevel(axis));
			
			List<BucketDefinition> buckets = new ArrayList<>(levelCount + 1);
			BucketDefinition rowRootBucket = createRootBucket();
			buckets.add(rowRootBucket);
			axisRoots[axis.ordinal()] = rowRootBucket.create(SingleValue.VALUE);
			
			for (DataAxisLevel dataLevel : dataLevels)
			{				
				// create the data source level
				AxisLevel level = createLevel(axis, dataLevel, evaluation, levels.size());
				levels.add(level);
				// create the bucket
				buckets.add(createServiceBucket(dataLevel, evaluation));
				
				if (log.isDebugEnabled())
				{
					log.debug("created level " + level);
				}
			}
			
			this.axisLevels.set(axis.ordinal(), levels);
			axisBuckets.set(axis.ordinal(), buckets);
		}

		List<DataMeasure> dataMeasures = data.getMeasures();
		this.measures = new ArrayList<>(dataMeasures.size());
		List<MeasureDefinition> measureList = new ArrayList<>(dataMeasures.size());
		for (DataMeasure dataMeasure : dataMeasures)
		{
			// create the data source measure
			Measure measure = createMeasure(dataMeasure, evaluation);
			this.measures.add(measure);
			// create the bucketing measure
			measureList.add(createServiceMeasure(dataMeasure));
			
			if (log.isDebugEnabled())
			{
				log.debug("created measure " + measure);
			}
		}

		// compute all totals
		
		List<BucketDefinition> rowBuckets = axisBuckets.get(Axis.ROWS.ordinal());
		if (rowBuckets.size() > 1)
		{
			rowBuckets.get(1).setComputeTotal();
		}
		List<BucketDefinition> colBuckets = axisBuckets.get(Axis.COLUMNS.ordinal());
		colBuckets.get(0).setComputeTotal();
		
		bucketValues = new Object[rowBuckets.size() + colBuckets.size()];
		measureValues = new Object[dataMeasures.size()];

		// all false
		boolean[][] retrieveTotal = new boolean[rowBuckets.size() + 1][colBuckets.size() + 2];
		bucketingService = new MultiAxisBucketingService(
				rowBuckets, colBuckets, measureList, retrieveTotal);
	}

	protected AxisLevel createRootLevel(Axis axis) throws JRException
	{
		StandardAxisLevel level = new StandardAxisLevel();
		level.setAxis(axis);
		level.setType(Type.ROOT);
		level.setName(StandardAxisLevel.ROOT_LEVEL_NAME);
		level.setLabel(StandardAxisLevel.ROOT_LEVEL_NAME);
		level.setValueType(SingleValue.class);
		level.setDepth(0);
		return level;
	}

	protected BucketDefinition createRootBucket() throws JRException
	{
		return new BucketDefinition(SingleValue.class,
				null, null, BucketOrder.ASCENDING, 
				CrosstabTotalPositionEnum.START);
	}

	protected AxisLevel createLevel(Axis axis, DataAxisLevel dataLevel, byte evaluation, int depth) throws JRException
	{
		StandardAxisLevel level = new StandardAxisLevel();
		level.setAxis(axis);
		level.setName(dataLevel.getName());
		String label = (String) serviceContext.getExpressionEvaluator().evaluate(
				dataLevel.getLabelExpression(), evaluation);
		level.setLabel(label);
		level.setValueType(dataLevel.getBucket().getValueClass());
		level.setDepth(depth);
		return level;
	}

	protected BucketDefinition createServiceBucket(DataAxisLevel level, byte evaluation) throws JRException
	{
		DataLevelBucket bucket = level.getBucket();
		Class<?> valueClass = bucket.getValueClass();

		Comparator<Object> comparator = null;
		JRExpression comparatorExpression = bucket.getComparatorExpression();
		if (comparatorExpression != null)
		{
			comparator = (Comparator<Object>) serviceContext.getExpressionEvaluator().evaluate(
					comparatorExpression, evaluation);
		}

		BucketDefinition bucketDefinition;
		List<DataLevelBucketProperty> bucketProperties = bucket.getBucketProperties();
		if (bucket.getLabelExpression() != null 
				|| (bucketProperties != null && !bucketProperties.isEmpty()))
		{
			// wrapping values in a ValuePropertiesWrapper in order to store property values
			Map<String, Integer> propertyIndexes = new LinkedHashMap<>();
			for (ListIterator<DataLevelBucketProperty> it = bucketProperties.listIterator(); it.hasNext();)
			{
				DataLevelBucketProperty bucketProperty = it.next();
				propertyIndexes.put(bucketProperty.getName(), it.previousIndex());
			}
			axisLevelBucketPropertyIndexes.put(level, propertyIndexes);
			
			bucketDefinition = new PropertiesWrapperBucketDefintion(
					comparator, bucket.getOrder(), 
					CrosstabTotalPositionEnum.START);
		}
		else
		{
			bucketDefinition = new BucketDefinition(valueClass,
					null, comparator, bucket.getOrder(), 
					CrosstabTotalPositionEnum.START);
		}
		
		return bucketDefinition;
	}

	protected Measure createMeasure(DataMeasure dataMeasure, byte evaluation) throws JRException
	{
		StandardMeasure measure = new StandardMeasure();
		measure.setName(dataMeasure.getName());
		String label = (String) serviceContext.getExpressionEvaluator().evaluate(
				dataMeasure.getLabelExpression(), evaluation);
		measure.setLabel(label);
		measure.setValueType(dataMeasure.getValueClass());
		return measure;
	}
	
	protected MeasureDefinition createServiceMeasure(DataMeasure measure)
	{
		JRExtendedIncrementerFactory incrFactory;
		String incrementerFactoryClassName = measure.getIncrementerFactoryClassName();
		if (incrementerFactoryClassName == null)
		{
			incrFactory = JRDefaultIncrementerFactory.getFactory(measure.getValueClass());
		}
		else
		{
			incrFactory = (JRExtendedIncrementerFactory) JRIncrementerFactoryCache.getInstance(
					measure.getIncrementerFactoryClass());
		}
		
		return new MeasureDefinition(
				measure.getValueClass(), 
				CalculationEnum.getValueOrDefault(measure.getCalculation()), 
				incrFactory); 
	}

	public void evaluateRecord(JRCalculator calculator) throws JRExpressionEvalException
	{
		int bucketIdx = 0;
		bucketValues[bucketIdx++] = SingleValue.VALUE;
		
		DataAxis rowAxis = data.getDataAxis(Axis.ROWS);
		if (rowAxis != null)
		{
			for (DataAxisLevel level : rowAxis.getLevels())
			{
				bucketValues[bucketIdx++] = evaluateBucketValue(calculator, level); 
			}
		}
		
		bucketValues[bucketIdx++] = SingleValue.VALUE;
		
		DataAxis colAxis = data.getDataAxis(Axis.COLUMNS);
		if (colAxis != null)
		{
			for (DataAxisLevel level : colAxis.getLevels())
			{
				bucketValues[bucketIdx++] = evaluateBucketValue(calculator, level);
			}
		}

		int measureIdx = 0;
		for (DataMeasure measure : data.getMeasures())
		{
			measureValues[measureIdx++] = calculator.evaluate(measure.getValueExpression());
		}
	}

	protected Object evaluateBucketValue(JRCalculator calculator,
			DataAxisLevel level) throws JRExpressionEvalException
	{
		DataLevelBucket bucket = level.getBucket();
		Object mainValue = calculator.evaluate(bucket.getExpression());
		
		JRExpression labelExpression = bucket.getLabelExpression();
		List<DataLevelBucketProperty> bucketProperties = bucket.getBucketProperties();
		Object bucketValue;
		if (labelExpression == null && (bucketProperties == null || bucketProperties.isEmpty()))
		{
			bucketValue = mainValue;
		}
		else
		{
			String label = labelExpression == null ? null : (String) calculator.evaluate(labelExpression);
			
			Object[] propertyValues;
			if (bucketProperties == null || bucketProperties.isEmpty())
			{
				propertyValues = null;
			}
			else
			{
				// evaluate property values
				//FIXME avoid evaluating these for each record
				propertyValues = new Object[bucketProperties.size()];
				for (ListIterator<DataLevelBucketProperty> it = bucketProperties.listIterator(); it.hasNext();)
				{
					DataLevelBucketProperty bucketProperty = it.next();
					propertyValues[it.previousIndex()] = calculator.evaluate(bucketProperty.getValueExpression());
				}
			}
			
			// wrap the main value and property values together
			bucketValue = new ValuePropertiesWrapper(mainValue, label, propertyValues);
		}
		
		return bucketValue;
	}
	
	public void addRecord()
	{
		try
		{
			bucketingService.addData(bucketValues, measureValues);
		}
		catch (JRException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INCREMENT_BIDIMENSIONAL_DATASET_ERROR,
					(Object[])null,
					e);
		}
	}
	
	public void clearData()
	{
		if (log.isDebugEnabled())
		{
			log.debug("clearing data");
		}
		
		bucketingService.clear();
	}

	public MultiAxisDataSource createDataSource() throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug("creating multi axis data source");
		}
		
		// TODO lucianc do we need a final increment?
		bucketingService.processData();
		
		return new DataSource();
	}
	
	protected class MultiAxisBucketingService extends BucketingService
	{
		public MultiAxisBucketingService(
				List<BucketDefinition> rowBuckets,
				List<BucketDefinition> columnBuckets,
				List<MeasureDefinition> measures,
				boolean[][] retrieveTotal)
		{
			super(MultiAxisDataService.this.serviceContext, rowBuckets, columnBuckets, measures, 
					false, retrieveTotal);
		}
		
		protected BucketMap getRowRootChildren()
		{
			if (axisLevels.get(Axis.ROWS.ordinal()).size() == 1)
			{
				// only root level
				return null;
			}

			return (BucketMap) bucketValueMap.get(axisRoots[Axis.ROWS.ordinal()]);
		}
		
		protected BucketMap getColumnRootChildren()
		{
			if (axisLevels.get(Axis.COLUMNS.ordinal()).size() == 1)
			{
				// only root level
				return null;
			}
			
			BucketMap bucketMap = (BucketMap) bucketValueMap.get(axisRoots[Axis.ROWS.ordinal()]);
			// descend on row total nodes
			int rowLevelsCount = axisLevels.get(Axis.ROWS.ordinal()).size();
			for (int idx = 1; bucketMap != null && idx < rowLevelsCount; ++idx)
			{
				bucketMap = (BucketMap) bucketMap.getTotalEntry().getValue();
			}
			
			if (bucketMap == null)
			{
				// this can happen when there was no data
				return null;
			}
			
			return (BucketMap) bucketMap.get(axisRoots[Axis.COLUMNS.ordinal()]);
		}
	}
	
	protected class DataSource implements MultiAxisDataSource
	{
		private final List<List<AxisLevel>> axisDataLevels;
		private static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_AXIS = "engine.analytics.dataset.unknown.axis";
		
		public DataSource()
		{
			axisDataLevels = new ArrayList<>(axisLevels.size());
			for (List<AxisLevel> levels : axisLevels)
			{
				List<AxisLevel> dataLevels = Collections.unmodifiableList(levels.subList(1, levels.size()));
				axisDataLevels.add(dataLevels);
			}
		}

		@Override
		public List<? extends AxisLevel> getAxisLevels(Axis axis)
		{
			return axisDataLevels.get(axis.ordinal());
		}

		@Override
		public List<? extends Measure> getMeasures()
		{
			return measures;
		}

		@Override
		public AxisLevelNode getAxisRootNode(Axis axis)
		{
			BucketMap rootChildren;
			switch (axis)
			{
			case ROWS:
				rootChildren = bucketingService.getRowRootChildren();
				break;
			case COLUMNS:
				rootChildren = bucketingService.getColumnRootChildren();
				break;
			default:
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_AXIS,
						new Object[]{axis});
			}
			
			Bucket rootBucket = axisRoots[axis.ordinal()];
			return new LevelNode(axis, 0, null, rootBucket, rootChildren);
		}

		@Override
		public List<? extends net.sf.jasperreports.engine.analytics.data.MeasureValue> getMeasureValues(AxisLevelNode... nodes)
		{
			if (nodes.length == 0)
			{
				throw new IllegalArgumentException("At least one node needs to be passed");
			}
			LevelNode rowNode = axisLevelNodeArgument(Axis.ROWS, nodes);
			LevelNode columnNode = axisLevelNodeArgument(Axis.COLUMNS, nodes);
			
			// the row node starting point
			BucketMap bucketMap;
			if (rowNode == null)
			{
				// if no row node was passed, start from the root
				bucketMap = bucketingService.getRowRootChildren();
			}
			else
			{
				bucketMap = rowNode.childrenMap;
			}
			
			// descend on row total nodes
			for (int idx = rowNode.axisDepth + 1; bucketMap != null && idx < axisLevels.get(Axis.ROWS.ordinal()).size(); ++idx)
			{
				bucketMap = (BucketMap) bucketMap.getTotal();
			}
			
			Object bucketValue;
			if (bucketMap != null)
			{
				LinkedList<Bucket> columnBuckets = new LinkedList<>();
				if (columnNode != null)
				{
					// add all column node parent buckets except the root
					for (LevelNode node = columnNode; node.axisDepth > 0; node = node.parent)
					{
						columnBuckets.addFirst(node.bucket);
					}
				}
				
				// descend on the root column node
				bucketValue = bucketMap.get(axisRoots[Axis.COLUMNS.ordinal()]);
				// descend on regular column nodes
				Iterator<Bucket> columnBucketIterator = columnBuckets.iterator();
				for (int idx = 1; idx < axisLevels.get(Axis.COLUMNS.ordinal()).size(); ++idx)
				{
					if (bucketValue == null)
					{
						// the bucket combination does not exist
						break;
					}
					
					bucketMap = (BucketMap) bucketValue;
					if (columnBucketIterator.hasNext())
					{
						// if we have a bucket value, descend on the value
						Bucket bucket = columnBucketIterator.next();
						bucketValue = bucketMap.get(bucket);
					}
					else
					{
						// if there's no value, descend on totals
						bucketValue = bucketMap.getTotal();
					}
				}
			}
			else
			{
				bucketValue = null;
			}
			
			MeasureValue[] values;
			if (bucketValue == null)
			{
				// the bucket combination does not exist
				values = bucketingService.getZeroUserMeasureValues();
			}
			else
			{
				MeasureValue[] rawValues = (MeasureValue[]) bucketValue;
				values = bucketingService.getUserMeasureValues(rawValues);
			}
			
			List<net.sf.jasperreports.engine.analytics.data.MeasureValue> measureValues = new ArrayList<>(values.length);
			for (int i = 0; i < values.length; i++)
			{
				Measure measure = measures.get(i);
				Object value = values[i].getValue();
				StandardMeasureValue measureValue = new StandardMeasureValue(measure, value);
				measureValues.add(measureValue);
			}
			
			if (log.isTraceEnabled())
			{
				log.trace("values for (" + rowNode + ", " + columnNode + "): " + measureValues);
			}
			
			return measureValues;
		}
		
		protected LevelNode axisLevelNodeArgument(Axis axis, AxisLevelNode... nodes)
		{
			LevelNode axisNode = null;
			for (AxisLevelNode node : nodes)
			{
				if (node.getLevel().getAxis() == axis)
				{
					if (axisNode != null)
					{
						throw new IllegalArgumentException("More than one node on axis " + axis);
					}
					
					if (!(node instanceof LevelNode))
					{
						throw new IllegalArgumentException("The method should be called with nodes created by this data source");
					}
					
					axisNode = (LevelNode) node;
				}
			}
			return axisNode;
		}
	}
	
	protected class LevelNode implements AxisLevelNode
	{
		protected final Axis axis;
		protected final int axisDepth;
		protected final LevelNode parent;
		protected final Bucket bucket;
		protected final BucketMap childrenMap;
		
		protected Object value;
		protected String label;
		protected PropertyValues propertyValues;
		
		public LevelNode(Axis axis, int axisDepth, LevelNode parent, Bucket bucket, BucketMap childrenMap)
		{
			this.axis = axis;
			this.axisDepth = axisDepth;
			this.parent = parent;
			this.bucket = bucket;
			this.childrenMap = childrenMap;
			
			initValues();
		}

		private void initValues()
		{
			Object bucketValue = bucket.getValue();
			Object nodeValue = bucketValue;
			String label = null;
			PropertyValues propertyValues = null;
			
			if (axisDepth > 0 && bucketValue != null)
			{
				DataAxisLevel dataLevel = data.getDataAxis(axis).getLevels().get(axisDepth - 1);
				DataLevelBucket dataBucket = dataLevel.getBucket();
				List<DataLevelBucketProperty> bucketProperties = dataBucket.getBucketProperties();
				if (dataBucket.getLabelExpression() != null || 
						(bucketProperties != null && !bucketProperties.isEmpty()))
				{
					// unwrap the raw value and the property values
					ValuePropertiesWrapper valueWrapper = (ValuePropertiesWrapper) bucketValue;
					nodeValue = valueWrapper.getValue();
					label = valueWrapper.getLabel();
					
					if (bucketProperties != null && !bucketProperties.isEmpty())
					{
						Map<String, Integer> propertyIndexes = axisLevelBucketPropertyIndexes.get(dataLevel);
						Object[] propertyValuesArray = valueWrapper.getPropertyValues();
						propertyValues = new MappedPropertyValues(propertyIndexes, propertyValuesArray);
					}
				}
			}
			
			this.value = nodeValue;
			this.label = label;
			this.propertyValues = propertyValues;
		}
		
		@Override
		public AxisLevel getLevel()
		{
			return axisLevels.get(axis.ordinal()).get(axisDepth);
		}

		@Override
		public boolean isTotal()
		{
			return axisDepth == 0 // the root level is considered total
					|| bucket.isTotal();
		}

		@Override
		public Object getValue()
		{
			return value;
		}

		@Override
		public String getLabel()
		{
			return label;
		}

		@Override
		public PropertyValues getNodePropertyValues()
		{
			return propertyValues;
		}

		@Override
		public AxisLevelNode getParent()
		{
			return parent;
		}

		@Override
		public List<? extends AxisLevelNode> getChildren()
		{
			if (axisDepth + 1 >= axisLevels.get(axis.ordinal()).size())
			{
				// last level on the axis
				return Collections.<AxisLevelNode>emptyList();
			}
			
			if (childrenMap == null)
			{
				// this happens for the root node when there was no data
				return Collections.<AxisLevelNode>emptyList();
			}

			List<LevelNode> children = new ArrayList<>(childrenMap.size());
			for (Iterator<Entry<Bucket, Object>> entryIterator = childrenMap.entryIterator(); entryIterator.hasNext();)
			{
				Entry<Bucket, Object> entry = entryIterator.next();
				Bucket childBucket = entry.getKey();
				Object entryValue = entry.getValue();
				BucketMap nextChildMap = entryValue instanceof BucketMap ? (BucketMap) entryValue : null;
				
				LevelNode childNode = new LevelNode(axis, axisDepth + 1, 
						this, childBucket, nextChildMap); 
				children.add(childNode);
			}
			
			if (log.isTraceEnabled())
			{
				log.trace("children of " + this + ": " + children);
			}
			
			return children;
		}
		
		// TODO lucianc equals & hashcode
		
		@Override
		public String toString()
		{
			return bucket + " on " + axis + " level " + axisDepth;
		}
	}
	
	protected static class ServiceContext implements BucketingServiceContext
	{
		private final JasperReportsContext jasperReportsContext;
		private final JRFillExpressionEvaluator expressionEvaluator;
		
		public ServiceContext(JasperReportsContext jasperReportsContext,
				JRFillExpressionEvaluator expressionEvaluator)
		{
			this.jasperReportsContext = jasperReportsContext;
			this.expressionEvaluator = expressionEvaluator;
		}

		@Override
		public JasperReportsContext getJasperReportsContext()
		{
			return jasperReportsContext;
		}

		@Override
		public JRFillExpressionEvaluator getExpressionEvaluator()
		{
			return expressionEvaluator;
		}

		@Override
		public Object evaluateMeasuresExpression(JRExpression expression,
				MeasureValue[] measureValues) throws JRException
		{
			throw new UnsupportedOperationException();
		}
		
	}
	
	protected static class PropertiesWrapperBucketDefintion extends BucketDefinition
	{
		public PropertiesWrapperBucketDefintion(Comparator<Object> comparator,
				BucketOrder order, CrosstabTotalPositionEnum totalPosition)
				throws JRException
		{
			super(ValuePropertiesWrapper.class, null, 
					comparator == null ? null : new ValuePropertiesWrapperComparator(comparator), 
					order, totalPosition);
		}

		@Override
		public Bucket create(Object value)
		{
			if (value instanceof ValuePropertiesWrapper && ((ValuePropertiesWrapper) value).getValue() == null)
			{
				return new Bucket(value, VALUE_TYPE_NULL);
			}
			
			return super.create(value);
		}
		
	}
}
