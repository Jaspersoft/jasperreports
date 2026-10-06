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
package net.sf.jasperreports.engine.base;

import java.io.Serializable;
import java.util.UUID;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVisitable;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartComponentManager;
import net.sf.jasperreports.engine.part.PartComponentsEnvironment;
import net.sf.jasperreports.engine.part.PartEvaluationTime;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * A read-only {@link JRPart} implementation which is included
 * in compiled reports.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBasePart implements JRPart, Serializable, JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected UUID uuid;
	private JRPropertiesMap propertiesMap;

	private JRPropertyExpression[] propertyExpressions;

	protected JRExpression printWhenExpression;
	protected JRExpression partNameExpression;

	protected PartComponent component;
	
	protected PartEvaluationTime evaluationTime;
	
	protected JRBasePart()
	{
	}
	
	public JRBasePart(JRPart part, JRBaseObjectFactory factory)
	{
		factory.put(part, this);
		
		this.uuid = part.getUUID();
		this.propertiesMap = JRPropertiesMap.getPropertiesClone(part);
		this.propertyExpressions = factory.getPropertyExpressions(part.getPropertyExpressions());
		this.printWhenExpression = factory.getExpression(part.getPrintWhenExpression());
		this.partNameExpression = factory.getExpression(part.getPartNameExpression());
		this.evaluationTime = part.getEvaluationTime();
		
		PartComponent partComponent = part.getComponent();
		PartComponentManager manager = PartComponentsEnvironment.getInstance(DefaultJasperReportsContext.getInstance()).getManager(partComponent);
		component = manager.getComponentCompiler(DefaultJasperReportsContext.getInstance()).toCompiledComponent(
				partComponent, factory);

		if (component instanceof JRVisitable)
		{
			((JRVisitable) component).visit(factory);
		}
	}

	@Override
	public UUID getUUID()
	{
		if (uuid == null)
		{
			uuid = UUID.randomUUID();
		}
		return uuid;
	}

	@Override
	public PartComponent getComponent()
	{
		return component;
	}

	@Override
	public PartEvaluationTime getEvaluationTime()
	{
		return evaluationTime;
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return this.printWhenExpression;
	}

	@Override
	public JRExpression getPartNameExpression()
	{
		return partNameExpression;
	}

	@Override
	public boolean hasProperties()
	{
		return propertiesMap != null && propertiesMap.hasProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		if (propertiesMap == null)
		{
			propertiesMap = new JRPropertiesMap();
		}
		return propertiesMap;
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

	@Override
	public JRPropertyExpression[] getPropertyExpressions()
	{
		return propertyExpressions;
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

	@Override
	public Object clone() 
	{
		JRBasePart clone = null;
		
		try
		{
			clone = (JRBasePart)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.printWhenExpression = JRCloneUtils.nullSafeClone(printWhenExpression);
		clone.partNameExpression = JRCloneUtils.nullSafeClone(partNameExpression);
		clone.propertiesMap = JRPropertiesMap.getPropertiesClone(this);
		clone.propertyExpressions = JRCloneUtils.cloneArray(propertyExpressions);
		clone.uuid = null;
		clone.eventSupport = null;
		
		return clone;
	}
}
