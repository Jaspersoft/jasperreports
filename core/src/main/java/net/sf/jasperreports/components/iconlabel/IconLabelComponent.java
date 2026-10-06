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
package net.sf.jasperreports.components.iconlabel;

import java.awt.Color;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.components.ComponentsExtensionsRegistryFactory;
import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRImageAlignment;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.BaseComponentContext;
import net.sf.jasperreports.engine.component.ComponentContext;
import net.sf.jasperreports.engine.component.ContextAwareComponent;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(ComponentsExtensionsRegistryFactory.ICONLABEL_COMPONENT_NAME)
public class IconLabelComponent implements ContextAwareComponent, JRBoxContainer, JRImageAlignment, Serializable, JRChangeEventsSupport 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_ICON_POSITION = "iconPosition";
	public static final String PROPERTY_LABEL_FILL = "labelFill";
	public static final String PROPERTY_HORIZONTAL_ALIGNMENT = "horizontalAlignment";
	public static final String PROPERTY_VERTICAL_ALIGNMENT = "verticalAlignment";

	private JRLineBox lineBox;
	private JRTextField labelTextField;
	private JRTextField iconTextField;
	private IconPositionEnum iconPosition;
	private ContainerFillEnum labelFill;
	private HorizontalImageAlignEnum horizontalImageAlign;
	private VerticalImageAlignEnum verticalImageAlign;

	private ComponentContext context;
	
	private transient JRPropertyChangeSupport eventSupport;
	
	@JsonCreator
	private IconLabelComponent() 
	{
		this(JasperDesign.getThreadInstance());
	}
	
	public IconLabelComponent(JRDefaultStyleProvider defaultStyleProvider) 
	{
		lineBox = new JRBaseLineBox(this);
		labelTextField = new JRDesignTextField(defaultStyleProvider);
		iconTextField = new JRDesignTextField(defaultStyleProvider);
	}
	
	public IconLabelComponent(IconLabelComponent component, JRBaseObjectFactory objectFactory) 
	{
		this.lineBox = component.getLineBox().clone(this);
		this.labelTextField = (JRTextField)objectFactory.getVisitResult(component.getLabelTextField());
		this.iconTextField = (JRTextField)objectFactory.getVisitResult(component.getIconTextField());
		
		this.iconPosition = component.getIconPosition();
		this.labelFill = component.getLabelFill();
		this.horizontalImageAlign = component.getOwnHorizontalImageAlign();
		this.verticalImageAlign = component.getOwnVerticalImageAlign();

		this.context = new BaseComponentContext(component.getContext(), objectFactory);
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
	public JRLineBox getLineBox()
	{
		return lineBox;
	}
	
	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ELEMENT_box)
	public void setLineBox(JRLineBox lineBox) 
	{
		this.lineBox = lineBox == null ? null : lineBox.clone(this);
	}
	
	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return context == null ? labelTextField.getDefaultStyleProvider() : context.getComponentElement().getDefaultStyleProvider();
	}
	
	/**
	 *
	 */
	protected StyleResolver getStyleResolver()
	{
		return getDefaultStyleProvider().getStyleResolver();
	}
	
	@Override
	public JRStyle getStyle()
	{
		return context == null ? null : context.getComponentElement().getStyle();
	}
	
	@Override
	public String getStyleNameReference()
	{
		return context == null ? null : context.getComponentElement().getStyleNameReference();
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return Color.black;
	}

	/**
	 *
	 */
	@JsonTypeInfo(use = Id.NONE)
	public JRTextField getLabelTextField() 
	{
		return labelTextField;
	}

	/**
	 *
	 */
	public void setLabelTextField(JRTextField labelTextField) 
	{
		this.labelTextField = labelTextField;
	}
	
	/**
	 *
	 */
	@JsonTypeInfo(use = Id.NONE)
	public JRTextField getIconTextField() 
	{
		return iconTextField;
	}

	/**
	 *
	 */
	public void setIconTextField(JRTextField iconTextField) 
	{
		this.iconTextField = iconTextField;
	}
	
	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public IconPositionEnum getIconPosition() 
	{
		return iconPosition;
	}

	/**
	 *
	 */
	public void setIconPosition(IconPositionEnum iconPosition) 
	{
		IconPositionEnum old = this.iconPosition;
		this.iconPosition = iconPosition;
		getEventSupport().firePropertyChange(PROPERTY_ICON_POSITION, old, this.iconPosition);
	}

	@Override
	public HorizontalImageAlignEnum getHorizontalImageAlign()
	{
		return getStyleResolver().getHorizontalImageAlign(this);
	}
		
	@Override
	@JsonGetter(JRXmlConstants.ATTRIBUTE_hAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_hAlign, isAttribute = true)
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign()
	{
		return horizontalImageAlign;
	}
		
	@Override
	@JsonSetter(JRXmlConstants.ATTRIBUTE_hAlign)
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalImageAlign)
	{
		Object old = this.horizontalImageAlign;
		this.horizontalImageAlign = horizontalImageAlign;
		getEventSupport().firePropertyChange(PROPERTY_HORIZONTAL_ALIGNMENT, old, this.horizontalImageAlign);
	}

	@Override
	public VerticalImageAlignEnum getVerticalImageAlign()
	{
		return getStyleResolver().getVerticalImageAlign(this);
	}
		
	@Override
	@JsonGetter(JRXmlConstants.ATTRIBUTE_vAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_vAlign, isAttribute = true)
	public VerticalImageAlignEnum getOwnVerticalImageAlign()
	{
		return verticalImageAlign;
	}
		
	@Override
	@JsonSetter(JRXmlConstants.ATTRIBUTE_vAlign)
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalImageAlign)
	{
		Object old = this.verticalImageAlign;
		this.verticalImageAlign = verticalImageAlign;
		getEventSupport().firePropertyChange(PROPERTY_VERTICAL_ALIGNMENT, old, this.verticalImageAlign);
	}

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public ContainerFillEnum getLabelFill() 
	{
		return labelFill;
	}

	/**
	 *
	 */
	public void setLabelFill(ContainerFillEnum labelFill) 
	{
		ContainerFillEnum old = this.labelFill;
		this.labelFill = labelFill;
		getEventSupport().firePropertyChange(PROPERTY_LABEL_FILL, old, this.labelFill);
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
