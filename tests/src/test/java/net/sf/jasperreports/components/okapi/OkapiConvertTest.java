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
package net.sf.jasperreports.components.okapi;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.awt.geom.Dimension2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.zxing.NotFoundException;

import net.sf.jasperreports.barbecue.BarbecueComponent;
import net.sf.jasperreports.barcode4j.BarcodeComponent;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRVisitorSupport;
import net.sf.jasperreports.engine.util.ReportUpdater;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.okapi.Code128CodeSetEnum;
import net.sf.jasperreports.okapi.Code128Component;
import net.sf.jasperreports.okapi.Code39CheckDigitEnum;
import net.sf.jasperreports.okapi.Code39Component;
import net.sf.jasperreports.okapi.Code39ExtendedComponent;
import net.sf.jasperreports.okapi.DataMatrixComponent;
import net.sf.jasperreports.okapi.DataMatrixForceModeEnum;
import net.sf.jasperreports.okapi.EANComponent;
import net.sf.jasperreports.okapi.EANModeEnum;
import net.sf.jasperreports.okapi.HumanReadableLocationEnum;
import net.sf.jasperreports.okapi.Interleaved2Of5CheckDigitEnum;
import net.sf.jasperreports.okapi.Interleaved2Of5Component;
import net.sf.jasperreports.okapi.PDF417Component;
import net.sf.jasperreports.okapi.POSTNETComponent;
import net.sf.jasperreports.okapi.QRCodeComponent;
import net.sf.jasperreports.okapi.QRCodeEccLevelEnum;
import net.sf.jasperreports.okapi.RoyalMail4StateComponent;
import net.sf.jasperreports.okapi.UPCComponent;
import net.sf.jasperreports.okapi.UPCModeEnum;
import net.sf.jasperreports.okapi.convert.BarbecueConverter;
import net.sf.jasperreports.okapi.convert.Barcode4jConverter;


