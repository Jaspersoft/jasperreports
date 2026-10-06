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

import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRPrintHyperlink
{

	/**
	 * Properties with this suffix are used to specify whether this hyperlink should be ignored at export time
	 */
	public static final String PROPERTY_IGNORE_HYPERLINK_SUFFIX = "ignore.hyperlink";

	/**
	 * Retrieves the hyperlink type for the element.
	 * <p>
	 * The actual hyperlink type is determined by {@link #getLinkType() getLinkType()}.
	 * This method can is used to determine whether the hyperlink type is one of the
	 * built-in types or a custom type. 
	 * When hyperlink is of custom type, {@link HyperlinkTypeEnum#CUSTOM CUSTOM} is returned.
	 * </p>
	 * @return one of the hyperlink type constants
	 * @see #getLinkType()
	 */
	public HyperlinkTypeEnum getHyperlinkType();

	/**
	 * Sets the link type as a built-in hyperlink type.
	 * 
	 * @param hyperlinkType the built-in hyperlink type
	 * @see #getLinkType()
	 */
	public void setHyperlinkType(HyperlinkTypeEnum hyperlinkType);
		
	/**
	 *
	 */
	public HyperlinkTargetEnum getHyperlinkTarget();
		
	/**
	 *
	 */
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget);
		
	/**
	 *
	 */
	public String getHyperlinkReference();
		
	/**
	 *
	 */
	public void setHyperlinkReference(String hyperlinkReference);

	/**
	 *
	 */
	public String getHyperlinkAnchor();
		
	/**
	 *
	 */
	public void setHyperlinkAnchor(String hyperlinkAnchor);

	/**
	 *
	 */
	public Integer getHyperlinkPage();
		
	/**
	 *
	 */
	public void setHyperlinkPage(Integer hyperlinkPage);

	
	/**
	 * Returns the hyperlink type.
	 * 
	 * @return the hyperlink type
	 */
	public String getLinkType();
	
	
	/**
	 * Sets the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @param type the hyperlink type
	 */
	public void setLinkType(String type);

	
	/**
	 * Returns the set of custom hyperlink parameters.
	 * 
	 * @return the set of custom hyperlink parameters
	 * @see #setHyperlinkParameters(JRPrintHyperlinkParameters)
	 * @see JRPrintHyperlinkParameter
	 */
	public JRPrintHyperlinkParameters getHyperlinkParameters();
	

	/**
	 * Sets the custom hyperlink parameters.
	 * <p>
	 * These parameters will be used to produce the actual hyperlink
	 * when the report is exported.
	 * </p>
	 * 
	 * @param parameters the set of custom hyperlink parameters
	 */
	public void setHyperlinkParameters(JRPrintHyperlinkParameters parameters);

	
	/**
	 * Returns the hyperlink tooltip.
	 * 
	 * @return the hyperlink tooltip
	 */
	public String getHyperlinkTooltip();
	
	
	/**
	 * Sets the tooltip to be used for the hyperlink.
	 * 
	 * @param tooltip the tooltip
	 */
	public void setHyperlinkTooltip(String tooltip);
	
	/**
	 * Returns the hyperlink target name.
	 * <p>
	 * The target name can be one of the built-in names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary name.
	 * </p>
	 * @return the hyperlink type
	 */
	public String getLinkTarget();


	/**
	 * Sets the hyperlink target name.
	 * <p>
	 * The target name can be one of the built-in names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary name.
	 * </p>
	 * @param linkTarget the hyperlink target name
	 */
	public void setLinkTarget(String linkTarget);
	
}
