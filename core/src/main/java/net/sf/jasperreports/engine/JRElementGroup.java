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
package net.sf.jasperreports.engine;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignElementGroup;


/**
 * Groups several report elements. Report elements placed in any report section can be arranged in multiple
 * nested groups. The only reason you might have for grouping your elements is to be able to customize the
 * stretch behavior of the report elements.
 * <p/>
 * One possible value of the <code>stretchType</code> attribute, available for all report elements, is
 * <code>RelativeToTallestObject</code>. If you choose this option, the engine tries to identify the
 * object from the same group as the current graphic element that has suffered the biggest
 * amount of stretch. It will then adapt the height of the current report element to the height
 * of this tallest element of the group.
 * <p/>
 * However, for this to work, you must group your elements. To do this, use the
 * <code>&lt;elementGroup&gt;</code> and <code>&lt;/elementGroup&gt;</code> tags to mark the elements that 
 * are part of the same group.
 * <p/>
 * Report sections are element groups themselves, so all report elements placed directly in a
 * containing band are part of the same default element group, which is the band itself. As
 * such, for these report elements, <code>stretchType="RelativeToTallestObject"</code> and
 * <code>stretchType= "RelativeToBandHeight"</code> have the same effect.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	"elements"
	})
@JsonTypeName("elementGroup")
@JsonDeserialize(as = JRDesignElementGroup.class)
public interface JRElementGroup extends JRChild
{


	/**
	 * Gets a list of all direct children elements or elements groups.
	 */
	@JsonGetter("elements")
	@JacksonXmlProperty(localName = "element")
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<JRChild> getChildren();

	/**
	 * Gets the parent element group.
	 * @return an instance of this class, or null if this is the root group.
	 */
	@JsonIgnore
	public JRElementGroup getElementGroup();

	/**
	 * Gets an array containing all the elements and element groups in the hierarchy.
	 */
	@JsonIgnore
	public JRElement[] getElements();


	/**
	 * Gets an element from this group, based on its element key.
	 */
	public JRElement getElementByKey(String key);


}
