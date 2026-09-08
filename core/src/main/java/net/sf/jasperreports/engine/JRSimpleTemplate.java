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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;

/**
 * Default {@link JRTemplate} implementation.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRSimpleTemplate implements JRTemplate, Serializable, JRChangeEventsSupport {

	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_TEMPLATE_STYLE = "engine.template.duplicate.template.style";

	public static final String PROPERTY_STYLE = "style";
	public static final String PROPERTY_INCLUDED_TEMPLATES = "incluldedTemplates";

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private int dpi = JasperPrint.DEFAULT_REPORT_DPI;
	private final List<JRTemplateReference> includedTemplates = new ArrayList<>();
	private final List<JRStyle> styles = new ArrayList<>();
	private transient JRPropertyChangeSupport eventSupport;//FIXMECLONE

	@Override
	public int getDpi()
	{
		return dpi;
	}

	public void setDpi(int dpi)
	{
		this.dpi = dpi;
	}

	@Override
	public JRPropertyChangeSupport getEventSupport() {
		synchronized (this) {
			if (eventSupport == null) {
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}

		return eventSupport;
	}

	/**
	 * Adds a style to the template.
	 * 
	 * @param style
	 *          the style to add
	 * @throws JRException
	 *           when a style with the same name already exists
	 */
	public void addStyle(JRStyle style) throws JRException {
		addStyle(-1, style);
	}

	/**
	 * Adds a style to the template.
	 * 
	 * @param style
	 *          the style to add
	 * @throws JRException
	 *           when a style with the same name already exists
	 */
	public void addStyle(int index, JRStyle style) throws JRException {
		checkExistingName(style.getName());

		if (index >= 0 && index < styles.size())
			styles.add(index, style);
		else {
			styles.add(style);
			index = styles.size() - 1;
		}
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_STYLE, style, index);
	}

	protected void checkExistingName(String name) throws JRException {
		if (getStyle(name) != null) {
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_TEMPLATE_STYLE,
					new Object[]{name});
		}
	}

	protected boolean nameMatches(JRStyle style, String name) {
		String styleName = style.getName();
		return name == null ? styleName == null : name.equals(styleName);
	}

	/**
	 * Returns an included style by name.
	 * 
	 * @param name
	 *          the name of the style to be returned
	 * @return the style having the specified name, or <code>null</code> if not found
	 */
	public JRStyle getStyle(String name) {
		JRStyle style = null;
		for (JRStyle itStyle : styles) {
			if (nameMatches(itStyle, name)) {
				style = itStyle;
				break;
			}
		}
		return style;
	}

	/**
	 * Removes an included style.
	 * 
	 * @param style
	 *          the style to remove
	 * @return <code>true</code> if and only if the style has been found and removed
	 */
	public boolean removeStyle(JRStyle style) {
		int idx = styles.indexOf(style);
		if (idx >= 0) {
			styles.remove(idx);

			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_STYLE, style, idx);
			return true;
		}
		return false;
	}

	/**
	 * Removes an included style.
	 * 
	 * @param name
	 *          the name of the style to be removed
	 * @return the removed style, or <code>null</code> if not found
	 */
	public JRStyle removeStyle(String name) {
		JRStyle removed = null;
		for (ListIterator<JRStyle> it = styles.listIterator(); it.hasNext();) {
			JRStyle style = it.next();
			if (nameMatches(style, name)) {
				removed = style;
				break;
			}
		}
		removeStyle(removed);
		return removed;
	}

	@JsonIgnore
	public List<JRStyle> getStylesList() {
		return styles;
	}

	@JsonSetter
	private void setStyles(List<JRStyle> styles) throws JRException
	{
		if (styles != null)
		{
			for (JRStyle style : styles)
			{
				JRConditionalStyle[] condStyles = style.getConditionalStyles();
				if (condStyles != null)
				{
					for (JRConditionalStyle condStyle : condStyles)
					{
						JRDesignExpression expression = (JRDesignExpression)condStyle.getConditionExpression();
						if (expression != null)
						{
							expression.setInterpreted();
						}
					}
				}
				
				addStyle(style);
			}
		}
	}

	@Override
	public JRStyle[] getStyles() {
		return styles.toArray(new JRStyle[styles.size()]);
	}

	/**
	 * Adds an included template.
	 * 
	 * @param reference
	 *          the template reference
	 * @see #getIncludedTemplates()
	 */
	public void addIncludedTemplate(int index, JRTemplateReference reference) {
		if (index >= 0 && index < includedTemplates.size())
			includedTemplates.add(index, reference);
		else {
			includedTemplates.add(reference);
			index = includedTemplates.size() - 1;
		}
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_INCLUDED_TEMPLATES, reference, index);
	}

	/**
	 * Adds an included template.
	 * 
	 * @param reference
	 *          the template reference
	 * @see #getIncludedTemplates()
	 */
	public void addIncludedTemplate(JRTemplateReference reference) {
		addIncludedTemplate(-1, reference);
	}

	/**
	 * Adds an included template.
	 * 
	 * @param templateLocation
	 *          the template location
	 * @see #getIncludedTemplates()
	 */
	public void addIncludedTemplate(String templateLocation) {
		addIncludedTemplate(new JRTemplateReference(templateLocation));
	}

	/**
	 * Removes an included template.
	 * 
	 * @param reference
	 *          the template reference to remove
	 * @return <code>true</code> if and only if the included template has been found and removed
	 */
	public boolean removeIncludedTemplate(JRTemplateReference reference) {
		int idx = includedTemplates.indexOf(reference);
		if (idx >= 0) {
			includedTemplates.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_INCLUDED_TEMPLATES, reference, idx);
			return true;
		}
		return false;
	}

	/**
	 * Removes an included template.
	 * <p/>
	 * The first template reference that matches the location is removed.
	 * 
	 * @param location
	 *          the location of the template to remove
	 * @return the removed template reference, or <code>null</code> if not found
	 */
	public JRTemplateReference removeIncludedTemplate(String location) {
		JRTemplateReference removed = null;
		for (ListIterator<JRTemplateReference> it = includedTemplates.listIterator(); it.hasNext();) {
			JRTemplateReference ref = it.next();
			if (ref.getLocation().equals(location)) {
				removed = ref;
			}
		}
		removeIncludedTemplate(removed);
		return removed;
	}

	@Override
	public JRTemplateReference[] getIncludedTemplates() {
		return includedTemplates.toArray(new JRTemplateReference[includedTemplates.size()]);
	}

	@JsonIgnore
	public List<JRTemplateReference> getIncludedTemplatesList() {
		return includedTemplates;
	}

	/*
	 * JACKSON-TIP
	 * This is the setter for "templates" (json) and "template" (xml) fields,
	 * but the matching is done by Jackson from the annotations of the getter with the similar name in the interface;
	 * Without such method name matching, Jackson would instead fall back to using the includedTemplates List field directly.
	 * And if that one would have a different name or would be marked with JsonIgnore, Jackons would not be able to deserialize
	 * these templates because the getter returns an array and Jackons cannot add to the arrays returned by getters.
	 * It can add to List objects returned by getters, such is the case with cellsList in JRCrosstab.
	 * A similar case with this one here is with the tabStops field in the JRParagraph interface.
	 */
	@JsonSetter
	private void setIncludedTemplates(List<JRTemplateReference> templates)
	{
		if (templates != null)
		{
			for (JRTemplateReference template : templates)
			{
				addIncludedTemplate(template);
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		if (dpi == 0)
		{
			dpi = JasperPrint.DEFAULT_REPORT_DPI;
		}
	}

}
