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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class XYZElement implements Serializable {

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Comparable<?> series;
	private List<Number> xElements;
	private List<Number> yElements;
	private List<Number> zElements;
	
	public XYZElement(){
		xElements = new ArrayList<>();
		yElements = new ArrayList<>();
		zElements = new ArrayList<>();
	}

	public void setSeries( Comparable<?> series ){
		this.series = series;
	}

	public Comparable<?> getSeries(){
		return series;
	}
	
	public void addElement( Number xElement, Number yElement, Number zElement ){
		xElements.add( xElement );
		yElements.add( yElement );
		zElements.add( zElement );
		
	}

	
	public Number getXElement( int index ){
		return xElements.get( index );
	}
	
	public Number getYElement( int index ){
		return yElements.get( index );
	}
	
	public Number getZElement( int index ){
		return zElements.get( index );
	}
	
	
	public int getCount(){
		int retVal = 0;
		if ( xElements != null ){
			retVal = xElements.size();
		}
		
		return retVal;
	}

}
