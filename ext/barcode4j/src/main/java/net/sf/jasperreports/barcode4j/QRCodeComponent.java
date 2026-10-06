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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Contains the main settings for the QRCode component
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
@JsonTypeName("barcode4j:QRCode")
public class QRCodeComponent extends BarcodeComponent
{
	
	public static final String COMPONENT_DESIGNATION = "net.sf.jasperreports.component.element:QRCode";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_MARGIN = "margin";
	public static final String PROPERTY_ERROR_CORRECTION_LEVEL = "errorCorrectionLevel";
	public static final String PROPERTY_QR_VERSION = "qrVersion";

	public static final String PROPERTY_DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * Property that provides the character encoding to be used for QR code component elements.
	 * 
	 * <p>
	 * The property can be set at context/global, report and component element levels.
	 * The default encoding is UTF-8.
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_BARCODE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.COMPONENT},
			scopeQualifications = {QRCodeComponent.COMPONENT_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_6_5_0,
			defaultValue = PROPERTY_DEFAULT_ENCODING
			)
	public static final String PROPERTY_QRCODE_CHARACTER_ENCODING = 
			JRPropertiesUtil.PROPERTY_PREFIX + "components.qrcode.character.encoding";
		
	private Integer margin;
	private Integer qrVersion;
	private ErrorCorrectionLevelEnum errorCorrectionLevel;
	
	@Override
	public void receive(BarcodeVisitor visitor)
	{
		visitor.visitQRCode(this);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Integer getMargin()
	{
		return margin;
	}

	public void setMargin(Integer margin)
	{
		Object old = this.margin;
		this.margin = margin;
		getEventSupport().firePropertyChange(PROPERTY_MARGIN, old, this.margin);
	}

	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public ErrorCorrectionLevelEnum getErrorCorrectionLevel()
	{
		return errorCorrectionLevel;
	}
	
	public void setErrorCorrectionLevel(ErrorCorrectionLevelEnum errorCorrectionLevel)
	{
		Object old = this.errorCorrectionLevel;
		this.errorCorrectionLevel = errorCorrectionLevel;
		getEventSupport().firePropertyChange(PROPERTY_ERROR_CORRECTION_LEVEL, 
				old, this.errorCorrectionLevel);
	}

	@JacksonXmlProperty(isAttribute = true)
	public Integer getQrVersion() 
	{
		return qrVersion;
	}

	public void setQrVersion(Integer qrVersion) 
	{
		Object old = this.qrVersion;
		this.qrVersion = qrVersion;
		getEventSupport().firePropertyChange(PROPERTY_QR_VERSION, old, this.qrVersion);
	}

	@Override
	public String getDesignation()
	{
		return COMPONENT_DESIGNATION;
	}
}
