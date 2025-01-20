FROM ghcr.io/ekristen/aws-nuke:v3.42.0

# Set environment variables
ENV AWS_SDK_LOAD_CONFIG=1 \
    AWS_DEBUG=true

# Switch to root, install AWS CLI and cleanup in single layer
USER root
RUN apk add --no-cache \
    python3 \
    py3-pip \
    aws-cli

# Copy configuration and script
COPY nuke_generic_config.yaml /nuke_generic_config.yaml
COPY --chmod=755 run.sh /run.sh

# Switch back to non-root user for security
USER aws-nuke

ENTRYPOINT ["/run.sh"]
