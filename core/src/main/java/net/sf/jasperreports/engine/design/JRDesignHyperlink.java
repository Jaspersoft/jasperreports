/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.base.JRBaseHyperlink;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;


/**
 * Stand-alone implementation of {@link JRHyperlink JRHyperlink}
 * which should be used for report design purposes. 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignHyperlink extends JRBaseHyperlink implements JRChangeEventsSupport
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_HYPERLINK_ANCHOR_EXPRESSION = "hyperlinkAnchorExpression";
	
	public static final String PROPERTY_HYPERLINK_PAGE_EXPRESSION = "hyperlinkPageExpression";
	
	public static final String PROPERTY_HYPERLINK_REFERENCE_EXPRESSION = "hyperlinkReferenceExpression";
	
	public static final String PROPERTY_HYPERLINK_WHEN_EXPRESSION = "hyperlinkWhenExpression";
	
	public static final String PROPERTY_HYPERLINK_TARGET = "hyperlinkTarget";
	
	public static final String PROPERTY_LINK_TARGET = "linkTarget";
	
	public static final String PROPERTY_HYPERLINK_TOOLTIP_EXPRESSION = "hyperlinkTooltipExpression";
	
	public static final String PROPERTY_LINK_TYPE = "hyperlinkType";
	
	public static final String PROPERTY_HYPERLINK_PARAMETERS = "hyperlinkParameters";
	
	private List<JRHyperlinkParameter> hyperlinkParametersList;
	
	public JRDesignHyperlink()
	{
		hyperlinkParametersList = new ArrayList<>();
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
	 * Sets the hyperlink target.
	 * 
	 * @param hyperlinkTarget the hyperlink target, one of
	 * <ul>
	 * <li>{@link HyperlinkTargetEnum#SELF HyperlinkTargetEnum.SELF}</li>
	 * <li>{@link HyperlinkTargetEnum#BLANK HyperlinkTargetEnum.BLANK}</li>
	 * </ul>
	 * @see #getHyperlinkTarget()
	 */
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		setLinkTarget(JRHyperlinkHelper.getLinkTarget(hyperlinkTarget));
	}


	/**
	 * Sets the expression that will generate the hyperlink reference URL
	 * or the referred document location.
	 * <p>
	 * This expression is used when the hyperlink type is
	 * {@link HyperlinkTypeEnum#REFERENCE HyperlinkTypeEnum.REFERENCE},
	 * {@link HyperlinkTypeEnum#REMOTE_ANCHOR HyperlinkTypeEnum.REMOTE_ANCHOR} or
	 * {@link HyperlinkTypeEnum#REMOTE_PAGE HyperlinkTypeEnum.REMOTE_PAGE}.
	 * The type of the expression should be <code>java.lang.String</code>
	 * </p>
	 * 
	 * @param hyperlinkReferenceExpression the reference expression
	 * @see #getHyperlinkReferenceExpression()
	 */
	public void setHyperlinkReferenceExpression(JRExpression hyperlinkReferenceExpression)
	{
		Object old = this.hyperlinkReferenceExpression;
		this.hyperlinkReferenceExpression = hyperlinkReferenceExpression;
		getEventSupport().firePropertyChange(PROPERTY_HYPERLINK_REFERENCE_EXPRESSION, old, this.hyperlinkReferenceExpression);
	}


	/**
	 * Sets the boolean expression that will specify if the hyperlink should be displayed or not.
	 */
	public void setHyperlinkWhenExpression(JRExpression hyperlinkWhenExpression)
	{
		Object old = this.hyperlinkWhenExpression;
		this.hyperlinkWhenExpression = hyperlinkWhenExpression;
		getEventSupport().firePropertyChange(PROPERTY_HYPERLINK_WHEN_EXPRESSION, old, this.hyperlinkWhenExpression);
	}


	/**
	 * Sets the expression that will generate the referred anchor.
	 * <p>
	 * This expression is used when the hyperlink type is
	 * {@link HyperlinkTypeEnum#LOCAL_ANCHOR HyperlinkTypeEnum.LOCAL_ANCHOR} or
	 * {@link HyperlinkTypeEnum#REMOTE_ANCHOR HyperlinkTypeEnum.REMOTE_ANCHOR}.
	 * The type of the expression should be <code>java.lang.String</code>
	 * </p>
	 * 
	 * @param hyperlinkAnchorExpression the anchor expression
	 * @see #getHyperlinkAnchorExpression()
	 */
	public void setHyperlinkAnchorExpression(JRExpression hyperlinkAnchorExpression)
	{
		Object old = this.hyperlinkAnchorExpression;
		this.hyperlinkAnchorExpression = hyperlinkAnchorExpression;
		getEventSupport().firePropertyChange(PROPERTY_HYPERLINK_ANCHOR_EXPRESSION, old, this.hyperlinkAnchorExpression);
	}


	/**
	 * Sets the expression that will generate the referred page.
	 * <p>
	 * This expression is used when the hyperlink type is
	 * {@link HyperlinkTypeEnum#LOCAL_PAGE HyperlinkTypeEnum.LOCAL_PAGE} or
	 * {@link HyperlinkTypeEnum#REMOTE_PAGE HyperlinkTypeEnum.REMOTE_PAGE}.
	 * The type of the expression should be <code>java.lang.Integer</code>
	 * </p>
	 * 
	 * @param hyperlinkPageExpression the page expression
	 * @see #getHyperlinkPageExpression()
	 */
	public void setHyperlinkPageExpression(JRExpression hyperlinkPageExpression)
	{
		Object old = this.hyperlinkPageExpression;
		this.hyperlinkPageExpression = hyperlinkPageExpression;
		getEventSupport().firePropertyChange(PROPERTY_HYPERLINK_PAGE_EXPRESSION, old, this.hyperlinkPageExpression);
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
		getEventSupport().firePropertyChange(PROPERTY_LINK_TYPE, old, this.linkType);
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
		getEventSupport().firePropertyChange(PROPERTY_LINK_TARGET, old, this.linkTarget);
	}


	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters()
	{
		JRHyperlinkParameter[] parameters;
		if (hyperlinkParametersList.isEmpty())
		{
			parameters = null;
		}
		else
		{
			parameters = new JRHyperlinkParameter[hyperlinkParametersList.size()];
			hyperlinkParametersList.toArray(parameters);
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
		return hyperlinkParametersList;
	}
	
	
	/**
	 * Adds a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to add
	 */
	public void addHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		hyperlinkParametersList.add(parameter);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_HYPERLINK_PARAMETERS, 
				parameter, hyperlinkParametersList.size() - 1);
	}
	

	/**
	 * Removes a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to remove
	 */
	public void removeHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		int idx = hyperlinkParametersList.indexOf(parameter);
		if (idx >= 0)
		{
			hyperlinkParametersList.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_HYPERLINK_PARAMETERS, 
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
		for (ListIterator<JRHyperlinkParameter> it = hyperlinkParametersList.listIterator(); it.hasNext();)
		{
			JRHyperlinkParameter parameter = it.next();
			if (parameter.getName() != null && parameter.getName().equals(parameterName))
			{
				it.remove();
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_HYPERLINK_PARAMETERS, 
						parameter, it.nextIndex());
			}
		}
	}

	
	/**
	 * Sets the expression which will be used to generate the hyperlink tooltip.
	 * The type of the expression should be <code>java.lang.String</code>.
	 * 
	 * @param hyperlinkTooltipExpression the expression which will be used to generate the hyperlink tooltip
	 * @see #getHyperlinkTooltipExpression()
	 */
	public void setHyperlinkTooltipExpression(JRExpression hyperlinkTooltipExpression)
	{
		Object old = this.hyperlinkTooltipExpression;
		this.hyperlinkTooltipExpression = hyperlinkTooltipExpression;
		getEventSupport().firePropertyChange(PROPERTY_HYPERLINK_TARGET, old, this.hyperlinkTooltipExpression);
	}
	
	@Override
	public Object clone()
	{
		JRDesignHyperlink clone = (JRDesignHyperlink)super.clone();
		clone.eventSupport = null;
		return clone;
	}

	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}
	
}
