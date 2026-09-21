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

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * Exception raised when a font name used as value for the <code>fontName</code> attribute in the report template,
 * is not found in any of the runtime available JasperReports font extensions, nor among the font names available 
 * to the Java Virtual Machine.
 * <p/>
 * The missing font check that raises this exception can be turned off by using the
 * {@link net.sf.jasperreports.engine.util.JRStyledText#PROPERTY_AWT_IGNORE_MISSING_FONT net.sf.jasperreports.awt.ignore.missing.font} boolean configuration property, 
 * which is available globally or at report level.
 * <p/>
 * However, we advise you to leave this font check in place and rather make sure that inside your report template 
 * you are using a font that will certainly be available at runtime, when the report will be filled or displayed/rendered 
 * using the JRGraphics2DExporter (or the JasperViewer).
 * <br/>
 * This ensures that all font metrics and layout calculations performed while the report was designed/previewed, 
 * remain consistent with those performed while the report is fill and then rendered on screen.
 * <br/>
 * By turning off the missing font check using the above mentioned configuration property, the engine will no longer 
 * raise an exception when the font used inside the report template is not available at runtime. It will use a platform 
 * dependent font instead of the missing font. It is highly likely that this default font that replaces the one specified 
 * in the report template, comes with totally different font metrics and, as a consequence, all the layout calculations 
 * made using this in-lieu font will be different then the ones used when the report was designed/previewed.
 * <br/>
 * The most common side effect of this is that text content will no longer fit the specified text element height 
 * and the text content will get cut off, not appearing in the generated document.
 * <p/>
 * With the font check in place (as it is by default in JasperReports), all you need to do is to make sure you
 * are using only fonts that you ship with your reports, packaged as JasperReports font extensions (recommended),
 * or that you use fonts that you are sure will be available to the JVM where you deploy the reports 
 * (Java logical fonts or system fonts).
 * <br/>
 * Note that the use of Java logical fonts such as: Serif, SansSerif, Monospaced, Dialog, and DialogInput is not encouraged
 * with JasperReports. This is because while these fonts are always available to JVM, they are not physical fonts, but rather
 * virtual/logical fonts that are mapped to different physical fonts, depending on the local JVM configuration.
 * <br/>
 * When using such a logical font in a report template, we are never sure which physical font will be used by the JVM, 
 * and thus we are never sure about its font metrics either. The worse case happens when the report is designed/previewed on a machine
 * that maps the logical font on some small glyph font, but when the report is filled, on some other machine, the same logical
 * font is mapped to some other font, with bigger glyphs, and the text fill no longer fit the specified text element height,
 * resulting in the text being cut.
 * <br/>
 * This is why we encourage our users to package up TTF files as font extensions, using built JasperReports font extension
 * support, and thus make sure the report template is deployed together with the fonts it needs to render properly.
 * Fonts used inside a report template should be considered as indispensable resources, just like image files and resource bundles.
 * Just as a report would not display properly if some required logo image is missing, the same way the report would not
 * display properly if some required font is missing as well. And just as the logo image is shipped with the report template,
 * the same way font files should be shipped as well.
 * <p/>
 * For more details about working with JasperReports font extensions, check the JasperReports documentation and the samples
 * provided with the JasperReports project distribution package.
 *  
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFontNotFoundException extends JRRuntimeException
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final String EXCEPTION_MESSAGE_KEY_FONT_NOT_AVAILABLE = "util.font.not.available";

	/**
	 * 
	 */
	public JRFontNotFoundException(String font)
	{
		super(
			EXCEPTION_MESSAGE_KEY_FONT_NOT_AVAILABLE, 
			new Object[]{font});

	}
}
