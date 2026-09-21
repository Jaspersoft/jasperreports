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
package net.sf.jasperreports.engine.fill;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.xml.JRXmlTemplateLoader;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.ResourceInfo;
import net.sf.jasperreports.repo.ResourcePathKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Fill-time {@link JRReportTemplate} implementation.
 * <p/>
 * Used to evaluate template source expressions.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillReportTemplate implements JRReportTemplate
{

	private static final Log log = LogFactory.getLog(JRFillReportTemplate.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_TEMPLATE_SOURCE = "fill.report.template.unknown.template.source";
	
	private final JRReportTemplate parent;
	private final JRBaseFiller filler;
	
	public JRFillReportTemplate(JRReportTemplate template, JRBaseFiller filler, JRFillObjectFactory factory)
	{
		factory.put(template, this);
		
		parent = template;
		this.filler = filler;
	}
	
	@Override
	public JRExpression getSourceExpression()
	{
		return parent.getSourceExpression();
	}

	public ReportTemplateSource evaluate() throws JRException
	{
		ReportTemplateSource template;
		JRExpression sourceExpression = getSourceExpression();
		Object source = filler.evaluateExpression(sourceExpression, JRExpression.EVALUATION_DEFAULT);
		if (source == null)
		{
			template = null;
		}
		else
		{
			if (source instanceof JRTemplate)
			{
				template = ReportTemplateSource.of((JRTemplate) source);
			}
			else
			{
				template = loadTemplate(source, filler, filler.getRepositoryContext());
			}
		}
		return template;
	}

	protected static ReportTemplateSource loadTemplate(Object source, JRBaseFiller filler, 
			RepositoryContext repositoryContext) throws JRException
	{
		Object cacheKey = source;
		if (source instanceof String)
		{
			cacheKey = ResourcePathKey.inContext(repositoryContext, (String) source);
		}
		
		ReportTemplateSource templateSource;
		if (filler.fillContext.hasLoadedTemplate(cacheKey))
		{
			templateSource = filler.fillContext.getLoadedTemplate(cacheKey);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("Loading styles template from " + source);
			}
			
			if (source instanceof String)
			{
				ResourceInfo resourceInfo = RepositoryUtil.getInstance(repositoryContext).getResourceInfo((String) source);
				if (resourceInfo == null)
				{
					JRTemplate template = JRXmlTemplateLoader.getInstance(repositoryContext).loadTemplate((String) source);
					templateSource = ReportTemplateSource.of(template);
				}
				else
				{
					String resourceLocation = resourceInfo.getRepositoryResourceLocation();
					ResourcePathKey absoluteKey = ResourcePathKey.absolute(resourceLocation);
					if (filler.fillContext.hasLoadedTemplate(absoluteKey))
					{
						templateSource = filler.fillContext.getLoadedTemplate(absoluteKey);
					}
					else
					{
						JRTemplate template = JRXmlTemplateLoader.getInstance(repositoryContext).loadTemplate(resourceLocation);
						templateSource = ReportTemplateSource.of(template, resourceInfo);
						templateSource = filler.fillContext.registerLoadedTemplate(absoluteKey, templateSource);
					}
				}
			}
			else if (source instanceof File)
			{
				JRTemplate template = JRXmlTemplateLoader.getInstance(filler.getJasperReportsContext()).loadTemplate((File) source);
				templateSource = ReportTemplateSource.of(template);
			}
			else if (source instanceof URL)
			{
				JRTemplate template = JRXmlTemplateLoader.getInstance(filler.getJasperReportsContext()).loadTemplate((URL) source);
				templateSource = ReportTemplateSource.of(template);
			}
			else if (source instanceof InputStream)
			{
				JRTemplate template = JRXmlTemplateLoader.getInstance(filler.getJasperReportsContext()).loadTemplate((InputStream) source);
				templateSource = ReportTemplateSource.of(template);
			}
			else
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_TEMPLATE_SOURCE,  
						new Object[]{source.getClass().getName()} 
						);
			}
			
			templateSource = filler.fillContext.registerLoadedTemplate(cacheKey, templateSource);
		}
		return templateSource;
	}
	
}
