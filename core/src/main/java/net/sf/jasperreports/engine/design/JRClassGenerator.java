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
 * Gaganis Giorgos - gaganis@users.sourceforge.net
 * Peter Severin - peter_p_s@users.sourceforge.net
 */
package net.sf.jasperreports.engine.design;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.functions.FunctionSupport;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRClassGenerator
{
	/**
	 * Property that determines the maximum size of a generated Java method
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_5_5_2,
			valueType = Integer.class
			)
	public static final String PROPERTY_MAX_METHOD_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "compiler.max.java.method.size";
	
	
	/**
	 *
	 */
	private static final int EXPR_MAX_COUNT_PER_METHOD = 60;

	protected static final String SOURCE_EXPRESSION_ID_START = "$JR_EXPR_ID=";
	protected static final int SOURCE_EXPRESSION_ID_START_LENGTH = SOURCE_EXPRESSION_ID_START.length();
	protected static final String SOURCE_EXPRESSION_ID_END = "$";

	private static Map<Byte,String> fieldPrefixMap;
	private static Map<Byte,String> variablePrefixMap;
	private static Map<Byte, String> methodSuffixMap;
	
	static
	{
		fieldPrefixMap = new HashMap<>();
		fieldPrefixMap.put(JRExpression.EVALUATION_OLD,       "Old");
		fieldPrefixMap.put(JRExpression.EVALUATION_ESTIMATED, "");
		fieldPrefixMap.put(JRExpression.EVALUATION_DEFAULT,   "");
		
		variablePrefixMap = new HashMap<>();
		variablePrefixMap.put(JRExpression.EVALUATION_OLD,       "Old");
		variablePrefixMap.put(JRExpression.EVALUATION_ESTIMATED, "Estimated");
		variablePrefixMap.put(JRExpression.EVALUATION_DEFAULT,   "");
		
		methodSuffixMap = new HashMap<>();
		methodSuffixMap.put(JRExpression.EVALUATION_OLD,       "Old");
		methodSuffixMap.put(JRExpression.EVALUATION_ESTIMATED, "Estimated");
		methodSuffixMap.put(JRExpression.EVALUATION_DEFAULT,   "");		
	}

	protected final JRSourceCompileTask sourceTask;

	private final int maxMethodSize;
	
	protected Map<String, ? extends JRParameter> parametersMap;
	protected Map<String,JRField> fieldsMap;
	protected Map<String,JRVariable> variablesMap;
	protected JRVariable[] variables;
	
	protected JRClassGenerator(JRSourceCompileTask sourceTask)
	{
		this.sourceTask = sourceTask;
		
		parametersMap = sourceTask.getParametersMap();
		fieldsMap = sourceTask.getFieldsMap();
		variablesMap = sourceTask.getVariablesMap();
		variables = sourceTask.getVariables();
		
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(sourceTask.getJasperReportsContext());
		maxMethodSize = properties.getIntegerProperty(PROPERTY_MAX_METHOD_SIZE, Integer.MAX_VALUE);
	}

	
	/**
	 * Generates Java source code for evaluating the expressions of a report/dataset/crosstab.
	 *
	 * @param sourceTask the source task containing data required to generate the source file
	 * @return the source code
	 * @throws JRException
	 */
	public static JRCompilationSourceCode generateClass(JRSourceCompileTask sourceTask) throws JRException
	{
		JRClassGenerator generator = new JRClassGenerator(sourceTask);
		return generator.generateClass();
	}

	
	/**
	 *
	 */
	public static JRCompilationSourceCode modifySource(JRSourceCompileTask sourceTask, Set<Method> missingMethods, String sourceCode)
	{
		JRClassGenerator generator = new JRClassGenerator(sourceTask);
		return generator.modifySource(missingMethods, sourceCode);
	}
	

	protected JRCompilationSourceCode generateClass() throws JRException
	{
		StringBuilder sb = new StringBuilder();

		generateClassStart(sb);

		generateDeclarations(sb);

		generateInitMethod(sb);
		generateInitParamsMethod(sb);
		if (fieldsMap != null)
		{
			generateInitFieldsMethod(sb);
		}
		generateInitVarsMethod(sb);

		List<JRExpression> expressions = sourceTask.getExpressions();
		sb.append(generateMethod(JRExpression.EVALUATION_DEFAULT, expressions));
		if (sourceTask.isOnlyDefaultEvaluation())
		{
			List<JRExpression> empty = new ArrayList<>();
			sb.append(generateMethod(JRExpression.EVALUATION_OLD, empty));
			sb.append(generateMethod(JRExpression.EVALUATION_ESTIMATED, empty));
		}
		else
		{
			sb.append(generateMethod(JRExpression.EVALUATION_OLD, expressions));
			sb.append(generateMethod(JRExpression.EVALUATION_ESTIMATED, expressions));
		}
		
		sb.append("}\n");

		String code = sb.toString();
		
		return parseSourceLines(code);
	}


	private void generateInitMethod(StringBuilder sb)
	{
		sb.append("\n");
		sb.append("\n");
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
		sb.append("    public void customizedInit(\n"); 
		sb.append("        Map pm,\n");
		sb.append("        Map fm,\n"); 
		sb.append("        Map vm\n");
		sb.append("        )\n");
		sb.append("    {\n");
		sb.append("        initParams(pm);\n");
		if (fieldsMap != null)
		{
			sb.append("        initFields(fm);\n");
		}
		sb.append("        initVars(vm);\n");
		sb.append("    }\n");
		sb.append("\n");
		sb.append("\n");
	}


	protected final void generateClassStart(StringBuilder sb)
	{
		sb.append("/*\n");
		sb.append(" * Generated by JasperReports\n");
		sb.append(" */\n");
		sb.append("import net.sf.jasperreports.engine.*;\n");
		sb.append("import net.sf.jasperreports.engine.fill.*;\n");
		sb.append("\n");
		sb.append("import java.util.*;\n");
		sb.append("import java.math.*;\n");
		sb.append("import java.text.*;\n");
		sb.append("import java.io.*;\n");
		sb.append("import java.net.*;\n");
		sb.append("\n");
		
		/*   */
		String[] imports = sourceTask.getImports();
		if (imports != null && imports.length > 0)
		{
			for (String importName : imports)
			{
				sb.append("import ");
				sb.append(importName);
				sb.append(";\n");
			}
		}

		/*   */
		sb.append("\n");
		sb.append("\n");
		sb.append("/**\n");
		sb.append(" *\n");
		sb.append(" */\n");
		sb.append("public class ");
		sb.append(sourceTask.getCompileName());
		sb.append(" extends JREvaluator\n");
		sb.append("{\n"); 
		sb.append("\n");
		sb.append("\n");
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
	}


	protected final void generateDeclarations(StringBuilder sb)
	{
		if (parametersMap != null && parametersMap.size() > 0)
		{
			Collection<String> parameterNames = parametersMap.keySet();
			for (Iterator<String> it = parameterNames.iterator(); it.hasNext();)
			{
				sb.append("    private JRFillParameter parameter_");
				sb.append(JRStringUtil.getJavaIdentifier(it.next()));
				sb.append(" = null;\n");
			}
		}
		
		if (fieldsMap != null && fieldsMap.size() > 0)
		{
			Collection<String> fieldNames = fieldsMap.keySet();
			for (Iterator<String> it = fieldNames.iterator(); it.hasNext();)
			{
				sb.append("    private JRFillField field_");
				sb.append(JRStringUtil.getJavaIdentifier(it.next()));
				sb.append(" = null;\n");
			}
		}
		
		if (variables != null && variables.length > 0)
		{
			for (JRVariable variable : variables)
			{
				sb.append("    private JRFillVariable variable_");
				sb.append(JRStringUtil.getJavaIdentifier(variable.getName()));
				sb.append(" = null;\n");
			}
		}
	}


	protected final void generateInitParamsMethod(StringBuilder sb) throws JRException
	{
		Iterator<String> parIt = null;
		if (parametersMap != null && parametersMap.size() > 0) 
		{
			parIt = parametersMap.keySet().iterator();
		}
		else
		{
			Set<String> emptySet = Collections.emptySet();
			parIt = emptySet.iterator();
		}
		generateInitParamsMethod(sb, parIt, 0);
	}		


	protected final void generateInitFieldsMethod(StringBuilder sb) throws JRException
	{
		Iterator<String> fieldIt = null;
		if (fieldsMap != null && fieldsMap.size() > 0) 
		{
			fieldIt = fieldsMap.keySet().iterator();
		}
		else
		{
			Set<String> emptySet = Collections.emptySet();
			fieldIt = emptySet.iterator();
		}
		generateInitFieldsMethod(sb, fieldIt, 0);
	}


	protected final void generateInitVarsMethod(StringBuilder sb) throws JRException
	{
		Iterator<JRVariable> varIt = null;
		if (variables != null && variables.length > 0) 
		{
			varIt = Arrays.asList(variables).iterator();
		}
		else
		{
			List<JRVariable> emptyList = Collections.emptyList();
			varIt = emptyList.iterator();
		}
		generateInitVarsMethod(sb, varIt, 0);
	}


	/**
	 *
	 */
	private void generateInitParamsMethod(StringBuilder sb, Iterator<String> it, int index) throws JRException
	{
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
		sb.append("    private void initParams");
		if (index > 0)
		{
			sb.append(index);
		}
		sb.append("(Map pm)\n");
		sb.append("    {\n");
		for (int i = 0; i < EXPR_MAX_COUNT_PER_METHOD && it.hasNext(); i++)
		{
			String parameterName = it.next();
			sb.append("        parameter_");
			sb.append(JRStringUtil.getJavaIdentifier(parameterName));
			sb.append(" = (JRFillParameter)pm.get(\"");
			sb.append(JRStringUtil.escapeJavaStringLiteral(parameterName));
			sb.append("\");\n");
		}
		if (it.hasNext())
		{
			sb.append("        initParams");
			sb.append(index + 1);
			sb.append("(pm);\n");
		}
		sb.append("    }\n");
		sb.append("\n");
		sb.append("\n");

		if (it.hasNext())
		{
			generateInitParamsMethod(sb, it, index + 1);
		}
	}		


	/**
	 *
	 */
	private void generateInitFieldsMethod(StringBuilder sb, Iterator<String> it, int index) throws JRException
	{
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
		sb.append("    private void initFields");
		if (index > 0)
		{
			sb.append(index);
		}
		sb.append("(Map fm)\n");
		sb.append("    {\n");
		for (int i = 0; i < EXPR_MAX_COUNT_PER_METHOD && it.hasNext(); i++)
		{
			String fieldName = it.next();
			sb.append("        field_");
			sb.append(JRStringUtil.getJavaIdentifier(fieldName));
			sb.append(" = (JRFillField)fm.get(\"");
			sb.append(JRStringUtil.escapeJavaStringLiteral(fieldName));
			sb.append("\");\n");
		}
		if (it.hasNext())
		{
			sb.append("        initFields");
			sb.append(index + 1);
			sb.append("(fm);\n");
		}
		sb.append("    }\n");
		sb.append("\n");
		sb.append("\n");

		if (it.hasNext())
		{
			generateInitFieldsMethod(sb, it, index + 1);
		}
	}		


	/**
	 *
	 */
	private void generateInitVarsMethod(StringBuilder sb, Iterator<JRVariable> it, int index) throws JRException
	{
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
		sb.append("    private void initVars");
		if (index > 0)
		{
			sb.append(index);
		}
		sb.append("(Map vm)\n");
		sb.append("    {\n");
		for (int i = 0; i < EXPR_MAX_COUNT_PER_METHOD && it.hasNext(); i++)
		{
			String variableName = (it.next()).getName();
			sb.append("        variable_");
			sb.append(JRStringUtil.getJavaIdentifier(variableName));
			sb.append(" = (JRFillVariable)vm.get(\"");
			sb.append(JRStringUtil.escapeJavaStringLiteral(variableName));
			sb.append("\");\n");
		}
		if (it.hasNext())
		{
			sb.append("        initVars");
			sb.append(index + 1);
			sb.append("(vm);\n");
		}
		sb.append("    }\n");
		sb.append("\n");
		sb.append("\n");

		if (it.hasNext())
		{
			generateInitVarsMethod(sb, it, index + 1);
		}
	}		


	protected final String generateMethod(byte evaluationType, List<JRExpression> expressionsList) throws JRException
	{
		StringBuilder sb = new StringBuilder();

		if (expressionsList.size() > 0)
		{
			sb.append(generateMethod(expressionsList.listIterator(), evaluationType));
		}
		else
		{
			/*   */
			sb.append("    /**\n");
			sb.append("     *\n");
			sb.append("     */\n");
			sb.append("    public Object evaluate");
			sb.append(methodSuffixMap.get(evaluationType));
			sb.append("(int id) throws Throwable\n");
			sb.append("    {\n");
			sb.append("        return null;\n");
			sb.append("    }\n");
			sb.append("\n");
			sb.append("\n");
		}
		
		return sb.toString();
	}


	/**
	 *
	 */
	private String generateMethod(Iterator<JRExpression> it, byte evaluationType) throws JRException
	{
		int methodIndex = 0;
		StringBuilder sb = new StringBuilder();

		writeMethodStart(sb, evaluationType, methodIndex);
		++methodIndex;

		StringBuilder expressionBuilder = new StringBuilder();
		int methodExpressionIndex = 0;
		int methodBuilderStartPosition = sb.length();
		while (it.hasNext())
		{
			JRExpression expression = it.next();
			
			expressionBuilder.setLength(0);
			writeExpression(expressionBuilder, expression, evaluationType);
			if (methodExpressionIndex >= EXPR_MAX_COUNT_PER_METHOD
					|| (methodExpressionIndex > 0 && sb.length() - methodBuilderStartPosition > maxMethodSize))
			{
				// end the current method
				writeMethodEnd(sb, evaluationType, methodIndex);
				
				// start a new method
				writeMethodStart(sb, evaluationType, methodIndex);
				++methodIndex;
				methodExpressionIndex = 0;
				methodBuilderStartPosition = sb.length();
			}
			
			sb.append(expressionBuilder);
			++methodExpressionIndex;
		}
		
		writeMethodEnd(sb, evaluationType, null);
		
		return sb.toString();
	}

	protected void writeMethodStart(StringBuilder sb, byte evaluationType, int methodIndex)
	{
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
		if (methodIndex > 0)
		{
			sb.append("    private Object evaluate");
			sb.append(methodSuffixMap.get(evaluationType));
			sb.append(methodIndex);
		}
		else
		{
			sb.append("    public Object evaluate");
			sb.append(methodSuffixMap.get(evaluationType));
		}
		sb.append("(int id) throws Throwable\n");
		sb.append("    {\n");
		sb.append("        Object value = null;\n");
		sb.append("\n");
		sb.append("        switch (id)\n");
		sb.append("        {\n");
	}

	protected void writeExpression(StringBuilder sb, JRExpression expression, byte evaluationType)
	{
		sb.append("            case "); 
		sb.append(sourceTask.getExpressionId(expression)); 
		sb.append(" : \n");
		sb.append("            {\n");
		sb.append("                value = ");
		sb.append(this.generateExpression(expression, evaluationType));
		sb.append(";");
		appendExpressionComment(sb, expression);
		sb.append("\n");
		sb.append("                break;\n");
		sb.append("            }\n");
	}
	
	protected void writeMethodEnd(StringBuilder sb, byte evaluationType, Integer nextMethodIndex)
	{
		sb.append("           default :\n");
		sb.append("           {\n");
		if (nextMethodIndex != null)
		{
			sb.append("               value = evaluate");
			sb.append(methodSuffixMap.get(evaluationType));
			sb.append(nextMethodIndex);
			sb.append("(id);\n");
		}
		sb.append("           }\n");
		sb.append("        }\n");
		sb.append("        \n");
		sb.append("        return value;\n");
		sb.append("    }\n");
		sb.append("\n");
		sb.append("\n");
	}



	/**
	 *
	 */
	private String generateExpression(
		JRExpression expression,
		byte evaluationType
		)
	{
		JRParameter jrParameter = null;
		JRField jrField = null;
		JRVariable jrVariable = null;

		StringBuilder sb = new StringBuilder();

		JRExpressionChunk[] chunks = expression.getChunks();
		JRExpressionChunk chunk = null;
		String chunkText = null;
		if (chunks != null && chunks.length > 0)
		{
			for (int i = 0; i < chunks.length; i++)
			{
				chunk = chunks[i];

				chunkText = chunk.getText();
				if (chunkText == null)
				{
					chunkText = "";
				}
				
				switch (chunk.getType())
				{
					case JRExpressionChunk.TYPE_TEXT :
					{
						appendExpressionText(expression, sb, chunkText);
						break;
					}
					case JRExpressionChunk.TYPE_PARAMETER :
					{
						jrParameter = parametersMap.get(chunkText);
	
						sb.append("((");
						sb.append(jrParameter.getValueClassName());
						sb.append(")parameter_");
						sb.append(JRStringUtil.getJavaIdentifier(chunkText));
						sb.append(".getValue())");
	
						break;
					}
					case JRExpressionChunk.TYPE_FIELD :
					{
						jrField = fieldsMap.get(chunkText);
	
						sb.append("((");
						sb.append(jrField.getValueClassName());
						sb.append(")field_");
						sb.append(JRStringUtil.getJavaIdentifier(chunkText)); 
						sb.append(".get");
						sb.append(fieldPrefixMap.get(evaluationType)); 
						sb.append("Value())");
	
						break;
					}
					case JRExpressionChunk.TYPE_VARIABLE :
					{
						jrVariable = variablesMap.get(chunkText);
	
						sb.append("((");
						sb.append(jrVariable.getValueClassName());
						sb.append(")variable_"); 
						sb.append(JRStringUtil.getJavaIdentifier(chunkText)); 
						sb.append(".get");
						sb.append(variablePrefixMap.get(evaluationType)); 
						sb.append("Value())");
	
						break;
					}
					case JRExpressionChunk.TYPE_RESOURCE :
					{
						sb.append("str(\"");
						sb.append(chunkText);
						sb.append("\")");
	
						break;
					}
					default :
				}
			}
		}
		
		if (sb.length() == 0)
		{
			sb.append("null");
		}

		return sb.toString();
	}


	protected void appendExpressionText(JRExpression expression, StringBuilder sb, String chunkText)
	{
		for (StringTokenizer tokenizer = new StringTokenizer(chunkText, "\n", true);
				tokenizer.hasMoreTokens();)
		{
			String token = tokenizer.nextToken();
			if (token.equals("\n"))
			{
				appendExpressionComment(sb, expression);
			}
			sb.append(token);
		}
	}

	protected void appendExpressionComment(StringBuilder sb, JRExpression expression)
	{
		sb.append(" //");
		sb.append(SOURCE_EXPRESSION_ID_START);
		sb.append(sourceTask.getExpressionId(expression));
		sb.append(SOURCE_EXPRESSION_ID_END);
	}
	
	protected JRDefaultCompilationSourceCode parseSourceLines(String sourceCode)
	{
		List<JRExpression> expressions = new ArrayList<>();
		int start = 0;
		int end = sourceCode.indexOf('\n');
		while (end >= 0)
		{
			JRExpression expression = null;
			if (start < end)
			{
				String line = sourceCode.substring(start, end);
				expression = getLineExpression(line);
			}
			expressions.add(expression);
			
			start = end + 1;
			end = sourceCode.indexOf('\n', start);
		}
		
		JRExpression[] lineExpressions = expressions.toArray(new JRExpression[expressions.size()]);

		return new JRDefaultCompilationSourceCode(sourceCode, lineExpressions);
	}


	protected JRExpression getLineExpression(String line)
	{
		JRExpression expression = null;
		int exprIdStart = line.indexOf(SOURCE_EXPRESSION_ID_START);
		if (exprIdStart >= 0)
		{
			exprIdStart += SOURCE_EXPRESSION_ID_START_LENGTH;
			int exprIdEnd = line.indexOf('$', exprIdStart);
			if (exprIdEnd >= 0)
			{
				try
				{
					int exprId = Integer.parseInt(line.substring(exprIdStart, exprIdEnd));
					expression = sourceTask.getExpression(exprId);
				}
				catch (NumberFormatException e)
				{
					// ignore
				}							
			}
		}
		return expression;
	}
	

	/**
	 * 
	 */
	protected JRCompilationSourceCode modifySource(Set<Method> missingMethods, String sourceCode)
	{
		int firstImportIndex = sourceCode.indexOf("\nimport ");
		int lastBracketIndex = sourceCode.lastIndexOf("}");
		StringBuilder importBuilder = new StringBuilder();
		StringBuilder methodBuilder = new StringBuilder();

		for (Method method : missingMethods)
		{
			Class<?> methodClass = method.getDeclaringClass();
			if (FunctionSupport.class.isAssignableFrom(methodClass))
			{
				// we need to add all methods with the same name because otherwise 
				// calls with different signatures will be listed as errors.
				Stream.of(methodClass.getMethods()).filter(classMethod -> classMethod.getName().equals(method.getName()) 
							&& Modifier.isPublic(classMethod.getModifiers()))
				.sorted(Comparator.comparing(Method::toString)).forEach(classMethod -> {
					addMethod(methodBuilder, classMethod);
				});
			}
			else if (Modifier.isStatic(method.getModifiers()))
			{
				importBuilder.append("\nimport static " + methodClass.getCanonicalName() + "." + method.getName() + ";");
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(sourceCode.substring(0,  firstImportIndex));
		sb.append(importBuilder);
		sb.append(sourceCode.substring(firstImportIndex, lastBracketIndex));
		sb.append(methodBuilder);
		sb.append(sourceCode.substring(lastBracketIndex));

		sourceCode = sb.toString();
		
		return parseSourceLines(sourceCode);
	}


	protected void addMethod(StringBuilder methodBuilder, Method method)
	{
		Class<?>[] paramTypes = method.getParameterTypes();
		StringBuilder methodSignature = new StringBuilder();
		StringBuilder methodCall = new StringBuilder();

		for (int j = 0; j < paramTypes.length; j++)
		{
			if (j > 0)
			{
				methodCall.append(", ");
				methodSignature.append(", ");
			}
			methodCall.append("arg" + j);
			
			Class<?> paramType = paramTypes[j];
			if (j == paramTypes.length - 1 && paramType.isArray() && method.isVarArgs())
			{
				// use varargs
				methodSignature.append(paramType.getComponentType().getCanonicalName()).append("... ");
			}
			else
			{
				methodSignature.append(paramType.getCanonicalName());
			}
			
			methodSignature.append(" arg" + j);
		}

		methodBuilder.append("    /**\n");
		methodBuilder.append("     *\n"); 
		methodBuilder.append("     */\n");
		methodBuilder.append("    public " + method.getReturnType().getCanonicalName() + " " + method.getName() + "(" + methodSignature.toString() + ")" + "\n");
		methodBuilder.append("    {\n");
		methodBuilder.append("        return getFunctionSupport(" + method.getDeclaringClass().getCanonicalName() + ".class)." + method.getName() + "(" + methodCall + ");\n");
		methodBuilder.append("    }\n");
		methodBuilder.append("\n");
		methodBuilder.append("\n");
	}

	
}
