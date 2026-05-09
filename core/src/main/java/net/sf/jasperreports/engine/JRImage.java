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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;


/**
 * An abstract representation of a graphic element representing an image. Images can be aligned and scaled. 
 * They can also contain hyperlinks or be anchors for other hyperlinks.
 * <h3>Image Alignment</h3>
 * If the scale type for the image is <code>Clip</code> or <code>RetainShape</code> 
 * (see {@link net.sf.jasperreports.engine.JRCommonImage}) and the actual image is smaller
 * than its defined size in the report template or does not have the same proportions, the
 * image might not occupy all the space allocated to it in the report template. In such cases,
 * one can align the image inside its predefined report space using the <code>hAlign</code> and 
 * <code>vAlign</code> attributes, which specify the alignment of the image on the horizontal 
 * axis (<code>Left</code>, <code>Center</code>, <code>Right</code>) and the vertical axis 
 * (<code>Top</code>, <code>Middle</code>, <code>Bottom</code>). By default, images are
 * aligned at the top and to the left inside their specified bounds.
 * <h3>Caching Images</h3>
 * All image elements have dynamic content. There are no special elements to introduce
 * static images on the reports as there are for static text elements. However, most of the
 * time, the images on a report are in fact static and do not necessarily come from the data
 * source or from parameters. Usually, they are loaded from files on disk and represent
 * logos and other static resources.
 * <p/>
 * To display the same image multiple times on a report (for example, a logo appearing on
 * the page header), you do not need to load the image file each time. Instead, you can
 * cache the image for better performance. When you set the <code>isUsingCache</code> flag 
 * attribute to true, the reporting engine will try to recognize previously loaded images 
 * using their specified source. For example, it will recognize an image if the image source 
 * is a file name that it has already loaded, or if it is the same URL. This attribute can 
 * be accessed using the {@link #getUsingCache()} method.
 * <p/>
 * The caching functionality is available for image elements whose expressions return
 * objects of any type as the image source. The <code>isUsingCache</code> flag is set to true by
 * default for images having <code>java.lang.String</code> expressions and to false for all other
 * types. The key used for the cache is the value of the image source expression; key
 * comparisons are performed using the standard equals method. As a corollary, for images
 * having a <code>java.io.InputStream</code> source with caching enabled, the input stream is read
 * only once, and subsequently the image will be taken from the cache.
 * <p/>
 * The <code>isUsingCache</code> flag should not be set in cases when an image has a dynamic source
 * (for example, the image is loaded from a binary database field for each row) because the
 * images would accumulate in the cache and the report filling would rapidly fail due to an
 * out-of-memory error. Obviously, the flag should also not be set when a single source is
 * used to produce different images (for example, a URL that would return a different
 * image each time it's accessed).
 * <h3>Lazy Loading Images</h3>
 * The <code>isLazy</code> flag attribute (see {@link #isLazy()} method) specifies whether the 
 * image should be loaded and processed during report filling or during exporting. This can be useful 
 * in cases in which the image is loaded from a URL and is not available at report-filling time, but 
 * will be available at report-export or display time. For instance, there might be a logo image that
 * has to be loaded from a public web server to which the machine that fills the reports does
 * not have access. However, if the reports will be rendered in HTML, the image can be
 * loaded by the browser from the specified URL at report-display time. In such cases, the
 * <code>isLazy</code> flag should be set to true (it is false by default) and the image expression
 * should be of type <code>java.util.String</code>, even if the specified image location is actually a
 * URL, a file, or a classpath resource. When lazy loading an image at fill time, the engine
 * will no longer try to load the image from the specified String location but only store
 * that location inside the generated document. The exporter class is responsible for using
 * that String value to access the image at report-export time.
 * <h3>Missing Images Behavior</h3>
 * For various reasons, an image may be unavailable when the engine tries to load it either
 * at report-filling or export time, especially if the image is loaded from some public URL.
 * In this case you may want to customize the way the engine handles missing images
 * during report generation. The <code>onErrorType</code> attribute available for images allows that. It
 * can take the following values:
 * <ul>
 * <li><code>Error</code> - An exception is thrown if the engine cannot load the image. This is the default behavior.</li>
 * <li><code>Blank</code> - Any image-loading exception is ignored and nothing will appear in the
 * generated document</li>
 * <li><code>Icon</code> - If the image does not load successfully, then the engine will put a small icon
 * in the document to indicate that the actual image is missing</li>
 * </ul>
 * <h3>Image Expression</h3>
 * The value returned by the image expression (see {@link #getExpression()}) is the source 
 * for the image to be displayed. The image expression is introduced by the 
 * <code>&lt;imageExpression&gt;</code> element and can return
 * values from only the limited range of classes listed following:
 * <ul>
 * <li><code>java.lang.String</code> (default)</li>
 * <li><code>java.io.File</code></li>
 * <li><code>java.io.InputStream</code></li>
 * <li><code>java.net.URL</code></li>
 * <li><code>java.awt.Image</code></li>
 * <li><code>{@link net.sf.jasperreports.renderers.Renderable}</code></li>
 * </ul>
 * When the image expression returns a <code>java.lang.String</code> value, the engine tries to see whether
 * the value represents a URL from which to load the image. If it is not a valid URL representation, it tries to locate a
 * file on disk and load the image from it, assuming that the value represents a file name. If no file is found, it finally
 * assumes that the string value represents the location of a classpath resource and tries to load the image from
 * there. An exception is thrown only if all these attempts fail.
 * <h3>Evaluating Images</h3>
 * As with text fields, one can postpone evaluating the image expression, which by default
 * is performed immediately. This will allow users to display somewhere in the document
 * images that will be built or chosen later in the report-filling process, due to complex
 * algorithms, for example.
 * <p/>
 * The evaluation attributes <code>evaluationTime</code> and <code>evaluationGroup</code> inherited from the 
 * {@link net.sf.jasperreports.engine.JREvaluation}, are available in the <code>&lt;image&gt;</code> element. 
 * The <code>evaluationTime</code> attribute can take the following values:
 * <ul>
 * <li><code>Now</code> - The image expression is evaluated when the current band is filled.</li>
 * <li><code>Report</code> - The image expression is evaluated when the end of the report is reached.</li>
 * <li><code>Page</code> - The image expression is evaluated when the end of the current page is reached.</li>
 * <li><code>Column</code> - The image expression is evaluated when the end of the current column is reached.</li>
 * <li><code>Group</code> - The image expression is evaluated when the group specified by the 
 * <code>evaluationGroup</code> attribute changes</li>
 * <li><code>Auto</code> - Each variable participating in the image expression is evaluated at
 * a time corresponding to its reset type. Fields are evaluated <code>Now</code></li>
 * </ul>
 * The default value for this attribute is <code>Now</code>.
 * 
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	"uuid",
	"key",
	"x",
	"y",
	"width",
	"height",
	"forecolor",
	"backcolor",
	"mode",
	"positionType",
	"stretchType",
	"printRepeatedValues",
	"printInFirstWholeBand",
	"printWhenDetailOverflows",
	"printWhenGroupChanges",
	"removeLineWhenBlank",
	"fill",
	"scaleImage",
	"rotation",
	"hImageAlign",
	"vImageAlign",
	"evaluationTime",
	"evaluationGroup",
	"linkType",
	"linkTarget",
	"usingCache",
	"lazy",
	"onErrorType",
	"bookmarkLevel",
	"style",
	"printWhenExpression",
	"styleExpression",
	"property",
	"propertyExpression",
	"box",
	"pen",
	"expression",
	"anchorNameExpression",
	"bookmarkLevelExpression",
	"hyperlinkReferenceExpression",
	"hyperlinkWhenExpression",
	"hyperlinkAnchorExpression",
	"hyperlinkPageExpression",
	"hyperlinkTooltipExpression",
	"hyperlinkParameter"
	})
@JsonTypeName("image")
@JsonDeserialize(as = JRDesignImage.class)
public interface JRImage extends JRGraphicElement, JREvaluation, JRAnchor, JRHyperlink, JRCommonImage
{


	/**
	 * Indicates if the engine is loading the current image from cache.
	 * Implementations of this method return the actual value for the internal flag that was explicitly 
	 * set on this image element.
	 * @return Boolean.TRUE if the image should be loaded from cache, Boolean.FALSE otherwise 
	 * or null in case the flag was never explicitly set on this image element
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getUsingCache();

	/**
	 * Specifies if the engine should be loading the current image from cache. If set to Boolean.TRUE, the reporting engine
	 * will try to recognize previously loaded images using their specified source. For example, it will recognize
	 * an image if the image source is a file name that it has already loaded, or if it is the same URL.
	 * <p>
	 * If set to null, the engine will rely on some default value which depends on the type of the image expression.
	 * The cache is turned on by default only for images that have <tt>java.lang.String</tt> objects in their expressions.
	 */
	public void setUsingCache(Boolean isUsingCache);

	/**
	 * Indicates if the images will be loaded lazily or not.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isLazy();

	/**
	 * Gives control over when the images are retrieved from their specified location. If set to true, the image is
	 * loaded from the specified location only when the document is viewed or exported to other formats. Otherwise
	 * it is loaded during the report filling process and stored in the resulting document.
	 * @param isLazy specifies whether
	 */
	public void setLazy(boolean isLazy);

	/**
	 * Indicates how the engine will treat a missing image.
	 * @return a value representing one of the missing image handling constants in {@link OnErrorTypeEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public OnErrorTypeEnum getOnErrorType();
	
	/**
	 * Specifies how the engine should treat a missing image.
	 * @param onErrorTypeEnum a value representing one of the missing image handling constants in {@link OnErrorTypeEnum}
	 */
	public void setOnErrorType(OnErrorTypeEnum onErrorTypeEnum);
	
	/**
	 * 
	 */
	public JRExpression getExpression();

}
