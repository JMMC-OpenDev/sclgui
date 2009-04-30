#!/usr/bin/env python
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: sclguiTestCSVColumns.sh,v 1.2 2008-02-13 14:03:09 lafrasse Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.1  2008/01/28 14:06:29  lafrasse
# Creation
#
#*******************************************************************************

#/**
# @file
# This program read a CSV file, and try to output the values of the given column
# names.
#
# @synopsis
# \<sclguiTestCSVColumns\> [\e \<FileName.csv\> \e \<ColumnName\> ...]
#
# @param FileName.csv : the name of the CSV file to read
# @param ColumnName : one (or more) column name(s) to search inside the CSV file
# 
# @details
# Any line shorter than one char or beginiing with a '#' will be skipped.
# All columns values will be outputed if no one has beenspecified by the user.
# 
# @ex
# @code
# sclguiTestCSVColumns ETA_TAU.csv dist vis2 vis2Err
# @endcode
#
# */

import sys
import os

try :
    fileName = sys.argv[1]
    searchedColumns = sys.argv[2:]
except:
    sys.stderr.write("Please give one filename and (at least) one column name as argument.\n")
    sys.exit(1)

file = open(fileName, "r")
columnNames = []
searchedColumnIndexes = []
numberOfLines = 0
separator = ","
# For each line of the CSV file
for currentLine in file:
    # If the current line length is less than 2 char
    if len(currentLine) < 2:
        # Skip the current line
        pass
    # If the current line is a comment
    elif currentLine.startswith("#"):
        # Skip the current line
        pass
    # If the column name list has not been built yet
    elif len(columnNames) == 0:
        columnNames = currentLine.split(separator)
        print "Found %d columns."%(len(columnNames))
        # For each searched column
        for currentColumnName in searchedColumns:
            # Find its index
            if currentColumnName in columnNames:
                index = columnNames.index(currentColumnName)
                searchedColumnIndexes.append(index)
                print "Column '%s' found at index %d."%(currentColumnName, index)
            else:
                print "Column '%s' NOT found."%(currentColumnName)
        # Add all columns if none specified by the user
        if len(searchedColumnIndexes) == 0:
            searchedColumnIndexes = range(len(columnNames))
    # Output each column values
    else:
        numberOfLines += 1
        lineValues = currentLine.split(separator)
        outputBuffer = ""
        # For each column indexes searched
        for currentColumnIndex in searchedColumnIndexes:
            outputBuffer += "%s = '%s';\t"%(columnNames[currentColumnIndex], lineValues[currentColumnIndex])
        print "[row %d]: %s"%(numberOfLines, outputBuffer)

#___oOo___
