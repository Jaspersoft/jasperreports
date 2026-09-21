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

import net.sf.jasperreports.crosstabs.CrosstabColumnCell;
import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.JRCrosstabBucket;
import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabDataset;
import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.JRCrosstabParameter;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.base.BaseCrosstabColumnCell;
import net.sf.jasperreports.crosstabs.base.JRBaseCellContents;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstab;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabBucket;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabCell;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabDataset;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabMeasure;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabParameter;
import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabRowGroup;
import net.sf.jasperreports.engine.DatasetPropertyExpression;
import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRGenericElementParameter;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRQueryChunk;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleContainer;
import net.sf.jasperreports.engine.JRStyleSetter;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.analytics.dataset.BaseDataAxis;
import net.sf.jasperreports.engine.analytics.dataset.BaseDataAxisLevel;
import net.sf.jasperreports.engine.analytics.dataset.BaseDataLevelBucket;
import net.sf.jasperreports.engine.analytics.dataset.BaseDataLevelBucketProperty;
import net.sf.jasperreports.engine.analytics.dataset.BaseDataMeasure;
import net.sf.jasperreports.engine.analytics.dataset.BaseMultiAxisData;
import net.sf.jasperreports.engine.analytics.dataset.BaseMultiAxisDataset;
import net.sf.jasperreports.engine.analytics.dataset.DataAxis;
import net.sf.jasperreports.engine.analytics.dataset.DataAxisLevel;
import net.sf.jasperreports.engine.analytics.dataset.DataLevelBucket;
import net.sf.jasperreports.engine.analytics.dataset.DataLevelBucketProperty;
import net.sf.jasperreports.engine.analytics.dataset.DataMeasure;
import net.sf.jasperreports.engine.analytics.dataset.MultiAxisData;
import net.sf.jasperreports.engine.analytics.dataset.MultiAxisDataset;


