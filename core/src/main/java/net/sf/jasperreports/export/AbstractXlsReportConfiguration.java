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
package net.sf.jasperreports.export;

import java.awt.Color;
import java.util.Map;

import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.export.type.ImageAnchorTypeEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class AbstractXlsReportConfiguration extends SimpleReportExportConfiguration implements XlsReportConfiguration
{
	private Boolean isOnePagePerSheet;
	private Boolean isRemoveEmptySpaceBetweenRows;
	private Boolean isRemoveEmptySpaceBetweenColumns;
	private Boolean isWhitePageBackground;
	private Boolean isDetectCellType;
	private Boolean isFontSizeFixEnabled;
	private Boolean isImageBorderFixEnabled;
	private Boolean isIgnoreGraphics;
	private Boolean isCollapseRowSpan;
	private Boolean isIgnoreCellBorder;
	private Boolean isIgnoreCellBackground;
	private Boolean isWrapText;
	private Boolean isCellLocked;
	private Boolean isCellHidden;
	private Integer maxRowsPerSheet;
	private Boolean isIgnorePageMargins;
	private String sheetHeaderLeft;
	private String sheetHeaderCenter;
	private String sheetHeaderRight;
	private String sheetFooterLeft;
	private String sheetFooterCenter;
	private String sheetFooterRight;
	private String password;
	private String singleSheetName;
	private String[] sheetNames;
	private Map<String,String> formatPatternsMap;
	private Boolean isIgnoreHyperlink;
	private Boolean isIgnoreAnchors;
	private Integer fitWidth;
	private Integer fitHeight;
	private Integer pageScale;
	private RunDirectionEnum sheetDirection;
	private Float columnWidthRatio;
	private Boolean isUseTimeZone;
	private Integer firstPageNumber;
	private Boolean isShowGridLines;
	private ImageAnchorTypeEnum imageAnchorType;
	private Boolean isAutoFitPageHeight;
	private Boolean isForcePageBreaks;
	private Boolean isShrinkToFit;
	private Boolean isIgnoreTextFormatting;
	private Color sheetTabColor;
	private Integer freezeRow;
	private String freezeColumn;
	private Integer printPageTopMargin;
	private Integer printPageLeftMargin;
	private Integer printPageBottomMargin;
	private Integer printPageRightMargin;
	private Integer printPageHeight;
	private Integer printPageWidth;
	private Integer printHeaderMargin;
	private Integer printFooterMargin;
	private PropertySuffix[] definedNames;
	private Boolean isAutoFitRow;

	
	/**
	 * 
	 */
	public AbstractXlsReportConfiguration()
	{
	}

	
	@Override
	public Boolean isOnePagePerSheet()
	{
		return isOnePagePerSheet;
	}

	
	/**
	 * 
	 */
	public void setOnePagePerSheet(Boolean isOnePagePerSheet)
	{
		this.isOnePagePerSheet = isOnePagePerSheet;
	}

	
	@Override
	public Boolean isRemoveEmptySpaceBetweenColumns()
	{
		return isRemoveEmptySpaceBetweenColumns;
	}

	
	/**
	 * 
	 */
	public void setRemoveEmptySpaceBetweenColumns(Boolean isRemoveEmptySpaceBetweenColumns)
	{
		this.isRemoveEmptySpaceBetweenColumns = isRemoveEmptySpaceBetweenColumns;
	}

	
	@Override
	public Boolean isRemoveEmptySpaceBetweenRows()
	{
		return isRemoveEmptySpaceBetweenRows;
	}

	
	/**
	 * 
	 */
	public void setRemoveEmptySpaceBetweenRows(Boolean isRemoveEmptySpaceBetweenRows)
	{
		this.isRemoveEmptySpaceBetweenRows = isRemoveEmptySpaceBetweenRows;
	}

	
	@Override
	public Boolean isWhitePageBackground()
	{
		return isWhitePageBackground;
	}

	
	/**
	 * 
	 */
	public void setWhitePageBackground(Boolean isWhitePageBackground)
	{
		this.isWhitePageBackground = isWhitePageBackground;
	}

	
	@Override
	public Boolean isDetectCellType()
	{
		return isDetectCellType;
	}

	
	/**
	 * 
	 */
	public void setDetectCellType(Boolean isDetectCellType)
	{
		this.isDetectCellType = isDetectCellType;
	}

	
	@Override
	public Boolean isFontSizeFixEnabled()
	{
		return isFontSizeFixEnabled;
	}

	
	/**
	 * 
	 */
	public void setFontSizeFixEnabled(Boolean isFontSizeFixEnabled)
	{
		this.isFontSizeFixEnabled = isFontSizeFixEnabled;
	}

	
	@Override
	public Boolean isImageBorderFixEnabled()
	{
		return isImageBorderFixEnabled;
	}

	
	/**
	 * 
	 */
	public void setImageBorderFixEnabled(Boolean isImageBorderFixEnabled)
	{
		this.isImageBorderFixEnabled = isImageBorderFixEnabled;
	}

	
	@Override
	public Boolean isIgnoreGraphics()
	{
		return isIgnoreGraphics;
	}

	
	/**
	 * 
	 */
	public void setIgnoreGraphics(Boolean isIgnoreGraphics)
	{
		this.isIgnoreGraphics = isIgnoreGraphics;
	}

	
	@Override
	public Boolean isCollapseRowSpan()
	{
		return isCollapseRowSpan;
	}

	
	/**
	 * 
	 */
	public void setCollapseRowSpan(Boolean isCollapseRowSpan)
	{
		this.isCollapseRowSpan = isCollapseRowSpan;
	}

	
	@Override
	public Boolean isIgnoreCellBorder()
	{
		return isIgnoreCellBorder;
	}

	
	/**
	 * 
	 */
	public void setIgnoreCellBorder(Boolean isIgnoreCellBorder)
	{
		this.isIgnoreCellBorder = isIgnoreCellBorder;
	}

	
	@Override
	public Boolean isIgnoreCellBackground()
	{
		return isIgnoreCellBackground;
	}

	
	/**
	 * 
	 */
	public void setIgnoreCellBackground(Boolean isIgnoreCellBackground)
	{
		this.isIgnoreCellBackground = isIgnoreCellBackground;
	}

	
	@Override
	public Boolean isWrapText()
	{
		return isWrapText;
	}

	
	/**
	 * 
	 */
	public void setWrapText(Boolean isWrapText)
	{
		this.isWrapText = isWrapText;
	}

	
	@Override
	public Boolean isCellLocked()
	{
		return isCellLocked;
	}

	
	/**
	 * 
	 */
	public void setCellLocked(Boolean isCellLocked)
	{
		this.isCellLocked = isCellLocked;
	}

	
	@Override
	public Boolean isCellHidden()
	{
		return isCellHidden;
	}

	
	/**
	 * 
	 */
	public void setCellHidden(Boolean isCellHidden)
	{
		this.isCellHidden = isCellHidden;
	}

	
	@Override
	public Integer getMaxRowsPerSheet()
	{
		return maxRowsPerSheet;
	}

	
	/**
	 * 
	 */
	public void setMaxRowsPerSheet(Integer maxRowsPerSheet)
	{
		this.maxRowsPerSheet = maxRowsPerSheet;
	}

	
	@Override
	public Boolean isIgnorePageMargins()
	{
		return isIgnorePageMargins;
	}

	
	/**
	 * 
	 */
	public void setIgnorePageMargins(Boolean isIgnorePageMargins)
	{
		this.isIgnorePageMargins = isIgnorePageMargins;
	}

	
	@Override
	public String getSheetHeaderLeft()
	{
		return sheetHeaderLeft;
	}

	
	/**
	 * 
	 */
	public void setSheetHeaderLeft(String sheetHeaderLeft)
	{
		this.sheetHeaderLeft = sheetHeaderLeft;
	}

	
	@Override
	public String getSheetHeaderCenter()
	{
		return sheetHeaderCenter;
	}

	
	/**
	 * 
	 */
	public void setSheetHeaderCenter(String sheetHeaderCenter)
	{
		this.sheetHeaderCenter = sheetHeaderCenter;
	}

	
	@Override
	public String getSheetHeaderRight()
	{
		return sheetHeaderRight;
	}

	
	/**
	 * 
	 */
	public void setSheetHeaderRight(String sheetHeaderRight)
	{
		this.sheetHeaderRight = sheetHeaderRight;
	}

	
	@Override
	public String getSheetFooterLeft()
	{
		return sheetFooterLeft;
	}

	
	/**
	 * 
	 */
	public void setSheetFooterLeft(String sheetFooterLeft)
	{
		this.sheetFooterLeft = sheetFooterLeft;
	}

	
	@Override
	public String getSheetFooterCenter()
	{
		return sheetFooterCenter;
	}

	
	/**
	 * 
	 */
	public void setSheetFooterCenter(String sheetFooterCenter)
	{
		this.sheetFooterCenter = sheetFooterCenter;
	}

	
	@Override
	public String getSheetFooterRight()
	{
		return sheetFooterRight;
	}

	
	/**
	 * 
	 */
	public void setSheetFooterRight(String sheetFooterRight)
	{
		this.sheetFooterRight = sheetFooterRight;
	}

	
	@Override
	public String getPassword()
	{
		return password;
	}

	
	/**
	 * 
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	
	@Override
	public String getSingleSheetName()
	{
		return singleSheetName;
	}

	
	/**
	 * 
	 */
	public void setSingleSheetName(String singleSheetName)
	{
		this.singleSheetName = singleSheetName;
	}

	
	@Override
	public String[] getSheetNames()
	{
		return sheetNames;
	}

	
	/**
	 * 
	 */
	public void setSheetNames(String[] sheetNames)
	{
		this.sheetNames = sheetNames;
	}

	
	@Override
	public Map<String, String> getFormatPatternsMap()
	{
		return formatPatternsMap;
	}

	
	/**
	 * 
	 */
	public void setFormatPatternsMap(Map<String, String> formatPatternsMap)
	{
		this.formatPatternsMap = formatPatternsMap;
	}

	
	@Override
	public Boolean isIgnoreHyperlink()
	{
		return isIgnoreHyperlink;
	}

	
	/**
	 * 
	 */
	public void setIgnoreHyperlink(Boolean isIgnoreHyperlink)
	{
		this.isIgnoreHyperlink = isIgnoreHyperlink;
	}

	
	@Override
	public Boolean isIgnoreAnchors()
	{
		return isIgnoreAnchors;
	}

	
	/**
	 * 
	 */
	public void setIgnoreAnchors(Boolean isIgnoreAnchors)
	{
		this.isIgnoreAnchors = isIgnoreAnchors;
	}

	
	@Override
	public Integer getFitWidth()
	{
		return fitWidth;
	}

	
	/**
	 * 
	 */
	public void setFitWidth(Integer fitWidth)
	{
		this.fitWidth = fitWidth;
	}

	
	@Override
	public Integer getFitHeight()
	{
		return fitHeight;
	}

	
	/**
	 * 
	 */
	public void setFitHeight(Integer fitHeight)
	{
		this.fitHeight = fitHeight;
	}

	
	@Override
	public Integer getPageScale()
	{
		return pageScale;
	}

	
	/**
	 * 
	 */
	public void setPageScale(Integer pageScale)
	{
		this.pageScale = pageScale;
	}

	
	@Override
	public RunDirectionEnum getSheetDirection()
	{
		return sheetDirection;
	}

	
	/**
	 * 
	 */
	public void setSheetDirection(RunDirectionEnum sheetDirection)
	{
		this.sheetDirection = sheetDirection;
	}

	
	@Override
	public Float getColumnWidthRatio()
	{
		return columnWidthRatio;
	}

	
	/**
	 * 
	 */
	public void setColumnWidthRatio(Float columnWidthRatio)
	{
		this.columnWidthRatio = columnWidthRatio;
	}

	
	@Override
	public Boolean isUseTimeZone()
	{
		return isUseTimeZone;
	}

	
	/**
	 * 
	 */
	public void setUseTimeZone(Boolean isUseTimeZone)
	{
		this.isUseTimeZone = isUseTimeZone;
	}

	
	@Override
	public Integer getFirstPageNumber()
	{
		return firstPageNumber;
	}

	
	/**
	 * 
	 */
	public void setFirstPageNumber(Integer firstPageNumber)
	{
		this.firstPageNumber = firstPageNumber;
	}

	
	@Override
	public Boolean isShowGridLines()
	{
		return isShowGridLines;
	}

	
	/**
	 * 
	 */
	public void setShowGridLines(Boolean isShowGridLines)
	{
		this.isShowGridLines = isShowGridLines;
	}

	
	@Override
	public ImageAnchorTypeEnum getImageAnchorType()
	{
		return imageAnchorType;
	}

	
	/**
	 * 
	 */
	public void setImageAnchorType(ImageAnchorTypeEnum imageAnchorType)
	{
		this.imageAnchorType = imageAnchorType;
	}
	
	@Override
	public Boolean isAutoFitPageHeight()
	{
		return isAutoFitPageHeight;
	}
	
	/**
	 * 
	 */
	public void setAutoFitPageHeight(Boolean isAutoFitPageHeight)
	{
		this.isAutoFitPageHeight = isAutoFitPageHeight;
	}
	
	@Override
	public Boolean isForcePageBreaks()
	{
		return isForcePageBreaks;
	}
	
	/**
	 * 
	 */
	public void setForcePageBreaks(Boolean isForcePageBreaks)
	{
		this.isForcePageBreaks = isForcePageBreaks;
	}


	@Override
	public Boolean isShrinkToFit() 
	{
		return isShrinkToFit;
	}


	public void setShrinkToFit(Boolean isShrinkToFit) 
	{
		this.isShrinkToFit = isShrinkToFit;
	}


	@Override
	public Boolean isIgnoreTextFormatting() 
	{
		return isIgnoreTextFormatting;
	}


	/**
	 * 
	 */
	public void setIgnoreTextFormatting(Boolean isIgnoreTextFormatting) 
	{
		this.isIgnoreTextFormatting = isIgnoreTextFormatting;
	}


	@Override
	public Color getSheetTabColor() 
	{
		return sheetTabColor;
	}
	
	public void setSheetTabColor(Color tabColor)
	{
		this.sheetTabColor = tabColor;
	}


	@Override
	public Integer getFreezeRow() 
	{
		return freezeRow;
	}

	public void setFreezeRow(Integer freezeRow)
	{
		this.freezeRow = freezeRow;
	}

	@Override
	public String getFreezeColumn() 
	{
		return freezeColumn;
	}

	public void setFreezeColumn(String freezeColumn)
	{
		this.freezeColumn = freezeColumn;
	}
	
	@Override
	public Integer getPrintPageTopMargin() 
	{
		return printPageTopMargin;
	}
	
	public void setPrintPageTopMargin(Integer printPageTopMargin)
	{
		this.printPageTopMargin = printPageTopMargin;
	}
	
	@Override
	public Integer getPrintPageLeftMargin() 
	{
		return printPageLeftMargin;
	}
	
	public void setPrintPageLeftMargin(Integer printPageLeftMargin)
	{
		this.printPageLeftMargin = printPageLeftMargin;
	}
	
	@Override
	public Integer getPrintPageBottomMargin() 
	{
		return printPageBottomMargin;
	}
	
	public void setPrintPageBottomMargin(Integer printPageBottomMargin)
	{
		this.printPageBottomMargin = printPageBottomMargin;
	}
	
	@Override
	public Integer getPrintPageRightMargin() 
	{
		return printPageRightMargin;
	}
	
	public void setPrintPageRightMargin(Integer printPageRightMargin)
	{
		this.printPageRightMargin = printPageRightMargin;
	}
	
	@Override
	public Integer getPrintPageHeight() 
	{
		return printPageHeight;
	}
	
	public void setPrintPageHeight(Integer printPageHeight)
	{
		this.printPageHeight = printPageHeight;
	}
	
	@Override
	public Integer getPrintPageWidth() 
	{
		return printPageWidth;
	}
	
	public void setPrintPageWidth(Integer printPageWidth)
	{
		this.printPageWidth = printPageWidth;
	}
	
	@Override
	public Integer getPrintHeaderMargin() 
	{
		return printHeaderMargin;
	}
	
	public void setPrintHeaderMargin(Integer printHeaderMargin)
	{
		this.printHeaderMargin = printHeaderMargin;
	}
	
	@Override
	public Integer getPrintFooterMargin() 
	{
		return printFooterMargin;
	}

	public void setPrintFooterMargin(Integer printFooterMargin)
	{
		this.printFooterMargin = printFooterMargin;
	}
	
	@Override
	public PropertySuffix[] getDefinedNames()
	{
		return definedNames;
	}

	
	/**
	 * 
	 */
	public void setDefinedNames(PropertySuffix[] definedNames)
	{
		this.definedNames = definedNames;
	}

	@Override
	public Boolean isAutoFitRow()
	{
		return isAutoFitRow;
	}
	
	public void setAutoFitRow(Boolean isAutoFitRow)
	{
		this.isAutoFitRow = isAutoFitRow;
	}
}
