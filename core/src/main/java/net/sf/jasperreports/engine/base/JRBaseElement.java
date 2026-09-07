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

import java.awt.Color;
import java.io.Serializable;
import java.util.UUID;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.util.StyleResolver;


/**
 * This class provides a skeleton implementation for a report element. It mostly provides internal variables, representing
 * the most common element properties, and their getter/setter methods. It also has a constructor for initializing
 * these properties.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRBaseElement implements JRElement, Serializable, JRChangeEventsSupport
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_POSITION_TYPE = "positionType";

	public static final String PROPERTY_PRINT_IN_FIRST_WHOLE_BAND = "isPrintInFirstWholeBand";

	public static final String PROPERTY_PRINT_REPEATED_VALUES = "isPrintRepeatedValues";

	public static final String PROPERTY_PRINT_WHEN_DETAIL_OVERFLOWS = "isPrintWhenDetailOverflows";

	public static final String PROPERTY_REMOVE_LINE_WHEN_BLANK = "isRemoveLineWhenBlank";

	public static final String PROPERTY_STRETCH_TYPE = "stretchType";

	public static final String PROPERTY_WIDTH = "width";

	public static final String PROPERTY_X = "x";

	/**
	 *
	 */
	protected UUID uuid;
	protected String key;
	protected PositionTypeEnum positionType;
	protected StretchTypeEnum stretchType;
	protected boolean isPrintRepeatedValues = true; // primitive boolean field can't be null; custom BooleanTrueAsEmptySerializer used 
	protected ModeEnum mode;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected boolean isRemoveLineWhenBlank;
	protected boolean isPrintInFirstWholeBand;
	protected boolean isPrintWhenDetailOverflows;
	protected Color forecolor;
	protected Color backcolor;

	/**
	 *
	 */
	protected JRExpression printWhenExpression;
	protected String printWhenGroupChanges;
	protected JRElementGroup elementGroup;

	protected final JRDefaultStyleProvider defaultStyleProvider;
	protected JRStyle parentStyle;
	protected String parentStyleNameReference;
	protected JRExpression styleExpression;

	private JRPropertiesMap propertiesMap;

	private JRPropertyExpression[] propertyExpressions;
	
	/**
	 *
	 */
	protected JRBaseElement(JRDefaultStyleProvider defaultStyleProvider)
	{
		this.defaultStyleProvider = defaultStyleProvider;
	}


	/**
	 * Initializes basic properties of the element.
	 * @param element an element whose properties are copied to this element. Usually it is a
	 * {@link net.sf.jasperreports.engine.design.JRDesignElement} that must be transformed into an
	 * <tt>JRBaseElement</tt> at compile time.
	 * @param factory a factory used in the compile process
	 */
	protected JRBaseElement(JRElement element, JRBaseObjectFactory factory)
	{
		factory.put(element, this);

		defaultStyleProvider = factory.getDefaultStyleProvider();

		parentStyle = factory.getStyle(element.getStyle());
		parentStyleNameReference = element.getStyleNameReference();
		styleExpression = factory.getExpression(element.getStyleExpression());

		uuid = element.getUUID();
		key = element.getKey();
		positionType = element.getPositionType();
		stretchType = element.getStretchType();
		isPrintRepeatedValues = element.isPrintRepeatedValues();
		mode = element.getOwnMode();
		x = element.getX();
		y = element.getY();
		width = element.getWidth();
		height = element.getHeight();
		isRemoveLineWhenBlank = element.isRemoveLineWhenBlank();
		isPrintInFirstWholeBand = element.isPrintInFirstWholeBand();
		isPrintWhenDetailOverflows = element.isPrintWhenDetailOverflows();
		forecolor = element.getOwnForecolor();
		backcolor = element.getOwnBackcolor();

		printWhenExpression = factory.getExpression(element.getPrintWhenExpression());
		printWhenGroupChanges = element.getPrintWhenGroupChanges();
		elementGroup = (JRElementGroup)factory.getVisitResult(element.getElementGroup());
		
		propertiesMap = JRPropertiesMap.getPropertiesClone(element);
		propertyExpressions = factory.getPropertyExpressions(element.getPropertyExpressions());
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
		if (getDefaultStyleProvider() != null)
		{
			return getDefaultStyleProvider().getStyleResolver();
		}
		return StyleResolver.getInstance();
	}

	/**
	 *
	 */
	protected JRStyle getBaseStyle()
	{
		if (parentStyle != null)
		{
			return parentStyle;
		}
		if (defaultStyleProvider != null)
		{
			return defaultStyleProvider.getDefaultStyle();
		}
		return null;
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

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}
		
	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public PositionTypeEnum getPositionType()
	{
		return positionType;
	}

	@Override
	public void setPositionType(PositionTypeEnum positionType)
	{
		PositionTypeEnum old = this.positionType;
		this.positionType = positionType;
		getEventSupport().firePropertyChange(PROPERTY_POSITION_TYPE, old, this.positionType);
	}

	@Override
	public StretchTypeEnum getStretchType()
	{
		return stretchType;
	}

	@Override
	public void setStretchType(StretchTypeEnum stretchType)
	{
		StretchTypeEnum old = this.stretchType;
		this.stretchType = stretchType;
		getEventSupport().firePropertyChange(PROPERTY_STRETCH_TYPE, old, this.stretchType);
	}

	@Override
	public boolean isPrintRepeatedValues()
	{
		return this.isPrintRepeatedValues;
	}

	@Override
	public void setPrintRepeatedValues(boolean isPrintRepeatedValues)
	{
		boolean old = this.isPrintRepeatedValues;
		this.isPrintRepeatedValues = isPrintRepeatedValues;
		getEventSupport().firePropertyChange(PROPERTY_PRINT_REPEATED_VALUES, old, this.isPrintRepeatedValues);
	}

	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.OPAQUE);
	}

	@Override
	public ModeEnum getOwnMode()
	{
		return mode;
	}

	@Override
	public void setMode(ModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_MODE, old, this.mode);
	}

	@Override
	public int getX()
	{
		return this.x;
	}

	@Override
	public void setX(int x)
	{
		int old = this.x;
		this.x = x;
		getEventSupport().firePropertyChange(PROPERTY_X, old, this.x);
	}

	@Override
	public int getY()
	{
		return this.y;
	}

	@Override
	public int getWidth()
	{
		return this.width;
	}

	@Override
	public void setWidth(int width)
	{
		int old = this.width;
		this.width = width;
		getEventSupport().firePropertyChange(PROPERTY_WIDTH, old, this.width);
	}

	@Override
	public int getHeight()
	{
		return this.height;
	}

	@Override
	public boolean isRemoveLineWhenBlank()
	{
		return this.isRemoveLineWhenBlank;
	}

	@Override
	public void setRemoveLineWhenBlank(boolean isRemoveLine)
	{
		boolean old = this.isRemoveLineWhenBlank;
		this.isRemoveLineWhenBlank = isRemoveLine;
		getEventSupport().firePropertyChange(PROPERTY_REMOVE_LINE_WHEN_BLANK, old, this.isRemoveLineWhenBlank);
	}

	@Override
	public boolean isPrintInFirstWholeBand()
	{
		return this.isPrintInFirstWholeBand;
	}

	@Override
	public void setPrintInFirstWholeBand(boolean isPrint)
	{
		boolean old = this.isPrintInFirstWholeBand;
		this.isPrintInFirstWholeBand = isPrint;
		getEventSupport().firePropertyChange(PROPERTY_PRINT_IN_FIRST_WHOLE_BAND, old, this.isPrintInFirstWholeBand);
	}

	@Override
	public boolean isPrintWhenDetailOverflows()
	{
		return this.isPrintWhenDetailOverflows;
	}

	@Override
	public void setPrintWhenDetailOverflows(boolean isPrint)
	{
		boolean old = this.isPrintWhenDetailOverflows;
		this.isPrintWhenDetailOverflows = isPrint;
		getEventSupport().firePropertyChange(PROPERTY_PRINT_WHEN_DETAIL_OVERFLOWS, old, this.isPrintWhenDetailOverflows);
	}

	@Override
	public Color getForecolor()
	{
		return getStyleResolver().getForecolor(this);
	}

	@Override
	public Color getOwnForecolor()
	{
		return forecolor;
	}

	@Override
	public void setForecolor(Color forecolor)
	{
		Object old = this.forecolor;
		this.forecolor = forecolor;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_FORECOLOR, old, this.forecolor);
	}

	@Override
	public Color getBackcolor()
	{
		return getStyleResolver().getBackcolor(this);
	}

	@Override
	public Color getOwnBackcolor()
	{
		return backcolor;
	}

	@Override
	public void setBackcolor(Color backcolor)
	{
		Object old = this.backcolor;
		this.backcolor = backcolor;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_BACKCOLOR, old, this.backcolor);
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return this.printWhenExpression;
	}

	@Override
	public String getPrintWhenGroupChanges()
	{
		return this.printWhenGroupChanges;
	}

	@Override
	public JRElementGroup getElementGroup()
	{
		return this.elementGroup;
	}

	@Override
	public JRStyle getStyle()
	{
		return parentStyle;
	}

	@Override
	public String getStyleNameReference()
	{
		return parentStyleNameReference;
	}
	
	@Override
	public JRExpression getStyleExpression()
	{
		return this.styleExpression;
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
		JRBaseElement clone = null;
		
		try
		{
			clone = (JRBaseElement)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.printWhenExpression = JRCloneUtils.nullSafeClone(printWhenExpression);
		clone.propertiesMap = JRPropertiesMap.getPropertiesClone(this);
		clone.propertyExpressions = JRCloneUtils.cloneArray(propertyExpressions);
		clone.eventSupport = null;
		
		return clone;
	}

	@Override
	public Object clone(JRElementGroup parentGroup) 
	{
		JRBaseElement clone = (JRBaseElement)this.clone();
		
		clone.elementGroup = parentGroup;
		
		return clone;
	}

	@Override
	public JRElement clone(JRElementGroup parentGroup, int y)
	{
		JRBaseElement clone = (JRBaseElement) clone(parentGroup);
		clone.y = y;
		return clone;
	}

	@Override
	public boolean hasProperties()
	{
		// checking for empty properties here instead of hasProperties because
		// table components create elements with dynamic base properties
		return propertiesMap != null && !propertiesMap.isEmpty();
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
}
