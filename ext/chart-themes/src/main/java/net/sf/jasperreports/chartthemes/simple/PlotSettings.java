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
import java.util.List;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.RectangleInsets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.chartthemes.simple.handlers.ImageAlignmentDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.ImageAlignmentSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.JRFontDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.JRFontSerializer;
import net.sf.jasperreports.chartthemes.simple.handlers.PlotOrientationDeserializer;
import net.sf.jasperreports.chartthemes.simple.handlers.PlotOrientationSerializer;
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
public class PlotSettings implements JRChangeEventsSupport, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_orientation = "orientation";
	public static final String PROPERTY_foregroundAlpha = "foregroundAlpha";
	public static final String PROPERTY_backgroundPaint = "backgroundPaint";
	public static final String PROPERTY_backgroundAlpha = "backgroundAlpha";
	public static final String PROPERTY_backgroundImage = "backgroundImage";
	public static final String PROPERTY_backgroundImageAlpha = "backgroundImageAlpha";
	public static final String PROPERTY_backgroundImageAlignment = "backgroundImageAlignment";
	public static final String PROPERTY_labelRotation = "labelRotation";
	public static final String PROPERTY_padding = "padding";
	public static final String PROPERTY_outlineVisible = "outlineVisible";
	public static final String PROPERTY_outlinePaint = "outlinePaint";
	public static final String PROPERTY_outlineStroke = "outlineStroke";
	public static final String PROPERTY_seriesColorSequence = "seriesColorSequence";
	public static final String PROPERTY_seriesGradientPaintSequence = "seriesGradientPaintSequence";
	public static final String PROPERTY_seriesOutlinePaintSequence = "seriesOutlinePaintSequence";
	public static final String PROPERTY_seriesStrokeSequence = "seriesStrokeSequence";
	public static final String PROPERTY_seriesOutlineStrokeSequence = "seriesOutlineStrokeSequence";
	public static final String PROPERTY_domainGridlineVisible = "domainGridlineVisible";
	public static final String PROPERTY_domainGridlinePaint = "domainGridlinePaint";
	public static final String PROPERTY_domainGridlineStroke = "domainGridlineStroke";
	public static final String PROPERTY_rangeGridlineVisible = "rangeGridlineVisible";
	public static final String PROPERTY_rangeGridlinePaint = "rangeGridlinePaint";
	public static final String PROPERTY_rangeGridlineStroke = "rangeGridlineStroke";
	public static final String PROPERTY_tickLabelFont = "tickLabelFont";
	public static final String PROPERTY_displayFont = "displayFont";

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	@JsonDeserialize(using = PlotOrientationDeserializer.class)
	@JsonSerialize(using = PlotOrientationSerializer.class)
	private PlotOrientation orientation;
	
	@JacksonXmlProperty(localName = "foreground-alpha")
	private Float foregroundAlpha;
	
	@JacksonXmlProperty(localName = "background-paint")
	private PaintProvider backgroundPaint;
	
	@JacksonXmlProperty(localName = "background-alpha")
	private Float backgroundAlpha;
	
	@JacksonXmlProperty(localName = "background-image")
	private ImageProvider backgroundImage;
	
	@JacksonXmlProperty(localName = "background-image-alpha", isAttribute = true)
	private Float backgroundImageAlpha;
	
	@JacksonXmlProperty(localName = "background-image-alignment", isAttribute = true)
	@JsonDeserialize(using = ImageAlignmentDeserializer.class)
	@JsonSerialize(using = ImageAlignmentSerializer.class)
	private Integer backgroundImageAlignment;
	
	@JacksonXmlProperty(localName = "label-rotation", isAttribute = true)
	private Double labelRotation;

	@JsonSerialize(using = RectangleInsetsSerializer.class)
	@JsonIgnoreProperties("unitType")
	private RectangleInsets padding;
	
	@JacksonXmlProperty(localName = "outline-visible", isAttribute = true)
	private Boolean outlineVisible;
	
	@JacksonXmlProperty(localName = "outline-paint")
	private PaintProvider outlinePaint;
	
	@JacksonXmlProperty(localName = "stroke")
	@JsonDeserialize(using = StrokeDeserializer.class)
	@JsonSerialize(using = StrokeSerializer.class)
	private Stroke outlineStroke;
	
	@JacksonXmlProperty(localName = "series-color-sequence")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<PaintProvider> seriesColorSequence;
	
	private List<PaintProvider> seriesGradientPaintSequence;
	
	@JacksonXmlProperty(localName = "series-outline-paint-sequence")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<PaintProvider> seriesOutlinePaintSequence;
	
	@JacksonXmlProperty(localName = "series-stroke-sequence")
	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonDeserialize(contentUsing = StrokeDeserializer.class)
	@JsonSerialize(contentUsing = StrokeSerializer.class)
	private List<Stroke> seriesStrokeSequence;
	
	@JacksonXmlProperty(localName = "series-outline-stroke-sequence")
	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonDeserialize(contentUsing = StrokeDeserializer.class)
	@JsonSerialize(contentUsing = StrokeSerializer.class)
	private List<Stroke> seriesOutlineStrokeSequence;
	
	@JacksonXmlProperty(localName = "domain-gridline-visible", isAttribute = true)
	private Boolean domainGridlineVisible;
	
	@JacksonXmlProperty(localName = "domain-gridline-paint")
	private PaintProvider domainGridlinePaint;
	
	@JacksonXmlProperty(localName = "domain-gridline-stroke")
	@JsonDeserialize(using = StrokeDeserializer.class)
	@JsonSerialize(using = StrokeSerializer.class)
	private Stroke domainGridlineStroke;
	
	@JacksonXmlProperty(localName = "range-gridline-visible", isAttribute = true)
	private Boolean rangeGridlineVisible;
	
	@JacksonXmlProperty(localName = "range-gridline-paint")
	private PaintProvider rangeGridlinePaint;
	
	@JacksonXmlProperty(localName = "range-gridline-stroke")
	@JsonDeserialize(using = StrokeDeserializer.class)
	@JsonSerialize(using = StrokeSerializer.class)
	private Stroke rangeGridlineStroke;
	
	@JacksonXmlProperty(localName = "tick-label-font")
	@JsonDeserialize(using = JRFontDeserializer.class)
	@JsonSerialize(using = JRFontSerializer.class)
	private JRFont tickLabelFont = new JRBaseFont();
	
	@JacksonXmlProperty(localName = "display-font")
	@JsonDeserialize(using = JRFontDeserializer.class)
	@JsonSerialize(using = JRFontSerializer.class)
	private JRFont displayFont = new JRBaseFont();
	
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
	 *
	 */
	public PlotSettings()
	{
	}
	
	/**
	 * @return the outlinePaint
	 */
	public PaintProvider getOutlinePaint() {
		return outlinePaint;
	}

	/**
	 * @param outlinePaint the outlinePaint to set
	 */
	public void setOutlinePaint(PaintProvider outlinePaint) {
		PaintProvider old = getOutlinePaint();
		this.outlinePaint = outlinePaint;
		getEventSupport().firePropertyChange(PROPERTY_outlinePaint, old, getOutlinePaint());
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
	 * @return the outlineVisible
	 */
	public Boolean getOutlineVisible() {
		return outlineVisible;
	}

	/**
	 * @param outlineVisible the outlineVisible to set
	 */
	public void setOutlineVisible(Boolean outlineVisible) {
		Boolean old = getOutlineVisible();
		this.outlineVisible = outlineVisible;
		getEventSupport().firePropertyChange(PROPERTY_outlineVisible, old, getOutlineVisible());
	}

	/**
	 * @return the outlineStroke
	 */
	public Stroke getOutlineStroke() {
		return outlineStroke;
	}

	/**
	 * @param outlineStroke the outlineStroke to set
	 */
	public void setOutlineStroke(Stroke outlineStroke) {
		Stroke old = getOutlineStroke();
		this.outlineStroke = outlineStroke;
		getEventSupport().firePropertyChange(PROPERTY_outlineStroke, old, getOutlineStroke());
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
	 * @return the backgroundAlpha
	 */
	public Float getBackgroundAlpha() {
		return backgroundAlpha;
	}

	/**
	 * @param backgroundAlpha the backgroundAlpha to set
	 */
	public void setBackgroundAlpha(Float backgroundAlpha) {
		Float old = getBackgroundAlpha();
		this.backgroundAlpha = backgroundAlpha;
		getEventSupport().firePropertyChange(PROPERTY_backgroundAlpha, old, getBackgroundAlpha());
	}

	/**
	 * @return the backgroundImageAlignment
	 */
	public Integer getBackgroundImageAlignment() {
		return backgroundImageAlignment;
	}

	/**
	 * @param backgroundImageAlignment the backgroundImageAlignment to set
	 */
	public void setBackgroundImageAlignment(Integer backgroundImageAlignment) {
		Integer old = getBackgroundImageAlignment();
		this.backgroundImageAlignment = backgroundImageAlignment;
		getEventSupport().firePropertyChange(PROPERTY_backgroundImageAlignment, old, getBackgroundImageAlignment());
	}

	/**
	 * @return the backgroundImageAlpha
	 */
	public Float getBackgroundImageAlpha() {
		return backgroundImageAlpha;
	}

	/**
	 * @param backgroundImageAlpha the backgroundImageAlpha to set
	 */
	public void setBackgroundImageAlpha(Float backgroundImageAlpha) {
		Float old = getBackgroundImageAlpha();
		this.backgroundImageAlpha = backgroundImageAlpha;
		getEventSupport().firePropertyChange(PROPERTY_backgroundImageAlpha, old, getBackgroundImageAlpha());
	}

	/**
	 * @return the foregroundAlpha
	 */
	public Float getForegroundAlpha() {
		return foregroundAlpha;
	}

	/**
	 * @param foregroundAlpha the foregroundAlpha to set
	 */
	public void setForegroundAlpha(Float foregroundAlpha) {
		Float old = getForegroundAlpha();
		this.foregroundAlpha = foregroundAlpha;
		getEventSupport().firePropertyChange(PROPERTY_foregroundAlpha, old, getForegroundAlpha());
	}

	/**
	 * @return the labelRotation
	 */
	public Double getLabelRotation() {
		return labelRotation;
	}

	/**
	 * @param labelRotation the labelRotation to set
	 */
	public void setLabelRotation(Double labelRotation) {
		Double old = getLabelRotation();
		this.labelRotation = labelRotation;
		getEventSupport().firePropertyChange(PROPERTY_labelRotation, old, getLabelRotation());
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
	 * @return the orientation
	 */
	public PlotOrientation getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(PlotOrientation orientation) {
		PlotOrientation old = getOrientation();
		this.orientation = orientation;
		getEventSupport().firePropertyChange(PROPERTY_orientation, old, getOrientation());
	}

	/**
	 * @return the seriesColorSequence
	 */
	public List<PaintProvider> getSeriesColorSequence()
	{
		return seriesColorSequence;
	}

	/**
	 * @param seriesColorSequence the seriesColorSequence to set
	 */
	public void setSeriesColorSequence(List<PaintProvider> seriesColorSequence)
	{
		List<PaintProvider> old = getSeriesColorSequence();
		this.seriesColorSequence = seriesColorSequence;
		getEventSupport().firePropertyChange(PROPERTY_seriesColorSequence, old, getSeriesColorSequence());
	}

	/**
	 * @return the seriesGradientPaintSequence
	 */
	public List<PaintProvider> getSeriesGradientPaintSequence()
	{
		return seriesGradientPaintSequence;
	}

	/**
	 * @param seriesGradientPaintSequence the seriesGradientPaintSequence to set
	 */
	public void setSeriesGradientPaintSequence(List<PaintProvider> seriesGradientPaintSequence)
	{
		List<PaintProvider> old = getSeriesGradientPaintSequence();
		this.seriesGradientPaintSequence = seriesGradientPaintSequence;
		getEventSupport().firePropertyChange(PROPERTY_seriesGradientPaintSequence, old, getSeriesGradientPaintSequence());
	}

	/**
	 * @return the seriesOutlinePaintSequence
	 */
	public List<PaintProvider> getSeriesOutlinePaintSequence()
	{
		return seriesOutlinePaintSequence;
	}

	/**
	 * @param seriesOutlinePaintSequence the seriesOutlinePaintSequence to set
	 */
	public void setSeriesOutlinePaintSequence(List<PaintProvider> seriesOutlinePaintSequence)
	{
		List<PaintProvider> old = getSeriesOutlinePaintSequence();
		this.seriesOutlinePaintSequence = seriesOutlinePaintSequence;
		getEventSupport().firePropertyChange(PROPERTY_seriesOutlinePaintSequence, old, getSeriesOutlinePaintSequence());
	}

	/**
	 * @return the seriesStrokeSequence
	 */
	public List<Stroke> getSeriesStrokeSequence()
	{
		return seriesStrokeSequence;
	}

	/**
	 * @param seriesStrokeSequence the seriesStrokeSequence to set
	 */
	public void setSeriesStrokeSequence(List<Stroke> seriesStrokeSequence)
	{
		List<Stroke> old = getSeriesStrokeSequence();
		this.seriesStrokeSequence = seriesStrokeSequence;
		getEventSupport().firePropertyChange(PROPERTY_seriesStrokeSequence, old, getSeriesStrokeSequence());
	}

	/**
	 * @return the seriesOutlineStrokeSequence
	 */
	public List<Stroke> getSeriesOutlineStrokeSequence()
	{
		return seriesOutlineStrokeSequence;
	}

	/**
	 * @param seriesOutlineStrokeSequence the seriesOutlineStrokeSequence to set
	 */
	public void setSeriesOutlineStrokeSequence(List<Stroke> seriesOutlineStrokeSequence)
	{
		List<Stroke> old = getSeriesOutlineStrokeSequence();
		this.seriesOutlineStrokeSequence = seriesOutlineStrokeSequence;
		getEventSupport().firePropertyChange(PROPERTY_seriesOutlineStrokeSequence, old, getSeriesOutlineStrokeSequence());
	}

	/**
	 * @return the domainGridlineVisible
	 */
	public Boolean getDomainGridlineVisible()
	{
		return domainGridlineVisible;
	}

	/**
	 * @param domainGridlineVisible the domainGridlineVisible to set
	 */
	public void setDomainGridlineVisible(Boolean domainGridlineVisible)
	{
		Boolean old = getDomainGridlineVisible();
		this.domainGridlineVisible = domainGridlineVisible;
		getEventSupport().firePropertyChange(PROPERTY_domainGridlineVisible, old, getDomainGridlineVisible());
	}

	/**
	 * @return the domainGridlinePaint
	 */
	public PaintProvider getDomainGridlinePaint()
	{
		return domainGridlinePaint;
	}

	/**
	 * @param domainGridlinePaint the domainGridlinePaint to set
	 */
	public void setDomainGridlinePaint(PaintProvider domainGridlinePaint)
	{
		PaintProvider old = getDomainGridlinePaint();
		this.domainGridlinePaint = domainGridlinePaint;
		getEventSupport().firePropertyChange(PROPERTY_domainGridlinePaint, old, getDomainGridlinePaint());
	}

	/**
	 * @return the domainGridlineStroke
	 */
	public Stroke getDomainGridlineStroke()
	{
		return domainGridlineStroke;
	}

	/**
	 * @param domainGridlineStroke the domainGridlineStroke to set
	 */
	public void setDomainGridlineStroke(Stroke domainGridlineStroke)
	{
		Stroke old = getDomainGridlineStroke();
		this.domainGridlineStroke = domainGridlineStroke;
		getEventSupport().firePropertyChange(PROPERTY_domainGridlineStroke, old, getDomainGridlineStroke());
	}

	/**
	 * @return the rangeGridlineVisible
	 */
	public Boolean getRangeGridlineVisible()
	{
		return rangeGridlineVisible;
	}

	/**
	 * @param rangeGridlineVisible the rangeGridlineVisible to set
	 */
	public void setRangeGridlineVisible(Boolean rangeGridlineVisible)
	{
		Boolean old = getRangeGridlineVisible();
		this.rangeGridlineVisible = rangeGridlineVisible;
		getEventSupport().firePropertyChange(PROPERTY_rangeGridlineVisible, old, getRangeGridlineVisible());
	}

	/**
	 * @return the rangeGridlinePaint
	 */
	public PaintProvider getRangeGridlinePaint()
	{
		return rangeGridlinePaint;
	}

	/**
	 * @param rangeGridlinePaint the rangeGridlinePaint to set
	 */
	public void setRangeGridlinePaint(PaintProvider rangeGridlinePaint)
	{
		PaintProvider old = getRangeGridlinePaint();
		this.rangeGridlinePaint = rangeGridlinePaint;
		getEventSupport().firePropertyChange(PROPERTY_rangeGridlinePaint, old, getRangeGridlinePaint());
	}

	/**
	 * @return the rangeGridlineStroke
	 */
	public Stroke getRangeGridlineStroke()
	{
		return rangeGridlineStroke;
	}

	/**
	 * @param rangeGridlineStroke the rangeGridlineStroke to set
	 */
	public void setRangeGridlineStroke(Stroke rangeGridlineStroke)
	{
		Stroke old = getRangeGridlineStroke();
		this.rangeGridlineStroke = rangeGridlineStroke;
		getEventSupport().firePropertyChange(PROPERTY_rangeGridlineStroke, old, getRangeGridlineStroke());
	}

	/**
	 * @return the tickLabelFont
	 */
	public JRFont getTickLabelFont()
	{
		return tickLabelFont;
	}

	/**
	 * @param tickLabelFont the tickLabelFont to set
	 */
	public void setTickLabelFont(JRFont tickLabelFont)
	{
		JRFont old = getTickLabelFont();
		this.tickLabelFont = tickLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_tickLabelFont, old, getTickLabelFont());
	}

	/**
	 * @return the displayFont
	 */
	public JRFont getDisplayFont()
	{
		return displayFont;
	}

	/**
	 * @param displayFont the displayFont to set
	 */
	public void setDisplayFont(JRFont displayFont)
	{
		JRFont old = getDisplayFont();
		this.displayFont = displayFont;
		getEventSupport().firePropertyChange(PROPERTY_displayFont, old, getDisplayFont());
	}
	
}
