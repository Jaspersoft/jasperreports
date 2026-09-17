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
package net.sf.jasperreports.barcode4j;

import java.awt.Dimension;

import org.krysalis.barcode4j.BaselineAlignment;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;
import org.krysalis.barcode4j.impl.fourstate.AbstractFourStateBean;
import org.krysalis.barcode4j.impl.fourstate.RoyalMailCBCBean;
import org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMailBean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.postnet.POSTNETBean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.krysalis.barcode4j.tools.UnitConv;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.Renderable;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class AbstractBarcodeEvaluator implements BarcodeVisitor
{
	
	protected final JasperReportsContext jasperReportsContext;
	protected final JRComponentElement componentElement;
	protected final BarcodeComponent barcodeComponent;
	protected final JRDefaultStyleProvider defaultStyleProvider;
	protected final int reportDpi;

	protected String message;
	protected AbstractBarcodeBean barcodeBean;
	protected QRCodeBean qrCodeBean;
	protected Renderable renderable;
	
	protected AbstractBarcodeEvaluator(
		JasperReportsContext jasperReportsContext,
		JRComponentElement componentElement, 
		JRDefaultStyleProvider defaultStyleProvider,
		int reportDpi
		)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.componentElement = componentElement;
		this.barcodeComponent = (BarcodeComponent) componentElement.getComponent();
		this.defaultStyleProvider = defaultStyleProvider;
		this.reportDpi = reportDpi;
	}

	/**
	 * Barcode geometry is expressed in millimetres, while element and component measurements are
	 * expressed in the pixels of the report, so they are converted to points before being handed
	 * over to the barcode. Font sizes are already expressed in points and are not converted.
	 */
	protected double toPoints(double reportPixels)
	{
		return reportDpi == JasperPrint.DEFAULT_REPORT_DPI
			? reportPixels
			: reportPixels * JasperPrint.DEFAULT_REPORT_DPI / reportDpi;
	}

	public void evaluateBarcode()
	{
		barcodeComponent.receive(this);
	}
	
	public Renderable getRenderable()
	{
		return renderable;
	}

	protected void evaluateBarcodeRenderable(BarcodeComponent barcodeComponent)
	{
		if (message == null)
		{
			renderable = null;
		}
		else
		{
			BarcodeImageProducer imageProducer = 
				BarcodeUtils.getInstance(jasperReportsContext).getProducer(
					componentElement);
			renderable = imageProducer.createImage(
					jasperReportsContext,
					componentElement, 
					barcodeBean, message);
		}
	}
	
	protected void evaluateBarcodeRenderable(QRCodeBean qrCodeBean)
	{
		if (message == null)
		{
			renderable = null;
		}
		else
		{
			QRCodeImageProducer imageProducer = 
				BarcodeUtils.getInstance(jasperReportsContext).getQRCodeProducer(
					componentElement);
			renderable = imageProducer.createImage(
					jasperReportsContext,
					componentElement, 
					qrCodeBean, message);
		}
	}
	
	protected void setBaseAttributes(Barcode4jComponent barcodeComponent)
	{
		JRStyle style = getElementStyle();
		if (style != null)
		{
			String fontName = style.getFontName();
			if (fontName != null)
			{
				barcodeBean.setFontName(fontName);
			}
			
			Float fontSize = style.getFontSize();
			if (fontSize != null)
			{
				double barFontSize = UnitConv.pt2mm(fontSize.floatValue());
				barcodeBean.setFontSize(barFontSize);
			}
		}
		
		Double moduleWidth = barcodeComponent.getModuleWidth();
		if (moduleWidth != null)
		{
			barcodeBean.setModuleWidth(UnitConv.pt2mm(toPoints(moduleWidth)));
		}
		
		TextPositionEnum textPlacement = barcodeComponent.getTextPosition();
		if (textPlacement != null)
		{
			barcodeBean.setMsgPosition(
					HumanReadablePlacement.byName(textPlacement.getName()));
		}
		
		Double quietZone = barcodeComponent.getQuietZone();
		if (quietZone != null)
		{
			barcodeBean.doQuietZone(true);
			barcodeBean.setQuietZone(UnitConv.pt2mm(toPoints(quietZone)));
		}
		
		Double vQuietZone = barcodeComponent.getVerticalQuietZone();
		if (vQuietZone != null)
		{
			barcodeBean.setVerticalQuietZone(UnitConv.pt2mm(toPoints(vQuietZone)));
		}

		// FIXME DataMatrix?
		double barcodeHeight;
		if (BarcodeUtils.isVertical(barcodeComponent))
		{
			barcodeHeight = UnitConv.pt2mm(toPoints(componentElement.getWidth()));
		}
		else
		{
			barcodeHeight = UnitConv.pt2mm(toPoints(componentElement.getHeight()));
		}
		barcodeBean.setHeight(barcodeHeight);
	}

	protected JRStyle getElementStyle()
	{
		JRStyle style = componentElement.getStyle();
		if (style == null)
		{
			style = defaultStyleProvider.getDefaultStyle();
		}
		return style;
	}
	
	@Override
	public void visitCodabar(CodabarComponent codabar)
	{
		CodabarBean codabarBean = new CodabarBean();
		barcodeBean = codabarBean;
		evaluateCodabar(codabar);
		setBaseAttributes(codabar);
		if (codabar.getWideFactor() != null)
		{
			codabarBean.setWideFactor(codabar.getWideFactor());
		}
		evaluateBarcodeRenderable(codabar);
	}

	protected abstract void evaluateCodabar(CodabarComponent codabar);

	@Override
	public void visitCode128(Code128Component code128)
	{
		barcodeBean = new Code128Bean();
		evaluateCode128(code128);
		setBaseAttributes(code128);
		evaluateBarcodeRenderable(code128);
	}

	protected abstract void evaluateCode128(Code128Component code128);

	@Override
	public void visitDataMatrix(DataMatrixComponent dataMatrix)
	{
		DataMatrixBean dataMatrixBean = new DataMatrixBean();
		barcodeBean = dataMatrixBean;
		evaluateDataMatrix(dataMatrix);
		setBaseAttributes(dataMatrix);
		if (dataMatrix.getShape() != null)
		{
			dataMatrixBean.setShape(SymbolShapeHint.byName(dataMatrix.getShape()));
		}
		
		Dimension minSymbolDimension = null; 
		if (dataMatrix.getMinSymbolWidth() != null)
		{
			if (dataMatrix.getMinSymbolHeight() != null)
			{
				minSymbolDimension = new Dimension(dataMatrix.getMinSymbolWidth(), dataMatrix.getMinSymbolHeight());
			}
			else
			{
				minSymbolDimension = new Dimension(dataMatrix.getMinSymbolWidth(), dataMatrix.getMinSymbolWidth());
			}
		}
		else if (dataMatrix.getMinSymbolHeight() != null)
		{
			minSymbolDimension = new Dimension(dataMatrix.getMinSymbolHeight(), dataMatrix.getMinSymbolHeight());
		}
		if (minSymbolDimension != null)
		{
			dataMatrixBean.setMinSize(minSymbolDimension);
		}	
		
		Dimension maxSymbolDimension = null; 
		if (dataMatrix.getMaxSymbolWidth() != null)
		{
			if (dataMatrix.getMaxSymbolHeight() != null)
			{
				maxSymbolDimension = new Dimension(dataMatrix.getMaxSymbolWidth(), dataMatrix.getMaxSymbolHeight());
			}
			else
			{
				maxSymbolDimension = new Dimension(dataMatrix.getMaxSymbolWidth(), dataMatrix.getMaxSymbolWidth());
			}
		}
		else if (dataMatrix.getMaxSymbolHeight() != null)
		{
			maxSymbolDimension = new Dimension(dataMatrix.getMaxSymbolHeight(), dataMatrix.getMaxSymbolHeight());
		}
		if (maxSymbolDimension != null)
		{
			dataMatrixBean.setMaxSize(maxSymbolDimension);
		}	
		
		evaluateBarcodeRenderable(dataMatrix);
	}

	protected abstract void evaluateDataMatrix(DataMatrixComponent dataMatrix);

	@Override
	public void visitEANCode128(EAN128Component ean128)
	{
		EAN128Bean ean128Bean = new EAN128Bean();
		barcodeBean = ean128Bean;
		evaluateEANCode128(ean128);
		setBaseAttributes(ean128);
		if (ean128.getChecksumMode() != null)
		{
			ean128Bean.setChecksumMode(ChecksumMode.byName(ean128.getChecksumMode()));
		}
		evaluateBarcodeRenderable(ean128);
	}

	protected abstract void evaluateEANCode128(EAN128Component ean128);

	@Override
	public void visitCode39(Code39Component code39)
	{
		Code39Bean code39Bean = new Code39Bean();
		barcodeBean = code39Bean;
		evaluateCode39(code39);
		setBaseAttributes(code39);
		if (code39.getChecksumMode() != null)
		{
			code39Bean.setChecksumMode(ChecksumMode.byName(code39.getChecksumMode()));
		}
		if (code39.isDisplayChecksum() != null)
		{
			code39Bean.setDisplayChecksum(code39.isDisplayChecksum());
		}
		if (code39.isDisplayStartStop() != null)
		{
			code39Bean.setDisplayStartStop(code39.isDisplayStartStop());
		}
		if (code39.isExtendedCharSetEnabled() != null)
		{
			code39Bean.setExtendedCharSetEnabled(code39.isExtendedCharSetEnabled());
		}
		if (code39.getIntercharGapWidth() != null)
		{
			code39Bean.setIntercharGapWidth(code39.getIntercharGapWidth());
		}
		if (code39.getWideFactor() != null)
		{
			code39Bean.setWideFactor(code39.getWideFactor());
		}
		evaluateBarcodeRenderable(code39);
	}

	protected abstract void evaluateCode39(Code39Component code39);

	protected abstract void evaluateInterleaved2Of5(Interleaved2Of5Component interleaved2Of5);

	@Override
	public void visitInterleaved2Of5(Interleaved2Of5Component interleaved2Of5)
	{
		Interleaved2Of5Bean interleaved2Of5Bean = new Interleaved2Of5Bean();
		barcodeBean = interleaved2Of5Bean;
		evaluateInterleaved2Of5(interleaved2Of5);
		setBaseAttributes(interleaved2Of5);
		if (interleaved2Of5.getChecksumMode() != null)
		{
			interleaved2Of5Bean.setChecksumMode(ChecksumMode.byName(interleaved2Of5.getChecksumMode()));
		}
		if (interleaved2Of5.isDisplayChecksum() != null)
		{
			interleaved2Of5Bean.setDisplayChecksum(interleaved2Of5.isDisplayChecksum());
		}
		if (interleaved2Of5.getWideFactor() != null)
		{
			interleaved2Of5Bean.setWideFactor(interleaved2Of5.getWideFactor());
		}
		evaluateBarcodeRenderable(interleaved2Of5);
	}

	@Override
	public void visitUPCA(UPCAComponent upcA)
	{
		UPCABean upcABean = new UPCABean();
		barcodeBean = upcABean;
		evaluateUPCA(upcA);
		setBaseAttributes(upcA);
		if (upcA.getChecksumMode() != null)
		{
			upcABean.setChecksumMode(ChecksumMode.byName(upcA.getChecksumMode()));
		}
		evaluateBarcodeRenderable(upcA);
	}

	protected abstract void evaluateUPCA(UPCAComponent upcA);

	@Override
	public void visitUPCE(UPCEComponent upcE)
	{
		UPCEBean upcEBean = new UPCEBean();
		barcodeBean = upcEBean;
		evaluateUPCE(upcE);
		setBaseAttributes(upcE);
		if (upcE.getChecksumMode() != null)
		{
			upcEBean.setChecksumMode(ChecksumMode.byName(upcE.getChecksumMode()));
		}
		evaluateBarcodeRenderable(upcE);
	}

	protected abstract void evaluateUPCE(UPCEComponent upcE);

	@Override
	public void visitEAN13(EAN13Component ean13)
	{
		EAN13Bean ean13Bean = new EAN13Bean();
		barcodeBean = ean13Bean;
		evaluateEAN13(ean13);
		setBaseAttributes(ean13);
		if (ean13.getChecksumMode() != null)
		{
			ean13Bean.setChecksumMode(ChecksumMode.byName(ean13.getChecksumMode()));
		}
		evaluateBarcodeRenderable(ean13);
	}

	protected abstract void evaluateEAN13(EAN13Component ean13);

	@Override
	public void visitEAN8(EAN8Component ean8)
	{
		EAN8Bean ean8Bean = new EAN8Bean();
		barcodeBean = ean8Bean;
		evaluateEAN8(ean8);
		setBaseAttributes(ean8);
		if (ean8.getChecksumMode() != null)
		{
			ean8Bean.setChecksumMode(ChecksumMode.byName(ean8.getChecksumMode()));
		}
		evaluateBarcodeRenderable(ean8);
	}

	protected abstract void evaluateEAN8(EAN8Component ean8);

	@Override
	public void visitRoyalMailCustomer(
			RoyalMailCustomerComponent royalMailCustomer)
	{
		RoyalMailCBCBean mailBean = new RoyalMailCBCBean();
		barcodeBean = mailBean;
		evaluateRoyalMailCustomer(royalMailCustomer);
		setBaseAttributes(royalMailCustomer);
		setFourStateAttributes(royalMailCustomer, mailBean);
		evaluateBarcodeRenderable(royalMailCustomer);
	}

	protected void setFourStateAttributes(
			FourStateBarcodeComponent barcodeComponent, 
			AbstractFourStateBean barcodeBean)
	{
		if (barcodeComponent.getChecksumMode() != null)
		{
			barcodeBean.setChecksumMode(
					ChecksumMode.byName(barcodeComponent.getChecksumMode()));
		}
		
		if (barcodeComponent.getAscenderHeight() != null)
		{
			barcodeBean.setAscenderHeight(
					UnitConv.pt2mm(toPoints(barcodeComponent.getAscenderHeight())));
		}
		
		if (barcodeComponent.getIntercharGapWidth() != null)
		{
			barcodeBean.setIntercharGapWidth(
					UnitConv.pt2mm(toPoints(barcodeComponent.getIntercharGapWidth())));
		}
		
		if (barcodeComponent.getTrackHeight() != null)
		{
			barcodeBean.setTrackHeight(
					UnitConv.pt2mm(toPoints(barcodeComponent.getTrackHeight())));
		}
	}

	protected abstract void evaluateRoyalMailCustomer(
			RoyalMailCustomerComponent royalMailCustomer);

	@Override
	public void visitUSPSIntelligentMail(
			USPSIntelligentMailComponent intelligentMail)
	{
		USPSIntelligentMailBean mailBean = new USPSIntelligentMailBean();
		barcodeBean = mailBean;
		evaluateUSPSIntelligentMail(intelligentMail);
		setBaseAttributes(intelligentMail);
		setFourStateAttributes(intelligentMail, mailBean);
		evaluateBarcodeRenderable(intelligentMail);
	}

	protected abstract void evaluateUSPSIntelligentMail(
			USPSIntelligentMailComponent intelligentMail);

	@Override
	public void visitPostnet(POSTNETComponent postnet)
	{
		POSTNETBean postnetBean = new POSTNETBean();
		barcodeBean = postnetBean;
		evaluatePOSTNET(postnet);
		setBaseAttributes(postnet);
		
		if (postnet.getShortBarHeight() != null)
		{
			postnetBean.setShortBarHeight(
					UnitConv.pt2mm(toPoints(postnet.getShortBarHeight())));
		}
		
		if (postnet.getBaselinePosition() != null)
		{
			postnetBean.setBaselinePosition(
					BaselineAlignment.byName(postnet.getBaselinePosition()));
		}
		
		if (postnet.getChecksumMode() != null)
		{
			postnetBean.setChecksumMode(
					ChecksumMode.byName(postnet.getChecksumMode()));
		}
		
		if (postnet.getDisplayChecksum() != null)
		{
			postnetBean.setDisplayChecksum(
					postnet.getDisplayChecksum());
		}
		
		if (postnet.getIntercharGapWidth() != null)
		{
			postnetBean.setIntercharGapWidth(
					UnitConv.pt2mm(toPoints(postnet.getIntercharGapWidth())));
		}
		evaluateBarcodeRenderable(postnet);
	}

	protected abstract void evaluatePOSTNET(
			POSTNETComponent intelligentMail);

	@Override
	public void visitPDF417(PDF417Component pdf417)
	{
		PDF417Bean pdf417Bean = new PDF417Bean();
		barcodeBean = pdf417Bean;
		evaluatePDF417(pdf417);
		setBaseAttributes(pdf417);
		
		if (pdf417.getMinColumns() != null)
		{
			pdf417Bean.setMinCols(pdf417.getMinColumns());
		}
		if (pdf417.getMaxColumns() != null)
		{
			pdf417Bean.setMaxCols(pdf417.getMaxColumns());
		}
		if (pdf417.getMinRows() != null)
		{
			pdf417Bean.setMinRows(pdf417.getMinRows());
		}
		if (pdf417.getMaxRows() != null)
		{
			pdf417Bean.setMaxRows(pdf417.getMaxRows());
		}
		if (pdf417.getWidthToHeightRatio() != null)
		{
			pdf417Bean.setWidthToHeightRatio(
					pdf417.getWidthToHeightRatio());
		}
		if (pdf417.getErrorCorrectionLevel() != null)
		{
			pdf417Bean.setErrorCorrectionLevel(
					pdf417.getErrorCorrectionLevel());
		}
		evaluateBarcodeRenderable(pdf417);
	}

	protected abstract void evaluatePDF417(PDF417Component pdf417);

	@Override
	public void visitQRCode(QRCodeComponent qrCode)
	{
		qrCodeBean = new QRCodeBean();
		
		evaluateQRCode(qrCode);

		qrCodeBean.setMargin(qrCode.getMargin());
		qrCodeBean.setErrorCorrectionLevel(qrCode.getErrorCorrectionLevel());
		qrCodeBean.setQrVersion(qrCode.getQrVersion());
		evaluateBarcodeRenderable(qrCodeBean);
	}

	protected abstract void evaluateQRCode(QRCodeComponent qrCode);

}
