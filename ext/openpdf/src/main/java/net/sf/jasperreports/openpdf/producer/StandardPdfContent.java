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
package net.sf.jasperreports.openpdf.producer;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;

import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfGState;
import org.openpdf.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.common.LineCapStyle;
import net.sf.jasperreports.pdf.common.PdfContent;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPdfContent implements PdfContent
{
	
	private PdfWriter pdfWriter;
	private PdfContentByte pdfContentByte;
	private ColorSpace cmykColorSpace;
	
	private PdfGState[] fillAlphaStates = new PdfGState[256];
	private boolean fillAlphaSet = false;

	private PdfGState[] strokeAlphaStates = new PdfGState[256];
	private boolean strokeAlphaSet = false;

	public StandardPdfContent(PdfWriter pdfWriter, ColorSpace cmykColorSpace)
	{
		this.pdfWriter = pdfWriter;
		this.pdfContentByte = pdfWriter.getDirectContent();
		this.cmykColorSpace = cmykColorSpace;
	}
	
	public PdfWriter getPdfWriter()
	{
		return pdfWriter;
	}

	public PdfContentByte getPdfContentByte()
	{
		return pdfContentByte;
	}
	
	public void refreshContent()
	{
		pdfContentByte = pdfWriter.getDirectContent();
	}
	
	@Override
	public void setFillColor(Color color)
	{
		setFillColorAlpha(color.getAlpha());
		pdfContentByte.setColorFill(
			StandardPdfUtils.convertColor(cmykColorSpace, color)
			);
	}

	@Override
	public void setFillColorAlpha(int alpha)
	{
		if (alpha != 255)
		{
			setFillAlpha(alpha);
			fillAlphaSet = true;
		}
	}
	
	@Override
	public void resetFillColor()
	{
		if (fillAlphaSet)
		{
			setFillAlpha(255);
			fillAlphaSet = false;
		}
	}

	protected void setFillAlpha(int alpha)
	{
		PdfGState state = fillAlphaStates[alpha];
		if (state == null)
		{
			state = new PdfGState();
			state.setFillOpacity(((float) alpha)/255);
			fillAlphaStates[alpha] = state;
		}
		pdfContentByte.setGState(state);
	}

	@Override
	public void setStrokeColor(Color color)
	{
		int alpha = color.getAlpha();
		if (alpha != 255)
		{
			setStrokeAlpha(alpha);
			strokeAlphaSet = true;
		}
		
		pdfContentByte.setColorStroke(
			StandardPdfUtils.convertColor(cmykColorSpace, color)
			);		
	}
	
	@Override
	public void resetStrokeColor()
	{
		if (strokeAlphaSet)
		{
			setStrokeAlpha(255);
			strokeAlphaSet = false;
		}
	}

	protected void setStrokeAlpha(int alpha)
	{
		PdfGState state = strokeAlphaStates[alpha];
		if (state == null)
		{
			state = new PdfGState();
			state.setStrokeOpacity(((float) alpha)/255);
			strokeAlphaStates[alpha] = state;
		}
		pdfContentByte.setGState(state);
	}

	@Override
	public void setLineWidth(float lineWidth)
	{
		pdfContentByte.setLineWidth(lineWidth);
	}

	@Override
	public void setLineCap(LineCapStyle lineCap)
	{
		int lineCapValue;
		switch (lineCap)
		{
		case BUTT:
			lineCapValue = PdfContentByte.LINE_CAP_BUTT;
			break;
		case ROUND:
			lineCapValue = PdfContentByte.LINE_CAP_ROUND;
			break;
		case PROJECTING_SQUARE:
			lineCapValue = PdfContentByte.LINE_CAP_PROJECTING_SQUARE;
			break;
		default:
			throw new JRRuntimeException("Unknown line cap style " + lineCap);
		}
		pdfContentByte.setLineCap(lineCapValue);
	}

	@Override
	public void setLineDash(float phase)
	{
		pdfContentByte.setLineDash(phase);
	}

	@Override
	public void setLineDash(float unitsOn, float unitsOff, float phase)
	{
		pdfContentByte.setLineDash(unitsOn, unitsOff, phase);
	}

	@Override
	public void strokeLine(float x1, float y1, float x2, float y2)
	{
		pdfContentByte.moveTo(x1, y1);
		pdfContentByte.lineTo(x2, y2);
		pdfContentByte.stroke();
	}

	@Override
	public void fillRectangle(float x, float y, float width, float height)
	{
		pdfContentByte.rectangle(x, y, width, height);
		pdfContentByte.fill();		
	}

	@Override
	public void fillRoundRectangle(float x, float y, float width, float height, float radius)
	{
		pdfContentByte.roundRectangle(x, y, width, height, radius);
		pdfContentByte.fill();
	}

	@Override
	public void strokeRoundRectangle(float x, float y, float width, float height, float radius)
	{
		pdfContentByte.roundRectangle(x, y, width, height, radius);
		pdfContentByte.stroke();
	}

	@Override
	public void fillEllipse(float x1, float y1, float x2, float y2)
	{
		pdfContentByte.ellipse(x1, y1, x2, y2);
		pdfContentByte.fill();
	}

	@Override
	public void strokeEllipse(float x1, float y1, float x2, float y2)
	{
		pdfContentByte.ellipse(x1, y1, x2, y2);
		pdfContentByte.stroke();
	}

	@Override
	public void setLiteral(String string)
	{
		pdfContentByte.setLiteral(string);
	}

	@Override
	public void transform(AffineTransform atrans)
	{
		pdfContentByte.transform(atrans);
	}

}
