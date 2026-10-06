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
package net.sf.jasperreports.data.ds;

import com.fasterxml.jackson.annotation.JsonRootName;

import net.sf.jasperreports.dataadapters.AbstractClasspathAwareDataAdapter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */

@JsonRootName(value = "dsDataAdapter")
public class DataSourceDataAdapterImpl extends AbstractClasspathAwareDataAdapter implements DataSourceDataAdapter
{
	private String factoryClass;
	private String methodToCall;
	
	@Override
	public String getFactoryClass() {
		return factoryClass;
	}

	@Override
	public void setFactoryClass(String factoryClass) {
		this.factoryClass = factoryClass;
	}

	@Override
	public String getMethodToCall() {
		return methodToCall;
	}

	@Override
	public void setMethodToCall(String methodToCall) {
		this.methodToCall = methodToCall;
	}
}
