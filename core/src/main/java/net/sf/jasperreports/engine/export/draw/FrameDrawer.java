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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.export.draw;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.ExporterFilter;
import net.sf.jasperreports.engine.export.GenericElementGraphics2DHandler;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterContext;
import net.sf.jasperreports.engine.type.ModeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FrameDrawer extends ElementDrawer<JRPrintFrame>
{
	/**
	 *
	 */
	private static final int ELEMENT_RECTANGLE_PADDING = 3;

	/**
	 *
	 */
	private ExporterFilter filter;
	private Graphics2D grx;
	private LinkedList<Offset> elementOffsetStack = new LinkedList<>();
	private Offset elementOffset;
	private boolean isClip;
	
	/**
	 *
	 */
	private final PrintDrawVisitor drawVisitor;

	private JRGraphics2DExporterContext exporterContext;
	
	/**
	 *
	 */
	public FrameDrawer(
		JasperReportsContext jasperReportsContext,
		ExporterFilter filter,
		PrintDrawVisitor drawVisitor
		)
	{
		super(jasperReportsContext);

		this.filter = filter;
		this.drawVisitor = drawVisitor;
	}
		
	/**
	 * 
	 */
	public FrameDrawer(
		JRGraphics2DExporterContext exporterContext, 
		ExporterFilter filter,
		PrintDrawVisitor drawVisitor
		)
	{
		this(exporterContext.getJasperReportsContext(), filter, drawVisitor);

		this.exporterContext = exporterContext;
	}
		
	
	/**
	 *
	 */
	public void setClip(boolean isClip)
	{
		this.isClip = isClip;
	}
	
	
	@Override
	public void draw(Graphics2D grx, JRPrintFrame frame, int offsetX, int offsetY) throws JRException
	{
		setGraphics(grx);
		
		Shape oldClipShape = null;
		if (isClip)
		{
			oldClipShape = grx.getClip();
			grx.clip(
				new Rectangle(
					frame.getX() + offsetX, 
					frame.getY() + offsetY, 
					frame.getWidth(), 
					frame.getHeight()
					)
				);
		}
		
		if (frame.getMode() == ModeEnum.OPAQUE)
		{
			grx.setColor(frame.getBackcolor());
			grx.fillRect(
				frame.getX() + offsetX, 
				frame.getY() + offsetY, 
				frame.getWidth(),
				frame.getHeight()
				);
		}

		grx.setColor(frame.getForecolor());//FIXMENOW is this needed?

		setFrameElementsOffset(frame, offsetX, offsetY);
		try
		{
			draw(frame.getElements());
		}
		finally
		{
			if (isClip)
			{
				grx.setClip(oldClipShape);
			}
			restoreElementOffsets();
		}
		
		/*   */
		drawBox(grx, frame.getLineBox(), frame, offsetX, offsetY);
	}


	/**
	 *
	 */
	public void draw(Graphics2D grx, Collection<JRPrintElement> elements, int offsetX, int offsetY) throws JRException
	{
		setGraphics(grx);
		
		setElementOffsets(offsetX, offsetY);
		try
		{
			draw(elements);
		}
		finally
		{
			restoreElementOffsets();
		}
	}


	protected void setGraphics(Graphics2D grx)
	{
		this.grx = grx;
		drawVisitor.setGraphics2D(grx);
	}


	/**
	 *
	 */
	private void draw(Collection<JRPrintElement> elements) throws JRException
	{
		if (elements != null && elements.size() > 0)
		{
			Shape clipArea = grx.getClip();
			for (Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
			{
				JRPrintElement element = it.next();
				boolean isGenericElement = element instanceof JRGenericPrintElement;
				JRGenericPrintElement genericElement =  isGenericElement ? (JRGenericPrintElement)element : null;
				GenericElementGraphics2DHandler handler = isGenericElement 
						? (GenericElementGraphics2DHandler)GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(genericElement.getGenericType(), JRGraphics2DExporter.GRAPHICS2D_EXPORTER_KEY)
						: null;
				
				boolean isGenericElementToExport = isGenericElement && handler != null && handler.toExport(genericElement); 
				
				if (
					(filter != null && !filter.isToExport(element))
					|| !clipArea.intersects(
						element.getX() + elementOffset.getX() - ELEMENT_RECTANGLE_PADDING, 
						element.getY() + elementOffset.getY() - ELEMENT_RECTANGLE_PADDING, 
						element.getWidth() + 2 * ELEMENT_RECTANGLE_PADDING, 
						element.getHeight() + 2 * ELEMENT_RECTANGLE_PADDING)
					)
				{
					continue;
				}
				else if (isGenericElementToExport)
				{
					handler.exportElement(exporterContext, genericElement, grx, elementOffset);
				}
				else
				{
					element.accept(drawVisitor, elementOffset);
				}
			}
		}
	}


	/**
	 *
	 */
	private void setFrameElementsOffset(JRPrintFrame frame, int offsetX, int offsetY)
	{	
		setElementOffsets(
			offsetX + frame.getX() + frame.getLineBox().getLeftPadding(), 
			offsetY + frame.getY() + frame.getLineBox().getTopPadding()
			);
	}
	
	
	/**
	 *
	 */
	private void setElementOffsets(int offsetX, int offsetY)
	{
		elementOffsetStack.addLast(elementOffset);
		
		elementOffset = new Offset(offsetX, offsetY);
	}

	
	/**
	 *
	 */
	private void restoreElementOffsets()
	{
		elementOffset = elementOffsetStack.removeLast();
	}

	/**
	 * @return the exporterContext
	 */
	public JRGraphics2DExporterContext getExporterContext()
	{
		return this.exporterContext;
	}

	/**
	 * @return the drawVisitor
	 */
	public PrintDrawVisitor getDrawVisitor()
	{
		return this.drawVisitor;
	}


}
