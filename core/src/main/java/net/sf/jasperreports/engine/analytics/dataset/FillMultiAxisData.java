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
package net.sf.jasperreports.engine.analytics.dataset;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.analytics.data.MultiAxisDataSource;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillMultiAxisData
{

	private final FillMultiAxisDataset fillDataset;

	public FillMultiAxisData(MultiAxisData data, JRFillObjectFactory factory)
	{
		factory.put(data, this);
		
		this.fillDataset = new FillMultiAxisDataset(data, factory);
	}

	public FillMultiAxisData(FillMultiAxisData data, JRFillCloneFactory factory)
	{
		this.fillDataset = new FillMultiAxisDataset(data.fillDataset, factory);
	}
	
	public void evaluate(byte evaluationType) throws JRException
	{
		fillDataset.evaluateData(evaluationType);
	}
	
	public MultiAxisDataSource getDataSource() throws JRException
	{
		return fillDataset.getDataSource();
	}
}
