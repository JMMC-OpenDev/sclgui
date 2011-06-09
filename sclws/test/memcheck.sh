#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************
source env.sh

VG_LOG=./vg_memcheck.log

rm $VG_LOG
touch $VG_LOG

# valgrind memcheck options:  --show-reachable=yes --track-origins=yes
# --gen-suppressions=all
valgrind -v --num-callers=12 --freelist-vol=500000000 --suppressions=./custom_suppressions.txt --log-file=$VG_LOG --leak-check=full sclwsServer -v $VERBOSITY &

# Remember server PID for later kill
VG_PID=$!
echo "valgrind started: $VG_PID"

# Wait for server bind
sleep 1

# queries (N, V, ...):
./testBright.sh

# Wait for valgrind overhead
sleep 3 

# kill server to get valgrind report
echo -n "valgrind stopping ..."
kill $VG_PID 
echo "done."

# wait for valgrind shutdown hook ...
sleep 3 

# Remove PID from output log
sed -i "s/^==[0123456789]*==//g" $VG_LOG
sed -i "s/^--[0123456789]*--//g" $VG_LOG
echo "Output: $VG_LOG"