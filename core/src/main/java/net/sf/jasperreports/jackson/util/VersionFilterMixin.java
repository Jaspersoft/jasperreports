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
package net.sf.jasperreports.jackson.util;

import com.fasterxml.jackson.annotation.JsonFilter;


/**
 * Jackson mixin class that applies the version property filter globally
 * to all serialized objects. This is registered via
 * {@code mapper.addMixIn(Object.class, VersionFilterMixin.class)} so that
 * the {@link VersionPropertyFilter} is active for all bean properties
 * without requiring {@code @JsonFilter} on each individual class.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonFilter(VersionPropertyFilter.FILTER_ID)
public class VersionFilterMixin
{
}
