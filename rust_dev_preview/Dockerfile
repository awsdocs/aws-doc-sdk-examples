# syntax=docker/dockerfile:1
ARG MSRV=1.66.1
FROM rust:$MSRV

# Update image
RUN apt-get update && \
    apt-get install -y libclang-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /rust_dev_preview/
WORKDIR /rust_dev_preview

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /rust_dev_preview \
USER automation:automation

# Perform build steps
RUN cargo clippy --manifest-path /rust_dev_preview/examples/Cargo.toml && \
    cargo clippy --manifest-path /rust_dev_preview/cross_service/Cargo.toml

CMD ["bash"]
