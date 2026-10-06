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
package net.sf.jasperreports.data.json;

import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.sf.jasperreports.data.StandardRepositoryDataLocation;
import net.sf.jasperreports.dataadapters.FileDataAdapter;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JsonDataAdapter extends FileDataAdapter {

	public String getSelectExpression();

	public void setSelectExpression(String selectExpression);

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
	public void setFileName(String fileName);

	public JsonExpressionLanguageEnum getLanguage();

	public void setLanguage(JsonExpressionLanguageEnum language);

	public Locale getLocale();

	public void setLocale(Locale locale);

	public TimeZone getTimeZone();

	public void setTimeZone(TimeZone timeZone);

	public boolean isUseConnection();
	
	public void setUseConnection(boolean useConnection);

	public String getDatePattern();

	public void setDatePattern(String datePattern);

	public String getNumberPattern();

	public void setNumberPattern(String numberPattern);
}
