<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:jr="http://jasperreports.sourceforge.net/jasperreports">

<xsl:output method="html"/>
<xsl:param name="sf.net"/>
<xsl:param name="version"/>

<xsl:variable name="api.url">api/</xsl:variable>

<xsl:template match="/">
<html>
<head>
<title>JasperReports <xsl:value-of select="$version"/> - Functions Reference</title>
<link rel="stylesheet" href="resources/stylesheet.css"/>
<style type="text/css">
</style>
</head>
<body bgcolor="#FFFFFF">
<xsl:if test="$sf.net = 'true'">
<!-- Tracker -->
<script>
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
ga('create', 'UA-399158-5', 'sourceforge.net');
ga('send', 'pageview');
</script>
<!-- End Tracker Tag -->
</xsl:if>

<a name="top"/>
<table class="top0">
  <tr>
  	<td align="left"><img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="left"/></td>
  	<td align="left"><span class="version">version <xsl:value-of select="$version"/></span></td>
    <td align="right">
<span class="element"><xsl:element name="a">
<xsl:attribute name="href">README.html</xsl:attribute>Home</xsl:element> - <xsl:element name="a">
<xsl:attribute name="href">sample.reference/README.html</xsl:attribute>Samples</xsl:element> - <xsl:element name="a">
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
    <td colspan="2">
<span class="title">JasperReports - Function Reference</span>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <hr size="1"/>
    </td>
  </tr>
</table>

<br/>

<xsl:copy-of select="document('../demo/samples/functions/target/reports/FunctionsReport.html')"/>

<table width="100%" cellspacing="0" cellpadding="0" border="0">
  <tr>
    <td><br/><br/></td>
  </tr>
  <tr>
    <td><hr size="1"/></td>
  </tr>
  <tr>
    <td align="center">
      <span class="copy">&#169; 2001 - <script language="javascript">document.write((new Date()).getFullYear())</script> Actian Corporation, a division of HCL Software. All rights reserved. <a href="http://www.jaspersoft.com" target="_blank" class="copy">www.jaspersoft.com</a></span>
    </td>
  </tr>
</table>
</article>
</body>
</html>
</xsl:template>


</xsl:stylesheet>
