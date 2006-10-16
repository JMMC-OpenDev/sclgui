#!/bin/bash

export CLASSPATH=../lib/kxml2-min.jar:../lib/cds.savot.writer.jar:../lib/cds.savot.sax.jar:../lib/cds.savot.pull.jar:../lib/cds.savot.model.jar:../lib/cds.savot.common.jar:.:../lib/Aladin.jar:../lib/java-getopt-1.0.13.jar


compile(){
	javac jmmc/scalib/sclgui/SearchCalibrators.java
}

run(){
	java -classpath $CLASSPATH jmmc.scalib.sclgui.SearchCalibrators -v 5
}

clean(){
    find  . -name "*.class" -exec rm {} \;
}

if [ -z "$1" ]
then
    echo "usage: $0 [compile|run|clean]"
	clean
    compile
	exit
fi

for cmd in $*
do
    $cmd
done
