#!/bin/bash

# Set your AWS region
AWS_REGION=us-east-1

REGISTRY=public.ecr.aws/b4v4v1s0

# Login to ECR registry
aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin $REGISTRY

# Build your Docker image
docker build -t $1 -f ../../../$1/Dockerfile ../../../$1

# Tag your Docker image with ECR registry URL
docker tag $1:latest $REGISTRY/$1:latest

# Push your Docker image to ECR registry
docker push $REGISTRY/$1:latest

