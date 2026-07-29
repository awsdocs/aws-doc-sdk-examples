FROM rust:1.82-slim-bookworm

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="rust"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    pkg-config \
    libssl-dev \
    && rm -rf /var/lib/apt/lists/*

# Install clippy and rustfmt
RUN rustup component add clippy rustfmt

# Pre-download common AWS SDK crates
RUN cargo init /tmp/warmup && cd /tmp/warmup && \
    cargo add aws-config@1.5.0 && \
    cargo add aws-sdk-s3@1.60.0 && \
    cargo add aws-sdk-dynamodb@1.50.0 && \
    cargo add aws-sdk-ecs@1.50.0 && \
    cargo add aws-sdk-lambda@1.50.0 && \
    cargo add tokio@1.41.0 --features full && \
    cargo build && \
    rm -rf /tmp/warmup

WORKDIR /repo
