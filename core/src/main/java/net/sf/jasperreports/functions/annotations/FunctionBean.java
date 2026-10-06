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
package net.sf.jasperreports.functions.annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FunctionBean
{
	/**
	 * 
	 */
	private String id;
	private String name;
	private String description;
	private List<FunctionParameterBean> parameters = new ArrayList<>();
	private Class<?> returnType;
	
	/**
	 * 
	 */
	public FunctionBean(
		String id,	
		String name,	
		String description,	
		Class<?> returnType
		) 
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.returnType = returnType;
	}

	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public List<FunctionParameterBean> getParameters() {
		return parameters;
	}

	public void addParameter(FunctionParameterBean parameter) {
		parameters.add(parameter);
	}

	public Class<?> getReturnType() {
		return returnType;
	}
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
}
