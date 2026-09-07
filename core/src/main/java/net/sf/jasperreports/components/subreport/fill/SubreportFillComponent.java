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
package net.sf.jasperreports.components.subreport.fill;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DatasetRunHolder;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.component.BaseFillComponent;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.fill.BuiltinExpressionEvaluatorFactory;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillComponentElement;
import net.sf.jasperreports.engine.fill.JRFillDatasetRun;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRTemplateFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.VirtualizableFrame;
import net.sf.jasperreports.engine.type.HorizontalPosition;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class SubreportFillComponent extends BaseFillComponent
{
	private static final Log log = LogFactory.getLog(SubreportFillComponent.class);
	
	private final Component subreportComponent;

	protected final JRFillObjectFactory factory;
	protected ComponentFillSubreport fillSubreport;
	
	private boolean filling;

	protected int fillWidth;
	protected int fillWeight;
	private Map<JRStyle, JRTemplateFrame> printFrameTemplates = new HashMap<>();

	public SubreportFillComponent(Component subreportComponent, JRFillObjectFactory factory)
	{
		this.subreportComponent = subreportComponent;
		this.factory = factory;
		
		JRDatasetRun datasetRun = getDatasetRun();
		if (datasetRun != null)
		{
			// we need to do this for return values with derived variables
			JRFillDatasetRun fillDatasetRun = factory.getDatasetRun(getDatasetRun());
			// this is needed for returned variables with evaluationTime=Auto
			factory.registerDatasetRun(fillDatasetRun);
		}
	}

	public SubreportFillComponent(SubreportFillComponent subreportComponent, JRFillCloneFactory factory)
	{
		super(subreportComponent, factory);
		
		this.subreportComponent = subreportComponent.subreportComponent;
		this.factory = subreportComponent.factory;
		
		this.printFrameTemplates = subreportComponent.printFrameTemplates;
	}
	
	protected JRDatasetRun getDatasetRun()
	{
		return subreportComponent instanceof DatasetRunHolder ? ((DatasetRunHolder)subreportComponent).getDatasetRun() : null;
	}

	@Override
	public void evaluate(byte evaluation) throws JRException
	{
		if (filling)
		{
			log.warn("Table fill did not complete, canceling previous table subreport");
			fillSubreport.cancelSubreportFill();
		}
		
		filling = false;
		
		if (!isEmpty())
		{
			createFillSubreport();
			fillSubreport.evaluateSubreport(evaluation);
		}
	}

	protected void createFillSubreport() throws JRException
	{
		ComponentFillSubreportFactory subreportFactory = getFillSubreportFactory();
		if (subreportFactory == null)
		{
			subreportFactory = createFillTableSubreportFactory();
			setFillSubreportFactory(subreportFactory);
		}
		
		fillSubreport = subreportFactory.createFillSubreport();
	}
	
	public abstract ComponentFillSubreportFactory getFillSubreportFactory();
	
	public abstract void setFillSubreportFactory(ComponentFillSubreportFactory subreportFactory);

	public abstract JasperReport getJasperReport(BuiltinExpressionEvaluatorFactory builtinEvaluatorFactory) throws JRException;

	public abstract boolean isEmpty();

	protected ComponentFillSubreportFactory createFillTableSubreportFactory() throws JRException
	{
		BuiltinExpressionEvaluatorFactory builtinEvaluatorFactory = new BuiltinExpressionEvaluatorFactory();
		
		SubreportElementAdapter subreport = 
			new SubreportElementAdapter(
				getDatasetRun(), 
				((JRFillComponentElement)fillContext.getComponentElement()).getParent()
				);
		return 
			new ComponentFillSubreportFactory(
				subreport, 
				getJasperReport(builtinEvaluatorFactory),
				//compiledTableReport,
				builtinEvaluatorFactory
				);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FillPrepareResult prepare(int availableHeight)
	{
		return prepare(availableHeight, true);
	}
	
	@Override
	public FillPrepareResult prepare(int availableHeight, boolean isOverflowAllowed)
	{
		try
		{
			if (isEmpty())
			{
				//no columns to print
				return FillPrepareResult.NO_PRINT_NO_OVERFLOW;
			}
			
			JRTemplatePrintFrame printFrame = new JRTemplatePrintFrame(getFrameTemplate(), printElementOriginator);
			JRLineBox lineBox = printFrame.getLineBox();
			int verticalPadding = lineBox.getTopPadding() + lineBox.getBottomPadding();
			
			FillPrepareResult result = 
				fillSubreport.prepareSubreport(
					availableHeight - verticalPadding, 
					filling,
					isOverflowAllowed
					);
			
			if (verticalPadding != 0)
			{
				result = result.addStretch(verticalPadding);
			}
			
			filling = result.willOverflow();
			return result;
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public JRPrintElement fill()
	{
		JRTemplatePrintFrame printFrame = new JRTemplatePrintFrame(getFrameTemplate(), printElementOriginator);

		JRLineBox lineBox = printFrame.getLineBox();
		
		printFrame.setUUID(fillContext.getComponentElement().getUUID());
		printFrame.setY(fillContext.getElementPrintY());
		printFrame.setHeight(fillSubreport.getContentsStretchHeight() + lineBox.getTopPadding() + lineBox.getBottomPadding());
		
		List<JRStyle> styles = fillSubreport.getSubreportStyles();
		for (JRStyle style : styles)
		{
			try
			{
				fillContext.getFiller().addPrintStyle(style);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		
		List<JROrigin> origins = fillSubreport.getSubreportOrigins();
		for (JROrigin origin : origins)
		{
			fillContext.getFiller().getJasperPrint().addOrigin(origin);
		}
		
		int contentsWidth = fillWidth;
		Collection<JRPrintElement> elements = fillSubreport.getPrintElements();
		if (elements != null)
		{
			if (contentsWidth < fillSubreport.getPrintContentsWidth())
			{
				contentsWidth = fillSubreport.getPrintContentsWidth();
			}
		}
		contentsWidth += lineBox.getLeftPadding() + lineBox.getRightPadding();

		int elementWidth = fillContext.getComponentElement().getWidth();
		
		int xElementsOffset = 0;
		HorizontalPosition horizontalPosition = getHorizontalPosition() == null ? HorizontalPosition.LEFT : getHorizontalPosition();
		switch (horizontalPosition)
		{
			case RIGHT:
			{
				xElementsOffset = elementWidth - contentsWidth;
				break;
			}
			case CENTER:
			{
				xElementsOffset = (elementWidth - contentsWidth) / 2;
				break;
			}
			case LEFT:
			default:
			{
				break;
			}
		}

		int xFrameOffset = 0;
		Boolean shrinkWidth = shrinkWidth() == null ? Boolean.FALSE : shrinkWidth();
		if (contentsWidth < elementWidth && !shrinkWidth)
		{
			contentsWidth = elementWidth;
		}
		else
		{
			xFrameOffset = xElementsOffset;
			xElementsOffset = 0;
		}
		
		printFrame.setX(fillContext.getComponentElement().getX() + xFrameOffset);
		printFrame.setWidth(contentsWidth);

		if (elements != null)
		{
			VirtualizableFrame virtualizableFrame = new VirtualizableFrame(printFrame, 
					fillContext.getFiller().getVirtualizationContext(), 
					fillContext.getFiller().getCurrentPage());
			
			virtualizableFrame.addOffsetElements(elements, xElementsOffset, 0);
			virtualizableFrame.fill();
		}
		
		fillSubreport.subreportPageFilled();
		
		return printFrame;
	}

	protected JRTemplateFrame getFrameTemplate()
	{
		JRStyle style = fillContext.getElementStyle();
		JRTemplateFrame frameTemplate = printFrameTemplates.get(style);
		if (frameTemplate == null)
		{
			frameTemplate = new JRTemplateFrame(
						fillContext.getElementOrigin(),
						fillContext.getDefaultStyleProvider());
			frameTemplate.setElement(fillContext.getComponentElement());
			frameTemplate = deduplicate(frameTemplate);
			
			printFrameTemplates.put(style, frameTemplate);
		}

		return frameTemplate;
	}

	@Override
	public void rewind()
	{
		if (filling)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Rewinding component subreport");
			}
			
			try
			{
				fillSubreport.rewind();
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			
			filling = false;
		}
	}

	protected HorizontalPosition getHorizontalPosition()
	{
		return null;
	}
	
	protected Boolean shrinkWidth()
	{
		return null;
	}
	
	public class ComponentFillSubreportFactory
	{
		private final JRSubreport subreport;
		private final JasperReport jasperReport;
		private final BuiltinExpressionEvaluatorFactory builtinEvaluatorFactory;
		
		public ComponentFillSubreportFactory(
			JRSubreport subreport, 
			JasperReport jasperReport, 
			BuiltinExpressionEvaluatorFactory builtinEvaluatorFactory
			)
		{
			this.subreport = subreport;
			this.jasperReport = jasperReport;
			this.builtinEvaluatorFactory = builtinEvaluatorFactory;
		}

		public ComponentFillSubreport createFillSubreport()
		{
			return 
				new ComponentFillSubreport(
					fillContext, subreport, factory, jasperReport,
					builtinEvaluatorFactory
					);
		}
	}
}
