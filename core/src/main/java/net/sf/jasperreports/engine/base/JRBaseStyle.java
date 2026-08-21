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

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.Deduplicable;
import net.sf.jasperreports.engine.JRAbstractObjectFactory;
import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleSetter;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.FillEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.util.ObjectUtils;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRBaseStyle implements JRStyle, Serializable, JRChangeEventsSupport, Deduplicable
{
	public static final String EXCEPTION_MESSAGE_KEY_CIRCULAR_DEPENDENCY = "engine.style.circular.dependency";
	
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_BACKCOLOR = "backcolor";
	
	public static final String PROPERTY_BLANK_WHEN_NULL = "isBlankWhenNull";
	
	public static final String PROPERTY_BOLD = "isBold";
	
	public static final String PROPERTY_FILL = "fill";
	
	public static final String PROPERTY_FONT_NAME = "fontName";
	
	public static final String PROPERTY_FONT_SIZE = "fontSize";
	
	public static final String PROPERTY_FORECOLOR = "forecolor";
	
	public static final String PROPERTY_HORIZONTAL_TEXT_ALIGNMENT = "horizontalTextAlignment";

	public static final String PROPERTY_HORIZONTAL_IMAGE_ALIGNMENT = "horizontalImageAlignment";

	public static final String PROPERTY_ITALIC = "isItalic";
	
	public static final String PROPERTY_MODE = "mode";
	
	public static final String PROPERTY_PATTERN = "pattern";
	
	public static final String PROPERTY_PDF_EMBEDDED = "isPdfEmbedded";
	
	public static final String PROPERTY_PDF_ENCODING = "pdfEncoding";
	
	public static final String PROPERTY_PDF_FONT_NAME = "pdfFontName";
	
	public static final String PROPERTY_RADIUS = "radius";
	
	public static final String PROPERTY_ROTATION = "rotation";
	
	public static final String PROPERTY_SCALE_IMAGE = "scaleImage";
	
	public static final String PROPERTY_STRIKE_THROUGH = "isStrikeThrough";
	
	public static final String PROPERTY_MARKUP = "markup";
	
	public static final String PROPERTY_UNDERLINE = "isUnderline";
	
	public static final String PROPERTY_VERTICAL_TEXT_ALIGNMENT = "verticalTextAlignment";
	
	public static final String PROPERTY_VERTICAL_IMAGE_ALIGNMENT = "verticalImageAlignment";


	/**
	 *
	 */
	protected final JRDefaultStyleProvider defaultStyleProvider;
	protected JRStyle parentStyle;
	protected String parentStyleNameReference;

	/**
	 *
	 */
	protected String name;
	protected boolean isDefault;

	protected ModeEnum mode;
	protected Color forecolor;
	protected Color backcolor;

	protected JRPen linePen;
	protected FillEnum fill;

	protected Integer radius;

	protected ScaleImageEnum scaleImage;
	protected HorizontalTextAlignEnum horizontalTextAlign;
	protected VerticalTextAlignEnum verticalTextAlign;
	protected HorizontalImageAlignEnum horizontalImageAlign;
	protected VerticalImageAlignEnum verticalImageAlign;

	protected JRLineBox lineBox;
	protected JRParagraph paragraph;

	protected String fontName;
	protected Boolean isBold;
	protected Boolean isItalic;
	protected Boolean isUnderline;
	protected Boolean isStrikeThrough;
	protected Float fontSize;
	protected String pdfFontName;
	protected String pdfEncoding;
	protected Boolean isPdfEmbedded;

	protected RotationEnum rotation;
	protected String markup;

	protected String pattern;
	protected Boolean isBlankWhenNull;

	protected JRConditionalStyle[] conditionalStyles;


	/**
	 *
	 */
	public JRBaseStyle()
	{
		this((JRDefaultStyleProvider)null);
	}

	/**
	 *
	 */
	public JRBaseStyle(JRDefaultStyleProvider defaultStyleProvider)
	{
		this.defaultStyleProvider = defaultStyleProvider;
		
		linePen = new JRBasePen(this);
		lineBox = new JRBaseLineBox(this);
		paragraph = new JRBaseParagraph(this);
	}

	/**
	 *
	 */
	public JRBaseStyle(String name)
	{
		this((JRDefaultStyleProvider)null);
		
		this.name = name;
	}

	/**
	 *
	 */
	public JRBaseStyle(JRDefaultStyleProvider defaultStyleProvider, String name)
	{
		this(defaultStyleProvider);
		
		this.name = name;
	}

	/**
	 *
	 */
	public JRBaseStyle(JRStyle style, JRAbstractObjectFactory factory)
	{
		name = style.getName();
		
		defaultStyleProvider = factory.getDefaultStyleProvider();
		
		factory.setStyle(new JRStyleSetter()
		{
			@Override
			public void setStyle(JRStyle aStyle)
			{
				setParentStyle(aStyle);
			}

			@Override
			public void setStyleNameReference(String name)
			{
				parentStyleNameReference = name;
			}
		}, style);
		
		isDefault = style.isDefault();

		mode = style.getOwnMode();
		forecolor = style.getOwnForecolor();
		backcolor = style.getOwnBackcolor();

		linePen = style.getLinePen().clone(this);
		fill = style.getOwnFill();

		radius = style.getOwnRadius();

		scaleImage = style.getOwnScaleImage();
		horizontalTextAlign = style.getOwnHorizontalTextAlign();
		verticalTextAlign = style.getOwnVerticalTextAlign();
		horizontalImageAlign = style.getOwnHorizontalImageAlign();
		verticalImageAlign = style.getOwnVerticalImageAlign();

		lineBox = style.getLineBox().clone(this);
		paragraph = style.getParagraph().clone(this);

		rotation = style.getOwnRotation();
		markup = style.getOwnMarkup();

		pattern = style.getOwnPattern();

		fontName = style.getOwnFontName();
		isBold = style.isOwnBold();
		isItalic = style.isOwnItalic();
		isUnderline = style.isOwnUnderline();
		isStrikeThrough = style.isOwnStrikeThrough();
		fontSize = style.getOwnFontSize();
		pdfFontName = style.getOwnPdfFontName();
		pdfEncoding = style.getOwnPdfEncoding();
		isPdfEmbedded = style.isOwnPdfEmbedded();
		
		isBlankWhenNull = style.isOwnBlankWhenNull();

		JRConditionalStyle[] condStyles = style.getConditionalStyles();
		if (condStyles != null && condStyles.length > 0) {
			this.conditionalStyles = new JRConditionalStyle[condStyles.length];
			for (int i = 0; i < condStyles.length; i++) {
				this.conditionalStyles[i] = factory.getConditionalStyle(condStyles[i], this);
			}
		}
	}

	protected void setParentStyle(JRStyle parentStyle)
	{
		this.parentStyle = parentStyle;
		checkCircularParent();
	}
	
	protected void checkCircularParent()
	{
		for (JRStyle ancestor = parentStyle; ancestor != null; ancestor = ancestor.getStyle())
		{
			if (ancestor == this)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_CIRCULAR_DEPENDENCY,
						new Object[]{getName()});
			}
		}
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

	@Override
	public String getName()
	{
		return name;
	}

	/**
	 * Changes the name of this style.
	 * <p/>
	 * Note that this method is mostly meant to be used internally.
	 * Use cautiously as it might have unexpected consequences.
	 * 
	 * @param newName the new name
	 */
	public void rename(String newName)
	{
		this.name = newName;
	}
	
	@Override
	public boolean isDefault()
	{
		return isDefault;
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
	public JRPen getLinePen()
	{
		return linePen;
	}

	@JsonSetter
	private void setLinePen(JRPen linePen)
	{
		this.linePen = linePen.clone(this);
	}

	@Override
	public FillEnum getFill()
	{
		return getStyleResolver().getFill(this);
	}

	@Override
	public FillEnum getOwnFill()
	{
		return fill;
	}

	@Override
	public Integer getRadius()
	{
		return getStyleResolver().getRadius(this);
	}

	@Override
	public Integer getOwnRadius()
	{
		return radius;
	}

	@Override
	public ScaleImageEnum getScaleImage()
	{
		return getStyleResolver().getScaleImage(this);
	}

	@Override
	public ScaleImageEnum getOwnScaleImage()
	{
		return this.scaleImage;
	}

	@Override
	public HorizontalTextAlignEnum getHorizontalTextAlign()
	{
		return getStyleResolver().getHorizontalTextAlign(this);
	}
		
	@Override
	public HorizontalTextAlignEnum getOwnHorizontalTextAlign()
	{
		return horizontalTextAlign;
	}
		
	@Override
	public VerticalTextAlignEnum getVerticalTextAlign()
	{
		return getStyleResolver().getVerticalTextAlign(this);
	}
		
	@Override
	public VerticalTextAlignEnum getOwnVerticalTextAlign()
	{
		return verticalTextAlign;
	}

	@Override
	public HorizontalImageAlignEnum getHorizontalImageAlign()
	{
		return getStyleResolver().getHorizontalImageAlign(this);
	}
		
	@Override
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign()
	{
		return horizontalImageAlign;
	}
		
	@Override
	public VerticalImageAlignEnum getVerticalImageAlign()
	{
		return getStyleResolver().getVerticalImageAlign(this);
	}
		
	@Override
	public VerticalImageAlignEnum getOwnVerticalImageAlign()
	{
		return verticalImageAlign;
	}
		
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	@JsonSetter(JRXmlConstants.ELEMENT_box)
	private void setLineBox(JRLineBox lineBox)
	{
		this.lineBox = lineBox == null ? null : lineBox.clone(this);
	}

	@Override
	public JRParagraph getParagraph()
	{
		return paragraph;
	}

	@JsonSetter
	private void setParagraph(JRParagraph paragraph)
	{
		this.paragraph = paragraph;
	}

	@Override
	public RotationEnum getRotation()
	{
		return getStyleResolver().getRotation(this);
	}

	@Override
	public RotationEnum getOwnRotation()
	{
		return this.rotation;
	}

	@Override
	public void setRotation(RotationEnum rotation)
	{
		Object old = this.rotation;
		this.rotation = rotation;
		getEventSupport().firePropertyChange(PROPERTY_ROTATION, old, this.rotation);
	}

	@Override
	public String getMarkup()
	{
		return getStyleResolver().getMarkup(this);
	}

	@Override
	public String getOwnMarkup()
	{
		return markup;
	}

	@Override
	public Boolean isBlankWhenNull()
	{
		return getStyleResolver().isBlankWhenNull(this);
	}

	@Override
	public Boolean isOwnBlankWhenNull()
	{
		return isBlankWhenNull;
	}


	@Override
	public String getFontName()
	{
		return getStyleResolver().getFontName(this);
	}

	@Override
	public String getOwnFontName()
	{
		return fontName;
	}

	@Override
	public Boolean isBold()
	{
		return getStyleResolver().isBold(this);
	}

	@Override
	public Boolean isOwnBold()
	{
		return isBold;
	}

	@Override
	public Boolean isItalic()
	{
		return getStyleResolver().isItalic(this);
	}

	@Override
	public Boolean isOwnItalic()
	{
		return isItalic;
	}

	@Override
	public Boolean isUnderline()
	{
		return getStyleResolver().isUnderline(this);
	}

	@Override
	public Boolean isOwnUnderline()
	{
		return isUnderline;
	}

	@Override
	public Boolean isStrikeThrough()
	{
		return getStyleResolver().isStrikeThrough(this);
	}

	@Override
	public Boolean isOwnStrikeThrough()
	{
		return isStrikeThrough;
	}

	@Override
	public Float getFontSize()
	{
		return getStyleResolver().getFontSize(this);
	}

	@Override
	public Float getOwnFontSize()
	{
		return fontSize;
	}

	@Override
	public String getPdfFontName()
	{
		return getStyleResolver().getPdfFontName(this);
	}

	@Override
	public String getOwnPdfFontName()
	{
		return pdfFontName;
	}

	@Override
	public String getPdfEncoding()
	{
		return getStyleResolver().getPdfEncoding(this);
	}

	@Override
	public String getOwnPdfEncoding()
	{
		return pdfEncoding;
	}

	@Override
	public Boolean isPdfEmbedded()
	{
		return getStyleResolver().isPdfEmbedded(this);
	}

	@Override
	public Boolean isOwnPdfEmbedded()
	{
		return isPdfEmbedded;
	}

	@Override
	public String getPattern()
	{
		return getStyleResolver().getPattern(this);
	}

	@Override
	public String getOwnPattern()
	{
		return pattern;
	}

	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getModeValue(this);
	}

	@Override
	public ModeEnum getOwnMode()
	{
		return mode;
	}

	@Override
	public void setForecolor(Color forecolor)
	{
		Object old = this.forecolor;
		this.forecolor = forecolor;
		getEventSupport().firePropertyChange(PROPERTY_FORECOLOR, old, this.forecolor);
	}

	@Override
	public void setBackcolor(Color backcolor)
	{
		Object old = this.backcolor;
		this.backcolor = backcolor;
		getEventSupport().firePropertyChange(PROPERTY_BACKCOLOR, old, this.backcolor);
	}

	@Override
	public void setMode(ModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_MODE, old, this.mode);
	}

	@Override
	public void setFill(FillEnum fill)
	{
		Object old = this.fill;
		this.fill = fill;
		getEventSupport().firePropertyChange(PROPERTY_FILL, old, this.fill);
	}

	@Override
	public void setRadius(Integer radius)
	{
		Object old = this.radius;
		this.radius = radius;
		getEventSupport().firePropertyChange(PROPERTY_RADIUS, old, this.radius);
	}

	@Override
	public void setScaleImage(ScaleImageEnum scaleImage)
	{
		Object old = this.scaleImage;
		this.scaleImage = scaleImage;
		getEventSupport().firePropertyChange(PROPERTY_SCALE_IMAGE, old, this.scaleImage);
	}

	@Override
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalImageAlign)
	{
		HorizontalImageAlignEnum old = this.horizontalImageAlign;
		this.horizontalImageAlign = horizontalImageAlign;
		getEventSupport().firePropertyChange(PROPERTY_HORIZONTAL_IMAGE_ALIGNMENT, old, this.horizontalImageAlign);
	}

	@Override
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalImageAlign)
	{
		VerticalImageAlignEnum old = this.verticalImageAlign;
		this.verticalImageAlign = verticalImageAlign;
		getEventSupport().firePropertyChange(PROPERTY_VERTICAL_IMAGE_ALIGNMENT, old, this.verticalImageAlign);
	}

	@Override
	public void setHorizontalTextAlign(HorizontalTextAlignEnum horizontalTextAlign)
	{
		HorizontalTextAlignEnum old = this.horizontalTextAlign;
		this.horizontalTextAlign = horizontalTextAlign;
		getEventSupport().firePropertyChange(PROPERTY_HORIZONTAL_TEXT_ALIGNMENT, old, this.horizontalTextAlign);
	}

	@Override
	public void setVerticalTextAlign(VerticalTextAlignEnum verticalTextAlign)
	{
		VerticalTextAlignEnum old = this.verticalTextAlign;
		this.verticalTextAlign = verticalTextAlign;
		getEventSupport().firePropertyChange(PROPERTY_VERTICAL_TEXT_ALIGNMENT, old, this.verticalTextAlign);
	}

	@Override
	public void setFontName(String fontName)
	{
		Object old = this.fontName;
		this.fontName = fontName;
		getEventSupport().firePropertyChange(PROPERTY_FONT_NAME, old, this.fontName);
	}

	@Override
	public void setBold(Boolean bold)
	{
		Object old = this.isBold;
		this.isBold = bold;
		getEventSupport().firePropertyChange(PROPERTY_BOLD, old, this.isBold);
	}

	@Override
	public void setItalic(Boolean italic)
	{
		Object old = this.isItalic;
		this.isItalic = italic;
		getEventSupport().firePropertyChange(PROPERTY_ITALIC, old, this.isItalic);
	}

	@Override
	public void setPdfEmbedded(Boolean pdfEmbedded)
	{
		Object old = this.isPdfEmbedded;
		this.isPdfEmbedded = pdfEmbedded;
		getEventSupport().firePropertyChange(PROPERTY_PDF_EMBEDDED, old, this.isPdfEmbedded);
	}

	@Override
	public void setStrikeThrough(Boolean strikeThrough)
	{
		Object old = this.isStrikeThrough;
		this.isStrikeThrough = strikeThrough;
		getEventSupport().firePropertyChange(PROPERTY_STRIKE_THROUGH, old, this.isStrikeThrough);
	}

	@Override
	public void setMarkup(String markup)
	{
		Object old = this.markup;
		this.markup = markup;
		getEventSupport().firePropertyChange(PROPERTY_MARKUP, old, this.markup);
	}

	@Override
	public void setBlankWhenNull(Boolean isBlankWhenNull)
	{
		Object old = this.isBlankWhenNull;
		this.isBlankWhenNull = isBlankWhenNull;
		getEventSupport().firePropertyChange(PROPERTY_BLANK_WHEN_NULL, old, this.isBlankWhenNull);
	}

	@Override
	public void setUnderline(Boolean underline)
	{
		Object old = this.isUnderline;
		this.isUnderline = underline;
		getEventSupport().firePropertyChange(PROPERTY_UNDERLINE, old, this.isUnderline);
	}

	@Override
	public void setPattern(String pattern)
	{
		Object old = this.pattern;
		this.pattern = pattern;
		getEventSupport().firePropertyChange(PROPERTY_PATTERN, old, this.pattern);
	}

	@Override
	public void setPdfEncoding(String pdfEncoding)
	{
		Object old = this.pdfEncoding;
		this.pdfEncoding = pdfEncoding;
		getEventSupport().firePropertyChange(PROPERTY_PDF_ENCODING, old, this.pdfEncoding);
	}

	@Override
	public void setPdfFontName(String pdfFontName)
	{
		Object old = this.pdfFontName;
		this.pdfFontName = pdfFontName;
		getEventSupport().firePropertyChange(PROPERTY_PDF_FONT_NAME, old, this.pdfFontName);
	}

	@Override
	public void setFontSize(Float fontSize)
	{
		Object old = this.fontSize;
		this.fontSize = fontSize;
		getEventSupport().firePropertyChange(PROPERTY_FONT_SIZE, old, this.fontSize);
	}

	@Override
	public JRConditionalStyle[] getConditionalStyles()
	{
		return conditionalStyles;
	}

	@Override
	public String getStyleNameReference()
	{
		return parentStyleNameReference;
	}
	
	@Override
	public Float getDefaultLineWidth()
	{
		return null;
	}
	
	@Override
	public Color getDefaultLineColor()
	{
		return getForecolor();
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
		JRBaseStyle clone = null;
		try
		{
			clone = (JRBaseStyle) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.lineBox = lineBox == null ? null : lineBox.clone(clone);
		clone.linePen = linePen == null ? null : linePen.clone(clone);
		clone.paragraph = paragraph == null ? null : paragraph.clone(clone);
		clone.conditionalStyles = JRCloneUtils.cloneArray(conditionalStyles);
		clone.eventSupport = null;

		return clone;
	}

	@Override
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		hash.addIdentity(parentStyle);
		addStyleHash(hash);
		
		// maybe adding conditional style to the hash is not worth it
		// as the remaining attributes provide good enough hash information
		hash.addIdentical(conditionalStyles);
		
		return hash.getHashCode();
	}

	protected void addStyleHash(ObjectUtils.HashCode hash)
	{
		hash.add(name);
		hash.add(isDefault);
		hash.add(mode);
		hash.add(forecolor);
		hash.add(backcolor);
		hash.addIdentical(linePen); 
		hash.add(fill);
		hash.add(radius);
		hash.add(scaleImage);
		hash.add(horizontalTextAlign);
		hash.add(verticalTextAlign);
		hash.add(horizontalImageAlign);
		hash.add(verticalImageAlign);
		hash.addIdentical(lineBox); 
		hash.addIdentical(paragraph); 
		hash.add(fontName);
		hash.add(isBold);
		hash.add(isItalic);
		hash.add(isUnderline);
		hash.add(isStrikeThrough);
		hash.add(fontSize);
		hash.add(pdfFontName);
		hash.add(pdfEncoding);
		hash.add(isPdfEmbedded);
		hash.add(rotation);
		hash.add(markup);
		hash.add(pattern);
		hash.add(isBlankWhenNull);
	}

	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRBaseStyle))
		{
			return false;
		}
		
		JRBaseStyle style = (JRBaseStyle) object;

		return
				ObjectUtils.equalsIdentity(parentStyle, style.parentStyle)
				&& identicalStyle(style)
				&& ObjectUtils.identical(conditionalStyles, style.conditionalStyles);
	}

	protected boolean identicalStyle(JRBaseStyle style)
	{
		return 
				ObjectUtils.equals(name, style.name)
				&& ObjectUtils.equals(isDefault, style.isDefault)
				&& ObjectUtils.equals(mode, style.mode)
				&& ObjectUtils.equals(forecolor, style.forecolor)
				&& ObjectUtils.equals(backcolor, style.backcolor)
				&& ObjectUtils.identical(linePen, style.linePen)
				&& ObjectUtils.equals(fill, style.fill)
				&& ObjectUtils.equals(radius, style.radius)
				&& ObjectUtils.equals(scaleImage, style.scaleImage)
				&& ObjectUtils.equals(horizontalTextAlign, style.horizontalTextAlign)
				&& ObjectUtils.equals(verticalTextAlign, style.verticalTextAlign)
				&& ObjectUtils.equals(horizontalImageAlign, style.horizontalImageAlign)
				&& ObjectUtils.equals(verticalImageAlign, style.verticalImageAlign)
				&& ObjectUtils.identical(lineBox, style.lineBox)
				&& ObjectUtils.identical(paragraph, style.paragraph)
				&& ObjectUtils.equals(fontName, style.fontName)
				&& ObjectUtils.equals(isBold, style.isBold)
				&& ObjectUtils.equals(isItalic, style.isItalic)
				&& ObjectUtils.equals(isUnderline, style.isUnderline)
				&& ObjectUtils.equals(isStrikeThrough, style.isStrikeThrough)
				&& ObjectUtils.equals(fontSize, style.fontSize)
				&& ObjectUtils.equals(pdfFontName, style.pdfFontName)
				&& ObjectUtils.equals(pdfEncoding, style.pdfEncoding)
				&& ObjectUtils.equals(isPdfEmbedded, style.isPdfEmbedded)
				&& ObjectUtils.equals(rotation, style.rotation)
				&& ObjectUtils.equals(markup, style.markup)
				&& ObjectUtils.equals(pattern, style.pattern)
				&& ObjectUtils.equals(isBlankWhenNull, style.isBlankWhenNull);
	}
}
