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
package net.sf.jasperreports.javascript;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.optimizer.ClassCompiler;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompilationUnit;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;
import net.sf.jasperreports.engine.util.CompositeExpressionChunkVisitor;
import net.sf.jasperreports.engine.util.JRExpressionUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Compiler for reports that use JavaScript as expression language.
 * 
 * This implementation produces Java bytecode for the expressions.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JavaScriptCompiledData
 * @see JavaScriptCompiledEvaluator
 */
public class JavaScriptClassCompiler extends JavaScriptCompilerBase
{

	/**
	 * Property that determines the optimization level to use when compiling expressions 
	 * into Java bytecode.
	 * 
	 * See <a href="http://www-archive.mozilla.org/rhino/apidocs/org/mozilla/javascript/Context.html#setOptimizationLevel%28int%29"/>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_7_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_OPTIMIZATION_LEVEL = JRPropertiesUtil.PROPERTY_PREFIX
			+ "javascript.class.compiler.optimization.level";

	/**
	 * Property that determines the maximum number of report expressions that will be included
	 * in a single generated Java class.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_7_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_EXPRESSIONS_PER_SCRIPT = JRPropertiesUtil.PROPERTY_PREFIX
			+ "javascript.class.compiler.expressions.per.script";
	
	/**
	 * Property that determines the maximum size of a script that will be compiled into
	 * a single Java class.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_7_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_SCRIPT_MAX_SIZE = JRPropertiesUtil.PROPERTY_PREFIX
			+ "javascript.class.compiler.script.max.size";
	
	private static final Log log = LogFactory.getLog(JavaScriptClassCompiler.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_CLASS_NAME = "compilers.javascript.unexpected.class.name";
	public static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_CLASSES_LENGTH = "compilers.javascript.unexpected.classes.length";

	/**
	 * Creates a JavaScript compiler.
	 */
	public JavaScriptClassCompiler(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}

	@Override
	protected String compileUnits(JRCompilationUnit[] units, String classpath,
			File tempDirFile) throws JRException
	{
		Context context = ContextFactory.getGlobal().enterContext();
		try
		{
			JRPropertiesUtil properties = JRPropertiesUtil.getInstance(jasperReportsContext);
			int expressionsPerScript = properties.getIntegerProperty(PROPERTY_EXPRESSIONS_PER_SCRIPT);
			int scriptMaxLength = properties.getIntegerProperty(PROPERTY_SCRIPT_MAX_SIZE);
			
			int optimizationLevel = properties.getIntegerProperty(PROPERTY_OPTIMIZATION_LEVEL);
			context.setOptimizationLevel(optimizationLevel);
			context.getWrapFactory().setJavaPrimitiveWrap(false);

			Errors errors = new Errors();
			
			CompilerEnvirons compilerEnv = new CompilerEnvirons();
			compilerEnv.initFromContext(context);
			
			// we're using the context to compile the expressions in interpreted mode to catch syntax errors 
			context.setOptimizationLevel(-1);
			
			for (JRCompilationUnit unit : units)
			{
				CompileSources compileSources = new CompileSources(expressionsPerScript, scriptMaxLength);
				JavaScriptCompiledData compiledData = new JavaScriptCompiledData();
				
				JRSourceCompileTask compileTask = unit.getCompileTask();
				for (JRExpression expr : compileTask.getExpressions())
				{
					int id = compileTask.getExpressionId(expr);
					
					ScriptExpressionVisitor defaultVisitor = defaultExpressionCreator();
					JRExpressionUtil.visitChunks(expr, defaultVisitor);
					String defaultExpression = defaultVisitor.getScript();
					
					//compile the default expression to catch syntax errors
					try
					{
						context.compileString(defaultExpression, "expression", 0, null);
					}
					catch (EvaluatorException e)
					{
						errors.addError(e);
					}

					if (!errors.hasErrors())
					{
						ScriptExpressionVisitor oldVisitor = oldExpressionCreator();
						ScriptExpressionVisitor estimatedVisitor = estimatedExpressionCreator();
						JRExpressionUtil.visitChunks(expr, new CompositeExpressionChunkVisitor(oldVisitor, estimatedVisitor));
						
						int defaultExpressionIdx = compileSources.addExpression(defaultExpression);
						int oldExpressionIdx = compileSources.addExpression(oldVisitor.getScript());
						int estimatedExpressionIdx = compileSources.addExpression(estimatedVisitor.getScript());
						
						compiledData.addExpression(id, defaultExpressionIdx, oldExpressionIdx, estimatedExpressionIdx);
					}
				}

				if (!errors.hasErrors())
				{
					compileScripts(unit, compilerEnv, compileSources, compiledData);
					unit.setCompileData(compiledData);
				}
			}

			return errors.errorMessage();
		}
		finally
		{
			Context.exit();
		}
	}

