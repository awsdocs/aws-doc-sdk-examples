# syntax=docker/dockerfile:1
ARG MSRV=1.83.0
FROM rust:$MSRV

# Update image
RUN apt-get update && \
    apt-get install -y libclang-dev && \
    rustup component add clippy rustfmt && \
    apt-get clean && \
    rm -rf ${CARGO_HOME}/registry/* ; \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* ; \
    :

# Copy source code
COPY . /rustv1/
ENV PATH="${PATH}:/${CARGO_HOME}/bin"
CMD ["/bin/bash", "-x", "/rustv1/run_all.sh", "--clean"]
