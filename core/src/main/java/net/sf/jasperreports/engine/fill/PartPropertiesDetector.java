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
package net.sf.jasperreports.engine.fill;

import java.util.List;
import java.util.function.BiConsumer;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.util.ElementalPropertiesHolder;
import net.sf.jasperreports.engine.util.UniformPrintElementVisitor;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PartPropertiesDetector extends UniformPrintElementVisitor<Void>
{

	private JRPropertiesUtil propertiesUtil;
	private BiConsumer<String, JRPropertiesHolder> partConsumer;

	public PartPropertiesDetector(JRPropertiesUtil propertiesUtil, 
			BiConsumer<String, JRPropertiesHolder> partConsumer)
	{
		super(true);
		this.propertiesUtil = propertiesUtil;
		this.partConsumer = partConsumer;
	}
	
	@Override
	protected void visitElement(JRPrintElement element, Void arg)
	{
		if (element.hasProperties())
		{
			String partName = element.getPropertiesMap().getProperty(PrintPart.ELEMENT_PROPERTY_PART_NAME);
			if (partName != null)
			{
				ElementalPropertiesHolder partProperties = new ElementalPropertiesHolder();
				propertiesUtil.transferProperties(element, partProperties, PrintPart.PROPERTIES_TRANSFER_PREFIX);
				partConsumer.accept(partName, partProperties);
			}
		}
	}

	public void detect(List<JRPrintElement> elements)
	{
		for (JRPrintElement element : elements)
		{
			element.accept(this, null);
		}
	}
}
