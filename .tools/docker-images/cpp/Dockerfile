FROM ubuntu:24.04

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="cpp"

RUN apt-get update && apt-get install -y --no-install-recommends \
    git build-essential cmake pkg-config \
    libcurl4-openssl-dev libssl-dev uuid-dev zlib1g-dev \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Build and install AWS SDK for C++
ARG AWS_SDK_VERSION=1.11.400
RUN git clone --recurse-submodules --depth 1 --branch ${AWS_SDK_VERSION} \
    https://github.com/aws/aws-sdk-cpp.git /tmp/aws-sdk-cpp && \
    mkdir /tmp/aws-sdk-cpp/build && cd /tmp/aws-sdk-cpp/build && \
    cmake .. -DCMAKE_BUILD_TYPE=Release \
      -DBUILD_ONLY="s3;dynamodb;ecs;lambda;sqs;cloudformation" \
      -DBUILD_SHARED_LIBS=ON \
      -DENABLE_TESTING=OFF && \
    make -j$(nproc) && make install && \
    rm -rf /tmp/aws-sdk-cpp

RUN ldconfig

WORKDIR /repo
