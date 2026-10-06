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
package net.sf.jasperreports.groovy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GroovyClassFilterTransformer extends SandboxTransformer
{
	
	private static final Log log = LogFactory.getLog(GroovyClassFilterTransformer.class);

	private static final Set<String> BUILTIN_ALLOWED_TYPES;
	static
	{
		BUILTIN_ALLOWED_TYPES = new HashSet<>();
		BUILTIN_ALLOWED_TYPES.add("char");
		BUILTIN_ALLOWED_TYPES.add("byte");
		BUILTIN_ALLOWED_TYPES.add("short");
		BUILTIN_ALLOWED_TYPES.add("int");
		BUILTIN_ALLOWED_TYPES.add("long");
		BUILTIN_ALLOWED_TYPES.add("float");
		BUILTIN_ALLOWED_TYPES.add("double");
		//same list as in ReportClassFilter.addHardcodedWhitelist
		BUILTIN_ALLOWED_TYPES.add("java.lang.Boolean");
		BUILTIN_ALLOWED_TYPES.add("java.lang.String");
		BUILTIN_ALLOWED_TYPES.add("java.lang.StringBuffer");
		BUILTIN_ALLOWED_TYPES.add("java.lang.StringBuilder");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Character");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Byte");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Short");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Integer");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Long");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Float");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Double");
		BUILTIN_ALLOWED_TYPES.add("java.lang.Math");
	}
	
	protected boolean allowed(ClassNode type)
	{
		return BUILTIN_ALLOWED_TYPES.contains(type.getName());
	}
	
	@Override
	public ClassCodeExpressionTransformer createVisitor(SourceUnit source, ClassNode clazz)
	{
		return new Transformer(source, clazz);
	}

	protected class Transformer extends VisitorImpl
	{
		protected Transformer(SourceUnit sourceUnit, ClassNode clazz)
		{
			super(sourceUnit, clazz);
		}

		@Override
		protected Expression transformBinaryExpression(String checkedOperation, BinaryExpression be)
		{
			Expression leftExpression = be.getLeftExpression();
			Expression rightExpression = be.getRightExpression();
			if (allowed(leftExpression.getType()) && allowed(rightExpression.getType()))
			{
				Expression transformedLeft = transform(leftExpression);
				Expression transformedRight = transform(rightExpression);
				if (transformedLeft.equals(leftExpression) 
						&& transformedRight.equals(rightExpression))
				{
					if (log.isDebugEnabled())
					{
						log.debug("allowed binary expression " + be);
					}
					return be;
				}
				
				BinaryExpression transformedExpression = new BinaryExpression(
						transformedLeft, be.getOperation(), transformedRight);
				if (log.isDebugEnabled())
				{
					log.debug("transformed binary expression " + transformedExpression);
				}
				return transformedExpression;
			}
			
			return super.transformBinaryExpression(checkedOperation, be);
		}

		@Override
		protected Expression transformConstructorCall(ConstructorCallExpression exp)
		{
			if (allowed(exp.getType()))
			{
				Expression originalArgs = exp.getArguments();
				Expression transformedArgs = transformArguments(originalArgs);
				Expression unwrappedArgs = unwrapTransformedArguments(transformedArgs, originalArgs);
				if (unwrappedArgs.equals(originalArgs))
				{
					if (log.isDebugEnabled())
					{
						log.debug("allowed constructor call " + exp);
					}
					return exp;
				}
				
				ConstructorCallExpression transformedCall = new ConstructorCallExpression(
						exp.getType(), unwrappedArgs);
				if (log.isDebugEnabled())
				{
					log.debug("transformed constructor call " + transformedCall);
				}
				return transformedCall;
			}
			
			return super.transformConstructorCall(exp);
		}

		@Override
		protected Expression transformMethodCall(MethodCallExpression originalCall, 
				Expression transformedObject, Expression transformedMethod, Expression transformedArgs)
		{
			Expression originalObject = originalCall.getObjectExpression();
			if (allowed(originalObject.getType()))
			{
				Expression originalMethod = originalCall.getMethod();
				Expression originalArgs = originalCall.getArguments();
				Expression unwrappedArgs = unwrapTransformedArguments(transformedArgs, originalArgs);
				if (unwrappedArgs != null)
				{
					if (transformedObject.equals(originalObject) 
							&& transformedMethod.equals(originalMethod)
							&& unwrappedArgs.equals(originalArgs))
					{
						if (log.isDebugEnabled())
						{
							log.debug("allowed method call " + originalCall);
						}
						return originalCall;
					}
					
					MethodCallExpression transformedCall = new MethodCallExpression(
							transformedObject, transformedMethod, unwrappedArgs);
					transformedCall.setSafe(originalCall.isSafe());
					transformedCall.setSpreadSafe(originalCall.isSpreadSafe());
					if (log.isDebugEnabled())
					{
						log.debug("transformed method call " + transformedCall);
					}
					return transformedCall;
				}
			}
			
			return super.transformMethodCall(originalCall, 
					transformedObject, transformedMethod, transformedArgs);
		}
		
		protected Expression unwrapTransformedArguments(Expression transformedArgs, 
				Expression originalArgs)
		{
			if (!(transformedArgs instanceof MethodCallExpression))
			{
				return null;
			}
			
			MethodCallExpression transformedArgsCall = (MethodCallExpression) transformedArgs;
			Expression transformedObject = transformedArgsCall.getObjectExpression();
			Expression transformedMethod = transformedArgsCall.getMethod();
			if (!(transformedObject instanceof ListExpression) 
					|| !(transformedMethod instanceof ConstantExpression)
					|| !("toArray".equals(transformedMethod.getText())))
			{
				return null;
			}
			
			List<Expression> transformedExpressions = ((ListExpression) transformedObject).getExpressions();
			if (originalArgs instanceof ArgumentListExpression)
			{
				List<Expression> originalExpressions = ((ArgumentListExpression) originalArgs).getExpressions();
				if (transformedExpressions.equals(originalExpressions))
				{
					return originalArgs;
				}
				
				return new ArgumentListExpression(transformedExpressions);
			}
			
			return null;
		}
	}
	
}
