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
package net.sf.jasperreports.engine.part;

import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.JRVerifier;

/**
 * Responsible with handling a part componet during report compile.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PartComponentCompiler
{

	/**
	 * Collects report expressions from a component.
	 * 
	 * @param component the component
	 * @param collector the expression collector
	 */
	void collectExpressions(PartComponent component, JRExpressionCollector collector);

	/**
	 * Logically verifies a component.
	 * 
	 * @param component the component
	 * @param verifier the verifier object, which can be used to raise validation
	 * errors
	 * @see JRVerifier#getCurrentComponentElement()
	 */
	void verify(PartComponent component, JRVerifier verifier);

	/**
	 * Provides a "compiled" component instance that will be included in the
	 * compiled report.
	 * 
	 * @param component the component from the design report
	 * @param baseFactory the factory of base/compiled report elements
	 * @return a component instance that is to be included in the compiled
	 * report
	 */
	PartComponent toCompiledComponent(PartComponent component, JRBaseObjectFactory baseFactory);

}
