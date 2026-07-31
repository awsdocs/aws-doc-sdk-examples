FROM swift:jammy

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="swift"

RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    libssl-dev \
    openssl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /repo
