#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# NOTE: This script may succeed but result in failures in AWS
# due to architecture differences between your local OS and
# that of the AWS Batch compute environment.

# NOTE: Be sure to set the following environment variables
# export REGISTRY_ACCOUNT=808326389482
# export AWS_REGION=us-east-1

REGISTRY={ADMIN_ACCOUNT}.dkr.ecr.{AWS_REGION}.amazonaws.com

# Login to ECR registry
aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin $REGISTRY

# Build your Docker image
docker build -t $1 -f ../../../$1/Dockerfile ../../../$1

# Tag your Docker image with ECR registry URL
docker tag $1:latest $REGISTRY/$1:latest

# Push your Docker image to ECR registry
docker push $REGISTRY/$1:latest

