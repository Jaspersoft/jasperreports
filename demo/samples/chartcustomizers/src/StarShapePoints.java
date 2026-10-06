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
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.customizers.shape.Point;
import net.sf.jasperreports.customizers.shape.ShapePoints;

/**
 * @author Marco Orlandin (dejawho2@users.sourceforge.net)
 */
public class StarShapePoints extends ShapePoints 
{
	public static final StarShapePoints INSTANCE = new StarShapePoints();
	
	private StarShapePoints()
	{
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(0, 707));
		points.add(new Point(747, 707));

		points.add(new Point(971, 0));
		points.add(new Point(1201, 707));
		points.add(new Point(1943, 707));
		points.add(new Point(1343, 1143));
		points.add(new Point(1572, 1849));
		points.add(new Point(971, 1414));
		points.add(new Point(371, 1849));
		points.add(new Point(600, 1143));
		points.add(new Point(0, 707));
		setPoints(points);
	}
}
