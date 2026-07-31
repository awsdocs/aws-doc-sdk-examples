FROM python:3.12-slim

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="python"

# System dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Python tooling
RUN pip install --no-cache-dir \
    pytest==8.3.4 \
    pytest-cov==6.0.0 \
    pytest-mock==3.14.0 \
    boto3==1.35.0 \
    botocore==1.35.0 \
    black==24.10.0 \
    pylint==3.3.0 \
    mypy==1.13.0 \
    moto[all]==5.0.0

# Common example dependencies (pre-install to speed up runs)
RUN pip install --no-cache-dir \
    requests==2.32.3 \
    Pillow==11.0.0 \
    pyyaml==6.0.2 \
    cryptography==44.0.0

WORKDIR /repo
