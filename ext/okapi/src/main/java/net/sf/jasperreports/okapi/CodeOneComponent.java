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
package net.sf.jasperreports.okapi;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;


/**
 * Code One barcode. Encodes ISO 8859-1 (Latin-1) text or GS1 data.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(CodeOneComponent.COMPONENT_NAME)
public class CodeOneComponent extends OkapiSizedComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "CodeOne";

	public static final String PROPERTY_PREFERRED_VERSION = "preferredVersion";
	public static final String PROPERTY_DATA_TYPE = "dataType";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private CodeOneVersionEnum preferredVersion;
	private CodeOneDataTypeEnum dataType;

	public CodeOneComponent()
	{
	}

	/**
	 * The symbol version; a larger one is used when the data needs it.
	 * When not set, the smallest of versions A to H that holds the data.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public CodeOneVersionEnum getPreferredVersion()
	{
		return preferredVersion;
	}

	public void setPreferredVersion(CodeOneVersionEnum preferredVersion)
	{
		Object old = this.preferredVersion;
		this.preferredVersion = preferredVersion;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_VERSION,
				old, this.preferredVersion);
	}

	/**
	 * How the content is interpreted; when not set, as plain data.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public CodeOneDataTypeEnum getDataType()
	{
		return dataType;
	}

	public void setDataType(CodeOneDataTypeEnum dataType)
	{
		Object old = this.dataType;
		this.dataType = dataType;
		getEventSupport().firePropertyChange(PROPERTY_DATA_TYPE,
				old, this.dataType);
	}
}
