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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.linear.ean.UCCEAN128Barcode;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class BarcodeProviders
{
	private static final String EXCEPTION_MESSAGE_KEY_BARCODE_PROVIDER_NOT_FOUND = "components.barbecue.barcode.provider.not.found";
	private static final String EXCEPTION_MESSAGE_KEY_ERROR_CREATING_BARCODE = "components.barbecue.error.creating.barcode";
	
	private static Map<String, BarcodeProvider> providers;

	private static synchronized void initProviders()
	{
		if (providers != null)
		{
			return;
		}

		providers = new HashMap<>();
		providers.put("2of7", new Barcode2of7Provider());
		providers.put("3of9", new Barcode3of9Provider());
		providers.put("Bookland", new BooklandProvider());
		providers.put("Codabar", new CodabarProvider());
		providers.put("Code128", new Code128Provider());
		providers.put("Code128A", new Code128AProvider());
		providers.put("Code128B", new Code128Provider());
		providers.put("Code128C", new Code128CProvider());
		providers.put("Code39", new Code39Provider());
		providers.put("Code39 (Extended)", 
				new Code39ExtendedProvider());
		providers.put("EAN128", new EAN128Provider());
		providers.put("EAN13", new EAN13Provider());
		providers.put("GlobalTradeItemNumber", 
				new GlobalTradeItemNumberProvider());
		providers.put("Int2of5", new Int2of5Provider());
		providers.put("Monarch", new MonarchProvider());
		providers.put("NW7", new NW7Provider());
		providers.put("PDF417", new PDF417Provider());
		providers.put("PostNet", new PostNetProvider());
		providers.put("RandomWeightUPCA", new RandomWeightUPCAProvider());
		providers.put("SCC14ShippingCode", 
				new SCC14ShippingCodeProvider());
		providers.put("ShipmentIdentificationNumber", 
				new ShipmentIdentificationNumberProvider());
		providers.put("SSCC18", new SSCC18Provider());
		providers.put("Std2of5", new Std2of5Provider());
		providers.put("UCC128", new UCC128Provider());
		providers.put("UPCA", new UPCAProvider());
		providers.put("USD3", new USD3Provider());
		providers.put("USD4", new USD4Provider());
		providers.put("USPS", new USPSProvider());
	}
	
	public static boolean isTypeSupported(String type)
	{
		initProviders();
		
		return providers.containsKey(type);
	}
	
	public static Barcode createBarcode(BarcodeInfo barcodeInfo)
	{
		initProviders();
		
		BarcodeProvider provider = providers.get(
				barcodeInfo.getType());
		if (provider == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_BARCODE_PROVIDER_NOT_FOUND,
					new Object[]{barcodeInfo.getType()});
		}
		try
		{
			return provider.createBarcode(barcodeInfo);
		}
		catch (BarcodeException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_ERROR_CREATING_BARCODE,
					(Object[])null,
					e);
		}
	}

	public static class Barcode2of7Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.create2of7(barcodeInfo.getCode());
		}
	}

	public static class Barcode3of9Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.create3of9(barcodeInfo.getCode(), 
					barcodeInfo.getRequiresChecksum());
		}
	}

	public static class BooklandProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createBookland(barcodeInfo.getCode());
		}
	}

	public static class CodabarProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createCodabar(barcodeInfo.getCode());
		}
	}

	public static class Code128Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createCode128(barcodeInfo.getCode());
		}
	}

	public static class Code128AProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createCode128A(barcodeInfo.getCode());
		}
	}

	public static class Code128BProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createCode128B(barcodeInfo.getCode());
		}
	}

	public static class Code128CProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createCode128C(barcodeInfo.getCode());
		}
	}

	public static class Code39Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createCode39(barcodeInfo.getCode(), 
					barcodeInfo.getRequiresChecksum());
		}
	}

	public static class Code39ExtendedProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return new net.sourceforge.barbecue.linear.code39.Code39Barcode(
					barcodeInfo.getCode(), barcodeInfo.getRequiresChecksum(), true);
		}
	}

	public static class EAN128Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return new UCCEAN128Barcode(UCCEAN128Barcode.EAN128_AI, 
					barcodeInfo.getCode(), barcodeInfo.getRequiresChecksum());
		}
	}

	public static class EAN13Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createEAN13(barcodeInfo.getCode());
		}
	}

	public static class GlobalTradeItemNumberProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createGlobalTradeItemNumber(barcodeInfo.getCode());
		}
	}

	public static class Int2of5Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createInt2of5(
					barcodeInfo.getCode(), barcodeInfo.getRequiresChecksum());
		}
	}

	public static class MonarchProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createMonarch(barcodeInfo.getCode());
		}
	}

	public static class NW7Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createNW7(barcodeInfo.getCode());
		}
	}

	public static class PDF417Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createPDF417(barcodeInfo.getCode());
		}
	}

	public static class PostNetProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createPostNet(barcodeInfo.getCode());
		}
	}

	public static class RandomWeightUPCAProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createRandomWeightUPCA(barcodeInfo.getCode());
		}
	}

	public static class SCC14ShippingCodeProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createSCC14ShippingCode(barcodeInfo.getCode());
		}
	}

	public static class ShipmentIdentificationNumberProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createShipmentIdentificationNumber(barcodeInfo.getCode());
		}
	}

	public static class SSCC18Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return new UCCEAN128Barcode(UCCEAN128Barcode.SSCC_18_AI, 
					barcodeInfo.getCode(),
					barcodeInfo.getRequiresChecksum());
		}
	}

	public static class Std2of5Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createStd2of5(barcodeInfo.getCode(), 
					barcodeInfo.getRequiresChecksum());
		}
	}

	public static class UCC128Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return new UCCEAN128Barcode(barcodeInfo.getApplicationIdentifier(), 
					barcodeInfo.getCode(), barcodeInfo.getRequiresChecksum());
		}
	}

	public static class UPCAProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createUPCA(barcodeInfo.getCode());
		}
	}

	public static class USD3Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createUSD3(barcodeInfo.getCode(),
					barcodeInfo.getRequiresChecksum());
		}
	}

	public static class USD4Provider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createUSD4(barcodeInfo.getCode());
		}
	}

	public static class USPSProvider extends BaseBarcodeProvider
	{
		@Override
		protected Barcode createBaseBarcode(BarcodeInfo barcodeInfo)
				throws BarcodeException
		{
			return BarcodeFactory.createUSPS(barcodeInfo.getCode());
		}
	}
	
	private BarcodeProviders()
	{
	}
}
