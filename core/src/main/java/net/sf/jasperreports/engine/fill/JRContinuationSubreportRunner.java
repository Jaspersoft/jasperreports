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

import org.apache.commons.javaflow.api.Continuation;
import org.apache.commons.javaflow.api.continuable;

/**
 * Implemetation of {@link net.sf.jasperreports.engine.fill.JRSubreportRunner JRSubreportRunner}
 * using <a href="http://jakarta.apache.org/commons/sandbox/javaflow/">Javaflow</a> continuations.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRContinuationSubreportRunner extends JRSubreportRunnable implements JRSubreportRunner
{
	private Continuation continuation;

	public JRContinuationSubreportRunner(JRFillSubreport fillSubreport)
	{
		super(fillSubreport);
	}

	@Override
	public boolean isFilling()
	{
		return continuation != null;
	}

	@Override
	public JRSubreportRunResult start()
	{
		continuation = Continuation.startWith(this);
		return runResult();
	}

	@Override
	public JRSubreportRunResult resume()
	{
		continuation = continuation.resume(null);
		return runResult();
	}

	@Override
	public void reset()
	{
		continuation = null;
	}

	@Override
	public void cancel()
	{
	}

	@Override
	@continuable
	public void suspend()
	{
		Continuation.suspend();
	}

	@Override
	public void abort()
	{
		//NOP
	}
}
