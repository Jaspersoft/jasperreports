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


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class SchemaNode {
	private int level;
	private String path;
	private String parentPath;
	private NodeTypeEnum type;
	private String key;
	private String childrenKey;
	private boolean isWriteAttributes;
	private List<SchemaNodeMember> members;
	private List<String> memberNames;

	public SchemaNode(int level, String path, String parentPath, NodeTypeEnum type, String key) {
		this.level = level;
		this.path = path;
		this.parentPath = parentPath;
		this.type = type;
		this.key = key;
		members = new ArrayList<>();
		memberNames = new ArrayList<>();
	}

	public int getLevel() {
		return level;
	}

	public String getPath() {
		return path;
	}

	public String getParentPath() {
		return parentPath;
	}

	public String getKey() {
		return key;
	}

	public NodeTypeEnum getType() {
		return type;
	}

	public String getChildrenKey() {
		return childrenKey;
	}

	public void setChildrenKey(String childrenKey) {
		this.childrenKey = childrenKey;
	}

	public boolean isWriteAttributes() {
		return isWriteAttributes;
	}

	public void setWriteAttributes(boolean isWriteAttributes) {
		this.isWriteAttributes = isWriteAttributes;
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

	public boolean isValue() {
		return NodeTypeEnum.VALUE.equals(type);
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
		out.append("path: ").append(path).append(", ");
		out.append("parentPath: ").append(parentPath).append(", ");
		out.append("type: ").append(type.getName()).append(", ");
		out.append("key: ").append(key).append(", ");
		out.append("childrenKey: ").append(childrenKey).append(", ");
		out.append("isWriteAttributes: ").append(isWriteAttributes).append(", ");
		out.append("members: [");
		if (isArray) {
			out.append("{");
		}
		for (int i=0, ln = members.size(); i < ln; i++) {
			out.append(members.get(i).getName());
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
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SchemaNode)) {
			return false;
		}

		SchemaNode other = (SchemaNode) obj;
		boolean pathEquals = (this.path == null && other.path == null) || (this.path != null && this.path.equals(other.path));
		boolean parentPathEquals = (this.parentPath == null && other.parentPath == null) || (this.parentPath != null && this.parentPath.equals(other.parentPath));
		boolean typeEquals = (this.type == null && other.type == null) || (this.type != null && this.type.equals(other.type));
		boolean keyEquals = (this.key == null && other.key == null) || (this.key != null && this.key.equals(other.key));
		boolean childrenKeyEquals = (this.childrenKey == null && other.childrenKey == null) || (this.childrenKey != null && this.childrenKey.equals(other.childrenKey));
		boolean isWriteAttributesEquals = (this.isWriteAttributes == other.isWriteAttributes);

		return this.level == ((SchemaNode)obj).level && pathEquals && parentPathEquals && typeEquals && keyEquals && childrenKeyEquals && isWriteAttributesEquals;
	}

	@Override
	public int hashCode() {
		int hash = level !=0 ? level : 41;
		hash = hash * 41 + (path == null ? 0 : path.hashCode());
		hash = hash * 41 + (parentPath == null ? 0 : parentPath.hashCode());
		hash = hash * 41 + (type == null ? 0 : type.hashCode());
		hash = hash * 41 + (key == null ? 0 : key.hashCode());
		hash = hash * 41 + (childrenKey == null ? 0 : childrenKey.hashCode());
		hash = hash * 41 + Boolean.hashCode(isWriteAttributes);
		return hash;
	}
}
