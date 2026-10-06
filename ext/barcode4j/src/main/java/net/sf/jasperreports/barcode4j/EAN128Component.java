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

import org.krysalis.barcode4j.ChecksumMode;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonTypeName("barcode4j:EAN128")
public class EAN128Component extends Barcode4jComponent
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_CHECKSUM_MODE = "checksumMode";
	public static final String PROPERTY_TEMPLATE_EXPRESSION = "templateExpression";

	private String checksumMode;
	private JRExpression templateExpression;

	@JacksonXmlProperty(isAttribute = true)
	public String getChecksumMode()
	{
		return checksumMode;
	}

	public void setChecksumMode(String checksumMode)
	{
		Object old = this.checksumMode;
		this.checksumMode = checksumMode;
		getEventSupport().firePropertyChange(PROPERTY_CHECKSUM_MODE, 
				old, this.checksumMode);
	}

	public void setChecksumMode(ChecksumMode checksumMode)
	{
		setChecksumMode(checksumMode == null ? null : checksumMode.getName());
	}

	public JRExpression getTemplateExpression()
	{
		return templateExpression;
	}

	public void setTemplateExpression(JRExpression templateExpression)
	{
		Object old = this.templateExpression;
		this.templateExpression = templateExpression;
		getEventSupport().firePropertyChange(PROPERTY_TEMPLATE_EXPRESSION, 
				old, this.templateExpression);
	}

	@Override
	public void receive(BarcodeVisitor visitor)
	{
		visitor.visitEANCode128(this);
	}
	
	@Override
	public Object clone()
	{
		EAN128Component clone = (EAN128Component) super.clone();
		clone.templateExpression = JRCloneUtils.nullSafeClone(templateExpression);
		return clone;
	}
}
