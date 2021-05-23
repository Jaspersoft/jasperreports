/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.properties;

import net.sf.jasperreports.engine.JRConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PropertyConstants
{
	
	String PREFIX_CATEGORY = "net.sf.jasperreports.metadata.property.category:";
	
	String CATEGORY_DATA_SOURCE = PREFIX_CATEGORY + "data.source";
	
	String CATEGORY_COMPILE = PREFIX_CATEGORY + "compile";
	
	String CATEGORY_DESIGN = PREFIX_CATEGORY + "design";
	
	String CATEGORY_FILL = PREFIX_CATEGORY + "fill";
	
	String CATEGORY_GOVERNOR = PREFIX_CATEGORY + "governor";
	
	String CATEGORY_CROSSTAB = PREFIX_CATEGORY + "crosstab";
	
	String CATEGORY_BARCODE = PREFIX_CATEGORY + "barcode";
	
	String CATEGORY_MAP = PREFIX_CATEGORY + "map";
	
	String CATEGORY_LIST = PREFIX_CATEGORY + "list";

	String CATEGORY_TABLE = PREFIX_CATEGORY + "table";
	
	String CATEGORY_CHART_THEME = PREFIX_CATEGORY + "chart.theme";
	
	String CATEGORY_EXPORT = PREFIX_CATEGORY + "export";
	
	String CATEGORY_EXTENSIONS = PREFIX_CATEGORY + "extensions";
	
	String CATEGORY_REPOSITORY = PREFIX_CATEGORY + "repository";
	
	String CATEGORY_OTHER = PREFIX_CATEGORY + "other";
	
	String CATEGORY_PHANTOM_JS = PREFIX_CATEGORY + "phantomjs";
	
	String CATEGORY_CHROME = PREFIX_CATEGORY + "chrome";
	
	String CATEGORY_DATA_CACHE = PREFIX_CATEGORY + "data.cache";
	
	String CATEGORY_WEB_UTIL = PREFIX_CATEGORY + "web.util";

	String BOOLEAN_FALSE = "false";
	
	String BOOLEAN_TRUE = "true";
	
	String UTF_8 = "UTF-8";
	
	String COMMA = ",";
	
	String QUOTES = "\"";
	
	String NEWLINE = "\\n";
	
	String SAME_DOMAIN = "sameDomain";
	
	String COLLAPSE = "collapse";
	
	String PIXEL_UNIT = "px";
	
	String DEFAULT = "default";
	
	String QUESTION_MARK = "?";
	
	String MOVE_NO_SIZE = "MoveNoSize";
	
	String LTR = "LTR";
	
	String COMPONENT_KEY_QUALIFICATION_SEPARATOR = ":";

	String VERSION_1_0_0 = "1.0.0";

	String VERSION_1_2_0 = "1.2.0";

	String VERSION_1_2_2 = "1.2.2";

	String VERSION_1_2_3 = "1.2.3";

	String VERSION_1_2_8 = "1.2.8";

	String VERSION_1_3_0 = "1.3.0";

	String VERSION_1_3_1 = "1.3.1";

	String VERSION_1_3_4 = "1.3.4";

	String VERSION_2_0_0 = "2.0.0";

	String VERSION_2_0_1 = "2.0.1";

	String VERSION_2_0_2 = "2.0.2";

	String VERSION_2_0_3 = "2.0.3";

	String VERSION_2_0_5 = "2.0.5";

	String VERSION_3_0_0 = "3.0.0";

	String VERSION_3_0_1 = "3.0.1";

	String VERSION_3_1_0 = JRConstants.VERSION_3_1_0;

	String VERSION_3_1_2 = JRConstants.VERSION_3_1_2;

	String VERSION_3_1_3 = "3.1.3";

	String VERSION_3_1_4 = JRConstants.VERSION_3_1_4;

	String VERSION_3_5_0 = JRConstants.VERSION_3_5_0;

	String VERSION_3_5_1 = JRConstants.VERSION_3_5_1;

	String VERSION_3_5_2 = JRConstants.VERSION_3_5_2;

	String VERSION_3_5_3 = JRConstants.VERSION_3_5_3;

	String VERSION_3_5_4 = "3.5.4";

	String VERSION_3_6_1 = JRConstants.VERSION_3_6_1;

	String VERSION_3_6_2 = JRConstants.VERSION_3_6_2;

	String VERSION_3_7_0 = "3.7.0";

	String VERSION_3_7_1 = "3.7.1";

	String VERSION_3_7_3 = "3.7.3";

	String VERSION_3_7_5 = JRConstants.VERSION_3_7_5;

	String VERSION_3_7_6 = "3.7.6";

	String VERSION_4_0_0 = JRConstants.VERSION_4_0_0;

	String VERSION_4_0_1 = "4.0.1";

	String VERSION_4_0_2 = JRConstants.VERSION_4_0_2;

	String VERSION_4_1_1 = JRConstants.VERSION_4_1_1;

	String VERSION_4_1_2 = "4.1.2";

	String VERSION_4_1_3 = JRConstants.VERSION_4_1_3;

	String VERSION_4_5_0 = JRConstants.VERSION_4_5_0;

	String VERSION_4_5_1 = "4.5.1";

	String VERSION_4_6_0 = JRConstants.VERSION_4_6_0;

	String VERSION_4_7_0 = JRConstants.VERSION_4_7_0;

	String VERSION_4_7_1 = "4.7.1";

	String VERSION_4_8_0 = JRConstants.VERSION_4_8_0;

	String VERSION_5_0_0 = "5.0.0";
	
	String VERSION_5_0_1 = JRConstants.VERSION_5_0_1;

	String VERSION_5_0_4 = JRConstants.VERSION_5_0_4;

	String VERSION_5_1_2 = "5.1.2";
	
	String VERSION_5_2_0 = "5.2.0";

	String VERSION_5_5_0 = JRConstants.VERSION_5_5_0;
	
	String VERSION_5_5_1 = "5.5.1";

	String VERSION_5_5_2 = JRConstants.VERSION_5_5_2;

	String VERSION_5_6_0 = "5.6.0";

	String VERSION_6_0_0 = JRConstants.VERSION_6_0_0;

	String VERSION_6_0_2 = JRConstants.VERSION_6_0_2;
	
	String VERSION_6_0_3 = "6.0.3";

	String VERSION_6_0_4 = "6.0.4";

	String VERSION_6_1_0 = "6.1.0";

	String VERSION_6_1_1 = JRConstants.VERSION_6_1_1;

	String VERSION_6_1_2 = "6.1.2";

	String VERSION_6_2_0 = JRConstants.VERSION_6_2_0;

	String VERSION_6_2_1 = JRConstants.VERSION_6_2_1;

	String VERSION_6_2_2 = JRConstants.VERSION_6_2_2;

	String VERSION_6_3_0 = JRConstants.VERSION_6_3_0;

	String VERSION_6_3_1 = JRConstants.VERSION_6_3_1;
	
	String VERSION_6_4_0 = JRConstants.VERSION_6_4_0;

	String VERSION_6_4_3 = JRConstants.VERSION_6_4_3;

	String VERSION_6_5_0 = "6.5.0";

	String VERSION_6_5_1 = JRConstants.VERSION_6_5_1;

	String VERSION_6_6_0 = "6.6.0";

	String VERSION_6_8_0 = "6.8.0";
	
	String VERSION_6_8_1 = "6.8.1";

	String VERSION_6_9_0 = "6.9.0";

	String VERSION_6_11_0 = JRConstants.VERSION_6_11_0;

	String VERSION_6_12_0 = JRConstants.VERSION_6_12_0;

	String VERSION_6_13_0 = JRConstants.VERSION_6_13_0;

}
