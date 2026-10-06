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
package net.sf.jasperreports.poi.export;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.export.zip.AbstractZip;
import net.sf.jasperreports.engine.util.FileBufferedOutputStream;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class PoiEncryptUtil 
{
	/**
	 *
	 */
	public static void zipEntries(AbstractZip zip, OutputStream os, String password) throws IOException
	{
		FileBufferedOutputStream fbos = new FileBufferedOutputStream();
		zip.zipEntries(fbos);

		try (POIFSFileSystem fs = new POIFSFileSystem()) 
		{
			EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
			Encryptor enc = info.getEncryptor();
			enc.confirmPassword(password);

			try (
				OPCPackage opc = OPCPackage.open(fbos.getDataInputStream());
				OutputStream eos = enc.getDataStream(fs)
				)
			{
				opc.save(eos);
			}
			catch (InvalidFormatException | GeneralSecurityException e)
			{
				throw new JRRuntimeException(e);
			}

			fs.writeFilesystem(os);
		}			
	}
}
