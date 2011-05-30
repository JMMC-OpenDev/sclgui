#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************
source env.sh

VG_LOG=./vg_threadcheck.log

rm $VG_LOG
touch $VG_LOG

# valgrind helgrind options:  --show-reachable=yes --track-origins=yes
valgrind -v --tool=helgrind --conflict-cache-size=10000000 --read-var-info=yes --log-file=$VG_LOG  sclwsServer -v 1 &

# Remember server PID for later kill
VG_PID=$!
echo "valgrind started: $VG_PID"

# Wait for server bind
sleep 3 

# queries (N, V, ...):
./testBright.sh

# Wait for valgrind overhead
sleep 3

# kill server to get valgrind report
echo -n "valgrind stopping ..."
kill $VG_PID 
echo "done."

# wait for valgrind shutdown hook ...
sleep 2 

# Remove PID from output log
sed -i "s/^==[0123456789]*==//g" $VG_LOG
sed -i "s/^--[0123456789]*--//g" $VG_LOG
echo "Output: $VG_LOG"
