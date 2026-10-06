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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sf.jasperreports.components.table.util.ColumnElementsVisitor;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.HorizontalPosition;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardTable implements TableComponent, Serializable, JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_DATASET_RUN = "datasetRun";
	public static final String PROPERTY_COLUMNS = "columns";
	public static final String PROPERTY_WHEN_NO_DATA_TYPE = "whenNoDataType";
	public static final String PROPERTY_HORIZONTAL_POSITION = "horizontalPosition";
	public static final String PROPERTY_SHRINK_WIDTH = "shrinkWidth";

	public static final String PROPERTY_TABLE_HEADER = "tableHeader";
	public static final String PROPERTY_TABLE_FOOTER = "tableFooter";
	public static final String PROPERTY_COLUMN_HEADER = "columnHeader";
	public static final String PROPERTY_COLUMN_FOOTER = "columnFooter";
	public static final String PROPERTY_GROUP_HEADERS = "groupHeaders";
	public static final String PROPERTY_GROUP_FOOTERS = "groupFooters";
	public static final String PROPERTY_DETAIL = "detail";
	public static final String PROPERTY_NO_DATA = "noData";
	
	private JRDatasetRun datasetRun;
	private List<BaseColumn> columns;
	
	private WhenNoDataTypeTableEnum whenNoDataType;
	private HorizontalPosition horizontalPosition;
	private Boolean shrinkWidth;

	private Row tableHeader;
	private Row tableFooter;
	private List<GroupRow> groupHeaders; 
	private List<GroupRow> groupFooters; 
	private Row columnHeader;
	private Row columnFooter;
	private Row detail;
	private BaseCell noData;
	
	public StandardTable()
	{
		columns = new ArrayList<>();

		// these fields are a later addition so they can be null through deserialization;
		// no point in instantiating them here
		//groupHeaders = new ArrayList<GroupRow>();
		//groupFooters = new ArrayList<GroupRow>();
	}

	public StandardTable(TableComponent table, JRBaseObjectFactory factory)
	{
		whenNoDataType = table.getWhenNoDataType();
		horizontalPosition = table.getHorizontalPosition();
		shrinkWidth = table.shrinkWidth();

		datasetRun = factory.getDatasetRun(table.getDatasetRun());
		
		ColumnFactory columnFactory = new ColumnFactory(factory);
		columns = columnFactory.createColumns(table.getColumns());

		RowFactory rowFactory = new RowFactory(factory);
		this.tableHeader = rowFactory.createRow(table.getTableHeader());
		this.tableFooter = rowFactory.createRow(table.getTableFooter());
		this.groupHeaders = rowFactory.createGroupRows(table.getGroupHeaders());
		this.groupFooters = rowFactory.createGroupRows(table.getGroupFooters());
		this.columnHeader = rowFactory.createRow(table.getColumnHeader());
		this.columnFooter = rowFactory.createRow(table.getColumnFooter());
		this.detail = rowFactory.createRow(table.getDetail());
		
		this.noData = rowFactory.createBaseCell(table.getNoData());
	}
	
	@Override
	public List<BaseColumn> getColumns()
	{
		return columns;
	}

	public void setColumns(List<BaseColumn> columns)
	{
		Object old = this.columns;
		this.columns = columns;
		getEventSupport().firePropertyChange(PROPERTY_COLUMNS, 
				old, this.columns);
	}

	public void addColumn(BaseColumn column)
	{
		columns.add(column);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_COLUMNS, 
				column, columns.size() - 1);
	}
	
	public void addColumn(int index, BaseColumn column)
	{
		columns.add(index, column);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_COLUMNS, 
				column, index);
	}

	public boolean removeColumn(BaseColumn column)
	{
		int idx = columns.indexOf(column);
		if (idx >= 0)
		{
			columns.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_COLUMNS, 
					column, idx);
		}
		return idx >= 0;
	}

	@Override
	public JRDatasetRun getDatasetRun()
	{
		return datasetRun;
	}

	public void setDatasetRun(JRDatasetRun datasetRun)
	{
		Object old = this.datasetRun;
		this.datasetRun = datasetRun;
		getEventSupport().firePropertyChange(PROPERTY_DATASET_RUN, 
				old, this.datasetRun);
	}
	
	@Override
	public WhenNoDataTypeTableEnum getWhenNoDataType()
	{
		return whenNoDataType;
	}
	
	public void setWhenNoDataType(WhenNoDataTypeTableEnum whenNoDataType)
	{
		Object old = this.whenNoDataType;
		this.whenNoDataType = whenNoDataType;
		getEventSupport().firePropertyChange(PROPERTY_WHEN_NO_DATA_TYPE, old, this.whenNoDataType);
	}
	
	@Override
	public HorizontalPosition getHorizontalPosition()
	{
		return horizontalPosition;
	}
	
	/**
	 * Sets the position of the table within its element box.
	 * 
	 * @param horizontalPosition the table position, 
	 * if <code>null</code> the default position is applied
	 */
	public void setHorizontalPosition(HorizontalPosition horizontalPosition)
	{
		Object old = this.horizontalPosition;
		this.horizontalPosition = horizontalPosition;
		getEventSupport().firePropertyChange(PROPERTY_HORIZONTAL_POSITION, old, this.horizontalPosition);
	}
	
	@Override
	public Boolean shrinkWidth()
	{
		return shrinkWidth;
	}
	
	/**
	 *
	 */
	public void setShrinkWidth(Boolean shrinkWidth)
	{
		Object old = this.shrinkWidth;
		this.shrinkWidth = shrinkWidth;
		getEventSupport().firePropertyChange(PROPERTY_SHRINK_WIDTH, old, this.shrinkWidth);
	}

	@Override
	public Row getTableHeader()
	{
		return tableHeader;
	}

	public void setTableHeader(Row tableHeader)
	{
		Object old = this.tableHeader;
		this.tableHeader = tableHeader;
		getEventSupport().firePropertyChange(PROPERTY_TABLE_HEADER, 
				old, this.tableHeader);
	}

	@Override
	public Row getTableFooter()
	{
		return tableFooter;
	}

	public void setTableFooter(Row tableFooter)
	{
		Object old = this.tableFooter;
		this.tableFooter = tableFooter;
		getEventSupport().firePropertyChange(PROPERTY_TABLE_FOOTER, 
				old, this.tableFooter);
	}
	
	@Override
	public List<GroupRow> getGroupHeaders()
	{
		return groupHeaders;
	}

	@Override
	public List<GroupRow> getGroupFooters()
	{
		return groupFooters;
	}

	protected int findGroupRowIndex(List<GroupRow> groupRows, String groupName)
	{
		int idx = -1;
		if (groupRows != null)
		{
			for (ListIterator<GroupRow> it = groupRows.listIterator(); it.hasNext();)
			{
				GroupRow groupRow = it.next();
				if (groupName.equals(groupRow.getGroupName()))
				{
					idx = it.previousIndex();
				}
			}
		}
		return idx;
	}
	
	@Override
	public Row getGroupFooter(String groupName)
	{
		int idx = findGroupRowIndex(groupFooters, groupName);
		return idx < 0 ? null : groupFooters.get(idx).getRow();
	}

	@Override
	public Row getGroupHeader(String groupName)
	{
		int idx = findGroupRowIndex(groupHeaders, groupName);
		return idx < 0 ? null : groupHeaders.get(idx).getRow();
	}

	public void setGroupHeaders(List<GroupRow> groupHeaders)
	{
		Object old = this.groupHeaders;
		this.groupHeaders = groupHeaders;
		getEventSupport().firePropertyChange(PROPERTY_GROUP_HEADERS, 
				old, this.groupHeaders);
	}

	public void setGroupFooters(List<GroupRow> groupFooters)
	{
		Object old = this.groupFooters;
		this.groupFooters = groupFooters;
		getEventSupport().firePropertyChange(PROPERTY_GROUP_FOOTERS, 
				old, this.groupFooters);
	}

	public void addGroupHeader(GroupRow groupRow)
	{
		if (groupHeaders == null)
		{
			groupHeaders = new ArrayList<>();
		}
		groupHeaders.add(groupRow);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_GROUP_HEADERS, 
				groupRow, groupHeaders.size() - 1);
	}

	public void addGroupFooter(GroupRow groupRow)
	{
		if (groupFooters == null)
		{
			groupFooters = new ArrayList<>();
		}
		groupFooters.add(groupRow);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_GROUP_FOOTERS, 
				groupRow, groupFooters.size() - 1);
	}

	public boolean removeGroupFooter(GroupRow groupRow)
	{
		if (groupFooters == null)
		{
			return false;
		}
		
		int idx = groupFooters.indexOf(groupRow);
		if (idx >= 0)
		{
			groupFooters.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_GROUP_FOOTERS, 
					groupRow, idx);
		}
		return idx >= 0;
	}

	public boolean removeGroupHeader(GroupRow groupRow)
	{
		if (groupHeaders == null)
		{
			return false;
		}
		
		int idx = groupHeaders.indexOf(groupRow);
		if (idx >= 0)
		{
			groupHeaders.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_GROUP_HEADERS, 
					groupRow, idx);
		}
		return idx >= 0;
	}
	
	public void setGroupFooter(String groupName, Row row)
	{
		int idx = findGroupRowIndex(groupFooters, groupName);
		if (idx >= 0)
		{
			GroupRow old = groupFooters.get(idx);
			if (row == null)
			{
				// removing group footer
				groupFooters.remove(idx);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_GROUP_FOOTERS, 
						old, idx);
			}
			else
			{
				// replacing group footer
				StandardGroupRow groupRow = new StandardGroupRow(groupName, row);
				groupFooters.set(idx, groupRow);
				getEventSupport().fireIndexedPropertyChange(PROPERTY_GROUP_FOOTERS, idx, 
						old, groupRow);
			}
		}
		else if (row != null)
		{
				// adding group footer
				StandardGroupRow groupRow = new StandardGroupRow(groupName, row);
				addGroupFooter(groupRow);
		}
	}
	
	public void setGroupHeader(String groupName, Row row)
	{
		int idx = findGroupRowIndex(groupHeaders, groupName);
		if (idx >= 0)
		{
			GroupRow old = groupHeaders.get(idx);
			if (row == null)
			{
				// removing group header
				groupHeaders.remove(idx);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_GROUP_HEADERS, 
						old, idx);
			}
			else
			{
				// replacing group header
				StandardGroupRow groupRow = new StandardGroupRow(groupName, row);
				groupHeaders.set(idx, groupRow);
				getEventSupport().fireIndexedPropertyChange(PROPERTY_GROUP_HEADERS, idx, 
						old, groupRow);
			}
		}
		else if (row != null)
		{
			// adding group header
			StandardGroupRow groupRow = new StandardGroupRow(groupName, row);
			addGroupHeader(groupRow);
		}
	}

	@Override
	public Row getColumnHeader()
	{
		return columnHeader;
	}
	
	public void setColumnHeader(Row header)
	{
		Object old = this.columnHeader;
		this.columnHeader = header;
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_HEADER, 
				old, this.columnHeader);
	}

	@Override
	public Row getColumnFooter()
	{
		return columnFooter;
	}

	public void setColumnFooter(Row header)
	{
		Object old = this.columnFooter;
		this.columnFooter = header;
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_FOOTER, 
				old, this.columnFooter);
	}

	@Override
	public Row getDetail()
	{
		return detail;
	}

	public void setDetail(Row detail)
	{
		Object old = this.detail;
		this.detail = detail;
		getEventSupport().firePropertyChange(PROPERTY_DETAIL, 
				old, this.detail);
	}
	
	@Override
	public BaseCell getNoData()
	{
		return noData;
	}

	public void setNoData(BaseCell noData)
	{
		Object old = this.noData;
		this.noData = noData;
		getEventSupport().firePropertyChange(PROPERTY_NO_DATA, 
				old, this.noData);
	}
	
	@Override
	public Object clone()
	{
		StandardTable clone = null;
		try
		{
			clone = (StandardTable) super.clone();
		} 
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.datasetRun = JRCloneUtils.nullSafeClone(datasetRun);
		clone.columns = JRCloneUtils.cloneList(columns);
		clone.tableHeader = JRCloneUtils.nullSafeClone(tableHeader);
		clone.tableFooter = JRCloneUtils.nullSafeClone(tableFooter);
		clone.groupHeaders = JRCloneUtils.cloneList(groupHeaders);
		clone.groupFooters = JRCloneUtils.cloneList(groupFooters);
		clone.columnHeader = JRCloneUtils.nullSafeClone(columnHeader);
		clone.columnFooter = JRCloneUtils.nullSafeClone(columnFooter);
		clone.detail = JRCloneUtils.nullSafeClone(detail);
		clone.noData = JRCloneUtils.nullSafeClone(noData);
		clone.eventSupport = null;
		return clone;
	}

	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		if (ElementsVisitorUtils.visitDeepElements(visitor))
		{
			ColumnElementsVisitor columnElementsVisitor = new ColumnElementsVisitor(visitor);
			for (BaseColumn column : columns)
			{
				column.visitColumn(columnElementsVisitor);
			}
		}
	}
	
}
