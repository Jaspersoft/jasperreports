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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import net.sf.jasperreports.Report;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.convert.ReportConverter;
import net.sf.jasperreports.engine.design.JRValidationException;
import net.sf.jasperreports.engine.design.JRValidationFault;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.okapi.OkapiComponent;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;

/**
 * Tests the okapi:* barcode components: JRXML, compile-time validation, sizing, the
 * drawn data and serialization of the filled report.
 */
public class OkapiTest
{

	private static final String RESOURCES = "net/sf/jasperreports/components/okapi/";
	private static final double TOLERANCE = 0.01;

	@Test
	public void jrxmlRoundTrip() throws JRException, IOException
	{
		String written = JRXmlWriter.writeReport(load("OkapiRoundTrip.jrxml"), "UTF-8");
		JasperDesign reloaded = JRXmlLoader.load(new ByteArrayInputStream(written.getBytes(StandardCharsets.UTF_8)));
		assertEquals(JRXmlWriter.writeReport(reloaded, "UTF-8"), written);

		String[] expected = {
			"kind=\"okapi:Code128\"", "evaluationTime=\"Group\"", "evaluationGroup=\"g\"", "rotation=\"Left\"",
			"hImageAlign=\"Center\"", "vImageAlign=\"Bottom\"", "quietZoneHorizontal=\"5.0\"", "quietZoneVertical=\"2.0\"",
			"moduleWidth=\"1.5\"", "humanReadableLocation=\"Top\"", "humanReadableAlignment=\"Justify\"",
			"codeSet=\"AB\"", "dataType=\"GS1\"", "readerInit=\"true\"",
			"kind=\"okapi:EAN\"", "mode=\"EAN8\"", "guardPatternExtraHeight=\"3.0\"", "humanReadableLocation=\"None\"",
			"kind=\"okapi:QRCode\"", "preferredVersion=\"3\"", "preferredEccLevel=\"Q\"", "forceByteCompaction=\"true\"",
			"dataType=\"HIBC\"", "eciMode=\"26\"", "structuredAppendForced=\"true\"",
			"<codeExpression><![CDATA[\"[01]09501101530003\"]]></codeExpression>",
			"<structuredAppendPositionExpression><![CDATA[1]]></structuredAppendPositionExpression>",
			"<structuredAppendTotalExpression><![CDATA[2]]></structuredAppendTotalExpression>",
			"<structuredAppendParityExpression><![CDATA[7]]></structuredAppendParityExpression>",
		};
		for (String text : expected)
		{
			assertTrue(written.contains(text), "written JRXML lacks " + text + ":\n" + written);
		}
	}

	@Test
	public void validation() throws JRException, IOException
	{
		List<String> messages = new ArrayList<>();
		try
		{
			JasperCompileManager.compileReport(load("OkapiInvalid.jrxml"));
			fail("the report should not compile");
		}
		catch (JRValidationException e)
		{
			for (JRValidationFault fault : e.getFaults())
			{
				messages.add(fault.getMessage());
			}
		}

		String[] expected = {
			"Barcode expression is null",
			"Barcode moduleWidth must be positive: 0.0",
			"Barcode quietZoneHorizontal must not be negative: -1.0",
			"Barcode evaluation group \"missing\" not found",
			"Barcode structured append needs both structuredAppendPositionExpression and structuredAppendTotalExpression",
			"Auto evaluation time is not supported for barcodes",
		};
		for (String message : expected)
		{
			assertTrue(messages.contains(message), "missing fault \"" + message + "\" in " + messages);
		}
		// Okapi's own setter checks: the QR version and the ECI mode
		assertEquals(messages.size(), expected.length + 2, "faults: " + messages);
	}

	@Test
	public void fill() throws JRException, IOException, NotFoundException
	{
		JasperPrint print = fill("OkapiFill.jrxml");
		checkFilled(images(print));
	}

	@Test
	public void serialization() throws JRException, IOException, NotFoundException
	{
		JasperPrint print = fill("OkapiFill.jrxml");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JRSaver.saveObject(print, out);
		// the renderers keep no Okapi symbol: they encode it again after loading
		JasperPrint loaded = (JasperPrint) JRLoader.loadObject(new ByteArrayInputStream(out.toByteArray()));
		checkFilled(images(loaded));
	}

