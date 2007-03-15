#!/bin/bash

# You should only have to define properly two next lines
MAINCLASS=fr/jmmc/scalib/sclgui/SearchCalibrators
ARGS="-v 5"
ARGS="-v 0"


wsdl2java(){
    INPUTPATH="../../sclws/include/sclws.wsdl"
    OUTPUTPATH="./"
	CMD="java -classpath $CLASSPATH org.apache.axis.wsdl.WSDL2Java $INPUTPATH -o $OUTPUTPATH"
	#CMD="java -classpath $CLASSPATH org.apache.axis.wsdl.WSDL2Java -h"
    echo $CMD
    $CMD
}

compile(){
	CMD="javac $MAINCLASS.java"
    echo $CMD
    $CMD
}

run(){
	CMD="java -classpath $CLASSPATH $MAINCLASS $ARGS"
    echo $CMD
    $CMD
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

# define classpath
CP=.
for j in ../lib/*.jar ../../../../INTROOT/lib/*.jar
do
  CP=$CP:$j
done
export CLASSPATH=$CP
echo CLASSPATH is $CLASSPATH


for cmd in $*
do
    $cmd
done
