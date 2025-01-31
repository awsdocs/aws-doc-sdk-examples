#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
yum -y update
sleep 30 # prevent "Error: Rpmdb changed underneath us"
yum install python-pip -y
python3 -m pip install boto3 ec2-metadata
wget -O server.py https://raw.githubusercontent.com/awsdocs/aws-doc-sdk-examples/main/scenarios/features/resilient_service/resources/server.py
python3 server.py 80
