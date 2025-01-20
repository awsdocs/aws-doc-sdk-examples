FROM ghcr.io/ekristen/aws-nuke:v3.42.0

# Set environment variable for AWS SDK debug logging
ENV AWS_SDK_LOAD_CONFIG=1
ENV AWS_DEBUG=true

USER root

# Install AWS CLI
USER root
RUN apk add --no-cache \
        python3 \
        py3-pip \
        aws-cli

COPY nuke_generic_config.yaml /nuke_generic_config.yaml

# Add a script for debugging
COPY --chmod=755 run.sh /run.sh

# Use shell as entrypoint
ENTRYPOINT ["/run.sh"]
