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
package net.sf.jasperreports.engine.export.tabulator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.export.ExporterFilter;
import net.sf.jasperreports.engine.export.PrintElementIndex;
import net.sf.jasperreports.engine.export.tabulator.TableCell.CellType;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.Bounds;
import net.sf.jasperreports.engine.util.JRBoxUtil;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.export.AccessibilityUtil;
import net.sf.jasperreports.export.type.AccessibilityTagEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Tabulator
{
	private static final Log log = LogFactory.getLog(Tabulator.class);
	public static final String EXCEPTION_MESSAGE_KEY_DROPPING_PARENT_ERROR = "export.tabulator.dropping.parent.error";
	
	private final ExporterFilter filter;
	private final List<? extends JRPrintElement> elements;
	private final boolean isAccessibleHtml;
	
	private Table mainTable;
	
	private ParentCheck parentCheck = new ParentCheck();
	private SpanRangeCheck spanRangeCheck = new SpanRangeCheck();
	private SpanCheck spanCheck = new SpanCheck();
	private CollapseCheck collapseCheck = new CollapseCheck();
	private TableCellCreator tableCellCreator = new TableCellCreator();

	public Tabulator(ExporterFilter filter, List<? extends JRPrintElement> elements, boolean isAccessibleHtml)
	{
		this.filter = filter;
		this.elements = elements;
		this.isAccessibleHtml = isAccessibleHtml;
		
		this.mainTable = new Table(this);
	}

	public void tabulate()
	{
		// TODO lucianc force background as different layer
		layoutElements(elements, mainTable, null, null, 0, 0, null);
	}
	
	public void tabulate(int xOffset, int yOffset)
	{
		layoutElements(elements, mainTable, null, null, xOffset, yOffset, null);
	}

	protected void layoutElements(List<? extends JRPrintElement> elementList, Table table, 
			FrameCell parentCell, PrintElementIndex parentIndex,
			int xOffset, int yOffset, Bounds elementBounds)
	{
		if (log.isTraceEnabled())
		{
			log.trace("laying out " + elements.size() + " elements for parent " + parentCell
					+ " at offsets " + xOffset + ", " + yOffset);
		}
		
		// iterating the list in reverse order so that background band elements come last
		for (ListIterator<? extends JRPrintElement> it = elementList.listIterator(elementList.size()); it.hasPrevious();)
		{
			JRPrintElement element = it.previous();
			if (filter != null && !filter.isToExport(element))
			{
				if (log.isTraceEnabled())
				{
					log.trace("element " + element.getUUID() + " skipped by filter " + element);
				}
				continue;
			}
			
			if (element.getWidth() <= 0 || element.getHeight() <= 0)
			{
				if (log.isDebugEnabled())
				{
					log.debug("element " + element.getUUID() 
							+ " skipped, size " + element.getWidth() + ", " + element.getHeight());
				}
				continue;
			}
			
			if (elementBounds != null && !elementBounds.contains(element.getX(), element.getX() + element.getWidth(), 
					element.getY(), element.getY() + element.getHeight()))
			{
				if (log.isDebugEnabled())
				{
					log.debug("element " + element.getUUID() 
							+ " at [" + element.getX() + "," + (element.getX() + element.getWidth())
							+ "),[" + element.getY() + "," + (element.getY() + element.getHeight())
							+ ") does not fit inside bounds " + elementBounds);
				}
				continue;
			}
			
			placeElement(table, parentCell, xOffset, yOffset, element, parentIndex, it.nextIndex(), true);
		}
	}
	
	protected boolean placeElement(Table table, FrameCell parentCell, 
			int xOffset, int yOffset,
			JRPrintElement element, PrintElementIndex parentIndex, int elementIndex, boolean allowOverlap)
	{
		DimensionRange<Column> colRange = table.columns.getRange(element.getX() + xOffset, 
				element.getX() + element.getWidth() + xOffset);
		DimensionRange<Row> rowRange = table.rows.getRange(element.getY() + yOffset, 
				element.getY() + element.getHeight() + yOffset);
		
		if (log.isTraceEnabled())
		{
			log.trace("placing element " + element.getUUID() + " at " + colRange.start + ", " + colRange.end
					+ ", " + rowRange.start + ", " + rowRange.end);
		}
		
		boolean overlap = false;
		Bounds overlapBounds = new Bounds(colRange.start, colRange.end, rowRange.start, rowRange.end);
		
		JROrigin elementOrigin = element.getOrigin();
		if (parentCell == null // top level element
				&& elementOrigin != null && elementOrigin.getReportName() == null
				// master background element
				// TODO lucianc do something for subreport background bands as well
				&& elementOrigin.getBandType() == BandTypeEnum.BACKGROUND)
		{
			// create a layer as big as the table for the master background band
			SortedSet<Column> userColumns = table.columns.getUserEntries();
			SortedSet<Row> userRows = table.rows.getUserEntries();
			// check if we have something in the table
			if (!userColumns.isEmpty() && !userRows.isEmpty())
			{
				overlapBounds.grow(userColumns.first().startCoord, userColumns.last().endCoord,
						userRows.first().startCoord, userRows.last().endCoord);
				// TODO lucianc avoid the following cell overlap checks
			}
		}
		
		Bounds covered = null;
		Bounds originalBounds;
		
		overlapLoop:
		do
		{
			originalBounds = overlapBounds.cloneBounds();
			
			if (rowRange.start != overlapBounds.getStartY() || rowRange.end != overlapBounds.getEndY())
			{
				rowRange = table.rows.getRange(overlapBounds.getStartY(), overlapBounds.getEndY());
			}
			if (colRange.start != overlapBounds.getStartX() || colRange.end != overlapBounds.getEndX())
			{
				colRange = table.columns.getRange(overlapBounds.getStartX(), overlapBounds.getEndX());
			}
			
			for (Row row : rowRange.rangeSet)
			{
				for (Column col : colRange.rangeSet)
				{
					if (covered != null && covered.contains(col.startCoord, col.endCoord, row.startCoord, row.endCoord))
					{
						//we've been here before
						continue;
					}
					
					Cell cell = row.getCell(col);
					if (!canOverwrite(cell, parentCell))
					{
						overlap = true;
						if (!allowOverlap)
						{
							break overlapLoop;
						}

						// TODO lucianc see if we can avoid some of these checks
						Cell overlapParentCell = overlapParentCell(cell, parentCell);
						Pair<Column, Column> colSpanRange = getColumnSpanRange(table, col, row, overlapParentCell);
						Pair<Row, Row> rowSpanRange = getRowSpanRange(table, col, row, overlapParentCell);
						
						if (log.isTraceEnabled())
						{
							log.trace("found overlap with cell " + cell 
									+ ", overlap parent " + overlapParentCell
									+ ", column span range " + colSpanRange.first().startCoord + " to " + colSpanRange.second().startCoord
									+ ", row span range " + rowSpanRange.first().startCoord + " to " + rowSpanRange.second().startCoord);
						}
						
						overlapBounds.grow(colSpanRange.first().startCoord, colSpanRange.second().startCoord, 
								rowSpanRange.first().startCoord, rowSpanRange.second().startCoord);
					}
				}
			}
			
			covered = originalBounds;
		}
		while (!originalBounds.equals(overlapBounds));
		
		if (!overlap)
		{
			setElementCells(table, parentCell, xOffset, yOffset, element, parentIndex, elementIndex, 
					colRange, rowRange);
			return true;
		}
		
		if (!allowOverlap)
		{
			return false;
		}
		
		placeOverlappedElement(table, parentCell, xOffset, yOffset, 
				element, parentIndex, elementIndex, 
				overlapBounds);
		return true;
	}

	protected void placeOverlappedElement(Table table, FrameCell parentCell, int xOffset, int yOffset, 
			JRPrintElement element, PrintElementIndex parentIndex, int elementIndex, 
			Bounds overlapBounds)
	{
		DimensionRange<Column> overlapColRange = table.columns.getRange(overlapBounds.getStartX(), overlapBounds.getEndX());
		DimensionRange<Row> overlapRowRange = table.rows.getRange(overlapBounds.getStartY(), overlapBounds.getEndY());
		DimensionRange<Column> layeredColRange = table.columns.addEntries(overlapColRange);
		DimensionRange<Row> layeredRowRange = table.rows.addEntries(overlapRowRange);
		
		// TODO lucianc expand existing layered cell if smaller than the current element
		boolean placed = false;
		Cell firstOverlapCell = overlapRowRange.floor.getCell(overlapColRange.floor);
		if (firstOverlapCell instanceof LayeredCell)
		{
			LayeredCell layeredCell = (LayeredCell) firstOverlapCell;
			// get the opposite corner cell
			Column lastCol = table.columns.getEntries().lower(layeredColRange.ceiling);
			Row lastRow = table.rows.getEntries().lower(layeredRowRange.ceiling);
			Cell lastCell = lastRow.getCell(lastCol);
			if (lastCell != null && (layeredCell.equals(lastCell) || layeredCell.accept(spanCheck, lastCell)))
			{
				placed = true;
				placeInLayeredCell(xOffset, yOffset, 
						element, parentIndex, elementIndex, 
						layeredCell, layeredColRange, layeredRowRange);
			}
		}
		
		if (!placed)
		{
			createLayeredCell(table, parentCell, xOffset, yOffset, 
					element, parentIndex, elementIndex, 
					layeredColRange, layeredRowRange);
		}
	}

	protected void placeInLayeredCell(int xOffset, int yOffset, 
			JRPrintElement element, PrintElementIndex parentIndex, int elementIndex,
			LayeredCell layeredCell, DimensionRange<Column> layeredColRange,
			DimensionRange<Row> layeredRowRange)
	{
		// attempt to place it on the first layer
		Table firstLayer = layeredCell.getLayers().get(0);
		boolean placed = placeElement(firstLayer, null, 
				xOffset - layeredColRange.start, yOffset - layeredRowRange.start, 
				element, parentIndex, elementIndex, false);
		if (placed)
		{
			if (log.isTraceEnabled())
			{
				log.trace("placed element on first layer of " + layeredCell);
			}
		}
		else
		{
			// create a new layer
			createOverlappedLayer(xOffset, yOffset, layeredCell, 
					element, parentIndex, elementIndex, 
					layeredColRange, layeredRowRange);
		}
	}

	protected void createLayeredCell(Table table, FrameCell parentCell, int xOffset, int yOffset, 
			JRPrintElement element, PrintElementIndex parentIndex, int elementIndex,
			DimensionRange<Column> layeredColRange, DimensionRange<Row> layeredRowRange)
	{
		if (log.isDebugEnabled())
		{
			log.debug("creating layered cell at " + layeredColRange + ", " + layeredRowRange);
		}
		
		LayeredCell layeredCell = new LayeredCell(parentCell);
		Table firstLayer = new Table(this);
		layeredCell.addLayer(firstLayer);
		moveCellsToLayerTable(parentCell, firstLayer, 
				layeredColRange, layeredRowRange);
		
		setElementCells(layeredColRange, layeredRowRange, layeredCell);
		
		collapseSpanColumns(table, layeredColRange);
		collapseSpanRows(table, layeredRowRange);
		
		createOverlappedLayer(xOffset, yOffset, layeredCell, 
				element, parentIndex, elementIndex, 
				layeredColRange, layeredRowRange);
	}

	protected void createOverlappedLayer(int xOffset, int yOffset, LayeredCell layeredCell, 
			JRPrintElement element, PrintElementIndex parentIndex, int elementIndex,
			DimensionRange<Column> layeredColRange, DimensionRange<Row> layeredRowRange)
	{
		Table overlappedLayer = new Table(this);
		layeredCell.addLayer(overlappedLayer);
		overlappedLayer.columns.addEntry(0);
		//adding the final range entry so that narrower layers do not stretch to 100% in HTML
		overlappedLayer.columns.addEntry(layeredColRange.end - layeredColRange.start);
		overlappedLayer.rows.addEntry(0);
		//not adding final range entry for rows for now, not needed at the moment
		
		int layerXOffset = xOffset - layeredColRange.start;
		int layerYOffset = yOffset - layeredRowRange.start;
		DimensionRange<Column> layerColRange = overlappedLayer.columns.getRange(element.getX() + layerXOffset, 
				element.getX() + element.getWidth() + layerXOffset);
		DimensionRange<Row> layerRowRange = overlappedLayer.rows.getRange(element.getY() + layerYOffset, 
				element.getY() + element.getHeight() + layerYOffset);
		setElementCells(overlappedLayer, null, layerXOffset, layerYOffset, 
				element, parentIndex, elementIndex, 
				layerColRange, layerRowRange);
	}

	protected void setElementCells(Table table, FrameCell parentCell, 
			int xOffset, int yOffset, 
			JRPrintElement element, PrintElementIndex parentIndex, int elementIndex,
			DimensionRange<Column> colRange, DimensionRange<Row> rowRange)
	{
		DimensionRange<Column> elementColRange = table.columns.addEntries(colRange);
		DimensionRange<Row> elementRowRange = table.rows.addEntries(rowRange);
		
		if (element instanceof JRPrintFrame)
		{
			JRPrintFrame frame = (JRPrintFrame) element;
			boolean createNestedTable = false;
			String nestedTableRole = null;
			if (isAccessibleHtml)
			{
				String accessibilityTagProp = JRPropertiesUtil.getOwnProperty(frame, AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG);
				AccessibilityTagEnum accessibilityTag = AccessibilityTagEnum.getByName(accessibilityTagProp);
				createNestedTable = AccessibilityTagEnum.TABLE == accessibilityTag || AccessibilityTagEnum.TABLE_LAYOUT == accessibilityTag;
				nestedTableRole = AccessibilityTagEnum.TABLE_LAYOUT == accessibilityTag ? "none" : null;
			}
			if (createNestedTable)
			{
				Table nestedTable = new Table(this);
				nestedTable.setRole(nestedTableRole);
				DimensionRange<Column> nestedColRange = nestedTable.columns.addEntries(nestedTable.columns.getRange(0, frame.getWidth()));
				DimensionRange<Row> nestedRowRange = nestedTable.rows.addEntries(nestedTable.rows.getRange(0, element.getHeight()));
				layoutFrame(nestedTable, null, 0, 0, parentIndex, elementIndex, nestedColRange, nestedRowRange, frame);
				
				NestedTableCell tableCell = new NestedTableCell(parentCell, nestedTable);
				setElementCells(elementColRange, elementRowRange, tableCell);
			}
			else
			{
				layoutFrame(table, parentCell, 
						xOffset + frame.getX(), yOffset + frame.getY(), 
						parentIndex, elementIndex, 
						elementColRange, elementRowRange, frame);
			}
		}
		else
		{
			ElementCell elementCell = new ElementCell(parentCell, parentIndex, elementIndex);
			setElementCells(elementColRange, elementRowRange, elementCell);
		}
	}

	protected void layoutFrame(Table table, FrameCell parentCell, int xOffset, int yOffset,
			PrintElementIndex parentIndex, int elementIndex, DimensionRange<Column> elementColRange,
			DimensionRange<Row> elementRowRange, JRPrintFrame frame)
	{
		FrameCell frameCell = new FrameCell(parentCell, parentIndex, elementIndex);
		setElementCells(elementColRange, elementRowRange, frameCell);
		
		// go deep in the frame
		PrintElementIndex frameIndex = new PrintElementIndex(parentIndex, elementIndex);
		JRLineBox box = frame.getLineBox();
		layoutElements(frame.getElements(), table, frameCell, frameIndex, 
				xOffset + box.getLeftPadding(), 
				yOffset + box.getTopPadding(),
				new Bounds(0, frame.getWidth()  - box.getLeftPadding() - box.getRightPadding(), 
						0, frame.getHeight() - box.getTopPadding() - box.getBottomPadding()));
	}

	protected boolean canOverwrite(Cell existingCell, FrameCell currentParent)
	{
		if (existingCell == null)
		{
			return true;
		}
		
		if (existingCell instanceof FrameCell)
		{
			return isParent((FrameCell) existingCell, currentParent);
		}
		
		return false;
	}

	protected boolean isParent(FrameCell parent, FrameCell child)
	{
		boolean foundAncestor = false;
		FrameCell ancestor = child;
		while (ancestor != null)
		{
			if (ancestor.equals(parent))
			{
				foundAncestor = true;
				break;
			}
			ancestor = ancestor.getParent();
		}
		return foundAncestor;
	}
	
	protected Cell overlapParentCell(Cell existingCell, FrameCell currentParent)
	{
		LinkedList<FrameCell> existingParents = new LinkedList<>();
		for (FrameCell parent = existingCell.getParent(); parent != null; parent = parent.getParent())
		{
			existingParents.addFirst(parent);
		}
		
		LinkedList<FrameCell> currentParents = new LinkedList<>();
		for (FrameCell parent = currentParent; parent != null; parent = parent.getParent())
		{
			currentParents.addFirst(parent);
		}
		
		Iterator<FrameCell> existingIt = existingParents.iterator();
		Iterator<FrameCell> currentIt = currentParents.iterator();
		while (existingIt.hasNext())
		{
			FrameCell existingParent = existingIt.next();
			FrameCell currentCell = currentIt.hasNext() ? currentIt.next() : null;
			if (currentCell == null || !existingParent.equals(currentCell))
			{
				return existingParent;
			}
		}
		
		return existingCell;
	}
	
	protected Pair<Column, Column> getColumnSpanRange(Table table, Column col, Row row, Cell spanned)
	{
		Column startCol = col;
		for (Column headCol : table.columns.getEntries().headSet(col, false).descendingSet())
		{
			Cell headCell = row.getCell(headCol);
			if (headCell == null || !spanned.accept(spanRangeCheck, headCell))
			{
				break;
			}
			startCol = headCol;
		}
		
		Column endCol = null;
		for (Column tailCol : table.columns.getEntries().tailSet(col))
		{
			endCol = tailCol;
			Cell tailCell = row.getCell(tailCol);
			if (tailCell == null || !spanned.accept(spanRangeCheck, tailCell))
			{
				break;
			}
		}
		assert endCol != null;
		assert startCol.startCoord < endCol.startCoord;
		
		return new Pair<>(startCol, endCol);
	}
	
	protected Pair<Row, Row> getRowSpanRange(Table table, Column col, Row row, Cell spanned)
	{
		Row startRow = row;
		for (Row headRow : table.rows.getEntries().headSet(row, false).descendingSet())
		{
			Cell headCell = headRow.getCell(col);
			if (headCell == null || !spanned.accept(spanRangeCheck, headCell))
			{
				break;
			}
			startRow = headRow;
		}
		
		Row endRow = null;
		for (Row tailRow : table.rows.getEntries().tailSet(row))
		{
			endRow = tailRow;
			Cell tailCell = tailRow.getCell(col);
			if (tailCell == null || !spanned.accept(spanRangeCheck, tailCell))
			{
				break;
			}
		}
		assert endRow != null;
		assert startRow.startCoord < endRow.startCoord;
		
		return new Pair<>(startRow, endRow);
	}

	protected void moveCellsToLayerTable(FrameCell parentCell, Table layerTable,
			DimensionRange<Column> colRange, DimensionRange<Row> rowRange)
	{
		layerTable.columns.addEntry(0, colRange.end - colRange.start);
		layerTable.rows.addEntry(0, rowRange.end - rowRange.start);

		ParentDrop parentDrop = new ParentDrop();
		for (Row row : rowRange.rangeSet)
		{
			for (Column column : colRange.rangeSet)
			{
				Cell cell = row.getCell(column);
				// drop parentCell from the cell's parent
				Cell layerCell = cell == null ? null : cell.accept(parentDrop, parentCell);
				if (layerCell != null)
				{
					// determine how much the original cell spans
					Column lastColSpan = getColumnCellSpan(colRange.rangeSet, column, row, cell).lastEntry;
					Row lastRowSpan = getRowCellSpan(rowRange.rangeSet, column, row, cell).lastEntry;
					
					// create ranges in the layer table
					DimensionRange<Column> cellColRange = layerTable.columns.getRange(
							column.startCoord - colRange.start, 
							lastColSpan.endCoord - colRange.start);
					DimensionRange<Row> cellRowRange = layerTable.rows.getRange(
							row.startCoord - rowRange.start, 
							lastRowSpan.endCoord - rowRange.start);
					
					// add entries for the ranges and set the cell in the layer table
					DimensionRange<Column> cellFinalColRange = layerTable.columns.addEntries(cellColRange);
					DimensionRange<Row> cellFinalRowRange = layerTable.rows.addEntries(cellRowRange);
					setElementCells(cellFinalColRange, cellFinalRowRange, layerCell);
				}
			}
		}
	}

	protected void collapseSpanColumns(Table table, DimensionRange<Column> range)
	{
		List<Pair<Column, Column>> removeList = new ArrayList<>();
		Column prevColumn = null;
		for (Column column : range.rangeSet)
		{
			if (prevColumn == null)
			{
				prevColumn = column;
				continue;
			}
			
			boolean collapse = true;
			for (Row row : table.getRows().getEntries())
			{
				Cell prevCell = row.getCell(prevColumn);
				Cell cell = row.getCell(column);
				boolean collapseCell = prevCell == null ? cell == null
						: (cell != null && prevCell.accept(collapseCheck, cell));
				if (!collapseCell)
				{
					collapse = false;
					break;
				}
			}
			
			if (collapse)
			{
				// removing outside the iteration so that the iterator is not broken
				removeList.add(new Pair<>(column, prevColumn));
			}
			else
			{
				prevColumn = column;
			}
		}
		
		for (Pair<Column, Column> removePair : removeList)
		{
			table.removeColumn(removePair.first(), removePair.second());
		}
	}

	protected void collapseSpanRows(Table table, DimensionRange<Row> range)
	{
		List<Pair<Row, Row>> removeList = new ArrayList<>();
		Row prevRow = null;
		for (Row row : range.rangeSet)
		{
			if (prevRow == null)
			{
				prevRow = row;
				continue;
			}
			
			boolean collapse = true;
			for (Column column : table.getColumns().getEntries())
			{
				Cell prevCell = prevRow.getCell(column);
				Cell cell = row.getCell(column);
				boolean collapseCell = prevCell == null ? cell == null
						: (cell != null && prevCell.accept(collapseCheck, cell));
				if (!collapseCell)
				{
					collapse = false;
					break;
				}
			}
			
			if (collapse)
			{
				// removing outside the iteration so that the iterator is not broken
				removeList.add(new Pair<>(row, prevRow));
			}
			else
			{
				prevRow = row;
			}
		}
		
		for (Pair<Row, Row> removePair : removeList)
		{
			table.removeRow(removePair.first(), removePair.second());
		}
	}

	protected void setElementCells(DimensionRange<Column> elementColRange,
			DimensionRange<Row> elementRowRange, Cell elementCell)
	{
		elementRowRange.floor.setCell(elementColRange.floor, elementCell);
		for (Column col : elementColRange.rangeSet.tailSet(elementColRange.floor, false))
		{
			elementRowRange.floor.setCell(col, elementCell.split());
		}
		
		for (Row row : elementRowRange.rangeSet.tailSet(elementRowRange.floor, false))
		{
			for (Column col : elementColRange.rangeSet)
			{
				row.setCell(col, elementCell.split());
			}
		}
	}

	public void addMargins(int width, int height)
	{
		mainTable.columns.addMargins(width);
		mainTable.rows.addMargins(height);
	}
	
	protected Column columnKey(int startCoord)
	{
		// TODO lucianc cache
		return new Column(startCoord);
	}
	
	protected Row rowKey(int startCoord)
	{
		// TODO lucianc cache
		return new Row(startCoord);
	}
	
	protected void columnSplit(Table table, Column splitCol, Column newCol)
	{
		for (Row row : table.rows.getEntries())
		{
			Cell cell = row.getCell(splitCol);
			if (cell != null)
			{
				Cell splitCell = cell.split();
				row.setCell(newCol, splitCell);
			}
		}
	}
	
	protected void rowSplit(Table table, Row splitRow, Row newRow)
	{
		for (Column col : table.columns.getEntries())
		{
			Cell cell = splitRow.getCell(col);
			if (cell != null)
			{
				Cell splitCell = cell.split();
				newRow.setCell(col, splitCell);
			}
		}
	}

	protected boolean isSplitCell(Cell spanned, Cell cell)
	{
		return (cell instanceof SplitCell) 
				&& ((SplitCell) cell).getSourceCell().equals(spanned);
	}
	
	public Table getTable()
	{
		return mainTable;
	}
	
	public JRPrintElement getCellElement(BaseElementCell cell)
	{
		return getCellElement(cell.getParentIndex(), cell.getElementIndex());
	}
	
	protected JRPrintElement getCellElement(PrintElementIndex parentIndex, int index)
	{
		// TODO lucianc keep a cache of current element position?
		JRPrintElement element;
		if (parentIndex == null)
		{
			element = elements.get(index);
		}
		else
		{
			JRPrintFrame parentFrame = (JRPrintFrame) getCellElement(parentIndex.getParentIndex(), parentIndex.getIndex());
			element = parentFrame.getElements().get(index);
		}
		return element;
	}
	
	protected boolean isParent(FrameCell parent, Cell child)
	{
		if (child == null)
		{
			return false;
		}
		
		return child.accept(parentCheck, parent);
	}

	protected SpanInfo<Column> getColumnCellSpan(TablePosition position, Cell cell)
	{
		return getColumnCellSpan(position.getTable().columns.getEntries(), 
				position.getColumn(), position.getRow(), cell);
	}
	
	protected SpanInfo<Column> getColumnCellSpan(NavigableSet<Column> columns, Column column, Row row, Cell cell)
	{
		int span = 1;
		Column lastCol = column;
		for (Column tailCol : columns.tailSet(column, false))
		{
			Cell tailCell = row.getCell(tailCol);
			if (tailCell == null || !cell.accept(spanCheck, tailCell))
			{
				break;
			}
			++span;
			lastCol = tailCol;
		}
		return new SpanInfo<>(span, lastCol);
	}

	protected SpanInfo<Row> getRowCellSpan(TablePosition position, Cell cell)
	{
		return getRowCellSpan(position.getTable().rows.getEntries(), 
				position.getColumn(), position.getRow(), cell);
	}

	protected SpanInfo<Row> getRowCellSpan(NavigableSet<Row> rows, Column column, Row row, Cell cell)
	{
		int span = 1;
		Row lastRow = row;
		for (Row tailRow : rows.tailSet(row, false))
		{
			Cell tailCell = tailRow.getCell(column);
			if (tailCell == null || !cell.accept(spanCheck, tailCell))
			{
				break;
			}
			++span;
			lastRow = tailRow;
		}
		return new SpanInfo<>(span, lastRow);
	}

	protected FrameCell droppedParent(FrameCell existingParent, FrameCell parent)
	{
		if (existingParent == null)
		{
			// should not happen
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_DROPPING_PARENT_ERROR,
					(Object[])null);
		}
		
		if (existingParent.equals(parent))
		{
			return null;
		}
		
		FrameCell droppedGrandParent = droppedParent(existingParent.getParent(), parent);
		FrameCell droppedParent = new FrameCell(droppedGrandParent, 
				existingParent.getParentIndex(), existingParent.getElementIndex());
		return droppedParent;
	}
	
	public TableCell getTableCell(TablePosition position, Cell cell)
	{
		return cell.accept(tableCellCreator, position);
	}
	
	protected class ParentCheck implements CellVisitor<FrameCell, Boolean, RuntimeException>
	{
		@Override
		public Boolean visit(ElementCell cell, FrameCell parentCell)
		{
			return Tabulator.this.isParent(parentCell, cell.getParent());
		}

		@Override
		public Boolean visit(SplitCell cell, FrameCell parentCell)
		{
			return Tabulator.this.isParent(parentCell, cell.getSourceCell().getParent());
		}

		@Override
		public Boolean visit(FrameCell frameCell, FrameCell parentCell)
		{
			return Tabulator.this.isParent(parentCell, frameCell);
		}

		@Override
		public Boolean visit(LayeredCell layeredCell, FrameCell parentCell)
		{
			return Tabulator.this.isParent(parentCell, layeredCell.getParent());
		}		

		@Override
		public Boolean visit(NestedTableCell nestedTableCell, FrameCell parentCell)
		{
			return Tabulator.this.isParent(parentCell, nestedTableCell.getParent());
		}		
	}
	
	protected class SpanRangeCheck implements CellVisitor<Cell, Boolean, RuntimeException>
	{
		@Override
		public Boolean visit(ElementCell spanned, Cell cell)
		{
			return spanned.equals(cell) || Tabulator.this.isSplitCell(spanned, cell);
		}

		@Override
		public Boolean visit(SplitCell spanned, Cell cell)
		{
			return spanned.getSourceCell().accept(this, cell);
		}

		@Override
		public Boolean visit(FrameCell spanned, Cell cell)
		{
			return Tabulator.this.isParent(spanned, cell);
		}

		@Override
		public Boolean visit(LayeredCell spanned, Cell cell)
		{
			return spanned.equals(cell) || Tabulator.this.isSplitCell(spanned, cell);
		}

		@Override
		public Boolean visit(NestedTableCell spanned, Cell cell)
		{
			return spanned.equals(cell) || Tabulator.this.isSplitCell(spanned, cell);
		}
	}
	
	protected class SpanCheck implements CellVisitor<Cell, Boolean, RuntimeException>
	{
		@Override
		public Boolean visit(ElementCell spanned, Cell cell)
		{
			return Tabulator.this.isSplitCell(spanned, cell);
		}

		@Override
		public Boolean visit(SplitCell spanned, Cell cell)
		{
			return false;
		}

		@Override
		public Boolean visit(FrameCell spanned, Cell cell)
		{
			return false;
		}

		@Override
		public Boolean visit(LayeredCell spanned, Cell cell)
		{
			return Tabulator.this.isSplitCell(spanned, cell);
		}

		@Override
		public Boolean visit(NestedTableCell spanned, Cell cell)
		{
			return Tabulator.this.isSplitCell(spanned, cell);
		}
	}
	
	protected class CollapseCheck implements CellVisitor<Cell, Boolean, RuntimeException>
	{
		@Override
		public Boolean visit(ElementCell spanned, Cell cell)
		{
			return Tabulator.this.isSplitCell(spanned, cell);
		}

		@Override
		public Boolean visit(SplitCell spanned, Cell cell)
		{
			// all element/layered split cells are the same
			return spanned.equals(cell);
		}

		@Override
		public Boolean visit(FrameCell spanned, Cell cell)
		{
			// all frame split cells are the same
			return spanned.equals(cell);
		}

		@Override
		public Boolean visit(LayeredCell spanned, Cell cell)
		{
			return Tabulator.this.isSplitCell(spanned, cell);
		}

		@Override
		public Boolean visit(NestedTableCell spanned, Cell cell)
		{
			return Tabulator.this.isSplitCell(spanned, cell);
		}
	}
	
	protected class ParentDrop implements CellVisitor<FrameCell, Cell, RuntimeException>
	{
		private final Map<FrameCell, FrameCell> parentMapping = new HashMap<>();

		protected FrameCell droppedParent(FrameCell existingParent, FrameCell parent)
		{
			FrameCell droppedParent;
			if (parent == null)
			{
				droppedParent = existingParent;
			}
			else if (existingParent == null)
			{
				droppedParent = null;
			}
			else if (parentMapping.containsKey(existingParent))// we can have nulls as values
			{
				droppedParent = parentMapping.get(existingParent);
			}
			else
			{
				droppedParent = Tabulator.this.droppedParent(existingParent, parent); 
				parentMapping.put(existingParent, droppedParent);
			}
			return droppedParent;
		}
		
		@Override
		public Cell visit(ElementCell cell, FrameCell parent)
		{
			FrameCell droppedParent = droppedParent(cell.getParent(), parent);
			cell.setParent(droppedParent);
			return cell;
		}

		@Override
		public Cell visit(SplitCell cell, FrameCell parent)
		{
			// we're not explicitly moving split cells
			return null;
		}

		@Override
		public Cell visit(FrameCell frameCell, FrameCell parent)
		{
			FrameCell droppedParent = droppedParent(frameCell, parent);
			return droppedParent;
		}

		@Override
		public Cell visit(LayeredCell layeredCell, FrameCell parent)
		{
			FrameCell droppedParent = droppedParent(layeredCell.getParent(), parent);
			layeredCell.setParent(droppedParent);
			return layeredCell;
		}

		@Override
		public Cell visit(NestedTableCell nestedTableCell, FrameCell parent)
		{
			FrameCell droppedParent = droppedParent(nestedTableCell.getParent(), parent);
			nestedTableCell.setParent(droppedParent);
			return nestedTableCell;
		}
	}
	
	protected class TableCellCreator implements CellVisitor<TablePosition, TableCell, RuntimeException>
	{
		@Override
		public TableCell visit(ElementCell cell, TablePosition position)
		{
			JRPrintElement element = getCellElement(cell);
			int colSpan = getColumnCellSpan(position, cell).span;
			int rowSpan = getRowCellSpan(position, cell).span;
			Color backcolor = getElementBackcolor(cell);
			CellType cellType = getCellType(cell);
			
			JRLineBox elementBox = (element instanceof JRBoxContainer) ? ((JRBoxContainer) element).getLineBox() : null;
			JRLineBox box = copyParentBox(cell, element, elementBox, true, true, true, true);
			
			TableCell tableCell = new TableCell(Tabulator.this, position, cell, element, cellType, colSpan, rowSpan, backcolor, box);
			return tableCell;
		}

		@Override
		public TableCell visit(SplitCell cell, TablePosition position)
		{
			// NOP
			return null;
		}

		@Override
		public TableCell visit(FrameCell frameCell, TablePosition position)
		{
			JRPrintElement element = getCellElement(frameCell);
			Color backcolor = getElementBackcolor(frameCell);
			CellType cellType = getCellType(frameCell);
			
			boolean[] borders = getFrameCellBorders(position.getTable(), frameCell,
					position.getColumn(), position.getColumn(),
					position.getRow(), position.getRow());
			JRLineBox box = copyFrameBox(frameCell, (JRPrintFrame) element, null, borders[0], borders[1], borders[2], borders[3]);
			
			return new TableCell(Tabulator.this, position, frameCell, element, cellType, 1, 1, backcolor, box);
		}

		@Override
		public TableCell visit(LayeredCell layeredCell, TablePosition position)
		{
			return createFromParent(layeredCell, position);
		}

		@Override
		public TableCell visit(NestedTableCell cell, TablePosition position) throws RuntimeException
		{
			return createFromParent(cell, position);
		}

		protected TableCell createFromParent(Cell cell, TablePosition position)
		{
			SpanInfo<Column> colSpan = getColumnCellSpan(position, cell);
			SpanInfo<Row> rowSpan = getRowCellSpan(position, cell);
			Color backcolor = getElementBackcolor(cell.getParent());
			CellType cellType = getCellType(cell.getParent()) ;
			
			JRLineBox box = null;
			FrameCell parentCell = cell.getParent();
			if (parentCell != null)
			{
				boolean[] borders = getFrameCellBorders(position.getTable(), parentCell,
						position.getColumn(), colSpan.lastEntry,
						position.getRow(), rowSpan.lastEntry);
				if (borders[0] || borders[1] || borders[2] || borders[3])
				{
					JRPrintFrame parentFrame = (JRPrintFrame) getCellElement(parentCell);
					box = copyFrameBox(parentCell, parentFrame, null, borders[0], borders[1], borders[2], borders[3]);
				}
			}
			
			return new TableCell(Tabulator.this, position, cell, null, cellType, colSpan.span, rowSpan.span, backcolor, box);
		}
		
		protected Color getElementBackcolor(BaseElementCell cell)
		{
			if (cell == null)
			{
				return null;
			}
			
			JRPrintElement element = getCellElement(cell);
			if (element.getMode() == ModeEnum.OPAQUE)
			{
				return element.getBackcolor();
			}
			
			return getElementBackcolor(cell.getParent());
		}
		
		protected CellType getCellType(BaseElementCell cell)
		{
			if (cell == null || !isAccessibleHtml)
			{
				return null;
			}
			
			JRPrintElement element = getCellElement(cell);
			if (element.getPropertiesMap().containsProperty(AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG))
			{
				AccessibilityTagEnum accesibitliTagEnum = 
					AccessibilityTagEnum.getByName(
						element.getPropertiesMap().getProperty(AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG)
						);
				return 
					accesibitliTagEnum == AccessibilityTagEnum.COLUMN_HEADER 
					? CellType.COLUMN_HEADER 
					: (accesibitliTagEnum == AccessibilityTagEnum.ROW_HEADER ? CellType.ROW_HEADER : null);
			}
			else if (element.getPropertiesMap().containsProperty(JRCellContents.PROPERTY_TYPE))
			{
				String cellContentsType = element.getPropertiesMap().getProperty(JRCellContents.PROPERTY_TYPE);
				return 
					JRCellContents.TYPE_COLUMN_HEADER.equals(cellContentsType) 
						|| JRCellContents.TYPE_CROSSTAB_HEADER.equals(cellContentsType)
						|| JRCellContents.TYPE_CROSSTAB_TITLE.equals(cellContentsType)
					? CellType.COLUMN_HEADER
					: (JRCellContents.TYPE_ROW_HEADER.equals(cellContentsType) ? CellType.ROW_HEADER : null);
			}
			
			return getCellType(cell.getParent());
		}
		
		protected JRLineBox copyParentBox(Cell cell, JRPrintElement element, JRLineBox baseBox, 
				boolean keepLeft, boolean keepRight, boolean keepTop, boolean keepBottom)
		{
			FrameCell parentCell = cell.getParent();
			if (parentCell == null)
			{
				return baseBox;
			}
			
			// TODO lucianc check this in the table instead?
			JRPrintFrame parentFrame = (JRPrintFrame) getCellElement(parentCell);
			keepLeft &= element.getX() == 0 && parentFrame.getLineBox().getLeftPadding() == 0;
			keepRight &= (element.getX() + element.getWidth() + parentFrame.getLineBox().getLeftPadding()) == parentFrame.getWidth();
			keepTop &= element.getY() == 0 && parentFrame.getLineBox().getTopPadding() == 0;
			keepBottom &= (element.getY() + element.getHeight() + parentFrame.getLineBox().getTopPadding()) == parentFrame.getHeight();
			
			JRLineBox resultBox = baseBox;
			if (keepLeft || keepRight || keepTop || keepBottom)
			{
				resultBox = copyFrameBox(parentCell, parentFrame, baseBox, 
						keepLeft, keepRight, keepTop, keepBottom);
			}
			return resultBox;
		}
		
		protected boolean[] getFrameCellBorders(Table table, FrameCell cell,
				Column firstCol, Column lastCol,
				Row firstRow, Row lastRow)
		{
			Column prevCol = table.columns.getEntries().lower(firstCol);
			boolean leftBorder = !isParent(cell, firstRow.getCell(prevCol));
			
			Column nextCol = table.columns.getEntries().higher(lastCol);
			boolean rightBorder = !isParent(cell, firstRow.getCell(nextCol));
			
			Row prevRow = table.rows.getEntries().lower(firstRow);
			boolean topBorder = !isParent(cell, prevRow.getCell(firstCol));
			
			Row nextRow = table.rows.getEntries().higher(lastRow);
			boolean bottomBorder = !isParent(cell, nextRow.getCell(firstCol));
			
			return new boolean[]{leftBorder, rightBorder, topBorder, bottomBorder};
		}

		protected JRLineBox copyFrameBox(FrameCell frameCell, JRPrintFrame frame, JRLineBox baseBox, 
				boolean keepLeft, boolean keepRight, boolean keepTop, boolean keepBottom)
		{
			// TODO lucianc cache
			JRLineBox resultBox = JRBoxUtil.copyBordersNoPadding(frame.getLineBox(), 
					keepLeft, keepRight, keepTop, keepBottom, baseBox);
			// recurse
			resultBox = copyParentBox(frameCell, frame, resultBox, keepLeft, keepRight, keepTop, keepBottom);
			return resultBox;
		}
		
	}
	
	protected static class SpanInfo<T extends DimensionEntry>
	{
		protected final int span;
		protected final T lastEntry;
		
		public SpanInfo(int span, T lastEntry)
		{
			this.span = span;
			this.lastEntry = lastEntry;
		}
	}
}