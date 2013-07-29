<?xml version="1.0"?>
<!--
********************************************************************************
* JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
********************************************************************************
DESCRIPTION
- transform VOTable into HTML files
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:VOT11="http://www.ivoa.net/xml/VOTable/v1.1"
                xmlns:exslt="http://exslt.org/common"
                exclude-result-prefixes="exslt VOT11">

    <xsl:output method="xml" indent="no" encoding="UTF-8"/>

    <!-- FIELD/PARAM ID keys -->
    <xsl:key name="fieldID" match="/VOT11:VOTABLE/VOT11:RESOURCE/VOT11:TABLE/VOT11:FIELD" use = "@ID" />
    <xsl:key name="paramID" match="/VOT11:VOTABLE/VOT11:RESOURCE/VOT11:TABLE/VOT11:PARAM" use = "@ID" />

<!-- Confidence and origin indexes defined for outputFormat 2013.7 (19/07/2013) -->
<xsl:variable name="colorSets">
    <!-- confidence indexes -->
    <set>
        <key>c0</key>
        <value>NO</value>
        <color>none</color>
    </set>
    <set>
        <key>c1</key>
        <value>LOW</value>
        <color>#6E6E6E</color>
    </set>
    <set>
        <key>c2</key>
        <value>MEDIUM</value>
        <color>#D8D8D8</color>
    </set>
    <set>
        <key>c3</key>
        <value>HIGH</value>
        <color>#EFEFEF</color>
    </set>

    <!-- origin indexes (catalog) -->
    <set>
        <key>o0</key>
        <value>NO CATALOG</value>
        <color>none</color>
    </set>
    <set>
        <key>o1</key>
        <value>MIXED CATALOG</value>
        <color>none</color>
    </set>
    <set>
        <key>o2</key>
        <value>computed</value>
        <color>none</color>
    </set>
    <set>
        <key>o3</key>
        <value>II/297/irc</value>
        <color>#ff80d4</color>
    </set>
    <set>
        <key>o4</key>
        <value>I/280</value>
        <color>#ffaa80</color>
    </set>
    <set>
        <key>o5</key>
        <value>I/280B</value>
        <color>#ffaa80</color>
    </set>
    <set>
        <key>o6</key>
        <value>V/50/catalog</value>
        <color>#80ff80</color>
    </set>
    <set>
        <key>o7</key>
        <value>II/225/catalog</value>
        <color>#ffff80</color>
    </set>
    <set>
        <key>o8</key>
        <value>B/denis</value>
        <color>#80ffd5</color>
    </set>
    <set>
        <key>o9</key>
        <value>J/A+A/413/1037/table1</value>
        <color>#80ffff</color>
    </set>
    <set>
        <key>o10</key>
        <value>I/196/main</value>
        <color>#80d4ff</color>
    </set>
    <set>
        <key>o11</key>
        <value>I/239/hip_main</value>
        <color>#ff8080</color>
    </set>
    <set>
        <key>o12</key>
        <value>I/311/hip2</value>
        <color>#ff80aa</color>
    </set>
    <set>
        <key>o13</key>
        <value>J/A+A/393/183/catalog</value>
        <color>#80aaff</color>
    </set>
    <set>
        <key>o14</key>
        <value>II/246/out</value>
        <color>#aaff80</color>
    </set>
    <set>
        <key>o15</key>
        <value>J/A+A/433/1155</value>
        <color>#80ffaa</color>
    </set>
    <set>
        <key>o16</key>
        <value>MIDI</value>
        <color>#8080ff</color>
    </set>
    <set>
        <key>o17</key>
        <value>II/7A/catalog</value>
        <color>#d5ff80</color>
    </set>
    <set>
        <key>o18</key>
        <value>V/36B/bsc4s</value>
        <color>#aa80ff</color>
    </set>
    <set>
        <key>o19</key>
        <value>B/sb9/main</value>
        <color>#d580ff</color>
    </set>
    <set>
        <key>o20</key>
        <value>I/284</value>
        <color>#ffd580</color>
    </set>
    <set>
        <key>o21</key>
        <value>B/wds/wds</value>
        <color>#ff80ff</color>
    </set>
</xsl:variable>
<!-- convert colorSet into node-set -->
<xsl:variable name="colorNodeSet" select="exslt:node-set($colorSets)" />


