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
package net.sf.jasperreports.search;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class HitSpanInfo implements JRCloneable, Serializable {

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private String pageNo;
	private List<HitTermInfo> hitTermInfoList = new ArrayList<>();

	public HitSpanInfo(HitTermInfo hitTermInfo) {
		this.hitTermInfoList.add(hitTermInfo);
	}

	public HitSpanInfo(List<HitTermInfo> hitTermInfoList) {
		this.hitTermInfoList.addAll(hitTermInfoList);
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}

	public String getPageNo() {
		return pageNo;
	}

	public List<HitTermInfo> getHitTermInfoList() {
		return hitTermInfoList;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// never
			throw new JRRuntimeException(e);
		}
	}

}
