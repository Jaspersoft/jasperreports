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
package net.sf.jasperreports.engine.fill;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.compilers.DirectExpressionEvaluator;
import net.sf.jasperreports.compilers.DirectExpressionEvaluators;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;
import net.sf.jasperreports.functions.FunctionSupport;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Base class for the dynamically generated expression evaluator classes.
 * This class also provides some built-in functions that will be described next.
 * <h3>Built-in Functions</h3>
 * Report expressions can perform method calls on various objects that are available during
 * report filling, such as parameters, fields, or variable values, but can also call methods on
 * a special object that is already available as the <code>this</code> reference. This is the calculator
 * object. It has public utility methods that are ready to use inside report expressions.
 * <p>
 * Currently, there are only a few utility methods of the calculator object available as built-in
 * functions inside report expressions. These are the following:</p>
 * <ul>
 * <li><code>msg</code> - this function offers a convenient way to format messages based on the current
 * report locale, just as you would normally do when using a
 * <code>java.text.MessageFormat</code> instance. Furthermore, several signatures for this
 * function take up to three message parameters in order to make the formatting
 * functionality easier to use.</li>
 * <li><code>str</code> - this function is the equivalent of the <code>$R{}</code> syntax. It gives access 
 * to locale specific resources from the associated resource bundle.</li>
 * </ul>
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JREvaluator implements DatasetExpressionEvaluator
{
	private static final String EXCEPTION_MESSAGE_KEY_RESOURCE_NOT_FOUND = "fill.evaluator.resource.not.found";
	
	/**
	 * The expression evaluation engine in JasperReports has always ignored java.lang.NullPointerException 
	 * exceptions raised during expression evaluation. An expression raising NullPointerException is evaluated to null. 
	 * However, in certain cases, users want to be able to track down the source of their NPE and this configuration 
	 * property can be set to instruct the expression evaluation engine to treat NPEs just the way all other expression 
	 * exceptions are treated. 
	 * The default value of this configuration property is true, meaning NPEs are ignored. 
	 * The property can be set globally, at report or at dataset level. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_1_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_IGNORE_NPE = JRPropertiesUtil.PROPERTY_PREFIX + "evaluator.ignore.npe";

	/**
	 * The resource bundle parameter.
	 */
	private JRFillParameter resourceBundle;
	
	/**
	 * The resource missing type.
	 */
	private WhenResourceMissingTypeEnum whenResourceMissingType;

	/**
	 * The report Locale used when parsing the bundle message.
	 */
	private JRFillParameter locale;
	
	/**
	 * The function objects.
	 */
	private Map<String, FunctionSupport> functions;
	
	/**
	 *
	 */
	private FillFunctionContext functionContext;

	/**
	 *
	 */
	protected boolean ignoreNPE = true;
	
	private DirectExpressionEvaluators directExpressionEvaluators;

	/**
	 * Default constructor.
	 */
	protected JREvaluator()
	{
	}
	
	public void setDirectExpressionEvaluators(DirectExpressionEvaluators directExpressionEvaluators)
	{
		this.directExpressionEvaluators = directExpressionEvaluators;
	}

	private DirectExpressionEvaluator directEvaluator(JRExpression expression)
	{
		return directExpressionEvaluators == null ? null 
				: directExpressionEvaluators.getEvaluator(expression);
	}

	/**
	 * Initializes the evaluator by setting the parameter, field and variable objects.
	 * 
	 * @param parametersMap the parameters indexed by name
	 * @param fieldsMap the fields indexed by name
	 * @param variablesMap the variables indexed by name
	 * @param resourceMissingType the resource missing type
	 * @throws JRException
	 */
	@Override
	public void init(
			Map<String, JRFillParameter> parametersMap, 
			Map<String, JRFillField> fieldsMap, 
			Map<String, JRFillVariable> variablesMap, 
			WhenResourceMissingTypeEnum resourceMissingType,
			boolean ignoreNPE
			) throws JRException
	{
		whenResourceMissingType = resourceMissingType;
		this.ignoreNPE = ignoreNPE;
		resourceBundle = parametersMap.get(JRParameter.REPORT_RESOURCE_BUNDLE);
		locale = parametersMap.get(JRParameter.REPORT_LOCALE);
		
		functions = new HashMap<>();
		functionContext = new FillFunctionContext(parametersMap);
		
		customizedInit(parametersMap, fieldsMap, variablesMap);
		
		if (directExpressionEvaluators != null)
		{
			directExpressionEvaluators.init(this, parametersMap, fieldsMap, variablesMap);
		}
	}

	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T extends FunctionSupport> T getFunctionSupport(Class<T> clazz)
	{
		String classId = clazz.getName();
		if (!functions.containsKey(classId))
		{
			try
			{
				FunctionSupport functionSupport = clazz.getDeclaredConstructor().newInstance();
				functionSupport.init(functionContext);
				functions.put(classId, functionSupport);
			}
			catch (IllegalAccessException | InstantiationException 
				| NoSuchMethodException | InvocationTargetException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		return (T)functions.get(classId);
	}

	/**
	 * Constructs a message using a pattern with one parameter.
	 * 
	 * @param pattern the message pattern
	 * @param arg0 the message parameter
	 * @return the constructed message
	 * @see MessageFormat#format(java.lang.Object[],java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public String msg(String pattern, Object arg0)
	{
		return MessageFormatWrapper.format(pattern, (Locale) locale.getValue(), new Object[] { arg0 });
	}

	/**
	 * Constructs a message using a pattern with two parameters.
	 * 
	 * @param pattern the message pattern
	 * @param arg0 the first message parameter
	 * @param arg1 the second message parameter
	 * @return the constructed message
	 * @see MessageFormat#format(java.lang.Object[],java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public String msg(String pattern, Object arg0, Object arg1)
	{
		return MessageFormatWrapper.format(pattern, (Locale) locale.getValue(), new Object[] { arg0, arg1 });
	}

	
	/**
	 * Constructs a message using a pattern with three parameters.
	 * 
	 * @param pattern the message pattern
	 * @param arg0 the first message parameter
	 * @param arg1 the second message parameter
	 * @param arg2 the third parameter
	 * @return the constructed message
	 * @see MessageFormat#format(java.lang.Object[],java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public String msg(String pattern, Object arg0, Object arg1, Object arg2)
	{
		return MessageFormatWrapper.format(pattern, (Locale) locale.getValue(), new Object[] { arg0, arg1, arg2 });
	}

	/**
	 * Constructs a message using a pattern with any number of parameters.
	 * 
	 * @param pattern the message pattern
	 * @param args the message parameters
	 * @return the constructed message
	 * @see MessageFormat#format(java.lang.Object[],java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public String msg(String pattern, Object... args)
	{
		return MessageFormatWrapper.format(pattern, (Locale) locale.getValue(), args);
	}

	/**
	 * Returns a string for a given key from the resource bundle associated with the evaluator.
	 * 
	 * @param key the key
	 * @return the string for the given key
	 * @see ResourceBundle#getString(java.lang.String)
	 */
	public String str(String key)
	{
		String str = null;

		try
		{
			str = ((ResourceBundle) resourceBundle.getValue()).getString(key);
		}
		catch (NullPointerException e) //NOPMD
		{
			if (ignoreNPE)
			{
				str = handleMissingResource(key, e);
			}
			else
			{
				throw e;
			}
		}
		catch (MissingResourceException e)
		{
			str = handleMissingResource(key, e);
		}

		return str;
	}

	protected Object handleEvaluationException(JRExpression expression, Throwable e) throws JRExpressionEvalException
	{
		throw new JRExpressionEvalException(expression, e);
	}

	@Override
	public Object evaluate(JRExpression expression) throws JRExpressionEvalException
	{
		Object value = null;
		
		if (expression != null)
		{
			DirectExpressionEvaluator directEvaluator = directEvaluator(expression);
			try
			{
				if (directEvaluator != null)
				{
					value = directEvaluator.evaluate();
				}
				else
				{
					value = evaluate(expression.getId());
				}
			}
			catch (NullPointerException e) //NOPMD
			{
				if (!ignoreNPE) throw new JRExpressionEvalException(expression, e);
			}
			catch (OutOfMemoryError e)
			{
				throw e;
			}
			// we have to catch Throwable because there is no way we could modify the signature
			// of the evaluate method, without breaking backward compatibility of compiled report templates 
			catch (Throwable e) //NOPMD
			{
				value = handleEvaluationException(expression, e);
			}
		}
		
		return value;
	}
	

	@Override
	public Object evaluateOld(JRExpression expression) throws JRExpressionEvalException
	{
		Object value = null;
		
		if (expression != null)
		{
			DirectExpressionEvaluator directEvaluator = directEvaluator(expression);
			try
			{
				if (directEvaluator != null)
				{
					value = directEvaluator.evaluateOld();
				}
				else
				{
					value = evaluateOld(expression.getId());
				}
			}
			catch (NullPointerException e) //NOPMD
			{
				if (!ignoreNPE) throw new JRExpressionEvalException(expression, e);
			}
			catch (OutOfMemoryError e)
			{
				throw e;
			}
			// we have to catch Throwable because there is no way we could modify the signature
			// of the evaluate method, without breaking backward compatibility of compiled report templates 
			catch (Throwable e) //NOPMD
			{
				value = handleEvaluationException(expression, e);
			}
		}
		
		return value;
	}


	@Override
	public Object evaluateEstimated(JRExpression expression) throws JRExpressionEvalException
	{
		Object value = null;
		
		if (expression != null)
		{
			DirectExpressionEvaluator directEvaluator = directEvaluator(expression);
			try
			{
				if (directEvaluator != null)
				{
					value = directEvaluator.evaluateEstimated();
				}
				else
				{
					value = evaluateEstimated(expression.getId());
				}
			}
			catch (NullPointerException e) //NOPMD
			{
				if (!ignoreNPE) throw new JRExpressionEvalException(expression, e);
			}
			catch (OutOfMemoryError e)
			{
				throw e;
			}
			// we have to catch Throwable because there is no way we could modify the signature
			// of the evaluate method, without breaking backward compatibility of compiled report templates 
			catch (Throwable e) //NOPMD
			{
				value = handleEvaluationException(expression, e);
			}
		}
		
		return value;
	}

	
	/**
	 * Handles the case when a resource is missing.
	 * 
	 * @param key
	 *            the resource key
	 * @param e
	 *            the exception
	 * @return the value to use for the resource
	 * @throws JRRuntimeException
	 *             when the resource missing handling type is Error
	 */
	protected String handleMissingResource(String key, Exception e) throws JRRuntimeException
	{
		String str;
		switch (WhenResourceMissingTypeEnum.getValueOrDefault(whenResourceMissingType))
		{
			case EMPTY:
			{
				str = "";
				break;
			}
			case KEY:
			{
				str = key;
				break;
			}
			case ERROR:
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_RESOURCE_NOT_FOUND,
						new Object[]{key},
						e);
			}
			case NULL:
			default:
			{
				str = null;
				break;
			}
		}

		return str;
	}


	/**
	 * Initializes the parameters, fields and variables of the evaluator.
	 * 
	 * @param parametersMap the parameters indexed by name
	 * @param fieldsMap the fields indexed by name
	 * @param variablesMap the variables indexed by name
	 * @throws JRException
	 */
	protected abstract void customizedInit(
			Map<String, JRFillParameter> parametersMap, 
			Map<String, JRFillField> fieldsMap, 
			Map<String, JRFillVariable> variablesMap
			) throws JRException;


	/**
	 * Evaluates an expression using current fields and variables values.
	 * 
	 * @param id the expression id
	 * @return the result of the evaluation
	 * @throws Throwable
	 * @see net.sf.jasperreports.engine.JRExpression#EVALUATION_DEFAULT
	 * @see JRFillVariable#getValue()
	 * @see JRFillField#getValue()
	 */
	protected abstract Object evaluate(int id) throws Throwable; //NOSONAR


	/**
	 * Evaluates an expression using old fields and variables values.
	 * 
	 * @param id the expression id
	 * @return the result of the evaluation
	 * @throws Throwable
	 * @see net.sf.jasperreports.engine.JRExpression#EVALUATION_OLD
	 * @see JRFillVariable#getOldValue()
	 * @see JRFillField#getOldValue()
	 */
	protected abstract Object evaluateOld(int id) throws Throwable; //NOSONAR


	/**
	 * Evaluates an expression using estimated variables values.
	 * 
	 * @param id the expression id
	 * @return the result of the evaluation
	 * @throws Throwable
	 * @see net.sf.jasperreports.engine.JRExpression#EVALUATION_ESTIMATED
	 * @see JRFillVariable#getEstimatedValue()
	 */
	protected abstract Object evaluateEstimated(int id) throws Throwable; //NOSONAR

}
