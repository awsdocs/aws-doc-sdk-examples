#!/bin/bash
yum -y update
sleep 30 # prevent "Error: Rpmdb changed underneath us"
yum install python-pip -y
python3 -m pip install boto3 ec2-metadata
wget -O server.py https://raw.githubusercontent.com/Laren-AWS/aws-doc-sdk-examples/resilient-architecture-python/workflows/resilient_service/resources/server.py
python3 server.py 80 us-west-2
