/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
import java.util.Map;

import net.sf.jasperreports.compilers.DirectExpressionEvaluation;

/**
 * Expression evaluator compilation unit used by report compilers.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRCompilationUnit
{
	/**
	 * The name of the unit.
	 */
	//no longer used, see getCompileName
	private final String name;
	
	/**
	 * The source code generated for the unit.
	 */
	private JRCompilationSourceCode source;
	
	/**
	 * The file where the source code was saved.
	 */
	private File sourceFile;
	
	private JRSourceCompileTask compileTask;

	/**
	 * The compilation data used for creating expression evaluators.
	 */
	private Serializable compileData;
	
	private Map<Integer, DirectExpressionEvaluation> directEvaluations;
	
	/**
	 * Creates a compilation unit.
	 */
	public JRCompilationUnit(String name)
	{
		this.name = name;
	}

	
	/**
	 * Returns the name of the unit.
	 * 
	 * @return the name of the unit
	 */
	public String getName()
	{
		return name;
	}

	public String getCompileName()
	{
		return compileTask == null ? null : compileTask.getCompileName();
	}
	
	/**
	 * Returns the source code generated for the unit.
	 * @return the source code generated for the unit
	 */
	public String getSourceCode()
	{
		return source.getCode();
	}

	
	/**
	 * Returns the compilation source code unit.
	 * 
	 * @return the compilation source code
	 */
	public JRCompilationSourceCode getCompilationSource()
	{
		return source;
	}
	
	/**
	 * Returns the file where the source code was saved.
	 * @return the file where the source code was saved
	 */
	public File getSourceFile()
	{
		return sourceFile;
	}
	
	
	/**
	 * Sets the compilation data used for creating expression evaluators.
	 * 
	 * @param compileData the compilation data
	 */
	public void setCompileData(Serializable compileData)
	{
		this.compileData = compileData;
	}
	
	
	/**
	 * Returns the compilation data used for creating expression evaluators.
	 * @return the compilation data used for creating expression evaluators
	 */
	public Serializable getCompileData()
	{
		return compileData;
	}
	
	/**
	 * Returns the compile task for the unit.
	 * 
	 * @return the compile task
	 */
	public JRSourceCompileTask getCompileTask()
	{
		return compileTask;
	}
	
	public void setSource(JRCompilationSourceCode sourceCode, File sourceFile, 
			JRSourceCompileTask compileTask)
	{
		this.source = sourceCode;
		this.sourceFile = sourceFile;
		this.compileTask = compileTask;
	}
	
	public boolean hasSource()
	{
		return compileTask != null;
	}

	public Map<Integer, DirectExpressionEvaluation> getDirectEvaluations()
	{
		return directEvaluations;
	}

	public void setDirectEvaluations(Map<Integer, DirectExpressionEvaluation> directEvaluations)
	{
		this.directEvaluations = directEvaluations;
	}
}
