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
* Contains classes for the built-in Barbecue component.
* <br/>
* <h3>The Barbecue Component</h3>
* This component uses the Barbecue library 
* (<a href="http://barbecue.sourceforge.net">http://barbecue.sourceforge.net</a>). Its barcodes can be embedded in 
* reports via a uniform component element that specifies common attributes and 
* determines the barcode type via a special attribute.
* <p> 
* The JRXML structure of a barcode component is listed in the JasperReports components schema. The XML element 
* belongs to the <code>http://jasperreports.sourceforge.net/jasperreports/components</code> namespace, 
* which is the namespace of the component elements built into JasperReports. 
* </p><p>
* The type of the barcode is given by the mandatory <code>type</code> attribute. The supported types 
* are listed in the JRXML structure as values allowed for this attribute. For the most part, 
* they correspond to the Barbecue barcode factory methods. 
* </p><p>
* The data/text to be encoded in a barcode is provided by the code expression, which is 
* expected to evaluate to a <code>java.lang.String</code> object. If the expression evaluates to null 
* and the barcode does not have delayed evaluation, the element will not 
* generate an image in the filled report. 
* </p><p>
* A second expression is used to provide an application identifier for the generic UCC 128 
* barcode type. The expression should not be used for any other barcode type. Note that 
* the list of supported barcode types includes types for specific UCC 128 application 
* domains: EAN 128, USPS, Shipment Identification Number, SSCC 18, SCC 14, and 
* Global Trade Item Number. 
* </p><p>
* Further attributes influence which barcode is displayed and how it is displayed. The 
* <code>drawText</code> attribute specifies whether or not the encoded data should be shown 
* underneath the barcode; the <code>barWidth</code> attribute can be used to set a desired width of 
* the thinnest bar, the <code>barHeight</code> attribute can specify the height of the bars, and the 
* <code>checksumRequired</code> attribute determines whether a check digit is to be included in the 
* barcode for the types that support it. 
* </p><p>
* The <code>evaluationTime</code> and <code>evaluationGroup</code> attributes allow the barcode to be 
* evaluated after the band on which it is placed has been rendered. This attribute functions 
* in the same way as for text fields and images, with the exception that <code>Auto</code> evaluation 
* type is not supported. 
* </p><p>
* On the API side, the Barbecue component is represented by the 
* {@link net.sf.jasperreports.barbecue.BarbecueComponent BarbecueComponent} 
* interface, whose default implementation is 
* {@link net.sf.jasperreports.barbecue.StandardBarbecueComponent StandardBarbecueComponent}. 
* </p><p>
* When the report is filled, a barcode renderer of type 
* {@link net.sf.jasperreports.barbecue.BarbecueRendererImpl BarbecueRendererImpl} is
* created. It is used for the image included in the generated report. The image takes its 
* scale type from the <code>RetainShape</code> attribute and its size from the <code>size</code> defined 
* for the barcode element in the report. 
*/
package net.sf.jasperreports.barbecue;