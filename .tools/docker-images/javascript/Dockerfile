FROM node:20-slim

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="javascript"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    && rm -rf /var/lib/apt/lists/*

# Pre-install common AWS SDK v3 packages globally for cache warming
RUN npm install -g \
    @aws-sdk/client-s3@latest \
    @aws-sdk/client-dynamodb@latest \
    @aws-sdk/client-ecs@latest \
    @aws-sdk/client-lambda@latest \
    @aws-sdk/client-sqs@latest \
    @aws-sdk/client-sns@latest \
    @aws-sdk/client-cloudformation@latest \
    @aws-sdk/lib-dynamodb@latest \
    vitest@latest \
    eslint@latest \
    prettier@latest \
    typescript@latest \
    ts-node@latest

WORKDIR /repo
