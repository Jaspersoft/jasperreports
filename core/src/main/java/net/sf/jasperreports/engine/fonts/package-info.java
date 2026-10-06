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
/**
 * Provides support for font extensions. 
 * <h3>The Font Extension Point</h3>
 * Among the various extension points that JasperReports exposes, this one 
 * allows users to make use of fonts that are not available to the JVM at runtime in normal 
 * circumstances. 
 * <p/>
 * Usually, in order to make a font file available to the JVM, one would need to install that 
 * font file into the system, or make some configuration changes in the local JVM. But if 
 * the application ends up being deployed on some other machine, the same font file 
 * installation process has to be performed on that machine as well.
 * <p/> 
 * When designing reports, we are immediately faced with the selection of fonts to be used 
 * by the various text fields present in the report template. Using fonts that are available 
 * only on the local JVM where we design and test our reports poses the risk of those fonts 
 * not being available on the system where the reports are actually generated at runtime. 
 * <p/> 
 * When specified fonts are missing on a system, the JVM uses some default fonts as 
 * on-the-fly replacements and these might have totally different properties and font metrics, 
 * producing various side effects, including but not limited to chunks of text being cut from 
 * the generated output. 
 * <p/> 
 * This is why it is so important for an application to control and make sure that the fonts used 
 * in its reports are available at runtime on the machines on which the application is 
 * running. 
 * <p/> 
 * The best way to control fonts in JasperReports is to provide the font files as extensions to 
 * the library. Basically, this involves putting various Font files (such as True Type, Open Type, 
 * SVG or Web Open Font Format) in a JAR file, together with an XML file that describes the content 
 * of the JAR and the various relationships between the fonts and the locales. 
 * <p/> 
 * JasperReports raises a {@link net.sf.jasperreports.engine.util.JRFontNotFoundException} in the 
 * case where the font used inside a report template is not available to the JVM as either as a 
 * system font or a font coming from a JR font extension. This ensure that all problems caused by 
 * font metrics mismatches are avoided and we have an early warning about the inconsistency. 
 * <p/> 
 * However, for backward compatibility reasons, this font runtime validation can be turned 
 * off using the {@link net.sf.jasperreports.engine.util.JRStyledText#PROPERTY_AWT_IGNORE_MISSING_FONT net.sf.jasperreports.awt.ignore.missing.font} configuration 
 * property, which can be employed either globally or at report level. 
 * <p/> 
 * The font extension point in JasperReports is exposed as the 
 * {@link net.sf.jasperreports.engine.fonts.FontFamily} public interface. Each font 
 * family is made out of 4 font faces: <code>normal</code>, <code>bold</code>, <code>italic</code> and 
 * <code>bolditalic</code>. None of them is required in a font family, but when they are all missing, 
 * then the name of the font family should be the name of an existing font face, already installed on 
 * the machine. Font faces are described by the {@link net.sf.jasperreports.engine.fonts.FontFace} interface. 
 * Besides the font faces, a font family also supports certain locales and tells if it is able to simulate 
 * the bold style and the italic style in PDF export, in case the corresponding font faces are missing from 
 * its declaration. 
 * <p/> 
 * JasperReports is shipped with convenience implementations for the above mentioned 
 * interfaces. The {@link net.sf.jasperreports.engine.fonts.SimpleFontFamily} is the default 
 * implementation of the {@link net.sf.jasperreports.engine.fonts.FontFamily} interface. This 
 * works with the {@link net.sf.jasperreports.engine.fonts.SimpleFontFace} default implementation 
 * of the font face.
 * <p/> 
 * The best way to deploy font files as extensions is to rely on the font extension 
 * registry factory shipped with JasperReports and make use of the default font interfaces. 
 * <h3>Simple Font Extension Example</h3>
 * The samples shipped with the JasperReports project distribution package under the
 * <code>/demo/samples</code> folder make use of two font families called <code>DejaVu Sans</code> and 
 * <code>DejaVu Serif</code>. These are open source fonts from the 
 * <a href="http://dejavu-fonts.org">http://dejavu-fonts.org</a> project and
 * are made available to the samples as a font extension.
 * <p/>
 * This font extension is found in the <code>/ext/fonts</code> folder of the JasperReports project
 * distribution package and consists of the following files:
 * <p/>
 * <code>jasperreports_extension.properties</code>
 * <p/>
 * This file is required by the JasperReports extension mechanism and describes the content
 * of any given extension. The first line in this particular properties file specifies that the 
 * built-in font extension registry factory is used by the current extension:
 * <pre>
 * net.sf.jasperreports.extension.registry.factory.simple.font.families=net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory
 * net.sf.jasperreports.extension.simple.font.families.dejavu=net/sf/jasperreports/fonts/fonts.xml
 * </pre>
 * The second line gives the name of the XML file required by this font
 * extension factory, containing the font family and font set definitions. 
 * <p/>
 * This XML file contains the elements that are going to be loaded by the font
 * extension registry factory. As mentioned earlier, the font extension point in
 * JasperReports is expecting font families, so the elements in our XML file are
 * converted into instances of the convenience
 * {@link net.sf.jasperreports.engine.fonts.SimpleFontFamily} implementation of the
 * {@link net.sf.jasperreports.engine.fonts.FontFamily} interface and introduce two 
 * font families: the <code>DejaVu Sans</code> and the <code>DejaVu Serif</code>.
 * <pre>
 * &lt;fontFamily name="DejaVu Sans"&gt;
 *   &lt;normal&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans.ttf&lt;/normal&gt;
 *   &lt;bold&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans-Bold.ttf&lt;/bold&gt;
 *   &lt;italic&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans-Oblique.ttf&lt;/italic&gt;
 *   &lt;boldItalic&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans-BoldOblique.ttf&lt;/boldItalic&gt;
 *   &lt;pdfEncoding&gt;Identity-H&lt;/pdfEncoding&gt;
 *   &lt;pdfEmbedded&gt;true&lt;/pdfEmbedded&gt;
 *   &lt;exportFonts&gt;
 *     &lt;export key="net.sf.jasperreports.html"&gt;'DejaVu Sans', Arial, Helvetica, sans-serif&lt;/export&gt;
 *   &lt;/exportFonts&gt;
 * &lt;/fontFamily&gt;
 * 
 * &lt;fontFamily name="DejaVu Serif"&gt;
 *   &lt;normal&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSerif.ttf&lt;/normal&gt;
 *   &lt;bold&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSerif-Bold.ttf&lt;/bold&gt;
 *   &lt;italic&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSerif-Italic.ttf&lt;/italic&gt;
 *   &lt;boldItalic&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSerif-BoldItalic.ttf&lt;/boldItalic&gt;
 *   &lt;pdfEncoding&gt;Identity-H&lt;/pdfEncoding&gt;
 *   &lt;pdfEmbedded&gt;true&lt;/pdfEmbedded&gt;
 *   &lt;exportFonts&gt;
 *     &lt;export key="net.sf.jasperreports.html"&gt;'DejaVu Serif', 'Times New Roman', Times, serif&lt;/export&gt;
 *   &lt;/exportFonts&gt;
 * &lt;/fontFamily&gt;
 * </pre>
 * Notice how font families are specifying a name and their different faces. The name and
 * the normal face are both required, while the other properties are optional.
 * <p/>
 * The name of the family is required because this is the value that gets used as the
 * <code>fontName</code> attribute of a text element or a style in the report template. The <code>fontName</code>
 * together with the <code>isBold</code> and <code>isItalic</code> attributes of the text field or style in the report
 * help to locate and load the appropriate font face from the family. In case a particular font
 * face is not present or declared in the family whose name was used, then the normal font
 * face is used instead and this makes the normal face mandatory in a font family.
 * <p/>
 * Font files usually support only some languages and this is why the font families can
 * specify the list of locales that they support. In the example above, no locale was
 * specified, meaning that the <code>DejaVu Sans</code> and the <code>DejaVu Serif</code> families would apply
 * to all locales (which is not true, but the JasperReports samples that use this font
 * extension do not make use of languages that are not supported by this font families, so
 * we did not bother to filter on supported locales).
 * <p/>
 * However, locale support in font families is extremely important in cases where font
 * families having the same name are made of different TTF files supporting different
 * locales.
 * <p/>
 * A very good example is of people using the same <code>fontName</code> value inside their reports
 * that sometimes are generated in Chinese and other times in Japanese. They would use a
 * set of TTF files that support Chinese and another set of TTF files that support Japanese.
 * With them, they would create two font families having the same <code>fontName</code> name, and
 * specify Chinese as supported locale for the first one and Japanese for the second. This
 * way, depending on the runtime locale, the appropriate font family would be selected,
 * with the appropriate TTF files for that locale, although at design time the <code>fontName</code>
 * attribute was the same for all text fields, regardless of locale.
 * <p/>
 * Below is the extract from a <code>fonts.xml</code> file that would declare a <code>DejaVu Serif</code> font
 * family supporting only English and German:
 * <pre>
 * &lt;fontFamily name="DejaVu Sans"&gt;
 *   &lt;normal&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans.ttf&lt;/normal&gt;
 *   &lt;bold&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans-Bold.ttf&lt;/bold&gt;
 *   &lt;italic&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans-Oblique.ttf&lt;/italic&gt;
 *   &lt;boldItalic&gt;net/sf/jasperreports/fonts/dejavu/DejaVuSans-BoldOblique.ttf&lt;/boldItalic&gt;
 *   &lt;pdfEncoding&gt;Identity-H&lt;/pdfEncoding&gt;
 *   &lt;pdfEmbedded&gt;true&lt;/pdfEmbedded&gt;
 *   &lt;exportFonts&gt;
 *     &lt;export key="net.sf.jasperreports.html"&gt;'DejaVu Sans', Arial, Helvetica, sans-serif&lt;/export&gt;
 *   &lt;/exportFonts&gt;
 *   &lt;!--
 *   &lt;locales&gt;
 *     &lt;locale&gt;en_US&lt;/locale&gt;
 *     &lt;locale&gt;de_DE&lt;/locale&gt;
 *   &lt;/locales&gt;
 *   --&gt;
 * &lt;/fontFamily&gt;
 * </pre>
 * For more details about deploying fonts as extensions, you can take a look at the
 * <code>/demo/samples/fonts</code> sample provided with the JasperReports project distribution
 * package, which adds one more font extension for another open source font called
 * <code>Lobster Two</code>.
 * 
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 * 
 * @see net.sf.jasperreports.engine.util.JRFontNotFoundException
 */
package net.sf.jasperreports.engine.fonts;
