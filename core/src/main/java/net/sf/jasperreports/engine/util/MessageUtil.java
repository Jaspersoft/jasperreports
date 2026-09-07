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
 * Gaganis Giorgos - gaganis@users.sourceforge.net
 */
package net.sf.jasperreports.engine.util;

import java.util.List;
import java.util.Locale;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class MessageUtil
{
	public static final String EXCEPTION_MESSAGE_KEY_MESSAGE_PROVIDER_NOT_FOUND = "util.message.provider.not.found";
	
	private final JasperReportsContext jasperReportsContext;
	
	/**
	 * 
	 */
	private MessageUtil(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 * 
	 */
	public static final MessageUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return new MessageUtil(jasperReportsContext);
	}
	
	/**
	 *
	 */
	public MessageProvider getMessageProvider(String name)
	{
		List<MessageProviderFactory> factories = jasperReportsContext.getExtensions(MessageProviderFactory.class);
		for (MessageProviderFactory factory : factories)
		{
			MessageProvider provider = factory.getMessageProvider(name);
			if (provider != null)
			{
				return provider;
			}
		}
		throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_MESSAGE_PROVIDER_NOT_FOUND,
				new Object[]{name});
	}
	
	/**
	 *
	 */
	public LocalizedMessageProvider getLocalizedMessageProvider(String name, Locale locale)
	{
		return new LocalizedMessageProvider(getMessageProvider(name), locale);
	}
}
