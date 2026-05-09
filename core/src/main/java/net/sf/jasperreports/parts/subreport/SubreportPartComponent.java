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
package net.sf.jasperreports.parts.subreport;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRSubreportParameter;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.parts.PartComponentsExtensionsRegistryFactory;

/**
 * Subreport part component interface.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"usingCache",
	"parametersMapExpression",
	"parameter",
	"returnValue",
	"expression"
	})
@JsonTypeName(PartComponentsExtensionsRegistryFactory.SUBREPORT_PART_COMPONENT_NAME)
@JsonDeserialize(as = StandardSubreportPartComponent.class)
public interface SubreportPartComponent extends PartComponent, JRCloneable
{

	/**
	 *
	 */
	public JRExpression getParametersMapExpression();

	/**
	 *
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_parameter)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRSubreportParameter[] getParameters();

	/**
	 * Returns the list of subreport copied values.
	 *
	 * @return the list of subreport copied values.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_returnValue)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRSubreportReturnValue[] getReturnValues();

	/**
	 *
	 */
	public JRExpression getExpression();
	
	/**
	 * Indicates if the engine is loading the current subreport from cache.
	 * Implementations of this method return the actual value for the internal flag that was explicitly 
	 * set on this subreport.
	 * @return Boolean.TRUE if the subreport should be loaded from cache, Boolean.FALSE otherwise 
	 * or null in case the flag was never explicitly set on this subreport element
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getUsingCache();
	
	/**
	 * Specifies if the engine should be loading the current subreport from cache. If set to Boolean.TRUE, the reporting engine
	 * will try to recognize previously loaded subreports using their specified source. For example, it will recognize
	 * an subreport if the subreport source is a file name that it has already loaded, or if it is the same URL.
	 * <p>
	 * If set to null, the engine will rely on some default value which depends on the type of the subreport expression.
	 * The cache is turned on by default only for subreports that have <tt>java.lang.String</tt> objects in their expressions.
	 */
	public void setUsingCache(Boolean isUsingCache);
	
}