# syntax=docker/dockerfile:1
ARG MSRV=1.70.0
FROM rust:$MSRV

# Update image
RUN apt-get update && \
    apt-get install -y libclang-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /rustv1/

# Perform build steps
RUN cargo clippy --manifest-path /rustv1/examples/Cargo.toml && \
    rustup component add clippy && \
    cargo clippy --manifest-path /rustv1/cross_service/Cargo.toml

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /rustv1 \
USER automation:automation

CMD ["bash"]
