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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;


/**
 * Grid Matrix barcode. Encodes ISO 8859-1 (Latin-1) and GB 2312 (Chinese) text.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(GridMatrixComponent.COMPONENT_NAME)
public class GridMatrixComponent extends OkapiSizedComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "GridMatrix";

	public static final String PROPERTY_PREFERRED_VERSION = "preferredVersion";
	public static final String PROPERTY_PREFERRED_ECC_LEVEL = "preferredEccLevel";
	public static final String PROPERTY_READER_INIT = "readerInit";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Integer preferredVersion;
	private Integer preferredEccLevel;
	private Boolean readerInit;

	public GridMatrixComponent()
	{
	}

	/**
	 * The smallest symbol version, 1 to 13, from 18 x 18 to 162 x 162 modules;
	 * a larger one is used when the data needs it.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredVersion()
	{
		return preferredVersion;
	}

	public void setPreferredVersion(Integer preferredVersion)
	{
		Object old = this.preferredVersion;
		this.preferredVersion = preferredVersion;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_VERSION,
				old, this.preferredVersion);
	}

	/**
	 * The lowest error correction level, 1 to 5, from about 10 to 50 percent of the symbol;
	 * a higher one is used when the symbol size allows it.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Integer getPreferredEccLevel()
	{
		return preferredEccLevel;
	}

	public void setPreferredEccLevel(Integer preferredEccLevel)
	{
		Object old = this.preferredEccLevel;
		this.preferredEccLevel = preferredEccLevel;
		getEventSupport().firePropertyChange(PROPERTY_PREFERRED_ECC_LEVEL,
				old, this.preferredEccLevel);
	}

	/**
	 * Whether the symbol is marked as a reader programming symbol.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getReaderInit()
	{
		return readerInit;
	}

	public void setReaderInit(Boolean readerInit)
	{
		Object old = this.readerInit;
		this.readerInit = readerInit;
		getEventSupport().firePropertyChange(PROPERTY_READER_INIT,
				old, this.readerInit);
	}
}
