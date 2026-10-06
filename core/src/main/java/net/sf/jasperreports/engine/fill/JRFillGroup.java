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

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.FooterPositionEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillGroup implements JRGroup
{


	/**
	 *
	 */
	protected JRGroup parent;

	/**
	 *
	 */
	private JRFillSection groupHeaderSection;
	private JRFillSection groupFooterSection;
	private JRVariable countVariable;
	
	private boolean startNewColumn;
	private boolean startNewPage;
	private boolean resetPageNumber;

	/**
	 *
	 */
	private boolean hasChanged = true;
	private boolean isTopLevelChange;
	private boolean isHeaderPrinted;
	private boolean isFooterPrinted = true;
	private int detailsCount;
	
	private ElementRange keepTogetherElementRange;

	/**
	 *
	 */
	public JRFillGroup(
		JRGroup group, 
		JRFillObjectFactory factory
		)
	{
		factory.put(group, this);

		parent = group;

		JRBaseFiller filler = factory.getFiller();
		if (filler != null)
		{
			String reportName = filler.getBandReportParent() == null ? null : filler.getBandReportParent().getReportName();
			
			groupHeaderSection = factory.getSection(group.getGroupHeaderSection());
			if (groupHeaderSection != factory.getFiller().missingFillSection)
			{
				groupHeaderSection.setOrigin(
					new JROrigin(
						reportName,
						group.getName(),
						BandTypeEnum.GROUP_HEADER
						)
					);
			}

			groupFooterSection = factory.getSection(group.getGroupFooterSection());
			if (groupFooterSection != factory.getFiller().missingFillSection)
			{
				groupFooterSection.setOrigin(
					new JROrigin(
						reportName,
						group.getName(),
						BandTypeEnum.GROUP_FOOTER
						)
					);
			}
		}

		countVariable = factory.getVariable(group.getCountVariable());
		
		startNewColumn = parent.isStartNewColumn();
		startNewPage = parent.isStartNewPage();
		resetPageNumber = parent.isResetPageNumber();
	}


	@Override
	public String getName()
	{
		return parent.getName();
	}
	
	@Override
	public JRExpression getExpression()
	{
		return parent.getExpression();
	}
		
	@Override
	public boolean isStartNewColumn()
	{
		return startNewColumn;
	}
		
	@Override
	public void setStartNewColumn(boolean isStart)
	{
		this.startNewColumn = isStart;
	}
		
	@Override
	public boolean isStartNewPage()
	{
		return startNewPage;
	}
		
	@Override
	public void setStartNewPage(boolean isStart)
	{
		this.startNewPage = isStart;
	}
		
	@Override
	public boolean isResetPageNumber()
	{
		return resetPageNumber;
	}
		
	@Override
	public void setResetPageNumber(boolean isReset)
	{
		this.resetPageNumber = isReset;
	}
		
	@Override
	public boolean isReprintHeaderOnEachPage()
	{
		return parent.isReprintHeaderOnEachPage();
	}
		
	@Override
	public void setReprintHeaderOnEachPage(boolean isReprint)
	{
	}
		
	@Override
	public boolean isReprintHeaderOnEachColumn()
	{
		return parent.isReprintHeaderOnEachColumn();
	}
		
	@Override
	public void setReprintHeaderOnEachColumn(boolean isReprint)
	{
	}
		
	@Override
	public int getMinHeightToStartNewPage()
	{
		return parent.getMinHeightToStartNewPage();
	}
		
	@Override
	public void setMinHeightToStartNewPage(int minHeight)
	{
	}
	
	@Override
	public int getMinDetailsToStartFromTop()
	{
		return parent.getMinDetailsToStartFromTop();
	}
		
	@Override
	public void setMinDetailsToStartFromTop(int minDetails)
	{
	}
	
	@Override
	public FooterPositionEnum getFooterPosition()
	{
		return FooterPositionEnum.getValueOrDefault(parent.getFooterPosition());
	}
		
	@Override
	public void setFooterPosition(FooterPositionEnum footerPosition)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public boolean isKeepTogether()
	{
		return parent.isKeepTogether();
	}
		
	@Override
	public void setKeepTogether(boolean keepTogether)
	{
	}
		
	@Override
	public boolean isPreventOrphanFooter()
	{
		return parent.isPreventOrphanFooter();
	}
		
	@Override
	public void setPreventOrphanFooter(boolean preventOrphanFooter)
	{
	}
		
	@Override
	public JRSection getGroupHeaderSection()
	{
		return groupHeaderSection;
	}
		
	@Override
	public JRSection getGroupFooterSection()
	{
		return groupFooterSection;
	}
		
	@Override
	public JRVariable getCountVariable()
	{
		return countVariable;
	}
	
	/**
	 *
	 */
	public boolean hasChanged()
	{
		return hasChanged;
	}
		
	/**
	 *
	 */
	public void setHasChanged(boolean hasChanged)
	{
		this.hasChanged = hasChanged;
	}

	/**
	 * Not used anymore.
	 */
	public boolean isTopLevelChange()
	{
		return isTopLevelChange;
	}
		
	/**
	 *
	 */
	public void setTopLevelChange(boolean isTopLevelChange)
	{
		this.isTopLevelChange = isTopLevelChange;
	}

	/**
	 *
	 */
	public boolean isHeaderPrinted()
	{
		return isHeaderPrinted;
	}
			
	/**
	 *
	 */
	public void setHeaderPrinted(boolean isHeaderPrinted)
	{
		this.isHeaderPrinted = isHeaderPrinted;
	}

	/**
	 *
	 */
	public boolean isFooterPrinted()
	{
		return isFooterPrinted;
	}
		
	/**
	 *
	 */
	public void setFooterPrinted(boolean isFooterPrinted)
	{
		this.isFooterPrinted = isFooterPrinted;
	}

	/**
	 *
	 */
	public ElementRange getKeepTogetherElementRange()
	{
		return keepTogetherElementRange;
	}

	/**
	 *
	 */
	public void setKeepTogetherElementRange(ElementRange keepTogetherElementRange)
	{
		this.keepTogetherElementRange = keepTogetherElementRange;
	}
	
	/**
	 *
	 */
	protected void incrementDetailsCount()
	{
		detailsCount++;
	}
	
	/**
	 *
	 */
	protected void resetDetailsCount()
	{
		detailsCount = 0;
	}

	/**
	 *
	 */
	protected boolean hasMinDetails()
	{
		return hasMinDetails(0);
	}

	/**
	 *
	 */
	protected boolean hasMinDetails(int detailsToMove)
	{
		return getMinDetailsToStartFromTop() == 0 || getMinDetailsToStartFromTop() <= detailsCount - detailsToMove;
	}

	/**
	 *
	 */
	protected int getDetailsCount()
	{
		return detailsCount;
	}
		
	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}

}
