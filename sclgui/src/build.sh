#!/bin/bash

# You should only have to define properly two next lines
MAINCLASS=fr/jmmc/scalib/sclgui/SearchCalibrators
ARGS="-v 5"
ARGS="-v 0"


all(){
    clean;
    wsdl2java;
    compile;
    run;
}

clean(){
    find  . -name "*.class" -exec rm {} \;
}

wsdl2java(){
    INPUTPATHES="../../sclws/include/sclws.wsdl"
    ## We don't need SIMBAD wsdl for the moment (DO NOT WORK FROM HERE)
    # INPUTPATHES="../../sclws/include/sclws.wsdl http://simweb.u-strasbg.fr/axis/services/WSQuery?wsdl"
    OUTPUTPATH="./"

	for INPUTPATH in $INPUTPATHES
	do
       CMD="java -classpath $CLASSPATH org.apache.axis.wsdl.WSDL2Java $INPUTPATH -o $OUTPUTPATH"
       echo $CMD
 	   $CMD
    done
}

compile(){
	CMD="javac fr/jmmc/scalib/sclgui/OSXAdapter.java"
    echo $CMD
    $CMD

	CMD="javac $MAINCLASS.java"
    echo $CMD
    $CMD
}

run(){
	CMD="java -classpath $CLASSPATH $MAINCLASS $ARGS"
    echo $CMD
    $CMD
}

if [ -z "$1" ]
then
    echo "usage: $0 [compile|run|clean]"
	clean
    compile
	exit
fi

# define classpath
CP=.
for j in ../lib/*.jar ../../../../INTROOT/lib/*.jar
do
  CP=$CP:$j
done
export CLASSPATH=$CP
echo CLASSPATH is $CLASSPATH
echo

for cmd in $*
do
    $cmd
done

