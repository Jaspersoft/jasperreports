<?xml version="1.0" encoding="UTF-8"?>

<!--
	Renders the JRXML schema (docs/xsd/7/jasperreport-1.1.xsd) as the schema reference page.
	Polymorphic elements are listed by their kind (and chartType for charts), from the xs:alternative declarations.
	Named types and anonymous (element) types each get a section; anonymous types are anchored by their element path,
	e.g. jasperReport.dataset, and anonymous enumerations are listed with the attribute or element that uses them.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema">

<xsl:output method = "html" />
<xsl:param name="version"/>
<!-- the site path of the published schemas, relative to this page -->
<xsl:param name="schema.path">xsd/7</xsl:param>

<xsl:variable name="api.url">api/</xsl:variable>

<!-- polymorphic elements, grouped by name and alternatives -->
<xsl:key name="polymorphic" match="xs:element[xs:alternative]" use="concat(@name, '|', xs:alternative[1]/@test)"/>
<xsl:key name="polymorphicByName" match="xs:element[xs:alternative]" use="@name"/>
<!-- usages of a type, as element type or as alternative type -->
<xsl:key name="elementsByType" match="xs:element[@type]" use="@type"/>
<xsl:key name="alternativesByType" match="xs:alternative[@test]" use="@type"/>
<!-- usages of a type grouped by element name and kind -->
<xsl:key name="usages" match="xs:element[@type] | xs:alternative[@test]"
	use="concat(@type, '|', ancestor-or-self::xs:element[1]/@name, '|', self::xs:alternative/@test)"/>
<xsl:key name="simpleTypes" match="/xs:schema/xs:simpleType" use="@name"/>
<xsl:key name="complexTypes" match="/xs:schema/xs:complexType" use="@name"/>

<!-- named types, excluding the types assigned to elements that do not match any kind, which are always invalid -->
<xsl:variable name="types" select="/xs:schema/xs:complexType[not(xs:assert)]"/>
<!-- anonymous types of elements and of element kinds, excluding the always invalid fallback types -->
<xsl:variable name="elementTypes" select="//xs:complexType[not(@name)][not(xs:assert)][parent::xs:element | parent::xs:alternative[@test]]"/>
<!-- one element for each polymorphic element, i.e. each group of elements with the same name and kinds -->
<xsl:variable name="kindElements" select="//xs:element[xs:alternative][generate-id() = generate-id(key('polymorphic', concat(@name, '|', xs:alternative[1]/@test))[1])]"/>

<xsl:template match="/">
<html>
<head>
<title>JasperReports <xsl:value-of select="$version"/> - Schema Reference</title>
<link rel="stylesheet" href="resources/stylesheet.css"/>
</head>
<body bgcolor="#FFFFFF">
<a name="top"/>
<table class="top0">
  <tr>
  	<td align="left"><img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="left"/></td>
  	<td align="left"><span class="version">version <xsl:value-of select="$version"/></span></td>
    <td align="right">
<span class="element"><xsl:element name="a">
<xsl:attribute name="href">README.html</xsl:attribute>Home</xsl:element> - <xsl:element name="a">
<xsl:attribute name="href">sample.reference/README.html</xsl:attribute>Samples</xsl:element> - <xsl:element name="a">
<xsl:attribute name="href">schema.reference.html</xsl:attribute>Schema</xsl:element> - <xsl:element name="a">
<xsl:attribute name="href">config.reference.html</xsl:attribute>Configuration</xsl:element> - <xsl:element name="a">
<xsl:attribute name="href">function.reference.html</xsl:attribute>Functions</xsl:element> - <xsl:element name="a">
<xsl:attribute name="href">http://community.jaspersoft.com/wiki/jasperreports-library-faqs</xsl:attribute>FAQ</xsl:element> - <xsl:element name="a">
<xsl:attribute name="href"><xsl:value-of select="$api.url"/>index.html</xsl:attribute>API (Javadoc)</xsl:element></span>
    </td>
  </tr>
  <tr>
    <td colspan="3">
      <hr size="1"/>
    </td>
  </tr>
 </table>
 <article class="body">
 <table class="top0">
  <tr valign="middle">
    <td>
