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
package net.sf.jasperreports.openpdf.producer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.ImgTemplate;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.SplitCharacter;
import org.openpdf.text.pdf.FopGlyphProcessor;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfFormField;
import org.openpdf.text.pdf.PdfImportedPage;
import org.openpdf.text.pdf.PdfOutline;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.PdfTemplate;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.pdf.RadioCheckField;
import org.openpdf.text.pdf.TextField;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.NullOutputStream;
import net.sf.jasperreports.openpdf.BreakIteratorSplitCharacter;
import net.sf.jasperreports.pdf.AbstractPdfTextRenderer;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.LineBreaksPdfTextRenderer;
import net.sf.jasperreports.pdf.PdfTextRenderer;
import net.sf.jasperreports.pdf.SimplePdfTextRenderer;
import net.sf.jasperreports.pdf.common.PdfChunk;
import net.sf.jasperreports.pdf.common.PdfContent;
import net.sf.jasperreports.pdf.common.PdfDocument;
import net.sf.jasperreports.pdf.common.PdfDocumentWriter;
import net.sf.jasperreports.pdf.common.PdfImage;
import net.sf.jasperreports.pdf.common.PdfOutlineEntry;
import net.sf.jasperreports.pdf.common.PdfPhrase;
import net.sf.jasperreports.pdf.common.PdfProducer;
import net.sf.jasperreports.pdf.common.PdfProducerContext;
import net.sf.jasperreports.pdf.common.PdfRadioCheck;
import net.sf.jasperreports.pdf.common.PdfStructure;
import net.sf.jasperreports.pdf.common.PdfTextChunk;
import net.sf.jasperreports.pdf.common.PdfTextField;
import net.sf.jasperreports.pdf.common.PdfTextRendererContext;
import net.sf.jasperreports.pdf.type.PdfFieldTypeEnum;
import net.sf.jasperreports.renderers.Graphics2DRenderable;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardPdfProducer implements PdfProducer
{
	
	private PdfProducerContext context;
	
	private StandardPdfStructure pdfStructure;
	
	private StandardDocument document;
	private StandardPdfWriter writer;

	private Document imageTesterDocument;
	private PdfContentByte imageTesterPdfContentByte;
	
	private SplitCharacter splitCharacter;
	private GlyphRendering glyphRendering;
	
	private StandardPdfContent pdfContent;
	
	private Map<String, RadioCheckField> radioFieldFactories;
	private Map<String, PdfFormField> radioGroups;
	
	private boolean defaultUseSavedLineBreaks;

	public StandardPdfProducer(PdfProducerContext context)
	{
		this.context = context;
		this.glyphRendering = new GlyphRendering(this);
	}

	@Override
	public PdfProducerContext getContext()
	{
		return context;
	}

	@Override
	public PdfDocument createDocument(PrintPageFormat pageFormat)
	{
		Document pdfDocument =
				new Document(
					new Rectangle(
						pageFormat.getPageWidth(),
						pageFormat.getPageHeight()
					)
				);
		setDocumentProperties(pdfDocument);
			
		imageTesterDocument =
				new Document(
					new Rectangle(
						10, //jasperPrint.getPageWidth(),
						10 //jasperPrint.getPageHeight()
					)
				);
		
		document = new StandardDocument(pdfDocument);
		return document;
	}

	protected void setDocumentProperties(Document pdfDocument)
	{
		String documentLanguage = context.getProperties().getProperty(context.getCurrentJasperPrint(), JRPdfExporter.PROPERTY_DOCUMENT_LANGUAGE);
		if (documentLanguage != null)
		{
			pdfDocument.setDocumentLanguage(documentLanguage);
		}
		
		boolean glyphSubstitutionEnabled = context.getProperties().getBooleanProperty(context.getCurrentJasperPrint(), 
				JRPdfExporter.PROPERTY_FOP_GLYPH_SUBSTITUTION_ENABLED, false);
		if (!glyphSubstitutionEnabled && FopGlyphProcessor.isFopSupported())
		{
			pdfDocument.setGlyphSubstitutionEnabled(false);
		}
	}

	@Override
	public PdfDocumentWriter createWriter(OutputStream os) throws JRException
	{
		try
		{
			PdfWriter pdfWriter = PdfWriter.getInstance(document.getDocument(), os);
			pdfWriter.setCloseStream(false);
			if (context.isTagged())
			{
				pdfWriter.setTagged();
			}
			
			PdfWriter imageTesterPdfWriter =
				PdfWriter.getInstance(
					imageTesterDocument,
					new NullOutputStream() // discard the output
					);
			imageTesterDocument.open();
			imageTesterDocument.newPage();
			imageTesterPdfContentByte = imageTesterPdfWriter.getDirectContent();
			imageTesterPdfContentByte.setLiteral("\n");
			
			writer = new StandardPdfWriter(this, pdfWriter);
			return writer;
		}
		catch (DocumentException e)
		{
			throw context.handleDocumentException(e);
		}
	}
	
	public PdfWriter getPdfWriter()
	{
		return writer.getPdfWriter();
	}

	public boolean isPdf2()
	{
		return writer.isPdf2();
	}

	@Override
	public PdfContent createPdfContent()
	{
		pdfContent = new StandardPdfContent(writer.getPdfWriter(), context.getCMYKColorSpace());
		return pdfContent;
	}

	@Override
	public PdfContent getPdfContent()
	{
		return pdfContent;
	}

	public PdfContentByte getPdfContentByte()
	{
		return pdfContent.getPdfContentByte();
	}

	@Override
	public void initReport()
	{
		glyphRendering.initGlyphRenderer();
		
		defaultUseSavedLineBreaks = context.getProperties().getBooleanProperty(
				context.getCurrentJasperPrint(), 
				JRPdfExporter.PROPERTY_USE_SAVED_LINE_BREAKS, false);
	}

	@Override
	public void setForceLineBreakPolicy(boolean forceLineBreakPolicy)
	{
		splitCharacter = forceLineBreakPolicy ? new BreakIteratorSplitCharacter() : null;
	}
	
	@Override
	public void newPage()
	{
		document.getDocument().newPage();
		pdfContent.refreshContent();
	}
	
	@Override
	public void setPageSize(PrintPageFormat pageFormat, int pageWidth, int pageHeight)
	{
		Rectangle pageSize;
		if (pageFormat.getOrientation() == OrientationEnum.LANDSCAPE)
		{
			pageSize = new Rectangle(pageHeight, pageWidth).rotate();
		}
		else
		{
			pageSize = new Rectangle(pageWidth, pageHeight);
		}
		document.getDocument().setPageSize(pageSize);		
	}

	@Override
	public void endPage()
	{
		if (radioGroups != null)
		{
			for (PdfFormField radioGroup : radioGroups.values())
			{
				getPdfWriter().addAnnotation(radioGroup);
			}
			radioGroups = null;
			radioFieldFactories = null; // radio groups that overflow unto next page don't seem to work; reset everything as it does not make sense to keep them
		}
	}

	@Override
	public void close()
	{
		document.getDocument().close();
		imageTesterDocument.close();
	}

	@Override
	public AbstractPdfTextRenderer getTextRenderer(PdfTextRendererContext textContext)
	{
		AbstractPdfTextRenderer textRenderer = glyphRendering.getGlyphTextRenderer(textContext);
		if (textRenderer == null)
		{
			if (textContext.getPrintText().getLeadingOffset() == 0)
			{
				// leading offset is non-zero only for multiline texts that have at least one tab character or some paragraph indent (first, left or right)
				textRenderer = 
					new PdfTextRenderer(
						context.getJasperReportsContext(), 
						textContext.getAwtIgnoreMissingFont(), 
						textContext.getIndentFirstLine(),
						textContext.getJustifyLastLine()
						);//FIXMENOW make some reusable instances here and below
			}
			else
			{
				boolean useSavedLineBreaks = toUseSavedLineBreaks(textContext);
				if (useSavedLineBreaks)
				{
					textRenderer = new LineBreaksPdfTextRenderer(
							context.getJasperReportsContext(), textContext);
				}
				else
				{
					textRenderer = 
							new SimplePdfTextRenderer(
								context.getJasperReportsContext(), 
								textContext
								);//FIXMETAB optimize this
				}
			}
		}
		return textRenderer;
	}

	protected boolean toUseSavedLineBreaks(PdfTextRendererContext textContext)
	{
		JRPrintText printText = textContext.getPrintText();
		boolean useSavedLineBreaks = false;
		if (printText.getLineBreakOffsets() != null
				&& printText.getLineBreakOffsets().length > 0)
		{
			useSavedLineBreaks = defaultUseSavedLineBreaks;
			if (printText.hasProperties())
			{
				String useSavedLineBreaksProperty = JRPropertiesUtil.getOwnProperty(printText, 
						JRPdfExporter.PROPERTY_USE_SAVED_LINE_BREAKS);
				if (useSavedLineBreaksProperty != null)
				{
					useSavedLineBreaks = JRPropertiesUtil.asBoolean(useSavedLineBreaksProperty);
				}
			}
		}
		return useSavedLineBreaks;
	}

	/**
	 * @deprecated Replaced by {@link #getTextRenderer(PdfTextRendererContext)}.
	 */
	@Override
	public AbstractPdfTextRenderer getTextRenderer(
			JRPrintText text, JRStyledText styledText, Locale textLocale,
			boolean awtIgnoreMissingFont, boolean defaultIndentFirstLine, boolean defaultJustifyLastLine)
	{
		AbstractPdfTextRenderer textRenderer = glyphRendering.getGlyphTextRenderer(text, styledText, textLocale,
				awtIgnoreMissingFont, defaultIndentFirstLine, defaultJustifyLastLine);
		if (textRenderer == null)
		{
			if (text.getLeadingOffset() == 0)
			{
				// leading offset is non-zero only for multiline texts that have at least one tab character or some paragraph indent (first, left or right)
				textRenderer = 
					new PdfTextRenderer(
						context.getJasperReportsContext(), 
						awtIgnoreMissingFont, 
						defaultIndentFirstLine,
						defaultJustifyLastLine
						);//FIXMENOW make some reusable instances here and below
			}
			else
			{
				textRenderer = 
					new SimplePdfTextRenderer(
						context.getJasperReportsContext(), 
						awtIgnoreMissingFont, 
						defaultIndentFirstLine,
						defaultJustifyLastLine
						);//FIXMETAB optimize this
			}
		}
		return textRenderer;
	}

	@Override
	public PdfImage createImage(byte[] data, boolean verify) throws IOException, JRException
	{
		try
		{
			Image image = Image.getInstance(data);
			
			if (verify)
			{
				imageTesterPdfContentByte.addImage(image, 10, 0, 0, 10, 0, 0);
			}
			
			return new StandardImage(image);
		}
		catch (DocumentException e)
		{
			throw context.handleDocumentException(e);
		}
	}
	
	@Override
	public PdfImage drawImage(
		JRPrintImage image, Graphics2DRenderable renderer, boolean forceSvgShapes, 
		double renderWidth, double renderHeight
		) throws JRException, IOException
	{
		PdfContentByte pdfContentByte = getPdfContentByte();
		PdfTemplate template = pdfContentByte.createTemplate(
				(float) renderWidth, (float) renderHeight);

		Graphics2D g = forceSvgShapes
			? template.createGraphicsShapes((float) renderWidth, (float) renderHeight)
			: template.createGraphics((float) renderWidth, (float) renderHeight, 
					new StandardPdfFontMapper(this));

		try
		{
			if (image.getMode() == ModeEnum.OPAQUE)
			{
				g.setColor(image.getBackcolor());
				g.fillRect(0, 0, (int) renderWidth, (int) renderHeight);
			}

			renderer.render(context.getJasperReportsContext(), g, 
					new Rectangle2D.Double(0, 0, renderWidth, renderHeight));
		}
		finally
		{
			g.dispose();
		}

		return new StandardImage(new ImgTemplate(template));
	}
	
	@Override
	public PdfImage clipImage(PdfImage image, int clipWidth, int clipHeight, int translateX, int translateY) throws JRException
	{
		Image img = ((StandardImage)image).getImage();

		PdfContentByte pdfContentByte = getPdfContentByte();
		PdfTemplate template = pdfContentByte.createTemplate(img.getWidth(), img.getHeight());
		template.newPath();
		template.rectangle(- translateX, img.getHeight() - clipHeight + translateY, clipWidth, clipHeight);
		template.clip();
		template.newPath();
		img.setAbsolutePosition(0, 0);
		template.addImage(img);
		
		return new StandardImage(Image.getInstance(template));
	}
	
	public Font getFont(Map<Attribute,Object> attributes, Locale locale)
	{
		StandardFontRecipient fontRecipient = new StandardFontRecipient(context.getCMYKColorSpace());
		context.setFont(attributes, locale, false, fontRecipient);
		Font font = fontRecipient.getFont();
		return font;
	}
	
	@Override
	public PdfTextChunk createChunk(String text, Map<Attribute,Object> attributes, Locale locale)
	{
		Font font = getFont(attributes, locale);
		Chunk chunk = new Chunk(text, font);

		if (splitCharacter != null)
		{
			//TODO use line break offsets if available?
			chunk.setSplitCharacter(splitCharacter);
		}
		
		return new StandardTextChunk(this, chunk, font);
	}

	@Override
	public PdfChunk createChunk(PdfImage imageContainer)
	{
		Image image = ((StandardImage) imageContainer).getImage();
		Chunk chunk = new Chunk(image, 0, 0);
		return new StandardChunk(this, chunk);
	}
	
	@Override
	public PdfPhrase createPhrase()
	{
		Phrase phrase = new Phrase();
		return new StandardPhrase(this, phrase);
	}

	@Override
	public PdfPhrase createPhrase(PdfChunk chunk)
	{
		Phrase phrase = new Phrase(((StandardChunk) chunk).getChunk());
		return new StandardPhrase(this, phrase);
	}

	@Override
	public PdfTextField createTextField(float llx, float lly, float urx, float ury, String fieldName)
	{
		TextField textField = createTextFormField(llx, lly, urx, ury, fieldName);
		return new StandardPdfTextField(this, textField, PdfFieldTypeEnum.TEXT);
	}

	protected TextField createTextFormField(float llx, float lly, float urx, float ury, String fieldName)
	{
		Rectangle rectangle = new Rectangle(llx, lly, urx, ury);
		TextField textField = new TextField(writer.getPdfWriter(), rectangle, fieldName);
		return textField;
	}

	@Override
	public PdfTextField createComboField(float llx, float lly, float urx, float ury, String fieldName, 
			String value, String[] choices)
	{
		TextField textField = createTextFormField(llx, lly, urx, ury, fieldName);		
		setFieldChoices(textField, value, choices);
		return new StandardPdfTextField(this, textField, PdfFieldTypeEnum.COMBO);
	}

	protected void setFieldChoices(TextField textField, String value, String[] choices)
	{
		if (choices != null)
		{
			textField.setChoices(choices);
			
			if (value != null)
			{
				int i = 0;
				for (String choice : choices)
				{
					if (value.equals(choice))
					{
						textField.setChoiceSelection(i);
						break;
					}
					i++;
				}
			}
		}
	}

	@Override
	public PdfTextField createListField(float llx, float lly, float urx, float ury, String fieldName, 
			String value, String[] choices)
	{
		TextField textField = createTextFormField(llx, lly, urx, ury, fieldName);		
		setFieldChoices(textField, value, choices);
		return new StandardPdfTextField(this, textField, PdfFieldTypeEnum.LIST);
	}

	@Override
	public PdfRadioCheck createCheckField(float llx, float lly, float urx, float ury, String fieldName, 
			String onValue)
	{
		Rectangle rectangle = new Rectangle(llx, lly, urx, ury);
		RadioCheckField radioField = new RadioCheckField(writer.getPdfWriter(), rectangle, fieldName, onValue);
		return new StandardRadioCheck(this, radioField);
	}

	@Override
	public PdfRadioCheck getRadioField(float llx, float lly, float urx, float ury, String fieldName, 
			String onValue)
	{
		Rectangle rectangle = new Rectangle(llx, lly, urx, ury);
		//TODO does this make sense?
		RadioCheckField radioField = radioFieldFactories == null ? null : radioFieldFactories.get(fieldName);
		if (radioField == null)
		{
			radioField = new RadioCheckField(writer.getPdfWriter(), rectangle, fieldName, onValue);
			if (radioFieldFactories == null)
			{
				radioFieldFactories = new HashMap<>();
			}
			radioFieldFactories.put(fieldName, radioField);
		}
		
		radioField.setBox(rectangle);
		
		return new StandardRadioCheck(this, radioField);
	}
	
	protected PdfFormField getRadioGroup(RadioCheckField radioCheckField)
	{
		String fieldName = radioCheckField.getFieldName();
		PdfFormField radioGroup = radioGroups == null ? null : radioGroups.get(fieldName);
		if (radioGroup == null)
		{
			if (radioGroups == null)
			{
				radioGroups = new HashMap<>();
			}
			
			radioGroup = radioCheckField.getRadioGroup(true, false);
			radioGroups.put(fieldName, radioGroup);
		}
		return radioGroup;
	}

	@Override
	public PdfOutlineEntry getRootOutline()
	{
		PdfOutline rootOutline = pdfContent.getPdfContentByte().getRootOutline();
		return new StandardPdfOutline(rootOutline, pdfContent.getPdfWriter(), isPdf2());
	}

	@Override
	public PdfStructure getPdfStructure()
	{
		if (pdfStructure == null)
		{
			pdfStructure = new StandardPdfStructure(this);
		}
		return pdfStructure;
	}

	@Override
	public void addPdfPage(byte[] pdfData,
			int x, int y, int width, int height)
	{
		try (PdfReader pdfReader = new PdfReader(pdfData))
		{
			PdfWriter pdfWriter = pdfContent.getPdfWriter();
			PdfImportedPage importedPage = pdfWriter.getImportedPage(pdfReader, 1);
			PdfContentByte directContent = pdfWriter.getDirectContent();
			float ratio = Math.min(width / importedPage.getWidth(), 
					height / importedPage.getHeight());
			directContent.addTemplate(importedPage,
					ratio, 0, 0, ratio,
					context.getOffsetX() + x, 
					context.getCurrentPageFormat().getPageHeight() - context.getOffsetY() - y - height);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}		
	}
	
}
