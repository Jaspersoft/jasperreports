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
package net.sf.jasperreports.charts.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.xy.AbstractXYZDataset;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class DefaultXYZDataset extends AbstractXYZDataset 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/**
	 * 
	 */
	List<XYZElement> dataset = null;
	
	/**
	 * 
	 */
	public DefaultXYZDataset()
	{
		dataset = new ArrayList<>();
	}
	
	/**
	 * 
	 */
	public void addValue( Comparable<?> series, Number xValue, Number yValue, Number zValue ){
		boolean found = false;
		for ( Iterator<XYZElement> it = dataset.iterator(); it.hasNext(); ){
			XYZElement element = it.next();
			if ( element.getSeries().equals( series )){
				element.addElement( xValue, yValue, zValue );
				found = true;
			}
		}

		if ( !found ){
			XYZElement element = new XYZElement();
			element.setSeries( series );
			element.addElement( xValue, yValue, zValue );

			dataset.add( element );
		}
	}
	
	/** 
	 *
	 */
	@Override
	public int getSeriesCount() {
		int retVal = 0;
		if ( dataset != null ){
			retVal = dataset.size();
		}
		
		return retVal;
	}

	@Override
	public Number getZ(int series, int index ) {
		Number retVal = null;
		if ( dataset != null ){
			if ( series < getSeriesCount() ){
				XYZElement element = dataset.get( series );
				retVal = element.getZElement( index );
			}
		}
		return retVal;
	}

	@Override
	public int getItemCount(int series ) {
		int retVal = 0;
		if ( dataset != null ){
			if ( series < getSeriesCount() ){
				XYZElement element = dataset.get( series );
				retVal = element.getCount();
			}
		}
		return retVal;
	}

	@Override
	public Number getX(int series, int index ) {
		Number retVal = null;
		if ( dataset != null ){
			if ( series < getSeriesCount() ){
				XYZElement element = dataset.get( series );
				retVal = element.getXElement( index );
			}
		}
		return retVal;
	}
	
	@Override
	public Number getY(int series, int index ) {
		Number retVal = null;
		if ( dataset != null ){
			if ( series < getSeriesCount() ){
				XYZElement element = dataset.get( series );
				retVal = element.getYElement( index );
			}
		}
		return retVal;
	}

	@Override
	public Comparable<?> getSeriesKey(int index) {
		String retVal = null;
		if ( dataset != null ){
			if ( index < getSeriesCount() ){
				XYZElement element = dataset.get( index );
				retVal = element.getSeries().toString();
			}
		}
		return retVal;
	}

}
