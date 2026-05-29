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
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.export;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.List;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.draw.PrintDrawVisitor;
import net.sf.jasperreports.engine.util.JRGraphEnvInitializer;
import net.sf.jasperreports.export.Graphics2DExporterConfiguration;
import net.sf.jasperreports.export.Graphics2DExporterOutput;
import net.sf.jasperreports.export.Graphics2DReportConfiguration;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.renderers.RenderersCache;


/**
 * Exports a JasperReports document to a <code>Graphics2D</code> object. 
 * <p/>
 * JasperReports relies on AWT for text measurements and all sorts of layout calculations
 * during report filling, so documents created using AWT will certainly look perfect when
 * rendered with AWT on a <code>java.awt.Graphics2D</code> context. For this reason, the
 * {@link net.sf.jasperreports.engine.export.JRGraphics2DExporter} is the perfect
 * exporter. The output it produces is considered to be the reference in terms of layout
 * capabilities and element styling.
 * <p/>
 * Generally speaking, the document quality produced by all the other exporters is only an
 * approximation of the perfect output that the Graphics2D exporter can produce. As its
 * name indicates, this exporter is special because it does not produce files or send character
 * or binary data to an output stream. Instead, its only target for rendering the content of a
 * page is a <code>java.awt.Graphics2D</code> object. This exporter is also special because it can
 * export only one page at a time.
 * <p/>
 * This exporter is used by the built-in Swing viewer to render the content of each page, and
 * it is also used when printing the documents. The documents are printed page by page,
 * and the exporter is invoked to draw each document page on the graphic context
 * associated with the selected printer job.
 * <p/>
 * Because we are relying on the same code (same exporter) when viewing the documents
 * using the built-in viewer and when printing them, JasperReports is a perfect WYSIWYG
 * tool. The document quality on paper is the same as on the screen.
 * <p/>
 * In terms of exporter input, note that this exporter does not work in batch mode. If a
 * <code>java.util.List</code> of JasperPrint documents is supplied to it through a
 * {@link net.sf.jasperreports.export.SimpleExporterInput} instance, the exporter 
 * considers only the first one for exporting and ignores all the others.
 * <p/>
 * Furthermore, this exporter can export only a single page at a time. The index of the page
 * to be exported can be set using either the 
 * {@link net.sf.jasperreports.export.ReportExportConfiguration#getStartPageIndex() getStartPageIndex()}
 * exporter configuration setting or the
 * {@link net.sf.jasperreports.export.ReportExportConfiguration#getPageIndex() getPageIndex()}.
 * Note that if present, {@link net.sf.jasperreports.export.ReportExportConfiguration#getPageIndex() getPageIndex()} overrides the value of
 * {@link net.sf.jasperreports.export.ReportExportConfiguration#getStartPageIndex() getStartPageIndex()}. 
 * Therefore, this exporter actually exports only the first page from
 * the specified page range, no matter how the page range is specified.
 * <p/>
 * As already mentioned, this exporter needs a target <code>java.awt.Graphics2D</code> object onto
 * which to render the specified page. This Graphics2D object can be set using the special
 * exporter output setting {@link net.sf.jasperreports.export.Graphics2DExporterOutput#getGraphics2D() getGraphics2D()}. 
 * If this setting is not present, the exporter will throw an
 * exception signaling to the caller program that no output target was specified for the
 * export process.
 * <p/>
 * By default, the exporter renders the content of the page at normal proportions. However,
 * it can also render it at different proportions if needed. For instance, when used inside the
 * Swing viewer, the Graphics2D exporter must render the page using the user-defined
 * zoom ratio. To set the zoom ratio for the exporter, supply a <code>java.lang.Float</code> value
 * ranging from 0 to 1 as the value for the 
 * {@link net.sf.jasperreports.export.Graphics2DReportConfiguration#getZoomRatio() getZoomRatio()} export 
 * configuration setting.
 * <p/>
 * The Graphics2D exporter is also used when printing directly from Java. The Java Print
 * Service exporter relies on the Graphics2D exporter
 * and delegates to it all the rendering that needs to be performed on the printer's graphic
 * context. Some of the existing JVM implementations have problems related to the huge
 * size of the printer spool jobs that are created even for small documents. To avoid this, a
 * bug fix was introduced in the Graphics2D exporter to minimize the impact of this
 * problem and reduce the size of print spool jobs, while preserving document quality when
 * printing. However, the bug fix itself is not perfect, and users might experience problems
 * when printing bidirectional writing texts such as Arabic and Hebrew.
 * <p/>
 * This is why the special
 * {@link net.sf.jasperreports.export.Graphics2DReportConfiguration#isMinimizePrinterJobSize() isMinimizePrinterJobSize()} 
 * export configuration setting was introduced, along with a configuration property called
 * <code>net.sf.jasperreports.export.graphics2d.min.job.size</code>, to allow users to turn
 * on and off this rendering optimization, depending on their actual needs. The
 * configuration property value is used only in the absence of the export configuration setting.
 * 
 * @see net.sf.jasperreports.export.Graphics2DExporterOutput
 * @see net.sf.jasperreports.export.Graphics2DReportConfiguration
 * @see net.sf.jasperreports.export.ReportExportConfiguration
 * @see net.sf.jasperreports.export.SimpleExporterInput
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRGraphics2DExporter extends JRAbstractExporter<Graphics2DReportConfiguration, Graphics2DExporterConfiguration, Graphics2DExporterOutput, JRGraphics2DExporterContext>
{
	private static final float DEFAULT_ZOOM = 1f;

	private static final String GRAPHICS2D_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.graphics2d.";

	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String GRAPHICS2D_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "graphics2d";
	
	/**
	 *
	 */
	protected PrintDrawVisitor drawVisitor;
	protected int reportDpi;
	
	private boolean whitePageBackground = true;
	
	protected class ExporterContext extends BaseExporterContext implements JRGraphics2DExporterContext
	{
	}
	
	/**
	 * @see #JRGraphics2DExporter(JasperReportsContext)
	 */
	public JRGraphics2DExporter() throws JRException
	{
		this(DefaultJasperReportsContext.getInstance());
	}
	

	/**
	 *
	 */
	public JRGraphics2DExporter(JasperReportsContext jasperReportsContext) throws JRException
	{
		super(jasperReportsContext);
		
		JRGraphEnvInitializer.initializeGraphEnv();

		exporterContext = new ExporterContext();
	}


	@Override
	protected Class<Graphics2DExporterConfiguration> getConfigurationInterface()
	{
		return Graphics2DExporterConfiguration.class;
	}


	@Override
	protected Class<Graphics2DReportConfiguration> getItemConfigurationInterface()
	{
		return Graphics2DReportConfiguration.class;
	}
	

	@Override
	public void exportReport() throws JRException
	{
		/*   */
		ensureJasperReportsContext();
		ensureInput();

		initExport();
		
		ensureOutput();

		Graphics2D grx = getExporterOutput().getGraphics2D();
		
		try
		{
			exportReportToGraphics2D(grx);
		}
		finally
		{
			resetExportContext();
		}
	}


	@Override
	protected void initExport()
	{
		super.initExport();
	}


	@Override
	protected void initReport()
	{
		super.initReport();
		
		reportDpi = jasperPrint.getDpi();

		setOffset(false);

		Graphics2DReportConfiguration configuration = getCurrentItemConfiguration();
		
		Boolean isMinimizePrinterJobSize = configuration.isMinimizePrinterJobSize();
		Boolean isIgnoreMissingFont = configuration.isIgnoreMissingFont();
		boolean defaultIndentFirstLine = 
			propertiesUtil.getBooleanProperty(
				jasperPrint, 
				JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE, 
				true
				);
		boolean defaultJustifyLastLine = 
			propertiesUtil.getBooleanProperty(
				jasperPrint, 
				JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE, 
				false
				);
		
		drawVisitor =
			new PrintDrawVisitor(
				exporterContext,
				filter,
				getRenderersCache(),
				isMinimizePrinterJobSize == null ? Boolean.TRUE : isMinimizePrinterJobSize,
				isIgnoreMissingFont == null ? Boolean.FALSE : isIgnoreMissingFont,
				defaultIndentFirstLine,
				defaultJustifyLastLine,
				reportDpi
				);
		
		whitePageBackground = configuration.isWhitePageBackground();
	}

	
	/**
	 *
	 */
	protected RenderersCache getRenderersCache()
	{
		return new RenderersCache(getJasperReportsContext());
	}


	/**
	 *
	 */
	public void exportReportToGraphics2D(Graphics2D grx) throws JRException
	{
		grx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//grx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		grx.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		grx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		setCurrentExporterInputItem(exporterInput.getItems().get(0));
		
		ReportExportConfiguration configuration = getCurrentItemConfiguration();
		
		AffineTransform atrans = new AffineTransform();
		atrans.translate(
			configuration.getOffsetX() == null ? 0 : configuration.getOffsetX(), 
			configuration.getOffsetY() == null ? 0 : configuration.getOffsetY()
			);
		float zoom = getZoom();
		float dpiScale = 72f / reportDpi;
		atrans.scale(zoom * dpiScale, zoom * dpiScale);
		grx.transform(atrans);

		List<JRPrintPage> pages = jasperPrint.getPages();
		if (pages != null)
		{
			PageRange pageRange = getPageRange();
			int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();

			Shape oldClipShape = grx.getClip();
	
			PrintPageFormat pageFormat = jasperPrint.getPageFormat(startPageIndex);
			grx.clip(new Rectangle(0, 0, pageFormat.getPageWidth(), pageFormat.getPageHeight()));
	
			try
			{
				exportPage(grx, startPageIndex);
			}
			finally
			{
				grx.setClip(oldClipShape);
			}
		}
	}
	

	/**
	 *
	 */
	protected void exportPage(Graphics2D grx, int pageIndex) throws JRException
	{
		List<JRPrintPage> pages = jasperPrint.getPages();
		JRPrintPage page = pages.get(pageIndex);
		PrintPageFormat pageFormat = jasperPrint.getPageFormat(pageIndex);

		if (whitePageBackground)
		{
			grx.setColor(Color.white);
			grx.fillRect(
				0, 
				0, 
				pageFormat.getPageWidth(), 
				pageFormat.getPageHeight()
				);
		}

		grx.setColor(Color.black);
		grx.setStroke(new BasicStroke(1));

		/*   */
		drawVisitor.getFrameDrawer().draw(grx, page.getElements(), getOffsetX(), getOffsetY());
		
		JRExportProgressMonitor progressMonitor = getCurrentItemConfiguration().getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}

	@Override
	public String getExporterKey()
	{
		return GRAPHICS2D_EXPORTER_KEY;
	}

	@Override
	public String getExporterPropertiesPrefix()
	{
		return GRAPHICS2D_EXPORTER_PROPERTIES_PREFIX;
	}


	/**
	 *
	 */
	public PrintDrawVisitor getDrawVisitor()
	{
		return drawVisitor;
	}


	private float getZoom()//FIXMEEXPORT
	{
		float zoom = DEFAULT_ZOOM;
		
		Float zoomRatio = getCurrentItemConfiguration().getZoomRatio();
		if (zoomRatio != null)
		{
			zoom = zoomRatio;
			if (zoom <= 0)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INVALID_ZOOM_RATIO,  
						new Object[]{zoom} 
						);
			}
		}

		return zoom;
	}
}
