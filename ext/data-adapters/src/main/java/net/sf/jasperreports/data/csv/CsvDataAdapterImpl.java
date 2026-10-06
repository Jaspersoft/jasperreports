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
package net.sf.jasperreports.data.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonRootName;

import net.sf.jasperreports.data.DataFile;
import net.sf.jasperreports.data.StandardRepositoryDataLocation;
import net.sf.jasperreports.dataadapters.AbstractDataAdapter;
import net.sf.jasperreports.dataadapters.RepositoryDataLocation;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */

@JsonRootName(value = "csvDataAdapter")
public class CsvDataAdapterImpl extends AbstractDataAdapter implements CsvDataAdapter
{
	private DataFile dataFile;
	private String encoding;
	private String recordDelimiter = "\n";
	private String fieldDelimiter = ",";
	private boolean useFirstRowAsHeader = false;
	private Locale locale;
	private TimeZone timeZone;
	private String datePattern = null;
	private String numberPattern = null;
	private boolean queryExecuterMode = false;
	private List<String> columnNames = new ArrayList<>();
	
	/**
	 * @deprecated replaced by {@link #getDataFile()}
	 */
	@Override
	@Deprecated
	public String getFileName() {
		if (dataFile instanceof RepositoryDataLocation) {
			return ((RepositoryDataLocation) dataFile).getLocation();
		}
		return null;
	}

	/**
	 * @deprecated replaced by {@link #setDataFile(net.sf.jasperreports.data.DataFile)} and {@link StandardRepositoryDataLocation}
	 */
	@Override
	@Deprecated
	public void setFileName(String fileName) {
		if (fileName != null) {
			StandardRepositoryDataLocation repositoryDataFile = new StandardRepositoryDataLocation(fileName);
			setDataFile(repositoryDataFile);
		}
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public boolean isUseFirstRowAsHeader() {
		return useFirstRowAsHeader;
	}

	@Override
	public void setUseFirstRowAsHeader(boolean useFirstRowAsHeader) {
		this.useFirstRowAsHeader = useFirstRowAsHeader;
	}
	
	@Override
	public String getRecordDelimiter() {
		return recordDelimiter;
	}

	@Override
	public void setRecordDelimiter(String recordDelimiter) {
		this.recordDelimiter = recordDelimiter;
	}
	
	@Override
	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	@Override
	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public String getDatePattern() {
		return datePattern;
	}

	@Override
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}

	@Override
	public String getNumberPattern() {
		return numberPattern;
	}

	@Override
	public void setNumberPattern(String numberPattern) {
		this.numberPattern = numberPattern;
	}

	@Override
	public boolean isQueryExecuterMode() {
		return queryExecuterMode;
	}

	@Override
	public void setQueryExecuterMode(boolean queryExecuterMode) {
		this.queryExecuterMode = queryExecuterMode;
	}

	@Override
	public List<String> getColumnNames() {
		return columnNames;
	}

	@Override
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	// FIXME lucianc use auto-naming="deriveByClass" in Castor?
	@Override
	public DataFile getDataFile()
	{
		return dataFile;
	}

	@Override
	public void setDataFile(DataFile dataFile)
	{
		this.dataFile = dataFile;
	}
}
