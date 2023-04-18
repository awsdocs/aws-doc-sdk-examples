#!/bin/bash

# Set your AWS region
AWS_REGION=us-east-1

# Set your ECR registry URL
# ECR_REGISTRY_URL=808326389482.dkr.ecr.$AWS_REGION.amazonaws.com

aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/b4v4v1s0

# aws --region $AWS_REGION ecr get-login-password \
#     | docker login \
#         --password-stdin \
#         --username AWS \
#         $ECR_REGISTRY_URL

# # Login to ECR registry
# aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY_URL

# Build your Docker image
docker build -t $1 -f ../../../$1/Dockerfile ../../../$1

# Tag your Docker image with ECR registry URL
docker tag $1:latest public.ecr.aws/b4v4v1s0/$1:latest

# Push your Docker image to ECR registry
docker push public.ecr.aws/b4v4v1s0/$1:latest

