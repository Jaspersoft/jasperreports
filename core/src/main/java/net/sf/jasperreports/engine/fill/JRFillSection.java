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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRSection;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillSection implements JRSection, JROriginProvider
{

	/**
	 *
	 */
	protected JRBaseFiller filler;

	protected JRFillBand[] bands;

	protected JROrigin origin;

	private boolean isEmpty = true;
	private boolean areAllPrintWhenExprNull = true;

	
	/**
	 *
	 */
	protected JRFillSection(
		JRBaseFiller filler,
		JRSection section,
		JRFillObjectFactory factory
		)
	{
		if (section != null)
		{
			factory.put(section, this);

			isEmpty = true;
			areAllPrintWhenExprNull = true;
			
			JRBand[] jrBands = section.getBands();
			if (jrBands != null && jrBands.length > 0)
			{
				bands = new JRFillBand[jrBands.length];
				for (int i = 0; i < jrBands.length; i++)
				{
					bands[i] = factory.getBand(jrBands[i]);
					isEmpty = isEmpty && bands[i].isEmpty();
					areAllPrintWhenExprNull = areAllPrintWhenExprNull && bands[i].isPrintWhenExpressionNull();
				}
			}
			else
			{
				// use a single missing band for empty sections
				bands = new JRFillBand[]{filler.missingFillBand};
			}
		}
		else
		{
			// use a single missing band for null/missing sections
			bands = new JRFillBand[]{filler.missingFillBand};
		}

		this.filler = filler;
	}

	
	@Override
	public JROrigin getOrigin()
	{
		return origin;
	}

	
	/**
	 *
	 */
	protected void setOrigin(JROrigin origin)
	{
		this.origin = origin;

		if (bands.length > 0)
		{
			for (int i = 0; i < bands.length; i++)
			{
				bands[i].setOrigin(origin);
			}
		}
		
		this.filler.getJasperPrint().addOrigin(origin);//FIXMESECTION detail origin appears even if empty
	}


	public JRFillBand[] getFillBands()
	{
		return bands;
	}
	
	@Override
	public JRBand[] getBands() 
	{
		return bands;
	}
	
	@Override
	public JRPart[] getParts() 
	{
		return null;
	}


	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}


	protected boolean isEmpty()
	{
		return isEmpty;
	}


	protected boolean areAllPrintWhenExpressionsNull()
	{
		return areAllPrintWhenExprNull;
	}


	protected void setNewPageColumn(boolean isNew)
	{
		for (int i = 0; i < bands.length; i++)
		{
			bands[i].setNewPageColumn(isNew);
		}
	}


	protected void setNewGroup(JRGroup group, boolean isNew)
	{
		for (int i = 0; i < bands.length; i++)
		{
			bands[i].setNewGroup(group, isNew);
		}
	}


	protected void addNowEvaluationTime(JREvaluationTime evaluationTime)
	{
		for (int i = 0; i < bands.length; i++)
		{
			bands[i].addNowEvaluationTime(evaluationTime);
		}
	}

}
