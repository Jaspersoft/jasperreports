/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.data.random;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 *
 */
public class RandomDataSource implements JRRewindableDataSource {

	private final int count;
	private final Random random;
	private int index;

	public RandomDataSource(int count) {
		this.random = new Random();
		this.count = count;
		this.index = 0;
	}

	@Override
	public boolean next() throws JRException {
		if (index >= count) {
			return false;
		}

		++index;
		return true;
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value;
		Class<?> valueClass = field.getValueClass();
		if (String.class.equals(valueClass)) {
			String name = field.getName();
			if (name.length() > 1000) {
				name = name.substring(0, 1000);
			}
			StringBuilder valueStr = new StringBuilder(name);
			int cnt = 2 + random.nextInt(3);
			for (int i = 0; i < cnt; ++i)
				valueStr.append(" ").append(random.nextInt(100));
			value = valueStr.toString();
		} else if (Float.class.equals(valueClass)) {
			value = 200000 * random.nextFloat();
		} else if (Double.class.equals(valueClass)) {
			value = 200000 * random.nextDouble();
		} else if (Long.class.equals(valueClass)) {
			value = random.nextLong();
		} else if (Integer.class.equals(valueClass)) {
			value = random.nextInt(20);
		} else if (BigInteger.class.equals(valueClass)) {
			value = BigInteger.valueOf(random.nextLong());
		} else if (BigDecimal.class.equals(valueClass)) {
			value = BigDecimal.valueOf(random.nextInt(10000) / 100d);
		} else if (Date.class.equals(valueClass)) {
			value = new Date(System.currentTimeMillis() - random.nextInt(20 * 24 * 60 * 60 * 1000));
		} else if (java.sql.Date.class.equals(valueClass)) {
			value = new java.sql.Date(System.currentTimeMillis() - random.nextInt(20 * 24 * 60 * 60 * 1000));
		} else if (Timestamp.class.equals(valueClass)) {
			value = new Timestamp(System.currentTimeMillis() - random.nextInt(20 * 24 * 60 * 60 * 1000));
		} else if (Boolean.class.equals(valueClass)) {
			value = random.nextInt(2) == 0;
		} else {
			value = null;
		}
		return value;
	}

	@Override
	public void moveFirst() throws JRException {
		index = 0;
	}

}
