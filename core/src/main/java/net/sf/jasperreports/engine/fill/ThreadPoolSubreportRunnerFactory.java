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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ThreadPoolSubreportRunnerFactory implements JRSubreportRunnerFactory
{
	private static final Log log = LogFactory.getLog(ThreadPoolSubreportRunnerFactory.class);
	
	private static final String THREAD_POOL_KEY = "net.sf.jasperreports.engine.fill.JRThreadSubreportRunner.ThreadPool";

	@Override
	public JRSubreportRunner createSubreportRunner(JRFillSubreport fillSubreport, JRBaseFiller subreportFiller)
	{
		JRFillContext fillContext = subreportFiller.getFillContext();
		ExecutorServiceDisposable executor = fillContext.getFillCache(THREAD_POOL_KEY, () ->
		{
			ExecutorService threadExecutor = createThreadExecutor(fillContext);
			return new ExecutorServiceDisposable(threadExecutor);
		});

		return new ThreadExecutorSubreportRunner(fillSubreport, subreportFiller, 
				executor.getExecutorService());
	}

	protected ExecutorService createThreadExecutor(JRFillContext fillContext)
	{
		SubreportsThreadFactory threadFactory = new SubreportsThreadFactory(fillContext);
		ExecutorService threadExecutor = Executors.newCachedThreadPool(threadFactory);
		if (log.isDebugEnabled())
		{
			log.debug("created subreports thread executor " + threadExecutor 
					+ " for " + fillContext.getMasterFiller().getJasperReport().getName());
		}
		return threadExecutor;
	}
	
	protected static class ExecutorServiceDisposable implements JRFillContext.FillCacheDisposable
	{
		private final ExecutorService executorService;
		
		public ExecutorServiceDisposable(ExecutorService executorService)
		{
			this.executorService = executorService;
		}
		
		public ExecutorService getExecutorService()
		{
			return executorService;
		}

		@Override
		public void dispose()
		{
			if (log.isDebugEnabled())
			{
				log.debug("shutting down " + executorService);
			}
			
			executorService.shutdownNow();
		}
	}
	
	protected static class SubreportsThreadFactory implements ThreadFactory
	{
		private final JRFillContext fillContext;
		private final AtomicInteger threadCount;
		
		public SubreportsThreadFactory(JRFillContext fillContext)
		{
			this.fillContext = fillContext;
			this.threadCount = new AtomicInteger();
		}

		@Override
		public Thread newThread(Runnable r)
		{
			String threadName = fillContext.getMasterFiller().getJasperReport().getName() 
					+ " subreports #" + threadCount.incrementAndGet();
			Thread thread = new Thread(r, threadName);
			if (log.isDebugEnabled())
			{
				log.debug("created thread " + thread);
			}
			return thread;
		}
		
	}
}
