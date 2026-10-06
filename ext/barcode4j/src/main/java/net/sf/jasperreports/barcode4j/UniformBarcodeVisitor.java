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


/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class UniformBarcodeVisitor implements BarcodeVisitor
{
	protected abstract void visitBarcode(BarcodeComponent barcode);
	
	protected abstract void visitBarcode(Barcode4jComponent barcode);

	@Override
	public void visitCodabar(CodabarComponent codabar)
	{
		visitBarcode(codabar);
	}

	@Override
	public void visitCode128(Code128Component code128)
	{
		visitBarcode(code128);
	}

	@Override
	public void visitDataMatrix(DataMatrixComponent dataMatrix)
	{
		visitBarcode(dataMatrix);
	}

	@Override
	public void visitEANCode128(EAN128Component ean128)
	{
		visitBarcode(ean128);
	}

	@Override
	public void visitCode39(Code39Component code39)
	{
		visitBarcode(code39);
	}

	@Override
	public void visitUPCA(UPCAComponent upcA)
	{
		visitBarcode(upcA);
	}

	@Override
	public void visitUPCE(UPCEComponent upcE)
	{
		visitBarcode(upcE);
	}

	@Override
	public void visitEAN13(EAN13Component ean13)
	{
		visitBarcode(ean13);
	}

	@Override
	public void visitEAN8(EAN8Component ean8)
	{
		visitBarcode(ean8);
	}

	@Override
	public void visitInterleaved2Of5(Interleaved2Of5Component interleaved2Of5)
	{
		visitBarcode(interleaved2Of5);
	}

	@Override
	public void visitRoyalMailCustomer(
			RoyalMailCustomerComponent royalMailCustomer)
	{
		visitBarcode(royalMailCustomer);
	}

	@Override
	public void visitUSPSIntelligentMail(
			USPSIntelligentMailComponent intelligentMail)
	{
		visitBarcode(intelligentMail);
	}

	@Override
	public void visitPostnet(POSTNETComponent postnet)
	{
		visitBarcode(postnet);
	}

	@Override
	public void visitPDF417(PDF417Component pdf417)
	{
		visitBarcode(pdf417);
	}
	
	@Override
	public void visitQRCode(QRCodeComponent qrCode)
	{
		visitBarcode(qrCode);
	}
}
