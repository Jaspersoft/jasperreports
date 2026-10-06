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
package net.sf.jasperreports.crosstabs.fill;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.engine.fill.JRFillCellContents;
import net.sf.jasperreports.engine.fill.JRFillExpressionEvaluator;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JROriginProvider;

/**
 * Factory of fill instances of crosstab internal objects.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillCrosstabObjectFactory extends JRFillObjectFactory
{

	private JROriginProvider parentOriginProvider;

	public JRFillCrosstabObjectFactory(JRFillObjectFactory parent,
			JRFillExpressionEvaluator expressionEvaluator)
	{
		super(parent, expressionEvaluator);
	}
	
	public JROriginProvider getParentOriginProvider()
	{
		return parentOriginProvider;
	}

	public void setParentOriginProvider(JROriginProvider parentOriginProvider)
	{
		this.parentOriginProvider = parentOriginProvider;
	}

	public JRFillCellContents getCell(JRCellContents cell, String cellType)
	{
		JRFillCellContents fillCell = null;

		if (cell != null)
		{
			fillCell = (JRFillCellContents) get(cell);
			if (fillCell == null)
			{
				fillCell = new JRFillCellContents(filler, cell, cellType, this);
			}
		}

		return fillCell;
	}

	public JRFillCrosstabRowGroup getCrosstabRowGroup(JRCrosstabRowGroup group)
	{
		JRFillCrosstabRowGroup fillGroup = null;

		if (group != null)
		{
			fillGroup = (JRFillCrosstabRowGroup) get(group);
			if (fillGroup == null)
			{
				fillGroup = new JRFillCrosstabRowGroup(group, this);
			}
		}

		return fillGroup;
	}

	public JRFillCrosstabColumnGroup getCrosstabColumnGroup(JRCrosstabColumnGroup group)
	{
		JRFillCrosstabColumnGroup fillGroup = null;

		if (group != null)
		{
			fillGroup = (JRFillCrosstabColumnGroup) get(group);
			if (fillGroup == null)
			{
				fillGroup = new JRFillCrosstabColumnGroup(group, this);
			}
		}

		return fillGroup;
	}

	public JRFillCrosstabCell getCrosstabCell(JRCrosstabCell cell)
	{
		JRFillCrosstabCell fillCell = null;

		if (cell != null)
		{
			fillCell = (JRFillCrosstabCell) get(cell);
			if (fillCell == null)
			{
				fillCell = new JRFillCrosstabCell(cell, this);
			}
		}

		return fillCell;
	}

	public JRFillCrosstabMeasure getCrosstabMeasure(JRCrosstabMeasure measure)
	{
		JRFillCrosstabMeasure fillMeasure = null;

		if (measure != null)
		{
			fillMeasure = (JRFillCrosstabMeasure) get(measure);
			if (fillMeasure == null)
			{
				fillMeasure = new JRFillCrosstabMeasure(measure, this);
			}
		}

		return fillMeasure;
	}

}
