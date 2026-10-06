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

import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Thread-based {@link net.sf.jasperreports.engine.fill.JRSubreportRunner JRSubreportRunner}
 * implementation.
 * <p>
 * The subreport fill is launched in a new thread which coordinates suspend/resume actions with
 * the master thread.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ThreadExecutorSubreportRunner extends AbstractThreadSubreportRunner
{
	
	private static final Log log = LogFactory.getLog(ThreadExecutorSubreportRunner.class);

	private Executor threadExecutor;
	private boolean filling;
	
	public ThreadExecutorSubreportRunner(JRFillSubreport fillSubreport, JRBaseFiller subreportFiller,
			Executor threadExecutor)
	{
		super(fillSubreport, subreportFiller);
		this.threadExecutor = threadExecutor;
	}

	@Override
	public boolean isFilling()
	{
		return filling;
	}

	@Override
	protected void doStart()
	{
		filling = true;
		
		if (log.isDebugEnabled())
		{
			log.debug("Fill " + subreportFiller.fillerId + ": starting");
		}
		
		threadExecutor.execute(this);
	}

	@Override
	public void reset()
	{
		filling = false;
	}
}
