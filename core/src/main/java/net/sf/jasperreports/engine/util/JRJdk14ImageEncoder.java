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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.type.ImageTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRJdk14ImageEncoder extends JRAbstractImageEncoder
{
	private static final String EXCEPTION_MESSAGE_KEY_IMAGE_WRITER_NOT_FOUND = "util.jdk14.image.writer.not.found";
	
	@Override
	public byte[] encode(BufferedImage bi, ImageTypeEnum imageType) throws JRException
	{
		String formatName = null;

		switch (imageType)
		{
			case GIF :
			{
				formatName = "gif";
				break;
			}
			case PNG :
			{
				formatName = "png";
				break;
			}
			case TIFF :
			{
				formatName = "tiff";
				break;
			}
			case JPEG :
			case UNKNOWN :
			default:
			{
				formatName = "jpeg";
				break;
			}
		}
		
		boolean success = false;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try
		{
			success = ImageIO.write(bi, formatName, baos);
		}
		catch (IOException e)
		{
			throw new JRException(e);
		}
		
		if (!success)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_IMAGE_WRITER_NOT_FOUND,
					new Object[]{formatName});
		}
		
		return baos.toByteArray();
	}


}
