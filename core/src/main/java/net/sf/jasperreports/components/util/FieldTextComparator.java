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

import java.util.Locale;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class FieldTextComparator extends AbstractFieldComparator<String> {

	private Locale locale;

	public FieldTextComparator(Locale locale) {
		this.locale = locale;
	}
	
	@Override
	public void initValues() {
		compareStart = valueStart;
	}
	
	@Override
	public boolean compare (String filterTypeOperator) {
		boolean defaultResult = true, 
				result = defaultResult;
		
		try {
			initValues();
		} catch (Exception e) {
			throw new JRRuntimeException(e);
		}
		
		FilterTypeTextOperatorsEnum textEnum = FilterTypeTextOperatorsEnum.getByEnumConstantName(filterTypeOperator);
		
		if (compareStart != null) {
			String lcCompareStart = compareStart.toLowerCase(locale);
			boolean validComparison = compareTo != null;
			String lcCompareTo = validComparison ? compareTo.toLowerCase(locale) : null;
			
			switch (textEnum) {
				case CONTAINS:
					result = validComparison ? lcCompareTo.contains(lcCompareStart) : false;
					break;
				case DOES_NOT_CONTAIN:
					result = validComparison ? !lcCompareTo.contains(lcCompareStart) : false;
					break;
				case DOES_NOT_END_WITH:
					result = validComparison ? !lcCompareTo.endsWith(lcCompareStart) : false;
					break;
				case DOES_NOT_START_WITH:
					result = validComparison ? !lcCompareTo.startsWith(lcCompareStart) : false;
					break;
				case ENDS_WITH:
					result = validComparison ? lcCompareTo.endsWith(lcCompareStart) : false;
					break;
				case EQUALS:
					result = validComparison ? lcCompareTo.equals(lcCompareStart) : false;
					break;
				case IS_NOT_EQUAL_TO:
					result = validComparison ? !lcCompareTo.equals(lcCompareStart) : defaultResult;
					break;
				case STARTS_WITH:
					result = validComparison ? lcCompareTo.startsWith(lcCompareStart) : false;
					break;
				case IS_NULL:
					result = !validComparison;
					break;
				case IS_NOT_NULL:
					result = validComparison;
					break;
			}
		}

		return result;
	}

}
