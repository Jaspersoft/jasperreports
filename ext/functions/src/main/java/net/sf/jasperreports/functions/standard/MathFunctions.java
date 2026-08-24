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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.functions.annotations.Function;
import net.sf.jasperreports.functions.annotations.FunctionCategories;
import net.sf.jasperreports.functions.annotations.FunctionParameter;
import net.sf.jasperreports.functions.annotations.FunctionParameters;


/**
 * This class should maintain all function methods that belongs to the Math category.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 */
@FunctionCategories({MathCategory.class})
public final class MathFunctions 
{
	private static final Log log = LogFactory.getLog(MathFunctions.class);
	
	// ===================== ABS function ===================== //
	/**
	 * Returns the absolute value of a number.
	 */
	@Function("ABS")
	@FunctionParameters({
			@FunctionParameter("number")})
	public static Number ABS(Number number){
		if (number==null) {
			logNullArgument();
			return null;
		}
		else{
			if (number instanceof Integer){
				return Math.abs((Integer)number);
			}
			else if (number instanceof Double){
				return Math.abs((Double)number);
			}
			else if (number instanceof Float){
				return Math.abs((Float)number);
			}
			else if (number instanceof Long){
				return Math.abs((Long)number);
			}
			else{
				// fall-back
				return Math.abs(number.doubleValue());
			}
		}
	}
	
	// ===================== FACT function ===================== //
	/**
	 * Returns the factorial of a number.
	 */
	@Function("FACT")
	@FunctionParameters({
			@FunctionParameter("number")})
	public static Long FACT(Integer number){
		if (number==null) {
			logNullArgument();
			return null;
		}
		if (number<0){
			if (log.isDebugEnabled()) {
				log.debug("Unable to calculate the factorial number of a negative number.");
			}
			return null;
		}
		else{
			Long result = 1l;
			for (int i = 1; i <= number; ++i) {
				result *= i;
			}
			return result;
		}
	}
	
	// ===================== ISEVEN function ===================== //
	/**
	 * Checks if a number is even. If a non-integer number is specified, any digits after the decimal point are ignored.
	 */
	@Function("ISEVEN")
	@FunctionParameters({
		@FunctionParameter("number")})
	public static Boolean ISEVEN(Number number){
		if (number==null) {
			logNullArgument();
			return null;
		}
		else{
			return number.intValue()%2==0;			
		}
	}

	// ===================== ISODD function ===================== //
	/**
	 * Checks if a number is odd. If a non-integer number is specified, any digits after the decimal point are ignored.
	 */
	@Function("ISODD")
	@FunctionParameters({
		@FunctionParameter("number")})
	public static Boolean ISODD(Number number){
		if (number==null) {
			logNullArgument();
			return null;
		}
		else{
			return number.intValue()%2==1;			
		}
	}
	
	// ===================== PRODUCT function ===================== //
	/**
	 * Returns the product of a list of numbers.
	 */
	@Function("PRODUCT")
	@FunctionParameters({
			@FunctionParameter("numbers")})
	public static Number PRODUCT(Number ...numbers){
		if (numbers.length==0) {
			logEmptyArgumentsList();
			return null;
		}
		if (!isNumberListValid(numbers)) {
			logArgumentsWithNullElements();
			return null;
		}
		double result=1;
		for (Number number : numbers){
			result*=number.doubleValue();
		}
		return result;
	}

	// ===================== RAND function ===================== //
	/**
	 * Returns a random number between 0.0 and 1.0.
	 */
	@Function("RAND")
	public static Double RAND(){
		return Math.random();
	}
	
	// ===================== RAND function ===================== //
	/**
	 * Returns an Integer random number between bottom and top range (both inclusive).
	 */
	@Function("RANDBETWEEN")
	@FunctionParameters({
		@FunctionParameter("bottomRange"),
		@FunctionParameter("topRange")})
	public static Integer RANDBETWEEN(Integer bottomRange, Integer topRange){
		int min=bottomRange;
		int max=topRange;
		return min + (int)(Math.random() * ((max - min) + 1));
	}	
	
	// ===================== SIGN function ===================== //
	/**
	 * Returns the sign of a number.
	 */
	@Function("SIGN")
	@FunctionParameters({
		@FunctionParameter("number")})
	public static Integer SIGN(Number number){
		if (number==null) {
			logNullArgument();
			return null;
		}
		else{
			return (int) Math.signum(number.doubleValue());			
		}
	}
	
