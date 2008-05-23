#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: sclcatPrimaGenerateConfig.sh,v 1.4 2007-04-10 08:34:17 scetre Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.3  2007/03/27 14:55:56  scetre
# Updated documentation
#
#
#*******************************************************************************

#/**
# @file
# Generate the configuration file for the PRIMA calibrators research. 
#
# @synopsis
# sclcatPrimaGenerateConfig
# 
# @details
# This script generate the sclcatPRIMA.cfg file which is used for the
# calibrators research. The script parse the file sclcatPrimaExoplanetData.xml
# provided by the web site http://exoplanet.eu. If sclcatPrimaExoplanetData.xml has been updated (new files in the catalog),
# the script try to find information in simbad catalog. If stars not found a
# message is displayed to list the not found stars ("Here comes the not found list: ...").
#
# @usedfiles
# @filename <b>../config/sclcatPrimaHeader.cfg :</b> SearchCal parameters file description which is the future header of the sclcatPRIMA.cfg file
# @filename <b>../tmp/tailBatch.cfg :</b> temporary file for calling SearchCal by batch for several objects. It is the body of the sclcatPRIMA.cfg
# @filename <b>../config/sclcatPRIMA.cfg :</b>  PRIMA configuration file (sclcatPrimaHeader.cfg + ../tmp/tailBatch)
# @filename <b>../config/sclcatSimbadList.xml :</b>  Simbad return properties for each stars (ra, dec, pmra, pmdec, parallax).
# @filename <b>../config/sclcatAliases.xml :</b>  file containing aliases of stars 
# @filename <b>../config/sclcatPrimaStars.xml :</b>  Stars list 
# @filename <b>../config/sclcatObjectsToBatch.xsl :</b>  xsl style sheet to create sclcatPrimaStars.xml
# @filename <b>../config/sclcatBuildMainList.xsl :</b>  xsl style sheet used to created sclcatPrimaStars.xml
# @filename <b>../tmp/command.dat :</b>  temporary file where are store generated body of the final sclcatPRIMA.cfg file
# @filename <b>sclcatPrimaPrepareBatchFile.py :</b>  python script used to prepare batch file before research
#
# */

#/**
# Generate the configuration file
# */
function generateBatch () {
    echo -e "Generate configuration file ..."
    
    headerFile="../config/sclcatPrimaHeader.cfg"
    contentFile="../tmp/command.dat"
    tailFile="../tmp/tailBatch.cfg"
    starListFile="$XML_STAR_LIST"
    sclcatPRIMA="../config/sclcatPRIMA.cfg"
    
    # build first batch file using collected informations on stars
    xsltproc -o $contentFile ../config/sclcatObjectsToBatch.xsl $starListFile

    # add final note (set radius, set filename, ...)
    sclcatPrimaPrepareBatchFile $contentFile -o $tailFile
    
    # Write date of creation
    echo "# File generated by sclcatPrimaGenerateConfig.sh" > $sclcatPRIMA
    echo "# at $(date)" >> $sclcatPRIMA
    # Cat files
    cat $headerFile $tailFile >> $sclcatPRIMA 
    echo "Final batch file generated into '$sclcatPRIMA'"
    
    echo -e "...Done."
}

#/**
# Fill element value (ra, dec, pmra, pmdec, plx)
# */
fillElement(){
    elName=$1
    shift 1
    elValue=$*

    if [ "$elValue" = '~' ]
    then
        echo "<!-- simbad returns '$elValue' for $elName -->"
    elif [ -z "$elValue" ] 
    then
        echo "<!-- simbad returns no value for $elName -->"
    else
        echo "<$elName>$elValue</$elName>"
    fi
}

#/**
# Get Simbad info about a stars
#
# It run the simbadWS.py script on the source name
# */
getSimbadInfos(){
    sourceName=$*
    simbadWS.py "$sourceName" "%COO(A|D)|%PM(A|D)|%PLX(V)" 
    return $?
}

