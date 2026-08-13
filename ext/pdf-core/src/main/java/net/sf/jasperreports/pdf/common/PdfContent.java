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
package net.sf.jasperreports.pdf.common;

import java.awt.Color;
import java.awt.geom.AffineTransform;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PdfContent
{
	
	void setFillColor(Color color);

	void setFillColorAlpha(int alpha);

	void resetFillColor();

	void setStrokeColor(Color color);

	void resetStrokeColor();
	
	void setLineWidth(float lineWidth);

	void setLineCap(LineCapStyle lineCap);

	void setLineDash(float f);

	void setLineDash(float lineWidth, float lineWidth2, float f);

	void strokeLine(float x1, float y1, float x2, float y2);
	
	void fillRectangle(float x, float y, float width, float height);

	void fillRoundRectangle(float x, float y, float width, float height, float radius);

	void strokeRoundRectangle(float x, float y, float width, float height, float radius);

	void fillEllipse(float x1, float y1, float x2, float y2);

	void strokeEllipse(float x1, float y1, float x2, float y2);

	void setLiteral(String string);

	void transform(AffineTransform atrans);

}
