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

import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintImage;
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
import net.sf.jasperreports.engine.util.ExifOrientationEnum;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.engine.util.StyleUtil;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;
import net.sf.jasperreports.renderers.util.RendererUtil;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.ResourceInfo;
import net.sf.jasperreports.repo.ResourcePathKey;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillImage extends JRFillGraphicElement implements JRImage
{
	private static final Log log = LogFactory.getLog(JRFillImage.class);

	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_SOURCE_CLASS = "fill.image.unknown.source.class";

	/**
	 *
	 */
	private Renderable renderer;
	private Renderable oldRenderer;
	private Object prevSource;
	private Renderable prevRenderer;
	private boolean usedCache;
	private boolean hasOverflowed;
	private Integer imageHeight;
	private Integer imageWidth;
	private Integer imageX;
	private Integer bookmarkLevel;
	private String anchorName;
	private String hyperlinkReference;
	private Boolean hyperlinkWhen;
	private String hyperlinkAnchor;
	private Integer hyperlinkPage;
	private String hyperlinkTooltip;
	private JRPrintHyperlinkParameters hyperlinkParameters;

	protected final JRLineBox initLineBox;
	protected JRLineBox lineBox;


	/**
	 *
	 */
	protected JRFillImage(
		JRBaseFiller filler,
		JRImage image, 
		JRFillObjectFactory factory
		)
	{
		super(filler, image, factory);
		
		initLineBox = image.getLineBox().clone(this);
	}


	protected JRFillImage(JRFillImage image, JRFillCloneFactory factory)
	{
		super(image, factory);
		
		initLineBox = image.getLineBox().clone(this);
	}


	@Override
	protected void evaluateStyle(
		byte evaluation
		) throws JRException
	{
		super.evaluateStyle(evaluation);

		lineBox = null;
		
		if (providerStyle != null)
		{
			lineBox = initLineBox.clone(this);
			StyleUtil.appendBox(lineBox, providerStyle.getLineBox());
		}
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
		return providerStyle == null || providerStyle.getOwnScaleImage() == null ? ((JRImage)this.parent).getOwnScaleImage() : providerStyle.getOwnScaleImage();
	}

	@Override
	public void setScaleImage(ScaleImageEnum scaleImage)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public RotationEnum getRotation()
	{
		return getStyleResolver().getRotation(this);
	}
		
	@Override
	public RotationEnum getOwnRotation()
	{
		return providerStyle == null || providerStyle.getOwnRotation() == null ? ((JRImage)this.parent).getOwnRotation() : providerStyle.getOwnRotation();
	}

	@Override
	public void setRotation(RotationEnum rotation)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public HorizontalImageAlignEnum getHorizontalImageAlign()
	{
		return getStyleResolver().getHorizontalImageAlign(this);
	}
		
	@Override
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign()
	{
		return providerStyle == null || providerStyle.getOwnHorizontalImageAlign() == null ? ((JRImage)this.parent).getOwnHorizontalImageAlign() : providerStyle.getOwnHorizontalImageAlign();
	}

	@Override
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public VerticalImageAlignEnum getVerticalImageAlign()
	{
		return getStyleResolver().getVerticalImageAlign(this);
	}
		
	@Override
	public VerticalImageAlignEnum getOwnVerticalImageAlign()
	{
		return providerStyle == null || providerStyle.getOwnVerticalImageAlign() == null ? ((JRImage)this.parent).getOwnVerticalImageAlign() : providerStyle.getOwnVerticalImageAlign();
	}

	@Override
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public Boolean getUsingCache()
	{
		return ((JRImage)this.parent).getUsingCache();
	}
		
	@Override
	public void setUsingCache(Boolean isUsingCache)
	{
	}
		
	@Override
	public boolean isLazy()
	{
		return ((JRImage)this.parent).isLazy();
	}
		
	@Override
	public void setLazy(boolean isLazy)
	{
	}

	@Override
	public OnErrorTypeEnum getOnErrorType()
	{
		return OnErrorTypeEnum.getValueOrDefault(((JRImage)this.parent).getOnErrorType());
	}
		
	@Override
	public void setOnErrorType(OnErrorTypeEnum onErrorType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return EvaluationTimeEnum.getValueOrDefault(((JRImage)this.parent).getEvaluationTime());
	}
		
	@Override
	public String getEvaluationGroup()
	{
		return ((JRImage)this.parent).getEvaluationGroup();
	}
		
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox == null ? initLineBox : lineBox;
	}

	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return ((JRImage)parent).getHyperlinkType();
	}
		
	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return ((JRImage)this.parent).getHyperlinkTarget();
	}
		
	@Override
	public String getLinkTarget()
	{
		return ((JRImage)this.parent).getLinkTarget();
	}
		
	@Override
	public JRExpression getExpression()
	{
		return ((JRImage)this.parent).getExpression();
	}

	@Override
	public JRExpression getBookmarkLevelExpression()
	{
		return ((JRImage)this.parent).getBookmarkLevelExpression();
	}
	
	@Override
	public JRExpression getAnchorNameExpression()
	{
		return ((JRImage)this.parent).getAnchorNameExpression();
	}

	@Override
	public JRExpression getHyperlinkReferenceExpression()
	{
		return ((JRImage)this.parent).getHyperlinkReferenceExpression();
	}

	@Override
	public JRExpression getHyperlinkWhenExpression()
	{
		return ((JRImage)this.parent).getHyperlinkWhenExpression();
	}

	@Override
	public JRExpression getHyperlinkAnchorExpression()
	{
		return ((JRImage)this.parent).getHyperlinkAnchorExpression();
	}

	@Override
	public JRExpression getHyperlinkPageExpression()
	{
		return ((JRImage)this.parent).getHyperlinkPageExpression();
	}

		
	/**
	 *
	 */
	protected Renderable getRenderable()
	{
		return this.renderer;
	}
		
	/**
	 *
	 */
	protected String getAnchorName()
	{
		return this.anchorName;
	}

	/**
	 *
	 */
	protected String getHyperlinkReference()
	{
		return this.hyperlinkReference;
	}

	/**
	 *
	 */
	protected String getHyperlinkAnchor()
	{
		return this.hyperlinkAnchor;
	}

	/**
	 *
	 */
	protected Integer getHyperlinkPage()
	{
		return this.hyperlinkPage;
	}
		

	protected String getHyperlinkTooltip()
	{
		return this.hyperlinkTooltip;
	}
		

	/**
	 *
	 */
	protected JRTemplateImage getJRTemplateImage()
	{
		return (JRTemplateImage) getElementTemplate();
	}

	@Override
	protected JRTemplateElement createElementTemplate()
	{
		JRTemplateImage template = 
			new JRTemplateImage(
				getElementOrigin(), 
				filler.getJasperPrint().getDefaultStyleProvider(), 
				this
				);
		
		if (getScaleImage() == ScaleImageEnum.REAL_HEIGHT
				|| getScaleImage() == ScaleImageEnum.REAL_SIZE)
		{
			template.setScaleImage(ScaleImageEnum.RETAIN_SHAPE);
		}
		
		return template;
	}


	@Override
	protected void evaluate(
		byte evaluation
		) throws JRException
	{
		initDelayedEvaluations();
		
		reset();
		
		evaluatePrintWhenExpression(evaluation);

		if (isPrintWhenExpressionNull() || isPrintWhenTrue())
		{
			bookmarkLevel = getBookmarkLevel(evaluateExpression(this.getBookmarkLevelExpression(), evaluation));

			if (isEvaluateNow())
			{
				evaluateProperties(evaluation);
				evaluateStyle(evaluation);
				
				hasOverflowed = false;
				evaluateImage(evaluation);
			}
		}
	}
	

	/**
	 *
	 */
	protected void evaluateImage(
		byte evaluation
		) throws JRException
	{
		JRExpression expression = this.getExpression();

		Renderable newRenderer = null;
		
		Boolean isUsingCache = getUsingCache();
		usedCache = isUsingCache == null ? true : isUsingCache;
		Object source = null;
		
		try
		{
			source = evaluateExpression(expression, evaluation);
		}
		catch (Exception e)
		{
			source = RendererUtil.getInstance(filler.getJasperReportsContext()).handleImageError(e, getOnErrorType());
		}
		
		if (source != null)
		{
			if (isUsingCache == null)
			{
				usedCache = isUsingCache = source instanceof String;
				//FIXME the flag determined based on the expression type is lost in print images
				//hence the isUsingCache test in exporters yields false positives for images that did not come from Strings
			}
			
			boolean lazy = isLazy();
			RepositoryContext repositoryContext = filler.getRepositoryContext();
			Object srcKey = source;
			if (source instanceof String)
			{
				ResourcePathKey pathKey = ResourcePathKey.inContext(lazy ? null : repositoryContext, (String) source);				
				srcKey = new Pair<>(lazy, pathKey);
			}

			if (isUsingCache && filler.fillContext.hasLoadedRenderer(srcKey))
			{
				newRenderer = filler.fillContext.getLoadedRenderer(srcKey);
			}
			else
			{
				if (source instanceof String)
				{
					String strSource = (String) source;
					if (lazy)//TODO lucianc resolve within repository context?
					{
						newRenderer = ResourceRenderer.getInstance(strSource, true);
					}
					else
					{
						ResourceInfo resourceInfo = RepositoryUtil.getInstance(repositoryContext).getResourceInfo((String) source);
						if (resourceInfo == null)
						{
							newRenderer = RendererUtil.getInstance(repositoryContext).getNonLazyRenderable(strSource, getOnErrorType());
						}
						else
						{
							String absoluteLocation = resourceInfo.getRepositoryResourceLocation();
							if (log.isDebugEnabled())
							{
								log.debug("image " + source + " resolved to " + absoluteLocation);
							}
							ResourcePathKey absolutePathKey = ResourcePathKey.absolute(absoluteLocation);
							Object absoluteKey = new Pair<>(lazy, absolutePathKey);
							if (isUsingCache && filler.fillContext.hasLoadedRenderer(absoluteKey))
							{
								newRenderer = filler.fillContext.getLoadedRenderer(absoluteKey);
							}
							else
							{
								newRenderer = RendererUtil.getInstance(repositoryContext).getNonLazyRenderable(absoluteLocation, getOnErrorType());
								if (isUsingCache)
								{
									newRenderer = filler.fillContext.registerLoadedRenderer(absoluteKey, newRenderer);
								}
							}
						}						
					}
				}
				else if (source instanceof Image)
				{
					Image img = (Image) source;
					newRenderer = RendererUtil.getInstance(filler.getJasperReportsContext()).getRenderable(img, getOnErrorType());
				}
				else if (source instanceof byte[])
				{
					byte[] data = (byte[]) source;
					newRenderer = RendererUtil.getInstance(filler.getJasperReportsContext()).getRenderable(data);
				}
				else if (source instanceof InputStream)
				{
					if (this.prevSource != null && source == this.prevSource)//testing for object identity
					{
						//the image can be evaluated twice when the band is prevented to split
						//if the image source is a stream, we can't read it again
						//TODO do the same thing for other source types (file, url, string) too?
						newRenderer = this.prevRenderer;
					}
					else
					{
						InputStream is = (InputStream) source;
						newRenderer = RendererUtil.getInstance(filler.getJasperReportsContext()).getRenderable(is, getOnErrorType());
					}
				}
				else if (source instanceof URL)
				{
					URL url = (URL) source;
					newRenderer = RendererUtil.getInstance(filler.getJasperReportsContext()).getRenderable(url, getOnErrorType());
				}
				else if (source instanceof File)
				{
					File file = (File) source;
					newRenderer = RendererUtil.getInstance(filler.getJasperReportsContext()).getRenderable(file, getOnErrorType());
				}
				else if (source instanceof Renderable)
				{
					newRenderer = (Renderable) source;
				}
				else
				{
					newRenderer = 
						RendererUtil.getInstance(filler.getJasperReportsContext()).getOnErrorRenderer(
							getOnErrorType(), 
							new JRException(
									EXCEPTION_MESSAGE_KEY_UNKNOWN_SOURCE_CLASS,  
									new Object[]{source.getClass().getName()} 
									)
							);
				}

				if (isUsingCache)
				{
					newRenderer = filler.fillContext.registerLoadedRenderer(srcKey, newRenderer);
				}
			}
		}

		Renderable crtRenderer = getRenderable();

		this.oldRenderer = renderer;
		this.renderer = newRenderer;
		
		this.prevSource = source;
		this.prevRenderer = renderer;

		setValueRepeating(crtRenderer == newRenderer);
		
		this.anchorName = (String) evaluateExpression(this.getAnchorNameExpression(), evaluation);
		this.hyperlinkReference = (String) evaluateExpression(this.getHyperlinkReferenceExpression(), evaluation);
		this.hyperlinkWhen = (Boolean) evaluateExpression(this.getHyperlinkWhenExpression(), evaluation);
		this.hyperlinkAnchor = (String) evaluateExpression(this.getHyperlinkAnchorExpression(), evaluation);
		this.hyperlinkPage = (Integer) evaluateExpression(this.getHyperlinkPageExpression(), evaluation);
		this.hyperlinkTooltip = (String) evaluateExpression(this.getHyperlinkTooltipExpression(), evaluation);
		hyperlinkParameters = JRFillHyperlinkHelper.evaluateHyperlinkParameters(this, expressionEvaluator, evaluation);
	}
	

	@Override
	public void rewind()
	{
		super.rewind();
		
		this.renderer = this.oldRenderer;
	}

	
	@Override
	protected boolean prepare(
		int availableHeight,
		boolean isOverflow,
		boolean isOverflowAllowed
		) throws JRException
	{
		boolean willOverflow = false;

		if (
			this.isPrintWhenExpressionNull() ||
			( !this.isPrintWhenExpressionNull() && 
			this.isPrintWhenTrue() )
			)
		{
			this.setToPrint(true);
		}
		else
		{
			this.setToPrint(false);
		}

		if (!this.isToPrint())
		{
			return willOverflow;
		}
		
		boolean isToPrint = true;
		boolean isReprinted = false;

		if (isEvaluateNow())
		{
			if (isOverflow && this.isAlreadyPrinted() && !this.isPrintWhenDetailOverflows())
			{
				isToPrint = false;
			}
	
			if (
				isToPrint && 
				this.isPrintWhenExpressionNull() &&
				!this.isPrintRepeatedValues() &&
				isValueRepeating()
				)
			{
				if (
					( !this.isPrintInFirstWholeBand() || !this.getBand().isFirstWholeOnPageColumn() ) &&
					( this.getPrintWhenGroupChanges() == null || !this.getBand().isNewGroup(this.getPrintWhenGroupChanges()) ) &&
					( !isOverflow || !this.isPrintWhenDetailOverflows() )
					)
				{
					isToPrint = false;
				}
			}

			if (
				isToPrint && 
				this.isRemoveLineWhenBlank() &&
				this.getRenderable() == null
				)
			{
				isToPrint = false;
			}
	
			if (isToPrint)
			{
				if (availableHeight < getRelativeY() + getHeight())
				{
					isToPrint = false;
					willOverflow = true;
				}
				else if (
					!isLazy() 
					&& (getScaleImage() == ScaleImageEnum.REAL_HEIGHT || getScaleImage() == ScaleImageEnum.REAL_SIZE)
					)
				{
					int padding = getLineBox().getBottomPadding() 
							+ getLineBox().getTopPadding();
					boolean reprinted = isOverflow 
						&& (this.isPrintWhenDetailOverflows() 
								&& (this.isAlreadyPrinted() 
										|| (!this.isAlreadyPrinted() && !this.isPrintRepeatedValues())));
					boolean imageOverflowAllowed = 
							filler.isBandOverFlowAllowed() && !reprinted && !hasOverflowed;

					if (renderer == null)
					{
						// if renderer is null, it means the isRemoveLineWhenBlank was false further up; 
						// no need to do anything here 
					}
					else
					{
						// image fill does not normally produce non-lazy ResourceRenderer instances, 
						// so we do not need to attempt load resource renderers from cache here, as we do in the catch below
						
						RenderersCache renderersCache = usedCache ?  filler.fillContext.getRenderersCache() 
								: new RenderersCache(filler.getJasperReportsContext());
						DimensionRenderable dimensionRenderer = renderersCache.getDimensionRenderable(renderer);
						
						if (dimensionRenderer != null)
						{
							try
							{
								dimensionRenderer.getDimension(filler.getJasperReportsContext());
							}
							catch (Exception e)
							{
								renderer = RendererUtil.getInstance(filler.getJasperReportsContext()).handleImageError(e, getOnErrorType());

								if (renderer instanceof ResourceRenderer)
								{
									renderer = renderersCache.getLoadedRenderer((ResourceRenderer)renderer);
								}

								dimensionRenderer = renderersCache.getDimensionRenderable(renderer);
							}
							
							if (dimensionRenderer == null) // OnErrorTypeEnum.BLANK can return null above
							{
								isToPrint = !isRemoveLineWhenBlank();
							}
							else
							{
								boolean fits = true; 

								Dimension2D imageSize = dimensionRenderer.getDimension(filler.getJasperReportsContext()); 
								if (imageSize != null)
								{
									ExifOrientationEnum exifOrientation = ExifOrientationEnum.NORMAL;
									
									DataRenderable dataRenderable = dimensionRenderer instanceof DataRenderable ? (DataRenderable)dimensionRenderer : null;
									if (dataRenderable != null)
									{
										exifOrientation = ImageUtil.getExifOrientation(dataRenderable.getData(filler.getJasperReportsContext()));
									}
									
									fits = 
										fitImage(
											imageSize,
											exifOrientation,
											availableHeight - getRelativeY() - padding, 
											imageOverflowAllowed, 
											getHorizontalImageAlign(),
											getVerticalImageAlign()
											);
								}

								if (fits)
								{
									if (imageHeight != null)
									{
										setPrepareHeight(imageHeight + padding);
									}
								}
								else
								{
									hasOverflowed = true;
									isToPrint = false;
									willOverflow = true;
									setPrepareHeight(availableHeight - getRelativeY() - padding);
								}
							}
						}
					}
				}
			}
			
			if (
				isToPrint && 
				isOverflow && 
				//(this.isAlreadyPrinted() || !this.isPrintRepeatedValues())
				(this.isPrintWhenDetailOverflows() && (this.isAlreadyPrinted() || (!this.isAlreadyPrinted() && !this.isPrintRepeatedValues())))
				)
			{
				isReprinted = true;
			}
		}
		else
		{
			if (isOverflow && this.isAlreadyPrinted() && !this.isPrintWhenDetailOverflows())
			{
				isToPrint = false;
			}
	
			if (
				isToPrint && 
				availableHeight < this.getRelativeY() + getHeight()
				)
			{
				isToPrint = false;
				willOverflow = true;
			}
			
			if (
				isToPrint && 
				isOverflow && 
				//(this.isAlreadyPrinted() || !this.isPrintRepeatedValues())
				(this.isPrintWhenDetailOverflows() && (this.isAlreadyPrinted() || (!this.isAlreadyPrinted() && !this.isPrintRepeatedValues())))
				)
			{
				isReprinted = true;
			}
		}

		this.setToPrint(isToPrint);
		this.setReprinted(isReprinted);
		
		return willOverflow;
	}

	@Override
	protected void reset()
	{
		imageHeight = null;
		imageWidth = null;
		imageX = null;
		
		super.reset();
	}

	protected boolean fitImage(
		Dimension2D imageSize, 
		ExifOrientationEnum exifOrientation,
		int availableHeight, 
		boolean overflowAllowed,
		HorizontalImageAlignEnum hAlign,
		VerticalImageAlignEnum vAlign
		) throws JRException
	{
		imageHeight = null;
		imageWidth = null;
		imageX = null;
		
		int realHeight = (int) imageSize.getHeight();
		int realWidth = (int) imageSize.getWidth();
		boolean fitted;
		
		RotationEnum exifRotation = ImageUtil.getRotation(getRotation(), exifOrientation);
		
		if (
			exifRotation == RotationEnum.LEFT
			|| exifRotation == RotationEnum.RIGHT
			)
		{
			int t = realWidth;
			realWidth = realHeight;
			realHeight = t;
		}
		
		int reducedHeight = realHeight;
		int reducedWidth = realWidth;
		if (realWidth > getWidth())
		{
			double wRatio = ((double) getWidth()) / realWidth;
			reducedHeight = (int) (wRatio * realHeight);
			reducedWidth = getWidth();
		}		
		
		if (reducedHeight <= availableHeight)
		{
			imageHeight = reducedHeight;
			if (getScaleImage() == ScaleImageEnum.REAL_SIZE)
			{
				imageWidth = reducedWidth;
			}
			fitted = true;
		}
		else if (overflowAllowed)
		{
			fitted = false;
		}
		else
		{
			imageHeight = availableHeight;
			if (getScaleImage() == ScaleImageEnum.REAL_SIZE)
			{
				double hRatio = ((double) availableHeight) / realHeight;
				imageWidth = (int) (hRatio * realWidth);
			}
			fitted = true;
		}

		if (imageWidth != null && imageWidth != getWidth())
		{
			switch (getRotation())
			{
			case LEFT:
				switch (vAlign)
				{
				case BOTTOM:
				case MIDDLE:
					imageX = getX() + (int)(ImageUtil.getYAlignFactor(this) * (getWidth() - imageWidth));
					break;
				case TOP:
				default:
					break;
				}
				break;
			case RIGHT:
				switch (vAlign)
				{
				case TOP:
				case MIDDLE:
					imageX = getX() + (int)((1f - ImageUtil.getYAlignFactor(this)) * (getWidth() - imageWidth));
					break;
				case BOTTOM:
				default:
					break;
				}
				break;
			case UPSIDE_DOWN:
				switch (hAlign)
				{
				case LEFT:
				case CENTER:
					imageX = getX() + getWidth() - imageWidth - (int)(ImageUtil.getXAlignFactor(this) * (getWidth() - imageWidth));
					break;
				case RIGHT:
				default:
					break;
				}
				break;
			case NONE:
			default:
				switch (hAlign)
				{
				case RIGHT:
				case CENTER:
					imageX = getX() + (int)(ImageUtil.getXAlignFactor(this) * (getWidth() - imageWidth));
					break;
				case LEFT:
				default:
					break;
				}
				break;
			}
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("Fitted image of dimension " + imageSize + " on " + availableHeight
					+ ", overflow allowed " + overflowAllowed + ": " + fitted);
		}
		
		return fitted;
	}

	@Override
	protected JRPrintElement fill() throws JRException
	{
		EvaluationTimeEnum evaluationTime = getEvaluationTime();
		JRTemplatePrintImage printImage;
		JRRecordedValuesPrintImage recordedValuesImage;
		if (isEvaluateAuto())
		{
			printImage = recordedValuesImage = new JRRecordedValuesPrintImage(getJRTemplateImage(), printElementOriginator);
		}
		else
		{
			printImage = new JRTemplatePrintImage(getJRTemplateImage(), printElementOriginator);
			recordedValuesImage = null;
		}
		
		printImage.setUUID(this.getUUID());
		printImage.setX(this.getX());
		printImage.setY(this.getRelativeY());
		printImage.setWidth(getWidth());
		printImage.setHeight(this.getStretchHeight());
		printImage.setBookmarkLevel(getBookmarkLevel());

		if (isEvaluateNow())
		{
			this.copy(printImage);
		}
		else if (isEvaluateAuto())
		{
			initDelayedEvaluationPrint(recordedValuesImage);
		}
		else
		{
			filler.addBoundElement(this, printImage, evaluationTime, getEvaluationGroup(), band);
		}
		
		return printImage;
	}
		

	/**
	 *
	 */
	protected void copy(JRPrintImage printImage)
	{
		printImage.setUUID(getUUID());

		if (imageX != null)
		{
			printImage.setX(imageX);
		}
		if (imageWidth != null)
		{
			printImage.setWidth(imageWidth);
		}
		
		printImage.setRenderer(getRenderable());
		printImage.setAnchorName(getAnchorName());
		if (getHyperlinkWhenExpression() == null || Boolean.TRUE.equals(hyperlinkWhen))
		{
			printImage.setHyperlinkReference(getHyperlinkReference());
			printImage.setHyperlinkAnchor(getHyperlinkAnchor());
			printImage.setHyperlinkPage(getHyperlinkPage());
			printImage.setHyperlinkTooltip(getHyperlinkTooltip());
			printImage.setHyperlinkParameters(hyperlinkParameters);
		}
		else
		{
			if (printImage instanceof JRTemplatePrintImage)//this is normally the case
			{
				((JRTemplatePrintImage) printImage).setHyperlinkOmitted(true);
			}
			
			printImage.setHyperlinkReference(null);
		}
		transferProperties(printImage);
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
	protected void resolveElement(JRPrintElement element, byte evaluation) throws JRException
	{
		evaluateImage(evaluation);

		JRPrintImage printImage = (JRPrintImage) element;

		if (getScaleImage() == ScaleImageEnum.REAL_SIZE)//to avoid get dimension and thus unnecessarily load the image
		{
			// image fill does not normally produce non-lazy ResourceRenderer instances, 
			// so we do not need to attempt load resource renderers from cache here, as we do in the catch below

			RenderersCache renderersCache = usedCache ?  filler.fillContext.getRenderersCache() 
					: new RenderersCache(filler.getJasperReportsContext());
			DimensionRenderable dimensionRenderer = renderersCache.getDimensionRenderable(renderer);
			
			if (dimensionRenderer != null)
			{
				try
				{
					dimensionRenderer.getDimension(filler.getJasperReportsContext());
				}
				catch (Exception e)
				{
					renderer = RendererUtil.getInstance(filler.getJasperReportsContext()).handleImageError(e, getOnErrorType());

					if (renderer instanceof ResourceRenderer)
					{
						renderer = renderersCache.getLoadedRenderer((ResourceRenderer)renderer);
					}
					
					dimensionRenderer = renderersCache.getDimensionRenderable(renderer);
				}
				
				if (dimensionRenderer != null) // OnErrorTypeEnum.BLANK can return null above
				{
					
					Dimension2D imageSize = dimensionRenderer.getDimension(filler.getJasperReportsContext());
					if (imageSize != null)
					{
						int padding = 
							printImage.getLineBox().getBottomPadding() 
							+ printImage.getLineBox().getTopPadding();
							
						ExifOrientationEnum exifOrientation = ExifOrientationEnum.NORMAL;
						
						DataRenderable dataRenderable = dimensionRenderer instanceof DataRenderable ? (DataRenderable)dimensionRenderer : null;
						if (dataRenderable != null)
						{
							exifOrientation = ImageUtil.getExifOrientation(dataRenderable.getData(filler.getJasperReportsContext()));
						}
						
						fitImage(
							imageSize,
							exifOrientation,
							getHeight() - padding, 
							false, 
							printImage.getHorizontalImageAlign(),
							printImage.getVerticalImageAlign()
							);
					}
				}
			}
		}
		
		copy(printImage);
		filler.updateBookmark(element);
	}


	@Override
	public int getBookmarkLevel()
	{
		return this.bookmarkLevel == null ? ((JRImage)this.parent).getBookmarkLevel() : this.bookmarkLevel;
	}


	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillImage(this, factory);
	}


	@Override
	protected void collectDelayedEvaluations()
	{
		super.collectDelayedEvaluations();
		
		collectDelayedEvaluations(getExpression());
		collectDelayedEvaluations(getAnchorNameExpression());
		collectDelayedEvaluations(getHyperlinkReferenceExpression());
		collectDelayedEvaluations(getHyperlinkWhenExpression());
		collectDelayedEvaluations(getHyperlinkAnchorExpression());
		collectDelayedEvaluations(getHyperlinkPageExpression());	
	}


	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters()
	{
		return ((JRImage) parent).getHyperlinkParameters();
	}


	@Override
	public String getLinkType()
	{
		return ((JRImage) parent).getLinkType();
	}


	@Override
	public JRExpression getHyperlinkTooltipExpression()
	{
		return ((JRImage) parent).getHyperlinkTooltipExpression();
	}

}
