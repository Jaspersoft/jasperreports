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
package net.sf.jasperreports.charts.util;

import java.io.Serializable;
import java.text.AttributedString;
import java.util.Map;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PieLabelGenerator implements PieSectionLabelGenerator, Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Map<Comparable<?>, String> labels;
	
	public PieLabelGenerator( Map<Comparable<?>, String> labels )
	{
		this.labels = labels;
	}
	
	@Override
	public String generateSectionLabel(PieDataset arg0, @SuppressWarnings("rawtypes") Comparable arg1)
	{
		return labels.get( arg1 );
	}

	@Override
	public AttributedString generateAttributedSectionLabel(PieDataset arg0, @SuppressWarnings("rawtypes") Comparable arg1)
	{
		return new AttributedString(generateSectionLabel(arg0, arg1));//FIXMECHART check this
	}
}