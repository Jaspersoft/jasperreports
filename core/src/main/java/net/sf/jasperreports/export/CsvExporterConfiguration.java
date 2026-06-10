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
package net.sf.jasperreports.export;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvMetadataExporter;
import net.sf.jasperreports.export.annotations.ExporterProperty;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Interface containing settings used by the CSV exporters.
 *
 * @see JRCsvExporter
 * @see JRCsvMetadataExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface CsvExporterConfiguration extends ExporterConfiguration
{
	/**
	 * Property whose value is used as default for the {@link #getFieldDelimiter()} export configuration setting.
	 * The default is a comma character.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.COMMA,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1
			)
	public static final String PROPERTY_FIELD_DELIMITER = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.field.delimiter";

	/**
	 * Property whose value is used as default for the {@link #getRecordDelimiter()} export configuration setting.
	 * The default is a character return (\n).
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.NEWLINE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_2_0_1
			)
	public static final String PROPERTY_RECORD_DELIMITER = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.record.delimiter";

	/**
	 * Property whose value is used as default for the {@link #isWriteBOM()} export configuration setting.
	 * The default is <code>false</code>.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_1_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_WRITE_BOM = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.write.bom";

	/**
	 * Property whose value is used as default for the {@link #getFieldEnclosure()} export configuration setting.
	 * Default value is &quot;.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.QUOTES,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_2_1
			)
	public static final String PROPERTY_FIELD_ENCLOSURE = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.field.enclosure";
	
	/**
	 * Property whose value is used as default for the {@link #getForceFieldEnclosure()} export configuration setting.
	 * Default value is <code>false</code>.
	 * 
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_2_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_FORCE_FIELD_ENCLOSURE = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.force.field.enclosure";

	/**
	 * Property whose value is used as default for the {@link #getEscapeFormula()} export configuration setting.
	 * Default value is <code>false</code>.
	 * <p/>
	 * As a security control against CSV formula injection, enabling formula escaping at context level acts as
	 * an enforced floor that cannot be turned off. The property can also be set at report level, but only to
	 * strengthen the behavior by enabling escaping; a report-level value of <code>false</code> cannot disable
	 * escaping that is enforced at context level.
	 *
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_18_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_ESCAPE_FORMULA = JRPropertiesUtil.PROPERTY_PREFIX + "export.csv.escape.formula";
	
	/**
	 * Returns the string representing the character or sequence of characters to be used to delimit two fields on the same record.
	 * @see #PROPERTY_FIELD_DELIMITER
	 */
	@ExporterProperty(PROPERTY_FIELD_DELIMITER)
	public String getFieldDelimiter();

	/**
	 * Returns a string representing the character to be used to enclose a field value on a record. 
	 * If this is a multi-character string, the first character only will be taken into account. 
	 * White spaces are not considered.
	 * <p/>
	 * Default value is &quot;.
	 * @see #PROPERTY_FIELD_ENCLOSURE
	 */
	@ExporterProperty(PROPERTY_FIELD_ENCLOSURE)
	public String getFieldEnclosure();
	
	/**
	 * Returns a flag that enforces all exported field values to be enclosed within 
	 * a pair of enclosure characters (usually a pair of quotes: &quot;&quot;). 
	 * <p/>
	 * Default value is <code>false</code>.
	 * @see #PROPERTY_FORCE_FIELD_ENCLOSURE
	 */
	@ExporterProperty(
			value=PROPERTY_FORCE_FIELD_ENCLOSURE, 
			booleanDefault=false
			)
	public Boolean getForceFieldEnclosure();

	/**
	 * Returns the string representing the character or sequence of characters to be used to delimit two records.
	 * @see #PROPERTY_RECORD_DELIMITER
	 */
	@ExporterProperty(PROPERTY_RECORD_DELIMITER)
	public String getRecordDelimiter();

	/**
	 * Specifies whether the exporter should put a BOM character at the beginning of the output.
	 * @see #PROPERTY_WRITE_BOM
	 */
	@ExporterProperty(PROPERTY_WRITE_BOM)
	public Boolean isWriteBOM();
	
	/**
	 * Returns a flag that enforces field values containing formula to be escaped in order to be interpreted as pure text. 
	 * <p/>
	 * Default value is <code>false</code>.
	 * <p/>
	 * As a security control against CSV formula injection, formula escaping enabled via the
	 * {@link #PROPERTY_ESCAPE_FORMULA} property at context level acts as an enforced floor that cannot be
	 * turned off. This configuration setting can only strengthen the behavior by additionally enabling
	 * escaping; it cannot disable escaping that is enforced at context level.
	 * @see #PROPERTY_ESCAPE_FORMULA
	 */
	@ExporterProperty(
			value=PROPERTY_ESCAPE_FORMULA, 
			booleanDefault=false
			)
	public Boolean getEscapeFormula();
	
}
