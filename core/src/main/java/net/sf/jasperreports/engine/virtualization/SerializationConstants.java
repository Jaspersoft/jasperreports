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

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface SerializationConstants
{

	int OBJECT_NULL = 0x01;
	int OBJECT_ARBITRARY = 0x02;

	int OBJECT_REF_MASK = 0x80;
	
	int OBJECT_TYPE_OFFSET = 0x10;
	int OBJECT_TYPE_COUNT = 0x3F - 0x10 + 1;
	
	int OBJECT_TYPE_STRING = 0x10;
	int OBJECT_TYPE_UUID = 0x11;
	int OBJECT_TYPE_BYTE = 0x12;
	int OBJECT_TYPE_SHORT = 0x13;
	int OBJECT_TYPE_INTEGER = 0x14;
	int OBJECT_TYPE_LONG = 0x15;
	int OBJECT_TYPE_FLOAT = 0x16;
	int OBJECT_TYPE_DOUBLE = 0x17;
	int OBJECT_TYPE_BOOLEAN = 0x18;
	int OBJECT_TYPE_BIG_INTEGER = 0x19;
	int OBJECT_TYPE_BIG_DECIMAL = 0x1A;
	
	// TODO lucianc 
	int OBJECT_TYPE_DATE = 0x1B;
	int OBJECT_TYPE_SQL_DATE = 0x1C;
	int OBJECT_TYPE_TIMESTAMP = 0x1D;
	int OBJECT_TYPE_TIME = 0x1E;
	
	int OBJECT_TYPE_ELEMENTS_DATA = 0x30;
	int OBJECT_TYPE_TEMPLATE_ELEMENT = 0x31;
	int OBJECT_TYPE_TEMPLATE_FRAME = 0x32;
	int OBJECT_TYPE_TEMPLATE_TEXT = 0x33;
	int OBJECT_TYPE_TEMPLATE_TEXT_RECORDED_VALUES = 0x34;
	int OBJECT_TYPE_TEMPLATE_IMAGE = 0x35;
	int OBJECT_TYPE_TEMPLATE_IMAGE_RECORDED_VALUES = 0x36;
	int OBJECT_TYPE_TEMPLATE_LINE = 0x37;
	int OBJECT_TYPE_TEMPLATE_RECTANGLE = 0x38;
	int OBJECT_TYPE_TEMPLATE_ELLIPSE = 0x39;
	int OBJECT_TYPE_TEMPLATE_GENERIC = 0x3A;
	int OBJECT_TYPE_TEMPLATE_GENERIC_RECORDED_VALUES = 0x3B;
	int OBJECT_TYPE_TEMPLATE_GRAPHIC = 0x3C;
	
	int OBJECT_TYPE_HYPERLINK_PARAMETERS = 0x3C;
	int OBJECT_TYPE_HYPERLINK_PARAMETER = 0x3D;
	int OBJECT_TYPE_EVALUATION_TIME = 0x3E;
	int OBJECT_TYPE_RECORDED_VALUES = 0x3F;

}