#/**
# Get source information
# */
askSourceInfo(){
    sourceName="$*" 
    simbadName="$*" 
    if res=$(getSimbadInfos "$simbadName")
    then
        if [ "No Coord." = "${res:0:9}" ]
        then
            echo "'$sourceName' exists but not coord returned"
            error="$error\n'$sourceName' has no coords"
            return
        fi

        tmp=($(wc -l "$MAIN_LIST_FILE"))
        let idx=${tmp[0]}-1
        BEGIN_CONTENT=$(head -$idx "$MAIN_LIST_FILE")
        END_CONTENT=$(tail -1 "$MAIN_LIST_FILE")
        #
        # build file list again:
        echo "$BEGIN_CONTENT" > $MAIN_LIST_FILE

        echo "<!-- add on $(LC_ALL=C date) -->" >> $MAIN_LIST_FILE
        echo "'$simbadName' : $res"
        oldIFS="$IFS"
        IFS='|'
        infos=( $res )
        IFS="$oldIFS"
        echo "<object>" >> $MAIN_LIST_FILE
        fillElement "name" "$sourceName" >> $MAIN_LIST_FILE
        fillElement "ra" ${infos[0]} >> $MAIN_LIST_FILE
        fillElement "dec" ${infos[1]} >> $MAIN_LIST_FILE
        fillElement "pmra" ${infos[2]} >> $MAIN_LIST_FILE
        fillElement "pmdec" ${infos[3]} >> $MAIN_LIST_FILE
        fillElement "plx" ${infos[4]} >> $MAIN_LIST_FILE
        echo "</object>" >> $MAIN_LIST_FILE

        # end with same previous end
        echo "$END_CONTENT" >> $MAIN_LIST_FILE
   else
            echo "'$sourceName' not known by simbad"
            error="$error\n'$sourceName' not found"
    fi
}

#/**
# Check source
# */
checkSource(){
    sourceName="$1"
    i=$(xml sel -t -v "count(//object[name='$sourceName'])" $MAIN_LIST_FILE )
    if [ "$i" -gt 0 ]
    then
        echo "'$sourceName' already defined"
    else
        echo "Trying to define '$sourceName' object "
        askSourceInfo "$sourceName"
    fi
}

#/**
# Collect candidates
# */
collectCandidates(){
    OUTPUT=$1
    echo "Creating main list file '$OUTPUT'"

    # get collection from exoplanet 
    # novalid is used because dtd is not reachable...
    xsltproc --novalid -o $OUTPUT ../config/sclcatBuildMainList.xsl ../config/sclcatPrimaExoplanetData.xml

    # next collection should be added here ...
    
    echo "   * Exoplanet list exported "
}


# Next file contains all grabed data
MAIN_LIST_FILE=../config/sclcatSimbadList.xml
if [ ! -e "$MAIN_LIST_FILE" ]
then
    echo "Creating new main list file '$MAIN_LIST_FILE'"
    echo "<objects>" >> $MAIN_LIST_FILE
    echo "</objects>" >> $MAIN_LIST_FILE
fi

# Simbad aliases name file
SIMBAD_ALIASES_FILE=../config/sclcatAliases.xml
echo "Using aliases file for simbad queries '$SIMBAD_ALIASES_FILE'"
echo "Aliases: "
echo "$(xml sel -t -m "//object" -o "  *" -v "./@name" -o "   :    " -v "./@alias" -n  $SIMBAD_ALIASES_FILE ) "
# next vars will tell user action to achieve much moe results
info=""
error=""


# XML_STAR_LIST stores final list of star in our own format
XML_STAR_LIST=../config/sclcatPrimaStars.xml
collectCandidates $XML_STAR_LIST

#list star to remove eg microlensing...
STAR_TO_REJECT="OGLE235-MOA53 OGLE-05-071L OGLE-05-169L OGLE-05-390L"
#STAR_TO_REJECT="OGLE235-MOA53 OGLE-05-390L"
for s in $STAR_TO_REJECT
do 
    echo rejecting $s from prima list
    xml ed -d "//star[./name='$s']" $XML_STAR_LIST > $XML_STAR_LIST.tmp
    mv $XML_STAR_LIST.tmp $XML_STAR_LIST
    echo mv $XML_STAR_LIST.tmp $XML_STAR_LIST
    
done

# create one file with one star per line to loop over
TMPFILE=../tmp/stars.txt
xml sel -t -m "//star" -v "./simbadName" -i "position()!=last()" -n $XML_STAR_LIST > $TMPFILE

echo "Building star list ( $(xml sel -t -v 'count(//star)' $XML_STAR_LIST) sources )"

# check every stars 
while read starName
do
    if [ -n "$starName" ]
    then
        checkSource "$starName"
    fi
done < $TMPFILE

# print information collected during checkSource calls
if [ -n "$info" ]
then
    echo "Please append next lines to alias file and run script again:"
    echo -e $info
fi
echo "!!!!!!!!!!!!!! Here comes the not found list: !!!!!!!!!!!!!!"
echo -e $error

# Generate batch file
generateBatch