<span class="title">JasperReports - Schema Reference</span>
    </td>
  </tr>
  <tr>
    <td>
      <hr size="1"/>
    </td>
  </tr>
</table>

<br/>

<span class="description">This document describes the JRXML structure of the report template files (<span class="code">jasperReport</span> root element)
and of the style template files (<span class="code">jasperTemplate</span> root element) for the JasperReports Library.
JRXML elements are not in a namespace, and child elements can appear in any order.
Report elements, components, chart datasets and report parts select their content by their <span class="code">kind</span> attribute,
and charts by their <span class="code">chartType</span> attribute.</span>
<br/><br/>
<span class="description">The structure is described by two XML schemas:
<a href="{$schema.path}/jasperreport-1.1.xsd"><xsl:value-of select="$schema.path"/>/jasperreport-1.1.xsd</a> for strict validation with an XSD 1.1 validator,
and <a href="{$schema.path}/jasperreport-1.0.xsd"><xsl:value-of select="$schema.path"/>/jasperreport-1.0.xsd</a>, less strict, for XML editors that only support XSD 1.0.
These schemas do not apply to JasperReports 6.x JRXML files, which have a different format.</span>
<br/><br/>
<span class="description"><b>Note:</b> The schemas are provided only for validating JRXML files with external tools, such as XML editors and XSD validators.
The JasperReports Library does not use them when loading or compiling report templates,
and JRXML files do not need to reference them.</span>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td style="width: 20px;"><br/></td>
    <td><br/></td>
  </tr>
  <tr>
    <td colspan="2"><span class="label"><br/>Root Elements</span></td>
  </tr>
  <xsl:for-each select="/xs:schema/xs:element">
  <tr>
    <td></td>
    <td><span class="element"><xsl:call-template name="valueType"/></span></td>
  </tr>
  </xsl:for-each>
  <tr>
    <td colspan="2"><span class="label"><br/>Element Kinds</span></td>
  </tr>
  <tr>
    <td></td>
    <td><span class="element">
    <xsl:for-each select="$kindElements">
      <xsl:sort select="@name"/>
      <xsl:variable name="anchor"><xsl:call-template name="kindsAnchor"/></xsl:variable>
      <a href="#{$anchor}">&lt;<xsl:value-of select="@name"/>&gt;</a>
      <span class="description"><xsl:text> in </xsl:text><xsl:call-template name="parentTypes"><xsl:with-param name="elements" select="key('polymorphic', concat(@name, '|', xs:alternative[1]/@test))"/></xsl:call-template></span>
      <xsl:if test="position() != last()"><br/></xsl:if>
    </xsl:for-each>
    </span></td>
  </tr>
  <tr>
    <td colspan="2"><span class="label"><br/>Elements</span></td>
  </tr>
  <tr>
    <td></td>
    <td><span class="element">
    <xsl:for-each select="$elementTypes[parent::xs:element/parent::xs:all]">
      <xsl:sort select="../@name"/>
      <xsl:call-template name="typeRef"/>
      <span class="description"> in <xsl:for-each select="ancestor::xs:complexType[1]"><xsl:call-template name="typeRef"/></xsl:for-each></span>
      <xsl:if test="position() != last()"><br/></xsl:if>
    </xsl:for-each>
    </span></td>
  </tr>
  <tr>
    <td colspan="2"><span class="label"><br/>Types</span></td>
  </tr>
  <tr>
    <td></td>
    <td><span class="element">
    <xsl:for-each select="$types">
      <xsl:sort select="@name"/>
      <xsl:call-template name="typeLink"><xsl:with-param name="type" select="@name"/></xsl:call-template>
      <xsl:if test="position() != last()"> - </xsl:if>
    </xsl:for-each>
    </span></td>
  </tr>
  <tr>
    <td colspan="2"><span class="label"><br/>Simple Types</span></td>
  </tr>
  <tr>
    <td></td>
    <td><span class="element">
    <xsl:for-each select="/xs:schema/xs:simpleType">
      <xsl:sort select="@name"/>
      <xsl:call-template name="typeLink"><xsl:with-param name="type" select="@name"/></xsl:call-template>
      <xsl:if test="position() != last()"> - </xsl:if>
    </xsl:for-each>
    </span></td>
  </tr>
