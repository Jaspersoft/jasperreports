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
package net.sf.jasperreports.util;

import java.util.UUID;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.util.DigestUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class UUIDTest
{
	@Test
	public void deriveUUIDZero()
	{
		UUID uuid = UUID.randomUUID();
		UUID derived = DigestUtils.instance().deriveUUID(uuid, 0, 0);
		assert derived.equals(uuid);
	}
	
	@Test(dataProvider = "longMasks")
	public void deriveUUID(long high, long low)
	{
		UUID uuid = UUID.randomUUID();
		UUID derived = DigestUtils.instance().deriveUUID(uuid, low, high);
		assert derived.version() == uuid.version();
		assert derived.variant() == uuid.variant();
	}

	@DataProvider
	public Object[][] longMasks()
	{
		return new Object[][] {
			{0l, 0l},
			{0xffffffffffffffffl, 0xffffffffffffffffl},
			{0xffffffffffffffffl, 0},
			{0, 0xffffffffffffffffl},
			{0x5555555555555555l, 0x5555555555555555l},
			{0xaaaaaaaaaaaaaaaal, 0xaaaaaaaaaaaaaaaal},
		};
	}
	
	@Test(dataProvider = "stringMasks")
	public void deriveUUID(String mask)
	{
		UUID uuid = UUID.randomUUID();
		UUID derived = DigestUtils.instance().deriveUUID(uuid, mask);
		assert derived.version() == uuid.version();
		assert derived.variant() == uuid.variant();
	}

	@DataProvider
	public Object[][] stringMasks()
	{
		return new Object[][] {
			{""},
			{"foo"},
			{"bar"},
			{BandTypeEnum.TITLE.name()},
			{BandTypeEnum.SUMMARY.name()},
			{BandTypeEnum.PAGE_HEADER.name()},
			{BandTypeEnum.PAGE_FOOTER.name()},
			{BandTypeEnum.COLUMN_FOOTER.name()},
			{BandTypeEnum.COLUMN_HEADER.name()},
			{BandTypeEnum.DETAIL.name()},
			{BandTypeEnum.GROUP_HEADER.name() + "-foo"},
			{BandTypeEnum.GROUP_FOOTER.name() + "-foo"},
		};
	}
}