<xsl:template match="/">
    <xsl:variable name="table" select="VOT11:VOTABLE/VOT11:RESOURCE/VOT11:TABLE"/>
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
            <title>SearchCal GUI</title>
            <xsl:call-template name="generateCSS" />
        </head>
        <xsl:text>&#10;</xsl:text>
        <body>
            <table>
                <tr><xsl:call-template name="generateLegend" /></tr>
            </table>
            <xsl:text>&#10;</xsl:text>
            
            <xsl:variable name="mappings">
                <xsl:call-template name="generateMapping">
                    <xsl:with-param name="groups" select="$table/VOT11:GROUP"/>
                </xsl:call-template>
            </xsl:variable>

            <!-- convert mappings into node-set -->
            <xsl:variable name="mappingNodeSet" select="exslt:node-set($mappings)" />

            <table>
                <tr>
                    <xsl:for-each select="$mappingNodeSet/*">
                        <xsl:if test="@valuePos"><th><xsl:value-of select="@name"/></th></xsl:if>
                        <xsl:if test="@errorPos"><th><xsl:value-of select="@errorName"/></th></xsl:if>
                    </xsl:for-each>
                </tr>
                <xsl:text>&#10;</xsl:text>
<!--
                <xsl:message>
                    <xsl:for-each select="$mappingNodeSet/*">
                        mapping {
                        name:            <xsl:value-of select="@name"/>
                        valuePos:        <xsl:value-of select="@valuePos"/>
                        originPos:       <xsl:value-of select="@originPos"/>
                        confidencePos:   <xsl:value-of select="@confidencePos"/>
                        originConst:     <xsl:value-of select="@originConst"/>
                        confidenceConst: <xsl:value-of select="@confidenceConst"/>
                        errorName:       <xsl:value-of select="@errorName"/>
                        errorPos:        <xsl:value-of select="@errorPos"/>
                        }
                    </xsl:for-each>
                </xsl:message>
-->
                <xsl:apply-templates select="$table/VOT11:DATA/VOT11:TABLEDATA/VOT11:TR">
                    <xsl:with-param name="mappingNodeSet" select="$mappingNodeSet"/>
                </xsl:apply-templates>
                
            </table>
        </body>
    </html>
</xsl:template>


<xsl:template name="generateMapping">
    <xsl:param name="groups"/>
    <xsl:variable name="fields" select="/VOT11:VOTABLE/VOT11:RESOURCE/VOT11:TABLE/VOT11:FIELD"/>
    <xsl:variable name="errorPattern" select="'_ERROR'"/>

    <xsl:for-each select="$groups">
        <xsl:element name="mapping">
            <xsl:for-each select="./VOT11:FIELDref">
                <xsl:variable name="field" select="key('fieldID', @ref)"/>
                <xsl:variable name="fieldName" select="$field/@name"/>
                <xsl:variable name="fieldUCD"  select="$field/@ucd"/>
                <xsl:choose>
                    <xsl:when test="contains($fieldName, 'origin')">
                        <xsl:for-each select="$fields">
                            <xsl:if test="$fieldName = @name">
                                <xsl:attribute name="originPos"><xsl:value-of select="position()"/></xsl:attribute> 
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:when test="contains($fieldName, 'confidence')">
                        <xsl:for-each select="$fields">
                            <xsl:if test="$fieldName = @name">
                                <xsl:attribute name="confidencePos"><xsl:value-of select="position()"/></xsl:attribute> 
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:when>
                    <!-- error property handling -->
                    <xsl:when test="substring($fieldUCD, (string-length($fieldUCD) - string-length($errorPattern)) + 1) = $errorPattern">
                        <xsl:attribute name="errorName"><xsl:value-of select="$fieldName"/></xsl:attribute> 
                        <xsl:for-each select="$fields">
                            <xsl:if test="$fieldName = @name">
                                <xsl:attribute name="errorPos"><xsl:value-of select="position()"/></xsl:attribute> 
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="name"><xsl:value-of select="$fieldName"/></xsl:attribute> 
                        <xsl:for-each select="$fields">
                            <xsl:if test="$fieldName = @name">
                                <xsl:attribute name="valuePos"><xsl:value-of select="position()"/></xsl:attribute> 
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>

            <xsl:for-each select="./VOT11:PARAMref">
                <xsl:variable name="param" select="key('paramID', @ref)"/>
                <xsl:variable name="paramName" select="$param/@name"/>
                <xsl:choose>
                    <xsl:when test="contains($paramName, 'origin')">
                        <xsl:attribute name="originConst"><xsl:value-of select="$param/@value"/></xsl:attribute> 
                    </xsl:when>
                    <xsl:when test="contains($paramName, 'confidence')">
                        <xsl:attribute name="confidenceConst"><xsl:value-of select="$param/@value"/></xsl:attribute> 
                    </xsl:when>
                </xsl:choose>
            </xsl:for-each>
        </xsl:element>
    </xsl:for-each>
