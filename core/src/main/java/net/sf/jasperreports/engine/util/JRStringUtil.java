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

/*
 * Contributors:
 * Gaganis Giorgos - gaganis@users.sourceforge.net
 */
package net.sf.jasperreports.engine.util;

import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.io.JsonStringEncoder;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRStringUtil
{
	protected static final String JAVA_IDENTIFIER_PREFIX = "j";
	
	protected static final Pattern PATTERN_CSS_INVALID_CHARACTER = Pattern.compile("[^a-zA-Z0-9_-]+");
	protected static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'a', 'b', 'c', 'd', 'e', 'f'};

	public static final String EXCEPTION_MESSAGE_KEY_NUMBER_OUTSIDE_BOUNDS = "util.markup.processor.number.outside.bounds";

	protected static final String[] THOUSAND_DIGITS = {"","M","MM","MMM"};
	protected static final String[] HUNDRED_DIGITS = {"","C","CC","CCC","CD","D","DC","DCC","DCCC","CM"};
	protected static final String[] TEN_DIGITS = {"","X","XX","XXX","XL","L","LX","LXX","LXXX","XC"};
	protected static final String[] UNIT_DIGITS = {"","I","II","III","IV","V","VI","VII","VIII","IX"};

	/**
	 * This method replaces all occurrences of the CR character with the LF character, 
	 * except when the CR is immediately followed by a LF (CRLF sequences), in which case the CR is removed.
	 */
	public static String replaceCRwithLF(String text)
	{
		if (text != null)
		{
			int length = text.length();
			char[] chars = text.toCharArray();
			int r = 0;
			boolean dirty = false;
			for (int i = 0; i < length; ++i)
			{
				char ch = chars[i];
				if (ch == '\r')
				{
					dirty = true;
					if (i + 1 < length && chars[i + 1] == '\n')
					{
						r++;
					}
					else
					{
						chars[i - r] = '\n';
					}
				}
				else
				{
					chars[i - r] = ch;
				}
			}

			return dirty ? new String(chars, 0, length - r) : text;
		}
		return null;
	}


	/**
	 *
	 */
	public static String xmlEncode(String text)
	{
		return xmlEncode(text, null);
	}


	/**
	 *
	 */
	public static String xmlEncode(String text, String invalidCharReplacement)
	{
		if (text == null || text.length() == 0)
		{
			return text;
		}
		
		int length = text.length();
		StringBuilder ret = new StringBuilder(length * 12 / 10);
		int last = 0;
		
		for (int i = 0; i < length; i++)
		{
			char c = text.charAt(i);
			if (Character.isISOControl(c) && c!='\t' && c!='\r' && c!='\n')
			{
				last = appendText(text, ret, i, last);
				if (invalidCharReplacement == null)
				{
					//the invalid character is preserved
					ret.append(c);
				}
				else if (invalidCharReplacement.length() == 0)
				{
					//the invalid character is removed
					continue;
				}
				else
				{
					//the invalid character is replaced
					ret.append(invalidCharReplacement);
				}
			}
			else
			{
				switch (c)
				{
					case '&' :
						last = appendText(text, ret, i, last);
						ret.append("&amp;");
						break;
					case '>' :
						last = appendText(text, ret, i, last);
						ret.append("&gt;");
						break;
					case '<' :
						last = appendText(text, ret, i, last);
						ret.append("&lt;");
						break;
					case '\"' :
						last = appendText(text, ret, i, last);
						ret.append("&quot;");
						break;
					case '\'' :
						last = appendText(text, ret, i, last);
						ret.append("&apos;");
						break;
					default :
						break;
				}
			}
		}
		appendText(text, ret, length, last);
		return ret.toString();
	}
	
	public static String encodeXmlAttribute(String text)
	{
		return encodeXmlAttribute(text, false);
	}
	
	public static String encodeXmlAttribute(String text, boolean exceptApos)
	{
		if (text == null || text.length() == 0)
		{
			return text;
		}
		
		int length = text.length();
		StringBuilder ret = new StringBuilder(length * 12 / 10);//FIXME avoid creating this when not necessary
		int last = 0;
		for (int i = 0; i < length; i++)
		{
			char c = text.charAt(i);
			switch (c)
			{
				case '&' :
					last = appendText(text, ret, i, last);
					ret.append("&amp;");
					break;
				case '>' :
					last = appendText(text, ret, i, last);
					ret.append("&gt;");
					break;
				case '<' :
					last = appendText(text, ret, i, last);
					ret.append("&lt;");
					break;
				case '\"' :
					last = appendText(text, ret, i, last);
					ret.append("&quot;");
					break;
				case '\'' :
					if (!exceptApos)
					{
						last = appendText(text, ret, i, last);
						ret.append("&apos;");
					}
					break;
				// encoding tabs and newlines because otherwise they get replaced by spaces on parsing
				case '\t' :
					last = appendText(text, ret, i, last);
					ret.append("&#x9;");
					break;
				case '\r' :
					last = appendText(text, ret, i, last);
					ret.append("&#xD;");
					break;
				case '\n' :
					last = appendText(text, ret, i, last);
					ret.append("&#xA;");
					break;
				default :
					// not checking for invalid characters, preserving them as per xmlEncode() with invalidCharReplacement = null
					break;
			}
		}
		
		if (last == 0)
		{
			// no changes made to the string
			return text;
		}
		
		appendText(text, ret, length, last);
		return ret.toString();
	}
	
	private static int appendText(String text, StringBuilder ret, int current, int old)
	{
		if (old < current)
		{
			ret.append(text.substring(old, current));
		}
		return current+1;
	}
	
	/**
	 *
	 */
	public static String htmlEncode(String text)
	{
		if (text == null || text.length() == 0)
		{
			return text;
		}
		
		int length = text.length();
		StringBuilder ret = new StringBuilder(length * 12 / 10);

		boolean isEncodeSpace = true;
		int last = 0;
		for (int i = 0; i < length; i ++)
		{
			char c = text.charAt(i);
			switch (c)
			{
				case ' ' : 
					if (isEncodeSpace)
					{
						if (last < i)
						{
							ret.append(text.substring(last, i));
						}
						last = i + 1;
						
						ret.append("&nbsp;");
						isEncodeSpace = false;
					}
					else
					{
						isEncodeSpace = true;
					}
					break;
				case '&' :
					if (last < i)
					{
						ret.append(text.substring(last, i));
					}
					last = i + 1;
					
					ret.append("&amp;");
					isEncodeSpace = false;
					break;
				case '>' :
					if (last < i)
					{
						ret.append(text.substring(last, i));
					}
					last = i + 1;
					
					ret.append("&gt;");
					isEncodeSpace = false;
					break;
				case '<' :
					if (last < i)
					{
						ret.append(text.substring(last, i));
					}
					last = i + 1;
					
					ret.append("&lt;");
					isEncodeSpace = false;
					break;
				case '\"' :
					if (last < i)
					{
						ret.append(text.substring(last, i));
					}
					last = i + 1;
					
					ret.append("&quot;");
					isEncodeSpace = false;
					break;
// it does not work in IE
//					case '\'' :
//						if (last < i)
//						{
//							ret.append(text.substring(last, i));
//						}
//						last = i + 1;
//						
//						ret.append("&apos;");
//						isEncodeSpace = false;
//						break;
// newline characters do not need to be encoded in HTML attributes; the only place where it is used for HTML element content encoding, 
// the text is already split by paragraphs and does not contain newline characters
//				case '\n' :
//					if (last < i)
//					{
//						ret.append(text.substring(last, i));
//					}
//					last = i + 1;
//					
//					ret.append("<br/>");
//					isEncodeSpace = false;
//					break;

				default :
					isEncodeSpace = false;
				break;
			}
		}

		if (last < length)
		{
			ret.append(text.substring(last));
		}

		return ret.toString();
	}


	/**
	 * Takes a name and returns the same if it is a Java identifier;
	 * else it substitutes the illegal characters so that it can be an identifier
	 *
	 * @param name
	 */
	public static String getJavaIdentifier(String name)
	{
		if (isValidJavaIdentifier(name))
		{
			return name;
		}

		StringBuilder sb = new StringBuilder(name.length() + 5);
		
		char[] literalChars = new char[name.length()];
		name.getChars(0, literalChars.length, literalChars, 0);
		
		for (int i = 0; i < literalChars.length; i++)
		{
			if (i == 0 && !Character.isJavaIdentifierStart(literalChars[i]))
			{
				sb.append(JAVA_IDENTIFIER_PREFIX);
				sb.append((int)literalChars[i]);
			}
			else if (i != 0 && !Character.isJavaIdentifierPart(literalChars[i]))
			{
				sb.append((int)literalChars[i]);
			}
			else
			{
				sb.append(literalChars[i]);
			}
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Checks if the input is a valid Java identifier
	 * @param literal
	 * @author Gaganis Giorgos (gaganis@users.sourceforge.net) 
	 */
	private static boolean isValidJavaIdentifier(String literal)
	{
		boolean result = true;
		
		char[] literalChars = new char[literal.length()];
		literal.getChars(0, literalChars.length, literalChars, 0);
		
		for (int i = 0; i < literalChars.length; i++)
		{
			if (i == 0 && !Character.isJavaIdentifierStart(literalChars[i]))
			{
				result = false;
				break;
			}
			
			if (i != 0 && !Character.isJavaIdentifierPart(literalChars[i]))
			{
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Escapes a text so that it can be used as a Java String literal.
	 * 
	 * @param text the text
	 * @return the text with escaped quotes and backslashes
	 */
	public static String escapeJavaStringLiteral(String text)
	{
		if (text == null)
		{
			return text;
		}
		
		StringBuilder sb = new StringBuilder();
		StringTokenizer tkzer = new StringTokenizer(text, "\\\"\n\r\t", true);
		while (tkzer.hasMoreTokens())
		{
			String token = tkzer.nextToken();
			//TODO optimize ifs?
			if ("\\".equals(token))
			{
				sb.append("\\\\");
			}
			else if ("\"".equals(token))
			{
				sb.append("\\\"");
			}
			else if ("\n".equals(token))
			{
				sb.append("\\n");
			}
			else if ("\r".equals(token))
			{
				sb.append("\\r");
			}
			else if ("\t".equals(token))
			{
				sb.append("\\t");
			}
			else
			{
				sb.append(token);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Escapes a text so that it can be used as a Java String literal.
	 * 
	 * @param input
	 */
	public static String escapeJavaScript(String input) {
		if (input == null) {
			return input;
		}

		StringBuilder filtered = new StringBuilder(input.length());
		char prevChar = '\u0000';
		char c;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '"') {
				filtered.append("\\\"");
			}
			else if (c == '\'') {
				filtered.append("\\'");
			}
			else if (c == '\\') {
				filtered.append("\\\\");
			}
			else if (c == '/') {
				filtered.append("\\/");
			}
			else if (c == '\t') {
				filtered.append("\\t");
			}
			else if (c == '\n') {
				if (prevChar != '\r') {
					filtered.append("\\n");
				}
			}
			else if (c == '\r') {
				filtered.append("\\n");
			}
			else if (c == '\f') {
				filtered.append("\\f");
			}
			else {
				filtered.append(c);
			}
			prevChar = c;

		}
		return filtered.toString();
	}

	/**
	 * Escapes a Java String so that it can be used as a JavaScript String literal.
	 * 
	 * @param input
	 */
	public static String escapeString4JavaScript(String input) {
		if (input == null) {
			return input;
		}
		
		StringBuilder filtered = new StringBuilder(input.length());
		char prevChar = '\u0000';
		char c;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '"') {
				filtered.append("\\\"");
			}
			else if (c == '\\') {
				filtered.append("\\\\");
			}
			else if (c == '/') {
				filtered.append("\\/");
			}
			else if (c == '\t') {
				filtered.append("\\t");
			}
			else if (c == '\n') {
				if (prevChar != '\r') {
					filtered.append("\\n");
				}
			}
			else if (c == '\r') {
				filtered.append("\\n");
			}
			else if (c == '\f') {
				filtered.append("\\f");
			}
			else {
				filtered.append(c);
			}
			prevChar = c;
			
		}
		return filtered.toString();
	}
	
	
	/**
	 * 
	 */
	public static List<Integer> getTabIndexes(String text)
	{
		List<Integer> tabIndexes = null;
		
		if (text != null)
		{
			int index = text.indexOf('\t');
			// returning null if no tabs
			if (index >= 0)
			{
				tabIndexes = new ArrayList<>();
				do
				{
					tabIndexes.add(index);
					index = text.indexOf('\t', index + 1);
				}
				while (index >= 0);
			}
		}
		
		return tabIndexes;
	}
	

	/**
	 * 
	 */
	public static List<String> split(String[] srcArray, String delimiterRegExp)
	{
		List<String> tokens = null;
		if (srcArray != null)
		{
			tokens = new ArrayList<>();
			for (String src : srcArray)
			{
				if (src == null)
				{
					tokens.add(null);
				}
				else
				{
					String[] currentTokensArray = src.split(delimiterRegExp);
					for (String currentToken : currentTokensArray)
					{
						tokens.add(currentToken.trim());
					}
				}
			}
		}
		return tokens;
	}

	
	/**
	 * 
	 */
	public static String getString(Object value)
	{
		return value == null ? null : value.toString();
	}
	
	/**
	 * Escapes a text to be used for a JSON string value.
	 * 
	 * @param text the text to escape for JSON
	 * @return the escaped text if not null
	 */
	public static String escapeJSONString(String text)
	{
		if (text == null)
		{
			return null;
		}
		
		// using Jackson's string quote method
		char[] escapedChars = JsonStringEncoder.getInstance().quoteAsString(text);
		if (text.contentEquals(CharBuffer.wrap(escapedChars)))
		{
			// nothing changed
			return text;
		}
		
		return String.valueOf(escapedChars);
	}
	
	public static String getCSSClass(String name)
	{
		if (name == null || name.isEmpty())
		{
			return name;
		}
		
		Matcher matcher = PATTERN_CSS_INVALID_CHARACTER.matcher(name);
		StringBuffer sb = null;
		while (matcher.find()) {
			if (sb == null) {
				sb = new StringBuffer(name.length() + 4);
			}
			
			String text = matcher.group();
			String replacement = cssClassReplacement(text);
			matcher.appendReplacement(sb, replacement);
		}
		
		String cssClass;
		if (sb == null)
		{
			cssClass = name;
		}
		else
		{
			matcher.appendTail(sb);
			cssClass = sb.toString();
		}
		
		if (!Character.isLetter(cssClass.charAt(0)))
		{
			cssClass = 'c' + cssClass;
		}
		return cssClass;
	}
	
	protected static String cssClassReplacement(String text)
	{
		try
		{
			byte[] bytes = text.getBytes("UTF-8");
			char[] chars = new char[bytes.length * 2 + 1];
			chars[0] = '-';
			for (int i = 0; i < bytes.length; i++)
			{
				int code = bytes[i] & 0xff;
				chars[2 * i + 1] = HEX_DIGITS[code >>> 4];
				chars[2 * i + 2] = HEX_DIGITS[code & 0xf];
			}
			return new String(chars);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	public static String getLetterNumeral(int number, boolean isUpperCase)
	{
		String romanNumeral = "";
		int tmpNumber = number;
		while (tmpNumber > 0)
		{
			int modulo = (tmpNumber - 1) % 26;
			romanNumeral = (char)(modulo + 'A') + romanNumeral;
			tmpNumber = (tmpNumber - modulo) / 26;
		}
		return isUpperCase ? romanNumeral : romanNumeral.toLowerCase();
	}
	
	/**
	 * @param number an integer value between 1 and 3999
	 * @param isUpperCase specifies whether the result should be made of upper case characters
	 * @return the Roman numeral representation of this number
	 */
	public static String getRomanNumeral(int number, boolean isUpperCase)
	{
		if (number < 1 || number > 3999)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_NUMBER_OUTSIDE_BOUNDS,
					new Object[]{number});
		}
		String strNumber = ("0000"+String.valueOf(number)).substring(String.valueOf(number).length());
		String result = THOUSAND_DIGITS[strNumber.charAt(0) - '0'] 
				+ HUNDRED_DIGITS[strNumber.charAt(1) - '0']
				+ TEN_DIGITS[strNumber.charAt(2) - '0']
				+ UNIT_DIGITS[strNumber.charAt(3) - '0'];
		return isUpperCase ? result : result.toLowerCase();
	}

	private JRStringUtil()
	{
	}
}
