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
package net.sf.jasperreports.charts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.engine.JRElementDataset;


/**
 * Datasets are used to represent the actual data needed to generate a chart. The dataset structure may vary with each chart type. This
 * is the superinterface for all datasets and contains common dataset properties.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	"resetType",
	"resetGroup",
	"incrementType",
	"incrementGroup",
	"datasetRun",
	"incrementWhenExpression"
	})
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonSubTypes({
	@JsonSubTypes.Type(value = JRCategoryDataset.class),
	@JsonSubTypes.Type(value = JRGanttDataset.class),
	@JsonSubTypes.Type(value = JRHighLowDataset.class),
	@JsonSubTypes.Type(value = JRPieDataset.class),
	@JsonSubTypes.Type(value = JRTimePeriodDataset.class),
	@JsonSubTypes.Type(value = JRTimeSeriesDataset.class),
	@JsonSubTypes.Type(value = JRValueDataset.class),
	@JsonSubTypes.Type(value = JRXyDataset.class),
	@JsonSubTypes.Type(value = JRXyzDataset.class)
})
public interface JRChartDataset extends JRElementDataset
{
	public static final byte PIE_DATASET = 1;
	public static final byte CATEGORY_DATASET = 2;
	public static final byte XY_DATASET = 3;
	public static final byte XYZ_DATASET = 4;
	public static final byte TIMEPERIOD_DATASET = 5;
	public static final byte TIMESERIES_DATASET = 6;
	public static final byte HIGHLOW_DATASET = 7;
	public static final byte VALUE_DATASET = 8;
	public static final byte GANTT_DATASET = 9;
	
	/**
	 * Gets the dataset type. Must be one of the dataset type constants defined in this class.
	 */
	@JsonIgnore
	public byte getDatasetType();

	
	/**
	 * Validates the dataset using a verifier.
	 * <p>
	 * Broken rules are collected by the verifier.
	 * </p>
	 * 
	 * @param verifier the verifier to use for validation
	 */
	public void validate(ChartsVerifier verifier);
	

	/**
	 * 
	 */
	public void collectExpressions(ChartsExpressionCollector collector);
}
