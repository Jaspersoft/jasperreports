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
package net.sf.jasperreports.crosstabs.design;

import net.sf.jasperreports.crosstabs.JRCrosstabDataset;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;

/**
 * Input crosstab dataset implementation to be used at design time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignCrosstabDataset extends JRDesignElementDataset implements JRCrosstabDataset
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_DATA_PRE_SORTED = "isDataPreSorted";
	
	protected boolean dataPreSorted;

	
	/**
	 * Creates a crosstab dataset.
	 */
	public JRDesignCrosstabDataset()
	{
		super();
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
	}

	@Override
	public boolean isDataPreSorted()
	{
		return dataPreSorted;
	}

	
	/**
	 * Sets the presorted flag for the dataset.
	 * 
	 * @param dataPreSorted whether the input data is presorted
	 * @see JRCrosstabDataset#isDataPreSorted()
	 */
	public void setDataPreSorted(boolean dataPreSorted)
	{
		boolean old = this.dataPreSorted;
		this.dataPreSorted = dataPreSorted;
		getEventSupport().firePropertyChange(PROPERTY_DATA_PRE_SORTED, old, this.dataPreSorted);
	}

}
