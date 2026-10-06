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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.SimpleDataRenderer;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarcodeRasterizedImageProducer implements BarcodeImageProducer
{
	
	@Property(
			category = PropertyConstants.CATEGORY_BARCODE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.COMPONENT},
			scopeQualifications = {Barcode4jComponent.COMPONENT_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_3_5_2,
			valueType = Integer.class,
			defaultValue = "300"
			)
	public static final String PROPERTY_RESOLUTION = 
		BarcodeComponent.PROPERTY_PREFIX + "image.resolution";
	
	@Property(
			category = PropertyConstants.CATEGORY_BARCODE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.COMPONENT},
			scopeQualifications = {Barcode4jComponent.COMPONENT_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_3_5_2,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_TRUE
			)
	public static final String PROPERTY_GRAY = 
		BarcodeComponent.PROPERTY_PREFIX + "image.gray";
	
	@Property(
			category = PropertyConstants.CATEGORY_BARCODE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.COMPONENT},
			scopeQualifications = {Barcode4jComponent.COMPONENT_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_3_5_2,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_TRUE
			)
	public static final String PROPERTY_ANTIALIAS = 
		BarcodeComponent.PROPERTY_PREFIX + "image.antiAlias";
	
	@Override
	public Renderable createImage(
		JasperReportsContext jasperReportsContext,
		JRComponentElement componentElement, 
		BarcodeGenerator barcode, 
		String message
		)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			int resolution = JRPropertiesUtil.getInstance(jasperReportsContext).getIntegerProperty(
					componentElement, PROPERTY_RESOLUTION, 300);
			boolean gray = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(
					componentElement, PROPERTY_GRAY, true);
			boolean antiAlias = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(
					componentElement, PROPERTY_ANTIALIAS, true);
			int imageType = gray ? BufferedImage.TYPE_BYTE_GRAY 
					: BufferedImage.TYPE_BYTE_BINARY;
			
			BitmapCanvasProvider provider = 
				new BitmapCanvasProvider(
					out, "image/x-png", resolution, imageType, antiAlias, 
					OrientationEnum.getValueOrDefault(
						((Barcode4jComponent)componentElement.getComponent()).getOrientation()
						).getValue()
					);
			barcode.generateBarcode(provider, message);
			provider.finish();
			
			byte[] imageData = out.toByteArray();
			return SimpleDataRenderer.getInstance(imageData);
		}
		catch (Exception e)
		{
			throw new JRRuntimeException(e);
		}
	}

}
