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
@JsonRootName(value = "xlsDataAdapter")
public class XlsDataAdapterImpl extends AbstractDataAdapter implements XlsDataAdapter
{
	private boolean useFirstRowAsHeader = false;
	private Locale locale;
	private TimeZone timeZone;
	private String datePattern = null;
	private String numberPattern = null;
	private DataFile dataFile;
	private List<String> columnNames = new ArrayList<>();
	private List<Integer> columnIndexes = new ArrayList<>();
	private boolean queryExecuterMode = false;
	private String sheetSelection;
	
	@Override
	public String getDatePattern() {
		return datePattern;
	}

	@Override
	public String getNumberPattern() {
		return numberPattern;
	}

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
	public void setFileName(String filename) {
		if (filename != null) {
			StandardRepositoryDataLocation repositoryDataFile = new StandardRepositoryDataLocation(filename);
			setDataFile(repositoryDataFile);
		}
	}
	
	@Override
	public boolean isUseFirstRowAsHeader() {
		return useFirstRowAsHeader;
	}
	
	@Override
	public List<String> getColumnNames() {
		return columnNames;
	}

	@Override
	public List<Integer> getColumnIndexes() {
		return columnIndexes;
	}
	
	@Override
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	@Override
	public void setColumnIndexes(List<Integer> columnIndexes) {
		this.columnIndexes = columnIndexes;
	}

	@Override
	public void setUseFirstRowAsHeader(boolean useFirstRowAsHeader) {
		this.useFirstRowAsHeader = useFirstRowAsHeader;
	}

	@Override
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
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
	public String getSheetSelection() {
		return sheetSelection;
	}

	@Override
	public void setSheetSelection(String sheetSelection) {
		this.sheetSelection = sheetSelection;
	}
	
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

}
