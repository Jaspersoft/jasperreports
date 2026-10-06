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
package net.sf.jasperreports.j2ee.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.data.hibernate.HibernateDataAdapter;
import net.sf.jasperreports.dataadapters.AbstractClasspathAwareDataAdapterService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.query.HibernateConstants;
import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * @author Veaceslov Chicu (schicu@users.sourceforge.net)
 */
public class HibernateDataAdapterService extends AbstractClasspathAwareDataAdapterService {
	private static final Log log = LogFactory
			.getLog(HibernateDataAdapterService.class);
	private Object session;

	/**
	 * 
	 */
	public HibernateDataAdapterService(ParameterContributorContext paramContribContext, HibernateDataAdapter jsonDataAdapter) 
	{
		super(paramContribContext, jsonDataAdapter);
	}

	public HibernateDataAdapter getHibernateDataAdapter() {
		return (HibernateDataAdapter) getDataAdapter();
	}

	@Override
	public void contributeParameters(Map<String, Object> parameters)
			throws JRException {
		HibernateDataAdapter hbmDA = getHibernateDataAdapter();
		if (hbmDA != null) {
			ClassLoader oldThreadClassLoader = Thread.currentThread().getContextClassLoader();
			
			try {
				Thread.currentThread().setContextClassLoader(getClassLoader(oldThreadClassLoader));		
				
				Class<?> clazz = null;
				if (!hbmDA.isUseAnnotation()) {
					clazz = JRClassLoader.loadClassForRealName("org.hibernate.cfg.Configuration");
				}
				else {
					clazz = JRClassLoader.loadClassForRealName("org.hibernate.cfg.AnnotationConfiguration");
				}
				if (clazz != null) {
					Object configure = clazz.getDeclaredConstructor().newInstance();
					if (configure != null) {
						String xmlFileName = hbmDA.getXMLFileName();
						if (xmlFileName != null && !xmlFileName.isEmpty()) {
							File file = new File(xmlFileName);
							clazz.getMethod("configure", file.getClass())
									.invoke(configure, file);
						} else {
							clazz.getMethod("configure", new Class[] {})
									.invoke(configure, new Object[] {});
						}
						String pFileName = hbmDA.getPropertiesFileName();
						if (pFileName != null && !pFileName.isEmpty()) {
							Properties propHibernate = new Properties();
							propHibernate.load(new FileInputStream(pFileName));

							clazz.getMethod("setProperties",
									propHibernate.getClass()).invoke(configure,
									propHibernate);
						}
						

						Object bsf = clazz.getMethod("buildSessionFactory",
								new Class[] {}).invoke(configure,
								new Object[] {});
						session = bsf.getClass()
								.getMethod("openSession", new Class[] {})
								.invoke(bsf, new Object[] {});
						session.getClass()
								.getMethod("beginTransaction", new Class[] {})
								.invoke(session, new Object[] {});
						parameters
								.put(HibernateConstants.PARAMETER_HIBERNATE_SESSION,
										session);
					}
				}
			} catch (IOException | ClassNotFoundException | InstantiationException 
				| IllegalAccessException | IllegalArgumentException | SecurityException 
				| InvocationTargetException | NoSuchMethodException e) {
				throw new JRException(e);
			} finally {
				Thread.currentThread().setContextClassLoader(oldThreadClassLoader);
			}
		}
	}

	@Override
	public void dispose() {
		if (session != null) {
			try {
				session.getClass().getMethod("close", new Class[] {})
						.invoke(session, new Object[] {});
			} catch (Exception ex) {
				if (log.isErrorEnabled())
					log.error("Error while closing the connection.", ex);
			}
		}
	}
}
