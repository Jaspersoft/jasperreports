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
package net.sf.jasperreports.engine.virtualization;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.base.VirtualElementsData;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.JRRecordedValues;
import net.sf.jasperreports.engine.fill.JRRecordedValuesGenericPrintElement;
import net.sf.jasperreports.engine.fill.JRRecordedValuesPrintImage;
import net.sf.jasperreports.engine.fill.JRRecordedValuesPrintText;
import net.sf.jasperreports.engine.fill.JRTemplateGenericPrintElement;
import net.sf.jasperreports.engine.fill.JRTemplatePrintElement;
import net.sf.jasperreports.engine.fill.JRTemplatePrintEllipse;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintGraphicElement;
import net.sf.jasperreports.engine.fill.JRTemplatePrintImage;
import net.sf.jasperreports.engine.fill.JRTemplatePrintLine;
import net.sf.jasperreports.engine.fill.JRTemplatePrintRectangle;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DefaultSerializerRegistry implements SerializerRegistry
{

	private static final DefaultSerializerRegistry INSTANCE = new DefaultSerializerRegistry();
	
	public static SerializerRegistry getInstance()
	{
		return INSTANCE;
	}
	
	private Map<Class<?>, ObjectSerializer<?>> classSerializers;
	private Map<Integer, ObjectSerializer<?>> typeSerializers;
	
	public DefaultSerializerRegistry()
	{
		classSerializers = new HashMap<>();
		typeSerializers = new HashMap<>();
		
		addSerializer(String.class, new StringSerializer());
		addSerializer(UUID.class, new UUIDSerializer());
		addSerializer(Byte.class, new ByteSerializer());
		addSerializer(Short.class, new ShortSerializer());
		addSerializer(Integer.class, new IntegerSerializer());
		addSerializer(Long.class, new LongSerializer());
		addSerializer(Boolean.class, new BooleanSerializer());
		addSerializer(Float.class, new FloatSerializer());
		addSerializer(Double.class, new DoubleSerializer());
		addSerializer(BigInteger.class, new BigIntegerSerializer());
		addSerializer(BigDecimal.class, new BigDecimalSerializer(new BigIntegerSerializer()));
		addSerializer(Date.class, new DateSerializer());
		addSerializer(java.sql.Date.class, new SqlDateSerializer());
		addSerializer(Time.class, new TimeSerializer());
		addSerializer(Timestamp.class, new TimestampSerializer());
		
		addSerializableType(VirtualElementsData.class, SerializationConstants.OBJECT_TYPE_ELEMENTS_DATA);
		addSerializableType(JRTemplatePrintElement.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_ELEMENT);
		addSerializableType(JRTemplatePrintFrame.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_FRAME);
		addSerializableType(JRTemplatePrintText.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_TEXT);
		addSerializableType(JRRecordedValuesPrintText.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_TEXT_RECORDED_VALUES);
		addSerializableType(JRTemplatePrintGraphicElement.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_GRAPHIC);
		addSerializableType(JRTemplatePrintImage.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_IMAGE);
		addSerializableType(JRRecordedValuesPrintImage.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_IMAGE_RECORDED_VALUES);
		addSerializableType(JRTemplatePrintLine.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_LINE);
		addSerializableType(JRTemplatePrintRectangle.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_RECTANGLE);
		addSerializableType(JRTemplatePrintEllipse.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_ELLIPSE);
		addSerializableType(JRTemplateGenericPrintElement.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_GENERIC);
		addSerializableType(JRRecordedValuesGenericPrintElement.class, SerializationConstants.OBJECT_TYPE_TEMPLATE_GENERIC_RECORDED_VALUES);
		
		addSerializableType(JRPrintHyperlinkParameters.class, SerializationConstants.OBJECT_TYPE_HYPERLINK_PARAMETERS);
		addSerializableType(JRPrintHyperlinkParameter.class, SerializationConstants.OBJECT_TYPE_HYPERLINK_PARAMETER);

		addSerializer(JREvaluationTime.class, new EvaluationTimeSerializer());
		addSerializer(JRRecordedValues.class, new RecordedValuesSerializer());
	}
	
	private <T> void addSerializer(Class<T> type, ObjectSerializer<T> serializer)
	{
		classSerializers.put(type, serializer);
		typeSerializers.put(serializer.typeValue(), serializer);
	}
	
	private <T extends VirtualizationSerializable> void addSerializableType(Class<T> type,
			int typeValue)
	{
		addSerializer(type, new SerializableSerializer<T>(typeValue, type));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ObjectSerializer<T> getSerializer(Class<T> objectClass)
	{
		return (ObjectSerializer<T>) classSerializers.get(objectClass);
	}

	@Override
	public ObjectSerializer<?> getSerializer(int typeValue)
	{
		return typeSerializers.get(typeValue);
	}

}
