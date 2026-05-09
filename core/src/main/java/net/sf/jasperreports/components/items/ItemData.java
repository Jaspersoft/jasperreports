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
package net.sf.jasperreports.components.items;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;

/**
 * The ItemData interface
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"dataset",
	"items"
	})
@JsonDeserialize(as = StandardItemData.class)
public interface ItemData extends JRCloneable
{

	/**
	 * Returns a list of {@link Item Item} 
	 * objects. Each item in the list provides a collection of item properties.
	 * 
	 * @return a list of items
	 * 
	 * @see Item
	 * @see ItemProperty
	 */
	@JacksonXmlProperty(localName = "item")
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<Item> getItems();
	
	/**
	 * Returns the dataset information that will be used by the {@link ItemData ItemData} object.
	 * <p>
	 * This information is required either to use the main dataset or to instantiate a 
	 * subdataset from the report. The data produced by the subdataset is fed to the items list.
	 * 
	 * @return the dataset
	 * @see net.sf.jasperreports.engine.JRElementDataset
	 */
	@JsonDeserialize(as = JRDesignElementDataset.class)
	public JRElementDataset getDataset();

}
