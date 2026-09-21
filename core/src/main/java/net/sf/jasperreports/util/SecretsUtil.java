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
package net.sf.jasperreports.util;

import java.util.List;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class SecretsUtil
{
	private static final String EXCEPTION_MESSAGE_KEY_SECRET_NOT_FOUND = "util.secret.not.found";
	
	private final JasperReportsContext jasperReportsContext;
	
	/**
	 * 
	 */
	private SecretsUtil(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 * 
	 */
	public static final SecretsUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return new SecretsUtil(jasperReportsContext);
	}
	
	/**
	 *
	 */
	public String getSecret(String category, String key)
	{
		List<SecretsProviderFactory> factories = jasperReportsContext.getExtensions(SecretsProviderFactory.class);
		for (SecretsProviderFactory factory : factories)
		{
			SecretsProvider provider = factory.getSecretsProvider(category);
			if (provider != null && provider.hasSecret(key))
			{
				return provider.getSecret(key);
			}
		}
		throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_SECRET_NOT_FOUND,
				new Object[]{key, category});
	}
}
