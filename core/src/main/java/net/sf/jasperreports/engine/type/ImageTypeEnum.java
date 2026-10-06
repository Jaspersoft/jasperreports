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
package net.sf.jasperreports.engine.type;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum ImageTypeEnum implements NamedEnum
{
	/**
	 * Specifies that the image is of unknown type.
	 */ 
	UNKNOWN("Unknown", null, null),
	
	/**
	 * Specifies that the image is of GIF type.
	 */ 
	GIF("GIF", "image/gif", "gif"),
	
	/**
	 * Specifies that the image is of JPEG type.
	 */ 
	JPEG("JPEG", "image/jpeg", "jpg"),
	
	/**
	 * Specifies that the image is of PNG type.
	 */ 
	PNG("PNG", "image/png", "png"),
	
	/**
	 * Specifies that the image is of TIFF type.
	 */ 
	TIFF("TIFF", "image/tiff", "tiff"),
	
	/**
	 * Specifies that the image is of WEBP type.
	 */ 
	WEBP("WEBP", "image/webp", "webp");

	/**
	 *
	 */
	private final transient String name;
	private final transient String mimeType;
	private final transient String fileExtension;

	private ImageTypeEnum(
		String name, 
		String mimeType,
		String fileExtension
		)
	{
		this.name = name;
		this.mimeType = mimeType;
		this.fileExtension = fileExtension;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 *
	 */
	public String getMimeType()
	{
		return mimeType;
	}
	
	/**
	 *
	 */
	public String getFileExtension()
	{
		return fileExtension;
	}
	
	/**
	 *
	 */
	public static ImageTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
