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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OsgiUtil
{
	/**
	 * Copies the JasperRerports JAR with a corrected Bundle-Version in the manifest,
	 * because development version strings (e.g. "develop-JS-78982-SNAPSHOT") are not valid OSGi versions.
	 */
	public static File alterOsgiBundle(File sourceJar) throws Exception
	{
		JarFile jarFile = new JarFile(sourceJar);
		try
		{
			Manifest manifest = jarFile.getManifest();
			String version = manifest.getMainAttributes().getValue("Bundle-Version");
			if (version != null)
			{
				manifest.getMainAttributes().putValue("Bundle-Version", toOsgiVersion(version));
			}

			File bundleJar = new File("target/osgi-bundle-cache/" + sourceJar.getName());
			bundleJar.getParentFile().mkdirs();

			JarOutputStream jos = new JarOutputStream(new FileOutputStream(bundleJar), manifest);
			try
			{
				byte[] buffer = new byte[8192];
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements())
				{
					JarEntry entry = entries.nextElement();
					if ("META-INF/MANIFEST.MF".equals(entry.getName()))
					{
						continue;
					}
					jos.putNextEntry(new JarEntry(entry.getName()));
					if (!entry.isDirectory())
					{
						InputStream is = jarFile.getInputStream(entry);
						try
						{
							int read;
							while ((read = is.read(buffer)) != -1)
							{
								jos.write(buffer, 0, read);
							}
						}
						finally
						{
							is.close();
						}
					}
					jos.closeEntry();
				}
			}
			finally
			{
				jos.close();
			}

			return bundleJar;
		}
		finally
		{
			jarFile.close();
		}
	}


	private static String toOsgiVersion(String mavenVersion)
	{
		// OSGi versions: major.minor.micro.qualifier (integers + alphanumeric qualifier with no hyphens)
		String cleaned = mavenVersion.replace("-", ".");
		String[] parts = cleaned.split("\\.");
		StringBuilder osgiVersion = new StringBuilder();
		int numericCount = 0;
		StringBuilder qualifier = new StringBuilder();
		for (String part : parts)
		{
			if (numericCount < 3 && part.matches("\\d+"))
			{
				if (numericCount > 0)
				{
					osgiVersion.append(".");
				}
				osgiVersion.append(part);
				numericCount++;
			}
			else
			{
				if (qualifier.length() > 0)
				{
					qualifier.append("_");
				}
				qualifier.append(part);
			}
		}
		while (numericCount < 3)
		{
			if (numericCount > 0)
			{
				osgiVersion.append(".");
			}
			osgiVersion.append("0");
			numericCount++;
		}
		if (qualifier.length() > 0)
		{
			osgiVersion.append(".").append(qualifier);
		}
		return osgiVersion.toString();
	}
}
