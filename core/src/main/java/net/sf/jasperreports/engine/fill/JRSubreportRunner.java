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
package net.sf.jasperreports.engine.fill;

import org.apache.commons.javaflow.api.continuable;

import net.sf.jasperreports.engine.JRException;


/**
 * Subreport runner interface.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRSubreportRunner
{
	/**
	 * Decides whether the subreport filling has ended or not.
	 * 
	 * @return <code>true</code> if and only if the subreport filling has not ended
	 */
	boolean isFilling();

	/**
	 * Starts to fill the subreport.
	 * <p>
	 * This method is always called by a thread owning the lock on the subreport filler.
	 * 
	 * @return the result of the fill process
	 * @throws JRException
	 */
	JRSubreportRunResult start() throws JRException;

	/**
	 * Resumes the filling of a subreport.
	 * <p>
	 * This method is called after the fill has been suspended by
	 * {@link #suspend() suspend} and the subreport should continue on the new page. 
	 * <p>
	 * This method is always called by a thread owning the lock on the subreport filler.
	 * 
	 * @return the result of the fill process
	 * @throws JRException
	 */
	JRSubreportRunResult resume() throws JRException;

	/**
	 * Resets the runner, preparing it for a new fill.
	 * 
	 * @throws JRException
	 */
	void reset() throws JRException;

	/**
	 * Cancels the current fill process.
	 * <p>
	 * This method is called when a subreport is placed on a non splitting band
	 * and needs to rewind.
	 * <p>
	 * This method is always called by a thread owning the lock on the subreport filler.
	 * 
	 * @throws JRException
	 */
	void cancel() throws JRException;

	/**
	 * Suspends the current fill.
	 * <p>
	 * This method is called when the subreport reaches the end of a page
	 * and needs to wait for the master to create a new page.
	 * <p>
	 * This method is always called by a thread owning the lock on the subreport filler.
	 * 
	 * @throws JRException
	 */
	@continuable
	void suspend() throws JRException;	
	
	/**
	 * Aborts the current fill.
	 */
	void abort();
}
