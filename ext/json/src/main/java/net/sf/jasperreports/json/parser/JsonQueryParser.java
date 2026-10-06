// $ANTLR 2.7.5 (20050128): "json_grammar.g" -> "JsonQueryParser.java"$

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

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQueryParser extends antlr.LLkParser       implements JsonQueryParserTokenTypes
 {

protected JsonQueryParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public JsonQueryParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected JsonQueryParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public JsonQueryParser(TokenStream lexer) {
  this(lexer,2);
}

public JsonQueryParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void pathExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pathExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case ABSOLUTE:
		{
			AST tmp35_AST = null;
			tmp35_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp35_AST);
			match(ABSOLUTE);
			break;
		}
		case EOF:
		case DOT:
		case DOTDOT:
		case ID:
		case WILDCARD:
		case LBRACKET:
		case CARET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop4:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				memberExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop4;
			}
			
		} while (true);
		}
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			pathExpr_AST = (AST)currentAST.root;
			pathExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PATH,"Path Expr:")).add(pathExpr_AST));
			currentAST.root = pathExpr_AST;
			currentAST.child = pathExpr_AST!=null &&pathExpr_AST.getFirstChild()!=null ?
				pathExpr_AST.getFirstChild() : pathExpr_AST;
			currentAST.advanceChildToEnd();
		}
		pathExpr_AST = (AST)currentAST.root;
		returnAST = pathExpr_AST;
	}
	
	public final void memberExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST memberExpr_AST = null;
		
		pathNaviExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			filterExprMain();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case DOT:
		case DOTDOT:
		case ID:
		case WILDCARD:
		case LBRACKET:
		case CARET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			memberExpr_AST = (AST)currentAST.root;
			memberExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MEMBER,"Member expression:")).add(memberExpr_AST));
			currentAST.root = memberExpr_AST;
			currentAST.child = memberExpr_AST!=null &&memberExpr_AST.getFirstChild()!=null ?
				memberExpr_AST.getFirstChild() : memberExpr_AST;
			currentAST.advanceChildToEnd();
		}
		memberExpr_AST = (AST)currentAST.root;
		returnAST = memberExpr_AST;
	}
	
	public final void pathNaviExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pathNaviExpr_AST = null;
		
		boolean synPredMatched11 = false;
		if ((((LA(1) >= DOT && LA(1) <= WILDCARD)) && (_tokenSet_1.member(LA(2))))) {
			int _m11 = mark();
			synPredMatched11 = true;
			inputState.guessing++;
			try {
				{
				{
				switch ( LA(1)) {
				case DOT:
				{
					match(DOT);
					break;
				}
				case DOTDOT:
				{
					match(DOTDOT);
					break;
				}
				case ID:
				case WILDCARD:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case ID:
				{
					match(ID);
					break;
				}
				case WILDCARD:
				{
					match(WILDCARD);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				}
			}
			catch (RecognitionException pe) {
				synPredMatched11 = false;
			}
			rewind(_m11);
			inputState.guessing--;
		}
		if ( synPredMatched11 ) {
			simpleKeyExpr();
			astFactory.addASTChild(currentAST, returnAST);
			pathNaviExpr_AST = (AST)currentAST.root;
		}
		else {
			boolean synPredMatched14 = false;
			if (((LA(1)==DOTDOT||LA(1)==LBRACKET) && (LA(2)==LBRACKET||LA(2)==STRING))) {
				int _m14 = mark();
				synPredMatched14 = true;
				inputState.guessing++;
				try {
					{
					{
					switch ( LA(1)) {
					case DOTDOT:
					{
						match(DOTDOT);
						break;
					}
					case LBRACKET:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					match(LBRACKET);
					match(STRING);
					match(RBRACKET);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched14 = false;
				}
				rewind(_m14);
				inputState.guessing--;
			}
			if ( synPredMatched14 ) {
				complexKeyExpr();
				astFactory.addASTChild(currentAST, returnAST);
				pathNaviExpr_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched19 = false;
				if (((LA(1)==DOTDOT||LA(1)==LBRACKET) && (LA(2)==ID||LA(2)==LBRACKET||LA(2)==STRING))) {
					int _m19 = mark();
					synPredMatched19 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case DOTDOT:
						{
							match(DOTDOT);
							break;
						}
						case LBRACKET:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(LBRACKET);
						{
						switch ( LA(1)) {
						case STRING:
						{
							match(STRING);
							break;
						}
						case ID:
						{
							match(ID);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(COMMA);
						{
						switch ( LA(1)) {
						case STRING:
						{
							match(STRING);
							break;
						}
						case ID:
						{
							match(ID);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched19 = false;
					}
					rewind(_m19);
					inputState.guessing--;
				}
				if ( synPredMatched19 ) {
					objectConstructionExpr();
					astFactory.addASTChild(currentAST, returnAST);
					pathNaviExpr_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched22 = false;
					if (((LA(1)==DOTDOT||LA(1)==LBRACKET) && (LA(2)==LBRACKET||LA(2)==INT))) {
						int _m22 = mark();
						synPredMatched22 = true;
						inputState.guessing++;
						try {
							{
							{
							switch ( LA(1)) {
							case DOTDOT:
							{
								match(DOTDOT);
								break;
							}
							case LBRACKET:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							}
							}
							match(LBRACKET);
							match(INT);
							match(RBRACKET);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched22 = false;
						}
						rewind(_m22);
						inputState.guessing--;
					}
					if ( synPredMatched22 ) {
						arrayExpr();
						astFactory.addASTChild(currentAST, returnAST);
						pathNaviExpr_AST = (AST)currentAST.root;
					}
					else {
						boolean synPredMatched25 = false;
						if (((LA(1)==DOTDOT||LA(1)==LBRACKET) && (LA(2)==LBRACKET||LA(2)==INT))) {
							int _m25 = mark();
							synPredMatched25 = true;
							inputState.guessing++;
							try {
								{
								{
								switch ( LA(1)) {
								case DOTDOT:
								{
									match(DOTDOT);
									break;
								}
								case LBRACKET:
								{
									break;
								}
								default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
								}
								}
								match(LBRACKET);
								match(INT);
								match(COMMA);
								match(INT);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched25 = false;
							}
							rewind(_m25);
							inputState.guessing--;
						}
						if ( synPredMatched25 ) {
							arrayConstructionExpr();
							astFactory.addASTChild(currentAST, returnAST);
							pathNaviExpr_AST = (AST)currentAST.root;
						}
						else if ((LA(1)==DOTDOT||LA(1)==LBRACKET) && (LA(2)==LBRACKET||LA(2)==INT||LA(2)==SEMI)) {
							arraySliceExpr();
							astFactory.addASTChild(currentAST, returnAST);
							pathNaviExpr_AST = (AST)currentAST.root;
						}
						else if ((LA(1)==CARET)) {
							multiLevelUpExpr();
							astFactory.addASTChild(currentAST, returnAST);
							pathNaviExpr_AST = (AST)currentAST.root;
						}
						else {
							throw new NoViableAltException(LT(1), getFilename());
						}
						}}}}
						returnAST = pathNaviExpr_AST;
					}
					
	public final void filterExprMain() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterExprMain_AST = null;
		
		match(LPAREN);
		filterExpr();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		if ( inputState.guessing==0 ) {
			filterExprMain_AST = (AST)currentAST.root;
			filterExprMain_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FILTER,"Filter expression main:")).add(filterExprMain_AST));
			currentAST.root = filterExprMain_AST;
			currentAST.child = filterExprMain_AST!=null &&filterExprMain_AST.getFirstChild()!=null ?
				filterExprMain_AST.getFirstChild() : filterExprMain_AST;
			currentAST.advanceChildToEnd();
		}
		filterExprMain_AST = (AST)currentAST.root;
		returnAST = filterExprMain_AST;
	}
	
	public final void simpleKeyExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simpleKeyExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case DOT:
		{
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp39_AST);
			match(DOT);
			break;
		}
		case DOTDOT:
		{
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp40_AST);
			match(DOTDOT);
			break;
		}
		case ID:
		case WILDCARD:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case ID:
		{
			AST tmp41_AST = null;
			tmp41_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp41_AST);
			match(ID);
			break;
		}
		case WILDCARD:
		{
			AST tmp42_AST = null;
			tmp42_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp42_AST);
			match(WILDCARD);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			simpleKeyExpr_AST = (AST)currentAST.root;
			simpleKeyExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SIMPLE_KEY,"Simple expression:")).add(simpleKeyExpr_AST));
			currentAST.root = simpleKeyExpr_AST;
			currentAST.child = simpleKeyExpr_AST!=null &&simpleKeyExpr_AST.getFirstChild()!=null ?
				simpleKeyExpr_AST.getFirstChild() : simpleKeyExpr_AST;
			currentAST.advanceChildToEnd();
		}
		simpleKeyExpr_AST = (AST)currentAST.root;
		returnAST = simpleKeyExpr_AST;
	}
	
	public final void complexKeyExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST complexKeyExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case DOTDOT:
		{
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp43_AST);
			match(DOTDOT);
			break;
		}
		case LBRACKET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LBRACKET);
		AST tmp45_AST = null;
		tmp45_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp45_AST);
		match(STRING);
		match(RBRACKET);
		if ( inputState.guessing==0 ) {
			complexKeyExpr_AST = (AST)currentAST.root;
			complexKeyExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(COMPLEX_KEY,"Complex expression:")).add(complexKeyExpr_AST));
			currentAST.root = complexKeyExpr_AST;
			currentAST.child = complexKeyExpr_AST!=null &&complexKeyExpr_AST.getFirstChild()!=null ?
				complexKeyExpr_AST.getFirstChild() : complexKeyExpr_AST;
			currentAST.advanceChildToEnd();
		}
		complexKeyExpr_AST = (AST)currentAST.root;
		returnAST = complexKeyExpr_AST;
	}
	
	public final void objectConstructionExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST objectConstructionExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case DOTDOT:
		{
			AST tmp47_AST = null;
			tmp47_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp47_AST);
			match(DOTDOT);
			break;
		}
		case LBRACKET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LBRACKET);
		{
		switch ( LA(1)) {
		case STRING:
		{
			AST tmp49_AST = null;
			tmp49_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp49_AST);
			match(STRING);
			break;
		}
		case ID:
		{
			AST tmp50_AST = null;
			tmp50_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp50_AST);
			match(ID);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		int _cnt36=0;
		_loop36:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				{
				switch ( LA(1)) {
				case STRING:
				{
					AST tmp52_AST = null;
					tmp52_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp52_AST);
					match(STRING);
					break;
				}
				case ID:
				{
					AST tmp53_AST = null;
					tmp53_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp53_AST);
					match(ID);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				if ( _cnt36>=1 ) { break _loop36; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt36++;
		} while (true);
		}
		match(RBRACKET);
		if ( inputState.guessing==0 ) {
			objectConstructionExpr_AST = (AST)currentAST.root;
			objectConstructionExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_CONSTRUCTION,"Object construction expression:")).add(objectConstructionExpr_AST));
			currentAST.root = objectConstructionExpr_AST;
			currentAST.child = objectConstructionExpr_AST!=null &&objectConstructionExpr_AST.getFirstChild()!=null ?
				objectConstructionExpr_AST.getFirstChild() : objectConstructionExpr_AST;
			currentAST.advanceChildToEnd();
		}
		objectConstructionExpr_AST = (AST)currentAST.root;
		returnAST = objectConstructionExpr_AST;
	}
	
	public final void arrayExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case DOTDOT:
		{
			AST tmp55_AST = null;
			tmp55_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp55_AST);
			match(DOTDOT);
			break;
		}
		case LBRACKET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LBRACKET);
		AST tmp57_AST = null;
		tmp57_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp57_AST);
		match(INT);
		match(RBRACKET);
		if ( inputState.guessing==0 ) {
			arrayExpr_AST = (AST)currentAST.root;
			arrayExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARRAY_INDEX,"Array expression:")).add(arrayExpr_AST));
			currentAST.root = arrayExpr_AST;
			currentAST.child = arrayExpr_AST!=null &&arrayExpr_AST.getFirstChild()!=null ?
				arrayExpr_AST.getFirstChild() : arrayExpr_AST;
			currentAST.advanceChildToEnd();
		}
		arrayExpr_AST = (AST)currentAST.root;
		returnAST = arrayExpr_AST;
	}
	
	public final void arrayConstructionExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayConstructionExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case DOTDOT:
		{
			AST tmp59_AST = null;
			tmp59_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp59_AST);
			match(DOTDOT);
			break;
		}
		case LBRACKET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LBRACKET);
		AST tmp61_AST = null;
		tmp61_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp61_AST);
		match(INT);
		{
		int _cnt42=0;
		_loop42:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				AST tmp63_AST = null;
				tmp63_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp63_AST);
				match(INT);
			}
			else {
				if ( _cnt42>=1 ) { break _loop42; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt42++;
		} while (true);
		}
		match(RBRACKET);
		if ( inputState.guessing==0 ) {
			arrayConstructionExpr_AST = (AST)currentAST.root;
			arrayConstructionExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARRAY_CONSTRUCTION,"Array construction expression:")).add(arrayConstructionExpr_AST));
			currentAST.root = arrayConstructionExpr_AST;
			currentAST.child = arrayConstructionExpr_AST!=null &&arrayConstructionExpr_AST.getFirstChild()!=null ?
				arrayConstructionExpr_AST.getFirstChild() : arrayConstructionExpr_AST;
			currentAST.advanceChildToEnd();
		}
		arrayConstructionExpr_AST = (AST)currentAST.root;
		returnAST = arrayConstructionExpr_AST;
	}
	
	public final void arraySliceExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arraySliceExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case DOTDOT:
		{
			AST tmp65_AST = null;
			tmp65_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp65_AST);
			match(DOTDOT);
			break;
		}
		case LBRACKET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LBRACKET);
		{
		switch ( LA(1)) {
		case INT:
		{
			{
			AST tmp67_AST = null;
			tmp67_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp67_AST);
			match(INT);
			AST tmp68_AST = null;
			tmp68_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp68_AST);
			match(SEMI);
			{
			switch ( LA(1)) {
			case INT:
			{
				AST tmp69_AST = null;
				tmp69_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp69_AST);
				match(INT);
				break;
			}
			case RBRACKET:
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
			break;
		}
		case SEMI:
		{
			{
			AST tmp70_AST = null;
			tmp70_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp70_AST);
			match(SEMI);
			AST tmp71_AST = null;
			tmp71_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp71_AST);
			match(INT);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RBRACKET);
		if ( inputState.guessing==0 ) {
			arraySliceExpr_AST = (AST)currentAST.root;
			arraySliceExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARRAY_SLICE,"Array slice expression:")).add(arraySliceExpr_AST));
			currentAST.root = arraySliceExpr_AST;
			currentAST.child = arraySliceExpr_AST!=null &&arraySliceExpr_AST.getFirstChild()!=null ?
				arraySliceExpr_AST.getFirstChild() : arraySliceExpr_AST;
			currentAST.advanceChildToEnd();
		}
		arraySliceExpr_AST = (AST)currentAST.root;
		returnAST = arraySliceExpr_AST;
	}
	
	public final void multiLevelUpExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiLevelUpExpr_AST = null;
		
		AST tmp73_AST = null;
		tmp73_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp73_AST);
		match(CARET);
		{
		switch ( LA(1)) {
		case LCURLY:
		{
			match(LCURLY);
			AST tmp75_AST = null;
			tmp75_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp75_AST);
			match(INT);
			match(RCURLY);
			break;
		}
		case EOF:
		case DOT:
		case DOTDOT:
		case ID:
		case WILDCARD:
		case LBRACKET:
		case CARET:
		case LPAREN:
		case AT_IS_NULL:
		case AT_IS_NOT_NULL:
		case AT_IS_ARRAY:
		case AT_IS_OBJECT:
		case AT_IS_VALUE:
		case AT_SIZE:
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
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			multiLevelUpExpr_AST = (AST)currentAST.root;
			multiLevelUpExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MULTI_LEVEL_UP,"Multi level up:")).add(multiLevelUpExpr_AST));
			currentAST.root = multiLevelUpExpr_AST;
			currentAST.child = multiLevelUpExpr_AST!=null &&multiLevelUpExpr_AST.getFirstChild()!=null ?
				multiLevelUpExpr_AST.getFirstChild() : multiLevelUpExpr_AST;
			currentAST.advanceChildToEnd();
		}
		multiLevelUpExpr_AST = (AST)currentAST.root;
		returnAST = multiLevelUpExpr_AST;
	}
	
	public final void filterExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterExpr_AST = null;
		
		andExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop54:
		do {
			if ((LA(1)==OR)) {
				AST tmp77_AST = null;
				tmp77_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp77_AST);
				match(OR);
				andExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop54;
			}
			
		} while (true);
		}
		filterExpr_AST = (AST)currentAST.root;
		returnAST = filterExpr_AST;
	}
	
	public final void andExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpr_AST = null;
		
		notExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop57:
		do {
			if ((LA(1)==AND)) {
				AST tmp78_AST = null;
				tmp78_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp78_AST);
				match(AND);
				notExpr();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop57;
			}
			
		} while (true);
		}
		andExpr_AST = (AST)currentAST.root;
		returnAST = andExpr_AST;
	}
	
	public final void notExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST notExpr_AST = null;
		
		{
		switch ( LA(1)) {
		case NOT:
		{
			AST tmp79_AST = null;
			tmp79_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp79_AST);
			match(NOT);
			break;
		}
		case DOT:
		case DOTDOT:
		case ID:
		case WILDCARD:
		case LBRACKET:
		case CARET:
		case LPAREN:
		case AT_IS_NULL:
		case AT_IS_NOT_NULL:
		case AT_IS_ARRAY:
		case AT_IS_OBJECT:
		case AT_IS_VALUE:
		case AT_SIZE:
		case AT_VALUE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		basicExpr();
		astFactory.addASTChild(currentAST, returnAST);
		notExpr_AST = (AST)currentAST.root;
		returnAST = notExpr_AST;
	}
	
	public final void basicExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST basicExpr_AST = null;
		
		switch ( LA(1)) {
		case DOT:
		case DOTDOT:
		case ID:
		case WILDCARD:
		case LBRACKET:
		case CARET:
		case AT_IS_NULL:
		case AT_IS_NOT_NULL:
		case AT_IS_ARRAY:
		case AT_IS_OBJECT:
		case AT_IS_VALUE:
		case AT_SIZE:
		case AT_VALUE:
		{
			filterNaviExpr();
			astFactory.addASTChild(currentAST, returnAST);
			basicExpr_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			filterExpr();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			basicExpr_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = basicExpr_AST;
	}
	
	public final void filterNaviExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterNaviExpr_AST = null;
		
		switch ( LA(1)) {
		case AT_IS_NULL:
		case AT_IS_NOT_NULL:
		case AT_IS_ARRAY:
		case AT_IS_OBJECT:
		case AT_IS_VALUE:
		{
			pathTypeCheckExpr();
			astFactory.addASTChild(currentAST, returnAST);
			filterNaviExpr_AST = (AST)currentAST.root;
			break;
		}
		case AT_SIZE:
		{
			sizeFnExpr();
			astFactory.addASTChild(currentAST, returnAST);
			filterNaviExpr_AST = (AST)currentAST.root;
			break;
		}
		case AT_VALUE:
		{
			valueFnExpr();
			astFactory.addASTChild(currentAST, returnAST);
			filterNaviExpr_AST = (AST)currentAST.root;
			break;
		}
		case DOT:
		case DOTDOT:
		case ID:
		case WILDCARD:
		case LBRACKET:
		case CARET:
		{
			{
			int _cnt63=0;
			_loop63:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					pathNaviExpr();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt63>=1 ) { break _loop63; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt63++;
			} while (true);
			}
			{
			switch ( LA(1)) {
			case AT_IS_NULL:
			case AT_IS_NOT_NULL:
			case AT_IS_ARRAY:
			case AT_IS_OBJECT:
			case AT_IS_VALUE:
			{
				pathTypeCheckExpr();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case AT_SIZE:
			{
				sizeFnExpr();
				astFactory.addASTChild(currentAST, returnAST);
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
				operator_to_value();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			filterNaviExpr_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = filterNaviExpr_AST;
	}
	
	public final void pathTypeCheckExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pathTypeCheckExpr_AST = null;
		
		switch ( LA(1)) {
		case AT_IS_NULL:
		{
			AST tmp82_AST = null;
			tmp82_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp82_AST);
			match(AT_IS_NULL);
			pathTypeCheckExpr_AST = (AST)currentAST.root;
			break;
		}
		case AT_IS_NOT_NULL:
		{
			AST tmp83_AST = null;
			tmp83_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp83_AST);
			match(AT_IS_NOT_NULL);
			pathTypeCheckExpr_AST = (AST)currentAST.root;
			break;
		}
		case AT_IS_ARRAY:
		{
			AST tmp84_AST = null;
			tmp84_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp84_AST);
			match(AT_IS_ARRAY);
			pathTypeCheckExpr_AST = (AST)currentAST.root;
			break;
		}
		case AT_IS_OBJECT:
		{
			AST tmp85_AST = null;
			tmp85_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp85_AST);
			match(AT_IS_OBJECT);
			pathTypeCheckExpr_AST = (AST)currentAST.root;
			break;
		}
		case AT_IS_VALUE:
		{
			AST tmp86_AST = null;
			tmp86_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp86_AST);
			match(AT_IS_VALUE);
			pathTypeCheckExpr_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = pathTypeCheckExpr_AST;
	}
	
	public final void sizeFnExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sizeFnExpr_AST = null;
		
		AST tmp87_AST = null;
		tmp87_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp87_AST);
		match(AT_SIZE);
		{
		switch ( LA(1)) {
		case EQ:
		{
			AST tmp88_AST = null;
			tmp88_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp88_AST);
			match(EQ);
			break;
		}
		case NE:
		{
			AST tmp89_AST = null;
			tmp89_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp89_AST);
			match(NE);
			break;
		}
		case LT:
		{
			AST tmp90_AST = null;
			tmp90_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp90_AST);
			match(LT);
			break;
		}
		case LE:
		{
			AST tmp91_AST = null;
			tmp91_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp91_AST);
			match(LE);
			break;
		}
		case GT:
		{
			AST tmp92_AST = null;
			tmp92_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp92_AST);
			match(GT);
			break;
		}
		case GE:
		{
			AST tmp93_AST = null;
			tmp93_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp93_AST);
			match(GE);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		AST tmp94_AST = null;
		tmp94_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp94_AST);
		match(INT);
		sizeFnExpr_AST = (AST)currentAST.root;
		returnAST = sizeFnExpr_AST;
	}
	
	public final void valueFnExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valueFnExpr_AST = null;
		
		AST tmp95_AST = null;
		tmp95_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp95_AST);
		match(AT_VALUE);
		operator_to_value();
		astFactory.addASTChild(currentAST, returnAST);
		valueFnExpr_AST = (AST)currentAST.root;
		returnAST = valueFnExpr_AST;
	}
	
	public final void operator_to_value() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST operator_to_value_AST = null;
		
		if ((LA(1)==EQ||LA(1)==NE||LA(1)==CONTAINS) && (LA(2)==STRING)) {
			{
			switch ( LA(1)) {
			case EQ:
			{
				AST tmp96_AST = null;
				tmp96_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp96_AST);
				match(EQ);
				break;
			}
			case NE:
			{
				AST tmp97_AST = null;
				tmp97_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp97_AST);
				match(NE);
				break;
			}
			case CONTAINS:
			{
				AST tmp98_AST = null;
				tmp98_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp98_AST);
				match(CONTAINS);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp99_AST = null;
			tmp99_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp99_AST);
			match(STRING);
			operator_to_value_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==EQ||LA(1)==NE) && (_tokenSet_2.member(LA(2)))) {
			{
			switch ( LA(1)) {
			case EQ:
			{
				AST tmp100_AST = null;
				tmp100_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp100_AST);
				match(EQ);
				break;
			}
			case NE:
			{
				AST tmp101_AST = null;
				tmp101_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp101_AST);
				match(NE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			non_string_value();
			astFactory.addASTChild(currentAST, returnAST);
			operator_to_value_AST = (AST)currentAST.root;
		}
		else if (((LA(1) >= LT && LA(1) <= GE))) {
			{
			switch ( LA(1)) {
			case LT:
			{
				AST tmp102_AST = null;
				tmp102_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp102_AST);
				match(LT);
				break;
			}
			case LE:
			{
				AST tmp103_AST = null;
				tmp103_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp103_AST);
				match(LE);
				break;
			}
			case GT:
			{
				AST tmp104_AST = null;
				tmp104_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp104_AST);
				match(GT);
				break;
			}
			case GE:
			{
				AST tmp105_AST = null;
				tmp105_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp105_AST);
				match(GE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case INT:
			{
				AST tmp106_AST = null;
				tmp106_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp106_AST);
				match(INT);
				break;
			}
			case REAL:
			{
				AST tmp107_AST = null;
				tmp107_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp107_AST);
				match(REAL);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			operator_to_value_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = operator_to_value_AST;
	}
	
	public final void non_string_value() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST non_string_value_AST = null;
		
		switch ( LA(1)) {
		case LITERAL_null:
		{
			AST tmp108_AST = null;
			tmp108_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp108_AST);
			match(LITERAL_null);
			non_string_value_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_true:
		{
			AST tmp109_AST = null;
			tmp109_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp109_AST);
			match(LITERAL_true);
			non_string_value_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_false:
		{
			AST tmp110_AST = null;
			tmp110_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp110_AST);
			match(LITERAL_false);
			non_string_value_AST = (AST)currentAST.root;
			break;
		}
		case INT:
		{
			AST tmp111_AST = null;
			tmp111_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp111_AST);
			match(INT);
			non_string_value_AST = (AST)currentAST.root;
			break;
		}
		case REAL:
		{
			AST tmp112_AST = null;
			tmp112_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp112_AST);
			match(REAL);
			non_string_value_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = non_string_value_AST;
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
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 34570240L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 105544829337602L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 2111062333718528L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
