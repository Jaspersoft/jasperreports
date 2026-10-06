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

public interface JsonQueryParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int PATH = 4;
	int MEMBER = 5;
	int SIMPLE_KEY = 6;
	int COMPLEX_KEY = 7;
	int OBJECT_CONSTRUCTION = 8;
	int ARRAY_INDEX = 9;
	int ARRAY_CONSTRUCTION = 10;
	int ARRAY_SLICE = 11;
	int MULTI_LEVEL_UP = 12;
	int FILTER = 13;
	int ABSOLUTE = 14;
	int DOT = 15;
	int DOTDOT = 16;
	int ID = 17;
	int WILDCARD = 18;
	int LBRACKET = 19;
	int STRING = 20;
	int RBRACKET = 21;
	int COMMA = 22;
	int INT = 23;
	int SEMI = 24;
	int CARET = 25;
	int LCURLY = 26;
	int RCURLY = 27;
	int LPAREN = 28;
	int RPAREN = 29;
	int OR = 30;
	int AND = 31;
	int NOT = 32;
	int AT_IS_NULL = 33;
	int AT_IS_NOT_NULL = 34;
	int AT_IS_ARRAY = 35;
	int AT_IS_OBJECT = 36;
	int AT_IS_VALUE = 37;
	int AT_SIZE = 38;
	int EQ = 39;
	int NE = 40;
	int LT = 41;
	int LE = 42;
	int GT = 43;
	int GE = 44;
	int AT_VALUE = 45;
	int CONTAINS = 46;
	int REAL = 47;
	int LITERAL_null = 48;
	int LITERAL_true = 49;
	int LITERAL_false = 50;
	int TYPE_CHECK = 51;
	int ID_OR_ABSOLUTE = 52;
	int INT_OR_REAL_OR_DOTS = 53;
	int NEWLINE = 54;
	int SINGLE_LINE_COMMENT = 55;
	int MULTI_LINE_COMMENT = 56;
	int WS = 57;
	int DIGIT = 58;
	int FRAC = 59;
	int EXP = 60;
	int ESC = 61;
	int ID_START_LETTER = 62;
	int ID_LETTER = 63;
}
