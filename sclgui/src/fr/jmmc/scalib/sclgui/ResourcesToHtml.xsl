<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
********************************************************************************
 JMMC project

 "@(#) $Id: ResourcesToHtml.xsl,v 1.1 2006-06-09 15:15:37 mella Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $

********************************************************************************
 NAME
 ResourcesToHtml.xsl   - transform one resource file into a web page

-->
<xsl:stylesheet version = '1.0'
    xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

    <xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"
        doctype-public="-//W3C//DTD HTML 4.01//EN"
        doctype-system="http://www.w3.org/TR/html4/strict.dtd" />

    <xsl:template match="resources">
        <html lang="en">
            <head>
                <title>Resources</title>
                <style type="text/css">
                    @import "http://www.jmmc.fr/doc/index.css";   
                </style>
            </head>
            <body>

                <div class="content">
                    <h1>Resources</h1>
                    <xsl:apply-templates select="//widgets"/>

                    <h1>XML Tree</h1>
                    <xsl:call-template name="t1"/>

                </div>



            </body>
        </html>
    </xsl:template>
    <xsl:template name="t1">
        <xsl:for-each select="/descendant::*">
            <xsl:for-each select="./ancestor::*"><xsl:value-of select="name(.)"/>/</xsl:for-each>
            <xsl:value-of select="name(.)"/>
            <xsl:if test="./text()">
            <xsl:value-of select="':'"/>
            <b><xsl:value-of select="./text()"/></b>
            </xsl:if>

            <xsl:if test="not(position()=last())">
                <br/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="widgets">
        <h2> <em><xsl:value-of select="./@name"/></em><xsl:value-of select="' '"/> <xsl:value-of select="name(.)"/></h2> 
        <xsl:for-each select="widget">
            <table>
                <tr>
                    <xsl:for-each select="./@*|./*">
                        <th>
                            <xsl:value-of select="name(.)"/>
                        </th>
                    </xsl:for-each>
                </tr>
                <tr>
                    <xsl:for-each select="./@*|./*">
                        <td>
                            <xsl:value-of select="."/>
                        </td>
                    </xsl:for-each>
                </tr>
            </table>
        </xsl:for-each>
            <hr/>
    </xsl:template>

</xsl:stylesheet>