	private void checkFilled(Map<String, JRPrintImage> images) throws JRException, NotFoundException
	{
		assertEquals(images.keySet(), new HashSet<>(java.util.Arrays.asList(
				"code128Fit", "code128Set", "code128TooWide", "code128Left", "code128Style", "code128Gs1",
				"ean13", "ean8", "qrFit", "qrSet", "qrTooLarge", "qrDelayed")));

		// linear codes: as wide as the element without a module width, or with one that doesn't fit
		check(images.get("code128Fit"), ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "OKAPI-128");
		assertEquals(size(images.get("code128Fit")).getWidth(), 250, TOLERANCE);
		check(images.get("code128TooWide"), ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "OKAPI-128");
		assertEquals(size(images.get("code128TooWide")).getWidth(), 150, TOLERANCE);

		// a set module width that fits: one point per module, at most the element height
		JRPrintImage set = images.get("code128Set");
		check(set, ScaleImageEnum.CLIP, RotationEnum.NONE, "OKAPI-128");
		assertEquals(set.getHorizontalImageAlign(), HorizontalImageAlignEnum.CENTER);
		Dimension2D setSize = size(set);
		assertEquals(setSize.getWidth(), Math.rint(setSize.getWidth()), TOLERANCE, "whole modules");
		assertTrue(setSize.getWidth() < 250 && setSize.getHeight() <= 50, "size " + setSize);
		// the same symbol filling the element is drawn at a larger module width
		assertTrue(size(images.get("code128Fit")).getWidth() > setSize.getWidth());

		// rotated by 90 degrees: sized against the element height
		JRPrintImage left = images.get("code128Left");
		check(left, ScaleImageEnum.RETAIN_SHAPE, RotationEnum.LEFT, "OKAPI-128");
		assertEquals(size(left).getWidth(), 250, TOLERANCE);
		assertTrue(size(left).getHeight() <= 50 + TOLERANCE);

		// rotation and alignment from the style
		JRPrintImage styled = images.get("code128Style");
		check(styled, ScaleImageEnum.CLIP, RotationEnum.RIGHT, "STYLE");
		assertEquals(styled.getHorizontalImageAlign(), HorizontalImageAlignEnum.RIGHT);

		// GS1-128: the decoder reads the application identifier data
		assertTrue(decode(images.get("code128Gs1")).contains("0109501101530003"));

		// EAN: the check digit is added
		check(images.get("ean13"), ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "5901234123457");
		check(images.get("ean8"), ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "12345670");

		// QR: square, filling the smaller element side without a module size
		JRPrintImage qrFit = images.get("qrFit");
		check(qrFit, ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "Hello Okapi");
		assertEquals(size(qrFit).getWidth(), 100, TOLERANCE);
		assertEquals(size(qrFit).getHeight(), 100, TOLERANCE);

		// a module size that fits: drawn at it, aligned as set
		JRPrintImage qrSet = images.get("qrSet");
		check(qrSet, ScaleImageEnum.CLIP, RotationEnum.NONE, "Hello Okapi");
		assertEquals(qrSet.getHorizontalImageAlign(), HorizontalImageAlignEnum.CENTER);
		assertEquals(qrSet.getVerticalImageAlign(), VerticalImageAlignEnum.MIDDLE);
		assertEquals(size(qrSet).getWidth() % 2, 0, TOLERANCE, "two points per module");

		// a module size that doesn't fit: kept, and the image shrinks the symbol
		JRPrintImage qrTooLarge = images.get("qrTooLarge");
		check(qrTooLarge, ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "Hello Okapi");
		assertTrue(size(qrTooLarge).getWidth() > 60);

		// delayed evaluation gets the template of its scale mode
		check(images.get("qrDelayed"), ScaleImageEnum.CLIP, RotationEnum.NONE, "Pages 1");
	}

	@Test
	public void designPreview() throws JRException, IOException, NotFoundException
	{
		JasperDesign design = load("OkapiFill.jrxml");
		Map<String, String> keys = new HashMap<>();
		for (JRElement element : design.getTitle().getElements())
		{
			keys.put(String.valueOf(element.getUUID()), element.getKey());
		}
		
		// the converter doesn't copy element keys: images are matched to elements by UUID
		Map<String, JRPrintImage> images = new HashMap<>();
		JasperPrint preview = new ReportConverter(DefaultJasperReportsContext.getInstance(), design, false).getJasperPrint();
		collectImages(preview.getPages().get(0).getElements(), keys, images);
		
		// literal codes are previewed as they are, and sized as when filled
		check(images.get("code128Fit"), ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "OKAPI-128");
		check(images.get("code128Left"), ScaleImageEnum.RETAIN_SHAPE, RotationEnum.LEFT, "OKAPI-128");
		check(images.get("code128Style"), ScaleImageEnum.CLIP, RotationEnum.RIGHT, "STYLE");
		check(images.get("ean8"), ScaleImageEnum.RETAIN_SHAPE, RotationEnum.NONE, "12345670");
		check(images.get("qrSet"), ScaleImageEnum.CLIP, RotationEnum.NONE, "Hello Okapi");
		assertEquals(images.get("qrSet").getHorizontalImageAlign(), HorizontalImageAlignEnum.CENTER);
		// a code computed by the report is previewed with the default code of the type
		check(images.get("qrDelayed"), ScaleImageEnum.CLIP, RotationEnum.NONE, "https://jasperreports.sourceforge.net");
	}
	
