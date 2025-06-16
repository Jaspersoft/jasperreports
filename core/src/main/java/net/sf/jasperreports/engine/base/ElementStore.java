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
package net.sf.jasperreports.engine.base;

import java.util.function.Consumer;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface ElementStore extends VirtualizablePageElements
{
	int size();
	
	default int deepSize()
	{
		throw new UnsupportedOperationException();
	}

	JRPrintElement get(int index);

	boolean add(JRPrintElement element);

	boolean add(int index, JRPrintElement element);

	JRPrintElement set(int index, JRPrintElement element);

	JRPrintElement remove(int index);
	
	void dispose();

	void updatePage(JRVirtualPrintPage page);
	
	void updateContext(JRVirtualizationContext context, JRVirtualPrintPage page);

	void transferElements(Consumer<JRPrintElement> consumer);
}