</xsl:template>


<xsl:template name="generateCSS">
<style type="text/css">
.content {
background-color:#F0F0F0;
border:1px solid #BBBBBB;
color: #010170;
padding: 1px;
margin: 1px;
font-family:Arial,Helvetica,sans-serif;
font-size:11px;
}
.vbox {
border:1px solid #CCCCCC;
padding: 3px;
margin: 3px;
float: left;
}
.box {
border:1px solid #CCCCCC;
padding: 3px;
margin: 3px;
}
.fixed {
position:fixed;
}
table {
border: 2px solid #000099;
border-collapse:collapse;
}
td {
border: 1px solid #000099;
}
<xsl:for-each select="$colorNodeSet/*">
<xsl:value-of select="concat('td.',xhtml:key,' { background-color : ',xhtml:color,' }&#10;')"/>
</xsl:for-each>
th {
border: 1px solid #000099;
background-color:#FFFFDD;
}
</style>
</xsl:template>


<xsl:template name="generateLegend">
<xsl:for-each select="$colorNodeSet/*">
    <td class="{xhtml:key}">
        <xsl:value-of select="xhtml:value"/>
    </td>
</xsl:for-each>
</xsl:template>


<xsl:template match="VOT11:TR">
    <xsl:param name="mappingNodeSet" />
    <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
    <xsl:apply-templates select="$mappingNodeSet/*">
        <xsl:with-param name="trNode" select="."/>
    </xsl:apply-templates>
    <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&#10;</xsl:text>
</xsl:template>


<!-- note: xsltproc requires xhtml namespace: match="xhtml:mapping" -->
<xsl:template match="mapping">
    <xsl:param name="trNode" />
    
    <xsl:if test="@valuePos">
        <xsl:variable name="iValuePos" select="number(@valuePos)"/>
        <xsl:variable name="cellValue" select="$trNode/VOT11:TD[$iValuePos]/text()"/>
        <xsl:choose>
            <xsl:when test="$cellValue">
                <xsl:text disable-output-escaping="yes">&lt;td class="o</xsl:text>
                <xsl:choose>
                    <xsl:when test="@originPos">
                        <xsl:variable name="iOriginPos" select="number(@originPos)"/>
                        <xsl:value-of disable-output-escaping="yes" select="$trNode/VOT11:TD[$iOriginPos]/text()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of disable-output-escaping="yes" select="@originConst"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text disable-output-escaping="yes"> c</xsl:text>
                <xsl:choose>
                    <xsl:when test="@confidencePos">
                        <xsl:variable name="iConfidencePos" select="number(@confidencePos)"/>
                        <xsl:value-of disable-output-escaping="yes" select="$trNode/VOT11:TD[$iConfidencePos]/text()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of disable-output-escaping="yes" select="@confidenceConst"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                <xsl:value-of select="$cellValue"/>
                <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
            </xsl:when>
            <xsl:otherwise><xsl:text disable-output-escaping="yes">&lt;td/&gt;</xsl:text></xsl:otherwise>
        </xsl:choose>
    </xsl:if>
    <xsl:if test="@errorPos">
        <xsl:variable name="iErrorPos" select="number(@errorPos)"/>
        <xsl:variable name="cellValue" select="$trNode/VOT11:TD[$iErrorPos]/text()"/>
        <xsl:choose>
            <xsl:when test="$cellValue">
                <xsl:text disable-output-escaping="yes">&lt;td class="o</xsl:text>
                <xsl:choose>
                    <xsl:when test="@originPos">
                        <xsl:variable name="iOriginPos" select="number(@originPos)"/>
                        <xsl:value-of disable-output-escaping="yes" select="$trNode/VOT11:TD[$iOriginPos]/text()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of disable-output-escaping="yes" select="@originConst"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text disable-output-escaping="yes"> c</xsl:text>
                <xsl:choose>
                    <xsl:when test="@confidencePos">
                        <xsl:variable name="iConfidencePos" select="number(@confidencePos)"/>
                        <xsl:value-of disable-output-escaping="yes" select="$trNode/VOT11:TD[$iConfidencePos]/text()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of disable-output-escaping="yes" select="@confidenceConst"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text disable-output-escaping="yes">"&gt;</xsl:text>
                <xsl:value-of select="$cellValue"/>
                <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
            </xsl:when>
            <xsl:otherwise><xsl:text disable-output-escaping="yes">&lt;td/&gt;</xsl:text></xsl:otherwise>
        </xsl:choose>
    </xsl:if>
</xsl:template>

</xsl:stylesheet>