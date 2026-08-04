FROM node:20-slim

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="javascript"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    && rm -rf /var/lib/apt/lists/*

# Pre-install common AWS SDK v3 packages globally for cache warming
RUN npm install -g \
    @aws-sdk/client-s3@3.750.0 \
    @aws-sdk/client-dynamodb@3.750.0 \
    @aws-sdk/client-ecs@3.750.0 \
    @aws-sdk/client-lambda@3.750.0 \
    @aws-sdk/client-sqs@3.750.0 \
    @aws-sdk/client-sns@3.750.0 \
    @aws-sdk/client-cloudformation@3.750.0 \
    @aws-sdk/lib-dynamodb@3.750.0 \
    vitest@2.1.8 \
    eslint@9.16.0 \
    prettier@3.4.2 \
    typescript@5.7.2 \
    ts-node@10.9.2

WORKDIR /repo
