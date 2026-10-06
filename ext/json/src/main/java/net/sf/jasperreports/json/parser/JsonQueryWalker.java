// $ANTLR 2.7.5 (20050128): "json_grammar.g" -> "JsonQueryWalker.java"$

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
package net.sf.jasperreports.json.parser;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;
import net.sf.jasperreports.engine.type.JsonOperatorEnum;
import net.sf.jasperreports.json.expression.JsonQLExpression;
import net.sf.jasperreports.json.expression.filter.*;
import net.sf.jasperreports.json.expression.filter.FilterExpression.LOGICAL_OPERATOR;
import net.sf.jasperreports.json.expression.filter.FilterExpression.VALUE_TYPE;
import net.sf.jasperreports.json.expression.member.*;
import net.sf.jasperreports.json.expression.member.MemberExpression.DIRECTION;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQueryWalker extends antlr.TreeParser       implements JsonQueryParserTokenTypes
 {
public JsonQueryWalker() {
	tokenNames = _tokenNames;
}

	public final JsonQLExpression  jsonQLExpression(AST _t) throws RecognitionException {
		JsonQLExpression jsonQLExpression = new JsonQLExpression();
		
		AST jsonQLExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST abs = null;
		
		try {      // for error handling
			AST __t76 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,PATH);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ABSOLUTE:
			{
				abs = (AST)_t;
				match(_t,ABSOLUTE);
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			case MEMBER:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			_loop79:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==MEMBER)) {
					memberExpr(_t,jsonQLExpression);
					_t = _retTree;
				}
				else {
					break _loop79;
				}
				
			} while (true);
			}
			_t = __t76;
			_t = _t.getNextSibling();
			
			if (abs != null) {
			jsonQLExpression.setIsAbsolute(true);
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return jsonQLExpression;
	}
	
	public final void memberExpr(AST _t,
		JsonQLExpression jsonQLExpression
	) throws RecognitionException {
		
		AST memberExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		MemberExpression memberExpr = null;
		FilterExpression filterExpression = null;
		
		
		try {      // for error handling
			AST __t81 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,MEMBER);
			_t = _t.getFirstChild();
			memberExpr=pathNaviExpr(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FILTER:
			{
				filterExpression=filterExprMain(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t81;
			_t = _t.getNextSibling();
			
			memberExpr.setFilterExpression(filterExpression);
			jsonQLExpression.addMemberExpression(memberExpr);
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final MemberExpression  pathNaviExpr(AST _t) throws RecognitionException {
		MemberExpression memberExpr = null;
		
		AST pathNaviExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST id = null;
		AST s = null;
		AST n = null;
		AST sliceStart = null;
		AST sliceEnd = null;
		AST levelUp = null;
		
		DIRECTION dir = DIRECTION.DOWN;
		
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case SIMPLE_KEY:
			{
				AST __t84 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,SIMPLE_KEY);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case DOT:
				case DOTDOT:
				{
					dir=direction(_t);
					_t = _retTree;
					break;
				}
				case ID:
				case WILDCARD:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ID:
				{
					id = (AST)_t;
					match(_t,ID);
					_t = _t.getNextSibling();
					break;
				}
				case WILDCARD:
				{
					AST tmp4_AST_in = (AST)_t;
					match(_t,WILDCARD);
					_t = _t.getNextSibling();
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t84;
				_t = _t.getNextSibling();
				
				if (id != null) {
				// object key
				memberExpr = new ObjectKeyExpression(dir, id.getText());
				} else {
				// wildcard
				memberExpr = new ObjectKeyExpression(dir);
				}
				
				break;
			}
			case COMPLEX_KEY:
			{
				AST __t87 = _t;
				AST tmp5_AST_in = (AST)_t;
				match(_t,COMPLEX_KEY);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case DOT:
				case DOTDOT:
				{
					dir=direction(_t);
					_t = _retTree;
					break;
				}
				case STRING:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				s = (AST)_t;
				match(_t,STRING);
				_t = _t.getNextSibling();
				_t = __t87;
				_t = _t.getNextSibling();
				
				memberExpr = new ObjectKeyExpression(dir, s.getText(), true);
				
				break;
			}
			case OBJECT_CONSTRUCTION:
			{
				
				memberExpr = new ObjectConstructionExpression();
				
				AST __t89 = _t;
				AST tmp6_AST_in = (AST)_t;
				match(_t,OBJECT_CONSTRUCTION);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case DOT:
				case DOTDOT:
				{
					dir=direction(_t);
					_t = _retTree;
					break;
				}
				case ID:
				case STRING:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				{
				int _cnt92=0;
				_loop92:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==ID||_t.getType()==STRING)) {
						addObjectKey(_t,(ObjectConstructionExpression)memberExpr);
						_t = _retTree;
					}
					else {
						if ( _cnt92>=1 ) { break _loop92; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt92++;
				} while (true);
				}
				_t = __t89;
				_t = _t.getNextSibling();
				
				((ObjectConstructionExpression)memberExpr).setDirection(dir);
				
				break;
			}
			case ARRAY_INDEX:
			{
				AST __t93 = _t;
				AST tmp7_AST_in = (AST)_t;
				match(_t,ARRAY_INDEX);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case DOT:
				case DOTDOT:
				{
					dir=direction(_t);
					_t = _retTree;
					break;
				}
				case INT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				n = (AST)_t;
				match(_t,INT);
				_t = _t.getNextSibling();
				_t = __t93;
				_t = _t.getNextSibling();
				
				memberExpr = new ArrayIndexExpression(dir, Integer.parseInt(n.getText()));
				
				break;
			}
			case ARRAY_CONSTRUCTION:
			{
				
				memberExpr = new ArrayConstructionExpression();
				
				AST __t95 = _t;
				AST tmp8_AST_in = (AST)_t;
				match(_t,ARRAY_CONSTRUCTION);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case DOT:
				case DOTDOT:
				{
					dir=direction(_t);
					_t = _retTree;
					break;
				}
				case INT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				{
				int _cnt98=0;
				_loop98:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==INT)) {
						addArrayIndex(_t,(ArrayConstructionExpression)memberExpr);
						_t = _retTree;
					}
					else {
						if ( _cnt98>=1 ) { break _loop98; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt98++;
				} while (true);
				}
				_t = __t95;
				_t = _t.getNextSibling();
				
				((ArrayConstructionExpression)memberExpr).setDirection(dir);
				
				break;
			}
			case ARRAY_SLICE:
			{
				AST __t99 = _t;
				AST tmp9_AST_in = (AST)_t;
				match(_t,ARRAY_SLICE);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case DOT:
				case DOTDOT:
				{
					dir=direction(_t);
					_t = _retTree;
					break;
				}
				case INT:
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case INT:
				{
					sliceStart = (AST)_t;
					match(_t,INT);
					_t = _t.getNextSibling();
					break;
				}
				case SEMI:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				AST tmp10_AST_in = (AST)_t;
				match(_t,SEMI);
				_t = _t.getNextSibling();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case INT:
				{
					sliceEnd = (AST)_t;
					match(_t,INT);
					_t = _t.getNextSibling();
					break;
				}
				case 3:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t99;
				_t = _t.getNextSibling();
				
				Integer start = null, end = null;
				
				if (sliceStart != null) {
				start = Integer.valueOf(sliceStart.getText());
				}
				if (sliceEnd != null) {
				end = Integer.valueOf(sliceEnd.getText());
				}
				
				memberExpr = new ArraySliceExpression(dir, start, end);
				
				break;
			}
			case MULTI_LEVEL_UP:
			{
				AST __t103 = _t;
				AST tmp11_AST_in = (AST)_t;
				match(_t,MULTI_LEVEL_UP);
				_t = _t.getFirstChild();
				AST tmp12_AST_in = (AST)_t;
				match(_t,CARET);
				_t = _t.getNextSibling();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case INT:
				{
					levelUp = (AST)_t;
					match(_t,INT);
					_t = _t.getNextSibling();
					break;
				}
				case 3:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t103;
				_t = _t.getNextSibling();
				
				int level = 1;
				if (levelUp != null) {
				level = Integer.parseInt(levelUp.getText());
				}
				memberExpr = new MultiLevelUpExpression(level);
				
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return memberExpr;
	}
	
	public final FilterExpression  filterExprMain(AST _t) throws RecognitionException {
		FilterExpression filterExpression = null;
		
		AST filterExprMain_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t109 = _t;
			AST tmp13_AST_in = (AST)_t;
			match(_t,FILTER);
			_t = _t.getFirstChild();
			filterExpression=filterExpr(_t);
			_t = _retTree;
			_t = __t109;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return filterExpression;
	}
	
	public final DIRECTION  direction(AST _t) throws RecognitionException {
		DIRECTION dir = DIRECTION.DOWN;
		
		AST direction_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DOT:
			{
				AST tmp14_AST_in = (AST)_t;
				match(_t,DOT);
				_t = _t.getNextSibling();
				break;
			}
			case DOTDOT:
			{
				AST tmp15_AST_in = (AST)_t;
				match(_t,DOTDOT);
				_t = _t.getNextSibling();
				dir = DIRECTION.ANYWHERE_DOWN;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return dir;
	}
	
	public final void addObjectKey(AST _t,
		ObjectConstructionExpression objConstrExpr
	) throws RecognitionException {
		
		AST addObjectKey_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST str = null;
		AST id = null;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case STRING:
			{
				str = (AST)_t;
				match(_t,STRING);
				_t = _t.getNextSibling();
				break;
			}
			case ID:
			{
				id = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			
			String key = null;
			if (str != null) {
			key = str.getText();
			} else {
			key = id.getText();
			}
			objConstrExpr.addKey(key);
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void addArrayIndex(AST _t,
		ArrayConstructionExpression arrayConstrExpr
	) throws RecognitionException {
		
		AST addArrayIndex_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST idx = null;
		
		try {      // for error handling
			idx = (AST)_t;
			match(_t,INT);
			_t = _t.getNextSibling();
			
			arrayConstrExpr.addIndex(Integer.parseInt(idx.getText()));
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final FilterExpression  filterExpr(AST _t) throws RecognitionException {
		FilterExpression result = null;
		
		AST filterExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		FilterExpression fe1, fe2;
		
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case AND:
			{
				AST __t111 = _t;
				AST tmp16_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				fe1=filterExpr(_t);
				_t = _retTree;
				fe2=filterExpr(_t);
				_t = _retTree;
				_t = __t111;
				_t = _t.getNextSibling();
				
				result = new CompoundFilterExpression(fe1, fe2, LOGICAL_OPERATOR.AND);
				
				break;
			}
			case OR:
			{
				AST __t112 = _t;
				AST tmp17_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				fe1=filterExpr(_t);
				_t = _retTree;
				fe2=filterExpr(_t);
				_t = _retTree;
				_t = __t112;
				_t = _t.getNextSibling();
				
				result = new CompoundFilterExpression(fe1, fe2, LOGICAL_OPERATOR.OR);
				
				break;
			}
			case NOT:
			{
				AST __t113 = _t;
				AST tmp18_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getFirstChild();
				fe1=filterExpr(_t);
				_t = _retTree;
				_t = __t113;
				_t = _t.getNextSibling();
				
				result = new NotFilterExpression(fe1);
				
				break;
			}
			case SIMPLE_KEY:
			case COMPLEX_KEY:
			case OBJECT_CONSTRUCTION:
			case ARRAY_INDEX:
			case ARRAY_CONSTRUCTION:
			case ARRAY_SLICE:
			case MULTI_LEVEL_UP:
			case AT_IS_NULL:
			case AT_IS_NOT_NULL:
			case AT_IS_ARRAY:
			case AT_IS_OBJECT:
			case AT_IS_VALUE:
			case AT_SIZE:
			case AT_VALUE:
			{
				result=filterExprMinimal(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return result;
	}
	
	public final BasicFilterExpression  filterExprMinimal(AST _t) throws RecognitionException {
		BasicFilterExpression filterExpression = new BasicFilterExpression();
		
		AST filterExprMinimal_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST sizeFn = null;
		AST szFn = null;
		
		ValueDescriptor val = null;
		JsonOperatorEnum op = null;
		
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case AT_SIZE:
			case AT_VALUE:
			{
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case AT_SIZE:
				{
					sizeFn = (AST)_t;
					match(_t,AT_SIZE);
					_t = _t.getNextSibling();
					break;
				}
				case AT_VALUE:
				{
					AST tmp19_AST_in = (AST)_t;
					match(_t,AT_VALUE);
					_t = _t.getNextSibling();
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				op=operator(_t);
				_t = _retTree;
				val=value(_t);
				_t = _retTree;
				
				if (sizeFn != null) {
				filterExpression.setIsSizeFunction(true);
				}
				filterExpression.setOperator(op);
				filterExpression.setValueDescriptor(val);
				
				break;
			}
			case AT_IS_NULL:
			case AT_IS_NOT_NULL:
			case AT_IS_ARRAY:
			case AT_IS_OBJECT:
			case AT_IS_VALUE:
			{
				pathTypeCheckExpr(_t,filterExpression);
				_t = _retTree;
				break;
			}
			case SIMPLE_KEY:
			case COMPLEX_KEY:
			case OBJECT_CONSTRUCTION:
			case ARRAY_INDEX:
			case ARRAY_CONSTRUCTION:
			case ARRAY_SLICE:
			case MULTI_LEVEL_UP:
			{
				{
				int _cnt117=0;
				_loop117:
				do {
					if (_t==null) _t=ASTNULL;
					if (((_t.getType() >= SIMPLE_KEY && _t.getType() <= MULTI_LEVEL_UP))) {
						filterMemberExpr(_t,filterExpression);
						_t = _retTree;
					}
					else {
						if ( _cnt117>=1 ) { break _loop117; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt117++;
				} while (true);
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case AT_IS_NULL:
				case AT_IS_NOT_NULL:
				case AT_IS_ARRAY:
				case AT_IS_OBJECT:
				case AT_IS_VALUE:
				{
					pathTypeCheckExpr(_t,filterExpression);
					_t = _retTree;
					break;
				}
				case AT_SIZE:
				case EQ:
				case NE:
				case LT:
				case LE:
				case GT:
				case GE:
				case CONTAINS:
				{
					{
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case AT_SIZE:
					{
						szFn = (AST)_t;
						match(_t,AT_SIZE);
						_t = _t.getNextSibling();
						break;
					}
					case EQ:
					case NE:
					case LT:
					case LE:
					case GT:
					case GE:
					case CONTAINS:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					op=operator(_t);
					_t = _retTree;
					val=value(_t);
					_t = _retTree;
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				
				if (szFn != null) {
				filterExpression.setIsSizeFunction(true);
				}
				filterExpression.setOperator(op);
				filterExpression.setValueDescriptor(val);
				
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return filterExpression;
	}
	
	public final JsonOperatorEnum  operator(AST _t) throws RecognitionException {
		JsonOperatorEnum operator = null;
		
		AST operator_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EQ:
			{
				AST tmp20_AST_in = (AST)_t;
				match(_t,EQ);
				_t = _t.getNextSibling();
				operator = JsonOperatorEnum.EQ;
				break;
			}
			case NE:
			{
				AST tmp21_AST_in = (AST)_t;
				match(_t,NE);
				_t = _t.getNextSibling();
				operator = JsonOperatorEnum.NE;
				break;
			}
			case LT:
			{
				AST tmp22_AST_in = (AST)_t;
				match(_t,LT);
				_t = _t.getNextSibling();
				operator = JsonOperatorEnum.LT;
				break;
			}
			case LE:
			{
				AST tmp23_AST_in = (AST)_t;
				match(_t,LE);
				_t = _t.getNextSibling();
				operator = JsonOperatorEnum.LE;
				break;
			}
			case GT:
			{
				AST tmp24_AST_in = (AST)_t;
				match(_t,GT);
				_t = _t.getNextSibling();
				operator = JsonOperatorEnum.GT;
				break;
			}
			case GE:
			{
				AST tmp25_AST_in = (AST)_t;
				match(_t,GE);
				_t = _t.getNextSibling();
				operator = JsonOperatorEnum.GE;
				break;
			}
			case CONTAINS:
			{
				AST tmp26_AST_in = (AST)_t;
				match(_t,CONTAINS);
				_t = _t.getNextSibling();
				operator = JsonOperatorEnum.CONTAINS;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return operator;
	}
	
	public final ValueDescriptor  value(AST _t) throws RecognitionException {
		ValueDescriptor valueDescriptor = null;
		
		AST value_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST integer = null;
		AST real = null;
		AST string = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_null:
			{
				AST tmp27_AST_in = (AST)_t;
				match(_t,LITERAL_null);
				_t = _t.getNextSibling();
				valueDescriptor = new ValueDescriptor(VALUE_TYPE.NULL, "null");
				break;
			}
			case LITERAL_true:
			{
				AST tmp28_AST_in = (AST)_t;
				match(_t,LITERAL_true);
				_t = _t.getNextSibling();
				valueDescriptor = new ValueDescriptor(VALUE_TYPE.BOOLEAN, "true");
				break;
			}
			case LITERAL_false:
			{
				AST tmp29_AST_in = (AST)_t;
				match(_t,LITERAL_false);
				_t = _t.getNextSibling();
				valueDescriptor = new ValueDescriptor(VALUE_TYPE.BOOLEAN, "false");
				break;
			}
			case INT:
			{
				integer = (AST)_t;
				match(_t,INT);
				_t = _t.getNextSibling();
				valueDescriptor = new ValueDescriptor(VALUE_TYPE.INTEGER, integer.getText());
				break;
			}
			case REAL:
			{
				real = (AST)_t;
				match(_t,REAL);
				_t = _t.getNextSibling();
				valueDescriptor = new ValueDescriptor(VALUE_TYPE.DOUBLE, real.getText());
				break;
			}
			case STRING:
			{
				string = (AST)_t;
				match(_t,STRING);
				_t = _t.getNextSibling();
				valueDescriptor = new ValueDescriptor(VALUE_TYPE.STRING, string.getText());
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return valueDescriptor;
	}
	
	public final void pathTypeCheckExpr(AST _t,
		BasicFilterExpression filterExpression
	) throws RecognitionException {
		
		AST pathTypeCheckExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case AT_IS_NULL:
			{
				AST tmp30_AST_in = (AST)_t;
				match(_t,AT_IS_NULL);
				_t = _t.getNextSibling();
				
				filterExpression.setIsNullFunction(true);
				
				break;
			}
			case AT_IS_NOT_NULL:
			{
				AST tmp31_AST_in = (AST)_t;
				match(_t,AT_IS_NOT_NULL);
				_t = _t.getNextSibling();
				
				filterExpression.setIsNotNullFunction(true);
				
				break;
			}
			case AT_IS_ARRAY:
			{
				AST tmp32_AST_in = (AST)_t;
				match(_t,AT_IS_ARRAY);
				_t = _t.getNextSibling();
				
				filterExpression.setIsArrayFunction(true);
				
				break;
			}
			case AT_IS_OBJECT:
			{
				AST tmp33_AST_in = (AST)_t;
				match(_t,AT_IS_OBJECT);
				_t = _t.getNextSibling();
				
				filterExpression.setIsObjectFunction(true);
				
				break;
			}
			case AT_IS_VALUE:
			{
				AST tmp34_AST_in = (AST)_t;
				match(_t,AT_IS_VALUE);
				_t = _t.getNextSibling();
				
				filterExpression.setIsValueFunction(true);
				
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void filterMemberExpr(AST _t,
		BasicFilterExpression filterExpression
	) throws RecognitionException {
		
		AST filterMemberExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		MemberExpression memberExpr = null;
		
		
		try {      // for error handling
			memberExpr=pathNaviExpr(_t);
			_t = _retTree;
			
			filterExpression.addMemberExpression(memberExpr);
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"PATH",
		"MEMBER",
		"SIMPLE_KEY",
		"COMPLEX_KEY",
		"OBJECT_CONSTRUCTION",
		"ARRAY_INDEX",
		"ARRAY_CONSTRUCTION",
		"ARRAY_SLICE",
		"MULTI_LEVEL_UP",
		"FILTER",
		"ABSOLUTE",
		"DOT",
		"DOTDOT",
		"ID",
		"WILDCARD",
		"LBRACKET",
		"STRING",
		"RBRACKET",
		"COMMA",
		"INT",
		"SEMI",
		"CARET",
		"LCURLY",
		"RCURLY",
		"LPAREN",
		"RPAREN",
		"OR",
		"AND",
		"NOT",
		"AT_IS_NULL",
		"AT_IS_NOT_NULL",
		"AT_IS_ARRAY",
		"AT_IS_OBJECT",
		"AT_IS_VALUE",
		"AT_SIZE",
		"EQ",
		"NE",
		"LT",
		"LE",
		"GT",
		"GE",
		"AT_VALUE",
		"CONTAINS",
		"REAL",
		"\"null\"",
		"\"true\"",
		"\"false\"",
		"TYPE_CHECK",
		"ID_OR_ABSOLUTE",
		"INT_OR_REAL_OR_DOTS",
		"NEWLINE",
		"SINGLE_LINE_COMMENT",
		"MULTI_LINE_COMMENT",
		"WS",
		"DIGIT",
		"FRAC",
		"EXP",
		"ESC",
		"ID_START_LETTER",
		"ID_LETTER"
	};
	
	}
	
