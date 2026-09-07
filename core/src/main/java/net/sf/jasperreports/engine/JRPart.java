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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignPart;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartEvaluationTime;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	// "evaluationTime" must be first: it is @JsonUnwrapped and its properties serialize as XML attributes of <part>.
	// Jackson XML writes attributes only while the opening tag is still open, so they must be emitted before any child elements.
	JRXmlConstants.ATTRIBUTE_evaluationTime,
	JRXmlConstants.ATTRIBUTE_uuid,
	"properties",
	JRXmlConstants.ELEMENT_property,
	"propertyExpressions",
	JRXmlConstants.ELEMENT_propertyExpression,
	JRXmlConstants.ELEMENT_printWhenExpression,
	JRXmlConstants.ELEMENT_partNameExpression,
	"component"
	})
@JsonDeserialize(as = JRDesignPart.class)
public interface JRPart extends JRPropertiesHolder, JRCloneable, JRIdentifiable
{


	/**
	 * Returns the list of dynamic/expression-based properties for this report part.
	 * 
	 * @return an array containing the expression-based properties of this report part
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_propertyExpression)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRPropertyExpression[] getPropertyExpressions();

	/**
	 * Returns the boolean expression that specifies if the part will be displayed.
	 */
	JRExpression getPrintWhenExpression();

	JRExpression getPartNameExpression();
	

	/**
	 * Returns the component instance wrapped by this part.
	 * 
	 * @return the component instance
	 */
	PartComponent getComponent();


	/**
	 * Determines the moment at which this part is to be evaluated.
	 * 
	 * @return the evaluation time of this part
	 */
	@JsonUnwrapped
	PartEvaluationTime getEvaluationTime();
	
}
