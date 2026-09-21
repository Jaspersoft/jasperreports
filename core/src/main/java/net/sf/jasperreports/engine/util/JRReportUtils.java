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

import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JRReportUtils
{
	private static final String EXCEPTION_MESSAGE_KEY_REPORT_SUBDATASET_NOT_FOUND = "util.report.subdataset.not.found";
	
	public static JRDataset findSubdataset(JRDatasetRun datasetRun, JRReport report)
	{
		return findSubdataset(datasetRun.getDatasetName(), report);
	}


	public static JRDataset findSubdataset(String datasetName, JRReport report)
	{
		JRDataset reportDataset = getSubdataset(datasetName, report);
		
		if (reportDataset == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_REPORT_SUBDATASET_NOT_FOUND,
					new Object[]{datasetName, report.getName()});
		}
		return reportDataset;
	}


	/**
	 * Returns the report subdataset with the given name, or <code>null</code> if the report does
	 * not have such a subdataset. Unlike {@link #findSubdataset(String, JRReport)}, this method
	 * tolerates a missing subdataset, which is useful before the report design gets verified.
	 */
	public static JRDataset getSubdataset(String datasetName, JRReport report)
	{
		JRDataset[] datasets = report.getDatasets();
		JRDataset reportDataset = null;
		if (datasetName != null && datasets != null)
		{
			for (int i = 0; i < datasets.length; i++)
			{
				if (datasetName.equals(datasets[i].getName()))
				{
					reportDataset = datasets[i];
					break;
				}
			}
		}
		return reportDataset;
	}


	/**
	 * @deprecated Replaced by {@link #findSubdataset(JRDatasetRun, JRReport)}.
	 */
	public static JRDataset findSubdataset(JRDatasetRun datasetRun, JasperReport report)
	{
		return findSubdataset(datasetRun, (JRReport)report);
	}

	
	private JRReportUtils()
	{
	}
}
