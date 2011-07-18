#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************
source env.sh

LOG=./crashtest.log

rm $LOG
touch $LOG

$SCLWS_CMD &> $LOG & 

# Remember server PID for later kill
PID=$!
echo "server started: $PID"

# Wait for server bind
sleep 3 

for i in $(seq 100)
do
# queries (N, V, ...):
./testBright.sh 10 &
./testBright.sh 9 &
./testBright.sh 8 &
./testBright.sh 7 &
./testBright.sh 6 &
./testBright.sh 5 &
./testBright.sh 4 &
./testBright.sh 3 &
./testBright.sh 2 &
./testBright.sh 1
done

echo "waiting for processes (60s) ..."
# Wait for server overhead
sleep 60 

# kill server to stop it properly 
echo -n "server stopping ..."
kill $PID 
echo "done."

# wait for shutdown hook ...
sleep 5 

echo "Output: $LOG"