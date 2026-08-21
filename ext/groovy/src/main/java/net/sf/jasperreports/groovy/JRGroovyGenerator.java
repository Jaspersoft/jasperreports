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
 * Gaganis Giorgos - gaganis@users.sourceforge.net
 */
package net.sf.jasperreports.groovy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.compilers.ReportClassFilter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.JRSourceCompileTask;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net), Peter Severin (peter_p_s@users.sourceforge.net)
 */
public class JRGroovyGenerator
{
	/**
	 * Property that determines the maximum size of a generated groovy method
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_5_5_2,
			valueType = Integer.class
			)
	public static final String PROPERTY_MAX_METHOD_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "compiler.max.groovy.method.size";
	
	/**
	 *
	 */
	private static final int EXPR_MAX_COUNT_PER_METHOD = 40;

	private static Map<Byte, String> fieldPrefixMap;
	private static Map<Byte, String> variablePrefixMap;
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
	
	/**
	 *
	 */
	protected final JRSourceCompileTask sourceTask;
	private final ReportClassFilter classFilter;

	private final int maxMethodSize;

	protected Map<String, ? extends JRParameter> parametersMap;
	protected Map<String, JRField> fieldsMap;
	protected Map<String, JRVariable> variablesMap;
	protected JRVariable[] variables;
	
	protected JRGroovyGenerator(JRSourceCompileTask sourceTask, ReportClassFilter classFilter)
	{
		this.sourceTask = sourceTask;
		this.classFilter = classFilter;
		
		this.parametersMap = sourceTask.getParametersMap();
		this.fieldsMap = sourceTask.getFieldsMap();
		this.variablesMap = sourceTask.getVariablesMap();
		this.variables = sourceTask.getVariables();
		
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(sourceTask.getJasperReportsContext());
		maxMethodSize = properties.getIntegerProperty(PROPERTY_MAX_METHOD_SIZE, Integer.MAX_VALUE);
	}


	/**
	 *
	 */
	public static String generateClass(JRSourceCompileTask sourceTask) throws JRException
	{
		return generateClass(sourceTask, null);
	}
	
	public static String generateClass(JRSourceCompileTask sourceTask, ReportClassFilter classFilter) throws JRException
	{
		JRGroovyGenerator generator = new JRGroovyGenerator(sourceTask, classFilter);
		return generator.generateClass();
	}
	
	
	protected String generateClass() throws JRException
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
		sb.append(this.generateMethod(JRExpression.EVALUATION_DEFAULT, expressions));
		if (sourceTask.isOnlyDefaultEvaluation())
		{
			List<JRExpression> empty = new ArrayList<>();
			sb.append(this.generateMethod(JRExpression.EVALUATION_OLD, empty));
			sb.append(this.generateMethod(JRExpression.EVALUATION_ESTIMATED, empty));
		}
		else
		{
			sb.append(this.generateMethod(JRExpression.EVALUATION_OLD, expressions));
			sb.append(this.generateMethod(JRExpression.EVALUATION_ESTIMATED, expressions));
		}

		sb.append("}\n");

