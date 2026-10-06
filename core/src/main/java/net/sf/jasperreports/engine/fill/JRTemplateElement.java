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
package net.sf.jasperreports.engine.fill;

import java.awt.Color;
import java.io.Serializable;
import java.util.Random;

import net.sf.jasperreports.engine.Deduplicable;
import net.sf.jasperreports.engine.JRCommonElement;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;
import net.sf.jasperreports.engine.util.StyleResolver;


/**
 * Base class consisting of print element information shared by multiple
 * print elements.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRTemplateElement implements JRCommonElement, Serializable, JRPropertiesHolder, Deduplicable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Random randomId = new Random();

	/**
	 *
	 */
	private String key;
	private ModeEnum mode;
	private Color forecolor;
	private Color backcolor;

	protected JROrigin origin;

	protected JRDefaultStyleProvider defaultStyleProvider;
	protected JRStyle parentStyle;

	private final String id;
	
	private JRPropertiesMap propertiesMap;
	
	/**
	 *
	 */
	protected JRTemplateElement(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider)
	{
		this.origin = origin;
		this.defaultStyleProvider = defaultStyleProvider;
		id = createId();
	}

	protected JRTemplateElement(String id)
	{
		this.id = id;
	}
	
	private String createId()
	{
		return System.identityHashCode(this) + "_" + System.currentTimeMillis() + "_" + randomId.nextInt();
	}


	/**
	 * Copies basic element attributes: the element style, key,
	 * mode, forecolor and backcolor.
	 * 
	 * @param element the element to copy attributes from
	 */
	public void setElement(JRElement element)
	{
		parentStyle = element.getStyle();
		
		key = element.getKey();
		
		mode = element.getOwnMode();
		forecolor = element.getOwnForecolor();
		backcolor = element.getOwnBackcolor();
	}

	/**
	 *
	 */
	public JROrigin getOrigin()
	{
		return origin;
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

	@Override
	public JRStyle getStyle()
	{
		return parentStyle;
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
	public int getWidth()
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public int getHeight()
	{
		throw new UnsupportedOperationException();
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
		this.mode = mode;
	}

	@Override
	public Color getForecolor()
	{
		return getStyleResolver().getForecolor(this);
	}
	
	@Override
	public Color getOwnForecolor()
	{
		return this.forecolor;
	}
	
	@Override
	public void setForecolor(Color forecolor)
	{
		this.forecolor = forecolor;
	}
	
	@Override
	public Color getBackcolor()
	{
		return getStyleResolver().getBackcolor(this);
	}
	
	@Override
	public Color getOwnBackcolor()
	{
		return this.backcolor;
	}
	
	@Override
	public void setBackcolor(Color backcolor)
	{
		this.backcolor = backcolor;
	}
	
	/**
	 *
	 */
	public String getId()
	{
		return id;
	}

	
	@Override
	public String getKey()
	{
		return key;
	}

	
	public void setKey(String key)
	{
		this.key = key;
	}


	/**
	 * Returns null as external style references are not allowed for print objects.
	 */
	@Override
	public String getStyleNameReference()
	{
		return null;
	}

	
	@Override
	public boolean hasProperties()
	{
		return propertiesMap != null && propertiesMap.hasProperties();
	}

	// we don't need any locking as properties are only set at creation time
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

	/**
	 * Sets the template element style.
	 * 
	 * @param style the style
	 */
	public void setStyle(JRStyle style)
	{
		this.parentStyle = style;
	}

	protected void addTemplateHash(ObjectUtils.HashCode hash)
	{
		hash.addIdentity(defaultStyleProvider == null ? null : defaultStyleProvider.getDefaultStyle());
		hash.addIdentity(parentStyle);
		hash.add(origin);
		hash.add(key);
		hash.add(mode);
		hash.add(forecolor);
		hash.add(backcolor);
		hash.add(propertiesMap);
	}
	
	protected boolean templateIdentical(JRTemplateElement template)
	{
		return (defaultStyleProvider == null 
				? template.defaultStyleProvider == null 
				: template.defaultStyleProvider != null 
					&& ObjectUtils.equalsIdentity(defaultStyleProvider.getDefaultStyle(), template.defaultStyleProvider.getDefaultStyle()))
				&& ObjectUtils.equalsIdentity(parentStyle, template.parentStyle)
				&& ObjectUtils.equals(origin, template.origin)
				&& ObjectUtils.equals(key, template.key)
				&& ObjectUtils.equals(mode, template.mode)
				&& ObjectUtils.equals(forecolor, template.forecolor)
				&& ObjectUtils.equals(backcolor, template.backcolor)
				&& ObjectUtils.equals(propertiesMap, template.propertiesMap);
	}
	
	public void populateStyle()
	{
		mode = getMode();
		forecolor = getForecolor();
		backcolor = getBackcolor();
	}
}
