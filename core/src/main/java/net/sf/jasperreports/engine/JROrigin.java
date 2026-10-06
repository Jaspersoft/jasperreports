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
package net.sf.jasperreports.engine;

import java.io.Serializable;

import net.sf.jasperreports.engine.type.BandTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JROrigin implements JRCloneable, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private BandTypeEnum bandType = BandTypeEnum.UNKNOWN;
	private String groupName;
	private String reportName;
	
	private transient Integer hashCode;
	
	/**
	 * 
	 */
	public JROrigin(
		BandTypeEnum bandType
		)
	{
		this(null, null, bandType);
	}

	/**
	 * 
	 */
	public JROrigin(
		String reportName,
		BandTypeEnum bandType
		)
	{
		this(reportName, null, bandType);
	}

	/**
	 * 
	 */
	public JROrigin(
		String reportName,
		String groupName,
		BandTypeEnum bandType
		)
	{
		this.reportName = reportName;
		this.groupName = groupName;
		this.bandType = bandType;
	}

	/**
	 * 
	 */
	public String getReportName()
	{
		return reportName;
	}

	/**
	 * 
	 */
	public String getGroupName()
	{
		return groupName;
	}

	/**
	 * 
	 */
	public BandTypeEnum getBandType()
	{
		return bandType;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof JROrigin)
		{
			JROrigin origin = (JROrigin)obj;
			String groupName2 = origin.getGroupName();
			String reportName2 = origin.getReportName();
			return
				origin.getBandType() == bandType
				&& (groupName == null ? groupName2 == null : groupName2 != null && groupName.equals(groupName2))
				&& (reportName == null ? reportName2 == null : reportName2 != null && reportName.equals(reportName2));
		}
		return false;
	}
	
	@Override
	public int hashCode() 
	{
		if (hashCode == null)
		{
			int hash = 17;
			hash = 31 * hash + (reportName == null ? 0 : reportName.hashCode());
			hash = 31 * hash + (groupName == null ? 0 : groupName.hashCode());
			hash = 31 * hash + bandType.hashCode();
			hashCode = hash;
		}
		return hashCode;
	}


	@Override
	public Object clone() 
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	@Override
	public String toString()
	{
		return "{reportName: " + reportName 
				+ ", groupName: " + groupName 
				+ ",bandType: " + bandType + "}";
	}
}
