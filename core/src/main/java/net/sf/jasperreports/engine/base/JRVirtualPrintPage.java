/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2005 Works, Inc. All rights reserved.
 * http://www.works.com
 * Copyright (C) 2005 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
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

/*
 * Licensed to Jaspersoft Corporation under a Contributer Agreement
 */
package net.sf.jasperreports.engine.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRTemplateElement;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.engine.fill.VirtualizationObjectInputStream;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.Renderable;

/**
 * A print page that can be virtualized to free heap memory.
 * 
 * @author John Bindel
 */
public class JRVirtualPrintPage implements JRPrintPage, Serializable
{
	protected static final Log log = LogFactory.getLog(JRVirtualPrintPage.class);

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/**
	 * Property that determines how many print elements will make up a virtual page
	 * handled as a unit by the virtualizer.
	 * 
	 * <p>
	 * For most paginated reports, a virtual page corresponds to a report page.
	 * But for non-paginated reports and for reports having very large pages, a report
	 * page is broken into several virtual pages which are handled individually by the
	 * virtualizer.
	 * 
	 * <p>
	 * This property provides the size of a virtual page in print elements.  Note that
	 * virtual page sizes will actually vary around the configured size since there 
	 * are cases when fewer elements remain on a report page and cases when the configured
	 * size is exceeded due to print frames being included at the end of virtual page.
	 * 
	 * <p>
	 * If set to 0 or negative, report pages will not be broken into several virtual pages.
	 * 
	 * <p>
	 * The property can be set at report and global levels or sent as a parameter value
	 * (as an integer, using the property name as parameter name).
	 * The default value is 2000.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "2000",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_1_3,
			valueType = Integer.class
			)
	public static final String PROPERTY_VIRTUAL_PAGE_ELEMENT_SIZE = 
			JRPropertiesUtil.PROPERTY_PREFIX + "virtual.page.element.size";
	
	protected VirtualizableElementList elements;
	
	/**
	 * Constructs a virtualizable page.
	 * 
	 * @deprecated the virtualizer should be passed as part of the virtualization context,
	 * use {@link #JRVirtualPrintPage(JasperPrint, JRVirtualizationContext)} instead
	 */
	public JRVirtualPrintPage(JasperPrint printObject, JRVirtualizer virtualizer, JRVirtualizationContext virtualizationContext)
	{
		this(printObject, virtualizationContext);
	}
	
	/**
	 * Constructs a virtualizable page.
	 * 
	 * @param printObject
	 * @param virtualizationContext
	 * @deprecated replaced by {@link #JRVirtualPrintPage(JRVirtualizationContext)}
	 */
	@Deprecated
	public JRVirtualPrintPage(JasperPrint printObject, JRVirtualizationContext virtualizationContext)
	{
		this(virtualizationContext);
	}
	
	/**
	 * Constructs a virtualizable page.
	 */
	public JRVirtualPrintPage(JRVirtualizationContext virtualizationContext)
	{
		super();
		
		this.elements = new VirtualizableElementList(virtualizationContext, this);
		
		if (log.isDebugEnabled())
		{
			log.debug("created list " + this.elements + " for page " + this);
		}
	}
	
	protected JRVirtualPrintPage()
	{
	}

	@Override
	public List<JRPrintElement> getElements()
	{
		return elements;
	}

	@Override
	public void setElements(List<JRPrintElement> elements)
	{
		this.elements.set(elements);
	}

	@Override
	public void addElement(JRPrintElement element)
	{
		elements.add(element);
	}
	
	
	/**
	 * Dummy image renderer that only stores the ID of a cached renderer.
	 * When a page gets serialized, all image renderers that are cached in the
	 * virtualization context are replaced with dummy renderers that only store the ID.
	 * When a page gets deserialized, the original renderers are restored from the 
	 * virtualization context based on the ID.
	 */
	// we have to keep these two classes here so that we're able to deserialize old reports
	public static class JRIdHolderRenderer implements Renderable
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
		
		protected final String id;
		
		public JRIdHolderRenderer(Renderable renderer)
		{
			this.id = renderer.getId();
		}

		@Override
		public String getId()
		{
			return id;
		}
	}
	
	
	public static class JRIdHolderTemplateElement extends JRTemplateElement
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
		
		public JRIdHolderTemplateElement(String id)
		{
			super(id);
		}

		@Override
		public int getHashCode()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isIdentical(Object object)
		{
			throw new UnsupportedOperationException();
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		Object object = in.readObject();
		if (object instanceof VirtualizableElementList)
		{
			elements = (VirtualizableElementList) object;
		}
		else
		{
			// page serialized by old version
			String oldUid = (String) object;
			if (log.isDebugEnabled())
			{
				log.debug("Original page uid " + oldUid);
			}
			
			JRVirtualizationContext virtualizationContext = (JRVirtualizationContext) in.readObject();
			
			int length = in.readInt();
			byte[] buffer = new byte[length];
			in.readFully(buffer);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer, 0, buffer.length);
			VirtualizationObjectInputStream elementsStream = new VirtualizationObjectInputStream(inputStream, virtualizationContext);
			try
			{
				@SuppressWarnings("unchecked")
				List<JRPrintElement> elementsList = (List<JRPrintElement>) elementsStream.readObject();
				
				// create a new list for the elements
				elements = new VirtualizableElementList(virtualizationContext, this);
				elements.addAll(elementsList);
			}
			finally
			{
				elementsStream.close();
			}
		}
	}

	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeObject(elements);
	}

	/**
	 * Disposes this page's data.
	 */
	public void dispose()
	{
		elements.dispose();
	}
	
	public JRVirtualizationContext getVirtualizationContext()
	{
		return elements.getVirtualizationContext();
	}
	
}
