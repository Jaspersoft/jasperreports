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
package net.sf.jasperreports.types.date;

import java.util.Date;

import net.sf.jasperreports.engine.query.JRQueryClauseContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DateRangeSQLNotEqualClause extends DateRangeBaseSQLEqualityClause
{

	private static final DateRangeSQLNotEqualClause INSTANCE = new DateRangeSQLNotEqualClause();
	
	public static DateRangeSQLNotEqualClause instance()
	{
		return INSTANCE;
	}
	
	protected DateRangeSQLNotEqualClause()
	{
	}

	@Override
	protected void applyDateRange(JRQueryClauseContext queryContext,
			String column, DateRange dateRange)
	{
		StringBuffer queryBuffer = queryContext.queryBuffer();
		if (dateRange == null || dateRange.getStart() == null || dateRange.getEnd() == null)
		{
			queryBuffer.append(column).append(" IS NOT NULL");
		}
		else
		{
			Date start = dateRange.getStart();
			Date end = dateRange.getEnd();
			if (start.equals(end))
			{
				queryBuffer.append(column).append(" <> ?");
				queryContext.addQueryParameter(null, start);
			}
			else
			{
				queryBuffer.append(column).append(" < ? OR ").append(column).append(" > ?");
				queryContext.addQueryParameter(null, start);
				queryContext.addQueryParameter(null, end);
			}
		}
	}

}
