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
package net.sf.jasperreports.engine.util;

import net.sf.jasperreports.engine.JRExpressionChunk;

/**
 * Composite expression chunk visitor.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CompositeExpressionChunkVisitor implements ExpressionChunkVisitor 
{

	private final ExpressionChunkVisitor[] visitors;
	
	/**
	 * Creates a composite visitor that uses a list of visitors.
	 * 
	 * @param visitors the list of visitors
	 */
	public CompositeExpressionChunkVisitor(ExpressionChunkVisitor ... visitors)
	{
		this.visitors = visitors;
	}
	
	@Override
	public void visitTextChunk(JRExpressionChunk chunk)
	{
		for (ExpressionChunkVisitor visitor : visitors)
		{
			visitor.visitTextChunk(chunk);
		}
	}

	@Override
	public void visitParameterChunk(JRExpressionChunk chunk)
	{
		for (ExpressionChunkVisitor visitor : visitors)
		{
			visitor.visitParameterChunk(chunk);
		}
	}

	@Override
	public void visitFieldChunk(JRExpressionChunk chunk)
	{
		for (ExpressionChunkVisitor visitor : visitors)
		{
			visitor.visitFieldChunk(chunk);
		}
	}

	@Override
	public void visitVariableChunk(JRExpressionChunk chunk)
	{
		for (ExpressionChunkVisitor visitor : visitors)
		{
			visitor.visitVariableChunk(chunk);
		}
	}

	@Override
	public void visitResourceChunk(JRExpressionChunk chunk)
	{
		for (ExpressionChunkVisitor visitor : visitors)
		{
			visitor.visitResourceChunk(chunk);
		}
	}

}