	// ===================== SQRT function ===================== //
	/**
	 * Returns the positive square root of a number. The number must be positive.
	 */
	@Function("SQRT")
	@FunctionParameters({
			@FunctionParameter("number")})
	public static Number SQRT(Number number){
		if (number==null) {
			logNullArgument();
			return null;
		}
		else{
			return Math.sqrt(number.doubleValue());			
		}
	}
	
	// ===================== SUM function ===================== //
	/**
	 * Returns the sum of a list of numbers.
	 */
	@Function("SUM")
	@FunctionParameters({
			@FunctionParameter("numbers")})
	public static Number SUM(Number ...numbers){
		if (numbers.length==0) {
			logEmptyArgumentsList();
			return null;
		}
		if (!isNumberListValid(numbers)) {
			logArgumentsWithNullElements();
			return null;
		}
		double result=0;
		for (Number number : numbers){
			result+=number.doubleValue();
		}
		return result;
	}
	
	// ===================== MIN function ===================== //
	/**
	 * Returns the minimum of a list of numeric values.
	 */
	@Function("MIN")
	@FunctionParameters({
		@FunctionParameter("numbers")})
	public static Number MIN(Number ...numbers){
		if (numbers.length==0) {
			logEmptyArgumentsList();
			return null;
		}
		if (!isNumberListValid(numbers)) {
			logArgumentsWithNullElements();
			return null;
		}
		double min= numbers[0].doubleValue();
		for (int i=1;i<numbers.length;i++){
			if (numbers[i].doubleValue()<min){
				min = numbers[i].doubleValue();
			}
		}
		return fixNumberReturnType(min, numbers);
	}	
	
	// ===================== MAX function ===================== //
	/**
	 * Returns the maximum of a list of numeric values.
	 */
	@Function("MAX")
	@FunctionParameters({
		@FunctionParameter("numbers")})
	public static Number MAX(Number ...numbers){
		if (numbers.length==0) {
			logEmptyArgumentsList();
			return null;
		}
		if (!isNumberListValid(numbers)) {
			logArgumentsWithNullElements();
			return null;
		}
		double max= numbers[0].doubleValue();
		for (int i=1;i<numbers.length;i++){
			if (numbers[i].doubleValue()>max){
				max = numbers[i].doubleValue();
			}
		}
		return fixNumberReturnType(max, numbers);
	}
	
	// ===================== FLOOR function ===================== //
	/**
	 * Returns the largest (closest to positive infinity) double value that is less than or equal to the argument and is equal to a mathematical integer.
	 */
	@Function("FLOOR")
	@FunctionParameters({
		@FunctionParameter("number")})
	public static Double FLOOR(Number number){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return Math.floor(number.doubleValue());
	}

	// ===================== CEIL function ===================== //
	/**
	 * Returns the smallest (closest to negative infinity) double value that is greater than or equal to the argument and is equal to a mathematical integer
	 */
	@Function("CEIL")
	@FunctionParameters({
		@FunctionParameter("number")})
	public static Double CEIL(Number number){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return Math.ceil(number.doubleValue());
	}
	
