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

import java.awt.event.KeyListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.SaveContributorUtils;
import net.sf.jasperreports.view.save.JRPrintSaveContributor;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRViewerToolbar extends JPanel implements JRViewerListener
{
	private static final Log log = LogFactory.getLog(JRViewerToolbar.class);
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected final JRViewerController viewerContext;
	
	protected final float MIN_ZOOM = 0.5f;
	protected final float MAX_ZOOM = 10f;
	protected int zooms[] = {50, 75, 100, 125, 150, 175, 200, 250, 400, 800};
	protected int defaultZoomIndex = 2;
	protected List<JRSaveContributor> saveContributors = new ArrayList<>();
	protected File lastFolder;
	protected JRSaveContributor lastSaveContributor;
	protected DecimalFormat zoomDecimalFormat;
	
	protected javax.swing.JToggleButton btnActualSize;
	protected javax.swing.JButton btnFirst;
	protected javax.swing.JToggleButton btnFitPage;
	protected javax.swing.JToggleButton btnFitWidth;
	protected javax.swing.JButton btnLast;
	protected javax.swing.JButton btnNext;
	protected javax.swing.JButton btnPrevious;
	protected javax.swing.JButton btnPrint;
	protected javax.swing.JButton btnReload;
	protected javax.swing.JButton btnSave;
	protected javax.swing.JButton btnZoomIn;
	protected javax.swing.JButton btnZoomOut;
	protected javax.swing.JComboBox cmbZoom;
	protected javax.swing.JPanel pnlSep01;
	protected javax.swing.JPanel pnlSep02;
	protected javax.swing.JPanel pnlSep03;
	protected javax.swing.JTextField txtGoTo;

	public JRViewerToolbar(JRViewerController viewerContext)
	{
		this.viewerContext = viewerContext;
		this.viewerContext.addListener(this);
		
		zoomDecimalFormat = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(viewerContext.getLocale()));

		initComponents();
		initSaveContributors();
	}

	private void initComponents()
	{
		btnSave = new javax.swing.JButton();
		btnPrint = new javax.swing.JButton();
		btnReload = new javax.swing.JButton();
		pnlSep01 = new javax.swing.JPanel();
		btnFirst = new javax.swing.JButton();
		btnPrevious = new javax.swing.JButton();
		btnNext = new javax.swing.JButton();
		btnLast = new javax.swing.JButton();
		txtGoTo = new javax.swing.JTextField();
		pnlSep02 = new javax.swing.JPanel();
		btnActualSize = new javax.swing.JToggleButton();
		btnFitPage = new javax.swing.JToggleButton();
		btnFitWidth = new javax.swing.JToggleButton();
		pnlSep03 = new javax.swing.JPanel();
		btnZoomIn = new javax.swing.JButton();
		btnZoomOut = new javax.swing.JButton();
		cmbZoom = new javax.swing.JComboBox();
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (int i = 0; i < zooms.length; i++)
		{
			model.addElement("" + zooms[i] + "%");
		}
		cmbZoom.setModel(model);
		
		setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 2));

		btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/save.GIF")));
		btnSave.setToolTipText(viewerContext.getBundleString("save"));
		btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnSave.setMaximumSize(new java.awt.Dimension(23, 23));
		btnSave.setMinimumSize(new java.awt.Dimension(23, 23));
		btnSave.setPreferredSize(new java.awt.Dimension(23, 23));
		btnSave.addActionListener(this::btnSaveActionPerformed);
		add(btnSave);

		btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/print.GIF")));
		btnPrint.setToolTipText(viewerContext.getBundleString("print"));
		btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnPrint.setMaximumSize(new java.awt.Dimension(23, 23));
		btnPrint.setMinimumSize(new java.awt.Dimension(23, 23));
		btnPrint.setPreferredSize(new java.awt.Dimension(23, 23));
		btnPrint.addActionListener(this::btnPrintActionPerformed);
		add(btnPrint);

		btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/reload.GIF")));
		btnReload.setToolTipText(viewerContext.getBundleString("reload"));
		btnReload.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnReload.setMaximumSize(new java.awt.Dimension(23, 23));
		btnReload.setMinimumSize(new java.awt.Dimension(23, 23));
		btnReload.setPreferredSize(new java.awt.Dimension(23, 23));
		btnReload.addActionListener(this::btnReloadActionPerformed);
		add(btnReload);

		pnlSep01.setMaximumSize(new java.awt.Dimension(10, 10));
		add(pnlSep01);

		btnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/first.GIF")));
		btnFirst.setToolTipText(viewerContext.getBundleString("first.page"));
		btnFirst.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnFirst.setMaximumSize(new java.awt.Dimension(23, 23));
		btnFirst.setMinimumSize(new java.awt.Dimension(23, 23));
		btnFirst.setPreferredSize(new java.awt.Dimension(23, 23));
		btnFirst.addActionListener(this::btnFirstActionPerformed);
		add(btnFirst);

		btnPrevious.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/previous.GIF")));
		btnPrevious.setToolTipText(viewerContext.getBundleString("previous.page"));
		btnPrevious.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnPrevious.setMaximumSize(new java.awt.Dimension(23, 23));
		btnPrevious.setMinimumSize(new java.awt.Dimension(23, 23));
		btnPrevious.setPreferredSize(new java.awt.Dimension(23, 23));
		btnPrevious.addActionListener(this::btnPreviousActionPerformed);
		add(btnPrevious);

		btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/next.GIF")));
		btnNext.setToolTipText(viewerContext.getBundleString("next.page"));
		btnNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnNext.setMaximumSize(new java.awt.Dimension(23, 23));
		btnNext.setMinimumSize(new java.awt.Dimension(23, 23));
		btnNext.setPreferredSize(new java.awt.Dimension(23, 23));
		btnNext.addActionListener(this::btnNextActionPerformed);
		add(btnNext);

		btnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/last.GIF")));
		btnLast.setToolTipText(viewerContext.getBundleString("last.page"));
		btnLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnLast.setMaximumSize(new java.awt.Dimension(23, 23));
		btnLast.setMinimumSize(new java.awt.Dimension(23, 23));
		btnLast.setPreferredSize(new java.awt.Dimension(23, 23));
		btnLast.addActionListener(this::btnLastActionPerformed);
		add(btnLast);

		txtGoTo.setToolTipText(viewerContext.getBundleString("go.to.page"));
		txtGoTo.setMaximumSize(new java.awt.Dimension(40, 23));
		txtGoTo.setMinimumSize(new java.awt.Dimension(40, 23));
		txtGoTo.setPreferredSize(new java.awt.Dimension(40, 23));
		txtGoTo.addActionListener(this::txtGoToActionPerformed);
		add(txtGoTo);

		pnlSep02.setMaximumSize(new java.awt.Dimension(10, 10));
		add(pnlSep02);

		btnActualSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/actualsize.GIF")));
		btnActualSize.setToolTipText(viewerContext.getBundleString("actual.size"));
		btnActualSize.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnActualSize.setMaximumSize(new java.awt.Dimension(23, 23));
		btnActualSize.setMinimumSize(new java.awt.Dimension(23, 23));
		btnActualSize.setPreferredSize(new java.awt.Dimension(23, 23));
		btnActualSize.addActionListener(this::btnActualSizeActionPerformed);
		add(btnActualSize);

		btnFitPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/fitpage.GIF")));
		btnFitPage.setToolTipText(viewerContext.getBundleString("fit.page"));
		btnFitPage.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnFitPage.setMaximumSize(new java.awt.Dimension(23, 23));
		btnFitPage.setMinimumSize(new java.awt.Dimension(23, 23));
		btnFitPage.setPreferredSize(new java.awt.Dimension(23, 23));
		btnFitPage.addActionListener(this::btnFitPageActionPerformed);
		add(btnFitPage);

		btnFitWidth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/fitwidth.GIF")));
		btnFitWidth.setToolTipText(viewerContext.getBundleString("fit.width"));
		btnFitWidth.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnFitWidth.setMaximumSize(new java.awt.Dimension(23, 23));
		btnFitWidth.setMinimumSize(new java.awt.Dimension(23, 23));
		btnFitWidth.setPreferredSize(new java.awt.Dimension(23, 23));
		btnFitWidth.addActionListener(this::btnFitWidthActionPerformed);
		add(btnFitWidth);

		pnlSep03.setMaximumSize(new java.awt.Dimension(10, 10));
		add(pnlSep03);

		btnZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/zoomin.GIF")));
		btnZoomIn.setToolTipText(viewerContext.getBundleString("zoom.in"));
		btnZoomIn.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnZoomIn.setMaximumSize(new java.awt.Dimension(23, 23));
		btnZoomIn.setMinimumSize(new java.awt.Dimension(23, 23));
		btnZoomIn.setPreferredSize(new java.awt.Dimension(23, 23));
		btnZoomIn.addActionListener(this::btnZoomInActionPerformed);
		add(btnZoomIn);

		btnZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/jasperreports/view/images/zoomout.GIF")));
		btnZoomOut.setToolTipText(viewerContext.getBundleString("zoom.out"));
		btnZoomOut.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnZoomOut.setMaximumSize(new java.awt.Dimension(23, 23));
		btnZoomOut.setMinimumSize(new java.awt.Dimension(23, 23));
		btnZoomOut.setPreferredSize(new java.awt.Dimension(23, 23));
		btnZoomOut.addActionListener(this::btnZoomOutActionPerformed);
		add(btnZoomOut);

		cmbZoom.setEditable(true);
		cmbZoom.setToolTipText(viewerContext.getBundleString("zoom.ratio"));
		cmbZoom.setMaximumSize(new java.awt.Dimension(80, 23));
		cmbZoom.setMinimumSize(new java.awt.Dimension(80, 23));
		cmbZoom.setPreferredSize(new java.awt.Dimension(80, 23));
		cmbZoom.addActionListener(this::cmbZoomActionPerformed);
		cmbZoom.addItemListener(this::cmbZoomItemStateChanged);
		add(cmbZoom);
	}
	
	public void init()
	{
		cmbZoom.setSelectedIndex(defaultZoomIndex);
	}
	
	public void addComponentKeyListener(KeyListener listener)
	{
		btnSave.addKeyListener(listener);
		btnPrint.addKeyListener(listener);
		btnReload.addKeyListener(listener);
		btnFirst.addKeyListener(listener);
		btnPrevious.addKeyListener(listener);
		btnNext.addKeyListener(listener);
		btnLast.addKeyListener(listener);
		txtGoTo.addKeyListener(listener);
		btnActualSize.addKeyListener(listener);
		btnFitPage.addKeyListener(listener);
		btnFitWidth.addKeyListener(listener);
		btnZoomIn.addKeyListener(listener);
		btnZoomOut.addKeyListener(listener);
		cmbZoom.addKeyListener(listener);
	}

	void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
		// Add your handling code here:

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setLocale(this.getLocale());
		fileChooser.updateUI();
		for (int i = 0; i < saveContributors.size(); i++)
		{
			fileChooser.addChoosableFileFilter(saveContributors.get(i));
		}

		if (saveContributors.contains(lastSaveContributor))
		{
			fileChooser.setFileFilter(lastSaveContributor);
		}
		else if (saveContributors.size() > 0)
		{
			fileChooser.setFileFilter(saveContributors.get(0));
		}
		
		if (lastFolder != null)
		{
			fileChooser.setCurrentDirectory(lastFolder);
		}
		
		int retValue = fileChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION)
		{
			FileFilter fileFilter = fileChooser.getFileFilter();
			File file = fileChooser.getSelectedFile();
			
			lastFolder = file.getParentFile();

			JRSaveContributor contributor = null;

			if (fileFilter instanceof JRSaveContributor)
			{
				contributor = (JRSaveContributor)fileFilter;
			}
			else
			{
				int i = 0;
				while (contributor == null && i < saveContributors.size())
				{
					contributor = saveContributors.get(i++);
					if (!contributor.accept(file))
					{
						contributor = null;
					}
				}

				if (contributor == null)
				{
					contributor = new JRPrintSaveContributor(viewerContext.getJasperReportsContext(), getLocale(), viewerContext.getResourceBundle());
				}
			}

			lastSaveContributor = contributor;
			
			try
			{
				contributor.save(viewerContext.getJasperPrint(), file);
			}
			catch (JRException | JRRuntimeException e)
			{
				if (log.isErrorEnabled())
				{
					log.error("Save error.", e);
				}
				JOptionPane.showMessageDialog(this, viewerContext.getBundleString("error.saving"));
			}
		}
	}//GEN-LAST:event_btnSaveActionPerformed

	void btnPrintActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPrintActionPerformed
	{//GEN-HEADEREND:event_btnPrintActionPerformed
		// Add your handling code here:

		Thread thread =
			new Thread(
				new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							btnPrint.setEnabled(false);
							JasperPrintManager.getInstance(viewerContext.getJasperReportsContext()).print(viewerContext.getJasperPrint(), true);
						}
						catch (Exception ex)
						{
							if (log.isErrorEnabled())
							{
								log.error("Print error.", ex);
							}
							JOptionPane.showMessageDialog(JRViewerToolbar.this, 
									viewerContext.getBundleString("error.printing"));
						}
						finally
						{
							btnPrint.setEnabled(true);
						}
					}
				}
			);

		thread.start();

	}//GEN-LAST:event_btnPrintActionPerformed

	void btnReloadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnReloadActionPerformed
	{//GEN-HEADEREND:event_btnReloadActionPerformed
		// Add your handling code here:
		viewerContext.reload();
	}//GEN-LAST:event_btnReloadActionPerformed

	void btnFirstActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnFirstActionPerformed
	{//GEN-HEADEREND:event_btnFirstActionPerformed
		// Add your handling code here:
		viewerContext.setPageIndex(0);
		viewerContext.refreshPage();
	}//GEN-LAST:event_btnFirstActionPerformed

	void btnPreviousActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnPreviousActionPerformed
	{//GEN-HEADEREND:event_btnPreviousActionPerformed
		// Add your handling code here:
		viewerContext.setPageIndex(viewerContext.getPageIndex() - 1);
		viewerContext.refreshPage();
	}//GEN-LAST:event_btnPreviousActionPerformed

	void btnNextActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnNextActionPerformed
	{//GEN-HEADEREND:event_btnNextActionPerformed
		// Add your handling code here:
		viewerContext.setPageIndex(viewerContext.getPageIndex() + 1);
		viewerContext.refreshPage();
	}//GEN-LAST:event_btnNextActionPerformed

	void btnLastActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnLastActionPerformed
	{//GEN-HEADEREND:event_btnLastActionPerformed
		// Add your handling code here:
		viewerContext.setPageIndex(viewerContext.getPageCount() - 1);
		viewerContext.refreshPage();
	}//GEN-LAST:event_btnLastActionPerformed

	void txtGoToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGoToActionPerformed
		try
		{
			int pageNumber = Integer.parseInt(txtGoTo.getText());
			if (
				pageNumber != viewerContext.getPageIndex() + 1
				&& pageNumber > 0
				&& pageNumber <= viewerContext.getPageCount()
				)
			{
				viewerContext.setPageIndex(pageNumber - 1);
				viewerContext.refreshPage();
			}
		}
		catch(NumberFormatException e)
		{
		}
	}//GEN-LAST:event_txtGoToActionPerformed

	void btnActualSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualSizeActionPerformed
		// Add your handling code here:
		if (btnActualSize.isSelected())
		{
			btnFitPage.setSelected(false);
			btnFitWidth.setSelected(false);
			cmbZoom.setSelectedIndex(-1);
			viewerContext.setZoomRatio(1);
			btnActualSize.setSelected(true);
		}
	}//GEN-LAST:event_btnActualSizeActionPerformed

	void btnFitPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitPageActionPerformed
		// Add your handling code here:
		if (btnFitPage.isSelected())
		{
			btnActualSize.setSelected(false);
			btnFitWidth.setSelected(false);
			cmbZoom.setSelectedIndex(-1);
			viewerContext.fitPage();
			btnFitPage.setSelected(true);
		}
	}//GEN-LAST:event_btnFitPageActionPerformed

	void btnFitWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFitWidthActionPerformed
		// Add your handling code here:
		if (btnFitWidth.isSelected())
		{
			btnActualSize.setSelected(false);
			btnFitPage.setSelected(false);
			cmbZoom.setSelectedIndex(-1);
			viewerContext.fitWidth();
			btnFitWidth.setSelected(true);
		}
	}//GEN-LAST:event_btnFitWidthActionPerformed

	void btnZoomInActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomInActionPerformed
	{//GEN-HEADEREND:event_btnZoomInActionPerformed
		// Add your handling code here:
		btnActualSize.setSelected(false);
		btnFitPage.setSelected(false);
		btnFitWidth.setSelected(false);

		int newZoomInt = (int)(100 * getZoomRatio());
		int index = Arrays.binarySearch(zooms, newZoomInt);
		if (index < 0)
		{
			viewerContext.setZoomRatio(zooms[- index - 1] / 100f);
		}
		else if (index < cmbZoom.getModel().getSize() - 1)
		{
			viewerContext.setZoomRatio(zooms[index + 1] / 100f);
		}
	}//GEN-LAST:event_btnZoomInActionPerformed


	protected float getZoomRatio()
	{
		float newZoom = viewerContext.getZoom();

		try
		{
			newZoom =
				zoomDecimalFormat.parse(
					String.valueOf(cmbZoom.getEditor().getItem())
					).floatValue() / 100f;
		}
		catch(ParseException e)
		{
		}

		return newZoom;
	}

	void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnZoomOutActionPerformed
	{//GEN-HEADEREND:event_btnZoomOutActionPerformed
		// Add your handling code here:
		btnActualSize.setSelected(false);
		btnFitPage.setSelected(false);
		btnFitWidth.setSelected(false);

		int newZoomInt = (int)(100 * getZoomRatio());
		int index = Arrays.binarySearch(zooms, newZoomInt);
		if (index > 0)
		{
			viewerContext.setZoomRatio(zooms[index - 1] / 100f);
		}
		else if (index < -1)
		{
			viewerContext.setZoomRatio(zooms[- index - 2] / 100f);
		}
	}//GEN-LAST:event_btnZoomOutActionPerformed

	void cmbZoomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmbZoomActionPerformed
	{//GEN-HEADEREND:event_cmbZoomActionPerformed
		// Add your handling code here:
		float newZoom = getZoomRatio();

		if (newZoom < MIN_ZOOM)
		{
			newZoom = MIN_ZOOM;
		}

		if (newZoom > MAX_ZOOM)
		{
			newZoom = MAX_ZOOM;
		}

		viewerContext.setZoomRatio(newZoom);
	}//GEN-LAST:event_cmbZoomActionPerformed

	void cmbZoomItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbZoomItemStateChanged
		// Add your handling code here:
		btnActualSize.setSelected(false);
		btnFitPage.setSelected(false);
		btnFitWidth.setSelected(false);
	}//GEN-LAST:event_cmbZoomItemStateChanged


	/**
	 *
	 */
	public void addSaveContributor(JRSaveContributor contributor)
	{
		saveContributors.add(contributor);
	}


	/**
	 *
	 */
	public void removeSaveContributor(JRSaveContributor contributor)
	{
		saveContributors.remove(contributor);
	}


	/**
	 *
	 */
	public JRSaveContributor[] getSaveContributors()
	{
		return saveContributors.toArray(new JRSaveContributor[saveContributors.size()]);
	}


	/**
	 * Replaces the save contributors with the ones provided as parameter. 
	 */
	public void setSaveContributors(JRSaveContributor[] saveContributors)
	{
		this.saveContributors = new ArrayList<>();
		if (saveContributors != null)
		{
			this.saveContributors.addAll(Arrays.asList(saveContributors));
		}
	}


	/**
	 *
	 */
	protected void initSaveContributors()
	{
		List<JRSaveContributor> builtinContributors = SaveContributorUtils.createBuiltinContributors(
				viewerContext.getJasperReportsContext(), getLocale(), viewerContext.getResourceBundle());
		saveContributors.addAll(builtinContributors);
	}

	protected void reportLoaded()
	{
		btnReload.setEnabled(viewerContext.isReloadSupported());
	}

	protected void refreshPage()
	{
		if (!viewerContext.hasPages())
		{
			btnSave.setEnabled(false);
			btnPrint.setEnabled(false);
			btnActualSize.setEnabled(false);
			btnFitPage.setEnabled(false);
			btnFitWidth.setEnabled(false);
			btnZoomIn.setEnabled(false);
			btnZoomOut.setEnabled(false);
			cmbZoom.setEnabled(false);

			return;
		}

		btnSave.setEnabled(true);
		btnPrint.setEnabled(true);
		btnActualSize.setEnabled(true);
		btnFitPage.setEnabled(true);
		btnFitWidth.setEnabled(true);
		btnZoomIn.setEnabled(viewerContext.getZoom() < MAX_ZOOM);
		btnZoomOut.setEnabled(viewerContext.getZoom() > MIN_ZOOM);
		cmbZoom.setEnabled(true);
	}

	protected void pageChanged()
	{
		if (viewerContext.hasPages())
		{
			int pageIndex = viewerContext.getPageIndex();
			btnFirst.setEnabled( (pageIndex > 0) );
			btnPrevious.setEnabled( (pageIndex > 0) );
			boolean notLast = pageIndex < viewerContext.getPageCount() - 1;
			btnNext.setEnabled(notLast);
			btnLast.setEnabled(notLast);
			txtGoTo.setEnabled(btnFirst.isEnabled() || btnLast.isEnabled());
			txtGoTo.setText("" + (pageIndex + 1));
		}
		else
		{
			btnFirst.setEnabled(false);
			btnPrevious.setEnabled(false);
			btnNext.setEnabled(false);
			btnLast.setEnabled(false);
			txtGoTo.setEnabled(false);
			txtGoTo.setText("");
		}
	}

	protected void zoomChanged()
	{
		cmbZoom.getEditor().setItem(zoomDecimalFormat.format(viewerContext.getZoom() * 100) + "%");
	}

	public boolean isFitPage()
	{
		return btnFitPage.isSelected();
	}

	public boolean isFitWidth()
	{
		return btnFitPage.isSelected();
	}

	public void setFitWidth()
	{
		btnFitWidth.setSelected(true);
	}

	protected void fitPage()
	{
		btnFitPage.setSelected(true);
	}

	protected void fitWidth()
	{
		btnFitWidth.setSelected(true);
	}

	@Override
	public void viewerEvent(JRViewerEvent event)
	{
		switch (event.getCode())
		{
		case JRViewerEvent.EVENT_FIT_PAGE:
			fitPage();
			break;
		case JRViewerEvent.EVENT_FIT_WIDTH:
			fitWidth();
			break;
		case JRViewerEvent.EVENT_PAGE_CHANGED:
			pageChanged();
			break;
		case JRViewerEvent.EVENT_REFRESH_PAGE:
			refreshPage();
			break;
		case JRViewerEvent.EVENT_ZOOM_CHANGED:
			zoomChanged();
			break;
		case JRViewerEvent.EVENT_REPORT_LOADED:
			reportLoaded();
			break;
		default:
		}
	}
}
