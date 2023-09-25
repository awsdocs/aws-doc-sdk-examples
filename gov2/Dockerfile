# syntax=docker/dockerfile:1
FROM golang:1.19.2

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /gov2/
WORKDIR /gov2

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /gov2/
USER automation:automation

# Perform build steps
RUN go env -w GOPROXY=direct

# Set default command
CMD ["./run_all_tests.sh", "integration"]
