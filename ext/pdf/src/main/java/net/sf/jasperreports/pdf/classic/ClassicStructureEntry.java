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
package net.sf.jasperreports.pdf.classic;

import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfStructureElement;

import net.sf.jasperreports.pdf.common.PdfStructureEntry;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassicStructureEntry implements PdfStructureEntry
{

	private ClassicPdfStructure pdfStructure;
	private PdfStructureElement element;
	
	public ClassicStructureEntry(ClassicPdfStructure pdfStructure, PdfStructureElement element)
	{
		this.pdfStructure = pdfStructure;
		this.element = element;
	}

	public PdfStructureElement getElement()
	{
		return element;
	}

	@Override
	public void putString(String name, String value)
	{
		element.put(pdfStructure.pdfName(name), new PdfString(value));
	}

	@Override
	public void putArray(String name)
	{
		element.put(pdfStructure.pdfName(name), new PdfArray());
	}

	@Override
	public void setSpan(int colSpan, int rowSpan)
	{
		PdfArray a = new PdfArray();
		PdfDictionary dict = new PdfDictionary();
		if (colSpan > 1)
		{
			dict.put(pdfStructure.pdfName("ColSpan"), new PdfNumber(colSpan));
		}
		if (rowSpan > 1)
		{
			dict.put(pdfStructure.pdfName("RowSpan"), new PdfNumber(rowSpan));
		}
		dict.put(PdfName.O, PdfName.TABLE);
		a.add(dict);
		element.put(PdfName.A, a);
	}

	@Override
	public void setBBox(float llx, float lly, float urx, float ury)
	{
		PdfDictionary dict = new PdfDictionary();
		PdfArray bbox = new PdfArray();
		bbox.add(new PdfNumber(llx));
		bbox.add(new PdfNumber(lly));
		bbox.add(new PdfNumber(urx));
		bbox.add(new PdfNumber(ury));
		dict.put(pdfStructure.pdfName("BBox"), bbox);
		dict.put(pdfStructure.pdfName("Placement"), pdfStructure.pdfName("Block"));
		dict.put(PdfName.O, pdfStructure.pdfName("Layout"));

		PdfObject existing = element.get(PdfName.A);
		if (existing instanceof PdfArray)
		{
			((PdfArray) existing).add(dict);
		}
		else
		{
			PdfArray a = new PdfArray();
			a.add(dict);
			element.put(PdfName.A, a);
		}
	}

	@Override
	public void setScope(String scope)
	{
		PdfObject existing = element.get(PdfName.A);
		if (existing instanceof PdfArray)
		{
			PdfArray a = (PdfArray) existing;
			PdfDictionary tableDict = findTableDict(a);
			if (tableDict != null)
			{
				tableDict.put(pdfStructure.pdfName("Scope"), pdfStructure.pdfName(scope));
			}
			else
			{
				PdfDictionary dict = new PdfDictionary();
				dict.put(pdfStructure.pdfName("Scope"), pdfStructure.pdfName(scope));
				dict.put(PdfName.O, PdfName.TABLE);
				a.add(dict);
			}
		}
		else
		{
			PdfArray a = new PdfArray();
			PdfDictionary dict = new PdfDictionary();
			dict.put(pdfStructure.pdfName("Scope"), pdfStructure.pdfName(scope));
			dict.put(PdfName.O, PdfName.TABLE);
			a.add(dict);
			element.put(PdfName.A, a);
		}
	}

	private PdfDictionary findTableDict(PdfArray a)
	{
		for (int i = 0; i < a.size(); i++)
		{
			PdfObject item = a.getPdfObject(i);
			if (item instanceof PdfDictionary)
			{
				PdfDictionary dict = (PdfDictionary) item;
				if (PdfName.TABLE.equals(dict.get(PdfName.O)))
				{
					return dict;
				}
			}
		}
		return null;
	}

}
