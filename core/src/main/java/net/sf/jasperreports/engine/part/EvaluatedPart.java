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
package net.sf.jasperreports.engine.part;

import net.sf.jasperreports.engine.JRPropertiesHolder;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class EvaluatedPart
{
	
	public static final EvaluatedPart NO_PRINT = new EvaluatedPart(false, null, null, null);
	
	private final boolean toPrint;
	private final String partName;
	private final JRPropertiesHolder printPartProperties;
	private final Object evaluatedComponent;
	
	public EvaluatedPart(String partName, JRPropertiesHolder printPartProperties,
			Object evaluatedComponent)
	{
		this(true, partName, printPartProperties, evaluatedComponent);
	}
	
	protected EvaluatedPart(boolean toPrint, 
			String partName, JRPropertiesHolder printPartProperties,
			Object evaluatedComponent)
	{
		this.toPrint = toPrint;
		this.partName = partName;
		this.printPartProperties = printPartProperties;
		this.evaluatedComponent = evaluatedComponent;
	}

	public boolean isToPrint()
	{
		return toPrint;
	}

	public String getPartName()
	{
		return partName;
	}
	
	public JRPropertiesHolder getPrintPartProperties()
	{
		return printPartProperties;
	}

	public Object getEvaluatedComponent()
	{
		return evaluatedComponent;
	}
	
}
