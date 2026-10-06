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
package net.sf.jasperreports.barcode4j;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

import org.krysalis.barcode4j.BaselineAlignment;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BarcodeVerifier implements BarcodeVisitor
{

	private final JRVerifier verifier;
	
	public BarcodeVerifier(JRVerifier verifier)
	{
		this.verifier = verifier;
	}

	protected void verifyBarcode(BarcodeComponent barcode)
	{
		JRExpression codeExpression = barcode.getCodeExpression();
		if (codeExpression == null)
		{
			verifier.addBrokenRule("Barcode expression is null", barcode);
		}
		
		EvaluationTimeEnum evaluationTime = barcode.getEvaluationTime();
		if (evaluationTime == EvaluationTimeEnum.AUTO)
		{
			verifier.addBrokenRule("Auto evaluation time is not supported for barcodes", barcode);
		}
		else if (evaluationTime == EvaluationTimeEnum.GROUP)
		{
			String evaluationGroup = barcode.getEvaluationGroup();
			if (evaluationGroup == null || evaluationGroup.length() == 0)
			{
				verifier.addBrokenRule("No evaluation group set for barcode", barcode);
			}
			else if (!verifier.getReportDesign().getGroupsMap().containsKey(evaluationGroup))
			{
				verifier.addBrokenRule("Barcode evaluation group \"" 
						+ evaluationGroup + " not found", barcode);
			}
		}
	}

	protected void verifyChecksumMode(String checksumMode, BarcodeComponent barcode)
	{
		try
		{
			if (checksumMode != null)
			{
				ChecksumMode.byName(checksumMode);
			}
		}
		catch (Exception e)
		{
			verifier.addBrokenRule(e, barcode);
		}
	}

	@Override
	public void visitCodabar(CodabarComponent codabar)
	{
		verifyBarcode(codabar);
	}

	@Override
	public void visitCode128(Code128Component code128)
	{
		verifyBarcode(code128);
	}

	@Override
	public void visitEANCode128(EAN128Component ean128)
	{
		verifyBarcode(ean128);
		verifyChecksumMode(ean128.getChecksumMode(), ean128);
	}

	@Override
	public void visitDataMatrix(DataMatrixComponent dataMatrix)
	{
		verifyBarcode(dataMatrix);
		
		try
		{
			String shape = dataMatrix.getShape();
			if (shape != null)
			{
				SymbolShapeHint.byName(shape);
			}
		}
		catch (Exception e)
		{
			verifier.addBrokenRule(e, dataMatrix);
		}
	}

	@Override
	public void visitCode39(Code39Component code39)
	{
		verifyBarcode(code39);
		verifyChecksumMode(code39.getChecksumMode(), code39);
	}

	@Override
	public void visitUPCA(UPCAComponent upcA)
	{
		verifyBarcode(upcA);
		verifyChecksumMode(upcA.getChecksumMode(), upcA);
	}

	@Override
	public void visitUPCE(UPCEComponent upcE)
	{
		verifyBarcode(upcE);
		verifyChecksumMode(upcE.getChecksumMode(), upcE);
	}

	@Override
	public void visitEAN13(EAN13Component ean13)
	{
		verifyBarcode(ean13);
		verifyChecksumMode(ean13.getChecksumMode(), ean13);
	}

	@Override
	public void visitEAN8(EAN8Component ean8)
	{
		verifyBarcode(ean8);
		verifyChecksumMode(ean8.getChecksumMode(), ean8);
	}

	@Override
	public void visitInterleaved2Of5(Interleaved2Of5Component interleaved2Of5)
	{
		verifyBarcode(interleaved2Of5);
		verifyChecksumMode(interleaved2Of5.getChecksumMode(), interleaved2Of5);
	}

	@Override
	public void visitRoyalMailCustomer(
			RoyalMailCustomerComponent royalMailCustomer)
	{
		verifyBarcode(royalMailCustomer);
		verifyChecksumMode(royalMailCustomer.getChecksumMode(), royalMailCustomer);
	}

	@Override
	public void visitUSPSIntelligentMail(
			USPSIntelligentMailComponent intelligentMail)
	{
		verifyBarcode(intelligentMail);
		verifyChecksumMode(intelligentMail.getChecksumMode(), intelligentMail);
	}

	@Override
	public void visitPostnet(POSTNETComponent postnet)
	{
		verifyBarcode(postnet);
		verifyChecksumMode(postnet.getChecksumMode(), postnet);
		
		if (postnet.getBaselinePosition() != null)
		{
			try
			{
				BaselineAlignment.byName(postnet.getBaselinePosition());
			}
			catch (Exception e)
			{
				verifier.addBrokenRule(e, postnet);
			}
		}
	}

	@Override
	public void visitPDF417(PDF417Component pdf417)
	{
		verifyBarcode(pdf417);
	}
	
	@Override
	public void visitQRCode(QRCodeComponent qrCode)
	{
		verifyBarcode(qrCode);
	}
}
