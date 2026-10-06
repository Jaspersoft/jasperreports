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
package net.sf.jasperreports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.BiConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PrintSerializer implements BiConsumer<Report, JasperPrint>
{

	private static final Log log = LogFactory.getLog(PrintSerializer.class);
	
	private static final PrintSerializer INSTANCE_NO_VIRTUALIZER = new PrintSerializer("serialized", new NoVirtualizerContainer());
	
	public static PrintSerializer instance()
	{
		return INSTANCE_NO_VIRTUALIZER;
	}

	private String checkName;
	private VirtualizerContainer virtualizerContainer;
	
	/**
	 * @param checkName name of the digest check, distinguishes its output file from the ones of other checks of the report
	 * @param virtualizerContainer provides the virtualizer used to load the serialized report
	 */
	public PrintSerializer(String checkName, VirtualizerContainer virtualizerContainer)
	{
		this.checkName = checkName;
		this.virtualizerContainer = virtualizerContainer;
	}
	
	@Override
	public void accept(Report report, JasperPrint print)
	{
		try
		{
			if (log.isDebugEnabled())
			{
				log.debug("Serializing report " + report.getJRXML());
			}
			
			ByteArrayOutputStream printOut = new ByteArrayOutputStream();
			JRSaver.saveObject(print, printOut);
			
			try
			{
				JRVirtualizer virtualizer = virtualizerContainer.getVirtualizer();
				if (log.isDebugEnabled())
				{
					log.debug("Loading serialized report " + report.getJRXML() + " with virtualizer " + virtualizer);
				}
				
				JasperPrint savedPrint = JRLoader.loadJasperPrint(new ByteArrayInputStream(printOut.toByteArray()), virtualizer);
				virtualizerContainer.setReadOnly();
				report.checkDigest(savedPrint, checkName);
			}
			finally
			{
				virtualizerContainer.dispose();
			}
		}
		catch (JRException e)
		{
			throw new RuntimeException(e);
		}
	}

}
