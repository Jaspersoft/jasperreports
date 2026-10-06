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
package net.sf.jasperreports.engine.json.parser;
}


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
class JsonQueryParser extends Parser;

options {
    buildAST=true;
    k=2;
    defaultErrorHandler=false;
}

tokens {
    PATH;
    MEMBER;

    SIMPLE_KEY;
    COMPLEX_KEY;
    OBJECT_CONSTRUCTION;
    ARRAY_INDEX;
    ARRAY_CONSTRUCTION;
    ARRAY_SLICE;
    MULTI_LEVEL_UP;

    FILTER;
}

pathExpr
    : (ABSOLUTE)? (memberExpr)* EOF!
        { #pathExpr = #([PATH, "Path Expr:"], #pathExpr); }
    ;

memberExpr
    : pathNaviExpr (filterExprMain)?
        { #memberExpr = #([MEMBER, "Member expression:"], #memberExpr); }
    ;

pathNaviExpr
    : ( (DOT | DOTDOT)? (ID | WILDCARD ) ) => simpleKeyExpr
    | ( (DOTDOT)? LBRACKET STRING RBRACKET) => complexKeyExpr
    | ( (DOTDOT)? LBRACKET (STRING | ID) COMMA (STRING | ID) ) => objectConstructionExpr
    | ( (DOTDOT)? LBRACKET INT RBRACKET ) => arrayExpr
    | ( (DOTDOT)? LBRACKET INT COMMA INT ) => arrayConstructionExpr
    | arraySliceExpr
    | multiLevelUpExpr
    ;

simpleKeyExpr
    : (DOT | DOTDOT)? (ID | WILDCARD )
        { #simpleKeyExpr = #([SIMPLE_KEY, "Simple expression:"], #simpleKeyExpr); }
    ;

complexKeyExpr
    : (DOTDOT)? LBRACKET! STRING RBRACKET!
        { #complexKeyExpr = #([COMPLEX_KEY, "Complex expression:"], #complexKeyExpr); }
    ;

objectConstructionExpr
    : (DOTDOT)? LBRACKET! (STRING | ID) (COMMA! (STRING | ID))+ RBRACKET!
       { #objectConstructionExpr = #([OBJECT_CONSTRUCTION, "Object construction expression:"], #objectConstructionExpr); }
    ;

arrayExpr
    : (DOTDOT)? LBRACKET! INT RBRACKET!
        { #arrayExpr = #([ARRAY_INDEX, "Array expression:"], #arrayExpr); }
    ;

arrayConstructionExpr
    : (DOTDOT)? LBRACKET! INT (COMMA! INT)+ RBRACKET!
        { #arrayConstructionExpr = #([ARRAY_CONSTRUCTION, "Array construction expression:"], #arrayConstructionExpr); }
    ;

arraySliceExpr
    : (DOTDOT)? LBRACKET! ( (INT SEMI (INT)?) | (SEMI INT) ) RBRACKET!
        { #arraySliceExpr = #([ARRAY_SLICE, "Array slice expression:"], #arraySliceExpr); }
    ;

multiLevelUpExpr
    : CARET (LCURLY! INT RCURLY!)?
        { #multiLevelUpExpr = #([MULTI_LEVEL_UP, "Multi level up:"], #multiLevelUpExpr); }
    ;


filterExprMain
    : LPAREN! filterExpr RPAREN!
        { #filterExprMain = #([FILTER, "Filter expression main:"], #filterExprMain); }
    ;

filterExpr
    : andExpr (OR^ andExpr)*
    ;

andExpr
    : notExpr (AND^ notExpr)*
    ;

notExpr
    : (NOT^)? basicExpr
    ;

basicExpr
    : filterNaviExpr
    | LPAREN! filterExpr RPAREN!
    ;

filterNaviExpr
    : pathTypeCheckExpr
    | sizeFnExpr
    | valueFnExpr
    | (pathNaviExpr)+ (pathTypeCheckExpr | sizeFnExpr | operator_to_value)
    ;

pathTypeCheckExpr
    : AT_IS_NULL
    | AT_IS_NOT_NULL
    | AT_IS_ARRAY
    | AT_IS_OBJECT
    | AT_IS_VALUE
    ;

sizeFnExpr
    : AT_SIZE (EQ | NE | LT | LE | GT | GE) INT
    ;

valueFnExpr
    : AT_VALUE operator_to_value
    ;

operator_to_value
    : (EQ | NE | CONTAINS) STRING
    | (EQ | NE ) non_string_value
    | (LT | LE | GT | GE) (INT | REAL)
    ;

non_string_value
    : "null"
    | "true"
    | "false"
    | INT
    | REAL
    ;


{
import net.sf.jasperreports.engine.json.expression.JsonQLExpression;
import net.sf.jasperreports.engine.json.expression.filter.FilterExpression.LOGICAL_OPERATOR;
import net.sf.jasperreports.engine.json.expression.filter.FilterExpression.VALUE_TYPE;
import net.sf.jasperreports.engine.json.expression.filter.*;
import net.sf.jasperreports.engine.json.expression.member.MemberExpression.DIRECTION;
import net.sf.jasperreports.engine.json.expression.member.*;

import net.sf.jasperreports.engine.type.JsonOperatorEnum;
}

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
class JsonQueryWalker extends TreeParser;

jsonQLExpression returns [JsonQLExpression jsonQLExpression = new JsonQLExpression()]
    : #(PATH (abs:ABSOLUTE)? (memberExpr[jsonQLExpression])*)
        {
            if (abs != null) {
                jsonQLExpression.setIsAbsolute(true);
            }
        }
    ;

memberExpr [JsonQLExpression jsonQLExpression]
        {
            MemberExpression memberExpr = null;
            FilterExpression filterExpression = null;
        }
    : #(MEMBER
            memberExpr=pathNaviExpr (filterExpression = filterExprMain)?
      )
        {
            memberExpr.setFilterExpression(filterExpression);
            jsonQLExpression.addMemberExpression(memberExpr);
        }
    ;

pathNaviExpr returns [MemberExpression memberExpr = null]
        {
            DIRECTION dir = DIRECTION.DOWN;
        }
    : #(SIMPLE_KEY (dir=direction)? (id:ID | WILDCARD ))
        {
            if (id != null) {
                // object key
                memberExpr = new ObjectKeyExpression(dir, id.getText());
            } else {
                // wildcard
                memberExpr = new ObjectKeyExpression(dir);
            }
        }
    | #(COMPLEX_KEY (dir=direction)? s:STRING)
        {
            memberExpr = new ObjectKeyExpression(dir, s.getText(), true);
        }
    |   {
            memberExpr = new ObjectConstructionExpression();
        }
      #(OBJECT_CONSTRUCTION (dir=direction)? (addObjectKey[(ObjectConstructionExpression)memberExpr])+)
        {
            ((ObjectConstructionExpression)memberExpr).setDirection(dir);
        }
    | #(ARRAY_INDEX (dir=direction)? n:INT)
        {
            memberExpr = new ArrayIndexExpression(dir, Integer.parseInt(n.getText()));
        }
    |   {
            memberExpr = new ArrayConstructionExpression();
        }
      #(ARRAY_CONSTRUCTION (dir=direction)? (addArrayIndex[(ArrayConstructionExpression)memberExpr])+)
        {
            ((ArrayConstructionExpression)memberExpr).setDirection(dir);
        }
    | #(ARRAY_SLICE (dir=direction)? (sliceStart:INT)? SEMI (sliceEnd:INT)?)
        {
            Integer start = null, end = null;

            if (sliceStart != null) {
                start = Integer.valueOf(sliceStart.getText());
            }
            if (sliceEnd != null) {
                end = Integer.valueOf(sliceEnd.getText());
            }

            memberExpr = new ArraySliceExpression(dir, start, end);
        }
    | #(MULTI_LEVEL_UP CARET (levelUp:INT)?)
        {
            int level = 1;
            if (levelUp != null) {
                level = Integer.parseInt(levelUp.getText());
            }
            memberExpr = new MultiLevelUpExpression(level);
        }
    ;

addObjectKey [ObjectConstructionExpression objConstrExpr]
    : (str:STRING | id:ID)
        {
            String key = null;
            if (str != null) {
                key = str.getText();
            } else {
                key = id.getText();
            }
            objConstrExpr.addKey(key);
        }
    ;

addArrayIndex [ArrayConstructionExpression arrayConstrExpr]
    : idx:INT
        {
            arrayConstrExpr.addIndex(Integer.parseInt(idx.getText()));
        }
    ;

filterExprMain returns [FilterExpression filterExpression = null]
    : #(FILTER filterExpression=filterExpr)
    ;

filterExpr returns [FilterExpression result = null]
        {
            FilterExpression fe1, fe2;
        }
    : #(AND fe1=filterExpr fe2=filterExpr)
        {
            result = new CompoundFilterExpression(fe1, fe2, LOGICAL_OPERATOR.AND);
        }
    | #(OR fe1=filterExpr fe2=filterExpr)
        {
            result = new CompoundFilterExpression(fe1, fe2, LOGICAL_OPERATOR.OR);
        }
    | #(NOT fe1=filterExpr)
        {
            result = new NotFilterExpression(fe1);
        }
    | result=filterExprMinimal
    ;

filterExprMinimal returns [BasicFilterExpression filterExpression = new BasicFilterExpression()]
        {
            ValueDescriptor val = null;
            JsonOperatorEnum op = null;
        }
    : (sizeFn:AT_SIZE | AT_VALUE) op=operator val=value
        {
            if (sizeFn != null) {
                filterExpression.setIsSizeFunction(true);
            }
            filterExpression.setOperator(op);
            filterExpression.setValueDescriptor(val);
        }
    | pathTypeCheckExpr[filterExpression]
    | (filterMemberExpr[filterExpression])+ (pathTypeCheckExpr[filterExpression] | ((szFn:AT_SIZE)? op=operator val=value))
        {
            if (szFn != null) {
                filterExpression.setIsSizeFunction(true);
            }
            filterExpression.setOperator(op);
            filterExpression.setValueDescriptor(val);
        }
    ;

pathTypeCheckExpr [BasicFilterExpression filterExpression]
    : AT_IS_NULL
        {
            filterExpression.setIsNullFunction(true);
        }
    | AT_IS_NOT_NULL
        {
            filterExpression.setIsNotNullFunction(true);
        }
    | AT_IS_ARRAY
        {
            filterExpression.setIsArrayFunction(true);
        }
    | AT_IS_OBJECT
        {
            filterExpression.setIsObjectFunction(true);
        }
    | AT_IS_VALUE
        {
            filterExpression.setIsValueFunction(true);
        }
    ;

filterMemberExpr [BasicFilterExpression filterExpression]
        {
            MemberExpression memberExpr = null;
        }
    :  memberExpr=pathNaviExpr
        {
            filterExpression.addMemberExpression(memberExpr);
        }
    ;

direction returns [DIRECTION dir = DIRECTION.DOWN]
    : DOT
    | DOTDOT
        { dir = DIRECTION.ANYWHERE_DOWN; }
    ;

operator returns [JsonOperatorEnum operator = null]
    : EQ
        { operator = JsonOperatorEnum.EQ; }
    | NE
        { operator = JsonOperatorEnum.NE; }
    | LT
        { operator = JsonOperatorEnum.LT; }
    | LE
        { operator = JsonOperatorEnum.LE; }
    | GT
        { operator = JsonOperatorEnum.GT; }
    | GE
        { operator = JsonOperatorEnum.GE; }
    | CONTAINS
        { operator = JsonOperatorEnum.CONTAINS; }
    ;

value returns [ValueDescriptor valueDescriptor = null]
    : "null"
        { valueDescriptor = new ValueDescriptor(VALUE_TYPE.NULL, "null"); }
    | "true"
        { valueDescriptor = new ValueDescriptor(VALUE_TYPE.BOOLEAN, "true"); }
    | "false"
        { valueDescriptor = new ValueDescriptor(VALUE_TYPE.BOOLEAN, "false"); }
    | integer: INT
        { valueDescriptor = new ValueDescriptor(VALUE_TYPE.INTEGER, integer.getText()); }
    | real: REAL
        { valueDescriptor = new ValueDescriptor(VALUE_TYPE.DOUBLE, real.getText()); }
    | string: STRING
        { valueDescriptor = new ValueDescriptor(VALUE_TYPE.STRING, string.getText()); }
    ;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
class JsonQueryLexer extends Lexer;

options {
    defaultErrorHandler=false;
    k=2;
    charVocabulary='\u0000'..'\uFFFE';
    filter=WS;
}

{
    @Override
    public void reportError(RecognitionException re) {
        throw new RuntimeException(re);
    }

}

SEMI
    : ':'
    ;
COMMA
    : ','
    ;
WILDCARD
    : '*'
    ;
CARET
    : '^'
    ;
LCURLY
    : '{'
    ;
RCURLY
    : '}'
    ;
LBRACKET
    : '['
    ;
RBRACKET
    : ']'
    ;
LPAREN
    : '('
    ;
RPAREN
    : ')'
    ;
AND
    : "&&"
    ;
OR
    : "||"
    ;
NOT
    : '!'
    ;
EQ
    : "=="
    ;
NE
    : "!="
    ;
CONTAINS
    : "*="
    ;
LT
    : "<"
    ;
LE
    : "<="
    ;
GT
    : ">"
    ;
GE
    : ">="
    ;
AT_SIZE
    : "@size"
    ;
AT_VALUE
    : "@val"
    ;
TYPE_CHECK
    : ("@isNotNull") => AT_IS_NOT_NULL { $setType(AT_IS_NOT_NULL); }
    | ("@isNull") => AT_IS_NULL { $setType(AT_IS_NULL); }
    | ("@isArray") => AT_IS_ARRAY { $setType(AT_IS_ARRAY); }
    | ("@isObject") => AT_IS_OBJECT { $setType(AT_IS_OBJECT); }
    | ("@isValue") => AT_IS_VALUE { $setType(AT_IS_VALUE); }
    ;
STRING
    : '"'! (ESC | ~('"' | '\\'))* '"'!
    ;
ID_OR_ABSOLUTE
    : { getColumn() == 1 }? ( '$' ID_LETTER ) => ID { $setType(ID); }
    | { getColumn() == 1 && getLine() == 1 }? '$' { $setType(ABSOLUTE); }
    | { getColumn() == 1 }? ( ('a'..'z' | 'A'..'Z' | '_') (ID_LETTER)? ) => ID { $setType(ID); }
    | { getColumn() == 1 && getLine() > 1 || getColumn() > 1 }? ID { $setType(ID); }
    ;
INT_OR_REAL_OR_DOTS
    :  ( ('-')? (DIGIT)* FRAC ) =>  REAL { $setType(REAL); }
    | ( "..") => DOTDOT { $setType(DOTDOT); }
    | INT { $setType(INT); }
    | DOT { $setType(DOT); }
    ;
NEWLINE
    : ("\r\n" // DOS
       | '\r'   // MAC
       | '\n'   // Unix
      )
        {
            newline();
            $setType(Token.SKIP);
        }
    ;
SINGLE_LINE_COMMENT
    : "//" (~('\r'|'\n'))* ("\r\n"|'\r'|'\n')?
        {
            newline();
            $setType(Token.SKIP);
        }
    ;
MULTI_LINE_COMMENT
    : "/*" (options {greedy=false;} :.)* "*/"
        { $setType(Token.SKIP); }
    ;
protected ABSOLUTE
    : '$'
    ;
protected ID
    : ID_START_LETTER (ID_LETTER)*
    ;
protected INT
    : ('-')? (DIGIT)+
    ;
protected REAL
    : ('-')? (DIGIT)* FRAC (EXP)?
    ;
protected DOT
    : '.'
    ;
protected DOTDOT
    : ".."
    ;
protected WS
    : ' '
    ;
protected DIGIT
    : '0'..'9'
    ;
protected FRAC
    : '.' (DIGIT)+
    ;
protected EXP
    : ('e'|'E') ('+'|'-')? (DIGIT)+
    ;
protected ESC
    : '\\' .
        {
            String ruleText = $getText;
            $setText(ruleText);
        }
    ;
protected ID_START_LETTER
    : 'a'..'z'
    | 'A'..'Z'
    | '$'
    | '_'
    ;
protected ID_LETTER
    : ID_START_LETTER
    | DIGIT
    ;
protected AT_IS_NULL
    : "@isNull"
    ;
protected AT_IS_NOT_NULL
    : "@isNotNull"
    ;
protected AT_IS_ARRAY
    : "@isArray"
    ;
protected AT_IS_OBJECT
    : "@isObject"
    ;
protected AT_IS_VALUE
    : "@isValue"
    ;