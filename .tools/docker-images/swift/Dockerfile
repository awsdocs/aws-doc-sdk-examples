FROM swift:5.10-jammy

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="swift"

RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    && rm -rf /var/lib/apt/lists/*

# Pre-resolve common AWS SDK for Swift packages
RUN mkdir -p /tmp/warmup && cd /tmp/warmup && \
    swift package init --type executable && \
    sed -i 's/]/],\n    dependencies: [\n        .package(url: "https:\/\/github.com\/awslabs\/aws-sdk-swift.git", from: "1.0.0"),\n    ]/' Package.swift && \
    swift package resolve 2>/dev/null || true && \
    rm -rf /tmp/warmup

WORKDIR /repo
