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

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.krysalis.barcode4j.tools.UnitConv;

import net.sf.jasperreports.barcode4j.Barcode4jComponent;
import net.sf.jasperreports.barcode4j.BarcodeComponent;
import net.sf.jasperreports.barcode4j.CodabarComponent;
import net.sf.jasperreports.barcode4j.EAN128Component;
import net.sf.jasperreports.barcode4j.EAN13Component;
import net.sf.jasperreports.barcode4j.EAN8Component;
import net.sf.jasperreports.barcode4j.ErrorCorrectionLevelEnum;
import net.sf.jasperreports.barcode4j.Interleaved2Of5Component;
import net.sf.jasperreports.barcode4j.OrientationEnum;
import net.sf.jasperreports.barcode4j.QRCodeSVGImageProducer;
import net.sf.jasperreports.barcode4j.TextPositionEnum;
import net.sf.jasperreports.barcode4j.UPCAComponent;
import net.sf.jasperreports.barcode4j.UPCEComponent;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.okapi.Code128Component;
import net.sf.jasperreports.okapi.Code39CheckDigitEnum;
import net.sf.jasperreports.okapi.Code39ExtendedComponent;
import net.sf.jasperreports.okapi.EANComponent;
import net.sf.jasperreports.okapi.EANModeEnum;
import net.sf.jasperreports.okapi.HumanReadableLocationEnum;
import net.sf.jasperreports.okapi.Interleaved2Of5CheckDigitEnum;
import net.sf.jasperreports.okapi.OkapiComponent;
import net.sf.jasperreports.okapi.OkapiTextComponent;
import net.sf.jasperreports.okapi.QRCodeComponent;
import net.sf.jasperreports.okapi.QRCodeEccLevelEnum;
import net.sf.jasperreports.okapi.UPCComponent;
import net.sf.jasperreports.okapi.UPCModeEnum;


