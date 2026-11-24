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
/**
* Contains the report updaters that convert the elements of the deprecated barbecue and barcode4j
* extensions to <code>okapi:*</code> elements:
* {@link net.sf.jasperreports.okapi.convert.BarbecueConverter} and
* {@link net.sf.jasperreports.okapi.convert.Barcode4jConverter}.
* <br/>
* They are run by the <code>update</code> goal of the JasperReports Maven plugin, or by the
* <code>update</code> Ant task, with the legacy extension on the classpath. Converted reports are
* written to the output directory of the goal or task, and the source reports are left unchanged.
* <p>
* An element is converted only when its <code>okapi:*</code> element encodes the same data. An element
* whose conversion could encode different data is left as it is and logged as a warning, so that its
* data can be reviewed and prepared for conversion; so is an element of a type that has no
* <code>okapi:*</code> element yet. Legacy settings that have no <code>okapi:*</code> equivalent are
* dropped and logged. Converted barcodes follow the <code>okapi:*</code> sizing rules, so their size
* may differ from the legacy output.
* </p>
*/
package net.sf.jasperreports.okapi.convert;
