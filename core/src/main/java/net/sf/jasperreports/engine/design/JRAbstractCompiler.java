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
package net.sf.jasperreports.engine.design;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.sf.jasperreports.compilers.CompositeDirectExpressionEvaluators;
import net.sf.jasperreports.compilers.DirectEvaluator;
import net.sf.jasperreports.compilers.DirectExpressionEvaluators;
import net.sf.jasperreports.compilers.DirectExpressionValueFilter;
import net.sf.jasperreports.compilers.DirectValueClassFilterDecorator;
import net.sf.jasperreports.compilers.IdentityExpressionValueFilter;
import net.sf.jasperreports.compilers.InterpretedExpressionEvaluators;
import net.sf.jasperreports.compilers.ReportClassFilter;
import net.sf.jasperreports.compilers.ReportExpressionEvaluationData;
import net.sf.jasperreports.compilers.ReportExpressionsCompilation;
import net.sf.jasperreports.compilers.ReportExpressionsCompiler;
import net.sf.jasperreports.compilers.ReportSourceCompilation;
import net.sf.jasperreports.compilers.SimpleTextEvaluators;
import net.sf.jasperreports.compilers.StandardExpressionEvaluators;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.JRCrosstabParameter;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.util.JRStringUtil;

/**
 * Base class for report compilers.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRAbstractCompiler implements JRCompiler
{
	public static final String EXCEPTION_MESSAGE_KEY_CROSSTAB_ID_NOT_FOUND = "compilers.crosstab.id.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_DESIGN_COMPILE_ERROR = "compilers.design.compile.error";
	public static final String EXCEPTION_MESSAGE_KEY_LANGUAGE_NOT_SUPPORTED = "compilers.language.not.supported";
	public static final String EXCEPTION_MESSAGE_KEY_REPORT_EXPRESSIONS_COMPILE_ERROR = "compilers.report.expressions.compile.error";
	public static final String EXCEPTION_MESSAGE_KEY_TEMP_DIR_NOT_FOUND = "compilers.temp.dir.not.found";
	
	protected final JasperReportsContext jasperReportsContext;
	private final boolean needsSourceFiles;
	
	private ReportExpressionsCompiler expressionsCompiler;

	protected ReportClassFilter reportClassFilter;

	/**
	 * Constructor.
	 * 
	 * @param needsSourceFiles whether the compiler needs source files or is able to do in memory compilation
	 * <p>
	 * If true, the generated code is saved in source files to be used by the compiler.
	 */
	protected JRAbstractCompiler(JasperReportsContext jasperReportsContext, boolean needsSourceFiles)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.needsSourceFiles = needsSourceFiles;
		this.expressionsCompiler = ReportExpressionsCompiler.instance();
		
		this.reportClassFilter = new ReportClassFilter(jasperReportsContext);
	}

	
	/**
	 * Returns the name of the expression evaluator unit for a dataset of a report.
	 * 
	 * @param report the report
	 * @param dataset the dataset
	 * @return the generated expression evaluator unit name
	 */
	public static String getUnitName(JasperReport report, JRDataset dataset)
	{
		return getUnitName(report, dataset, report.getCompileNameSuffix());
	}

	protected static String getUnitName(JRReport report, JRDataset dataset, String nameSuffix)
	{
		String className;
		if (dataset.isMainDataset())
		{
			className = dataset.getName();
		}
		else
		{
			className = report.getName() + "_" + dataset.getName();
		}
		
		className = JRStringUtil.getJavaIdentifier(className) + nameSuffix;
		
		return className;
	}
	
	/**
	 * Returns the name of the expression evaluator unit for a crosstab of a report.
	 * 
	 * @param report the report
	 * @param crosstab the crosstab
	 * @return the generated expression evaluator unit name
	 */
	public static String getUnitName(JasperReport report, JRCrosstab crosstab)
	{
		return getUnitName(report, crosstab.getId(), report.getCompileNameSuffix());
	}

	
	protected static String getUnitName(JRReport report, JRCrosstab crosstab, JRExpressionCollector expressionCollector, String nameSuffix)
	{
		Integer crosstabId = expressionCollector.getCrosstabId(crosstab);
		if (crosstabId == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CROSSTAB_ID_NOT_FOUND,
					(Object[])null);
		}
		
		return getUnitName(report, crosstabId, nameSuffix);
	}

	protected static String getUnitName(JRReport report, int crosstabId, String nameSuffix)
	{
		return JRStringUtil.getJavaIdentifier(report.getName()) + "_CROSSTAB" + crosstabId + nameSuffix;
	}
	
	@Override
	public final JasperReport compileReport(JasperDesign jasperDesign) throws JRException
	{
		// check if the language is supported by the compiler
		checkLanguage(jasperDesign.getLanguage() == null ? JRReport.LANGUAGE_JAVA : jasperDesign.getLanguage());
		
		// collect all report expressions
		JRExpressionCollector expressionCollector = JRExpressionCollector.collector(jasperReportsContext, jasperDesign);
		
		// verify the report design
		verifyDesign(jasperDesign, expressionCollector);

		String nameSuffix = createNameSuffix();
		
		// check if saving source files is required
		boolean isKeepJavaFile = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(JRCompiler.COMPILER_KEEP_JAVA_FILE);
		File tempDirFile = null;
		if (isKeepJavaFile || needsSourceFiles)
		{
			String tempDirStr = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(JRCompiler.COMPILER_TEMP_DIR);

			tempDirFile = new File(tempDirStr);
			if (!tempDirFile.exists() || !tempDirFile.isDirectory())
			{
				throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_TEMP_DIR_NOT_FOUND,
					new Object[]{tempDirStr});
			}
		}

		List<JRDataset> datasets = jasperDesign.getDatasetsList();
		List<JRCrosstab> crosstabs = jasperDesign.getCrosstabs();
		
		JRCompilationUnit[] units = new JRCompilationUnit[datasets.size() + crosstabs.size() + 1];
		
		// generating source code for the main report dataset
		units[0] = createCompileUnit(jasperDesign, jasperDesign.getMainDesignDataset(), expressionCollector, tempDirFile, nameSuffix);

		int sourcesCount = 1;
		for (Iterator<JRDataset> it = datasets.iterator(); it.hasNext(); ++sourcesCount)
		{
			JRDesignDataset dataset = (JRDesignDataset) it.next();
			// generating source code for a sub dataset
			units[sourcesCount] = createCompileUnit(jasperDesign, dataset, expressionCollector, tempDirFile, nameSuffix);
		}
		
		for (Iterator<JRCrosstab> it = crosstabs.iterator(); it.hasNext(); ++sourcesCount)
		{
			JRDesignCrosstab crosstab = (JRDesignCrosstab) it.next();
			// generating source code for a sub dataset
			units[sourcesCount] = createCompileUnit(jasperDesign, crosstab, expressionCollector, tempDirFile, nameSuffix);
		}
		
		//TODO component - component compilation units?

		String classpath = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(JRCompiler.COMPILER_CLASSPATH);
		
		// compiling generated sources
		CompilationUnits compilationUnits = new CompilationUnits(units);
		JRCompilationUnit[] sourceUnits = compilationUnits.getSourceUnits();
		try
		{
			if (sourceUnits.length > 0)
			{
				String compileErrors = compileUnits(sourceUnits, classpath, tempDirFile);
				if (compileErrors != null)
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_REPORT_EXPRESSIONS_COMPILE_ERROR,
							new Object[]{compileErrors});
				}
			}

			// creating the report compile data
			JRReportCompileData reportCompileData = new JRReportCompileData();
			reportCompileData.setMainDatasetCompileData(createCompileData(compilationUnits.getCompiledUnit(0)));
			
			for (ListIterator<JRDataset> it = datasets.listIterator(); it.hasNext();)
			{
				JRDesignDataset dataset = (JRDesignDataset) it.next();
				reportCompileData.setDatasetCompileData(dataset, createCompileData(compilationUnits.getCompiledUnit(it.nextIndex())));
			}
			
			for (ListIterator<JRCrosstab> it = crosstabs.listIterator(); it.hasNext();)
			{
				JRDesignCrosstab crosstab = (JRDesignCrosstab) it.next();
				Integer crosstabId = expressionCollector.getCrosstabId(crosstab);
				reportCompileData.setCrosstabCompileData(crosstabId, createCompileData(compilationUnits.getCompiledUnit(datasets.size() + it.nextIndex())));
			}

			// creating the report
			JasperReport jasperReport = 
				new JasperReport(
					jasperDesign,
					getCompilerClass(),
					reportCompileData,
					expressionCollector,
					nameSuffix
					);
			
			return jasperReport;
		}
		catch (JRException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DESIGN_COMPILE_ERROR, 
					null, 
					e);
		}
		finally
		{
			if (needsSourceFiles && !isKeepJavaFile)
			{
				deleteSourceFiles(sourceUnits);
			}
		}
	}
	
	protected ReportExpressionEvaluationData createCompileData(JRCompilationUnit unit)
	{
		ReportExpressionEvaluationData data = new ReportExpressionEvaluationData();
		data.setCompileName(unit.getCompileName());
		data.setCompileData(unit.getCompileData());
		data.setDirectEvaluations(unit.getDirectEvaluations());
		return data;
	}


	private static String createNameSuffix()
	{
		//no longer used, we now generate a hash suffix for each compiled unit
		return "";
	}


	protected String getCompilerClass()
	{
		return getClass().getName();
	}

	
	private void verifyDesign(JasperDesign jasperDesign, JRExpressionCollector expressionCollector) throws JRException
	{
		Collection<JRValidationFault> brokenRules = JRVerifier.verifyDesign(jasperReportsContext, jasperDesign, expressionCollector);
		if (brokenRules != null && brokenRules.size() > 0)
		{
			throw new JRValidationException(brokenRules);
		}
	}
	
	private JRCompilationUnit createCompileUnit(JasperDesign jasperDesign, JRDesignDataset dataset, JRExpressionCollector expressionCollector, File saveSourceDir, String nameSuffix) throws JRException
	{		
		String unitName = JRAbstractCompiler.getUnitName(jasperDesign, dataset, nameSuffix);
		
		JRExpressionCollector datasetCollector = expressionCollector.getCollector(dataset);
		ReportExpressionsCompilation expressions = expressionsCompiler.getExpressionsCompilation(datasetCollector);
		
		JRCompilationUnit compilationUnit = new JRCompilationUnit(unitName);
		compilationUnit.setDirectEvaluations(expressions.getDirectEvaluations());
		
		ReportSourceCompilation<JRParameter> sourceCompilation = new ReportSourceCompilation<>(
				jasperReportsContext, jasperDesign, expressions, 
				listToMap(dataset.getParametersList(), JRParameter::getName), 
				listToMap(dataset.getFieldsList(), JRField::getName), 
				dataset.getVariablesMap(), dataset.getVariables());
		if (sourceCompilation.hasSource())
		{
			JRSourceCompileTask sourceTask = new JRSourceCompileTask(jasperDesign, unitName,
					datasetCollector, sourceCompilation, false);
			JRCompilationSourceCode sourceCode = generateSourceCode(sourceTask);			
			File sourceFile = getSourceFile(saveSourceDir, sourceTask.getCompileName(), sourceCode);
			
			compilationUnit.setSource(sourceCode, sourceFile, sourceTask);
		}
		return compilationUnit;
	}
	
	private JRCompilationUnit createCompileUnit(JasperDesign jasperDesign, JRDesignCrosstab crosstab, JRExpressionCollector expressionCollector, File saveSourceDir, String nameSuffix) throws JRException
	{		
		String unitName = JRAbstractCompiler.getUnitName(jasperDesign, crosstab, expressionCollector, nameSuffix);
		
		JRExpressionCollector crosstabCollector = expressionCollector.getCollector(crosstab);
		ReportExpressionsCompilation expressions = expressionsCompiler.getExpressionsCompilation(crosstabCollector);
		
		JRCompilationUnit compilationUnit = new JRCompilationUnit(unitName);
		compilationUnit.setDirectEvaluations(expressions.getDirectEvaluations());
		
		ReportSourceCompilation<JRCrosstabParameter> sourceCompilation = new ReportSourceCompilation<>(
				jasperReportsContext, jasperDesign, expressions, 
				listToMap(crosstab.getParametersList(), JRCrosstabParameter::getName), 
				null, crosstab.getVariablesMap(), crosstab.getVariables());
		if (sourceCompilation.hasSource())
		{
			JRSourceCompileTask sourceTask = new JRSourceCompileTask(jasperDesign, unitName, 
					crosstabCollector, sourceCompilation, true);
			JRCompilationSourceCode sourceCode = generateSourceCode(sourceTask);			
			File sourceFile = getSourceFile(saveSourceDir, sourceTask.getCompileName(), sourceCode);

			compilationUnit.setSource(sourceCode, sourceFile, sourceTask);
		}
		return compilationUnit;
	}

	private static <T> Map<String, T> listToMap(List<T> list, Function<T, String> key)
	{
		if (list == null)
		{
			return null;
		}
		
		return list.stream().collect(Collectors.toMap(key, Function.identity(), 
				(a, b) -> b, LinkedHashMap::new));
	}

	protected File getSourceFile(File saveSourceDir, String unitName, JRCompilationSourceCode sourceCode)
	{
		File sourceFile = null;
		if (saveSourceDir != null && sourceCode != null && sourceCode.getCode() != null)
		{
			String fileName = getSourceFileName(unitName);
			sourceFile = new File(saveSourceDir,  fileName);

			try
			{
				JRSaver.saveClassSource(sourceCode.getCode(), sourceFile);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		return sourceFile;
	}

	private void deleteSourceFiles(JRCompilationUnit[] units)
	{
		for (int i = 0; i < units.length; i++)
		{
			units[i].getSourceFile().delete();
		}
	}

	@Override
	public JREvaluator loadEvaluator(JasperReport jasperReport) throws JRException
	{
		return loadEvaluator(jasperReport, jasperReport.getMainDataset());
	}

	@Override
	public JREvaluator loadEvaluator(JasperReport jasperReport, JRDataset dataset) throws JRException
	{
		JRReportCompileData reportCompileData = (JRReportCompileData) jasperReport.getCompileData();
		String unitName = reportCompileData.getUnitName(jasperReport, dataset);
		Serializable compileData = reportCompileData.getDatasetCompileData(dataset);
		return createEvaluator(compileData, unitName);
	}

	@Override
	public JREvaluator loadEvaluator(JasperReport jasperReport, JRCrosstab crosstab) throws JRException
	{
		JRReportCompileData reportCompileData = (JRReportCompileData) jasperReport.getCompileData();
		String unitName = reportCompileData.getUnitName(jasperReport, crosstab);
		Serializable compileData = reportCompileData.getCrosstabCompileData(crosstab);
		return createEvaluator(compileData, unitName);
	}

	protected JREvaluator createEvaluator(Serializable compileData, String unitName) throws JRException
	{
		DirectExpressionValueFilter directValueFilter = effectiveDirectValueFilter();
		JREvaluator evaluator;
		DirectExpressionEvaluators baseDirectEvaluators;
		if (compileData instanceof ReportExpressionEvaluationData)
		{
			ReportExpressionEvaluationData evaluationData = (ReportExpressionEvaluationData) compileData;
			Serializable evaluatorCompileData = evaluationData.getCompileData();
			if (evaluatorCompileData == null)
			{
				evaluator = new DirectEvaluator();
			}
			else
			{
				String compileName = evaluationData.getCompileName();
				if (compileName == null)//report compiled with version older than 6.21
				{
					compileName = unitName;
				}
				evaluator = loadEvaluator(evaluatorCompileData, compileName);
			}
			
			baseDirectEvaluators = new StandardExpressionEvaluators(
					evaluationData.getDirectEvaluations(), 
					directValueFilter);
		}
		else
		{
			//report compiled with version older than 6.13
			evaluator = loadEvaluator(compileData, unitName);
			baseDirectEvaluators = new SimpleTextEvaluators();
		}

		CompositeDirectExpressionEvaluators directEvaluators = new CompositeDirectExpressionEvaluators();
		directEvaluators.add(baseDirectEvaluators);
		directEvaluators.add(new InterpretedExpressionEvaluators(directValueFilter));
		evaluator.setDirectExpressionEvaluators(directEvaluators);
		return evaluator;
	}
	
	protected DirectExpressionValueFilter effectiveDirectValueFilter()
	{
		DirectExpressionValueFilter baseFilter = directValueFilter();
		DirectExpressionValueFilter effectiveFilter;
		if (reportClassFilter.isFilteringEnabled())
		{
			effectiveFilter = new DirectValueClassFilterDecorator(baseFilter, reportClassFilter);
		}
		else
		{
			effectiveFilter = baseFilter;
		}
		return effectiveFilter;
	}
	
	protected DirectExpressionValueFilter directValueFilter()
	{
		return IdentityExpressionValueFilter.instance();
	}
	
	/**
	 * Creates an expression evaluator instance from data saved when the report was compiled.
	 * 
	 * @param compileData the data saved when the report was compiled
	 * @param unitName the evaluator unit name
	 * @return an expression evaluator instance
	 * @throws JRException
	 */
	protected abstract JREvaluator loadEvaluator(Serializable compileData, String unitName) throws JRException;

	
	/**
	 * Checks that the report language is supported by the compiler.
	 * 
	 * @param language the report language
	 * @throws JRException
	 */
	protected abstract void checkLanguage(String language) throws JRException;

	
	/**
	 * Generates expression evaluator code.
	 *
	 * @param sourceTask the source code generation information
	 * @return generated expression evaluator code
	 * @throws JRException
	 */
	protected abstract JRCompilationSourceCode generateSourceCode(JRSourceCompileTask sourceTask) throws JRException;

	
	/**
	 * Compiles several expression evaluator units.
	 * <p>
	 * The result of the compilation should be set by calling 
	 * {@link JRCompilationUnit#setCompileData(Serializable) setCompileData} on all compile units.
	 * 
	 * @param units the compilation units
	 * @param classpath the compilation classpath
	 * @param tempDirFile temporary directory
	 * @return a string containing compilation errors, or null if the compilation was successfull
	 * @throws JRException
	 */
	protected abstract String compileUnits(JRCompilationUnit[] units, String classpath, File tempDirFile) throws JRException;

	
	/**
	 * Returns the name of the source file where generated source code for an unit is saved.
	 * <p>
	 * If the compiler needs source files for compilation
	 * or {@link JRCompiler#COMPILER_KEEP_JAVA_FILE COMPILER_KEEP_JAVA_FILE} is set, the generated source
	 * will be saved in a file having the name returned by this method.
	 * 
	 * @param unitName the unit name
	 * @return the source file name
	 */
	protected abstract String getSourceFileName(String unitName);
}
