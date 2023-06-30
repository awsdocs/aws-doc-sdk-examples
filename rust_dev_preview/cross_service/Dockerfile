# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4134
ARG MSRV=1.62.1
FROM rust:$MSRV
COPY . /rust_dev_preview/
WORKDIR /rust_dev_preview
RUN apt-get update && \
  apt-get install libclang-dev -y && \
  cargo check --manifest-path /rust_dev_preview/Cargo.toml
CMD ["bash"]
