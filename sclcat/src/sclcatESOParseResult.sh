#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: sclcatESOParseResult.sh,v 1.9 2009-04-24 12:03:11 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.8  2008/12/01 10:45:14  lafrasse
# Corrected vot list bug.
# Enhanced output format.
#
# Revision 1.7  2008/11/26 10:25:52  lafrasse
# Corrected 'no VOTable found' case.
#
# Revision 1.6  2008/11/04 09:39:56  lafrasse
# Handled case where no VOTable were found.
#
# Revision 1.5  2008/07/23 23:06:23  lafrasse
# Added more detailled progression monitoring.
#
# Revision 1.4  2008/07/23 22:32:53  lafrasse
# Changed calibrator couting method.
#
# Revision 1.3  2008/07/21 15:45:33  lafrasse
# Corrected way to list vot files to handle large collections to remove "argument
# list too long" errors.
# Removed unneeded VOtable copying process.
# Changed default path of generated catalog file.
# Added output log to better monitor progression.
# Removed HTML resume generation.
# Disabled calibrator numbering due to memory errors on large catalogs.
#
# Revision 1.2  2008/07/11 15:55:22  lafrasse
# Removed 'Finished' timestamp.
#
# Revision 1.1  2008/07/11 12:56:56  lafrasse
# Added ESO Calibrator catalog generation scripts.
#
#*******************************************************************************

#/**
# @file
# Create synthesis files for the ESO calibrator catalog
#
# @synopsis
# sclcatESOParseResult \<run-directory\>
# 
# @details
# This script build the synthesis file from the collected calibrator lists.
#
# @usedfiles
# @filename ../config/sclcatESO.cfg : list of stars of interest for PRIMA
# */

# Print usage 
function printUsage () {
    echo -e "Usage: sclcatESOParseResult [eso-ref] or [eso-run-<YYYY-MM-DDTHH-MM-SS>]" 
    echo -e "\t-h\t\tprint this help."
    echo -e "\t<eso-run>\tParse results in eso-run directory"
    exit 1;
}

# Command line options parsing
while getopts "h" option
do
  case $option in
    h ) # Help option
        printUsage ;;
    * ) # Unknown option
        printUsage ;;
    esac
done

# Define temporary PATH # change it if the script becomes extern
PATH=$PATH:$PWD/../bin

# Parse command-line parameters
dir=$1
if [ ! -d "$dir" ]
then
    echo "Please give one run directory"
    exit 1
else
    echo "Parsing results from $dir"
fi

# Moving to the right directory
cd $dir
RESULTPATH=result
mkdir $RESULTPATH

# Generating VOTable header
firstVotFile="$(xml ls |xml sel -t -v "//f[1]/@n")"

RESULTFILE=$RESULTPATH/catalog.vot

# Generating VOTable header
echo -n "Generating result header ... "
cat "$firstVotFile" | awk '{if ($1=="<TR>")end=1;if(end!=1)print;}' &> $RESULTFILE
echo "DONE"

# Loop on every calibrators of every stars, then build calibrator file
nbOfVOTablesDone=0
totalNbOfVOTables=`ls -l | grep ".vot"| wc | awk '{print $1}'`
totalNbOfCalibrators=0
for file in *.vot
do
    # Increment counter
    let "nbOfVOTablesDone += 1"

    if [ -f "$file" ]
    then
        echo -n "Extracting data from '$file' (${nbOfVOTablesDone} / ${totalNbOfVOTables}) : "

        xml sel  -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -m "/" -c "//VOT:TR" "${file}" >> $RESULTFILE

        nbOfCalibrators=`xml sel  -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -m "/" -v "count(//VOT:TR)" "${file}"`
        let "totalNbOfCalibrators += nbOfCalibrators"

        echo "found '${nbOfCalibrators}' new calibrators for a total of '${totalNbOfCalibrators}' ... DONE."
    else
        echo "ERROR accessing file !"
    fi
done

# Generating VOTable footer
echo -n "Generating result footer ... "
cat "$firstVotFile" | awk '{if ($1=="</TABLEDATA>")start=1;if(start==1)print;}' >> $RESULTFILE
echo "DONE"

#___oOo___
