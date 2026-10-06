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
package net.sf.jasperreports.components.table;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectInputStream.GetField;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CompiledCell extends CompiledBaseCell implements Cell
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Integer rowSpan;
	
	public CompiledCell()
	{
		super();
	}

	public CompiledCell(Cell cell, JRBaseObjectFactory factory)
	{
		super(cell, factory);
		
		this.rowSpan = cell.getRowSpan();
	}

	@Override
	public Integer getRowSpan()
	{
		return rowSpan;
	}

	@Override
	public Object clone() 
	{
		CompiledCell clone = (CompiledCell) super.clone();
		return clone;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		GetField fields = in.readFields();
		rowSpan = (Integer) fields.get("rowSpan", null);
		
		//CompiledBaseCell fields were originally (until 6.11) in this class
		//read the values from the stream object if present and set them in the parent
		ObjectStreamClass streamClass = fields.getObjectStreamClass();
		if (streamClass.getField("defaultStyleProvider") != null)
		{
			defaultStyleProvider = (JRDefaultStyleProvider) fields.get("defaultStyleProvider", null);
		}
		if (streamClass.getField("style") != null)
		{
			style = (JRStyle) fields.get("style", null);
		}
		if (streamClass.getField("styleNameReference") != null)
		{
			styleNameReference = (String) fields.get("styleNameReference", null);
		}
		if (streamClass.getField("box") != null)
		{
			box = (JRLineBox) fields.get("box", null);
		}
		if (streamClass.getField("height") != null)
		{
			height = (Integer) fields.get("height", null);
		}
		if (streamClass.getField("propertiesMap") != null)
		{
			propertiesMap = (JRPropertiesMap) fields.get("propertiesMap", null);
		}
	}
	
}
