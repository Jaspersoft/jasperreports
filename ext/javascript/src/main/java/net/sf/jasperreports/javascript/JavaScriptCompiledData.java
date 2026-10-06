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
package net.sf.jasperreports.javascript;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * Compiled Java code for reports that use JavaScript as expression language.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JavaScriptClassCompiler
 */
public class JavaScriptCompiledData implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	public static final String EXCEPTION_MESSAGE_KEY_TOO_MANY_EXPRESSIONs = "compilers.javascript.too.many.expressions";
	
	protected static class ExpressionIndexes implements Serializable
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		private final int defaultExpressionIdx;
		private final int oldExpressionIdx;
		private final int estimatedExpressionIdx;

		public ExpressionIndexes(int defaultExpressionIdx, int oldExpressionIdx, int estimatedExpressionIdx)
		{
			this.defaultExpressionIdx = defaultExpressionIdx;
			this.oldExpressionIdx = oldExpressionIdx;
			this.estimatedExpressionIdx = estimatedExpressionIdx;
		}

		public int getDefaultExpressionIndex()
		{
			return defaultExpressionIdx;
		}

		public int getOldExpressionIndex()
		{
			return oldExpressionIdx;
		}

		public int getEstimatedExpressionIndex()
		{
			return estimatedExpressionIdx;
		}
	}
	
	protected static class CompiledClass implements Serializable
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		private final String className;
		private final byte[] classBytes;
		
		public CompiledClass(String className, byte[] classBytes)
		{
			this.className = className;
			this.classBytes = classBytes;
		}

		public String getClassName()
		{
			return className;
		}

		public byte[] getClassBytes()
		{
			return classBytes;
		}
	}
	
	protected static int makeExpressionIndex(int scriptIndex, int expressionId)
	{
		if (scriptIndex > 0x7fff || expressionId > 0x7fff)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_TOO_MANY_EXPRESSIONs,
					(Object[])null);
		}
		
		return ((scriptIndex & 0x7fff) << 16) | (expressionId & 0x7fff);
	}
	
	protected static int scriptIndex(int expressionIndex)
	{
		return (expressionIndex >> 16) & 0x7fff;
	}
	
	protected static int expressionId(int expressionIndex)
	{
		return expressionIndex & 0x7fff;
	}
	
	private final List<ExpressionIndexes> expressionIndexes = new ArrayList<>();
	private final List<CompiledClass> compiledClasses = new ArrayList<>(1);
	
	public void addExpression(int expressionId, 
			int defaultExpressionIdx, int oldExpressionIdx, int estimatedExpressionIdx)
	{
		for (int idx = expressionIndexes.size(); idx <= expressionId; ++idx)
		{
			expressionIndexes.add(idx, null);
		}

		ExpressionIndexes expressionData = new ExpressionIndexes(defaultExpressionIdx, oldExpressionIdx, estimatedExpressionIdx);
		expressionIndexes.set(expressionId, expressionData);
	}
	
	public ExpressionIndexes getExpression(int id)
	{
		if (id >= expressionIndexes.size())
		{
			throw 
				new JRRuntimeException(
					JavaScriptCompileData.EXCEPTION_MESSAGE_KEY_EXPRESSION_NOT_FOUND,
					new Object[]{id});
		}
		ExpressionIndexes expr = expressionIndexes.get(id);
		if (expr == null)
		{
			throw 
				new JRRuntimeException(
					JavaScriptCompileData.EXCEPTION_MESSAGE_KEY_EXPRESSION_NOT_FOUND,
					new Object[]{id});
		}
		return expr;
	}

	public void addClass(String className, byte[] data)
	{
		compiledClasses.add(new CompiledClass(className, data));
	}
	
	public CompiledClass getCompiledClass(int classIndex)
	{
		return compiledClasses.get(classIndex);
	}
}
