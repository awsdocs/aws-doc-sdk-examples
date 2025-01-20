# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
#!/bin/sh

# Get AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "AWS Account ID: $AWS_ACCOUNT_ID"

# Copy the config file to /tmp and inject Account ID
echo "Copying & updating config file..."
cp /nuke_generic_config.yaml /tmp/nuke_config.yaml
sed -i "s/AWSACCOUNTID/$AWS_ACCOUNT_ID/g" /tmp/nuke_config.yaml

echo "Running aws-nuke command:"
/usr/local/bin/aws-nuke run --config /tmp/nuke_config.yaml --force --max-wait-retries --no-dry-run 10 2>&1
