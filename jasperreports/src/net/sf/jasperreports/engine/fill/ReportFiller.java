/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.engine.fill;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.javaflow.api.continuable;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface ReportFiller
{

	@continuable
	JasperPrint fill(Map<String, Object> parameters, Connection connection) throws JRException;

	@continuable
	JasperPrint fill(Map<String, Object> parameters, Connection connection, int rowsToFill) throws JRException;

	@continuable
	JasperPrint fill(Map<String, Object> parameters, JRDataSource dataSource) throws JRException;

	@continuable
	JasperPrint fill(Map<String, Object> parameters, JRDataSource dataSource, int rowsToFill) throws JRException;

	@continuable
	JasperPrint fill(Map<String, Object> parameters) throws JRException;

	@continuable
	JasperPrint fill(Map<String, Object> parameters, int rowsToFill) throws JRException;

	void addFillListener(FillListener listener);

	void cancelFill() throws JRException;

	boolean isPageFinal(int pageIndex);

	JRFillContext getFillContext();

}
