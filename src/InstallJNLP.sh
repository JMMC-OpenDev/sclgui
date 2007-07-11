#*******************************************************************************
# JMMC project
#
# "@(#) $Id: InstallJNLP.sh,v 1.2 2007-07-11 16:09:45 lafrasse Exp $"
#
# History
#
# $Log: not supported by cvs2svn $
# Revision 1.1  2007/07/11 15:59:13  lafrasse
# Added automatic JNLP and Jar files generation and installation.
#
#
#*******************************************************************************

#*******************************************************************************
# This script aims to build a directory with all required jar to make it ready
# to be used calling applet.php
#*******************************************************************************
cd SearchCal

echo "Cleaning jar directory..."
rm -rf jar/*.jar

read -s -p "Enter 'mykey' password to sign every jar files:" passwd
passwdFile=passwd.$$
echo $passwd > $passwdFile

for i in $INTROOT/lib/*.jar ../../lib/*.jar
do 
    echo "Copy and sign $i"
    cp $i jar 
    jarsigner -keystore ../config/keystore jar/$(basename $i) mykey < $passwdFile
done

rm $passwdFile

cp -rf ../SearchCal ~/public_html/