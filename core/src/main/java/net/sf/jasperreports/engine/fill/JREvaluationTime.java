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

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;


/**
 * An evaluation time during the report fill process.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JREvaluationTime implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;


	/**
	 * Evaluation time corresponding to {@link EvaluationTimeEnum#REPORT EvaluationTimeEnum.REPORT}.
	 */
	public static final JREvaluationTime EVALUATION_TIME_REPORT = new JREvaluationTime(EvaluationTimeEnum.REPORT, null, null);
	/**
	 * Evaluation time corresponding to {@link EvaluationTimeEnum#PAGE EvaluationTimeEnum.PAGE}.
	 */
	public static final JREvaluationTime EVALUATION_TIME_PAGE = new JREvaluationTime(EvaluationTimeEnum.PAGE, null, null);
	/**
	 * Evaluation time corresponding to {@link EvaluationTimeEnum#COLUMN EvaluationTimeEnum.COLUMN}.
	 */
	public static final JREvaluationTime EVALUATION_TIME_COLUMN = new JREvaluationTime(EvaluationTimeEnum.COLUMN, null, null);
	/**
	 * Evaluation time corresponding to {@link EvaluationTimeEnum#NOW EvaluationTimeEnum.NOW}.
	 */
	public static final JREvaluationTime EVALUATION_TIME_NOW = new JREvaluationTime(EvaluationTimeEnum.NOW, null, null);
	

	/**
	 * Evaluation time corresponding to {@link EvaluationTimeEnum#MASTER EvaluationTimeEnum.MASTER}.
	 */
	public static final JREvaluationTime EVALUATION_TIME_MASTER = new JREvaluationTime(EvaluationTimeEnum.MASTER, null, null);
	
	/**
	 * Returns the evaluation time corresponding to
	 * {@link EvaluationTimeEnum#GROUP EvaluationTimeEnum.GROUP} for a specific group.
	 * 
	 * @param groupName the group name
	 * @return corresponding group evaluation time
	 */
	public static JREvaluationTime getGroupEvaluationTime(String groupName)
	{
		return new JREvaluationTime(EvaluationTimeEnum.GROUP, groupName, null);
	}
	
	/**
	 * Returns the evaluation time corresponding to
	 * {@link EvaluationTimeEnum#BAND EvaluationTimeEnum.BAND} for a specific band.
	 * 
	 * @param band the band
	 * @return corresponding band evaluation time
	 */
	public static JREvaluationTime getBandEvaluationTime(JRFillBand band)
	{
		return new JREvaluationTime(EvaluationTimeEnum.BAND, null, band);
	}
	
	public static JREvaluationTime getBandEvaluationTime(int bandId)
	{
		return new JREvaluationTime(EvaluationTimeEnum.BAND, null, bandId);
	}
	
	
	/**
	 * Returns the evaluation time corresponding to an evaluation time type.
	 * 
	 * @param type the evaluation time type
	 * @param group the group used for {@link EvaluationTimeEnum#GROUP EvaluationTimeEnum.GROUP}
	 * 	evaluation time type
	 * @param band the band used for {@link EvaluationTimeEnum#BAND EvaluationTimeEnum.BAND}
	 * 	evaluation time type
	 * @return the evaluation time corresponding to an evaluation time type
	 */
	public static JREvaluationTime getEvaluationTime(EvaluationTimeEnum type, JRGroup group, JRFillBand band)
	{
		JREvaluationTime evaluationTime = null;
		
		if (type != null)
		{
			switch (type)
			{
				case REPORT:
					evaluationTime = EVALUATION_TIME_REPORT;
					break;
				case MASTER:
					evaluationTime = EVALUATION_TIME_MASTER;
					break;
				case PAGE:
					evaluationTime = EVALUATION_TIME_PAGE;
					break;
				case COLUMN:
					evaluationTime = EVALUATION_TIME_COLUMN;
					break;
				case GROUP:
					evaluationTime = getGroupEvaluationTime(group.getName());
					break;
				case BAND:
					evaluationTime = getBandEvaluationTime(band);
					break;
				case NOW:
				default:
					break;
			}
		}
		
		return evaluationTime;
	}
	
	private final EvaluationTimeEnum type;
	private final String groupName;
	private final int bandId;
	private final int hash;
	
	
	private JREvaluationTime(EvaluationTimeEnum type, String groupName, JRFillBand band)
	{
		this(type, groupName, band == null ? 0 : band.getId());
	}
	
	private JREvaluationTime(EvaluationTimeEnum type, String groupName, int bandId)
	{
		this.type = type;
		this.groupName = groupName;
		this.bandId = bandId;
		
		this.hash = computeHash();
	}


	private int computeHash()
	{
		int hashCode = type.ordinal();
		hashCode = 31*hashCode + (groupName == null ? 0 : groupName.hashCode());
		hashCode = 31*hashCode + bandId;
		return hashCode;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		
		JREvaluationTime e = (JREvaluationTime) obj;
		
		boolean eq = e.type == type;
		
		if (eq)
		{
			switch (type)
			{
				case GROUP:
					eq = groupName.equals(e.groupName);
					break;
				case BAND:
					eq = bandId == e.bandId;
					break;
				default:
			}
		}
		
		return eq;
	}


	@Override
	public int hashCode()
	{
		return hash;
	}

	@Override
	public String toString()
	{
		return "{type: " + type
				+ ", group: " + groupName
				+ ", band: " + bandId
				+ "}";
	}

	public EvaluationTimeEnum getType()
	{
		return type;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public int getBandId()
	{
		return bandId;
	}
	
}
