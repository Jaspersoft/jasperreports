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

/*
 * Contributors:
 * Ryan Johnson - delscovich@users.sourceforge.net
 * Carlton Moore - cmoore79@users.sourceforge.net
 */
package net.sf.jasperreports.engine.convert;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleContainer;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.JRTemplateReference;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBasePrintFrame;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.fill.JRFiller;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRExpressionUtil;
import net.sf.jasperreports.engine.util.StyleUtil;
import net.sf.jasperreports.engine.xml.JRXmlTemplateLoader;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ReportConverter 
{

	private static final Log log = LogFactory.getLog(ReportConverter.class);
	public static final Color GRID_LINE_COLOR = new Color(170, 170, 255);
	public static final String EXCEPTION_MESSAGE_KEY_CIRCULAR_DEPENDENCY_FOUND = "convert.report.converter.circular.dependency.found";
	
	private final JasperReportsContext jasperReportsContext;
	private final ComponentsEnvironment componentsEnvironment;
	private final JRReport report;
	private final Locale locale;
	private final TimeZone timezone;
	private JasperPrint jasperPrint;
	private JRPrintPage page;
	int pageWidth;
	private int offsetY;
	private int upColumnHeader;
	private int downColumnHeader;
	private int upDetails;
	private int downDetails;
	private int upColumnFooter;
	private int downColumnFooter;
	
	/**
	 * List containing page elements in a given order 
	 */
	private List<JRPrintElement> pageElements = new ArrayList<>();
	
	protected Map<String, JRStyle> stylesMap;

	
	/**
	 *
	 */
	public ReportConverter(JasperReportsContext jasperReportsContext, JRReport report, boolean ignoreContent)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.componentsEnvironment = ComponentsEnvironment.getInstance(jasperReportsContext);
		this.report = report;
		this.locale = readLocale();//allow to pass this explicitly?
		this.timezone = readTimeZone();
		
		if (report instanceof JasperDesign)
		{
			((JasperDesign)report).preprocess();
		}
		
		convert(ignoreContent);
	}

	private Locale readLocale()
	{
		//duplicates code from JRFillDataset.defaultLocale
		String localeCode = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(report, 
				JRFiller.PROPERTY_DEFAULT_LOCALE);
		Locale locale = (localeCode == null || localeCode.isEmpty()) 
				? Locale.getDefault()
				: JRDataUtils.getLocale(localeCode);
		return locale;
	}
	
	private TimeZone readTimeZone()
	{
		//duplicates code from JRFillDataset.defaultTimeZone
		String timezoneId = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(report, 
				JRFiller.PROPERTY_DEFAULT_TIMEZONE);
		TimeZone timezone = (timezoneId == null || timezoneId.isEmpty()) 
				? TimeZone.getDefault()
				: JRDataUtils.getTimeZone(timezoneId);
		return timezone;
	}
	
	/**
	 *
	 */
	protected void convert(boolean ignoreContent)
	{
		jasperPrint = new JasperPrint();
		
		jasperPrint.setFormatFactoryClass(report.getFormatFactoryClass());
		//FIXME locale and timezone settings jasperprint object
		//jasperPrint.setLocaleCode(JRDataUtils.getLocaleCode(Locale.getDefault()));
		//jasperPrint.setTimeZoneId(JRDataUtils.getTimeZoneId(TimeZone.getDefault()));
		//FIXMEFONT the locale is important for font
		//jasperPrint.setLocaleCode(report.getProperty(JRPropertiesUtil.PROPERTY_PREFIX + "locale"));
		//JRStyledTextAttributeSelector.setLocale(locale);
		jasperPrint.setName(report.getName());
		jasperPrint.setOrientation(report.getOrientation());
		jasperPrint.setPageWidth(report.getPageWidth());
		jasperPrint.setPageHeight(report.getPageHeight());
		jasperPrint.setTopMargin(report.getTopMargin());
		jasperPrint.setLeftMargin(report.getLeftMargin());
		jasperPrint.setBottomMargin(report.getBottomMargin());
		jasperPrint.setRightMargin(report.getRightMargin());
		jasperPrint.setDpi(report.getDpi());
		
		JRPropertiesUtil.getInstance(jasperReportsContext).transferProperties(report, jasperPrint, JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);

		setStyles(report);

		if (!ignoreContent)
		{
			pageWidth = report.getPageWidth();
			page = new JRBasePrintPage();
			
			offsetY = report.getTopMargin();

			addBand(report.getBackground());
			addBand(report.getTitle());
			addBand(report.getPageHeader());
			upColumnHeader = offsetY;
			addBand(report.getColumnHeader(), true);
			downColumnHeader = offsetY;

			boolean isColumnGroupBands = PrintOrderEnum.getValueOrDefault(report.getPrintOrder()) == PrintOrderEnum.VERTICAL;
			
			JRGroup[] groups = report.getGroups();
			if (groups != null)
			{
				for (JRGroup group : groups)
				{
					addSection(group.getGroupHeaderSection(), isColumnGroupBands);
				}
			}
			
			upDetails = offsetY;
			addSection(report.getDetailSection(), true);
			downDetails = offsetY;

			if (groups != null)
			{
				for (JRGroup group : groups)
				{
					addSection(group.getGroupFooterSection(), isColumnGroupBands);
				}
			}
			
			upColumnFooter = offsetY;
			addBand(report.getColumnFooter(), true);
			downColumnFooter = offsetY;
			addBand(report.getPageFooter());
			addBand(report.getLastPageFooter());
			addBand(report.getSummary());
			addBand(report.getNoData());
			
			jasperPrint.setPageHeight(offsetY + report.getBottomMargin());
			
			// column dotted delimitation 
			int colX = report.getLeftMargin();
			int columnCount = report.getColumnCount() == null ? 1 : report.getColumnCount();
			for (int i = 0; i < columnCount; i++)
			{
				addColumnSeparator(colX);
				colX += report.getColumnWidth();
				addColumnSeparator(colX);
				colX += report.getColumnSpacing();
			}

			// page dotted contour line
			addHorizontalGridLine(0, report.getTopMargin(), pageWidth);
			addHorizontalGridLine(0, offsetY, pageWidth);
			addVerticalGridLine(report.getLeftMargin(), 0, jasperPrint.getPageHeight());
			addVerticalGridLine(pageWidth - report.getRightMargin(), 0, jasperPrint.getPageHeight());

			page.setElements(pageElements);
			jasperPrint.addPage(page);
		}
	}

	protected void setStyles(JRReport report)
	{
		//styleFactory = new StyleFactory();
		stylesMap = new LinkedMap<>();
		
		loadReportStyles(report);
		
		try
		{
			for (JRStyle style : stylesMap.values())
			{
				jasperPrint.addStyle(style);
			}
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}

		JRStyle reportDefault = report.getDefaultStyle();
		JRStyle printDefault = null;
		if (reportDefault == null)
		{
			//search for the last default style
			for (JRStyle style : stylesMap.values())
			{
				if (style.isDefault())
				{
					printDefault = style;
				}
			}
		}
		else
		{
			printDefault = reportDefault;
		}
		
		if (printDefault != null)
		{
			jasperPrint.setDefaultStyle(printDefault);
		}		
	}

	protected void loadReportStyles(JRReport report)
	{
		JRReportTemplate[] templates = report.getTemplates();
		if (templates != null)
		{
			Set<String> loadedLocations = new HashSet<>();
			for (JRReportTemplate template : templates)
			{
				loadReportTemplateStyles(template, loadedLocations);
			}
		}
		
		collectStyles(report.getStyles(), report.getDpi());
	}

	protected void loadReportTemplateStyles(JRReportTemplate template, Set<String> loadedLocations)
	{
		JRExpression sourceExpression = template.getSourceExpression();
		if (sourceExpression != null)
		{
			String location = JRExpressionUtil.getSimpleExpressionText(sourceExpression);
			if (location == null)
			{
				log.warn("Template source expression " + sourceExpression.getText() 
						+ "cannot be evaluated; some styles might remain unresolved.");
			}
			else
			{
				HashSet<String> parentLocations = new HashSet<>();
				loadTemplateStyles(location, loadedLocations, parentLocations);
			}
		}
	}

	protected void loadTemplateStyles(String location, Set<String> loadedLocations, Set<String> parentLocations)
	{
		if (!parentLocations.add(location))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CIRCULAR_DEPENDENCY_FOUND,  
					new Object[]{location} 
					);
		}
		
		if (!loadedLocations.add(location))
		{
			//already loaded
			return;
		}
		
		JRTemplate template;
		try
		{
			template = JRXmlTemplateLoader.getInstance(getJasperReportsContext()).loadTemplate(location);//TODO lucianc repository context
		}
		catch (Exception e)
		{
			log.warn("Could not load template from location " + location 
					+ "; some styles might remain unresolved.");
			return;
		}
		
		JRTemplateReference[] includedTemplates = template.getIncludedTemplates();
		if (includedTemplates != null)
		{
			for (JRTemplateReference reference : includedTemplates)
			{
				loadTemplateStyles(reference.getLocation(), loadedLocations, parentLocations);
			}
		}
		
		collectStyles(template.getStyles(), template.getDpi());
	}

	protected void collectStyles(JRStyle[] styles, int templateDpi)
	{
		if (styles != null)
		{
			int reportDpi = report.getDpi();
			boolean needsDpiScaling = templateDpi != reportDpi;
			
			for (JRStyle style : styles)
			{
				stylesMap.put(
					style.getName(),
					needsDpiScaling
						? StyleUtil.scaleDpiStyle(style, templateDpi, reportDpi)
						: style
					);
			}
		}
	}

	/**
	 *
	 */
	private void addSection(JRSection section, boolean isColumnSection)
	{
		if (section != null)
		{
			JRBand[] bands = section.getBands();
			if (bands != null && bands.length > 0)
			{
				for (JRBand band : bands)
				{
					addBand(band, isColumnSection);
				}
			}
		}
	}

	/**
	 *
	 */
	private void addBand(JRBand band)
	{
		addBand(band, false);
	}

	/**
	 *
	 */
	private void addBand(JRBand band, boolean isColumnBand)
	{
		if (band != null)
		{
			JRBasePrintFrame frame = new JRBasePrintFrame(null);
			frame.setX(
				isColumnBand && report.getColumnDirection() == RunDirectionEnum.RTL 
					? report.getPageWidth() - report.getRightMargin() - report.getColumnWidth() 
					: report.getLeftMargin()
				);
			frame.setY(offsetY);
			frame.setWidth(
				isColumnBand
				? report.getColumnWidth()
				: report.getPageWidth() - report.getLeftMargin() - report.getRightMargin()
				);
			frame.setHeight(band.getHeight());
			
			band.visit(new ConvertVisitor(this, frame));
			
			pageElements.add(frame);
			
			offsetY += band.getHeight();
			addBandSeparator(offsetY);
		}
	}
	
	/**
	 *
	 */
	private void addBandSeparator(int bandY)
	{
		addHorizontalGridLine(0, bandY, pageWidth);
	}
	
	/**
	 *
	 */
	private void addColumnSeparator(int colX)
	{
		if (PrintOrderEnum.getValueOrDefault(report.getPrintOrder()) == PrintOrderEnum.HORIZONTAL)
		{
			if (downColumnHeader > upColumnHeader)
			{
				addVerticalGridLine(colX, upColumnHeader, downColumnHeader - upColumnHeader);
			}

			if (downDetails > upDetails)
			{
				addVerticalGridLine(colX, upDetails, downDetails - upDetails);
			}

			if (downColumnFooter > upColumnFooter)
			{
				addVerticalGridLine(colX, upColumnFooter, downColumnFooter - upColumnFooter);
			}
		}
		else //vertical printOrder
		{
			if (downColumnFooter > upColumnHeader)
			{
				addVerticalGridLine(colX, upColumnHeader, downColumnFooter - upColumnHeader);
			}
		}
	}
	
	/**
	 *
	 */
	private void addHorizontalGridLine(int x, int y, int width)
	{
		float dpiScale = (float) report.getDpi() / JasperPrint.DEFAULT_REPORT_DPI;
		JRPrintFrame printFrame = new JRBasePrintFrame(getDefaultStyleProvider());
		printFrame.setX(x);
		printFrame.setY(y);
		printFrame.setWidth(width);
		printFrame.setHeight(Math.max(1, (int) dpiScale));
		printFrame.getLineBox().getPen().setLineWidth((Float)0f);
		printFrame.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
		printFrame.getLineBox().getTopPen().setLineWidth(0.1f * dpiScale);
		printFrame.getLineBox().getTopPen().setLineStyle(LineStyleEnum.DASHED);
		printFrame.getLineBox().getTopPen().setLineColor(GRID_LINE_COLOR);
		pageElements.add(0, printFrame);
	}
	
	/**
	 *
	 */
	private void addVerticalGridLine(int x, int y, int height)
	{
		float dpiScale = (float) report.getDpi() / JasperPrint.DEFAULT_REPORT_DPI;
		JRPrintFrame printFrame = new JRBasePrintFrame(getDefaultStyleProvider());
		printFrame.setX(x);
		printFrame.setY(y);
		printFrame.setWidth(Math.max(1, (int) dpiScale));
		printFrame.setHeight(height);
		printFrame.getLineBox().getPen().setLineWidth((Float)0f);
		printFrame.getLineBox().getPen().setLineStyle(LineStyleEnum.SOLID);
		printFrame.getLineBox().getLeftPen().setLineWidth(0.1f * dpiScale);
		printFrame.getLineBox().getLeftPen().setLineStyle(LineStyleEnum.DASHED);
		printFrame.getLineBox().getLeftPen().setLineColor(GRID_LINE_COLOR);
		pageElements.add(0, printFrame);
	}
	
	/**
	 * 
	 */
	public JRStyle resolveStyle(JRStyleContainer originalContainer)
	{
		JRStyle originalStyle = originalContainer.getStyle();
		String nameReference = originalContainer.getStyleNameReference();
		JRStyle style;
		if (originalStyle != null)
		{
			style = originalStyle;
		}
		else if (nameReference != null)
		{
			style = stylesMap.get(nameReference);
			if (style == null)
			{
				log.warn("Style " + nameReference + " could not be resolved.");
			}
		}
		else
		{
			style = null;
		}
		return style;
	}	


	/**
	 * 
	 */	
	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}

	public ComponentsEnvironment getComponentsEnvironment()
	{
		return componentsEnvironment;
	}
	
	
	/**
	 * 
	 */	
	public JRReport getReport()
	{
		return report;
	}
	
	
	/**
	 * 
	 */	
	public JasperPrint getJasperPrint()
	{
		return jasperPrint;
	}
	
	
	/**
	 * 
	 */	
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		//return jasperPrint.getDefaultStyleProvider();
		return report;
	}

	
	public void copyBaseAttributes(JRElement source, JRPrintElement converted)
	{
		converted.setUUID(source.getUUID());
		converted.setX(source.getX());
		converted.setY(source.getY());
		converted.setWidth(source.getWidth());			
		converted.setHeight(source.getHeight());
		converted.setBackcolor(source.getOwnBackcolor());
		converted.setForecolor(source.getOwnForecolor());
		//printElement.setKey(element.getKey());
		converted.setMode(source.getOwnMode());
		converted.setStyle(resolveStyle(source));
	}

	public Locale getLocale()
	{
		return locale;
	}

	public TimeZone getTimeZone()
	{
		return timezone;
	}
	
}
