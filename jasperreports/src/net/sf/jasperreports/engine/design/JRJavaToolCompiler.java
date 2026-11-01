/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2021 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.engine.design;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * A compiler for that uses the built in {@link JavaCompiler} to compile
 * reports.
 *
 * @author Philippe Marschall (philippe.marschall@gmail.com)
 */
public final class JRJavaToolCompiler extends JRAbstractJavaCompiler {

	private static final Log LOG = LogFactory.getLog(JRJavaToolCompiler.class);

	private final JavaCompiler compiler;

	/**
	 *
	 */
	public JRJavaToolCompiler(JasperReportsContext jasperReportsContext) {
		super(jasperReportsContext, false);
		this.compiler = ToolProvider.getSystemJavaCompiler();
	}

	@Override
	protected void checkLanguage(String language) throws JRException
	{
		if (!JRReport.LANGUAGE_JAVA.equals(language))
		{
			throw
				new JRException(
					EXCEPTION_MESSAGE_KEY_EXPECTED_JAVA_LANGUAGE,
					new Object[] { language, JRReport.LANGUAGE_JAVA});
		}
	}

	@Override
	protected JRCompilationSourceCode generateSourceCode(JRSourceCompileTask sourceTask) throws JRException
	{
		return JRClassGenerator.generateClass(sourceTask);
	}

	@Override
	protected String compileUnits(JRCompilationUnit[] units, String classpath, File tempDirFile) throws JRException
	{

		StandardJavaFileManager standardFileManager = this.compiler.getStandardFileManager(null, null, null);
		Map<String, JRCompilationUnit> unitsByName = Arrays.stream(units)
															.collect(toMap(JRCompilationUnit::getName, identity()));
		JavaFileManager jrFileManager = new JRJavaFileManager(standardFileManager, unitsByName);

		Writer out = new LoggingWriter(LOG);
		ReportingDiagnosticListener diagnosticListener = new ReportingDiagnosticListener();
		Iterable<String> options = classpath != null ? Arrays.asList("-classpath", classpath) : null;
		Iterable<String> classesToBeProcessed = null;
		Iterable<? extends JavaFileObject> compilationUnits = unitsByName.values().stream()
																					.map(JavaFileObjectInputAdapter::new)
																					.collect(Collectors.toList());
		CompilationTask task = this.compiler.getTask(out, jrFileManager, diagnosticListener, options, classesToBeProcessed, compilationUnits);
		task.call();

		String errors = diagnosticListener.getErrors();
		if (errors.isEmpty())
		{
			return null;
		}
		else
		{
			return errors;
		}
	}

	@Override
	protected String getSourceFileName(String unitName)
	{
		return unitName + ".java";
	}

	/**
	 * A {@link DiagnosticListener} that collects all errors to a {@link String}.
	 */
	static final class ReportingDiagnosticListener implements DiagnosticListener<JavaFileObject> {

		private final StringBuilder errors;

		ReportingDiagnosticListener() {
			this.errors = new StringBuilder();
		}

		@Override
		public void report(Diagnostic<? extends JavaFileObject> diagnostic)
		{
			if (diagnostic.getKind() == javax.tools.Diagnostic.Kind.ERROR)
			{
				this.errors.append(diagnostic.getMessage(null));
			}

		}

		String getErrors()
		{
			return this.errors.toString();
		}

	}

	/**
	 * A {@link Writer} that delegates to a {@link Log}.
	 */
	static final class LoggingWriter extends Writer {

		private final Log log;

		LoggingWriter(Log log) {
			this.log = log;
		}

		@Override
		public void write(char[] cbuf)
		{
			this.log.error(new String(cbuf));
		}

		@Override
		public void write(char[] cbuf, int off, int len)
		{
			this.log.error(new String(cbuf, off, len));
		}

		@Override
		public void write(String str)
		{
			this.log.error(str);
		}

		@Override
		public void write(String str, int off, int len)
		{
			this.log.error(str.substring(off, off + len));
		}

		@Override
		public Writer append(CharSequence csq)
		{
			this.log.error(csq);
			return this;
		}

		@Override
		public Writer append(CharSequence csq, int start, int end)
		{
			this.log.error(csq.subSequence(start, end));
			return this;
		}

		@Override
		public Writer append(char c)
		{
			this.log.error(c);
			return this;
		}

		@Override
		public void flush()
		{
			// ignore
		}

		@Override
		public void close()
		{
			// ignore
		}

	}

	/**
	 * A file manager that delegate to a {@link JRCompilationUnit} for reading
	 * sources and writing classes. All other operations a delegate the a
	 * default {@link JavaFileManager}.
	 */
	static final class JRJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

