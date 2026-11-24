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
package net.sf.jasperreports.barbecue;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.util.ContextClassLoaderObjectInputStream;
import net.sf.jasperreports.engine.util.DeserializationClassFilter;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;

/**
 * Tests the serialization of barbecue renderers, and that renderers serialized with the deprecated
 * {@link BarbecueRendererImpl} can only be deserialized when the legacy classes are whitelisted.
 */
@SuppressWarnings("deprecation")
public class BarbecueRendererSerializationTest
{

	/**
	 * The whitelist that needs to be configured to deserialize {@link BarbecueRendererImpl} instances,
	 * as documented for users that need to read old prints.
	 */
	private static final String LEGACY_WHITELIST =
			"net.sf.jasperreports.barbecue.BarbecueRendererImpl,"
			+ "net.sourceforge.barbecue.Barcode,net.sourceforge.barbecue.**.*Barcode,"
			+ "java.awt.Component,java.awt.Container,java.awt.ComponentOrientation,java.awt.Dimension,"
			+ "javax.swing.JComponent,javax.swing.event.EventListenerList,"
			+ "java.beans.PropertyChangeSupport,java.beans.VetoableChangeSupport";

	// DeserializationClassFilter.EXCEPTION_MESSAGE_KEY_CLASS_NOT_VISIBLE, which is private
	private static final String CLASS_NOT_VISIBLE_MESSAGE_KEY = "deserialization.class.not.visible";

	private static final String[] TYPES = {
			"2of7", "3of9", "Bookland", "Codabar", "Code128", "Code128A", "Code128B", "Code128C",
			"Code39", "Code39 (Extended)", "EAN128", "EAN13", "GlobalTradeItemNumber", "Int2of5",
			"Monarch", "NW7", "PDF417", "PostNet", "RandomWeightUPCA", "SCC14ShippingCode",
			"ShipmentIdentificationNumber", "SSCC18", "Std2of5", "UCC128", "UPCA", "USD3", "USD4", "USPS"};

	private static final String[] CODES = {"123456789012", "1234567890", "12345678901", "1234567890123",
			"12345678901234567", "0123456789"};

	private static final String[] APPLICATION_IDENTIFIERS = {null, "00", "01"};

	@DataProvider
	public Object[][] barcodeTypes()
	{
		Object[][] data = new Object[TYPES.length][];
		for (int i = 0; i < TYPES.length; i++)
		{
			data[i] = new Object[] {TYPES[i]};
		}
		return data;
	}

	/**
	 * Finds a code that is valid for the barcode type.
	 */
	private static BarcodeInfo createBarcodeInfo(String type)
	{
		List<Exception> errors = new ArrayList<>();
		for (String applicationIdentifier : APPLICATION_IDENTIFIERS)
		{
			for (String code : CODES)
			{
				BarcodeInfo barcodeInfo = new BarcodeInfo();
				barcodeInfo.setType(type);
				barcodeInfo.setCode(code);
				barcodeInfo.setApplicationIdentifier(applicationIdentifier);
				barcodeInfo.setDrawText(true);
				try
				{
					BarcodeProviders.createBarcode(barcodeInfo);
					return barcodeInfo;
				}
				catch (RuntimeException e)
				{
					errors.add(e);
				}
			}
		}
		throw new AssertionError("Could not create barcode of type " + type + ": " + errors);
	}

