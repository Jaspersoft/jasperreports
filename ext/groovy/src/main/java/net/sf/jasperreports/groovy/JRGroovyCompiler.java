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
 * Peter Severin - peter_p_s@users.sourceforge.net 
 */
package net.sf.jasperreports.groovy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.tools.GroovyClass;

import net.sf.jasperreports.compilers.DirectExpressionValueFilter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.CompiledClasses;
import net.sf.jasperreports.engine.design.JRAbstractJavaCompiler;
import net.sf.jasperreports.engine.design.JRCompilationSourceCode;
import net.sf.jasperreports.engine.design.JRCompilationUnit;
import net.sf.jasperreports.engine.design.JRDefaultCompilationSourceCode;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * Calculator compiler that uses groovy to compile expressions.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net), Peter Severin (peter_p_s@users.sourceforge.net)
 */
public class JRGroovyCompiler extends JRAbstractJavaCompiler 
{

	public static final String EXCEPTION_MESSAGE_KEY_COMPILING_EXPRESSIONS_CLASS_FILE = "compilers.compiling.expressions.class.file";
	public static final String EXCEPTION_MESSAGE_KEY_TOO_FEW_CLASSES_GENERATED = "compilers.groovy.too.few.classes.generated";
	public static final String EXCEPTION_MESSAGE_KEY_TOO_MANY_CLASSES_GENERATED = "compilers.groovy.too.many.classes.generated";
	public static final String EXCEPTION_MESSAGE_KEY_REPORT_NOT_COMPILED_FOR_CLASS_FILTERING = 
			"compilers.groovy.report.not.compiled.for.class.filtering";
	
	/**
	 * 
	 */
	public JRGroovyCompiler(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext, false);
	}

	@Override
	protected DirectExpressionValueFilter directValueFilter()
	{
		return GroovyDirectExpressionValueFilter.instance();
	}
	

	@Override
	protected String compileUnits(JRCompilationUnit[] units, String classpath, File tempDirFile) throws JRException
	{
		CompilerConfiguration config = new CompilerConfiguration();
		config.setSourceEncoding(StandardCharsets.UTF_8.name());
		//config.setClasspath(classpath);
		
		if (reportClassFilter.isFilteringEnabled())
		{
			config.addCompilationCustomizers(new GroovyClassFilterTransformer());
		}
		
		CompilationUnit unit = new CompilationUnit(config);
		
		for (JRCompilationUnit crtUnit : units)
		{
			byte[] sourceBytes = crtUnit.getSourceCode().getBytes(StandardCharsets.UTF_8);
			unit.addSource("calculator_" + crtUnit.getCompileName(), new ByteArrayInputStream(sourceBytes));
		}
		
		try 
		{
			unit.compile(Phases.CLASS_GENERATION);
		} 
		catch (CompilationFailedException e) 
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_COMPILING_EXPRESSIONS_CLASS_FILE, 
					new Object[] { e.toString()}, 
					e);
		}

		List<GroovyClass> generatedClasses = unit.getClasses();
		if (generatedClasses.size() < units.length) 
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_TOO_FEW_CLASSES_GENERATED,
					(Object[])null);
		} 
		
		Map<String, byte[]> classBytes = generatedClasses.stream().collect(
				Collectors.toMap(GroovyClass::getName, GroovyClass::getBytes));
		CompiledClasses compiledClasses = new CompiledClasses(classBytes);
		for (JRCompilationUnit crtUnit : units)
		{
			crtUnit.setCompileData(compiledClasses);
		}
		
		return null;
	}


	@Override
	protected void checkLanguage(String language) throws JRException
	{
		if (
			!JRReport.LANGUAGE_GROOVY.equals(language)
			&& !JRReport.LANGUAGE_JAVA.equals(language)
			)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_LANGUAGE_NOT_SUPPORTED,
					new Object[]{language, JRReport.LANGUAGE_GROOVY, JRReport.LANGUAGE_JAVA});
		}
	}


	@Override
	protected JRCompilationSourceCode generateSourceCode(JRSourceCompileTask sourceTask) throws JRException
	{
		return new JRDefaultCompilationSourceCode(JRGroovyGenerator.generateClass(sourceTask, reportClassFilter), null);
	}


	@Override
	protected String getSourceFileName(String unitName)
	{
		return unitName + ".groovy";
	}

	@Override
	protected Class<?> loadClass(String className, byte[] compileData)
	{
		return JRClassLoader.loadClassFromBytes(className, compileData);
	}

	@Override
	protected Class<?> loadClass(String className, CompiledClasses classes)
	{
		return JRClassLoader.loadClassFromBytes(null, className, classes);
	}

	@Override
	protected JREvaluator loadEvaluator(Serializable compileData, String className) throws JRException
	{
		JREvaluator evaluator = super.loadEvaluator(compileData, className);
		if (reportClassFilter.isFilteringEnabled())
		{
			if (!(evaluator instanceof GroovySandboxEvaluator))
			{
				throw new JRException(EXCEPTION_MESSAGE_KEY_REPORT_NOT_COMPILED_FOR_CLASS_FILTERING);
			}
			
			((GroovySandboxEvaluator) evaluator).setReportClassFilter(reportClassFilter);
		}
		return evaluator;
	}

}