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
package net.sf.jasperreports.okapi;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.design.JRVerifier;
import uk.org.okapibarcode.backend.Codabar;
import uk.org.okapibarcode.backend.Code128;
import uk.org.okapibarcode.backend.Code2Of5;
import uk.org.okapibarcode.backend.Code3Of9;
import uk.org.okapibarcode.backend.Code3Of9Extended;
import uk.org.okapibarcode.backend.Ean;
import uk.org.okapibarcode.backend.HumanReadableLocation;
import uk.org.okapibarcode.backend.QrCode;
import uk.org.okapibarcode.backend.Symbol;
import uk.org.okapibarcode.backend.Upc;
import uk.org.okapibarcode.graphics.TextAlignment;


/**
 * The {@link OkapiElementType} of each Okapi component class, and the mapping of
 * the JasperReports enums onto Okapi's.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
final class OkapiElementTypes
{

	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_COMPONENT = "components.okapi.unknown.component";

	private static final Map<Class<? extends OkapiComponent>, OkapiElementType<?>> TYPES;

	static
	{
		Map<Class<? extends OkapiComponent>, OkapiElementType<?>> types = new LinkedHashMap<>();
		register(types, new Code128Type());
		register(types, new EanType());
		register(types, new QrCodeType());
		register(types, new Code39Type());
		register(types, new Code39ExtendedType());
		register(types, new CodabarType());
		register(types, new Code2Of5Type());
		register(types, new Interleaved2Of5Type());
		register(types, new Itf14Type());
		register(types, new UpcType());
		TYPES = Collections.unmodifiableMap(types);
	}

	private static void register(Map<Class<? extends OkapiComponent>, OkapiElementType<?>> types, OkapiElementType<?> type)
	{
		types.put(type.getComponentClass(), type);
	}

	private OkapiElementTypes()
	{
	}

	/**
	 * The component classes that have an element type, in registration order.
	 */
	static Iterable<Class<? extends OkapiComponent>> getComponentClasses()
	{
		return TYPES.keySet();
	}

	@SuppressWarnings("unchecked")
	static <C extends OkapiComponent> OkapiElementType<C> getType(C component)
	{
		OkapiElementType<?> type = TYPES.get(component.getClass());
		if (type == null)
		{
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_UNKNOWN_COMPONENT,
					new Object[]{component.getClass().getName()});
		}
		return (OkapiElementType<C>) type;
	}

	/**
	 * Applies the text settings of the text layer.
	 */
	static void applyText(OkapiTextComponent component, Symbol symbol)
	{
		if (component.getHumanReadableLocation() != null)
		{
			switch (component.getHumanReadableLocation())
			{
				case NONE:
					symbol.setHumanReadableLocation(HumanReadableLocation.NONE);
					break;
				case TOP:
					symbol.setHumanReadableLocation(HumanReadableLocation.TOP);
					break;
				case BOTTOM:
				default:
					symbol.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
					break;
			}
		}
		if (component.getHumanReadableAlignment() != null)
		{
			switch (component.getHumanReadableAlignment())
			{
				case LEFT:
					symbol.setHumanReadableAlignment(TextAlignment.LEFT);
					break;
				case RIGHT:
					symbol.setHumanReadableAlignment(TextAlignment.RIGHT);
					break;
				case JUSTIFY:
					symbol.setHumanReadableAlignment(TextAlignment.JUSTIFY);
					break;
				case CENTER:
				default:
					symbol.setHumanReadableAlignment(TextAlignment.CENTER);
					break;
			}
		}
	}

	static int toInt(Object value)
	{
		return ((Number) value).intValue();
	}

	static class Code128Type extends OkapiElementType<Code128Component>
	{
		Code128Type()
		{
			super(Code128Component.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		@Override
		public Symbol createSymbol(Code128Component component)
		{
			Code128 symbol = new Code128();
			if (component.getCodeSet() != null)
			{
				switch (component.getCodeSet())
				{
					case A:
						symbol.setCodeSet(Code128.CodeSet.A);
						break;
					case B:
						symbol.setCodeSet(Code128.CodeSet.B);
						break;
					case C:
						symbol.setCodeSet(Code128.CodeSet.C);
						break;
					case AB:
						symbol.setCodeSet(Code128.CodeSet.AB);
						break;
					case ABC:
					default:
						symbol.setCodeSet(Code128.CodeSet.ABC);
						break;
				}
			}
			if (component.getDataType() != null)
			{
				switch (component.getDataType())
				{
					case GS1:
						symbol.setDataType(Symbol.DataType.GS1);
						break;
					case HIBC:
					default:
						symbol.setDataType(Symbol.DataType.HIBC);
						break;
				}
			}
			if (component.getReaderInit() != null)
			{
				symbol.setReaderInit(component.getReaderInit());
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public int getDefaultQuietZoneHorizontal(Code128Component component)
		{
			// ISO/IEC 15417
			return 10;
		}

		@Override
		public String getDefaultPreviewCode(Code128Component component)
		{
			return "0123456789";
		}
	}

	static class EanType extends OkapiElementType<EANComponent>
	{
		EanType()
		{
			super(EANComponent.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		private static boolean isEan8(EANComponent component)
		{
			return component.getMode() == EANModeEnum.EAN8;
		}

		@Override
		public Symbol createSymbol(EANComponent component)
		{
			Ean symbol = new Ean();
			symbol.setMode(isEan8(component) ? Ean.Mode.EAN8 : Ean.Mode.EAN13);
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void applySizes(EANComponent component, Symbol symbol, double modulePt, double unitsToPoints)
		{
			Double extraHeight = component.getGuardPatternExtraHeight();
			if (extraHeight != null)
			{
				((Ean) symbol).setGuardPatternExtraHeight(toModules(extraHeight, modulePt, unitsToPoints));
			}
		}

		@Override
		public void verify(EANComponent component, JRVerifier verifier)
		{
			OkapiCompiler.verifyNotNegative(verifier, component, EANComponent.PROPERTY_GUARD_PATTERN_EXTRA_HEIGHT,
					component.getGuardPatternExtraHeight());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(EANComponent component)
		{
			// GS1 General Specifications: 11 modules left of EAN-13, 7 for EAN-8;
			// Okapi uses one value for both sides and draws the leading EAN-13 digit in it
			return isEan8(component) ? 7 : 11;
		}

		@Override
		public String getDefaultPreviewCode(EANComponent component)
		{
			// the check digit is added
			return isEan8(component) ? "1234567" : "590123412345";
		}
	}

	static class QrCodeType extends OkapiElementType<QRCodeComponent>
	{
		QrCodeType()
		{
			super(QRCodeComponent.class, Layout.MATRIX);
			addExpression(QRCodeComponent.PROPERTY_STRUCTURED_APPEND_POSITION_EXPRESSION,
					QRCodeComponent::getStructuredAppendPositionExpression,
					QRCodeComponent::setStructuredAppendPositionExpression, Integer.class);
			addExpression(QRCodeComponent.PROPERTY_STRUCTURED_APPEND_TOTAL_EXPRESSION,
					QRCodeComponent::getStructuredAppendTotalExpression,
					QRCodeComponent::setStructuredAppendTotalExpression, Integer.class);
			addExpression(QRCodeComponent.PROPERTY_STRUCTURED_APPEND_PARITY_EXPRESSION,
					QRCodeComponent::getStructuredAppendParityExpression,
					QRCodeComponent::setStructuredAppendParityExpression, Integer.class);
		}

		@Override
		public boolean hasText()
		{
			return false;
		}

		@Override
		public Symbol createSymbol(QRCodeComponent component)
		{
			QrCode symbol = new QrCode();
			if (component.getPreferredVersion() != null)
			{
				symbol.setPreferredVersion(component.getPreferredVersion());
			}
			if (component.getPreferredEccLevel() != null)
			{
				switch (component.getPreferredEccLevel())
				{
					case L:
						symbol.setPreferredEccLevel(QrCode.EccLevel.L);
						break;
					case M:
						symbol.setPreferredEccLevel(QrCode.EccLevel.M);
						break;
					case Q:
						symbol.setPreferredEccLevel(QrCode.EccLevel.Q);
						break;
					case H:
					default:
						symbol.setPreferredEccLevel(QrCode.EccLevel.H);
						break;
				}
			}
			if (component.getForceByteCompaction() != null)
			{
				symbol.setForceByteCompaction(component.getForceByteCompaction());
			}
			if (component.getDataType() != null)
			{
				switch (component.getDataType())
				{
					case GS1:
						symbol.setDataType(Symbol.DataType.GS1);
						break;
					case HIBC:
					default:
						symbol.setDataType(Symbol.DataType.HIBC);
						break;
				}
			}
			if (component.getEciMode() != null)
			{
				symbol.setEciMode(component.getEciMode());
			}
			if (component.getStructuredAppendForced() != null)
			{
				symbol.setStructuredAppendForced(component.getStructuredAppendForced());
			}
			return symbol;
		}

		@Override
		public void applyValues(QRCodeComponent component, Symbol symbol, Object[] values)
		{
			QrCode qrCode = (QrCode) symbol;
			if (values[1] != null)
			{
				qrCode.setStructuredAppendPosition(toInt(values[1]));
			}
			if (values[2] != null)
			{
				qrCode.setStructuredAppendTotal(toInt(values[2]));
			}
			if (values[3] != null)
			{
				qrCode.setStructuredAppendParity(toInt(values[3]));
			}
		}

		@Override
		public void verify(QRCodeComponent component, JRVerifier verifier)
		{
			if ((component.getStructuredAppendPositionExpression() == null)
					!= (component.getStructuredAppendTotalExpression() == null))
			{
				verifier.addBrokenRule("Barcode structured append needs both "
						+ QRCodeComponent.PROPERTY_STRUCTURED_APPEND_POSITION_EXPRESSION + " and "
						+ QRCodeComponent.PROPERTY_STRUCTURED_APPEND_TOTAL_EXPRESSION, component);
			}
		}

		@Override
		public int getDefaultQuietZoneHorizontal(QRCodeComponent component)
		{
			// ISO/IEC 18004
			return 4;
		}

		@Override
		public int getDefaultQuietZoneVertical(QRCodeComponent component)
		{
			return 4;
		}

		@Override
		public String getDefaultPreviewCode(QRCodeComponent component)
		{
			return "https://jasperreports.sourceforge.net";
		}
	}

	/**
	 * A size in report units in whole modules, rounded to the nearest.
	 */
	static int toModules(double units, double modulePt, double unitsToPoints)
	{
		return (int) Math.round(units * unitsToPoints / modulePt);
	}

	static class Code39Type extends OkapiElementType<Code39Component>
	{
		Code39Type()
		{
			super(Code39Component.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		@Override
		public Symbol createSymbol(Code39Component component)
		{
			Code3Of9 symbol = new Code3Of9();
			if (component.getModuleWidthRatio() != null)
			{
				symbol.setModuleWidthRatio(component.getModuleWidthRatio());
			}
			if (component.getCheckDigit() != null)
			{
				switch (component.getCheckDigit())
				{
					case MOD43:
						symbol.setCheckDigit(Code3Of9.CheckDigit.MOD43);
						break;
					case NONE:
					default:
						symbol.setCheckDigit(Code3Of9.CheckDigit.NONE);
						break;
				}
			}
			if (component.getDataType() == Code39DataTypeEnum.HIBC)
			{
				symbol.setDataType(Symbol.DataType.HIBC);
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void verify(Code39Component component, JRVerifier verifier)
		{
			OkapiCompiler.verifyPositive(verifier, component, Code39Component.PROPERTY_MODULE_WIDTH_RATIO, component.getModuleWidthRatio());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(Code39Component component)
		{
			// ISO/IEC 16388
			return 10;
		}

		@Override
		public String getDefaultPreviewCode(Code39Component component)
		{
			return "OKAPI-39";
		}
	}

	static class Code39ExtendedType extends OkapiElementType<Code39ExtendedComponent>
	{
		Code39ExtendedType()
		{
			super(Code39ExtendedComponent.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		@Override
		public Symbol createSymbol(Code39ExtendedComponent component)
		{
			Code3Of9Extended symbol = new Code3Of9Extended();
			if (component.getModuleWidthRatio() != null)
			{
				symbol.setModuleWidthRatio(component.getModuleWidthRatio());
			}
			if (component.getCheckDigit() != null)
			{
				switch (component.getCheckDigit())
				{
					case MOD43:
						symbol.setCheckDigit(Code3Of9Extended.CheckDigit.MOD43);
						break;
					case NONE:
					default:
						symbol.setCheckDigit(Code3Of9Extended.CheckDigit.NONE);
						break;
				}
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void verify(Code39ExtendedComponent component, JRVerifier verifier)
		{
			OkapiCompiler.verifyPositive(verifier, component, Code39ExtendedComponent.PROPERTY_MODULE_WIDTH_RATIO, component.getModuleWidthRatio());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(Code39ExtendedComponent component)
		{
			// ISO/IEC 16388
			return 10;
		}

		@Override
		public String getDefaultPreviewCode(Code39ExtendedComponent component)
		{
			return "Okapi 39";
		}
	}

	static class CodabarType extends OkapiElementType<CodabarComponent>
	{
		CodabarType()
		{
			super(CodabarComponent.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		@Override
		public Symbol createSymbol(CodabarComponent component)
		{
			Codabar symbol = new Codabar();
			if (component.getModuleWidthRatio() != null)
			{
				symbol.setModuleWidthRatio(component.getModuleWidthRatio());
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void verify(CodabarComponent component, JRVerifier verifier)
		{
			OkapiCompiler.verifyPositive(verifier, component, CodabarComponent.PROPERTY_MODULE_WIDTH_RATIO, component.getModuleWidthRatio());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(CodabarComponent component)
		{
			// ANSI/AIM BC3
			return 10;
		}

		@Override
		public String getDefaultPreviewCode(CodabarComponent component)
		{
			return "A0123456789B";
		}
	}

	static class Code2Of5Type extends OkapiElementType<Code2Of5Component>
	{
		Code2Of5Type()
		{
			super(Code2Of5Component.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		private static Code2Of5ModeEnum getMode(Code2Of5Component component)
		{
			return component.getMode() == null ? Code2Of5ModeEnum.MATRIX : component.getMode();
		}

		@Override
		public Symbol createSymbol(Code2Of5Component component)
		{
			Code2Of5 symbol = new Code2Of5();
			switch (getMode(component))
			{
				case INDUSTRIAL:
					symbol.setMode(Code2Of5.ToFMode.INDUSTRIAL);
					break;
				case IATA:
					symbol.setMode(Code2Of5.ToFMode.IATA);
					break;
				case DATA_LOGIC:
					symbol.setMode(Code2Of5.ToFMode.DATA_LOGIC);
					break;
				case DP_LEITCODE:
					symbol.setMode(Code2Of5.ToFMode.DP_LEITCODE);
					break;
				case DP_IDENTCODE:
					symbol.setMode(Code2Of5.ToFMode.DP_IDENTCODE);
					break;
				case MATRIX:
				default:
					symbol.setMode(Code2Of5.ToFMode.MATRIX);
					break;
			}
			if (component.getModuleWidthRatio() != null)
			{
				symbol.setModuleWidthRatio(component.getModuleWidthRatio());
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void verify(Code2Of5Component component, JRVerifier verifier)
		{
			OkapiCompiler.verifyPositive(verifier, component, Code2Of5Component.PROPERTY_MODULE_WIDTH_RATIO, component.getModuleWidthRatio());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(Code2Of5Component component)
		{
			// as for Interleaved 2 of 5, ISO/IEC 16390
			return 10;
		}

		@Override
		public String getDefaultPreviewCode(Code2Of5Component component)
		{
			switch (getMode(component))
			{
				case DP_LEITCODE:
					// 13 digits, the check digit is added
					return "2163100011102";
				case DP_IDENTCODE:
					// 11 digits, the check digit is added
					return "56310243031";
				default:
					return "0123456789";
			}
		}
	}

	static class Interleaved2Of5Type extends OkapiElementType<Interleaved2Of5Component>
	{
		Interleaved2Of5Type()
		{
			super(Interleaved2Of5Component.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		@Override
		public Symbol createSymbol(Interleaved2Of5Component component)
		{
			Code2Of5 symbol = new Code2Of5(component.getCheckDigit() == Interleaved2Of5CheckDigitEnum.MOD10
					? Code2Of5.ToFMode.INTERLEAVED_WITH_CHECK_DIGIT : Code2Of5.ToFMode.INTERLEAVED);
			if (component.getModuleWidthRatio() != null)
			{
				symbol.setModuleWidthRatio(component.getModuleWidthRatio());
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void verify(Interleaved2Of5Component component, JRVerifier verifier)
		{
			OkapiCompiler.verifyPositive(verifier, component, Interleaved2Of5Component.PROPERTY_MODULE_WIDTH_RATIO, component.getModuleWidthRatio());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(Interleaved2Of5Component component)
		{
			// ISO/IEC 16390
			return 10;
		}

		@Override
		public String getDefaultPreviewCode(Interleaved2Of5Component component)
		{
			return "0123456789";
		}
	}

	static class Itf14Type extends OkapiElementType<ITF14Component>
	{
		Itf14Type()
		{
			super(ITF14Component.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		@Override
		public Symbol createSymbol(ITF14Component component)
		{
			Code2Of5 symbol = new Code2Of5(Code2Of5.ToFMode.ITF14);
			if (component.getModuleWidthRatio() != null)
			{
				symbol.setModuleWidthRatio(component.getModuleWidthRatio());
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void verify(ITF14Component component, JRVerifier verifier)
		{
			OkapiCompiler.verifyPositive(verifier, component, ITF14Component.PROPERTY_MODULE_WIDTH_RATIO, component.getModuleWidthRatio());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(ITF14Component component)
		{
			// GS1 General Specifications: at least 10 times the narrow bar
			return 10;
		}

		@Override
		public String getDefaultPreviewCode(ITF14Component component)
		{
			// the check digit is added
			return "1540014128876";
		}
	}

	static class UpcType extends OkapiElementType<UPCComponent>
	{
		UpcType()
		{
			super(UPCComponent.class, Layout.LINEAR);
		}

		@Override
		public boolean hasText()
		{
			return true;
		}

		private static boolean isUpcE(UPCComponent component)
		{
			return component.getMode() == UPCModeEnum.UPCE;
		}

		@Override
		public Symbol createSymbol(UPCComponent component)
		{
			Upc symbol = new Upc();
			symbol.setMode(isUpcE(component) ? Upc.Mode.UPCE : Upc.Mode.UPCA);
			if (component.getShowCheckDigit() != null)
			{
				symbol.setShowCheckDigit(component.getShowCheckDigit());
			}
			applyText(component, symbol);
			return symbol;
		}

		@Override
		public void applySizes(UPCComponent component, Symbol symbol, double modulePt, double unitsToPoints)
		{
			Double extraHeight = component.getGuardPatternExtraHeight();
			if (extraHeight != null)
			{
				((Upc) symbol).setGuardPatternExtraHeight(toModules(extraHeight, modulePt, unitsToPoints));
			}
		}

		@Override
		public void verify(UPCComponent component, JRVerifier verifier)
		{
			OkapiCompiler.verifyNotNegative(verifier, component, UPCComponent.PROPERTY_GUARD_PATTERN_EXTRA_HEIGHT,
					component.getGuardPatternExtraHeight());
		}

		@Override
		public int getDefaultQuietZoneHorizontal(UPCComponent component)
		{
			// GS1 General Specifications: 9 modules on each side of UPC-A, and left of UPC-E;
			// Okapi draws the outer digits in them
			return 9;
		}

		@Override
		public String getDefaultPreviewCode(UPCComponent component)
		{
			// the check digit is added
			return isUpcE(component) ? "0123456" : "03600029145";
		}
	}
}
