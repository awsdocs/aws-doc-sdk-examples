# syntax=docker/dockerfile:1
FROM maven:latest

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /javav2

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /javav2
USER automation:automation

CMD ["./run_tests.sh"]
