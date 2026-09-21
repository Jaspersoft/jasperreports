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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class DigestUtils
{
	private static final String EXCEPTION_MESSAGE_KEY_MD5_NOT_AVAILABLE = "util.digest.md5.not.available";

	private static final DigestUtils INSTANCE = new DigestUtils();
	
	private static final char[] HEX_CHARS = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	public static DigestUtils instance()
	{
		return INSTANCE;
	}
	
	private DigestUtils()
	{
	}
	
	public MD5Digest md5(String text)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] digestBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			long low = (long) (digestBytes[0] &0xFF) << 56
					| (long) (digestBytes[1] &0xFF) << 48
					| (long) (digestBytes[2] &0xFF) << 40
					| (long) (digestBytes[3] &0xFF) << 32
					| (long) (digestBytes[4] &0xFF) << 24
					| (long) (digestBytes[5] &0xFF) << 16
					| (long) (digestBytes[6] &0xFF) << 8
					| (long) (digestBytes[7] &0xFF) << 0;
			long high = (long) (digestBytes[8] &0xFF) << 56
					| (long) (digestBytes[9] &0xFF) << 48
					| (long) (digestBytes[10] &0xFF) << 40
					| (long) (digestBytes[11] &0xFF) << 32
					| (long) (digestBytes[12] &0xFF) << 24
					| (long) (digestBytes[13] &0xFF) << 16
					| (long) (digestBytes[14] &0xFF) << 8
					| (long) (digestBytes[15] &0xFF) << 0;
			return new MD5Digest(low, high);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_MD5_NOT_AVAILABLE,
					(Object[])null,
					e);
		}
	}
	
	public String sha256(String text)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] digestBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			
			char[] digestChars = new char[digestBytes.length * 2];
			for (int i = 0; i < digestBytes.length; i++)
			{
				digestChars[2*i] = HEX_CHARS[(digestBytes[i] & 0xf0) >>> 4];
				digestChars[2*i + 1] = HEX_CHARS[digestBytes[i] & 0xf];
			}
			return new String(digestChars);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_MD5_NOT_AVAILABLE,
					(Object[])null,
					e);
		}
	}
	
	public UUID deriveUUID(UUID base, String text)
	{
		MD5Digest textMD5 = md5(text);
		long md5Low = textMD5.getLow();
		long md5High = textMD5.getHigh();
		return deriveUUID(base, md5Low, md5High);
	}
	
	public UUID deriveUUID(UUID base, long maskLow, long maskHigh)
	{
		long baseMostSig = base.getMostSignificantBits();
		long baseLeastSig = base.getLeastSignificantBits();
		
		return new UUID(
				baseMostSig ^ (maskLow & 0xffffffffffff0fffl),//preserve version 
				baseLeastSig ^ (maskHigh & 0x3fffffffffffffffl)//preserve variant
				);
	}
	
}
