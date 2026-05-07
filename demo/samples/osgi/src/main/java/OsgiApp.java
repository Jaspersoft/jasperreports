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
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.framework.FrameworkFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.wiring.BundleWiring;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OsgiApp
{
	private Framework framework;
	private Bundle jrBundle;


	/**
	 *
	 */
	public static void main(String[] args)
	{
		OsgiApp app = new OsgiApp();

		try
		{
			app.test();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}


	private void test() throws Exception
	{
		startFramework();
		try
		{
			installBundles();
			fill();
			xml();
		}
		finally
		{
			stopFramework();
		}
	}


	private void startFramework() throws Exception
	{
		try
		{
			Map<String, String> config = new HashMap<String, String>();
			config.put("org.osgi.framework.storage", "target/felix-cache");
			config.put("org.osgi.framework.storage.clean", "onFirstInit");
			config.put("felix.log.level", "1");
			config.put("org.osgi.framework.bundle.parent", "framework");
			config.put(
				"org.osgi.framework.bootdelegation",
				"sun.*,com.sun.*,"
				+ "javax.*,"
				+ "org.xml.*,org.w3c.*,"
				+ "com.fasterxml.*,"
				+ "org.apache.*,"
				+ "org.eclipse.*"
			);

			FrameworkFactory frameworkFactory = new FrameworkFactory();
			framework = frameworkFactory.newFramework(config);
			framework.start();

			System.out.println("OSGi Framework [" + framework.getSymbolicName() + " " + framework.getVersion() + "] started.");
		}
		catch (Exception e)
		{
			throw new Exception("Failed to start OSGi framework", e);
		}
	}


	private void installBundles() throws Exception
	{
		File[] files = getFiles(new File("target/dependency"), "jar");
		for (File jarFile : files)
		{
			try
			{
				if (jarFile.getName().startsWith("jasperreports-"))
				{
					jarFile = OsgiUtil.alterBundleVersion(jarFile);
				}
				else
				{
					jarFile = OsgiUtil.ensureImportExportPackage(jarFile);
				}

				Bundle bundle = installBundle(jarFile);

				if ("net.sf.jasperreports.core".equals(bundle.getSymbolicName()))
				{
					jrBundle = bundle;
				}
			}
			catch (Exception e)
			{
				throw new Exception("Failed to install bundle: " + jarFile.getAbsolutePath(), e);
			}
		}

		BundleContext bundleContext = framework.getBundleContext();
		for (Bundle bundle : bundleContext.getBundles())
		{
			try
			{
				bundle.start();
			}
			catch (Exception e)
			{
				System.out.println("Could not start bundle: " + bundle.getSymbolicName() + " (" + e.getMessage() + ")");
			}
		}
	}


	private Bundle installBundle(File bundleJar) throws Exception
	{
		BundleContext bundleContext = framework.getBundleContext();
		String bundleLocation = "file:" + bundleJar.getAbsolutePath();
		System.out.println("Installing: " + bundleLocation);
		Bundle bundle = bundleContext.installBundle(bundleLocation);
		return bundle;
	}


	private void fill() throws Exception
	{
		ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(createCompositeClassLoader());

			Class<?> fillManagerClass = jrBundle.loadClass("net.sf.jasperreports.engine.JasperFillManager");

			Method fillMethod = 
				fillManagerClass.getMethod(
					"fillReportToFile", 
					String.class, 
					Map.class
					);

			System.out.println("Filling reports via bundle-loaded JasperFillManager...");

			File[] files = getFiles(new File("target/reports"), "jasper");
			for(int i = 0; i < files.length; i++)
			{
				File reportFile = files[i];
				long start = System.currentTimeMillis();
				fillMethod.invoke(
					null,
					reportFile.getAbsolutePath(), 
					(Map<String, Object>)null
					);
				System.out.println("Report : " + reportFile + ". Filling time : " + (System.currentTimeMillis() - start));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Failed to fill report via OSGi bundle" + e);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(originalCL);
		}
	}


	private void xml() throws Exception
	{
		ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(createCompositeClassLoader());

			Class<?> exportManagerClass = jrBundle.loadClass("net.sf.jasperreports.engine.JasperExportManager");

			Method exportMethod = exportManagerClass.getMethod("exportReportToXmlFile", String.class, boolean.class);

			System.out.println("Exporting reports via bundle-loaded JasperExportManager...");

			File[] files = getFiles(new File("target/reports"), "jrprint");
			for (int i = 0; i < files.length; i++)
			{
				File reportFile = files[i];
				long start = System.currentTimeMillis();
				exportMethod.invoke(null, reportFile.getAbsolutePath(), false);
				System.out.println("Report : " + reportFile + ". XML export time : " + (System.currentTimeMillis() - start));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Failed to export report to XML via OSGi bundle", e);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(originalCL);
		}
	}


	private ClassLoader createCompositeClassLoader()
	{
		List<ClassLoader> classLoaders = new ArrayList<>();
		BundleContext bundleContext = framework.getBundleContext();
		for (Bundle bundle : bundleContext.getBundles())
		{
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			if (wiring != null)
			{
				ClassLoader cl = wiring.getClassLoader();
				if (cl != null)
				{
					classLoaders.add(cl);
				}
			}
		}
		return new CompositeClassLoader(classLoaders);
	}


	private void stopFramework() throws Exception
	{
		if (framework != null)
		{
			try
			{
				framework.stop();
				framework.waitForStop(5000);
				System.out.println("OSGi framework stopped.");
			}
			catch (Exception e)
			{
				throw new Exception("Failed to stop OSGi framework", e);
			}
		}
	}

	
	private File[] getFiles(File parentFile, String extension)
	{
		List<File> fileList = new ArrayList<>();
		String[] files = parentFile.list();
		if (files != null)
		{
			for(int i = 0; i < files.length; i++)
			{
				String reportFile = files[i];
				if (reportFile.endsWith("." + extension))
				{
					fileList.add(new File(parentFile, reportFile)); 
				}
			}
		}
		return fileList.toArray(new File[fileList.size()]);
	}
}
