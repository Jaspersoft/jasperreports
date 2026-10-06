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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import net.sf.jasperreports.crosstabs.JRCrosstab;

/**
 * An abstract representation of a report element. All report elements implement this interface. The interface contains
 * constants and methods that apply to all report elements.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "kind")
@JsonSubTypes({
	@JsonSubTypes.Type(value = JRStaticText.class),
	@JsonSubTypes.Type(value = JRTextField.class),
	@JsonSubTypes.Type(value = JRLine.class),
	@JsonSubTypes.Type(value = JRRectangle.class),
	@JsonSubTypes.Type(value = JREllipse.class),
	@JsonSubTypes.Type(value = JRImage.class),
	@JsonSubTypes.Type(value = JRElementGroup.class),
	@JsonSubTypes.Type(value = JRFrame.class),
	@JsonSubTypes.Type(value = JRCrosstab.class),
	@JsonSubTypes.Type(value = JRSubreport.class),
	@JsonSubTypes.Type(value = JRGenericElement.class),
	@JsonSubTypes.Type(value = JRBreak.class),
	//@JsonSubTypes.Type(value = JRCellContents.class),
	@JsonSubTypes.Type(value = JRComponentElement.class)
})
public interface JRChild extends JRVisitable, JRCloneable
{
	/**
	 * 
	 */
	public Object clone(JRElementGroup parentGroup);
}
