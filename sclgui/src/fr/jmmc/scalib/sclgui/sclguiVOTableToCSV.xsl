<?xml version="1.0" encoding="UTF-8" ?>
<!--
********************************************************************************
 JMMC project

 "@(#) $Id: sclguiVOTableToCSV.xsl,v 1.1 2006-06-19 11:28:01 mella Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $

********************************************************************************
 NAME
 voTableToCSV.xsl   - transform one VOTable into a csv file

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:VOT="http://www.ivoa.net/xml/VOTable/v1.1">
	<xsl:output method="text" encoding="UTF-8" />
    <xsl:template match="/"># This file was generated using Java SearchCal program
<xsl:apply-templates select="/VOTABLE" />
    </xsl:template>
    
    <xsl:template match="DESCRIPTION">#<xsl:call-template name="SubstringReplace">
            <xsl:with-param name="stringIn" select="."/>
            <xsl:with-param name="substringIn" select="'&#10;'"/>
            <xsl:with-param name="substringOut" select="'&#10;#'"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="VOTABLE">
        <!-- Print description -->
        <xsl:apply-templates select="./DESCRIPTION"/>
# FORMAT = CUSTOM
<xsl:for-each select="./RESOURCE/TABLE">
        <!-- Print fields name as header -->
<xsl:for-each select="./FIELD">
    <xsl:value-of select="./@name"/>
<xsl:value-of select="'&#9;'"/>
</xsl:for-each>


        <!-- Print data -->
<xsl:for-each select="./DATA/TABLEDATA">
<xsl:for-each select="./TR">
<xsl:value-of select="'&#10;'"/>
<xsl:for-each select="./TD">
<xsl:value-of select="."/>
<xsl:value-of select="'&#9;'"/>
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
