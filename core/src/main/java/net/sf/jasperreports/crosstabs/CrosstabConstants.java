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
package net.sf.jasperreports.crosstabs;

import net.sf.jasperreports.engine.JRPropertiesUtil;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface CrosstabConstants
{
	public static final String PROPERTY_CROSSTAB_ID = JRPropertiesUtil.PROPERTY_PREFIX + "export.crosstab.crosstabId";

	public static final String ATTRIBUTE_CROSSTAB_ID = "data-jrxtid";

	public static final String PROPERTY_COLUMN_INDEX = JRPropertiesUtil.PROPERTY_PREFIX + "export.crosstab.columnIndex";

	public static final String ATTRIBUTE_COLUMN_INDEX = "data-jrxtcolidx";

	public static final String ELEMENT_PARAMETER_CROSSTAB_ID = "crosstabId";

	public static final String ELEMENT_PARAMETER_CROSSTAB_FRAGMENT_ID = "crosstabFragmentId";

	public static final String ELEMENT_PARAMETER_START_COLUMN_INDEX = "startColumnIndex";

	public static final String ELEMENT_PARAMETER_ROW_GROUPS = "rowGroups";

	public static final String ELEMENT_PARAMETER_DATA_COLUMNS = "dataColumns";

	public static final String ELEMENT_PARAMETER_FLOATING_HEADERS = "hasFloatingHeaders";
}
