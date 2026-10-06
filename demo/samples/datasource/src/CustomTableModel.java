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

import javax.swing.table.AbstractTableModel;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class CustomTableModel extends AbstractTableModel
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	private String[] columnNames = {"the_city", "id", "name", "street"};

	/**
	 *
	 */
	private Object[][] data =
		{
			{"Berne", 9, "James Schneider", "277 Seventh Av."},
			{"Berne", 22, "Bill Ott", "250 - 20th Ave."},
			{"Boston", 32, "Michael Ott", "339 College Av."},
			{"Boston", 23, "Julia Heiniger", "358 College Av."},
			{"Chicago", 39, "Mary Karsen", "202 College Av."},
			{"Chicago", 35, "George Karsen", "412 College Av."},
			{"Chicago", 11, "Julia White", "412 Upland Pl."},
			{"Dallas", 47, "Janet Fuller", "445 Upland Pl."},
			{"Dallas", 43, "Susanne Smith", "2 Upland Pl."},
			{"Dallas", 40, "Susanne Miller", "440 - 20th Ave."},
			{"Dallas", 36, "John Steel", "276 Upland Pl."},
			{"Dallas", 37, "Michael Clancy", "19 Seventh Av."},
			{"Dallas", 19, "Susanne Heiniger", "86 - 20th Ave."},
			{"Dallas", 10, "Anne Fuller", "135 Upland Pl."},
			{"Dallas", 4, "Sylvia Ringer", "365 College Av."},
			{"Dallas", 0, "Laura Steel", "429 Seventh Av."},
			{"Lyon", 38, "Andrew Heiniger", "347 College Av."},
			{"Lyon", 28, "Susanne White", "74 - 20th Ave."},
			{"Lyon", 17, "Laura Ott", "443 Seventh Av."},
			{"Lyon", 2, "Anne Miller", "20 Upland Pl."},
			{"New York", 46, "Andrew May", "172 Seventh Av."},
			{"New York", 44, "Sylvia Ott", "361 College Av."},
			{"New York", 41, "Bill King", "546 College Av."},
			{"Oslo", 45, "Janet May", "396 Seventh Av."},
			{"Oslo", 42, "Robert Ott", "503 Seventh Av."},
			{"Paris", 25, "Sylvia Steel", "269 College Av."},
			{"Paris", 18, "Sylvia Fuller", "158 - 20th Ave."},
			{"Paris", 5, "Laura Miller", "294 Seventh Av."},
			{"San Francisco", 48, "Robert White", "549 Seventh Av."},
			{"San Francisco", 7, "James Peterson", "231 Upland Pl."}
		};


	/**
	 *
	 */
	public CustomTableModel()
	{
	}


	@Override
	public int getColumnCount()
	{
		return this.columnNames.length;
	}


	@Override
	public String getColumnName(int columnIndex)
	{
		return this.columnNames[columnIndex];
	}


	@Override
	public int getRowCount()
	{
		return this.data.length;
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return this.data[rowIndex][columnIndex];
	}


}
