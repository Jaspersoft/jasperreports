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

import java.io.Serializable;

import net.sf.jasperreports.charts.JRGanttSeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Peter Risko (peter@risko.hu)
 */
public class JRBaseGanttSeries implements JRGanttSeries, Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected JRExpression seriesExpression;
	protected JRExpression taskExpression;
	protected JRExpression subtaskExpression;
	protected JRExpression startDateExpression;
	protected JRExpression endDateExpression;
	protected JRExpression percentExpression;
	protected JRExpression labelExpression;
	protected JRHyperlink itemHyperlink;

	/**
	 *
	 */
	protected JRBaseGanttSeries()
	{
	}


	/**
	 *
	 */
	public JRBaseGanttSeries(JRGanttSeries ganttSeries, ChartsBaseObjectFactory factory)
	{
		JRBaseObjectFactory parentFactory = factory.getParent();
		parentFactory.put(ganttSeries, this);

		seriesExpression = parentFactory.getExpression(ganttSeries.getSeriesExpression());
		taskExpression = parentFactory.getExpression(ganttSeries.getTaskExpression());
		subtaskExpression = parentFactory.getExpression(ganttSeries.getSubtaskExpression());
		startDateExpression = parentFactory.getExpression(ganttSeries.getStartDateExpression());
		endDateExpression = parentFactory.getExpression(ganttSeries.getEndDateExpression());
		percentExpression = parentFactory.getExpression(ganttSeries.getPercentExpression());
		labelExpression = parentFactory.getExpression(ganttSeries.getLabelExpression());
		itemHyperlink = parentFactory.getHyperlink(ganttSeries.getItemHyperlink());
	}


	@Override
	public JRExpression getSeriesExpression()
	{
		return seriesExpression;
	}

	@Override
	public JRExpression getTaskExpression()
	{
		return taskExpression;
	}

	@Override
	public JRExpression getSubtaskExpression()
	{
		return subtaskExpression;
	}

	@Override
	public JRExpression getStartDateExpression()
	{
		return startDateExpression;
	}

	@Override
	public JRExpression getEndDateExpression()
	{
		return endDateExpression;
	}

	@Override
	public JRExpression getPercentExpression()
	{
		return percentExpression;
	}

	@Override
	public JRExpression getLabelExpression()
	{
		return labelExpression;
	}


	@Override
	public JRHyperlink getItemHyperlink()
	{
		return itemHyperlink;
	}


	@Override
	public Object clone()
	{
		JRBaseGanttSeries clone = null;

		try
		{
			clone = (JRBaseGanttSeries)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.seriesExpression = JRCloneUtils.nullSafeClone(seriesExpression);
		clone.taskExpression = JRCloneUtils.nullSafeClone(taskExpression);
		clone.subtaskExpression = JRCloneUtils.nullSafeClone(subtaskExpression);
		clone.startDateExpression = JRCloneUtils.nullSafeClone(startDateExpression);
		clone.endDateExpression = JRCloneUtils.nullSafeClone(endDateExpression);
		clone.percentExpression = JRCloneUtils.nullSafeClone(percentExpression);
		clone.labelExpression = JRCloneUtils.nullSafeClone(labelExpression);
		clone.itemHyperlink = JRCloneUtils.nullSafeClone(itemHyperlink);
		return clone;
	}

}
