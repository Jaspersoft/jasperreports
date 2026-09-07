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
import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.TextAdjustEnum;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOdsReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.poi.export.JRXlsExporter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class NoXmlDesignApp extends AbstractSampleApp
{


	/**
	 *
	 */
	public static void main(String[] args) 
	{
		main(new NoXmlDesignApp(), args);
	}
	
	
	@Override
	public void test() throws JRException
	{
		compile();
		writeXml();
		fill();
		pdf();
		xmlEmbed();
		xml();
		html();
		rtf();
		xls();
		csv();
		odt();
		ods();
		docx();
		xlsx();
		pptx();
	}
	
	
	public void compile() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperDesign jasperDesign = getJasperDesign();
		File file = new File("target/reports/NoXmlDesignReport.jasper");
		file.getParentFile().mkdirs();
		JasperCompileManager.compileReportToFile(jasperDesign, file.getPath());
		System.out.println("Compile time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void fill() throws JRException
	{
		long start = System.currentTimeMillis();
		//Preparing parameters
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("OrderByClause", "ORDER BY City");

		JasperFillManager.fillReportToFile("target/reports/NoXmlDesignReport.jasper", parameters, getDemoHsqldbConnection());
		System.out.println("Filling time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void print() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperPrintManager.printReport("target/reports/NoXmlDesignReport.jrprint", true);
		System.out.println("Printing time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void pdf() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToPdfFile("target/reports/NoXmlDesignReport.jrprint");
		System.out.println("PDF creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void rtf() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");
		
		JRRtfExporter exporter = new JRRtfExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));
		
		exporter.exportReport();

		System.out.println("RTF creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xml() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToXmlFile("target/reports/NoXmlDesignReport.jrprint", false);
		System.out.println("XML creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xmlEmbed() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToXmlFile("target/reports/NoXmlDesignReport.jrprint", true);
		System.out.println("XML creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void html() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToHtmlFile("target/reports/NoXmlDesignReport.jrprint");
		System.out.println("HTML creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xls() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
		
		JRXlsExporter exporter = new JRXlsExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
		configuration.setOnePagePerSheet(false);
		exporter.setConfiguration(configuration);
		
		exporter.exportReport();

		System.out.println("XLS creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void csv() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".csv");
		
		JRCsvExporter exporter = new JRCsvExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));
		
		exporter.exportReport();

		System.out.println("CSV creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void odt() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".odt");
		
		JROdtExporter exporter = new JROdtExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		
		exporter.exportReport();

		System.out.println("ODT creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void ods() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".ods");
		
		JROdsExporter exporter = new JROdsExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		SimpleOdsReportConfiguration configuration = new SimpleOdsReportConfiguration();
		configuration.setOnePagePerSheet(true);
		exporter.setConfiguration(configuration);
		
		exporter.exportReport();

		System.out.println("ODS creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void docx() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".docx");
		
		JRDocxExporter exporter = new JRDocxExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		
		exporter.exportReport();

		System.out.println("DOCX creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xlsx() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xlsx");
		
		JRXlsxExporter exporter = new JRXlsxExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setOnePagePerSheet(false);
		exporter.setConfiguration(configuration);
		
		exporter.exportReport();

		System.out.println("XLSX creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void pptx() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/NoXmlDesignReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pptx");
		
		JRPptxExporter exporter = new JRPptxExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

		exporter.exportReport();

		System.out.println("PPTX creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void writeXml() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperCompileManager.writeReportToXmlFile("target/reports/NoXmlDesignReport.jasper");
		System.out.println("XML design creation time : " + (System.currentTimeMillis() - start));
	}


	/**
	 *
	 */
	private static JasperDesign getJasperDesign() throws JRException
	{
		//JasperDesign
		JasperDesign jasperDesign = new JasperDesign();
		jasperDesign.setName("NoXmlDesignReport");
		jasperDesign.setPageWidth(595);
		jasperDesign.setPageHeight(842);
		jasperDesign.setColumnWidth(515);
		jasperDesign.setColumnSpacing(0);
		jasperDesign.setLeftMargin(40);
		jasperDesign.setRightMargin(40);
		jasperDesign.setTopMargin(50);
		jasperDesign.setBottomMargin(50);
		
		//Fonts
		JRDesignStyle normalStyle = new JRDesignStyle();
		normalStyle.setName("Sans_Normal");
		normalStyle.setDefault(true);
		normalStyle.setFontName("DejaVu Sans");
		normalStyle.setFontSize(12f);
		normalStyle.setPdfFontName("Helvetica");
		normalStyle.setPdfEncoding("Cp1252");
		normalStyle.setPdfEmbedded(Boolean.FALSE);
		jasperDesign.addStyle(normalStyle);

		JRDesignStyle boldStyle = new JRDesignStyle();
		boldStyle.setName("Sans_Bold");
		boldStyle.setFontName("DejaVu Sans");
		boldStyle.setFontSize(12f);
		boldStyle.setBold(Boolean.TRUE);
		boldStyle.setPdfFontName("Helvetica-Bold");
		boldStyle.setPdfEncoding("Cp1252");
		boldStyle.setPdfEmbedded(Boolean.FALSE);
		jasperDesign.addStyle(boldStyle);

		JRDesignStyle italicStyle = new JRDesignStyle();
		italicStyle.setName("Sans_Italic");
		italicStyle.setFontName("DejaVu Sans");
		italicStyle.setFontSize(12f);
		italicStyle.setItalic(Boolean.TRUE);
		italicStyle.setPdfFontName("Helvetica-Oblique");
		italicStyle.setPdfEncoding("Cp1252");
		italicStyle.setPdfEmbedded(Boolean.FALSE);
		jasperDesign.addStyle(italicStyle);
		
		//Parameters
		JRDesignParameter parameter = new JRDesignParameter();
		parameter.setName("ReportTitle");
		parameter.setValueClass(java.lang.String.class);
		jasperDesign.addParameter(parameter);

		parameter = new JRDesignParameter();
		parameter.setName("OrderByClause");
		parameter.setValueClass(java.lang.String.class);
		jasperDesign.addParameter(parameter);

		//Query
		JRDesignQuery query = new JRDesignQuery();
		query.setText("SELECT * FROM Address $P!{OrderByClause}");
		jasperDesign.setQuery(query);

		//Fields
		JRDesignField field = new JRDesignField();
		field.setName("Id");
		field.setValueClass(java.lang.Integer.class);
		jasperDesign.addField(field);

		field = new JRDesignField();
		field.setName("FirstName");
		field.setValueClass(java.lang.String.class);
		jasperDesign.addField(field);

		field = new JRDesignField();
		field.setName("LastName");
		field.setValueClass(java.lang.String.class);
		jasperDesign.addField(field);

		field = new JRDesignField();
		field.setName("Street");
		field.setValueClass(java.lang.String.class);
		jasperDesign.addField(field);

		field = new JRDesignField();
		field.setName("City");
		field.setValueClass(java.lang.String.class);
		jasperDesign.addField(field);

		//Variables
		JRDesignVariable variable = new JRDesignVariable();
		variable.setName("CityNumber");
		variable.setValueClass(java.lang.Integer.class);
		variable.setResetType(ResetTypeEnum.GROUP);
		JRDesignGroup group = new JRDesignGroup();
		group.setName("CityGroup");
		variable.setResetGroup(group.getName());
		variable.setCalculation(CalculationEnum.SYSTEM);
		variable.setInitialValueExpression(new JRDesignExpression("$V{CityNumber} == null ? 1 : ($V{CityNumber} + 1)"));
		jasperDesign.addVariable(variable);

		variable = new JRDesignVariable();
		variable.setName("AllCities");
		variable.setValueClass(java.lang.String.class);
		variable.setResetType(ResetTypeEnum.REPORT);
		variable.setCalculation(CalculationEnum.SYSTEM);
		jasperDesign.addVariable(variable);

		//Groups
		group.setMinHeightToStartNewPage(60);
		group.setExpression(new JRDesignExpression("$F{City}"));

		JRDesignBand band = new JRDesignBand();
		band.setHeight(20);
		JRDesignTextField textField = new JRDesignTextField();
		textField.setX(0);
		textField.setY(4);
		textField.setWidth(515);
		textField.setHeight(15);
		textField.setBackcolor(new Color(0xC0, 0xC0, 0xC0));
		textField.setMode(ModeEnum.OPAQUE);
		textField.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
		textField.setStyle(boldStyle);
		textField.setExpression(new JRDesignExpression("\"  \" + String.valueOf($V{CityNumber}) + \". \" + String.valueOf($F{City})"));
		band.addElement(textField);
		JRDesignLine line = new JRDesignLine();
		line.setX(0);
		line.setY(19);
		line.setWidth(515);
		line.setHeight(0);
		band.addElement(line);
		((JRDesignSection)group.getGroupHeaderSection()).addBand(band);

		band = new JRDesignBand();
		band.setHeight(20);
		line = new JRDesignLine();
		line.setX(0);
		line.setY(-1);
		line.setWidth(515);
		line.setHeight(0);
		band.addElement(line);
		JRDesignStaticText staticText = new JRDesignStaticText();
		staticText.setX(400);
		staticText.setY(0);
		staticText.setWidth(60);
		staticText.setHeight(15);
		staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		staticText.setStyle(boldStyle);
		staticText.setText("Count : ");
		band.addElement(staticText);
		textField = new JRDesignTextField();
		textField.setX(460);
		textField.setY(0);
		textField.setWidth(30);
		textField.setHeight(15);
		textField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		textField.setStyle(boldStyle);
		textField.setExpression(new JRDesignExpression("$V{CityGroup_COUNT}"));
		band.addElement(textField);
		((JRDesignSection)group.getGroupFooterSection()).addBand(band);

		jasperDesign.addGroup(group);

		//Title
		band = new JRDesignBand();
		band.setHeight(50);
		line = new JRDesignLine();
		line.setX(0);
		line.setY(0);
		line.setWidth(515);
		line.setHeight(0);
		band.addElement(line);
		textField = new JRDesignTextField();
		textField.setBlankWhenNull(true);
		textField.setX(0);
		textField.setY(10);
		textField.setWidth(515);
		textField.setHeight(30);
		textField.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		textField.setStyle(normalStyle);
		textField.setFontSize(22f);
		textField.setExpression(new JRDesignExpression("$P{ReportTitle}"));
		band.addElement(textField);
		jasperDesign.setTitle(band);
		
		//Page header
		band = new JRDesignBand();
		band.setHeight(20);
		JRDesignFrame frame = new JRDesignFrame();
		frame.setX(0);
		frame.setY(5);
		frame.setWidth(515);
		frame.setHeight(15);
		frame.setForecolor(new Color(0x33, 0x33, 0x33));
		frame.setBackcolor(new Color(0x33, 0x33, 0x33));
		frame.setMode(ModeEnum.OPAQUE);
		band.addElement(frame);
		staticText = new JRDesignStaticText();
		staticText.setX(0);
		staticText.setY(0);
		staticText.setWidth(55);
		staticText.setHeight(15);
		staticText.setForecolor(Color.white);
		staticText.setBackcolor(new Color(0x33, 0x33, 0x33));
		staticText.setMode(ModeEnum.OPAQUE);
		staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.CENTER);
		staticText.setStyle(boldStyle);
		staticText.setText("ID");
		frame.addElement(staticText);
		staticText = new JRDesignStaticText();
		staticText.setX(55);
		staticText.setY(0);
		staticText.setWidth(205);
		staticText.setHeight(15);
		staticText.setForecolor(Color.white);
		staticText.setBackcolor(new Color(0x33, 0x33, 0x33));
		staticText.setMode(ModeEnum.OPAQUE);
		staticText.setStyle(boldStyle);
		staticText.setText("Name");
		frame.addElement(staticText);
		staticText = new JRDesignStaticText();
		staticText.setX(260);
		staticText.setY(0);
		staticText.setWidth(255);
		staticText.setHeight(15);
		staticText.setForecolor(Color.white);
		staticText.setBackcolor(new Color(0x33, 0x33, 0x33));
		staticText.setMode(ModeEnum.OPAQUE);
		staticText.setStyle(boldStyle);
		staticText.setText("Street");
		frame.addElement(staticText);
		jasperDesign.setPageHeader(band);

		//Column header
		band = new JRDesignBand();
		jasperDesign.setColumnHeader(band);

		//Detail
		band = new JRDesignBand();
		band.setHeight(20);
		textField = new JRDesignTextField();
		textField.setX(0);
		textField.setY(4);
		textField.setWidth(50);
		textField.setHeight(15);
		textField.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
		textField.setStyle(normalStyle);
		textField.setExpression(new JRDesignExpression("$F{Id}"));
		band.addElement(textField);
		textField = new JRDesignTextField();
		textField.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
		textField.setX(55);
		textField.setY(4);
		textField.setWidth(200);
		textField.setHeight(15);
		textField.setPositionType(PositionTypeEnum.FLOAT);
		textField.setStyle(normalStyle);
		textField.setExpression(new JRDesignExpression("$F{FirstName} + \" \" + $F{LastName}"));
		band.addElement(textField);
		textField = new JRDesignTextField();
		textField.setTextAdjust(TextAdjustEnum.STRETCH_HEIGHT);
		textField.setX(260);
		textField.setY(4);
		textField.setWidth(255);
		textField.setHeight(15);
		textField.setPositionType(PositionTypeEnum.FLOAT);
		textField.setStyle(normalStyle);
		textField.setExpression(new JRDesignExpression("$F{Street}"));
		band.addElement(textField);
		line = new JRDesignLine();
		line.setX(0);
		line.setY(19);
		line.setWidth(515);
		line.setHeight(0);
		line.setForecolor(new Color(0x80, 0x80, 0x80));
		line.setPositionType(PositionTypeEnum.FLOAT);
		band.addElement(line);
		((JRDesignSection)jasperDesign.getDetailSection()).addBand(band);

		//Column footer
		band = new JRDesignBand();
		jasperDesign.setColumnFooter(band);

		//Page footer
		band = new JRDesignBand();
		jasperDesign.setPageFooter(band);

		//Summary
		band = new JRDesignBand();
		jasperDesign.setSummary(band);
		
		return jasperDesign;
	}
	

}
