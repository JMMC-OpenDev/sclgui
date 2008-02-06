<?xml version="1.0" encoding="UTF-8" ?>
<!--
********************************************************************************
JMMC project

"@(#) $Id: sclguiVOTableToCSV.xsl,v 1.3 2008-02-06 14:54:03 mella Exp $"

History
~~~~~~~
$Log: not supported by cvs2svn $
Revision 1.2  2007/06/26 15:06:39  lafrasse
Corrected by Guillaume MELLA (different namespace handling).

Revision 1.1  2006/06/19 11:28:01  mella
Xsl material to export VOTable content

********************************************************************************
NAME
voTableToCSV.xsl
- transform one VOTable into a csv file 
- accept external parameter to specify field separator (fieldSeparator)

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:VOT="http://www.ivoa.net/xml/VOTable/v1.1"
    xmlns:exslt="http://exslt.org/common"
    extension-element-prefixes="exslt">
    <xsl:output method="text" encoding="UTF-8" />

    <xsl:param name="fieldSeparator">,</xsl:param>
    <xsl:variable name="rcsId">$Id voTableToCSV.xsl,v 1.2 2007/06/26 15:06:39 lafrasse Exp $</xsl:variable>

    <!--  This template try to apply-template for /VOTABLE nodes that have no namespace 
    and make the same treatment on a copy of the xml document handled 
    by the remove namespace template -->		
    <xsl:template match="/">
        <xsl:apply-templates select="/VOTABLE" />
        <xsl:variable name="votableWithoutNS">
            <xsl:for-each select="/VOT:*">
                <xsl:call-template name="removeNS"/> 
            </xsl:for-each>
        </xsl:variable>
        <xsl:apply-templates select="exslt:node-set($votableWithoutNS)/VOTABLE" />
    </xsl:template>

    <!-- This template remove the namespace of current node and associated descendants -->    
    <xsl:template name="removeNS">
        <xsl:element name="{local-name()}">
            <xsl:copy-of select="@*|text()"/>
            <xsl:for-each select="./VOT:*">
                <xsl:call-template name="removeNS"/> 
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <!-- Read line separated description and output them prefixed with dash --> 
    <xsl:template match="DESCRIPTION">#  <xsl:call-template name="SubstringReplace">
            <xsl:with-param name="stringIn" select="."/>
            <xsl:with-param name="substringIn" select="'&#10;'"/>
            <xsl:with-param name="substringOut" select="'&#10;#'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="VOTABLE">
        <xsl:text>#  This file was generated using Java SearchCal program&#10;</xsl:text>
        <xsl:value-of select="concat('#  XSLT stylesheet version used: ',$rcsId)"/>
        <xsl:value-of select="'&#10;'"/>
        <!-- Print description -->
        <xsl:apply-templates select="./DESCRIPTION"/>
        <xsl:value-of select="'&#10;'"/>
        <xsl:for-each select="./RESOURCE/TABLE">
            <!-- Print fields name as header -->
            <xsl:for-each select="./FIELD[not(@type='hidden')]">
                <xsl:value-of select="./@name"/>
                <xsl:value-of select="$fieldSeparator"/>
            </xsl:for-each>
            <!-- store indices for future output of TD linked to non hidden fields -->
            <xsl:variable name="indices">
                <xsl:for-each select="./FIELD">
                    <xsl:if test="not(@type='hidden')">
                        <indice> <xsl:value-of select="position()"/> </indice>
                    </xsl:if>
                </xsl:for-each>
            </xsl:variable>

            <!-- Print data -->
            <xsl:for-each select="./DATA/TABLEDATA">
                <xsl:for-each select="./TR">
                    <xsl:variable name="TDs">
                        <xsl:copy-of select="./TD"/>
                    </xsl:variable>
                    <xsl:value-of select="'&#10;'"/>
                    <xsl:for-each select="exslt:node-set($indices)/indice">
                        <xsl:variable name="index" select="."/>
                        <xsl:value-of select="exslt:node-set($TDs)/*[position()=$index]"/>
                        <xsl:value-of select="$fieldSeparator"/>
                    </xsl:for-each>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    
    
    <xsl:template name="SubstringReplace">
        <xsl:param name="stringIn"/>
        <xsl:param name="substringIn"/>
        <xsl:param name="substringOut"/>
        <xsl:choose>
            <xsl:when
                test="contains($stringIn,$substringIn)">
                <xsl:value-of select="concat(substring-before($stringIn,$substringIn),$substringOut)"/>
                <xsl:call-template name="SubstringReplace">
                    <xsl:with-param name="stringIn" select="substring-after($stringIn,$substringIn)"/>
                    <xsl:with-param name="substringIn" select="$substringIn"/>
                    <xsl:with-param name="substringOut" select="$substringOut"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$stringIn"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>
