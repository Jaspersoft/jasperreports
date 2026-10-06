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
package net.sf.jasperreports.virtualization;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.DefaultPrintElementOriginator;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.fill.JRRecordedValues;
import net.sf.jasperreports.engine.fill.JRRecordedValuesPrintImage;
import net.sf.jasperreports.engine.fill.JRTemplateImage;
import net.sf.jasperreports.engine.fill.JRTemplatePrintImage;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.renderers.AbstractRenderToImageDataRenderer;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.SimpleDataRenderer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ImageElementTest extends BaseElementsTests
{
	
	private static final byte[] DUMMY_IMAGE_DATA = new byte[]{0x7A, 0x7B, 0x7C, 0x7D, 0x7E, 0x7F};

	@Test
	public void basicImage()
	{
		JRTemplatePrintImage image = imageElement();
		JRTemplatePrintImage readImage = passThroughImageSerialization(image);
		compareXml(image, readImage);
	}

	@Test
	public void cachedRenderer()
	{
		JRTemplatePrintImage image = imageElement();
		JRTemplatePrintImage readImage = passThroughImageSerialization(image);
		assert readImage.getRenderer() != null;
		assert readImage.getRenderer() == image.getRenderer();
	}

	@Test
	public void noCachedRenderer() throws JRException
	{
		JRTemplatePrintImage image = imageElement();
		JRTemplatePrintImage readImage = passThroughElementSerialization(image);
		assert readImage.getRenderer() != null;
		assert readImage.getRenderer() != image.getRenderer();
		DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
		assert Arrays.equals(((DataRenderable) readImage.getRenderer()).getData(context),
				((DataRenderable) image.getRenderer()).getData(context));
	}

	@Test
	public void customRenderer()
	{
		JRTemplatePrintImage image = imageElement();
		image.setRenderer(new CustomRenderer("x"));
		JRTemplatePrintImage readImage = compareSerialized(image);
		assert readImage.getRenderer() != null;
		assert readImage.getRenderer() != image.getRenderer();
		assert readImage.getRenderer() instanceof CustomRenderer;
		assert ((CustomRenderer) readImage.getRenderer()).a.equals("x");
	}
	
	@Test
	public void anchor()
	{
		JRTemplatePrintImage image = imageElement();
		image.setAnchorName("a");
		JRTemplatePrintImage read = compareSerialized(image);
		assert read.getAnchorName().equals("a");
	}
	
	@Test
	public void bookmarkLevel()
	{
		JRTemplatePrintImage image = imageElement();
		image.setAnchorName("a");
		image.setBookmarkLevel(2);
		JRTemplatePrintImage read = compareSerialized(image);
		assert read.getBookmarkLevel() == 2;
	}
	
	@Test
	public void hyperlinkReference()
	{
		JRTemplatePrintImage image = imageElement();
		image.setHyperlinkReference("ref");
		JRTemplatePrintImage read = compareSerialized(image);
		assert read.getHyperlinkReference().equals("ref");
	}
	
	@Test
	public void hyperlinkAnchor()
	{
		JRTemplatePrintImage image = imageElement();
		image.setHyperlinkAnchor("ref");
		JRTemplatePrintImage read = compareSerialized(image);
		assert read.getHyperlinkAnchor().equals("ref");
	}
	
	@Test
	public void hyperlinkPage()
	{
		JRTemplatePrintImage image = imageElement();
		image.setHyperlinkPage(3);
		JRTemplatePrintImage read = compareSerialized(image);
		assert read.getHyperlinkPage() == 3;
	}
	
	@Test
	public void hyperlinkTooltip()
	{
		JRTemplatePrintImage image = imageElement();
		image.setHyperlinkTooltip("ref");
		JRTemplatePrintImage read = compareSerialized(image);
		assert read.getHyperlinkTooltip().equals("ref");
	}
	
	@Test
	public void hyperlinkParameters()
	{
		JRTemplatePrintImage image = imageElement();
		
		JRPrintHyperlinkParameters params = new JRPrintHyperlinkParameters();
		JRPrintHyperlinkParameter param1 = new JRPrintHyperlinkParameter();
		param1.setName("a");
		param1.setValue("x");
		params.addParameter(param1);
		JRPrintHyperlinkParameter param2 = new JRPrintHyperlinkParameter();
		param2.setName("a");
		param2.setValueClass(Integer.class.getName());
		param2.setValue(5);
		params.addParameter(param2);
		image.setHyperlinkParameters(params);
		
		JRTemplatePrintImage read = compareSerialized(image);
		assert read.getHyperlinkParameters() != null;
		assert read.getHyperlinkParameters().getParameters().size() == 2;
		assert read.getHyperlinkParameters().getParameters().get(0).getValue().equals("x");
	}
	
	@Test 
	public void recordedValues()
	{
		JRTemplateImage template = new JRTemplateImage(null, null);
		JRRecordedValuesPrintImage image = new JRRecordedValuesPrintImage(template, new DefaultPrintElementOriginator(10));
		setImageElement(image);
		
		Set<JREvaluationTime> evaluationTimes = new HashSet<>();
		evaluationTimes.add(JREvaluationTime.EVALUATION_TIME_REPORT);
		evaluationTimes.add(JREvaluationTime.getGroupEvaluationTime("g"));
		image.initRecordedValues(evaluationTimes);
		
		JRRecordedValues values = image.getRecordedValues();
		values.recordFieldValue("f1", "x");
		values.recordFieldValue("f2", 5);
		values.recordVariableValue("v1", 7.5d);
		
		JRRecordedValuesPrintImage read = passThroughElementSerialization(image);
		JRRecordedValues readValues = read.getRecordedValues();
		assert readValues != null;
		assert readValues != values;
		
		Set<JREvaluationTime> readEvaluationTimes = readValues.getEvaluationTimes();
		assert readEvaluationTimes.size() == 2;
		assert readEvaluationTimes.contains(JREvaluationTime.EVALUATION_TIME_REPORT);
		assert readEvaluationTimes.contains(JREvaluationTime.getGroupEvaluationTime("g"));
		
		Map<String, Object> readFieldValues = readValues.getRecordedFieldValues();
		assert readFieldValues.size() == 2;
		assert readFieldValues.get("f1").equals("x");
		assert readFieldValues.get("f2").equals(5);
		
		Map<String, Object> readVarValues = readValues.getRecordedVariableValues();
		assert readVarValues.size() == 1;
		assert readVarValues.get("v1").equals(7.5d);
	}

	protected JRTemplatePrintImage passThroughImageSerialization(JRTemplatePrintImage image)
	{
		JRVirtualizationContext virtualizationContext = createVirtualizationContext();
		virtualizationContext.cacheRenderer(image.getRenderer());
		return passThroughSerialization(virtualizationContext, image);
	}
	
	protected JRTemplatePrintImage imageElement()
	{
		JRTemplateImage template = new JRTemplateImage(null, null);
		JRTemplatePrintImage image = new JRTemplatePrintImage(template, new DefaultPrintElementOriginator(10));
		setImageElement(image);
		return image;
	}

	protected void setImageElement(JRTemplatePrintImage image)
	{
		image.setUUID(UUID.randomUUID());
		image.setX(10);
		image.setY(20);
		image.setWidth(50);
		image.setHeight(30);
		
		SimpleDataRenderer renderer = SimpleDataRenderer.getInstance(DUMMY_IMAGE_DATA);
		image.setRenderer(renderer);
	}

	private static final class CustomRenderer extends AbstractRenderToImageDataRenderer
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
		
		protected final String a;
		
		public CustomRenderer(String a)
		{
			this.a = a;
		}
		
		@Override
		public void render(JasperReportsContext context, Graphics2D grx, Rectangle2D rectangle) throws JRException
		{
			// NOP
		}
		
		@Override
		public Dimension2D getDimension(JasperReportsContext jasperReportsContext) throws JRException
		{
			return new Dimension(1, 1);
		}
	}
	
}
