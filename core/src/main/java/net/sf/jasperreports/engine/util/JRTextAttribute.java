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
package net.sf.jasperreports.engine.util;

import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRTextAttribute extends AttributedCharacterIterator.Attribute
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private static final Map<String,JRTextAttribute> instanceMap = new HashMap<>(4);

	/**
	 *
	 */
	public static final JRTextAttribute PDF_FONT_NAME = new JRTextAttribute("PDF_FONT_NAME");
	public static final JRTextAttribute PDF_ENCODING = new JRTextAttribute("PDF_ENCODING");
	public static final JRTextAttribute IS_PDF_EMBEDDED = new JRTextAttribute("IS_PDF_EMBEDDED");
	public static final JRTextAttribute FONT_SIZE = new JRTextAttribute("FONT_SIZE");
	
	/**
	 *
	 */
	public static final JRTextAttribute REFERENCE = new JRTextAttribute("REFERENCE");
	public static final JRTextAttribute NOTE = new JRTextAttribute("NOTE");
	
	/**
	 *
	 */
	public static final JRTextAttribute HYPERLINK = new JRTextAttribute("HYPERLINK");
	
	/**
	 *
	 */
	public static final JRTextAttribute HTML_LIST = new JRTextAttribute("HTML_LIST");
	public static final JRTextAttribute HTML_LIST_ITEM = new JRTextAttribute("HTML_LIST_ITEM");

	/**
	 * Used in report search to mark the matching terms
	 */
	public static final JRTextAttribute SEARCH_HIGHLIGHT = new JRTextAttribute("SEARCH_HIGHLIGHT");

	/**
	 * Used in report search to mark the whitespace between the matching terms
	 */
	public static final JRTextAttribute SEARCH_HIGHLIGHT_WHITESPACE = new JRTextAttribute("SEARCH_HIGHLIGHT_WHITESPACE");

	public static final JRTextAttribute FONT_INFO = new JRTextAttribute("FONT_INFO");
	
	
	public static final Set<JRTextAttribute> HTML_LIST_ATTRIBUTES = new HashSet<>();
	static
	{
		HTML_LIST_ATTRIBUTES.add(JRTextAttribute.HTML_LIST);
		HTML_LIST_ATTRIBUTES.add(JRTextAttribute.HTML_LIST_ITEM);
	}

	/**
	 *
	 */
	private JRTextAttribute(String name)
	{
		super(name);
		
		if (this.getClass() == JRTextAttribute.class)
		{
			instanceMap.put(name, this);
		}
	}

	/**
	 * Resolves instances being deserialized to the predefined constants.
	*/
	@Override
	protected Object readResolve() throws InvalidObjectException 
	{
		if (this.getClass() != JRTextAttribute.class)
		{
			throw new InvalidObjectException("Subclass didn't correctly implement readResolve");
		}
		
		JRTextAttribute instance = instanceMap.get(getName());
		if (instance != null)
		{
			return instance;
		}

		throw new InvalidObjectException("Unknown attribute name");
	}
	
}
