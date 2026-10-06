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
package net.sf.jasperreports.components.table.fill;

import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.FooterPositionEnum;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TableReportGroup implements JRGroup
{

	private final JRGroup datasetGroup;
	private JRSection headerSection;
	private JRSection footerSection;
	
	public TableReportGroup(JRGroup datasetGroup)
	{
		this.datasetGroup = datasetGroup;
	}

	@Override
	public JRVariable getCountVariable()
	{
		return datasetGroup.getCountVariable();
	}

	@Override
	public JRExpression getExpression()
	{
		return datasetGroup.getExpression();
	}

	@Override
	public FooterPositionEnum getFooterPosition()
	{
		return datasetGroup.getFooterPosition();
	}

	protected void setGroupFooter(JRBand footer)
	{
		this.footerSection = wrapBand(footer, BandTypeEnum.GROUP_FOOTER);
	}
	
	protected JRSection wrapBand(JRBand band, BandTypeEnum bandType)
	{
		if (band == null)
		{
			return null;
		}
		
		JROrigin origin = new JROrigin(null, getName(), bandType);
		JRDesignSection section = new JRDesignSection(origin);
		section.addBand(band);
		return section;
	}
	
	@Override
	public JRSection getGroupFooterSection()
	{
		return footerSection;
	}

	protected void setGroupHeader(JRBand header)
	{
		this.headerSection = wrapBand(header, BandTypeEnum.GROUP_HEADER);
	}

	@Override
	public JRSection getGroupHeaderSection()
	{
		return headerSection;
	}

	@Override
	public int getMinHeightToStartNewPage()
	{
		return datasetGroup.getMinHeightToStartNewPage();
	}

	@Override
	public int getMinDetailsToStartFromTop()
	{
		return datasetGroup.getMinDetailsToStartFromTop();
	}

	@Override
	public String getName()
	{
		return datasetGroup.getName();
	}

	@Override
	public boolean isKeepTogether()
	{
		return datasetGroup.isKeepTogether();
	}

	@Override
	public boolean isPreventOrphanFooter()
	{
		return datasetGroup.isPreventOrphanFooter();
	}

	@Override
	public boolean isReprintHeaderOnEachPage()
	{
		return datasetGroup.isReprintHeaderOnEachPage();
	}

	@Override
	public boolean isReprintHeaderOnEachColumn()
	{
		return datasetGroup.isReprintHeaderOnEachColumn();
	}

	@Override
	public boolean isResetPageNumber()
	{
		return datasetGroup.isResetPageNumber();
	}

	@Override
	public boolean isStartNewColumn()
	{
		return datasetGroup.isStartNewColumn();
	}

	@Override
	public boolean isStartNewPage()
	{
		return datasetGroup.isStartNewPage();
	}

	@Override
	public void setFooterPosition(FooterPositionEnum footerPosition)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setKeepTogether(boolean keepTogether)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPreventOrphanFooter(boolean preventOrphanFooter)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMinHeightToStartNewPage(int minHeight)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMinDetailsToStartFromTop(int minDetails)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReprintHeaderOnEachPage(boolean isReprint)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setReprintHeaderOnEachColumn(boolean isReprint)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setResetPageNumber(boolean isReset)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStartNewColumn(boolean isStart)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStartNewPage(boolean isStart)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object clone()
	{
		throw new UnsupportedOperationException();
	}

	public JRGroup getOriginalGroup()
	{
		return datasetGroup;
	}
	
}
