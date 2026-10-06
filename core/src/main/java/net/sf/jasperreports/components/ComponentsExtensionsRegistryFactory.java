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
package net.sf.jasperreports.components;

import java.util.HashMap;

import net.sf.jasperreports.components.iconlabel.IconLabelComponent;
import net.sf.jasperreports.components.iconlabel.IconLabelComponentCompiler;
import net.sf.jasperreports.components.iconlabel.IconLabelComponentDesignConverter;
import net.sf.jasperreports.components.iconlabel.IconLabelComponentFillFactory;
import net.sf.jasperreports.components.list.FillListFactory;
import net.sf.jasperreports.components.list.ListComponent;
import net.sf.jasperreports.components.list.ListComponentCompiler;
import net.sf.jasperreports.components.list.ListDesignConverter;
import net.sf.jasperreports.components.table.FillTableFactory;
import net.sf.jasperreports.components.table.TableCompiler;
import net.sf.jasperreports.components.table.TableComponent;
import net.sf.jasperreports.components.table.TableDesignConverter;
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
 * Extension registry factory that includes built-in component element
 * implementations.
 * 
 * <p>
 * This registry factory is registered by default in JasperReports.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see ListComponent
 */
public class ComponentsExtensionsRegistryFactory implements
		ExtensionsRegistryFactory
{
	
	public static final String LIST_COMPONENT_NAME = "list";
	public static final String TABLE_COMPONENT_NAME = "table";
	public static final String ICONLABEL_COMPONENT_NAME = "iconLabel";
	
	private static final ExtensionsRegistry REGISTRY;
	
	static
	{
		final DefaultComponentsBundle bundle = new DefaultComponentsBundle();
		
		HashMap<Class<? extends Component>, ComponentManager> componentManagers = new HashMap<>();
		
		DefaultComponentManager listManager = new DefaultComponentManager();
		listManager.setDesignConverter(new ListDesignConverter());
		listManager.setComponentCompiler(new ListComponentCompiler());
		listManager.setComponentFillFactory(new FillListFactory());
		componentManagers.put(ListComponent.class, listManager);
		
		DefaultComponentManager tableManager = new DefaultComponentManager();
		tableManager.setDesignConverter(new TableDesignConverter());
		tableManager.setComponentCompiler(new TableCompiler());
		tableManager.setComponentFillFactory(new FillTableFactory());
		componentManagers.put(TableComponent.class, tableManager);
		
		DefaultComponentManager iconLabelManager = new DefaultComponentManager();
		iconLabelManager.setDesignConverter(IconLabelComponentDesignConverter.getInstance());
		iconLabelManager.setComponentCompiler(new IconLabelComponentCompiler());
		iconLabelManager.setComponentFillFactory(new IconLabelComponentFillFactory());
		componentManagers.put(IconLabelComponent.class, iconLabelManager);

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
