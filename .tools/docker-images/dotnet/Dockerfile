FROM mcr.microsoft.com/dotnet/sdk:8.0

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="dotnet"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    && rm -rf /var/lib/apt/lists/*

# Pre-install common NuGet packages into a warm cache
RUN dotnet new console -n warmup -o /tmp/warmup && \
    cd /tmp/warmup && \
    dotnet add package AWSSDK.S3 --version 4.0.0 && \
    dotnet add package AWSSDK.DynamoDBv2 --version 4.0.0 && \
    dotnet add package AWSSDK.ECS --version 4.0.0 && \
    dotnet add package AWSSDK.Lambda --version 4.0.0 && \
    dotnet add package AWSSDK.CloudFormation --version 4.0.0 && \
    dotnet add package AWSSDK.IdentityManagement --version 4.0.0 && \
    dotnet add package Moq --version 4.20.72 && \
    dotnet add package xunit --version 2.9.2 && \
    dotnet restore && \
    rm -rf /tmp/warmup

WORKDIR /repo
