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

import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRTextAlignment;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.base.JRBaseParagraph;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;


/**
 * Text element information shared by multiple print text objects.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @see JRTemplatePrintText
 */
public class JRTemplateText extends JRTemplateElement implements JRTextAlignment, JRFont, JRCommonText, TextFormat
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	private HorizontalTextAlignEnum horizontalTextAlign;
	private VerticalTextAlignEnum verticalTextAlign;
	private RotationEnum rotation;
	private String markup;
	private String linkType;
	private String linkTarget;

	/**
	 *
	 */
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
	protected String valueClassName;
	protected String pattern;
	protected String formatFactoryClass;
	protected String localeCode;
	protected String timeZoneId;
	
	
	/**
	 *
	 */
	protected JRTemplateText(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider, JRStaticText staticText)
	{
		super(origin, defaultStyleProvider);
		
		setStaticText(staticText);
	}

	/**
	 *
	 */
	protected JRTemplateText(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider, JRTextField textField)
	{
		super(origin, defaultStyleProvider);
		
		setTextField(textField);
	}


	/**
	 * Creates a template text.
	 * 
	 * @param origin the origin of the elements that will use this template
	 * @param defaultStyleProvider the default style provider to use for
	 * this template
	 */
	public JRTemplateText(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider)
	{
		super(origin, defaultStyleProvider);
		
		lineBox = new JRBaseLineBox(this);
		paragraph = new JRBaseParagraph(this);
	}
	
	/**
	 *
	 */
	protected void setStaticText(JRStaticText staticText)
	{
		setTextElement(staticText);
	}

	/**
	 *
	 */
	protected void setTextField(JRTextField textField)
	{
		setTextElement(textField);

		setLinkType(textField.getLinkType());
		setLinkTarget(textField.getLinkTarget());
	}

	/**
	 *
	 */
	protected void setTextElement(JRTextElement textElement)
	{
		super.setElement(textElement);
		
		fontName = textElement.getOwnFontName();
		isBold = textElement.isOwnBold();
		isItalic = textElement.isOwnItalic();
		isUnderline = textElement.isOwnUnderline();
		isStrikeThrough = textElement.isOwnStrikeThrough();
		fontSize = textElement.getOwnFontSize();
		pdfFontName = textElement.getOwnPdfFontName();
		pdfEncoding = textElement.getOwnPdfEncoding();
		isPdfEmbedded = textElement.isOwnPdfEmbedded();

		horizontalTextAlign = textElement.getOwnHorizontalTextAlign();
		verticalTextAlign = textElement.getOwnVerticalTextAlign();
		rotation = textElement.getOwnRotation();
		markup = textElement.getOwnMarkup();
	}

	public void setTextFormat(TextFormat textFormat)
	{
		if (textFormat != null)
		{
			setValueClassName(textFormat.getValueClassName());
			setPattern(textFormat.getPattern());
			setFormatFactoryClass(textFormat.getFormatFactoryClass());
			setLocaleCode(textFormat.getLocaleCode());
			setTimeZoneId(textFormat.getTimeZoneId());
		}
	}

	/**
	 * Copies box attributes.
	 * 
	 * @param box the object to copy attributes from
	 */
	public void copyLineBox(JRLineBox box)
	{
		lineBox = box.clone(this);
	}

	/**
	 * Copies paragraph attributes.
	 * 
	 * @param prg the object to copy attributes from
	 */
	public void copyParagraph(JRParagraph prg)
	{
		paragraph = prg.clone(this);
	}

	
	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
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
	public void setHorizontalTextAlign(HorizontalTextAlignEnum horizontalTextAlign)
	{
		this.horizontalTextAlign = horizontalTextAlign;
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
	public void setVerticalTextAlign(VerticalTextAlignEnum verticalTextAlign)
	{
		this.verticalTextAlign = verticalTextAlign;
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

	/**
	 * Sets the text rotation.
	 * 
	 * @param rotation one of
	 * 	<ul>
	 * 		<li>{@link RotationEnum#NONE}</li>
	 * 		<li>{@link RotationEnum#LEFT}</li>
	 * 		<li>{@link RotationEnum#RIGHT}</li>
	 * 		<li>{@link RotationEnum#UPSIDE_DOWN}</li>
	 * 	</ul>
	 * values, or <code>null</code> if this template
	 * should not specify a rotation attribute of its own
	 */
	@Override
	public void setRotation(RotationEnum rotation)
	{
		this.rotation = rotation;
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

	/**
	 * Sets the text markup attribute.
	 * 
	 * @param markup the markup attribute
	 * @see #getMarkup()
	 */
	@Override
	public void setMarkup(String markup)
	{
		this.markup = markup;
	}
	
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}
		
	@Override
	public JRParagraph getParagraph()
	{
		return paragraph;
	}

	
	/**
	 * Retrieves the hyperlink type for the element.
	 * <p>
	 * The actual hyperlink type is determined by {@link #getLinkType() getLinkType()}.
	 * This method can is used to determine whether the hyperlink type is one of the
	 * built-in types or a custom type. 
O	 * When hyperlink is of custom type, {@link HyperlinkTypeEnum#CUSTOM CUSTOM} is returned.
	 * </p>
	 * @return one of the hyperlink type constants
	 * @see #getLinkType()
	 */
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(getLinkType());
	}
	
	/**
	 * Retrieves the hyperlink target name for the element.
	 * <p>
	 * The actual hyperlink target name is determined by {@link #getLinkTarget() getLinkTarget()}.
	 * This method is used to determine whether the hyperlink target name is one of the
	 * built-in names or a custom one. 
	 * When hyperlink target has a custom name, {@link HyperlinkTargetEnum#CUSTOM CUSTOM} is returned.
	 * </p>
	 * @return one of the hyperlink target name constants
	 * @see #getLinkTarget()
	 */
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(getLinkTarget());
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
	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}


	@Override
	public boolean isBold()
	{
		return getStyleResolver().isBold(this);
	}

	@Override
	public Boolean isOwnBold()
	{
		return isBold;
	}

	/**
	 * Alternative setBold method which allows also to reset
	 * the "own" isBold property.
	 */
	@Override
	public void setBold(Boolean isBold)
	{
		this.isBold = isBold;
	}


	@Override
	public boolean isItalic()
	{
		return getStyleResolver().isItalic(this);
	}

	@Override
	public Boolean isOwnItalic()
	{
		return isItalic;
	}

	/**
	 * Alternative setItalic method which allows also to reset
	 * the "own" isItalic property.
	 */
	@Override
	public void setItalic(Boolean isItalic)
	{
		this.isItalic = isItalic;
	}

	@Override
	public boolean isUnderline()
	{
		return getStyleResolver().isUnderline(this);
	}

	@Override
	public Boolean isOwnUnderline()
	{
		return isUnderline;
	}

	/**
	 * Alternative setUnderline method which allows also to reset
	 * the "own" isUnderline property.
	 */
	@Override
	public void setUnderline(Boolean isUnderline)
	{
		this.isUnderline = isUnderline;
	}

	@Override
	public boolean isStrikeThrough()
	{
		return getStyleResolver().isStrikeThrough(this);
	}

	@Override
	public Boolean isOwnStrikeThrough()
	{
		return isStrikeThrough;
	}

	/**
	 * Alternative setStrikeThrough method which allows also to reset
	 * the "own" isStrikeThrough property.
	 */
	@Override
	public void setStrikeThrough(Boolean isStrikeThrough)
	{
		this.isStrikeThrough = isStrikeThrough;
	}

	@Override
	public float getFontSize()
	{
		return getStyleResolver().getFontSize(this);
	}

	@Override
	public Float getOwnFontSize()
	{
		return fontSize;
	}

	/**
	 * Method which allows also to reset the "own" size property.
	 */
	@Override
	public void setFontSize(Float fontSize)
	{
		this.fontSize = fontSize;
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
	public void setPdfFontName(String pdfFontName)
	{
		this.pdfFontName = pdfFontName;
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
	public void setPdfEncoding(String pdfEncoding)
	{
		this.pdfEncoding = pdfEncoding;
	}


	@Override
	public boolean isPdfEmbedded()
	{
		return getStyleResolver().isPdfEmbedded(this);
	}

	@Override
	public Boolean isOwnPdfEmbedded()
	{
		return isPdfEmbedded;
	}

	/**
	 * Alternative setPdfEmbedded method which allows also to reset
	 * the "own" isPdfEmbedded property.
	 */
	@Override
	public void setPdfEmbedded(Boolean isPdfEmbedded)
	{
		this.isPdfEmbedded = isPdfEmbedded;
	}

	@Override
	public JRStyle getStyle()
	{
		return parentStyle;
	}

	
	@Override
	public String getPattern()
	{
		return pattern;
	}

	
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	
	@Override
	public String getValueClassName()
	{
		return valueClassName;
	}

	
	public void setValueClassName(String valueClassName)
	{
		this.valueClassName = valueClassName;
	}

	
	@Override
	public String getFormatFactoryClass()
	{
		return formatFactoryClass;
	}

	
	public void setFormatFactoryClass(String formatFactoryClass)
	{
		this.formatFactoryClass = formatFactoryClass;
	}

	
	@Override
	public String getLocaleCode()
	{
		return localeCode;
	}

	
	public void setLocaleCode(String localeCode)
	{
		this.localeCode = localeCode;
	}

	
	@Override
	public String getTimeZoneId()
	{
		return timeZoneId;
	}

	
	public void setTimeZoneId(String timeZoneId)
	{
		this.timeZoneId = timeZoneId;
	}

	
	/**
	 * Returns the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @return the hyperlink type
	 */
	public String getLinkType()
	{
		return linkType;
	}


	/**
	 * Sets the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @param linkType the hyperlink type
	 */
	public void setLinkType(String linkType)
	{
		this.linkType = linkType;
	}
	
	/**
	 *
	 */
	protected void setLinkTarget(String linkTarget)
	{
		this.linkTarget = linkTarget;
	}

	
	/**
	 * Returns the hyperlink target name.
	 * <p>
	 * The target name can be one of the built-in names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary name.
	 * </p>
	 * @return the hyperlink type
	 */
	public String getLinkTarget()
	{
		return linkTarget;
	}


	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}
	

	@Override
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		addTemplateHash(hash);
		hash.add(horizontalTextAlign);
		hash.add(verticalTextAlign);
		hash.add(rotation);
		hash.add(markup);
		hash.add(linkType);
		hash.add(linkTarget);
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
		hash.add(valueClassName);
		hash.add(pattern);
		hash.add(formatFactoryClass);
		hash.add(localeCode);
		hash.add(timeZoneId);
		return hash.getHashCode();
	}

	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRTemplateText))
		{
			return false;
		}
		
		JRTemplateText template = (JRTemplateText) object;
		return templateIdentical(template)
				&& ObjectUtils.equals(horizontalTextAlign, template.horizontalTextAlign)
				&& ObjectUtils.equals(verticalTextAlign, template.verticalTextAlign)
				&& ObjectUtils.equals(rotation, template.rotation)
				&& ObjectUtils.equals(markup, template.markup)
				&& ObjectUtils.equals(linkType, template.linkType)
				&& ObjectUtils.equals(linkTarget, template.linkTarget)
				&& ObjectUtils.identical(lineBox, template.lineBox)
				&& ObjectUtils.identical(paragraph, template.paragraph)
				&& ObjectUtils.equals(fontName, template.fontName)
				&& ObjectUtils.equals(isBold, template.isBold)
				&& ObjectUtils.equals(isItalic, template.isItalic)
				&& ObjectUtils.equals(isUnderline, template.isUnderline)
				&& ObjectUtils.equals(isStrikeThrough, template.isStrikeThrough)
				&& ObjectUtils.equals(fontSize, template.fontSize)
				&& ObjectUtils.equals(pdfFontName, template.pdfFontName)
				&& ObjectUtils.equals(pdfEncoding, template.pdfEncoding)
				&& ObjectUtils.equals(isPdfEmbedded, template.isPdfEmbedded)
				&& ObjectUtils.equals(valueClassName, template.valueClassName)
				&& ObjectUtils.equals(pattern, template.pattern)
				&& ObjectUtils.equals(formatFactoryClass, template.formatFactoryClass)
				&& ObjectUtils.equals(localeCode, template.localeCode)
				&& ObjectUtils.equals(timeZoneId, template.timeZoneId);
	}
	
	@Override
	public void populateStyle()
	{
		super.populateStyle();
		
		fontName = getFontName();
		isBold = isBold();
		isItalic = isItalic();
		isUnderline = isUnderline();
		isStrikeThrough = isStrikeThrough();
		fontSize = getFontSize();
		pdfFontName = getPdfFontName();
		pdfEncoding = getPdfEncoding();
		isPdfEmbedded = isPdfEmbedded();

		horizontalTextAlign = getHorizontalTextAlign();
		verticalTextAlign = getVerticalTextAlign();
		rotation = getRotation();
		markup = getMarkup();
		
		if (paragraph != null)
		{
			paragraph.populateStyle();
		}
		
		if (lineBox != null)
		{
			lineBox.populateStyle();
		}
	}
}
