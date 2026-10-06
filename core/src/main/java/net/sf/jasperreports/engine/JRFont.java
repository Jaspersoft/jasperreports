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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.design.JRDesignFont;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * An abstract representation of a font. Fonts in JasperReports are very complex because of the library portability
 * across operating systems and export formats. This interface provides basic font functionality methods for
 * managing font attributes and special PDF font attributes.
 * <p>
 * Users can define report level fonts that can be referenced by name in text elements. Their default properties
 * can be overridden in each element (for example, a text element can use a report level font and just change its
 * "underline" attribute). All the "own" methods in this class actually return the override values of font properties.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonDeserialize(as = JRDesignFont.class)
public interface JRFont extends JRStyleContainer
{

	@Property(
		category = PropertyConstants.CATEGORY_FILL,
		defaultValue = "SansSerif",
		scopes = {PropertyScope.CONTEXT},
		sinceVersion = PropertyConstants.VERSION_1_3_0
	)
	public static final String DEFAULT_FONT_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "default.font.name";
	
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			valueType = Float.class,
			defaultValue = "10",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_3_0
	)
	public static final String DEFAULT_FONT_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "default.font.size";
	
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "Helvetica",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_3_0
	)
	public static final String DEFAULT_PDF_FONT_NAME = JRPropertiesUtil.PROPERTY_PREFIX + "default.pdf.font.name";
	
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = "Cp1252",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_3_0
	)
	public static final String DEFAULT_PDF_ENCODING = JRPropertiesUtil.PROPERTY_PREFIX + "default.pdf.encoding";
	
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_3_0
	)
	public static final String DEFAULT_PDF_EMBEDDED = JRPropertiesUtil.PROPERTY_PREFIX + "default.pdf.embedded";

	/**
	 *
	 */
	@JsonIgnore
	public String getFontName();
	
	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_fontName)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_fontName, isAttribute = true)
	public String getOwnFontName();
	
	/**
	 *
	 */
	@JsonSetter
	public void setFontName(String fontName);
	
	/**
	 *
	 */
	@JsonIgnore
	public boolean isBold();
	
	/**
	 *
	 */
	@JsonGetter("bold")
	@JacksonXmlProperty(localName = "bold", isAttribute = true)
	public Boolean isOwnBold();
	
	/**
	 *
	 */
	@JsonSetter
	public void setBold(Boolean isBold);
	
	/**
	 *
	 */
	@JsonIgnore
	public boolean isItalic();
	
	/**
	 *
	 */
	@JsonGetter("italic")
	@JacksonXmlProperty(localName = "italic", isAttribute = true)
	public Boolean isOwnItalic();
	
	/**
	 *
	 */
	@JsonSetter
	public void setItalic(Boolean isItalic);
	
	/**
	 *
	 */
	@JsonIgnore
	public boolean isUnderline();
	
	/**
	 *
	 */
	@JsonGetter("underline")
	@JacksonXmlProperty(localName = "underline", isAttribute = true)
	public Boolean isOwnUnderline();
	
	/**
	 *
	 */
	@JsonSetter
	public void setUnderline(Boolean isUnderline);
	
	/**
	 *
	 */
	@JsonIgnore
	public boolean isStrikeThrough();
	
	/**
	 *
	 */
	@JsonGetter("strikeThrough")
	@JacksonXmlProperty(localName = "strikeThrough", isAttribute = true)
	public Boolean isOwnStrikeThrough();
	
	/**
	 *
	 */
	@JsonSetter
	public void setStrikeThrough(Boolean isStrikeThrough);

	/**
	 *
	 */
	@JsonIgnore
	public float getFontSize();
	
	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_fontSize)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_fontSize, isAttribute = true)
	public Float getOwnFontSize();

	/**
	 *
	 */
	@JsonSetter
	public void setFontSize(Float size);

	/**
	 *
	 */
	@JsonIgnore
	public String getPdfFontName();
	
	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_pdfFontName)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_pdfFontName, isAttribute = true)
	public String getOwnPdfFontName();
	
	/**
	 *
	 */
	@JsonSetter
	public void setPdfFontName(String pdfFontName);

	/**
	 *
	 */
	@JsonIgnore
	public String getPdfEncoding();
	
	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_pdfEncoding)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_pdfEncoding, isAttribute = true)
	public String getOwnPdfEncoding();
	
	/**
	 *
	 */
	@JsonSetter
	public void setPdfEncoding(String pdfEncoding);

	/**
	 *
	 */
	@JsonIgnore
	public boolean isPdfEmbedded();

	/**
	 *
	 */
	@JsonGetter("pdfEmbedded")
	@JacksonXmlProperty(localName = "pdfEmbedded", isAttribute = true)
	public Boolean isOwnPdfEmbedded();
	
	/**
	 *
	 */
	@JsonSetter
	public void setPdfEmbedded(Boolean isPdfEmbedded);

}
