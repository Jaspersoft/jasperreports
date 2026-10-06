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
package net.sf.jasperreports.chartthemes.simple;

import java.awt.Stroke;
import java.io.Serializable;

import org.jfree.chart.ui.RectangleInsets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.chartthemes.simple.handlers.ImageAlignmentDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.ImageAlignmentSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.JRFontDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.JRFontSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.RectangleInsetsSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.StrokeDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.StrokeSerializer;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonIgnoreProperties("eventSupport")
public class ChartSettings implements JRChangeEventsSupport, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_backgroundPaint = "backgroundPaint";
	public static final String PROPERTY_backgroundImage = "backgroundImage";
	public static final String PROPERTY_backgroundImageAlignment = "backgroundImageAlignment";
	public static final String PROPERTY_backgroundImageAlpha = "backgroundImageAlpha";
	public static final String PROPERTY_font = "font";
	public static final String PROPERTY_borderVisible = "borderVisible";
	public static final String PROPERTY_borderPaint = "borderPaint";
	public static final String PROPERTY_borderStroke = "borderStroke";
	public static final String PROPERTY_antiAlias = "antiAlias";
	public static final String PROPERTY_textAntiAlias = "textAntiAlias";
	public static final String PROPERTY_padding = "padding";

	/**
	 *
	 */
	@JacksonXmlProperty(localName = "background-paint")
	private PaintProvider backgroundPaint;
	
	@JacksonXmlProperty(localName = "background-image")
	private ImageProvider backgroundImage;
	
	@JacksonXmlProperty(localName = "background-image-alignment", isAttribute = true)
	@JsonDeserialize(using = ImageAlignmentDeserializer.class)
	@JsonSerialize(using = ImageAlignmentSerializer.class)
	private Integer backgroundImageAlignment;
	
	@JacksonXmlProperty(localName = "background-image-alpha", isAttribute = true)
	private Float backgroundImageAlpha;
	
	@JsonDeserialize(using = JRFontDeserializer.class)
	@JsonSerialize(using = JRFontSerializer.class)
	private JRFont font = new JRBaseFont();
	
	@JacksonXmlProperty(localName = "border-visible", isAttribute = true)
	private Boolean borderVisible;
	
	@JacksonXmlProperty(localName = "border-paint")
	private PaintProvider borderPaint;
	
	@JacksonXmlProperty(localName = "stroke")
	@JsonDeserialize(using = StrokeDeserializer.class)
	@JsonSerialize(using = StrokeSerializer.class)
	private Stroke borderStroke;
	
	@JacksonXmlProperty(localName = "anti-alias", isAttribute = true)
	private Boolean antiAlias;
	
	@JacksonXmlProperty(localName = "text-anti-alias", isAttribute = true)
	private Boolean textAntiAlias;
	
	@JsonSerialize(using = RectangleInsetsSerializer.class)
	@JsonIgnoreProperties("unitType")
	private RectangleInsets padding;
	
	/**
	 *
	 */
	public ChartSettings()
	{
	}
	
	/**
	 *
	 */
	public PaintProvider getBackgroundPaint()
	{
		return backgroundPaint;
	}
	
	/**
	 *
	 */
	public void setBackgroundPaint(PaintProvider backgroundPaint)
	{
		PaintProvider old = getBackgroundPaint();
		this.backgroundPaint = backgroundPaint;
		getEventSupport().firePropertyChange(PROPERTY_backgroundPaint, old, getBackgroundPaint());
	}
	
	/**
	 * @return the antiAlias
	 */
	public Boolean getAntiAlias() {
		return antiAlias;
	}

	/**
	 * @param antiAlias the antiAlias to set
	 */
	public void setAntiAlias(Boolean antiAlias) {
		Boolean old = getAntiAlias();
		this.antiAlias = antiAlias;
		getEventSupport().firePropertyChange(PROPERTY_antiAlias, old, getAntiAlias());
	}

	/**
	 * @return the borderVisible
	 */
	public Boolean getBorderVisible() {
		return borderVisible;
	}

	/**
	 * @param borderVisible the borderVisible to set
	 */
	public void setBorderVisible(Boolean borderVisible) {
		Boolean old = getBorderVisible();
		this.borderVisible = borderVisible;
		getEventSupport().firePropertyChange(PROPERTY_borderVisible, old, getBorderVisible());
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

	/**
	 * @return the font
	 */
	public JRFont getFont() {
		return font;
	}

	/**
	 * @param font the font to set
	 */
	public void setFont(JRFont font) {
		JRFont old = getFont();
		this.font = font;
		getEventSupport().firePropertyChange(PROPERTY_font, old, getFont());

//		titleSettings.setTitleFont(titleSettings.getTitleFont());
//		subtitleSettings.setTitleFont(subtitleSettings.getTitleFont());
//		setLegendFont(legendFont);
	}

	/**
	 * @return the padding
	 */
	public RectangleInsets getPadding() {
		return padding;
	}

	/**
	 * @param padding the padding to set
	 */
	public void setPadding(RectangleInsets padding) {
		RectangleInsets old = getPadding();
		this.padding = padding;
		getEventSupport().firePropertyChange(PROPERTY_padding, old, getPadding());
	}

	/**
	 * @return the textAntiAlias
	 */
	public Boolean getTextAntiAlias()
	{
		return textAntiAlias;
	}

	/**
	 * @param textAntiAlias the textAntiAlias to set
	 */
	public void setTextAntiAlias(Boolean textAntiAlias)
	{
		Boolean old = getTextAntiAlias();
		this.textAntiAlias = textAntiAlias;
		getEventSupport().firePropertyChange(PROPERTY_textAntiAlias, old, getTextAntiAlias());
	}

	/**
	 * @return the backgroundImageAlignment
	 */
	public Integer getBackgroundImageAlignment()
	{
		return backgroundImageAlignment;
	}

	/**
	 * @param backgroundImageAlignment the backgroundImageAlignment to set
	 */
	public void setBackgroundImageAlignment(Integer backgroundImageAlignment)
	{
		Integer old = getBackgroundImageAlignment();
		this.backgroundImageAlignment = backgroundImageAlignment;
		getEventSupport().firePropertyChange(PROPERTY_backgroundImageAlignment, old, getBackgroundImageAlignment());
	}

	/**
	 * @return the backgroundImageAlpha
	 */
	public Float getBackgroundImageAlpha()
	{
		return backgroundImageAlpha;
	}

	/**
	 * @param backgroundImageAlpha the backgroundImageAlpha to set
	 */
	public void setBackgroundImageAlpha(Float backgroundImageAlpha)
	{
		Float old = getBackgroundImageAlpha();
		this.backgroundImageAlpha = backgroundImageAlpha;
		getEventSupport().firePropertyChange(PROPERTY_backgroundImageAlpha, old, getBackgroundImageAlpha());
	}

	/**
	 * @return the borderPaint
	 */
	public PaintProvider getBorderPaint()
	{
		return borderPaint;
	}

	/**
	 * @param borderPaint the borderPaint to set
	 */
	public void setBorderPaint(PaintProvider borderPaint)
	{
		PaintProvider old = getBorderPaint();
		this.borderPaint = borderPaint;
		getEventSupport().firePropertyChange(PROPERTY_borderPaint, old, getBorderPaint());
	}

	/**
	 * @return the backgroundImage
	 */
	public ImageProvider getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(ImageProvider backgroundImage) {
		ImageProvider old = getBackgroundImage();
		this.backgroundImage = backgroundImage;
		getEventSupport().firePropertyChange(PROPERTY_backgroundImage, old, getBackgroundImage());
	}

	/**
	 * @return the borderStroke
	 */
	public Stroke getBorderStroke() {
		return borderStroke;
	}

	/**
	 * @param borderStroke the borderStroke to set
	 */
	public void setBorderStroke(Stroke borderStroke) {
		Stroke old = getBorderStroke();
		this.borderStroke = borderStroke;
		getEventSupport().firePropertyChange(PROPERTY_borderStroke, old, getBorderStroke());
	}

//	/**
//	 * @return the seriesColors
//	 */
//	public List getSeriesColors()
//	{
//		return seriesColors;
//	}
//
//	/**
//	 * @param seriesColors the seriesColors to set
//	 */
//	public void setSeriesColors(List seriesColors)
//	{
//		List old = getSeriesColors();
//		this.seriesColors = seriesColors;
//		getEventSupport().firePropertyChange(PROPERTY_seriesColors, old, getSeriesColors());
//	}

}
