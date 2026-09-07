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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintAnchorIndex;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.PrintParts;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleGraphics2DReportConfiguration;
import net.sf.jasperreports.renderers.AreaHyperlinksRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.view.JRHyperlinkListener;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRViewerPanel extends JPanel implements JRHyperlinkListener, JRViewerListener
{
	private static final Log log = LogFactory.getLog(JRViewerPanel.class);
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * The DPI of the generated report.
	 */
	public static final int REPORT_RESOLUTION = 72;
	
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private PageRenderer lblPage;
	private javax.swing.JPanel pnlInScroll;
	private javax.swing.JPanel pnlLinks;
	private javax.swing.JPanel pnlPage;
	private javax.swing.JTabbedPane pnlTabs;
	private javax.swing.JScrollPane scrollPane;

	protected final JRViewerController viewerContext;
	private JRGraphics2DExporter exporter;
	private boolean pageError;

	private int downX;
	private int downY;

	private boolean pnlTabsChangeListenerEnabled = true;
	
	private List<JRHyperlinkListener> hyperlinkListeners = new ArrayList<>();
	private Map<JPanel, JRPrintHyperlink> linksMap = new HashMap<>();

	/**
	 * the screen resolution.
	 */
	private int screenResolution = REPORT_RESOLUTION;

	/**
	 * the zoom ratio adjusted to the screen resolution.
	 */
	protected float realZoom;

	private MouseListener mouseListener =
		new java.awt.event.MouseAdapter()
		{
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt)
			{
				hyperlinkClicked(evt);
			}
		};

	protected KeyListener keyNavigationListener = new KeyListener()
	{
		@Override
		public void keyTyped(KeyEvent evt)
		{
		}

		@Override
		public void keyPressed(KeyEvent evt)
		{
			keyNavigate(evt);
		}

		@Override
		public void keyReleased(KeyEvent evt)
		{
		}
	};
	
	public JRViewerPanel(JRViewerController viewerContext)
	{
		this.viewerContext = viewerContext;
		this.viewerContext.addListener(this);
		setScreenDetails();
		initComponents();
		addHyperlinkListener(this);
	}

	private void initComponents()
	{
		scrollPane = new javax.swing.JScrollPane();
		scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);

		pnlTabs = new javax.swing.JTabbedPane();
		pnlInScroll = new javax.swing.JPanel();
		pnlPage = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		pnlLinks = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();
		jPanel6 = new javax.swing.JPanel();
		jPanel7 = new javax.swing.JPanel();
		jPanel8 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jPanel9 = new javax.swing.JPanel();
		lblPage = new PageRenderer();

		setMinimumSize(new java.awt.Dimension(450, 150));
		setPreferredSize(new java.awt.Dimension(450, 150));

		setLayout(new java.awt.BorderLayout());
		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				pnlMainComponentResized(evt);
			}
		});

		pnlTabs.addChangeListener(this::pnlTabsStateChanged);
		add(pnlTabs, java.awt.BorderLayout.CENTER);

		scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pnlInScroll.setLayout(new java.awt.GridBagLayout());

		pnlPage.setLayout(new java.awt.BorderLayout());
		pnlPage.setMinimumSize(new java.awt.Dimension(100, 100));
		pnlPage.setPreferredSize(new java.awt.Dimension(100, 100));

		jPanel4.setLayout(new java.awt.GridBagLayout());
		jPanel4.setMinimumSize(new java.awt.Dimension(100, 120));
		jPanel4.setPreferredSize(new java.awt.Dimension(100, 120));

		pnlLinks.setLayout(null);
		pnlLinks.setMinimumSize(new java.awt.Dimension(5, 5));
		pnlLinks.setPreferredSize(new java.awt.Dimension(5, 5));
		pnlLinks.setOpaque(false);
		pnlLinks.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				pnlLinksMousePressed(evt);
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				pnlLinksMouseReleased(evt);
			}
		});
		pnlLinks.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				pnlLinksMouseDragged(evt);
			}
		});

		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		jPanel4.add(pnlLinks, gridBagConstraints);

		jPanel5.setBackground(java.awt.Color.gray);
		jPanel5.setMinimumSize(new java.awt.Dimension(5, 5));
		jPanel5.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
		jPanel4.add(jPanel5, gridBagConstraints);

		jPanel6.setMinimumSize(new java.awt.Dimension(5, 5));
		jPanel6.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		jPanel4.add(jPanel6, gridBagConstraints);

		jPanel7.setBackground(java.awt.Color.gray);
		jPanel7.setMinimumSize(new java.awt.Dimension(5, 5));
		jPanel7.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		jPanel4.add(jPanel7, gridBagConstraints);

		jPanel8.setBackground(java.awt.Color.gray);
		jPanel8.setMinimumSize(new java.awt.Dimension(5, 5));
		jPanel8.setPreferredSize(new java.awt.Dimension(5, 5));
		jLabel1.setText("jLabel1");
		jPanel8.add(jLabel1);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		jPanel4.add(jPanel8, gridBagConstraints);

		jPanel9.setMinimumSize(new java.awt.Dimension(5, 5));
		jPanel9.setPreferredSize(new java.awt.Dimension(5, 5));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		jPanel4.add(jPanel9, gridBagConstraints);

		lblPage.setBackground(java.awt.Color.white);
		lblPage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		lblPage.setOpaque(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		jPanel4.add(lblPage, gridBagConstraints);

		pnlPage.add(jPanel4, java.awt.BorderLayout.CENTER);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		pnlInScroll.add(pnlPage, gridBagConstraints);

		scrollPane.setViewportView(pnlInScroll);
		add(scrollPane, java.awt.BorderLayout.CENTER);
		
	}

	public KeyListener getKeyNavigationListener()
	{
		return keyNavigationListener;
	}
	
	protected void setScreenDetails()
	{
		screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
	}

	public void addHyperlinkListener(JRHyperlinkListener listener)
	{
		hyperlinkListeners.add(listener);
	}

	public void removeHyperlinkListener(JRHyperlinkListener listener)
	{
		hyperlinkListeners.remove(listener);
	}

	public JRHyperlinkListener[] getHyperlinkListeners()
	{
		return hyperlinkListeners.toArray(new JRHyperlinkListener[hyperlinkListeners.size()]);
	}


	@Override
	public void gotoHyperlink(JRPrintHyperlink hyperlink)
	{
		switch (hyperlink.getHyperlinkType())
		{
			case REFERENCE :
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case LOCAL_ANCHOR :
			{
				if (hyperlink.getHyperlinkAnchor() != null)
				{
					Map<String, JRPrintAnchorIndex> anchorIndexes = viewerContext.getJasperPrint().getAnchorIndexes();
					JRPrintAnchorIndex anchorIndex = anchorIndexes.get(hyperlink.getHyperlinkAnchor());
					if (anchorIndex.getPageIndex() != viewerContext.getPageIndex())
					{
						viewerContext.setPageIndex(anchorIndex.getPageIndex());
						viewerContext.refreshPage();
					}
					Container container = pnlInScroll.getParent();
					if (container instanceof JViewport)
					{
						JViewport viewport = (JViewport) container;

						int newX = (int)(anchorIndex.getElementAbsoluteX() * realZoom);
						int newY = (int)(anchorIndex.getElementAbsoluteY() * realZoom);

						int maxX = pnlInScroll.getWidth() - viewport.getWidth();
						int maxY = pnlInScroll.getHeight() - viewport.getHeight();

						if (newX < 0)
						{
							newX = 0;
						}
						if (newX > maxX)
						{
							newX = maxX;
						}
						if (newY < 0)
						{
							newY = 0;
						}
						if (newY > maxY)
						{
							newY = maxY;
						}

						viewport.setViewPosition(new Point(newX, newY));
					}
				}

				break;
			}
			case LOCAL_PAGE :
			{
				int page = viewerContext.getPageIndex() + 1;
				if (hyperlink.getHyperlinkPage() != null)
				{
					page = hyperlink.getHyperlinkPage();
				}

				if (page >= 1 && page <= viewerContext.getJasperPrint().getPages().size() && page != viewerContext.getPageIndex() + 1)
				{
					viewerContext.setPageIndex(page - 1);
					viewerContext.refreshPage();
					Container container = pnlInScroll.getParent();
					if (container instanceof JViewport)
					{
						JViewport viewport = (JViewport) container;
						viewport.setViewPosition(new Point(0, 0));
					}
				}

				break;
			}
			case REMOTE_ANCHOR :
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
					System.out.println("Hyperlink anchor    : " + hyperlink.getHyperlinkAnchor());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case REMOTE_PAGE :
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink reference : " + hyperlink.getHyperlinkReference());
					System.out.println("Hyperlink page      : " + hyperlink.getHyperlinkPage());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case CUSTOM:
			{
				if (isOnlyHyperlinkListener())
				{
					System.out.println("Hyperlink of type " + hyperlink.getLinkType());
					System.out.println("Implement your own JRHyperlinkListener to manage this type of event.");
				}
				break;
			}
			case NONE :
			default :
			{
				break;
			}
		}
	}


	protected boolean isOnlyHyperlinkListener()
	{
		int listenerCount;
		if (hyperlinkListeners == null)
		{
			listenerCount = 0;
		}
		else
		{
			listenerCount = hyperlinkListeners.size();
			if (hyperlinkListeners.contains(this))
			{
				--listenerCount;
			}
		}
		return listenerCount == 0;
	}

	protected void paintPage(Graphics2D grx)
	{
		if (pageError)
		{
			paintPageError(grx);
			return;
		}
		
		try
		{
			if (exporter == null)
			{
				exporter = getGraphics2DExporter();
			}
			else
			{
				exporter.reset();
			}

			exporter.setExporterInput(new SimpleExporterInput(viewerContext.getJasperPrint()));

			SimpleGraphics2DReportConfiguration configuration = new SimpleGraphics2DReportConfiguration();
			configuration.setPageIndex(viewerContext.getPageIndex());
			configuration.setZoomRatio(realZoom);
			configuration.setOffsetX(1); //lblPage border
			configuration.setOffsetY(1);
			exporter.setConfiguration(configuration);

			SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
			Graphics2D g = (Graphics2D)grx.create();
			output.setGraphics2D(g);
			exporter.setExporterOutput(output);

			try
			{
				exporter.exportReport();
			}
			finally
			{
				g.dispose();
			}
		}
		catch(Exception e)
		{
			if (log.isErrorEnabled())
			{
				log.error("Page paint error.", e);
			}

			pageError = true;
			
			paintPageError(grx);
			
			SwingUtilities.invokeLater(
				() -> JOptionPane.showMessageDialog(JRViewerPanel.this, viewerContext.getBundleString("error.displaying"))
				);
		}

	}

	protected JRGraphics2DExporter getGraphics2DExporter() throws JRException
	{
		return 
			new JRGraphics2DExporter(viewerContext.getJasperReportsContext())
			{
				@Override
				protected RenderersCache getRenderersCache()
				{
					return viewerContext.getRenderersCache();
				}
			};
	}

	protected void paintPageError(Graphics2D grx)
	{
		AffineTransform origTransform = grx.getTransform();
		
		AffineTransform transform = new AffineTransform();
		transform.translate(1, 1);
		transform.scale(realZoom, realZoom);
		grx.transform(transform);
		
		try
		{
			drawPageError(grx);
		}
		finally
		{
			grx.setTransform(origTransform);
		}
	}

	protected void drawPageError(Graphics grx)
	{
		PrintPageFormat pageFormat = viewerContext.getPageFormat();
		grx.setColor(Color.white);
		grx.fillRect(0, 0, pageFormat.getPageWidth() + 1, pageFormat.getPageHeight() + 1);
	}

	class PageRenderer extends JLabel
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		private boolean renderImage;

		public PageRenderer()
		{
		}

		@Override
		public void paintComponent(Graphics g)
		{
			if (isRenderImage())
			{
				super.paintComponent(g);
			}
			else
			{
				Graphics2D grx = (Graphics2D)g.create();
				try
				{
					paintPage(grx);
				}
				finally
				{
					grx.dispose();
				}
			}
		}

		public boolean isRenderImage()
		{
			return renderImage;
		}

		public void setRenderImage(boolean renderImage)
		{
			this.renderImage = renderImage;
		}
	}

	void pnlMainComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlMainComponentResized
		// Add your handling code here:
		if (viewerContext.isFitPage())
		{
			viewerContext.fitPage();
		}
		else if (viewerContext.isFitWidth())
		{
			viewerContext.fitWidth();
		}

	}//GEN-LAST:event_pnlMainComponentResized

	protected void fitPage()
	{
		PrintPageFormat pageFormat = viewerContext.getPageFormat();
		float heightRatio = getPageCanvasHeight() / pageFormat.getPageHeight();
		float widthRatio = getPageCanvasWidth() / pageFormat.getPageWidth();
		setRealZoomRatio(heightRatio < widthRatio ? heightRatio : widthRatio);
	}

	protected void fitWidth()
	{
		setRealZoomRatio(getPageCanvasWidth() / viewerContext.getPageFormat().getPageWidth());
	}

	protected float getPageCanvasWidth()
	{
		return (float) pnlInScroll.getVisibleRect().getWidth() - 20f;
	}

	protected float getPageCanvasHeight()
	{
		return (float) pnlInScroll.getVisibleRect().getHeight() - 20f;
	}

	void pnlLinksMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMousePressed
		// Add your handling code here:
		pnlLinks.setCursor(new Cursor(Cursor.MOVE_CURSOR));

		downX = evt.getX();
		downY = evt.getY();
	}//GEN-LAST:event_pnlLinksMousePressed

	void pnlLinksMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseDragged
		// Add your handling code here:

		Container container = pnlInScroll.getParent();
		if (container instanceof JViewport)
		{
			JViewport viewport = (JViewport) container;
			Point point = viewport.getViewPosition();
			int newX = point.x - (evt.getX() - downX);
			int newY = point.y - (evt.getY() - downY);

			int maxX = pnlInScroll.getWidth() - viewport.getWidth();
			int maxY = pnlInScroll.getHeight() - viewport.getHeight();

			if (newX < 0)
			{
				newX = 0;
			}
			if (newX > maxX)
			{
				newX = maxX;
			}
			if (newY < 0)
			{
				newY = 0;
			}
			if (newY > maxY)
			{
				newY = maxY;
			}

			viewport.setViewPosition(new Point(newX, newY));
		}
	}//GEN-LAST:event_pnlLinksMouseDragged

	void pnlLinksMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlLinksMouseReleased
		// Add your handling code here:
		pnlLinks.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}//GEN-LAST:event_pnlLinksMouseReleased

	private void pnlTabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_pnlTabsStateChanged
		if (pnlTabsChangeListenerEnabled)
		{
			((JPanel)pnlTabs.getSelectedComponent()).add(scrollPane);

			Integer pgIdx = 0;

			Integer partIndex = pnlTabs.getSelectedIndex();
			if (partIndex > 0)
			{
				PrintParts parts = viewerContext.getPrintParts();
				
				if (parts != null && parts.hasParts())
				{
					partIndex = parts.startsAtZero() ? partIndex : partIndex - 1;
					pgIdx = parts.getStartPageIndex(partIndex);
				}
			}

			viewerContext.setPageIndex(pgIdx);
			viewerContext.refreshPage();
		}
	}//GEN-LAST:event_pnlTabsStateChanged

	protected void pageChanged()
	{
		if (viewerContext.hasPages())
		{
			pageError = false;
			
			PrintParts parts = viewerContext.getPrintParts();
			if (parts.hasParts())
			{
				Integer pageIndex = viewerContext.getPageIndex();
				Integer partIndex = parts.getPartIndex(pageIndex);
				Integer tabIndex = partIndex - (parts.startsAtZero() ? 1 : 0);
				
				if (tabIndex < pnlTabs.getComponentCount())
				{
					pnlTabsChangeListenerEnabled = false;
					pnlTabs.setSelectedIndex(tabIndex);
					((JPanel)pnlTabs.getSelectedComponent()).add(scrollPane);
					pnlTabsChangeListenerEnabled = true;
				}
			}
		}
	}

	protected void refreshTabs()
	{
		pnlTabsChangeListenerEnabled = false;

		pnlTabs.removeAll();
		removeAll();

		PrintParts parts = viewerContext.getPrintParts();
		if (parts == null || !parts.hasParts())
		{
			add(scrollPane, java.awt.BorderLayout.CENTER);
		}
		else
		{
			if (!parts.startsAtZero())
			{
				JPanel partTab = new JPanel();
				partTab.setLayout(new BorderLayout());
				partTab.setName(viewerContext.getJasperPrint().getName());
				pnlTabs.add(partTab);
			}
			
			for (Iterator<Entry<Integer, PrintPart>> it = parts.partsIterator(); it.hasNext();)
			{
				PrintPart part = it.next().getValue();
				JPanel partTab = new JPanel();
				partTab.setLayout(new BorderLayout());
				partTab.setName(part.getName());
				pnlTabs.add(partTab);
			}
			
			add(pnlTabs, java.awt.BorderLayout.CENTER);
		}

		pnlTabsChangeListenerEnabled = true;
	}

	protected void refreshPage()
	{
		if (!viewerContext.hasPages())
		{
			pnlPage.setVisible(false);
			
			if (viewerContext.getJasperPrint() != null)
			{
				JOptionPane.showMessageDialog(this, viewerContext.getBundleString("no.pages"));
			}

			return;
		}

		pnlPage.setVisible(true);

		PrintPageFormat pageFormat = viewerContext.getPageFormat();
		Dimension dim = new Dimension(
			(int)(pageFormat.getPageWidth() * realZoom) + 8, // 2 from border, 5 from shadow and 1 extra pixel for image
			(int)(pageFormat.getPageHeight() * realZoom) + 8
			);
		pnlPage.setMaximumSize(dim);
		pnlPage.setMinimumSize(dim);
		pnlPage.setPreferredSize(dim);

		long maxImageSize = JRPropertiesUtil.getInstance(viewerContext.getJasperReportsContext()).getLongProperty(JRViewer.VIEWER_RENDER_BUFFER_MAX_SIZE);
		boolean renderImage;
		if (maxImageSize <= 0)
		{
			renderImage = false;
		}
		else
		{
			long imageSize = ((int) (pageFormat.getPageWidth() * realZoom) + 1) * ((int) (pageFormat.getPageHeight() * realZoom) + 1);
			renderImage = imageSize <= maxImageSize;
		}

		lblPage.setRenderImage(renderImage);

		if (renderImage)
		{
			setPageImage();
		}

		pnlLinks.removeAll();
		linksMap = new HashMap<>();

		createHyperlinks();

		if (!renderImage)
		{
			lblPage.setIcon(null);

			validate();
			repaint();
		}
	}

	protected void setPageImage()
	{
		Image image;
		if (pageError)
		{
			image = getPageErrorImage();
		}
		else
		{
			try
			{
				image = JasperPrintManager.getInstance(viewerContext.getJasperReportsContext()).printToImage(viewerContext.getJasperPrint(), viewerContext.getPageIndex(), realZoom);
			}
			catch (Exception e)
			{
				if (log.isErrorEnabled())
				{
					log.error("Print page to image error.", e);
				}

				pageError = true;

				image = getPageErrorImage();
				JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("net/sf/jasperreports/view/viewer").getString("error.displaying"));
			}
		}
		ImageIcon imageIcon = new ImageIcon(image);
		lblPage.setIcon(imageIcon);
	}

	protected Image getPageErrorImage()
	{
		PrintPageFormat pageFormat = viewerContext.getPageFormat();
		Image image = new BufferedImage(
				(int) (pageFormat.getPageWidth() * realZoom) + 1,
				(int) (pageFormat.getPageHeight() * realZoom) + 1,
				BufferedImage.TYPE_INT_RGB
				);
		
		AffineTransform transform = new AffineTransform();
		transform.scale(realZoom, realZoom);

		Graphics2D grx = (Graphics2D) image.getGraphics();
		try
		{
			grx.transform(transform);

			drawPageError(grx);
		}
		finally
		{
			grx.dispose();
		}
		
		return image;
	}

	protected void zoomChanged()
	{
		realZoom = viewerContext.getZoom() * screenResolution / REPORT_RESOLUTION;
	}

	protected void createHyperlinks()
	{
		List<JRPrintPage> pages = viewerContext.getJasperPrint().getPages();
		JRPrintPage page = pages.get(viewerContext.getPageIndex());
		createHyperlinks(page.getElements(), 0, 0);
	}

	protected void createHyperlinks(List<JRPrintElement> elements, int offsetX, int offsetY)
	{
		if (elements != null && elements.size() > 0)
		{
			for (JRPrintElement element : elements)
			{
				AreaHyperlinksRenderable imageMap = null;
				if (element instanceof JRPrintImage)
				{
					Renderable renderer = ((JRPrintImage) element).getRenderer();
					if (renderer instanceof AreaHyperlinksRenderable)
					{
						imageMap = (AreaHyperlinksRenderable) renderer;
						if (!imageMap.hasImageAreaHyperlinks())
						{
							imageMap = null;
						}
					}
				}
				boolean hasImageMap = imageMap != null;

				JRPrintHyperlink hyperlink = null;
				if (element instanceof JRPrintHyperlink)
				{
					hyperlink = (JRPrintHyperlink) element;
				}
				boolean hasHyperlink = !hasImageMap 
					&& hyperlink != null && hyperlink.getHyperlinkType() != HyperlinkTypeEnum.NONE;
				boolean hasTooltip = hyperlink != null && hyperlink.getHyperlinkTooltip() != null;

				if (hasHyperlink || hasImageMap || hasTooltip)
				{
					JPanel link;
					if (hasImageMap)
					{
						Rectangle renderingArea = new Rectangle(0, 0, element.getWidth(), element.getHeight());
						link = new ImageMapPanel(renderingArea, imageMap);
					}
					else //hasImageMap
					{
						link = new JPanel();
						if (hasHyperlink)
						{
							link.addMouseListener(mouseListener);
						}
					}

					if (hasHyperlink)
					{
						link.setCursor(new Cursor(Cursor.HAND_CURSOR));
					}

					link.setLocation(
						(int)((element.getX() + offsetX) * realZoom),
						(int)((element.getY() + offsetY) * realZoom)
						);
					link.setSize(
						(int)(element.getWidth() * realZoom),
						(int)(element.getHeight() * realZoom)
						);
					link.setOpaque(false);

					String toolTip = getHyperlinkTooltip(hyperlink);
					if (toolTip == null && hasImageMap)
					{
						toolTip = "";//not null to register the panel as having a tool tip
					}
					link.setToolTipText(toolTip);

					pnlLinks.add(link);
					linksMap.put(link, hyperlink);
				}

				if (element instanceof JRPrintFrame)
				{
					JRPrintFrame frame = (JRPrintFrame) element;
					int frameOffsetX = offsetX + frame.getX() + frame.getLineBox().getLeftPadding();
					int frameOffsetY = offsetY + frame.getY() + frame.getLineBox().getTopPadding();
					createHyperlinks(frame.getElements(), frameOffsetX, frameOffsetY);
				}
			}
		}
	}


	protected class ImageMapPanel extends JPanel implements MouseListener, MouseMotionListener
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		protected final List<JRPrintImageAreaHyperlink> imageAreaHyperlinks;

		public ImageMapPanel(Rectangle renderingArea, AreaHyperlinksRenderable imageMap)
		{
			try
			{
				imageAreaHyperlinks = imageMap.getImageAreaHyperlinks(renderingArea);//FIXMECHART
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}

			addMouseListener(this);
			addMouseMotionListener(this);
		}

		@Override
		public String getToolTipText(MouseEvent event)
		{
			String tooltip = null;
			JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(event);
			if (imageMapArea != null)
			{
				tooltip = getHyperlinkTooltip(imageMapArea.getHyperlink());
			}

			if (tooltip == null)
			{
				tooltip = super.getToolTipText(event);
			}

			return tooltip;
		}

		@Override
		public void mouseDragged(MouseEvent e)
		{
			pnlLinksMouseDragged(e);
		}

		@Override
		public void mouseMoved(MouseEvent e)
		{
			JRPrintImageAreaHyperlink imageArea = getImageMapArea(e);
			if (imageArea != null
					&& imageArea.getHyperlink().getHyperlinkType() != HyperlinkTypeEnum.NONE)
			{
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			else
			{
				e.getComponent().setCursor(Cursor.getDefaultCursor());
			}
		}

		protected JRPrintImageAreaHyperlink getImageMapArea(MouseEvent e)
		{
			return getImageMapArea((int) (e.getX() / realZoom), (int) (e.getY() / realZoom));
		}

		protected JRPrintImageAreaHyperlink getImageMapArea(int x, int y)
		{
			JRPrintImageAreaHyperlink image = null;
			if (imageAreaHyperlinks != null)
			{
				for (ListIterator<JRPrintImageAreaHyperlink> it = imageAreaHyperlinks.listIterator(imageAreaHyperlinks.size()); image == null && it.hasPrevious();)
				{
					JRPrintImageAreaHyperlink area = it.previous();
					if (area.getArea().containsPoint(x, y))
					{
						image = area;
					}
				}
			}
			return image;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			JRPrintImageAreaHyperlink imageMapArea = getImageMapArea(e);
			if (imageMapArea != null)
			{
				hyperlinkClicked(imageMapArea.getHyperlink());
			}
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			pnlLinksMousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			e.getComponent().setCursor(Cursor.getDefaultCursor());
			pnlLinksMouseReleased(e);
		}
	}


	protected String getHyperlinkTooltip(JRPrintHyperlink hyperlink)
	{
		String toolTip;
		toolTip = hyperlink.getHyperlinkTooltip();
		if (toolTip == null)
		{
			toolTip = getFallbackTooltip(hyperlink);
		}
		return toolTip;
	}


	protected String getFallbackTooltip(JRPrintHyperlink hyperlink)
	{
		String toolTip = null;
		switch (hyperlink.getHyperlinkType())
		{
			case REFERENCE :
			{
				toolTip = hyperlink.getHyperlinkReference();
				break;
			}
			case LOCAL_ANCHOR :
			{
				if (hyperlink.getHyperlinkAnchor() != null)
				{
					toolTip = "#" + hyperlink.getHyperlinkAnchor();
				}
				break;
			}
			case LOCAL_PAGE :
			{
				if (hyperlink.getHyperlinkPage() != null)
				{
					toolTip = "#page " + hyperlink.getHyperlinkPage();
				}
				break;
			}
			case REMOTE_ANCHOR :
			{
				toolTip = "";
				if (hyperlink.getHyperlinkReference() != null)
				{
					toolTip = toolTip + hyperlink.getHyperlinkReference();
				}
				if (hyperlink.getHyperlinkAnchor() != null)
				{
					toolTip = toolTip + "#" + hyperlink.getHyperlinkAnchor();
				}
				break;
			}
			case REMOTE_PAGE :
			{
				toolTip = "";
				if (hyperlink.getHyperlinkReference() != null)
				{
					toolTip = toolTip + hyperlink.getHyperlinkReference();
				}
				if (hyperlink.getHyperlinkPage() != null)
				{
					toolTip = toolTip + "#page " + hyperlink.getHyperlinkPage();
				}
				break;
			}
			default :
			{
				break;
			}
		}
		return toolTip;
	}

	void hyperlinkClicked(MouseEvent evt)
	{
		JPanel link = (JPanel)evt.getSource();
		JRPrintHyperlink element = linksMap.get(link);
		hyperlinkClicked(element);
	}

	protected void hyperlinkClicked(JRPrintHyperlink hyperlink)
	{
		try
		{
			JRHyperlinkListener listener = null;
			for (int i = 0; i < hyperlinkListeners.size(); i++)
			{
				listener = hyperlinkListeners.get(i);
				listener.gotoHyperlink(hyperlink);
			}
		}
		catch(JRException e)
		{
			if (log.isErrorEnabled())
			{
				log.error("Hyperlink click error.", e);
			}

			JOptionPane.showMessageDialog(this, viewerContext.getBundleString("error.hyperlink"));
		}
	}

	protected void setRealZoomRatio(float newZoom)
	{
		if (newZoom > 0 && realZoom != newZoom)
		{
			float zoom = newZoom * REPORT_RESOLUTION / screenResolution;
			viewerContext.setZoomRatio(zoom);
		}
	}

	public void setFitWidthZoomRatio()
	{
		setRealZoomRatio(getPageCanvasWidth() / viewerContext.getPageFormat().getPageWidth());
	}

	public void setFitPageZoomRatio()
	{
		setRealZoomRatio(getPageCanvasHeight() / viewerContext.getPageFormat().getPageHeight());
	}

	protected void keyNavigate(KeyEvent evt)
	{
		boolean refresh = true;
		switch (evt.getKeyCode())
		{
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_PAGE_DOWN:
			dnNavigate(evt);
			break;
		case KeyEvent.VK_UP:
		case KeyEvent.VK_PAGE_UP:
			upNavigate(evt);
			break;
		case KeyEvent.VK_HOME:
			homeEndNavigate(0);
			break;
		case KeyEvent.VK_END:
			homeEndNavigate(viewerContext.getPageCount() - 1);
			break;
		default:
			refresh = false;
		}
		
		if (refresh)
		{
			viewerContext.refreshPage();
		}
	}

	protected void dnNavigate(KeyEvent evt)
	{
		int bottomPosition = scrollPane.getVerticalScrollBar().getValue();
		scrollPane.dispatchEvent(evt);
		if ((scrollPane.getViewport().getHeight() > pnlPage.getHeight() ||
				scrollPane.getVerticalScrollBar().getValue() == bottomPosition) &&
				viewerContext.getPageIndex() < viewerContext.getPageCount() - 1)
		{
			viewerContext.setPageIndex(viewerContext.getPageIndex() + 1);
			if (scrollPane.isEnabled())
			{
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		}
	}

	protected void upNavigate(KeyEvent evt)
	{
		if ((scrollPane.getViewport().getHeight() > pnlPage.getHeight() ||
				scrollPane.getVerticalScrollBar().getValue() == 0) &&
				viewerContext.getPageIndex() > 0)
		{
			viewerContext.setPageIndex(viewerContext.getPageIndex() - 1);
			if (scrollPane.isEnabled())
			{
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			}
		}
		else
		{
			scrollPane.dispatchEvent(evt);
		}
	}

	protected void homeEndNavigate(int pageNumber)
	{
		viewerContext.setPageIndex(pageNumber);
		if (scrollPane.isEnabled())
		{
			scrollPane.getVerticalScrollBar().setValue(0);
		}
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
		case JRViewerEvent.EVENT_REPORT_LOAD_FAILED:
			refreshTabs();
			break;
		default:
		}
	}
}