	private static byte[] serialize(Object object) throws IOException
	{
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(bytesOut))
		{
			out.writeObject(object);
		}
		return bytesOut.toByteArray();
	}

	/**
	 * Serializes a legacy renderer, skipping the test for barcode types that were never serializable.
	 */
	private static byte[] serializeLegacy(BarbecueRendererImpl renderer) throws IOException
	{
		try
		{
			return serialize(renderer);
		}
		catch (NotSerializableException e)
		{
			throw new SkipException("barcode type not serializable with the legacy renderer: " + e.getMessage());
		}
	}
	
	private static Object deserialize(JasperReportsContext jasperReportsContext, byte[] data)
			throws IOException, ClassNotFoundException
	{
		try (ContextClassLoaderObjectInputStream in = new ContextClassLoaderObjectInputStream(
				jasperReportsContext, new ByteArrayInputStream(data)))
		{
			return in.readObject();
		}
	}

	private static <R extends Graphics2DRenderable & DimensionRenderable> void assertRenders(
			JasperReportsContext jasperReportsContext, Dimension2D expectedDimension, R renderer) throws JRException
	{
		Dimension2D dimension = renderer.getDimension(jasperReportsContext);
		assert dimension.equals(expectedDimension) : dimension + " differs from " + expectedDimension;

		BufferedImage image = new BufferedImage(
				(int) Math.ceil(dimension.getWidth()), (int) Math.ceil(dimension.getHeight()), BufferedImage.TYPE_INT_RGB);
		Graphics2D grx = image.createGraphics();
		try
		{
			renderer.render(jasperReportsContext, grx, new Rectangle2D.Double(0, 0, dimension.getWidth(), dimension.getHeight()));
		}
		finally
		{
			grx.dispose();
		}
	}

	@Test(dataProvider = "barcodeTypes")
	public void rendererRoundTrip(String type) throws IOException, ClassNotFoundException, JRException
	{
		JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
		BarcodeInfo barcodeInfo = createBarcodeInfo(type);
		BarbecueBarcodeRenderer renderer = new BarbecueBarcodeRenderer(barcodeInfo);
		Dimension2D dimension = renderer.getDimension(jasperReportsContext);

		BarbecueBarcodeRenderer deserialized = (BarbecueBarcodeRenderer) deserialize(jasperReportsContext, serialize(renderer));

		assert deserialized.getId().equals(renderer.getId());
		assert deserialized.getBarcodeInfo().getCode().equals(barcodeInfo.getCode());
		assertRenders(jasperReportsContext, dimension, deserialized);
	}

	@Test(dataProvider = "barcodeTypes")
	public void legacyRendererRejectedByDefault(String type) throws IOException, ClassNotFoundException
	{
		byte[] data = serializeLegacy(new BarbecueRendererImpl(BarcodeProviders.createBarcode(createBarcodeInfo(type))));

		try
		{
			deserialize(DefaultJasperReportsContext.getInstance(), data);
			assert false : "the legacy renderer should not be deserialized without whitelisting";
		}
		catch (JRRuntimeException e)
		{
			assert CLASS_NOT_VISIBLE_MESSAGE_KEY.equals(e.getMessageKey()) : e;
		}
	}

	/**
	 * Legacy renderer as serialized right after the report is filled.
	 */
	@Test(dataProvider = "barcodeTypes")
	public void legacyRendererWhitelisted(String type) throws IOException, ClassNotFoundException, JRException
	{
		assertLegacyRendererWhitelisted(type, false);
	}

	/**
	 * Legacy renderer as serialized after the barcode was sized, for instance when the report was exported.
	 * Sizing the barcode adds a java.awt.Dimension to the serialized object graph.
	 */
	@Test(dataProvider = "barcodeTypes")
	public void legacySizedRendererWhitelisted(String type) throws IOException, ClassNotFoundException, JRException
	{
		assertLegacyRendererWhitelisted(type, true);
	}

	private void assertLegacyRendererWhitelisted(String type, boolean sized) throws IOException, ClassNotFoundException, JRException
	{
		SimpleJasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();
		jasperReportsContext.setProperty(DeserializationClassFilter.PROPERTY_PREFIX_CLASS_WHITELIST + "legacy.barbecue",
				LEGACY_WHITELIST);

		BarbecueRendererImpl renderer = new BarbecueRendererImpl(BarcodeProviders.createBarcode(createBarcodeInfo(type)));
		renderer.setRotation(RotationEnum.RIGHT);
		if (sized)
		{
			renderer.getDimension(jasperReportsContext);
		}
		byte[] data = serializeLegacy(renderer);
		Dimension2D dimension = renderer.getDimension(jasperReportsContext);

		BarbecueRendererImpl deserialized = (BarbecueRendererImpl) deserialize(jasperReportsContext, data);

		assertRenders(jasperReportsContext, dimension, deserialized);
	}

}
