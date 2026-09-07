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
package xchart;

import java.util.Collections;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.component.ComponentsBundle;
import net.sf.jasperreports.engine.component.DefaultComponentManager;
import net.sf.jasperreports.engine.component.DefaultComponentsBundle;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;

/**
 * XChart component extensions registry factory.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class XChartExtensions implements ExtensionsRegistryFactory
{

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties)
	{
		DefaultComponentManager componentManager = new DefaultComponentManager();
		componentManager.setComponentCompiler(new XYChartCompiler());
		componentManager.setComponentFillFactory(new XYChartFillFactory());
		
		DefaultComponentsBundle components = new DefaultComponentsBundle();
		components.setComponentManagers(Collections.singletonMap(
				XYChartComponent.class, componentManager));
		
		return new SingletonExtensionRegistry<ComponentsBundle>(
				ComponentsBundle.class, components);
	}

}
