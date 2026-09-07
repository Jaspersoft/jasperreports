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
package net.sf.jasperreports.engine.fill;

import net.sf.jasperreports.engine.JRCommonImage;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.base.JRBasePen;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;


/**
 * Image information shared by multiple print image objects.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @see JRTemplatePrintImage
 */
public class JRTemplateImage extends JRTemplateGraphicElement implements JRCommonImage
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	private ScaleImageEnum scaleImage;
	private RotationEnum rotation;
	private Boolean isUsingCache;
	private HorizontalImageAlignEnum horizontalImageAlign;
	private VerticalImageAlignEnum verticalImageAlign;
	protected boolean isLazy;
	protected OnErrorTypeEnum onErrorType;
	private String linkType;
	private String linkTarget;

	/**
	 *
	 */
	private JRLineBox lineBox;
	

	/**
	 *
	 */
	protected JRTemplateImage(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider, JRImage image)
	{
		super(origin, defaultStyleProvider);
		
		setImage(image);
	}


	/**
	 * Creates a template image.
	 * 
	 * @param origin the origin of the elements that will use this template
	 * @param defaultStyleProvider the default style provider to use for
	 * this template
	 */
	public JRTemplateImage(JROrigin origin, JRDefaultStyleProvider defaultStyleProvider)
	{
		super(origin, defaultStyleProvider);
		
		this.lineBox = new JRBaseLineBox(this);
		this.linePen = new JRBasePen(this);
	}

	/**
	 *
	 */
	protected void setImage(JRImage image)
	{
		super.setGraphicElement(image);
		
		lineBox = image.getLineBox().clone(this);

		setScaleImage(image.getOwnScaleImage());
		setRotation(image.getOwnRotation());
		setUsingCache(image.getUsingCache());
		setHorizontalImageAlign(image.getOwnHorizontalImageAlign());
		setVerticalImageAlign(image.getOwnVerticalImageAlign());
		setLazy(image.isLazy());
		setOnErrorType(image.getOnErrorType());
		setLinkType(image.getLinkType());
		setLinkTarget(image.getLinkTarget());
	}

	/**
	 * Copies box attributes.
	 * 
	 * @param box the object to copy attributes from
	 */
	public void copyLineBox(JRLineBox box)
	{
		this.lineBox = box.clone(this);
	}
	
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
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
		return this.rotation;
	}

	@Override
	public void setRotation(RotationEnum rotation)
	{
		this.rotation = rotation;
	}

	/**
	 *
	 */
	public boolean isUsingCache()
	{
		return isUsingCache == null ? true : isUsingCache;
	}

	/**
	 *
	 */
	public void setUsingCache(boolean isUsingCache)
	{
		this.isUsingCache = isUsingCache;
	}

	/**
	 *
	 */
	public void setUsingCache(Boolean isUsingCache)
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
		
	/**
	 *
	 */
	public boolean isLazy()
	{
		return isLazy;
	}

	/**
	 *
	 */
	public void setLazy(boolean isLazy)
	{
		this.isLazy = isLazy;
	}

	/**
	 *
	 */
	public OnErrorTypeEnum getOnErrorType()
	{
		return this.onErrorType;
	}

	/**
	 *
	 */
	public void setOnErrorType(OnErrorTypeEnum onErrorType)
	{
		this.onErrorType = onErrorType;
	}

	
	/**
	 * Retrieves the hyperlink type for the element.
	 * <p>
	 * The actual hyperlink type is determined by {@link #getLinkType() getLinkType()}.
	 * This method can is used to determine whether the hyperlink type is one of the
	 * built-in types or a custom type. 
	 * When hyperlink is of custom type, {@link HyperlinkTypeEnum#CUSTOM CUSTOM} is returned.
	 * </p>
	 * @return one of the hyperlink type constants
	 * @see #getLinkType()
	 */
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(getLinkType());
	}

	
	/**
	 * Sets the link type as a built-in hyperlink type.
	 * 
	 * @param hyperlinkType the built-in hyperlink type
	 * @see #getLinkType()
	 */
	protected void setHyperlinkType(HyperlinkTypeEnum hyperlinkType)
	{
		setLinkType(JRHyperlinkHelper.getLinkType(hyperlinkType));
	}
		
	
	/**
	 *
	 */
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(getLinkTarget());
	}
		
	/**
	 *
	 */
	protected void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		setLinkTarget(JRHyperlinkHelper.getLinkTarget(hyperlinkTarget));
	}

	
	/**
	 * Returns the hyperlink target name.
	 * <p>
	 * The target name can be one of the built-in names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary name.
	 * </p>
	 * @return the hyperlink type
	 */
	public String getLinkTarget()
	{
		return linkTarget;
	}


	/**
	 * Sets the hyperlink target name.
	 * <p>
	 * The target name can be one of the built-in names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary name.
	 * </p>
	 * @param linkTarget the hyperlink target name
	 */
	public void setLinkTarget(String linkTarget)
	{
		this.linkTarget = linkTarget;
	}
	/**
	 * Returns the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @return the hyperlink type
	 */
	public String getLinkType()
	{
		return linkType;
	}


	/**
	 * Sets the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @param linkType the hyperlink type
	 */
	public void setLinkType(String linkType)
	{
		this.linkType = linkType;
	}
	
	
	@Override
	public Float getDefaultLineWidth() 
	{
		return JRPen.LINE_WIDTH_0;
	}

	@Override
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		addGraphicHash(hash);
		hash.add(scaleImage);
		hash.add(rotation);
		hash.add(isUsingCache);
		hash.add(horizontalImageAlign);
		hash.add(verticalImageAlign);
		hash.add(isLazy);
		hash.add(onErrorType);
		hash.add(linkType);
		hash.add(linkTarget);
		hash.addIdentical(lineBox);
		return hash.getHashCode();
	}

	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRTemplateImage))
		{
			return false;
		}
		
		JRTemplateImage template = (JRTemplateImage) object;
		return graphicIdentical(template)
				&& ObjectUtils.equals(scaleImage, template.scaleImage)
				&& ObjectUtils.equals(rotation, template.rotation)
				&& ObjectUtils.equals(isUsingCache, template.isUsingCache)
				&& ObjectUtils.equals(horizontalImageAlign, template.horizontalImageAlign)
				&& ObjectUtils.equals(verticalImageAlign, template.verticalImageAlign)
				&& ObjectUtils.equals(isLazy, template.isLazy)
				&& ObjectUtils.equals(onErrorType, template.onErrorType)
				&& ObjectUtils.equals(linkType, template.linkType)
				&& ObjectUtils.equals(linkTarget, template.linkTarget)
				&& ObjectUtils.identical(lineBox, template.lineBox);
	}
	
	@Override
	public void populateStyle()
	{
		super.populateStyle();
		
		scaleImage = getScaleImage();
		rotation = getRotation();
		horizontalImageAlign = getHorizontalImageAlign();
		verticalImageAlign = getVerticalImageAlign();
		
		if (lineBox != null)
		{
			lineBox.populateStyle();
		}
	}
}
