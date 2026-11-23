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
package net.sf.jasperreports.okapi;

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentManager;
import net.sf.jasperreports.engine.component.ComponentsBundle;
import net.sf.jasperreports.engine.component.DefaultComponentManager;
import net.sf.jasperreports.engine.component.DefaultComponentsBundle;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;


/**
 * Registers the Okapi barcode components.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OkapiExtensionsRegistryFactory implements ExtensionsRegistryFactory
{

	private static final ExtensionsRegistry REGISTRY;

	static
	{
		DefaultComponentManager manager = new DefaultComponentManager();
		manager.setDesignConverter(new OkapiDesignConverter());
		manager.setComponentCompiler(new OkapiCompiler());
		manager.setComponentFillFactory(new OkapiFillFactory());

		// keyed on each concrete class, not on OkapiComponent: the JRXML parser registers
		// the component classes it finds as keys, and needs the ones that have a type name
		Map<Class<? extends Component>, ComponentManager> componentManagers = new HashMap<>();
		for (Class<? extends OkapiComponent> type : OkapiElementTypes.getComponentClasses())
		{
			componentManagers.put(type, manager);
		}

		DefaultComponentsBundle bundle = new DefaultComponentsBundle();
		bundle.setComponentManagers(componentManagers);

		REGISTRY = new SingletonExtensionRegistry<>(ComponentsBundle.class, bundle);
	}

	@Override
	public ExtensionsRegistry createRegistry(String registryId,
			JRPropertiesMap properties)
	{
		return REGISTRY;
	}
}
