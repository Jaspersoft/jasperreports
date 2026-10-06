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
package net.sf.jasperreports.components.table.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sf.jasperreports.components.table.BaseCell;
import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.Cell;
import net.sf.jasperreports.components.table.Column;
import net.sf.jasperreports.components.table.ColumnGroup;
import net.sf.jasperreports.components.table.TableComponent;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * @author Veaceslav Chicu (schicu@users.sourceforge.net)
 */
public class TableUtil 
{
	public static final int TABLE_HEADER = 0;
	public static final int TABLE_FOOTER = 1;
	public static final int COLUMN_HEADER = 2;
	public static final int COLUMN_FOOTER = 3;
	public static final int COLUMN_DETAIL = 4;
	public static final int COLUMN_GROUP_HEADER = 5;
	public static final int COLUMN_GROUP_FOOTER = 6;
	
	private TableComponent table;
	private Map<BaseCell, Rectangle> boundsMap = new HashMap<>();
	private JRReport report;

	public TableUtil(TableComponent table, JRReport report) {
		this.table = table;
		this.report = report;
		init(table);
	}

	public Map<BaseCell, Rectangle> getCellBounds() {
		return boundsMap;
	}

	public void refresh() {
		init(table);
	}

	public void init(TableComponent table) {
		boundsMap.clear();

		List<BaseColumn> allColumns = getAllColumns(table.getColumns());
		int y = 0;
		int h = 0;
		Rectangle r = new Rectangle(0, 0, 0, 0);
		for (BaseColumn bc : table.getColumns()) {
			r = initHeader(r, bc, TABLE_HEADER, null);
			r.setLocation(r.x , y);
			if (h < r.height)
				h = r.height;
		}
		y += h;
		r = new Rectangle(0, y, 0, 0);
		h = 0;
		for (BaseColumn bc : table.getColumns()) {
			r = initHeader(r, bc, COLUMN_HEADER, null);
			r.setLocation(r.x, y);
			if (h < r.height)
				h = r.height;
		}

		List<?> groupsList = getGroupList();
		if (groupsList != null)
			for (Iterator<?> it = groupsList.iterator(); it.hasNext();) {
				JRGroup jrGroup = (JRGroup) it.next();
				y += h;
				r = new Rectangle(0, y, 0, 0);
				h = 0;
				for (BaseColumn bc : table.getColumns()) {
					r = initHeader(r, bc, COLUMN_GROUP_HEADER, jrGroup.getName());
					r.setLocation(r.x, y);
					if (h < r.height)
						h = r.height;
				}
			}

		y += h;
		r = new Rectangle(0, y, 0, 0);
		h = 0;
		for (BaseColumn bc : allColumns) {
			r = initDetail(r, bc);
			r.setLocation(r.x, y);
			if (h < r.height)
				h = r.height;
		}

		if (groupsList != null)
			for (ListIterator<?> it = groupsList.listIterator(groupsList.size()); it.hasPrevious();) {
				JRGroup jrGroup = (JRGroup) it.previous();
				y += h;
				r = new Rectangle(0, y, 0, 0);
				h = 0;
				for (BaseColumn bc : table.getColumns()) {
					r = initFooter(r, bc, COLUMN_GROUP_FOOTER, jrGroup.getName());
					r.setLocation(r.x, y);
					if (h < r.height)
						h = r.height;
				}
			}

		y += h;
		r = new Rectangle(0, y, 0, 0);
		h = 0;
		for (BaseColumn bc : table.getColumns()) {
			r = initFooter(r, bc, COLUMN_FOOTER, null);
			r.setLocation(r.x, y);
			if (h < r.height)
				h = r.height;
		}
		y += h;
		r = new Rectangle(0, y, 0, 0);
		h = 0;
		for (BaseColumn bc : table.getColumns()) {
			r = initFooter(r, bc, TABLE_FOOTER, null);
			r.setLocation(r.x, y);
			if (h < r.height)
				h = r.height;
		}
		y += h;
		r = new Rectangle(0, y, r.width, 0);
		h = 0;
		BaseCell noDataCell = table.getNoData(); 
		if (noDataCell != null)
		{
			int w = 0;
			for (BaseColumn bc : table.getColumns()) {
				w += bc.getWidth();
			}
			h = noDataCell.getHeight();
			boundsMap.put(noDataCell, new Rectangle(r.x, r.y, w, h));
			r.setLocation(r.x, y);
		}
		y += h;
	}

