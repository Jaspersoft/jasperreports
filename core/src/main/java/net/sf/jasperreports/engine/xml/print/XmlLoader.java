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
package net.sf.jasperreports.engine.xml.print;

import java.awt.Color;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.NamedEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class XmlLoader
{
	
	private static final Log log = LogFactory.getLog(XmlLoader.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_ELEMENT = "xml.print.unexpected.element";
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_START_ELEMENT = "xml.print.unexpected.start.element";
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_END_ELEMENT = "xml.print.unexpected.end.element";
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_EVENT_TYPE = "xml.print.unexpected.event.type";
	
	private XMLStreamReader reader;
	private int currentElementLevel;
	
	public void open(InputStream is) throws JRException
	{
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
		inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		try
		{
			reader = inputFactory.createXMLStreamReader(is);
		}
		catch (XMLStreamException e)
		{
			throw new JRRuntimeException(e);
		}
		
		currentElementLevel = 0;
	}
	
	protected void loadElements(Consumer<String> elementConsumer)
	{
		try
		{
			int startElementLevel = currentElementLevel;
			boolean ended = false;
			while (!ended && reader.hasNext())
			{
				reader.next();
				switch (reader.getEventType())
				{
				case XMLEvent.START_ELEMENT:
					if (startElementLevel != currentElementLevel)
					{
						throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_UNEXPECTED_START_ELEMENT, 
								new Object[] {reader.getLocalName()});
					}
					++currentElementLevel;
					if (log.isTraceEnabled())
					{
						log.trace("start " + reader.getLocalName() + " level " + currentElementLevel);
					}
					
					elementConsumer.accept(reader.getLocalName());
					break;
				case XMLEvent.END_ELEMENT:
					if (log.isTraceEnabled())
					{
						log.trace("end " + reader.getLocalName() + " level " + currentElementLevel);
					}
					if (startElementLevel != currentElementLevel)
					{
						throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_UNEXPECTED_END_ELEMENT, 
								new Object[] {reader.getLocalName()});
					}
					--currentElementLevel;
					
					ended = true;
					break;
				case XMLEvent.CHARACTERS:
				case XMLEvent.END_DOCUMENT:
					//nothing to do
					break;
				default:
					throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_UNEXPECTED_EVENT_TYPE, 
							new Object[] {reader.getEventType()});
				}
			}
		}
		catch (XMLStreamException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	protected void endElement()
	{
		loadElements(this::unexpectedElement);
	}
	
	protected String loadText(boolean trim)
	{
		try
		{
			String text = reader.getElementText();
			if (log.isTraceEnabled())
			{
				log.trace("loaded text from " + reader.getLocalName() + " level " + currentElementLevel);
			}
			--currentElementLevel;

			return text == null ? null : (trim ? text.trim() : text);
		}
		catch (XMLStreamException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	protected void setAttribute(String name, Consumer<String> setter)
	{
		String value = getAttribute(name);
		if (value != null)
		{
			setter.accept(value);
		}
	}

	protected String getAttribute(String name)
	{
		return reader.getAttributeValue(null, name);
	}

	protected <T> T getAttribute(String name, Function<String, T> transform)
	{
		String stringValue = getAttribute(name);
		return (stringValue != null && !stringValue.isEmpty()) ? transform.apply(stringValue) : null;
	}

	protected <T> void setAttribute(String name, Function<String, T> transform, Consumer<T> setter)
	{
		T value = getAttribute(name, transform);
		if (value != null)
		{
			setter.accept(value);
		}
	}
	
	protected Boolean getBooleanAttribute(String name)
	{
		return getAttribute(name, Boolean::valueOf);
	}
	
	protected void setBooleanAttribute(String name, Consumer<Boolean> setter)
	{
		setAttribute(name, Boolean::valueOf, setter);
	}
	
	protected Integer getIntAttribute(String name)
	{
		return getAttribute(name, Integer::parseInt);
	}
	
	protected void setIntAttribute(String name, Consumer<Integer> setter)
	{
		setAttribute(name, Integer::parseInt, setter);
	}
	
	protected Float getFloatAttribute(String name)
	{
		return getAttribute(name, Float::valueOf);
	}
	
	protected void setFloatAttribute(String name, Consumer<Float> setter)
	{
		setAttribute(name, Float::valueOf, setter);
	}
	
	protected <E extends NamedEnum> E getEnumAttribute(String name, Function<String, E> getByName)
	{
		return getAttribute(name, getByName::apply);
	}
	
	protected <E extends NamedEnum> void setEnumAttribute(String name, Function<String, E> getByName, Consumer<E> setter)
	{
		setAttribute(name, getByName::apply, setter);
	}
	
	protected void setColorAttribute(String name, Consumer<Color> setter)
	{
		setAttribute(name, color -> JRColorUtil.getColor(color, null), setter);
	}
	
	protected void unexpectedElement(String element)
	{
		throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_UNEXPECTED_ELEMENT, new Object[] {element});
	}
}