		private final Map<String, JRCompilationUnit> unitsByName;

		JRJavaFileManager(JavaFileManager delegate, Map<String, JRCompilationUnit> unitsByName) {
			super(delegate);
			this.unitsByName = unitsByName;
		}

		@Override
		public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException
		{
			List<JavaFileObject> ownFiles;
			if (isSourcePath(location) && kinds.contains(Kind.SOURCE))
			{
				ownFiles = this.unitsByName.values().stream()
														.map(JavaFileObjectInputAdapter::new)
														.collect(toList());
			}
			else if (isClassOutput(location) && kinds.contains(Kind.CLASS))
			{
				ownFiles = this.unitsByName.values().stream()
														.map(JavaFileObjectOutputAdapter::new)
														.collect(toList());
			}
			else
			{
				ownFiles = Collections.emptyList();
			}
			Iterable<JavaFileObject> delegateFiles = super.list(location, packageName, kinds, recurse);
			if (ownFiles.isEmpty())
			{
				return delegateFiles;
			}
			else
			{
				List<JavaFileObject> merged = new ArrayList<>(ownFiles);
				for (JavaFileObject toAdd : delegateFiles)
				{
					merged.add(toAdd);
				}
				return merged;
			}
		}

		@Override
		public String inferBinaryName(Location location, JavaFileObject file)
		{
			if (this.isSourcePathOrClassOutput(location))
			{
				if (file instanceof AbstractJRJavaFileObject)
				{
					AbstractJRJavaFileObject jrFileObject = (AbstractJRJavaFileObject) file;
					return jrFileObject.getCompilationUnitName();
				}
			}
			return super.inferBinaryName(location, file);
		}

		@Override
		public boolean isSameFile(FileObject a, FileObject b)
		{
			if (a instanceof AbstractJRJavaFileObject)
			{
				if (!(b instanceof AbstractJRJavaFileObject))
				{
					return false;
				}
				AbstractJRJavaFileObject first = (AbstractJRJavaFileObject) a;
				AbstractJRJavaFileObject second = (AbstractJRJavaFileObject) b;
				return (first.getKind() == second.getKind()) && first.getCompilationUnitName().equals(second.getCompilationUnitName());
			}
			else if (b instanceof AbstractJRJavaFileObject)
			{
				return false;
			}
			return super.isSameFile(a, b);
		}

		@Override
		public boolean hasLocation(Location location)
		{
			if (this.isSourcePathOrClassOutput(location))
			{
				return true;
			}
			return super.hasLocation(location);
		}

		@Override
		public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException
		{
			if (isSourcePath(location))
			{
				if (kind == Kind.SOURCE)
				{
					JRCompilationUnit compilationUnit = this.unitsByName.get(className);
					if (compilationUnit != null)
					{
						return new JavaFileObjectInputAdapter(compilationUnit, kind);
					}
				}
			}
			return super.getJavaFileForInput(location, className, kind);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException
		{
			if (isClassOutput(location))
			{
				if (kind == Kind.CLASS)
				{
					JRCompilationUnit compilationUnit = this.unitsByName.get(className);
					if (compilationUnit != null)
					{
						return new JavaFileObjectOutputAdapter(compilationUnit, kind);
					}
				}
			}
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}

		@Override
		public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException
		{
			if (isSourcePath(location) && packageName.equals(""))
			{
				JRCompilationUnit compilationUnit = this.unitsByName.get(relativeName);
				if (compilationUnit != null)
				{
					return new JavaFileObjectInputAdapter(compilationUnit);
				}
			}
			return super.getFileForInput(location, packageName, relativeName);
		}

		@Override
		public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException
		{
			if (isClassOutput(location) && packageName.equals(""))
			{
				JRCompilationUnit compilationUnit = this.unitsByName.get(relativeName);
				if (compilationUnit != null)
				{
					return new JavaFileObjectOutputAdapter(compilationUnit);
				}
			}
			return super.getFileForOutput(location, packageName, relativeName, sibling);
		}

		private boolean isSourcePathOrClassOutput(Location location)
		{
			return isSourcePath(location) || isClassOutput(location);
		}

		private static boolean isClassOutput(Location location)
		{
			return location == StandardLocation.CLASS_OUTPUT;
		}

		private static boolean isSourcePath(Location location)
		{
			return location == StandardLocation.SOURCE_PATH;
		}

	}

	/**
	 * Abstract base class for a {@link JavaFileObject} over a
	 * {@link JRCompilationUnit}.
	 */
	static abstract class AbstractJRJavaFileObject implements JavaFileObject {

		protected final JRCompilationUnit jasperCompilationUnit;
		private final Kind kind;