/**
 * Report updater that converts the <code>barcode4j:*</code> elements of a report to <code>okapi:*</code>
 * elements, for use with the <code>update</code> goal of the JasperReports Maven plugin or the
 * <code>update</code> Ant task. The barcode4j extension and its libraries have to be on the classpath.
 * <p>
 * Code 128, Interleaved 2 of 5, QR Code, Code 39 with the extended character set and a checksum mode
 * other than <code>check</code>, and EAN-13, EAN-8, UPC-A and UPC-E barcodes whose checksum mode is
 * <code>add</code> are converted. Barcodes whose conversion could encode different data are left
 * unconverted, so that their data can be reviewed first: EAN-13, EAN-8, UPC-A and UPC-E with any other
 * checksum mode, since their data may then carry a check digit, EAN-128, Code 39 with the extended
 * character set and the checksum mode <code>check</code>, and QR Code with a character encoding other
 * than UTF-8 set on the element or the report. So are barcodes whose data Okapi may reject: Codabar,
 * and Code 39 without the extended character set. Other types are left unconverted until they have an
 * <code>okapi:*</code> element. Each of these, and each setting that has no <code>okapi:*</code>
 * equivalent, is logged as a warning.
 * <p>
 * A linear barcode with no module width gets the default module width of the barcode4j library for
 * its type, and one with no wide factor the default wide factor, so that it keeps its printed length.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class Barcode4jConverter extends LegacyConverter
{

	private static final String REASON_DATA = "converting it could encode different data; review the data first";
	private static final String REASON_REJECTED = "Okapi rejects data the legacy barcode accepts; review the data first";
	private static final String REASON_NO_ELEMENT = "there is no okapi:* element for this type yet";
	private static final String REASON_TEXT = "Okapi draws the human-readable text";

	private static final String CHECKSUM_MODE_ADD = "add";
	private static final String CHECKSUM_MODE_CHECK = "check";


	@Override
	protected String getExtensionName()
	{
		return "barcode4j";
	}


	@Override
	protected boolean isLegacy(Component component)
	{
		return component instanceof BarcodeComponent;
	}


	@Override
	protected OkapiComponent convert(Component component, ElementConversion conversion)
	{
		if (component instanceof net.sf.jasperreports.barcode4j.Code128Component)
		{
			return text(new Code128Component(), (Barcode4jComponent) component, new Code128Bean(), conversion);
		}
		if (component instanceof EAN13Component)
		{
			return ean(EANModeEnum.EAN13, ((EAN13Component) component).getChecksumMode(),
				(Barcode4jComponent) component, new EAN13Bean(), conversion);
		}
		if (component instanceof EAN8Component)
		{
			return ean(EANModeEnum.EAN8, ((EAN8Component) component).getChecksumMode(),
				(Barcode4jComponent) component, new EAN8Bean(), conversion);
		}
		if (component instanceof UPCAComponent)
		{
			return upc(UPCModeEnum.UPCA, ((UPCAComponent) component).getChecksumMode(),
				(Barcode4jComponent) component, new UPCABean(), conversion);
		}
		if (component instanceof UPCEComponent)
		{
			return upc(UPCModeEnum.UPCE, ((UPCEComponent) component).getChecksumMode(),
				(Barcode4jComponent) component, new UPCEBean(), conversion);
		}
		if (component instanceof net.sf.jasperreports.barcode4j.Code39Component)
		{
			return code39((net.sf.jasperreports.barcode4j.Code39Component) component, conversion);
		}
		if (component instanceof Interleaved2Of5Component)
		{
			return interleaved2Of5((Interleaved2Of5Component) component, conversion);
		}
		if (component instanceof CodabarComponent)
		{
			// Okapi takes only A, B, C and D in upper case as start/stop characters, while barcode4j also takes
			// them in lower case, the alternate names T, N, * and E, and data with no start/stop characters
			return conversion.notConverted(REASON_REJECTED);
		}
		if (component instanceof EAN128Component)
		{
			// the message has its application identifiers without the brackets that Okapi's GS1 data
			// needs, barcode4j adds the check digits of some of them, and a template splits the message
			// into application identifiers: each of these would mean rewriting the data
			return conversion.notConverted(REASON_DATA);
		}
		if (component instanceof net.sf.jasperreports.barcode4j.QRCodeComponent)
		{
			return qrCode((net.sf.jasperreports.barcode4j.QRCodeComponent) component, conversion);
		}
		return conversion.notConverted(REASON_NO_ELEMENT);
	}


	private static OkapiComponent ean(EANModeEnum mode, String checksumMode, Barcode4jComponent barcode4j,
			AbstractBarcodeBean bean, ElementConversion conversion)
	{
		if (!CHECKSUM_MODE_ADD.equals(checksumMode))
		{
			return checksumNotAdded(checksumMode, conversion);
		}

		EANComponent ean = text(new EANComponent(), barcode4j, bean, conversion);
		ean.setMode(mode);
		return ean;
	}


	private static OkapiComponent upc(UPCModeEnum mode, String checksumMode, Barcode4jComponent barcode4j,
			AbstractBarcodeBean bean, ElementConversion conversion)
	{
		// as for EAN
		if (!CHECKSUM_MODE_ADD.equals(checksumMode))
		{
			return checksumNotAdded(checksumMode, conversion);
		}

		UPCComponent upc = text(new UPCComponent(), barcode4j, bean, conversion);
		upc.setMode(mode);
		return upc;
	}


	/**
	 * Leaves an EAN or UPC barcode unconverted: only with the checksum mode "add" is the data known to have
	 * no check digit, which Okapi always computes and rejects in the input; with "check", "ignore" or "auto"
	 * (the default), the data may carry one.
	 */
	private static OkapiComponent checksumNotAdded(String checksumMode, ElementConversion conversion)
	{
		return conversion.notConverted("checksum mode " + (checksumMode == null ? "auto (default)" : checksumMode)
			+ ": " + REASON_DATA);
	}


	private static OkapiComponent code39(net.sf.jasperreports.barcode4j.Code39Component legacy,
			ElementConversion conversion)
	{
		if (!Boolean.TRUE.equals(legacy.isExtendedCharSetEnabled()))
		{
			// barcode4j removes a * start/stop pair around the data, which Okapi rejects
			return conversion.notConverted("standard character set: " + REASON_REJECTED);
		}
		String checksumMode = legacy.getChecksumMode();
		if (CHECKSUM_MODE_CHECK.equals(checksumMode))
		{
			// the data ends with a check character that barcode4j computes over its own escapes of - and .
			// (/M and /N), while Okapi encodes them as they are
			return conversion.notConverted("checksum mode check: " + REASON_DATA);
		}

		Code39Bean bean = new Code39Bean();
		Code39ExtendedComponent code39 = text(new Code39ExtendedComponent(), legacy, bean, conversion);
		code39.setModuleWidthRatio(legacy.getWideFactor() != null ? legacy.getWideFactor() : bean.getWideFactor());
		// with "ignore" or "auto" (the default), barcode4j encodes the data as it is
		if (CHECKSUM_MODE_ADD.equals(checksumMode))
		{
			code39.setCheckDigit(Code39CheckDigitEnum.MOD43);
		}
		droppedIfSet(conversion, "displayChecksum", legacy.isDisplayChecksum(), REASON_TEXT);
		droppedIfSet(conversion, "displayStartStop", legacy.isDisplayStartStop(), REASON_TEXT);
		droppedIfSet(conversion, "intercharGapWidth", legacy.getIntercharGapWidth(),
			"Okapi draws the gap between characters as wide as a narrow bar");
		return code39;
	}


	private static OkapiComponent interleaved2Of5(Interleaved2Of5Component legacy, ElementConversion conversion)
	{
		Interleaved2Of5Bean bean = new Interleaved2Of5Bean();
		net.sf.jasperreports.okapi.Interleaved2Of5Component itf =
			text(new net.sf.jasperreports.okapi.Interleaved2Of5Component(), legacy, bean, conversion);
		itf.setModuleWidthRatio(legacy.getWideFactor() != null ? legacy.getWideFactor() : bean.getWideFactor());
		// barcode4j's check digit and its leading zero for an odd number of digits are Okapi's; with "check",
		// "ignore" or "auto" (the default), barcode4j encodes the data as it is
		if (CHECKSUM_MODE_ADD.equals(legacy.getChecksumMode()))
		{
			itf.setCheckDigit(Interleaved2Of5CheckDigitEnum.MOD10);
		}
		droppedIfSet(conversion, "displayChecksum", legacy.isDisplayChecksum(), REASON_TEXT);
		return itf;
	}


	private static void droppedIfSet(ElementConversion conversion, String setting, Object value, String reason)
	{
		if (value != null)
		{
			conversion.dropped(setting, reason);
		}
	}


	/**
	 * @param bean a barcode4j bean of the legacy type, for its default module width
	 */
	private static <C extends OkapiTextComponent> C text(C component, Barcode4jComponent barcode4j,
			AbstractBarcodeBean bean, ElementConversion conversion)
	{
		common(component, barcode4j.getCodeExpression(), barcode4j.getEvaluationTime(), barcode4j.getEvaluationGroup());
		// the own orientation only: when not set, both components take the rotation of the style
		component.setRotation(rotation(barcode4j.getOrientation()));
		component.setHumanReadableLocation(location(barcode4j.getTextPosition()));
		// both in report units
		Double moduleWidth = barcode4j.getModuleWidth();
		// with no module width, barcode4j draws at the default of its library, while okapi:* would fill
		// the element width; carrying the default over keeps the printed length of the barcode
		component.setModuleWidth(moduleWidth != null ? moduleWidth : defaultModuleWidth(bean, conversion));
		component.setQuietZoneHorizontal(barcode4j.getQuietZone());
		component.setQuietZoneVertical(barcode4j.getVerticalQuietZone());
		if (barcode4j.getPatternExpression() != null)
		{
			conversion.dropped("patternExpression", REASON_TEXT);
		}
		return component;
	}


	/**
	 * The default module width of the barcode4j bean, in millimetres, converted to the units of the
	 * report, as the legacy component converts the other way.
	 */
	private static double defaultModuleWidth(AbstractBarcodeBean bean, ElementConversion conversion)
	{
		double points = UnitConv.mm2pt(bean.getModuleWidth());
		int dpi = conversion.getReport().getDpi();
		return dpi == JasperPrint.DEFAULT_REPORT_DPI ? points : points * dpi / JasperPrint.DEFAULT_REPORT_DPI;
	}


	private static OkapiComponent qrCode(net.sf.jasperreports.barcode4j.QRCodeComponent legacy,
			ElementConversion conversion)
	{
		String encoding = getEncoding(conversion);
		if (encoding != null
			&& !net.sf.jasperreports.barcode4j.QRCodeComponent.PROPERTY_DEFAULT_ENCODING.equalsIgnoreCase(encoding))
		{
			return conversion.notConverted("character encoding " + encoding + ": " + REASON_DATA);
		}

		QRCodeComponent qrCode = common(new QRCodeComponent(), legacy.getCodeExpression(),
			legacy.getEvaluationTime(), legacy.getEvaluationGroup());
		qrCode.setPreferredEccLevel(eccLevel(legacy.getErrorCorrectionLevel()));
		qrCode.setPreferredVersion(legacy.getQrVersion());
		if (legacy.getMargin() != null && legacy.getMargin() != QRCodeSVGImageProducer.DEFAULT_MARGIN)
		{
			// the legacy default margin of 4 modules is also the okapi:* default quiet zone; any other
			// legacy margin is in modules, while okapi:* quiet zones are in report units
			conversion.dropped("margin", "okapi:* quiet zones are in report units, not modules");
		}
		return qrCode;
	}


	/**
	 * The QR Code character encoding set on the element or on the report; one set in the
	 * JasperReports context is only known at fill time.
	 */
	private static String getEncoding(ElementConversion conversion)
	{
		String property = net.sf.jasperreports.barcode4j.QRCodeComponent.PROPERTY_QRCODE_CHARACTER_ENCODING;
		String encoding = getProperty(conversion.getElement().getPropertiesMap(), property);
		return encoding != null ? encoding : getProperty(conversion.getReport().getPropertiesMap(), property);
	}


	private static String getProperty(JRPropertiesMap properties, String name)
	{
		return properties == null ? null : properties.getProperty(name);
	}


	private static QRCodeEccLevelEnum eccLevel(ErrorCorrectionLevelEnum level)
	{
		if (level == null)
		{
			// the legacy default
			return QRCodeEccLevelEnum.L;
		}
		switch (level)
		{
			case M:
				return QRCodeEccLevelEnum.M;
			case Q:
				return QRCodeEccLevelEnum.Q;
			case H:
				return QRCodeEccLevelEnum.H;
			case L:
			default:
				return QRCodeEccLevelEnum.L;
		}
	}


	private static RotationEnum rotation(OrientationEnum orientation)
	{
		if (orientation == null)
		{
			return null;
		}
		switch (orientation)
		{
			case LEFT:
				return RotationEnum.LEFT;
			case DOWN:
				return RotationEnum.UPSIDE_DOWN;
			case RIGHT:
				return RotationEnum.RIGHT;
			case UP:
			default:
				return RotationEnum.NONE;
		}
	}


	private static HumanReadableLocationEnum location(TextPositionEnum position)
	{
		if (position == null)
		{
			return null;
		}
		switch (position)
		{
			case NONE:
				return HumanReadableLocationEnum.NONE;
			case TOP:
				return HumanReadableLocationEnum.TOP;
			case BOTTOM:
			default:
				return HumanReadableLocationEnum.BOTTOM;
		}
	}
}
