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
package net.sf.jasperreports.engine.design;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.base.JRBaseGroup;
import net.sf.jasperreports.engine.type.BandTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignGroup extends JRBaseGroup
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_COUNT_VARIABLE = "countVariable";
	
	public static final String PROPERTY_EXPRESSION = "expression";
	
	public static final String PROPERTY_GROUP_FOOTER = "groupFooter";
	
	public static final String PROPERTY_GROUP_HEADER = "groupHeader";
	
	public static final String PROPERTY_NAME = "name";


	/**
	 *
	 */
	public JRDesignGroup() 
	{
		groupHeaderSection = new JRDesignSection(new JROrigin(null, getName(), BandTypeEnum.GROUP_HEADER));
		groupFooterSection = new JRDesignSection(new JROrigin(null, getName(), BandTypeEnum.GROUP_FOOTER));
	}
	
	/**
	 *
	 */
	public void setName(String name)
	{
		Object old = this.name;
		this.name = name;
		updateSectionOrigins();
		getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
	}

	/**
	 *
	 */
	public void setExpression(JRExpression expression)
	{
		Object old = this.expression;
		this.expression = expression;
		getEventSupport().firePropertyChange(PROPERTY_EXPRESSION, old, this.expression);
	}
		
	/**
	 *
	 */
	public void setCountVariable(JRVariable countVariable)
	{
		Object old = this.countVariable;
		this.countVariable = countVariable;
		getEventSupport().firePropertyChange(PROPERTY_COUNT_VARIABLE, old, this.countVariable);
	}

	protected void setSectionOrigin(JRSection section, BandTypeEnum type)
	{
		if (section instanceof JRDesignSection)
		{
			JROrigin origin = new JROrigin(null, getName(), type);
			((JRDesignSection) section).setOrigin(origin);
		}
	}
	
	protected void setBandOrigin(JRBand band, BandTypeEnum type)
	{
		if (band instanceof JRDesignBand)
		{
			JROrigin origin = new JROrigin(null, getName(), type);
			((JRDesignBand) band).setOrigin(origin);
		}
	}
	
	protected void updateSectionOrigins()
	{
		setSectionOrigin(getGroupHeaderSection(), BandTypeEnum.GROUP_HEADER);
		setSectionOrigin(getGroupFooterSection(), BandTypeEnum.GROUP_FOOTER);
	}

}
