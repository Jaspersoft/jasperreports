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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.data.http.HttpLocationParameter;
import net.sf.jasperreports.dataadapters.AbstractDataAdapterService;
import net.sf.jasperreports.dataadapters.DataFileConnection;
import net.sf.jasperreports.dataadapters.DataFileService;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.util.SecretsUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class HttpDataService implements DataFileService
{
	
	private static final Log log = LogFactory.getLog(HttpDataService.class);
	
	public static final String HTTP_DATA_SERVICE_NAME = "net.sf.jasperreports.data.file.service:HTTP";
	
	private static final String EXCEPTION_MESSAGE_KEY_NO_HTTP_URL_SET = "data.http.no.http.url.set";
	private static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_REQUEST_METHOD = "data.http.unknown.request.method";
	
	/**
	 * @deprecated Replaced by {@link #PROPERTY_URL}.
	 */
	public static final String PARAMETER_URL = "HTTP_DATA_URL";
	
	/**
	 * @deprecated Replaced by {@link #PROPERTY_USERNAME}.
	 */
	public static final String PARAMETER_USERNAME = "HTTP_DATA_USERNAME";
	
	/**
	 * @deprecated Replaced by {@link #PROPERTY_PASSWORD}.
	 */
	public static final String PARAMETER_PASSWORD = "HTTP_DATA_PASSWORD";
	
	/**
	 * @deprecated Replaced by {@link #PROPERTY_URL_PARAMETER}.
	 */
	public static final String PARAMETER_PREFIX_URL_PARAMETER = "HTTP_DATA_URL_PARAMETER_";
	
	/**
	 * @deprecated Replaced by {@link #PROPERTY_POST_PARAMETER}.
	 */
	public static final String PARAMETER_PREFIX_POST_PARAMETER = "HTTP_DATA_POST_PARAMETER_";
	
	/**
	 * Property that specifies the HTTP request method to be used by the HTTP data adapters. 
	 * When used at parameter level, it does not need to provide a value, but is just used to mark the parameter that will provide the HTTP method.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.DATASET, PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = RequestMethod.class
	)
	public static final String PROPERTY_METHOD = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.method";
	
	/**
	 * Property that specifies the base URL to be used by the HTTP data adapters. 
	 * When used at parameter level, it does not need to provide a value, but is just used to mark the parameter that will provide the URL value.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.DATASET, PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_URL = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.url";

	/**
	 * Property that specifies the user name to be used by the HTTP data adapters with basic authentication. 
	 * When used at parameter level, it does not need to provide a value, but is just used to mark the parameter that will provide the user name value.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.DATASET, PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_USERNAME = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.username";

	/**
	 * Property that specifies the password to be used by the HTTP data adapters with basic authentication. 
	 * When used at parameter level, it does not need to provide a value, but is just used to mark the parameter that will provide the user password value.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.DATASET, PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_PASSWORD = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.password";

	/**
	 * Property that specifies the name of the request parameter to be added to the URL when HTTP data adapter is used.
	 * If the property is present, but has no value, the name of the request parameter is the same as the report parameter name.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_URL_PARAMETER = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.url.parameter";

	/**
	 * Property that specifies the POST/PUT request body to be sent when HTTP data adapter is used.
	 * When used at parameter level, it does not need to provide a value, but is just used to mark the parameter that will provide the POST/PUT request body value.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.DATASET, PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_BODY = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.body";

	/**
	 * Property that specifies the name of the request POST parameter to be sent when HTTP data adapter is used.
	 * If the property is present, but has no value, the name of the request parameter is the same as the report parameter name.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_POST_PARAMETER = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.post.parameter";

	/**
	 * Property that specifies the name of the request header to be sent when HTTP data adapter is used.
	 * If the property is present, but has no value, the name of the request header is the same as the report parameter name.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.PARAMETER},
			scopeQualifications = {HTTP_DATA_SERVICE_NAME},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_HEADER = JRPropertiesUtil.PROPERTY_PREFIX + "http.data.header";

	private final ParameterContributorContext context;
	
	private final HttpDataLocation dataLocation;
	
	public HttpDataService(ParameterContributorContext context, HttpDataLocation dataLocation)
	{
		this.context = context;
		this.dataLocation = dataLocation;
	}

	@Override
	public DataFileConnection getDataFileConnection(Map<String, Object> parameters) throws JRException
	{
		CloseableHttpClient httpClient = createHttpClient(parameters);
		HttpRequestBase request = createRequest(parameters);
		return new HttpDataConnection(httpClient, request);
	}

	protected CloseableHttpClient createHttpClient(Map<String, Object> parameters)
	{
		HttpClientBuilder clientBuilder = HttpClients.custom();
		
		// single connection
		BasicHttpClientConnectionManager connManager = new BasicHttpClientConnectionManager();
		clientBuilder.setConnectionManager(connManager);
		
		// ignore cookies for now
		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		clientBuilder.setDefaultRequestConfig(requestConfig);
		
		setAuthentication(parameters, clientBuilder);
		
		CloseableHttpClient client = clientBuilder.build();
		return client;
	}

	protected void setAuthentication(Map<String, Object> parameters, HttpClientBuilder clientBuilder)
	{
		String username = getUsername(parameters);
		if (username != null)
		{
			String password = getPassword(parameters);
			
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			//FIXME proxy authentication?
			credentialsProvider.setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), 
					new UsernamePasswordCredentials(username, password));
			clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
		}
	}

	protected String getUsername(Map<String, Object> parameters)
	{
		String username = getPropertyOrParameterValue(PROPERTY_USERNAME, PARAMETER_USERNAME, parameters);
		if (username == null)
		{
			username = dataLocation.getUsername();
		}
		return username;
	}

	protected String getPassword(Map<String, Object> parameters)
	{
		String password = getPropertyOrParameterValue(PROPERTY_PASSWORD, PARAMETER_PASSWORD, parameters);
		if (password == null)
		{
			password = dataLocation.getPassword();
		}
		
		if (password != null)
		{
			SecretsUtil secrets = SecretsUtil.getInstance(context.getJasperReportsContext());
			password = secrets.getSecret(AbstractDataAdapterService.SECRETS_CATEGORY, password);
		}
		
		return password;
	}

	protected HttpRequestBase createRequest(Map<String, Object> parameters)
	{
		URI requestURI = getRequestURI(parameters);

		RequestMethod method = getMethod(parameters);
		String body = getBody(parameters);
		List<NameValuePair> postParameters = collectPostParameters(parameters);

		if (method == null)
		{
			method = (body == null && postParameters.isEmpty()) ? RequestMethod.GET : RequestMethod.POST;
		}
		HttpRequestBase request;
		switch (method)
		{
		case GET:
			if (body != null)
			{
				log.warn("Ignoring request body for GET request to " + dataLocation.getUrl());
			}
			if (!postParameters.isEmpty())
			{
				log.warn("Ignoring POST parameters for GET request to " + dataLocation.getUrl());
			}
			request = createGetRequest(requestURI);
			break;
		case POST:
			if (body == null)
			{
				request = createPostRequest(requestURI, postParameters);
			}
			else
			{
				if (!postParameters.isEmpty())
				{
					log.warn("Ignoring POST parameters for POST request having request body to " + dataLocation.getUrl());
				}
				request = createPostRequest(requestURI, body);
			}
			break;
		case PUT:
			if (body == null)
			{
				request = createPutRequest(requestURI, postParameters);
			}
			else
			{
				if (!postParameters.isEmpty())
				{
					log.warn("Ignoring POST parameters for PUT request having request body to " + dataLocation.getUrl());
				}
				request = createPutRequest(requestURI, body);
			}
			break;
		default:
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNKNOWN_REQUEST_METHOD,
					new Object[]{method});
		}
		
		List<NameValuePair> headers = collectHeaders(parameters);
		if (headers != null)
		{
			for (NameValuePair header : headers)
			{
				request.addHeader(header.getName(), header.getValue());
			}
		}
		
		return request;
	}

	protected HttpGet createGetRequest(URI requestURI)
	{
		HttpGet httpGet = new HttpGet(requestURI);
		return httpGet;
	}

	protected HttpPost createPostRequest(URI requestURI, String body)
	{
		HttpPost httpPost = new HttpPost(requestURI);
		HttpEntity entity = createRequestEntity(body);
		httpPost.setEntity(entity);
		return httpPost;
	}

	protected HttpPost createPostRequest(URI requestURI, List<NameValuePair> postParameters)
	{
		HttpPost httpPost = new HttpPost(requestURI);
		HttpEntity entity = createRequestEntity(postParameters);
		httpPost.setEntity(entity);
		return httpPost;
	}

	protected HttpPut createPutRequest(URI requestURI, String body)
	{
		HttpPut httpPost = new HttpPut(requestURI);
		HttpEntity entity = createRequestEntity(body);
		httpPost.setEntity(entity);
		return httpPost;
	}

	protected HttpPut createPutRequest(URI requestURI, List<NameValuePair> postParameters)
	{
		HttpPut httpPost = new HttpPut(requestURI);
		HttpEntity entity = createRequestEntity(postParameters);
		httpPost.setEntity(entity);
		return httpPost;
	}

	protected HttpEntity createRequestEntity(String body)
	{
		return new StringEntity(body, "UTF-8");//allow custom?
	}

	protected HttpEntity createRequestEntity(List<NameValuePair> postParameters)
	{
		UrlEncodedFormEntity formEntity;
		try
		{
			formEntity = new UrlEncodedFormEntity(postParameters, "UTF-8");//allow custom?
		}
		catch (UnsupportedEncodingException e)
		{
			// should not happen
			throw new JRRuntimeException(e);
		}
		return formEntity;
	}

	protected List<NameValuePair> collectUrlParameters(Map<String, Object> reportParameters)
	{
		return collectParameters(dataLocation.getUrlParameters(), reportParameters, PROPERTY_URL_PARAMETER, PARAMETER_PREFIX_URL_PARAMETER);
	}

	protected List<NameValuePair> collectPostParameters(Map<String, Object> reportParameters)
	{
		return collectParameters(dataLocation.getPostParameters(), reportParameters, PROPERTY_POST_PARAMETER, PARAMETER_PREFIX_POST_PARAMETER);
	}
	
	protected List<NameValuePair> collectHeaders(Map<String, Object> reportParameters)
	{
		return collectParameters(dataLocation.getHeaders(), reportParameters, PROPERTY_HEADER, null);
	}

	protected List<NameValuePair> collectParameters(
		List<HttpLocationParameter> dataAdapterParameters,
		Map<String, Object> parameterValues, 
		String propertyName,
		String parameterPrefix
		)
	{
		List<NameValuePair> postParameters = new ArrayList<>();
		
		Map<String, String> requestParamMappings = new HashMap<>();
		if (
			context.getDataset() != null
			&& context.getDataset().getParameters() != null
			)
		{
			for (JRParameter parameter : context.getDataset().getParameters())
			{
				if (
					parameter.hasProperties()
					&& parameter.getPropertiesMap().containsProperty(propertyName)
					)
				{
					String requestParamName = parameter.getPropertiesMap().getProperty(propertyName);
					if (requestParamName == null)
					{
						requestParamName = parameter.getName();
					}
					requestParamMappings.put(requestParamName, parameter.getName());
				}
			}
		}
		
		if (dataAdapterParameters != null && !dataAdapterParameters.isEmpty())
		{
			for (HttpLocationParameter dataAdapterParameter : dataAdapterParameters)
			{
				String dataAdapterParamName = dataAdapterParameter.getName();
				String paramValue = dataAdapterParameter.getValue();
				if (paramValue != null)
				{
					String prefixConventionParamName = parameterPrefix == null ? null : (parameterPrefix + dataAdapterParamName);
					String mappedParamName = requestParamMappings.get(dataAdapterParamName);
					if (
						(prefixConventionParamName != null 
							&& parameterValues.containsKey(prefixConventionParamName))
						|| (requestParamMappings.containsKey(dataAdapterParamName)
							&& parameterValues.containsKey(mappedParamName))
						)
					{
						if (log.isDebugEnabled())
						{
							log.debug("data adapter parameter " + dataAdapterParamName + " overridden by the report");
						}
					}
					else
					{
						if (log.isDebugEnabled())
						{
							log.debug("adding parameter " + dataAdapterParamName + " with value " + paramValue);
						}
						postParameters.add(new BasicNameValuePair(dataAdapterParamName, paramValue));
					}
				}
			}
		}
		
		if (parameterPrefix != null)
		{
			for (Entry<String, Object> paramEntry : parameterValues.entrySet())
			{
				String paramName = paramEntry.getKey();
				Object value = paramEntry.getValue();
				if (paramName.startsWith(parameterPrefix))
				{
					String requestParamName = paramName.substring(parameterPrefix.length(), paramName.length());
					String mappedParamName = requestParamMappings.get(requestParamName);
					if (
						value != null
						&& (!requestParamMappings.containsKey(requestParamName)
							|| !parameterValues.containsKey(mappedParamName))
						)
					{
						String paramValue = toHttpParameterValue(value);
						if (log.isDebugEnabled())
						{
							log.debug("adding parameter " + requestParamName + " with value " + paramValue);
						}
						postParameters.add(new BasicNameValuePair(requestParamName, paramValue));
					}
					else
					{
						if (log.isDebugEnabled())
						{
							log.debug("prefix convention parameter " + requestParamName + " overridden by the property mapped parameter");
						}
					}
				}
			}
		}
		
		if (
			context.getDataset() != null
			&& context.getDataset().getParameters() != null
			)
		{
			// here, loop through dataset parameters and not through requestParamMappings because we want to support request parameter arrays
			// by allowing multiple dataset parameters to provide value for the same request parameter
			for (JRParameter parameter : context.getDataset().getParameters())
			{
				if (
					parameter.hasProperties()
					&& parameter.getPropertiesMap().containsProperty(propertyName)
					)
				{
					String requestParamName = parameter.getPropertiesMap().getProperty(propertyName);
					if (requestParamName == null)
					{
						requestParamName = parameter.getName();
					}
					
					String paramName = parameter.getName();
					
					if (parameterValues.containsKey(paramName))
					{
						Object value = parameterValues.get(paramName);
						String paramValue = toHttpParameterValue(value);
						if (log.isDebugEnabled())
						{
							log.debug("adding parameter " + requestParamName + " with value " + paramValue);
						}
						postParameters.add(new BasicNameValuePair(requestParamName, paramValue));
					}
				}
			}
		}
		
		return postParameters;
	}

	protected URI getRequestURI(Map<String, Object> parameters)
	{
		String url = getURL(parameters);
		if (url == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_NO_HTTP_URL_SET,
					(Object[])null);
		}
		
		try
		{
			URIBuilder uriBuilder = new URIBuilder(url);
			List<NameValuePair> urlParameters = collectUrlParameters(parameters);
			if (!urlParameters.isEmpty())
			{
				uriBuilder.addParameters(urlParameters);
			}
			
			URI uri = uriBuilder.build();
			if (log.isDebugEnabled())
			{
				log.debug("request URI " + uri);
			}
			return uri;
		}
		catch (URISyntaxException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	protected String getURL(Map<String, Object> parameters)
	{
		String url = getPropertyOrParameterValue(PROPERTY_URL, PARAMETER_URL, parameters);
		if (url == null)
		{
			url = dataLocation.getUrl();
		}
		return url;
	}

	protected RequestMethod getMethod(Map<String, Object> parameters)
	{
		String method = getPropertyOrParameterValue(PROPERTY_METHOD, null, parameters);
		return method != null ? RequestMethod.valueOf(method.toUpperCase()) : dataLocation.getMethod();
	}
	
	protected String getBody(Map<String, Object> parameters)
	{
		String body = getPropertyOrParameterValue(PROPERTY_BODY, null, parameters);
		if (body == null)
		{
			body = dataLocation.getBody();
		}
		return body;
	}

	protected String getPropertyOrParameterValue(String propName, String paramName, Map<String, Object> parameterValues)
	{
		String value = null;

		JRDataset dataset = context.getDataset();

		if (dataset != null && dataset.hasProperties())
		{
			value = JRPropertiesUtil.getOwnProperty(dataset, propName);
		}

		if (paramName != null && parameterValues.containsKey(paramName))//FIXMEDATAADAPTER should we fallback to prop name used as param name?
		{
			value = (String) parameterValues.get(paramName);
		}
		
		if (dataset != null)
		{
			JRParameter[] parameters = dataset.getParameters();
			if (parameters != null)
			{
				for (JRParameter parameter : parameters)
				{
					if (
						parameter.hasProperties()
						&& parameter.getPropertiesMap().containsProperty(propName) 
						&& parameterValues.containsKey(parameter.getName()) 
						)
					{
						value = (String)parameterValues.get(parameter.getName());
					}
				}
			}
		}
		
		return value;
	}

	protected String toHttpParameterValue(Object value)
	{
		return String.valueOf(value);//FIXME do something smarter than String.valueOf
	}
}
