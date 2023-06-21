/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
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
package net.sf.jasperreports.view;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;

import aQute.bnd.annotation.spi.ServiceConsumer;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @author stefan Bischof
 */
@ServiceConsumer(JRSaveContributorFactory.class)
public class SaveContributorUtils {

	private SaveContributorUtils() {}
	
	public static List<JRSaveContributor> createBuiltinContributors(JasperReportsContext context, Locale locale,
			ResourceBundle resourceBundle) {
		return ServiceLoader.load(JRSaveContributorFactory.class)
				.stream()
				.map(Provider<JRSaveContributorFactory>::get)
				.map(factory -> factory.create(context, locale, resourceBundle))
				.toList();
	}

}
