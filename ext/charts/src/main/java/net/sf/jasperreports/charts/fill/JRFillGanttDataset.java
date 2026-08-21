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
package net.sf.jasperreports.charts.fill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.SimpleTimePeriod;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRGanttDataset;
import net.sf.jasperreports.charts.JRGanttSeries;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.charts.util.CategoryLabelGenerator;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.util.Pair;

/**
 * @author Peter Risko (peter@risko.hu)
 */
public class JRFillGanttDataset extends JRFillChartDataset implements JRGanttDataset {

	/**
	 *
	 */
	protected JRFillGanttSeries[] ganttSeries;

	private List<Comparable<?>> seriesNames;
	private Map<Comparable<?>, TaskSeries> seriesMap;
	private Map<Comparable<?>, Map<Comparable<?>, String>> labelsMap;

	private Map<Comparable<?>, Map<Pair, JRPrintHyperlink>> itemHyperlinks;


	/**
	 *
	 */
	public JRFillGanttDataset(JRGanttDataset ganttDataset, ChartsFillObjectFactory factory)
	{
		super(ganttDataset, factory.getParent());

		/*   */
		JRGanttSeries[] srcGanttSeries = ganttDataset.getSeries();
		if (srcGanttSeries != null && srcGanttSeries.length > 0)
		{
			ganttSeries = new JRFillGanttSeries[srcGanttSeries.length];
			for (int i = 0; i < ganttSeries.length; i++)
			{
				ganttSeries[i] = (JRFillGanttSeries)factory.getGanttSeries(srcGanttSeries[i]);
			}
		}
	}


	@Override
	public JRGanttSeries[] getSeries()
	{
		return ganttSeries;
	}


	@Override
	protected void customInitialize()
	{
		seriesNames = null;
		seriesMap = null;
		labelsMap = null;
		itemHyperlinks = null;
	}


	@Override
	protected void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		if (ganttSeries != null && ganttSeries.length > 0)
		{
			for (int i = 0; i < ganttSeries.length; i++)
			{
				ganttSeries[i].evaluate(calculator);
			}
		}
	}


	@Override
	protected void customIncrement()
	{
		if (ganttSeries != null && ganttSeries.length > 0)
		{
			if (seriesNames == null)
			{
				seriesNames = new ArrayList<>();
				seriesMap = new HashMap<>();
				labelsMap = new HashMap<>();
				itemHyperlinks = new HashMap<>();
			}

			for (int i = 0; i < ganttSeries.length; i++)
			{
				JRFillGanttSeries crtGanttSeries = ganttSeries[i];

				Comparable<?> seriesName = crtGanttSeries.getSeries();
				TaskSeries taskSrs = seriesMap.get(seriesName);
				if (taskSrs == null)
				{
					taskSrs =  new TaskSeries((String)seriesName);
					seriesNames.add(seriesName);
					seriesMap.put(seriesName, taskSrs);
				}

				// create task
				Task task = taskSrs.get(crtGanttSeries.getTask());
				if (task == null) {
					task = new Task(crtGanttSeries.getTask(),
							crtGanttSeries.getStartDate(),
							crtGanttSeries.getEndDate());
					taskSrs.add(task);
				}
				// create subtask
				Task subtask = new Task(crtGanttSeries.getSubtask(),
						crtGanttSeries.getStartDate(),
						crtGanttSeries.getEndDate());
				// NOTE: For correct scaling/plotting JFreeChart needs the subtasks
				//       to be 'inside' of the containing task.
				//       Therefore the earliest subtask startvalue
				//       is set as startvalue for the whole task, and the
				//       latest subtask endvalue set as endvalue for the
				//       whole task.
				if (subtask.getDuration().getStart().before(task.getDuration().getStart())) {
					task.setDuration(new SimpleTimePeriod(subtask.getDuration().getStart(), task.getDuration().getEnd()));
				}
				if (subtask.getDuration().getEnd().after(task.getDuration().getEnd())) {
					task.setDuration(new SimpleTimePeriod(task.getDuration().getStart(), subtask.getDuration().getEnd()));
				}
				Number percent = crtGanttSeries.getPercent();
				if (percent != null)
				{
					subtask.setPercentComplete(percent.doubleValue());
				}
				task.addSubtask(subtask);

				if (crtGanttSeries.getLabelExpression() != null)
				{
					labelsMap.computeIfAbsent(seriesName, k -> new HashMap<>()).put(crtGanttSeries.getTask(), crtGanttSeries.getLabel());

					// TODO: is it OK like this?
					//seriesLabels.put(crtXySeries.getXValue(), crtXySeries.getLabel());
				}

				if (crtGanttSeries.hasItemHyperlinks())
				{
					itemHyperlinks.computeIfAbsent(seriesName, k -> new HashMap<>())
						.put(new Pair<>(crtGanttSeries.getTask(), crtGanttSeries.getSubtask()), crtGanttSeries.getPrintItemHyperlink());

					// TODO: ?? not sure how to do
					//Pair xyKey = new Pair(crtXySeries.getXValue(), crtXySeries.getYValue());
					//seriesLinks.put(xyKey, crtXySeries.getPrintItemHyperlink());
				}
			}
		}
	}


	@Override
	public Dataset getCustomDataset()
	{
		TaskSeriesCollection dataset = new TaskSeriesCollection();
		if (seriesNames != null)
		{
			for (int i = 0; i < seriesNames.size(); i++)
			{
				Comparable<?> seriesName = seriesNames.get(i);
				dataset.add(seriesMap.get(seriesName));
			}
		}
		return dataset;
	}


	@Override
	public byte getDatasetType() {
		return JRChartDataset.GANTT_DATASET;
	}


	@Override
	public Object getLabelGenerator(){
		return new CategoryLabelGenerator(labelsMap, getLocale());
	}


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


	public Map<Comparable<?>, Map<Pair, JRPrintHyperlink>> getItemHyperlinks()
	{
		return itemHyperlinks;
	}


	public boolean hasItemHyperlinks()
	{
		boolean foundLinks = false;
		if (ganttSeries != null && ganttSeries.length > 0)
		{
			for (int i = 0; i < ganttSeries.length && !foundLinks; i++)
			{
				JRFillGanttSeries series = ganttSeries[i];
				foundLinks = series.hasItemHyperlinks();
			}
		}
		return foundLinks;
	}


	@Override
	public void validate(ChartsVerifier verifier)
	{
		verifier.verify(this);
	}



}
