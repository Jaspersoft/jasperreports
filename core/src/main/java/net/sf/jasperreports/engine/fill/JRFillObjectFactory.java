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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.JRCrosstabDataset;
import net.sf.jasperreports.crosstabs.JRCrosstabParameter;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabParameter;
import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleContainer;
import net.sf.jasperreports.engine.JRStyleSetter;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.VariableReturnValue;
import net.sf.jasperreports.engine.analytics.dataset.FillMultiAxisData;
import net.sf.jasperreports.engine.analytics.dataset.MultiAxisData;
import net.sf.jasperreports.engine.base.JRBaseConditionalStyle;
import net.sf.jasperreports.engine.base.JRBaseStyle;


/**
 * A factory used to instantiate fill objects based on compiled report objects.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillObjectFactory extends JRAbstractObjectFactory
{

	private static final Log log = LogFactory.getLog(JRFillObjectFactory.class);

	public static final String EXCEPTION_MESSAGE_KEY_UNRESOLVED_STYLE = "fill.object.factory.unresolved.style";
	public static final String EXCEPTION_MESSAGE_KEY_STYLE_NOT_FOUND = "fill.object.factory.style.not.found";
	
	/**
	 *
	 */
	protected JRBaseFiller filler;
	protected BaseReportFiller reportFiller;
	private JRFillExpressionEvaluator evaluator;

	private JRFillObjectFactory parentFiller;
	
