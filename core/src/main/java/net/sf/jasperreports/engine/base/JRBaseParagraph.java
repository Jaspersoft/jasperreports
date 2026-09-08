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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.Deduplicable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRParagraphContainer;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.LineSpacingEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.util.ObjectUtils;
import net.sf.jasperreports.engine.util.StyleResolver;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseParagraph implements JRParagraph, Serializable, Cloneable, JRChangeEventsSupport, Deduplicable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_LINE_SPACING = "lineSpacing";
	public static final String PROPERTY_LINE_SPACING_SIZE = "lineSpacingSize";
	public static final String PROPERTY_FIRST_LINE_INDENT = "firstLineIndent";
	public static final String PROPERTY_LEFT_INDENT = "leftIndent";
	public static final String PROPERTY_RIGHT_INDENT = "rightIndent";
	public static final String PROPERTY_SPACING_BEFORE = "spacingBefore";
	public static final String PROPERTY_SPACING_AFTER = "spacingAfter";
	public static final String PROPERTY_TAB_STOP_WIDTH = "tabStopWidth";
	public static final String PROPERTY_TAB_STOPS = "tabStops";


	protected JRParagraphContainer paragraphContainer;

	/**
	 *
	 */
	protected LineSpacingEnum lineSpacing;
	protected Float lineSpacingSize;
	protected Integer firstLineIndent;
	protected Integer leftIndent;
	protected Integer rightIndent;
	protected Integer spacingBefore;
	protected Integer spacingAfter;
	protected Integer tabStopWidth;
	protected List<TabStop> tabStopsList;

	
	@JsonCreator
	private JRBaseParagraph()
	{
		this(null);
	}
	
	
	/**
	 *
	 */
	public JRBaseParagraph(JRParagraphContainer paragraphContainer)
	{
		this.paragraphContainer = paragraphContainer;
	}
	
	
	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider() 
	{
		if (paragraphContainer != null)
		{
			return paragraphContainer.getDefaultStyleProvider();
		}
		return null;
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

	@Override
	public JRStyle getStyle() 
	{
		if (paragraphContainer != null)
		{
			return paragraphContainer.getStyle();
		}
		return null;
	}

	@Override
	public String getStyleNameReference()
	{
		if (paragraphContainer != null)
		{
			return paragraphContainer.getStyleNameReference();
		}
		return null;
	}

	@Override
	public JRParagraphContainer getParagraphContainer()
	{
		return paragraphContainer;
	}

	@Override
	public LineSpacingEnum getLineSpacing()
	{
		return getStyleResolver().getLineSpacing(this);
	}

	@Override
	public LineSpacingEnum getOwnLineSpacing()
	{
		return lineSpacing;
	}
	
	@Override
	public void setLineSpacing(LineSpacingEnum lineSpacing)
	{
		Object old = this.lineSpacing;
		this.lineSpacing = lineSpacing;
		getEventSupport().firePropertyChange(PROPERTY_LINE_SPACING, old, this.lineSpacing);
	}

	@Override
	public Float getLineSpacingSize()
	{
		return getStyleResolver().getLineSpacingSize(this);
	}

	@Override
	public Float getOwnLineSpacingSize()
	{
		return lineSpacingSize;
	}
	
	@Override
	public void setLineSpacingSize(Float lineSpacingSize)
	{
		Object old = this.lineSpacingSize;
		this.lineSpacingSize = lineSpacingSize;
		getEventSupport().firePropertyChange(PROPERTY_LINE_SPACING_SIZE, old, this.lineSpacingSize);
	}

	@Override
	public Integer getFirstLineIndent()
	{
		return getStyleResolver().getFirstLineIndent(this);
	}

	@Override
	public Integer getOwnFirstLineIndent()
	{
		return firstLineIndent;
	}
	
	@Override
	public void setFirstLineIndent(Integer firstLineIndent)
	{
		Object old = this.firstLineIndent;
		this.firstLineIndent = firstLineIndent;
		getEventSupport().firePropertyChange(PROPERTY_FIRST_LINE_INDENT, old, this.firstLineIndent);
	}

	@Override
	public Integer getLeftIndent()
	{
		return getStyleResolver().getLeftIndent(this);
	}

	@Override
	public Integer getOwnLeftIndent()
	{
		return leftIndent;
	}
	
	@Override
	public void setLeftIndent(Integer leftIndent)
	{
		Object old = this.leftIndent;
		this.leftIndent = leftIndent;
		getEventSupport().firePropertyChange(PROPERTY_LEFT_INDENT, old, this.leftIndent);
	}

	@Override
	public Integer getRightIndent()
	{
		return getStyleResolver().getRightIndent(this);
	}

	@Override
	public Integer getOwnRightIndent()
	{
		return rightIndent;
	}
	
	@Override
	public void setRightIndent(Integer rightIndent)
	{
		Object old = this.rightIndent;
		this.rightIndent = rightIndent;
		getEventSupport().firePropertyChange(PROPERTY_RIGHT_INDENT, old, this.rightIndent);
	}

	@Override
	public Integer getSpacingBefore()
	{
		return getStyleResolver().getSpacingBefore(this);
	}

	@Override
	public Integer getOwnSpacingBefore()
	{
		return spacingBefore;
	}
	
	@Override
	public void setSpacingBefore(Integer spacingBefore)
	{
		Object old = this.spacingBefore;
		this.spacingBefore = spacingBefore;
		getEventSupport().firePropertyChange(PROPERTY_SPACING_BEFORE, old, this.spacingBefore);
	}

	@Override
	public Integer getSpacingAfter()
	{
		return getStyleResolver().getSpacingAfter(this);
	}

	@Override
	public Integer getOwnSpacingAfter()
	{
		return spacingAfter;
	}
	
	@Override
	public void setSpacingAfter(Integer spacingAfter)
	{
		Object old = this.spacingAfter;
		this.spacingAfter = spacingAfter;
		getEventSupport().firePropertyChange(PROPERTY_SPACING_AFTER, old, this.spacingAfter);
	}

	@Override
	public Integer getTabStopWidth()
	{
		return getStyleResolver().getTabStopWidth(this);
	}

	@Override
	public Integer getOwnTabStopWidth()
	{
		return tabStopWidth;
	}
	
	@Override
	public void setTabStopWidth(Integer tabStopWidth)
	{
		Object old = this.tabStopWidth;
		this.tabStopWidth = tabStopWidth;
		getEventSupport().firePropertyChange(PROPERTY_TAB_STOP_WIDTH, old, this.tabStopWidth);
	}
	
	@Override
	public TabStop[] getTabStops()
	{
		return getStyleResolver().getTabStops(this);
	}
	
	
	@Override
	public TabStop[] getOwnTabStops()
	{
		if (tabStopsList == null || tabStopsList.size() == 0)
		{
			return null;
		}
		
		return tabStopsList.toArray(new TabStop[tabStopsList.size()]);
	}
	
	/*
	 * JACKSON-TIP
	 * The name of this setter is important here because it needs to match the name of the getter in the interface
	 * so that Jackson can match up json and xml field names from the annotations of that getter.
	 * A similar case exists with the includedTemplates field in the JRTemplate interface.
	 */
	@JsonSetter
	private void setOwnTabStops(List<TabStop> tabStops)
	{
		if (tabStops != null)
		{
			for (TabStop tabStop : tabStops)
			{
				addTabStop(tabStop);
			}
		}
	}
	
	@Override
	public void addTabStop(TabStop tabStop)
	{
		if (tabStopsList == null)
		{
			tabStopsList = new ArrayList<>();
		}
		
		tabStopsList.add(tabStop);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_TAB_STOPS, tabStop, tabStopsList.size() - 1);
	}
	
	@Override
	public void addTabStop(int index, TabStop tabStop)
	{
		if (tabStopsList == null)
		{
			tabStopsList = new ArrayList<>();
		}
		
		tabStopsList.add(index, tabStop);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_TAB_STOPS, tabStop, index);
	}
	
	@Override
	public void removeTabStop(TabStop tabStop)
	{
		if (tabStopsList != null)
		{
			int index = tabStopsList.indexOf(tabStop);
			if (index >= 0)
			{
				tabStopsList.remove(index);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_TAB_STOPS, tabStop, index);
			}
		}
	}
	
	@Override
	public void removeTabStop(int index)
	{
		if (tabStopsList != null)
		{
			if (index >= 0 && index < tabStopsList.size())
			{
				TabStop tabStop = tabStopsList.remove(index);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_TAB_STOPS, tabStop, index);
			}
		}
	}


	@Override
	public JRParagraph clone(JRParagraphContainer paragraphContainer)
	{
		JRBaseParagraph clone = null;
		
		try
		{
			clone = (JRBaseParagraph)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		clone.paragraphContainer = paragraphContainer;
		clone.tabStopsList = JRCloneUtils.cloneList(tabStopsList);
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
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		hash.add(lineSpacing);
		hash.add(lineSpacingSize);
		hash.add(firstLineIndent);
		hash.add(leftIndent);
		hash.add(rightIndent);
		hash.add(spacingBefore);
		hash.add(spacingAfter);
		hash.add(tabStopWidth);
		hash.addIdentical(tabStopsList);
		return hash.getHashCode();
	}


	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRBaseParagraph))
		{
			return false;
		}
		
		JRBaseParagraph para = (JRBaseParagraph) object;

		return 
				ObjectUtils.equals(lineSpacing, para.lineSpacing)
				&& ObjectUtils.equals(lineSpacingSize, para.lineSpacingSize)
				&& ObjectUtils.equals(firstLineIndent, para.firstLineIndent)
				&& ObjectUtils.equals(leftIndent, para.leftIndent)
				&& ObjectUtils.equals(rightIndent, para.rightIndent)
				&& ObjectUtils.equals(spacingBefore, para.spacingBefore)
				&& ObjectUtils.equals(spacingAfter, para.spacingAfter)
				&& ObjectUtils.equals(tabStopWidth, para.tabStopWidth)
				&& ObjectUtils.identical(tabStopsList, para.tabStopsList);
	}

	@Override
	public void populateStyle()
	{
		lineSpacing = getLineSpacing();
		lineSpacingSize = getLineSpacingSize();
		firstLineIndent = getFirstLineIndent();
		leftIndent = getLeftIndent();
		rightIndent = getRightIndent();
		spacingBefore = getSpacingBefore();
		spacingAfter = getSpacingAfter();
		
		TabStop[] tabStopArray = getTabStops();
		if (tabStopArray != null)
		{
			tabStopsList = new ArrayList<>(Arrays.asList(tabStopArray));
		}
	}

}
