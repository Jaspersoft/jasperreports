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
package net.sf.jasperreports.engine.type;

/**
 * There are two main reasons for an element to stretch.
 * <p>
 * First, there is the natural stretch of the element, which is caused by the element growing naturally in order to display its entire content.
 * This is the case with text fields that have isStretchWithOverflow set to true and thus growing in height in order to display all the rows of text 
 * that the value of their text field expression provides at runtime.
 * <p>
 * Secondly, there is the forced stretch of an element, which in addition to its natural growth required by its own content, needs to further grow
 * to match the growing height of some other element that it is put in relation with at report design time.
 * <p>
 * A forced stretch can be imposed to elements that are part of element groups (JRElementGroup) or element containers (bands, frames, table cells, list cells, etc.).
 * The forced stretch also comes in two flavors.
 * <br>
 * There is element group stretch and container stretch.
 * <br>
 * Element group stretch is forced upon an element by the natural growth of the other elements in the same group.
 * <br>
 * The container stretch is forced upon an element by all types of growth that the container itself suffers, 
 * including forced container stretch imposed onto the container by its own parent container.
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum StretchTypeEnum implements NamedEnum
{
	/**
	 * A constant indicating that the element preserves its original specified height.
	 */
	NO_STRETCH("NoStretch"),//FIXMEENUM check all 0 constants for initialization
	
	/**
	 * Constant used for specifying that the element will adapt its height to match the changing 
	 * height of the overall element group it is part of, but without taking into account the fact that the Y position of the 
	 * element within the group has probably changed due to floating and/or
	 * collapsing white space above it. This position change is not compensated for, which might result in the element distance 
	 * to the group's initial bottom edge increasing or diminishing.
	 * <br>
	 * Like all element group based types of stretch, the amount of stretch forced onto the element comes only from the natural
	 * stretch of the sibling elements in the same group. 
	 */
	ELEMENT_GROUP_HEIGHT("ElementGroupHeight"),

	/**
	 * Constant used for specifying that the element will adapt its height to match the changing 
	 * height of the overall element group it is part of, but also taking into account the fact that the Y position of the 
	 * element within the group has probably changed due to floating and/or
	 * collapsing white space above it and should be compensated for, resulting in the element's distance to the group's initial 
	 * bottom edge being preserved.
	 * <br>
	 * Like all element group based types of stretch, the amount of stretch forced onto the element comes only from the natural
	 * stretch of the sibling elements in the same group. 
	 */
	ELEMENT_GROUP_BOTTOM("ElementGroupBottom"),

	/**
	 * Constant used for specifying that the element will adapt its height to match the new 
	 * height of the container it is placed on, which has been affected by stretch, but without taking
	 * into account the fact that the Y position of the element within the container has probably changed due to floating and/or
	 * collapsing white space above it. This position change is not compensated for, which might result in the element distance 
	 * to the container's bottom edge increasing or diminishing. It could even happen that the element bottom edge goes beyond
	 * container bottom edge and thus the element will no longer render at all.
	 * <br>
	 * Like all container based types of stretch, the amount of stretch forced onto the element can come from both the natural
	 * stretch of the sibling elements in the same container, or from the container's own forced stretch imposed onto it by its own parent container. 
	 */
	CONTAINER_HEIGHT("ContainerHeight"),
	
	/**
	 * Constant used for specifying that the element will adapt its height to match the new 
	 * height of the container it is placed on, which has been affected by stretch, but also taking
	 * into account the fact that the Y position of the element has probably changed and should be compensated for,
	 * resulting in the element's distance to container bottom being preserved.
	 * <br>
	 * Like all container based types of stretch, the amount of stretch forced onto the element can come from both the natural
	 * stretch of the sibling elements in the same container, or from the container's own forced stretch imposed onto it by its own parent container. 
	 */
	CONTAINER_BOTTOM("ContainerBottom");
	
	/**
	 *
	 */
	private final transient String name;

	private StretchTypeEnum(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 *
	 */
	public static StretchTypeEnum getByName(String name)
	{
		if (RELATIVE_TO_TALLEST_OBJECT_deprecated.equals(name))
		{
			return ELEMENT_GROUP_HEIGHT;
		}
		if (RELATIVE_TO_BAND_HEIGHT_deprecated.equals(name))
		{
			return CONTAINER_HEIGHT;
		}
		return EnumUtil.getEnumByName(values(), name);
	}


	public static final String RELATIVE_TO_TALLEST_OBJECT_deprecated = "RelativeToTallestObject";
	public static final String RELATIVE_TO_BAND_HEIGHT_deprecated = "RelativeToBandHeight";
	
	/**
	 *
	 */
	public static StretchTypeEnum getValueOrDefault(StretchTypeEnum value)
	{
		return value == null ? NO_STRETCH : value;
	}
}
