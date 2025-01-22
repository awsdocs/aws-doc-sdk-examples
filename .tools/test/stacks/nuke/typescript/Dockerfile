FROM ghcr.io/ekristen/aws-nuke:v3.42.0
ENV AWS_SDK_LOAD_CONFIG=1 \
    AWS_DEBUG=true
USER root
RUN apk add --no-cache \
    python3 \
    py3-pip \
    aws-cli
COPY nuke_generic_config.yaml /nuke_generic_config.yaml
COPY --chmod=755 run.sh /run.sh
USER aws-nuke
ENTRYPOINT ["/run.sh"]
