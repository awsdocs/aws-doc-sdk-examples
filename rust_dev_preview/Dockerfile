# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4134
ARG MSRV=1.58
FROM rust:$MSRV
COPY . /src/
RUN ls -la
RUN pwd
WORKDIR /src/rust_dev_preview
RUN cargo build --ignore-rust-version
CMD ["bash"]
