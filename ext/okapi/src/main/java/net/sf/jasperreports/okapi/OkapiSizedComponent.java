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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;


/**
 * Base class of the Okapi barcode components whose width depends on the encoded data,
 * adding an optional module width.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class OkapiSizedComponent extends OkapiComponent
{

	public static final String PROPERTY_MODULE_WIDTH = "moduleWidth";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Double moduleWidth;

	public OkapiSizedComponent()
	{
	}

	/**
	 * The width of one module (the size of one module for 2D codes), in report units.
	 * <p>
	 * When set, the barcode is drawn at that module width if it fits the element and shrunk
	 * proportionally otherwise. When not set, the module width is computed so that the barcode
	 * fills the element.
	 * </p>
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getModuleWidth()
	{
		return moduleWidth;
	}

	public void setModuleWidth(Double moduleWidth)
	{
		Object old = this.moduleWidth;
		this.moduleWidth = moduleWidth;
		getEventSupport().firePropertyChange(PROPERTY_MODULE_WIDTH,
				old, this.moduleWidth);
	}
}
