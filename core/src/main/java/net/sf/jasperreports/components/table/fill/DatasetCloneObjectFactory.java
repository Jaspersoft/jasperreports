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
package net.sf.jasperreports.components.table.fill;

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * Object factory used to clone a report dataset.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DatasetCloneObjectFactory extends JRBaseObjectFactory
{

	public static JRDataset cloneDataset(JRDataset dataset)
	{
		DatasetCloneObjectFactory factory = new DatasetCloneObjectFactory();
		return factory.getDataset(dataset);
	}
	
	public DatasetCloneObjectFactory()
	{
		super((JRExpressionCollector) null);
	}
	
	@Override
	public JRExpression getExpression(JRExpression expression,
			boolean assignNotUsedId)
	{
		// same expression objects are used in the cloned dataset
		return expression;
	}
	
}
