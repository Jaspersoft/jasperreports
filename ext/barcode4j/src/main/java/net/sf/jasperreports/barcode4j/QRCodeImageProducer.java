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

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.Renderable;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface QRCodeImageProducer
{

	@Property(
			name = "net.sf.jasperreports.components.barcode4j.qrcode.producer.{alias}",
			category = PropertyConstants.CATEGORY_BARCODE,
			valueType = Class.class,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.COMPONENT},
			scopeQualifications = {QRCodeComponent.COMPONENT_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_6_0_2
			)
	String PROPERTY_PREFIX_QRCODE_PRODUCER = 
		BarcodeComponent.PROPERTY_PREFIX + "qrcode.producer.";
	
	Renderable createImage(
		JasperReportsContext jasperReportsContext,
		JRComponentElement componentElement, 
		QRCodeBean qrCodeBean, 
		String message
		);
	
}
