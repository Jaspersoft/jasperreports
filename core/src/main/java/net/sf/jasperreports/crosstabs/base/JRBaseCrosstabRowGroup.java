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
package net.sf.jasperreports.crosstabs.base;

import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.type.CrosstabRowPositionEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;


/**
 * Base read-only implementation of crosstab row groups.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRBaseCrosstabRowGroup extends JRBaseCrosstabGroup implements JRCrosstabRowGroup
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected int width;
	protected CrosstabRowPositionEnum position;
	protected boolean keepTogether;

	public JRBaseCrosstabRowGroup(JRCrosstabRowGroup group, JRBaseObjectFactory factory)
	{
		super(group, factory);

		width = group.getWidth();
		position = group.getPosition();
		keepTogether = group.isKeepTogether();
	}

	@Override
	public CrosstabRowPositionEnum getPosition()
	{
		return position;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public boolean isKeepTogether()
	{
		return keepTogether;
	}
}
