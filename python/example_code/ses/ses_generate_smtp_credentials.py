# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[ses_generate_smtp_credentials.py demonstrates how to convert an IAM Secret Access Key into a password that you can use to connect to the Amazon SES SMTP interface.]
# snippet-service:[ses]
# snippet-keyword:[python]
# snippet-keyword:[Amazon SES]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-02-06]
# snippet-sourceauthor:[AWS]
# snippet-start:[ses.python.ses_generate_smtp_credentials.complete]

#!/usr/bin/env python3

import hmac
import hashlib
import base64
import argparse

# Values that are required to calculate the signature. These values should
# never change.
DATE = "11111111"
SERVICE = "ses"
MESSAGE = "SendRawEmail"
TERMINAL = "aws4_request"
VERSION = 0x04

def sign(key, msg):
    return hmac.new(key, msg.encode('utf-8'), hashlib.sha256).digest()

def calculateKey(secretAccessKey, region):
    signature = sign(("AWS4" + secretAccessKey).encode('utf-8'), DATE)
    signature = sign(signature, region)
    signature = sign(signature, SERVICE)
    signature = sign(signature, TERMINAL)
    signature = sign(signature, MESSAGE)
    signatureAndVersion = bytes([VERSION]) + signature
    smtpPassword = base64.b64encode(signatureAndVersion)
    print(smtpPassword.decode('utf-8'))

def main():
    parser = argparse.ArgumentParser(description='Convert a Secret Access Key for an IAM user to an SMTP password.')
    parser.add_argument('--secret',
            help='The Secret Access Key that you want to convert.',
            required=True,
            action="store")
    parser.add_argument('--region',
            help='The name of the AWS Region that the SMTP password will be used in.',
            required=True,
            choices=['us-east-1','us-west-2','eu-west-1'],
            action="store")
    args = parser.parse_args()

    calculateKey(args.secret,args.region)

main()

# snippet-end:[ses.python.ses_generate_smtp_credentials.complete]
