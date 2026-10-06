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
package net.sf.jasperreports.barcode4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Barcode4JExtensionsRegistryFactory implements ExtensionsRegistryFactory
{
	
	public static final List<Class<? extends Component>> BARCODE4J_COMPONENT_TYPES = Collections.unmodifiableList(Arrays.asList(
			CodabarComponent.class, 
			Code128Component.class, 
			EAN128Component.class, 
			DataMatrixComponent.class, 
			Code39Component.class, 
			Interleaved2Of5Component.class,
			UPCAComponent.class, 
			UPCEComponent.class, 
			EAN13Component.class, 
			EAN8Component.class, 
			USPSIntelligentMailComponent.class, 
			RoyalMailCustomerComponent.class, 
			POSTNETComponent.class, 
			PDF417Component.class, 
			QRCodeComponent.class));
	
	private static final ExtensionsRegistry REGISTRY;
	
	static
	{
		final DefaultComponentsBundle bundle = new DefaultComponentsBundle();
		
		HashMap<Class<? extends Component>, ComponentManager> componentManagers = new HashMap<>();
		
		DefaultComponentManager barcode4jManager = new DefaultComponentManager();
		barcode4jManager.setDesignConverter(new BarcodeDesignConverter());
		barcode4jManager.setComponentCompiler(new BarcodeCompiler());
		barcode4jManager.setComponentFillFactory(new BarcodeFillFactory());
		for (Class<? extends Component> type : BARCODE4J_COMPONENT_TYPES)
		{
			componentManagers.put(type, barcode4jManager);
		}
		
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
