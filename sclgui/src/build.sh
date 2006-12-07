#!/bin/bash

# You should only have to define properly two next lines
MAINCLASS=jmmc/scalib/sclgui/SearchCalibrators
ARGS="-v 5"



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
# define classpath
CP=.
for j in ../lib/*.jar
do
  CP=$CP:$j
done
export CLASSPATH=$CP
echo CLASSPATH is $CLASSPATH

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
