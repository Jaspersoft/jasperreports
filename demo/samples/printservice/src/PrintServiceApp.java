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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.base.JRBasePrintLine;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.renderers.ResourceRenderer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PrintServiceApp extends AbstractSampleApp
{


	/**
	 *
	 */
	public static void main(String[] args) 
	{
		main(new PrintServiceApp(), args);
	}
	
	
	@Override
	public void test() throws JRException
	{
		fill();
	}
	
	
	/**
	 *
	 */
	public void fill() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperPrint jasperPrint = getJasperPrint();
		File file = new File("target/reports/PrintServiceReport.jrprint");
		file.getParentFile().mkdirs();
		JRSaver.saveObject(jasperPrint, file);
		System.out.println("Filling time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void print() throws JRException
	{
		long start = System.currentTimeMillis();
		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		printRequestAttributeSet.add(MediaSizeName.ISO_A4);

		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		//printServiceAttributeSet.add(new PrinterName("Epson Stylus 820 ESC/P 2", null));
		//printServiceAttributeSet.add(new PrinterName("hp LaserJet 1320 PCL 6", null));
		//printServiceAttributeSet.add(new PrinterName("PDFCreator", null));
		
		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		
		exporter.setExporterInput(new SimpleExporterInput("target/reports/PrintServiceReport.jrprint"));
		SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
		configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
		configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
		configuration.setDisplayPageDialog(false);
		configuration.setDisplayPrintDialog(true);
		exporter.setConfiguration(configuration);
		exporter.exportReport();

		System.out.println("Printing time : " + (System.currentTimeMillis() - start));
	}


	/**
	 *
	 */
	private static JasperPrint getJasperPrint() throws JRException
	{
		//JasperPrint
		JasperPrint jasperPrint = new JasperPrint();
		jasperPrint.setName("NoReport");
		jasperPrint.setPageWidth(595);
		jasperPrint.setPageHeight(842);
		
		//Fonts
		JRDesignStyle normalStyle = new JRDesignStyle();
		normalStyle.setName("Sans_Normal");
		normalStyle.setDefault(true);
		normalStyle.setFontName("DejaVu Sans");
		normalStyle.setFontSize(8f);
		normalStyle.setPdfFontName("Helvetica");
		normalStyle.setPdfEncoding("Cp1252");
		normalStyle.setPdfEmbedded(Boolean.FALSE);
		jasperPrint.addStyle(normalStyle);

		JRDesignStyle boldStyle = new JRDesignStyle();
		boldStyle.setName("Sans_Bold");
		boldStyle.setFontName("DejaVu Sans");
		boldStyle.setFontSize(8f);
		boldStyle.setBold(Boolean.TRUE);
		boldStyle.setPdfFontName("Helvetica-Bold");
		boldStyle.setPdfEncoding("Cp1252");
		boldStyle.setPdfEmbedded(Boolean.FALSE);
		jasperPrint.addStyle(boldStyle);

		JRDesignStyle italicStyle = new JRDesignStyle();
		italicStyle.setName("Sans_Italic");
		italicStyle.setFontName("DejaVu Sans");
		italicStyle.setFontSize(8f);
		italicStyle.setItalic(Boolean.TRUE);
		italicStyle.setPdfFontName("Helvetica-Oblique");
		italicStyle.setPdfEncoding("Cp1252");
		italicStyle.setPdfEmbedded(Boolean.FALSE);
		jasperPrint.addStyle(italicStyle);
		
		JRPrintPage page = new JRBasePrintPage();

		JRPrintLine line = new JRBasePrintLine(jasperPrint.getDefaultStyleProvider());
		line.setX(40);
		line.setY(50);
		line.setWidth(515);
		line.setHeight(0);
		page.addElement(line);
		
		JRPrintImage image = new JRBasePrintImage(jasperPrint.getDefaultStyleProvider());
		image.setX(45);
		image.setY(55);
		image.setWidth(165);
		image.setHeight(40);
		image.setScaleImage(ScaleImageEnum.CLIP);
		image.setRenderer(
			ResourceRenderer.getInstance("jasperreports.png", false)
			);
		page.addElement(image);

		JRPrintText text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(210);
		text.setY(55);
		text.setWidth(345);
		text.setHeight(30);
		text.setTextHeight(text.getHeight());
		text.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		text.setLineSpacingFactor(1.3133681f);
		text.setLeadingOffset(-4.955078f);
		text.setStyle(boldStyle);
		text.setFontSize(18f);
		text.setText("JasperReports Project Description");
		page.addElement(text);

		text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(210);
		text.setY(85);
		text.setWidth(325);
		text.setHeight(15);
		text.setTextHeight(text.getHeight());
		text.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		text.setLineSpacingFactor(1.329241f);
		text.setLeadingOffset(-4.076172f);
		text.setStyle(italicStyle);
		text.setFontSize(12f);
		text.setText((new SimpleDateFormat("EEE, MMM d, yyyy")).format(new Date()));
		page.addElement(text);

		text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(40);
		text.setY(150);
		text.setWidth(515);
		text.setHeight(200);
		text.setTextHeight(text.getHeight());
		text.setHorizontalTextAlign(HorizontalTextAlignEnum.JUSTIFIED);
		text.setLineSpacingFactor(1.329241f);
		text.setLeadingOffset(-4.076172f);
		text.setStyle(normalStyle);
		text.setFontSize(14f);
		text.setText(
			"JasperReports is a powerful report-generating tool that has the ability to deliver rich content onto the screen, to the printer or into PDF, HTML, XLS, CSV or XML files.\n\n" +
			"It is entirely written in Java and can be used in a variety of Java enabled applications, including J2EE or Web applications, to generate dynamic content.\n\n" +
			"Its main purpose is to help creating page oriented, ready to print documents in a simple and flexible manner."
			);
		page.addElement(text);

		jasperPrint.addPage(page);

		return jasperPrint;
	}
	

}
