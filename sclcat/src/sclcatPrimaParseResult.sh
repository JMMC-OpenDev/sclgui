#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: sclcatPrimaParseResult.sh,v 1.5 2007-03-27 14:55:56 scetre Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.4  2007/02/07 14:57:23  mella
# Set calPmRa or calPmDec to 0 for filter process call
#
# Revision 1.3  2007/01/31 12:39:54  mella
# Use pmra and pmDec to 0 if star has not yet for filter program
#
# Revision 1.2  2007/01/31 10:25:31  mella
# Change bad test condition
#
# Revision 1.1  2007/01/31 10:01:00  mella
# First revision
#
#
#*******************************************************************************

#/**
# @file
# Create synthesis files for the PRIMA calibrator catalog
#
# @synopsis
# sclcatPrimaParseResult \<run-directory\>
# 
# @details
# This script build the synthesis file from the collected calibrator lists.
#
# @usedfiles
# @filename ../config/sclcatPRIMA.cfg : list of stars of interest for PRIMA
# @filename ../config/sclcatVOTable2html.xsl : xsl style sheet to convert VOTable to html
# @filename ../config/sclcatObjectsToHtml.xsl :  xsl style sheet to convert object file to html
# @filename ../config/sclcatSimbadList.xml : Simbad return properties for each stars (ra, dec, pmra, pmdec, parallax).
# @filename ../config/sclcatPrimaStars.xml : Stars list
#
# */

# Print usage 
function printUsage () {
    echo -e "Usage: sclcatPrimaParseResult [prima-ref] or [prima-run-<YYYY-MM-DDTHH-MM-SS>]" 
    echo -e "\t-h\t\tprint this help."
    echo -e "\t<prima-run>\tParse results in prima-run directory"
    exit 1;
}

while getopts "h" option
# Initial declaration.
# c, h, u and t are the options (flags) expected.
# The : after option 't' shows it will have an argument passed with it.
do
  case $option in
    h ) # Help option
        printUsage ;;
    * ) # Unknown option
        printUsage ;;
    esac
done

XSLT_VOT2HTML="$PWD/../config/sclcatVOTable2html.xsl"
XSLT_OBJECT2HTML="$PWD/../config/sclcatObjectsToHtml.xsl"
# Next file contains all grabed data
MAIN_LIST_FILE="$PWD/../config/sclcatSimbadList.xml"
PRIMA_STAR_LIST="$PWD/../config/sclcatPrimaStars.xml"

# define temporary PATH # change it if the script becomes extern
PATH=$PATH:$PWD/../bin

# define timespan to use 
timespan=20
echo "Timespan is $timespan [yr]"

# Parse command-line parameters
dir=$1
if [ ! -d "$dir" ]
then
    echo "Please give one run directory"
    exit 1
fi

cd $dir

HTMLDIR="html/"
mkdir $HTMLDIR &> /dev/null

# copy every votable and transform it into html
for i in *.vot
do
    if [ ! -e "$HTMLDIR/$i" ]
    then
        cp "$i" $HTMLDIR
        xsltproc --path .:.. -o "$HTMLDIR/$i.html" $XSLT_VOT2HTML "$i"
    fi
done

cd $HTMLDIR

CALIBRATORS=calibrators.xml
echo "<calibrators>" > $CALIBRATORS

# loop on every calibrators of every stars
# and build calibrator file
for i in *.vot
do
    if [ -f "$i" ]
    then
        starName=${i%.vot}
        starRa=$(xml sel -t -v "//object[name='$starName']/ra" $MAIN_LIST_FILE)
        starPmRa=$(xml sel -t -v "//object[name='$starName']/pmra" $MAIN_LIST_FILE)
        starDec=$(xml sel -t -v "//object[name='$starName']/dec" $MAIN_LIST_FILE)
        starPmDec=$(xml sel -t -v "//object[name='$starName']/pmdec" $MAIN_LIST_FILE)
        
        # if pmra or pmdec not found set it to 0
        if [ -z "$starPmRa" ]
        then
            starPmRa=0
        fi
        if [ -z "$starPmDec" ]
        then
            starPmDec=0
        fi
        
        echo -n "$starName  "
        xml sel   -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t \
        -v "count(//VOT:TR[./VOT:TD[268]='OK'])" -o "/" \
        -v "count(//VOT:TR)" -n "$i" 

        NBCAL=$(xml sel   -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t \
        -v "count(//VOT:TR)" "$i")

        # output copy of calibrator
        INDICES=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t \
        -m "//VOT:TR" -i "./VOT:TD[268]='OK'" \
        -v "position()" -n "$i")

        echo "<star simbadName=\"$starName\" scCount=\"$NBCAL\">" >>  $CALIBRATORS
        for index in $INDICES
        do
            calName=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -v "//VOT:TR[$index]/VOT:TD[22]" "$i")
            calMagK=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -v "//VOT:TR[$index]/VOT:TD[157]" "$i")
            calRa=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -v "//VOT:TR[$index]/VOT:TD[25]" "$i")
            calDec=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -v "//VOT:TR[$index]/VOT:TD[28]" "$i")
            calPmRa=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -v "//VOT:TR[$index]/VOT:TD[43]" "$i")
            calPmDec=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -v "//VOT:TR[$index]/VOT:TD[40]" "$i")
            calDist=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -v "//VOT:TR[$index]/VOT:TD[322]" "$i")
            echo "  <calibrator index=\"$index\">" >>  $CALIBRATORS
            echo "    <name>$calName</name>" >>  $CALIBRATORS
            echo "    <magK>$calMagK</magK>" >>  $CALIBRATORS
            echo "    <ra>$calRa</ra><dec>$calDec</dec>" >>  $CALIBRATORS
            echo "    <pmra>$calPmRa</pmra><pmdec>$calPmDec</pmdec>" >>  $CALIBRATORS
            echo "    <dist>$calDist</dist>" >>  $CALIBRATORS
            # if pmra or pmdec not found set it to 0
            if [ -z "$calPmRa" ]
            then
                calPmRa=0
            fi
            if [ -z "$calPmDec" ]
            then
                calPmDec=0
            fi

            # prep command
            FILTERINFO=$(sclcatPrimaFilter "$starRa" "$starDec" "$starPmRa" \
            "$starPmDec" "$calRa" "$calDec" "$calPmRa" "$calPmDec" "$timespan")
            if [ $? -ne 0 ]
            then
                echo "ERROR occured for calib n� $index"
                echo "<calibInfo><error/></calibInfo>" >> $CALIBRATORS
            else
               echo $FILTERINFO >> $CALIBRATORS
            fi
            echo "<!-- \ 
            sclcatPrimaFilter \"$starRa\" \"$starDec\" \"$starPmRa\" \
            \"$starPmDec\" \"$calRa\" \"$calDec\" \"$calPmRa\" \"$calPmDec\" \"$timespan\" \
            -->" >> $CALIBRATORS

            echo "  </calibrator>" >>  $CALIBRATORS

        done
        echo "</star>" >>  $CALIBRATORS  
    fi
done
echo "</calibrators>" >> $CALIBRATORS


#Now CALIBRATORS file can be presented by next stylesheet
echo "Html resume generated"
# generate the html view
xsltproc  --path ./html:.:.. -o index.html --stringparam calibratorsFilename \
          $CALIBRATORS --stringparam mainFilename $MAIN_LIST_FILE \
          $XSLT_OBJECT2HTML $PRIMA_STAR_LIST

#___oOo___