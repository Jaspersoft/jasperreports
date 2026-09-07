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

/*
 * Contributors:
 * Henri Chen - henrichen@users.sourceforge.net
 * Kees Kuip  - keeskuip@users.sourceforge.net
 */
package net.sf.jasperreports.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

import net.sf.jasperreports.engine.SimpleJasperReportsContext;


/**
 * Base class for JasperReports built-in Ant task implementations.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseAntTask extends MatchingTask
{

	/**
	 *
	 */
	protected SimpleJasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();
	
	protected int threads = 1;

	private boolean isError = false;
	
	protected synchronized void error()
	{
		this.isError = true;
	}
	
	protected boolean isError()
	{
		return isError;
	}
	
	/**
	 * Sets the number of threads to use for executing the task on multiple files in parallel.
	 * <p>
	 * The value should be a positive integer or a float number representing a multiplier of the number of CPU cores, when followed by the letter C.
	 * For example, 2C means twice the number of CPU cores, while 0.5C means half the number of CPU cores.
	 * The default is 1.
	 */
	public void setThreads(String threadsOption)
	{
		if (threadsOption.endsWith("C")) 
		{
			float multiplier = Float.parseFloat(threadsOption.substring(0, threadsOption.length() - 1));
			if (multiplier <= 0.0f)
			{
				throw new BuildException("The threads multiplier must be a positive float number followed by 'C'.");
			}
			threads = (int) (Runtime.getRuntime().availableProcessors() * multiplier);
			threads = threads == 0 ? 1 : threads;
		}
		else
		{
			threads = Integer.parseInt(threadsOption);
			if (threads <= 0)
			{
				throw new BuildException("The number of threads must be a positive integer or a float number followed by 'C' representing a multiplier of the number of CPU cores.");
			}
		}
	}

}
