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
package net.sf.jasperreports.poi.view.save;

import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.poi.export.JRXlsExporter;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.SaveContributorFactory;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRMultipleSheetsXlsSaveContributor extends JRSaveContributor
{

	/**
	 * 
	 */
	private static final String EXTENSION_XLS = ".xls"; 

	/**
	 * @see #JRMultipleSheetsXlsSaveContributor(JasperReportsContext, Locale, ResourceBundle)
	 * @deprecated To be removed.
	 */
	public JRMultipleSheetsXlsSaveContributor(Locale locale, ResourceBundle resBundle)
	{
		super(locale, resBundle);
	}
	
	/**
	 * 
	 */
	public JRMultipleSheetsXlsSaveContributor(
		JasperReportsContext jasperReportsContext, 
		Locale locale, 
		ResourceBundle resBundle
		)
	{
		super(jasperReportsContext, locale, resBundle);
	}
	
	@Override
	public boolean accept(File file)
	{
		if (file.isDirectory())
		{
			return true;
		}
		return file.getName().toLowerCase().endsWith(EXTENSION_XLS);
	}

	@Override
	public String getDescription()
	{
		return getBundleString("file.desc.xls.multiple.sheets");
	}

	@Override
	public void save(JasperPrint jasperPrint, File file) throws JRException
	{
		if (!file.getName().toLowerCase().endsWith(EXTENSION_XLS))
		{
			file = new File(file.getAbsolutePath() + EXTENSION_XLS);
		}
		
		if (
			!file.exists() ||
			JOptionPane.OK_OPTION == 
				JOptionPane.showConfirmDialog(
					null, 
					MessageFormat.format(
						getBundleString("file.exists"),
						new Object[]{file.getName()}
						), 
					getBundleString("save"), 
					JOptionPane.OK_CANCEL_OPTION
					)
			)
		{
			JRXlsExporter exporter = new JRXlsExporter(getJasperReportsContext());
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint)); 
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
			SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
			configuration.setOnePagePerSheet(true);
			exporter.setConfiguration(configuration);
			exporter.exportReport(); 
		}
	}


	public static class Factory implements SaveContributorFactory
	{
		@Override
		public JRSaveContributor create(
			JasperReportsContext jasperReportsContext, 
			Locale locale,
			ResourceBundle resBundle) 
		{
			return new JRMultipleSheetsXlsSaveContributor(jasperReportsContext, locale, resBundle);
		}
	}
}
