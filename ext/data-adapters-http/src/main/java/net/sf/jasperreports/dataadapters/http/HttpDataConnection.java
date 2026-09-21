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
package net.sf.jasperreports.dataadapters.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import net.sf.jasperreports.dataadapters.DataFileConnection;
import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class HttpDataConnection implements DataFileConnection
{
	
	private static final Log log = LogFactory.getLog(HttpDataConnection.class);
	private static final String EXCEPTION_MESSAGE_KEY_NO_RESPONSE = "data.http.no.response";
	private static final String EXCEPTION_MESSAGE_KEY_STATUS_CODE_ERROR = "data.http.status.code.error";

	private final CloseableHttpClient httpClient;
	private final HttpRequestBase request;
	private CloseableHttpResponse response;
	
	public HttpDataConnection(CloseableHttpClient httpClient, HttpRequestBase request)
	{
		this.httpClient = httpClient;
		this.request = request;
	}

	@Override
	public InputStream getInputStream()
	{
		try
		{
			response = httpClient.execute(request);
			StatusLine status = response.getStatusLine();
			if (log.isDebugEnabled())
			{
				log.debug("HTTP response status " + status);
			}
			
			HttpEntity entity = response.getEntity();
			if (entity == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_NO_RESPONSE,
						(Object[])null);
			}
			
			if (status.getStatusCode() >= 300)
			{
				EntityUtils.consumeQuietly(entity);
				//FIXME include request URI in the exception?  that might be a security issue
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_STATUS_CODE_ERROR,
						new Object[]{status});
			}
			
			return entity.getContent();
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		} 
	}

	@Override
	public void dispose()
	{
		if (response != null)
		{
			try
			{
				response.close();
			}
			catch (IOException e)
			{
				if (log.isWarnEnabled())
				{
					log.warn("Error closing HTTP response", e);
				}
			}
		}
		
		try
		{
			httpClient.close();
		}
		catch (IOException e)
		{
			if (log.isWarnEnabled())
			{
				log.warn("Error closing HTTP client", e);
			}
		}
	}

}
