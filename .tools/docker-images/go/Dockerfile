FROM golang:1.22-bookworm

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="go"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    && rm -rf /var/lib/apt/lists/*

# Pre-download common AWS SDK for Go v2 modules
RUN mkdir -p /tmp/warmup && cd /tmp/warmup && \
    go mod init warmup && \
    go get github.com/aws/aws-sdk-go-v2@latest && \
    go get github.com/aws/aws-sdk-go-v2/config@latest && \
    go get github.com/aws/aws-sdk-go-v2/service/s3@latest && \
    go get github.com/aws/aws-sdk-go-v2/service/dynamodb@latest && \
    go get github.com/aws/aws-sdk-go-v2/service/ecs@latest && \
    go get github.com/aws/aws-sdk-go-v2/service/lambda@latest && \
    go get github.com/aws/aws-sdk-go-v2/service/sqs@latest && \
    go get github.com/aws/aws-sdk-go-v2/service/cloudformation@latest && \
    rm -rf /tmp/warmup

WORKDIR /repo
