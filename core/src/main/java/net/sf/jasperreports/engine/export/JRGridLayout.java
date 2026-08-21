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
 * Greg Hilton
 */

package net.sf.jasperreports.engine.export;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRBoxUtil;
import net.sf.jasperreports.engine.util.Pair;

/**
 * Utility class used by grid exporters to create a grid for page layout.
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRGridLayout
{
	private static final Log log = LogFactory.getLog(JRGridLayout.class);
	
	private final ExporterNature nature;
	private final List<JRPrintElement> elementList;
	
	private final Map<GridCellSize, GridCellSize> cellSizes;
	private final Map<GridCellStyle, GridCellStyle> cellStyles;
	private final Map<Pair<GridCellSize, GridCellStyle>, EmptyGridCell> emptyCells;

	private int width;
	private int height;
	private int offsetX;
	private int offsetY;

	private CutsInfo xCuts;
	private CutsInfo yCuts;
	private Grid grid;

	private Map<BoxKey,JRLineBox> boxesCache;
	private boolean hasTopMargin = true;
	private boolean hasBottomMargin = true;
	private boolean hasLeftMargin = true;
	private boolean hasRightMargin = true;
	
	private boolean isNested;
	
	/**
	 * Constructor.
	 *
	 * @param elements the elements that should arranged in a grid
	 * @param width the width available for the grid
	 * @param height the height available for the grid
	 * @param offsetX horizontal element position offset
	 * @param offsetY vertical element position offset
	 */
	public JRGridLayout(
		ExporterNature nature,
		List<JRPrintElement> elements,
		int width,
		int height,
		int offsetX,
		int offsetY
		)
	{
		this(
			nature,
			elements,
			width,
			height,
			offsetX,
			offsetY,
			null //xCuts
			);
	}

	/**
	 * Constructor.
	 *
	 * @param elements the elements that should arranged in a grid
	 * @param width the width available for the grid
	 * @param height the height available for the grid
	 * @param offsetX horizontal element position offset
	 * @param offsetY vertical element position offset
	 * @param xCuts An optional list of pre-calculated X cuts.
	 */
	public JRGridLayout(
		ExporterNature nature,
		List<JRPrintElement> elements,
		int width,
		int height,
		int offsetX,
		int offsetY,
		CutsInfo xCuts
		)
	{
		this.nature = nature;
		this.elementList = elements;
		
		// TODO lucianc cache these across report pages?
		this.cellSizes = new HashMap<>();
		this.cellStyles = new HashMap<>();
		this.emptyCells = new HashMap<>();
		
		this.height = height;
		this.width = width;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.xCuts = xCuts;

		boxesCache = new HashMap<>();

		layoutGrid(null, elements);
	}

	/**
	 * Constructor.
	 *
	 * @param width the width available for the grid
	 * @param height the height available for the grid
	 * @param offsetX horizontal element position offset
	 * @param offsetY vertical element position offset
	 */
	protected JRGridLayout(
		JRGridLayout parent,
		List<JRPrintElement> elements,
		int width,
		int height,
		int offsetX,
		int offsetY,
		PrintElementIndex parentElementIndex
		)
	{
		this.nature = parent.nature;
		this.elementList = parent.elementList;
		
		this.cellSizes = parent.cellSizes;
		this.cellStyles = parent.cellStyles;
		this.emptyCells = parent.emptyCells;
		
		this.height = height;
		this.width = width;
		this.offsetX = offsetX;
		this.offsetY = offsetY;

		//this constructor is called only in nested grids:
		this.isNested = true;

		boxesCache = new HashMap<>();
		
		layoutGrid(parentElementIndex, elements);
	}
	
	public JRPrintElement getElement(PrintElementIndex parentIndex, int index)
	{
		// TODO lucianc keep a cache of current element position?
		JRPrintElement element;
		if (parentIndex == null)
		{
			element = elementList.get(index);
		}
		else
		{
			JRPrintFrame parentFrame = (JRPrintFrame) getElement(parentIndex.getParentIndex(), parentIndex.getIndex());
			element = parentFrame.getElements().get(index);
		}
		return element;
	}

	/**
	 * Constructs the element grid.
	 * @param parentElementIndex 
	 */
	protected void layoutGrid(PrintElementIndex parentElementIndex, List<JRPrintElement> elements)
	{

		boolean createXCuts = (xCuts == null);

		xCuts = createXCuts ? new CutsInfo() : xCuts;
		yCuts = nature.isIgnoreLastRow() ? new CutsInfo(0) : new CutsInfo(height);

		if (!isNested && nature.isIgnorePageMargins()) //FIXMEXLS left and right margins are not ignored when all pages on a single sheet
		{
			// TODO lucianc this is an extra virtualization iteration
			setMargins(elements);

			if (createXCuts)
			{
				if (hasLeftMargin)
				{
					xCuts.removeCutOffset(0);
				}
			}

			if (hasTopMargin)
			{
				yCuts.removeCutOffset(0);
			}
			if (hasBottomMargin)
			{
				yCuts.removeCutOffset(height);
			}
		}

		createCuts(elements, offsetX, offsetY, createXCuts);

		// add a cut at the width if it's a nested grid, or if the right margin
		// is not to be removed and no element goes beyond the width
		if (createXCuts && (isNested
				|| (!(nature.isIgnorePageMargins() && hasRightMargin)
				&& !(xCuts.hasCuts() && xCuts.getLastCutOffset() >= width))))
		{
			xCuts.addCutOffset(width);
		}
		
		xCuts.use();
		yCuts.use();

		int colCount = Math.max(xCuts.size() - 1, 0);
		int rowCount = Math.max(yCuts.size() - 1, 0);

		grid = new Grid(rowCount, colCount);

		for (int row = 0; row < rowCount; row++)
		{
			for (int col = 0; col < colCount; col++)
			{
				GridCellSize size = cellSize(
					xCuts.getCutOffset(col + 1) - xCuts.getCutOffset(col),
					yCuts.getCutOffset(row + 1) - yCuts.getCutOffset(row),
					1,
					1
					);
				grid.set(row, col, emptyCell(size, null));
			}
		}

		setGridElements(parentElementIndex, elements, 
				offsetX, offsetY,
				0, 0, rowCount, colCount);

		width = xCuts.getTotalLength();
		height = yCuts.getTotalLength();
	}
	
	protected GridCellSize cellSize(int width, int height, int colSpan, int rowSpan)
	{
		GridCellSize key = new GridCellSize(width, height, colSpan, rowSpan);
		GridCellSize size = cellSizes.get(key);
		if (size == null)
		{
			size = key;
			cellSizes.put(key, size);
			
			if (log.isTraceEnabled())
			{
				log.trace(this + " added cell size " + size);
			}
		}
		return size;
	}

	protected void createCuts(List<JRPrintElement> elements, int elementOffsetX, int elementOffsetY, boolean createXCuts)
	{
		for (Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
		{
			JRPrintElement element = it.next();
			if (nature.isToExport(element))
			{
				if (createXCuts)
				{
					xCuts.addCutOffset(element.getX() + elementOffsetX);
					xCuts.addCutOffset(element.getX() + element.getWidth() + elementOffsetX);
				}

				yCuts.addCutOffset(element.getY() + elementOffsetY);
				yCuts.addCutOffset(element.getY() + element.getHeight() + elementOffsetY);
				
				JRPrintFrame frame = element instanceof JRPrintFrame ? (JRPrintFrame)element : null;
				if (frame != null && nature.isDeep(frame))
				{
					createCuts(
						frame.getElements(),
						element.getX() + elementOffsetX + frame.getLineBox().getLeftPadding(),
						element.getY() + elementOffsetY + frame.getLineBox().getTopPadding(),
						createXCuts
						);
				}
			}
		}
	}

	protected void setMargins(List<JRPrintElement> elements)
	{
		for (Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
		{
			JRPrintElement element = it.next();
			if (nature.isToExport(element))
			{
				if (hasLeftMargin && element.getX() <= 0)
				{
					hasLeftMargin = false;
				}

				if (hasRightMargin && element.getX() >= width - element.getWidth())
				{
					hasRightMargin = false;
				}

				if (hasTopMargin && element.getY() <= 0)
				{
					hasTopMargin = false;
				}

				if (hasBottomMargin && element.getY() >= height - element.getHeight())
				{
					hasBottomMargin = false;
				}
			}
		}
	}


	protected void setGridElements(PrintElementIndex parentIndex, List<JRPrintElement> elements, 
			int elementOffsetX, int elementOffsetY,
			int startRow, int startCol, int endRow, int endCol)
	{
		for (ListIterator<JRPrintElement> it = elements.listIterator(elements.size()); it.hasPrevious();)
		{
			JRPrintElement element = it.previous();
			int elementIndex = it.nextIndex();
			
			if (nature.isToExport(element))
			{
				int x = element.getX() + elementOffsetX;
				int y = element.getY() + elementOffsetY;

				int col1 = xCuts.indexOfCutOffset(x);
				int row1 = yCuts.indexOfCutOffset(y);
				int col2 = xCuts.indexOfCutOffset(x + element.getWidth());
				int row2 = yCuts.indexOfCutOffset(y + element.getHeight());

				if (!isOverlap(row1, col1, row2, col2))
				{
					JRPrintFrame frame = element instanceof JRPrintFrame ? (JRPrintFrame)element : null;
					if (frame != null && nature.isDeep(frame))
					{
						PrintElementIndex frameIndex = new PrintElementIndex(parentIndex, elementIndex);
						setGridElements(
							frameIndex, frame.getElements(),
							x + frame.getLineBox().getLeftPadding(),
							y + frame.getLineBox().getTopPadding(),
							row1, col1, row2, col2
							);
						
						setFrameCellsStyle(frame, row1, col1, row2, col2);
					}
					else
					{
						setGridElement(element, parentIndex, elementIndex, row1, col1, row2, col2);
					}
				}
			}
		}

		if (nature.isHorizontallyMergeEmptyCells())
		{
			horizontallyMergeEmptyCells(startRow, startCol, endRow, endCol);
		}
	}

	protected EmptyGridCell emptyCell(GridCellSize size, GridCellStyle style)
	{
		Pair<GridCellSize, GridCellStyle> key = new Pair<>(size, style);
		EmptyGridCell cell = emptyCells.get(key);
		if (cell == null)
		{
			cell = new EmptyGridCell(size, style);
			emptyCells.put(key, cell);
			
			if (log.isDebugEnabled())
			{
				log.debug(this + " created empty cell for " + size + " and " + style);
			}
		}
		return cell;
	}
	
	protected void horizontallyMergeEmptyCells(int startRow, int startCol, int endRow, int endCol)
	{
		for (int row = startRow; row < endRow; ++row)
		{
			int startSpan = -1;
			int spanWidth = 0;
			int col = startCol;
			for (; col < endCol; ++col)
			{
				JRExporterGridCell cell = grid.get(row, col);
				if (isEmpty(cell))
				{
					if (startSpan == -1)
					{
						startSpan = col;
					}
					spanWidth += cell.getWidth();
				}
				else
				{
					if (startSpan != -1 && col - startSpan > 1)
					{
						spanEmptyCell(row, startSpan, spanWidth, col - startSpan);
					}
					startSpan = -1;
					spanWidth = 0;
				}
			}
			if (startSpan != -1 && col - startSpan > 1)
			{
				spanEmptyCell(row, startSpan, spanWidth, col - startSpan);
			}
		}
	}

	protected void spanEmptyCell(int row, int col, int spanWidth, int colSpan)
	{
		EmptyGridCell spanCell = (EmptyGridCell) grid.get(row, col);
		GridCellSize newSize = cellSize(spanWidth, spanCell.getHeight(), 
				colSpan, spanCell.getRowSpan());
		grid.set(row, col, emptyCell(newSize, spanCell.getStyle()));
		//TODO set OCCUPIED_CELL?
	}
	
	protected boolean isEmpty(JRExporterGridCell cell)
	{
		return cell.getType() == JRExporterGridCell.TYPE_EMPTY_CELL && ((EmptyGridCell) cell).isEmpty();
	}

	protected boolean isOverlap(int row1, int col1, int row2, int col2)
	{
		boolean isOverlap = false;
		if (nature.isSpanCells())
		{
			is_overlap_out:
			for (int row = row1; row < row2; row++)
			{
				for (int col = col1; col < col2; col++)
				{
					if (!isEmpty(grid.get(row, col)))
					{
						isOverlap = true;
						break is_overlap_out;
					}
				}
			}
		}
		else
		{
			isOverlap = !isEmpty(grid.get(row1, col1));
		}
		return isOverlap;
	}


	protected void setGridElement(JRPrintElement element, PrintElementIndex parentIndex, int elementIndex,
			int row1, int col1, int row2, int col2)
	{
		yCuts.addUsage(row1, Cut.USAGE_NOT_EMPTY);
		xCuts.addUsage(col1, Cut.USAGE_NOT_EMPTY);

		int rowSpan = nature.isSpanCells() ? row2 - row1 : 1;
		int colSpan = nature.isSpanCells() ? col2 - col1 : 1;

		JRExporterGridCell gridCell = new ElementGridCell(
				this,
				parentIndex,
				elementIndex,
				cellSize(
						element.getWidth(),
						element.getHeight(),
						colSpan,
						rowSpan
				));

		nature.setXProperties(xCuts, element, row1, col1, row2, col2);
		nature.setYProperties(yCuts, element, row1, col1, row2, col2);

		if (nature.isSpanCells())
		{
			OccupiedGridCell occupiedGridCell = new OccupiedGridCell(gridCell);
			for (int row = row1; row < row2; row++)
			{
				for (int col = col1; col < col2; col++)
				{
					grid.set(row, col, occupiedGridCell);
				}
				yCuts.addUsage(row, Cut.USAGE_SPANNED);
			}

			for (int col = col1; col < col2; col++)
			{
				xCuts.addUsage(col, Cut.USAGE_SPANNED);
			}
		}

		if (col2 - col1 != 0 && row2 - row1 != 0)
		{
			JRLineBox box = (element instanceof JRBoxContainer)?((JRBoxContainer)element).getLineBox():null;
			gridCell.setStyle(cellStyle(null, null, box));

			if (nature.isBreakBeforeRow(element))
			{
				yCuts.addUsage(row1,  Cut.USAGE_BREAK);
			}
			if (nature.isBreakAfterRow(element))
			{
				yCuts.addUsage(row1 + rowSpan,  Cut.USAGE_BREAK);
			}

			grid.set(row1, col1, gridCell);
		}
	}
	
	protected GridCellStyle cellStyle(Color backcolor, Color forecolor, JRLineBox box)
	{
		if (backcolor == null && forecolor == null && box == null)
		{
			return null;
		}

		GridCellStyle key = new GridCellStyle(backcolor, forecolor, box);
		GridCellStyle style = cellStyles.get(key);
		if (style == null)
		{
			style = key;
			cellStyles.put(key, style);
			
			if (log.isTraceEnabled())
			{
				log.trace(this + " added cell style " + style);
			}
		}
		return style;
	}


	protected void setFrameCellsStyle(JRPrintFrame frame, int row1, int col1, int row2, int col2)
	{
		Color backcolor = frame.getMode() == ModeEnum.OPAQUE ? frame.getBackcolor() : null;

		for (int row = row1; row < row2; row++)
		{
			for (int col = col1; col < col2; col++)
			{
				JRExporterGridCell cell = grid.get(row, col);

				boolean modifiedStyle = false;
				Color cellBackcolor = cell.getBackcolor();
				if (cellBackcolor == null)
				{
					if (frame.getMode() == ModeEnum.OPAQUE)
					{
						cellBackcolor = backcolor;
						modifiedStyle = true;
					}
				}

				Color cellForecolor = cell.getForecolor();
				if (cellForecolor == null)
				{
					cellForecolor = frame.getForecolor();
					modifiedStyle = true;
				}

				boolean keepLeft = col == col1;
				boolean keepRight = col == col2 - cell.getColSpan();
				boolean keepTop = row == row1;
				boolean keepBottom = row == row2 - cell.getRowSpan();

				JRLineBox cellBox = cell.getBox();
				if (keepLeft || keepRight || keepTop || keepBottom)
				{
					BoxKey key = new BoxKey(frame.getLineBox(), cellBox, keepLeft, keepRight, keepTop, keepBottom);
					JRLineBox modBox = boxesCache.get(key);
					if (modBox == null)
					{
						modBox = JRBoxUtil.copyBordersNoPadding(frame.getLineBox(), keepLeft, keepRight, keepTop, keepBottom, cellBox);
						boxesCache.put(key, modBox);
					}

					cellBox = modBox;
					modifiedStyle = true;
				}
				
				if (modifiedStyle)
				{
					GridCellStyle newStyle = cellStyle(cellBackcolor, cellForecolor, cellBox);
					grid.set(row, col, changeStyle(cell, newStyle));
				}
			}
		}
	}
	
	protected JRExporterGridCell changeStyle(JRExporterGridCell cell, GridCellStyle newStyle)
	{
		if (cell.getType() == JRExporterGridCell.TYPE_EMPTY_CELL)
		{
			// empty cells are shared so they should not be modified
			return emptyCell(cell.getSize(), newStyle);
		}
		
		// other types of cells can be modified
		cell.setStyle(newStyle);
		return cell;
	}

	/**
	 * Returns the constructed element grid.
	 *
	 * @return the constructed element grid
	 */
	public Grid getGrid()
	{
		return grid;
	}


	/**
	 * Returns the list of cut points on the X axis for the grid.
	 *
	 * @return the list of cut points on the X axis for the grid
	 */
	public CutsInfo getXCuts()
	{
		return xCuts;
	}


	/**
	 * Returns the list of cut points on the Y axis for the grid.
	 *
	 * @return the list of cut points on the Y axis for the grid
	 */
	public CutsInfo getYCuts()
	{
		return yCuts;
	}


	/**
	 * Returns the width available for the grid.
	 *
	 * @return the width available for the grid
	 */
	public int getWidth()
	{
		return width;
	}


	public int getColumnWidth(int col)
	{
		return xCuts.getCutOffset(col + 1) - xCuts.getCutOffset(col);
	}


	public int getRowHeight(int row)
	{
		return yCuts.getCutOffset(row + 1) - yCuts.getCutOffset(row);
	}


	public int getMaxRowHeight(int rowIndex)
	{
		GridRow row = grid.getRow(rowIndex);
		int maxRowHeight = row.get(0).getHeight();
		int rowSize = row.size();
		for (int col = 0; col < rowSize; col++)
		{
			JRExporterGridCell cell = row.get(col);

			if (cell.getType() != JRExporterGridCell.TYPE_OCCUPIED_CELL)
			{
				if (maxRowHeight < cell.getHeight())
				{
					maxRowHeight = cell.getHeight();
				}
			}
		}
		return maxRowHeight;
	}


	public static int getRowHeight(GridRow row)//FIXMEODT are we still using this?
	{
		JRExporterGridCell firstCell = row.get(0);
		if (firstCell.getRowSpan() == 1 && firstCell.getType() != JRExporterGridCell.TYPE_OCCUPIED_CELL) //quick exit
		{
			return firstCell.getHeight();
		}

		int rowHeight = 0;
		int minSpanIdx = 0;

		int colCount = row.size();

		int col;
		for (col = 0; col < colCount; col++)
		{
			JRExporterGridCell cell = row.get(col);

			if (cell.getType() != JRExporterGridCell.TYPE_OCCUPIED_CELL)
			{
				if (cell.getRowSpan() == 1)
				{
					rowHeight = cell.getHeight();
					break;
				}

				if (cell.getRowSpan() < row.get(minSpanIdx).getRowSpan())
				{
					minSpanIdx = col;
				}
			}
		}

		if (col >= colCount) //no cell with rowSpan = 1 was found, getting the height of the cell with min rowSpan
		{
			rowHeight = row.get(minSpanIdx).getHeight();
		}

		return rowHeight;
	}


	/**
	 * This static method calculates all the X cuts for a list of pages.
	 *
	 * @param jasperPrint
	 *            The JasperPrint document.
	 * @param startPageIndex
	 *            The first page to consider.
	 * @param endPageIndex
	 *            The last page to consider.
	 * @param offsetX
	 *            horizontal element position offset
	 */
	public static CutsInfo calculateXCuts(ExporterNature nature, JasperPrint jasperPrint, int startPageIndex, int endPageIndex, int offsetX)
	{
		CutsInfo xCuts = new CutsInfo();

		List<JRPrintPage> pages = jasperPrint.getPages();
		for (int pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
		{
			JRPrintPage page = pages.get(pageIndex);
			addXCuts(nature, page.getElements(), offsetX, xCuts);
		}

		// add a cut at the page width if there are not parts and if no element goes beyond the page width
		if (!jasperPrint.hasParts())
		{
			int width = jasperPrint.getPageWidth();
			int lastCut = xCuts.getLastCutOffset();
			if (lastCut < width)
			{
				xCuts.addCutOffset(width);
			}
		}

		return xCuts;
	}


	/**
	 * This static method calculates the X cuts for a list of print elements and
	 * stores them in the list indicated by the xCuts parameter.
	 *
	 * @param elementsList
	 *            The list of elements to be used to determine the X cuts.
	 * @param elementOffsetX
	 *            horizontal element position offset
	 * @param xCuts
	 *            The list to which the X cuts are to be added.
	 */
	protected static void addXCuts(ExporterNature nature, List<JRPrintElement> elementsList, int elementOffsetX, CutsInfo xCuts)
	{
		for (Iterator<JRPrintElement> it = elementsList.iterator(); it.hasNext();)
		{
			JRPrintElement element = it.next();

			if (nature.isToExport(element))
			{
				xCuts.addCutOffset(element.getX() + elementOffsetX);
				xCuts.addCutOffset(element.getX() + element.getWidth() + elementOffsetX);
				
				if (element instanceof JRPrintFrame)
				{
					JRPrintFrame frame = (JRPrintFrame) element;
					addXCuts(
						nature,
						frame.getElements(),
						element.getX() + elementOffsetX + frame.getLineBox().getLeftPadding(),
						xCuts
						);
				}
				
				nature.setXProperties(xCuts.getPropertiesMap(), element);
			}
		}
	}
	
	
	/**
	 *
	 */
	protected static class BoxKey
	{
		final JRLineBox box;
		final JRLineBox cellBox;
		final boolean left;
		final boolean right;
		final boolean top;
		final boolean bottom;
		final int hashCode;

		BoxKey(JRLineBox box, JRLineBox cellBox, boolean left, boolean right, boolean top, boolean bottom)
		{
			this.box = box;
			this.cellBox = cellBox;
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;

			int hash = box.hashCode();
			if (cellBox != null)
			{
				hash = 31*hash + cellBox.hashCode();
			}
			hash = 31*hash + (left ? 1231 : 1237);
			hash = 31*hash + (right ? 1231 : 1237);
			hash = 31*hash + (top ? 1231 : 1237);
			hash = 31*hash + (bottom ? 1231 : 1237);
			hashCode = hash;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}

			BoxKey b = (BoxKey) obj;

			return b.box.equals(box) &&
				(b.cellBox == null ? cellBox == null : (cellBox != null && b.cellBox.equals(cellBox))) &&
				b.left == left && b.right == right && b.top == top && b.bottom == bottom;
		}

		@Override
		public int hashCode()
		{
			return hashCode;
		}
	}

}