		AbstractJRJavaFileObject(JRCompilationUnit jasperCompilationUnit, Kind kind) {
			this.jasperCompilationUnit = jasperCompilationUnit;
			this.kind = kind;
		}

		@Override
		public URI toUri()
		{
			return URI.create("jasper://" + this.getCompilationUnitName());
		}

		String getCompilationUnitName()
		{
			return this.jasperCompilationUnit.getName();
		}

		@Override
		public long getLastModified()
		{
			return 0;
		}

		@Override
		public boolean delete()
		{
			return false;
		}

		@Override
		public Kind getKind()
		{
			return this.kind;
		}

		@Override
		public boolean isNameCompatible(String simpleName, Kind kind)
		{
			return (this.kind == kind) && this.getCompilationUnitName().equals(simpleName);
		}

		@Override
		public NestingKind getNestingKind()
		{
			return NestingKind.TOP_LEVEL;
		}

		@Override
		public Modifier getAccessLevel()
		{
			return Modifier.PUBLIC;
		}

	}

	/**
	 * An input {@link JavaFileObject} over a {@link JRCompilationUnit}.
	 */
	static final class JavaFileObjectInputAdapter extends AbstractJRJavaFileObject {

		JavaFileObjectInputAdapter(JRCompilationUnit jasperCompilationUnit, Kind kind) {
			super(jasperCompilationUnit, kind);
		}

		JavaFileObjectInputAdapter(JRCompilationUnit jasperCompilationUnit) {
			this(jasperCompilationUnit, Kind.SOURCE);
		}

		@Override
		public String getName()
		{
			return this.getCompilationUnitName() + ".java";
		}

		@Override
		public InputStream openInputStream()
		{
			// not optimized but never clalled
			return new ByteArrayInputStream(this.jasperCompilationUnit.getSourceCode().getBytes());
		}

		@Override
		public OutputStream openOutputStream()
		{
			throw new IllegalStateException("not for writing");
		}

		@Override
		public Reader openReader(boolean ignoreEncodingErrors)
		{
			return new StringReader(this.jasperCompilationUnit.getSourceCode());
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
		{
			return this.jasperCompilationUnit.getSourceCode();
		}

		@Override
		public Writer openWriter()
		{
			throw new IllegalStateException("not for writing");
		}

		@Override
		public String toString()
		{
			return "input for: " + this.getCompilationUnitName() + ".java";
		}

	}

	/**
	 * An output {@link JavaFileObject} over a {@link JRCompilationUnit}, saves
	 * the output using {@link JRCompilationUnit#setCompileData(java.io.Serializable)}.
	 */
	static final class JavaFileObjectOutputAdapter extends AbstractJRJavaFileObject {

		JavaFileObjectOutputAdapter(JRCompilationUnit jasperCompilationUnit, Kind kind) {
			super(jasperCompilationUnit, kind);
		}

		JavaFileObjectOutputAdapter(JRCompilationUnit jasperCompilationUnit) {
			this(jasperCompilationUnit, Kind.CLASS);
		}

		@Override
		public String getName()
		{
			return this.getCompilationUnitName() + "class";
		}

		@Override
		public InputStream openInputStream()
		{
			throw new IllegalStateException("not for reading");
		}

		@Override
		public OutputStream openOutputStream()
		{
			return new CompilationUnitOutputStream(this.jasperCompilationUnit);
		}

		@Override
		public Reader openReader(boolean ignoreEncodingErrors)
		{
			throw new IllegalStateException("not for reading");
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
		{
			throw new IllegalStateException("not for reading");
		}

		@Override
		public Writer openWriter()
		{
			throw new IllegalStateException("only binary writing supported");
		}

		@Override
		public String toString()
		{
			return "output for: " + this.getCompilationUnitName() + ".class";
		}

	}

	/**
	 * An {@link OutputStream} that calls
	 * {@link JRCompilationUnit#setCompileData(java.io.Serializable)} when closed.
	 */
	static final class CompilationUnitOutputStream extends OutputStream {

		private final JRCompilationUnit compilationUnit;

		private final ByteArrayOutputStream delegate;

		CompilationUnitOutputStream(JRCompilationUnit compilationUnit) {
			this.compilationUnit = compilationUnit;
			this.delegate = new ByteArrayOutputStream();
		}

		@Override
		public void write(int b)
		{
			this.delegate.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException
		{
			this.delegate.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len)
		{
			this.delegate.write(b, off, len);
		}

		@Override
		public void flush() throws IOException
		{
			this.delegate.flush();
		}

		@Override
		public void close() throws IOException
		{
			this.delegate.close();
			this.compilationUnit.setCompileData(this.delegate.toByteArray());
		}

	}

}
