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
package net.sf.jasperreports.engine.data;

import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class RewindableDataSourceCollection<D extends JRRewindableDataSource> 
	extends DataSourceCollection<D, RewindableDataSourceProvider<D>> 
	implements JRRewindableDataSource
{

	public RewindableDataSourceCollection(List<? extends RewindableDataSourceProvider<D>> dataSources) throws JRException
	{
		super(dataSources);
	}

	@Override
	public void moveFirst() throws JRException
	{
		if (currentDataSource != null)
		{
			// rewind the current data source
			currentDataSource.moveFirst();
		}

		int currentIndex = providerIterator.previousIndex();
		
		// rewind all previous providers
		while (providerIterator.hasPrevious())
		{
			RewindableDataSourceProvider<D> provider = providerIterator.previous();
			provider.rewind();
		}
		
		if (currentIndex > 0)
		{
			// we were not on the first data source, go back
			start();
		}
	}

}
