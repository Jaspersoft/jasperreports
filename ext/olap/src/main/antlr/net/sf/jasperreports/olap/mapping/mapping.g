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


header
{
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
package net.sf.jasperreports.olap.mapping;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.olap.mapping.*;
}

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
class MappingParser extends Parser;
options
{
    k=2;
}
{
	private MappingMetadata mappingMeta;
	
	public void setMappingMetadata(MappingMetadata mappingMeta)
	{
		this.mappingMeta = mappingMeta;
	}
	
	private String getMondrName (String text)
	{
		return text.substring(1, text.length() - 1).trim();
	}
	
	private static final Pattern IDX_PATTERN = Pattern.compile("#(\\d+)");
	private static final int IDX_GROUP = 1;
	
	private int getDimensionIndex(Axis axis, String dimensionName)
	{
		int idx;
		Matcher matcher = IDX_PATTERN.matcher(dimensionName);
		if (matcher.matches())
		{
			String idxStr = matcher.group(IDX_GROUP);
			idx = Integer.parseInt(idxStr);
		}
		else
		{
			idx = mappingMeta.getDimensionIndex(axis, dimensionName);
		}
		return idx;
	}
	
	private int getLevelDepth(TuplePosition pos, String levelName)
	{
		int depth;
		Matcher matcher = IDX_PATTERN.matcher(levelName);
		if (matcher.matches())
		{
			String depthStr = matcher.group(IDX_GROUP);
			depth = Integer.parseInt(depthStr);
		}
		else
		{
			depth = mappingMeta.getLevelDepth(pos, levelName);
		}
		return depth;
	}
}

mapping returns [Mapping mapping = null]
	:
		(mapping=memberMapping | mapping=dataMapping) EOF
	;

memberMapping returns [MemberMapping mapping = null]
{Member member; MemberProperty prop = null;}
	:
		member=member (prop=property)?
		{mapping = new MemberMapping(member, prop);}
	;

member returns [Member member = null]
{Axis axis; TuplePosition pos; MemberDepth depth = null;}
	:
		axis=axis pos=tuplePosition [axis] (depth=memberDepth [axis, pos])?
		{member = new Member(pos, depth);}
	;

memberLevel returns [Member member = null]
{Axis axis; TuplePosition pos; MemberDepth depth;}
	:
		axis=axis pos=tuplePosition [axis] depth=memberDepth [axis, pos]
		{member = new Member(pos, depth);}
	;

axis returns [Axis axis = null]
{int idx;}
	:
		(( "Axis" LPAREN idx=idx RPAREN ) |
		( idx=axisName ))
		{axis = new Axis(idx);}
	;

axisName returns [int idx = -1]
	: ("Columns" {idx = 0;} | 
		"Rows" {idx = 1;} | 
		"Pages" {idx = 2;} | 
		"Chapters" {idx = 3;} | 
		"Sections"{idx = 4;} )
	;

tuplePosition [Axis axis] returns [TuplePosition pos = null]
{int idx;}
	:
		{String dimensionName;} dimensionName=mondrName {idx = getDimensionIndex(axis, dimensionName);} 
		{pos = new TuplePosition(axis, idx);}
	;

memberDepth [Axis axis, TuplePosition pos] returns [MemberDepth memberDepth = null]
	:
		{String levelName;} levelName=mondrName {memberDepth = new MemberDepth(getLevelDepth(pos, levelName));}
	;

property returns [MemberProperty prop = null]
{String name;}
	:
		LPAREN name=name RPAREN
		{prop = new MemberProperty(name);}
	;

dataMapping returns [DataMapping mapping = null]
{boolean formatted = false; List filter = null; List positions = null;}
	:
		(("Data" | "FormattedData" {formatted = true;})
		(filter=memberFilter)?
		(positions=axisPositions)?)
		{mapping = new DataMapping(formatted, filter, positions);}
	;

memberFilter returns [List filter = new ArrayList()]
{Member member;}
	:
		LPAREN
		member=memberLevel {filter.add(member);}
		(COMMA member=memberLevel {filter.add(member);} )*
		RPAREN
	;

axisPositions returns [List positions = new ArrayList()]
{AxisPosition pos; int axis = 0;}
	:
		LPAREN
		pos=axisPosition [axis] {++axis; positions.add(pos);}
		(COMMA pos=axisPosition [axis] {++axis; positions.add(pos);} )*
		RPAREN
	;

axisPosition [int axis] returns [AxisPosition pos = null]
{int idx;}
	:
		(QMARK {idx = AxisPosition.POSITION_UNSPECIFIED;} | 
			idx=idx |
			{Tuple tuple;} tuple=tuple {idx = mappingMeta.getTuplePosition(axis, tuple);}
		)
		{pos = new AxisPosition(idx);}
	;

tuple returns [Tuple tuple = null]
{TupleMember member;}
	:
		(LPAREN {tuple = new Tuple();}
			member=tupleMember {tuple.addMember(member);}
			(COMMA member=tupleMember {tuple.addMember(member);} )* RPAREN) |
		member=tupleMember {tuple = new Tuple(member);}
	;

tupleMember returns [TupleMember tuple = new TupleMember()]
{String name;}
	:
		name=mondrName {tuple.addName(name);}
		( POINT name=mondrName {tuple.addName(name);} )*
	;

idx returns [int i = -1]
	:
		idx:INT {i = Integer.parseInt(idx.getText());}
	;

mondrName returns [String name = null]
	:
		n:MONDRNAME {name = getMondrName(n.getText());}
	;

name returns [String name = null]
	:
		n:NAME {name = n.getText();}
	;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
class MappingLexer extends Lexer;
options
{
    k=2; // needed for newline junk
    charVocabulary='\u0000'..'\uFFFE'; // allow unicode
    caseSensitive=false;
}

POINT		: '.';
LPAREN		: '(' ;
RPAREN		: ')' ;
PLUS		: '+' ;
MINUS		: '-' ;
STAR		: '*' ;
QMARK		: '?' ;
COMMA		: ',' ;
INT			: (DIGIT)+ ;
MONDRNAME	: '[' (~(']'))+ ']';
NAME		: LETTER (LETTER | DIGIT | ' ')* ;
WS			: ( ' ' | '\r' '\n' | '\n' | '\t' )
				{$setType(Token.SKIP);}
				;

protected
DIGIT 		: '0'..'9' ;
protected
LETTER 		: 'a'..'z' | '\u0080'..'\ufffe';
