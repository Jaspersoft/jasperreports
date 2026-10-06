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
package net.sf.jasperreports.engine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DefaultedMessageProvider implements MessageProvider
{
	
	private static final Log log = LogFactory.getLog(DefaultedMessageProvider.class);

	public static MessageProvider wrap(MessageProvider wrapped, String defaultsResource)
	{
		Properties defaults = loadResourceProperties(defaultsResource);
		return defaults == null ? wrapped : new DefaultedMessageProvider(wrapped, defaults);
	}

	protected static Properties loadResourceProperties(String defaultsResource)
	{
		InputStream in = JRLoader.getResourceInputStream(defaultsResource);
		Properties defaults = null;
		if (in != null)
		{
			try
			{
				defaults = new Properties();
				defaults.load(in);
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
			finally
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					log.debug("Failed to close stream for " + defaultsResource, e);
				}
			}
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("Did not find default messages resource " + defaultsResource);
			}
		}
		return defaults;
	}
	
	private final MessageProvider wrapped;
	private final Properties defaultsMessages;
	
	public DefaultedMessageProvider(MessageProvider wrapped, Properties defaultsMessages)
	{
		this.defaultsMessages = defaultsMessages;
		this.wrapped = wrapped;
	}
	
	@Override
	public String getMessage(String code, Object[] args, Locale locale)
	{
		String message;
		try
		{
			message = wrapped.getMessage(code, args, locale);
		}
		catch (MissingResourceException e)//FIXME add a JR exception type?
		{
			message = defaultsMessages.getProperty(code);//not applying args for now
			if (message == null && log.isDebugEnabled())
			{
				log.debug("Message not found for " + code);
				//returning null
			}
		}
		return message;
	}

}