	// ===================== ROUND_UP function ===================== //
	/**
	 * Returns a BigDecimal number rounded away from zero. It always increments the digit prior to a non-zero discarded fraction.
	 */
	@Function("ROUND_UP")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("scale")})
	public static BigDecimal ROUND_UP(Number number, int scale){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return new BigDecimal(number.toString()).setScale(scale, RoundingMode.UP);
		
	}
	
	// ===================== ROUND_DOWN function ===================== //
	/**
	 * Returns a BigDecimal number rounded towards zero. It never increments the digit prior to a discarded fraction
	 */
	@Function("ROUND_DOWN")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("scale")})
	public static BigDecimal ROUND_DOWN(Number number, int scale){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return new BigDecimal(number.toString()).setScale(scale, RoundingMode.DOWN);
	}
	
	// ===================== ROUND_CEILING function ===================== //
	/**
	 * Returns a BigDecimal number rounded towards positive infinity. For positive values behaves as the {@link #ROUND_UP} function, 
	 * for negative values behaves as the {@link #ROUND_DOWN} function.
	 */
	@Function("ROUND_CEILING")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("scale")})
	public static BigDecimal ROUND_CEILING(Number number, int scale){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return new BigDecimal(number.toString()).setScale(scale, RoundingMode.CEILING);
	}
	
	// ===================== ROUND_FLOOR function ===================== //
	/**
	 * Returns a BigDecimal number rounded towards negative infinity. For positive values behaves as the {@link #ROUND_DOWN} function, 
	 * for negative values behaves as the {@link #ROUND_UP} function.
	 */
	@Function("ROUND_FLOOR")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("scale")})
	public static BigDecimal ROUND_FLOOR(Number number, int scale){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return new BigDecimal(number.toString()).setScale(scale, RoundingMode.FLOOR);
	}
	
	// ===================== ROUND_HALF_UP function ===================== //
	/**
	 * Returns a BigDecimal number rounded towards its nearest neighbor. If both neighbors are equidistant, the number is rounded up. 
	 * Behaves as the {@link #ROUND_UP} function if the discarded fraction is ≥ 0.5; otherwise, behaves as the {@link #ROUND_DOWN} function.
	 */
	@Function("ROUND_HALF_UP")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("scale")})
	public static BigDecimal ROUND_HALF_UP(Number number, int scale){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return new BigDecimal(number.toString()).setScale(scale, RoundingMode.HALF_UP);
	}
	
	// ===================== ROUND_HALF_EVEN function ===================== //
	/**
	 * Returns a BigDecimal number rounded towards its nearest neighbor. If both neighbors are equidistant, the number is rounded towards the even neighbor. 
	 * Behaves as the {@link #ROUND_HALF_UP} function if the digit to the left of the discarded fraction is odd; otherwise, behaves as the {@link #ROUND_HALF_DOWN} function. 
	 * This rounding mode statistically minimizes cumulative error when it is repeatedly applied over a sequence of calculations.
	 */
	@Function("ROUND_HALF_EVEN")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("scale")})
	public static BigDecimal ROUND_HALF_EVEN(Number number, int scale){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return new BigDecimal(number.toString()).setScale(scale, RoundingMode.HALF_EVEN);
	}
	
	// ===================== ROUND_HALF_DOWN function ===================== //
	/**
	 * Returns a BigDecimal number rounded towards its nearest neighbor. If both neighbors are equidistant, the number is rounded down. 
	 * Behaves as the {@link #ROUND_UP} function if the discarded fraction is > 0.5; otherwise, behaves as the {@link #ROUND_DOWN} function.
	 */
	@Function("ROUND_HALF_DOWN")
	@FunctionParameters({
		@FunctionParameter("number"),
		@FunctionParameter("scale")})
	public static java.math.BigDecimal ROUND_HALF_DOWN(Number number, int scale){
		if (number == null) {
			logNullArgument();
			return null;
		}
		return new BigDecimal(number.toString()).setScale(scale, RoundingMode.HALF_DOWN);
	}
	
	/*
	 * Checks if the array of numbers is valid. 
	 * No null element must be contained.
	 */
	private static boolean isNumberListValid(Number ...numbers){
		for (Number number : numbers){
			if (number==null) return false;
		}
		return true;
	}
	
	/*
	 * Fixes the return type for the numeric result value.
	 */
	private static Number fixNumberReturnType(Number returnValue, Number ...numbers){
		if (haveSameType(Integer.class, numbers)) return returnValue.intValue();
		if (haveSameType(Long.class, numbers)) return returnValue.longValue();
		if (haveSameType(Float.class, numbers)) return returnValue.floatValue();
		return returnValue.doubleValue();
	}
	
	/*
	 * Checks if the list of generic numbers have all the same type.
	 */
	private static boolean haveSameType(
			Class<? extends Number> clazz, Number ...numbers){
		for (Number number : numbers){
			if (number.getClass() != clazz){
				return false;
			}
		}
		return true;
	}

	/* Utility methods for logging common messages */
	
	private static void logNullArgument() {
		if (log.isDebugEnabled()) {
			log.debug("The argument can not be null.");
		}
	}

	private static void logEmptyArgumentsList() {
		if (log.isDebugEnabled()) {
			log.debug("No arguments were specified.");
		}
	}
	
	private static void logArgumentsWithNullElements() {
		if (log.isDebugEnabled()) {
			log.debug("No null element is allowed among arguments");
		}
	}
	
}
