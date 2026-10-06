// $ANTLR 2.7.5 (20050128): "mapping.g" -> "MappingParser.java"$

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class MappingParser extends antlr.LLkParser implements MappingParserTokenTypes
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

protected MappingParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public MappingParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected MappingParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public MappingParser(TokenStream lexer) {
  this(lexer,2);
}

public MappingParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final Mapping  mapping() throws RecognitionException, TokenStreamException {
		Mapping mapping = null;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_Axis:
			case LITERAL_Columns:
			case LITERAL_Rows:
			case LITERAL_Pages:
			case LITERAL_Chapters:
			case LITERAL_Sections:
			{
				mapping=memberMapping();
				break;
			}
			case LITERAL_Data:
			case LITERAL_FormattedData:
			{
				mapping=dataMapping();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return mapping;
	}
	
	public final MemberMapping  memberMapping() throws RecognitionException, TokenStreamException {
		MemberMapping mapping = null;
		
		Member member; MemberProperty prop = null;
		
		try {      // for error handling
			member=member();
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				prop=property();
				break;
			}
			case EOF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			mapping = new MemberMapping(member, prop);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return mapping;
	}
	
	public final DataMapping  dataMapping() throws RecognitionException, TokenStreamException {
		DataMapping mapping = null;
		
		boolean formatted = false; 
		List<Member> filter = null; 
		List<AxisPosition> positions = null;
		
		try {      // for error handling
			{
			{
			switch ( LA(1)) {
			case LITERAL_Data:
			{
				match(LITERAL_Data);
				break;
			}
			case LITERAL_FormattedData:
			{
				match(LITERAL_FormattedData);
				formatted = true;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			if ((LA(1)==LPAREN) && (_tokenSet_1.member(LA(2)))) {
				filter=memberFilter();
			}
			else if ((LA(1)==EOF||LA(1)==LPAREN) && (_tokenSet_2.member(LA(2)))) { //NOPMD
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				positions=axisPositions();
				break;
			}
			case EOF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			}
			mapping = new DataMapping(formatted, filter, positions);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return mapping;
	}
	
	public final Member  member() throws RecognitionException, TokenStreamException {
		Member member = null;
		
		Axis axis; TuplePosition pos; MemberDepth depth = null;
		
		try {      // for error handling
			axis=axis();
			pos=tuplePosition(axis);
			{
			switch ( LA(1)) {
			case MONDRNAME:
			{
				depth=memberDepth(axis, pos);
				break;
			}
			case EOF:
			case LPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			member = new Member(pos, depth);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return member;
	}
	
	public final MemberProperty  property() throws RecognitionException, TokenStreamException {
		MemberProperty prop = null;
		
		String name;
		
		try {      // for error handling
			match(LPAREN);
			name=name();
			match(RPAREN);
			prop = new MemberProperty(name);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return prop;
	}
	
	public final Axis  axis() throws RecognitionException, TokenStreamException {
		Axis axis = null;
		
		int idx;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_Axis:
			{
				{
				match(LITERAL_Axis);
				match(LPAREN);
				idx=idx();
				match(RPAREN);
				}
				break;
			}
			case LITERAL_Columns:
			case LITERAL_Rows:
			case LITERAL_Pages:
			case LITERAL_Chapters:
			case LITERAL_Sections:
			{
				{
				idx=axisName();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			axis = new Axis(idx);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		return axis;
	}
	
	public final TuplePosition  tuplePosition(
		Axis axis
	) throws RecognitionException, TokenStreamException {
		TuplePosition pos = null;
		
		int idx;
		
		try {      // for error handling
			String dimensionName;
			dimensionName=mondrName();
			idx = getDimensionIndex(axis, dimensionName);
			pos = new TuplePosition(axis, idx);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		return pos;
	}
	
	public final MemberDepth  memberDepth(
		Axis axis, TuplePosition pos
	) throws RecognitionException, TokenStreamException {
		MemberDepth memberDepth = null;
		
		
		try {      // for error handling
			String levelName;
			levelName=mondrName();
			memberDepth = new MemberDepth(getLevelDepth(pos, levelName));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		return memberDepth;
	}
	
	public final Member  memberLevel() throws RecognitionException, TokenStreamException {
		Member member = null;
		
		Axis axis; TuplePosition pos; MemberDepth depth;
		
		try {      // for error handling
			axis=axis();
			pos=tuplePosition(axis);
			depth=memberDepth(axis, pos);
			member = new Member(pos, depth);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return member;
	}
	
	public final int  idx() throws RecognitionException, TokenStreamException {
		int i = -1;
		
		Token  idx = null;
		
		try {      // for error handling
			idx = LT(1);
			match(INT);
			i = Integer.parseInt(idx.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return i;
	}
	
	public final int  axisName() throws RecognitionException, TokenStreamException {
		int idx = -1;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_Columns:
			{
				match(LITERAL_Columns);
				idx = 0;
				break;
			}
			case LITERAL_Rows:
			{
				match(LITERAL_Rows);
				idx = 1;
				break;
			}
			case LITERAL_Pages:
			{
				match(LITERAL_Pages);
				idx = 2;
				break;
			}
			case LITERAL_Chapters:
			{
				match(LITERAL_Chapters);
				idx = 3;
				break;
			}
			case LITERAL_Sections:
			{
				match(LITERAL_Sections);
				idx = 4;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		return idx;
	}
	
	public final String  mondrName() throws RecognitionException, TokenStreamException {
		String name = null;
		
		Token  n = null;
		
		try {      // for error handling
			n = LT(1);
			match(MONDRNAME);
			name = getMondrName(n.getText());
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return name;
	}
	
	public final String  name() throws RecognitionException, TokenStreamException {
		String name = null;
		
		Token  n = null;
		
		try {      // for error handling
			n = LT(1);
			match(NAME);
			name = n.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		return name;
	}
	
	public final List<Member>  memberFilter() throws RecognitionException, TokenStreamException {
		List<Member> filter = new ArrayList<>();
		
		Member member;
		
		try {      // for error handling
			match(LPAREN);
			member=memberLevel();
			filter.add(member);
			{
			_loop24:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					member=memberLevel();
					filter.add(member);
				}
				else {
					break _loop24;
				}
				
			} while (true);
			}
			match(RPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return filter;
	}
	
	public final List<AxisPosition> axisPositions() throws RecognitionException, TokenStreamException {
		List<AxisPosition> positions = new ArrayList<>();
		
		AxisPosition pos; int axis = 0;
		
		try {      // for error handling
			match(LPAREN);
			pos=axisPosition(axis);
			++axis; positions.add(pos);
			{
			_loop27:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					pos=axisPosition(axis);
					++axis; positions.add(pos);
				}
				else {
					break _loop27;
				}
				
			} while (true);
			}
			match(RPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return positions;
	}
	
	public final AxisPosition  axisPosition(
		int axis
	) throws RecognitionException, TokenStreamException {
		AxisPosition pos = null;
		
		int idx;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case QMARK:
			{
				match(QMARK);
				idx = AxisPosition.POSITION_UNSPECIFIED;
				break;
			}
			case INT:
			{
				idx=idx();
				break;
			}
			case LPAREN:
			case MONDRNAME:
			{
				Tuple tuple;
				tuple=tuple();
				idx = mappingMeta.getTuplePosition(axis, tuple);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			pos = new AxisPosition(idx);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return pos;
	}
	
	public final Tuple  tuple() throws RecognitionException, TokenStreamException {
		Tuple tuple = null;
		
		TupleMember member;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LPAREN:
			{
				{
				match(LPAREN);
				tuple = new Tuple();
				member=tupleMember();
				tuple.addMember(member);
				{
				_loop33:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						member=tupleMember();
						tuple.addMember(member);
					}
					else {
						break _loop33;
					}
					
				} while (true);
				}
				match(RPAREN);
				}
				break;
			}
			case MONDRNAME:
			{
				member=tupleMember();
				tuple = new Tuple(member);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return tuple;
	}
	
	public final TupleMember  tupleMember() throws RecognitionException, TokenStreamException {
		TupleMember tuple = new TupleMember();
		
		String name;
		
		try {      // for error handling
			name=mondrName();
			tuple.addName(name);
			{
			_loop36:
			do {
				if ((LA(1)==POINT)) {
					match(POINT);
					name=mondrName();
					tuple.addName(name);
				}
				else {
					break _loop36;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return tuple;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"Axis\"",
		"LPAREN",
		"RPAREN",
		"\"Columns\"",
		"\"Rows\"",
		"\"Pages\"",
		"\"Chapters\"",
		"\"Sections\"",
		"\"Data\"",
		"\"FormattedData\"",
		"COMMA",
		"QMARK",
		"POINT",
		"INT",
		"MONDRNAME",
		"NAME",
		"PLUS",
		"MINUS",
		"STAR",
		"WS",
		"DIGIT",
		"LETTER"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 3984L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 426018L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 34L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 262144L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 262178L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 16482L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 16448L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 344162L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 64L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	
	}