		return sb.toString();
	}


	private void generateInitMethod(StringBuilder sb)
	{
		sb.append("\n");
		sb.append("\n");
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
		sb.append("    void customizedInit(\n"); 
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
		/*   */
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
			for (int i = 0; i < imports.length; i++)
			{
				sb.append("import ");
				sb.append(imports[i]);
				sb.append(";\n");
			}
		}

		/*   */
		sb.append("\n");
		sb.append("\n");
		sb.append("/**\n");
		sb.append(" *\n");
		sb.append(" */\n");
		sb.append("class ");
		sb.append(sourceTask.getCompileName());
		sb.append(" extends ");
		String baseClass = classFilter != null && classFilter.isFilteringEnabled() 
				? "net.sf.jasperreports.groovy.GroovySandboxEvaluator" 
				: "net.sf.jasperreports.groovy.GroovyEvaluator";
		sb.append(baseClass);
		sb.append("\n{\n"); 
		sb.append("\n");
		sb.append(
				"    def methodMissing(String name, args) {\n" +
				"        return functionCall(name, args);\n" +
				"    }\n");
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
			for (int i = 0; i < variables.length; i++)
			{
				sb.append("    private JRFillVariable variable_");
				sb.append(JRStringUtil.getJavaIdentifier(variables[i].getName()));
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
		sb.append("    void initParams");
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
		sb.append("    void initFields");
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
		sb.append("    void initVars");
		if (index > 0)
		{
			sb.append(index);
		}
		sb.append("(Map vm)\n");
		sb.append("    {\n");
		for (int i = 0; i < EXPR_MAX_COUNT_PER_METHOD && it.hasNext(); i++)
		{
			String variableName = it.next().getName();
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


	/**
	 *  
	 */
	protected final String generateMethod(byte evaluationType, List<JRExpression> expressionsList) throws JRException 
	{
		StringBuilder sb = new StringBuilder();
		
		if (expressionsList != null && !expressionsList.isEmpty())
		{
			sb.append(generateMethod(expressionsList.iterator(), evaluationType));
		}
		else
		{
			/*   */
			sb.append("    /**\n");
			sb.append("     *\n");
			sb.append("     */\n");
			sb.append("    Object evaluate");
			sb.append(methodSuffixMap.get(evaluationType));
			sb.append("(int id)\n");
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
		
		writeMethodHeader(sb, evaluationType, methodIndex);
		++methodIndex;
		
		StringBuilder methodBuffer = new StringBuilder();
		StringBuilder expressionBuffer = new StringBuilder();
		int methodExpressionIndex = 0;
		while (it.hasNext()) 
		{
			JRExpression expression = it.next();
			expressionBuffer.setLength(0);
			writeExpression(expressionBuffer, expression, evaluationType);
			
			if (methodExpressionIndex >= EXPR_MAX_COUNT_PER_METHOD //FIXME use a property for this
					|| (methodExpressionIndex > 0 && methodBuffer.length() + expressionBuffer.length() > maxMethodSize))
			{
				// end the current method
				
				//NB: relying on the fact that the expression ids are in ascending order
				//FIXME investigate if using a main method that directly delegates to the other methods is better
				writeNextMethodCall(sb, evaluationType, methodIndex, sourceTask.getExpressionId(expression));
				
				sb.append(methodBuffer);
				methodBuffer.setLength(0);
				writeMethodEnd(sb);
				
				// start a new method
				writeMethodHeader(sb, evaluationType, methodIndex);
				++methodIndex;
				methodExpressionIndex = 0;
			}
			
			// write expression to current method
			if (methodExpressionIndex > 0)
			{
				methodBuffer.append("        ");
				methodBuffer.append("else ");
			}
			methodBuffer.append(expressionBuffer);
			++methodExpressionIndex;
		}

		sb.append("        ");
		sb.append(methodBuffer);
		writeMethodEnd(sb);
		
		return sb.toString();
	}

	protected void writeMethodHeader(StringBuilder sb, byte evaluationType, int methodIndex)
	{
		/*   */
		sb.append("    /**\n");
		sb.append("     *\n");
		sb.append("     */\n");
		sb.append("    Object evaluate");
		sb.append( methodSuffixMap.get(evaluationType));
		if (methodIndex > 0)
		{
			sb.append(methodIndex);
		}
		sb.append("(int id)\n");
		sb.append("    {\n");
		sb.append("        Object value = null;\n");
		sb.append("\n");
	}

	protected void writeMethodEnd(StringBuilder sb)
	{
		sb.append("\n");
		sb.append("        return value;\n");
		sb.append("    }\n");
		sb.append("\n");
		sb.append("\n");
	}

	protected void writeNextMethodCall(StringBuilder sb, byte evaluationType, int methodIndex, int startId)
	{
		sb.append("        if (id >= ");
		sb.append(startId);
		sb.append(")\n");
		sb.append("        {\n");
		sb.append("            value = evaluate");
		sb.append(methodSuffixMap.get(evaluationType));
		sb.append(methodIndex);
		sb.append("(id);\n");
		sb.append("        }\n");
		sb.append("        else ");
	}

	protected void writeExpression(StringBuilder expressionBuffer, JRExpression expression, byte evaluationType)
	{
		expressionBuffer.append("if (id == ");
		expressionBuffer.append(sourceTask.getExpressionId(expression));
		expressionBuffer.append(")\n");
		expressionBuffer.append("        {\n");
		expressionBuffer.append("            value = (");
		expressionBuffer.append(this.generateExpression(expression, evaluationType));
		expressionBuffer.append(");\n");
		expressionBuffer.append("        }\n");
	}

	/**
	 *
	 */
	private String generateExpression(
		JRExpression expression,
		byte evaluationType
		)
	{
		StringBuilder sb = new StringBuilder();

		JRExpressionChunk[] chunks = expression.getChunks();
		if (chunks != null && chunks.length > 0)
		{
			for (int i = 0; i < chunks.length; i++)
			{
				JRExpressionChunk chunk = chunks[i];

				String chunkText = chunk.getText();
				if (chunkText == null)
				{
					chunkText = "";
				}
				
				switch (chunk.getType())
				{
					case JRExpressionChunk.TYPE_TEXT :
					{
						sb.append(chunkText);
						break;
					}
					case JRExpressionChunk.TYPE_PARAMETER :
					{
						JRParameter jrParameter = parametersMap.get(chunkText);
	
						sb.append("(");
						if (!"java.lang.Object".equals(jrParameter.getValueClassName()))
						{
							sb.append("(");
							sb.append(jrParameter.getValueClassName());
							sb.append(")");
						}
						sb.append("parameter_");
						sb.append(JRStringUtil.getJavaIdentifier(chunkText));
						sb.append(".getValue())");
	
						break;
					}
					case JRExpressionChunk.TYPE_FIELD :
					{
						JRField jrField = fieldsMap.get(chunkText);

						sb.append("(");
						if (!"java.lang.Object".equals(jrField.getValueClassName()))
						{
							sb.append("(");
							sb.append(jrField.getValueClassName());
							sb.append(")");
						}
						sb.append("field_");
						sb.append(JRStringUtil.getJavaIdentifier(chunkText)); 
						sb.append(".get");
						sb.append(fieldPrefixMap.get(evaluationType)); 
						sb.append("Value())");
	
						break;
					}
					case JRExpressionChunk.TYPE_VARIABLE :
					{
						JRVariable jrVariable = variablesMap.get(chunkText);
	
						sb.append("(");
						if (!"java.lang.Object".equals(jrVariable.getValueClassName()))
						{
							sb.append("(");
							sb.append(jrVariable.getValueClassName());
							sb.append(")"); 
						}
						sb.append("variable_"); 
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
}
