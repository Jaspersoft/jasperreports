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
package net.sf.jasperreports.okapi.convert;

import net.sf.jasperreports.barbecue.BarbecueComponent;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.okapi.Code128CodeSetEnum;
import net.sf.jasperreports.okapi.Code128Component;
import net.sf.jasperreports.okapi.Code39CheckDigitEnum;
import net.sf.jasperreports.okapi.Code39Component;
import net.sf.jasperreports.okapi.EANComponent;
import net.sf.jasperreports.okapi.EANModeEnum;
import net.sf.jasperreports.okapi.HumanReadableLocationEnum;
import net.sf.jasperreports.okapi.OkapiComponent;
import net.sf.jasperreports.okapi.OkapiTextComponent;
import net.sf.jasperreports.okapi.PDF417Component;
import net.sf.jasperreports.okapi.POSTNETComponent;
import net.sf.jasperreports.okapi.UPCComponent;
import net.sf.jasperreports.okapi.UPCModeEnum;


/**
 * Report updater that converts the <code>barbecue</code> elements of a report to <code>okapi:*</code>
 * elements, for use with the <code>update</code> goal of the JasperReports Maven plugin or the
 * <code>update</code> Ant task. The barbecue extension has to be on the classpath.
 * <p>
 * Code 128 (with any of its code sets), Code 39 (3of9, Code39, USD3), EAN-13, UPC-A, PostNet and PDF417
 * barcodes are converted. Barcodes whose conversion could encode different data are left unconverted, so that their
 * data can be reviewed first: the GS1 types (EAN128, UCC128, GlobalTradeItemNumber, SCC14ShippingCode,
 * SSCC18, ShipmentIdentificationNumber, USPS), Bookland, RandomWeightUPCA, the Codabar types (2of7,
 * Codabar, Monarch, NW7, USD4), Code39 (Extended), and Int2of5 and Std2of5. Other types are left
 * unconverted until they have an <code>okapi:*</code> element. Each of these, and each setting that
 * has no <code>okapi:*</code> equivalent, is logged as a warning.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class BarbecueConverter extends LegacyConverter
{

	private static final String REASON_DATA = "converting it could encode different data; review the data first";
	private static final String REASON_REJECTED = "Okapi rejects data the legacy barcode accepts; review the data first";
	private static final String REASON_NO_ELEMENT = "there is no okapi:* element for this type yet";


	@Override
	protected String getExtensionName()
	{
		return "barbecue";
	}


	@Override
	protected boolean isLegacy(Component component)
	{
		return component instanceof BarbecueComponent;
	}


	@Override
	protected OkapiComponent convert(Component component, ElementConversion conversion)
	{
		BarbecueComponent barbecue = (BarbecueComponent) component;
		String type = barbecue.getType();
		if (type == null)
		{
			return conversion.notConverted("it has no barcode type");
		}

		switch (type)
		{
			case "Code128":
				return text(new Code128Component(), barbecue, conversion);
			case "Code128A":
			case "Code128B":
			case "Code128C":
			{
				Code128Component code128 = text(new Code128Component(), barbecue, conversion);
				code128.setCodeSet(Code128CodeSetEnum.valueOf(type.substring("Code128".length())));
				return code128;
			}
			case "EAN13":
			{
				EANComponent ean = text(new EANComponent(), barbecue, conversion);
				ean.setMode(EANModeEnum.EAN13);
				return ean;
			}
			case "UPCA":
			{
				// barbecue takes the 11 digits without the check digit, and adds the standard one, as Okapi does
				UPCComponent upc = text(new UPCComponent(), barbecue, conversion);
				upc.setMode(UPCModeEnum.UPCA);
				return upc;
			}
			case "PostNet":
				// barbecue adds the standard check digit, as Okapi does
				return text(new POSTNETComponent(), barbecue, conversion);
			case "PDF417":
			{
				PDF417Component pdf417 = common(new PDF417Component(), barbecue.getCodeExpression(),
					barbecue.getEvaluationTime(), barbecue.getEvaluationGroup());
				pdf417.setRotation(barbecue.getOwnRotation());
				// barbecue's PDF417 always has 12 data columns and error correction level 0, and a fixed
				// size: its drawText, barWidth and barHeight have no effect, so nothing is dropped
				pdf417.setDataColumns(12);
				pdf417.setPreferredEccLevel(0);
				return pdf417;
			}
			case "3of9":
			case "Code39":
			case "USD3":
			{
				// barbecue's character set and mod 43 check character are the standard ones, as Okapi's
				Code39Component code39 = text(new Code39Component(), barbecue, conversion);
				if (barbecue.isChecksumRequired())
				{
					code39.setCheckDigit(Code39CheckDigitEnum.MOD43);
				}
				return code39;
			}
			// barbecue's own application identifiers and check digits, which are not the standard GS1 ones
			case "EAN128":
			case "UCC128":
			case "GlobalTradeItemNumber":
			case "SCC14ShippingCode":
			case "SSCC18":
			case "ShipmentIdentificationNumber":
			case "USPS":
			// ISBN to EAN-13, and the price check digit, computed by barbecue from the data
			case "Bookland":
			case "RandomWeightUPCA":
			// barbecue swaps the B and C start/stop patterns, and the data may use them
			case "2of7":
			case "Codabar":
			case "Monarch":
			case "NW7":
			case "USD4":
			// barbecue encodes '<' as the four characters of "null"
			case "Code39 (Extended)":
				return conversion.notConverted("type " + type + ": " + REASON_DATA);
			case "Int2of5":
			case "Std2of5":
				// barbecue's Int2of5 check digit uses non-standard weights, and Okapi has none for Std2of5
				return barbecue.isChecksumRequired()
					? conversion.notConverted("type " + type + " with a required checksum: " + REASON_DATA)
					// barbecue removes whitespace from the data, which Okapi rejects
					: conversion.notConverted("type " + type + ": " + REASON_REJECTED);
			default:
				return conversion.notConverted("type " + type + ": " + REASON_NO_ELEMENT);
		}
	}


	private static <C extends OkapiTextComponent> C text(C component, BarbecueComponent barbecue,
			ElementConversion conversion)
	{
		common(component, barbecue.getCodeExpression(), barbecue.getEvaluationTime(), barbecue.getEvaluationGroup());
		// the own rotation only: when not set, both components take the rotation of the style
		component.setRotation(barbecue.getOwnRotation());
		component.setHumanReadableLocation(barbecue.isDrawText()
			? HumanReadableLocationEnum.BOTTOM
			: HumanReadableLocationEnum.NONE);
		if (barbecue.getBarWidth() != null)
		{
			component.setModuleWidth(barbecue.getBarWidth().doubleValue());
		}
		if (barbecue.getBarHeight() != null)
		{
			conversion.dropped("barHeight", "okapi:* linear barcodes take their height from the element");
		}
		return component;
	}
}
