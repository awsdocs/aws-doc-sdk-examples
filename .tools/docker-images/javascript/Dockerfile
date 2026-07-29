FROM node:20-slim

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="javascript"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    && rm -rf /var/lib/apt/lists/*

# Pre-install common AWS SDK v3 packages globally for cache warming
RUN npm install -g \
    @aws-sdk/client-s3@3.700.0 \
    @aws-sdk/client-dynamodb@3.700.0 \
    @aws-sdk/client-ecs@3.700.0 \
    @aws-sdk/client-lambda@3.700.0 \
    @aws-sdk/client-sqs@3.700.0 \
    @aws-sdk/client-sns@3.700.0 \
    @aws-sdk/client-cloudformation@3.700.0 \
    @aws-sdk/lib-dynamodb@3.700.0 \
    vitest@2.1.0 \
    eslint@9.15.0 \
    prettier@3.4.0 \
    typescript@5.7.0 \
    ts-node@10.9.2

WORKDIR /repo
