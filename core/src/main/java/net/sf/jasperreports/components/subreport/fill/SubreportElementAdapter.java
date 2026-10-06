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
package net.sf.jasperreports.components.subreport.fill;

import java.util.List;
import java.util.ListIterator;

import net.sf.jasperreports.engine.ElementDecorator;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.type.OverflowType;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SubreportElementAdapter extends ElementDecorator implements JRSubreport
{
	
	private final JRDatasetRun datasetRun;
	private final JRSubreportParameter[] parameters;
	private final JRSubreportReturnValue[] returnValues;

	public SubreportElementAdapter(JRDatasetRun datasetRun, JRComponentElement componentElement)
	{
		super(componentElement);
		
		this.datasetRun = datasetRun;
		if (datasetRun == null)
		{
			this.parameters = null;
			this.returnValues = null;
		}
		else
		{
			JRDatasetParameter[] datasetParameters = datasetRun.getParameters();
			if (datasetParameters == null)
			{
				this.parameters = null;
			}
			else
			{
				this.parameters = new JRSubreportParameter[datasetParameters.length];
				for (int i = 0; i < datasetParameters.length; i++)
				{
					JRDatasetParameter datasetParameter = datasetParameters[i];
					SubreportParameterAdapter subreportParameter = 
						new SubreportParameterAdapter(datasetParameter);
					this.parameters[i] = subreportParameter;
				}
			}
			
			List<ReturnValue> datasetReturnValues = datasetRun.getReturnValues();
			if (datasetReturnValues == null || datasetReturnValues.isEmpty())
			{
				returnValues = null;
			}
			else
			{
				returnValues = new JRSubreportReturnValue[datasetReturnValues.size()];
				for (ListIterator<ReturnValue> it = datasetReturnValues.listIterator(); it.hasNext();)
				{
					ReturnValue returnValue = it.next();
					returnValues[it.previousIndex()] = new SubreportReturnValueAdapter(returnValue);
				}
			}
		}
	}

	@Override
	public JRExpression getConnectionExpression()
	{
		return datasetRun == null ? null : datasetRun.getConnectionExpression();
	}

	@Override
	public JRExpression getDataSourceExpression()
	{
		return datasetRun == null ? null : datasetRun.getDataSourceExpression();
	}

	@Override
	public JRExpression getExpression()
	{
		// no report expression
		return null;
	}

	@Override
	public JRSubreportParameter[] getParameters()
	{
		return parameters;
	}

	@Override
	public JRExpression getParametersMapExpression()
	{
		return datasetRun == null ? null : datasetRun.getParametersMapExpression();
	}

	@Override
	public JRSubreportReturnValue[] getReturnValues()
	{
		return returnValues;
	}

	@Override
	public Boolean getUsingCache()
	{
		return Boolean.FALSE;
	}

	@Override
	public Boolean isRunToBottom()
	{
		return false;
	}

	@Override
	public void setRunToBottom(Boolean runToBottom)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUsingCache(Boolean isUsingCache)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public OverflowType getOverflowType()
	{
		// default
		return null;
	}

	@Override
	public void setOverflowType(OverflowType overflowType)
	{
		throw new UnsupportedOperationException();
	}

}
