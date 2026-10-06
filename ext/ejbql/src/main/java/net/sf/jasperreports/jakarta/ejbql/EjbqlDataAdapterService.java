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
package net.sf.jasperreports.jakarta.ejbql;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.persistence.Persistence;
import net.sf.jasperreports.data.ejbql.EjbqlDataAdapter;
import net.sf.jasperreports.dataadapters.AbstractDataAdapterService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.ParameterContributorContext;
import net.sf.jasperreports.engine.query.EjbqlConstants;
import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * @author Veaceslav Chicu (schicu@users.sourceforge.net)
 */
public class EjbqlDataAdapterService extends AbstractDataAdapterService {
	private static final Log log = LogFactory
			.getLog(EjbqlDataAdapterService.class);
	private Object em = null;

	/**
	 * 
	 */
	public EjbqlDataAdapterService(ParameterContributorContext paramContribContext, EjbqlDataAdapter jsonDataAdapter) 
	{
		super(paramContribContext, jsonDataAdapter);
	}

	public EjbqlDataAdapter getEjbqlDataAdapter() {
		return (EjbqlDataAdapter) getDataAdapter();
	}

	@Override
	public void contributeParameters(Map<String, Object> parameters)
			throws JRException {
		EjbqlDataAdapter hbmDA = getEjbqlDataAdapter();
		if (hbmDA != null) {
			try {
				String punitname = hbmDA.getPersistanceUnitName();

				Class<?> clazz = JRClassLoader
						.loadClassForRealName(Persistence.class.getName());
				Object emf = clazz.getMethod("createEntityManagerFactory",
						String.class).invoke(null, punitname);
				em = emf.getClass()
						.getMethod("createEntityManager", new Class[] {})
						.invoke(emf, new Object[] {});

				parameters.put(
						EjbqlConstants.PARAMETER_JPA_ENTITY_MANAGER,
						em);
			} catch (IllegalArgumentException | SecurityException | IllegalAccessException 
					| InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
				throw new JRException(e);
			}
		}
	}

	@Override
	public void dispose() {
		if (em != null) {
			try {
				em.getClass().getMethod("close", new Class[] {})
						.invoke(em, new Object[] {});
			} catch (Exception ex) {
				if (log.isErrorEnabled())
					log.error("Error while closing the connection.", ex);
			}
		}
	}
}
