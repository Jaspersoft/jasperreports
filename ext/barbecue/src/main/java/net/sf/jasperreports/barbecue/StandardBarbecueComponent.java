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
package net.sf.jasperreports.barbecue;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.BaseComponentContext;
import net.sf.jasperreports.engine.component.ComponentContext;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardBarbecueComponent implements BarbecueComponent, Serializable, JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_CODE_EXPRESSION = "codeExpression";
	public static final String PROPERTY_APPLICATION_IDENTIFIER_EXPRESSION = "applicationIdentifierExpression";
	public static final String PROPERTY_DRAW_TEXT = "drawText";
	public static final String PROPERTY_CHECKSUM_REQUIRED = "checksumRequired";
	public static final String PROPERTY_BAR_WIDTH = "barWidth";
	public static final String PROPERTY_BAR_HEIGTH = "barHeight";
	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";
	public static final String PROPERTY_EVALUATION_GROUP = "evaluationGroup";
	public static final String PROPERTY_ROTATION = "rotation";

	private String type;
	private JRExpression codeExpression;
	private JRExpression applicationIdentifierExpression;
	private boolean drawText;
	private boolean checksumRequired;
	private Integer barWidth;
	private Integer barHeight;
	
	private RotationEnum rotation;
	
	private EvaluationTimeEnum evaluationTime;
	private String evaluationGroup;
	
	private ComponentContext context;

	private transient JRPropertyChangeSupport eventSupport;

	public StandardBarbecueComponent()
	{
	}

	public StandardBarbecueComponent(BarbecueComponent barcode, JRBaseObjectFactory objectFactory)
	{
		this.type = barcode.getType();
		this.codeExpression = objectFactory.getExpression(
				barcode.getCodeExpression());
		this.applicationIdentifierExpression = objectFactory.getExpression(
				barcode.getApplicationIdentifierExpression());
		this.drawText = barcode.isDrawText();
		this.checksumRequired = barcode.isChecksumRequired();
		this.barWidth = barcode.getBarWidth();
		this.barHeight = barcode.getBarHeight();
		this.evaluationTime= barcode.getEvaluationTime();
		this.evaluationGroup = barcode.getEvaluationGroup();
		this.rotation = barcode.getOwnRotation();
		
		this.context = new BaseComponentContext(barcode.getContext(), objectFactory);
	}
	
	@Override
	public void setContext(ComponentContext context)
	{
		this.context = context;
	}

	@Override
	public ComponentContext getContext()
	{
		return context;
	}

	@Override
	public JRExpression getCodeExpression()
	{
		return codeExpression;
	}

	public void setCodeExpression(JRExpression codeExpression)
	{
		Object old = this.codeExpression;
		this.codeExpression = codeExpression;
		getEventSupport().firePropertyChange(PROPERTY_CODE_EXPRESSION, 
				old, this.codeExpression);
	}

	@Override
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		Object old = this.type;
		this.type = type;
		getEventSupport().firePropertyChange(PROPERTY_TYPE, 
				old, this.type);
	}

	@Override
	public JRExpression getApplicationIdentifierExpression()
	{
		return applicationIdentifierExpression;
	}

	public void setApplicationIdentifierExpression(
			JRExpression applicationIdentifierExpression)
	{
		Object old = this.applicationIdentifierExpression;
		this.applicationIdentifierExpression = applicationIdentifierExpression;
		getEventSupport().firePropertyChange(PROPERTY_APPLICATION_IDENTIFIER_EXPRESSION, 
				old, this.applicationIdentifierExpression);
	}

	@Override
	public Integer getBarWidth()
	{
		return barWidth;
	}

	public void setBarWidth(Integer barWidth)
	{
		Object old = this.barWidth;
		this.barWidth = barWidth;
		getEventSupport().firePropertyChange(PROPERTY_BAR_WIDTH, 
				old, this.barWidth);
	}

	@Override
	public Integer getBarHeight()
	{
		return barHeight;
	}

	public void setBarHeight(Integer barHeight)
	{
		Object old = this.barHeight;
		this.barHeight = barHeight;
		getEventSupport().firePropertyChange(PROPERTY_BAR_HEIGTH, 
				old, this.barHeight);
	}
	
	@Override
	public RotationEnum getRotation()
	{
		return BarbecueStyleResolver.getRotation(getContext().getComponentElement());
	}
	
	@Override
	public RotationEnum getOwnRotation()
	{
		return rotation;
	}
	
	@JsonSetter
	public void setRotation(RotationEnum rotation)
	{
		Object old = this.rotation;
		this.rotation = rotation;
		getEventSupport().firePropertyChange(PROPERTY_ROTATION, 
				old, this.rotation);
	}

	@Override
	public boolean isChecksumRequired()
	{
		return checksumRequired;
	}

	public void setChecksumRequired(boolean checksumRequired)
	{
		boolean old = this.checksumRequired;
		this.checksumRequired = checksumRequired;
		getEventSupport().firePropertyChange(PROPERTY_CHECKSUM_REQUIRED, 
				old, this.checksumRequired);
	}

	@Override
	public boolean isDrawText()
	{
		return drawText;
	}

	public void setDrawText(boolean drawText)
	{
		boolean old = this.drawText;
		this.drawText = drawText;
		getEventSupport().firePropertyChange(PROPERTY_DRAW_TEXT, 
				old, this.drawText);
	}
	
	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}

	public void setEvaluationTime(EvaluationTimeEnum evaluationTime)
	{
		Object old = this.evaluationTime;
		this.evaluationTime = evaluationTime;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME, 
				old, this.evaluationTime);
	}

	@Override
	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}

	public void setEvaluationGroup(String evaluationGroup)
	{
		Object old = this.evaluationGroup;
		this.evaluationGroup = evaluationGroup;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_GROUP, 
				old, this.evaluationGroup);
	}
	
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
	
	@Override
	public Object clone()
	{
		StandardBarbecueComponent clone = null;
		try
		{
			clone = (StandardBarbecueComponent) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.codeExpression = JRCloneUtils.nullSafeClone(codeExpression);
		clone.applicationIdentifierExpression = JRCloneUtils.nullSafeClone(applicationIdentifierExpression);
		//FIXMENOW should context be cloned?
		clone.eventSupport = null;
		return clone;
	}
}
