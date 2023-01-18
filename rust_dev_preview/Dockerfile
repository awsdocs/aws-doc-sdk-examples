# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4134
ARG MSRV=1.58
FROM rust:$MSRV
COPY . /rust_dev_preview/
RUN cd /rust_dev_preview && \
    cargo build --ignore-rust-version
CMD ["bash"]