/**
 * Factory of objects used in compiled reports.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseObjectFactory extends JRAbstractObjectFactory
{
	private static final String EXCEPTION_MESSAGE_KEY_CROSSTAB_ID_NOT_FOUND = "engine.object.factory.crosstab.id.not.found";
	private static final String EXCEPTION_MESSAGE_KEY_EXPRESSION_ID_NOT_FOUND = "engine.object.factory.expression.id.not.found";

	/**
	 *
	 */
	private JRDefaultStyleProvider defaultStyleProvider;

	/**
	 * Expression collector used to retrieve generated expression IDs.
	 */
	private JRExpressionCollector expressionCollector;
	

	/**
	 *
	 */
	protected JRBaseObjectFactory(JRDefaultStyleProvider defaultStyleProvider)
	{
		this.defaultStyleProvider = defaultStyleProvider;
	}


	/**
	 * Constructs a base object factory.
	 *
	 * @param defaultStyleProvider the default style provider
	 * @param expressionCollector the expression collector used as expression ID provider
	 */
	protected JRBaseObjectFactory(JRDefaultStyleProvider defaultStyleProvider, JRExpressionCollector expressionCollector)
	{
		this.defaultStyleProvider = defaultStyleProvider;
		this.expressionCollector = expressionCollector;
	}

	protected JRBaseObjectFactory(JRExpressionCollector expressionCollector)
	{
		this.expressionCollector = expressionCollector;
	}
	
	public void setDefaultStyleProvider(JRDefaultStyleProvider defaultStyleProvider)
	{
		this.defaultStyleProvider = defaultStyleProvider;
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return defaultStyleProvider;
	}


	@Override
	public JRStyle getStyle(JRStyle style)
	{
		JRBaseStyle baseStyle = null;

		if (style != null)
		{
			baseStyle = (JRBaseStyle)get(style);
			if (baseStyle == null)
			{
				baseStyle = new JRBaseStyle(style, this);
				put(style, baseStyle);
			}
		}

		return baseStyle;
	}


	/**
	 * This method preserves both specified styles and external style name references.
	 *
	 * @see JRAbstractObjectFactory#setStyle(JRStyleSetter, JRStyleContainer)
	 */
	@Override
	public void setStyle(JRStyleSetter setter, JRStyleContainer styleContainer)
	{
		JRStyle style = styleContainer.getStyle();
		String nameReference = styleContainer.getStyleNameReference();
		if (style != null)
		{
			JRStyle newStyle = getStyle(style);
			setter.setStyle(newStyle);
		}
		else if (nameReference != null)
		{
			handleStyleNameReference(setter, nameReference);
		}
	}

	protected void handleStyleNameReference(JRStyleSetter setter, String nameReference)
	{
		setter.setStyleNameReference(nameReference);
	}


	/**
	 *
	 */
	protected JRBaseScriptlet getScriptlet(JRScriptlet scriptlet)
	{
		JRBaseScriptlet baseScriptlet = null;

		if (scriptlet != null)
		{
			baseScriptlet = (JRBaseScriptlet)get(scriptlet);
			if (baseScriptlet == null)
			{
				baseScriptlet = new JRBaseScriptlet(scriptlet, this);
			}
		}

		return baseScriptlet;
	}


	/**
	 *
	 */
	protected JRBaseParameter getParameter(JRParameter parameter)
	{
		JRBaseParameter baseParameter = null;

		if (parameter != null)
		{
			baseParameter = (JRBaseParameter)get(parameter);
			if (baseParameter == null)
			{
				baseParameter = new JRBaseParameter(parameter, this);
			}
		}

		return baseParameter;
	}


	/**
	 *
	 */
	protected JRBaseQuery getQuery(JRQuery query)
	{
		JRBaseQuery baseQuery = null;

		if (query != null)
		{
			baseQuery = (JRBaseQuery)get(query);
			if (baseQuery == null)
			{
				baseQuery = new JRBaseQuery(query, this);
			}
		}

		return baseQuery;
	}


	/**
	 *
	 */
	protected JRBaseQueryChunk getQueryChunk(JRQueryChunk queryChunk)
	{
		JRBaseQueryChunk baseQueryChunk = null;

		if (queryChunk != null)
		{
			baseQueryChunk = (JRBaseQueryChunk)get(queryChunk);
			if (baseQueryChunk == null)
			{
				baseQueryChunk = new JRBaseQueryChunk(queryChunk, this);
			}
		}

		return baseQueryChunk;
	}


	/**
	 *
	 */
	protected JRBaseField getField(JRField field)
	{
		JRBaseField baseField = null;

		if (field != null)
		{
			baseField = (JRBaseField)get(field);
			if (baseField == null)
			{
				baseField = new JRBaseField(field, this);
			}
		}

		return baseField;
	}


	/**
	 *
	 */
	protected JRBaseSortField getSortField(JRSortField sortField)
	{
		JRBaseSortField baseSortField = null;

		if (sortField != null)
		{
			baseSortField = (JRBaseSortField)get(sortField);
			if (baseSortField == null)
			{
				baseSortField = new JRBaseSortField(sortField, this);
			}
		}

		return baseSortField;
	}


	/**
	 *
	 */
	public JRBaseVariable getVariable(JRVariable variable)
	{
		JRBaseVariable baseVariable = null;

		if (variable != null)
		{
			baseVariable = (JRBaseVariable)get(variable);
			if (baseVariable == null)
			{
				baseVariable = new JRBaseVariable(variable, this);
			}
		}

		return baseVariable;
	}


	@Override
	public JRExpression getExpression(JRExpression expression, boolean assignNotUsedId)
	{
		JRBaseExpression baseExpression = null;

		if (expression != null)
		{
			baseExpression = (JRBaseExpression)get(expression);
			if (baseExpression == null)
			{
				Integer expressionId = getCollectedExpressionId(expression, assignNotUsedId);
				baseExpression = new JRBaseExpression(expression, this, expressionId);
			}
		}

		return baseExpression;
	}


	private Integer getCollectedExpressionId(JRExpression expression, boolean assignNotUsedId)
	{
		Integer expressionId = null;
		if (expressionCollector != null)
		{
			expressionId = expressionCollector.getExpressionId(expression);
			if (expressionId == null)
			{
				if (assignNotUsedId)
				{
					expressionId = JRExpression.NOT_USED_ID;
				}
				else
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_EXPRESSION_ID_NOT_FOUND,
							new Object[]{expression.getText()});
				}
			}
		}
		return expressionId;
	}


	/**
	 *
	 */
	protected JRBaseExpressionChunk getExpressionChunk(JRExpressionChunk expressionChunk)
	{
		JRBaseExpressionChunk baseExpressionChunk = null;

		if (expressionChunk != null)
		{
			baseExpressionChunk = (JRBaseExpressionChunk)get(expressionChunk);
			if (baseExpressionChunk == null)
			{
				baseExpressionChunk = new JRBaseExpressionChunk(expressionChunk, this);
			}
		}

		return baseExpressionChunk;
	}


	/**
	 *
	 */
	public JRBaseGroup getGroup(JRGroup group)
	{
		JRBaseGroup baseGroup = null;

		if (group != null)
		{
			baseGroup = (JRBaseGroup)get(group);
			if (baseGroup == null)
			{
				baseGroup = new JRBaseGroup(group, this);
			}
		}

		return baseGroup;
	}


	/**
	 *
	 */
	protected JRBaseSection getSection(JRSection section)
	{
		JRBaseSection baseSection = null;

		if (section != null)
		{
			baseSection = (JRBaseSection)get(section);
			if (baseSection == null)
			{
				baseSection = new JRBaseSection(section, this);
			}
		}

		return baseSection;
	}


	/**
	 *
	 */
	protected JRBaseBand getBand(JRBand band)
	{
		JRBaseBand baseBand = null;

		if (band != null)
		{
			baseBand = (JRBaseBand)get(band);
			if (baseBand == null)
			{
				baseBand = new JRBaseBand(band, this);
			}
		}

		return baseBand;
	}


	/**
	 *
	 */
	protected JRBasePart getPart(JRPart part)
	{
		JRBasePart basePart = null;

		if (part != null)
		{
			basePart = (JRBasePart)get(part);
			if (basePart == null)
			{
				basePart = new JRBasePart(part, this);
			}
		}

		return basePart;
	}


	@Override
	public void visitElementGroup(JRElementGroup elementGroup)
	{
		JRElementGroup baseElementGroup = null;

		if (elementGroup != null)
		{
			baseElementGroup = (JRElementGroup)get(elementGroup);
			if (baseElementGroup == null)
			{
				baseElementGroup = new JRBaseElementGroup(elementGroup, this);
			}
		}

		setVisitResult(baseElementGroup);
	}


	@Override
	public void visitBreak(JRBreak breakElement)
	{
		JRBaseBreak baseBreak = null;

		if (breakElement != null)
		{
			baseBreak = (JRBaseBreak)get(breakElement);
			if (baseBreak == null)
			{
				baseBreak = new JRBaseBreak(breakElement, this);
			}
		}

		setVisitResult(baseBreak);
	}


	@Override
	public void visitLine(JRLine line)
	{
		JRBaseLine baseLine = null;

		if (line != null)
		{
			baseLine = (JRBaseLine)get(line);
			if (baseLine == null)
			{
				baseLine = new JRBaseLine(line, this);
			}
		}

		setVisitResult(baseLine);
	}


	@Override
	public void visitRectangle(JRRectangle rectangle)
	{
		JRBaseRectangle baseRectangle = null;

		if (rectangle != null)
		{
			baseRectangle = (JRBaseRectangle)get(rectangle);
			if (baseRectangle == null)
			{
				baseRectangle = new JRBaseRectangle(rectangle, this);
			}
		}

		setVisitResult(baseRectangle);
	}


	@Override
	public void visitEllipse(JREllipse ellipse)
	{
		JRBaseEllipse baseEllipse = null;

		if (ellipse != null)
		{
			baseEllipse = (JRBaseEllipse)get(ellipse);
			if (baseEllipse == null)
			{
				baseEllipse = new JRBaseEllipse(ellipse, this);
			}
		}

		setVisitResult(baseEllipse);
	}


	@Override
	public void visitImage(JRImage image)
	{
		JRBaseImage baseImage = null;

		if (image != null)
		{
			baseImage = (JRBaseImage)get(image);
			if (baseImage == null)
			{
				baseImage = new JRBaseImage(image, this);
			}
		}

		setVisitResult(baseImage);
	}


	@Override
	public void visitStaticText(JRStaticText staticText)
	{
		JRBaseStaticText baseStaticText = null;

		if (staticText != null)
		{
			baseStaticText = (JRBaseStaticText)get(staticText);
			if (baseStaticText == null)
			{
				baseStaticText = new JRBaseStaticText(staticText, this);
			}
		}

		setVisitResult(baseStaticText);
	}


	@Override
	public void visitTextField(JRTextField textField)
	{
		JRBaseTextField baseTextField = null;

		if (textField != null)
		{
			baseTextField = (JRBaseTextField)get(textField);
			if (baseTextField == null)
			{
				baseTextField = new JRBaseTextField(textField, this);
			}
		}

		setVisitResult(baseTextField);
	}


	@Override
	public void visitSubreport(JRSubreport subreport)
	{
		JRBaseSubreport baseSubreport = null;

		if (subreport != null)
		{
			baseSubreport = (JRBaseSubreport)get(subreport);
			if (baseSubreport == null)
			{
				baseSubreport = new JRBaseSubreport(subreport, this);
			}
		}

		setVisitResult(baseSubreport);
	}


	/**
	 *
	 */
	public JRBaseSubreportParameter getSubreportParameter(JRSubreportParameter subreportParameter)
	{
		JRBaseSubreportParameter baseSubreportParameter = null;

		if (subreportParameter != null)
		{
			baseSubreportParameter = (JRBaseSubreportParameter)get(subreportParameter);
			if (baseSubreportParameter == null)
			{
				baseSubreportParameter = new JRBaseSubreportParameter(subreportParameter, this);
				put(subreportParameter, baseSubreportParameter);
			}
		}

		return baseSubreportParameter;
	}


	protected JRBaseDatasetParameter getDatasetParameter(JRDatasetParameter datasetParameter)
	{
		JRBaseDatasetParameter baseSubreportParameter = null;

		if (datasetParameter != null)
		{
			baseSubreportParameter = (JRBaseDatasetParameter) get(datasetParameter);
			if (baseSubreportParameter == null)
			{
				baseSubreportParameter = new JRBaseDatasetParameter(datasetParameter, this);
				put(datasetParameter, baseSubreportParameter);
			}
		}

		return baseSubreportParameter;
	}


	/**
	 *
	 */
	public JRBaseSubreportReturnValue getSubreportReturnValue(JRSubreportReturnValue returnValue)
	{
		JRBaseSubreportReturnValue baseSubreportReturnValue = null;

		if (returnValue != null)
		{
			baseSubreportReturnValue = (JRBaseSubreportReturnValue)get(returnValue);
			if (baseSubreportReturnValue == null)
			{
				baseSubreportReturnValue = new JRBaseSubreportReturnValue(returnValue, this);
				put(returnValue, baseSubreportReturnValue);
			}
		}

		return baseSubreportReturnValue;
	}


	protected BaseReturnValue getReturnValue(ReturnValue returnValue)
	{
		BaseReturnValue baseReturnValue = null;

		if (returnValue != null)
		{
			baseReturnValue = (BaseReturnValue) get(returnValue);
			if (baseReturnValue == null)
			{
				baseReturnValue = new BaseReturnValue(returnValue, this);
				put(returnValue, baseReturnValue);
			}
		}

		return baseReturnValue;
	}


	protected BaseExpressionReturnValue getReturnValue(ExpressionReturnValue returnValue)
	{
		BaseExpressionReturnValue baseReturnValue = null;

		if (returnValue != null)
		{
			baseReturnValue = (BaseExpressionReturnValue) get(returnValue);
			if (baseReturnValue == null)
			{
				baseReturnValue = new BaseExpressionReturnValue(returnValue, this);
				put(returnValue, baseReturnValue);
			}
		}

		return baseReturnValue;
	}


	@Override
	public JRConditionalStyle getConditionalStyle(JRConditionalStyle conditionalStyle, JRStyle style)
	{
		JRBaseConditionalStyle baseConditionalStyle = null;
		if (conditionalStyle != null)
		{
			baseConditionalStyle = (JRBaseConditionalStyle) get(conditionalStyle);
			if (baseConditionalStyle == null) {
				baseConditionalStyle = new JRBaseConditionalStyle(conditionalStyle, style, this);
				put(conditionalStyle, baseConditionalStyle);
			}
		}
		return baseConditionalStyle;
	}


	public JRBaseCrosstabDataset getCrosstabDataset(JRCrosstabDataset crosstabDataset)
	{
		JRBaseCrosstabDataset baseCrosstabDataset = null;

		if (crosstabDataset != null)
		{
			baseCrosstabDataset = (JRBaseCrosstabDataset) get(crosstabDataset);
			if (baseCrosstabDataset == null)
			{
				baseCrosstabDataset = new JRBaseCrosstabDataset(crosstabDataset, this);
			}
		}

		return baseCrosstabDataset;
	}


	public JRBaseCrosstabRowGroup getCrosstabRowGroup(JRCrosstabRowGroup group)
	{
		JRBaseCrosstabRowGroup baseCrosstabRowGroup = null;

		if (group != null)
		{
			baseCrosstabRowGroup = (JRBaseCrosstabRowGroup) get(group);
			if (baseCrosstabRowGroup == null)
			{
				baseCrosstabRowGroup = new JRBaseCrosstabRowGroup(group, this);
			}
		}

		return baseCrosstabRowGroup;
	}


	public JRBaseCrosstabColumnGroup getCrosstabColumnGroup(JRCrosstabColumnGroup group)
	{
		JRBaseCrosstabColumnGroup baseCrosstabDataset = null;

		if (group != null)
		{
			baseCrosstabDataset = (JRBaseCrosstabColumnGroup) get(group);
			if (baseCrosstabDataset == null)
			{
				baseCrosstabDataset = new JRBaseCrosstabColumnGroup(group, this);
			}
		}

		return baseCrosstabDataset;
	}


	public JRBaseCrosstabBucket getCrosstabBucket(JRCrosstabBucket bucket)
	{
		JRBaseCrosstabBucket baseCrosstabBucket = null;

		if (bucket != null)
		{
			baseCrosstabBucket = (JRBaseCrosstabBucket) get(bucket);
			if (baseCrosstabBucket == null)
			{
				baseCrosstabBucket = new JRBaseCrosstabBucket(bucket, this);
			}
		}

		return baseCrosstabBucket;
	}


	public JRBaseCrosstabMeasure getCrosstabMeasure(JRCrosstabMeasure measure)
	{
		JRBaseCrosstabMeasure baseCrosstabMeasure = null;

		if (measure != null)
		{
			baseCrosstabMeasure = (JRBaseCrosstabMeasure) get(measure);
			if (baseCrosstabMeasure == null)
			{
				baseCrosstabMeasure = new JRBaseCrosstabMeasure(measure, this);
			}
		}

		return baseCrosstabMeasure;
	}


	@Override
	public void visitCrosstab(JRCrosstab crosstab)
	{
		JRBaseCrosstab baseCrosstab = null;

		if (crosstab != null)
		{
			baseCrosstab = (JRBaseCrosstab) get(crosstab);
			if (baseCrosstab == null)
			{
				int crosstabId = resolveCrosstabId(crosstab);
				baseCrosstab = new JRBaseCrosstab(crosstab, this, crosstabId);
			}
		}

		setVisitResult(baseCrosstab);
	}


	protected int resolveCrosstabId(JRCrosstab crosstab)
	{
		Integer id = expressionCollector.getCrosstabId(crosstab);
		if (id == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CROSSTAB_ID_NOT_FOUND,
					(Object[])null);
		}
		return id;
	}


	public JRBaseDataset getDataset(JRDataset dataset)
	{
		JRBaseDataset baseDataset = null;

		if (dataset != null)
		{
			baseDataset = (JRBaseDataset)get(dataset);
			if (baseDataset == null)
			{
				baseDataset = new JRBaseDataset(dataset, this);
			}
		}

		return baseDataset;
	}


	public JRBaseDatasetRun getDatasetRun(JRDatasetRun datasetRun)
	{
		JRBaseDatasetRun baseDatasetRun = null;

		if (datasetRun != null)
		{
			baseDatasetRun = (JRBaseDatasetRun)get(datasetRun);
			if (baseDatasetRun == null)
			{
				baseDatasetRun = new JRBaseDatasetRun(datasetRun, this);
			}
		}

		return baseDatasetRun;
	}


	public JRBaseCellContents getCell(JRCellContents cell)
	{
		JRBaseCellContents baseCell = null;

		if (cell != null)
		{
			baseCell = (JRBaseCellContents)get(cell);
			if (baseCell == null)
			{
				baseCell = new JRBaseCellContents(cell, this);
			}
		}

		return baseCell;
	}


	public JRCrosstabCell getCrosstabCell(JRCrosstabCell cell)
	{
		JRBaseCrosstabCell baseCell = null;

		if (cell != null)
		{
			baseCell = (JRBaseCrosstabCell)get(cell);
			if (baseCell == null)
			{
				baseCell = new JRBaseCrosstabCell(cell, this);
			}
		}

		return baseCell;
	}


	public JRBaseCrosstabParameter getCrosstabParameter(JRCrosstabParameter parameter)
	{
		JRBaseCrosstabParameter baseParameter = null;

		if (parameter != null)
		{
			baseParameter = (JRBaseCrosstabParameter) get(parameter);
			if (baseParameter == null)
			{
				baseParameter = new JRBaseCrosstabParameter(parameter, this);
			}
		}

		return baseParameter;
	}


	@Override
	public void visitFrame(JRFrame frame)
	{
		JRBaseFrame baseFrame = null;

		if (frame != null)
		{
			baseFrame = (JRBaseFrame) get(frame);
			if (baseFrame == null)
			{
				baseFrame = new JRBaseFrame(frame, this);
			}
		}

		setVisitResult(baseFrame);
	}


	public JRHyperlinkParameter getHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		JRHyperlinkParameter baseParameter = null;

		if (parameter != null)
		{
			baseParameter = (JRHyperlinkParameter) get(parameter);
			if (baseParameter == null)
			{
				baseParameter = new JRBaseHyperlinkParameter(parameter, this);
			}
		}

		return baseParameter;
	}


	public JRHyperlink getHyperlink(JRHyperlink hyperlink)
	{
		JRHyperlink link = null;
		if (hyperlink != null)
		{
			link = (JRHyperlink) get(hyperlink);
			if (link == null)
			{
				link = new JRBaseHyperlink(hyperlink, this);
			}
		}
		return link;
	}


	public JRReportTemplate getReportTemplate(JRReportTemplate template)
	{
		JRReportTemplate baseTemplate = null;
		if (template != null)
		{
			baseTemplate = (JRReportTemplate) get(template);
			if (baseTemplate == null)
			{
				baseTemplate = new JRBaseReportTemplate(template, this);
			}
		}
		return baseTemplate;
	}

	public JRPropertyExpression[] getPropertyExpressions(JRPropertyExpression[] props)
	{
		JRPropertyExpression[] propertyExpressions = null;
		if (props != null && props.length > 0)
		{
			propertyExpressions = new JRPropertyExpression[props.length];
			for (int i = 0; i < props.length; i++)
			{
				propertyExpressions[i] = getPropertyExpression(props[i]);
			}
		}
		return propertyExpressions;
	}

	public JRPropertyExpression getPropertyExpression(JRPropertyExpression propertyExpression)
	{
		JRPropertyExpression baseProp = null;
		if (propertyExpression != null)
		{
			baseProp = (JRPropertyExpression) get(propertyExpression);
			if (baseProp == null)
			{
				baseProp = new JRBasePropertyExpression(propertyExpression, this);
			}
		}
		return baseProp;
	}

	public DatasetPropertyExpression[] getPropertyExpressions(DatasetPropertyExpression[] props)
	{
		DatasetPropertyExpression[] propertyExpressions = null;
		if (props != null && props.length > 0)
		{
			propertyExpressions = new DatasetPropertyExpression[props.length];
			for (int i = 0; i < props.length; i++)
			{
				propertyExpressions[i] = getPropertyExpression(props[i]);
			}
		}
		return propertyExpressions;
	}

	public DatasetPropertyExpression getPropertyExpression(DatasetPropertyExpression propertyExpression)
	{
		DatasetPropertyExpression baseProp = null;
		if (propertyExpression != null)
		{
			baseProp = (DatasetPropertyExpression) get(propertyExpression);
			if (baseProp == null)
			{
				baseProp = new BaseDatasetPropertyExpression(propertyExpression, this);
			}
		}
		return baseProp;
	}


	@Override
	public void visitComponentElement(JRComponentElement componentElement)
	{
		JRBaseComponentElement base = null;

		if (componentElement != null)
		{
			base = (JRBaseComponentElement) get(componentElement);
			if (base == null)
			{
				base = new JRBaseComponentElement(componentElement, this);
			}
		}

		setVisitResult(base);
	}


	public JRGenericElementParameter getGenericElementParameter(
			JRGenericElementParameter elementParameter)
	{
		JRGenericElementParameter baseParameter = null;
		if (elementParameter != null)
		{
			baseParameter = (JRGenericElementParameter) get(elementParameter);
			if (baseParameter == null)
			{
				baseParameter = new JRBaseGenericElementParameter(
						elementParameter, this);
			}
		}
		return baseParameter;
	}


	@Override
	public void visitGenericElement(JRGenericElement element)
	{
		JRBaseGenericElement base = null;
		if (element != null)
		{
			base = (JRBaseGenericElement) get(element);
			if (base == null)
			{
				base = new JRBaseGenericElement(element, this);
			}
		}
		setVisitResult(base);
	}


	public MultiAxisData getMultiAxisData(MultiAxisData data)
	{
		MultiAxisData baseData = null;
		if (data != null)
		{
			baseData = (MultiAxisData) get(data);
			if (baseData == null)
			{
				baseData = new BaseMultiAxisData(data, this);
			}
		}
		return baseData;
	}


	public MultiAxisDataset getMultiAxisDataset(MultiAxisDataset dataset)
	{
		MultiAxisDataset baseDataset = null;
		if (dataset != null)
		{
			baseDataset = (MultiAxisDataset) get(dataset);
			if (baseDataset == null)
			{
				baseDataset = new BaseMultiAxisDataset(dataset, this);
			}
		}
		return baseDataset;
	}

	
	public JRElementDataset getElementDataset(JRElementDataset dataset)
	{
		JRElementDataset baseDataset = null;
		if (dataset != null)
		{
			baseDataset = (JRElementDataset) get(dataset);
			if (baseDataset == null)
			{
				baseDataset = new JRBaseElementDataset(dataset, this);
			}
		}
		return baseDataset;
	}


	public DataAxis getDataAxis(DataAxis axis)
	{
		DataAxis baseAxis = null;
		if (axis != null)
		{
			baseAxis = (DataAxis) get(axis);
			if (baseAxis == null)
			{
				baseAxis = new BaseDataAxis(axis, this);
			}
		}
		return baseAxis;
	}


	public DataAxisLevel getDataAxisLevel(DataAxisLevel level)
	{
		DataAxisLevel baseLevel = null;
		if (level != null)
		{
			baseLevel = (DataAxisLevel) get(level);
			if (baseLevel == null)
			{
				baseLevel = new BaseDataAxisLevel(level, this);
			}
		}
		return baseLevel;
	}


	public DataLevelBucket getDataLevelBucket(DataLevelBucket bucket)
	{
		DataLevelBucket baseBucket = null;
		if (bucket != null)
		{
			baseBucket = (DataLevelBucket) get(bucket);
			if (baseBucket == null)
			{
				baseBucket = new BaseDataLevelBucket(bucket, this);
			}
		}
		return baseBucket;
	}


	public DataMeasure getDataMeasure(DataMeasure measure)
	{
		DataMeasure baseMeasure = null;
		if (measure != null)
		{
			baseMeasure = (DataMeasure) get(measure);
			if (baseMeasure == null)
			{
				baseMeasure = new BaseDataMeasure(measure, this);
			}
		}
		return baseMeasure;
	}


	public DataLevelBucketProperty getDataLevelBucketProperty(DataLevelBucketProperty bucketProperty)
	{
		DataLevelBucketProperty baseBucketProperty = null;
		if (bucketProperty != null)
		{
			baseBucketProperty = (DataLevelBucketProperty) get(bucketProperty);
			if (baseBucketProperty == null)
			{
				baseBucketProperty = new BaseDataLevelBucketProperty(bucketProperty, this);
			}
		}
		return baseBucketProperty;
	}


	public CrosstabColumnCell getCrosstabColumnCell(CrosstabColumnCell cell)
	{
		BaseCrosstabColumnCell baseCell = null;

		if (cell != null)
		{
			baseCell = (BaseCrosstabColumnCell) get(cell);
			if (baseCell == null)
			{
				baseCell = new BaseCrosstabColumnCell(cell, this);
			}
		}

		return baseCell;
	}
}
