# syntax=docker/dockerfile:1
ARG MSRV=1.66.1
FROM rust:$MSRV

RUN apt-get update && \
    apt-get install -y libclang-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* \

COPY . /rust_dev_preview/

RUN useradd -m automation && \
    chown -R automation:automation /rust_dev_preview

USER automation:automation

RUN cargo clippy --manifest-path /rust_dev_preview/examples/Cargo.toml && \
    cargo clippy --manifest-path /rust_dev_preview/cross_service/Cargo.toml

WORKDIR /rust_dev_preview

CMD ["bash"]
