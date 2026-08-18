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
package net.sf.jasperreports.functions.standard;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.functions.AbstractFunctionSupport;
import net.sf.jasperreports.functions.annotations.Function;
import net.sf.jasperreports.functions.annotations.FunctionCategories;
import net.sf.jasperreports.functions.annotations.FunctionParameter;
import net.sf.jasperreports.functions.annotations.FunctionParameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class should maintain all function methods that belongs to the category {@link #TEXT}.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 */
@FunctionCategories({TextCategory.class})
public final class TextFunctions  extends AbstractFunctionSupport
{
	private static final Log log = LogFactory.getLog(TextFunctions.class);	
	private static final int BASE_MIN_RADIX=2;
	private static final int BASE_MAX_RADIX=36;

	// ===================== BASE function ===================== //
	/**
	 * Returns a text representation of a number, in a specified base radix.
	 */
	@Function("BASE")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("radix"),
		@FunctionParameter("minLength")})
	public static String BASE(Integer number, Integer radix){
		// java.lang.Character.MIN_RADIX and java.lang.Character.MAX_RADIX are already 2 and 36 respectively
		// However we should check the parameter specified because the method we rely on uses 10 radix
		// as fallback when a smaller/greater radix is specified.
		if(number==null){
			if(log.isDebugEnabled()){
				log.debug("The number can not be null.");
			}
			return null;
		}
		if(radix==null || radix>BASE_MAX_RADIX || radix<BASE_MIN_RADIX) {
			if(log.isDebugEnabled()) {
				log.debug("The radix parameter must be an integer number between 2 and 36.");
			}
			return null;
		}
		return Integer.toString(number, radix);
	}

	public static String BASE(Integer number, Integer radix, Integer minlength){
		String base = BASE(number, radix);
		if(base!=null) {
			if(minlength == null){
				if(log.isDebugEnabled()) {
					log.debug("The minimum length can not be null.");
				}
				return null;
			}
			String spacePaddedStr = String.format("%"+minlength+"s",base);
			return spacePaddedStr.replace(' ', '0');
		}
		else {
			return null;
		}
	}

	// ===================== CHAR function ===================== //
	/**
	 * Returns a single text character, given a character code.
	 */
	@Function("CHAR")
	@FunctionParameters({
			@FunctionParameter("number")})
	public static String CHAR(Integer number){
		if(number==null || (number <1 || number>255)){
			if(log.isDebugEnabled()) {
				log.debug("The number must be an integer number between 1 and 255.");
			}
			return null;
		}
		return Character.toString((char)number.intValue());
	}
	
	// ===================== CLEAN function ===================== //
	/**
	 * Returns a new text string without non-printable characters.
	 */
	@Function("CLEAN")
	@FunctionParameters({
			@FunctionParameter("text")})
	public static String CLEAN(String text){
		if(text==null){
			logNullTextString();
			return null;
		}
		String cleanedString = text.replaceAll("\\p{Cntrl}", "");
		return cleanedString;
	}

	// ===================== CODE function ===================== //
	/**
	 * Returns the numeric code (0-255) for the first character in a string.
	 */
	@Function("CODE")
	@FunctionParameters({
			@FunctionParameter("textString")})
	public static Integer CODE(String textString){
		if(textString==null){
			logNullTextString();
			return null;
		}
		int firstCharAsNum = textString.charAt(0);
		if(firstCharAsNum<0 || firstCharAsNum>255){
			throw new JRRuntimeException("The first character of the text can not be converted to a valid numeric ASCII code.");
		}
		return firstCharAsNum;
	}
	
	// ===================== CONCATENATE function ===================== //
	/**
	 * Combines a list of strings into a single one.
	 */
	@Function("CONCATENATE")
	@FunctionParameters({
			@FunctionParameter("strings")})
	public static String CONCATENATE(String ...strings){
		if(strings.length==0) {
			if(log.isDebugEnabled()) {
				log.debug("No arguments were specified.");
			}
			return null;
		}
		return String.join("", strings);
	}
	
	// ===================== EXACT function ===================== //
	/**
	 * Returns TRUE if the two text specified are exactly the same (case sensitive compare).
	 */
	@Function("EXACT")
	@FunctionParameters({
			@FunctionParameter("text1"),
			@FunctionParameter("text2")})
	public static Boolean EXACT(String text1, String text2){
		if(text1!=null){
			return text1.equals(text2);
		}
		else if(text2!=null){
			return text2.equals(text1);
		}
		else {
			if(log.isDebugEnabled()){
				log.debug("The texts to be compared are both null.");
			}
			return null;
		}
	}

	// ===================== DOUBLE_VALUE function ===================== //
	/**
	 * Returns a Double number representing the given text string.
	 */
	@Function("DOUBLE_VALUE")
	@FunctionParameters({
			@FunctionParameter("textNumber")})
	public static Double DOUBLE_VALUE(String textNumber){
		if(textNumber==null) {
			logNullTextString();
			return null;
		}
		return Double.parseDouble(textNumber);
	}	
	
	// ===================== FIND function ===================== //
	/**
	 * Returns the character position of a string inside another text. If the text is not found then -1 is returned.
	 */
	@Function("FIND")
	@FunctionParameters({
			@FunctionParameter("findText"),
			@FunctionParameter("searchText"),
			@FunctionParameter("startPosition")})
	public static Integer FIND(String findText, String searchText){
		return FIND(findText, searchText, 0);
	}
	
	public static Integer FIND(String findText, String searchText, Integer startPosition){
		if(findText==null || searchText==null || startPosition == null) {
			logHavingNullArguments();
			return null;
		}
		int position = findText.indexOf(searchText, startPosition);
		return position;
	}

	// ===================== FIXED function ===================== //
	/**
	 * Returns the text representing number with the specified decimal places.
	 */
	@Function("FIXED")
	@FunctionParameters({
			@FunctionParameter("number"),
			@FunctionParameter("decimals"),
			@FunctionParameter("omitSeparators")})
	public String FIXED(Number number, Integer decimals){
		return FIXED(number, decimals,false);
	}

	public String FIXED(Number number, Integer decimals, Boolean omitSeparators){
		if(number==null || decimals==null || omitSeparators==null) {
			logHavingNullArguments();
			return null;
		}
		// Pattern samples:
		// 123456.789	###,###.###	123,456.789
		// 123456.789	###.##	123456.79
		// 123.78	000000.000	000123.780
		StringBuilder patternBuf=new StringBuilder("###");
		if(!omitSeparators){
			patternBuf.append(",###");
		}
		patternBuf.append(".");
		for(int i=0;i<decimals;i++){
			patternBuf.append("0");
		}
		DecimalFormat myFormatter = getDecimalFormat();
		myFormatter.applyPattern(patternBuf.toString());
		return myFormatter.format(number);
	}
	
	// ===================== FLOAT_VALUE function ===================== //
	/**
	 * Returns a Float number representing the given text string.
	 */
	@Function("FLOAT_VALUE")
	@FunctionParameters({
			@FunctionParameter("textNumber")})
	public static Float FLOAT_VALUE(String textNumber){
		if(textNumber==null) {
			logNullTextString();
			return null;
		}
		return Float.parseFloat(textNumber);
	}
	
	// ===================== INTEGER_VALUE function ===================== //
	/**
	 * Returns an Integer number representing the given text string.
	 */
	@Function("INTEGER_VALUE")
	@FunctionParameters({
		@FunctionParameter("textNumber")})
	public static Integer INTEGER_VALUE(String textNumber){
		if(textNumber==null) {
			logNullTextString();
			return null;
		}
		return Integer.parseInt(textNumber);
	}

	
	// ===================== LEFT function ===================== //
	/**
	 * Returns the specified number of characters (1 by default) from the left side of the input text.
	 */
	@Function("LEFT")
	@FunctionParameters({
			@FunctionParameter("text"),
			@FunctionParameter("charactersNum")})
	public static String LEFT(String text){
		return LEFT(text,1);
	}
	
	public static String LEFT(String text, Integer charactersNum){
		if(text==null || charactersNum==null) {
			logHavingNullArguments();
			return null;
		}
		return text.substring(0,charactersNum);
	}
	
	// ===================== LEN function ===================== //
	/**
	 * Returns the length of the specified text string.
	 */
	@Function("LEN")
	@FunctionParameters({
			@FunctionParameter("text")})
	public static Integer LEN(String text){
		if(text==null) {
			logNullTextString();
			return null;
		}
		return text.length();
	}

	// ===================== LONG_VALUE function ===================== //
	/**
	 * Returns a Long number representing the given text string.
	 */
	@Function("LONG_VALUE")
	@FunctionParameters({
		@FunctionParameter("textNumber")})
	public static Long LONG_VALUE(String textNumber){
		if(textNumber==null) {
			logNullTextString();
			return null;
		}
		return Long.parseLong(textNumber);
	}

	// ===================== LOWER function ===================== //
	/**
	 * Performs the lower case conversion of the specified text string.
	 */
	@Function("LOWER")
	@FunctionParameters({
		@FunctionParameter("text")})
	public static String LOWER(String text){
		if(text==null) {
			logNullTextString();
			return null;
		}
		return text.toLowerCase();
	}

	// ===================== LTRIM function ===================== //
	/**
	 * Clear a string, removing leading whitespaces.
	 */
	@Function("LTRIM")
	@FunctionParameters({
		@FunctionParameter("text")})
	public static String LTRIM(String text){
		if(text==null) {
			logNullTextString();
			return null;
		}
		return text.replaceAll("^\\s+", "");
	}
		
	// ===================== MID function ===================== //
	/**
	 * Returns the text from the middle of a text string.
	 */
	@Function("MID")
	@FunctionParameters({
			@FunctionParameter("text"),
			@FunctionParameter("startPosition"),
			@FunctionParameter("charactersNum")})
	public static String MID(String text, Integer startPosition) {
		if(text == null || startPosition == null) {
			logHavingNullArguments();
			return null;
		}
		else {
			return text.substring(startPosition-1, text.length()-startPosition+1);
		}
	}

	public static String MID(String text, Integer startPosition, Integer charactersNum){
		if(text==null || startPosition==null || charactersNum==null) {
			logHavingNullArguments();
			return null;
		}
		else {
			return text.substring(startPosition-1,startPosition-1+charactersNum);
		}
	}
	
	// ===================== PROPER function ===================== //
	/**
	 * Capitalizes each words of the specified text. The remaining parts of words are in lowercase.
	 */
	@Function("PROPER")
	@FunctionParameters({
		@FunctionParameter("text")})
	public static String PROPER(String text){
		if(text==null) {
			logNullTextString();
			return null;
		}
		else {
			String lowerCaseString=LOWER(text);
			StringBuilder result=new StringBuilder();
			result.append(Character.toTitleCase(lowerCaseString.charAt(0)));
			boolean capitalizeNext=false;
			
			for (int i=1; i<lowerCaseString.length(); i++){
				char c = lowerCaseString.charAt(i);
				if(!isDelimiter(c)){
					if(capitalizeNext){
						result.append(Character.toTitleCase(c));
					}
					else{
						result.append(c);
					}
					capitalizeNext=false;
				}
				else{
					result.append(c);
					capitalizeNext=true;
				}
			}
			
			return result.toString();
		}
	}
	
	// ===================== REPLACE function ===================== //
	/**
	 * Replaces parts of a text string with a different one. Starting from a specified position, removes a certain number of characters and then insert the new text.
	 */
	@Function("REPLACE")
	@FunctionParameters({
			@FunctionParameter("originalText"),
			@FunctionParameter("startPosition"),
			@FunctionParameter("charsNum"),
			@FunctionParameter("newText")})
	public static String REPLACE(String originalText, Integer startPosition, Integer charsNum, String newText){
		if(originalText==null || startPosition==null || charsNum==null || newText==null) {
			logHavingNullArguments();
			return null;		
		}
		else {
			StringBuilder output=new StringBuilder();
			output.append(originalText.substring(0, startPosition-1));
			output.append(newText);
			output.append(originalText.substring(startPosition+charsNum-1, originalText.length()));
			return output.toString();
		}
	}
	
	// ===================== REPT function ===================== //
	/**
	 * Replicates an input text string for a specified number of times.
	 */
	@Function("REPT")
	@FunctionParameters({
			@FunctionParameter("originalText"),
			@FunctionParameter("numberOfTimes")})
	public static String REPT(String originalText, Integer numberOfTimes){
		if(originalText==null || numberOfTimes==null) {
			logHavingNullArguments();
			return null;
		}
		else{
			StringBuilder output=new StringBuilder();
			for(int i=0;i<numberOfTimes;i++){
				output.append(originalText);
			}
			return output.toString();
		}
	}
	
	// ===================== RIGHT function ===================== //
	/**
	 * Returns the specified number of characters (1 by default) from the right side of the input text.
	 */
	@Function("RIGHT")
	@FunctionParameters({
			@FunctionParameter("text"),
			@FunctionParameter("charactersNum")})
	public static String RIGHT(String text){
		return RIGHT(text,1);
	}
	
	public static String RIGHT(String text, Integer charactersNum){
		if(text==null || charactersNum==null) {
			logHavingNullArguments();
			return null;
		}
		else{
			int length = text.length();
			return text.substring(length-charactersNum,length);
		}
	}
	
	// ===================== RTRIM function ===================== //
	/**
	 * Clear a string, removing trailing whitespaces.
	 */
	@Function("RTRIM")
	@FunctionParameters({
			@FunctionParameter("text")})
	public static String RTRIM(String text){
		if(text==null) {
			logNullTextString();
			return null;
		}
		else{
			return text.replaceAll("\\s+$", "");
		}
	}	
	
	// ===================== SEARCH function ===================== //
	/**
	 * Returns the position of a string of text in another string. Search is not case-sensitive.
	 */
	@Function("SEARCH")
	@FunctionParameters({
			@FunctionParameter("findText"),
			@FunctionParameter("textToSearch"),
			@FunctionParameter("startPosition")})
	public static Integer SEARCH(String findText, String textToSearch){
		return SEARCH(findText, textToSearch,1);
	}	
	
	public static Integer SEARCH(String findText, String textToSearch, Integer startPosition){
		if(findText==null || textToSearch==null || startPosition==null) {
			logHavingNullArguments();
			return null;
		}
		else {
			return textToSearch.toLowerCase().indexOf(findText.toLowerCase(), startPosition-1);			
		}
	}
	
	// ===================== SUBSTITUTE function ===================== //
	/**
	 * Substitutes new text for old text in a text string. When no occurrence is specified all occurrences are replaced.
	 */
	@Function("SUBSTITUTE")
	@FunctionParameters({
			@FunctionParameter("originalText"),
			@FunctionParameter("oldText"),
			@FunctionParameter("newText"),
			@FunctionParameter("occurrenceNum")})
	public static String SUBSTITUTE(String originalText, String oldText, String newText){
		return SUBSTITUTE(originalText, oldText, newText, null);
	}	
	
	public static String SUBSTITUTE(String originalText, String oldText, String newText, Integer occurrenceNum){
		if(originalText==null || oldText==null || newText==null) {
			logHavingNullArguments();
			return null;
		}
		else if(occurrenceNum==null){
			// Replace all occurrences
			return originalText.replaceAll(Pattern.quote(oldText), Matcher.quoteReplacement(newText));
		}
		else{
			int startIdx=0;
			int counter=1;

			// Find the right occurrence
			while (startIdx<originalText.length()-1){
				// Locate the next index position of the occurrence of the old text
				int foundPosition = originalText.indexOf(oldText,startIdx);
				if(counter==occurrenceNum){
					return REPLACE(originalText,foundPosition+1,oldText.length(),newText);
				}
				else{
					startIdx=foundPosition+oldText.length();
					counter++;
				}
			}
			// Fall-back
			return null;					
		}
	}
	
	// ===================== T function ===================== //
	/**
	 * Returns the text string if the value is a string, otherwise an empty string is returned.
	 */
	@Function("T")
	@FunctionParameters({
			@FunctionParameter("value")})
	public static String T(Object value){
		if(value instanceof String) {
			return (String)value;
		}
		else{
			return "";
		}
	}	
	
	// ===================== TEXT function ===================== //
	/**
	 * Converts a number into a text string according to a specified format.
	 */
	@Function("TEXT")
	@FunctionParameters({
			@FunctionParameter("number"),
			@FunctionParameter("numberFormat")})
	public String TEXT(Number number, String numberFormat){
		if(number==null || numberFormat==null) {
			logNullTextString();
			return null;
		}
		else {
			DecimalFormat nformat = getDecimalFormat();
			nformat.applyPattern(numberFormat);
			return nformat.format(number);			
		}
	}	
	
	// ===================== TRIM function ===================== //
	/**
	 * Clear a string,removing leading and trailing whitespaces.
	 */
	@Function("TRIM")
	@FunctionParameters({
			@FunctionParameter("text")})
	public static String TRIM(String text){
		if(text==null) {
			logNullTextString();
			return null;
		}
		else {
			return text.trim();
		}
	}	
		
	// ===================== UPPER function ===================== //
	/**
	 * Performs the upper case conversion of the specified text string.
	 */
	@Function("UPPER")
	@FunctionParameters({
		@FunctionParameter("text")})
	public static String UPPER(String text){
		if(text==null) {
			logNullTextString();
			return null;
		}
		else {
			return text.toUpperCase();
		}
	}
	
	// Internal private methods
	private static boolean isDelimiter(char c){
		return Character.isWhitespace(c) || Character.isSpaceChar(c);
	}
	
	private static void logNullTextString() {
		if(log.isDebugEnabled()) {
			log.debug("The text string can not be null.");
		}
	}
	
	private static void logHavingNullArguments() {
		if(log.isDebugEnabled()){
			log.debug("None of the arguments can be null.");
		}
	}
	
	private DecimalFormat getDecimalFormat() {
		return (DecimalFormat) NumberFormat.getNumberInstance(getReportLocale());
	}
	
	/*
	 * Tries to retrieve the {@link Locale} to be used in the report, 
	 * using the parameter {@link JRParameter#REPORT_LOCALE}. 
	 * If not available it will default the {@link Locale#getDefault()} value.
	 * 
	 * @return the {@link Locale} instance to be used
	 */
	private Locale getReportLocale() {
		Locale reportLocale = Locale.getDefault(); 
		if(getContext()!=null) {
			reportLocale = (Locale) getContext().getParameterValue(JRParameter.REPORT_LOCALE);
		}
		return reportLocale;
	}
}
