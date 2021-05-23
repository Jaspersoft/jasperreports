/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleSetter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.design.JRDesignPropertyExpression;
import net.sf.jasperreports.engine.style.StyleProvider;
import net.sf.jasperreports.engine.style.StyleProviderFactory;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.engine.util.StyleUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRFillElement implements JRElement, JRFillCloneable, JRStyleSetter, DynamicPropertiesHolder
{
	/**
	 *
	 */
	public static final String EXCEPTION_MESSAGE_KEY_INVALID_BOOKMARK_LEVEL = "fill.anchor.bookmark.level.invalid";


	/**
	 *
	 */
	protected JRElement parent;
	protected List<JRPropertyExpression> propertyExpressions;
	protected List<String> dynamicTransferProperties;
	protected JRStyle providerStyle;
	protected Map<JRStyle,JRTemplateElement> templates = new HashMap<JRStyle,JRTemplateElement>();
	protected List<StyleProvider> styleProviders;

	/**
	 *
	 */
	protected JRBaseFiller filler;
	protected JRFillExpressionEvaluator expressionEvaluator;

	protected JRDefaultStyleProvider defaultStyleProvider;
	
	/**
	 *
	 */
	protected JRGroup printWhenGroupChanges;
	protected JRFillElementGroup elementGroup;

	/**
	 *
	 */
	protected JRFillBand band;
	
	protected JROriginProvider originProvider;
	
	protected PrintElementOriginator printElementOriginator;

	/**
	 *
	 */
	private boolean isPrintWhenExpressionNull = true;
	private boolean isPrintWhenTrue = true;
	private boolean isToPrint = true;
	private boolean isReprinted;
	private boolean isAlreadyPrinted;
	private Collection<JRFillElement> dependantElements = new ArrayList<JRFillElement>();
	private int relativeY;
	private int collapsedHeightAbove;
	private int collapsedHeightBelow;
	/**
	 * Keeps total stretch height, including forced stretch after honoring the stretchType property of the element. 
	 */
	private int stretchHeight;
	/**
	 * Keeps the natural stretch height calculated during element prepare. 
	 */
	private int prepareHeight;

	private int x;
	private int y;
	private int width;
	private int height;
	
	private boolean isValueRepeating;
	
	protected byte currentEvaluation;
	
	// used by elements that support evaluationTime=Auto
	protected Map<JREvaluationTime,DelayedEvaluations> delayedEvaluationsMap;

	protected JRFillElementContainer conditionalStylesContainer;
	protected FillContainerContext fillContainerContext;
	
	protected JRStyle initStyle;
	
	protected JRStyle currentStyle;
	
	/**
	 * Flag indicating whether the element is shrinkable.
	 * @see #setShrinkable(boolean)
	 */
	private boolean shrinkable;

	protected JRPropertiesMap staticProperties;
	protected JRPropertiesMap dynamicProperties;
	protected JRPropertiesMap mergedProperties;
	
	/**
	 *
	 *
	private JRElement topElementInGroup;
	private JRElement bottomElementInGroup;


	/**
	 *
	 */
	protected JRFillElement(
		JRBaseFiller filler,
		JRElement element,
		JRFillObjectFactory factory
		)
	{
		factory.put(element, this);

		this.parent = element;
		this.filler = filler;
		this.expressionEvaluator = factory.getExpressionEvaluator();
		this.defaultStyleProvider = factory.getDefaultStyleProvider();
		
		printElementOriginator = filler.assignElementId(this);

		/*   */
		printWhenGroupChanges = factory.getGroup(element.getPrintWhenGroupChanges());
		elementGroup = (JRFillElementGroup)factory.getVisitResult(element.getElementGroup());
		
		x = element.getX();
		y = element.getY();
		width = element.getWidth();
		height = element.getHeight();
		
		staticProperties = element.hasProperties() ? element.getPropertiesMap().cloneProperties() : null;
		mergedProperties = staticProperties;
		
		JRPropertyExpression[] elementPropertyExpressions = element.getPropertyExpressions();
		propertyExpressions = elementPropertyExpressions == null ? new ArrayList<JRPropertyExpression>(0)
				: new ArrayList<JRPropertyExpression>(Arrays.asList(elementPropertyExpressions));
		
		dynamicTransferProperties = findDynamicTransferProperties();
		
		factory.registerDelayedStyleSetter(this, parent);
		
		initStyleProviders();
	}


	protected JRFillElement(JRFillElement element, JRFillCloneFactory factory)
	{
		factory.put(element, this);

		this.parent = element.parent;
		this.filler = element.filler;
		this.expressionEvaluator = element.expressionEvaluator;
		this.defaultStyleProvider = element.defaultStyleProvider;
		this.originProvider = element.originProvider;
		
		printElementOriginator = element.printElementOriginator;

		/*   */
		printWhenGroupChanges = element.printWhenGroupChanges;
		elementGroup = (JRFillElementGroup) factory.getClone((JRFillElementGroup) element.getElementGroup());

		x = element.getX();
		y = element.getY();
		width = element.getWidth();
		height = element.getHeight();
		
		templates = element.templates;
		
		initStyle = element.initStyle;
		
		shrinkable = element.shrinkable;
		
		staticProperties = element.staticProperties == null ? null : element.staticProperties.cloneProperties();
		mergedProperties = staticProperties;
		this.propertyExpressions = new ArrayList<JRPropertyExpression>(element.propertyExpressions);
		this.dynamicTransferProperties = element.dynamicTransferProperties;
		
		// we need a style provider context for this element instance
		initStyleProviders();
	}
	
	private List<String> findDynamicTransferProperties()
	{
		if (propertyExpressions.isEmpty())
		{
			return null;
		}
		
		List<String> prefixes = filler.getPrintTransferPropertyPrefixes();
		List<String> transferProperties = new ArrayList<String>(propertyExpressions.size());
		for (JRPropertyExpression propertyExpression : propertyExpressions)
		{
			String propertyName = propertyExpression.getName();
			for (String prefix : prefixes)
			{
				if (propertyName.startsWith(prefix))
				{
					transferProperties.add(propertyName);
					break;
				}
			}
		}
		return transferProperties;
	}


	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return defaultStyleProvider;
	}

	/**
	 *
	 */
	protected StyleResolver getStyleResolver() 
	{
		return getDefaultStyleProvider().getStyleResolver();
	}

	@Override
	public UUID getUUID()
	{
		return parent.getUUID();
	}
	
	@Override
	public String getKey()
	{
		return parent.getKey();
	}

	@Override
	public PositionTypeEnum getPositionTypeValue()
	{
		return parent.getPositionTypeValue();//FIXME optimize this by consolidating style properties
	}

	@Override
	public void setPositionType(PositionTypeEnum positionType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public StretchTypeEnum getStretchTypeValue()
	{
		return parent.getStretchTypeValue();
	}

	@Override
	public void setStretchType(StretchTypeEnum stretchType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPrintRepeatedValues()
	{
		return parent.isPrintRepeatedValues();
	}

	@Override
	public void setPrintRepeatedValues(boolean isPrintRepeatedValues)
	{
	}

	@Override
	public ModeEnum getModeValue()
	{
		return getStyleResolver().getMode(this, ModeEnum.OPAQUE);
	}

	@Override
	public ModeEnum getOwnModeValue()
	{
		return providerStyle == null || providerStyle.getOwnModeValue() == null ? parent.getOwnModeValue() : providerStyle.getOwnModeValue();
	}

	@Override
	public void setMode(ModeEnum modeValue)
	{
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 *
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	/**
	 *
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public boolean isRemoveLineWhenBlank()
	{
		return parent.isRemoveLineWhenBlank();
	}

	@Override
	public void setRemoveLineWhenBlank(boolean isRemoveLine)
	{
	}

	@Override
	public boolean isPrintInFirstWholeBand()
	{
		return parent.isPrintInFirstWholeBand();
	}

	@Override
	public void setPrintInFirstWholeBand(boolean isPrint)
	{
	}

	@Override
	public boolean isPrintWhenDetailOverflows()
	{
		return parent.isPrintWhenDetailOverflows();
	}

	@Override
	public void setPrintWhenDetailOverflows(boolean isPrint)
	{
	}

	@Override
	public Color getForecolor()
	{
		return getStyleResolver().getForecolor(this);
	}

	@Override
	public Color getOwnForecolor()
	{
		return providerStyle == null || providerStyle.getOwnForecolor() == null ? parent.getOwnForecolor() : providerStyle.getOwnForecolor();
	}

	@Override
	public void setForecolor(Color forecolor)
	{
	}

	@Override
	public Color getBackcolor()
	{
		return getStyleResolver().getBackcolor(this);
	}

	@Override
	public Color getOwnBackcolor()
	{
		return providerStyle == null || providerStyle.getOwnBackcolor() == null ? parent.getOwnBackcolor() : providerStyle.getOwnBackcolor();
	}

	@Override
	public void setBackcolor(Color backcolor)
	{
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return parent.getPrintWhenExpression();
	}

	@Override
	public JRGroup getPrintWhenGroupChanges()
	{
		return printWhenGroupChanges;
	}

	@Override
	public JRElementGroup getElementGroup()
	{
		return elementGroup;
	}

	/**
	 *
	 */
	protected boolean isPrintWhenExpressionNull()
	{
		return isPrintWhenExpressionNull;
	}

	/**
	 *
	 */
	protected void setPrintWhenExpressionNull(boolean isPrintWhenExpressionNull)
	{
		this.isPrintWhenExpressionNull = isPrintWhenExpressionNull;
	}

	/**
	 *
	 */
	protected boolean isPrintWhenTrue()
	{
		return isPrintWhenTrue;
	}

	/**
	 *
	 */
	protected void setPrintWhenTrue(boolean isPrintWhenTrue)
	{
		this.isPrintWhenTrue = isPrintWhenTrue;
	}

	/**
	 *
	 */
	public boolean isToPrint()
	{
		return isToPrint;
	}

	/**
	 *
	 */
	protected void setToPrint(boolean isToPrint)
	{
		this.isToPrint = isToPrint;
	}

	/**
	 *
	 */
	protected boolean isReprinted()
	{
		return isReprinted;
	}

	/**
	 *
	 */
	protected void setReprinted(boolean isReprinted)
	{
		this.isReprinted = isReprinted;
	}

	/**
	 *
	 */
	public boolean isAlreadyPrinted()
	{
		return isAlreadyPrinted;
	}

	/**
	 *
	 */
	public void setAlreadyPrinted(boolean isAlreadyPrinted)
	{
		this.isAlreadyPrinted = isAlreadyPrinted;
	}

	/**
	 *
	 */
	protected JRElement[] getGroupElements()
	{
		JRElement[] groupElements = null;

		if (elementGroup != null)
		{
			groupElements = elementGroup.getElements();
		}

		return groupElements;
	}

	/**
	 *
	 */
	protected Collection<JRFillElement> getDependantElements()
	{
		return dependantElements;
	}

	/**
	 *
	 */
	protected void addDependantElement(JRFillElement element)
	{
		dependantElements.add(element);
	}

	/**
	 *
	 */
	protected int getRelativeY()
	{
		return relativeY;
	}

	/**
	 *
	 */
	protected void setRelativeY(int relativeY)
	{
		this.relativeY = relativeY;
	}

	/**
	 *
	 */
	protected int getCollapsedHeightAbove()
	{
		return collapsedHeightAbove;
	}

	/**
	 *
	 */
	protected void setCollapsedHeightAbove(int collapsedHeightAbove)
	{
		this.collapsedHeightAbove = collapsedHeightAbove;
	}

 	/**
	 *
	 */
	protected int getCollapsedHeightBelow()
	{
		return collapsedHeightBelow;
	}

	/**
	 *
	 */
	protected void setCollapsedHeightBelow(int collapsedHeightBelow)
	{
		this.collapsedHeightBelow = collapsedHeightBelow;
	}

	/**
	 *
	 */
	public int getStretchHeight()
	{
		return stretchHeight;
	}

	/**
	 *
	 */
	protected void setStretchHeight(int stretchHeight)
	{
		if (stretchHeight > getHeight() || (shrinkable && isRemoveLineWhenBlank()))
		{
			this.stretchHeight = stretchHeight;
		}
		else
		{
			this.stretchHeight = getHeight();
		}
	}

	/**
	 *
	 */
	public int getPrepareHeight()
	{
		return prepareHeight;
	}

	/**
	 * Element height is calculated in two phases. 
	 * First, the element stretches on its own during prepare, to accommodate all its content.
	 * This is the natural stretch and we keep track of the calculated height in prepareHeight.
	 * Secondly, the element stretches further in accordance with its stretchType property.
	 * This forced stretch occurs at a later time and the amount of stretch is kept in stretchHeight. 
	 */
	protected void setPrepareHeight(int prepareHeight)
	{
		this.prepareHeight = prepareHeight;
		
		setStretchHeight(prepareHeight);
	}

	/**
	 *
	 */
	protected JRFillBand getBand()
	{
		return band;
	}

	/**
	 *
	 */
	protected void setBand(JRFillBand band)
	{
		this.band = band;
		
		if (this.originProvider == null)
		{
			setOriginProvider(band);
		}
	}


	/**
	 *
	 */
	protected void initStyleProviders()
	{
		List<StyleProviderFactory> styleProviderFactories = filler.getJasperReportsContext().getExtensions(StyleProviderFactory.class);
		if (styleProviderFactories != null && styleProviderFactories.size() > 0)
		{
			FillStyleProviderContext styleProviderContext = new FillStyleProviderContext(this);
			for (StyleProviderFactory styleProviderFactory : styleProviderFactories)
			{
				StyleProvider styleProvider = styleProviderFactory.getStyleProvider(styleProviderContext, filler.getJasperReportsContext());
				if (styleProvider != null)
				{
					if (styleProviders == null)
					{
						styleProviders = new ArrayList<StyleProvider>();
					}
					styleProviders.add(styleProvider);
				}
			}
		}
	}

	
	/**
	 *
	 */
	protected void reset()
	{
		relativeY = y;
		collapsedHeightAbove = 0;
		collapsedHeightBelow = 0;
		stretchHeight = height;
		prepareHeight = height;

		if (elementGroup != null)
		{
			elementGroup.reset();
		}
	}

	protected void setCurrentEvaluation(byte evaluation)
	{
		currentEvaluation = evaluation;
	}

	/**
	 *
	 */
	protected abstract void evaluate(
		byte evaluation
		) throws JRException;


	/**
	 *
	 */
	protected void evaluateStyle(
		byte evaluation
		) throws JRException
	{
		providerStyle = null;

		if (styleProviders != null && styleProviders.size() > 0)
		{
			for (StyleProvider styleProvider : styleProviders)
			{
				JRStyle style = styleProvider.getStyle(evaluation);
				if (style != null)
				{
					if (providerStyle == null)
					{
						providerStyle = new JRBaseStyle();
					}
					StyleUtil.appendStyle(providerStyle, style);
				}
			}
		}
	}

	protected TimeZone getTimeZone()
	{
		return filler.getTimeZone();
	}

	/**
	 *
	 */
	protected void evaluatePrintWhenExpression(
		byte evaluation
		) throws JRException
	{
		boolean isExprNull = true;
		boolean isExprTrue = false;

		JRExpression expression = getPrintWhenExpression();
		if (expression != null)
		{
			isExprNull = false;
			Boolean printWhenExpressionValue = (Boolean) evaluateExpression(expression, evaluation);
			if (printWhenExpressionValue == null)
			{
				isExprTrue = false;
			}
			else
			{
				isExprTrue = printWhenExpressionValue;
			}
		}

		setPrintWhenExpressionNull(isExprNull);
		setPrintWhenTrue(isExprTrue);
	}


	/**
	 *
	 */
	protected abstract void rewind() throws JRException;


	/**
	 *
	 */
	protected abstract JRPrintElement fill() throws JRException;

	protected JRTemplateElement getElementTemplate()
	{
		JRTemplateElement template = null;
		JRStyle style = null;
		
		if (providerStyle == null)
		{
			// no style provider has been used so we can use cache template per style below
			style = getStyle();
			template = getTemplate(style);
		}
		
		if (template == null)
		{
			template = createElementTemplate();
			transferProperties(template);
			
			// deduplicate to previously created identical objects
			template = filler.fillContext.deduplicate(template);
			
			if (providerStyle == null)
			{
				registerTemplate(style, template);
			}
		}
		return template;
	}

	protected abstract JRTemplateElement createElementTemplate();
	
	/**
	 *
	 */
	protected boolean prepare(
		int availableHeight,
		boolean isOverflow
		) throws JRException
	{
		if (
			isPrintWhenExpressionNull() ||
			( !isPrintWhenExpressionNull() &&
			  isPrintWhenTrue() )
			)
		{
			setToPrint(true);
		}
		else
		{
			setToPrint(false);
		}

		setReprinted(false);

		return false;
	}


	/**
	 * @deprecated To be removed.
	 */
	protected void _stretchElement(int bandStretch)
	{
		switch (getStretchTypeValue())
		{
			case RELATIVE_TO_BAND_HEIGHT :
			case CONTAINER_HEIGHT :
			case CONTAINER_BOTTOM :
			{
				_stretchElementToHeight(getHeight() + bandStretch);
				break;
			}
			case RELATIVE_TO_TALLEST_OBJECT :
			case ELEMENT_GROUP_HEIGHT :
			case ELEMENT_GROUP_BOTTOM :
			{
				if (elementGroup != null)
				{
					//setStretchHeight(getHeight() + getStretchHeightDiff());
					_stretchElementToHeight(getHeight() + elementGroup.getStretchHeightDiff());
				}

				break;
			}
			case NO_STRETCH :
			default :
			{
				break;
			}
		}
	}
	
	/**
	 * @deprecated To be removed.
	 */
	protected void _stretchElementToHeight(int stretchHeight)
	{
		if (stretchHeight > getStretchHeight())
		{
			setStretchHeight(stretchHeight);
		}
	}


	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected boolean stretchElement(int containerStretch)
	{
		boolean applied = false;
		switch (getStretchTypeValue())
		{
			case RELATIVE_TO_BAND_HEIGHT :
			case CONTAINER_HEIGHT :
			case CONTAINER_BOTTOM :
			{
				applied = stretchElementToContainer(containerStretch);
				break;
			}
			case RELATIVE_TO_TALLEST_OBJECT :
			case ELEMENT_GROUP_HEIGHT :
			case ELEMENT_GROUP_BOTTOM :
			{
				applied = stretchElementToElementGroup();
				break;
			}
			case NO_STRETCH :
			default :
			{
				break;
			}
		}
		return applied;
	}


	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected boolean stretchElementToContainer(int containerStretch)//TODO subtract firstY?
	{
		boolean applied = false;
		switch (getStretchTypeValue())
		{
			case RELATIVE_TO_BAND_HEIGHT :
			case CONTAINER_HEIGHT :
			{
				applied = stretchElementToHeight(getHeight() + containerStretch);
				break;
			}
			case CONTAINER_BOTTOM :
			{
				applied = stretchElementToHeight(getY() - getRelativeY() + getHeight() + containerStretch);
				break;
			}
		}
		return applied;
	}


	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	protected boolean stretchElementToElementGroup()
	{
		boolean applied = false;
		if (elementGroup != null)
		{
			switch (getStretchTypeValue())
			{
				case RELATIVE_TO_TALLEST_OBJECT :
				case ELEMENT_GROUP_HEIGHT :
				{
					applied = stretchElementToHeight(getHeight() + elementGroup.getStretchHeightDiff());
					break;
				}
				case ELEMENT_GROUP_BOTTOM :
				{
					applied = stretchElementToHeight(getY() - getRelativeY() + getHeight() + elementGroup.getStretchHeightDiff());
					break;
				}
			}
		}
		return applied;
	}


	/**
	 * This method returns a boolean signaling if any stretch change occurred.
	 * It does not say which amount of stretch was applied, but that is OK, because the only place where this is checked
	 * is during frame cascading stretch, where the stretchHeight field of the frame (set here) is used directly.
	 */
	protected boolean stretchElementToHeight(int stretchHeight)
	{
		// cannot force the element to shrink below its natural stretch calculated during prepare;
		// such situation could occur when the container breaks and overflows
		boolean applied = false;
		if (stretchHeight > getPrepareHeight())
		{
			// any new stretchHeight that is greater than element's natural growth is fine
			setStretchHeight(stretchHeight);
			applied = true;
		}
		return applied;
	}


	/**
	 * @deprecated To be removed.
	 */
	protected void _moveDependantElements()
	{
		Collection<JRFillElement> elements = getDependantElements();
		if (elements != null && elements.size() > 0)
		{
			for (JRFillElement element : elements)
			{
				int newRelativeY = 
					getRelativeY() + getStretchHeight() //pusher element current bottom edge
					+ (element.getY() - (getY() + getHeight())); //design time distance between elements; difference between float element top edge and pusher element bottom edge

				if (newRelativeY > element.getRelativeY())
				{
					element.setRelativeY(newRelativeY);
				}
			}
		}
	}


	/**
	 *
	 */
	protected void moveDependantElements()
	{
		Collection<JRFillElement> elements = getDependantElements();
		if (elements != null && elements.size() > 0)
		{
			for (JRFillElement element : elements)
			{
				int newRelativeY = 
					getRelativeY() + getStretchHeight() //pusher element current bottom edge
					+ (element.getY() - (getY() + getHeight())) //design time distance between elements; difference between float element top edge and pusher element bottom edge
					- (element.getCollapsedHeightAbove() - getCollapsedHeightAbove()); //difference in collapsedY amount, meaning the elements could only have become closer together due to blank element removal

				if (newRelativeY > element.getRelativeY())
				{
					element.setRelativeY(newRelativeY);

					element.moveDependantElements();
				}
			}
		}
	}


	/**
	 * Resolves an element.
	 * 
	 * @param element the element
	 * @param evaluation the evaluation type
	 */
	protected abstract void resolveElement (JRPrintElement element, byte evaluation) throws JRException;
	
	protected void performDelayedEvaluation(JRPrintElement element, byte evaluation) 
			throws JRException
	{
		boolean updateTemplate = false;

		JRStyle printStyle = element.getStyle();
		if (isDelayedStyleEvaluation())
		{
			JRStyle elementStyle = initStyle;
			if (elementStyle == null)
			{
				elementStyle = filler.getDefaultStyle();
			}
			
			if (elementStyle != null)
			{
				JRStyle evaluatedStyle = conditionalStylesContainer.evaluateConditionalStyle(
						elementStyle, evaluation);
				// if the evaluated style differs from the existing style
				if (evaluatedStyle != printStyle)
				{
					// set the evaluated style as element style
					printStyle = evaluatedStyle;
					
					updateTemplate = true;
				}
			}
		}
		
		// set the current element style
		this.currentStyle = printStyle;
		
		resolveElement(element, evaluation);
		
		if (updateTemplate || providerStyle != null
				|| delayedEvaluationUpdatesTemplate())
		{
			// get/create an element template that corresponds to the
			// current style
			JRTemplateElement newTemplate = getElementTemplate();
			((JRTemplatePrintElement) element).updateElementTemplate(
					newTemplate);
		}
		
		// reset the current style
		this.currentStyle = null;
		//this.providerStyle = null;
	}

	protected boolean delayedEvaluationUpdatesTemplate()
	{
		return false;
	}


	/**
	 * Evaluates an expression.
	 * 
	 * @param expression the expression
	 * @param evaluation the evaluation type
	 * @return the evaluation result
	 * @throws JRException
	 */
	public final Object evaluateExpression(JRExpression expression, byte evaluation) throws JRException
	{
		if (filler.evaluateElementToBlank()) {
			return null;
		}
		return expressionEvaluator.evaluate(expression, evaluation);
	}


	/**
	 * Decides whether the value for this element is repeating.
	 * <p>
	 * Dynamic elements should call {@link #setValueRepeating(boolean) setValueRepeating(boolean)} on
	 * {@link #evaluate(byte) evaluate(byte)}.  Static elements don't have to do anything, this method
	 * will return <code>true</code> by default.
	 * 
	 * @return whether the value for this element is repeating
	 * @see #setValueRepeating(boolean)
	 */
	protected boolean isValueRepeating()
	{
		return isValueRepeating;
	}


	/**
	 * Sets the repeating flag for this element.
	 * <p>
	 * This method should be called by dynamic elements on {@link #evaluate(byte) evaluate(byte)}.
	 * 
	 * @param isValueRepeating whether the value of the element is repeating
	 * @see #isValueRepeating()
	 */
	protected void setValueRepeating(boolean isValueRepeating)
	{
		this.isValueRepeating = isValueRepeating;
	}


	protected JRFillVariable getVariable(String variableName)
	{
		return filler.getVariable(variableName);
	}


	protected JRFillField getField(String fieldName)
	{
		return filler.getField(fieldName);
	}
	
	// default for elements not supporting evaluationTime
	protected EvaluationTimeEnum getEvaluationTimeValue()
	{
		return EvaluationTimeEnum.NOW;
	}

	/**
	 * Resolves an element.
	 * 
	 * @param element the element
	 * @param evaluation the evaluation type
	 * @param evaluationTime the current evaluation time
	 */
	protected void resolveElement (JRPrintElement element, byte evaluation, JREvaluationTime evaluationTime) throws JRException
	{
		EvaluationTimeEnum evaluationTimeType = getEvaluationTimeValue();
		switch (evaluationTimeType)
		{
			case NOW:
				break;
			case AUTO:
				delayedEvaluate((JRRecordedValuesPrintElement) element, evaluationTime, evaluation);
				break;
			default:
				performDelayedEvaluation(element, evaluation);
				break;
		}
	}

	private static class DelayedEvaluations implements Serializable
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		final Set<String> fields;
		final Set<String> variables;

		DelayedEvaluations()
		{
			fields = new HashSet<String>();
			variables = new HashSet<String>();
		}
	}

	protected void initDelayedEvaluations()
	{
		if (getEvaluationTimeValue() == EvaluationTimeEnum.AUTO && delayedEvaluationsMap == null)
		{
			delayedEvaluationsMap = new HashMap<JREvaluationTime,DelayedEvaluations>();
			collectDelayedEvaluations();
		}
	}
	
	protected void collectDelayedEvaluations()
	{
		if (isDelayedStyleEvaluation())
		{
			collectStyleDelayedEvaluations();
			collectStyleProviderDelayedEvaluations();
		}
	}

	protected void collectStyleDelayedEvaluations()
	{
		JRStyle elementStyle = initStyle;
		if (elementStyle == null)
		{
			elementStyle = filler.getDefaultStyle();
		}
		
		if (elementStyle != null)
		{
			JRStyle style = elementStyle;
			while (style != null)
			{
				collectDelayedEvaluations(style);
				
				// proceed to the parent style
				style = style.getStyle();
			}
		}
	}

	protected void collectDelayedEvaluations(JRStyle style)
	{
		JRConditionalStyle[] conditionalStyles = style.getConditionalStyles();
		// collect delayed evaluations from conditional style expressions
		if (conditionalStyles != null && conditionalStyles.length > 0)
		{
			for (int i = 0; i < conditionalStyles.length; i++)
			{
				collectDelayedEvaluations(
						conditionalStyles[i].getConditionExpression());
			}
		}
	}


	protected void collectDelayedEvaluations(JRExpression expression)
	{
		if (expression != null)
		{
			JRExpressionChunk[] chunks = expression.getChunks();
			if (chunks != null)
			{
				for (int i = 0; i < chunks.length; i++)
				{
					JRExpressionChunk chunk = chunks[i];
					switch (chunk.getType())
					{
						case JRExpressionChunk.TYPE_FIELD:
						{
							DelayedEvaluations delayedEvaluations = getDelayedEvaluations(JREvaluationTime.EVALUATION_TIME_NOW);
							delayedEvaluations.fields.add(chunk.getText());
							break;
						}
						case JRExpressionChunk.TYPE_VARIABLE:
						{
							JREvaluationTime time = autogetVariableEvaluationTime(chunk.getText());
							DelayedEvaluations delayedEvaluations = getDelayedEvaluations(time);
							delayedEvaluations.variables.add(chunk.getText());
							break;
						}
					}
				}
			}
		}
	}

	
	protected void collectStyleProviderDelayedEvaluations()
	{
		if (styleProviders != null && styleProviders.size() > 0)
		{
			for (StyleProvider styleProvider : styleProviders)
			{
				String[] fields = styleProvider.getFields();
				if (fields != null && fields.length > 0)
				{
					DelayedEvaluations delayedEvaluations = getDelayedEvaluations(JREvaluationTime.EVALUATION_TIME_NOW);
					for (String field : fields)
					{
						delayedEvaluations.fields.add(field);
					}
				}
				String[] variables = styleProvider.getVariables();
				if (variables != null && variables.length > 0)
				{
					for (String variable : variables)
					{
						JREvaluationTime time = autogetVariableEvaluationTime(variable);
						DelayedEvaluations delayedEvaluations = getDelayedEvaluations(time);
						delayedEvaluations.variables.add(variable);
					}
				}
			}
		}
	}


	private DelayedEvaluations getDelayedEvaluations(JREvaluationTime time)
	{
		DelayedEvaluations delayedEvaluations = delayedEvaluationsMap.get(time);
		if (delayedEvaluations == null)
		{
			delayedEvaluations = new DelayedEvaluations();
			delayedEvaluationsMap.put(time, delayedEvaluations);
		}
		return delayedEvaluations;
	}


	private JREvaluationTime autogetVariableEvaluationTime(String variableName)
	{
		JRFillVariable variable = getVariable(variableName);
		JREvaluationTime evaluationTime;
		switch (variable.getResetTypeValue())
		{
			case REPORT:
				evaluationTime = JREvaluationTime.EVALUATION_TIME_REPORT;
				break;
			case MASTER:
				evaluationTime = JREvaluationTime.EVALUATION_TIME_MASTER;
				break;
			case PAGE:
				evaluationTime = JREvaluationTime.EVALUATION_TIME_PAGE;
				break;
			case COLUMN:
				evaluationTime = JREvaluationTime.EVALUATION_TIME_COLUMN;
				break;
			case GROUP:
				evaluationTime = JREvaluationTime.getGroupEvaluationTime(variable.getResetGroup().getName());
				break;
			default:
				evaluationTime = JREvaluationTime.EVALUATION_TIME_NOW;
				break;
		}
		
		if (!evaluationTime.equals(JREvaluationTime.EVALUATION_TIME_NOW) &&
				band.isNowEvaluationTime(evaluationTime))
		{
			evaluationTime = JREvaluationTime.EVALUATION_TIME_NOW;
		}
		
		if (variable.getCalculationValue() == CalculationEnum.SYSTEM &&
				evaluationTime.equals(JREvaluationTime.EVALUATION_TIME_NOW) &&
				band.isVariableUsedInReturns(variableName))
		{
			evaluationTime = JREvaluationTime.getBandEvaluationTime(band);
		}

		return evaluationTime;
	}
	
	
	protected void initDelayedEvaluationPrint(JRRecordedValuesPrintElement printElement) throws JRException
	{
		for (Iterator<JREvaluationTime> it = delayedEvaluationsMap.keySet().iterator(); it.hasNext();)
		{
			JREvaluationTime evaluationTime = it.next();
			if (!evaluationTime.equals(JREvaluationTime.EVALUATION_TIME_NOW))
			{
				filler.addBoundElement(this, printElement, evaluationTime);
			}
		}
		
		printElement.initRecordedValues(delayedEvaluationsMap.keySet());
		
		if (delayedEvaluationsMap.containsKey(JREvaluationTime.EVALUATION_TIME_NOW))
		{
			delayedEvaluate(printElement, JREvaluationTime.EVALUATION_TIME_NOW, currentEvaluation);
		}
	}


	protected void delayedEvaluate(JRRecordedValuesPrintElement printElement, JREvaluationTime evaluationTime, byte evaluation) throws JRException
	{
		JRRecordedValues recordedValues = printElement.getRecordedValues();
		if (!recordedValues.lastEvaluationTime())
		{
			DelayedEvaluations delayedEvaluations = delayedEvaluationsMap.get(evaluationTime);
			
			for (Iterator<String> it = delayedEvaluations.fields.iterator(); it.hasNext();)
			{
				String fieldName = it.next();
				JRFillField field = getField(fieldName);
				recordedValues.recordFieldValue(fieldName, field.getValue(evaluation));
			}

			for (Iterator<String> it = delayedEvaluations.variables.iterator(); it.hasNext();)
			{
				String variableName = it.next();
				JRFillVariable variable = getVariable(variableName);
				recordedValues.recordVariableValue(variableName, variable.getValue(evaluation));
			}
		}

		recordedValues.doneEvaluation(evaluationTime);
		
		if (recordedValues.finishedEvaluations())
		{
			overwriteWithRecordedValues(recordedValues, evaluation);
			performDelayedEvaluation(printElement, evaluation);
			restoreValues(recordedValues, evaluation);
			printElement.deleteRecordedValues();
		}
	}

	
	private void overwriteWithRecordedValues(JRRecordedValues recordedValues, byte evaluation)
	{
		Map<String,Object> fieldValues = recordedValues.getRecordedFieldValues();
		if (fieldValues != null)
		{
			for (Iterator<Map.Entry<String,Object>> it = fieldValues.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry<String,Object> entry = it.next();
				String fieldName = entry.getKey();
				Object fieldValue = entry.getValue();
				JRFillField field = getField(fieldName);
				field.overwriteValue(fieldValue, evaluation);
			}
		}
		
		Map<String,Object> variableValues = recordedValues.getRecordedVariableValues();
		if (variableValues != null)
		{
			for (Iterator<Map.Entry<String,Object>> it = variableValues.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry<String,Object> entry = it.next();
				String variableName = entry.getKey();
				Object variableValue = entry.getValue();
				JRFillVariable variable = getVariable(variableName);
				variable.overwriteValue(variableValue, evaluation);
			}
		}
	}

	private void restoreValues(JRRecordedValues recordedValues, byte evaluation)
	{
		Map<String,Object> fieldValues = recordedValues.getRecordedFieldValues();
		if (fieldValues != null)
		{
			for (Iterator<String> it = fieldValues.keySet().iterator(); it.hasNext();)
			{
				String fieldName = it.next();
				JRFillField field = getField(fieldName);
				field.restoreValue(evaluation);
			}
		}
		
		Map<String,Object> variableValues = recordedValues.getRecordedVariableValues();
		if (variableValues != null)
		{
			for (Iterator<String> it = variableValues.keySet().iterator(); it.hasNext();)
			{
				String variableName = it.next();
				JRFillVariable variable = getVariable(variableName);
				variable.restoreValue(evaluation);
			}
		}
	}

	/**
	 * 
	 */
	public void setConditionalStylesContainer(JRFillElementContainer conditionalStylesContainer)
	{
		this.conditionalStylesContainer = conditionalStylesContainer;
		if (fillContainerContext == null)
		{
			fillContainerContext = conditionalStylesContainer;
		}
	}

	/**
	 * 
	 */
	public JRFillElementContainer getConditionalStylesContainer()
	{
		return conditionalStylesContainer;
	}

	@Override
	public JRStyle getStyle()
	{
		// the current style overrides other style objects
		if (currentStyle != null)
		{
			return currentStyle;
		}
		
		JRStyle crtStyle = initStyle;
		
		boolean isUsingDefaultStyle = false;

		if (crtStyle == null)
		{
			crtStyle = filler.getDefaultStyle();
			isUsingDefaultStyle = true;
		}

		JRStyle evalStyle = crtStyle;

		if (conditionalStylesContainer != null)
		{
			evalStyle = conditionalStylesContainer.getEvaluatedConditionalStyle(crtStyle);
		}
		if (isUsingDefaultStyle && evalStyle == crtStyle)
		{
			evalStyle = null;
		}
		
		return evalStyle;
	}
	
	/**
	 * 
	 */
	protected JRTemplateElement getTemplate(JRStyle style)
	{
		return templates.get(style);
	}

	/**
	 * 
	 */
	protected void registerTemplate(JRStyle style, JRTemplateElement template)
	{
		templates.put(style, template);
	}
	
	
	/**
	 * Indicates whether an element is shrinkable.
	 * <p>
	 * This flag is only effective when {@link #isRemoveLineWhenBlank() isRemoveLineWhenBlank} is also set.
	 * 
	 * @param shrinkable whether the element is shrinkable
	 */
	protected final void setShrinkable(boolean shrinkable)
	{
		this.shrinkable = shrinkable;
	}


	/**
	 * Called when the stretch height of an element is final so that
	 * the element can perform any adjustments.
	 * @deprecated To be removed.
	 */
	protected void stretchHeightFinal()
	{
		// nothing		
	}
	
	
	protected boolean isEvaluateNow()
	{
		boolean evaluateNow;
		switch (getEvaluationTimeValue())
		{
			case NOW:
				evaluateNow = true;
				break;

			case AUTO:
				evaluateNow = isAutoEvaluateNow();
				break;

			default:
				evaluateNow = false;
				break;
		}
		return evaluateNow;
	}
	
	
	protected boolean isAutoEvaluateNow()
	{
		return delayedEvaluationsMap == null || delayedEvaluationsMap.isEmpty() 
				|| (delayedEvaluationsMap.size() == 1 
						&& delayedEvaluationsMap.containsKey(JREvaluationTime.EVALUATION_TIME_NOW));
	}
	
	
	protected boolean isEvaluateAuto()
	{
		return getEvaluationTimeValue() == EvaluationTimeEnum.AUTO && !isAutoEvaluateNow();
	}

	@Override
	public String getStyleNameReference()
	{
		return null;
	}

	@Override
	public void setStyle(JRStyle style)
	{
		initStyle = style;
		if (conditionalStylesContainer != null)
		{
			conditionalStylesContainer.collectConditionalStyle(style);
		}
	}

	@Override
	public void setStyleNameReference(String name)
	{
		throw new UnsupportedOperationException("Style name references not allowed at fill time");
	}
	
	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object clone(JRElementGroup parentGroup) 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRElement clone(JRElementGroup parentGroup, int y)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasProperties()
	{
		return mergedProperties != null && mergedProperties.hasProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return mergedProperties;
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		//element properties default to report properties
		return filler.getMainDataset();
	}


	@Override
	public JRPropertyExpression[] getPropertyExpressions()
	{
		return propertyExpressions.toArray(new JRPropertyExpression[propertyExpressions.size()]);
	}
	
	protected void transferProperties(JRTemplateElement template)
	{
		filler.getPropertiesUtil().transferProperties(parent, template, 
				JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);
	}
	
	protected void transferProperties(JRPrintElement element)
	{
		filler.getPropertiesUtil().transferProperties(dynamicProperties, element, 
				dynamicTransferProperties);
	}
	
	protected JRPropertiesMap getEvaluatedProperties()
	{
		return mergedProperties;
	}
	
	protected void evaluateProperties(byte evaluation) throws JRException
	{
		if (propertyExpressions.isEmpty())
		{
			dynamicProperties = null;
			mergedProperties = staticProperties;
		}
		else
		{
			dynamicProperties = new JRPropertiesMap();
			
			for (JRPropertyExpression prop : propertyExpressions)
			{
				String value = (String) evaluateExpression(prop.getValueExpression(), evaluation);
				//if (value != null) //for some properties such as data properties in metadata exporters, the null value is significant
				{
					dynamicProperties.setProperty(prop.getName(), value);
				}
			}
			
			mergedProperties = dynamicProperties.cloneProperties();
			mergedProperties.setBaseProperties(staticProperties);
		}
	}
	
	protected void setOriginProvider(JROriginProvider originProvider)
	{
		this.originProvider = originProvider;
	}

	protected JROrigin getElementOrigin()
	{
		JROrigin elementOrigin = null;
		if (originProvider != null)
		{
			elementOrigin = originProvider.getOrigin();
		}
		return elementOrigin;
	}
	
	protected boolean isDelayedStyleEvaluation()
	{
		return filler.getPropertiesUtil().getBooleanProperty(this, 
				JRStyle.PROPERTY_EVALUATION_TIME_ENABLED, false);
	}
	
	public JRBaseFiller getFiller()
	{
		return filler;
	}


	@Override
	public boolean hasDynamicProperties()
	{
		return !propertyExpressions.isEmpty();
	}

	@Override
	public boolean hasDynamicProperty(String name)
	{
		// not called very often for now so doing linear search in array
		for (JRPropertyExpression prop : propertyExpressions)
		{
			if (prop.getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public JRPropertiesMap getDynamicProperties()
	{
		return dynamicProperties;
	}

	protected JRStyle getInitStyle()
	{
		return initStyle;
	}

	protected JRElement getParent()
	{
		return parent;
	}
	
	protected void addDynamicProperty(String name, JRExpression expression)
	{
		JRDesignPropertyExpression prop = new JRDesignPropertyExpression();
		prop.setName(name);
		prop.setValueExpression(expression);
		
		propertyExpressions.add(prop);
		// recomputing
		dynamicTransferProperties = findDynamicTransferProperties();
	}
	
	protected void setExpressionEvaluator(JRFillExpressionEvaluator expressionEvaluator)
	{
		this.expressionEvaluator = expressionEvaluator;
	}


	/**
	 *
	 */
	public static Integer getBookmarkLevel(Object value) throws JRException
	{
		Integer level = null;
		
		if (value != null)
		{
			if (value instanceof Number)
			{
				level = ((Number)value).intValue();
			}
			else
			{
				try
				{
					level = Integer.parseInt(value.toString());
				}
				catch (NumberFormatException e)
				{
					//do nothing
				}
			}
			
			if (level == null || level < 0)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_INVALID_BOOKMARK_LEVEL,  
						new Object[] {value} 
						);
			}
		}
		
		return level;
	}
}
