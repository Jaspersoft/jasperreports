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
package net.sf.jasperreports.engine.util;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.DocumentParser;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * HTML editor kit that can safely parse HTML concurrently.
 * <p>
 * The default Swing HTML parser shares a single DTD instance JVM wide, and the DTD content models
 * lazily cache element validity in unsynchronized arrays (<code>ContentModel.first()</code>).
 * Concurrent parsing can corrupt the cache, which results in tags such as &lt;b&gt; being dropped
 * for the life of the JVM.
 * This kit parses using private DTD copies, each used by a single parse at a time.
 * If private DTD copies cannot be created, it falls back to serializing parsing with the shared DTD.
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
class PooledDtdHtmlEditorKit extends HTMLEditorKit
{
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(PooledDtdHtmlEditorKit.class);

	private static final String DTD_NAME = "html32";
	private static final int MAX_IDLE_DTDS = Runtime.getRuntime().availableProcessors();

	private static final ConcurrentLinkedQueue<DTD> idleDtds = new ConcurrentLinkedQueue<>();
	private static final AtomicInteger idleDtdCount = new AtomicInteger();
	private static volatile boolean privateDtdFailed;

	private static final Object SHARED_PARSER_LOCK = new Object();
	private static final Parser sharedParser = new ParserDelegator();

	static
	{
		// the default style sheet is published before its rules are loaded,
		// making sure that it's fully loaded before concurrent use
		new HTMLEditorKit().getStyleSheet();
	}

	private static final Parser PARSER = new Parser()
	{
		@Override
		public void parse(Reader reader, ParserCallback callback, boolean ignoreCharSet) throws IOException
		{
			DTD dtd = acquireDtd();
			if (dtd == null)
			{
				synchronized (SHARED_PARSER_LOCK)
				{
					sharedParser.parse(reader, callback, ignoreCharSet);
				}
				return;
			}

			try
			{
				new DocumentParser(dtd).parse(reader, callback, ignoreCharSet);
			}
			finally
			{
				releaseDtd(dtd);
			}
		}
	};

	@Override
	protected Parser getParser()
	{
		return PARSER;
	}

	private static DTD acquireDtd()
	{
		DTD dtd = idleDtds.poll();
		if (dtd != null)
		{
			idleDtdCount.decrementAndGet();
			return dtd;
		}

		return privateDtdFailed ? null : PrivateDtdLoader.loadDtd();
	}

	private static void releaseDtd(DTD dtd)
	{
		// only keep a limited number of idle DTDs, the rest are discarded
		if (idleDtdCount.incrementAndGet() <= MAX_IDLE_DTDS)
		{
			idleDtds.offer(dtd);
		}
		else
		{
			idleDtdCount.decrementAndGet();
		}
	}

	private static class PrivateDTD extends DTD
	{
		PrivateDTD()
		{
			super(DTD_NAME);
		}
	}

	// extending ParserDelegator only to get access to the protected createDTD()
	private static class PrivateDtdLoader extends ParserDelegator
	{
		private static final long serialVersionUID = 1L;

		// element count of the shared DTD at first load, guarded by ParserDelegator.class.
		// captured once because DTD.getElement(String) adds missing elements, so the shared DTD can grow.
		private static int expectedElementCount = -1;

		static DTD loadDtd()
		{
			// ParserDelegator.createDTD() is the only way to read the DTD resource (encapsulated in java.desktop),
			// but it also registers the DTD under the global name, so we need to restore the shared DTD registration.
			// synchronizing on the same lock as ParserDelegator uses while creating the default DTD.
			synchronized (ParserDelegator.class)
			{
				try
				{
					new ParserDelegator();// makes sure the default DTD is loaded and registered
					DTD sharedDtd = DTD.getDTD(DTD_NAME);
					if (expectedElementCount < 0)
					{
						expectedElementCount = sharedDtd.elementHash.size();
					}

					DTD dtd;
					try
					{
						dtd = createDTD(new PrivateDTD(), DTD_NAME);
					}
					finally
					{
						DTD.putDTDHash(DTD_NAME, sharedDtd);
					}

					// createDTD() does not throw exceptions on failure, checking that the DTD was loaded
					if (dtd.elementHash.size() < expectedElementCount)
					{
						privateDtdFailed("incomplete DTD loaded", null);
						return null;
					}

					return dtd;
				}
				catch (IOException | RuntimeException e)
				{
					privateDtdFailed("error loading DTD", e);
					return null;
				}
			}
		}

		private static void privateDtdFailed(String message, Exception e)
		{
			if (!privateDtdFailed)
			{
				privateDtdFailed = true;
				log.warn("Failed to create private HTML parser DTD (" + message
						+ "), falling back to serialized parsing with the shared DTD", e);
			}
		}
	}
}
