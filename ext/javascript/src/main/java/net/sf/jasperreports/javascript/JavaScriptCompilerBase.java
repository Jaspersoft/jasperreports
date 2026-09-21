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

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.EvaluatorException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRAbstractCompiler;
import net.sf.jasperreports.engine.design.JRCompilationSourceCode;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.util.ExpressionChunkVisitor;
import net.sf.jasperreports.engine.util.JRStringUtil;

/**
 * Base compiler class for reports that use JavaScript as expression language.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JavaScriptCompilerBase extends JRAbstractCompiler
{

	private static final Log log = LogFactory.getLog(JavaScriptCompilerBase.class);
	private static final String EXCEPTION_MESSAGE_KEY_INVALID_COMPILE_DATA_TYPE = "compilers.invalid.data.type";
	
	/**
	 * Creates a JavaScript compiler.
	 */
	protected JavaScriptCompilerBase(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext, false);
	}

	@Override
	protected void checkLanguage(String language) throws JRException
	{
		//NOOP
	}

	@Override
	protected JRCompilationSourceCode generateSourceCode(
			JRSourceCompileTask sourceTask) throws JRException
	{
		//no source code
		return null;
	}

	@Override
	protected String getSourceFileName(String unitName)
	{
		return unitName + ".js";
	}

	@Override
	protected JREvaluator loadEvaluator(Serializable compileData,
			String unitName) throws JRException
	{
		if (compileData instanceof JavaScriptCompileData)
		{
			if (log.isDebugEnabled())
			{
				log.debug("JavaScriptCompileData found for " + unitName);
			}
			
			JavaScriptCompileData jsCompileData = (JavaScriptCompileData) compileData;
			return new JavaScriptEvaluator(jasperReportsContext, jsCompileData);
		}
		
		if (compileData instanceof JavaScriptCompiledData)
		{
			JavaScriptCompiledData jsCompiledData = (JavaScriptCompiledData) compileData;
			return new JavaScriptCompiledEvaluator(jasperReportsContext, unitName, jsCompiledData);
		}
		
		throw 
			new JRException(
				EXCEPTION_MESSAGE_KEY_INVALID_COMPILE_DATA_TYPE,
				new Object[]{compileData.getClass().getName()});
	}

	protected ScriptExpressionVisitor defaultExpressionCreator()
	{
		return new ScriptExpressionVisitor("getValue", "getValue");
	}

	protected ScriptExpressionVisitor oldExpressionCreator()
	{
		return new ScriptExpressionVisitor("getOldValue", "getOldValue");
	}

	protected ScriptExpressionVisitor estimatedExpressionCreator()
	{
		return new ScriptExpressionVisitor("getValue", "getEstimatedValue");
	}

	protected static String getParameterVar(String name)
	{
		return "param_" + JRStringUtil.getJavaIdentifier(name);
	}

	protected static String getVariableVar(String name)
	{
		return "var_" + JRStringUtil.getJavaIdentifier(name);
	}

	protected static String getFieldVar(String name)
	{
		return "field_" + JRStringUtil.getJavaIdentifier(name);
	}
	
	protected static class ScriptExpressionVisitor implements ExpressionChunkVisitor
	{
		private final String fieldMethod;
		private final String variableMethod;
		protected final StringBuilder script = new StringBuilder();
		
		public ScriptExpressionVisitor(String fieldMethod, String variableMethod)
		{
			this.fieldMethod = fieldMethod;
			this.variableMethod = variableMethod;
		}
		
		public String getScript()
		{
			if (script.length() == 0)
			{
				// empty expression, should evaluate to null
				return "null";
			}
			
			return script.toString();
		}
		
		@Override
		public void visitTextChunk(JRExpressionChunk chunk)
		{
			script.append(chunk.getText());
		}

		@Override
		public void visitParameterChunk(JRExpressionChunk chunk)
		{
			String paramName = getParameterVar(chunk.getText());
			script.append(paramName);
			script.append(".getValue()");
		}
		
		@Override
		public void visitFieldChunk(JRExpressionChunk chunk)
		{
			String fieldName = getFieldVar(chunk.getText());
			script.append(fieldName).append('.').append(fieldMethod).append("()");
		}

		@Override
		public void visitVariableChunk(JRExpressionChunk chunk)
		{
			String varName = getVariableVar(chunk.getText());
			script.append(varName).append('.').append(variableMethod).append("()");
		}

		@Override
		public void visitResourceChunk(JRExpressionChunk chunk)
		{
			String key = chunk.getText();
			script.append(JavaScriptEvaluatorScope.EVALUATOR_VAR);
			script.append(".str('");
			script.append(JRStringUtil.escapeJavaScript(key));
			script.append("')");
		}
	}

	protected static class Errors
	{
		private final StringBuilder errors = new StringBuilder();
		private int errorCount = 0;
		
		public void addError(EvaluatorException error)
		{
			++errorCount;
			
			errors.append(errorCount);
			errors.append(". ");
			String message = error.getMessage();
			errors.append(message);
			errors.append(" at column ");
			errors.append(error.columnNumber());
			String lineSource = error.lineSource();
			if (lineSource != null)
			{
				errors.append(" in line\n");
				errors.append(lineSource);
			}
			errors.append("\n");
		}
		
		public boolean hasErrors()
		{
			return errorCount > 0;
		}
		
		public String errorMessage()
		{
			String errorsMessage = null;
			if (errorCount > 0)
			{
				errorsMessage = errorCount + " error(s):\n" + errors;
			}
			return errorsMessage;
		}
	}
}
