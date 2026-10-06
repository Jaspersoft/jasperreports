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
public class JRThreadSubreportRunner extends AbstractThreadSubreportRunner
{
	
	private static final Log log = LogFactory.getLog(JRThreadSubreportRunner.class);
	
	private Thread fillThread;
	
	public JRThreadSubreportRunner(JRFillSubreport fillSubreport, JRBaseFiller subreportFiller)
	{
		super(fillSubreport, subreportFiller);
	}

	@Override
	public boolean isFilling()
	{
		return fillThread != null;
	}

	@Override
	protected void doStart()
	{
		fillThread = new Thread(this, subreportFiller.getJasperReport().getName() + " subreport filler");
		
		if (log.isDebugEnabled())
		{
			log.debug("Fill " + subreportFiller.fillerId + ": starting thread " + fillThread);
		}
		
		fillThread.start();
	}

	@Override
	public void reset()
	{
		fillThread = null;
	}

}
