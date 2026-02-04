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
package net.sf.jasperreports.json.export.schema;

import java.util.ArrayList;
import java.util.List;

public class SchemaNode {
	private int level;
	private String name;
	private NodeTypeEnum type;
	private String path;
	private List<SchemaNodeMember> members;
	private List<String> memberNames;

	public SchemaNode(int _level, String _name, NodeTypeEnum _type, String _path) {
		level = _level;
		name = _name;
		type = _type;
		path = _path;
		members = new ArrayList<>();
		memberNames = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public NodeTypeEnum getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public void addMember(String memberName) {
		members.add(new SchemaNodeMember(memberName));
		memberNames.add(memberName);
	}

	public boolean isObject() {
		return NodeTypeEnum.OBJECT.equals(type);
	}

	public boolean isArray() {
		return NodeTypeEnum.ARRAY.equals(type);
	}

	public int indexOfMember(String memberName) {
		return memberNames.indexOf(memberName);
	}

	public SchemaNodeMember getMember(int i) {
		return members.get(i);
	}

	public SchemaNodeMember getMember(String memberName) {
		if (indexOfMember(memberName) != -1) {
			return members.get(indexOfMember(memberName));
		}  else {
			return null;
		}
	}

	public List<SchemaNodeMember> getMembers() {
		return members;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder("{");
		boolean isArray = NodeTypeEnum.ARRAY.equals(type);

		out.append("level: ").append(level).append(", ");
		out.append("name: \"").append(name).append("\", ");
		out.append("type: \"").append(type.getName()).append("\", ");
		out.append("path: \"").append(path).append("\", ");
		out.append("members: [");
		if (isArray) {
			out.append("{");
		}
		for (int i=0, ln = members.size(); i < ln; i++) {
			out.append("\"").append(members.get(i).getName()).append("\"");
			if (i < ln-1) {
				out.append(", ");
			}
		}
		if (isArray) {
			out.append("}");
		}
		out.append("]}");
		return out.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return this.level == ((SchemaNode)obj).level
				&& this.name.equals(((SchemaNode)obj).name)
				&& this.type.equals(((SchemaNode)obj).type)
				&& this.path.equals(((SchemaNode)obj).path);
	}

	@Override
	public int hashCode() {
		int hash = level !=0 ? level : 41;
		hash = hash * 41 + name.hashCode();
		hash = hash * 41 + type.hashCode();
		hash = hash * 41 + path.hashCode();
		return hash;
	}
}
