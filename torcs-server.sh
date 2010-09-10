#!/bin/sh
#
# For some reason the torcs server goes down after each evaluateAll.
# This script checks if the torcs process is running, starting it if 
# necessary.

while true
do
    [ -z "`pgrep torcs`" ] && torcs -T
    sleep .5
done
