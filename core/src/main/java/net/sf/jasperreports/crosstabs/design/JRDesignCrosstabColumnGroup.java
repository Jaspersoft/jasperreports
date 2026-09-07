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
package net.sf.jasperreports.crosstabs.design;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.type.CrosstabColumnPositionEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * Crosstab column group implementation to be used for report designing. 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignCrosstabColumnGroup extends JRDesignCrosstabGroup implements JRCrosstabColumnGroup
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_HEIGHT = "height";

	public static final String PROPERTY_POSITION = "position";

	public static final String PROPERTY_CROSSTAB_HEADER = "crosstabHeader";

	protected int height;
	protected CrosstabColumnPositionEnum position;
	protected JRCellContents crosstabHeader;

	
	/**
	 * Creates a column group.
	 */
	public JRDesignCrosstabColumnGroup()
	{
		super();
	}

	@Override
	public CrosstabColumnPositionEnum getPosition()
	{
		return position;
	}
	
	
	/**
	 * Sets the header contents stretch position.
	 * 
	 * @param position the header contents stretch position
	 * @see JRCrosstabColumnGroup#getPosition()
	 */
	public void setPosition(CrosstabColumnPositionEnum position)
	{
		Object old = this.position;
		this.position = position;
		getEventSupport().firePropertyChange(PROPERTY_POSITION, old, this.position);
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	
	/**
	 * Sets the header cell height.
	 * 
	 * @param height the height
	 * @see JRCrosstabColumnGroup#getHeight()
	 */
	public void setHeight(int height)
	{
		int old = this.height;
		this.height = height;
		getEventSupport().firePropertyChange(PROPERTY_HEIGHT, old, this.height);
	}

	@Override
	public void setHeader(JRDesignCellContents header)
	{
		super.setHeader(header);
		
		setCellOrigin(this.header, 
				new JRCrosstabOrigin(getParent(), JRCrosstabOrigin.TYPE_COLUMN_GROUP_HEADER,
						null, getName()));
	}

	@Override
	public void setTotalHeader(JRDesignCellContents totalHeader)
	{
		super.setTotalHeader(totalHeader);
		
		setCellOrigin(this.totalHeader, 
				new JRCrosstabOrigin(getParent(), JRCrosstabOrigin.TYPE_COLUMN_GROUP_TOTAL_HEADER,
						null, getName()));
	}

	@Override
	void setParent(JRDesignCrosstab parent)
	{
		super.setParent(parent);
		
		setCellOrigin(this.header, 
				new JRCrosstabOrigin(getParent(), JRCrosstabOrigin.TYPE_COLUMN_GROUP_HEADER,
						null, getName()));
		setCellOrigin(this.totalHeader, 
				new JRCrosstabOrigin(getParent(), JRCrosstabOrigin.TYPE_COLUMN_GROUP_TOTAL_HEADER,
						null, getName()));
		setCrosstabHeaderOrigin();
	}

	@Override
	public JRCellContents getCrosstabHeader()
	{
		return crosstabHeader;
	}

	public void setCrosstabHeader(JRDesignCellContents crosstabHeader)
	{
		Object old = this.crosstabHeader;
		this.crosstabHeader = crosstabHeader;
		getEventSupport().firePropertyChange(PROPERTY_CROSSTAB_HEADER, old, this.crosstabHeader);
		
		setCrosstabHeaderOrigin();
	}

	protected void setCrosstabHeaderOrigin()
	{
		if (this.crosstabHeader != null)
		{
			JRCrosstabOrigin origin = new JRCrosstabOrigin(getParent(), 
					JRCrosstabOrigin.TYPE_COLUMN_GROUP_CROSSTAB_HEADER,
					null, getName());
			setCellOrigin(this.crosstabHeader, origin);
		}
	}

	@Override
	public Object clone()
	{
		JRDesignCrosstabColumnGroup clone = (JRDesignCrosstabColumnGroup) super.clone();
		clone.crosstabHeader = JRCloneUtils.nullSafeClone(crosstabHeader);
		return clone;
	}
}
