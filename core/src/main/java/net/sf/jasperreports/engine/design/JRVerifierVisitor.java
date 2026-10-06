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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.design;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVisitor;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRVerifierVisitor implements JRVisitor
{
	
	private JRVerifier verifier;
	
	/**
	 *
	 */
	public JRVerifierVisitor(JRVerifier verifier)
	{
		this.verifier = verifier;
	}
	
	public JRVerifier getVerifier()
	{
		return verifier;
	}

	@Override
	public void visitBreak(JRBreak breakElement)
	{
		//nothing to do
	}

	@Override
	public void visitCrosstab(JRCrosstab crosstab)
	{
		verifier.verifyCrosstab((JRDesignCrosstab)crosstab);
	}

	@Override
	public void visitElementGroup(JRElementGroup elementGroup)
	{
		//nothing to do
	}

	@Override
	public void visitEllipse(JREllipse ellipse)
	{
		//nothing to do
	}

	@Override
	public void visitFrame(JRFrame frame)
	{
		verifier.verifyFrame(frame);
	}

	@Override
	public void visitImage(JRImage image)
	{
		verifier.verifyImage(image);
	}

	@Override
	public void visitLine(JRLine line)
	{
		//nothing to do
	}

	@Override
	public void visitRectangle(JRRectangle rectangle)
	{
		//nothing to do
	}

	@Override
	public void visitStaticText(JRStaticText staticText)
	{
		verifier.verifyStaticText(staticText);
	}

	@Override
	public void visitSubreport(JRSubreport subreport)
	{
		verifier.verifySubreport(subreport);
	}

	@Override
	public void visitTextField(JRTextField textField)
	{
		verifier.verifyTextField(textField);
	}
	
	@Override
	public void visitComponentElement(JRComponentElement componentElement)
	{
		verifier.verifyComponentElement(componentElement);
	}

	@Override
	public void visitGenericElement(JRGenericElement element)
	{
		verifier.verifyGenericElement(element);
	}

}
