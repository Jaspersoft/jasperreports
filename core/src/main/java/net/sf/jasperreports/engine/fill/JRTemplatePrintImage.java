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

import java.io.IOException;

import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;
import net.sf.jasperreports.renderers.Renderable;


/**
 * Implementation of {@link net.sf.jasperreports.engine.JRPrintImage} that uses
 * a {@link net.sf.jasperreports.engine.fill.JRTemplateImage} instance to
 * store common attributes. 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRTemplatePrintImage extends JRTemplatePrintGraphicElement implements JRPrintImage
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final int SERIALIZATION_FLAG_CACHED_RENDERER = 1;
	private static final int SERIALIZATION_FLAG_ANCHOR = 1 << 1;
	private static final int SERIALIZATION_FLAG_HYPERLINK = 1 << 2;
	private static final int SERIALIZATION_FLAG_HYPERLINK_OMITTED = 1 << 3;

	/**
	 *
	 */
	private Renderable renderable;
	private String anchorName;
	private boolean hyperlinkOmitted;
	private String hyperlinkReference;
	private String hyperlinkAnchor;
	private Integer hyperlinkPage;
	private String hyperlinkTooltip;
	private JRPrintHyperlinkParameters hyperlinkParameters;

	/**
	 * The bookmark level for the anchor associated with this field.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity
	
	public JRTemplatePrintImage()
	{
		
	}
	
	/**
	 * Creates a print image element.
	 * 
	 * @param image the template image that the element will use
	 * @param originator
	 */
	public JRTemplatePrintImage(JRTemplateImage image, PrintElementOriginator originator)
	{
		super(image, originator);
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
		return ((JRTemplateImage)this.template).getScaleImage();
	}

	@Override
	public ScaleImageEnum getOwnScaleImage()
	{
		return ((JRTemplateImage)this.template).getOwnScaleImage();
	}

	@Override
	public void setScaleImage(ScaleImageEnum scaleImage)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public RotationEnum getRotation()
	{
		return ((JRTemplateImage)this.template).getRotation();
	}
		
	@Override
	public RotationEnum getOwnRotation()
	{
		return ((JRTemplateImage)this.template).getOwnRotation();
	}
		
	@Override
	public void setRotation(RotationEnum rotation)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUsingCache()
	{
		return ((JRTemplateImage)this.template).isUsingCache();
	}

	@Override
	public void setUsingCache(boolean isUsingCache)
	{
	}

	@Override
	public HorizontalImageAlignEnum getHorizontalImageAlign()
	{
		return ((JRTemplateImage)this.template).getHorizontalImageAlign();
	}
		
	@Override
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign()
	{
		return ((JRTemplateImage)this.template).getOwnHorizontalImageAlign();
	}
		
	@Override
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public VerticalImageAlignEnum getVerticalImageAlign()
	{
		return ((JRTemplateImage)this.template).getVerticalImageAlign();
	}
		
	@Override
	public VerticalImageAlignEnum getOwnVerticalImageAlign()
	{
		return ((JRTemplateImage)this.template).getOwnVerticalImageAlign();
	}
		
	@Override
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public OnErrorTypeEnum getOnErrorType()
	{
		return ((JRTemplateImage)this.template).getOnErrorType();
	}
		
	@Override
	public void setOnErrorType(OnErrorTypeEnum onErrorType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRLineBox getLineBox()
	{
		return ((JRTemplateImage)template).getLineBox();
	}
		
	@Override
	public String getAnchorName()
	{
		return this.anchorName;
	}
		
	@Override
	public void setAnchorName(String anchorName)
	{
		this.anchorName = anchorName;
	}
	
	public void setHyperlinkOmitted(boolean hyperlinkOmitted)
	{
		this.hyperlinkOmitted = hyperlinkOmitted;
	}
		
	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return hyperlinkOmitted ? HyperlinkTypeEnum.NONE : ((JRTemplateImage)this.template).getHyperlinkType();
	}
		
	@Override
	public void setHyperlinkType(HyperlinkTypeEnum hyperlinkType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return ((JRTemplateImage)this.template).getHyperlinkTarget();
	}
		
	@Override
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHyperlinkReference()
	{
		return this.hyperlinkReference;
	}
		
	@Override
	public void setHyperlinkReference(String hyperlinkReference)
	{
		this.hyperlinkReference = hyperlinkReference;
	}
		
	@Override
	public String getHyperlinkAnchor()
	{
		return this.hyperlinkAnchor;
	}
		
	@Override
	public void setHyperlinkAnchor(String hyperlinkAnchor)
	{
		this.hyperlinkAnchor = hyperlinkAnchor;
	}
		
	@Override
	public Integer getHyperlinkPage()
	{
		return this.hyperlinkPage;
	}
		
	@Override
	public void setHyperlinkPage(Integer hyperlinkPage)
	{
		this.hyperlinkPage = hyperlinkPage;
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
	public void setHyperlinkParameters(JRPrintHyperlinkParameters parameters)
	{
		this.hyperlinkParameters = parameters;
	}

	@Override
	public String getLinkType()
	{
		return hyperlinkOmitted ? null : ((JRTemplateImage) this.template).getLinkType();
	}

	@Override
	public void setLinkType(String type)
	{
	}

	@Override
	public String getLinkTarget()
	{
		return ((JRTemplateImage) this.template).getLinkTarget();
	}

	@Override
	public void setLinkTarget(String target)
	{
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

	@Override
	public void writeVirtualized(VirtualizationOutput out) throws IOException
	{
		super.writeVirtualized(out);
		
		JRVirtualizationContext context = out.getVirtualizationContext();
		
		int flags = 0;
		boolean cachedRenderer = renderable != null && context.hasCachedRenderer(renderable.getId());
		boolean hasAnchor = anchorName != null || bookmarkLevel != JRAnchor.NO_BOOKMARK;
		boolean hasHyperlink = hyperlinkReference != null || hyperlinkAnchor != null
				|| hyperlinkPage != null || hyperlinkTooltip != null || hyperlinkParameters != null;
		
		if (cachedRenderer)
		{
			flags |= SERIALIZATION_FLAG_CACHED_RENDERER;
		}
		if (hasAnchor)
		{
			flags |= SERIALIZATION_FLAG_ANCHOR;
		}
		if (hasHyperlink)
		{
			flags |= SERIALIZATION_FLAG_HYPERLINK;
		}
		if (hyperlinkOmitted)
		{
			flags |= SERIALIZATION_FLAG_HYPERLINK_OMITTED;
		}
		
		out.writeByte(flags);
		
		if (cachedRenderer)
		{
			out.writeJRObject(renderable.getId());
		}
		else
		{
			out.writeJRObject(renderable);
		}
		
		if (hasAnchor)
		{
			out.writeJRObject(anchorName);
			out.writeIntCompressed(bookmarkLevel);
		}

		if (hasHyperlink)
		{
			out.writeJRObject(hyperlinkReference);
			out.writeJRObject(hyperlinkAnchor);
			out.writeJRObject(hyperlinkPage);
			out.writeJRObject(hyperlinkTooltip);
			out.writeJRObject(hyperlinkParameters);
		}		
	}

	@Override
	public void readVirtualized(VirtualizationInput in) throws IOException
	{
		super.readVirtualized(in);
		
		JRVirtualizationContext context = in.getVirtualizationContext();
		int flags = in.readUnsignedByte();
		
		if ((flags & SERIALIZATION_FLAG_CACHED_RENDERER) != 0)
		{
			String renderedId = (String) in.readJRObject();
			renderable = context.getCachedRenderer(renderedId);
			if (renderable == null)
			{
				throw new RuntimeException();
			}
		}
		else
		{
			renderable = (Renderable) in.readJRObject();
		}
		
		if ((flags & SERIALIZATION_FLAG_ANCHOR) != 0)
		{
			anchorName = (String) in.readJRObject();
			bookmarkLevel = in.readIntCompressed();
		}
		else
		{
			bookmarkLevel = JRAnchor.NO_BOOKMARK;
		}
		
		if ((flags & SERIALIZATION_FLAG_HYPERLINK_OMITTED) != 0)
		{
			hyperlinkOmitted = true;
		}

		if ((flags & SERIALIZATION_FLAG_HYPERLINK) != 0)
		{
			hyperlinkReference = (String) in.readJRObject();
			hyperlinkAnchor = (String) in.readJRObject();
			hyperlinkPage = (Integer) in.readJRObject();
			hyperlinkTooltip = (String) in.readJRObject();
			hyperlinkParameters = (JRPrintHyperlinkParameters) in.readJRObject();
		}
	}
}
