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
import uk.org.okapibarcode.backend.Code128;
import uk.org.okapibarcode.backend.Ean;
import uk.org.okapibarcode.backend.HumanReadableLocation;
import uk.org.okapibarcode.backend.QrCode;
import uk.org.okapibarcode.backend.Symbol;
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
				((Ean) symbol).setGuardPatternExtraHeight(
						(int) Math.round(extraHeight * unitsToPoints / modulePt));
			}
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
}
