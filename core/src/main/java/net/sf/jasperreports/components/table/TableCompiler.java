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
package net.sf.jasperreports.components.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentCompiler;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRVerifier;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableCompiler implements ComponentCompiler
{

	@Override
	public void collectExpressions(Component component,
			JRExpressionCollector collector)
	{
		TableComponent table = (TableComponent) component;
		
		JRDatasetRun datasetRun = table.getDatasetRun();
		collector.collect(datasetRun);
		
		JRExpressionCollector datasetCollector = collector.getDatasetCollector(
				datasetRun.getDatasetName());
		ColumnExpressionCollector columnCollector = new ColumnExpressionCollector(
				collector, datasetCollector);
		columnCollector.collectColumns(table.getColumns());
		
		RowExpressionCollector rowCollector = new RowExpressionCollector(datasetCollector);
		rowCollector.collectRow(table.getTableHeader());
		rowCollector.collectRow(table.getTableFooter());
		rowCollector.collectGroupRows(table.getGroupHeaders());
		rowCollector.collectGroupRows(table.getGroupFooters());
		rowCollector.collectRow(table.getColumnHeader());
		rowCollector.collectRow(table.getColumnFooter());
		rowCollector.collectRow(table.getDetail());
		
		columnCollector.collectCell(table.getNoData());
	}

	@Override
	public Component toCompiledComponent(Component component,
			JRBaseObjectFactory baseFactory)
	{
		TableComponent table = (TableComponent) component;
		return new StandardTable(table, baseFactory);
	}

	@Override
	public void verify(Component component, JRVerifier verifier)
	{
		TableComponent table = (TableComponent) component;

		JRDatasetRun datasetRun = table.getDatasetRun();
		if (datasetRun == null)
		{
			verifier.addBrokenRule("No list subdataset run set", table);
		}
		else
		{
			verifier.verifyDatasetRun(datasetRun);
		}
		
		List<BaseColumn> columns = table.getColumns();
		if (columns == null || columns.isEmpty())
		{
			verifier.addBrokenRule("No columns defined in the table", table);
		}
		else
		{
			if (!detectLoops(verifier, columns))
			{
				String subdataset = datasetRun == null ? null : datasetRun.getDatasetName();
				if (subdataset != null)
				{
					verifier.pushSubdatasetContext(subdataset);
				}
				try
				{
					verifyColumns(table, verifier);
				}
				finally
				{
					if (subdataset != null)
					{
						verifier.popSubdatasetContext();
					}
				}
				
				verifyColumnHeights(table, verifier);
			}
		}
	}

	protected boolean detectLoops(JRVerifier verifier, List<BaseColumn> columns)
	{
		Set<BaseColumn> parents = new HashSet<>();
		return detectLoops(verifier, columns, parents);
	}

	protected boolean detectLoops(final JRVerifier verifier, List<BaseColumn> columns, 
			final Set<BaseColumn> parents)
	{
		boolean loop = false;
		for (BaseColumn column : columns)
		{
			if (parents.contains(column))
			{
				verifier.addBrokenRule("Table column is its own ancestor", column);
				loop = true;
			}
			else
			{
				loop = column.visitColumn(new ColumnVisitor<Boolean>()
				{
					@Override
					public Boolean visitColumn(Column column)
					{
						return false;
					}

					@Override
					public Boolean visitColumnGroup(ColumnGroup columnGroup)
					{
						parents.add(columnGroup);
						boolean loopDetected = detectLoops(verifier, 
								columnGroup.getColumns(), parents);
						parents.remove(columnGroup);
						return loopDetected;
					}
				});
			}
			
			if (loop)
			{
				break;
			}
		}
		
		return false;
	}

	protected void verifyColumns(final TableComponent table, final JRVerifier verifier)
	{
		ColumnVisitor<Void> columnVerifier = new ColumnVisitor<Void>()
		{
			@Override
			public Void visitColumn(Column column)
			{
				verifyColumn(table, column, verifier);
				return null;
			}

			@Override
			public Void visitColumnGroup(ColumnGroup columnGroup)
			{
				verifyBaseColumn(table, columnGroup, verifier);
				
				List<BaseColumn> subcolumns = columnGroup.getColumns();
				if (subcolumns == null || subcolumns.isEmpty())
				{
					verifier.addBrokenRule("No columns defined in column group", columnGroup);
				}
				else
				{
					int subwidth = 0;
					boolean subwidthValid = true;
					for (BaseColumn column : columnGroup.getColumns())
					{
						column.visitColumn(this);
						
						Integer width = column.getWidth();
						if (width == null)
						{
							subwidthValid = false;
						}
						else
						{
							subwidth += width;
						}
					}
					
					if (subwidthValid && columnGroup.getWidth() != null
							&& columnGroup.getWidth() != subwidth)
					{
						verifier.addBrokenRule("Column group width " + columnGroup.getWidth() 
								+ " does not match sum of subcolumn widths " + subwidth, columnGroup);
					}
				}
				return null;
			}
		};
		
		for (BaseColumn column : table.getColumns())
		{
			column.visitColumn(columnVerifier);
		}
	}
	
	protected void verifyBaseColumn(TableComponent table, BaseColumn column, JRVerifier verifier)
	{
		Integer width = column.getWidth();
		if (width == null)
		{
			verifier.addBrokenRule("Column width not set", column);
		}
		else if (width < 0)
		{
			verifier.addBrokenRule("Negative column width", column);
		}
		else
		{
			verifyCell(column.getTableHeader(), width, "table header", verifier);
			verifyCell(column.getTableFooter(), width, "table footer", verifier);
			verifyGroupCells(table, column.getGroupHeaders(), width, "group header", verifier);
			verifyGroupCells(table, column.getGroupFooters(), width, "group footer", verifier);
			verifyCell(column.getColumnHeader(), width, "column header", verifier);
			verifyCell(column.getColumnFooter(), width, "column footer", verifier);
		}
	}
	
	protected void verifyGroupCells(TableComponent table, List<GroupCell> cells, int width, 
			String cellName, JRVerifier verifier)
	{
		if (cells != null)
		{
			Set<String> groupNames = new HashSet<>();
			for (GroupCell groupCell : cells)
			{
				String groupName = groupCell.getGroupName();
				if (groupName == null)
				{
					verifier.addBrokenRule("No group name set for table column group cell", groupCell);
				}
				else
				{
					if (!groupNames.add(groupName))
					{
						verifier.addBrokenRule("Duplicate " + cellName + " for group \"" + groupName + "\"", 
								groupCell);
					}
					
					JRDatasetRun datasetRun = table.getDatasetRun();
					if (datasetRun != null)
					{
						JRDesignDataset dataset = (JRDesignDataset) verifier.getReportDesign().getDatasetMap().get(
								datasetRun.getDatasetName());
						if (dataset != null && dataset.getGroupsMap().get(groupName) == null)
						{
							verifier.addBrokenRule("No group named \"" + groupName 
									+ "\" found in subdataset " + datasetRun.getDatasetName(), 
									groupCell);
						}
					}
				}
				
				verifyCell(groupCell.getCell(), width, cellName, verifier);
			}
		}
	}
	
	protected void verifyCell(Cell cell, int width, String cellName, JRVerifier verifier)
	{
		if (cell == null)
		{
			return;
		}
		
		if (cell.getRowSpan() != null && cell.getRowSpan() < 1)
		{
			verifier.addBrokenRule("Negative or zero cell row span", cell);
		}
		
		Integer height = cell.getHeight();
		if (height == null)
		{
			verifier.addBrokenRule("Cell height not set", cell);
		}
		else if (height < 0)
		{
			verifier.addBrokenRule("Negative cell height", cell);
		}
		else
		{
			JRElement[] elements = cell.getElements();
			if (elements != null && elements.length > 0)
			{
				int topPadding = cell.getLineBox().getTopPadding();
				int leftPadding = cell.getLineBox().getLeftPadding();
				int bottomPadding = cell.getLineBox().getBottomPadding();
				int rightPadding = cell.getLineBox().getRightPadding();

				int avlblWidth = width - leftPadding - rightPadding;
				int avlblHeight = height - topPadding - bottomPadding;
				
				for (JRElement element : elements)
				{
					verifier.verifyElement(element);
					
					if (element.getX() < 0 || element.getY() < 0)
					{
						verifier.addBrokenRule("Element must be placed at positive coordinates.", 
								element);
					}
					
					if (element.getY() + element.getHeight() > avlblHeight)
					{
						verifier.addBrokenRule("Element reaches outside table " + cellName + " contents height: y = " 
								+ element.getY() + ", height = " + element.getHeight() 
								+ ", cell available height = " + avlblHeight + ".", element);
					}
					
					if (element.getX() + element.getWidth() > avlblWidth)
					{
						verifier.addBrokenRule("Element reaches outside table " + cellName + " contents width: x = " 
								+ element.getX() + ", width = " + element.getWidth() 
								+ ", cell available width = " + avlblWidth + ".", element);
					}
					
				}
			}
		}
	}
	
	protected void verifyColumn(TableComponent table, Column column, JRVerifier verifier)
	{
		verifyBaseColumn(table, column, verifier);
		
		if (column.getWidth() != null)
		{
			Cell detailCell = column.getDetailCell();
			verifyCell(detailCell, column.getWidth(), "detail", verifier);
		}
	}
	
	protected interface ColumnCellSelector
	{
		Cell getCell(Column column);
		
		Cell getCell(ColumnGroup group);
		
		String getCellName();
	}
	
	protected void verifyColumnHeights(TableComponent table, 
			JRVerifier verifier)
	{
		verifyColumnHeights(table, verifier, new BaseColumnCellSelector()
		{
			@Override
			protected Cell getCell(BaseColumn column)
			{
				return column.getTableHeader();
			}

			@Override
			public String getCellName()
			{
				return "table header";
			}
		});
		
		verifyColumnHeights(table, verifier, new BaseColumnCellSelector()
		{
			@Override
			protected Cell getCell(BaseColumn column)
			{
				return column.getTableFooter();
			}

			@Override
			public String getCellName()
			{
				return "table footer";
			}
		});
		
		JRDatasetRun datasetRun = table.getDatasetRun();
		if (datasetRun != null)
		{
			JRDesignDataset dataset = (JRDesignDataset) verifier.getReportDesign().getDatasetMap().get(
					datasetRun.getDatasetName());
			if (dataset != null)
			{
				JRGroup[] groups = dataset.getGroups();
				if (groups != null)
				{
					for (int i = 0; i < groups.length; i++)
					{
						final String groupName = groups[i].getName();
						
						verifyColumnHeights(table, verifier, new BaseColumnCellSelector()
						{
							@Override
							protected Cell getCell(BaseColumn column)
							{
								return column.getGroupHeader(groupName);
							}

							@Override
							public String getCellName()
							{
								return "group " + groupName + " header";
							}
						});
						
						verifyColumnHeights(table, verifier, new BaseColumnCellSelector()
						{
							@Override
							protected Cell getCell(BaseColumn column)
							{
								return column.getGroupFooter(groupName);
							}

							@Override
							public String getCellName()
							{
								return "group " + groupName + " footer";
							}
						});
					}
				}
			}
		}
		
		verifyColumnHeights(table, verifier, new BaseColumnCellSelector()
		{
			@Override
			protected Cell getCell(BaseColumn column)
			{
				return column.getColumnHeader();
			}

			@Override
			public String getCellName()
			{
				return "column header";
			}
		});
		
		verifyColumnHeights(table, verifier, new BaseColumnCellSelector()
		{
			@Override
			protected Cell getCell(BaseColumn column)
			{
				return column.getColumnFooter();
			}

			@Override
			public String getCellName()
			{
				return "column footer";
			}
		});
		
		verifyColumnHeights(table, verifier, new ColumnCellSelector()
		{
			@Override
			public Cell getCell(Column column)
			{
				return column.getDetailCell();
			}

			@Override
			public Cell getCell(ColumnGroup group)
			{
				return null;
			}
			
			@Override
			public String getCellName()
			{
				return "detail";
			}
		});		
	}
	
	protected abstract class BaseColumnCellSelector implements ColumnCellSelector
	{

		@Override
		public Cell getCell(Column column)
		{
			return getCell((BaseColumn) column);
		}

		@Override
		public Cell getCell(ColumnGroup group)
		{
			return getCell((BaseColumn) group);
		}
		
		protected abstract Cell getCell(BaseColumn column);
	}
	
	protected void verifyColumnHeights(TableComponent table, 
			JRVerifier verifier, 
			final ColumnCellSelector cellSelector)
	{
		final List<List<Cell>> tableCellRows = new ArrayList<>();
		
		ColumnVisitor<Void> cellCollector = new ColumnVisitor<Void>()
		{
			int rowIdx = 0;
			
			protected List<Cell> getRow()
			{
				int currentRowCount = tableCellRows.size();
				if (rowIdx >= currentRowCount)
				{
					for (int i = currentRowCount; i <= rowIdx; i++)
					{
						tableCellRows.add(new ArrayList<>());
					}
				}
				return tableCellRows.get(rowIdx);
			}
			
			@Override
			public Void visitColumn(Column column)
			{
				Cell cell = cellSelector.getCell(column);
				if (cell != null)
				{
					getRow().add(cell);
				}
				return null;
			}

			@Override
			public Void visitColumnGroup(ColumnGroup columnGroup)
			{
				Cell cell = cellSelector.getCell(columnGroup);
				if (cell != null)
				{
					getRow().add(cell);
				}
				
				int span = cell == null ? 0 : 1;
				if (cell != null && cell.getRowSpan() != null && cell.getRowSpan() > 1)
				{
					span = cell.getRowSpan();
				}
				
				rowIdx += span;
				for (BaseColumn subcolumn : columnGroup.getColumns())
				{
					subcolumn.visitColumn(this);
				}
				rowIdx -= span;
				
				return null;
			}
		};
		
		for (BaseColumn column : table.getColumns())
		{
			column.visitColumn(cellCollector);
		}
		
		boolean validRowHeights = true;
		
		List<Integer> rowHeights = new ArrayList<>(tableCellRows.size());
		for (int rowIdx = 0; rowIdx < tableCellRows.size(); ++rowIdx)
		{
			Integer rowHeight = null;
			// going back on rows in order to determine row height
			int spanHeight = 0;
			prevRowLoop:
			for (int idx = rowIdx; idx >= 0; --idx)
			{
				for (Cell cell : tableCellRows.get(idx))
				{
					int rowSpan = cell.getRowSpan() == null ? 1 : cell.getRowSpan();
					if (idx + rowSpan - 1 == rowIdx && cell.getHeight() != null)
					{
						rowHeight = cell.getHeight() - spanHeight;
						break prevRowLoop;
					}
				}
				
				if (rowIdx > 0)
				{
					spanHeight += rowHeights.get(rowIdx - 1);
				}
			}
			
			if (rowHeight == null)
			{
				verifier.addBrokenRule("Unable to determine " + cellSelector.getCellName() 
						+ " row #" + (rowIdx + 1) + " height.", 
						table);
				validRowHeights = false;
			}
			else
			{
				rowHeights.add(rowHeight);
			}
		}
		
		// don't do any more verifications if row heights could not be determined
		if (validRowHeights)
		{
			for (ListIterator<List<Cell>> rowIt = tableCellRows.listIterator(); rowIt.hasNext();)
			{
				List<Cell> row = rowIt.next();
				int rowIdx = rowIt.previousIndex();
				int rowHeight = rowHeights.get(rowIdx);
				
				for (Cell cell : row)
				{
					Integer rowSpan = cell.getRowSpan();
					Integer height = cell.getHeight();
					if ((rowSpan == null || rowSpan >= 1) 
							&& height != null)
					{
						int span = rowSpan == null ? 1 : rowSpan;
						if (rowIdx + span > tableCellRows.size())
						{
							verifier.addBrokenRule("Row span of " + cellSelector.getCellName() 
									+ " exceeds number of rows", cell);
						}
						else
						{
							int spanHeight = rowHeight;
							for (int idx = 1; idx < span; ++idx)
							{
								spanHeight += rowHeights.get(rowIdx + idx);
							}
							
							if (cell.getHeight() != spanHeight)
							{
								verifier.addBrokenRule("Height " + cell.getHeight() + " of " + cellSelector.getCellName() 
										+ " does not match computed row height of " + spanHeight, cell);
							}
						}
					}
				}
			}
		}
	}
}
