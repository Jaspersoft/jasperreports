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

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ReferenceMap;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.fill.JRFillCrosstabObjectFactory;
import net.sf.jasperreports.crosstabs.type.CrosstabColumnPositionEnum;
import net.sf.jasperreports.crosstabs.type.CrosstabRowPositionEnum;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleSetter;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.type.ModeEnum;

/**
 * Crosstab cell contents filler.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillCellContents extends JRFillElementContainer implements JRCellContents, JRStyleSetter
{
	public static final String EXCEPTION_MESSAGE_KEY_CANNOT_SHRINK_CONTENTS = "fill.cell.contents.cannot.shrink.contents";

	private final Map<StretchedContents,JRFillCellContents> transformedContentsCache;
	private final Map<BoxContents,JRFillCellContents> boxContentsCache;
	private final JRClonePool clonePool;
	private final JROriginProvider originProvider;
	private final PrintElementOriginator printElementOriginator;
	
	private JRFillCellContents original;
	
	private final JRCellContents parentCell;
	private final String cellType;
	
	private JRLineBox lineBox;
	
	private int height;
	private int width;
	
	private int x;
	private int y;
	private int verticalSpan;
	private CrosstabRowPositionEnum verticalPositionType;
	private int horizontalSpan;
	
	private Map<JRStyle,JRTemplateFrame> templateFrames;
	
	private JRDefaultStyleProvider defaultStyleProvider;
	private JRStyle initStyle;
	private int prepareStretchHeight;
	
	private Map<String, String> printProperties = new HashMap<>();

	public JRFillCellContents(JRBaseFiller filler, JRCellContents cell, String cellType, 
			JRFillCrosstabObjectFactory factory)
	{
		super(filler, cell, factory);
		
		defaultStyleProvider = factory.getDefaultStyleProvider();
		
		parentCell = cell;
		this.cellType = cellType;
		
		
		int elementId = filler.getFillContext().generateFillElementId();
		printElementOriginator = new DefaultPrintElementOriginator(elementId);
		
		lineBox = cell.getLineBox().clone(this);
		
		width = cell.getWidth();
		height = cell.getHeight();
		
		factory.registerDelayedStyleSetter(this, parentCell);
		
		initElements();
		
		initConditionalStyles();
		
		initTemplatesMap();
		
		this.originProvider = factory.getParentOriginProvider();
		setElementOriginProvider(this.originProvider);
		
		transformedContentsCache = new ReferenceMap<>();
		boxContentsCache = new HashMap<>();
		clonePool = new JRClonePool(this, true, true);
	}

	private void initTemplatesMap()
	{
		templateFrames = new HashMap<>();
	}

	protected JRFillCellContents(JRFillCellContents cellContents, JRFillCloneFactory factory)
	{
		super(cellContents, factory);
		
		defaultStyleProvider = cellContents.defaultStyleProvider;
		
		parentCell = cellContents.parentCell;
		cellType = cellContents.cellType;
		printElementOriginator = cellContents.printElementOriginator;
		
		lineBox = cellContents.getLineBox().clone(this);
		
		width = cellContents.width;
		height = cellContents.height;
		
		initStyle = cellContents.initStyle;
		
		initElements();
		
		initConditionalStyles();
		
		this.templateFrames = cellContents.templateFrames;
		
		this.originProvider = cellContents.originProvider;
		
		transformedContentsCache = new ReferenceMap<>();
		boxContentsCache = new HashMap<>();
		clonePool = new JRClonePool(this, true, true);
		
		verticalPositionType = cellContents.verticalPositionType;
	}
	
	@Override
	public Color getBackcolor()
	{
		return parentCell.getBackcolor();
	}

	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	/**
	 * 
	 */
	protected void setBox(JRLineBox box)
	{
		this.lineBox = box;
		
		initTemplatesMap();
	}
	
	@Override
	public int getHeight()
	{
		return height;
	}
	
	
	@Override
	public int getWidth()
	{
		return width;
	}
	
	
	public JRFillCellContents getBoxContents(boolean left, boolean right, boolean top)
	{
		if (lineBox == null)
		{
			return this;
		}
		
		boolean copyLeft = left && lineBox.getLeftPen().getLineWidth() <= 0f && lineBox.getRightPen().getLineWidth() > 0f;
		boolean copyRight = right && lineBox.getRightPen().getLineWidth() <= 0f && lineBox.getLeftPen().getLineWidth() > 0f;
		boolean copyTop = top && lineBox.getTopPen().getLineWidth() <= 0f && lineBox.getBottomPen().getLineWidth() > 0f;
		
		if (!(copyLeft || copyRight || copyTop))
		{
			return this;
		}
		
		Object key = new BoxContents(copyLeft, copyRight, copyTop);
		JRFillCellContents boxContents = boxContentsCache.get(key);
		if (boxContents == null)
		{
			boxContents = (JRFillCellContents) createClone();
			
			JRLineBox newBox = lineBox.clone(this);
			
			if (copyLeft)
			{
				newBox.copyLeftPen(lineBox.getRightPen());
			}
			
			if (copyRight)
			{
				newBox.copyRightPen(lineBox.getLeftPen());
			}
			
			if (copyTop)
			{
				newBox.copyTopPen(lineBox.getBottomPen());
			}
			
			boxContents.setBox(newBox);
			
			boxContentsCache.put((BoxContents)key, boxContents);
		}
		
		return boxContents;
	}
	
	
	public JRFillCellContents getTransformedContents(
			int newWidth, int newHeight,
			CrosstabColumnPositionEnum xPosition, CrosstabRowPositionEnum yPosition) throws JRException
	{
		if ((getHeight() == newHeight) && 
				(getWidth() == newWidth))
		{
			return this;
		}
		
		if (newHeight < getHeight() || newWidth < getWidth())
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_CANNOT_SHRINK_CONTENTS,  
					(Object[])null 
					);
		}
		
		StretchedContents key = new StretchedContents(newWidth, newHeight, xPosition, yPosition);
		
		JRFillCellContents transformedCell = transformedContentsCache.get(key);
		if (transformedCell == null)
		{
			transformedCell = (JRFillCellContents) createClone();
			transformedCell.transform(newWidth, newHeight, xPosition, yPosition);
			
			transformedContentsCache.put(key, transformedCell);
		}
		
		return transformedCell;
	}
	
	
	private void transform(int newWidth, int newHeight, CrosstabColumnPositionEnum xPosition, CrosstabRowPositionEnum yPosition)
	{
		transformElements(newWidth, newHeight, xPosition, yPosition);
		
		width = newWidth;
		height = newHeight;
	}

	private void transformElements(int newWidth, int newHeight, CrosstabColumnPositionEnum xPosition, CrosstabRowPositionEnum yPosition)
	{
		if ((height == newHeight || yPosition == CrosstabRowPositionEnum.TOP) && 
				(width == newWidth || xPosition == CrosstabColumnPositionEnum.LEFT))
		{
			return;
		}

		double scaleX =  -1d;
		int offsetX = 0;
		switch (xPosition)
		{
			case CENTER:
				offsetX = (newWidth - width) / 2;
				break;
			case RIGHT:
				offsetX = newWidth - width;
				break;
			case STRETCH:
				scaleX = ((double) newWidth) / width;
				break;
			default:
		}
		
		double scaleY =  -1d;
		int offsetY = 0;
		switch (yPosition)
		{
			case MIDDLE:
				offsetY = (newHeight - height) / 2;
				break;
			case BOTTOM:
				offsetY = newHeight - height;
				break;
			case STRETCH:
				scaleY = ((double) newHeight) / height;
				break;
			default:
		}
		
		transformElements(getElements(), scaleX, offsetX, scaleY, offsetY);
	}

	private static void transformElements(JRElement[] elements, double scaleX, int offsetX, double scaleY, int offsetY)
	{
		if (elements != null)
		{
			for (int i = 0; i < elements.length; i++)
			{
				JRFillElement element = (JRFillElement) elements[i];
				
				if (scaleX != -1d)
				{
					element.setX((int) (element.getX() * scaleX));
					element.setWidth((int) (element.getWidth() * scaleX));
				}
				
				if (offsetX != 0)
				{
					element.setX(element.getX() + offsetX);
				}				
				
				if (scaleY != -1d)
				{
					element.setY((int) (element.getY() * scaleY));
					element.setHeight((int) (element.getHeight() * scaleY));
				}
				
				if (offsetY != 0)
				{
					element.setY(element.getY() + offsetY);
				}
				
				if (element instanceof JRFrame)
				{
					JRElement[] frameElements = ((JRFrame) element).getElements();
					transformElements(frameElements, scaleX, offsetX, scaleY, offsetY);
				}
			}
		}
	}
	
	
	protected void prepare(int availableHeight) throws JRException
	{
		initFill();
		resetElements();
		
		prepareElements(availableHeight, true);
		
		// store the original stretch height in order to compute the external
		// stretch height
		prepareStretchHeight = getStretchHeight();
	}

	public void setPrintProperty(String name, String value)
	{
		printProperties.put(name, value);
	}
	
	protected JRPrintFrame fill() throws JRException
	{
		stretchElementsToContainer();
		moveBandBottomElements();

		JRTemplatePrintFrame printCell = new JRTemplatePrintFrame(getTemplateFrame(), printElementOriginator);
		//printCell.setUUID();
		printCell.setX(x);
		printCell.setY(y);
		printCell.setWidth(width);
		
		fillElements(printCell);
		
		verticallyPositionElements(printCell);
		
		printCell.setHeight(getPrintHeight());
		
		setCellProperties(printCell);

		return printCell;
	}

	protected void setCellProperties(JRTemplatePrintFrame printCell)
	{
		if (verticalSpan > 1)
		{
			printCell.getPropertiesMap().setProperty(
					JRCellContents.PROPERTY_ROW_SPAN, Integer.toString(verticalSpan));
		}
		
		if (horizontalSpan > 1)
		{
			printCell.getPropertiesMap().setProperty(
					JRCellContents.PROPERTY_COLUMN_SPAN, Integer.toString(horizontalSpan));
		}
		
		// TODO lucianc find a way to put this in templates
		for (Entry<String, String> propEntry : printProperties.entrySet())
		{
			printCell.getPropertiesMap().setProperty(propEntry.getKey(), propEntry.getValue());
		}

		// not transferring cell properties for now
	}

	
	protected JRTemplateFrame getTemplateFrame()
	{
		JRStyle style = getStyle();
		JRTemplateFrame template = templateFrames.get(style);
		if (template == null)
		{
			template = new JRTemplateFrame(getOrigin(), 
					filler.getJasperPrint().getDefaultStyleProvider(), this);
			template.getPropertiesMap().setProperty(
					JRCellContents.PROPERTY_TYPE, cellType);
			
			template = filler.fillContext.deduplicate(template);
			templateFrames.put(style, template);
		}
		return template;
	}

	protected JROrigin getOrigin()
	{
		return originProvider == null ? null : originProvider.getOrigin();
	}
	
	protected void verticallyPositionElements(JRTemplatePrintFrame printCell)
	{
		int positionOffset;
		
		switch (CrosstabRowPositionEnum.getValueOrDefault(verticalPositionType))
		{
			case MIDDLE:
				positionOffset = (getStretchHeight() - prepareStretchHeight) / 2;
				break;
			case BOTTOM:
				positionOffset = getStretchHeight() - prepareStretchHeight;
				break;
			default:
				positionOffset = 0;
				break;
		}
		
		if (positionOffset != 0)
		{
			List<JRPrintElement> printElements = printCell.getElements();
			
			int positionY = getStretchHeight() - positionOffset;
			boolean outside = false;
			for (Iterator<JRPrintElement> it = printElements.iterator(); !outside && it.hasNext();)
			{
				JRPrintElement element = it.next();
				outside = element.getY() + element.getHeight() > positionY;
			}
			
			if (!outside)
			{
				for (Iterator<JRPrintElement> it = printElements.iterator(); it.hasNext();)
				{
					JRPrintElement element = it.next();
					element.setY(element.getY() + positionOffset);
				}
			}
		}
	}

	protected int getPrintHeight()
	{
		return getStretchHeight() + getTopPadding() + getBottomPadding();
	}

	protected void stretchTo(int stretchHeight)
	{
		setStretchHeight(stretchHeight - getTopPadding() - getBottomPadding());
	}
	
	protected static class BoxContents
	{
		final boolean left;
		final boolean right;
		final boolean top;
		final int hashCode;
		
		public BoxContents(boolean left, boolean right, boolean top)
		{
			this.left = left;
			this.right = right;
			this.top = top;
			
			int hash = left ? 1231 : 1237;
			hash = 31*hash + (right ? 1231 : 1237);
			hash = 31*hash + (top ? 1231 : 1237);
			hashCode = hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			
			BoxContents b = (BoxContents) obj;
			
			return  
				b.left == left && b.right == right && b.top == top;
		}

		@Override
		public int hashCode()
		{
			return hashCode;
		}
	}
	
	protected static class StretchedContents
	{
		final int newHeight;
		final int newWidth;
		final int hashCode;
		final CrosstabColumnPositionEnum xPosition;
		final CrosstabRowPositionEnum yPosition;
		
		StretchedContents(
				int newWidth, int newHeight, CrosstabColumnPositionEnum xPosition, CrosstabRowPositionEnum yPosition)
		{
			this.newHeight = newHeight;
			this.newWidth = newWidth;
			this.xPosition = xPosition;
			this.yPosition = yPosition;
			
			int hash = newHeight;
			hash = 31*hash + newWidth;
			hash = 31*hash + xPosition.ordinal();
			hash = 31*hash + yPosition.ordinal();
			hashCode = hash;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			
			StretchedContents s = (StretchedContents) o;
			
			return  
				s.newHeight == newHeight && s.newWidth == newWidth &&
				s.xPosition == xPosition && s.yPosition == yPosition;
		}
		
		@Override
		public int hashCode()
		{
			return hashCode;
		}
	}

	@Override
	protected int getContainerHeight()
	{
		return getHeight() - getTopPadding() - getBottomPadding();
	}

	@Override
	protected int getActualContainerHeight()
	{
		return getContainerHeight(); 
	}
	
	protected int getTopPadding()
	{
		return lineBox == null ? 0 : lineBox.getTopPadding();
	}
	
	protected int getBottomPadding()
	{
		return lineBox == null ? 0 : lineBox.getBottomPadding();
	}

	public JRFillCloneable createClone()
	{
		JRFillCloneFactory factory = new JRFillCloneFactory();
		return createClone(factory);
	}

	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillCellContents(this, factory);
	}
	
	public JRFillCellContents getWorkingClone()
	{
		JRFillCellContents clone = (JRFillCellContents) clonePool.getClone();
		clone.original = this;
		return clone;
	}
	
	public void releaseWorkingClone()
	{
		original.clonePool.releaseClone(this);
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getVerticalSpan()
	{
		return verticalSpan;
	}

	public void setVerticalSpan(int span)
	{
		verticalSpan = span;
	}

	public void setVerticalPositionType(CrosstabRowPositionEnum positionType)
	{
		this.verticalPositionType = positionType;
	}
	
	public int getHorizontalSpan()
	{
		return horizontalSpan;
	}

	public void setHorizontalSpan(int horizontalSpan)
	{
		this.horizontalSpan = horizontalSpan;
	}


	@Override
	protected void evaluate(byte evaluation) throws JRException
	{
		printProperties.clear();
		
		evaluateConditionalStyles(evaluation);
		super.evaluate(evaluation);
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return defaultStyleProvider;
	}

	@Override
	public JRStyle getStyle()
	{
		JRStyle crtStyle = initStyle;
		
		boolean isUsingDefaultStyle = false;

		if (crtStyle == null)
		{
			crtStyle = filler.getDefaultStyle();
			isUsingDefaultStyle = true;
		}

		JRStyle evalStyle = getEvaluatedConditionalStyle(crtStyle);
		
		if (isUsingDefaultStyle && evalStyle == crtStyle)
		{
			evalStyle = null;
		}
		return evalStyle;
	}

	@Override
	protected void initConditionalStyles()
	{
		super.initConditionalStyles();
		collectConditionalStyle(initStyle);
	}

	@Override
	public ModeEnum getMode()
	{
		return parentCell.getMode();
	}

	@Override
	public String getStyleNameReference()
	{
		return null;
	}

	@Override
	public void setStyle(JRStyle style)
	{
		this.initStyle = style;
		collectConditionalStyle(style);
	}

	@Override
	public void setStyleNameReference(String name)
	{
		throw new UnsupportedOperationException("Style name references not allowed at fill time");
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return parentCell.getDefaultLineColor();
	}
	
	@Override
	public boolean hasProperties()
	{
		return parentCell.hasProperties();
	}

	// not doing anything with the properties at fill time
	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return parentCell.getPropertiesMap();
	}
	
	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}
	
	protected void addElement(int index, JRFillElement element)
	{
		children.add(index, element);
		
		// recreate elements array
		this.elements = null;
		getElements();
		initDeepElements();
		
		// we need to reinit elements
		initElements();
		
		// assuming that the element is not deep and that it does not bring new conditional styles
		element.setConditionalStylesContainer(this);
		element.setOriginProvider(originProvider);
	}
	
	protected void addHtmlClass(String className)
	{
		String originalClasses = printProperties.get(HtmlExporter.PROPERTY_HTML_CLASS);
		String newClasses = originalClasses == null ? className : (originalClasses + " " + className);
		setPrintProperty(HtmlExporter.PROPERTY_HTML_CLASS, newClasses);
	}

	@Override
	public boolean isSplitTypePreventInhibited(boolean isTopLevelCall)
	{
		//FIXME implement logic
		return false;
	}

}
