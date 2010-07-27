#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: sclguiVOTableToHTML.sh,v 1.1 2009-11-05 14:16:28 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.1  2009/10/26 13:54:58  mella
# Add new utility to transform a searchcal votable to html
#
#*******************************************************************************

#/**
# @file
# This command output one copy of the given SearchCal votable into html on
# stdout.
#
# @synopsis
# sclguiVOTableToHtml \<votableFilename\>
#
# @param votableFilename : filename of votable to be read and modified
#
# @details
# The column values will be the param one.
# 
# @usedfiles
# @filename ../config/some.xsl :  xslt file
#
# */

# signal trap (if any)

# one argument must be given
if [ $# != 1 ]
then
    echo "Usage: $0 votable.xml"
    exit 1
fi
votableFile="$1"

XSL="$(basename $0 .sh)".xsl

XSLT=$(miscLocateFile $XSL)
if [ $? -ne 0 ]
then
    echo "ERROR: Can't find '$XSL'!" >&2
    exit 1
fi


# proceed to real job
xsltproc $XSLT "$votableFile"

#___oOo___
