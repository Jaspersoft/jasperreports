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
package net.sf.jasperreports.components.util;

import java.text.Format;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.FormatFactory;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public abstract class AbstractFieldComparator<T> {
	
	protected String valueStart;
	protected String valueEnd;
	
	protected T compareStart;
	protected T compareEnd;
	protected T compareTo;
	protected Class<?> compareToClass;
	
	protected Format formatter;
	
	private static FormatFactory formatFactory;
	
	static {
		formatFactory = new DefaultFormatFactory();
	}
	
	@SuppressWarnings("unchecked")
	public void setCompareTo(Object compareTo) {
		this.compareTo = (T) compareTo;
	}
	
	public void setValueStart(String valueStart) {
		this.valueStart = valueStart;
	}
	
	public void setValueEnd(String valueEnd) {
		this.valueEnd = valueEnd;
	}

	public void setCompareToClass(Class<?> compareToClass) {
		this.compareToClass = compareToClass;
	}
	
	public abstract boolean compare(String filterTypeOperator);
	
	public abstract void initValues() throws Exception;
	
	protected FormatFactory getFormatFactory() {
		return formatFactory;
	}
	
	public boolean isValid() {
		try {
			initValues();
		} catch (Exception e) {
			throw new JRRuntimeException(e);
		}
		return true;
	}
	

}
