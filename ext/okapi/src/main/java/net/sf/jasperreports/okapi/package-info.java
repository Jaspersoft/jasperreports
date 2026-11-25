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
* Contains the Okapi barcode components, which draw barcodes with the Okapi Barcode library
* (<a href="https://github.com/woo-j/OkapiBarcode">https://github.com/woo-j/OkapiBarcode</a>).
* <br/>
* There is one component per barcode type, such as <code>okapi:Code128</code>, <code>okapi:EAN</code>
* and <code>okapi:QRCode</code>. Every component has a code expression, the evaluation time and group,
* the rotation and image alignment of the barcode image, and the horizontal and vertical quiet zones.
* Components whose size depends on the data add a module width, and those that draw human-readable
* text add its location and alignment; the text uses the font of the element style.
* <p>
* Sizes are in report units. When a module width is set and the barcode fits the element, it is drawn
* at that size; otherwise it is sized to fill the element. Linear barcodes take the element height.
* </p><p>
* The components themselves don't use the Okapi API, which is used only when barcodes are
* filled and previewed, by the non-public classes of this package.
* </p>
*/
package net.sf.jasperreports.okapi;
