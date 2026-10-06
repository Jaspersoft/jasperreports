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

import java.util.Arrays;
import java.util.Collection;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class CustomBeanFactory
{


	/**
	 *
	 */
	private static CustomBean[] data =
		{
			new CustomBean("Berne", 9, "James Schneider", "277 Seventh Av."),
			new CustomBean("Berne", 22, "Bill Ott", "250 - 20th Ave."),
			new CustomBean("Boston", 23, "Julia Heiniger", "358 College Av."),
			new CustomBean("Boston", 32, "Michael Ott", "339 College Av."),
			new CustomBean("Chicago", 39, "Mary Karsen", "202 College Av."),
			new CustomBean("Chicago", 35, "George Karsen", "412 College Av."),
			new CustomBean("Chicago", 11, "Julia White", "412 Upland Pl."),
			new CustomBean("Dallas", 47, "Janet Fuller", "445 Upland Pl."),
			new CustomBean("Dallas", 43, "Susanne Smith", "2 Upland Pl."),
			new CustomBean("Dallas", 40, "Susanne Miller", "440 - 20th Ave."),
			new CustomBean("Dallas", 36, "John Steel", "276 Upland Pl."),
			new CustomBean("Dallas", 37, "Michael Clancy", "19 Seventh Av."),
			new CustomBean("Dallas", 19, "Susanne Heiniger", "86 - 20th Ave."),
			new CustomBean("Dallas", 10, "Anne Fuller", "135 Upland Pl."),
			new CustomBean("Dallas", 4, "Sylvia Ringer", "365 College Av."),
			new CustomBean("Dallas", 0, "Laura Steel", "429 Seventh Av."),
			new CustomBean("Lyon", 38, "Andrew Heiniger", "347 College Av."),
			new CustomBean("Lyon", 28, "Susanne White", "74 - 20th Ave."),
			new CustomBean("Lyon", 17, "Laura Ott", "443 Seventh Av."),
			new CustomBean("Lyon", 2, "Anne Miller", "20 Upland Pl."),
			new CustomBean("New York", 46, "Andrew May", "172 Seventh Av."),
			new CustomBean("New York", 44, "Sylvia Ott", "361 College Av."),
			new CustomBean("New York", 41, "Bill King", "546 College Av."),
			new CustomBean("Oslo", 45, "Janet May", "396 Seventh Av."),
			new CustomBean("Oslo", 42, "Robert Ott", "503 Seventh Av."),
			new CustomBean("Paris", 25, "Sylvia Steel", "269 College Av."),
			new CustomBean("Paris", 18, "Sylvia Fuller", "158 - 20th Ave."),
			new CustomBean("Paris", 5, "Laura Miller", "294 Seventh Av."),
			new CustomBean("San Francisco", 48, "Robert White", "549 Seventh Av."),
			new CustomBean("San Francisco", 7, "James Peterson", "231 Upland Pl.")
		};  
			

	/**
	 *
	 */
	public static Object[] getBeanArray()
	{
		return data;
	}


	/**
	 *
	 */
	public static Collection<CustomBean> getBeanCollection()
	{
		return Arrays.asList(data);
	}


}
