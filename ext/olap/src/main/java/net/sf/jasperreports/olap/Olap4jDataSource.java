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
package net.sf.jasperreports.olap;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.olap.olap4j.Olap4jResult;

import org.olap4j.CellSet;


/**
 * @author swood
 */
public class Olap4jDataSource extends JROlapDataSource
{
	protected CellSet cellSet = null;

	public Olap4jDataSource(JRDataset dataset, CellSet cellSet)
	{
		super(dataset, new Olap4jResult(cellSet));

		this.cellSet = cellSet;
	}
}
