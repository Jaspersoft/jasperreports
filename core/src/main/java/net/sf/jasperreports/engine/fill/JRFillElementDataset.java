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
package net.sf.jasperreports.engine.fill;

import java.util.Locale;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;


/**
 * Abstract {@link JRElementDataset} implementation used at report fill time.
 * 
 * <p>
 * A dataset implementation usually involves a set of expressions which are
 * used to feed data to some internal data structures.  The collected data is
 * then transformed into report output.
 * An implementation needs to implement abstract methods that initialize,
 * evaluate and increment the dataset.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @see JRFillObjectFactory#registerElementDataset(JRFillElementDataset)
 */
public abstract class JRFillElementDataset implements JRElementDataset
{


	/**
	 *
	 */
	protected JRElementDataset parent;
	private final JRBaseFiller filler;

	protected String resetGroup;
	protected String incrementGroup;

	private boolean isIncremented = true;

	protected JRFillDatasetRun datasetRun;
	private boolean increment;

	/**
	 *
	 */
	protected JRFillElementDataset(
		JRElementDataset dataset, 
		JRFillObjectFactory factory
		)
	{
		factory.put(dataset, this);

		parent = dataset;
		filler = factory.getFiller();//FIXMEBOOK
		
		resetGroup = dataset.getResetGroup();
		incrementGroup = dataset.getIncrementGroup();
		
		datasetRun = factory.getDatasetRun(dataset.getDatasetRun());
	}

	protected JRFillElementDataset(JRFillElementDataset dataset, JRFillCloneFactory factory)
	{
		parent = dataset.parent;
		filler = dataset.filler;
		
		resetGroup = dataset.resetGroup;
		incrementGroup = dataset.incrementGroup;
		
		datasetRun = new JRFillDatasetRun(dataset.datasetRun, factory);
	}
	
	
	public void setBand(JRFillBand band)
	{
		if (datasetRun != null)
		{
			datasetRun.setBand(band);
		}
	}

	@Override
	public DatasetResetTypeEnum getDatasetResetType()
	{
		return parent.getDatasetResetType();
	}
		
	@Override
	public IncrementTypeEnum getIncrementType()
	{
		return IncrementTypeEnum.getValueOrDefault(parent.getIncrementType());
	}
		
	@Override
	public String getResetGroup()
	{
		return resetGroup;
	}
		
	@Override
	public String getIncrementGroup()
	{
		return incrementGroup;
	}
		
	/**
	 *
	 */
	protected JRBaseFiller getFiller()
	{
		return filler;
	}

	protected Locale getLocale()
	{
		return filler.getLocale();
	}
	
	/**
	 *
	 */
	protected TimeZone getTimeZone()
	{
		return filler.getTimeZone();
	}
		
	/**
	 *
	 */
	protected void initialize()
	{
		customInitialize();
		isIncremented = false;
		increment = false;
	}

	/**
	 *
	 */
	protected void evaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		evaluateIncrementWhenExpression(calculator);
		
		if (increment)
		{
			customEvaluate(calculator);
		}

		isIncremented = false;
	}

	
	protected void evaluateIncrementWhenExpression(JRCalculator calculator) throws JRExpressionEvalException
	{
		JRExpression incrementWhenExpression = getIncrementWhenExpression();
		if (incrementWhenExpression == null)
		{
			increment = true;
		}
		else
		{
			Boolean evaluated = (Boolean) calculator.evaluate(incrementWhenExpression);
			increment = evaluated != null && evaluated;
		}
	}


	/**
	 *
	 */
	protected void increment()
	{
		if (!isIncremented && increment)
		{
			customIncrement();
		}
		isIncremented = true;
	}


	/**
	 * Initializes the element dataset.
	 * 
	 * <p>
	 * The dataset is initialized before being used and after each time the
	 * dataset gets reset.
	 * 
	 * @see #getDatasetResetType()
	 */
	protected abstract void customInitialize();

	/**
	 * Evaluates the expressions associated with the dataset.
	 * 
	 * <p>
	 * Usually, the result of the evaluation would be preserved so that it is
	 * used in {@link #customIncrement()}
	 * 
	 * @param calculator used to evaluate expressions
	 * @throws JRExpressionEvalException any exception that occurs while
	 * evaluating expressions
	 */
	protected abstract void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException;

	/**
	 * Increments the dataset by collecting the result of the expression
	 * evaluation.
	 */
	protected abstract void customIncrement();


	@Override
	public JRDatasetRun getDatasetRun()
	{
		return datasetRun;
	}
	
	
	public void evaluateDatasetRun(byte evaluation) throws JRException
	{
		if (datasetRun != null)
		{
			datasetRun.evaluate(this, evaluation);
		}
	}
	
	public JRFillDataset getInputDataset()
	{
		JRFillDataset inputDataset;
		if (datasetRun != null)
		{
			inputDataset = datasetRun.getDataset();
		}
		else
		{
			inputDataset = filler.mainDataset;
		}
		
		return inputDataset;
	}


	@Override
	public JRExpression getIncrementWhenExpression()
	{
		return parent.getIncrementWhenExpression();
	}
	
	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
	
	public JRFillDataset getFillDataset()
	{
		return datasetRun == null ? filler.getMainDataset() : datasetRun.getDataset();
	}
}
