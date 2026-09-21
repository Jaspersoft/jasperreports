/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2006 Works, Inc. All rights reserved.
 * http://www.works.com
 * Copyright (C) 2006 - 2025 Cloud Software Group, Inc. All rights reserved.
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
 * Licensed to Jaspersoft Corporation under a Contributer Agreement
 */
package net.sf.jasperreports.engine.fill;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVirtualizable;

/**
 * GZips the pages that it doesn't need, but keeps them in memory.
 * 
 * @author John Bindel
 */
public class JRGzipVirtualizer extends JRAbstractLRUVirtualizer
{
	private static final String EXCEPTION_MESSAGE_KEY_NO_DATA_FOUND = "fill.virtualizer.no.data.found";
	
	private final Map<String,byte[]> zippedData;

	/**
	 * @param maxSize
	 *            the maximum size (in JRVirtualizable objects) of the paged in
	 *            cache.
	 */
	public JRGzipVirtualizer(int maxSize) {
		super(maxSize);
		this.zippedData = Collections.synchronizedMap(new HashMap<String,byte[]>());
	}

	@Override
	protected void dispose(String virtualId) {
		zippedData.remove(virtualId);
	}

	@Override
	protected void pageOut(JRVirtualizable o) throws IOException {
		if (!zippedData.containsKey(o.getUID())) {
			GZIPOutputStream gos = null;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
				gos = new GZIPOutputStream(baos);
				writeData(o, gos);
				gos.finish();
				gos.flush();

				byte[] data = baos.toByteArray();
				zippedData.put(o.getUID(), data);
			}
			finally {
				if (gos != null) {
					gos.close();
				}
			}
		}
		else {
			if (!isReadOnly(o)) {
				throw new IllegalStateException(
						"Cannot virtualize data because the data for object UID \"" + o.getUID()
						+ "\" already exists.");
			}
		}
	}

	@Override
	protected void pageIn(JRVirtualizable o) throws IOException {
		GZIPInputStream gis = null;
		try {
			byte[] data = zippedData.get(o.getUID());
			if (data == null) {
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_NO_DATA_FOUND,
						new Object[]{o.getUID()});
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			gis = new GZIPInputStream(bais);
			readData(o, gis);
		}
		finally {
			if (gis != null) {
				gis.close();
			}
		}

		if (!isReadOnly(o)) {
			// Wait until we know it worked before tossing the data.
			zippedData.remove(o.getUID());
		}
	}

	@Override
	public void cleanup()
	{
		zippedData.clear();
		reset();
	}
}
