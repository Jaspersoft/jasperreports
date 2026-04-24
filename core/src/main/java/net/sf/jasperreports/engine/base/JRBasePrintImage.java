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
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.renderers.Renderable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBasePrintImage extends JRBasePrintGraphicElement implements JRPrintImage
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	protected Renderable renderable;
	protected ScaleImageEnum scaleImage;
	protected RotationEnum rotation;
	protected Boolean isUsingCache;
	protected HorizontalImageAlignEnum horizontalImageAlign;
	protected VerticalImageAlignEnum verticalImageAlign;
	protected OnErrorTypeEnum onErrorType;
	protected JRLineBox lineBox;
	protected String anchorName;
	protected String linkType;
	protected String linkTarget;
	protected String hyperlinkReference;
	protected String hyperlinkAnchor;
	protected Integer hyperlinkPage;
	protected String hyperlinkTooltip;
	protected JRPrintHyperlinkParameters hyperlinkParameters;

	/**
	 * The bookmark level for the anchor associated with this field.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity

	
	/**
	 *
	 */
	public JRBasePrintImage(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);

		lineBox = new JRBaseLineBox(this);
	}


	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}
	
	@Override
	public Renderable getRenderer()
	{
		return renderable;
	}
		
	@Override
	public void setRenderer(Renderable renderable)
	{
		this.renderable = renderable;
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
		this.scaleImage = scaleImage;
	}

	@Override
	public RotationEnum getRotation()
	{
		return getStyleResolver().getRotation(this);
	}
		
	@Override
	public RotationEnum getOwnRotation()
	{
		return rotation;
	}

	@Override
	public void setRotation(RotationEnum rotation)
	{
		this.rotation = rotation;
	}

	@Override
	public boolean isUsingCache()
	{
		return isUsingCache == null ? true : isUsingCache;
	}

	@Override
	public void setUsingCache(boolean isUsingCache)
	{
		this.isUsingCache = isUsingCache;
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
		this.horizontalImageAlign = horizontalImageAlign;
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
		this.verticalImageAlign = verticalImageAlign;
	}

	@Override
	public OnErrorTypeEnum getOnErrorType()
	{
		return this.onErrorType;
	}

	@Override
	public void setOnErrorType(OnErrorTypeEnum onErrorType)
	{
		this.onErrorType = onErrorType;
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
	public Float getDefaultLineWidth() 
	{
		return JRPen.LINE_WIDTH_0;
	}

	@Override
	public String getAnchorName()
	{
		return anchorName;
	}
		
	@Override
	public void setAnchorName(String anchorName)
	{
		this.anchorName = anchorName;
	}
		
	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(getLinkType());
	}
		
	@Override
	public void setHyperlinkType(HyperlinkTypeEnum hyperlinkType)
	{
		setLinkType(JRHyperlinkHelper.getLinkType(hyperlinkType));
	}

	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(getLinkTarget());
	}
		
	@Override
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		setLinkTarget(JRHyperlinkHelper.getLinkTarget(hyperlinkTarget));
	}

	@Override
	public String getHyperlinkReference()
	{
		return hyperlinkReference;
	}
		
	@Override
	public void setHyperlinkReference(String hyperlinkReference)
	{
		this.hyperlinkReference = hyperlinkReference;
	}
		
	@Override
	public String getHyperlinkAnchor()
	{
		return hyperlinkAnchor;
	}
		
	@Override
	public void setHyperlinkAnchor(String hyperlinkAnchor)
	{
		this.hyperlinkAnchor = hyperlinkAnchor;
	}
		
	@Override
	public Integer getHyperlinkPage()
	{
		return hyperlinkPage;
	}
		
	@Override
	public void setHyperlinkPage(Integer hyperlinkPage)
	{
		this.hyperlinkPage = hyperlinkPage;
	}
		
	/**
	 *
	 */
	public void setHyperlinkPage(String hyperlinkPage)
	{
		this.hyperlinkPage = Integer.valueOf(hyperlinkPage);
	}

	@Override
	public int getBookmarkLevel()
	{
		return bookmarkLevel;
	}

	@Override
	public void setBookmarkLevel(int bookmarkLevel)
	{
		this.bookmarkLevel = bookmarkLevel;
	}

	@Override
	public JRPrintHyperlinkParameters getHyperlinkParameters()
	{
		return hyperlinkParameters;
	}

	@Override
	public void setHyperlinkParameters(JRPrintHyperlinkParameters hyperlinkParameters)
	{
		this.hyperlinkParameters = hyperlinkParameters;
	}

	
	/**
	 * Adds a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to add
	 * @see #getHyperlinkParameters()
	 * @see JRPrintHyperlinkParameters#addParameter(JRPrintHyperlinkParameter)
	 */
	public void addHyperlinkParameter(JRPrintHyperlinkParameter parameter)
	{
		if (hyperlinkParameters == null)
		{
			hyperlinkParameters = new JRPrintHyperlinkParameters();
		}
		hyperlinkParameters.addParameter(parameter);
	}

	@Override
	public String getLinkType()
	{
		return linkType;
	}

	@Override
	public void setLinkType(String linkType)
	{
		this.linkType = linkType;
	}
	
	@Override
	public String getLinkTarget()
	{
		return linkTarget;
	}

	@Override
	public void setLinkTarget(String linkTarget)
	{
		this.linkTarget = linkTarget;
	}
	
	@Override
	public String getHyperlinkTooltip()
	{
		return hyperlinkTooltip;
	}

	@Override
	public void setHyperlinkTooltip(String hyperlinkTooltip)
	{
		this.hyperlinkTooltip = hyperlinkTooltip;
	}

	@Override
	public <T> void accept(PrintElementVisitor<T> visitor, T arg)
	{
		visitor.visit(this, arg);
	}
}
