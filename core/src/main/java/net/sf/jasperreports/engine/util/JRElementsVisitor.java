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
package net.sf.jasperreports.engine.util;

import java.util.List;

import net.sf.jasperreports.engine.ElementsVisitor;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JRVisitor;


/**
 * Report elements visitor.
 * 
 * This class can be used to recursively visit all the elements of a report.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRElementsVisitor extends JRDelegationVisitor implements ElementsVisitor
{

	/**
	 * Visits all the elements of a report.
	 * 
	 * @param report the report
	 * @param visitor the element visitor
	 */
	public static void visitReport(JRReport report, JRVisitor visitor)
	{
		JRElementsVisitor reportVisitor = new JRElementsVisitor(visitor);
		reportVisitor.visitReport(report);
	}
	
	/**
	 * Creates a report visitor.
	 * 
	 * @param visitor the elements visitor
	 */
	public JRElementsVisitor(JRVisitor visitor)
	{
		super(visitor);
	}

	@Override
	public boolean visitDeepElements()
	{
		return true;
	}

	/**
	 * Visits all the elements of a report.
	 * 
	 * @param report the report
	 */
	public void visitReport(JRReport report)
	{
		visitBand(report.getBackground());
		visitBand(report.getTitle());
		visitBand(report.getPageHeader());
		visitBand(report.getColumnHeader());
		visitSection(report.getDetailSection());
		visitBand(report.getColumnFooter());
		visitBand(report.getPageFooter());
		visitBand(report.getLastPageFooter());
		visitBand(report.getSummary());
		visitBand(report.getNoData());
		
		JRGroup[] groups = report.getGroups();
		if (groups != null)
		{
			for (JRGroup group : groups)
			{
				visitSection(group.getGroupHeaderSection());
				visitSection(group.getGroupFooterSection());
			}
		}
	}
	
	protected void visitSection(JRSection section)
	{
		if (section != null)
		{
			JRBand[] bands = section.getBands();
			if (bands != null)
			{
				for (int i = 0; i < bands.length; i++)
				{
					visitBand(bands[i]);
				}
			}
		}
	}
	
	protected void visitBand(JRBand band)
	{
		if (band != null)
		{
			band.visit(this);
		}
	}

	protected void visitElements(List<JRChild> elements)
	{
		ElementsVisitorUtils.visitElements(this, elements);
	}
}
