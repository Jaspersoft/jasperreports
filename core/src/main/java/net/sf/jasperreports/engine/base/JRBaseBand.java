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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * Used for implementing band functionality. A report can contain the following bands: background, title,
 * summary, page header, page footer, last page footer, column header and column footer.
 * @see JRBaseSection
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseBand extends JRBaseElementGroup implements JRBand, JRChangeEventsSupport
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	public static final String PROPERTY_splitType = "splitType";

	/**
	 *
	 */
	protected int height;
	protected SplitTypeEnum splitType;

	/**
	 *
	 */
	protected JRExpression printWhenExpression;
	
	private JRPropertiesMap propertiesMap;
	protected List<ExpressionReturnValue> returnValues;
	

	/**
	 *
	 */
	protected JRBaseBand(JRBand band, JRBaseObjectFactory factory)
	{
		super(band, factory);
		
		height = band.getHeight();
		splitType = band.getSplitType();

		printWhenExpression = factory.getExpression(band.getPrintWhenExpression());
		this.propertiesMap = JRPropertiesMap.getPropertiesClone(band);

		List<ExpressionReturnValue> bandReturnValues = band.getReturnValues();
		if (bandReturnValues != null && !bandReturnValues.isEmpty())
		{
			this.returnValues = new ArrayList<>(bandReturnValues.size());
			for (ExpressionReturnValue bandReturnValue : bandReturnValues)
			{
				BaseExpressionReturnValue returnValue = factory.getReturnValue(bandReturnValue);
				this.returnValues.add(returnValue);
			}
		}
	}
		

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public SplitTypeEnum getSplitType()
	{
		return splitType;
	}

	@Override
	public void setSplitType(SplitTypeEnum splitType)
	{
		SplitTypeEnum old = this.splitType;
		this.splitType = splitType;
		getEventSupport().firePropertyChange(JRBaseBand.PROPERTY_splitType, old, this.splitType);
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return this.printWhenExpression;
	}

	@Override
	public List<ExpressionReturnValue> getReturnValues()
	{
		return returnValues == null ? null : Collections.unmodifiableList(returnValues);
	}

	@Override
	public Object clone() 
	{
		JRBaseBand clone = (JRBaseBand)super.clone();
		clone.printWhenExpression = JRCloneUtils.nullSafeClone(printWhenExpression);
		clone.propertiesMap = JRPropertiesMap.getPropertiesClone(this);
		clone.returnValues = JRCloneUtils.cloneList(returnValues);
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
}
