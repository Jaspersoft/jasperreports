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
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.base.JRBaseBand;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignBand extends JRDesignElementGroup implements JRBand
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_HEIGHT = "height";

	public static final String PROPERTY_PRINT_WHEN_EXPRESSION = "printWhenExpression";
	
	public static final String PROPERTY_RETURN_VALUES = "returnValues";

	/**
	 *
	 */
	protected int height;
	protected SplitTypeEnum splitType;

	/**
	 *
	 */
	protected JRExpression printWhenExpression;
	
	private JROrigin origin;
	
	private JRPropertiesMap propertiesMap;
	protected List<ExpressionReturnValue> returnValues;

	/**
	 *
	 */
	public JRDesignBand()
	{
		returnValues = new ArrayList<>(2);
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	/**
	 *
	 */
	public void setHeight(int height)
	{
		int old = this.height;
		this.height = height;
		getEventSupport().firePropertyChange(PROPERTY_HEIGHT, old, this.height);
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
	
	/**
	 *
	 */
	public void setPrintWhenExpression(JRExpression expression)
	{
		Object old = this.printWhenExpression;
		this.printWhenExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_PRINT_WHEN_EXPRESSION, old, this.printWhenExpression);
	}

	/**
	 * Returns the band origin, i.e. its location/role within the report
	 * (e.g. detail/title/group header/etc).
	 * The location is automatically set when the band is inserted
	 * into the report (via one of the
	 * {@link JasperDesign#setTitle(JRBand) setTitle} /
	 * {@link JasperDesign#setPageHeader(JRBand) setPageHeader}
	 * methods).
	 * 
	 * @return the band origin
	 */
	@JsonIgnore
	public JROrigin getOrigin()
	{
		return origin;
	}

	void setOrigin(JROrigin origin)
	{
		this.origin = origin;
	}


	@Override
	public List<ExpressionReturnValue> getReturnValues()
	{
		return returnValues == null ? null : Collections.unmodifiableList(returnValues);
	}

	@JsonSetter
	private void setReturnValues(List<ExpressionReturnValue> returnValues)
	{
		if (returnValues != null)
		{
			for (ExpressionReturnValue returnValue : returnValues)
			{
				addReturnValue(returnValue);
			}
		}
	}

	/**
	 * Adds a return value to the band.
	 * 
	 * @param returnValue the return value to be added.
	 */
	public void addReturnValue(ExpressionReturnValue returnValue)
	{
		this.returnValues.add(returnValue);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_RETURN_VALUES, 
				returnValue, returnValues.size() - 1);
	}

	
	/**
	 * Returns the list of values to increment report variables with.
	 * 
	 * @return list of {@link ReturnValue ReturnValue} objects
	 */
	@JsonIgnore
	public List<ExpressionReturnValue> getReturnValuesList()
	{
		return returnValues;
	}

	
	/**
	 * Removes a return value from the band.
	 * 
	 * @param returnValue the return value to be removed
	 * @return <code>true</code> if the return value was found and removed 
	 */
	public boolean removeReturnValue(ExpressionReturnValue returnValue)
	{
		int idx = this.returnValues.indexOf(returnValue);
		if (idx >= 0)
		{
			this.returnValues.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_RETURN_VALUES, returnValue, idx);
			return true;
		}
		return false;
	}
	

	@Override
	public Object clone() 
	{
		JRDesignBand clone = (JRDesignBand)super.clone();
		if (printWhenExpression != null)
		{
			clone.printWhenExpression = (JRExpression)printWhenExpression.clone();
		}
		if (origin != null)
		{
			clone.origin = (JROrigin)origin.clone();
		}
		clone.propertiesMap = JRPropertiesMap.getPropertiesClone(this);
		clone.returnValues = JRCloneUtils.cloneList(returnValues);
		return clone;
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
