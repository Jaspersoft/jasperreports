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
package net.sf.jasperreports.pdf.classic.producer;

import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.RadioCheckField;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.common.PdfRadioCheck;
import net.sf.jasperreports.pdf.type.PdfFieldCheckTypeEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardRadioCheck extends StandardPdfField implements PdfRadioCheck
{

	private RadioCheckField radioCheckField;
	
	public StandardRadioCheck(StandardPdfProducer pdfProducer, RadioCheckField radioCheckField)
	{
		super(pdfProducer, radioCheckField);
		this.radioCheckField = radioCheckField;
	}

	public RadioCheckField getRadioCheckField()
	{
		return radioCheckField;
	}
	
	@Override
	public void setCheckType(PdfFieldCheckTypeEnum checkType)
	{
		radioCheckField.setCheckType(toCheckType(checkType));
	}
	
	protected int toCheckType(PdfFieldCheckTypeEnum checkType)
	{
		int value;
		switch (checkType)
		{
			case CHECK:
				value = RadioCheckField.TYPE_CHECK;
				break;
			case CIRCLE:
				value = RadioCheckField.TYPE_CIRCLE;
				break;
			case CROSS:
				value = RadioCheckField.TYPE_CROSS;
				break;
			case DIAMOND:
				value = RadioCheckField.TYPE_DIAMOND;
				break;
			case SQUARE:
				value = RadioCheckField.TYPE_SQUARE;
				break;
			case STAR:
				value = RadioCheckField.TYPE_STAR;
				break;
			default:
				throw new JRRuntimeException("Unknown check type: " + checkType);
		}
		return value;
	}

	@Override
	public void setOnValue(String value)
	{
		radioCheckField.setOnValue(value);
	}

	@Override
	public void setChecked(boolean checked)
	{
		radioCheckField.setChecked(checked);
	}

	@Override
	public void add()
	{
		try
		{
			PdfFormField ck = radioCheckField.getFullField();
			pdfProducer.getPdfWriter().addAnnotation(ck);
		}
		catch (Exception e)
		{
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public void addToGroup() throws IOException
	{
		PdfFormField radioGroup = pdfProducer.getRadioGroup(radioCheckField);
		try
		{
			radioGroup.addKid(radioCheckField.getKidField());
		}
		catch (DocumentException e)
		{
			throw new JRRuntimeException(e);
		}
	}

}
