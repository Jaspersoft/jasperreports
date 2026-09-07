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

/*
 * Special thanks to Google 'Summer of Code 2005' program for supporting this development
 * 
 * Contributors:
 * Majid Ali Khan - majidkk@users.sourceforge.net
 * Frank Schönheit - Frank.Schoenheit@Sun.COM
 */
package net.sf.jasperreports.engine.export.oasis;

import java.util.StringTokenizer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class Utility 
{

//	public static String getIndent(int i) 
//	{
//		String indent = "";
//		for (int j=0; j<i; j++) {
//			indent += " ";
//		}
//		return indent;
//	}
	
	protected static String replaceNewLineWithLineBreak(String source)
	{
		String str = null;
		
		if (source != null)
		{
			StringBuilder sb = new StringBuilder();
			StringTokenizer tkzer = new StringTokenizer(source, "\n\t", true);
			String token = null;
			while (tkzer.hasMoreTokens())
			{
				token = tkzer.nextToken();
				if ("\n".equals(token))
				{
					sb.append("<text:line-break/>");
				}
				else if ("\t".equals(token))
				{
					sb.append("<text:tab/>");
				}
				else
				{
					sb.append(token);
				}
			}
			
			str = sb.toString();
		}
		
		return str;
	}
	
	private Utility()
	{
	}

}