	protected void compileScripts(JRCompilationUnit unit, CompilerEnvirons compilerEnv, 
			CompileSources compileSources, JavaScriptCompiledData compiledData)
	{
		List<String> scripts = compileSources.getScripts();
		int scriptIndex = 0;
		for (String scriptSource : scripts)
		{
			String scriptClassName = unit.getCompileName() + "_" + scriptIndex;
			
			if (log.isTraceEnabled())
			{
				log.trace("compiling script with name " + scriptClassName
						+ "\n" + scriptSource);
			}
			
			ClassCompiler compiler = new ClassCompiler(compilerEnv);
			// this should not fail since we've already separately compiled the default expression
			Object[] compilationResult = compiler.compileToClassFiles(scriptSource, unit.getCompileName(), 0, scriptClassName);
			if (compilationResult.length != 2)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNEXPECTED_CLASSES_LENGTH,
						new Object[]{compilationResult.length});
			}
			if (!scriptClassName.equals(compilationResult[0]))
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNEXPECTED_CLASS_NAME,
						new Object[]{compilationResult[0], scriptClassName});
			}
			
			byte[] compiledClass = (byte[]) compilationResult[1];
			compiledData.addClass(scriptClassName, compiledClass);
			
			++scriptIndex;
		}
	}

	protected static class CompileSources
	{
		private final int expressionsPerSource;
		private final int scriptMaxLength;
		
		private final Map<String, Integer> expressionIndexes = new HashMap<>();
		private final List<String> scriptSources = new LinkedList<>();
		private int currentScriptIndex = 0;
		private int currentExpressionId = 0;
		private StringBuilder currentScriptSource;
		
		public CompileSources(int expressionsPerSource, int scriptMaxLength)
		{
			this.expressionsPerSource = expressionsPerSource;
			this.scriptMaxLength = scriptMaxLength;
			
			currentScriptSource = initScriptSource();
		}

		private StringBuilder initScriptSource()
		{
			StringBuilder source = new StringBuilder(1024);
			source.append("switch(");
			source.append(JavaScriptCompiledEvaluator.EXPRESSION_ID_VAR);
			source.append("){\n");
			return source;
		}
		
		public Integer expressionIndex(String expression)
		{
			return expressionIndexes.get(expression);
		}
		
		public int addExpression(String expression)
		{
			Integer existingIdx = expressionIndexes.get(expression);
			if (existingIdx != null)
			{
				return existingIdx;
			}
			
			String expressionFragment = "case " + currentExpressionId + ":\n"
					+ expression + "\nbreak\n";
			
			if (currentExpressionId >= expressionsPerSource
					|| currentScriptSource.length() + expressionFragment.length() > scriptMaxLength)
			{
				addScriptSource();
				
				// currentExpressionId has changed
				expressionFragment = "case " + currentExpressionId + ":\n"
						+ expression + "\nbreak\n";
			}
			
			currentScriptSource.append(expressionFragment);
			
			int expressionIdx = JavaScriptCompiledData.makeExpressionIndex(currentScriptIndex, currentExpressionId);
			++currentExpressionId;
			expressionIndexes.put(expression, expressionIdx);
			
			if (log.isTraceEnabled())
			{
				log.trace("expression index " + expressionIdx + " for expression " + expression);
			}
			return expressionIdx;
		}
		
		protected void addScriptSource()
		{
			closeCurrentScript();
			
			currentScriptSource = initScriptSource();
			++currentScriptIndex;
			currentExpressionId = 0;
		}

		protected void closeCurrentScript()
		{
			if (currentExpressionId > 0)
			{
				currentScriptSource.append("}");
				String scriptSource = currentScriptSource.toString();
				scriptSources.add(scriptSource);
				
				if (log.isDebugEnabled())
				{
					log.debug("created script of length " + scriptSource.length() 
							+ ", expression count " + currentExpressionId);
				}
			}
		}
		
		public List<String> getScripts()
		{
			closeCurrentScript();
			return scriptSources;
		}
	}

}