	public static List<BaseColumn> getAllColumns(TableComponent table) {
		return getAllColumns(table.getColumns());
	}

	public static List<BaseColumn> getAllColumns(List<BaseColumn> cols) {
		List<BaseColumn> lst = new ArrayList<>();
		for (BaseColumn bc : cols) {
			if (bc instanceof ColumnGroup)
				lst.addAll(getAllColumns(((ColumnGroup) bc).getColumns()));
			else
				lst.add(bc);
		}
		return lst;
	}

	private Rectangle initDetail(Rectangle p, BaseColumn bc) {
		int h = 0;
		int w = 0;
		if (bc != null && bc instanceof Column) {
			Cell c = ((Column) bc).getDetailCell();
			w = bc.getWidth();
			if (c != null)
				h = c.getHeight();
			boundsMap.put(c, new Rectangle(p.x, p.y, w, h));
		}
		return new Rectangle(p.x + w, p.y, w, h);
	}

	private Rectangle initHeader(Rectangle p, BaseColumn bc, int type, String grName) {
		int y = p.y;
		int h = 0;
		int w = bc.getWidth();
		Cell c = getCell(bc, type, grName);
		if (c != null) {
			y = p.y + c.getHeight();
			h = c.getHeight();
			boundsMap.put(c, new Rectangle(p.x, p.y, w, h));
		}
		if (bc instanceof ColumnGroup) {
			Rectangle pi = new Rectangle(p.x, y, w, h);
			int hi = 0;
			for (BaseColumn bcg : ((ColumnGroup) bc).getColumns()) {
				pi = initHeader(pi, bcg, type, grName);
				pi.setLocation(pi.x, y);
				if (hi < pi.height)
					hi = pi.height;
			}
			h += hi;
		}
		return new Rectangle(p.x + w, y, w, h);
	}

	private Rectangle initFooter(Rectangle p, BaseColumn bc, int type, String grName) {
		int y = p.y;
		int h = 0;
		int w = bc.getWidth();
		Cell c = getCell(bc, type, grName);
		if (bc instanceof ColumnGroup) {
			Rectangle pi = new Rectangle(p.x, y, w, h);
			int hi = 0;
			for (BaseColumn bcg : ((ColumnGroup) bc).getColumns()) {
				pi = initFooter(pi, bcg, type, grName);
				pi.setLocation(pi.x, y);
				if (hi < pi.height)
					hi = pi.height;
			}
			h += hi;
		}
		if (c != null) {
			y = p.y + h;
			int cellHeight = c.getHeight();
			boundsMap.put(c, new Rectangle(p.x, y, w, cellHeight));
			h += cellHeight;
		}
		return new Rectangle(p.x + w, y, w, h);
	}

	public Rectangle getBounds(int width, Cell cell, BaseColumn col) {
		Rectangle b = boundsMap.get(cell);
		if (b != null)
			return b;
		int w = col != null ? col.getWidth() : 0;
		int h = cell != null ? cell.getHeight() : 0;
		return new Rectangle(0, 0, w, h);
	}

	public List<?> getGroupList() {
		return getGroupList(table, report);
	}

	public static List<?> getGroupList(TableComponent table, JRReport report) 
	{
		List<?> groupsList = null;
		JRDatasetRun datasetRun = table.getDatasetRun();
		if (datasetRun != null) 
		{
			String dataSetName = datasetRun.getDatasetName();
			JRDataset[] datasets = report.getDatasets();
			if (datasets != null && dataSetName != null)
			{
				for (JRDataset ds : datasets)
				{
					JRGroup[] groups = ds.getGroups();
					if (dataSetName.equals(ds.getName()) && groups != null)
					{
						groupsList = Arrays.asList(groups);
						break;
					}
				}
			}
		}
		return groupsList;
	}

