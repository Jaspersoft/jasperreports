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
* Contains classes for the built-in Barcode4J component.
* <br/>
* <h3>The Barcode4J Component</h3>
* This component relies on the Barcode4J library (<a href="http://barcode4j.sourceforge.net">http://barcode4j.sourceforge.net</a>). 
* <br/>
* Unlike the Barbecue barcode component, there is only one component per Barcode4J 
* barcode type. All the barcode's components derive from a base type which defines its 
* common attributes.
* <p>
* All barcodes in this library include a code expression, which provides the textual data to 
* be encoded in the barcode, and an optional expression that provides a pattern to be 
* applied on the message displayed in the barcode. Delayed evaluation of the barcode can 
* be configured using the two evaluation attributes and works in the same way as for 
* Barbecue barcodes. 
* </p>
* Several optional barcode rendering attributes can be set:
* <ul>
* <li><code>orientation</code> - specifies how the barcode and any accompanying text are to be 
* oriented when rendered. There are 4 possible values: 
* <ul>
* <li>0 - indicates that the barcode is to be rendered in the default orientation 
* (which for linear barcodes is with vertical bars arranged from left to right)</li>
* <li>90 - indicates that the barcode is to be rotated anti-clockwise by 90 degrees</li>
* <li>180 - indicates that the barcode is to be rotated anti-clockwise by 180 degrees</li>
* <li>270 - indicates that the barcode is to be rotated anti-clockwise by 270 degrees</li>
* </ul></li>
* <li><code>moduleWidth</code> - specifies the width (in pixels) of the thinnest bar/module.</li>
* <li><code>textPosition</code> - Sets the placement of the human-readable barcode message. It has 
* three possible values: <code>none</code> - meaning no human-readable message, and 
* <code>bottom</code> and <code>top</code>.
* <br/> 
* Note that the message and barcode are oriented together as a unit, so if the orientation 
* is set to 180 and the text position to top, the code appears reversed and the text 
* appears upside down beneath the image.</li>
* <li><code>quietZone</code> - Specifies the width of the quiet zone (in pixels).</li>
* <li><code>verticalQuietZone</code> - Specifies the height of the vertical quiet zone (in pixels).</li>
* </ul>
* Concrete barcode types extend the base type by adding further attributes supported by the specific symboloy:
* <ul>
* <li>Codabar barcodes have a <code>wideFactor</code> attribute that specifies the factor between the 
* width of wide bars and the width of narrow bars.</li>
* <li>Code128 barcodes do not have any specific attributes.</li>
* <li>EAN128, EAN13, EAN8, UPCA and UPCE barcodes have a <code>checksumMode</code> 
* attribute which indicates how the check digit is to be handled. The attribute accepts 
* four values: 
* <ul>
* <li><code>add</code> - adds the checksum to the data;</li>
* <li><code>check</code> - indicates that the data should already contain a checksum;</li>
* <li><code>ignore</code> - doesn't expect or add a checksum to the data;</li>
* <li><code>auto</code> - attempts to detect whether a checksum is already present in the 
* data or one should be added.</li>
* </ul></li>
* <li>Data Matrix barcodes can be configured to render as a square or rectangle by the 
* <code>shape</code> attribute.</li>
* <li>Code39 barcodes have a <code>checksumMode</code> attribute, a <code>displayChecksum</code> 
* attribute that controls whether the human-readable message shows the checksum 
* character, a <code>displayStartStop</code> attribute which decides whether start/stop 
* characters are to be displayed in the human-readable message, 
* <code>extendedCharSetEnabled</code> to indicate that the barcode can display characters 
* from the entire 7-bit ASCII set, <code>intercharGapWidth</code> to control the width between 
* the characters and a <code>wideFactor</code> attribute.</li>
* <li>Interleaved2Of5 barcodes can specify <code>checksumMode</code>, <code>displayChecksum</code> and 
* <code>wideFactor</code> attributes with the same meanings as in Code39 barcodes.</li>
* <li>RoyalMailCustomer and USPSIntelligentMail barcodes have a <code>checksumMode</code> 
* attribute, an <code>intercharGapWidth</code> attribute, an <code>ascenderHeight</code> attribute which 
* sets the length of the bar ascender/descender, and a <code>trackHeight</code> attribute which 
* provides the height of the barcode track.</li>
* <li>POSTNET barcodes can specify <code>checksumMode</code>, <code>displayChecksum</code> and 
* <code>intercharGapWidth</code> attributes, plus <code>shortBarHeight</code> for setting the height of 
* the short bar, and <code>baselinePosition</code> which can be <code>top</code> or <code>bottom</code> 
* to indicate how bars should align.</li>
* <li>PDF417 barcode can control the number of columns and rows with the 
* <code>minColumns</code>, <code>maxColumns</code>, <code>minRows</code>, <code>maxRows</code> and 
* <code>widthToHeightRatio</code> attributes. The error correction level can be set using the 
* <code>errorCorrectionLevel</code> attribute.</li>
* <li>QRCode barcode can control its error correction level using the <code>errorCorrectionLevel</code> attribute.</li>
* </ul>
* The object model for the barcode components uses 
* {@link net.sf.jasperreports.barcode4j.BarcodeComponent BarcodeComponent}
* as base class and concrete classes for each barcode type. 
* <p>
* When a report that contains barcodes is filled, the data and attributes of the barcode are 
* collected into an object and passed to an image producer whose responsibility is to create 
* a renderer for the barcode. The image producer implements the 
* {@link net.sf.jasperreports.barcode4j.BarcodeImageProducer BarcodeImageProducer}
* interface. As for Barbecue barcode elements, the resulting images have the size of the 
* design barcode element and use <code>RetainShape</code> as scale type. 
* </p><p>
* Determining which image producer to use for a barcode component element relies on 
* custom properties defined at the element, report and global levels. The 
* {@link net.sf.jasperreports.barcode4j.BarcodeImageProducer#PROPERTY_IMAGE_PRODUCER net.sf.jasperreports.components.barcode4j.image.producer} property can
* have a value of the name of the class that implements the image producer interface or an 
* alias that has been set for such a class, using a property of the form 
* {@link net.sf.jasperreports.barcode4j.BarcodeImageProducer#PROPERTY_PREFIX_IMAGE_PRODUCER net.sf.jasperreports.components.barcode4j.image.producer.&lt;alias&gt;}.
* JasperReports has two barcode image producer implementations: one which renders the 
* barcode in SVG format and one which renders the barcode as a rasterized image. The 
* first implementation is registered under the <code>svg</code> alias and is used by default; 
* the second one has <code>image</code> as alias and can be used by changing the 
* {@link net.sf.jasperreports.barcode4j.BarcodeImageProducer#PROPERTY_PREFIX_IMAGE_PRODUCER net.sf.jasperreports.components.barcode4j.image.producer.&lt;alias&gt;}
* property at any level. 
* </p><p>
* The SVG barcode image producer uses the Barcode4J API to export the barcode to SVG, 
* then it creates an SVG renderer based on the Batik SVG library. Hence, this option 
* introduces a dependency on Batik for viewing or exporting a report that includes 
* barcodes.
* </p><p> 
* The rasterized image producer draws the barcode on a PNG image which can be then 
* displayed in the generated report. This producer uses further properties, set at the same 
* levels as the image producer property, to allow the customization of the generated image. 
* </p>
*/
package net.sf.jasperreports.barcode4j;