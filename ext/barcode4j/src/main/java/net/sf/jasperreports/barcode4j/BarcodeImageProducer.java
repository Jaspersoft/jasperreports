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
package net.sf.jasperreports.barcode4j;

import org.krysalis.barcode4j.BarcodeGenerator;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.Renderable;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface BarcodeImageProducer
{

	@Property(
			category = PropertyConstants.CATEGORY_BARCODE,
			defaultValue = "svg",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.COMPONENT},
			scopeQualifications = {Barcode4jComponent.COMPONENT_DESIGNATION, QRCodeComponent.COMPONENT_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_3_5_2
			)
	String PROPERTY_IMAGE_PRODUCER = 
		BarcodeComponent.PROPERTY_PREFIX + "image.producer";

	@Property(
			name = "net.sf.jasperreports.components.barcode4j.image.producer.{alias}",
			category = PropertyConstants.CATEGORY_BARCODE,
			valueType = Class.class,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.COMPONENT},
			scopeQualifications = {Barcode4jComponent.COMPONENT_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_3_5_2
			)
	String PROPERTY_PREFIX_IMAGE_PRODUCER = 
		BarcodeComponent.PROPERTY_PREFIX + "image.producer.";
	
	Renderable createImage(
		JasperReportsContext jasperReportsContext,
		JRComponentElement componentElement, 
		BarcodeGenerator barcode, 
		String message
		);
	
}
