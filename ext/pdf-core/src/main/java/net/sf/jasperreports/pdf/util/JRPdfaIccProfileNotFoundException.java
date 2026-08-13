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
package net.sf.jasperreports.pdf.util;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.PdfExporterConfiguration;


/**
 * Exception raised when an ICC profile is not available to the JVM. 
 * <p/>
 * The ICC profiles are defined by the International Color Consortium. They are a means by which OpenPDF ensures color
 * management in a device-independent manner and they need to be embedded for PDF/A-1 compliance.
 * <p/>
 * Here are some sources of freely available and high quality ICC profiles:
 * <ul>
 * 	<li>sRGB ICC profiles provided by the International Color Consortium:
 *		<ul> 
 *			<li><a href="http://www.color.org/srgbprofiles.html">http://www.color.org/srgbprofiles.html</a></li>
 *		</ul>
 *	</li>
 * </li>
 * <li>RGB and CMYK ICC profiles for various printing conditions provided by the European Color Initiative (ECI):
 *		<ul>
 * 			<li><a href="http://www.eci.org/doku.php?id=en:downloads#icc_profiles_from_eci">http://www.eci.org/doku.php?id=en:downloads#icc_profiles_from_eci</a></li>
 * 		</ul>
 * </li>
 * <li>RGB and CMYK ICC profiles provided by Adobe:
 * 		<ul>
 * 			<li><a href="http://www.adobe.com/support/downloads/iccprofiles/iccprofiles_win.html">http://www.adobe.com/support/downloads/iccprofiles/iccprofiles_win.html</a></li>
 *		</ul> 
 * </li>
 * <li>ICC profiles for standardised newspaper printing:
 * 		<ul>
 *			<li><a href="https://www.wan-ifra.org/reports/1997/07/01/icc-profiles-for-standardised-newspaper-printing">https://www.wan-ifra.org/reports/1997/07/01/icc-profiles-for-standardised-newspaper-printing</a></li>
 * 		</ul> 		
 * </li>
 * </ul>
 * <p/>
 * After obtaining a valid ICC profile, use the path to the profile as value for the {@link PdfExporterConfiguration#getIccProfilePath()} configuration property.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JRPdfaIccProfileNotFoundException extends JRRuntimeException
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String EXCEPTION_MESSAGE_KEY_ICC_PROFILE_NOT_AVAILABLE = "util.icc.profile.not.available";

	/**
	 * 
	 */
	public JRPdfaIccProfileNotFoundException()
	{
		super(
			EXCEPTION_MESSAGE_KEY_ICC_PROFILE_NOT_AVAILABLE,
			(Object[])null);
	}
}
