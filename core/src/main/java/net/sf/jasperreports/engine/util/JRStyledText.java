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
package net.sf.jasperreports.engine.util;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fonts.AwtFontAttribute;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRStyledText implements Cloneable
{
	public static final String EXCEPTION_MESSAGE_KEY_CANNOT_COPY_CHARACTERS = "util.styled.text.cannot.copy.characters";
	/**
	 * 
	 */
	@Property(
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_6_1
			)
	public static final String PROPERTY_AWT_IGNORE_MISSING_FONT = JRPropertiesUtil.PROPERTY_PREFIX + "awt.ignore.missing.font";
	
	@Property(
			valueType = Boolean.class,
			scopes = {PropertyScope.GLOBAL},
			sinceVersion = PropertyConstants.VERSION_3_1_3
			)
	public static final String PROPERTY_AWT_SUPERSCRIPT_FIX_ENABLED = JRPropertiesUtil.PROPERTY_PREFIX + "awt.superscript.fix.enabled";

	private static final boolean AWT_SUPERSCRIPT_FIX_ENABLED = 
		JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).getBooleanProperty(PROPERTY_AWT_SUPERSCRIPT_FIX_ENABLED);
	
	private static final Set<Attribute> FONT_ATTRS = new HashSet<>();
	static
	{
		FONT_ATTRS.add(TextAttribute.FAMILY);
		FONT_ATTRS.add(JRTextAttribute.FONT_INFO);
		FONT_ATTRS.add(TextAttribute.WEIGHT);
		FONT_ATTRS.add(TextAttribute.POSTURE);
		FONT_ATTRS.add(TextAttribute.SIZE);
		FONT_ATTRS.add(TextAttribute.SUPERSCRIPT);
	}
	
	/**
	 *
	 */
	private StringBuilder sbuffer;
	private String text;
	
	private List<Run> runs = Collections.emptyList();
	private AttributedString attributedString;
	private AttributedString awtAttributedString;
	private Map<Attribute,Object> globalAttributes;
	private Locale locale;

	
	/**
	 *
	 */
	public JRStyledText()
	{
		this(null);
	}


	/**
	 *
	 */
	public JRStyledText(Locale locale)
	{
		this.locale = locale;
	}

	public JRStyledText(Locale locale, String text)
	{
		this.locale = locale;
		this.text = text;
	}

	public JRStyledText(Locale locale, String text, Map<Attribute,Object> globalAttributes)
	{
		this.locale = locale;
		this.text = text;
		this.globalAttributes = globalAttributes;
		this.runs = Collections.singletonList(new Run(globalAttributes, 0, text.length()));
	}
	
	public JRStyledText(Locale locale, String text, Map<Attribute,Object> globalAttributes, List<Run> runs)
	{
		this.locale = locale;
		this.text = text;
		this.globalAttributes = globalAttributes;
		this.runs = runs;
	}
	
	private void ensureBuffer()
	{
		if (sbuffer == null)
		{
			sbuffer = text == null ? new StringBuilder() : new StringBuilder(text);
		}
		text = null;
	}
	
	private void ensureText()
	{
		if (text == null)
		{
			text = sbuffer == null ? "" : sbuffer.toString();
		}
		sbuffer = null;
	}


	/**
	 *
	 */
	public void append(String text)
	{
		ensureBuffer();
		int previousLength = sbuffer.length();
		sbuffer.append(text);
		attributedString = null;
		awtAttributedString = null;
		
		if (globalAttributes != null && !"".equals(text))
		{
			//updating the range of the global attributes run
			for (Run run : runs)
			{
				if (run.startIndex == 0 && run.endIndex == previousLength
						//testing for attributes map identity
						&& run.attributes == globalAttributes)
				{
					run.endIndex = length();
				}
			}
		}
	}

	/**
	 *
	 */
	public void addRun(Run run)
	{
		int currentSize = runs.size();
		if (currentSize == 0)
		{
			runs = Collections.singletonList(run);
		}
		else
		{
			if (currentSize == 1 && !(runs instanceof ArrayList))
			{
				List<Run> newRuns = new ArrayList<>();
				newRuns.add(runs.get(0));
				runs = newRuns;
			}

			runs.add(run);
		}

		attributedString = null;
		awtAttributedString = null;
	}

	/**
	 *
	 */
	public int length()
	{
		return text == null ? (sbuffer == null ? 0 : sbuffer.length()) : text.length();
	}

	/**
	 *
	 */
	public String getText()
	{
		ensureText();
		return text;
	}

	/**
	 *
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 *
	 */
	public AttributedString getAttributedString()
	{
		if (attributedString == null)
		{
			ensureText();
			attributedString = new AttributedString(text);

			for (int i = runs.size() - 1; i >= 0; i--)
			{
				Run run = runs.get(i);
				if (run.startIndex != run.endIndex && run.attributes != null)
				{
					attributedString.addAttributes(run.attributes, run.startIndex, run.endIndex);
				}
			}
		}
		
		return attributedString;
	}

	public void consumeText(StyledTextRunConsumer consumer)
	{
		if (globalAttributes != null && runs.size() == 1)
		{
			consumer.accept(0, length(), globalAttributes, getText());
			return;
		}
		
		AttributedCharacterIterator iterator = getAttributedString().getIterator();
		int runLimit = 0;
		int length = length();
		while (runLimit < length && (runLimit = iterator.getRunLimit()) <= length)
		{
			int runStart = iterator.getIndex();
			Map<Attribute,Object> attributes = iterator.getAttributes();

			String runText = text.substring(runStart, runLimit);
			consumer.accept(runStart, runLimit, attributes, runText);
			
			iterator.setIndex(runLimit);
		}
	}

	/**
	 * Returns an attributed string that contains the AWT font attribute, as the font is actually loaded.
	 */
	public AttributedString getAwtAttributedString(FontUtil fontUtil, boolean ignoreMissingFont)
	{
		return getAwtAttributedString(() -> fontUtil, ignoreMissingFont, 1f);
	}

	/**
	 *
	 */
	public AttributedString getAwtAttributedString(FontUtil fontUtil, boolean ignoreMissingFont, float fontSizeScale)
	{
		return getAwtAttributedString(() -> fontUtil, ignoreMissingFont, fontSizeScale);
	}

	/**
	 * Returns an attributed string that contains the AWT font attribute, as the font is actually loaded.
	 */
	public AttributedString getAwtAttributedString(JasperReportsContext jasperReportsContext, boolean ignoreMissingFont)
	{
		return getAwtAttributedString(() -> FontUtil.getInstance(jasperReportsContext), ignoreMissingFont, 1f);
	}

	protected AttributedString getAwtAttributedString(Supplier<FontUtil> fontUtilSupplier, boolean ignoreMissingFont, float fontSizeScale)
	{
		if (awtAttributedString == null)
		{
			ensureText();
			awtAttributedString = new AttributedString(text);

			for (int i = runs.size() - 1; i >= 0; i--)
			{
				Run run = runs.get(i);
				if (run.startIndex != run.endIndex && run.attributes != null)
				{
					awtAttributedString.addAttributes(run.attributes, run.startIndex, run.endIndex);
				}
//				if (
//					run.startIndex != run.endIndex 
//					&& run.attributes != null 
//					&& !run.attributes.isEmpty()
//					)
//				{
//					for (Iterator it = run.attributes.entrySet().iterator(); it.hasNext();)
//					{
//						Map.Entry entry = (Map.Entry) it.next();
//						AttributedCharacterIterator.Attribute attribute = 
//							(AttributedCharacterIterator.Attribute) entry.getKey();
//						if (!(attribute instanceof JRTextAttribute))
//						{
//							Object value = entry.getValue();
//							awtAttributedString.addAttribute(attribute, value, run.startIndex, run.endIndex);
//						}
//					}
//				}
			}

			if (fontSizeScale != 1f)
			{
				AttributedCharacterIterator sizeIterator = awtAttributedString.getIterator();
				int sizeRunLimit = 0;
				while (sizeRunLimit < sizeIterator.getEndIndex()
						&& (sizeRunLimit = sizeIterator.getRunLimit(TextAttribute.SIZE)) <= sizeIterator.getEndIndex())
				{
					Float size = (Float) sizeIterator.getAttribute(TextAttribute.SIZE);
					if (size != null)
					{
						awtAttributedString.addAttribute(TextAttribute.SIZE, size * fontSizeScale,
								sizeIterator.getIndex(), sizeRunLimit);
					}
					sizeIterator.setIndex(sizeRunLimit);
				}
			}

			AttributedCharacterIterator iterator = awtAttributedString.getIterator();

			int runLimit = 0;
			AffineTransform atrans = null;

			while (runLimit < iterator.getEndIndex() && (runLimit = iterator.getRunLimit(FONT_ATTRS)) <= iterator.getEndIndex())
			{
				Map<Attribute,Object> attrs = iterator.getAttributes();

				AwtFontAttribute fontAttribute = AwtFontAttribute.fromAttributes(attrs);

				FontUtil fontUtil = fontUtilSupplier.get();
				Font awtFont = fontUtil.getAwtFontFromBundles(
						fontAttribute,
						((TextAttribute.WEIGHT_BOLD.equals(attrs.get(TextAttribute.WEIGHT))?Font.BOLD:Font.PLAIN)
							|(TextAttribute.POSTURE_OBLIQUE.equals(attrs.get(TextAttribute.POSTURE))?Font.ITALIC:Font.PLAIN)),
						(Float)attrs.get(TextAttribute.SIZE),
						locale,
						ignoreMissingFont
						);
				if (awtFont == null)
				{
					// The font was not found in any of the font extensions, so it is expected that the TextAttribute.FAMILY attribute
					// will be used by AWT. In that case, we want make sure the font family name is available to the JVM.
					fontUtil.checkAwtFont(fontAttribute.getFamily(), ignoreMissingFont);
				}
				else
				{
					if (AWT_SUPERSCRIPT_FIX_ENABLED && atrans != null)
					{
						double y = atrans.getTranslateY();
						atrans = new AffineTransform();
						atrans.translate(0, - y);
						awtFont = awtFont.deriveFont(atrans);
						atrans = null;
					}
					Integer superscript = (Integer)attrs.get(TextAttribute.SUPERSCRIPT);
					if (TextAttribute.SUPERSCRIPT_SUPER.equals(superscript))
					{
						atrans = new AffineTransform();
						atrans.scale(2 / 3d, 2 / 3d);
						atrans.translate(0, - awtFont.getSize() / 2f);
						awtFont = awtFont.deriveFont(atrans);
					}
					else if (TextAttribute.SUPERSCRIPT_SUB.equals(superscript))
					{
						atrans = new AffineTransform();
						atrans.scale(2 / 3d, 2 / 3d);
						atrans.translate(0, awtFont.getSize() / 2f);
						awtFont = awtFont.deriveFont(atrans);
					}
					awtAttributedString.addAttribute(TextAttribute.FONT, awtFont, iterator.getIndex(), runLimit);
				}
				
				iterator.setIndex(runLimit);
			}

		}
		
		return awtAttributedString;
	}


	/**
	 *
	 */
	public List<Run> getRuns()
	{
		return runs;
	}

	/**
	 * 
	 */
	public static class Run implements Cloneable
	{
		/**
		 *
		 */
		public Map<Attribute,Object> attributes;
		public int startIndex;
		public int endIndex;

		/**
		 *
		 */
		public Run(Map<Attribute,Object> attributes, int startIndex, int endIndex) 
		{
			this.attributes = attributes;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		protected Object clone()
		{
			return cloneRun();
		}
		
		/**
		 * Clones this object.
		 * 
		 * @return a clone of this object
		 */
		public Run cloneRun()
		{
			try
			{
				Run clone = (Run) super.clone();
				clone.attributes = cloneAttributesMap(attributes);
				return clone;
			}
			catch (CloneNotSupportedException e)
			{
				// never
				throw new JRRuntimeException(e);
			}
		}
	}

	public void setGlobalAttributes(Map<Attribute,Object> attributes)
	{
		this.globalAttributes = attributes;
		addRun(new Run(attributes, 0, length()));
	}
	
	
	public Map<Attribute,Object> getGlobalAttributes()
	{
		return globalAttributes;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	protected static Map<Attribute,Object> cloneAttributesMap(Map<Attribute,Object> attributes)
	{
		return attributes == null ? null : new HashMap<>(attributes);
	}

	
	/**
	 * Clones this object.
	 * 
	 * @return a clone of this object
	 */
	public JRStyledText cloneText()
	{
		try
		{
			JRStyledText clone = (JRStyledText) super.clone();
			clone.globalAttributes = cloneAttributesMap(globalAttributes);
			
			int runsCount = runs.size();
			if (runsCount == 0)
			{
				clone.runs = Collections.emptyList();
			}
			else if (runsCount == 1)
			{
				clone.runs = Collections.singletonList(runs.get(0).cloneRun());
			}
			else
			{
				clone.runs = new ArrayList<>(runsCount);
				for (Run run : runs)
				{
					Run runClone = run.cloneRun();
					clone.runs.add(runClone);
				}
			}
			
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
	}
	
	/**
	 * Inserts a string at specified positions in the styled text.
	 * 
	 * <p>
	 * The string is inserted in the style runs located at the insertion
	 * positions.  If a style run finished right before the insertion position,
	 * the string will be part of this run (but not of the runs that start
	 * right after the insertion position).
	 * 
	 * @param str the string to insert
	 * @param offsets the incremental offsets of the positions at which to
	 * insert the string
	 */
	public void insert(String str, short[] offsets)
	{
		int insertLength = str.length();
		
		int currentLength = length();
		//new buffer to do the insertion
		StringBuilder newText = new StringBuilder(currentLength + insertLength * offsets.length); //NOPMD
		char[] buffer = null;
		int offset = 0;
		for (int i = 0; i < offsets.length; i++)
		{
			int charCount = offsets[i];
			int prevOffset = offset;
			offset += offsets[i];
			
			//append chunk of text
			if (buffer == null || buffer.length < charCount)
			{
				buffer = new char[charCount];
			}
			getChars(prevOffset, offset, buffer, 0);
			newText.append(buffer, 0, charCount);
			
			//append inserted text
			newText.append(str);
			
			//adjust runs
			//TODO optimize this?
			for (Run run : runs)
			{
				if (run.startIndex >= offset)
				{
					//inserted before run
					run.startIndex += insertLength;
					run.endIndex += insertLength;
				}
				else if (run.endIndex >= offset)
				{
					//inserted in the middle or immediately after a run
					//the inserted text is included in the run
					run.endIndex += insertLength;
				}
			}
		}
		
		//append remaining text
		int charCount = currentLength - offset;
		if (buffer == null || buffer.length < charCount)
		{
			buffer = new char[charCount];
		}
		getChars(offset, currentLength, buffer, 0);
		newText.append(buffer, 0, charCount);
		
		//overwrite with the inserted buffer
		sbuffer = newText;
		text = null;
		
		attributedString = null;
		awtAttributedString = null;
	}
	
	private void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)
	{
		if (text != null)
		{
			text.getChars(srcBegin, srcEnd, dst, dstBegin);
		}
		else if (sbuffer != null)
		{
			sbuffer.getChars(srcBegin, srcEnd, dst, dstBegin);
		}
		else if (srcBegin < srcEnd)
		{
			// should not happen
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CANNOT_COPY_CHARACTERS,
					new Object[]{srcBegin, srcEnd});
		}
	}
}
