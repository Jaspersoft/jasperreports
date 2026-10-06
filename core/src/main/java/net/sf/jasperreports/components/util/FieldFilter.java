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
import java.util.TimeZone;

import net.sf.jasperreports.engine.DatasetFilter;
import net.sf.jasperreports.engine.EvaluationType;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.fill.DatasetFillContext;
import net.sf.jasperreports.engine.fill.JRFillDataset;
import net.sf.jasperreports.engine.util.JRDataUtils;

/**
 * A dataset filter that matches String values based on substrings.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FieldFilter implements DatasetFilter {

	private String field;

	private String filterValueStart;
	private String filterValueEnd;
	
	private String filterType;
	private String filterTypeOperator;
	private String filterPattern;
	private String localeCode;
	private String timeZoneId;

	private DatasetFillContext context;
	
	private Boolean isValid;
	private Boolean isField;

	private FilterTypesEnum filterTypeEnum;
	
	private AbstractFieldComparator<?> fieldComparator;
	
	public FieldFilter() {
	}
	
	/**
	 * Creates a field filter.
	 * 
	 * 
	 */
	public FieldFilter(String field, String filterValueStart,
			String filterValueEnd, String filterType, String filterTypeOperator) {
		this.field = field;
		this.filterValueStart = filterValueStart;
		this.filterValueEnd = filterValueEnd;
		this.filterType = filterType;
		this.filterTypeOperator = filterTypeOperator;
	}

	@Override
	public void init(DatasetFillContext context) {
		this.context = context;
		this.filterTypeEnum = FilterTypesEnum.getByName(filterType);
		if (fieldComparator == null) {
			Locale locale = getFilterLocale();
			TimeZone timeZone = getFilterTimeZone();
			
			fieldComparator = FieldComparatorFactory
					.createFieldComparator(
							filterTypeEnum,
							filterPattern,
							locale,
							timeZone);
		}
	}

	protected Locale getFilterLocale()
	{
		Locale locale;
		if (localeCode != null)
		{
			locale = JRDataUtils.getLocale(localeCode);
		}
		else if (context.getLocale() != null)
		{
			locale = context.getLocale();
		}
		else
		{
			locale = Locale.getDefault();
		}
		return locale;
	}

	protected TimeZone getFilterTimeZone()
	{
		TimeZone timeZone;
		if (timeZoneId != null)
		{
			timeZone = JRDataUtils.getTimeZone(timeZoneId);
		}
		else
		{
			timeZone = (TimeZone) context.getParameterValue(JRParameter.REPORT_TIME_ZONE);
		}
		return timeZone;
	}

	@Override
	public boolean matches(EvaluationType evaluation) {
		Object value;
		if (isField == null || Boolean.TRUE.equals(isField)) {
			value = context.getFieldValue(field, evaluation);
			fieldComparator.setCompareToClass(((JRFillDataset)context).getFillField(field).getValueClass());
		} else {
			value = context.getVariableValue(field, evaluation);
			fieldComparator.setCompareToClass(((JRFillDataset)context).getFillVariable(field).getValueClass());
		}

		fieldComparator.setValueStart(filterValueStart);
		fieldComparator.setValueEnd(filterValueEnd);
		fieldComparator.setCompareTo(value);
		
		
		if (isValid == null) {
			isValid = fieldComparator.isValid();
		}
		if (!isValid) {
			return true;
		}
		return fieldComparator.compare(filterTypeOperator);
	}

	public String getField() {
		return this.field;
	}

	public String getFilterValueStart() {
		return filterValueStart;
	}

	public void setFilterValueStart(String filterValueStart) {
		this.filterValueStart = filterValueStart;
	}

	public String getFilterValueEnd() {
		return filterValueEnd;
	}

	public void setFilterValueEnd(String filterValueEnd) {
		this.filterValueEnd = filterValueEnd;
	}

	public String getFilterType() {
		return filterType;
	}

	public String getFilterTypeOperator() {
		return filterTypeOperator;
	}

	public void setFilterTypeOperator(String filterTypeOperator) {
		this.filterTypeOperator = filterTypeOperator;
	}

	public FilterTypesEnum getFilterTypeEnum() {
		return filterTypeEnum;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public Boolean getIsField() {
		return isField;
	}
	
	public void setIsField(Boolean isField) {
		this.isField = isField;
	}
	
	public String getFilterPattern() {
		return filterPattern;
	}

	public void setFilterPattern(String filterPattern) {
		this.filterPattern = filterPattern;
	}
	
	public String getLocaleCode() {
		return localeCode;
	}

	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

}
