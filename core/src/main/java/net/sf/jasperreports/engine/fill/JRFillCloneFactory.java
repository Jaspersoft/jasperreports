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

import java.util.HashMap;
import java.util.Map;


/**
 * Factory class used for fill-time working clones creation.
 * <p>
 * A instance of this class is used for each created clone.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillCloneFactory
{
	private Map<JRFillCloneable,JRFillCloneable> cloneMap;
	
	public JRFillCloneFactory()
	{
		cloneMap = new HashMap<>();
	}

	protected JRFillCloneable getCached(JRFillCloneable original)
	{
		return cloneMap.get(original);
	}

	public void put(JRFillCloneable original, JRFillCloneable clone)
	{
		cloneMap.put(original, clone);
	}

	public JRFillCloneable getClone(JRFillCloneable original)
	{
		JRFillCloneable clone;
		
		if (original == null)
		{
			clone = null;
		}
		else
		{
			clone = getCached(original);
			
			if (clone == null)
			{
				clone = original.createClone(this);
			}
		}

		return clone;
	}
}
