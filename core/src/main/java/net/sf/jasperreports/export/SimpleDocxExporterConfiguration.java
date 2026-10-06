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
package net.sf.jasperreports.export;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleDocxExporterConfiguration extends SimpleExporterConfiguration implements DocxExporterConfiguration
{
	private String metadataTitle;
	private String metadataAuthor;
	private String metadataSubject;
	private String metadataKeywords;
	private String metadataApplication;
	private Boolean embedFonts;
	private String encryptionPassword;
	
	@Override
	public String getMetadataTitle()
	{
		return metadataTitle;
	}
	
	/**
	 * 
	 */
	public void setMetadataTitle(String metadataTitle)
	{
		this.metadataTitle = metadataTitle;
	}
	
	@Override
	public String getMetadataAuthor()
	{
		return metadataAuthor;
	}
	
	/**
	 * 
	 */
	public void setMetadataAuthor(String metadataAuthor)
	{
		this.metadataAuthor = metadataAuthor;
	}
	
	@Override
	public String getMetadataSubject()
	{
		return metadataSubject;
	}
	
	/**
	 * 
	 */
	public void setMetadataSubject(String metadataSubject)
	{
		this.metadataSubject = metadataSubject;
	}
	
	@Override
	public String getMetadataKeywords()
	{
		return metadataKeywords;
	}
	
	/**
	 * 
	 */
	public void setMetadataKeywords(String metadataKeywords)
	{
		this.metadataKeywords = metadataKeywords;
	}
	
	@Override
	public String getMetadataApplication()
	{
		return metadataApplication;
	}
	
	/**
	 * 
	 */
	public void setMetadataApplication(String metadataApplication)
	{
		this.metadataApplication = metadataApplication;
	}
	
	@Override
	public Boolean isEmbedFonts()
	{
		return embedFonts;
	}
	
	/**
	 * 
	 */
	public void setEmbedFonts(Boolean embedFonts)
	{
		this.embedFonts = embedFonts;
	}

	@Override
	public String getEncryptionPassword()
	{
		return encryptionPassword;
	}
	
	/**
	 * 
	 */
	public void setEncryptionPassword(String encryptionPassword)
	{
		this.encryptionPassword = encryptionPassword;
	}
}
