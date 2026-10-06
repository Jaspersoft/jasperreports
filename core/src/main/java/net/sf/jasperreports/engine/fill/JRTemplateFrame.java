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

import java.awt.Color;

import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;

/**
 * Frame information shared by multiple print frame objects.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see net.sf.jasperreports.engine.fill.JRTemplatePrintFrame
 */
public class JRTemplateFrame extends JRTemplateElement implements JRBoxContainer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected JRLineBox lineBox;


	/**
	 * Creates a template frame for a crosstab cell.
	 * 
	 * @param cell the cell
	 */
	public JRTemplateFrame(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider, JRFillCellContents cell)
	{
		super(origin, defaultStyleProvider);
	
		//FIXME properties?
		parentStyle = cell.getStyle();
		
		setBackcolor(cell.getBackcolor());
		setMode(cell.getMode());
		copyBox(cell.getLineBox());
	}
	
	
	/**
	 * Creates a template frame for a frame.
	 * 
	 * @param frame the frame
	 */
	public JRTemplateFrame(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider, JRFillFrame frame)
	{
		super(origin, defaultStyleProvider);

		setElement(frame);
		
		copyBox(frame.getLineBox());
	}

	/**
	 * Creates a template frame.
	 * 
	 * @param origin the origin of the elements that will use this template
	 * @param defaultStyleProvider the default style provider to use for
	 * this template
	 */
	public JRTemplateFrame(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider)
	{
		super(origin, defaultStyleProvider);
		
		this.lineBox = new JRBaseLineBox(this);
	}
	
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	/**
	 *
	 */
	public void copyBox(JRLineBox lineBox)
	{
		this.lineBox = lineBox.clone(this);
	}

	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}


	@Override
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		addTemplateHash(hash);
		hash.addIdentical(lineBox);
		return hash.getHashCode();
	}


	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRTemplateFrame))
		{
			return false;
		}
		
		JRTemplateFrame template = (JRTemplateFrame) object;
		return templateIdentical(template)
				&& ObjectUtils.identical(lineBox, template.lineBox);
	}
	
	@Override
	public void populateStyle()
	{
		super.populateStyle();
		
		if (lineBox != null)
		{
			lineBox.populateStyle();
		}
	}
}