//	private JRFont defaultFont;

	private List<JRFillElementDataset> elementDatasets = new ArrayList<>();
	private Map<String,List<JRFillElementDataset>> elementDatasetMap = new HashMap<>();
	
	private LinkedList<List<JRFillDatasetRun>> trackedDatasetRunsStack = new LinkedList<>();
	
	private Map<String,List<JRStyleSetter>> delayedStyleSettersByName = new HashMap<>();
	
	protected static class StylesList
	{
		private final List<JRStyle> styles = new ArrayList<>();
		private final Map<String,Integer> stylesIdx = new HashMap<>();
		
		public boolean containsStyle(String name)
		{
			return stylesIdx.containsKey(name);
		}
		
		public JRStyle getStyle(String name)
		{
			Integer idx = stylesIdx.get(name);
			return idx == null ? null : styles.get(idx);
		}
		
		public void addStyle(JRStyle style)
		{
			styles.add(style);
			stylesIdx.put(style.getName(), styles.size() - 1);
		}
		
		public void renamed(String oldName, String newName)
		{
			Integer idx = stylesIdx.remove(oldName);
			stylesIdx.put(newName, idx);
		}
	}
	
	private Set<JRStyle> originalStyleList;
	protected StylesList stylesMap = new StylesList();


	/**
	 *
	 */
	protected JRFillObjectFactory(JRBaseFiller filler)
	{
		this(filler, filler.calculator);
	}


	/**
	 *
	 */
	public JRFillObjectFactory(JRBaseFiller filler, JRFillExpressionEvaluator expressionEvaluator)
	{
		this.filler = filler;
		this.reportFiller = filler;
		this.evaluator = expressionEvaluator;
	}

	
	public JRFillObjectFactory(JRFillObjectFactory parent, JRFillExpressionEvaluator expressionEvaluator)
	{
		this.parentFiller = parent;
		this.filler = parent.filler;
		this.reportFiller = parent.reportFiller;
		this.evaluator = expressionEvaluator;
	}

	
	public JRFillObjectFactory(BaseReportFiller reportFiller)
	{
		this.reportFiller = reportFiller;
		this.evaluator = reportFiller.calculator;
	}


	/**
	 * Returns the expression evaluator which is to be used by objects
	 * created by this factory.
	 * 
	 * @return the expression evaluator associated with this factory
	 */
	public JRFillExpressionEvaluator getExpressionEvaluator()
	{
		return evaluator;
	}


	protected JRFillElementDataset[] getElementDatasets(JRDataset dataset)
	{
		JRFillElementDataset[] elementDatasetsArray;
		List<JRFillElementDataset> elementDatasetsList;
		if (dataset.isMainDataset())
		{
			elementDatasetsList = elementDatasets;
		}
		else
		{
			elementDatasetsList = elementDatasetMap.get(dataset.getName());
		}

		if (elementDatasetsList == null || elementDatasetsList.size() == 0)
		{
			elementDatasetsArray = new JRFillElementDataset[0];
		}
		else
		{
			elementDatasetsArray = new JRFillElementDataset[elementDatasetsList.size()];
			elementDatasetsList.toArray(elementDatasetsArray);
		}

		return elementDatasetsArray;
	}


	protected void registerDelayedStyleSetter(JRStyleSetter delayedSetter, String styleName)
	{
		if (parentFiller == null)
		{
			delayedStyleSettersByName.computeIfAbsent(styleName, k -> new ArrayList<>()).add(delayedSetter);
		}
		else
		{
			parentFiller.registerDelayedStyleSetter(delayedSetter, styleName);
		}
	}

	public void registerDelayedStyleSetter(JRStyleSetter delayedSetter, JRStyleContainer styleContainer)
	{
		JRStyle style = styleContainer.getStyle();
		String nameReference = styleContainer.getStyleNameReference();
		if (style != null)
		{
			registerDelayedStyleSetter(delayedSetter, style.getName());
		}
		else if (nameReference != null)
		{
			registerDelayedStyleSetter(delayedSetter, nameReference);
		}
	}
	
	@Override
	public JRBaseStyle getStyle(JRStyle style)
	{
		JRBaseStyle fillStyle = null;

		if (style != null)
		{
			fillStyle = (JRBaseStyle) get(style);
			if (fillStyle == null)
			{
				fillStyle = new JRBaseStyle(style, this);
				
				// deduplicate to previously created identical instances
				fillStyle = filler.fillContext.deduplicate(fillStyle);
				
				put(style, fillStyle);
				
				if (originalStyleList != null && originalStyleList.contains(style))
				{
					renameExistingStyle(style.getName());
					stylesMap.addStyle(style);
				}				
			}
		}

		return fillStyle;
	}

	protected void renameExistingStyle(String name)
	{
		JRStyle originalStyle = stylesMap.getStyle(name);
		if (originalStyle != null)
		{
			//found a previous external style with the same name
			//renaming the previous style
			JRBaseStyle style = (JRBaseStyle) get(originalStyle);
			
			String newName;
			int suf = 1;
			do
			{
				newName = name + suf;
				++suf;
			}
			while (stylesMap.containsStyle(newName));
			
			style.rename(newName);
			stylesMap.renamed(name, newName);
		}
	}


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
			JRStyle originalStyle = stylesMap.getStyle(nameReference);
			if (originalStyle == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNRESOLVED_STYLE,  
						new Object[]{nameReference} 
						);
			}
			
			JRStyle externalStyle = (JRStyle) get(originalStyle);
			setter.setStyle(externalStyle);
		}
	}


	/**
	 *
	 */
	protected JRFillParameter getParameter(JRParameter parameter)
	{
		JRFillParameter fillParameter = null;

		if (parameter != null)
		{
			fillParameter = (JRFillParameter)get(parameter);
			if (fillParameter == null)
			{
				fillParameter = new JRFillParameter(parameter, this);
			}
		}

		return fillParameter;
	}


	/**
	 *
	 */
	protected JRFillField getField(JRField field)
	{
		JRFillField fillField = null;

		if (field != null)
		{
			fillField = (JRFillField)get(field);
			if (fillField == null)
			{
				fillField = new JRFillField(field, this);
			}
		}

		return fillField;
	}


	/**
	 *
	 */
	public JRFillVariable getVariable(JRVariable variable)
	{
		JRFillVariable fillVariable = null;

		if (variable != null)
		{
			fillVariable = (JRFillVariable)get(variable);
			if (fillVariable == null)
			{
				fillVariable = new JRFillVariable(variable, this);
			}
		}

		return fillVariable;
	}


	/**
	 *
	 */
	public JRFillGroup getGroup(JRGroup group)
	{
		JRFillGroup fillGroup = null;

		if (group != null)
		{
			fillGroup = (JRFillGroup)get(group);
			if (fillGroup == null)
			{
				fillGroup = new JRFillGroup(group, this);
			}
		}

		return fillGroup;
	}


	/**
	 *
	 */
	protected JRFillSection getSection(JRSection section)
	{
		JRFillSection fillSection = null;

		if (section == null)
		{
			fillSection = filler.missingFillSection;
		}
		else
		{
			fillSection = (JRFillSection)get(section);
			if (fillSection == null)
			{
				fillSection = new JRFillSection(filler, section, this);
			}
		}

		return fillSection;
	}


	/**
	 *
	 */
	protected JRFillBand getBand(JRBand band)
	{
		JRFillBand fillBand = null;

		if (band == null)
		{
			fillBand = filler.missingFillBand;
		}
		else
		{
			fillBand = (JRFillBand)get(band);
			if (fillBand == null)
			{
				fillBand = new JRFillBand(filler, band, this);
			}
		}

		return fillBand;
	}


	@Override
	public void visitElementGroup(JRElementGroup elementGroup)
	{
		JRFillElementGroup fillElementGroup = null;

		if (elementGroup != null)
		{
			fillElementGroup = (JRFillElementGroup)get(elementGroup);
			if (fillElementGroup == null)
			{
				fillElementGroup = new JRFillElementGroup(elementGroup, this);
			}
		}

		setVisitResult(fillElementGroup);
	}


	@Override
	public void visitBreak(JRBreak breakElement)
	{
		JRFillBreak fillBreak = null;

		if (breakElement != null)
		{
			fillBreak = (JRFillBreak)get(breakElement);
			if (fillBreak == null)
			{
				fillBreak = new JRFillBreak(filler, breakElement, this);
			}
		}

		setVisitResult(fillBreak);
	}


	@Override
	public void visitLine(JRLine line)
	{
		JRFillLine fillLine = null;

		if (line != null)
		{
			fillLine = (JRFillLine)get(line);
			if (fillLine == null)
			{
				fillLine = new JRFillLine(filler, line, this);
			}
		}

		setVisitResult(fillLine);
	}


	@Override
	public void visitRectangle(JRRectangle rectangle)
	{
		JRFillRectangle fillRectangle = null;

		if (rectangle != null)
		{
			fillRectangle = (JRFillRectangle)get(rectangle);
			if (fillRectangle == null)
			{
				fillRectangle = new JRFillRectangle(filler, rectangle, this);
			}
		}

		setVisitResult(fillRectangle);
	}


	@Override
	public void visitEllipse(JREllipse ellipse)
	{
		JRFillEllipse fillEllipse = null;

		if (ellipse != null)
		{
			fillEllipse = (JRFillEllipse)get(ellipse);
			if (fillEllipse == null)
			{
				fillEllipse = new JRFillEllipse(filler, ellipse, this);
			}
		}

		setVisitResult(fillEllipse);
	}


	@Override
	public void visitImage(JRImage image)
	{
		JRFillImage fillImage = null;

		if (image != null)
		{
			fillImage = (JRFillImage)get(image);
			if (fillImage == null)
			{
				fillImage = new JRFillImage(filler, image, this);
			}
		}

		setVisitResult(fillImage);
	}


	@Override
	public void visitStaticText(JRStaticText staticText)
	{
		JRFillStaticText fillStaticText = null;

		if (staticText != null)
		{
			fillStaticText = (JRFillStaticText)get(staticText);
			if (fillStaticText == null)
			{
				fillStaticText = new JRFillStaticText(filler, staticText, this);
			}
		}

		setVisitResult(fillStaticText);
	}


	@Override
	public void visitTextField(JRTextField textField)
	{
		JRFillTextField fillTextField = null;

		if (textField != null)
		{
			fillTextField = (JRFillTextField)get(textField);
			if (fillTextField == null)
			{
				fillTextField = new JRFillTextField(filler, textField, this);
			}
		}

		setVisitResult(fillTextField);
	}


	@Override
	public void visitSubreport(JRSubreport subreport)
	{
		JRFillSubreport fillSubreport = null;

		if (subreport != null)
		{
			fillSubreport = (JRFillSubreport)get(subreport);
			if (fillSubreport == null)
			{
				fillSubreport = new JRFillSubreport(filler, subreport, this);
			}
		}

		setVisitResult(fillSubreport);
	}


	protected JRFillVariableReturnValue getSubreportReturnValue(JRSubreportReturnValue returnValue)
	{
		JRFillVariableReturnValue fillReturnValue = null;

		if (returnValue != null)
		{
			fillReturnValue = (JRFillVariableReturnValue) get(returnValue);
			if (fillReturnValue == null)
			{
				fillReturnValue = new JRFillVariableReturnValue(returnValue, this, reportFiller);
			}
		}

		return fillReturnValue;
	}


	protected JRFillVariableReturnValue getReturnValue(VariableReturnValue returnValue)
	{
		JRFillVariableReturnValue fillReturnValue = null;

		if (returnValue != null)
		{
			fillReturnValue = (JRFillVariableReturnValue) get(returnValue);
			if (fillReturnValue == null)
			{
				fillReturnValue = new JRFillVariableReturnValue(returnValue, this, filler);
			}
		}

		return fillReturnValue;
	}


	protected JRFillExpressionReturnValue getReturnValue(ExpressionReturnValue returnValue)
	{
		JRFillExpressionReturnValue fillReturnValue = null;

		if (returnValue != null)
		{
			fillReturnValue = (JRFillExpressionReturnValue) get(returnValue);
			if (fillReturnValue == null)
			{
				fillReturnValue = new JRFillExpressionReturnValue(returnValue, this, filler);//FIXMERETURN passing the right filler? 
				//why two possible fillers here after all? if single filler, could remove getSubreportReturnValue() above
			}
		}

		return fillReturnValue;
	}


	@Override
	public void visitCrosstab(JRCrosstab crosstabElement)
	{
		JRFillCrosstab fillCrosstab = null;

		if (crosstabElement != null)
		{
			fillCrosstab = (JRFillCrosstab) get(crosstabElement);
			if (fillCrosstab == null)
			{
				fillCrosstab = new JRFillCrosstab(filler, crosstabElement, this);
			}
		}

		setVisitResult(fillCrosstab);
	}


	public JRFillCrosstab.JRFillCrosstabDataset getCrosstabDataset(JRCrosstabDataset dataset, JRFillCrosstab fillCrosstab)
	{
		JRFillCrosstab.JRFillCrosstabDataset fillDataset = null;

		if (dataset != null)
		{
			fillDataset = (JRFillCrosstab.JRFillCrosstabDataset)get(dataset);
			if (fillDataset == null)
			{
				fillDataset = fillCrosstab.new JRFillCrosstabDataset(dataset, this);
				registerElementDataset(fillDataset);
			}
		}

		return fillDataset;
	}


	public JRFillDataset getDataset(JRDataset dataset)
	{
		JRFillDataset fillDataset = null;

		if (dataset != null)
		{
			fillDataset = (JRFillDataset) get(dataset);
			if (fillDataset == null)
			{
				fillDataset = new JRFillDataset(reportFiller, dataset, this);
			}
		}

		return fillDataset;
	}


	/**
	 * Register an element dataset with the report filler.
	 * 
	 * <p>
	 * Registration of element datasets is required in order for the filler
	 * to increment the datasets when iterating through the datasource.
	 * 
	 * @param elementDataset the dataset to register
	 */
	public void registerElementDataset(JRFillElementDataset elementDataset)
	{
		List<JRFillElementDataset> elementDatasetsList;
		JRDatasetRun datasetRun = elementDataset.getDatasetRun();
		if (datasetRun == null)
		{
			elementDatasetsList = elementDatasets;
		}
		else
		{
			String datasetName = datasetRun.getDatasetName();
			elementDatasetsList = getElementDatasetsList(datasetName);
			
			registerDatasetRun((JRFillDatasetRun) datasetRun);
		}
		elementDatasetsList.add(elementDataset);
	}
	
	protected List<JRFillElementDataset> getElementDatasetsList(String datasetName)
	{
		return
			parentFiller == null
				? elementDatasetMap.computeIfAbsent(datasetName, k -> new ArrayList<>())
				: parentFiller.getElementDatasetsList(datasetName);
	}

	public void trackDatasetRuns()
	{
		if (log.isDebugEnabled())
		{
			log.debug("tracking dataset runs");
		}

		ArrayList<JRFillDatasetRun> trackedDatasets = new ArrayList<>(2);
		trackedDatasetRunsStack.push(trackedDatasets);
	}
	
	public void registerDatasetRun(JRFillDatasetRun datasetRun)
	{
		if (!trackedDatasetRunsStack.isEmpty())
		{
			if (log.isDebugEnabled())
			{
				log.debug("added tracked dataset run " + datasetRun);
			}
			
			trackedDatasetRunsStack.getFirst().add(datasetRun);
		}
	}
	
	public List<JRFillDatasetRun> getTrackedDatasetRuns()
	{
		if (log.isDebugEnabled())
		{
			log.debug("poping tracked dataset runs");
		}

		return trackedDatasetRunsStack.pop();
	}

	public JRFillDatasetRun getDatasetRun(JRDatasetRun datasetRun)
	{
		JRFillDatasetRun fillDatasetRun = null;

		if (datasetRun != null)
		{
			fillDatasetRun = (JRFillDatasetRun) get(datasetRun);
			if (fillDatasetRun == null)
			{
				fillDatasetRun = new JRFillDatasetRun(datasetRun, this);
			}
		}

		return fillDatasetRun;
	}


	public JRFillCrosstabParameter getCrosstabParameter(JRCrosstabParameter parameter)
	{
		JRFillCrosstabParameter fillParameter = null;

		if (parameter != null)
		{
			fillParameter = (JRFillCrosstabParameter) get(parameter);
			if (fillParameter == null)
			{
				fillParameter = new JRFillCrosstabParameter(parameter, this);
			}
		}

		return fillParameter;
	}


	@Override
	public void visitFrame(JRFrame frame)
	{
		Object fillFrame = null;
		// This is the only place where an object gets replaced in the factory map by something else,
		// and we can no longer make a precise cast when getting it.
		// The JRFillFrame object is replaced in the map by a JRFillFrameElements object.
		//JRFillFrame fillFrame = null;

		if (frame != null)
		{
			fillFrame = get(frame);
			//fillFrame = (JRFillFrame) get(frame);
			if (fillFrame == null)
			{
				fillFrame = new JRFillFrame(filler, frame, this);
			}
		}

		setVisitResult(fillFrame);
	}

	public BaseReportFiller getReportFiller()
	{
		return reportFiller;
	}

	/**
	 * Returns the current report filler.
	 * 
	 * @return the current report filler
	 */
	public JRBaseFiller getFiller()
	{
		return filler;
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


	@Override
	public JRExpression getExpression(JRExpression expression, boolean assignNotUsedId)
	{
		return expression;
	}


	public JRFillReportTemplate getReportTemplate(JRReportTemplate template)
	{
		JRFillReportTemplate fillTemplate = null;
		if (template != null)
		{
			fillTemplate = (JRFillReportTemplate) get(template);
			if (fillTemplate == null)
			{
				fillTemplate = new JRFillReportTemplate(template, filler, this);
			}
		}
		return fillTemplate;
	}
	
	public List<JRStyle> setStyles(List<JRStyle> styles)
	{
		originalStyleList = new HashSet<>(styles);
		
		//filtering requested styles
		Set<JRStyle> requestedStyles = collectRequestedStyles(styles);
		
		//collect used styles
		Map<JRStyle,Object> usedStylesMap = new LinkedMap<>();
		Map<String,JRStyle> allStylesMap = new HashMap<>();
		for (Iterator<JRStyle> it = styles.iterator(); it.hasNext();)
		{
			JRStyle style = it.next();
			if (requestedStyles.contains(style))
			{
				collectUsedStyles(style, usedStylesMap, allStylesMap);
			}
			allStylesMap.put(style.getName(), style);
		}
		
		List<JRStyle> includedStyles = new ArrayList<>();
		for (Iterator<JRStyle> it = usedStylesMap.keySet().iterator(); it.hasNext();)
		{
			JRStyle style = it.next();
			JRStyle newStyle = getStyle(style);
			
			includedStyles.add(newStyle);
			if (requestedStyles.contains(style))
			{
				useDelayedStyle(newStyle);
			}			
		}
		
		checkUnresolvedReferences();
		
		return includedStyles;
	}

	protected Set<JRStyle> collectRequestedStyles(List<JRStyle> externalStyles)
	{
		Map<String,JRStyle> requestedStylesMap = new HashMap<>();
		for (Iterator<JRStyle> it = externalStyles.iterator(); it.hasNext();)
		{
			JRStyle style = it.next();
			String name = style.getName();
			if (delayedStyleSettersByName.containsKey(name))
			{
				requestedStylesMap.put(name, style);
			}
		}
		
		return new HashSet<>(requestedStylesMap.values());
	}

	protected void collectUsedStyles(JRStyle style, Map<JRStyle,Object> usedStylesMap, Map<String,JRStyle> allStylesMap)
	{
		if (!usedStylesMap.containsKey(style) && originalStyleList.contains(style))
		{
			JRStyle parent = style.getStyle();
			if (parent == null)
			{
				String parentName = style.getStyleNameReference();
				if (parentName != null)
				{
					parent = allStylesMap.get(parentName);
					if (parent == null)
					{
						throw 
							new JRRuntimeException(
								EXCEPTION_MESSAGE_KEY_STYLE_NOT_FOUND,  
								new Object[]{parentName} 
								);
					}
				}
			}
			
			if (parent != null)
			{
				collectUsedStyles(parent, usedStylesMap, allStylesMap);
			}
			
			usedStylesMap.put(style, null);
		}
	}
	
	protected void useDelayedStyle(JRStyle style)
	{
		List<JRStyleSetter> delayedSetters = delayedStyleSettersByName.remove(style.getName());
		if (delayedSetters != null)
		{
			for (Iterator<JRStyleSetter> it = delayedSetters.iterator(); it.hasNext();)
			{
				JRStyleSetter setter = it.next();
				setter.setStyle(style);
			}
		}
	}

	protected void checkUnresolvedReferences()
	{
		if (!delayedStyleSettersByName.isEmpty())
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNRESOLVED_STYLE,  
					new Object[]{String.join(", ", delayedStyleSettersByName.keySet())} 
					);
		}
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return filler.getJasperPrint().getDefaultStyleProvider();
	}


	@Override
	public void visitComponentElement(JRComponentElement componentElement)
	{
		JRFillComponentElement fill = null;

		if (componentElement != null)
		{
			fill = (JRFillComponentElement) get(componentElement);
			if (fill == null)
			{
				fill = new JRFillComponentElement(filler, componentElement, this);
			}
		}

		setVisitResult(fill);
	}


	@Override
	public void visitGenericElement(JRGenericElement element)
	{
		JRFillGenericElement fill = null;
		if (element != null)
		{
			fill = (JRFillGenericElement) get(element);
			if (fill == null)
			{
				fill = new JRFillGenericElement(filler, element, this);
			}
		}
		setVisitResult(fill);
	}

	public FillMultiAxisData getBidimensionalData(MultiAxisData data)
	{
		FillMultiAxisData fillData = null;
		if (data != null)
		{
			fillData = (FillMultiAxisData) get(data);
			if (fillData == null)
			{
				fillData = new FillMultiAxisData(data, this);
			}
		}
		return fillData;
	}

}
