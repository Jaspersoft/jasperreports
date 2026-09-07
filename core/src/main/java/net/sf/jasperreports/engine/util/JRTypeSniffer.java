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
package net.sf.jasperreports.engine.util;

import net.sf.jasperreports.engine.type.ImageTypeEnum;


/**
 * @author George Stojanoff (gstojanoff@jaspersoft.com), Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public final class JRTypeSniffer
{

	/**
	 * Sniffs an incoming byte array to see if the first 3 characters
	 * are GIF. This is also known as the GIF signature. See
	 * http://www.dcs.ed.ac.uk/home/mxr/gfx/2d/GIF87a.txt for more details
	 * Note: Perhaps we should be checking for the more restive string GIF87a but
	 *       I am not sure if older GIF images are sill out there in use on the web.
	 * Note: This method only really needs the first 3 bytes.
	 */
	public static boolean isGIF(byte[] data) {
		// chech if the image data length is less than 3 bytes
		if (data.length < 3) {
			return false;
		}
		
		if (
			data[0] == 'G'
			&& data[1] == 'I'
			&& data[2] == 'F'
			) 
		{
			return true;
		}
	
		return false;
	}
	
	/**
	 * Sniffs an incoming byte array to see if the starting value is 0xffd8
	 * which is the "header" for JPEG data
	 * Note: This method only really needs the first 2 bytes.
	 */
	public static boolean isJPEG(byte[] data) {
		// check if the image data length is less than 2 bytes
		if (data.length < 2) {
			return false;
		}
		
		//0xff is -1 and 0xd8 is -40
		if (data[0] == -1 && data[1] == -40) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sniffs an incoming byte array to see if the first eight
	 * bytes are the following (decimal) values:
	 * 137 80 78 71 13 10 26 10
	 * which is the "signature" for PNG data
	 * See http://www.w3.org/TR/PNG/#5PNG-file-signature
	 * for more details.
	 * Note: This method only really needs the first 8 bytes.
	 */
	public static boolean isPNG(byte[] data) {
		if (data.length < 8) {
			return false;
		}
		
		if (data[0] == -119 &&
			data[1] == 80 &&
			data[2] == 78 &&
			data[3] == 71 &&
			data[4] == 13 && 
			data[5] == 10 &&
			data[6] == 26 &&
			data[7] == 10) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sniffs an incoming byte array to see if the starting value is 0x4949
	 * (little endian) or 0x4D4D (big endian) which is the "header" for TIFF data
	 * The TIFF standards supports both endians.
	 * See http://palimpsest.stanford.edu/bytopic/imaging/std/tiff5.html
	 * for more details.
	 * Note: This method only really needs the first 2 bytes.
	 */
	public static boolean isTIFF(byte[] data) {
		if (data.length < 2) {
			return false;
		}
		
		if ((data[0] == 73 && data[1] == 73) || 
			(data[0] == 77 && data[1] == 77)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Sniffs an incoming byte array to see if the starting values are matching
	 * the WEBP magic number.
	 * See https://en.wikipedia.org/wiki/WebP for more details.
	 * Note: This method only really needs the first 15 bytes.
	 */
	public static boolean isWEBP(byte[] data) {
		if (data.length < 15) {
			return false;
		}
		
		if (
			data[0] == 'R'
			&& data[1] == 'I'
			&& data[2] == 'F'
			&& data[3] == 'F'
			&& data[8] == 'W'
			&& data[9] == 'E'
			&& data[10] == 'B'
			&& data[11] == 'P'
			&& data[12] == 'V'
			&& data[13] == 'P'
			&& data[14] == '8'
			) 
		{
			return true;
		}
		
		return false;
	}

	/**
	 * 
	 */
	public static ImageTypeEnum getImageTypeValue(byte[] data) 
	{
		if (JRTypeSniffer.isGIF(data)) 
		{
			return ImageTypeEnum.GIF;
		}
		else if (JRTypeSniffer.isJPEG(data)) 
		{
			return ImageTypeEnum.JPEG;
		}
		else if (JRTypeSniffer.isPNG(data)) 
		{
			return ImageTypeEnum.PNG;
		}
		else if (JRTypeSniffer.isTIFF(data)) 
		{
			return ImageTypeEnum.TIFF;
		}
		else if (JRTypeSniffer.isWEBP(data)) 
		{
			return ImageTypeEnum.WEBP;
		}
		
		return ImageTypeEnum.UNKNOWN;
	}

	
	private JRTypeSniffer()
	{
	}
}
