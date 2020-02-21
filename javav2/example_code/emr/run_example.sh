#!/bin/bash

mvn exec:java -Dexec.mainClass="aws.example.emr.CreateEmrFleet" -Dexec.cleanupDaemonThreads=false