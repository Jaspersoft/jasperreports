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
package net.sf.jasperreports.pdf;

import net.sf.jasperreports.pdf.common.PdfTagger;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated Replaced by {@link PdfTagger}.
 */
public class JRPdfExporterTagHelper
{
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_L}.
	 */
	public static final String PROPERTY_TAG_L = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.l";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_LI}.
	 */
	public static final String PROPERTY_TAG_LI = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.li";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_LBL}.
	 */
	public static final String PROPERTY_TAG_LBL = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.lbl";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_LBODY}.
	 */
	public static final String PROPERTY_TAG_LBODY = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.lbody";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_REFERENCE}.
	 */
	public static final String PROPERTY_TAG_REFERENCE = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.reference";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_NOTE}.
	 */
	public static final String PROPERTY_TAG_NOTE = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.note";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_H1}.
	 */
	public static final String PROPERTY_TAG_H1 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h1";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_H2}.
	 */
	public static final String PROPERTY_TAG_H2 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h2";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_H3}.
	 */
	public static final String PROPERTY_TAG_H3 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h3";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_H4}.
	 */
	public static final String PROPERTY_TAG_H4 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h4";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_H5}.
	 */
	public static final String PROPERTY_TAG_H5 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h5";
	/**
	 * @deprecated Replaced by {@link PdfTagger#PROPERTY_TAG_H6}.
	 */
	public static final String PROPERTY_TAG_H6 = JRPdfExporter.PDF_EXPORTER_PROPERTIES_PREFIX + "tag.h6";
}
