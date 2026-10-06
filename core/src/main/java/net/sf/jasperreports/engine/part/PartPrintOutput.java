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

import java.util.Collection;

import net.sf.jasperreports.engine.BookmarkHelper;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.fill.DelayedFillActions;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PartPrintOutput
{

	void startPart(PrintPart part, FillingPrintPart fillingPart);

	void addPage(JRPrintPage page, DelayedFillActions delayedActions);

	JRPrintPage getPage(int pageIndex);
	
	void pageUpdated(int partPageIndex);
	
	void append(FillPartPrintOutput output);
	
	BookmarkHelper getBookmarkHelper();

	void addStyles(Collection<JRStyle> styles);

	void addOrigins(Collection<JROrigin> origins);
	
}
