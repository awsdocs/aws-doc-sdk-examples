# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

STACK_NAME="sdk-examples-photo-analyzer"

echo "Deleting the stack..."
aws cloudformation delete-stack \
  --stack-name "$STACK_NAME"

aws cloudformation wait stack-delete-complete --stack-name "$STACK_NAME"