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
package net.sf.jasperreports.engine;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * An area on an image.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRPrintImageAreaHyperlink
 */
public class JRPrintImageArea implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final String EXCEPTION_MESSAGE_KEY_CIRCLE_COORDINATES_ERROR = "engine.print.image.area.circle.coordinates.error";
	private static final String EXCEPTION_MESSAGE_KEY_POLYGON_COORDINATES_ERROR = "engine.print.image.area.polygon.coordinates.error";
	private static final String EXCEPTION_MESSAGE_KEY_RECTANGLE_COORDINATES_ERROR = "engine.print.image.area.rectangle.coordinates.error";
	private static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_HTML_SHAPE = "engine.print.image.area.unknown.html.shape";
	private static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_SHAPE = "engine.print.image.area.unknown.shape";
	
	public final static byte SHAPE_DEFAULT = 0;
	public final static byte SHAPE_RECTANGLE = 1;
	public final static byte SHAPE_CIRCLE = 2;
	public final static byte SHAPE_POLYGON = 3;
	
	public final static String SHAPE_HTML_DEFAULT = "default";
	public final static String SHAPE_HTML_RECTANGLE = "rect";
	public final static String SHAPE_HTML_CIRCLE = "circle";
	public final static String SHAPE_HTML_POLYGON = "poly";
	
	private final static Map<String,Byte> htmlShapes;
	
	static
	{
		htmlShapes = new HashMap<>();
		htmlShapes.put(SHAPE_HTML_DEFAULT, SHAPE_DEFAULT);
		htmlShapes.put(SHAPE_HTML_RECTANGLE, SHAPE_RECTANGLE);
		htmlShapes.put(SHAPE_HTML_CIRCLE, SHAPE_CIRCLE);
		htmlShapes.put(SHAPE_HTML_POLYGON, SHAPE_POLYGON);
	}
	
	
	/**
	 * Returns the shape constant corresponding the HTML are shape type.
	 * 
	 * @param htmlShape the HTML are shape type
	 * @return the corresponding shape constant
	 */
	public static byte getShape(String htmlShape)
	{
		Byte shape = htmlShapes.get(htmlShape.toLowerCase());
		if (shape == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNKNOWN_HTML_SHAPE,
					new Object[]{htmlShape});
		}
		return shape;
	}
	
	
	/**
	 * Returns the HTML shape type corresponding to a shape type.
	 * 
	 * @param shape the shape type
	 * @return the HTML shape type
	 */
	public static String getHtmlShape(byte shape)
	{
		String htmlShape;
		switch (shape)
		{
			case SHAPE_DEFAULT:
				htmlShape = SHAPE_HTML_DEFAULT;
				break;
			case SHAPE_RECTANGLE:
				htmlShape = SHAPE_HTML_RECTANGLE;
				break;
			case SHAPE_CIRCLE:
				htmlShape = SHAPE_HTML_CIRCLE;
				break;
			case SHAPE_POLYGON:
				htmlShape = SHAPE_HTML_POLYGON;
				break;
			default:
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_SHAPE,
						new Object[]{shape});
		}
		return htmlShape;
	}
	
	private byte shape = SHAPE_DEFAULT;
	private int[] coordinates;
	
	private transient Shape cachedAWTShape;
	
	/**
	 * Creates a blank image area.
	 */
	public JRPrintImageArea()
	{
	}


	/**
	 * Returns the shape type.
	 * 
	 * @return the shape type
	 */
	public byte getShape()
	{
		return shape;
	}

	
	/**
	 * Sets the area shape type.
	 * 
	 * @param shape the shape type, one of
	 * <ul>
	 * <li>{@link #SHAPE_DEFAULT SHAPE_DEFAULT}</li>
	 * <li>{@link #SHAPE_RECTANGLE SHAPE_RECTANGLE}</li>
	 * <li>{@link #SHAPE_POLYGON SHAPE_POLYGON}</li>
	 * <li>{@link #SHAPE_CIRCLE SHAPE_CIRCLE}</li>
	 * </ul>
	 */
	public void setShape(byte shape)
	{
		this.shape = shape;
	}

	
	/**
	 * Returns the shape coordinates.
	 * 
	 * @return the shape coordinates
	 */
	public int[] getCoordinates()
	{
		return coordinates;
	}

	
	/**
	 * Sets the shape coordinates.
	 * 
	 * @param coordinates the shape coordinates
	 */
	public void setCoordinates(int[] coordinates)
	{
		this.coordinates = coordinates;
	}
	
	
	/**
	 * Decides whether a specific point is inside this area.
	 * 
	 * @param x the X coordinate of the point
	 * @param y the Y coordinate of the point
	 * @return whether the point is inside this area
	 */
	public boolean containsPoint(int x, int y)
	{
		boolean contains;
		if (hasAWTShape())
		{
			ensureAWTShape();
			contains = cachedAWTShape.contains(x, y);
		}
		else
		{
			contains = true;
		}
		return contains;
	}
	
	
	protected void ensureAWTShape()
	{
		if (cachedAWTShape == null)
		{
			cachedAWTShape = createAWTShape();
		}
	}
	
	
	protected boolean hasAWTShape()
	{
		return shape != SHAPE_DEFAULT;
	}
	
	
	protected Shape createAWTShape()
	{
		Shape awtShape;
		switch (shape)
		{
			case SHAPE_RECTANGLE:
				awtShape = createAWTRectangle();
				break;
			case SHAPE_CIRCLE:
				awtShape = createAWTCircle();
				break;
			case SHAPE_POLYGON:
				awtShape = createAWTPolygon();
				break;
			default:
				awtShape = null;
				break;
		}
		return awtShape;
	}


	protected Shape createAWTRectangle()
	{
		if (coordinates == null || coordinates.length != 4)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_RECTANGLE_COORDINATES_ERROR,
					(Object[])null);
		}
		
		return new Rectangle(
				coordinates[0], 
				coordinates[1], 
				coordinates[2] - coordinates[0],
				coordinates[3] - coordinates[1]);
	}


	private Shape createAWTCircle()
	{
		if (coordinates == null || coordinates.length != 3)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CIRCLE_COORDINATES_ERROR,
					(Object[])null);
		}
		
		return new Ellipse2D.Float(coordinates[0], coordinates[1], coordinates[2], coordinates[2]);
	}


	private Shape createAWTPolygon()
	{
		if (coordinates == null || coordinates.length == 0 || coordinates.length % 2 != 0)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_POLYGON_COORDINATES_ERROR,
					(Object[])null);
		}
		
		Polygon polygon = new Polygon();
		
		int i;
		for (i = 0; i < coordinates.length - 2; i += 2)
		{
			polygon.addPoint(coordinates[i], coordinates[i + 1]);
		}
		if (coordinates[i] != coordinates[0] || coordinates[i + 1] != coordinates[1])
		{
			polygon.addPoint(coordinates[i], coordinates[i + 1]);
		}

		return polygon;
	}
	
}
