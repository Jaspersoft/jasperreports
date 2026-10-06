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
package net.sf.jasperreports.data.xls;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import net.sf.jasperreports.data.StandardRepositoryDataLocation;
import net.sf.jasperreports.dataadapters.FileDataAdapter;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */

public interface XlsDataAdapter extends FileDataAdapter 
{
	public String getDatePattern();

	public String getNumberPattern();

	/**
	 * @deprecated replaced by {@link #getDataFile()}
	 */
	@Deprecated
	@JsonIgnore
	public String getFileName();

	/**
	 * @deprecated replaced by {@link #setDataFile(net.sf.jasperreports.dataadapters.DataFile)} and {@link StandardRepositoryDataLocation}
	 */
	@Deprecated
	@JsonProperty
	public void setFileName(String filename);
	
	public boolean isUseFirstRowAsHeader();
	
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<String> getColumnNames();

	@JacksonXmlElementWrapper(useWrapping = false)
	public List<Integer> getColumnIndexes();
	
	public void setColumnNames(List<String> columnNames);

	public void setColumnIndexes(List<Integer> columnIndexes);

	public void setUseFirstRowAsHeader(boolean useFirstRowAsHeader);

	public Locale getLocale();

	public void setLocale(Locale locale);

	public TimeZone getTimeZone();

	public void setTimeZone(TimeZone timeZone);

	public void setDatePattern(String datePattern);

	public void setNumberPattern(String numberPattern);
	
	public boolean isQueryExecuterMode();

	public void setQueryExecuterMode(boolean queryExecuterMode);
	
	public String getSheetSelection();
	
	public void setSheetSelection(String sheetSelection);
	
}
