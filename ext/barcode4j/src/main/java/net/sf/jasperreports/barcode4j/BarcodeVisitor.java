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
public interface BarcodeVisitor
{

	void visitCodabar(CodabarComponent codabar);

	void visitCode128(Code128Component code128);

	void visitEANCode128(EAN128Component ean128);

	void visitDataMatrix(DataMatrixComponent dataMatrix);

	void visitRoyalMailCustomer(RoyalMailCustomerComponent royalMailCustomer);

	void visitUSPSIntelligentMail(USPSIntelligentMailComponent intelligentMail);
	
	void visitCode39(Code39Component code39);

	void visitUPCA(UPCAComponent upcA);

	void visitUPCE(UPCEComponent upcE);

	void visitEAN13(EAN13Component ean13);

	void visitEAN8(EAN8Component ean8);

	void visitInterleaved2Of5(Interleaved2Of5Component interleaved2Of5);

	void visitPostnet(POSTNETComponent postnet);

	void visitPDF417(PDF417Component pdf417);
	
	void visitQRCode(QRCodeComponent qrCode);

}