</table>

<table width="100%" cellspacing="0" cellpadding="0" border="0">
  <tr>
    <td style="width: 20px;"><br/></td>
    <td style="width: 20px;"><br/></td>
    <td style="width: 20px;"><br/></td>
    <td style="width: 20px;"><br/></td>
    <td><br/></td>
  </tr>
  <xsl:for-each select="$elementTypes[parent::xs:element/parent::xs:schema]">
    <xsl:apply-templates select="." mode="section"/>
  </xsl:for-each>
  <xsl:for-each select="$kindElements">
    <xsl:sort select="@name"/>
    <xsl:apply-templates select="." mode="kinds"/>
  </xsl:for-each>
  <xsl:for-each select="$elementTypes[not(parent::xs:element/parent::xs:schema)]">
    <xsl:sort select="ancestor::xs:element[1]/@name"/>
    <xsl:sort select="ancestor::xs:complexType[2]/@name"/>
    <xsl:apply-templates select="." mode="section"/>
  </xsl:for-each>
  <xsl:for-each select="$types">
    <xsl:sort select="@name"/>
    <xsl:apply-templates select="." mode="section"/>
  </xsl:for-each>
  <xsl:for-each select="/xs:schema/xs:simpleType">
    <xsl:sort select="@name"/>
    <xsl:apply-templates select="." mode="section"/>
  </xsl:for-each>
  <tr>
    <td colspan="5"><br/><br/></td>
  </tr>
  <tr>
    <td colspan="5"><hr size="1"/></td>
  </tr>
  <tr>
    <td colspan="5" align="center">
      <span class="copy">&#169; 2001 - <script language="javascript">document.write((new Date()).getFullYear())</script> Actian Corporation, a division of HCL Software. All rights reserved. <a href="http://www.jaspersoft.com" target="_blank" class="copy">www.jaspersoft.com</a></span>
    </td>
  </tr>
</table>
</article>
</body>
</html>
</xsl:template>


