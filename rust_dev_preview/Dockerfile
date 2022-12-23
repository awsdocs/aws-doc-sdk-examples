# syntax=docker/dockerfile:1
ARG MSRV=1.58
FROM rust:$MSRV
COPY . /src/
RUN ls -la
RUN pwd
WORKDIR /src/rust_dev_preview
RUN cargo build --ignore-rust-version
CMD ["bash"]
