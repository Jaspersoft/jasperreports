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

package net.sf.jasperreports.swing;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.convert.ReportConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignViewer extends JRViewer
{
	private static final Log log = LogFactory.getLog(JRDesignViewer.class);

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	/**
	 * @see #JRDesignViewer(JasperReportsContext, String, boolean)
	 */
	public JRDesignViewer(String fileName, boolean isXML) throws JRException
	{
		this(DefaultJasperReportsContext.getInstance(), fileName, isXML);
	}
	
	/**
	 * @see #JRDesignViewer(JasperReportsContext, InputStream, boolean)
	 */
	public JRDesignViewer(InputStream is, boolean isXML) throws JRException
	{
		this(DefaultJasperReportsContext.getInstance(), is, isXML);
	}
	
	/**
	 * @see #JRDesignViewer(JasperReportsContext, JRReport)
	 */
	public JRDesignViewer(JRReport report) throws JRException
	{
		this(DefaultJasperReportsContext.getInstance(), report);
	}
	
	/**
	 *
	 */
	public JRDesignViewer(
		JasperReportsContext jasperReportsContext,
		String fileName,
		boolean isXML
		) throws JRException
	{
		super(jasperReportsContext, fileName, isXML, null, null);
		hideUnusedComponents();
	}
	
	/**
	 *
	 */
	public JRDesignViewer(
		JasperReportsContext jasperReportsContext,
		InputStream is,
		boolean isXML
		) throws JRException
	{
		super(jasperReportsContext, is, isXML, null, null);
		hideUnusedComponents();
	}
	
	/**
	 *
	 */
	public JRDesignViewer(
		JasperReportsContext jasperReportsContext,
		JRReport report
		) throws JRException
	{
		super(jasperReportsContext, new ReportConverter(jasperReportsContext, report, false).getJasperPrint(), null, null);
		//reconfigureReloadButton();
		hideUnusedComponents();
	}
	
	private void hideUnusedComponents()
	{
		pnlStatus.setVisible(false);
	}

	@Override
	protected void initViewerContext(JasperReportsContext jasperReportsContext, Locale locale, ResourceBundle resBundle)
	{
		viewerContext = new JRDesignViewerController(jasperReportsContext, locale, resBundle);
		setLocale(viewerContext.getLocale());
		viewerContext.addListener(this);
	}

	@Override
	protected JRViewerToolbar createToolbar()
	{
		return new JRDesignViewerToolbar(viewerContext);
	}

	@Override
	protected JRViewerPanel createViewerPanel()
	{
		return new JRDesignViewerPanel(viewerContext);
	}

}
