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
package net.sf.jasperreports.engine.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

import javax.swing.SwingUtilities;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * Utility methods for running code on the Swing event dispatch thread.
 */
public final class SwingUtil
{

	private SwingUtil()
	{
	}

	/**
	 * Runs an action synchronously on the event dispatch thread.
	 */
	public static void runOnEventDispatchThread(Runnable action)
	{
		runOnEventDispatchThread(() ->
		{
			action.run();
			return null;
		});
	}

	/**
	 * Runs an action synchronously on the event dispatch thread and returns its result.
	 */
	public static <T> T runOnEventDispatchThread(Supplier<T> action)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			return action.get();
		}

		FutureTask<T> task = new FutureTask<>(action::get);
		SwingUtilities.invokeLater(task);
		try
		{
			return task.get();
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw new JRRuntimeException(e);
		}
		catch (ExecutionException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof RuntimeException)
			{
				throw (RuntimeException) cause;
			}
			if (cause instanceof Error)
			{
				throw (Error) cause;
			}
			throw new JRRuntimeException(cause);
		}
	}
}
