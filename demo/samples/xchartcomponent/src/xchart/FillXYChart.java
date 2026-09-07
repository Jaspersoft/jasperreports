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
package xchart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.component.BaseFillComponent;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillCloneable;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRTemplateImage;
import net.sf.jasperreports.engine.fill.JRTemplatePrintImage;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.util.RendererUtil;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.XYStyler;

/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FillXYChart extends BaseFillComponent implements JRFillCloneable
{

	private final XYChartComponent chart;
	private final FillXYDataset dataset;
	
	private String chartTitle;
	private String xAxisTitle;
	private String yAxisTitle;
	
	public FillXYChart(XYChartComponent chart, JRFillObjectFactory factory)
	{
		this.chart = chart;
		this.dataset = new FillXYDataset(chart.getDataset(), factory);
		factory.registerElementDataset(this.dataset);
	}

	protected boolean isEvaluateNow()
	{
		return chart.getEvaluationTime() == EvaluationTimeEnum.NOW;
	}
	
	@Override
	public void evaluate(byte evaluation) throws JRException
	{
		if (isEvaluateNow())
		{
			 evaluateChart(evaluation);
		}
	}

	protected void evaluateChart(byte evaluation) throws JRException
	{
		chartTitle = JRStringUtil.getString(fillContext.evaluate(chart.getChartTitleExpression(), evaluation));
		xAxisTitle = JRStringUtil.getString(fillContext.evaluate(chart.getXAxisTitleExpression(), evaluation));
		yAxisTitle = JRStringUtil.getString(fillContext.evaluate(chart.getYAxisTitleExpression(), evaluation));
		
		dataset.evaluateDatasetRun(evaluation);
	}

	@Override
	public JRPrintElement fill()
	{
		JRComponentElement element = fillContext.getComponentElement();
		JRTemplateImage templateImage = new JRTemplateImage(fillContext.getElementOrigin(), 
				fillContext.getDefaultStyleProvider());
		templateImage.setStyle(fillContext.getElementStyle());
		
		JRTemplatePrintImage image = new JRTemplatePrintImage(templateImage, printElementOriginator);
		image.setX(element.getX());
		image.setY(fillContext.getElementPrintY());
		image.setWidth(element.getWidth());
		image.setHeight(element.getHeight());

		if (isEvaluateNow())
		{
			copy(image);
		}
		else
		{
			fillContext.registerDelayedEvaluation(image, 
					chart.getEvaluationTime(), chart.getEvaluationGroup());
		}
		
		return image;
	}

	@Override
	public FillPrepareResult prepare(int availableHeight)
	{
		return FillPrepareResult.PRINT_NO_STRETCH;
	}

	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void evaluateDelayedElement(JRPrintElement element, byte evaluation) throws JRException
	{
		evaluateChart(evaluation);
		copy((JRPrintImage) element);
	}

	protected void copy(JRPrintImage image)
	{
		dataset.finishDataset();
		
		JRComponentElement element = fillContext.getComponentElement();
		
	    XYChart xyChart = new XYChartBuilder()
	    		.width(element.getWidth())
	    		.height(element.getHeight())
	    		.title(chartTitle == null ? "" : chartTitle)
	    		.xAxisTitle(xAxisTitle == null ? "" : xAxisTitle)
	    		.yAxisTitle(yAxisTitle == null ? "" : yAxisTitle)
	    		.build();
	    XYStyler styler = xyChart.getStyler();
	    styler.setLegendPosition(Styler.LegendPosition.InsideNE);
	    styler.setAxisTitlesVisible(true);
	    styler.setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
	    styler.setChartBackgroundColor(element.getBackcolor() == null ? Color.WHITE : element.getBackcolor());
	    
	    List<Comparable<?>> xySeriesNames = dataset.getXYSeriesNames();
		Map<Comparable<?>, XYSeriesData> xySeriesMap = dataset.getXYSeriesMap();
		if (xySeriesMap != null && !xySeriesMap.isEmpty())
		{
			int i = 0;
			for (Comparable<?> name : xySeriesNames)
			{
				XYSeriesData data = xySeriesMap.get(name);
				org.knowm.xchart.XYSeries series = xyChart.addSeries(name.toString(), data.getXData(), data.getYData());
				Color color = data.getColor();
				if (color != null)
				{
					series.setLineColor(color);
					styler.getSeriesColors()[i] = color;
					//series.setFillColor(color);
				}
				i++;
			}
		}
		try
		{
			BufferedImage img = BitmapEncoder.getBufferedImage(xyChart);
			Renderable renderable = RendererUtil
					.getInstance(fillContext.getFiller().getJasperReportsContext())
					.getRenderable(img, ImageTypeEnum.PNG, OnErrorTypeEnum.ERROR);
			image.setRenderer(renderable);
		}
		catch(Exception e)
		{
			throw new JRRuntimeException(e);
		}
	}

}
