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
package net.sf.jasperreports.engine.export;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.HyperlinkData;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class HyperlinkUtil 
{

	private static final Log log = LogFactory.getLog(HyperlinkUtil.class);

	protected static final Pattern LINK_PATTERN = Pattern.compile("\\{(link\\-[\\-\\w]+)\\}");
	protected static final int LINK_PARAM_NAME_GROUP = 1;
	

	/**
	 *
	 */
	public static Boolean getIgnoreHyperlink(String propName, JRPrintHyperlink link)
	{
		if (link != null)
		{
			Boolean hyperlinkVisible = null;
			if (link.getHyperlinkParameters() != null)
			{
				List<JRPrintHyperlinkParameter> parameters = link.getHyperlinkParameters().getParameters();
				if (parameters != null)
				{
					for (JRPrintHyperlinkParameter parameter : parameters)
					{
						if (propName.equals(parameter.getName()))
						{
							hyperlinkVisible = (Boolean)parameter.getValue();
							break;
						}
					}
				}
			}
			
			return hyperlinkVisible;
		}

		return null;
	}

	
	/**
	 * Returns the name of the parameter to be used for a hyperlink, as used
	 * by {@link #makeLinkPlaceholder(JRPrintHyperlink)} and
	 * {@link #resolveLinks(String, JRGenericPrintElement, JRHyperlinkProducer, boolean)}.
	 * 
	 * @param hyperlink the hyperlink
	 * @return the hyperlink parameter name
	 * @see #makeLinkPlaceholder(JRPrintHyperlink)
	 */
	public static String makeLinkParameterName(JRPrintHyperlink hyperlink)
	{
		return "link-" + System.identityHashCode(hyperlink);
	}

	
	/**
	 * Returns a placeholder to be used in a Flash variable for a hyperlink.
	 * 
	 * <p>
	 * This method uses <code>System.identityHashCode(hyperlink)</code> as link Id.
	 * </p>
	 * 
	 * @param hyperlink the hyperlink
	 * @return the link placeholder
	 * @see #makeLinkPlaceholder(String)
	 */
	public static String makeLinkPlaceholder(JRPrintHyperlink hyperlink)
	{
		String id = makeLinkParameterName(hyperlink);
		return makeLinkPlaceholder(id);
	}

	
	/**
	 * Returns a placeholder to be used in a Flash variable for a hyperlink.
	 * 
	 * <p>
	 * The placeholders will be resolved to links at export time by
	 * {@link #resolveLinks(String, JRGenericPrintElement, JRHyperlinkProducer, boolean)}.
	 * </p>
	 * 
	 * @param linkId the Id of the link, which needs to be used as hyperlink
	 * parameter name
	 * @return the link placeholder
	 */
	public static String makeLinkPlaceholder(String linkId)
	{
		return "{" + linkId + "}";
	}

	
	/**
	 * Resolves hyperlink placeholders to URLs in a Flash variable.
	 * 
	 * @param text the text in which hyperlink placeholders are to be replaced
	 * @param element the print element where hyperlink parameters will be looked for
	 * @param linkProducer the hyperlink producer which transforms hyperlink
	 * objects to String URLs
	 * @return the text with hyperlink placeholders replaced by URLs
	 * @see #makeLinkPlaceholder(String)
	 */
	public static String resolveLinks(String text,
			JRGenericPrintElement element,
			JRHyperlinkProducer linkProducer,
			boolean prepareForSerialization)
	{
		Matcher matcher = LINK_PATTERN.matcher(text);
		StringBuffer xml = new StringBuffer();
		List<HyperlinkData> hyperlinksData = new ArrayList<>();

		while (matcher.find())
		{
			String paramName = matcher.group(LINK_PARAM_NAME_GROUP);
			JRPrintHyperlink hyperlink = 
				(JRPrintHyperlink) element.getParameterValue(paramName);
			
			String replacement;
			if (hyperlink == null)
			{
				if (log.isWarnEnabled())
				{
					log.warn("Hyperlink parameter " + paramName 
							+ " not found in element");
				}
				
				replacement = null;
			}
			else
			{
				replacement = linkProducer.getHyperlink(hyperlink);
				if (prepareForSerialization) {
					String id = String.valueOf(hyperlink.hashCode() & 0x7FFFFFFF);

					HyperlinkData hyperlinkData = new HyperlinkData();
					hyperlinkData.setId(id);
					hyperlinkData.setHref(replacement);
					hyperlinkData.setHyperlink(hyperlink);
					hyperlinkData.setSelector("._jrHyperLink." + hyperlink.getLinkType());
					replacement = "jrhl-" + id + ";" + hyperlink.getLinkType();

					hyperlinksData.add(hyperlinkData);
				}
			}
			
			if (replacement == null)
			{
				replacement = "";
			}
			else
			{
				try
				{
					if (!prepareForSerialization) {
						replacement = URLEncoder.encode(replacement, "UTF-8");
					}
				}
				catch (UnsupportedEncodingException e)
				{
					throw new JRRuntimeException(e);
				}
			}
			
			matcher.appendReplacement(xml, replacement);
		}
		matcher.appendTail(xml);

		if (hyperlinksData.size() > 0) {
			element.setParameterValue("hyperlinksData", hyperlinksData);
		}
		
		return xml.toString();

	}

	
	private HyperlinkUtil()
	{
	}

}
