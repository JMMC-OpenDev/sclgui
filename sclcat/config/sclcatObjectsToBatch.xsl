<?xml version="1.0"?>
<!--
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: sclcatObjectsToBatch.xsl,v 1.7 2009-10-08 19:32:41 mella Exp $"
#
# History
#
# $Log: not supported by cvs2svn $
# Revision 1.6  2009/10/08 19:13:44  mella
# add support for kmag
#
# Revision 1.5  2007/03/27 14:53:42  scetre
# Updated documentation
# Added creation information in resulting file
#
# Revision 1.4  2007/03/26 11:53:03  scetre
# Moved documentation blok in the first xml tag
#
# Revision 1.3  2007/03/26 11:34:13  scetre
# Added header documentation
#
#
#*******************************************************************************

##
# Create a file containing one block per star. In each blok are presented the
# Simbad name, the current object name, its coordinates and its proper motion
#
# This script take as parameter an xml file where are stored the different names
# of the stars. for more information on the file format, see sclcatObjectsToBatch.xsl which generates it.
#
# To provide the results file, it uses the sclcatSimbadList.xml where are store
# ifnformations about stars.
#
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exslt="http://exslt.org/common"
    xmlns:math="http://exslt.org/math"
    xmlns:date="http://exslt.org/dates-and-times"
    xmlns:func="http://exslt.org/functions"
    xmlns:set="http://exslt.org/sets"
    xmlns:str="http://exslt.org/strings"
    xmlns:dyn="http://exslt.org/dynamic"
    xmlns:saxon="http://icl.com/saxon"
    xmlns:xalanredirect="org.apache.xalan.xslt.extensions.Redirect"
    xmlns:xt="http://www.jclark.com/xt"
    xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
    xmlns:test="http://xmlsoft.org/XSLT/"
    extension-element-prefixes="exslt math date func set str dyn saxon xalanredirect xt libxslt test"
    exclude-result-prefixes="math str">
    <xsl:output method="text" />

    <xsl:param name="mainFile">../config/sclcatSimbadList.xml</xsl:param>

    <xsl:template match="/">
        <xsl:for-each select="//star">
            <xsl:variable name="simbadName" select="./simbadName"/>
            <xsl:variable name="object" select="document($mainFile)//object[name=$simbadName]"/>

            <xsl:if test="not($object/ra and $object/dec)">
                <xsl:message>'<xsl:value-of select="$simbadName"/>' has no ra and dec </xsl:message>
            </xsl:if>
            <xsl:if test="$object/ra and $object/dec">
                <xsl:value-of select="'['"/>
                <xsl:value-of select="$simbadName"/>
                <xsl:value-of select="']'"/>
                <xsl:value-of select="'&#10;'"/>

                <xsl:value-of select="'objectName='"/>
                <xsl:value-of select="$simbadName"/>
                <xsl:value-of select="'&#10;'"/>

                <xsl:value-of select="'ra='"/>
                <xsl:value-of select="$object/ra"/>
                <xsl:value-of select="'&#10;'"/>

                <xsl:value-of select="'dec='"/>
                <xsl:value-of select="$object/dec"/>
                <xsl:value-of select="'&#10;'"/>

                <xsl:value-of select="'pmra='"/>
                <xsl:value-of select="$object/pmra"/>
                <xsl:value-of select="'&#10;'"/>

                <xsl:value-of select="'pmdec='"/>
                <xsl:value-of select="$object/pmdec"/>
                <xsl:value-of select="'&#10;'"/>
    
                    <xsl:value-of select="'mag='"/>
		    <xsl:if test="$object/kmag">
			    <xsl:value-of select="$object/kmag"/>
		    </xsl:if>
		    <xsl:if test="not($object/kmag)">
			    <xsl:value-of select="'0'"/>
		    </xsl:if>
                    <xsl:value-of select="'&#10;'"/>
 
                <xsl:value-of select="'&#10;'"/>
            </xsl:if>
            
            </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
