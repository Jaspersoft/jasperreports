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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.base.JRBasePen;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;


/**
 * Line element information shared by multiple print line objects.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @see JRTemplatePrintLine
 */
public class JRTemplateLine extends JRTemplateGraphicElement
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	protected LineDirectionEnum direction;

	/**
	 *
	 */
	protected JRTemplateLine(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider, JRLine line)
	{
		super(origin, defaultStyleProvider);

		setLine(line);
	}

	
	/**
	 * Creates a template line.
	 * 
	 * @param origin the origin of the elements that will use this template
	 * @param defaultStyleProvider the default style provider to use for
	 * this template
	 */
	public JRTemplateLine(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider)
	{
		super(origin, defaultStyleProvider);
		
		this.linePen = new JRBasePen(this);
	}


	/**
	 *
	 */
	protected void setLine(JRLine line)
	{
		super.setGraphicElement(line);

		setDirection(line.getDirection());
	}

	/**
	 * 
	 */
	public LineDirectionEnum getDirection()
	{
		return this.direction;
	}

	/**
	 * 
	 */
	public void setDirection(LineDirectionEnum direction)
	{
		this.direction = direction;
	}


	@Override
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		addGraphicHash(hash);
		hash.add(direction);
		return hash.getHashCode();
	}


	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRTemplateLine))
		{
			return false;
		}
		
		JRTemplateLine template = (JRTemplateLine) object;
		return graphicIdentical(template) && ObjectUtils.equals(direction, template.direction);
	}

}
