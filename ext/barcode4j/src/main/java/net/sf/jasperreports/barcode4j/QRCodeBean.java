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
package net.sf.jasperreports.barcode4j;


/**
 * This class is used to generate QRCode component barcode logic. 
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class QRCodeBean
{
	private Integer margin;
	private ErrorCorrectionLevelEnum errorCorrectionLevel;
	private Integer qrVersion;
  
	public QRCodeBean() {
	}
	
	public Integer getMargin() {
		return margin;
	}

	public void setMargin(Integer margin) {
		this.margin = margin;
	}

	public ErrorCorrectionLevelEnum getErrorCorrectionLevel() {
		return errorCorrectionLevel;
	}

	public void setErrorCorrectionLevel(ErrorCorrectionLevelEnum errorCorrectionLevel) {
		this.errorCorrectionLevel = errorCorrectionLevel;
	}

	public Integer getQrVersion() {
		return qrVersion;
	}

	public void setQrVersion(Integer qrVersion) {
		this.qrVersion = qrVersion;
	}
}

