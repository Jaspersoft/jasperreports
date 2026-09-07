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
import java.util.List;
import java.util.ListIterator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.base.JRBaseTextField;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignTextField extends JRDesignTextElement implements JRTextField
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	
	/*
	 * Text field properties
	 */
	
	public static final String PROPERTY_ANCHOR_NAME_EXPRESSION = "anchorNameExpression";
	
	public static final String PROPERTY_BOOKMARK_LEVEL = "bookmarkLevel";
	
	public static final String PROPERTY_BOOKMARK_LEVEL_EXPRESSION = "bookmarkLevelExpression";
	
	public static final String PROPERTY_EVALUATION_GROUP = "evaluationGroup";
	
	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";
	
	public static final String PROPERTY_EXPRESSION = "expression";
	
	public static final String PROPERTY_PATTERN_EXPRESSION = "patternExpression";

	/**
	 *
	 */
	protected TextAdjustEnum textAdjust;
	protected EvaluationTimeEnum evaluationTime;
	protected String pattern;
	protected Boolean isBlankWhenNull;
	protected String linkType;
	protected String linkTarget;
	private List<JRHyperlinkParameter> hyperlinkParameters;

	/**
	 *
	 */
	protected String evaluationGroup;
	protected JRExpression expression;
	protected JRExpression patternExpression;
	protected JRExpression anchorNameExpression;
	protected JRExpression bookmarkLevelExpression;
	protected JRExpression hyperlinkReferenceExpression;
	protected JRExpression hyperlinkWhenExpression;
	protected JRExpression hyperlinkAnchorExpression;
	protected JRExpression hyperlinkPageExpression;
	private JRExpression hyperlinkTooltipExpression;

	/**
	 * The bookmark level for the anchor associated with this field.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity


	@JsonCreator // not actually needed; used for clarity
	public JRDesignTextField()
	{
		this(JasperDesign.getThreadInstance());
	}
		
	/**
	 *
	 */
	public JRDesignTextField(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);
		
		hyperlinkParameters = new ArrayList<>();
	}
		

	@Override
	public TextAdjustEnum getTextAdjust()
	{
		return this.textAdjust;
	}
		
	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return this.evaluationTime;
	}
		
	@Override
	public String getPattern()
	{
		return getStyleResolver().getPattern(this);
	}

	@Override
	public String getOwnPattern()
	{
		return this.pattern;
	}
		
	@Override
	public boolean isBlankWhenNull()
	{
		return getStyleResolver().isBlankWhenNull(this);
	}

	@Override
	public Boolean isOwnBlankWhenNull()
	{
		return isBlankWhenNull;
	}

	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(this);
	}
		
	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(this);
	}
		
	@Override
	public String getEvaluationGroup()
	{
		return this.evaluationGroup;
	}
		
	@Override
	public JRExpression getExpression()
	{
		return this.expression;
	}

	@Override
	public JRExpression getPatternExpression()
	{
		return this.patternExpression;
	}

	@Override
	public JRExpression getAnchorNameExpression()
	{
		return this.anchorNameExpression;
	}
	
	@Override
	public JRExpression getBookmarkLevelExpression()
	{
		return this.bookmarkLevelExpression;
	}

	@Override
	public JRExpression getHyperlinkReferenceExpression()
	{
		return this.hyperlinkReferenceExpression;
	}

	@Override
	public JRExpression getHyperlinkWhenExpression()
	{
		return this.hyperlinkWhenExpression;
	}

	@Override
	public JRExpression getHyperlinkAnchorExpression()
	{
		return this.hyperlinkAnchorExpression;
	}

	@Override
	public JRExpression getHyperlinkPageExpression()
	{
		return this.hyperlinkPageExpression;
	}

	@Override
	public void setTextAdjust(TextAdjustEnum textAdjust)
	{
		TextAdjustEnum old = this.textAdjust;
		this.textAdjust = textAdjust;
		getEventSupport().firePropertyChange(JRBaseTextField.PROPERTY_TEXT_ADJUST, old, this.textAdjust);
	}
	
	/**
	 *
	 */
	public void setEvaluationTime(EvaluationTimeEnum evaluationTime)
	{
		Object old = this.evaluationTime;
		this.evaluationTime = evaluationTime;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME, old, this.evaluationTime);
	}
		
	@Override
	public void setPattern(String pattern)
	{
		Object old = this.pattern;
		this.pattern = pattern;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_PATTERN, old, this.pattern);
	}

	@Override
	public void setBlankWhenNull(Boolean isBlank)
	{
		Object old = this.isBlankWhenNull;
		this.isBlankWhenNull = isBlank;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_BLANK_WHEN_NULL, old, this.isBlankWhenNull);
	}

	/**
	 * Sets the link type as a built-in hyperlink type.
	 * 
	 * @param hyperlinkType the built-in hyperlink type
	 * @see #getLinkType()
	 */
	public void setHyperlinkType(HyperlinkTypeEnum hyperlinkType)
	{
		setLinkType(JRHyperlinkHelper.getLinkType(hyperlinkType));
	}
		
	/**
	 *
	 */
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		setLinkTarget(JRHyperlinkHelper.getLinkTarget(hyperlinkTarget));
	}
		
	/**
	 *
	 */
	public void setEvaluationGroup(String evaluationGroup)
	{
		Object old = this.evaluationGroup;
		this.evaluationGroup = evaluationGroup;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_GROUP, old, this.evaluationGroup);
	}
		
	/**
	 *
	 */
	public void setExpression(JRExpression expression)
	{
		Object old = this.expression;
		this.expression = expression;
		getEventSupport().firePropertyChange(PROPERTY_EXPRESSION, old, this.expression);
	}

	/**
	 *
	 */
	public void setPatternExpression(JRExpression patternExpression)
	{
		Object old = this.patternExpression;
		this.patternExpression = patternExpression;
		getEventSupport().firePropertyChange(PROPERTY_PATTERN_EXPRESSION, old, this.patternExpression);
	}

	/**
	 *
	 */
	public void setAnchorNameExpression(JRExpression anchorNameExpression)
	{
		Object old = this.anchorNameExpression;
		this.anchorNameExpression = anchorNameExpression;
		getEventSupport().firePropertyChange(PROPERTY_ANCHOR_NAME_EXPRESSION, old, this.anchorNameExpression);
	}
	
	/**
	 *
	 */
	public void setBookmarkLevelExpression(JRExpression bookmarkLevelExpression)
	{
		Object old = this.bookmarkLevelExpression;
		this.bookmarkLevelExpression = bookmarkLevelExpression;
		getEventSupport().firePropertyChange(PROPERTY_BOOKMARK_LEVEL_EXPRESSION, old, this.bookmarkLevelExpression);
	}

	/**
	 *
	 */
	public void setHyperlinkReferenceExpression(JRExpression hyperlinkReferenceExpression)
	{
		Object old = this.hyperlinkReferenceExpression;
		this.hyperlinkReferenceExpression = hyperlinkReferenceExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_REFERENCE_EXPRESSION, old, this.hyperlinkReferenceExpression);
	}

	/**
	 *
	 */
	public void setHyperlinkWhenExpression(JRExpression hyperlinkWhenExpression)
	{
		Object old = this.hyperlinkWhenExpression;
		this.hyperlinkWhenExpression = hyperlinkWhenExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_WHEN_EXPRESSION, old, this.hyperlinkWhenExpression);
	}

	/**
	 *
	 */
	public void setHyperlinkAnchorExpression(JRExpression hyperlinkAnchorExpression)
	{
		Object old = this.hyperlinkAnchorExpression;
		this.hyperlinkAnchorExpression = hyperlinkAnchorExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_ANCHOR_EXPRESSION, old, this.hyperlinkAnchorExpression);
	}

	/**
	 *
	 */
	public void setHyperlinkPageExpression(JRExpression hyperlinkPageExpression)
	{
		Object old = this.hyperlinkPageExpression;
		this.hyperlinkPageExpression = hyperlinkPageExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_PAGE_EXPRESSION, old, this.hyperlinkPageExpression);
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitTextField(this);
	}
	

	@Override
	public int getBookmarkLevel()
	{
		return bookmarkLevel;
	}


	/**
	 * Sets the boomark level for the anchor associated with this field.
	 * 
	 * @param bookmarkLevel the bookmark level (starting from 1)
	 * or {@link JRAnchor#NO_BOOKMARK NO_BOOKMARK} if no bookmark should be created 
	 */
	public void setBookmarkLevel(int bookmarkLevel)
	{
		int old = this.bookmarkLevel;
		this.bookmarkLevel = bookmarkLevel;
		getEventSupport().firePropertyChange(PROPERTY_BOOKMARK_LEVEL, old, this.bookmarkLevel);
	}


	@Override
	public String getLinkType()
	{
		return linkType;
	}


	/**
	 * Sets the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @param type the hyperlink type
	 */
	public void setLinkType(String type)
	{
		Object old = this.linkType;
		this.linkType = type;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_LINK_TYPE, old, this.linkType);
	}

	@Override
	public String getLinkTarget()
	{
		return linkTarget;
	}


	/**
	 * Sets the hyperlink target name.
	 * <p>
	 * The target name can be one of the built-in names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary name.
	 * </p>
	 * @param target the hyperlink target name
	 */
	public void setLinkTarget(String target)
	{
		Object old = this.linkTarget;
		this.linkTarget = target;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_LINK_TARGET, old, this.linkTarget);
	}


	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters()
	{
		JRHyperlinkParameter[] parameters;
		if (hyperlinkParameters.isEmpty())
		{
			parameters = null;
		}
		else
		{
			parameters = new JRHyperlinkParameter[hyperlinkParameters.size()];
			hyperlinkParameters.toArray(parameters);
		}
		return parameters;
	}
	
	
	@JsonSetter
	private void setHyperlinkParameters(List<JRHyperlinkParameter> parameters)
	{
		if (parameters != null)
		{
			for (JRHyperlinkParameter parameter : parameters)
			{
				addHyperlinkParameter(parameter);
			}
		}
	}
	
	
	/**
	 * Returns the list of custom hyperlink parameters.
	 * 
	 * @return the list of custom hyperlink parameters
	 */
	@JsonIgnore
	public List<JRHyperlinkParameter> getHyperlinkParametersList()
	{
		return hyperlinkParameters;
	}
	
	
	/**
	 * Adds a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to add
	 */
	public void addHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		hyperlinkParameters.add(parameter);
		getEventSupport().fireCollectionElementAddedEvent(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS, 
				parameter, hyperlinkParameters.size() - 1);
	}
	

	/**
	 * Removes a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to remove
	 */
	public void removeHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		int idx = hyperlinkParameters.indexOf(parameter);
		if (idx >= 0)
		{
			hyperlinkParameters.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS, 
					parameter, idx);
		}
	}
	
	
	/**
	 * Removes a custom hyperlink parameter.
	 * <p>
	 * If multiple parameters having the specified name exist, all of them
	 * will be removed
	 * </p>
	 * 
	 * @param parameterName the parameter name
	 */
	public void removeHyperlinkParameter(String parameterName)
	{
		for (ListIterator<JRHyperlinkParameter> it = hyperlinkParameters.listIterator(); it.hasNext();)
		{
			JRHyperlinkParameter parameter = it.next();
			if (parameter.getName() != null && parameter.getName().equals(parameterName))
			{
				it.remove();
				getEventSupport().fireCollectionElementRemovedEvent(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS, 
						parameter, it.nextIndex());
			}
		}
	}
	
	
	@Override
	public JRExpression getHyperlinkTooltipExpression()
	{
		return hyperlinkTooltipExpression;
	}

	
	/**
	 * Sets the expression which will be used to generate the hyperlink tooltip.
	 * 
	 * @param hyperlinkTooltipExpression the expression which will be used to generate the hyperlink tooltip
	 * @see #getHyperlinkTooltipExpression()
	 */
	public void setHyperlinkTooltipExpression(JRExpression hyperlinkTooltipExpression)
	{
		Object old = this.hyperlinkTooltipExpression;
		this.hyperlinkTooltipExpression = hyperlinkTooltipExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_TOOLTIP_EXPRESSION, old, this.hyperlinkTooltipExpression);
	}
	
	@Override
	public Object clone() 
	{
		JRDesignTextField clone = (JRDesignTextField)super.clone();
		clone.hyperlinkParameters = JRCloneUtils.cloneList(hyperlinkParameters);
		clone.expression = JRCloneUtils.nullSafeClone(expression);
		clone.patternExpression = JRCloneUtils.nullSafeClone(patternExpression);
		clone.anchorNameExpression = JRCloneUtils.nullSafeClone(anchorNameExpression);
		clone.bookmarkLevelExpression = JRCloneUtils.nullSafeClone(bookmarkLevelExpression);
		clone.hyperlinkReferenceExpression = JRCloneUtils.nullSafeClone(hyperlinkReferenceExpression);
		clone.hyperlinkWhenExpression = JRCloneUtils.nullSafeClone(hyperlinkWhenExpression);
		clone.hyperlinkAnchorExpression = JRCloneUtils.nullSafeClone(hyperlinkAnchorExpression);
		clone.hyperlinkPageExpression = JRCloneUtils.nullSafeClone(hyperlinkPageExpression);
		clone.hyperlinkTooltipExpression = JRCloneUtils.nullSafeClone(hyperlinkTooltipExpression);
		return clone;
	}
}
