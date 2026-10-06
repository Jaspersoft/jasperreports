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
package net.sf.jasperreports.data.http;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import net.sf.jasperreports.dataadapters.http.HttpDataLocation;
import net.sf.jasperreports.dataadapters.http.RequestMethod;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */

@JsonTypeName("httpDataLocation")
public class StandardHttpDataLocation implements HttpDataLocation
{

	private RequestMethod method;
	private String url;
	private String username;
	private String password;
	private List<HttpLocationParameter> urlParameters;
	private String body;
	private List<HttpLocationParameter> postParameters;
	private List<HttpLocationParameter> headers;

	@Override
	public RequestMethod getMethod()
	{
		return method;
	}

	public void setMethod(RequestMethod method)
	{
		this.method = method;
	}
	
	@Override
	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	@Override
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	@Override
	public Object clone()
	{
		try
		{
			StandardHttpDataLocation clone = (StandardHttpDataLocation) super.clone();
			clone.urlParameters = JRCloneUtils.cloneList(urlParameters);
			clone.postParameters = JRCloneUtils.cloneList(postParameters);
			clone.headers = JRCloneUtils.cloneList(headers);
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			// should not happen
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public List<HttpLocationParameter> getUrlParameters()
	{
		return urlParameters;
	}

	public void setUrlParameters(List<HttpLocationParameter> urlParameters)
	{
		this.urlParameters = urlParameters;
	}

	@Override
	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	@Override
	public List<HttpLocationParameter> getPostParameters()
	{
		return postParameters;
	}

	public void setPostParameters(List<HttpLocationParameter> postParameters)
	{
		this.postParameters = postParameters;
	}

	@Override
	public List<HttpLocationParameter> getHeaders()
	{
		return headers;
	}

	public void setHeaders(List<HttpLocationParameter> headers)
	{
		this.headers = headers;
	}
	
}
