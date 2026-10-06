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
package net.sf.jasperreports.web.actions;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class SearchData implements Serializable {
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private String searchString;
	private boolean caseSensitive;
	private boolean wholeWordsOnly;

	private boolean removeAccents;

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isWholeWordsOnly() {
		return wholeWordsOnly;
	}

	public void setWholeWordsOnly(boolean wholeWordsOnly) {
		this.wholeWordsOnly = wholeWordsOnly;
	}

	public boolean isRemoveAccents() {
		return removeAccents;
	}

	public void setRemoveAccents(boolean removeAccents) {
		this.removeAccents = removeAccents;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
