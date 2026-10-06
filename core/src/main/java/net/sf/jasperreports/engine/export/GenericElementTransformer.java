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
package net.sf.jasperreports.engine.export;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;

/**
 * A generic print element transformer.
 * 
 * <p>
 * Such transformers can be used to preprocess a filled report prior to export
 * by translating generic print elements into other print element types or into
 * generic print elements of a different type.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see GenericElementTransformer
 */
public interface GenericElementTransformer extends GenericElementHandler
{

	/**
	 * Transforms a generic print element into another element.
	 * 
	 * @param context the transformation context
	 * @param element the element to transform
	 * @return a print element obtained by transforming the element provided
	 * as argument
	 */
	JRPrintElement transformElement(GenericElementTransformerContext context, 
			JRGenericPrintElement element);
	
}