	private static void collectImages(List<JRPrintElement> elements, Map<String, String> keys, Map<String, JRPrintImage> images)
	{
		for (JRPrintElement element : elements)
		{
			if (element instanceof JRPrintFrame)
			{
				collectImages(((JRPrintFrame) element).getElements(), keys, images);
			}
			else if (element instanceof JRPrintImage)
			{
				images.put(keys.get(String.valueOf(element.getUUID())), (JRPrintImage) element);
			}
		}
	}
	
	@Test
	public void everyComponentClassIsRegistered() throws IOException, URISyntaxException, ClassNotFoundException
	{
		// the extension registers a manager for each component class that has an element type,
		// so a class without one could not be parsed, compiled or filled;
		// the concrete component classes in the jar
		File location = new File(OkapiComponent.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		List<String> classNames = new ArrayList<>();
		if (location.isDirectory())
		{
			File dir = new File(location, "net/sf/jasperreports/okapi");
			for (String name : dir.list())
			{
				classNames.add("net/sf/jasperreports/okapi/" + name);
			}
		}
		else
		{
			try (JarFile jar = new JarFile(location))
			{
				for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();)
				{
					classNames.add(entries.nextElement().getName());
				}
			}
		}
		int concrete = 0;
		for (String name : classNames)
		{
			if (name.matches("net/sf/jasperreports/okapi/[^/$]+\\.class"))
			{
				Class<?> type = Class.forName(name.replace('/', '.').replace(".class", ""));
				if (OkapiComponent.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers()))
				{
					++concrete;
					assertNotNull(ComponentsEnvironment.getInstance(DefaultJasperReportsContext.getInstance())
							.getManager(type.asSubclass(OkapiComponent.class)), type + " has no component manager");
				}
			}
		}
		assertEquals(concrete, 3, "concrete component classes found");
	}

	@Test
	public void nullCode(ITestContext testContext)
	{
		Report report = new Report(RESOURCES + "OkapiNull.jrxml", RESOURCES + "OkapiNull.jrpxml");
		report.init(testContext);
		report.runReport(new HashMap<>());
	}

	private static JasperDesign load(String name) throws JRException, IOException
	{
		try (InputStream input = JRLoader.getResourceInputStream(RESOURCES + name))
		{
			return JRXmlLoader.load(input);
		}
	}

	private static JasperPrint fill(String name) throws JRException, IOException
	{
		JasperReport report = JasperCompileManager.compileReport(load(name));
		Map<String, Object> params = new HashMap<>();
		params.put(JRParameter.REPORT_LOCALE, Locale.US);
		return JasperFillManager.fillReport(report, params);
	}

	static Map<String, JRPrintImage> images(JasperPrint print)
	{
		Map<String, JRPrintImage> images = new HashMap<>();
		for (JRPrintPage page : print.getPages())
		{
			for (JRPrintElement element : page.getElements())
			{
				if (element instanceof JRPrintImage && element.getKey() != null)
				{
					images.put(element.getKey(), (JRPrintImage) element);
				}
			}
		}
		return images;
	}

	private static void check(JRPrintImage image, ScaleImageEnum scale, RotationEnum rotation, String data)
			throws JRException, NotFoundException
	{
		String key = image.getKey();
		assertEquals(image.getScaleImage(), scale, key + " scale");
		assertEquals(image.getRotation(), rotation, key + " rotation");
		assertEquals(decode(image), data, key + " data");
	}

	private static Dimension2D size(JRPrintImage image) throws JRException
	{
		return ((DimensionRenderable) image.getRenderer()).getDimension(DefaultJasperReportsContext.getInstance());
	}

	/**
	 * Draws the barcode upright at 4 pixels per point, with a white margin, and decodes it.
	 */
	static String decode(JRPrintImage image) throws JRException, NotFoundException
	{
		Dimension2D size = size(image);
		int zoom = 4;
		int margin = 40;
		int width = (int) Math.ceil(size.getWidth() * zoom) + 2 * margin;
		int height = (int) Math.ceil(size.getHeight() * zoom) + 2 * margin;
		BufferedImage raster = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = raster.createGraphics();
		try
		{
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, width, height);
			((Graphics2DRenderable) image.getRenderer()).render(DefaultJasperReportsContext.getInstance(), graphics,
					new Rectangle2D.Double(margin, margin, size.getWidth() * zoom, size.getHeight() * zoom));
		}
		finally
		{
			graphics.dispose();
		}
		int[] pixels = raster.getRGB(0, 0, width, height, null, 0, width);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(width, height, pixels)));
		Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
		hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		return new MultiFormatReader().decode(bitmap, hints).getText();
	}
}
