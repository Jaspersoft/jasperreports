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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompilationUnit;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;
import net.sf.jasperreports.engine.util.CompositeExpressionChunkVisitor;
import net.sf.jasperreports.engine.util.JRExpressionUtil;

/**
 * Compiler for reports that use JavaScript as expression language.
 * 
 * This implementation produces evaluators that compile expressions at fill time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JavaScriptEvaluator
 * @see JavaScriptClassCompiler
 */
public class JavaScriptCompiler extends JavaScriptCompilerBase
{

	/**
	 * Creates a JavaScript compiler.
	 */
	public JavaScriptCompiler(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}

	@Override
	protected void checkLanguage(String language) throws JRException
	{
		//NOOP
	}

	@Override
	protected String compileUnits(JRCompilationUnit[] units, String classpath,
			File tempDirFile) throws JRException
	{
		Context context = ContextFactory.getGlobal().enterContext();
		try
		{
			Errors errors = new Errors();
			for (JRCompilationUnit unit : units)
			{
				JavaScriptCompileData compileData = new JavaScriptCompileData();
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

						compileData.addExpression(id, defaultExpression, estimatedVisitor.getScript(), oldVisitor.getScript());
					}
				}
				
				if (!errors.hasErrors())
				{
					unit.setCompileData(compileData);
				}
			}
			
			return errors.errorMessage();
		}
		finally
		{
			Context.exit();
		}
	}

}
