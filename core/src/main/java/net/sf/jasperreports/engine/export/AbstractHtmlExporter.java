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
package net.sf.jasperreports.engine.export;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintElementIndex;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.FontInfo;
import net.sf.jasperreports.engine.fonts.FontSetInfo;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.export.CommonExportConfiguration;
import net.sf.jasperreports.export.HtmlExporterConfiguration;
import net.sf.jasperreports.export.HtmlExporterOutput;
import net.sf.jasperreports.export.HtmlReportConfiguration;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractHtmlExporter<RC extends HtmlReportConfiguration, C extends HtmlExporterConfiguration> extends JRAbstractExporter<RC, C, HtmlExporterOutput, JRHtmlExporterContext>
{
	/**
	 *
	 */
	protected static final String CSS_TEXT_ALIGN_LEFT = "left";
	protected static final String CSS_TEXT_ALIGN_RIGHT = "right";
	protected static final String CSS_TEXT_ALIGN_CENTER = "center";
	protected static final String CSS_TEXT_ALIGN_JUSTIFY = "justify";

	/**
	 *
	 */
	protected static final String HTML_VERTICAL_ALIGN_TOP = "top";
	protected static final String HTML_VERTICAL_ALIGN_MIDDLE = "middle";
	protected static final String HTML_VERTICAL_ALIGN_BOTTOM = "bottom";

	/**
	 *
	 */
	public static final String JR_PAGE_ANCHOR_PREFIX = "JR_PAGE_ANCHOR_";
	protected static final String JR_BOOKMARK_ANCHOR_PREFIX = "JR_BKMRK_";

	protected static final float DEFAULT_ZOOM = 1f;
	
	public static final String IMAGE_NAME_PREFIX = "img_";
	protected static final int IMAGE_NAME_PREFIX_LEGTH = IMAGE_NAME_PREFIX.length();
	
	protected Map<String, HtmlFontFamily> fontsToProcess;
	
	/**
	 * 
	 */
	public AbstractHtmlExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}


	/**
	 * 
	 */
	public static JRPrintImage getImage(List<JasperPrint> jasperPrintList, String imageName)
	{
		return getImage(jasperPrintList, getPrintElementIndex(imageName));
	}

	
	/**
	 * 
	 */
	public static String getImageName(JRPrintElementIndex printElementIndex, String fileExtension)
	{
		return IMAGE_NAME_PREFIX + printElementIndex.toString() + (fileExtension == null ? "" : ("." + fileExtension));
	}

	
	/**
	 * 
	 */
	public static JRPrintImage getImage(List<JasperPrint> jasperPrintList, JRPrintElementIndex imageIndex)
	{
		JasperPrint report = jasperPrintList.get(imageIndex.getReportIndex());
		JRPrintPage page = report.getPages().get(imageIndex.getPageIndex());

		Integer[] elementIndexes = imageIndex.getAddressArray();
		Object element = page.getElements().get(elementIndexes[0]);

		for (int i = 1; i < elementIndexes.length; ++i)
		{
			JRPrintFrame frame = (JRPrintFrame) element;
			element = frame.getElements().get(elementIndexes[i]);
		}

		return (JRPrintImage) element;
	}

	
	/**
	 *
	 */
	public static JRPrintElementIndex getPrintElementIndex(String imageName)
	{
		if (!imageName.startsWith(IMAGE_NAME_PREFIX))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INVALID_IMAGE_NAME,
					new Object[]{imageName});
		}

		int fileExtensionStart = imageName.lastIndexOf('.');
		fileExtensionStart = fileExtensionStart < 0 ? imageName.length() : fileExtensionStart;
		
		return JRPrintElementIndex.parsePrintElementIndex(imageName.substring(IMAGE_NAME_PREFIX_LEGTH, fileExtensionStart));
	}
	
	protected String resolveFontFamily(Map<Attribute, Object> attributes, Locale locale)
	{
		String fontFamilyAttr = (String)attributes.get(TextAttribute.FAMILY);
		FontInfo fontInfo = (FontInfo) attributes.get(JRTextAttribute.FONT_INFO);
		
		String defaultFontFamily;
		if (fontInfo == null) // if export font mapping was found at the time of styled text processing, then no FONT_INFO is set, so we need to look for it again
		{
			//no resolved font, using the family
			defaultFontFamily = fontFamilyAttr;
			//check if the family is an extension font family
			fontInfo = fontUtil.getFontInfo(fontFamilyAttr, locale);
		}
		else
		{
			//we have an already resolved font, using it
			defaultFontFamily = fontInfo.getFontFamily().getName();
		}
		
		String exportFont = null;
		if (fontInfo == null)
		{
			//we don't have a resolved font family, check if it's a font set
			FontSetInfo fontSetInfo = fontUtil.getFontSetInfo(fontFamilyAttr, locale, true);
			if (fontSetInfo != null)
			{
				//it's a font set, check the font mapping
				exportFont = fontSetInfo.getFontSet().getExportFont(getExporterKey());
			}
		}
		else
		{
			//it's a font family, check the font mapping
			exportFont = fontInfo.getFontFamily().getExportFont(getExporterKey());
		}

		//by default the font family is used
		String fontFamily = defaultFontFamily;
		if (exportFont != null)
		{
			//we have a font mapping
			fontFamily = exportFont;
		}
		else if (fontInfo != null)
		{
			String handlerFamily = handleFont(fontInfo, locale);
			if (handlerFamily != null)
			{
				fontFamily = handlerFamily + ", '" + fontFamily + "'"; // fallback to font family just in case it already exists in the system
			}
		}
		return fontFamily;
	}
	
	/**
	 * 
	 */
	public String getFontFamily(
		boolean ignoreCase,
		String fontFamily,
		Locale locale
		)
	{
		FontInfo fontInfo =
			FontUtil.getInstance(jasperReportsContext).getFontInfo(fontFamily, ignoreCase, locale);
		
		String htmlFamily = fontFamily;
		if (fontInfo != null)
		{
			//fontName found in font extensions
			FontFamily family = fontInfo.getFontFamily();
			String exportFont = family.getExportFont(getExporterKey());
			if (exportFont == null)
			{
				String handlerFamily = handleFont(fontInfo, locale);
				if (handlerFamily != null)
				{
					htmlFamily = handlerFamily;
				}
			}
			else
			{
				htmlFamily = exportFont;
			}
		}

		return htmlFamily;
	}

	protected String handleFont(FontInfo fontInfo, Locale locale)
	{
		HtmlExporterOutput output = getExporterOutput();
		HtmlResourceHandler resourceHandler = output.getResourceHandler();
		String family = null;
		if (resourceHandler != null)
		{
			HtmlFontFamily htmlFontFamily = HtmlFontFamily.getInstance(locale, fontInfo);
			
			if (htmlFontFamily != null)
			{
				addFontFamily(htmlFontFamily);
				
				family = "'" + htmlFontFamily.getShortId() + "'";
			}
		}
		return family;
	}

	
	/**
	 *
	 */
	public void addFontFamily(HtmlFontFamily htmlFontFamily) 
	{
		if (!fontsToProcess.containsKey(htmlFontFamily.getId()))
		{
			fontsToProcess.put(htmlFontFamily.getId(), htmlFontFamily);

			if (getReportContext() == null)
			{
				HtmlExporterOutput output = getExporterOutput();
				HtmlResourceHandler resourceHandler = output.getResourceHandler();
					
				// create font resources only in static HTML export
				HtmlFontUtil.getInstance(jasperReportsContext).handleHtmlFont(
					resourceHandler, 
					null,
					resourceHandler,
					htmlFontFamily,
					true,
					true
					);
			}
		}
	}


	protected boolean isOverrideHints()
	{
		Boolean overrideHints = getCurrentItemConfiguration().isOverrideHints();
		return overrideHints != null ? overrideHints
				: propertiesUtil.getBooleanProperty(
						CommonExportConfiguration.PROPERTY_EXPORT_CONFIGURATION_OVERRIDE_REPORT_HINTS);
	}

	/**
	 * 
	 */
	protected boolean isEmbedImage(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(HtmlReportConfiguration.PROPERTY_EMBED_IMAGE)
			&& !isOverrideHints()
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, HtmlReportConfiguration.PROPERTY_EMBED_IMAGE, getCurrentItemConfiguration().isEmbedImage());
		}
		return getCurrentItemConfiguration().isEmbedImage();
	}


	/**
	 * 
	 */
	protected boolean isEmbeddedSvgUseFonts(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(HtmlReportConfiguration.PROPERTY_EMBEDDED_SVG_USE_FONTS)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, HtmlReportConfiguration.PROPERTY_EMBEDDED_SVG_USE_FONTS, getCurrentItemConfiguration().isEmbeddedSvgUseFonts());
		}
		return getCurrentItemConfiguration().isEmbeddedSvgUseFonts();
	}


	/**
	 * 
	 */
	protected boolean isConvertSvgToImage(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(HtmlReportConfiguration.PROPERTY_CONVERT_SVG_TO_IMAGE)
			&& !isOverrideHints()
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, HtmlReportConfiguration.PROPERTY_CONVERT_SVG_TO_IMAGE, getCurrentItemConfiguration().isConvertSvgToImage());
		}
		return getCurrentItemConfiguration().isConvertSvgToImage();
	}


	/**
	 * 
	 */
	protected boolean isUseBackgroundImageToAlign(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(HtmlReportConfiguration.PROPERTY_USE_BACKGROUND_IMAGE_TO_ALIGN)
			&& !isOverrideHints()
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, HtmlReportConfiguration.PROPERTY_USE_BACKGROUND_IMAGE_TO_ALIGN, getCurrentItemConfiguration().isUseBackgroundImageToAlign());
		}
		return getCurrentItemConfiguration().isUseBackgroundImageToAlign();
	}

	@Override
	protected JRStyledText getStyledText(JRPrintText textElement, boolean setBackcolor)
	{
		JRStyledText styledText = styledTextUtil.getProcessedStyledText(textElement, 
				setBackcolor ? allSelector : noBackcolorSelector, getExporterKey());
		
		if (styledText != null)
		{
			short[] lineBreakOffsets = textElement.getLineBreakOffsets();
			if (lineBreakOffsets != null && lineBreakOffsets.length > 0)
			{
				//insert new lines at the line break positions saved at fill time
				//cloning the text first
				//FIXME do we need this?  styled text instances are no longer shared
				styledText = styledText.cloneText();
				styledText.insert("\u0085", lineBreakOffsets);
			}
		}
		
		return styledText;
	}

}
