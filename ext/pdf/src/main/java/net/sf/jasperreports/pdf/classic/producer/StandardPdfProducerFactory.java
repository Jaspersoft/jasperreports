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
package net.sf.jasperreports.pdf.classic.producer;

import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.common.PdfProducer;
import net.sf.jasperreports.pdf.common.PdfProducerContext;
import net.sf.jasperreports.pdf.common.PdfProducerFactory;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPdfProducerFactory implements PdfProducerFactory
{

	protected static boolean fontsRegistered;

	public StandardPdfProducerFactory()
	{
		registerFonts();
	}

	@Override
	public PdfProducer createProducer(PdfProducerContext context)
	{
		return new StandardPdfProducer(context);
	}

	protected static synchronized void registerFonts ()
	{
		if (!fontsRegistered)
		{
			JRPropertiesUtil properties = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance());
			List<PropertySuffix> fontFiles = properties.getProperties(JRPdfExporter.PDF_FONT_FILES_PREFIX);//FIXMECONTEXT no default here and below
			if (!fontFiles.isEmpty())
			{
				for (JRPropertiesUtil.PropertySuffix font : fontFiles)
				{
					String file = font.getValue();
					if (file.toLowerCase().endsWith(".ttc"))
					{
						FontFactory.register(file);
					}
					else
					{
						String alias = font.getSuffix();
						FontFactory.register(file, alias);
					}
				}
			}

			List<PropertySuffix> fontDirs = properties.getProperties(JRPdfExporter.PDF_FONT_DIRS_PREFIX);
			if (!fontDirs.isEmpty())
			{
				for (JRPropertiesUtil.PropertySuffix dir : fontDirs)
				{
					FontFactory.registerDirectory(dir.getValue());
				}
			}

			fontsRegistered = true;
		}
	}

}
