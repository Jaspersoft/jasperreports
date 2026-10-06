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
package net.sf.jasperreports.extensions;

import net.sf.jasperreports.components.iconlabel.IconLabelElement;
import net.sf.jasperreports.components.iconlabel.IconLabelElementCsvHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementDocxHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementGraphics2DHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementHtmlHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementOdsHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementOdtHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementPptxHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementRtfHandler;
import net.sf.jasperreports.components.iconlabel.IconLabelElementXlsxHandler;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.export.GenericElementHandler;
import net.sf.jasperreports.engine.export.GenericElementHandlerBundle;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.DefaultQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle;
import net.sf.jasperreports.engine.scriptlets.DefaultScriptletFactory;
import net.sf.jasperreports.engine.scriptlets.ScriptletFactory;
import net.sf.jasperreports.engine.util.MessageProviderFactory;
import net.sf.jasperreports.engine.util.ResourceBundleMessageProviderFactory;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.xml.JacksonReportLoader;
import net.sf.jasperreports.engine.xml.JacksonReportWriterFactory;
import net.sf.jasperreports.engine.xml.ReportLoader;
import net.sf.jasperreports.engine.xml.ReportWriterFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DefaultExtensionsRegistryFactory implements ExtensionsRegistryFactory
{
	private static final GenericElementHandlerBundle HANDLER_BUNDLE = 
		new GenericElementHandlerBundle()
		{
			@Override
			public String getNamespace()
			{
				return JRXmlConstants.JASPERREPORTS_NAMESPACE;
			}
			
			@Override
			public GenericElementHandler getHandler(String elementName,
					String exporterKey)
			{
				if (IconLabelElement.ELEMENT_NAME.equals(elementName))
				{
					switch (exporterKey)
					{
						case JRGraphics2DExporter.GRAPHICS2D_EXPORTER_KEY :
						{
							return IconLabelElementGraphics2DHandler.getInstance();
						}
						case HtmlExporter.HTML_EXPORTER_KEY:
						{
							return IconLabelElementHtmlHandler.getInstance();
						}		
						case JRCsvExporter.CSV_EXPORTER_KEY:
						{
							return IconLabelElementCsvHandler.getInstance();
						}		
						case JRXlsxExporter.XLSX_EXPORTER_KEY:
						{
							return IconLabelElementXlsxHandler.getInstance();
						}		
						case JRDocxExporter.DOCX_EXPORTER_KEY:
						{
							return IconLabelElementDocxHandler.getInstance();
						}		
						case JRPptxExporter.PPTX_EXPORTER_KEY:
						{
							return IconLabelElementPptxHandler.getInstance();
						}		
						case JROdsExporter.ODS_EXPORTER_KEY:
						{
							return IconLabelElementOdsHandler.getInstance();
						}		
						case JROdtExporter.ODT_EXPORTER_KEY:
						{
							return IconLabelElementOdtHandler.getInstance();
						}		
						case JRRtfExporter.RTF_EXPORTER_KEY:
						{
							return IconLabelElementRtfHandler.getInstance();
						}		
	//					else if (JRXmlExporter.XML_EXPORTER_KEY.equals(exporterKey))
	//					{
	//						return IconLabelElementXmlHandler.getInstance();
	//					}
					}
				}
				
				return null;
			}
		};

	private static final ExtensionsRegistry REGISTRY; 

	static
	{
		ListExtensionsRegistry registry = new ListExtensionsRegistry();
		registry.add(JRQueryExecuterFactoryBundle.class, DefaultQueryExecuterFactoryBundle.getInstance());
		registry.add(ScriptletFactory.class, DefaultScriptletFactory.getInstance());
		registry.add(GenericElementHandlerBundle.class, HANDLER_BUNDLE);
		registry.add(MessageProviderFactory.class, ResourceBundleMessageProviderFactory.getInstance());
		registry.add(ReportLoader.class, JacksonReportLoader.instance());
		registry.add(ReportWriterFactory.class, JacksonReportWriterFactory.instance());
	
		REGISTRY = registry;
	}

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties) 
	{
		return REGISTRY;
	}
}
