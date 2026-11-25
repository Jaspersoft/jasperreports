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

import net.sf.jasperreports.engine.JRConstants;


/**
 * DPD parcel label barcode, a Code 128 variant. Encodes 27 or 28 characters: an optional
 * leading character, 11 upper-case letters or digits, and 16 digits. The check character is drawn in the
 * human-readable text only.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName(DPDCodeComponent.COMPONENT_NAME)
public class DPDCodeComponent extends OkapiTextComponent
{

	public static final String COMPONENT_NAME = COMPONENT_NAME_PREFIX + "DPDCode";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public DPDCodeComponent()
	{
	}
}
