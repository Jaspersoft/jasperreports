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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.functions.annotations.Function;
import net.sf.jasperreports.functions.annotations.FunctionCategories;
import net.sf.jasperreports.functions.annotations.FunctionParameter;
import net.sf.jasperreports.functions.annotations.FunctionParameters;


/**
 * This class should maintain all functions that allows logic decisions, and belong to the Logical category.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 */
@FunctionCategories({LogicalCategory.class})
public final class LogicalFunctions 
{
	
	private static final Log log = LogFactory.getLog(LogicalFunctions.class);
	
	// ===================== AND function ===================== //
	/**
	 * Returns true if all arguments are considered true, false otherwise. Argument must be a logical result or a direct boolean value.
	 */
	@Function("AND")
	@FunctionParameters({
		@FunctionParameter("arguments")})
	public static Boolean AND(Boolean ... arguments){
		if (arguments.length==0) {
			if (log.isDebugEnabled()){
				log.debug("No arguments were specified.");
			}
			return null;
		}
		boolean result=true;
		for (Boolean arg : arguments){
			result=result && arg;
			// Stops at first false argument
			if (!result) return false;
		}
		return result;
	}

	// ===================== FALSE function ===================== //
	/**
	 * Returns the logical value FALSE.
	 */
	@Function("FALSE")
	public static Boolean FALSE(){
		return Boolean.FALSE;
	}
	
	// ===================== TRUE function ===================== //
	/**
	 * Returns the logical value TRUE.
	 */
	@Function("TRUE")
	public static Boolean TRUE(){
		return Boolean.TRUE;
	}
	
	// ===================== NOT function ===================== //
	/**
	 * Returns the negation of the specified boolean expression.
	 */
	@Function("NOT")
	@FunctionParameters({
		@FunctionParameter("boolValue")})
	public static Boolean NOT(Boolean boolValue){
		if (boolValue==null){
			if (log.isDebugEnabled()){
				log.debug("Argument can not be null.");
			}
			return null;
		}
		else{
			return !boolValue;			
		}
	}
	
	// ===================== OR function ===================== //
	/**
	 * Returns true if any of the arguments is considered true, false otherwise. Argument must be a logical result or a direct boolean value.
	 */
	@Function("OR")
	@FunctionParameters({
		@FunctionParameter("arguments")})
	public static Boolean OR(Boolean ... arguments){
		if (arguments.length==0) {
			if (log.isDebugEnabled()){
				log.debug("No arguments were specified.");
			}
			return null;
		}
		boolean result=false;
		for (Boolean arg : arguments){
			result=result || arg;
			// Stops at first true argument
			if (result) return true;
		}
		return result;
	}
	
	// ===================== IF function ===================== //
	/**
	 * Returns one of two values, depending on a test condition.
	 */
	@Function("IF")
	@FunctionParameters({
		@FunctionParameter("test"),
		@FunctionParameter("value1"),
		@FunctionParameter("value2")})
	public static Object IF(Boolean test, Object value1, Object value2){
		if (test==null) {
			if (log.isDebugEnabled()){
				log.debug("Test condition can not be null.");
			}
			return null;
		}
		return test ? value1 : value2; 
	}
	
	// ===================== EQUALS function ===================== //
	/**
	 * Checks if the two specified objects are equal.
	 */
	@Function("EQUALS")
	@FunctionParameters({
		@FunctionParameter("obj1"),
		@FunctionParameter("obj2")})
	public static Boolean EQUALS(Object obj1, Object obj2){
		if (obj1!=null){
			return obj1.equals(obj2);
		}
		else if (obj2!=null){
			return obj2.equals(obj1);
		}
		return true;	// both null
	}	
}
