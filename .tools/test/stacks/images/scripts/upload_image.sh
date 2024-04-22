#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# NOTE: This script may succeed but result in failures in AWS
# due to architecture differences between your local OS and
# that of the AWS Batch compute environment.

# Set your AWS region
AWS_REGION=us-east-1

REGISTRY=808326389482.dkr.ecr.us-east-1.amazonaws.com

# Login to ECR registry
aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin $REGISTRY

# Build your Docker image
docker build -t $1 -f ../../../$1/Dockerfile ../../../$1

# Tag your Docker image with ECR registry URL
docker tag $1:latest $REGISTRY/$1:latest

# Push your Docker image to ECR registry
docker push $REGISTRY/$1:latest

