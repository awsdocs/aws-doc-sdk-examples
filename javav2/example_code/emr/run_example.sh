#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

mvn exec:java -Dexec.mainClass="aws.example.emr.CreateEmrFleet" -Dexec.cleanupDaemonThreads=false