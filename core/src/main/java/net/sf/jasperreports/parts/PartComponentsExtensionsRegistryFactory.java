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
package net.sf.jasperreports.parts;

import java.util.HashMap;

import net.sf.jasperreports.components.list.ListComponent;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.part.DefaultPartComponentManager;
import net.sf.jasperreports.engine.part.DefaultPartComponentsBundle;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartComponentManager;
import net.sf.jasperreports.engine.part.PartComponentsBundle;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;
import net.sf.jasperreports.parts.subreport.FillSubreportPartFactory;
import net.sf.jasperreports.parts.subreport.SubreportPartComponent;
import net.sf.jasperreports.parts.subreport.SubreportPartComponentCompiler;

/**
 * Extension registry factory that includes built-in part component implementations.
 * 
 * <p>
 * This registry factory is registered by default in JasperReports.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see ListComponent
 */
public class PartComponentsExtensionsRegistryFactory implements
		ExtensionsRegistryFactory
{
	
	public static final String SUBREPORT_PART_COMPONENT_NAME = "subreportPart";
	
	private static final ExtensionsRegistry REGISTRY;
	
	static
	{
		final DefaultPartComponentsBundle bundle = new DefaultPartComponentsBundle();
		
		HashMap<Class<? extends PartComponent>, PartComponentManager> componentManagers = new HashMap<>();
		
		DefaultPartComponentManager reportManager = new DefaultPartComponentManager();
		reportManager.setComponentCompiler(new SubreportPartComponentCompiler());
		reportManager.setComponentFillFactory(new FillSubreportPartFactory());
		componentManagers.put(SubreportPartComponent.class, reportManager);
		
		bundle.setComponentManagers(componentManagers);
		
		REGISTRY = new SingletonExtensionRegistry<>(PartComponentsBundle.class, bundle);
	}
	
	@Override
	public ExtensionsRegistry createRegistry(String registryId,
			JRPropertiesMap properties)
	{
		return REGISTRY;
	}

}
