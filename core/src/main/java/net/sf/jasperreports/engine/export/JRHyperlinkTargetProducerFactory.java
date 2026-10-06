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
 * An abstract factory of {@link JRHyperlinkTargetProducer hyperlink target producers}.
 * <p>
 * The factory is responsible for returning a hyperlink target producer for a
 * custom hyperlink type.
 * </p>
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRHyperlinkTargetProducerFactory
{

	/**
	 * Returns the hyperlink target producer associated with a specific hyperlink target.
	 * 
	 * @param linkTarget the hyperlink target
	 * @return an associated hyperlink target producer, or <code>null</code> when none associated
	 */
	public abstract JRHyperlinkTargetProducer getHyperlinkTargetProducer(String linkTarget);
	
	
	/**
	 * Generates the String hyperlink target for a hyperlink instance based on its
	 * type and on the associated hyperlink target producer.
	 * 
	 * @param hyperlink the hyperlink instance
	 * @return the genereated String hyperlink target
	 * @see JRHyperlinkTargetProducer#getHyperlinkTarget(JRPrintHyperlink)
	 */
	public String produceHyperlinkTarget(JRPrintHyperlink hyperlink)
	{
		String linkTarget = hyperlink.getLinkTarget();
		String target = null;
		if (linkTarget != null)
		{
			JRHyperlinkTargetProducer producer = getHyperlinkTargetProducer(linkTarget);
			if (producer != null)
			{
				target = producer.getHyperlinkTarget(hyperlink);
			}
		}
		return target;
	}

}
