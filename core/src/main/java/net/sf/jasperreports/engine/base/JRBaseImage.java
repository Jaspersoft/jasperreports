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
package net.sf.jasperreports.engine.base;

import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * The actual implementation of a graphic element representing an image.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseImage extends JRBaseGraphicElement implements JRImage
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/*
	 * Image properties
	 */

	public static final String PROPERTY_LAZY = "isLazy";
	
	public static final String PROPERTY_ON_ERROR_TYPE = "onErrorType";
	
	public static final String PROPERTY_USING_CACHE = "isUsingCache";
	
	
	/**
	 *
	 */
	protected ScaleImageEnum scaleImage;
	protected RotationEnum rotation;
	protected HorizontalImageAlignEnum horizontalImageAlign;
	protected VerticalImageAlignEnum verticalImageAlign;
	protected Boolean isUsingCache;
	protected boolean isLazy;
	protected OnErrorTypeEnum onErrorType;
	protected EvaluationTimeEnum evaluationTime;
	protected String linkType;
	protected String linkTarget;
	private JRHyperlinkParameter[] hyperlinkParameters;

	/**
	 *
	 */
	protected JRLineBox lineBox;

	/**
	 *
	 */
	protected String evaluationGroup;
	protected JRExpression expression;
	protected JRExpression anchorNameExpression;
	protected JRExpression bookmarkLevelExpression;
	protected JRExpression hyperlinkReferenceExpression;
	protected JRExpression hyperlinkWhenExpression;
	protected JRExpression hyperlinkAnchorExpression;
	protected JRExpression hyperlinkPageExpression;
	private JRExpression hyperlinkTooltipExpression;

	/**
	 * The bookmark level for the anchor associated with this image.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity

	/**
	 *
	 *
	protected JRBaseImage()
	{
		super();
	}
		

	/**
	 * Initializes properties that are specific to images. Common properties are initialized by its
	 * parent constructors.
	 * @param image an element whose properties are copied to this element. Usually it is a
	 * {@link net.sf.jasperreports.engine.design.JRDesignImage} that must be transformed into an
	 * <tt>JRBaseImage</tt> at compile time.
	 * @param factory a factory used in the compile process
	 */
	protected JRBaseImage(JRImage image, JRBaseObjectFactory factory)
	{
		super(image, factory);
		
		scaleImage = image.getOwnScaleImage();
		rotation = image.getOwnRotation();
		horizontalImageAlign = image.getOwnHorizontalImageAlign();
		verticalImageAlign = image.getOwnVerticalImageAlign();
		isUsingCache = image.getUsingCache();
		isLazy = image.isLazy();
		onErrorType = image.getOnErrorType();
		evaluationTime = image.getEvaluationTime();
		linkType = image.getLinkType();
		linkTarget = image.getLinkTarget();
		hyperlinkParameters = JRBaseHyperlink.copyHyperlinkParameters(image, factory);

		lineBox = image.getLineBox().clone(this);

		evaluationGroup = image.getEvaluationGroup();
		expression = factory.getExpression(image.getExpression());
		anchorNameExpression = factory.getExpression(image.getAnchorNameExpression());
		bookmarkLevelExpression = factory.getExpression(image.getBookmarkLevelExpression());
		hyperlinkReferenceExpression = factory.getExpression(image.getHyperlinkReferenceExpression());
		hyperlinkWhenExpression = factory.getExpression(image.getHyperlinkWhenExpression());
		hyperlinkAnchorExpression = factory.getExpression(image.getHyperlinkAnchorExpression());
		hyperlinkPageExpression = factory.getExpression(image.getHyperlinkPageExpression());
		hyperlinkTooltipExpression = factory.getExpression(image.getHyperlinkTooltipExpression());
		bookmarkLevel = image.getBookmarkLevel();
	}


	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}

	@Override
	public ScaleImageEnum getScaleImage()
	{
		return getStyleResolver().getScaleImage(this);
	}

	@Override
	public ScaleImageEnum getOwnScaleImage()
	{
		return this.scaleImage;
	}

	@Override
	public void setScaleImage(ScaleImageEnum scaleImage)
	{
		Object old = this.scaleImage;
		this.scaleImage = scaleImage;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_SCALE_IMAGE, old, this.scaleImage);
	}

	@Override
	public RotationEnum getRotation()
	{
		return getStyleResolver().getRotation(this);
	}

	@Override
	public RotationEnum getOwnRotation()
	{
		return this.rotation;
	}

	@Override
	public void setRotation(RotationEnum rotation)
	{
		Object old = this.rotation;
		this.rotation = rotation;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_ROTATION, old, this.rotation);
	}

	@Override
	public HorizontalImageAlignEnum getHorizontalImageAlign()
	{
		return getStyleResolver().getHorizontalImageAlign(this);
	}
		
	@Override
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign()
	{
		return horizontalImageAlign;
	}
		
	@Override
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalImageAlign)
	{
		Object old = this.horizontalImageAlign;
		this.horizontalImageAlign = horizontalImageAlign;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_HORIZONTAL_IMAGE_ALIGNMENT, old, this.horizontalImageAlign);
	}

	@Override
	public VerticalImageAlignEnum getVerticalImageAlign()
	{
		return getStyleResolver().getVerticalImageAlign(this);
	}
		
	@Override
	public VerticalImageAlignEnum getOwnVerticalImageAlign()
	{
		return verticalImageAlign;
	}
		
	@Override
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalImageAlign)
	{
		Object old = this.verticalImageAlign;
		this.verticalImageAlign = verticalImageAlign;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_VERTICAL_IMAGE_ALIGNMENT, old, this.verticalImageAlign);
	}

	@Override
	public Boolean getUsingCache()
	{
		return isUsingCache;
	}

	@Override
	public void setUsingCache(Boolean isUsingCache)
	{
		Object old = this.isUsingCache;
		this.isUsingCache = isUsingCache;
		getEventSupport().firePropertyChange(PROPERTY_USING_CACHE, old, this.isUsingCache);
	}

	@Override
	public boolean isLazy()
	{
		return isLazy;
	}

	@Override
	public void setLazy(boolean isLazy)
	{
		boolean old = this.isLazy;
		this.isLazy = isLazy;
		getEventSupport().firePropertyChange(PROPERTY_LAZY, old, this.isLazy);
	}

	@Override
	public OnErrorTypeEnum getOnErrorType()
	{
		return this.onErrorType;
	}

	@Override
	public void setOnErrorType(OnErrorTypeEnum onErrorType)
	{
		OnErrorTypeEnum old = this.onErrorType;
		this.onErrorType = onErrorType;
		getEventSupport().firePropertyChange(PROPERTY_ON_ERROR_TYPE, old, this.onErrorType);
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}
		
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(this);
	}
		
	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(this);
	}
		
	@Override
	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}
		
	@Override
	public JRExpression getExpression()
	{
		return expression;
	}

	@Override
	public JRExpression getAnchorNameExpression()
	{
		return anchorNameExpression;
	}
	
	@Override
	public JRExpression getBookmarkLevelExpression()
	{
		return bookmarkLevelExpression;
	}
	

	@Override
	public JRExpression getHyperlinkReferenceExpression()
	{
		return hyperlinkReferenceExpression;
	}

	@Override
	public JRExpression getHyperlinkWhenExpression()
	{
		return hyperlinkWhenExpression;
	}

	@Override
	public JRExpression getHyperlinkAnchorExpression()
	{
		return hyperlinkAnchorExpression;
	}

	@Override
	public JRExpression getHyperlinkPageExpression()
	{
		return hyperlinkPageExpression;
	}
	
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitImage(this);
	}

	
	@Override
	public int getBookmarkLevel()
	{
		return bookmarkLevel;
	}

	
	@Override
	public Float getDefaultLineWidth() 
	{
		return JRPen.LINE_WIDTH_0;
	}

	
	@Override
	public String getLinkType()
	{
		return linkType;
	}

	@Override
	public String getLinkTarget()
	{
		return linkTarget;
	}


	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters()
	{
		return hyperlinkParameters;
	}
	
	
	@Override
	public JRExpression getHyperlinkTooltipExpression()
	{
		return hyperlinkTooltipExpression;
	}

	
	@Override
	public Object clone() 
	{
		JRBaseImage clone = (JRBaseImage)super.clone();
		clone.lineBox = lineBox.clone(clone);
		clone.hyperlinkParameters = JRCloneUtils.cloneArray(hyperlinkParameters);
		clone.expression = JRCloneUtils.nullSafeClone(expression);
		clone.anchorNameExpression = JRCloneUtils.nullSafeClone(anchorNameExpression);
		clone.bookmarkLevelExpression = JRCloneUtils.nullSafeClone(bookmarkLevelExpression);
		clone.hyperlinkReferenceExpression = JRCloneUtils.nullSafeClone(hyperlinkReferenceExpression);
		clone.hyperlinkWhenExpression = JRCloneUtils.nullSafeClone(hyperlinkWhenExpression);
		clone.hyperlinkAnchorExpression = JRCloneUtils.nullSafeClone(hyperlinkAnchorExpression);
		clone.hyperlinkPageExpression = JRCloneUtils.nullSafeClone(hyperlinkPageExpression);
		clone.hyperlinkTooltipExpression = JRCloneUtils.nullSafeClone(hyperlinkTooltipExpression);
		return clone;
	}
}
