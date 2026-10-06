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
package net.sf.jasperreports.engine.export;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRConstants;


/**
 * Map-based hyperlink target producer factory implementation.
 * <p>
 * This implementation wraps a hyperlink target to hyperling target producer instance
 * association map.
 * </p>
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRHyperlinkTargetProducerMapFactory extends JRHyperlinkTargetProducerFactory implements Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private Map<String,JRHyperlinkTargetProducer> producers;
	
	
	/**
	 * Creates a blank factory.
	 */
	public JRHyperlinkTargetProducerMapFactory()
	{
		producers = new HashMap<>();
	}

	
	/**
	 * Exposes the target to producer association map.
	 * 
	 * @return the target to producer association map
	 */
	public Map<String,JRHyperlinkTargetProducer> getProducersMap()
	{
		return producers;
	}

	
	/**
	 * Sets the target to producer association map.
	 *
	 * @param producers bulk target to producer association map
	 * @see #getProducersMap()
	 */
	public void setProducersMap(Map<String,JRHyperlinkTargetProducer> producers)
	{
		this.producers = producers;
	}
	
	
	/**
	 * Adds a hyperlink target producer instance associated to a hyperlink target.
	 *  
	 * @param linkTarget the target
	 * @param producer the producer
	 */
	public void addProducer(String linkTarget, JRHyperlinkTargetProducer producer)
	{
		producers.put(linkTarget, producer);
	}
	
	
	/**
	 * Removes a target to producer association.
	 * 
	 * @param linkTarget the hyperlink target
	 * @return the producer which was associated to the target, if any
	 */
	public JRHyperlinkTargetProducer removeProducer(String linkTarget)
	{
		return producers.remove(linkTarget);
	}
	
	
	@Override
	public JRHyperlinkTargetProducer getHyperlinkTargetProducer(String linkTarget)
	{
		return producers.get(linkTarget);
	}

}
