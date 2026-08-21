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
package net.sf.jasperreports.engine.export;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.TextExporterConfiguration;
import net.sf.jasperreports.export.TextReportConfiguration;
import net.sf.jasperreports.export.WriterExporterOutput;


/**
 * Exports filled reports in plain text format. The text exporter allows users to define a custom character resolution
 * (the number of columns and rows in text format). Since the character resolution is mapped on the actual pixel resolution,
 * every character corresponds to a rectangle of pixels. If a certain text element has a smaller size in pixels (width,
 * height, or both) than the number of pixels that map to a character, the text element will not be rendered. 
 * <p/>
 * The text exporter will yield the better results if the space needed for displaying a text is large. So
 * users have to either design reports with few text or export to big text pages. Another good practice is to arrange text
 * elements at design time as similar as possible to a grid.
 * <p/>
 * The text exporter tries to convert the JasperReports document into a simple text document with a fixed page width 
 * and height, measured in characters. Users can specify the desired page width and height, and the engine will make the 
 * best effort to fit text elements into the corresponding text page. The basic idea of the algorithm is to convert pixels 
 * to characters (to find a pixel/character ratio). To achieve this, use the following configuration settings 
 * (see {@link net.sf.jasperreports.export.TextReportConfiguration}):
 * <ul>
 * <li>{@link net.sf.jasperreports.export.TextReportConfiguration#getCharWidth() getCharWidth()} and 
 * {@link net.sf.jasperreports.export.TextReportConfiguration#getCharHeight() getCharHeight()} - 
 * these specify how many pixels in the original report should be mapped onto a character in the exported text.</li>
 * <li>{@link net.sf.jasperreports.export.TextReportConfiguration#getPageWidthInChars() getPageWidthInChars()} and 
 * {@link net.sf.jasperreports.export.TextReportConfiguration#getPageHeightInChars() getPageHeightInChars()} - 
 * these specify the text page width and height in characters.</li>
 * </ul>
 * <p/>
 * Note that both width and height must be specified and that character sizes have priority
 * over page sizes.
 * <p/>
 * Since the algorithm causes loss of precision, a few precautions should be taken when
 * creating templates that will eventually be exported to plain text:
 * <ul>
 * <li>Report sizes and text page sizes should be divisible (for example, specify a
 * template width of 1,000 pixels and a page width of 100 characters, resulting in a
 * character width of 10 pixels).</li>
 * <li>Text element sizes should also follow the preceding rule (for example, if the
 * character height is 10 pixels and a particular text element is expected to span two
 * rows, then the text element should be 20 pixels tall).</li>
 * <li>For best results, text elements should be aligned in a grid-like fashion.</li>
 * <li>Text fields should not be too small. Following are two examples of problems that
 * this can cause:
 * <ul>
 * <li>If the element height is smaller than the character height, then the element will
 * not appear in the exported text file.</li>
 * <li>If the character width is 10 and the element width is 80, then only the first eight
 * characters will be displayed.</li>
 * </ul>
 * </li>
 * </ul>
 * <p/>
 * Users can specify the text that should be inserted between two subsequent pages by using
 * the {@link net.sf.jasperreports.export.TextExporterConfiguration#getPageSeparator() getPageSeparator()} 
 * exporter configuration setting. The default value is two blank lines.
 * <p/>
 * The line separator to be used in the generated text file can be specified using the
 * {@link net.sf.jasperreports.export.TextExporterConfiguration#getLineSeparator() getLineSeparator()} 
 * exporter configuration setting. This is most useful when you want to force a
 * particular line separator, knowing that the default line separator is operating system
 * dependent, as specified by the <code>line.separator</code> system property of the JVM.
 * <p/>
 * Check the supplied /demo/samples/text sample to see the kind of output this exporter
 * can produce.
 * 
 * @see net.sf.jasperreports.export.TextExporterConfiguration
 * @see net.sf.jasperreports.export.TextReportConfiguration
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRTextExporter extends JRAbstractExporter<TextReportConfiguration, TextExporterConfiguration, WriterExporterOutput, JRTextExporterContext>
{
	private static final String TXT_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.txt.";
	public static final String EXCEPTION_MESSAGE_KEY_REQUIRED_POSITIVE_PAGE_OR_CHARACTER_WIDTH = "export.text.required.positive.page.or.character.width";
	public static final String EXCEPTION_MESSAGE_KEY_CHARACTER_WIDTH_NEGATIVE = "export.text.character.width.negative";
	public static final String EXCEPTION_MESSAGE_KEY_REQUIRED_POSITIVE_PAGE_OR_CHARACTER_HEIGHT = "export.text.required.positive.page.or.character.height";
	public static final String EXCEPTION_MESSAGE_KEY_CHARACTER_HEIGHT_NEGATIVE = "export.text.character.height.negative";

	protected Writer writer;
	char[][] pageData;
	protected int pageWidthInChars;
	protected int pageHeightInChars;
	protected float charWidth;
	protected float charHeight;
	protected String pageSeparator;
	protected String lineSeparator;
	protected boolean isTrimLineRight;

	protected static final String systemLineSeparator = System.getProperty("line.separator");

	protected class ExporterContext extends BaseExporterContext implements JRTextExporterContext
	{
	}

	/**
	 * @see #JRTextExporter(JasperReportsContext)
	 */
	public JRTextExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	
	/**
	 *
	 */
	public JRTextExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
	}


	@Override
	protected Class<TextExporterConfiguration> getConfigurationInterface()
	{
		return TextExporterConfiguration.class;
	}


	@Override
	protected Class<TextReportConfiguration> getItemConfigurationInterface()
	{
		return TextReportConfiguration.class;
	}
	

	@Override
	public void exportReport() throws JRException
	{
		/*   */
		ensureJasperReportsContext();
		ensureInput();

		initExport();
		
		ensureOutput();
		
		writer = getExporterOutput().getWriter();

		try
		{
			exportReportToWriter();
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_OUTPUT_WRITER_ERROR,
					new Object[]{jasperPrint.getName()}, 
					e);
		}
		finally
		{
			getExporterOutput().close();
		}
	}


	@Override
	protected void initExport()
	{
		super.initExport();
		
		TextExporterConfiguration configuration = getCurrentConfiguration();
		
		lineSeparator = configuration.getLineSeparator();
		if (lineSeparator == null) 
		{
			lineSeparator = systemLineSeparator;
		}

		pageSeparator = configuration.getPageSeparator();
		if (pageSeparator == null) 
		{
			pageSeparator = systemLineSeparator + systemLineSeparator;
		}

		isTrimLineRight = configuration.isTrimLineRight();
	}


	@Override
	protected void initReport()
	{
		super.initReport();

		TextReportConfiguration configuration = getCurrentItemConfiguration();
		
		Float charWidthValue = configuration.getCharWidth();
		charWidth = charWidthValue == null ? 0 : charWidthValue;
		if (charWidth < 0)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CHARACTER_WIDTH_NEGATIVE,  
					(Object[])null 
					);
		}
		else if (charWidth == 0)
		{
			Integer pageWidthInCharsValue = configuration.getPageWidthInChars();
			pageWidthInChars = pageWidthInCharsValue == null ? 0 : pageWidthInCharsValue;
			
			if (pageWidthInChars <= 0)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_REQUIRED_POSITIVE_PAGE_OR_CHARACTER_WIDTH,  
						(Object[])null 
						);
			}
			
			charWidth = jasperPrint.getPageWidth() / (float)pageWidthInChars;
		}
		else
		{
			pageWidthInChars = (int)(jasperPrint.getPageWidth() / charWidth);
		}
		

		Float charHeightValue = configuration.getCharHeight(); 
		charHeight = charHeightValue == null ? 0 : charHeightValue; 
		if (charHeight < 0)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CHARACTER_HEIGHT_NEGATIVE,  
					(Object[])null 
					);
		}
		else if (charHeight == 0)
		{
			Integer pageHeightInCharsValue = configuration.getPageHeightInChars();
			pageHeightInChars = pageHeightInCharsValue == null ? 0 : pageHeightInCharsValue;
			if (pageHeightInChars <= 0)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_REQUIRED_POSITIVE_PAGE_OR_CHARACTER_HEIGHT,  
						(Object[])null 
						);
			}

			charHeight = jasperPrint.getPageHeight() / (float)pageHeightInChars;
		}
		else
		{
			pageHeightInChars = (int)(jasperPrint.getPageHeight() / charHeight);
		}
	}


	/**
	 *
	 */
	protected void exportReportToWriter() throws JRException, IOException
	{
		List<ExporterInputItem> items = exporterInput.getItems();

		for (int reportIndex = 0; reportIndex < items.size(); reportIndex++)
		{
			ExporterInputItem item = items.get(reportIndex);

			setCurrentExporterInputItem(item);

			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				PageRange pageRange = getPageRange();
				int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
				int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

				for (int pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
				{
					checkInterrupted();

					JRPrintPage page = pages.get(pageIndex);

					/*   */
					exportPage(page);

					if (reportIndex < items.size() - 1 || pageIndex < endPageIndex)
					{
						writer.write(pageSeparator);
					}
				}
			}
		}

		writer.flush();
	}


	/**
	 * Exports a page to the output writer. Only text elements within the page are considered. For each page, the engine
	 * creates a matrix of characters and each rendered text element is placed at the appropriate position in the matrix.
	 * After all texts are parsed, the character matrix is sent to the output writer.
	 */
	protected void exportPage(JRPrintPage page) throws IOException
	{
		List<JRPrintElement> elements = page.getElements();

		pageData = new char[pageHeightInChars][];
		for (int i = 0; i < pageHeightInChars; i++) {
			pageData[i] = new char[pageWidthInChars];
			Arrays.fill(pageData[i], ' ');
		}

		exportElements(elements);

		for (int i = 0; i < pageHeightInChars; i++) 
		{
			int lineLength = pageWidthInChars;
			if (isTrimLineRight)
			{
				int j = pageWidthInChars - 1;
				while (j >= 0 && pageData[i][j] == ' ')
				{
					j--;
				}
				lineLength = j + 1;
			}

			writer.write(pageData[i], 0, lineLength);
			writer.write(lineSeparator);
		}

		JRExportProgressMonitor progressMonitor = getCurrentItemConfiguration().getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}


	protected void exportElements(List<JRPrintElement> elements)
	{
		for (int i = 0; i < elements.size();i++)
		{
			Object element = elements.get(i);
			if (element instanceof JRPrintText)
			{
				exportText((JRPrintText) element);
			}
			else if (element instanceof JRPrintFrame)
			{
				JRPrintFrame frame = (JRPrintFrame) element;
				setFrameElementsOffset(frame, false);
				try
				{
					exportElements(frame.getElements());
				}
				finally
				{
					restoreElementOffsets();
				}
			}
		}
	}


	/**
	 * Renders a text and places it in the output matrix.
	 */
	protected void exportText(JRPrintText element)
	{
		int topPadding = element.getLineBox().getTopPadding();
		int leftPadding = element.getLineBox().getLeftPadding();
		int rightPadding = element.getLineBox().getRightPadding();
		int bottomPadding = element.getLineBox().getBottomPadding();
		int colSpan = getWidthInChars(element.getWidth() - leftPadding - rightPadding);
		int rowSpan = getHeightInChars(element.getHeight() - topPadding - bottomPadding);
		int col = getWidthInChars(element.getX() + getOffsetX() + leftPadding);
		int row = getHeightInChars(element.getY() + getOffsetY() + topPadding);
		
		if (col + colSpan > pageWidthInChars) 
		{
			//if the text exceeds the page width, truncate the column count
			colSpan = pageWidthInChars - col;
		}

		String allText;
		JRStyledText styledText = getStyledText(element);
		if (styledText == null)
		{
			allText = "";
		}
		else
		{
			allText = styledText.getText();
		}

		// if the space is too small, the element will not be rendered
		if (rowSpan <= 0 || colSpan <= 0)
		{
			return;
		}

		if (allText != null && allText.length() == 0)
		{
			return;
		}

		// uses an array of string buffers, since the maximum number of rows is already calculated
		StringBuilder[] rows = new StringBuilder[rowSpan];
		rows[0] = new StringBuilder();
		int rowIndex = 0;
		int rowPosition = 0;
		boolean isFirstLine = true;
		
		// first search for \n, because it causes immediate line break
		StringTokenizer lfTokenizer = new StringTokenizer(allText, "\n", true);
		label:while (lfTokenizer.hasMoreTokens()) {
			String line = lfTokenizer.nextToken();
			// if text starts with a new line:
			if (isFirstLine && line.equals("\n"))
			{
				rows[rowIndex].append("");
				rowIndex++;
				if (rowIndex == rowSpan || !lfTokenizer.hasMoreTokens())
				{
					break label;
				}
				rowPosition = 0;
				rows[rowIndex] = new StringBuilder();
				line = lfTokenizer.nextToken();
			}

			isFirstLine = false;
			
			// if there is a series of new lines:
			int emptyLinesCount = 0;
			while (line.equals("\n") && lfTokenizer.hasMoreTokens())
			{
				emptyLinesCount ++;
				line = lfTokenizer.nextToken();
			}
				
			if (emptyLinesCount > 1)
			{
				for (int i = 0; i < emptyLinesCount-1; i++)
				{
					rows[rowIndex].append("");
					rowIndex++;
					if (rowIndex == rowSpan)
					{
						break label;
					}
					rowPosition = 0;
					rows[rowIndex] = new StringBuilder();
					//if this is the last empty line:
					if (!lfTokenizer.hasMoreTokens() && line.equals("\n"))
					{
						rows[rowIndex].append("");
						break label;
					}
				}
			}
			if (!line.equals("\n"))
			{
				
				StringTokenizer spaceTokenizer = new StringTokenizer(line, " ", true);
	
				// divide each text line in words
				while (spaceTokenizer.hasMoreTokens()) {
					String word = spaceTokenizer.nextToken();
	
					// situation: word is larger than the entire column
					// in this case breaking occurs in the middle of the word
					while (word.length() > colSpan) {
						rows[rowIndex].append(word.substring(0, colSpan - rowPosition));
						word = word.substring(colSpan - rowPosition, word.length());
						rowIndex++;
						if (rowIndex == rowSpan)
						{
							break label;
						}
						rowPosition = 0;
						rows[rowIndex] = new StringBuilder();
					}
	
					// situation: word is larger than remaining space on the current line
					// in this case, go to the next line
					if (rowPosition + word.length() > colSpan) 
					{
						rowIndex++;
						if (rowIndex == rowSpan)
						{
							break label;
						}
						rowPosition = 0;
						rows[rowIndex] = new StringBuilder();
					}
	
					// situation: the word is actually a space and it situated at the beginning of a new line
					// in this case, it is removed
					if (rowIndex > 0 && rowPosition == 0 && word.equals(" "))
					{
						continue;
					}
					// situation: the word is small enough to fit in the current line
					// in this case just add the word and increment the cursor position
					rows[rowIndex].append(word);
					rowPosition += word.length();
				}
	
	
				rowIndex++;
				if (rowIndex == rowSpan)
				{
					break;
				}
				rowPosition = 0;
				rows[rowIndex] = new StringBuilder();
			}
		}

		int colOffset = 0;
		int rowOffset = 0;

		switch (element.getVerticalTextAlign())
		{
			case BOTTOM :
			{
				rowOffset = rowSpan - rowIndex;
				break;
			}
			case MIDDLE :
			{
				rowOffset = (rowSpan - rowIndex) / 2;
				break;
			}
			default :
		}

		for (int i = 0; i < rowIndex; i++) {
			String line = rows[i].toString();
			int pos = line.length() - 1;
			while (pos >= 0 && line.charAt(pos) == ' ')
			{
				pos--;
			}
			line = line.substring(0, pos + 1);
			switch (element.getHorizontalTextAlign())
			{
				case RIGHT :
				{
					colOffset = colSpan - line.length();
					break;
				}
				case CENTER :
				{
					colOffset = (colSpan - line.length()) / 2;
					break;
				}
	
				// if text is justified, there is no offset, but the line text is modified
				// the last line in the paragraph is not justified.
				case JUSTIFIED :
				{
					if (i < rowIndex -1)
					{
						line = justifyText(line, colSpan);
					}
					break;
				}
				default :
			}

			char[] chars = line.toCharArray();
			System.arraycopy(chars, 0, pageData[row + rowOffset + i], col + colOffset, chars.length);
		}
	}


	/**
	 * Justifies the text inside a specified space.
	 */
	private String justifyText(String s, int width)
	{
		StringBuilder justified = new StringBuilder();

		StringTokenizer t = new StringTokenizer(s, " ");
		int tokenCount = t.countTokens();
		if (tokenCount <= 1)
		{
			return s;
		}

		String[] words = new String[tokenCount];
		int i = 0;
		while (t.hasMoreTokens())
		{
			words[i++] = t.nextToken();
		}

		int emptySpace = width - s.length() + (words.length - 1);
		int spaceCount = emptySpace / (words.length - 1);
		int remainingSpace = emptySpace % (words.length - 1);

		char[] spaces = new char[spaceCount];
		Arrays.fill(spaces, ' ');

		for (i = 0; i < words.length - 1; i++)
		{
			justified.append(words[i]);
			justified.append(spaces);
			if (i < remainingSpace)
			{
				justified.append(' ');
			}
		}
		justified.append(words[words.length-1]);

		return justified.toString();
	}


	/**
	 * Transforms height from pixel space to character space.
	 */
	protected int getHeightInChars(int height)
	{
		//return (int) (((long) pageHeightInChars * height) / jasperPrint.getPageHeight());
		return Math.round(height / charHeight);
	}

	/**
	 * Transforms width from pixel space to character space.
	 */
	protected int getWidthInChars(int width)
	{
//		return pageWidthInChars * width / jasperPrint.getPageWidth();
		return Math.round(width / charWidth);
	}


	@Override
	protected JRStyledText getStyledText(JRPrintText textElement)
	{
		JRStyledText styledText = styledTextUtil.getStyledText(textElement, noneSelector);
		
		if (styledText != null && !JRCommonText.MARKUP_NONE.equals(textElement.getMarkup()))
		{
			styledText = JRStyledTextUtil.getBulletedText(styledText);
		}

		return styledText;
	}

	@Override
	public String getExporterKey()
	{
		return null;
	}
	
	@Override
	public String getExporterPropertiesPrefix()
	{
		return TXT_EXPORTER_PROPERTIES_PREFIX;
	}
}
