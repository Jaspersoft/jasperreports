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

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.query.JRClauseFunction;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRQueryChunk extends JRCloneable
{

	/**
	 * A property that specifies the list of token separators for
	 * {@link #TYPE_CLAUSE_TOKENS tokenized query clauses}.
	 * <p>
	 * The default separators are the comma (<code>','</code>), the semicolon (<code>';'</code>)
	 * and the vertical bar (<code>'|'</code>). 
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			defaultValue = ", ; |",
			scopes = {PropertyScope.GLOBAL},
			sinceVersion = PropertyConstants.VERSION_2_0_5
			)
	public static final String PROPERTY_CHUNK_TOKEN_SEPARATOR = 
		JRPropertiesUtil.PROPERTY_PREFIX + "query.chunk.token.separators";


	/**
	 *
	 */
	public static final byte TYPE_TEXT = 1;
	public static final byte TYPE_PARAMETER = 2;
	public static final byte TYPE_PARAMETER_CLAUSE = 3;
	
	/**
	 * A <code>$X{..}</code> query clause containing one or several tokens.
	 * <p>
	 * The clause will be processed by the query executer.
	 * The default implementation treats the first token as a function ID and delegates the processing
	 * to a {@link JRClauseFunction function} registered for the ID. 
	 * </p>
	 * <p>
	 * The clause text is tokenized in the following manner:
	 * <ul>
	 * 	<li>The first appearance of any separator (as specified by {@link #PROPERTY_CHUNK_TOKEN_SEPARATOR})
	 * if located in the clause text.</li>
	 * 	<li>This separator is then used to tokenize the entire text, including the remaining separators
	 * characters in tokens.</li>
	 * </ul>
	 * Note that this implies that the first token cannot contain any of the separator characters,
	 * only subsequent tokens are able to do so.
	 * </p>
	 * @see #PROPERTY_CHUNK_TOKEN_SEPARATOR
	 * @see #getTokens()
	 */
	public static final byte TYPE_CLAUSE_TOKENS = 4;


	/**
	 *
	 */
	public byte getType();
		
	/**
	 *
	 */
	public String getText();
		

	/**
	 * Returns the chunk tokens for {@link #TYPE_CLAUSE_TOKENS clause} chunks.
	 * 
	 * @return the chunk tokens
	 */
	public String[] getTokens();
		
	
	/**
	 * Returns the chunk tokens separator character for {@link #TYPE_CLAUSE_TOKENS clause} chunks.
	 * 
	 * @return the chunk tokens separator character
	 */
	public Character getTokenSeparator();
}
