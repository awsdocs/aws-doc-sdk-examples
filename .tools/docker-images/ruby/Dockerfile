FROM ruby:3.2-slim

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="ruby"

RUN apt-get update && apt-get install -y --no-install-recommends \
    git build-essential \
    && rm -rf /var/lib/apt/lists/*

RUN gem install \
    aws-sdk-s3:1.170.0 \
    aws-sdk-dynamodb:1.120.0 \
    aws-sdk-ecs:1.160.0 \
    aws-sdk-lambda:1.130.0 \
    aws-sdk-cloudformation:1.110.0 \
    rspec:3.13.0 \
    rubocop:1.68.0 \
    webmock:3.24.0

WORKDIR /repo
