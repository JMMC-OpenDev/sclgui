#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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

# SIMBAD_LIST_FILE is a local cache of simbad
SIMBAD_LIST_FILE=../config/sclcatSimbadList.xml
# Ensure that simbad list file exist 
if [ ! -e "$SIMBAD_LIST_FILE" ]
then
    echo "Creating new main list file '$SIMBAD_LIST_FILE'"
    echo "<objects>" >> $SIMBAD_LIST_FILE
    echo "</objects>" >> $SIMBAD_LIST_FILE
fi

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
    simbadWS.py "$sourceName" "%COO(A|D)|%PM(A|D)|%PLX(V)|%FLUXLIST(K;F)"  2> /dev/null
    return $?
}

#/**
# Ask source information to simbad or local cache file
# */
askSourceInfo(){
    sourceName="$*" 
    simbadName="$*" 

    i=$(xml sel -t -v "count(//object[name='$sourceName'])" $SIMBAD_LIST_FILE )
    if [ "$i" -gt 0 ]
    then
        #echo "'$sourceName' already defined"
        return
    fi
    
    if res=$(getSimbadInfos "$simbadName")
    then
        if [ "No Coord." = "${res:0:9}" ]
        then
            echo "'$sourceName' exists but not coord returned"
            return
        fi

        tmp=($(wc -l "$SIMBAD_LIST_FILE"))
        let idx=${tmp[0]}-1
        BEGIN_CONTENT=$(head -$idx "$SIMBAD_LIST_FILE")
        END_CONTENT=$(tail -1 "$SIMBAD_LIST_FILE")
        #
        # build file list again:
        echo "$BEGIN_CONTENT" > $SIMBAD_LIST_FILE

        echo "<!-- add on $(LC_ALL=C date) -->" >> $SIMBAD_LIST_FILE
        echo "'$simbadName' : $res"
        oldIFS="$IFS"
        IFS='|'
        infos=( $res )
        IFS="$oldIFS"
        echo "<object>" >> $SIMBAD_LIST_FILE
        fillElement "name" "$sourceName" >> $SIMBAD_LIST_FILE
        fillElement "ra" ${infos[0]} >> $SIMBAD_LIST_FILE
        fillElement "dec" ${infos[1]} >> $SIMBAD_LIST_FILE
        fillElement "pmra" ${infos[2]} >> $SIMBAD_LIST_FILE
        fillElement "pmdec" ${infos[3]} >> $SIMBAD_LIST_FILE
        fillElement "plx" ${infos[4]} >> $SIMBAD_LIST_FILE
        fillElement "kmag" ${infos[5]} >> $SIMBAD_LIST_FILE
        echo "</object>" >> $SIMBAD_LIST_FILE

        # end with same previous end
        echo "$END_CONTENT" >> $SIMBAD_LIST_FILE
   else
            GUESS="NAME $simbadName"
            if res=$(getSimbadInfos "$GUESS")
            then
                echo "<object name='$simbadName' alias='$GUESS' />"
            else
                GUESS="NAME ${simbadName/-/-Exo-}"
                if res=$(getSimbadInfos "$GUESS")
                then
                    echo "<object name='$simbadName' alias='$GUESS' />"
                else
                    echo "'$sourceName' not found by simbad"
                fi
            fi
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
    EXOPLANET_VOTABLE_FILE=../config/sclcatPrimaExoplanetData.xml
    EXOPLANET_VOTABLE_URL="http://www.exoplanet.eu/export.php?all=yes&outputType=votable"
    curl $EXOPLANET_VOTABLE_URL > $EXOPLANET_VOTABLE_FILE
    xsltproc --novalid -o $OUTPUT ../config/sclcatBuildMainList.xsl $EXOPLANET_VOTABLE_FILE || exit 1
    # next collection should be added here ...
    
    echo "   * Exoplanet list exported "
    xml sel -t -o "Number of stars : " -v "count(//star)" -n $OUTPUT
}


# Starting point

# Simbad aliases name file
SIMBAD_ALIASES_FILE=../config/sclcatAliases.xml
echo "Using aliases file for simbad queries '$SIMBAD_ALIASES_FILE'"
echo "Aliases: "
echo "$(xml sel -t -m "//object" -o "'" -v "./@name" -o "':'" -v "./@alias" -o "', "   $SIMBAD_ALIASES_FILE ) "
# next vars will tell user action to achieve much moe results
info=""
error=""

echo 
echo "Next step collects star and stores them in our own format"
echo

XML_STAR_LIST=../config/sclcatPrimaStars.xml
collectCandidates $XML_STAR_LIST

MICROLENSING_STAR_LIST=../config/sclcatMicrolensingStars.xml

STAR_TO_REJECT=$(xml sel -N VOT=http://www.ivoa.net/xml/VOTable/v1.1 -t -m "//VOT:TR/VOT:TD[1]|//TR/TD[1]" -v "." -n $MICROLENSING_STAR_LIST)
echo 
echo "Next step rejects following stars:"
echo "$STAR_TO_REJECT"
echo
echo "$STAR_TO_REJECT" | while read s 
do 
    xml ed -d "//star[./name='$s']" $XML_STAR_LIST > $XML_STAR_LIST.tmp
    mv $XML_STAR_LIST.tmp $XML_STAR_LIST
    # we should assert that the file has shrunk
done


# create one file with one star per line to loop over
echo 
echo "Building star list ( $(xml sel -t -v 'count(//star)' $XML_STAR_LIST) sources )"
echo
STAR_LIST="$(xml sel -t -m "//star" -v "./simbadName" -n $XML_STAR_LIST)"
# check every stars 
echo "$STAR_LIST" | while read starName
do
    if [ -n "$starName" ]
    then
        askSourceInfo "$starName"
    fi
done

# Generate batch file
generateBatch
