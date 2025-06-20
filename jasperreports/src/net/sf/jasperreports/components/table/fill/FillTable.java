/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
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
package net.sf.jasperreports.components.table.fill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.components.headertoolbar.HeaderToolbarElement;
import net.sf.jasperreports.components.subreport.fill.SubreportFillComponent;
import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Column;
import net.sf.jasperreports.components.table.ColumnGroup;
import net.sf.jasperreports.components.table.ColumnVisitor;
import net.sf.jasperreports.components.table.TableComponent;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRAbstractCompiler;
import net.sf.jasperreports.engine.design.JRReportCompileData;
import net.sf.jasperreports.engine.export.JRPdfExporterTagHelper;
import net.sf.jasperreports.engine.fill.BuiltinExpressionEvaluatorFactory;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillContext;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.type.HorizontalPosition;
import net.sf.jasperreports.engine.util.JRReportUtils;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.export.AccessibilityUtil;
import net.sf.jasperreports.export.type.AccessibilityTagEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillTable extends SubreportFillComponent
{

	private static final Log log = LogFactory.getLog(FillTable.class);
	
	protected static final String FILL_CACHE_KEY_TABLE_INSTANCE_COUNTER = FillTable.class.getName() + "#instanceCounter";
	protected static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_REPORT_DATA_TYPE = "components.table.unsupported.report.data.type";
	
	private final TableComponent table;
	private Map<List<FillColumn>, ComponentFillSubreportFactory> fillSubreportFactories;
	
	private boolean filling;
	private List<FillColumn> fillColumns;
	
	private HorizontalPosition horizontalPosition;
	private Boolean shrinkWidth;
	private Integer columnWeight;

	public FillTable(TableComponent table, JRFillObjectFactory factory)
	{
		super(table, factory);
		
		this.table = table;
		
		this.fillSubreportFactories = new HashMap<>();
	}

	public FillTable(FillTable table, JRFillCloneFactory factory)
	{
		super(table, factory);
		
		this.table = table.table;

		this.fillSubreportFactories = table.fillSubreportFactories;
	}
	
	private TableReport getBaseReport()
	{
		return ((TableJasperReport)fillSubreport.getJasperReport()).getBaseReport();
	}
	
	@Override
	protected HorizontalPosition getHorizontalPosition()
	{
		return horizontalPosition;
	}
	
	@Override
	protected Boolean shrinkWidth()
	{
		return shrinkWidth;
	}

	@Override
	public void evaluate(byte evaluation) throws JRException
	{
		if (filling)
		{
			log.warn("Table fill did not complete, canceling previous table subreport");
			fillSubreport.cancelSubreportFill();
		}
		
		filling = false;
		
		horizontalPosition = table.getHorizontalPosition();
		if (horizontalPosition == null)
		{
			String strHorizontalPosition = 
				fillContext.getFiller().getPropertiesUtil().getProperty(
					TableComponent.CONFIG_PROPERTY_HORIZONTAL_POSITION,
					fillContext.getComponentElement(),
					fillContext.getFiller().getMasterFiller().getJasperReport()
					);
			horizontalPosition = HorizontalPosition.getByName(strHorizontalPosition);
		}

		shrinkWidth = table.shrinkWidth();
		if (shrinkWidth == null)
		{
			String strShrinkWidth = 
				fillContext.getFiller().getPropertiesUtil().getProperty(
					TableComponent.CONFIG_PROPERTY_SHRINK_WIDTH,
					fillContext.getComponentElement(),
					fillContext.getFiller().getMasterFiller().getJasperReport()
					);
			shrinkWidth = Boolean.valueOf(strShrinkWidth);
		}

		String strColumnWeight = fillContext.getFiller().getPropertiesUtil().getProperty(
			TableComponent.CONFIG_PROPERTY_COLUMN_WEIGHT,
			fillContext.getComponentElement(),
			fillContext.getFiller().getMasterFiller().getJasperReport()
			);
		if (strColumnWeight != null && strColumnWeight.trim().length() > 0)
		{
			columnWeight = Integer.parseInt(strColumnWeight);
		}
		
		evaluateColumns(evaluation);
		if (!fillColumns.isEmpty())
		{
			resizeWeightedColumns(fillColumns, fillContext.getComponentElement().getWidth() - fillWidth, fillWeight);
			createFillSubreport();
			setTableInstanceCounter();
			fillSubreport.evaluateSubreport(evaluation);
		}
	}
	
	protected void setTableInstanceCounter()
	{
		JRFillContext fillerContext = fillContext.getFiller().getFillContext();
		AtomicInteger counter = (AtomicInteger) fillerContext.getFillCache(FILL_CACHE_KEY_TABLE_INSTANCE_COUNTER);
		if (counter == null)
		{
			// we just need a mutable integer, there's no actual concurrency here
			counter = new AtomicInteger();
			fillerContext.setFillCache(FILL_CACHE_KEY_TABLE_INSTANCE_COUNTER, counter);
		}
		
		int instanceIndex = counter.getAndIncrement();
		if (log.isDebugEnabled())
		{
			log.debug("table instance index is " + instanceIndex);
		}
		
		getBaseReport().setTableInstanceIndex(instanceIndex);
	}

	protected boolean toPrintColumn(BaseColumn column, byte evaluation) throws JRException
	{
		boolean toPrint;
		JRExpression printWhenExpression = column.getPrintWhenExpression();
		if (printWhenExpression == null)
		{
			toPrint = true;
		}
		else
		{
			Boolean printWhenVal = (Boolean) evaluateExpression(
					printWhenExpression, evaluation);
			if (printWhenVal == null)
			{
				toPrint = false;
			}
			else
			{
				toPrint = printWhenVal;
			}
		}
		return toPrint;
	}
	
	protected JRPropertiesMap evaluateProperties(BaseColumn column, byte evaluation) throws JRException
	{
		JRPropertiesMap staticProperties = column.hasProperties() ? column.getPropertiesMap().cloneProperties() : null;
		JRPropertiesMap mergedProperties = null;

		JRPropertyExpression[] propExprs = column.getPropertyExpressions();
		if (propExprs == null || propExprs.length == 0)
		{
			mergedProperties = staticProperties;
		}
		else
		{
			JRPropertiesMap dynamicProperties = new JRPropertiesMap();
			
			for (int i = 0; i < propExprs.length; i++)
			{
				JRPropertyExpression prop = propExprs[i];
				String value = (String) evaluateExpression(prop.getValueExpression(), evaluation);
				//if (value != null) //for some properties such as data properties in metadata exporters, the null value is significant
				{
					dynamicProperties.setProperty(prop.getName(), value);
				}
			}
			
			mergedProperties = dynamicProperties.cloneProperties();
			mergedProperties.setBaseProperties(staticProperties);
		}
		
		return mergedProperties;
	}
	
	protected class FillColumnEvaluator implements ColumnVisitor<FillColumn>
	{
		final byte evaluation;
		
		public FillColumnEvaluator(byte evaluation)
		{
			this.evaluation = evaluation;
		}

		@Override
		public FillColumn visitColumn(Column column)
		{
			try
			{
				boolean toPrint = toPrintColumn(column, evaluation);
				if (toPrint)
				{
					JRPropertiesMap properties = evaluateProperties(column, evaluation);
					return new FillColumn(column, columnWeight, properties); 
				}
				return null;
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public FillColumn visitColumnGroup(ColumnGroup columnGroup)
		{
			try
			{
				boolean toPrint = toPrintColumn(columnGroup, evaluation);
				FillColumn fillColumn;
				if (toPrint)
				{
					List<BaseColumn> columns = columnGroup.getColumns();
					List<FillColumn> subColumns = new ArrayList<>(columns.size());
					int printWidth = 0;
					int printWeight = 0;
					for (BaseColumn column : columns)
					{
						FillColumn fillSubColumn = column.visitColumn(this);
						if (fillSubColumn != null)
						{
							printWidth += fillSubColumn.getWidth();
							printWeight += fillSubColumn.getWeight();
							subColumns.add(fillSubColumn);
						}
					}
					
					if (subColumns.isEmpty())
					{
						// no sub columns prints
						// the column group won't print either
						fillColumn = null;
					}
					else
					{
						JRPropertiesMap properties = evaluateProperties(columnGroup, evaluation);
						fillColumn = new FillColumn(columnGroup, printWidth, subColumns, printWeight, properties);
					}
				}
				else
				{
					fillColumn = null;
				}
				return fillColumn;
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}
	
	protected void evaluateColumns(byte evaluation)
	{
		FillColumnEvaluator columnEvaluator = new FillColumnEvaluator(evaluation);
		List<BaseColumn> columns = table.getColumns();
		fillColumns = new ArrayList<>(columns.size());
		fillWidth = 0;
		fillWeight = 0;
		for (BaseColumn column : columns)
		{
			FillColumn fillColumn = column.visitColumn(columnEvaluator);
			if (fillColumn != null)
			{
				fillColumns.add(fillColumn);
				fillWidth += fillColumn.getWidth();
				fillWeight += fillColumn.getWeight();
			}
		}
	}
	
	private static void resizeWeightedColumns(List<FillColumn> fillColumns, int deltaWidth, int totalWeight)
	{
		if (
			fillColumns != null 
			&& !fillColumns.isEmpty()
			&& totalWeight > 0
			&& deltaWidth > 0
			)
		{
			float floatWidthSum = 0;
			int intWidthSum = 0;
			
			for (FillColumn fillColumn : fillColumns)
			{
				int colWidthBeforeWeigthResize = fillColumn.getWidth();
				
				if (fillColumn.getWeight() > 0)
				{
					float floatWidth = (float)deltaWidth * fillColumn.getWeight() / totalWeight;
					floatWidthSum += floatWidth;
					int intWidth = Math.round(floatWidthSum) - intWidthSum;
					intWidthSum += intWidth;
					fillColumn.setWidth(
						fillColumn.getWidth()
						+ intWidth
						);
				}

				resizeWeightedColumns(fillColumn.getSubcolumns(), fillColumn.getWidth() - colWidthBeforeWeigthResize, fillColumn.getSubColsTotalWeight());
			}
		}
	}

	@Override
	public ComponentFillSubreportFactory getFillSubreportFactory()
	{
		return fillSubreportFactories.get(fillColumns);
	}
	
	@Override
	public void setFillSubreportFactory(ComponentFillSubreportFactory subreportFactory)
	{
		fillSubreportFactories.put(fillColumns, subreportFactory);
	}
	
	@Override
	public boolean isEmpty()
	{
		return fillColumns.isEmpty();
	}

	@Override
	public JasperReport getJasperReport(BuiltinExpressionEvaluatorFactory builtinEvaluatorFactory) throws JRException
	{
		JasperReport parentReport = fillContext.getFiller().getJasperReport();
		JasperReport containingReport = containingReport(parentReport);
		JRDataset reportSubdataset = JRReportUtils.findSubdataset(table.getDatasetRun(), (JRReport)containingReport);
		
//		BuiltinExpressionEvaluatorFactory builtinEvaluatorFactory = new BuiltinExpressionEvaluatorFactory();
		
		String tableReportName = JRAbstractCompiler.getUnitName(containingReport, reportSubdataset);
		
		// clone the table subdataset in order to have a different instance for other
		// elements that might be using it.
		// we're cloning the subdataset via an object factory in order to preserve
		// the relationship between objects (e.g. variables and groups) in the cloned
		// dataset
		JRDataset tableSubdataset = DatasetCloneObjectFactory.cloneDataset(reportSubdataset);
		TableReportDataset reportDataset = new TableReportDataset(tableSubdataset, tableReportName);

		TableReport tableReport = new TableReport(fillContext, table, reportDataset, fillColumns, builtinEvaluatorFactory);
		
		if (log.isDebugEnabled())
		{
			String tableReportXml = new JRXmlWriter(fillContext.getFiller().getJasperReportsContext()).write(tableReport, "UTF-8");
			log.debug("Generated table report:\n" + tableReportXml);
		}
		
		JRReportCompileData tableReportCompileData = createTableReportCompileData(
				containingReport, reportSubdataset);
		
		return 
			new TableJasperReport(parentReport, tableReport, 
				tableReportCompileData, 
				new TableReportBaseObjectFactory(reportDataset),
				"");// no suffix as already included in the report name
	}

	protected JasperReport containingReport(JasperReport parentReport)
	{
		JasperReport containingReport = parentReport;
		while (containingReport instanceof TableJasperReport)
		{
			containingReport = ((TableJasperReport) containingReport).getParentReport();
		}
		return containingReport;
	}
	
	protected JRReportCompileData createTableReportCompileData(
			JasperReport parentReport, JRDataset reportSubdataset)
			throws JRException
	{
		Serializable reportCompileDataObj = parentReport.getCompileData();
		if (!(reportCompileDataObj instanceof JRReportCompileData))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNSUPPORTED_REPORT_DATA_TYPE,  
					new Object[]{reportCompileDataObj.getClass().getName()} 
					);
		}
		
		JRReportCompileData reportCompileData = (JRReportCompileData) reportCompileDataObj;
		Serializable datasetCompileData = reportCompileData.getDatasetCompileData(
				reportSubdataset);
		
		TableReportCompileData tableReportCompileData = new TableReportCompileData(
				parentReport);
		tableReportCompileData.setMainDatasetCompileData(datasetCompileData);
		
		JRDataset[] datasets = parentReport.getDatasets();
		if (datasets != null)
		{
			for (JRDataset dataset : datasets)
			{
				Serializable compileData = reportCompileData.getDatasetCompileData(dataset);
				tableReportCompileData.setDatasetCompileData(dataset, compileData);
			}
		}
		tableReportCompileData.copyCrosstabCompileData(reportCompileData);
		return tableReportCompileData;
	}
	
	@Override
	public JRPrintElement fill()
	{
		JRPrintElement printFrame = super.fill();
		
		if (getBaseReport().isInteractiveTable()) {
			printFrame.getPropertiesMap().setProperty(HeaderToolbarElement.PROPERTY_TABLE_UUID, fillContext.getComponentElement().getUUID().toString());
		}

		if (getBaseReport().isAccessibleTable())
		{
			printFrame.getPropertiesMap().setProperty(JRPdfExporterTagHelper.PROPERTY_TAG_TABLE, JRPdfExporterTagHelper.TAG_FULL);
			printFrame.getPropertiesMap().setProperty(AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG, AccessibilityTagEnum.TABLE.getName());
		}
		
		fillContext.getFiller().getPropertiesUtil().transferProperties(
			fillContext.getComponentElement(), 
			printFrame, JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX
			);

		return printFrame;
	}
}
