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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.component.FillContext;
import net.sf.jasperreports.engine.util.JRStringUtil;

import org.krysalis.barcode4j.impl.code128.EAN128Bean;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarcodeEvaluator extends AbstractBarcodeEvaluator
{

	private final FillContext fillContext;
	private final byte evaluationType;
	
	public BarcodeEvaluator(FillContext fillContext, byte evaluationType)
	{
		super(
			fillContext.getFiller().getJasperReportsContext(), 
			fillContext.getComponentElement(), 
			fillContext.getDefaultStyleProvider(),
			fillContext.getFiller().getDpi()
			);
		
		this.fillContext = fillContext;
		this.evaluationType = evaluationType;
	}
	
	protected void evaluateBaseBarcode(BarcodeComponent barcodeComponent)
	{
		message = JRStringUtil.getString(evaluateExpression(barcodeComponent.getCodeExpression()));
	}
	
	protected void evaluateBaseBarcode(Barcode4jComponent barcodeComponent)
	{
		evaluateBaseBarcode((BarcodeComponent)barcodeComponent);
		
		String pattern = JRStringUtil.getString(evaluateExpression(barcodeComponent.getPatternExpression()));
		if (pattern != null) 
		{
			barcodeBean.setPattern(pattern);
		}
	}
	
	protected Object evaluateExpression(JRExpression expression)
	{
		try
		{
			return fillContext.evaluate(expression, evaluationType);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	@Override
	protected void evaluateCodabar(CodabarComponent codabar)
	{
		evaluateBaseBarcode(codabar);
	}

	@Override
	protected void evaluateCode128(Code128Component code128)
	{
		evaluateBaseBarcode(code128);
	}

	@Override
	protected void evaluateDataMatrix(DataMatrixComponent dataMatrix)
	{
		evaluateBaseBarcode(dataMatrix);
	}

	@Override
	protected void evaluateEANCode128(EAN128Component ean128)
	{
		evaluateBaseBarcode(ean128);
		String template = JRStringUtil.getString(evaluateExpression(ean128.getTemplateExpression()));
		if (template != null) 
		{
			((EAN128Bean)barcodeBean).setTemplate(template);
		}
	}

	@Override
	protected void evaluateCode39(Code39Component code39)
	{
		evaluateBaseBarcode(code39);
	}

	@Override
	protected void evaluateUPCA(UPCAComponent upcA)
	{
		evaluateBaseBarcode(upcA);
	}

	@Override
	protected void evaluateUPCE(UPCEComponent upcE)
	{
		evaluateBaseBarcode(upcE);
	}

	@Override
	protected void evaluateEAN13(EAN13Component ean13)
	{
		evaluateBaseBarcode(ean13);
	}

	@Override
	protected void evaluateEAN8(EAN8Component ean8)
	{
		evaluateBaseBarcode(ean8);
	}

	@Override
	protected void evaluateInterleaved2Of5(Interleaved2Of5Component interleaved2Of5)
	{
		evaluateBaseBarcode(interleaved2Of5);
	}

	@Override
	protected void evaluateRoyalMailCustomer(
			RoyalMailCustomerComponent royalMailCustomer)
	{
		evaluateBaseBarcode(royalMailCustomer);
	}

	@Override
	protected void evaluateUSPSIntelligentMail(
			USPSIntelligentMailComponent intelligentMail)
	{
		evaluateBaseBarcode(intelligentMail);
	}

	@Override
	protected void evaluatePOSTNET(POSTNETComponent intelligentMail)
	{
		evaluateBaseBarcode(intelligentMail);
	}

	@Override
	protected void evaluatePDF417(PDF417Component pdf417)
	{
		evaluateBaseBarcode(pdf417);
	}
	
	@Override
	protected void evaluateQRCode(QRCodeComponent qrCode)
	{
		evaluateBaseBarcode(qrCode);
	}

}
