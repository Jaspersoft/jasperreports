/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.pdf.type;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum PdfaConformanceEnum implements NamedEnum
{
	/**
	 * 
	 */
	NONE("none", null),

	/**
	 * 
	 */
	PDFA_1A("pdfa1a", PdfVersionEnum.VERSION_1_4),

	/**
	 * 
	 */
	PDFA_1B("pdfa1b", PdfVersionEnum.VERSION_1_4),
	
	
	/**
	 * 
	 */
	PDFA_2A("pdfa2a", PdfVersionEnum.VERSION_1_7),
	
	/**
	 * 
	 */
	PDFA_2B("pdfa2b", PdfVersionEnum.VERSION_1_7),
	
	/**
	 * 
	 */
	PDFA_2U("pdfa2u", PdfVersionEnum.VERSION_1_7),
	
	/**
	 * 
	 */
	PDFA_3A("pdfa3a", PdfVersionEnum.VERSION_1_7),
	
	/**
	 * 
	 */
	PDFA_3B("pdfa3b", PdfVersionEnum.VERSION_1_7),
	
	/**
	 * 
	 */
	PDFA_3U("pdfa3u", PdfVersionEnum.VERSION_1_7),
	
	/**
	 * 
	 */
	PDFA_4("pdfa4", PdfVersionEnum.VERSION_2_0);
	
	/**
	 *
	 */
	private final transient String name;
	private final transient PdfVersionEnum pdfVersion;

	private PdfaConformanceEnum(String name, PdfVersionEnum pdfVersion)
	{
		this.name = name;
		this.pdfVersion = pdfVersion;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public PdfVersionEnum getPdfVersion()
	{
		return pdfVersion;
	}

	/**
	 *
	 */
	public static PdfaConformanceEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
