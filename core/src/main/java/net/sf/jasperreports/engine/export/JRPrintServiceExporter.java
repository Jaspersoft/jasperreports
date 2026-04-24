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
package net.sf.jasperreports.engine.export;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.print.JRPrinterAWT;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.PrintServiceExporterConfiguration;
import net.sf.jasperreports.export.PrintServiceReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleGraphics2DReportConfiguration;


/**
 * Prints a JasperReports document using the Java Print Service API.
 * <p/>
 * There are four ways of using the Java Print Service with the Java 2D API:
 * <ul>
 * <li>Printing 2D graphics using <code>java.awt.print.PrinterJob</code></li>
 * <li>Streaming 2D graphics using <code>java.awt.print.PrinterJob</code></li>
 * <li>Printing 2D graphics using <code>javax.print.DocPrintJob</code> and a service-formatted <code>javax.print.DocFlavor</code></li>
 * <li>Streaming 2D graphics using <code>javax.print.DocPrintJob</code> and a service-formatted <code>javax.print.DocFlavor</code></li>
 * </ul>
 * <p/>
 * The {@link net.sf.jasperreports.engine.export.JRPrintServiceExporter}
 * implementation takes the first approach and uses some of the methods added to the
 * <code>java.awt.print.PrinterJob</code> class:
 * <ul>
 * <li>Static convenience methods to look up print services that can image 2D graphics,
 * which are returned as an array of <code>PrintService</code> or
 * <code>StreamPrintServiceFactory</code> objects depending on the method</li>
 * <li>Methods to set and get a <code>PrintService</code> on a <code>PrinterJob</code></li>
 * <li>A <code>pageDialog()</code> method that takes a <code>PrintRequestAttributeSet</code> parameter</li>
 * <li>A <code>printDialog()()</code> method that takes a <code>PrintRequestAttributeSet</code> parameter</li>
 * <li>A print method that takes a <code>PrintRequestAttributeSet</code> parameter</li>
 * </ul>
 * <h3>Looking Up a Printing Service</h3>
 * This exporter tries to find a print service that supports the necessary attributes. The set of
 * attributes can be supplied to the exporter in the form of a
 * <code>javax.print.attribute.PrintServiceAttributeSet</code> object that is passed as the
 * value for the special 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#getPrintServiceAttributeSet() getPrintServiceAttributeSet()} 
 * exporter configuration setting. For more
 * details about the attributes that can be part of such an attribute set, check the Java Print
 * Service API documentation.
 * <p/>
 * The lookup procedure might return one or more print services able to handle the
 * specified print service attributes. If so, the exporter uses the first one in the list. If no
 * suitable print service is found, then the exporter throws an exception. As an alternative, a
 * <code>javax.print.PrintService</code> instance can be passed in using the 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#getPrintService() getPrintService()} 
 * exporter configuration setting when users do not want the Java Print Service to search for an
 * available print service.
 * <h3>Configuring the Printer Job</h3>
 * Once a print service has been located, it is associated with a <code>PrinterJob</code> instance.
 * Further customization is made by passing a
 * <code>javax.print.attribute.PrintRequestAttributeSet</code> instance when calling the
 * <code>print()</code> method on the <code>PrinterJob</code> object to start the printing process.
 * <p/>
 * To supply the <code>javax.print.attribute.PrintRequestAttributeSet</code> object
 * containing the desired <code>javax.print.attribute.PrintRequestAttribute</code> values to
 * the exporter, set the special 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#getPrintRequestAttributeSet() getPrintRequestAttributeSet()} 
 * exporter configuration setting.
 * <h3>Displaying Print Dialogs</h3>
 * If this exporter is invoked by a desktop or client-side Java application, you can offer the
 * end user a final chance to customize the printer job before the printing process actually
 * starts. The exporter has two other predefined configuration settings: 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPageDialog() isDisplayPageDialog()}
 * and
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPrintDialog() isDisplayPrintDialog()}, 
 * both receiving <code>java.lang.Boolean</code> values, which show or
 * suppress the page dialog and/or the print dialog associated with the <code>PrinterJob</code>
 * instance.
 * <p/>
 * The two dialogs are cross-platform. They enable users to alter the print service attributes
 * and the print request attributes that are already set for the current print service and printer
 * job. They also allow canceling the current printing procedure altogether. When batch
 * printing a set of documents, if 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPageDialog() isDisplayPageDialog()} or 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPrintDialog() isDisplayPrintDialog()} are
 * set to true, a dialog window will pop up each time a document in the list is to be
 * printed. This is very useful if you intend to set different printing options for each
 * document. However, setting the same page/printing options each time would quickly
 * become cumbersome. If same settings are intended for all documents in the list, the
 * exporter provides two additional predefined export configuration settings:
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPageDialogOnlyOnce() isDisplayPageDialogOnlyOnce()} and 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPrintDialogOnlyOnce() isDisplayPrintDialogOnlyOnce()}. These
 * are only effective if the corresponding 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPageDialog() isDisplayPageDialog()} or 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPrintDialog() isDisplayPrintDialog()} are
 * set to true.
 * <p/>
 * If {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPageDialogOnlyOnce() isDisplayPageDialogOnlyOnce()}
 * is true, then the page dialog will open only
 * once, and the export options set within will be preserved for all documents in the list.
 * The same thing happens when 
 * {@link net.sf.jasperreports.export.PrintServiceExporterConfiguration#isDisplayPrintDialogOnlyOnce() isDisplayPrintDialogOnlyOnce()}
 * is set to true - the print dialog will open only once.
 * <p/>
 * Below is an example of configuring the print service exporter taken from the supplied
 * <code>/demo/ samples/printservice</code> sample:
 * <pre>
 * public void print() throws JRException
 * {
 *   PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
 *   printRequestAttributeSet.add(MediaSizeName.ISO_A4);
 *
 *   PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
 *   //printServiceAttributeSet.add(new PrinterName("Epson Stylus 820 ESC/P 2", null));
 *   //printServiceAttributeSet.add(new PrinterName("hp LaserJet 1320 PCL 6", null));
 *   //printServiceAttributeSet.add(new PrinterName("PDFCreator", null));
 *
 *   JRPrintServiceExporter exporter = new JRPrintServiceExporter();
 *
 *   exporter.setExporterInput(new SimpleExporterInput("build/reports/PrintServiceReport.jrprint"));
 *   SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
 *   configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
 *   configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
 *   configuration.setDisplayPageDialog(false);
 *   configuration.setDisplayPrintDialog(true);
 *   exporter.setConfiguration(configuration);
 *   exporter.exportReport();

 *   System.err.println("Printing time : " + (System.currentTimeMillis() - start));
 * }</pre>
 * 
 * @see net.sf.jasperreports.export.PrintServiceExporterConfiguration
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRPrintServiceExporter extends JRAbstractExporter<PrintServiceReportConfiguration, PrintServiceExporterConfiguration, ExporterOutput, JRExporterContext> implements Printable
{
	protected static final String PRINT_SERVICE_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.print.service.";
	public static final String EXCEPTION_MESSAGE_KEY_PRINT_SERVICE_NOT_FOUND = "export.print.service.not.found";

	/**
	 *
	 */
	protected JRGraphics2DExporter exporter;
	protected SimpleGraphics2DReportConfiguration grxConfiguration;

	protected int reportIndex;
	
	private PrintService printService;
	private Boolean[] printStatus;
	
	protected class ExporterContext extends BaseExporterContext
	{
	}

	/**
	 * @see #JRPrintServiceExporter(JasperReportsContext)
	 */
	public JRPrintServiceExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	
	/**
	 *
	 */
	public JRPrintServiceExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
	}


	@Override
	protected Class<PrintServiceExporterConfiguration> getConfigurationInterface()
	{
		return PrintServiceExporterConfiguration.class;
	}
	

	@Override
	protected Class<PrintServiceReportConfiguration> getItemConfigurationInterface()
	{
		return PrintServiceReportConfiguration.class;
	}
	

	@Override
	protected void ensureOutput()
	{
		//nothing to do
	}
	

	@Override
	public void exportReport() throws JRException
	{
		/*   */
		ensureJasperReportsContext();
		ensureInput();
		
		initExport();

		ensureOutput();

		try
		{
			PrintServiceExporterConfiguration configuration = getCurrentConfiguration();
			
			PrintServiceAttributeSet printServiceAttributeSet = configuration.getPrintServiceAttributeSet();
			if (printServiceAttributeSet == null)
			{
				printServiceAttributeSet = new HashPrintServiceAttributeSet();
			}

			boolean displayPageDialog = false;
			boolean displayPageDialogOnlyOnce = false;
			boolean displayPrintDialog = false;
			boolean displayPrintDialogOnlyOnce = false;

			Boolean pageDialog = configuration.isDisplayPageDialog();
			if (pageDialog != null)
			{
				displayPageDialog = pageDialog;
			}
	
			Boolean pageDialogOnlyOnce = configuration.isDisplayPageDialogOnlyOnce();
			if (displayPageDialog && pageDialogOnlyOnce != null)
			{
				// it can be (eventually) set to true only if displayPageDialog is true
				displayPageDialogOnlyOnce = pageDialogOnlyOnce;
			}
	
			Boolean printDialog = configuration.isDisplayPrintDialog();
			if (printDialog != null)
			{
				displayPrintDialog = printDialog;
			}
	
			Boolean printDialogOnlyOnce = configuration.isDisplayPrintDialogOnlyOnce();
			if (displayPrintDialog && printDialogOnlyOnce != null)
			{
//				 it can be (eventually) set to true only if displayPrintDialog is true
				displayPrintDialogOnlyOnce = printDialogOnlyOnce;
			}
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			
			JRPrinterAWT.initPrinterJobFields(printerJob);
			
			printerJob.setPrintable(this);
			
			printStatus = null;
			
			// determining the print service only once
			printService = configuration.getPrintService();
			if (printService == null) {
				PrintService[] services = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);
				if (services.length > 0)
				{
					printService = services[0];
				}
			}
			
			if (printService == null)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_PRINT_SERVICE_NOT_FOUND,  
						(Object[])null 
						);
			}

			try 
			{
				printerJob.setPrintService(printService);
			}
			catch (PrinterException e) 
			{ 
				throw new JRException(e);
			}

			List<ExporterInputItem> items = exporterInput.getItems();
			
			PrintRequestAttributeSet printRequestAttributeSet = null;
			if(displayPrintDialogOnlyOnce || displayPageDialogOnlyOnce)
			{
				printRequestAttributeSet = new HashPrintRequestAttributeSet();
				setDefaultPrintRequestAttributeSet(printRequestAttributeSet);
				setOrientation(items.get(0).getJasperPrint(), printRequestAttributeSet);
				if(displayPageDialogOnlyOnce)
				{
					if(printerJob.pageDialog(printRequestAttributeSet) == null)
					{
						return;
					}
					else
					{
						displayPageDialog = false;
					}
				}
				if(displayPrintDialogOnlyOnce)
				{
					if(!printerJob.printDialog(printRequestAttributeSet))
					{
						printStatus = new Boolean[]{Boolean.FALSE};
						return;
					}
					else
					{
						displayPrintDialog = false;
					}
				}
			}
			
			List<Boolean> status = new ArrayList<>();
			// fix for bug ID artf1455 from jasperforge.org bug database
			for(reportIndex = 0; reportIndex < items.size(); reportIndex++)
			{
				ExporterInputItem item = items.get(reportIndex);

				setCurrentExporterInputItem(item);
				
				PrintServiceReportConfiguration lcItemConfiguration = getCurrentItemConfiguration(); 

				exporter = new JRGraphics2DExporter(jasperReportsContext);
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				grxConfiguration = new SimpleGraphics2DReportConfiguration();
				grxConfiguration.setProgressMonitor(lcItemConfiguration.getProgressMonitor());
				grxConfiguration.setOffsetX(lcItemConfiguration.getOffsetX());
				grxConfiguration.setOffsetY(lcItemConfiguration.getOffsetY());
				grxConfiguration.setZoomRatio(lcItemConfiguration.getZoomRatio());
//				exporter.setParameter(JRExporterParameter.CLASS_LOADER, classLoader);
//				exporter.setParameter(JRExporterParameter.URL_HANDLER_FACTORY, urlHandlerFactory);
//				exporter.setParameter(JRExporterParameter.FILE_RESOLVER, fileResolver);
				grxConfiguration.setExporterFilter(filter);
				grxConfiguration.setMinimizePrinterJobSize(lcItemConfiguration.isMinimizePrinterJobSize());
				
				if(displayPrintDialog || displayPageDialog ||
						(!displayPrintDialogOnlyOnce && !displayPageDialogOnlyOnce))
				{
					printRequestAttributeSet = new HashPrintRequestAttributeSet();
					setDefaultPrintRequestAttributeSet(printRequestAttributeSet);
					setOrientation(jasperPrint, printRequestAttributeSet);
				}
		
				try 
				{
					PageRange pageRange = getPageRange();
					int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
					int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (jasperPrint.getPages().size() - 1) : pageRange.getEndPageIndex();

					if (items.size() == 1)
					{
						printRequestAttributeSet.add(new PageRanges(startPageIndex + 1, endPageIndex + 1));
					}

					printerJob.setJobName("JasperReports - " + jasperPrint.getName());

					if (displayPageDialog)
					{
						printerJob.pageDialog(printRequestAttributeSet);
					}
					if (displayPrintDialog)
					{
						if (printerJob.printDialog(printRequestAttributeSet))
						{
							status.add(Boolean.TRUE);
							printerJob.print(printRequestAttributeSet);
						}
						else
						{
							status.add(Boolean.FALSE);
						}
					}
					else
					{
						PageFormat pageFormat = printerJob.defaultPage();
						Paper paper = pageFormat.getPaper();
						
						switch (OrientationEnum.getValueOrDefault(jasperPrint.getOrientation()))
						{
							case LANDSCAPE :
							{
								pageFormat.setOrientation(PageFormat.LANDSCAPE);
								paper.setSize(jasperPrint.getPageHeight(), jasperPrint.getPageWidth());
								paper.setImageableArea(
									0,
									0,
									jasperPrint.getPageHeight(),
									jasperPrint.getPageWidth()
									);
								break;
							}
							case PORTRAIT :
							default :
							{
								pageFormat.setOrientation(PageFormat.PORTRAIT);
								paper.setSize(jasperPrint.getPageWidth(), jasperPrint.getPageHeight());
								paper.setImageableArea(
									0,
									0,
									jasperPrint.getPageWidth(),
									jasperPrint.getPageHeight()
									);
							}
						}
						
						// setting the paper object back as getPaper() returns a clone
						pageFormat.setPaper(paper);
						
						printerJob.setPrintable(this, pageFormat);

						printerJob.print(printRequestAttributeSet);
					}
				}
				catch (PrinterException e) 
				{ 
					throw new JRException(e);
				}
			}
			
			printStatus = status.toArray(new Boolean[status.size()]);
			printService = printerJob.getPrintService();
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
	}
	

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException
	{
		if (Thread.interrupted())
		{
			throw new PrinterException("Current thread interrupted.");
		}

		if ( pageIndex < 0 || pageIndex >= jasperPrint.getPages().size() )
		{
			return Printable.NO_SUCH_PAGE;
		}
		
		SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
		output.setGraphics2D((Graphics2D)graphics);
		exporter.setExporterOutput(output);

		grxConfiguration.setPageIndex(pageIndex);
		exporter.setConfiguration(grxConfiguration);
		
		try
		{
			exporter.exportReport();
		}
		catch (JRException e)
		{
			throw new PrinterException(e.getMessage()); //NOPMD
		}

		return Printable.PAGE_EXISTS;
	}


	private void setOrientation(JasperPrint jPrint,PrintRequestAttributeSet printRequestAttributeSet)
	{
		if (!printRequestAttributeSet.containsKey(MediaPrintableArea.class))
		{
			int printableWidth;
			int printableHeight;
			switch (OrientationEnum.getValueOrDefault(jPrint.getOrientation()))
			{
				case LANDSCAPE:
					printableWidth = jPrint.getPageHeight();
					printableHeight = jPrint.getPageWidth();
					break;
				default:
					printableWidth = jPrint.getPageWidth();
					printableHeight = jPrint.getPageHeight();
					break;
			}
			
			printRequestAttributeSet.add(
				new MediaPrintableArea(
					0f, 
					0f, 
					printableWidth / 72f,
					printableHeight / 72f,
					MediaPrintableArea.INCH
					)
				);
		}

		if (!printRequestAttributeSet.containsKey(OrientationRequested.class))
		{
			OrientationRequested orientation;
			switch (OrientationEnum.getValueOrDefault(jPrint.getOrientation()))
			{
				case LANDSCAPE:
					orientation = OrientationRequested.LANDSCAPE;
					break;
				default:
					orientation = OrientationRequested.PORTRAIT;
					break;
			}
			printRequestAttributeSet.add(orientation);
		}
		
	}
	
	private void setDefaultPrintRequestAttributeSet(PrintRequestAttributeSet printRequestAttributeSet)
	{
		PrintRequestAttributeSet configPrintRequestAttributeSet = getCurrentConfiguration().getPrintRequestAttributeSet();
		if (configPrintRequestAttributeSet != null)
		{
			printRequestAttributeSet.addAll(configPrintRequestAttributeSet);
		}
	}

	// artf1936
	public static boolean checkAvailablePrinters() 
	{
		PrintService[] ss = java.awt.print.PrinterJob.lookupPrintServices();
		for (int i=0;i<ss.length;i++) {
			Attribute[] att = ss[i].getAttributes().toArray();
			for (int j=0;j<att.length;j++) {
				if (att[j].equals(PrinterIsAcceptingJobs.ACCEPTING_JOBS)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns an array of <tt>java.lang.Boolean</tt> values, one for each appearance of the print dialog during the last export operation.
	 * A Boolean.TRUE value in this array means that for that particular occurrence of the print dialog, the OK button was hit. 
	 * A Boolean.FALSE value means the respective print dialog was canceled.
	 */
	public Boolean[] getPrintStatus() 
	{
		return printStatus;
	}

	/**
	 * Returns the {@link PrintService} instance used by the exporter last time the exportReport() method was run.
	 */
	public PrintService getPrintService() 
	{
		return printService;
	}
	
	@Override
	public String getExporterKey()
	{
		return null;
	}
	
	@Override
	public String getExporterPropertiesPrefix()
	{
		return PRINT_SERVICE_EXPORTER_PROPERTIES_PREFIX;
	}
}