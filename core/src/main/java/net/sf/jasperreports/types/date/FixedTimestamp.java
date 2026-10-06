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

import java.sql.Timestamp;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRConstants;

/**
 * <p>Implementation of {@link DateRange} for fixed, non relative timestamp.</p>
 *
 * @author Sergey Prilukin
 */
public class FixedTimestamp extends FixedDate implements TimestampRange 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public FixedTimestamp(String expression) {
		super(expression);
	}

	public FixedTimestamp(String expression, TimeZone timeZone, String datePattern) {
		super(expression, timeZone, datePattern);
	}

	public FixedTimestamp(Timestamp date) {
		super(date);
	}

	@Override
	protected String getDefaultPattern() {
		return TIMESTAMP_PATTERN;
	}

	@Override
	public Timestamp getStart() {
		return new Timestamp(super.getStart().getTime());
	}

	@Override
	public Timestamp getEnd() {
		return new Timestamp(super.getEnd().getTime());
	}
}
