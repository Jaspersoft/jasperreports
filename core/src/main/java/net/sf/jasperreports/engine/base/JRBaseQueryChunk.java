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
package net.sf.jasperreports.engine.base;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRQueryChunk;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRQueryParser;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseQueryChunk implements JRQueryChunk, Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	protected byte type = TYPE_TEXT; // primitive byte field would have zero as default value; need to set it to TYPE_TEXT explicitly
	protected String text;
	protected String[] tokens;
	protected Character tokenSeparator;


	/**
	 *
	 */
	protected JRBaseQueryChunk()
	{
	}
	
	
	/**
	 *
	 */
	protected JRBaseQueryChunk(JRQueryChunk queryChunk, JRBaseObjectFactory factory)
	{
		factory.put(queryChunk, this);
		
		type = queryChunk.getType();
		text = queryChunk.getText();
		tokenSeparator = queryChunk.getTokenSeparator();
		
		String[] chunkTokens = queryChunk.getTokens();
		if (chunkTokens == null)
		{
			tokens = null;
		}
		else
		{
			tokens = new String[chunkTokens.length];
			System.arraycopy(chunkTokens, 0, tokens, 0, chunkTokens.length);
		}
	}
		

	@Override
	public byte getType()
	{
		return this.type;
	}
		
	@Override
	public String getText()
	{
		if (type == TYPE_CLAUSE_TOKENS)
		{
			return JRQueryParser.instance().asClauseText(getTokens(), getTokenSeparator());
		}
		
		return this.text;
	}


	@Override
	public String[] getTokens()
	{
		return tokens;
	}
		

	@Override
	public Character getTokenSeparator()
	{
		return tokenSeparator;
	}
		

	@Override
	public Object clone() 
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
	}


}
