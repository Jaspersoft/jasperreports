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
package net.sf.jasperreports.okapi;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JREvaluation;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.Designated;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * Base class of the Okapi barcode components, holding the settings every barcode type has.
 * <p>
 * Sizes are in report units. The barcode is drawn as an image whose rotation and alignment
 * come from this component when set, and from the element style otherwise.
 * </p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class OkapiComponent implements Component, JREvaluation, Serializable, JRCloneable, JRChangeEventsSupport, Designated
{

	public static final String COMPONENT_NAME_PREFIX = "okapi:";

	public static final String COMPONENT_DESIGNATION = "net.sf.jasperreports.component.element:Okapi";

	// used as prefix for specific Okapi component properties
	public static final String PROPERTY_PREFIX =
		JRPropertiesUtil.PROPERTY_PREFIX + "components.okapi.";

	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";
	public static final String PROPERTY_EVALUATION_GROUP = "evaluationGroup";
	public static final String PROPERTY_CODE_EXPRESSION = "codeExpression";
	public static final String PROPERTY_ROTATION = "rotation";
	public static final String PROPERTY_HORIZONTAL_IMAGE_ALIGN = "horizontalImageAlign";
	public static final String PROPERTY_VERTICAL_IMAGE_ALIGN = "verticalImageAlign";
	public static final String PROPERTY_QUIET_ZONE_HORIZONTAL = "quietZoneHorizontal";
	public static final String PROPERTY_QUIET_ZONE_VERTICAL = "quietZoneVertical";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private transient JRPropertyChangeSupport eventSupport;

	private EvaluationTimeEnum evaluationTime;
	private String evaluationGroup;
	private JRExpression codeExpression;
	private RotationEnum rotation;
	private HorizontalImageAlignEnum horizontalImageAlign;
	private VerticalImageAlignEnum verticalImageAlign;
	private Double quietZoneHorizontal;
	private Double quietZoneVertical;

	public OkapiComponent()
	{
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}

	public void setEvaluationTime(EvaluationTimeEnum evaluationTime)
	{
		Object old = this.evaluationTime;
		this.evaluationTime = evaluationTime;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME,
				old, this.evaluationTime);
	}

	@Override
	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}

	public void setEvaluationGroup(String evaluationGroup)
	{
		Object old = this.evaluationGroup;
		this.evaluationGroup = evaluationGroup;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_GROUP,
				old, this.evaluationGroup);
	}

	public JRExpression getCodeExpression()
	{
		return codeExpression;
	}

	public void setCodeExpression(JRExpression codeExpression)
	{
		Object old = this.codeExpression;
		this.codeExpression = codeExpression;
		getEventSupport().firePropertyChange(PROPERTY_CODE_EXPRESSION,
				old, this.codeExpression);
	}

	/**
	 * The rotation of the barcode image; when not set, the rotation of the element style applies.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_rotation, isAttribute = true)
	public RotationEnum getRotation()
	{
		return rotation;
	}

	public void setRotation(RotationEnum rotation)
	{
		Object old = this.rotation;
		this.rotation = rotation;
		getEventSupport().firePropertyChange(PROPERTY_ROTATION,
				old, this.rotation);
	}

	/**
	 * The horizontal alignment of a barcode narrower than the element, relative to the rotated image;
	 * when not set, the alignment of the element style applies, then left.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_hImageAlign, isAttribute = true)
	public HorizontalImageAlignEnum getHorizontalImageAlign()
	{
		return horizontalImageAlign;
	}

	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalImageAlign)
	{
		Object old = this.horizontalImageAlign;
		this.horizontalImageAlign = horizontalImageAlign;
		getEventSupport().firePropertyChange(PROPERTY_HORIZONTAL_IMAGE_ALIGN,
				old, this.horizontalImageAlign);
	}

	/**
	 * The vertical alignment of a barcode shorter than the element, relative to the rotated image;
	 * when not set, the alignment of the element style applies, then top.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_vImageAlign, isAttribute = true)
	public VerticalImageAlignEnum getVerticalImageAlign()
	{
		return verticalImageAlign;
	}

	public void setVerticalImageAlign(VerticalImageAlignEnum verticalImageAlign)
	{
		Object old = this.verticalImageAlign;
		this.verticalImageAlign = verticalImageAlign;
		getEventSupport().firePropertyChange(PROPERTY_VERTICAL_IMAGE_ALIGN,
				old, this.verticalImageAlign);
	}

	/**
	 * The width of the quiet zone on each side, in report units, rounded up to whole modules;
	 * when not set, the standard quiet zone of the symbology applies.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getQuietZoneHorizontal()
	{
		return quietZoneHorizontal;
	}

	public void setQuietZoneHorizontal(Double quietZoneHorizontal)
	{
		Object old = this.quietZoneHorizontal;
		this.quietZoneHorizontal = quietZoneHorizontal;
		getEventSupport().firePropertyChange(PROPERTY_QUIET_ZONE_HORIZONTAL,
				old, this.quietZoneHorizontal);
	}

	/**
	 * The height of the quiet zone above and below the barcode, in report units, rounded up to whole modules;
	 * when not set, the standard quiet zone of the symbology applies.
	 */
	@JsonInclude(Include.NON_NULL)
	@JacksonXmlProperty(isAttribute = true)
	public Double getQuietZoneVertical()
	{
		return quietZoneVertical;
	}

	public void setQuietZoneVertical(Double quietZoneVertical)
	{
		Object old = this.quietZoneVertical;
		this.quietZoneVertical = quietZoneVertical;
		getEventSupport().firePropertyChange(PROPERTY_QUIET_ZONE_VERTICAL,
				old, this.quietZoneVertical);
	}

	@Override
	public String getDesignation()
	{
		return COMPONENT_DESIGNATION;
	}

	@Override
	public Object clone()
	{
		OkapiComponent clone = null;
		try
		{
			clone = (OkapiComponent) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.codeExpression = JRCloneUtils.nullSafeClone(codeExpression);
		clone.eventSupport = null;
		return clone;
	}

	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}

		return eventSupport;
	}
}
