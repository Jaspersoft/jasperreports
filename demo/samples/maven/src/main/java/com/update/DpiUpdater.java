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
package com.update;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.components.iconlabel.IconLabelComponent;
import net.sf.jasperreports.components.list.DesignListContents;
import net.sf.jasperreports.components.list.StandardListComponent;
import net.sf.jasperreports.components.table.BaseColumn;
import net.sf.jasperreports.components.table.DesignBaseCell;
import net.sf.jasperreports.components.table.GroupCell;
import net.sf.jasperreports.components.table.StandardBaseColumn;
import net.sf.jasperreports.components.table.StandardColumnGroup;
import net.sf.jasperreports.components.table.StandardTable;
import net.sf.jasperreports.components.table.util.TableUtil;
import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.design.DesignCrosstabColumnCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabRowGroup;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRCommonGraphicElement;
import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRParagraphContainer;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.ReportUpdater;


/**
 * Report updater that converts a report template to a different DPI resolution.
 * <p/>
 * All lengths in a report template are expressed in pixels and are interpreted at the resolution
 * declared by the <code>dpi</code> attribute of the <code>&lt;jasperReport/&gt;</code> element,
 * which defaults to {@link JasperPrint#DEFAULT_REPORT_DPI 72} DPI. Raising the report resolution
 * gives a finer coordinate grid, which allows for more precise positioning and sizing of the
 * report content, but it requires all the lengths in the template to be recalculated in
 * accordance with the target resolution.
 * <p/>
 * This updater multiplies every pixel length in the design by
 * <code>targetDpi / sourceDpi</code> and then stamps the new resolution onto the design.
 * <p/>
 * Font sizes are deliberately left untouched, because they are expressed in typographic points
 * and not in pixels. The engine itself scales them by <code>dpi / 72</code> when measuring and
 * rendering text, so scaling them here as well would make the text twice as large as intended.
 * For the same reason, pen line widths that are not explicitly set are left unset, so that they
 * keep resolving to the resolution dependent default line width computed by the engine.
 * <p/>
 * Lengths are rounded down. Rounding down loses at most one pixel at the target resolution, but
 * it guarantees that the scaled design still satisfies the layout constraints checked by the
 * report verifier: since the sum of the rounded down parts is never greater than the rounded down
 * sum, the columns and margins still fit the page width and the bands and margins still fit the
 * page height. Element positions and sizes are scaled as a chain of edges, that is, the scaled
 * size of an element is the difference between the scaled positions of its two edges, so that
 * elements that were adjacent in the original design remain exactly adjacent afterwards.
 * <p/>
 * Note that report templates referenced through <code>&lt;template/&gt;</code> elements are not
 * visible to this updater and have to be converted separately.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DpiUpdater implements ReportUpdater
{

	/**
	 * System property specifying the resolution to which report templates are converted.
	 */
	public static final String PROPERTY_TARGET_DPI = "net.sf.jasperreports.maven.updater.dpi";

	/**
	 * The resolution used when the {@link #PROPERTY_TARGET_DPI} system property is not set.
	 */
	public static final int DEFAULT_TARGET_DPI = 300;

	private static final Log log = LogFactory.getLog(DpiUpdater.class);

	private final int targetDpi;

	/** component types already reported as not converted; the instance is shared by several threads */
	private final Set<String> unconvertedComponents = ConcurrentHashMap.newKeySet();


	/**
	 * Creates an updater that converts report templates to the resolution specified by the
	 * {@link #PROPERTY_TARGET_DPI} system property, or to {@link #DEFAULT_TARGET_DPI} DPI in case
	 * the property is not set.
	 * <p/>
	 * This is the constructor used by the JasperReports Maven Plugin, which instantiates report
	 * updaters through their no-argument constructor.
	 */
	public DpiUpdater()
	{
		this(Integer.getInteger(PROPERTY_TARGET_DPI, DEFAULT_TARGET_DPI));
	}


	/**
	 * Creates an updater that converts report templates to the specified resolution.
	 *
	 * @param targetDpi the target resolution, in dots per inch
	 */
	public DpiUpdater(int targetDpi)
	{
		if (targetDpi <= 0)
		{
			throw new JRRuntimeException("The target DPI resolution must be greater than zero.");
		}

		this.targetDpi = targetDpi;
	}


	@Override
	public JasperDesign update(JasperDesign jasperDesign)
	{
		int sourceDpi = jasperDesign.getDpi() > 0 ? jasperDesign.getDpi() : JasperPrint.DEFAULT_REPORT_DPI;

		if (sourceDpi != targetDpi)
		{
			// the updater instance is shared by the concurrent tasks of the Maven plugin,
			// so the state of a single conversion is kept in a local object
			Scaler scaler = new Scaler((double)targetDpi / sourceDpi);

			updateReport(jasperDesign, scaler);

			jasperDesign.setDpi(targetDpi);
		}

		return jasperDesign;
	}


	/**
	 *
	 */
	private void updateReport(JasperDesign jasperDesign, Scaler scaler)
	{
		jasperDesign.setPageWidth(scaler.scale(jasperDesign.getPageWidth()));
		jasperDesign.setPageHeight(scaler.scale(jasperDesign.getPageHeight()));
		jasperDesign.setLeftMargin(scaler.scale(jasperDesign.getLeftMargin()));
		jasperDesign.setRightMargin(scaler.scale(jasperDesign.getRightMargin()));
		jasperDesign.setTopMargin(scaler.scale(jasperDesign.getTopMargin()));
		jasperDesign.setBottomMargin(scaler.scale(jasperDesign.getBottomMargin()));
		jasperDesign.setColumnWidth(scaler.scale(jasperDesign.getColumnWidth()));
		jasperDesign.setColumnSpacing(scaler.scale(jasperDesign.getColumnSpacing()));

		for (JRStyle style : jasperDesign.getStylesList())
		{
			updateStyle((JRDesignStyle)style, scaler);
		}

		updateBand(jasperDesign.getBackground(), scaler);
		updateBand(jasperDesign.getTitle(), scaler);
		updateBand(jasperDesign.getPageHeader(), scaler);
		updateBand(jasperDesign.getColumnHeader(), scaler);
		updateSection(jasperDesign.getDetailSection(), scaler);
		updateBand(jasperDesign.getColumnFooter(), scaler);
		updateBand(jasperDesign.getPageFooter(), scaler);
		updateBand(jasperDesign.getLastPageFooter(), scaler);
		updateBand(jasperDesign.getSummary(), scaler);
		updateBand(jasperDesign.getNoData(), scaler);

		for (JRGroup group : jasperDesign.getGroupsList())
		{
			JRDesignGroup designGroup = (JRDesignGroup)group;
			designGroup.setMinHeightToStartNewPage(scaler.scale(designGroup.getMinHeightToStartNewPage()));
			updateSection(designGroup.getGroupHeaderSection(), scaler);
			updateSection(designGroup.getGroupFooterSection(), scaler);
		}
	}


	/**
	 *
	 */
	private void updateStyle(JRDesignStyle style, Scaler scaler)
	{
		updateCommonAttributes(style, scaler);

		for (JRConditionalStyle conditionalStyle : style.getConditionalStyleList())
		{
			updateCommonAttributes(conditionalStyle, scaler);
		}
	}


	/**
	 *
	 */
	private void updateSection(JRSection section, Scaler scaler)
	{
		if (section != null)
		{
			for (JRBand band : ((JRDesignSection)section).getBandsList())
			{
				updateBand(band, scaler);
			}
		}
	}


	/**
	 *
	 */
	private void updateBand(JRBand band, Scaler scaler)
	{
		if (band != null)
		{
			JRDesignBand designBand = (JRDesignBand)band;
			designBand.setHeight(scaler.scale(designBand.getHeight()));
			updateChildren(designBand.getChildren(), scaler);
		}
	}


	/**
	 *
	 */
	private void updateChildren(List<JRChild> children, Scaler scaler)
	{
		if (children != null)
		{
			for (JRChild child : children)
			{
				if (child instanceof JRDesignElement)
				{
					updateElement((JRDesignElement)child, scaler);
				}
				else if (child instanceof JRDesignElementGroup)
				{
					updateChildren(((JRDesignElementGroup)child).getChildren(), scaler);
				}
			}
		}
	}


	/**
	 *
	 */
	private void updateElement(JRDesignElement element, Scaler scaler)
	{
		int x = element.getX();
		int y = element.getY();

		// the sizes have to be calculated before the positions are overwritten
		element.setWidth(scaler.scaleSize(x, element.getWidth()));
		element.setHeight(scaler.scaleSize(y, element.getHeight()));
		element.setX(scaler.scale(x));
		element.setY(scaler.scale(y));

		updateCommonAttributes(element, scaler);

		if (element instanceof JRDesignFrame)
		{
			updateChildren(((JRDesignFrame)element).getChildren(), scaler);
		}
		else if (element instanceof JRDesignCrosstab)
		{
			updateCrosstab((JRDesignCrosstab)element, scaler);
		}
		else if (element instanceof JRDesignComponentElement)
		{
			updateComponent(((JRDesignComponentElement)element).getComponent(), scaler);
		}
	}


	/**
	 * Scales the lengths that any styled object might have, regardless of its actual type.
	 */
	private void updateCommonAttributes(Object object, Scaler scaler)
	{
		if (object instanceof JRBoxContainer)
		{
			updateLineBox(((JRBoxContainer)object).getLineBox(), scaler);
		}

		if (object instanceof JRCommonGraphicElement)
		{
			updatePen(((JRCommonGraphicElement)object).getLinePen(), scaler);
		}
		else if (object instanceof JRStyle)
		{
			updatePen(((JRStyle)object).getLinePen(), scaler);
		}

		if (object instanceof JRParagraphContainer)
		{
			updateParagraph(((JRParagraphContainer)object).getParagraph(), scaler);
		}
	}


	/**
	 *
	 */
	private void updateLineBox(JRLineBox lineBox, Scaler scaler)
	{
		if (lineBox != null)
		{
			lineBox.setPadding(scaler.scale(lineBox.getOwnPadding()));
			lineBox.setTopPadding(scaler.scale(lineBox.getOwnTopPadding()));
			lineBox.setLeftPadding(scaler.scale(lineBox.getOwnLeftPadding()));
			lineBox.setBottomPadding(scaler.scale(lineBox.getOwnBottomPadding()));
			lineBox.setRightPadding(scaler.scale(lineBox.getOwnRightPadding()));

			updatePen(lineBox.getPen(), scaler);
			updatePen(lineBox.getTopPen(), scaler);
			updatePen(lineBox.getLeftPen(), scaler);
			updatePen(lineBox.getBottomPen(), scaler);
			updatePen(lineBox.getRightPen(), scaler);
		}
	}


	/**
	 *
	 */
	private void updatePen(JRPen pen, Scaler scaler)
	{
		if (pen != null)
		{
			// a null own line width means the engine calculates the default line width
			// for the report resolution, so it has to be left null
			Float lineWidth = pen.getOwnLineWidth();
			if (lineWidth != null)
			{
				pen.setLineWidth(scaler.scale(lineWidth));
			}
		}
	}


	/**
	 *
	 */
	private void updateParagraph(JRParagraph paragraph, Scaler scaler)
	{
		if (paragraph != null)
		{
			// the line spacing size is a factor applied to the line height, not a length
			paragraph.setFirstLineIndent(scaler.scale(paragraph.getOwnFirstLineIndent()));
			paragraph.setLeftIndent(scaler.scale(paragraph.getOwnLeftIndent()));
			paragraph.setRightIndent(scaler.scale(paragraph.getOwnRightIndent()));
			paragraph.setSpacingBefore(scaler.scale(paragraph.getOwnSpacingBefore()));
			paragraph.setSpacingAfter(scaler.scale(paragraph.getOwnSpacingAfter()));
			paragraph.setTabStopWidth(scaler.scale(paragraph.getOwnTabStopWidth()));

			TabStop[] tabStops = paragraph.getOwnTabStops();
			if (tabStops != null)
			{
				for (TabStop tabStop : tabStops)
				{
					tabStop.setPosition(scaler.scale(tabStop.getPosition()));
				}
			}
		}
	}


	/**
	 * Scales the sizes declared by a crosstab.
	 * <p/>
	 * The cells of a crosstab are laid out on a grid whose horizontal axis is made of the row
	 * group widths followed by the cell widths, and whose vertical axis is made of the title cell
	 * height, the column group heights and the cell heights. The sizes of the cell contents are
	 * not declared, but derived from this grid when the report is compiled, as sums of consecutive
	 * sizes along one of the two axes. Scaling each axis as a single chain of edges keeps these
	 * sums large enough to hold the scaled elements of the cells.
	 */
	private void updateCrosstab(JRDesignCrosstab crosstab, Scaler scaler)
	{
		List<JRCrosstabRowGroup> rowGroups = crosstab.getRowGroupsList();
		List<JRCrosstabColumnGroup> columnGroups = crosstab.getColumnGroupsList();
		DesignCrosstabColumnCell titleCell = (DesignCrosstabColumnCell)crosstab.getTitleCell();

		crosstab.setColumnBreakOffset(scaler.scale(crosstab.getColumnBreakOffset()));

		// there is one cell column per column group total, plus the one holding the data,
		// and the same goes for the cell rows
		int[] cellWidths = new int[columnGroups.size() + 1];
		int[] cellHeights = new int[rowGroups.size() + 1];

		for (JRCrosstabCell cell : crosstab.getCellsList())
		{
			if (cell.getWidth() != null)
			{
				cellWidths[getCellColumnIndex(cell, columnGroups)] = cell.getWidth();
			}

			if (cell.getHeight() != null)
			{
				cellHeights[getCellRowIndex(cell, rowGroups)] = cell.getHeight();
			}
		}

		// the offsets of the cells on the two axes have to be calculated
		// before any of the declared sizes is overwritten
		int rowHeadersWidth = 0;
		for (JRCrosstabRowGroup rowGroup : rowGroups)
		{
			rowHeadersWidth += rowGroup.getWidth();
		}

		int columnHeadersHeight = titleCell == null ? 0 : titleCell.getHeight();
		for (JRCrosstabColumnGroup columnGroup : columnGroups)
		{
			columnHeadersHeight += columnGroup.getHeight();
		}

		int[] cellOffsetsX = calculateOffsets(cellWidths, rowHeadersWidth);
		int[] cellOffsetsY = calculateOffsets(cellHeights, columnHeadersHeight);

		int rowGroupOffset = 0;
		for (JRCrosstabRowGroup rowGroup : rowGroups)
		{
			JRDesignCrosstabRowGroup designRowGroup = (JRDesignCrosstabRowGroup)rowGroup;
			int width = designRowGroup.getWidth();
			designRowGroup.setWidth(scaler.scaleSize(rowGroupOffset, width));
			rowGroupOffset += width;

			updateCellContents((JRDesignCellContents)designRowGroup.getHeader(), scaler);
			updateCellContents((JRDesignCellContents)designRowGroup.getTotalHeader(), scaler);
		}

		int columnGroupOffset = titleCell == null ? 0 : titleCell.getHeight();
		for (JRCrosstabColumnGroup columnGroup : columnGroups)
		{
			JRDesignCrosstabColumnGroup designColumnGroup = (JRDesignCrosstabColumnGroup)columnGroup;
			int height = designColumnGroup.getHeight();
			designColumnGroup.setHeight(scaler.scaleSize(columnGroupOffset, height));
			columnGroupOffset += height;

			updateCellContents((JRDesignCellContents)designColumnGroup.getCrosstabHeader(), scaler);
			updateCellContents((JRDesignCellContents)designColumnGroup.getHeader(), scaler);
			updateCellContents((JRDesignCellContents)designColumnGroup.getTotalHeader(), scaler);
		}

		if (titleCell != null)
		{
			titleCell.setHeight(scaler.scaleSize(0, titleCell.getHeight()));
			updateCellContents(titleCell.getDesignCellContents(), scaler);
		}

		for (JRCrosstabCell cell : crosstab.getCellsList())
		{
			JRDesignCrosstabCell designCell = (JRDesignCrosstabCell)cell;
			designCell.setWidth(
				scaler.scaleSize(cellOffsetsX[getCellColumnIndex(cell, columnGroups)], designCell.getWidth()));
			designCell.setHeight(
				scaler.scaleSize(cellOffsetsY[getCellRowIndex(cell, rowGroups)], designCell.getHeight()));
			updateCellContents((JRDesignCellContents)designCell.getContents(), scaler);
		}

		updateCellContents((JRDesignCellContents)crosstab.getHeaderCell(), scaler);
		updateCellContents((JRDesignCellContents)crosstab.getWhenNoDataCell(), scaler);
	}


	/**
	 * Turns a list of consecutive sizes into the list of the offsets at which they start.
	 */
	private int[] calculateOffsets(int[] sizes, int firstOffset)
	{
		int[] offsets = new int[sizes.length];
		int offset = firstOffset;

		for (int i = 0; i < sizes.length; i++)
		{
			offsets[i] = offset;
			offset += sizes[i];
		}

		return offsets;
	}


	/**
	 * Returns the index of the crosstab cell row that a cell belongs to. The row holding the data
	 * comes first, followed by the row totals, from the innermost row group outwards.
	 */
	private int getCellRowIndex(JRCrosstabCell cell, List<JRCrosstabRowGroup> rowGroups)
	{
		String totalGroupName = cell.getRowTotalGroup();

		if (totalGroupName != null)
		{
			for (int i = 0; i < rowGroups.size(); i++)
			{
				if (totalGroupName.equals(rowGroups.get(i).getName()))
				{
					return rowGroups.size() - i;
				}
			}
		}

		return 0;
	}


	/**
	 * Returns the index of the crosstab cell column that a cell belongs to. The column holding the
	 * data comes first, followed by the column totals, from the innermost column group outwards.
	 */
	private int getCellColumnIndex(JRCrosstabCell cell, List<JRCrosstabColumnGroup> columnGroups)
	{
		String totalGroupName = cell.getColumnTotalGroup();

		if (totalGroupName != null)
		{
			for (int i = 0; i < columnGroups.size(); i++)
			{
				if (totalGroupName.equals(columnGroups.get(i).getName()))
				{
					return columnGroups.size() - i;
				}
			}
		}

		return 0;
	}


	/**
	 *
	 */
	private void updateCellContents(JRDesignCellContents cellContents, Scaler scaler)
	{
		if (cellContents != null)
		{
			updateCommonAttributes(cellContents, scaler);
			updateChildren(cellContents.getChildren(), scaler);
		}
	}


	/**
	 * Scales the lengths declared by a component.
	 * <p/>
	 * Components keep their nested elements in their own structures rather than in the element
	 * list of their parent, and there is no general way of reaching them: only
	 * {@link net.sf.jasperreports.components.table.TableComponent TableComponent} and
	 * {@link net.sf.jasperreports.components.list.ListComponent ListComponent} implement
	 * {@link net.sf.jasperreports.engine.JRVisitable JRVisitable}, so a visitor does not reach
	 * inside the others either. Each component type therefore has to be handled explicitly, and a
	 * component this updater does not know about is reported instead of being silently left at
	 * the source resolution.
	 */
	private void updateComponent(Component component, Scaler scaler)
	{
		// a component may declare a box or a paragraph of its own, whatever its type
		updateCommonAttributes(component, scaler);

		if (component instanceof StandardTable)
		{
			updateTable((StandardTable)component, scaler);
		}
		else if (component instanceof StandardListComponent)
		{
			DesignListContents contents = (DesignListContents)((StandardListComponent)component).getContents();
			if (contents != null)
			{
				contents.setWidth(scaler.scale(contents.getWidth()));
				contents.setHeight(scaler.scale(contents.getHeight()));
				updateChildren(contents.getChildren(), scaler);
			}
		}
		else if (component instanceof IconLabelComponent)
		{
			IconLabelComponent iconLabel = (IconLabelComponent)component;
			updateComponentElement(iconLabel.getLabelTextField(), scaler);
			updateComponentElement(iconLabel.getIconTextField(), scaler);
		}
		else
		{
			reportUnconvertedComponent(component);
		}
	}


	/**
	 * Scales an element that a component keeps outside the element list of its parent.
	 */
	private void updateComponentElement(Object element, Scaler scaler)
	{
		if (element instanceof JRDesignElement)
		{
			updateElement((JRDesignElement)element, scaler);
		}
	}


	/**
	 * Warns, once per component type, that a component was left at the source resolution because
	 * this updater does not know which of its properties are lengths.
	 */
	private void reportUnconvertedComponent(Component component)
	{
		if (component != null && unconvertedComponents.add(component.getClass().getName()))
		{
			log.warn("Component " + component.getClass().getName()
				+ " is not converted to the target DPI resolution, because the DpiUpdater does not know it. "
				+ "Any length it declares stays at the source resolution.");
		}
	}


	/**
	 * Scales the sizes declared by a table component.
	 * <p/>
	 * The cells of a table form a grid of rows and columns, and the report compiler checks that
	 * the size of a cell spanning several columns or rows matches the sum of the sizes of the
	 * columns or the rows it spans. Column widths are therefore scaled as a chain of edges along
	 * the horizontal axis of the table, and cell heights as a chain of edges along the vertical
	 * axis of the table section they belong to, which keeps these sums matching after the
	 * conversion. Each section is a grid of its own, so each one starts a new chain.
	 */
	private void updateTable(StandardTable table, Scaler scaler)
	{
		Set<String> groupNames = new LinkedHashSet<>();
		collectGroupNames(table.getColumns(), groupNames);

		updateHeaderCells(table.getColumns(), 0, TableUtil.TABLE_HEADER, null, scaler);
		updateHeaderCells(table.getColumns(), 0, TableUtil.COLUMN_HEADER, null, scaler);

		for (String groupName : groupNames)
		{
			updateHeaderCells(table.getColumns(), 0, TableUtil.COLUMN_GROUP_HEADER, groupName, scaler);
		}

		updateHeaderCells(table.getColumns(), 0, TableUtil.COLUMN_DETAIL, null, scaler);

		for (String groupName : groupNames)
		{
			updateFooterCells(table.getColumns(), 0, TableUtil.COLUMN_GROUP_FOOTER, groupName, scaler);
		}

		updateFooterCells(table.getColumns(), 0, TableUtil.COLUMN_FOOTER, null, scaler);
		updateFooterCells(table.getColumns(), 0, TableUtil.TABLE_FOOTER, null, scaler);

		updateCell((DesignBaseCell)table.getNoData(), 0, scaler);

		updateColumnWidths(table.getColumns(), 0, scaler);
	}


	/**
	 * Collects the names of the table groups for which the columns declare header or footer cells.
	 */
	private void collectGroupNames(List<BaseColumn> columns, Set<String> groupNames)
	{
		if (columns != null)
		{
			for (BaseColumn column : columns)
			{
				StandardBaseColumn baseColumn = (StandardBaseColumn)column;
				collectGroupCellNames(baseColumn.getGroupHeaders(), groupNames);
				collectGroupCellNames(baseColumn.getGroupFooters(), groupNames);

				if (column instanceof StandardColumnGroup)
				{
					collectGroupNames(((StandardColumnGroup)column).getColumns(), groupNames);
				}
			}
		}
	}


	/**
	 *
	 */
	private void collectGroupCellNames(List<GroupCell> groupCells, Set<String> groupNames)
	{
		if (groupCells != null)
		{
			for (GroupCell groupCell : groupCells)
			{
				groupNames.add(groupCell.getGroupName());
			}
		}
	}


	/**
	 * Scales the widths of a list of table columns as a chain of edges starting at the specified
	 * offset, so that the scaled columns still add up to the scaled width of their column group.
	 */
	private void updateColumnWidths(List<BaseColumn> columns, int offset, Scaler scaler)
	{
		if (columns != null)
		{
			int columnOffset = offset;

			for (BaseColumn column : columns)
			{
				StandardBaseColumn baseColumn = (StandardBaseColumn)column;
				Integer width = baseColumn.getWidth();

				if (column instanceof StandardColumnGroup)
				{
					updateColumnWidths(((StandardColumnGroup)column).getColumns(), columnOffset, scaler);
				}

				if (width != null)
				{
					baseColumn.setWidth(scaler.scaleSize(columnOffset, width));
					columnOffset += width;
				}
			}
		}
	}


	/**
	 * Scales the heights of the cells that a list of table columns declares for one of the header
	 * sections of a table, where the cell of a column group sits above the cells of its columns.
	 * Returns the total height of the section, measured in source resolution pixels.
	 */
	private int updateHeaderCells(List<BaseColumn> columns, int offset, int cellType, String groupName, Scaler scaler)
	{
		int sectionHeight = 0;

		for (BaseColumn column : columns)
		{
			DesignBaseCell cell = (DesignBaseCell)TableUtil.getCell(column, cellType, groupName);
			int cellHeight = getCellHeight(cell);
			int columnHeight = cellHeight;

			if (column instanceof StandardColumnGroup)
			{
				columnHeight +=
					updateHeaderCells(
						((StandardColumnGroup)column).getColumns(),
						offset + cellHeight,
						cellType,
						groupName,
						scaler
						);
			}

			updateCell(cell, offset, scaler);

			sectionHeight = Math.max(sectionHeight, columnHeight);
		}

		return sectionHeight;
	}


	/**
	 * Scales the heights of the cells that a list of table columns declares for one of the footer
	 * sections of a table, where the cell of a column group sits below the cells of its columns.
	 * Returns the total height of the section, measured in source resolution pixels.
	 */
	private int updateFooterCells(List<BaseColumn> columns, int offset, int cellType, String groupName, Scaler scaler)
	{
		int sectionHeight = 0;

		for (BaseColumn column : columns)
		{
			DesignBaseCell cell = (DesignBaseCell)TableUtil.getCell(column, cellType, groupName);
			int cellHeight = getCellHeight(cell);
			int columnsHeight = 0;

			if (column instanceof StandardColumnGroup)
			{
				columnsHeight =
					updateFooterCells(
						((StandardColumnGroup)column).getColumns(),
						offset,
						cellType,
						groupName,
						scaler
						);
			}

			updateCell(cell, offset + columnsHeight, scaler);

			sectionHeight = Math.max(sectionHeight, columnsHeight + cellHeight);
		}

		return sectionHeight;
	}


	/**
	 *
	 */
	private void updateCell(DesignBaseCell cell, int offset, Scaler scaler)
	{
		if (cell != null)
		{
			cell.setHeight(scaler.scaleSize(offset, cell.getHeight()));
			updateCommonAttributes(cell, scaler);
			updateChildren(cell.getChildren(), scaler);
		}
	}


	/**
	 *
	 */
	private int getCellHeight(DesignBaseCell cell)
	{
		return cell == null || cell.getHeight() == null ? 0 : cell.getHeight();
	}


	/**
	 * Converts pixel lengths from the source resolution to the target resolution.
	 */
	private static class Scaler
	{
		private final double factor;

		Scaler(double factor)
		{
			this.factor = factor;
		}

		/**
		 * Scales a length measured from the origin of its container.
		 */
		int scale(int length)
		{
			return (int)Math.floor(length * factor);
		}

		/**
		 *
		 */
		Integer scale(Integer length)
		{
			return length == null ? null : scale(length.intValue());
		}

		/**
		 *
		 */
		Float scale(Float length)
		{
			return length == null ? null : (float)(length.floatValue() * factor);
		}

		/**
		 * Scales a size that starts at the specified offset, as the distance between the scaled
		 * positions of its two edges. This keeps adjacent content adjacent after the conversion.
		 */
		int scaleSize(int offset, int size)
		{
			return scale(offset + size) - scale(offset);
		}

		/**
		 *
		 */
		Integer scaleSize(int offset, Integer size)
		{
			return size == null ? null : scaleSize(offset, size.intValue());
		}
	}
}
