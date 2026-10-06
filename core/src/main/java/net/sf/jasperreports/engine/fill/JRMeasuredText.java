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

import net.sf.jasperreports.engine.JRPrintText;

/**
 * Text measuring information as produced by a {@link JRTextMeasurer text measurer}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRTextMeasurer#measure(net.sf.jasperreports.engine.util.JRStyledText, int, int, boolean, boolean)
 */
public interface JRMeasuredText
{

	/**
	 * Returns the text leading offset.
	 * 
	 * @return the text leading offset
	 */
	float getLeadingOffset();

	/**
	 * Return the line spacing factor for the text.
	 * 
	 * @return the line spacing factor
	 */
	float getLineSpacingFactor();

	/**
	 * Returns the text's measure width.
	 * 
	 * @return the text's measure width
	 */
	float getTextWidth();

	/**
	 * Returns the text's average character width.
	 * 
	 * @return the text's average character width
	 */
	float getAverageCharWidth();

	/**
	 * Returns the text's measure height.
	 * 
	 * @return the text's measure height
	 */
	float getTextHeight();

	/**
	 * Returns the offset up to which text fitted.
	 * 
	 * @return the offset up to which text fitted
	 */
	int getTextOffset();

	/**
	 * Returns whether the text was determined to be left to right or not.
	 * 
	 * @return whether the text was determined to be left to right
	 */
	boolean isLeftToRight();

	/**
	 * Returns whether the text was cut during a paragraph or the last paragraph was complete.
	 * 
	 * @return whether the last paragraph was cut
	 */
	boolean isParagraphCut();

	/**
	 * Returns the suffix that was appended to the text
	 * (after {@link #getTextOffset()}).
	 * 
	 * @return the suffix that was appended to the text
	 */
	String getTextSuffix();

	/**
	 * Returns the line break offsets as required for
	 * {@link JRPrintText#getLineBreakOffsets()}.
	 * 
	 * @return the line break offsets for the measured text
	 */
	short[] getLineBreakOffsets();
}
