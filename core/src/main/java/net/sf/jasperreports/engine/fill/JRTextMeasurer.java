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

import net.sf.jasperreports.engine.util.JRStyledText;

/**
 * Text measurer interface.
 * 
 * A text measurer is responsible for fitting a text in a given space
 * and for computing other text measuring information.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRTextMeasurer
{

	/**
	 * Fit a text chunk in a given space. 
	 * 
	 * @param styledText the full text
	 * @param remainingTextStart the start index of the remaining text
	 * @param availableStretchHeight the available stretch height
	 * @param indentFirstLine whether should honor first line indent
	 * @param canOverflow whether the text element is able to overflow
	 * @return text measuring information
	 */
	JRMeasuredText measure(JRStyledText styledText,
		int remainingTextStart,
		int availableStretchHeight,
		boolean indentFirstLine,
		boolean canOverflow
		);

}
