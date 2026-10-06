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
package net.sf.jasperreports.chartthemes.simple;

import java.awt.Image;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.repo.RepositoryUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName("file")
public class FileImageProvider implements ImageProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	private String file;

	
	/**
	 *
	 */
	public FileImageProvider()
	{
	}
	
	
	/**
	 *
	 */
	public FileImageProvider(String file)
	{
		this.file = file;
	}
	
	
	@Override
	public Image getImage(JasperReportsContext jasperReportsContext)
	{
		try
		{
			return
				JRImageLoader.getInstance(jasperReportsContext).loadAwtImageFromBytes(
					RepositoryUtil.getInstance(jasperReportsContext).getBytesFromLocation(file)
					);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}


	public String getFile() {
		return file;
	}


	public void setFile(String file) {
		this.file = file;
	}

}