<xsl:template match="xs:complexType" mode="section">
  <xsl:call-template name="sectionHeader"/>
  <xsl:variable name="usages" select="key('elementsByType', @name) | key('alternativesByType', @name)"/>
  <xsl:if test="not(@name)">
  <tr>
    <td></td>
    <td colspan="2"><span class="label">Used in:</span></td>
    <td colspan="2"><span class="description">
      <xsl:choose>
        <xsl:when test="ancestor::xs:complexType"><xsl:for-each select="ancestor::xs:complexType[1]"><xsl:call-template name="typeRef"/></xsl:for-each></xsl:when>
        <xsl:otherwise>root element</xsl:otherwise>
      </xsl:choose>
    </span></td>
  </tr>
  </xsl:if>
  <xsl:if test="$usages">
  <tr>
    <td></td>
    <td colspan="2"><span class="label">Used in:</span></td>
    <td colspan="2"><span class="description"><xsl:call-template name="usages"><xsl:with-param name="usages" select="$usages"/></xsl:call-template></span></td>
  </tr>
  </xsl:if>
  <xsl:if test="xs:simpleContent">
  <tr>
    <td></td>
    <td colspan="2"><span class="label">Content:</span></td>
    <td colspan="2"><span class="description">text (<xsl:call-template name="typeLink"><xsl:with-param name="type" select="xs:simpleContent/xs:extension/@base"/></xsl:call-template>)</span></td>
  </tr>
  </xsl:if>
  <xsl:variable name="attributes" select="xs:attribute | xs:simpleContent/xs:extension/xs:attribute"/>
  <xsl:if test="$attributes">
  <tr>
    <td colspan="5"><br/></td>
  </tr>
  <tr>
    <td></td>
    <td colspan="4"><span class="label">Attributes</span></td>
  </tr>
  <xsl:for-each select="$attributes">
  <tr valign="top">
    <td></td>
    <td></td>
    <td colspan="2"><span class="element"><xsl:value-of select="@name"/></span></td>
    <td><span class="description">
      <xsl:call-template name="valueType"/>
      <xsl:if test="@use = 'required'">, required</xsl:if>
      <xsl:if test="xs:annotation/xs:documentation"> - <xsl:value-of select="xs:annotation/xs:documentation"/></xsl:if>
      <xsl:apply-templates select="xs:simpleType" mode="values"/>
    </span></td>
  </tr>
  </xsl:for-each>
  </xsl:if>
  <xsl:if test="xs:all/xs:element">
  <tr>
    <td colspan="5"><br/></td>
  </tr>
  <tr>
    <td></td>
    <td colspan="4"><span class="label">Child Elements</span></td>
  </tr>
  <xsl:for-each select="xs:all/xs:element">
  <tr valign="top">
    <td></td>
    <td></td>
    <td colspan="2"><span class="element">&lt;<xsl:value-of select="@name"/>&gt;</span></td>
    <td><span class="description">
      <xsl:choose>
        <xsl:when test="@maxOccurs = 'unbounded'">0..n</xsl:when>
        <xsl:otherwise>0..1</xsl:otherwise>
      </xsl:choose>
      <xsl:if test="not(xs:alternative)">, <xsl:call-template name="valueType"/></xsl:if>
      <xsl:if test="xs:alternative">, by <xsl:call-template name="selectors"><xsl:with-param name="element" select="."/></xsl:call-template> (see <a class="toc"><xsl:attribute name="href">#<xsl:call-template name="kindsAnchor"/></xsl:attribute>all kinds</a>)</xsl:if>
      <xsl:if test="xs:annotation/xs:documentation"> - <xsl:value-of select="xs:annotation/xs:documentation"/></xsl:if>
      <xsl:apply-templates select="xs:simpleType" mode="values"/>
    </span></td>
  </tr>
  </xsl:for-each>
  </xsl:if>
</xsl:template>


<!-- the values of an anonymous enumeration, listed under the attribute or element that uses it -->
<xsl:template match="xs:simpleType" mode="values">
  <xsl:for-each select="xs:restriction/xs:enumeration">
    <br/><span class="code"><xsl:value-of select="@value"/></span>
    <xsl:if test="xs:annotation/xs:documentation"> - <xsl:value-of select="xs:annotation/xs:documentation"/></xsl:if>
  </xsl:for-each>
</xsl:template>


<xsl:template match="xs:simpleType" mode="section">
  <xsl:call-template name="sectionHeader"/>
  <xsl:if test="xs:restriction/xs:pattern">
  <tr>
    <td></td>
    <td colspan="2"><span class="label">Pattern:</span></td>
    <td colspan="2"><span class="code"><xsl:value-of select="xs:restriction/xs:pattern/@value"/></span></td>
  </tr>
  </xsl:if>
  <xsl:if test="xs:restriction/xs:enumeration">
  <tr>
    <td colspan="5"><br/></td>
  </tr>
  <tr>
    <td></td>
    <td colspan="4"><span class="label">Values</span></td>
  </tr>
  <xsl:for-each select="xs:restriction/xs:enumeration">
  <tr valign="top">
    <td></td>
    <td></td>
    <td colspan="2"><span class="element"><xsl:value-of select="@value"/></span></td>
    <td><span class="description"><xsl:value-of select="xs:annotation/xs:documentation"/></span></td>
  </tr>
  </xsl:for-each>
  </xsl:if>
</xsl:template>


<!-- anchor, name, documentation and model class of a type -->
<xsl:template name="sectionHeader">
  <xsl:variable name="anchor"><xsl:call-template name="anchor"/></xsl:variable>
  <!-- anonymous types can leave their documentation to their element -->
  <xsl:variable name="documentation" select="(xs:annotation/xs:documentation | self::xs:complexType[not(@name)][not(xs:annotation/xs:documentation)]/../xs:annotation/xs:documentation)[1]"/>
  <tr>
    <td colspan="5" align="right"><br/><a name="{$anchor}"/><a href="#top" class="toc">top</a></td>
  </tr>
  <tr>
    <td colspan="5"><hr size="1"/></td>
  </tr>
  <tr>
    <td colspan="5"><span class="name"><xsl:call-template name="typeTitle"/></span></td>
  </tr>
  <xsl:if test="$documentation">
  <tr>
    <td></td>
    <td colspan="4"><span class="description"><xsl:value-of select="$documentation"/></span></td>
  </tr>
  </xsl:if>
  <tr>
    <td colspan="5"><br/></td>
  </tr>
  <xsl:if test="xs:annotation/xs:appinfo">
  <tr>
    <td></td>
    <td colspan="2"><span class="label">API:</span></td>
    <td colspan="2"><span class="element"><xsl:call-template name="apiLink"><xsl:with-param name="appinfo" select="xs:annotation/xs:appinfo"/></xsl:call-template></span></td>
  </tr>
  </xsl:if>
</xsl:template>


<!-- the section of a polymorphic element, listing its kinds, each linked to its type -->
<xsl:template match="xs:element" mode="kinds">
  <xsl:variable name="anchor"><xsl:call-template name="kindsAnchor"/></xsl:variable>
  <tr>
    <td colspan="5" align="right"><br/><a name="{$anchor}"/><a href="#top" class="toc">top</a></td>
  </tr>
  <tr>
    <td colspan="5"><hr size="1"/></td>
  </tr>
  <tr>
    <td colspan="5"><span class="name">&lt;<xsl:value-of select="@name"/>&gt; kinds</span></td>
  </tr>
  <xsl:if test="xs:annotation/xs:documentation">
  <tr>
    <td></td>
    <td colspan="4"><span class="description"><xsl:value-of select="xs:annotation/xs:documentation"/></span></td>
  </tr>
  </xsl:if>
  <tr>
    <td colspan="5"><br/></td>
  </tr>
  <tr>
    <td></td>
    <td colspan="2"><span class="label">Selected by:</span></td>
    <td colspan="2"><span class="description"><xsl:call-template name="selectors"><xsl:with-param name="element" select="."/></xsl:call-template></span></td>
  </tr>
  <tr>
    <td></td>
    <td colspan="2"><span class="label">Used in:</span></td>
    <td colspan="2"><span class="description"><xsl:call-template name="parentTypes"><xsl:with-param name="elements" select="key('polymorphic', concat(@name, '|', xs:alternative[1]/@test))"/></xsl:call-template></span></td>
  </tr>
  <tr>
    <td colspan="5"><br/></td>
  </tr>
  <tr>
    <td></td>
    <td colspan="4"><span class="label">Kinds</span></td>
  </tr>
  <xsl:for-each select="xs:alternative[@test]">
  <tr valign="top">
    <td></td>
    <td></td>
    <td colspan="2"><span class="element"><xsl:call-template name="condition"><xsl:with-param name="test" select="@test"/></xsl:call-template></span></td>
    <td><span class="description">
      <xsl:call-template name="valueType"/>
      <xsl:if test="xs:annotation/xs:documentation"> - <xsl:value-of select="xs:annotation/xs:documentation"/></xsl:if>
    </span></td>
  </tr>
  </xsl:for-each>
</xsl:template>


<!-- the anchor of the kinds section of the current polymorphic element, e.g. kinds.element;
	elements with the same name and different kinds are prefixed by the anchor of their parent type,
	e.g. kinds.ComponentElement.component -->
<xsl:template name="kindsAnchor">
  <xsl:for-each select="key('polymorphic', concat(@name, '|', xs:alternative[1]/@test))[1]">
    <xsl:text>kinds.</xsl:text>
    <xsl:if test="count(key('polymorphicByName', @name)[generate-id() = generate-id(key('polymorphic', concat(@name, '|', xs:alternative[1]/@test))[1])]) &gt; 1">
      <xsl:for-each select="ancestor::xs:complexType[1]"><xsl:call-template name="anchor"/></xsl:for-each>
      <xsl:text>.</xsl:text>
    </xsl:if>
    <xsl:value-of select="@name"/>
  </xsl:for-each>
</xsl:template>


<!-- the attributes that select the kind of a polymorphic element, e.g. "kind" or "kind and chartType" -->
<xsl:template name="selectors">
  <xsl:param name="element"/>
  <xsl:variable name="test" select="$element/xs:alternative[1]/@test"/>
  <!-- a second selector, such as chartType, might be tested only by some kinds -->
  <xsl:variable name="combined" select="$element/xs:alternative[contains(@test, ' and ')][1]/@test"/>
  <span class="code"><xsl:value-of select="substring-before(substring-after($test, '@'), ' ')"/></span>
  <xsl:if test="$combined"> and <span class="code"><xsl:value-of select="substring-before(substring-after(substring-after($combined, ' and '), '@'), ' ')"/></span></xsl:if>
</xsl:template>


<!-- renders an alternative test such as "@kind = 'chart' and @chartType = 'bar'" as kind="chart" chartType="bar" -->
<xsl:template name="condition">
  <xsl:param name="test"/>
  <xsl:variable name="first">
    <xsl:choose>
      <xsl:when test="contains($test, ' and ')"><xsl:value-of select="substring-before($test, ' and ')"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$test"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:value-of select="substring-before(substring-after($first, '@'), ' = ')"/>
  <xsl:text>="</xsl:text>
  <xsl:value-of select="translate(substring-after($first, ' = '), &quot;'&quot;, '')"/>
  <xsl:text>"</xsl:text>
  <xsl:if test="contains($test, ' and ')">
    <xsl:text> </xsl:text>
    <xsl:call-template name="condition"><xsl:with-param name="test" select="substring-after($test, ' and ')"/></xsl:call-template>
  </xsl:if>
</xsl:template>


<!-- links to the parent types of the given elements -->
<xsl:template name="parentTypes">
  <xsl:param name="elements"/>
  <xsl:for-each select="$elements/ancestor::xs:complexType[1]">
    <xsl:sort select="concat(@name, ancestor::xs:element[1]/@name)"/>
    <xsl:call-template name="typeRef"/>
    <xsl:if test="position() != last()">, </xsl:if>
  </xsl:for-each>
</xsl:template>


<!-- elements of a type, each with its parent types -->
<xsl:template name="usages">
  <xsl:param name="usages"/>
  <xsl:for-each select="$usages[generate-id() = generate-id(key('usages', concat(@type, '|', ancestor-or-self::xs:element[1]/@name, '|', self::xs:alternative/@test))[1])]">
    <xsl:sort select="ancestor-or-self::xs:element[1]/@name"/>
    <xsl:variable name="group" select="key('usages', concat(@type, '|', ancestor-or-self::xs:element[1]/@name, '|', self::xs:alternative/@test))"/>
    <span class="element">&lt;<xsl:value-of select="ancestor-or-self::xs:element[1]/@name"/>
      <xsl:if test="self::xs:alternative"><xsl:text> </xsl:text><xsl:call-template name="condition"><xsl:with-param name="test" select="@test"/></xsl:call-template></xsl:if>&gt;</span>
    <xsl:choose>
      <xsl:when test="ancestor::xs:complexType">
        <xsl:text> in </xsl:text>
        <xsl:call-template name="parentTypes"><xsl:with-param name="elements" select="$group"/></xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:text> (root element)</xsl:text></xsl:otherwise>
    </xsl:choose>
    <xsl:if test="position() != last()"><br/></xsl:if>
  </xsl:for-each>
</xsl:template>


<!-- the type of the current attribute, element or alternative: a named type, an anonymous type or an enumeration -->
<xsl:template name="valueType">
  <xsl:choose>
    <xsl:when test="@type"><xsl:call-template name="typeLink"><xsl:with-param name="type" select="@type"/></xsl:call-template></xsl:when>
    <xsl:when test="xs:complexType"><xsl:for-each select="xs:complexType"><xsl:call-template name="typeRef"/></xsl:for-each></xsl:when>
    <xsl:when test="xs:simpleType/xs:restriction/xs:enumeration">one of</xsl:when>
    <xsl:when test="xs:simpleType"><span class="code"><xsl:value-of select="xs:simpleType/xs:restriction/@base"/></span></xsl:when>
  </xsl:choose>
</xsl:template>


<!-- a link to the current complex type, named or anonymous -->
<xsl:template name="typeRef">
  <xsl:variable name="anchor"><xsl:call-template name="anchor"/></xsl:variable>
  <a href="#{$anchor}"><xsl:call-template name="typeTitle"/></a>
</xsl:template>


<!-- the name of the current type, or <element> / <element kind="..."> for anonymous types -->
<xsl:template name="typeTitle">
  <xsl:choose>
    <xsl:when test="@name"><xsl:value-of select="@name"/></xsl:when>
    <xsl:otherwise>
      <xsl:text>&lt;</xsl:text><xsl:value-of select="ancestor::xs:element[1]/@name"/>
      <xsl:if test="parent::xs:alternative"><xsl:text> </xsl:text><xsl:call-template name="condition"><xsl:with-param name="test" select="../@test"/></xsl:call-template></xsl:if>
      <xsl:text>&gt;</xsl:text>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- the anchor of the current type: its name, or the path of its element for anonymous types,
	e.g. jasperReport.dataset or Band.element.chart.bar -->
<xsl:template name="anchor">
  <xsl:choose>
    <xsl:when test="@name"><xsl:value-of select="@name"/></xsl:when>
    <xsl:otherwise>
      <xsl:for-each select="ancestor::xs:complexType[1]"><xsl:call-template name="anchor"/>.</xsl:for-each>
      <xsl:value-of select="ancestor::xs:element[1]/@name"/>
      <xsl:if test="parent::xs:alternative">.<xsl:call-template name="conditionValues"><xsl:with-param name="test" select="../@test"/></xsl:call-template></xsl:if>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- the values of an alternative test, e.g. "chart.bar" for "@kind = 'chart' and @chartType = 'bar'" -->
<xsl:template name="conditionValues">
  <xsl:param name="test"/>
  <xsl:value-of select="translate(substring-before(substring-after($test, &quot;'&quot;), &quot;'&quot;), ':', '.')"/>
  <xsl:if test="contains($test, ' and ')">.<xsl:call-template name="conditionValues"><xsl:with-param name="test" select="substring-after($test, ' and ')"/></xsl:call-template></xsl:if>
</xsl:template>


<!-- a link to a type defined in the schema, or the name of a built-in XSD type -->
<xsl:template name="typeLink">
  <xsl:param name="type"/>
  <xsl:choose>
    <xsl:when test="key('complexTypes', $type) | key('simpleTypes', $type)">
      <a href="#{$type}"><xsl:value-of select="$type"/></a>
    </xsl:when>
    <xsl:otherwise><span class="code"><xsl:value-of select="$type"/></span></xsl:otherwise>
  </xsl:choose>
</xsl:template>


<!-- a Javadoc link for the model class in appinfo, e.g. "net.sf.jasperreports.charts.JRChart (chartType=bar)" -->
<xsl:template name="apiLink">
  <xsl:param name="appinfo"/>
  <xsl:variable name="class">
    <xsl:choose>
      <xsl:when test="contains($appinfo, ' ')"><xsl:value-of select="substring-before($appinfo, ' ')"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$appinfo"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <a href="{$api.url}{translate(translate($class, '.', '/'), '$', '.')}.html"><xsl:value-of select="$class"/></a>
  <xsl:if test="contains($appinfo, ' ')"><xsl:text> </xsl:text><xsl:value-of select="substring-after($appinfo, ' ')"/></xsl:if>
</xsl:template>


</xsl:stylesheet>
