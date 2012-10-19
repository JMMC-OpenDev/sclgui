<?xml version="1.0" encoding="UTF-8" ?>
<!--
********************************************************************************
* JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
********************************************************************************
DESCRIPTION
- transform VOTable into CSV files
- accept external parameter to specify field separator (fieldSeparator)
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:VOT11="http://www.ivoa.net/xml/VOTable/v1.1"
                xmlns:exslt="http://exslt.org/common"
                extension-element-prefixes="exslt">

    <xsl:output method="text" encoding="UTF-8" />

    <xsl:param name="fieldSeparator">,</xsl:param>
   		
    <xsl:template match="/">
        <xsl:apply-templates select="VOT11:VOTABLE" />        
    </xsl:template>

    <!-- Read line separated description and output them prefixed with dash --> 
    <xsl:template match="VOT11:DESCRIPTION">#  
        <xsl:call-template name="SubstringReplace">
            <xsl:with-param name="stringIn" select="."/>
            <xsl:with-param name="substringIn" select="'&#10;'"/>
            <xsl:with-param name="substringOut" select="'&#10;#'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="VOT11:VOTABLE">
        <xsl:text>#  This file was generated using Java SearchCal program&#10;</xsl:text>

        <!-- Print description -->
        <xsl:apply-templates select="VOT11:DESCRIPTION"/>
        <xsl:value-of select="'&#10;'"/>
        <xsl:for-each select="VOT11:RESOURCE/VOT11:TABLE">
            <!-- Print fields name as header -->
            <xsl:for-each select="VOT11:FIELD[not(@type='hidden')]">
                <xsl:value-of select="./@name"/>
                <xsl:value-of select="$fieldSeparator"/>
            </xsl:for-each>
            <!-- store indices for future output of TD linked to non hidden fields -->
            <xsl:variable name="indices">
                <xsl:for-each select="VOT11:FIELD">
                    <xsl:if test="not(@type='hidden')">
                        <indice> 
                            <xsl:value-of select="position()"/> 
                        </indice>
                    </xsl:if>
                </xsl:for-each>
            </xsl:variable>
            <xsl:variable name="indiceNodes" select="exslt:node-set($indices)/indice" />

            <!-- Print data -->            
            <xsl:for-each select="VOT11:DATA/VOT11:TABLEDATA/VOT11:TR">
                <xsl:variable name="row" select="."/>                    
                <xsl:value-of select="'&#10;'"/>
                <xsl:for-each select="$indiceNodes">
                    <xsl:value-of select="$row/VOT11:TD[number($index)]"/>
                    <xsl:value-of select="$fieldSeparator"/>
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
