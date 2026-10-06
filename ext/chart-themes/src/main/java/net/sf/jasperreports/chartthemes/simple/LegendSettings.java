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

import java.io.Serializable;

import org.jfree.chart.block.BlockFrame;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.VerticalAlignment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.chartthemes.simple.handlers.HorizontalAlignmentDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.HorizontalAlignmentSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.JRFontDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.JRFontSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.RectangleInsetsSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.VerticalAlignmentDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.VerticalAlignmentSerializer;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonIgnoreProperties("eventSupport")
public class LegendSettings implements JRChangeEventsSupport, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_showLegend = "showLegend";
	public static final String PROPERTY_position = "position";
	public static final String PROPERTY_foregroundPaint = "foregroundPaint";
	public static final String PROPERTY_backgroundPaint = "backgroundPaint";
	public static final String PROPERTY_font = "font";
	public static final String PROPERTY_horizontalAlignment = "horizontalAlignment";
	public static final String PROPERTY_verticalAlignment = "verticalAlignment";
	
	public static final String PROPERTY_frame = "frame";
	public static final String PROPERTY_padding = "padding";

	/**
	 *
	 */
	@JacksonXmlProperty(localName = "show-legend", isAttribute = true)
	private Boolean showLegend;

	@JacksonXmlProperty(localName = "position", isAttribute = true)
	private EdgeEnum position;
	
	@JacksonXmlProperty(localName = "foreground-paint")
	private PaintProvider foregroundPaint;
	
	@JacksonXmlProperty(localName = "background-paint")
	private PaintProvider backgroundPaint;
	
	@JsonDeserialize(using = JRFontDeserializer.class)
	@JsonSerialize(using = JRFontSerializer.class)
	private JRFont font = new JRBaseFont();
	
	@JacksonXmlProperty(localName = "horizontal-alignment", isAttribute = true)
	@JsonDeserialize(using = HorizontalAlignmentDeserializer.class)
	@JsonSerialize(using = HorizontalAlignmentSerializer.class)
	private HorizontalAlignment horizontalAlignment;
	
	@JacksonXmlProperty(localName = "vertical-alignment", isAttribute = true)
	@JsonDeserialize(using = VerticalAlignmentDeserializer.class)
	@JsonSerialize(using = VerticalAlignmentSerializer.class)
	private VerticalAlignment verticalAlignment;
	
	private BlockFrameProvider frame;
	
	@JsonSerialize(using = RectangleInsetsSerializer.class)
	@JsonIgnoreProperties("unitType")
	private RectangleInsets padding;
	
	/**
	 *
	 */
	public LegendSettings()
	{
	}
	
	/**
	 *
	 */
	public Boolean getShowLegend()
	{
		return showLegend;
	}
	
	/**
	 *
	 */
	public void setShowLegend(Boolean showLegend)
	{
		Boolean old = getShowLegend();
		this.showLegend = showLegend;
		getEventSupport().firePropertyChange(PROPERTY_showLegend, old, getShowLegend());
	}
	
	/**
	 * @return the backgroundPaint
	 */
	public PaintProvider getBackgroundPaint() {
		return backgroundPaint;
	}

	/**
	 * @param backgroundPaint the backgroundPaint to set
	 */
	public void setBackgroundPaint(PaintProvider backgroundPaint) {
		PaintProvider old = getBackgroundPaint();
		this.backgroundPaint = backgroundPaint;
		getEventSupport().firePropertyChange(PROPERTY_backgroundPaint, old, getBackgroundPaint());
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
	}

	/**
	 * @return the foregroundPaint
	 */
	public PaintProvider getForegroundPaint() {
		return foregroundPaint;
	}

	/**
	 * @param foregroundPaint the foregroundPaint to set
	 */
	public void setForegroundPaint(PaintProvider foregroundPaint) {
		PaintProvider old = getForegroundPaint();
		this.foregroundPaint = foregroundPaint;
		getEventSupport().firePropertyChange(PROPERTY_foregroundPaint, old, getForegroundPaint());
	}

	/**
	 * @return the horizontalAlignment
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * @param horizontalAlignment the horizontalAlignment to set
	 */
	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		HorizontalAlignment old = getHorizontalAlignment();
		this.horizontalAlignment = horizontalAlignment;
		getEventSupport().firePropertyChange(PROPERTY_horizontalAlignment, old, getHorizontalAlignment());
	}

	/**
	 * @return the position
	 */
	public EdgeEnum getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(EdgeEnum position) {
		EdgeEnum old = this.position ;
		this.position = position;
		getEventSupport().firePropertyChange(PROPERTY_position, old, this.position);
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
	 * @return the blockFrame
	 */
	public BlockFrame getBlockFrame() {
		return frame == null ? null : frame.getBlockFrame();
	}

	public BlockFrameProvider getFrame() {
		return frame;
	}

	public void setFrame(BlockFrameProvider frame) {
		BlockFrameProvider old = this.frame;
		this.frame = frame;
		getEventSupport().firePropertyChange(PROPERTY_frame, old, this.frame);
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
	 * @return the verticalAlignment
	 */
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * @param verticalAlignment the verticalAlignment to set
	 */
	public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
		VerticalAlignment old = getVerticalAlignment();
		this.verticalAlignment = verticalAlignment;
		getEventSupport().firePropertyChange(PROPERTY_verticalAlignment, old, getVerticalAlignment());
	}
}