	public static Cell getCell(BaseColumn bc, int type, String grName) {
		Cell cell = null;
		switch (type) {
		case TABLE_HEADER:
			cell = bc.getTableHeader();
			break;
		case TABLE_FOOTER:
			cell = bc.getTableFooter();
			break;
		case COLUMN_HEADER:
			cell = bc.getColumnHeader();
			break;
		case COLUMN_FOOTER:
			cell = bc.getColumnFooter();
			break;
		case COLUMN_DETAIL:
			if (bc instanceof Column)
				cell = ((Column) bc).getDetailCell();
			break;
		case COLUMN_GROUP_HEADER:
			cell = bc.getGroupHeader(grName);
			break;
		case COLUMN_GROUP_FOOTER:
			cell = bc.getGroupFooter(grName);
			break;
		default:
		}
		return cell;
	}

	public static ColumnGroup getColumnGroupForColumn(BaseColumn column, List<BaseColumn> columns) {
		for (BaseColumn bc: columns) {
			if (bc instanceof ColumnGroup) {
				ColumnGroup cg = (ColumnGroup) bc;
				if (cg.getColumns().contains(column)) {
					return cg;
				} else {
					return getColumnGroupForColumn(column, cg.getColumns());
				}
			}
		}
		return null;
	}

	/**
	 * 
	 */
	public static <T extends JRElement> T getCellElement(Class<T> type, Cell cell, boolean oneElementPerCell) 
	{
		List<JRChild> detailElements = cell == null ? null : cell.getChildren();
		
		if (detailElements == null || (detailElements != null && oneElementPerCell && detailElements.size() != 1)) 
		{
			return null;
		}
		
		for (JRChild detailElement: detailElements) 
		{
			if (type.isInstance(detailElement) ) 
			{
				@SuppressWarnings("unchecked")
				T de = (T) detailElement;
				return de;
			}
		}
		
		return null;
	}

	public static boolean isFilterable(JRTextField textField) 
	{
		if (textField != null)
		{
			return EvaluationTimeEnum.getValueOrDefault(textField.getEvaluationTime()) == EvaluationTimeEnum.NOW;
		}
		
		return false;
	}

	public static boolean hasSingleChunkExpression(JRTextField textField) 
	{
		if (textField != null)
		{
			JRExpression textExpression = textField.getExpression();
			JRExpressionChunk[] chunks = textExpression == null ? null : textExpression.getChunks();
			if (chunks == null || chunks.length != 1
					|| (chunks[0].getType() != JRExpressionChunk.TYPE_FIELD
					&& chunks[0].getType() != JRExpressionChunk.TYPE_VARIABLE))
			{
				return false;
			}
			
			return true;
		}
		
		return false;
	}

	public static int getColumnIndex(Column column, TableComponent table) {
		List<BaseColumn> columns = getAllColumns(table);
		return columns.indexOf(column);
	}

	public static List<ColumnGroup> getHierarchicalColumnGroupsForColumn(BaseColumn column, List<BaseColumn> columns, TableComponent table) {
		List<ColumnGroup> result = new ArrayList<>();
		List<BaseColumn> cols = columns != null ? columns : table.getColumns();
		for (BaseColumn bc : cols) {
			if (bc instanceof ColumnGroup){
				if (((ColumnGroup)bc).getColumns().contains(column)) {
					result.add((ColumnGroup)bc);
					result.addAll(getHierarchicalColumnGroupsForColumn(bc, null, table));
				} else {
					result.addAll(getHierarchicalColumnGroupsForColumn(column, ((ColumnGroup) bc).getColumns(), table));
				}
			}
		}

		return result;
	}

	/**
	 * 
	 */
	public static <T extends JRElement> T getCellElement(Class<T> type, BaseColumn column, int sectionType, String groupName, TableComponent table) 
	{
		T element = TableUtil.getCellElement(type, getCell(column, sectionType, groupName), false);

		if (element == null) 
		{
			List<ColumnGroup> colGroups = TableUtil.getHierarchicalColumnGroupsForColumn(column, table.getColumns(), table);

			for (ColumnGroup colGroup: colGroups) 
			{
				if (colGroup.getGroupHeader(groupName) == null) 
				{
					continue;
				}
				element = TableUtil.getCellElement(type, getCell(colGroup, sectionType, groupName), false);
				if (element != null)
			   	{
					break;
			   	}
			}

		}

		return element;
	}

}
