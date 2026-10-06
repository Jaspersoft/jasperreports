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

/*
 * Contributors:
 * Ryan Johnson - delscovich@users.sourceforge.net 
 */
package net.sf.jasperreports.engine.design;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.base.JRBaseExpression;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;
import net.sf.jasperreports.engine.util.ExpressionParser;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignExpression extends JRBaseExpression implements JRChangeEventsSupport
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * Property that specifies whether a legacy parser method should be used to parse this expression. Default value is <code>false</code>.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_5_5_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_LEGACY_PARSER = 
			JRPropertiesUtil.PROPERTY_PREFIX + "legacy.expression.parser";
	
	protected static final boolean LEGACY_PARSER;
	static
	{
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance());
		LEGACY_PARSER = properties.getBooleanProperty(PROPERTY_LEGACY_PARSER, false);
	}
	
	public static final String PROPERTY_TEXT = "text";
	
	/**
	 *
	 */
	protected List<JRExpressionChunk> chunks = new ArrayList<>();


	/**
	 *
	 */
	public JRDesignExpression()
	{
		super();

		regenerateId();
	}


	/**
	 *
	 */
	public JRDesignExpression(String text)
	{
		this();

		setText(text);
	}


	/**
	 * FIXMENOW remove me?
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	public void setInterpreted()
	{
		this.id = ID_INTERPRETED;
	}

	/**
	 *
	 */
	public void setType(ExpressionTypeEnum type)
	{
		this.type = type; //FIXMENOW why not raise change event?
	}

	@Override
	public JRExpressionChunk[] getChunks()
	{
		JRExpressionChunk[] chunkArray = null;
		
		if (chunks != null && chunks.size() > 0)
		{
			chunkArray = new JRExpressionChunk[chunks.size()];
			chunks.toArray(chunkArray);
		}
		
		return chunkArray;
	}
		
	/**
	 * Clears the current list of chunks and adds the passed list of chunks.  The reference
	 * to the list passed is not kept.
	 */
	public void setChunks(List<JRExpressionChunk> chunks)
	{
		this.chunks.clear();
		this.chunks.addAll(chunks);
	}

	/**
	 *
	 */
	public void addChunk(JRDesignExpressionChunk chunk)
	{
		this.chunks.add(chunk);
	}
		
	protected void addChunk(byte type, String text)
	{
		JRDesignExpressionChunk chunk = new JRDesignExpressionChunk();
		chunk.setType(type);
		chunk.setText(text);
		
		this.chunks.add(chunk);
	}
		
	/**
	 *
	 */
	public void addTextChunk(String text)
	{
		JRDesignExpressionChunk chunk = new JRDesignExpressionChunk();
		chunk.setType(JRExpressionChunk.TYPE_TEXT);
		chunk.setText(text);
		
		this.chunks.add(chunk);
	}
		
	/**
	 *
	 */
	public void addParameterChunk(String text)
	{
		JRDesignExpressionChunk chunk = new JRDesignExpressionChunk();
		chunk.setType(JRExpressionChunk.TYPE_PARAMETER);
		chunk.setText(text);
		
		this.chunks.add(chunk);
	}
		
	/**
	 *
	 */
	public void addFieldChunk(String text)
	{
		JRDesignExpressionChunk chunk = new JRDesignExpressionChunk();
		chunk.setType(JRExpressionChunk.TYPE_FIELD);
		chunk.setText(text);
		
		this.chunks.add(chunk);
	}
		
	/**
	 *
	 */
	public void addVariableChunk(String text)
	{
		JRDesignExpressionChunk chunk = new JRDesignExpressionChunk();
		chunk.setType(JRExpressionChunk.TYPE_VARIABLE);
		chunk.setText(text);
		
		this.chunks.add(chunk);
	}

	/**
	 *
	 */
	public void addResourceChunk(String text)
	{
		JRDesignExpressionChunk chunk = new JRDesignExpressionChunk();
		chunk.setType(JRExpressionChunk.TYPE_RESOURCE);
		chunk.setText(text);
		
		this.chunks.add(chunk);
	}

	/**
	 *
	 */
	public void setText(String text)
	{
		Object old = getText();
		
		chunks.clear();
		
		if (text != null)
		{
			if (LEGACY_PARSER)
			{
				//FIXME remove this at some point
				legacyParseText(text);
			}
			else
			{
				parseText(text);
			}
		}
		
		getEventSupport().firePropertyChange(PROPERTY_TEXT, old, text);
	}

	protected void legacyParseText(String text)
	{
		StringBuilder textChunk = new StringBuilder();
		
		StringTokenizer tkzer = new StringTokenizer(text, "$", true);
		int behindDelims = 0;
		while (tkzer.hasMoreTokens())
		{
			String token = tkzer.nextToken();

			if (token.equals("$"))
			{
				if (behindDelims > 0)
				{
					textChunk.append("$");
				}

				++behindDelims;
			}
			else
			{
				byte chunkType = JRExpressionChunk.TYPE_TEXT;
				if (behindDelims > 0)
				{
					if (token.startsWith("P{"))
					{
						chunkType = JRExpressionChunk.TYPE_PARAMETER;
					}
					else if (token.startsWith("F{"))
					{
						chunkType = JRExpressionChunk.TYPE_FIELD;
					}
					else if (token.startsWith("V{"))
					{
						chunkType = JRExpressionChunk.TYPE_VARIABLE;
					}
					else if (token.startsWith("R{"))
					{
						chunkType = JRExpressionChunk.TYPE_RESOURCE;
					}
				}
				
				if (chunkType == JRExpressionChunk.TYPE_TEXT)
				{
					if (behindDelims > 0)
					{
						textChunk.append("$");
					}
					textChunk.append(token);
				}
				else
				{
					int end = token.indexOf('}');
					if (end > 0)
					{
						if (behindDelims > 1)
						{
							textChunk.append(token);
						}
						else
						{
							if (textChunk.length() > 0)
							{
								addTextChunk(textChunk.toString());					
							}
							
							addChunk(chunkType, token.substring(2, end));					
							textChunk = new StringBuilder(token.substring(end + 1));
						}
					}
					else
					{
						if (behindDelims > 0)
						{
							textChunk.append("$");
						}
						textChunk.append(token);
					}
				}

				behindDelims = 0;
			}
		}

		if (behindDelims > 0)
		{
			textChunk.append("$");
		}

		if (textChunk.length() > 0)
		{
			this.addTextChunk(textChunk.toString());					
		}
	}
	
	protected void parseText(String text)
	{
		ExpressionParser.instance().parseExpression(text, new ExpressionParser.ParseResult()
		{
			@Override
			public void addTextChunk(String text)
			{
				JRDesignExpression.this.addTextChunk(text);
			}
			
			@Override
			public void addChunk(byte chunkType, String chunkText)
			{
				JRDesignExpression.this.addChunk(chunkType, chunkText);
			}
		});
	}

	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}

	@Override
	public Object clone() 
	{
		JRDesignExpression clone = (JRDesignExpression)super.clone();
		clone.chunks = JRCloneUtils.cloneList(chunks);
		clone.eventSupport = null;
		return clone;
	}
	
}