/**
 * Tests the converters from barbecue and barcode4j elements to okapi:* elements.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OkapiConvertTest
{

	private static final String RESOURCES = "net/sf/jasperreports/components/okapi/";


	@Test
	public void barbecue() throws JRException, IOException, NotFoundException
	{
		JasperDesign design = load("BarbecueConvert.jrxml");
		Map<String, JRComponentElement> before = elements(design);
		JRExpression code128Expression = ((BarbecueComponent) before.get("code128").getComponent()).getCodeExpression();

		JasperDesign converted = convert(new BarbecueConverter(), design);
		// the updater changes the design in place, keeping the element and the expression objects
		JRComponentElement updated = elements(design).get("code128");
		assertSame(updated, before.get("code128"), "element kept");
		assertSame(((Code128Component) updated.getComponent()).getCodeExpression(), code128Expression,
			"code expression kept");

		Map<String, JRComponentElement> after = elements(converted);
		Code128Component code128 = (Code128Component) after.get("code128").getComponent();
		assertEquals(code128.getEvaluationTime(), EvaluationTimeEnum.REPORT);
		assertEquals(code128.getRotation(), RotationEnum.LEFT);
		assertEquals(code128.getHumanReadableLocation(), HumanReadableLocationEnum.BOTTOM);
		assertEquals(code128.getModuleWidth(), 1.0);
		assertNull(code128.getCodeSet());

		Code128Component code128B = (Code128Component) after.get("code128B").getComponent();
		assertEquals(code128B.getCodeSet(), Code128CodeSetEnum.B);
		assertEquals(code128B.getHumanReadableLocation(), HumanReadableLocationEnum.NONE);
		assertNull(code128B.getModuleWidth(), "unset bar width stays unset");
		assertNull(code128B.getRotation(), "unset rotation stays unset");

		assertEquals(((EANComponent) after.get("ean13").getComponent()).getMode(), EANModeEnum.EAN13);
		assertTrue(after.get("inFrame").getComponent() instanceof Code128Component, "element in a frame converted");
		assertTrue(after.get("inTable").getComponent() instanceof Code128Component, "element in a table converted");

		Code39Component code39 = (Code39Component) after.get("code39").getComponent();
		assertEquals(code39.getCheckDigit(), Code39CheckDigitEnum.MOD43);
		assertEquals(code39.getModuleWidth(), 1.0);
		assertNull(((Code39Component) after.get("usd3").getComponent()).getCheckDigit());
		assertEquals(((UPCComponent) after.get("upca").getComponent()).getMode(), UPCModeEnum.UPCA);

		PDF417Component barbecuePdf417 = (PDF417Component) after.get("pdf417").getComponent();
		assertEquals(barbecuePdf417.getDataColumns(), Integer.valueOf(12), "barbecue's fixed data columns");
		assertEquals(barbecuePdf417.getPreferredEccLevel(), Integer.valueOf(0));
		POSTNETComponent barbecuePostnet = (POSTNETComponent) after.get("postnet").getComponent();
		assertEquals(barbecuePostnet.getHumanReadableLocation(), HumanReadableLocationEnum.BOTTOM);

		for (String key : new String[] {"gtin", "codabar", "std2of5Checksum", "int2of5",
			"code39Extended", "std2of5"})
		{
			assertTrue(after.get(key).getComponent() instanceof BarbecueComponent, key + " left unconverted");
		}

		Map<String, JRPrintImage> images = OkapiTest.images(fill(converted));
		assertEquals(OkapiTest.decode(images.get("code128")), "BARBECUE-128");
		assertEquals(OkapiTest.decode(images.get("code128B")), "Code128B");
		// barbecue adds the check digit to the 12 digits, as Okapi does
		assertEquals(OkapiTest.decode(images.get("ean13")), "5901234123457");
		// the decoder doesn't strip the mod 43 check character, 5
		assertEquals(OkapiTest.decode(images.get("code39")), "BARBECUE-395");
		assertEquals(OkapiTest.decode(images.get("usd3")), "USD3");
		assertEquals(OkapiTest.decode(images.get("pdf417")), "PDF417");
		assertEquals(OkapiTest.decode(images.get("upca")), "036000291452");
	}


	@Test
	public void barcode4j() throws JRException, IOException, NotFoundException
	{
		JasperDesign design = load("Barcode4jConvert.jrxml");
		JasperDesign converted = convert(new Barcode4jConverter(), design);
		Map<String, JRComponentElement> after = elements(converted);

		Code128Component code128 = (Code128Component) after.get("code128").getComponent();
		assertEquals(code128.getEvaluationTime(), EvaluationTimeEnum.REPORT);
		assertEquals(code128.getRotation(), RotationEnum.LEFT);
		assertEquals(code128.getHumanReadableLocation(), HumanReadableLocationEnum.TOP);
		assertEquals(code128.getModuleWidth(), 1.0);
		assertEquals(code128.getQuietZoneHorizontal(), 5.0);
		assertEquals(code128.getQuietZoneVertical(), 2.0);

		EANComponent ean13 = (EANComponent) after.get("ean13Add").getComponent();
		assertEquals(ean13.getMode(), EANModeEnum.EAN13);
		assertNull(ean13.getHumanReadableLocation(), "unset text position stays unset");
		// barcode4j's default module width, 0.33 mm for EAN and 0.21 mm for Code 128, in report units at
		// 72 dpi, by barcode4j's own 2.835 points per millimetre
		assertEquals(ean13.getModuleWidth(), 0.33 * 2.835, 1e-6);
		EANComponent ean8 = (EANComponent) after.get("ean8Add").getComponent();
		assertEquals(ean8.getMode(), EANModeEnum.EAN8);
		assertEquals(ean8.getHumanReadableLocation(), HumanReadableLocationEnum.NONE);
		assertEquals(ean8.getModuleWidth(), 0.33 * 2.835, 1e-6);

		QRCodeComponent qrCode = (QRCodeComponent) after.get("qrCode").getComponent();
		assertEquals(qrCode.getPreferredEccLevel(), QRCodeEccLevelEnum.H);
		assertEquals(qrCode.getPreferredVersion(), Integer.valueOf(3));
		assertNull(qrCode.getQuietZoneHorizontal(), "margin in modules is dropped");

		Code128Component inFrame = (Code128Component) after.get("inFrame").getComponent();
		assertEquals(inFrame.getModuleWidth(), 0.21 * 2.835, 1e-6,
			"element in a frame converted, with the default module width");
		QRCodeComponent inTable = (QRCodeComponent) after.get("inTable").getComponent();
		assertEquals(inTable.getPreferredEccLevel(), QRCodeEccLevelEnum.L, "legacy default error correction");

		Code39ExtendedComponent code39 = (Code39ExtendedComponent) after.get("code39Extended").getComponent();
		assertEquals(code39.getCheckDigit(), Code39CheckDigitEnum.MOD43);
		assertEquals(code39.getModuleWidthRatio(), 3.0);
		assertEquals(code39.getModuleWidth(), 0.19 * 2.835, 1e-6);
		Interleaved2Of5Component itfAdd = (Interleaved2Of5Component) after.get("itfAdd").getComponent();
		assertEquals(itfAdd.getCheckDigit(), Interleaved2Of5CheckDigitEnum.MOD10);
		assertEquals(itfAdd.getModuleWidthRatio(), 2.5);
		Interleaved2Of5Component itfAuto = (Interleaved2Of5Component) after.get("itfAuto").getComponent();
		assertNull(itfAuto.getCheckDigit());
		assertEquals(itfAuto.getModuleWidthRatio(), 3.0, "barcode4j's default wide factor");
		assertEquals(itfAuto.getModuleWidth(), 0.21 * 2.835, 1e-6);
		assertEquals(((UPCComponent) after.get("upcaAdd").getComponent()).getMode(), UPCModeEnum.UPCA);
		UPCComponent upce = (UPCComponent) after.get("upceAdd").getComponent();
		assertEquals(upce.getMode(), UPCModeEnum.UPCE);
		assertEquals(upce.getModuleWidth(), 0.33 * 2.835, 1e-6);
		DataMatrixComponent dataMatrix = (DataMatrixComponent) after.get("dataMatrix").getComponent();
		assertEquals(dataMatrix.getRotation(), RotationEnum.RIGHT);
		assertEquals(dataMatrix.getQuietZoneHorizontal(), 2.0);
		assertNull(dataMatrix.getModuleWidth(), "the legacy 2D barcode fills the element whatever its module width");
		assertNull(dataMatrix.getForceMode());
		DataMatrixComponent dataMatrixRectangle = (DataMatrixComponent) after.get("dataMatrixRectangle").getComponent();
		assertEquals(dataMatrixRectangle.getForceMode(), DataMatrixForceModeEnum.RECTANGULAR);
		PDF417Component pdf417 = (PDF417Component) after.get("pdf417").getComponent();
		assertEquals(pdf417.getDataColumns(), Integer.valueOf(2), "barcode4j's default column bounds, 2 to 2");
		assertEquals(pdf417.getPreferredEccLevel(), Integer.valueOf(2));
		assertNull(pdf417.getModuleWidth(), "the legacy 2D barcode fills the element whatever its module width");
		POSTNETComponent postnet = (POSTNETComponent) after.get("postnetAdd").getComponent();
		assertEquals(postnet.getModuleWidth(), 1.0);
		assertEquals(postnet.getModuleWidthRatio(), 1.5, 1e-6, "the inter-character gap over the module width");
		assertEquals(postnet.getHumanReadableLocation(), HumanReadableLocationEnum.BOTTOM);
		// the checksum mode auto, the default, adds the check character as Okapi does
		assertTrue(after.get("royalMail").getComponent() instanceof RoyalMail4StateComponent, "royalMail converted");

		for (String key : new String[] {"ean13Auto", "ean13Check", "ean128", "qrLatin1", "code39",
			"code39ExtendedCheck", "upcaAuto", "codabar", "dataMatrixBounds", "pdf417Columns", "postnetAuto",
			"royalMailCheck", "uspsIntelligentMail"})
		{
			assertTrue(after.get(key).getComponent() instanceof BarcodeComponent, key + " left unconverted");
		}

		Map<String, JRPrintImage> images = OkapiTest.images(fill(converted));
		assertEquals(OkapiTest.decode(images.get("code128")), "BARCODE4J-128");
		assertEquals(OkapiTest.decode(images.get("ean13Add")), "5901234123457");
		assertEquals(OkapiTest.decode(images.get("ean8Add")), "96385074");
		assertEquals(OkapiTest.decode(images.get("qrCode")), "BARCODE4J QR");
		// the decoder isn't in Full ASCII mode and doesn't strip the mod 43 check character, $
		assertEquals(OkapiTest.decode(images.get("code39Extended")), "B+A+R+C+O+D+E4+J-39$");
		assertEquals(OkapiTest.decode(images.get("itfAdd")), "1234567895");
		// barcode4j's default module, 0.21 mm, with its default wide factor of 3 is too fine for the decoder at 288 dpi
		assertEquals(OkapiTest.decode(images.get("itfAuto"), 8), "0123456789");
		assertEquals(OkapiTest.decode(images.get("upcaAdd")), "036000291452");
		assertEquals(OkapiTest.decode(images.get("upceAdd")), "01234565");
		assertEquals(OkapiTest.decode(images.get("dataMatrix")), "BARCODE4J DM");
		assertEquals(OkapiTest.decode(images.get("pdf417")), "BARCODE4J PDF417");
		// the decoder doesn't find the 8 x 32 symbol; zxing-cpp reads it
		Dimension2D rectangle = OkapiTest.size(images.get("dataMatrixRectangle"));
		assertTrue(rectangle.getWidth() > 2 * rectangle.getHeight(), "rectangular in a square element: " + rectangle);
	}


	/**
	 * Converts the report and reads the JRXML written for it back, as the Maven plugin's update goal
	 * writes it, so that the result is also checked to parse.
	 */
	private static JasperDesign convert(ReportUpdater updater, JasperDesign design) throws JRException
	{
		JasperDesign updated = updater.update(design);
		String jrxml = JRXmlWriter.writeReport(updated, StandardCharsets.UTF_8.name());
		assertTrue(jrxml.contains("okapi:"), "okapi:* elements written");
		return JRXmlLoader.load(new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8)));
	}

	private static JasperDesign load(String name) throws JRException, IOException
	{
		try (InputStream input = JRLoader.getResourceInputStream(RESOURCES + name))
		{
			return JRXmlLoader.load(input);
		}
	}

	/**
	 * The component elements of the report, at any depth, by key.
	 */
	private static Map<String, JRComponentElement> elements(JasperDesign design)
	{
		Map<String, JRComponentElement> elements = new HashMap<>();
		JRElementsVisitor.visitReport(design, new JRVisitorSupport()
		{
			@Override
			public void visitComponentElement(JRComponentElement element)
			{
				if (element.getKey() != null)
				{
					elements.put(element.getKey(), element);
				}
			}
		});
		return elements;
	}

	private static JasperPrint fill(JasperDesign design) throws JRException
	{
		Map<String, Object> params = new HashMap<>();
		params.put(JRParameter.REPORT_LOCALE, Locale.US);
		return JasperFillManager.fillReport(JasperCompileManager.compileReport(design), params);
	}
}
