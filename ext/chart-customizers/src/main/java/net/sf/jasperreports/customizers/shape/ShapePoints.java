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
package net.sf.jasperreports.customizers.shape;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * Specifies a list of points representing a shape.
 * 
 * @author Marco Orlandin (dejawho2@users.sourceforge.net)
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ShapePoints
{
	/**
	 * The points that represent the shape.
	 */
	private List<Point> points;

	public List<Point> getPoints() 
	{
		return points;
	}

	public void setPoints(List<Point> points) 
	{
		this.points = points;
	}

	/**
	 * Build a {@link ShapePoints} from its JSON string.
	 * 
	 * @param encodedShapePoints the JSON string of the class
	 * @return a {@link ShapePoints} or null if the JSON is invalid
	 */
	public static ShapePoints decode(String encodedShapePoints)
	{
		ShapePoints result = null;
		if (encodedShapePoints != null)
		{
			try 
			{
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
				result = mapper.readValue(encodedShapePoints, ShapePoints.class);  
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			} 
		}
		return result;
	}

	/**
	 * Create a JSON string from a {@link ShapePoints}.
	 * 
	 * @return its JSON string or null if the parameter is null
	 */
	public String encode()
	{
		String result = null;
		ObjectMapper mapper = new ObjectMapper();
		try 
		{
			result = mapper.writeValueAsString(this);
		}
		catch (JsonProcessingException e) 
		{
			throw new JRRuntimeException(e);
		}
		return result;
	}
}
