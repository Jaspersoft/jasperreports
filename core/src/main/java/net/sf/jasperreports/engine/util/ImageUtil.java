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
package net.sf.jasperreports.engine.util;

import net.sf.jasperreports.engine.JRImageAlignment;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class ImageUtil
{
	private static final boolean checkExif;
	
	static
	{
		boolean drewFound = false;
		try
		{
			ImageUtil.class.getClassLoader().loadClass("com.drew.metadata.Metadata");
			drewFound = true;
		}
		catch (ClassNotFoundException e)
		{
			//nothing to do
		}
		checkExif = drewFound;
	}
	
	public static float getXAlignFactor(JRImageAlignment imageAlignment)
	{
		return getXAlignFactor(imageAlignment.getHorizontalImageAlign());
	}

	public static float getXAlignFactor(HorizontalImageAlignEnum horizontalAlign)
	{
		float xalignFactor = 0f;
		switch (horizontalAlign)
		{
			case RIGHT :
			{
				xalignFactor = 1f;
				break;
			}
			case CENTER :
			{
				xalignFactor = 0.5f;
				break;
			}
			case LEFT :
			default :
			{
				xalignFactor = 0f;
				break;
			}
		}
		return xalignFactor;
	}

	public static float getYAlignFactor(JRImageAlignment imageAlignment)
	{
		return getYAlignFactor(imageAlignment.getVerticalImageAlign());
	}

	public static float getYAlignFactor(VerticalImageAlignEnum verticalAlign)
	{
		float yalignFactor = 0f;
		switch (verticalAlign)
		{
			case BOTTOM :
			{
				yalignFactor = 1f;
				break;
			}
			case MIDDLE :
			{
				yalignFactor = 0.5f;
				break;
			}
			case TOP :
			default :
			{
				yalignFactor = 0f;
				break;
			}
		}
		return yalignFactor;
	}

	/**
	 *
	 */
	public static ExifOrientationEnum getExifOrientation(byte[] data)
	{
		ExifOrientationEnum exifOrientation = null;
		
		if (checkExif && JRTypeSniffer.isJPEG(data))
		{
			exifOrientation = ExifUtil.getExifOrientation(data);
		}
		
		return exifOrientation == null ? ExifOrientationEnum.NORMAL : exifOrientation;
	}

	/**
	 *
	 */
	public static RotationEnum getRotation(RotationEnum rotation, ExifOrientationEnum exifOrientation)
	{
		RotationEnum result = rotation;
		
		switch (exifOrientation)
		{
			case UPSIDE_DOWN :
				switch (rotation)
				{
					case NONE : result = RotationEnum.UPSIDE_DOWN; break;
					case LEFT : result = RotationEnum.RIGHT; break;
					case RIGHT : result = RotationEnum.LEFT; break;
					case UPSIDE_DOWN : result = RotationEnum.NONE; break;
				}
				break;
			case RIGHT :
				switch (rotation)
				{
					case NONE : result = RotationEnum.RIGHT; break;
					case LEFT : result = RotationEnum.NONE; break;
					case RIGHT : result = RotationEnum.UPSIDE_DOWN; break;
					case UPSIDE_DOWN : result = RotationEnum.LEFT; break;
				}
				break;
			case LEFT :
				switch (rotation)
				{
					case NONE : result = RotationEnum.LEFT; break;
					case LEFT : result = RotationEnum.UPSIDE_DOWN; break;
					case RIGHT : result = RotationEnum.NONE; break;
					case UPSIDE_DOWN : result = RotationEnum.RIGHT; break;
				}
				break;
			default :
			{
				break;
			}
		}
		
		return result;
	}
	
	/*
	 * This method is the result of tests alone and not the result of prior design or understanding of how image cropping works in the Microsoft document formats.
	 * Trial and error during tests were the only way to achieve desired output and this code is the result of this trial and error technique alone, 
	 * without actually understanding how image cropping works in these document formats.
	 */
	public static Insets getExifCrop(
		JRPrintImage image, 
		ExifOrientationEnum exifOrientation, 
		double cropTop, 
		double cropLeft, 
		double cropBottom, 
		double cropRight
		)
	{
		switch (image.getRotation())
		{
			case LEFT :
				switch (exifOrientation)
				{
					case UPSIDE_DOWN :
					{
						double t = cropLeft;
						cropLeft = cropRight;
						cropRight = t;
						t = cropTop;
						cropTop = cropBottom;
						cropBottom = t;
						break;
					}
					case RIGHT :
					{
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						break;
					}
					case LEFT :
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						break;
					case NORMAL :
					default :
						break;
				}
				break;
			case RIGHT :
				switch (exifOrientation)
				{
					case UPSIDE_DOWN :
					{
						double t = cropLeft;
						cropLeft = cropRight;
						cropRight = t;
						t = cropTop;
						cropTop = cropBottom;
						cropBottom = t;
						break;
					}
					case RIGHT :
					{
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						break;
					}
					case LEFT :
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						break;
					case NORMAL :
					default :
						break;
				}
				break;
			case UPSIDE_DOWN :
				switch (exifOrientation)
				{
					case UPSIDE_DOWN :
					{
						double t = cropLeft;
						cropLeft = cropRight;
						cropRight = t;
						t = cropTop;
						cropTop = cropBottom;
						cropBottom = t;
						break;
					}
					case RIGHT :
					{
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						break;
					}
					case LEFT :
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						break;
					case NORMAL :
					default :
						break;
				}
				break;
			case NONE :
			default :
				switch (exifOrientation)
				{
					case UPSIDE_DOWN :
					{
						double t = cropLeft;
						cropLeft = cropRight;
						cropRight = t;
						t = cropTop;
						cropTop = cropBottom;
						cropBottom = t;
						break;
					}
					case RIGHT :
					{
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						break;
					}
					case LEFT :
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							)
						{
							double t = cropTop;
							cropTop = cropBottom;
							cropBottom = t;
						}
						if (
							(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
							|| (image.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && image.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
							)
						{
							double t = cropLeft;
							cropLeft = cropRight;
							cropRight = t;
						}
						break;
					case NORMAL :
					default :
						break;
				}
				break;
		}
		return new Insets(cropTop, cropLeft, cropBottom, cropRight);
	}
	

	/**
	 * 
	 */
	public static class Insets
	{
		public final double top;
		public final double left;
		public final double bottom;
		public final double right;
		
		public Insets(double top, double left, double bottom, double right)
		{
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}
	}
}
