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
package net.sf.jasperreports.charts.base;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRValueDataset;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * An immutable version of a dataset that generates a single value.  A value
 * dataset is suitable for using with charts that show a single value against
 * a potential range, such as meter chart or a thermometer chart.
 *
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRBaseValueDataset extends JRBaseChartDataset implements JRValueDataset
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * The expression that returns the single value contained in this dataset.
	 */
	protected JRExpression valueExpression;


	/**
	 * Construct a new dataset that is a copy of an existing one.
	 *
	 * @param dataset the dataset to copy
	 */
	public JRBaseValueDataset(JRChartDataset dataset)
	{
		super(dataset);
	}


	/**
	 * Constructs a new dataset that is a copy of an existing one, and
	 * register all of the new dataset's expressions with the specified
	 * factory.
	 *
	 * @param dataset the datast to copy
	 * @param factory the factory to register the new dataset's expressions with
	 */
	public JRBaseValueDataset(JRValueDataset dataset, ChartsBaseObjectFactory factory)
	{
		super(dataset, factory);

		JRBaseObjectFactory parentFactory = factory.getParent();

		valueExpression = parentFactory.getExpression(dataset.getValueExpression());
	}


	@Override
	public JRExpression getValueExpression()
	{
		return valueExpression;
	}


	@Override
	public byte getDatasetType() {
		return JRChartDataset.VALUE_DATASET;
	}

	/**
	 * Adds all the expression used by this plot with the specified collector.
	 * All collected expression that are also registered with a factory will
	 * be included with the report is compiled.
	 *
	 * @param collector the expression collector to use
	 */
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void collectExpressions(ChartsExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void validate(ChartsVerifier verifier)
	{
		verifier.verify(this);
	}

	@Override
	public Object clone() 
	{
		JRBaseValueDataset clone = (JRBaseValueDataset)super.clone();
		clone.valueExpression = JRCloneUtils.nullSafeClone(valueExpression);
		return clone;
	}
}
