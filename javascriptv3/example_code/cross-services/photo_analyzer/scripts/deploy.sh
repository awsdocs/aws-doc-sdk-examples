# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

STACK_NAME="sdk-examples-photo-analyzer"

echo "Creating stack..."
aws cloudformation create-stack --stack-name "$STACK_NAME" \
  --template-body file://stack.yaml \
  --capabilities CAPABILITY_IAM

aws cloudformation wait stack-create-complete --stack-name "$STACK_NAME"

echo "$(aws cloudformation describe-stacks --stack-name $STACK_NAME \
  --query 'Stacks[*].Outputs[*].{OutputKey: OutputKey, OutputValue: OutputValue}')"