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
package net.sf.jasperreports.renderers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class AwtComponentRendererImpl extends AbstractRenderer implements Graphics2DRenderable, DimensionRenderable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Component component;


	public AwtComponentRendererImpl(Component component) 
	{
		this.component = component;
	}
	
	@Override
	public Dimension2D getDimension(JasperReportsContext jasperReportsContext)
	{
		return component.getSize();
	}

	@Override
	public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle) 
	{
		AffineTransform origTransform = grx.getTransform();
		try
		{
			Dimension size = component.getSize();

			grx.translate(rectangle.getX(), rectangle.getY());
			if (rectangle.getWidth() != size.getWidth() 
					|| rectangle.getHeight() != size.getHeight())
			{
				grx.scale(rectangle.getWidth() / size.getWidth(), 
						rectangle.getHeight() / size.getHeight());
			}
			component.paint(grx);
		}
		catch (Exception e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			grx.setTransform(origTransform);
		}
	}
}
