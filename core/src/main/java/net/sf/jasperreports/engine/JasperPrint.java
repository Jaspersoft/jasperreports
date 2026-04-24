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


/*
 * Contributors:
 * John Bindel - jbindel@users.sourceforge.net 
 */

package net.sf.jasperreports.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.base.StandardPrintParts;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * An instance of this class represents a page-oriented document
 * that can be viewed, printed or exported to other formats.
 * <p>
 * When filling report designs with data, the engine produces instances
 * of this class and these can be transferred over the network,
 * stored in a serialized form on disk or exported to various
 * other formats like PDF, HTML, XLS, CSV or XML.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JasperPrint implements Serializable, JRPropertiesHolder, JRChangeEventsSupport
{
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_STYLE = "engine.jasper.print.duplicate.style";
	
	/**
	 * Prefix for JasperReports properties that specify properties to be
	 * transferred from report templates to print objects.
	 * 
	 * @see JRPropertiesUtil#transferProperties(JRPropertiesHolder, JRPropertiesHolder, String)
	 */
	@Property(
			name = "net.sf.jasperreports.print.transfer.{arbitrary_suffix}",
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public static final String PROPERTIES_PRINT_TRANSFER_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "print.transfer.";

	/**
	 * 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_5_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CREATE_BOOKMARKS = JRPropertiesUtil.PROPERTY_PREFIX + "print.create.bookmarks";

	/**
	 * 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_5_5_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_COLLAPSE_MISSING_BOOKMARK_LEVELS = 
		JRPropertiesUtil.PROPERTY_PREFIX + "print.collapse.missing.bookmark.levels";
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_PAGE_WIDTH = "pageWidth";
	public static final String PROPERTY_PAGE_HEIGHT = "pageHeight";
	public static final String PROPERTY_TOP_MARGIN = "topMargin";
	public static final String PROPERTY_LEFT_MARGIN = "leftMargin";
	public static final String PROPERTY_BOTTOM_MARGIN = "bottomMargin";
	public static final String PROPERTY_RIGHT_MARGIN = "rightMargin";
	public static final String PROPERTY_ORIENTATION = "orientation";
	public static final String PROPERTY_STYLES = "styles";
	public static final String PROPERTY_ORIGINS = "origins";
	public static final String PROPERTY_PARTS = "parts";
	public static final String PROPERTY_PAGES = "pages";
	public static final String PROPERTY_BOOKMARKS = "bookmarks";
	public static final String PROPERTY_FORMAT_FACTORY_CLASS = "formatFactoryClass";
	public static final String PROPERTY_LOCALE_CODE = "localeCode";
	public static final String PROPERTY_TIME_ZONE_ID = "timeZoneId";
	
	/**
	 * A small class for implementing just the style provider functionality.
	 */
	private static class DefaultStyleProvider implements JRDefaultStyleProvider, Serializable
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
		
		private JRStyle defaultStyle;
		protected transient StyleResolver styleResolver = StyleResolver.getInstance();

		DefaultStyleProvider(JRStyle style)
		{
			this.defaultStyle = style;
		}

		@Override
		public JRStyle getDefaultStyle()
		{
			return defaultStyle;
		}

		void setDefaultStyle(JRStyle style)
		{
			this.defaultStyle = style;
		}

		public synchronized void setJasperReportsContext(JasperReportsContext jasperReportsContext)
		{
			styleResolver = new StyleResolver(jasperReportsContext);
		}

		@Override
		public StyleResolver getStyleResolver()
		{
			return styleResolver;
		}

		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
		{
			in.defaultReadObject();
			styleResolver = StyleResolver.getInstance();
		}
	}


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	private String name;
	private int pageWidth;
	private int pageHeight;
	private Integer topMargin = 0;
	private Integer leftMargin = 0;
	private Integer bottomMargin = 0;
	private Integer rightMargin = 0;
	private OrientationEnum orientation;
	
	private transient PrintPageFormat pageFormat;

	private class DefaultPrintPageFormat implements PrintPageFormat
	{
		@Override
		public Integer getPageWidth() {
			return JasperPrint.this.getPageWidth();
		}

		@Override
		public Integer getPageHeight() {
			return JasperPrint.this.getPageHeight();
		}

		@Override
		public Integer getTopMargin() {
			return JasperPrint.this.getTopMargin();
		}

		@Override
		public Integer getLeftMargin() {
			return JasperPrint.this.getLeftMargin();
		}

		@Override
		public Integer getBottomMargin() {
			return JasperPrint.this.getBottomMargin();
		}

		@Override
		public Integer getRightMargin() {
			return JasperPrint.this.getRightMargin();
		}

		@Override
		public OrientationEnum getOrientation() {
			return JasperPrint.this.getOrientation();
		}
	}

	private Map<String, JRStyle> stylesMap = new HashMap<>();
	private List<JRStyle> stylesList = new ArrayList<>();
	private Map<JROrigin, Integer> originsMap = new HashMap<>();
	private List<JROrigin> originsList = new ArrayList<>();

	private PrintParts parts;
	//FIXME unsynchronize on serialization?
	private List<JRPrintPage> pages;

	private transient Map<String,JRPrintAnchorIndex> anchorIndexes;
	private DefaultStyleProvider defaultStyleProvider;
	
	private String formatFactoryClass;
	private String localeCode;
	private String timeZoneId;
	
	private JRPropertiesMap propertiesMap;

	private List<PrintBookmark> bookmarks;
	

	/**
	 * Creates a new empty document. 
	 */
	public JasperPrint()
	{
		this(new ArrayList<>());
	}

	protected JasperPrint(List<JRPrintPage> pages)
	{
		defaultStyleProvider = new DefaultStyleProvider(null);

		propertiesMap = new JRPropertiesMap();
		
		this.pages = Collections.synchronizedList(pages);
	}

	/**
	 *
	 */
	public synchronized void setJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		defaultStyleProvider.setJasperReportsContext(jasperReportsContext);
	}

	/**
	 * @return Returns the name of the document
	 */
	public String getName()
	{
		return name;
	}
		
	/**
	 * Sets the name of the document.
	 * 
	 * @param name name of the document
	 */
	public void setName(String name)
	{
		Object old = this.name;
		this.name = name;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
		}
	}

	/**
	 * @return Returns the page format for specified page index.
	 */
	public PrintPageFormat getPageFormat(int pageIndex)
	{
		if (parts == null || !parts.hasParts())
		{
			return getPageFormat();
		}
		else
		{
			PrintPageFormat partPageFormat = parts.getPageFormat(pageIndex);
			if (partPageFormat == null)
			{
				return getPageFormat();
			}
			else
			{
				return partPageFormat;
			}
		}
	}

	/**
	 *
	 */
	public synchronized PrintPageFormat getPageFormat()
	{
		if (pageFormat == null)
		{
			pageFormat = new DefaultPrintPageFormat();
		}
		return pageFormat;
	}
		
	/**
	 * @return Returns the page width
	 */
	public int getPageWidth()
	{
		return pageWidth;
	}
		
	/**
	 * Sets the page width.
	 * 
	 * @param pageWidth page width
	 */
	public void setPageWidth(int pageWidth)
	{
		Object old = this.pageWidth;
		this.pageWidth = pageWidth;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_PAGE_WIDTH, old, this.pageWidth);
		}
	}

	/**
	 * @return Returns the page height.
	 */
	public int getPageHeight()
	{
		return pageHeight;
	}
		
	/**
	 * Sets the page height.
	 * 
	 * @param pageHeight page height
	 */
	public void setPageHeight(int pageHeight)
	{
		Object old = this.pageHeight;
		this.pageHeight = pageHeight;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_PAGE_HEIGHT, old, this.pageHeight);
		}
	}

	/**
	 * @return Returns the top page margin
	 */
	public Integer getTopMargin()
	{
		return topMargin;
	}
		
	/**
	 * Sets the top page margin.
	 * 
	 * @param topMargin top page margin
	 */
	public void setTopMargin(Integer topMargin)
	{
		Object old = this.topMargin;
		this.topMargin = topMargin;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_TOP_MARGIN, old, this.topMargin);
		}
	}

	/**
	 * @return Returns the left page margin
	 */
	public Integer getLeftMargin()
	{
		return leftMargin;
	}
		
	/**
	 * Sets the left page margin.
	 * 
	 * @param leftMargin left page margin
	 */
	public void setLeftMargin(Integer leftMargin)
	{
		Object old = this.leftMargin;
		this.leftMargin = leftMargin;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_LEFT_MARGIN, old, this.leftMargin);
		}
	}

	/**
	 * @return Returns the bottom page margin
	 */
	public Integer getBottomMargin()
	{
		return bottomMargin;
	}
		
	/**
	 * Sets the bottom page margin.
	 * 
	 * @param bottomMargin bottom page margin
	 */
	public void setBottomMargin(Integer bottomMargin)
	{
		Object old = this.bottomMargin;
		this.bottomMargin = bottomMargin;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_BOTTOM_MARGIN, old, this.bottomMargin);
		}
	}

	/**
	 * @return Returns the right page margin
	 */
	public Integer getRightMargin()
	{
		return rightMargin;
	}
		
	/**
	 * Sets the right page margin.
	 * 
	 * @param rightMargin right page margin
	 */
	public void setRightMargin(Integer rightMargin)
	{
		Object old = this.rightMargin;
		this.rightMargin = rightMargin;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_RIGHT_MARGIN, old, this.rightMargin);
		}
	}

	/**
	 * Returns the page orientation.
	 * @see OrientationEnum PORTRAIT,
	 * @see OrientationEnum LANDSCAPE
	 */
	public OrientationEnum getOrientation()
	{
		return orientation;
	}
		
	/**
	 * Sets the page orientation.
	 * @see OrientationEnum PORTRAIT,
	 * @see OrientationEnum LANDSCAPE
	 */
	public void setOrientation(OrientationEnum orientation)
	{
		Object old = this.orientation;
		this.orientation = orientation;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_ORIENTATION, old, this.orientation);
		}
	}

	@Override
	public boolean hasProperties()
	{
		return propertiesMap != null && propertiesMap.hasProperties();
	}
	
	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return propertiesMap;
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}
	
	/**
	 *
	 */
	public String[] getPropertyNames()
	{
		return propertiesMap.getPropertyNames();
	}

	/**
	 *
	 */
	public String getProperty(String propName)
	{
		return propertiesMap.getProperty(propName);
	}

	/**
	 *
	 */
	public void setProperty(String propName, String value)
	{
		propertiesMap.setProperty(propName, value);
	}

	/**
	 *
	 */
	public void removeProperty(String propName)
	{
		propertiesMap.removeProperty(propName);
	}

	/**
	 * Returns the default report style.
	 */
	public JRStyle getDefaultStyle()
	{
		return defaultStyleProvider.getDefaultStyle();
	}

	/**
	 * Sets the default report style.
	 */
	public synchronized void setDefaultStyle(JRStyle style)
	{
		defaultStyleProvider.setDefaultStyle(style);
	}

	/**
	 * When we want to virtualize pages, we want a style provider that
	 * is <i>not</i> the print object itself.
	 */
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return defaultStyleProvider;
	}
		
	/**
	 * Gets an array of report styles.
	 */
	public JRStyle[] getStyles()
	{
		JRStyle[] stylesArray = new JRStyle[stylesList.size()];
		
		stylesList.toArray(stylesArray);

		return stylesArray;
	}

	/**
	 * Gets a list of report styles.
	 */
	public List<JRStyle> getStylesList()
	{
		return stylesList;
	}

	/**
	 * Gets a map of report styles.
	 */
	public Map<String, JRStyle> getStylesMap()
	{
		return stylesMap;
	}

	/**
	 * Adds a new style to the report styles.
	 */
	public synchronized void addStyle(JRStyle style) throws JRException
	{
		addStyle(style, false);
	}

	/**
	 * Adds a new style to the report styles.
	 */
	public synchronized void addStyle(JRStyle style, boolean isIgnoreDuplicate) throws JRException
	{
		if (stylesMap.containsKey(style.getName()))
		{
			if (!isIgnoreDuplicate)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_DUPLICATE_STYLE,
						new Object[]{style.getName()});
			}
		}
		else
		{
			stylesList.add(style);
			stylesMap.put(style.getName(), style);
			
			if (style.isDefault())
			{
				setDefaultStyle(style);
			}
			
			if (hasEventSupport())
			{
				getEventSupport().fireCollectionElementAddedEvent(PROPERTY_STYLES, style, stylesList.size() - 1);
			}
		}
	}

	/**
	 *
	 */
	public synchronized JRStyle removeStyle(String styleName)
	{
		return removeStyle(stylesMap.get(styleName));
	}

	/**
	 *
	 */
	public synchronized JRStyle removeStyle(JRStyle style)
	{
		if (style != null)
		{
			if (style.isDefault())
			{
				setDefaultStyle(null);
			}

			boolean removed = stylesList.remove(style);
			stylesMap.remove(style.getName());
			if (removed && hasEventSupport())
			{
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_STYLES, style, -1);//FIXME index
			}
		}
		
		return style;
	}

	/**
	 * Gets an array of report origins.
	 */
	public JROrigin[] getOrigins()
	{
		return originsList.toArray(new JROrigin[originsList.size()]);
	}

	/**
	 * Gets a list of report origins.
	 */
	public List<JROrigin> getOriginsList()
	{
		return originsList;
	}

	/**
	 * Gets a map of report origins.
	 */
	public Map<JROrigin, Integer> getOriginsMap()
	{
		return originsMap;
	}

	/**
	 * Adds a new style to the report origins.
	 */
	public synchronized void addOrigin(JROrigin origin)
	{
		if (!originsMap.containsKey(origin))
		{
			originsList.add(origin);
			originsMap.put(origin, originsList.size() - 1);
			
			if (hasEventSupport())
			{
				getEventSupport().fireCollectionElementAddedEvent(PROPERTY_ORIGINS, origin, originsList.size() - 1);
			}
		}
	}

	/**
	 *
	 */
	public synchronized JROrigin removeOrigin(JROrigin origin)
	{
		if (originsMap.containsKey(origin))
		{
			originsList.remove(origin);
			originsMap = new HashMap<>();
			for (int i = 0; i < originsList.size(); i++)
			{
				originsMap.put(originsList.get(i), i);
			}
			
			if (hasEventSupport())
			{
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_ORIGINS, origin, -1);//FIXME index
			}
		}
		
		return origin;
	}

	/**
	 * Determines whether this document contains parts.
	 * 
	 * @return whether this document contains parts
	 * @see #getParts()
	 */
	public boolean hasParts()
	{
		return parts != null && parts.hasParts();
	}
	
	/**
	 * Returns a list of all parts in the filled report.
	 */
	public PrintParts getParts()
	{
		return parts;
	}

	/**
	 * Adds a new part to the document.
	 */
	public synchronized void addPart(int pageIndex, PrintPart part)
	{
		if (parts == null)
		{
			parts = new StandardPrintParts();
		}

		parts.addPart(pageIndex, part);
		
		if (hasEventSupport())
		{
			getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PARTS, part, pageIndex);
		}
	}

	/**
	 * Removes a part from the document.
	 */
	public synchronized PrintPart removePart(int pageIndex)
	{
		if (parts == null)
		{
			return null;
		}

		PrintPart part = parts.removePart(pageIndex);
		
		if (part != null && hasEventSupport())
		{
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PARTS, part, pageIndex);
		}
		
		return part;
	}

	/**
	 * Returns a list of all pages in the filled report.
	 */
	public List<JRPrintPage> getPages()
	{
		return pages;
	}

	/**
	 * Adds a new page to the document.
	 */
	public synchronized void addPage(JRPrintPage page)
	{
		anchorIndexes = null;
		pages.add(page);
		
		if (hasEventSupport())
		{
			getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PAGES, page, pages.size() - 1);
		}
	}

	/**
	 * Adds a new page to the document, placing it at the specified index.
	 */
	public synchronized void addPage(int index, JRPrintPage page)
	{
		anchorIndexes = null;
		pages.add(index, page);
		
		if (hasEventSupport())
		{
			getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PAGES, page, index);
		}
	}

	/**
	 * Removes a page from the document.
	 */
	public synchronized JRPrintPage removePage(int index)
	{
		anchorIndexes = null;
		JRPrintPage page = pages.remove(index);
		
		if (page != null && hasEventSupport())
		{
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PAGES, page, index);
		}
		
		return page;
	}

	/**
	 *
	 */
	public List<PrintBookmark> getBookmarks()
	{
		return bookmarks;
	}

	/**
	 * Adds a new page to the document.
	 */
	public synchronized void addBookmark(PrintBookmark bookmark)
	{
		if (bookmarks == null)
		{
			bookmarks = new ArrayList<>();
		}
		bookmarks.add(bookmark);
		
		if (hasEventSupport())
		{
			getEventSupport().fireCollectionElementAddedEvent(PROPERTY_BOOKMARKS, bookmark, bookmarks.size() - 1);
		}
	}
	
	/**
	 *
	 */
	public void setBookmarks(List<PrintBookmark> bookmarks)
	{
		Object old = this.bookmarks;
		this.bookmarks = bookmarks;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_BOOKMARKS, old, this.bookmarks);
		}
	}

	/**
	 *
	 */
	public synchronized Map<String,JRPrintAnchorIndex> getAnchorIndexes()
	{
		if (anchorIndexes == null)
		{
			anchorIndexes = new HashMap<>();
			
			int i = 0;
			for(Iterator<JRPrintPage> itp = pages.iterator(); itp.hasNext(); i++)
			{
				JRPrintPage page = itp.next();
				Collection<JRPrintElement> elements = page.getElements();
				collectAnchors(elements, i, 0, 0);
			}
		}
		
		return anchorIndexes;
	}

	protected void collectAnchors(Collection<JRPrintElement> elements, int pageIndex, int offsetX, int offsetY)
	{
		if (elements != null && elements.size() > 0)
		{
			JRPrintElement element = null;
			for(Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
			{
				element = it.next();
				if (element instanceof JRPrintAnchor)
				{
					anchorIndexes.put(
						((JRPrintAnchor)element).getAnchorName(), 
						new JRPrintAnchorIndex(pageIndex, element, offsetX, offsetY)
						);
				}
				
				if (element instanceof JRPrintFrame)
				{
					JRPrintFrame frame = (JRPrintFrame) element;
					collectAnchors(frame.getElements(), pageIndex, offsetX + frame.getX(), offsetY + frame.getY());
				}
			}
		}
	}


	/**
	 * Returns the name of the class implementing the {@link net.sf.jasperreports.engine.util.FormatFactory FormatFactory}
	 * interface to use with this document.
	 */
	public String getFormatFactoryClass()
	{
		return formatFactoryClass;
	}


	/**
	 * Sets the name of the class implementing the {@link net.sf.jasperreports.engine.util.FormatFactory FormatFactory}
	 * interface to use with this document.
	 */
	public void setFormatFactoryClass(String formatFactoryClass)
	{
		Object old = this.formatFactoryClass;
		this.formatFactoryClass = formatFactoryClass;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_FORMAT_FACTORY_CLASS, old, this.formatFactoryClass);
		}
	}


	/**
	 * Returns the code of the default <code>java.util.Locale</code> to be used for the
	 * elements of this print object.
	 * <p>
	 * When filling a report, the value of the {@link JRParameter#REPORT_LOCALE REPORT_LOCALE} parameter 
	 * (or the default locale if the parameter has no explicit value) 
	 * is saved using this attribute.  Some elements (e.g. elements rendered by a subreport)
	 * in the print object can override this default locale.
	 * </p>
	 * 
	 * @return the code of the default <code>java.util.Locale</code> for this object
	 * @see JRPrintText#getLocaleCode()
	 */
	public String getLocaleCode()
	{
		return localeCode;
	}


	/**
	 * Sets the the code of the default <code>java.util.Locale</code> to be used for this object.
	 * 
	 * @param localeCode the locale code, using the {@link java.util.Locale#toString() java.util.Locale.toString()}
	 * convention.
	 * @see #getLocaleCode()
	 * @see java.util.Locale#toString()
	 */
	public void setLocaleCode(String localeCode)
	{
		Object old = this.localeCode;
		this.localeCode = localeCode;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_LOCALE_CODE, old, this.localeCode);
		}
	}


	/**
	 * Returns the {@link java.util.TimeZone#getID() ID} of the default <code>java.util.TimeZone</code>
	 * to be used for the elements of this print object.
	 * <p>
	 * When filling a report, the value of the {@link JRParameter#REPORT_TIME_ZONE REPORT_TIME_ZONE} parameter
	 * (or the default time zine if the parameter has no explicit value) 
	 * is saved using this attribute.  Some elements (e.g. elements rendered by a subreport)
	 * in the print object can override this default time zone.
	 * </p>
	 * 
	 * @return the ID of the default <code>java.util.TimeZone</code> for this object
	 * @see JRPrintText#getTimeZoneId()
	 */
	public String getTimeZoneId()
	{
		return timeZoneId;
	}


	/**
	 * Sets the the {@link java.util.TimeZone#getID() ID} of the default <code>java.util.TimeZone</code>
	 * to be used for this object.
	 * 
	 * @param timeZoneId the time zone ID
	 * @see #getTimeZoneId()
	 * @see java.util.TimeZone#getID()
	 */
	public void setTimeZoneId(String timeZoneId)
	{
		Object old = this.timeZoneId;
		this.timeZoneId = timeZoneId;
		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(PROPERTY_TIME_ZONE_ID, old, this.timeZoneId);
		}
	}
	
	public void copyFrom(JasperPrint jasperPrint)
	{
		this.name = jasperPrint.name;
		this.pageWidth = jasperPrint.pageWidth;
		this.pageHeight = jasperPrint.pageHeight;
		this.topMargin = jasperPrint.topMargin;
		this.leftMargin = jasperPrint.leftMargin;
		this.bottomMargin = jasperPrint.bottomMargin;
		this.rightMargin = jasperPrint.rightMargin;
		this.orientation = jasperPrint.orientation;
		this.formatFactoryClass = jasperPrint.formatFactoryClass;
		this.localeCode = jasperPrint.localeCode;
		this.timeZoneId = jasperPrint.timeZoneId;
		
		if (jasperPrint.propertiesMap != null)
		{
			this.propertiesMap = jasperPrint.propertiesMap.cloneProperties();
		}
		
		this.stylesList.addAll(jasperPrint.stylesList);
		this.stylesMap.putAll(jasperPrint.stylesMap);
		this.defaultStyleProvider.setDefaultStyle(jasperPrint.defaultStyleProvider.getDefaultStyle());

		this.originsList.addAll(jasperPrint.originsList);
		this.originsMap.putAll(jasperPrint.originsMap);
		
		if (jasperPrint.bookmarks != null)
		{
			this.bookmarks = new ArrayList<>(jasperPrint.bookmarks);
		}
		
		if (jasperPrint.parts != null)
		{
			this.parts = ((StandardPrintParts) jasperPrint.parts).shallowClone();
		}
		
		this.pages.addAll(jasperPrint.pages);
	}
	
	private transient JRPropertyChangeSupport eventSupport;
	
	protected boolean hasEventSupport()
	{
		return eventSupport != null;
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
}
