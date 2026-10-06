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

import net.sf.jasperreports.engine.JRPrintHyperlink;


/**
 * An abstract factory of {@link JRHyperlinkProducer hyperlink producers}.
 * <p>
 * The factory is responsible for returning a hyperlink producer for a
 * custom hyperlink type.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRHyperlinkProducerFactory //FIXMENOW why would somebody need anything else than the DefaultHyperlinkProducerFactory, which is based on extensions?
{

	/**
	 * Returns the hyperlink producer associated with a specific hyperlink type.
	 * 
	 * @param linkType the hyperlink type
	 * @return an associated hyperlink producer, or <code>null</code> when none associated
	 */
	public abstract JRHyperlinkProducer getHandler(String linkType);
	
	
	/**
	 * Generates the String hyperlink for a hyperlink instance based on its
	 * type and on the associated hyperlink producer.
	 * 
	 * @param hyperlink the hyperlink instance
	 * @return the genereated String hyperlink
	 * @see JRHyperlinkProducer#getHyperlink(JRPrintHyperlink)
	 */
	public String produceHyperlink(JRPrintHyperlink hyperlink)
	{
		String linkType = hyperlink.getLinkType();
		String href = null;
		if (linkType != null)
		{
			JRHyperlinkProducer producer = getHandler(linkType);
			if (producer != null)
			{
				href = producer.getHyperlink(hyperlink);
			}
		}
		return href;
	}

}
