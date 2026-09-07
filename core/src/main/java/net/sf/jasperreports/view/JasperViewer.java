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

/*
 * Contributors:
 * Ryan Johnson - delscovich@users.sourceforge.net
 * Carlton Moore - cmoore79@users.sourceforge.net
 */
package net.sf.jasperreports.view;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class uses the {@link net.sf.jasperreports.swing.JRViewer} component to display reports. 
 * It represents a simple Java Swing application that can load and display reports. It is used 
 * in almost all of the supplied samples to display the generated documents.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JasperViewer extends javax.swing.JFrame 
{
	private static final Log log = LogFactory.getLog(JasperViewer.class);

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;


	/**
	 *
	 */
	protected net.sf.jasperreports.swing.JRViewer viewer;

	/**
	 *
	 */
	private boolean isExitOnClose = true;
	
	
	/**
	 * @see #JasperViewer(JasperReportsContext, String, boolean, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		String sourceFile, 
		boolean isXMLFile
		) throws JRException
	{
		this(sourceFile, isXMLFile, true);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, InputStream, boolean, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		InputStream is,
		boolean isXMLFile
		) throws JRException
	{
		this(is, isXMLFile, true);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, JasperPrint, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		JasperPrint jasperPrint
		)
	{
		this(jasperPrint, true);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, String, boolean, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose
		)  throws JRException
	{
		this(sourceFile, isXMLFile, isExitOnClose, null);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, InputStream, boolean, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose
		) throws JRException
	{
		this(is, isXMLFile, isExitOnClose, null);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, JasperPrint, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		JasperPrint jasperPrint,
		boolean isExitOnClose
		)
	{
		this(jasperPrint, isExitOnClose, null);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, String, boolean, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale
		)  throws JRException
	{
		this(
			DefaultJasperReportsContext.getInstance(), 
			sourceFile, 
			isXMLFile, 
			isExitOnClose, 
			locale, 
			null
			);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, InputStream, boolean, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale
		) throws JRException
	{
		this(
			DefaultJasperReportsContext.getInstance(), 
			is, 
			isXMLFile, 
			isExitOnClose, 
			locale, 
			null
			);
	}


	/**
	 * @see #JasperViewer(JasperReportsContext, JasperPrint, boolean, Locale, ResourceBundle)
	 */
	public JasperViewer(
		JasperPrint jasperPrint,
		boolean isExitOnClose,
		Locale locale
		)
	{
		this(
			DefaultJasperReportsContext.getInstance(), 
			jasperPrint, 
			isExitOnClose, 
			locale, 
			null
			);
	}


	/**
	 * 
	 */
	public JasperViewer(
		JasperReportsContext jasperReportsContext,
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale,
		ResourceBundle resBundle
		)  throws JRException
	{
		if (locale != null)
		{
			setLocale(locale);
		}
		this.isExitOnClose = isExitOnClose;

		initComponents();

		this.viewer = new net.sf.jasperreports.swing.JRViewer(jasperReportsContext, sourceFile, isXMLFile, locale, resBundle);
		this.pnlMain.add(this.viewer, BorderLayout.CENTER);
	}


	/**
	 * 
	 */
	public JasperViewer(
		JasperReportsContext jasperReportsContext,
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale,
		ResourceBundle resBundle
		) throws JRException
	{
		if (locale != null)
		{
			setLocale(locale);
		}
		this.isExitOnClose = isExitOnClose;

		initComponents();

		this.viewer = new net.sf.jasperreports.swing.JRViewer(jasperReportsContext, is, isXMLFile, locale, resBundle);
		this.pnlMain.add(this.viewer, BorderLayout.CENTER);
	}


	/**
	 * 
	 */
	public JasperViewer(
		JasperReportsContext jasperReportsContext,
		JasperPrint jasperPrint,
		boolean isExitOnClose,
		Locale locale,
		ResourceBundle resBundle
		)
	{
		if (locale != null)
		{
			setLocale(locale);
		}
		this.isExitOnClose = isExitOnClose;

		initComponents();

		this.viewer = new net.sf.jasperreports.swing.JRViewer(jasperReportsContext, jasperPrint, locale, resBundle);
		this.pnlMain.add(this.viewer, BorderLayout.CENTER);
	}


	/**
	 * 
	 */
	public JasperViewer(
		JasperReportsContext jasperReportsContext,
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose
		)  throws JRException
	{
		this(jasperReportsContext, sourceFile, isXMLFile, isExitOnClose, null, null);
	}


	/**
	 * 
	 */
	public JasperViewer(
		JasperReportsContext jasperReportsContext,
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose
		) throws JRException
	{
		this(jasperReportsContext, is, isXMLFile, isExitOnClose, null, null);
	}


	/**
	 * 
	 */
	public JasperViewer(
		JasperReportsContext jasperReportsContext,
		JasperPrint jasperPrint,
		boolean isExitOnClose
		)
	{
		this(jasperReportsContext, jasperPrint, isExitOnClose, null, null);
	}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {//GEN-BEGIN:initComponents
		pnlMain = new javax.swing.JPanel();

		setTitle("JasperViewer");
		setIconImage(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/jricon.GIF")).getImage());
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm();
			}
		});

		pnlMain.setLayout(new java.awt.BorderLayout());

		getContentPane().add(pnlMain, java.awt.BorderLayout.CENTER);

		pack();

		Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
		java.awt.Dimension screenSize = toolkit.getScreenSize();
		int screenResolution = toolkit.getScreenResolution();
		float zoom = ((float) screenResolution) / net.sf.jasperreports.swing.JRViewerPanel.REPORT_RESOLUTION;

		int height = (int) (550 * zoom);
		if (height > screenSize.getHeight())
		{
			height = (int) screenSize.getHeight();
		}
		int width = (int) (750 * zoom);
		if (width > screenSize.getWidth())
		{
			width = (int) screenSize.getWidth();
		}

		java.awt.Dimension dimension = new java.awt.Dimension(width, height);
		setSize(dimension);
		setLocation((screenSize.width-width)/2,(screenSize.height-height)/2);
	}//GEN-END:initComponents

	/** Exit the Application */
	void exitForm() {//GEN-FIRST:event_exitForm

		if (this.isExitOnClose)
		{
			System.exit(0);
		}
		else
		{
			this.setVisible(false);
			this.viewer.clear();
			this.viewer = null;
			this.getContentPane().removeAll();
			this.dispose();
		}

	}//GEN-LAST:event_exitForm


	/**
	 *
	 */
	public void setZoomRatio(float zoomRatio)
	{
		viewer.setZoomRatio(zoomRatio);
	}


	/**
	 *
	 */
	public void setFitWidthZoomRatio()
	{
		viewer.setFitWidthZoomRatio();
	}


	/**
	 *
	 */
	public void setFitPageZoomRatio()
	{
		viewer.setFitPageZoomRatio();
	}


	/**
	* @param args the command line arguments
	*/
	public static void main(String args[])
	{
		String fileName = null;
		boolean isXMLFile = false;

		for (int i = 0; i < args.length; i++)
		{
			if ( args[i].startsWith("-XML") )
			{
				isXMLFile = true;
			}
			else if ( args[i].startsWith("-F") )
			{
				fileName = args[i].substring(2);
			}
			else
			{
				fileName = args[i];
			}
		}
		
		if (fileName == null)
		{
			usage();
			return;
		}

		if (!isXMLFile && fileName.endsWith(".jrpxml"))
		{
			isXMLFile = true;
		}

		try
		{
			viewReport(fileName, isXMLFile);
		}
		catch (JRException e)
		{
			if (log.isErrorEnabled())
			{
				log.error("Error viewing report.", e);
			}
			System.exit(1);
		}
	}


	/**
	 *
	 */
	private static void usage()
	{
		System.out.println( "JasperViewer usage:" );
		System.out.println( "\tjava JasperViewer [-XML] file" );
	}


	/**
	 * @see #viewReport(JasperReportsContext, String, boolean, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		String sourceFile,
		boolean isXMLFile
		) throws JRException
	{
		viewReport(sourceFile, isXMLFile, true, null);
	}

	/**
	 * @see #viewReport(JasperReportsContext, InputStream, boolean, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		InputStream is,
		boolean isXMLFile
		) throws JRException
	{
		viewReport(is, isXMLFile, true, null);
	}

	/**
	 * @see #viewReport(JasperReportsContext, JasperPrint, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		JasperPrint jasperPrint
		)
	{
		viewReport(jasperPrint, true, null);
	}

	/**
	 * @see #viewReport(JasperReportsContext, String, boolean, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose
		) throws JRException
	{
		viewReport(sourceFile, isXMLFile, isExitOnClose, null);
	}

	/**
	 * @see #viewReport(JasperReportsContext, InputStream, boolean, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose
		) throws JRException
	{
		viewReport(is, isXMLFile, isExitOnClose, null);
	}

	/**
	 * @see #viewReport(JasperReportsContext, JasperPrint, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		JasperPrint jasperPrint,
		boolean isExitOnClose
		)
	{
		viewReport(jasperPrint, isExitOnClose, null);
	}

	/**
	 * @see #viewReport(JasperReportsContext, String, boolean, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale
		) throws JRException
	{
		viewReport(
			DefaultJasperReportsContext.getInstance(),
			sourceFile,
			isXMLFile,
			isExitOnClose,
			locale,
			null
			);
	}

	/**
	 * @see #viewReport(JasperReportsContext, InputStream, boolean, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale
		) throws JRException
	{
		viewReport(
			DefaultJasperReportsContext.getInstance(),
			is,
			isXMLFile,
			isExitOnClose,
			locale,
			null
			);
	}

	/**
	 * @see #viewReport(JasperReportsContext, JasperPrint, boolean, Locale, ResourceBundle)
	 */
	public static void viewReport(
		JasperPrint jasperPrint,
		boolean isExitOnClose,
		Locale locale
		)
	{
		viewReport(
			DefaultJasperReportsContext.getInstance(),
			jasperPrint,
			isExitOnClose,
			locale,
			null
			);
	}

	/**
	 *
	 */
	public static void viewReport(
		JasperReportsContext jasperReportsContext,
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale,
		ResourceBundle resBundle
		) throws JRException
	{
		JasperViewer jasperViewer =
			new JasperViewer(
				jasperReportsContext,
				sourceFile,
				isXMLFile,
				isExitOnClose,
				locale,
				resBundle
				);
		jasperViewer.setVisible(true);
	}

	/**
	 *
	 */
	public static void viewReport(
		JasperReportsContext jasperReportsContext,
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose,
		Locale locale,
		ResourceBundle resBundle
		) throws JRException
	{
		JasperViewer jasperViewer =
			new JasperViewer(
				jasperReportsContext,
				is,
				isXMLFile,
				isExitOnClose,
				locale,
				resBundle
				);
		jasperViewer.setVisible(true);
	}

	/**
	 *
	 */
	public static void viewReport(
		JasperReportsContext jasperReportsContext,
		JasperPrint jasperPrint,
		boolean isExitOnClose,
		Locale locale,
		ResourceBundle resBundle
		)
	{
		JasperViewer jasperViewer =
			new JasperViewer(
				jasperReportsContext,
				jasperPrint,
				isExitOnClose,
				locale,
				resBundle
				);
		jasperViewer.setVisible(true);
	}

	/**
	 *
	 */
	public static void viewReport(
		JasperReportsContext jasperReportsContext,
		String sourceFile,
		boolean isXMLFile,
		boolean isExitOnClose
		) throws JRException
	{
		viewReport(
			jasperReportsContext,
			sourceFile,
			isXMLFile,
			isExitOnClose,
			null,
			null
			);
	}

	/**
	 *
	 */
	public static void viewReport(
		JasperReportsContext jasperReportsContext,
		InputStream is,
		boolean isXMLFile,
		boolean isExitOnClose
		) throws JRException
	{
		viewReport(
			jasperReportsContext,
			is,
			isXMLFile,
			isExitOnClose,
			null,
			null
			);
	}

	/**
	 *
	 */
	public static void viewReport(
		JasperReportsContext jasperReportsContext,
		JasperPrint jasperPrint,
		boolean isExitOnClose
		)
	{
		viewReport(
			jasperReportsContext,
			jasperPrint,
			isExitOnClose,
			null,
			null
			);
	}


	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel pnlMain;
	// End of variables declaration//GEN-END:variables

}
