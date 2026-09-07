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
package net.sf.jasperreports.engine.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.properties.PropertyConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FileBufferedOutputStream extends OutputStream 
{
	
	private static final Log log = LogFactory.getLog(FileBufferedOutputStream.class);
	public static final String EXCEPTION_MESSAGE_KEY_OUTPUT_STREAM_ALREADY_CLOSED = "util.file.buffered.output.stream.already.closed";
	
	/**
	 * Specifies the maximum in-memory buffer length that triggers the creation of a temporary file on disk to store further content sent to this output stream.  
	 */
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			defaultValue = "262144",
			scopes = {PropertyScope.GLOBAL},
			sinceVersion = PropertyConstants.VERSION_1_3_4,
			valueType = Integer.class
			)
	public static final String PROPERTY_MEMORY_THRESHOLD = JRPropertiesUtil.PROPERTY_PREFIX + "file.buffer.os.memory.threshold";
	//public static final int DEFAULT_MEMORY_THRESHOLD = 1 << 18;
	public static final int INFINIT_MEMORY_THRESHOLD = -1;
	public static final int DEFAULT_INITIAL_MEMORY_BUFFER_SIZE = 1 << 16;
	public static final int DEFAULT_INPUT_BUFFER_LENGTH = 1 << 14;
	
	private final int memoryThreshold;
	private final int initialMemoryBufferSize;
	private final int inputBufferLength;
	
	private final ByteArrayOutputStream memoryOutput;
	private int size;
	private File file;
	private BufferedOutputStream fileOutput;
	private boolean closed;
	private boolean disposed;
	
	public FileBufferedOutputStream() {
		this(JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).getIntegerProperty(PROPERTY_MEMORY_THRESHOLD, INFINIT_MEMORY_THRESHOLD), DEFAULT_INITIAL_MEMORY_BUFFER_SIZE, DEFAULT_INPUT_BUFFER_LENGTH);
	}
	
	public FileBufferedOutputStream(int memoryThreshold) {
		this(memoryThreshold, DEFAULT_INITIAL_MEMORY_BUFFER_SIZE, DEFAULT_INPUT_BUFFER_LENGTH);
	}
	
	public FileBufferedOutputStream(int memoryThreshold, int initialMemoryBufferSize) {
		this(memoryThreshold, initialMemoryBufferSize, DEFAULT_INPUT_BUFFER_LENGTH);
	}
	
	public FileBufferedOutputStream(int memoryThreshold, int initialMemoryBufferSize, int inputBufferLength) {
		this.memoryThreshold = memoryThreshold;
		this.initialMemoryBufferSize = initialMemoryBufferSize;
		this.inputBufferLength = inputBufferLength;
		
		size = 0;
		if (this.memoryThreshold == 0)
		{
			memoryOutput = null;
		}
		else
		{
			int initialSize = this.initialMemoryBufferSize;
			if (initialSize > this.memoryThreshold)
			{
				initialSize = this.memoryThreshold;
			}
			memoryOutput = new ByteArrayOutputStream(initialSize);
		}
	}

	@Override
	public void write(int b) throws IOException {
		checkClosed();
		
		if (availableMemorySpace() > 0) {
			memoryOutput.write(b);
		} else {
			ensureFileOutput().write(b);
		}
		
		++size;
	}

	protected int availableMemorySpace() {
		int availableMemorySpace;
		if (memoryOutput != null
				&& (memoryThreshold < 0 || memoryOutput.size() < memoryThreshold)) {
			availableMemorySpace = memoryThreshold - memoryOutput.size();
		} else {
			availableMemorySpace = 0;
		}
		return availableMemorySpace;
	}
	
	protected BufferedOutputStream ensureFileOutput() throws IOException, FileNotFoundException {
		if (fileOutput == null) {
			file = File.createTempFile("file.buff.os.", ".tmp");
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutput = new BufferedOutputStream(fileOutputStream);
		}
		return fileOutput;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		checkClosed();
		
		int memoryLen = availableMemorySpace();
		if (len < memoryLen) {
			memoryLen = len;
		}
		
		if (memoryLen > 0) {
			memoryOutput.write(b, off, memoryLen);
		}
		
		if (memoryLen < len) {
			ensureFileOutput().write(b, off + memoryLen, len - memoryLen);
		}
		
		size += len;
	}

	public void checkClosed() {
		if (closed) {
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_OUTPUT_STREAM_ALREADY_CLOSED,
					(Object[])null);
		}
	}

	@Override
	public void close() throws IOException {
		if (!closed && fileOutput != null) {
			fileOutput.flush();
			fileOutput.close();
		}
		
		closed = true;
	}

	@Override
	public void flush() throws IOException {
		if (fileOutput != null) {
			fileOutput.flush();
		}
	}

	public int size() {
		return size;
	}
	
	public void writeData(OutputStream out) throws IOException {
		if (!closed) {
			close();
		}

		if (memoryOutput != null) {
			memoryOutput.writeTo(out);
		}
		
		if (file != null) {
			FileInputStream fileInput = new FileInputStream(file);
			boolean inputClosed = false;
			try {
				byte[] buffer = new byte[inputBufferLength];
				int read;
				while ((read = fileInput.read(buffer)) > 0) {
					out.write(buffer, 0, read);
				}
				fileInput.close();
				inputClosed = true;
			} finally {
				if (!inputClosed) {
					try {
						fileInput.close();
					} catch (IOException e) {
						log.warn("Could not close file input stream", e);
					}
				}
			}
		}
	}
	
	public void dispose() {
		if (disposed) {
			return;
		}
		
		boolean success = true;
		if (!closed && fileOutput != null) {
			try {
				fileOutput.close();
			} catch (IOException e) {
				log.warn("Error while closing the temporary file output stream", e);
				success = false;
			}
		}
		
		if (file != null && !file.delete()) {
			log.warn("Error while deleting the temporary file");
			success = false;
		}
		
		disposed = success;
	}

	@Override
	protected void finalize() throws Throwable //NOSONAR 
	{
		dispose();
		super.finalize();
	}
	
	public InputStream getDataInputStream() throws IOException
	{
		if (!closed)
		{
			close();
		}
		
		return new DataStream();
	}
	
	protected class DataStream extends InputStream
	{
		private int memoryIdx;
		private final byte[] memoryData;
		private final InputStream fileInput;
		
		public DataStream() throws FileNotFoundException
		{
			memoryIdx = 0;
			memoryData = memoryOutput == null ? new byte[0] : memoryOutput.toByteArray(); 
			fileInput = file == null ? null : new BufferedInputStream(new FileInputStream(file));
		}
		
		@Override
		public synchronized int read() throws IOException
		{
			int read;
			if (memoryIdx < memoryData.length)
			{
				read = memoryData[memoryIdx] & 0xff;
				++memoryIdx;
			}
			else if (fileInput != null)
			{
				read = fileInput.read();
			}
			else
			{
				read = -1;
			}
			return read;
		}
		
		@Override
		public synchronized int read(byte b[], int off, int len) throws IOException
		{
			if (len <= 0)
			{
				return 0;
			}
			
			int read;
			if (memoryIdx < memoryData.length)
			{
				read = len;
				if (read > memoryData.length - memoryIdx)
				{
					read = memoryData.length - memoryIdx;
				}
				
				System.arraycopy(memoryData, memoryIdx, b, off, read);
				memoryIdx += read;
			}
			else
			{
				read = 0;
			}
			
			if (read < len && fileInput != null)
			{
				int readFile = fileInput.read(b, off + read, len - read);
				if (readFile > 0)
				{
					read += readFile;
				}
			}
			
			return read == 0 ? -1 : read;
		}

		@Override
		public void close() throws IOException
		{
			if (fileInput != null)
			{
				fileInput.close();
			}
		}

		@Override
		public synchronized int available() throws IOException
		{
			int available = memoryData.length - memoryIdx;
			if (fileInput != null)
			{
				available += fileInput.available();
			}
			return available;
		}

		@Override
		public synchronized long skip(long n) throws IOException
		{
			if (n <= 0)
			{
				return 0;
			}
			
			long skipped;
			if (memoryIdx < memoryData.length)
			{
				skipped = n;
				if (skipped > memoryData.length - memoryIdx)
				{
					skipped = memoryData.length - memoryIdx;
				}
				
				memoryIdx += skipped;
			}
			else
			{
				skipped = 0;
			}
			
			if (skipped < n && fileInput != null)
			{
				skipped += fileInput.skip(n - skipped);
			}
			
			return skipped;
		}
		
	}
}
