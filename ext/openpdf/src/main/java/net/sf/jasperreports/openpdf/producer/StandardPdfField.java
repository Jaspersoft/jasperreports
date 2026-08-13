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
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Locale;
import java.util.Map;

import org.openpdf.text.Font;
import org.openpdf.text.pdf.BaseField;
import org.openpdf.text.pdf.PdfBorderDictionary;
import org.openpdf.text.pdf.TextField;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.common.PdfField;
import net.sf.jasperreports.pdf.common.PdfTextAlignment;
import net.sf.jasperreports.pdf.type.PdfFieldBorderStyleEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class StandardPdfField implements PdfField
{

	protected StandardPdfProducer pdfProducer;
	private BaseField field;
	
	public StandardPdfField(StandardPdfProducer pdfProducer, BaseField field)
	{
		this.pdfProducer = pdfProducer;
		this.field = field;
	}

	@Override
	public void setBorderWidth(float borderWidth)
	{
		float width = borderWidth > BaseField.BORDER_WIDTH_THICK ? BaseField.BORDER_WIDTH_THICK : borderWidth;
		field.setBorderWidth(width);
	}

	@Override
	public void setBackgroundColor(Color backcolor)
	{
		field.setBackgroundColor(backcolor);
	}

	@Override
	public void setTextColor(Color forecolor)
	{
		field.setTextColor(forecolor);
	}

	@Override
	public void setAlignment(PdfTextAlignment alignment)
	{
		field.setAlignment(StandardPdfUtils.toPdfAlignment(alignment));
	}

	@Override
	public void setBorderColor(Color lineColor)
	{
		field.setBorderColor(lineColor);
	}

	@Override
	public void setBorderStyle(PdfFieldBorderStyleEnum borderStyle)
	{
		field.setBorderStyle(toBorderStyleValue(borderStyle));
	}
	
	protected int toBorderStyleValue(PdfFieldBorderStyleEnum borderStyle)
	{
		int value;
		switch (borderStyle)
		{
			case SOLID:
				value = PdfBorderDictionary.STYLE_SOLID;
				break;
			case DASHED:
				value = PdfBorderDictionary.STYLE_DASHED;
				break;
			case BEVELED:
				value = PdfBorderDictionary.STYLE_BEVELED;
				break;
			case INSET:
				value = PdfBorderDictionary.STYLE_INSET;
				break;
			case UNDERLINE:
				value = PdfBorderDictionary.STYLE_UNDERLINE;
				break;
			default:
				throw new JRRuntimeException("Unexpected border style: " + borderStyle);
		}
		return value;
	}

	@Override
	public void setReadOnly()
	{
		field.setOptions(field.getOptions() | TextField.READ_ONLY);
	}

	@Override
	public void setText(String value)
	{
		field.setText(value);
	}

	@Override
	public void setFont(Map<Attribute, Object> attributes, Locale locale)
	{
		Font font = pdfProducer.getFont(attributes, locale);
		field.setFont(font.getBaseFont());
	}
	
	@Override
	public void setFontSize(float fontSize)
	{
		field.setFontSize(fontSize);
	}

	@Override
	public void setRotation(int rotation)
	{
		field.setRotation(rotation);
	}

	@Override
	public void setVisible()
	{
		field.setVisibility(TextField.VISIBLE);
	}

}
