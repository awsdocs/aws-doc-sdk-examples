# syntax=docker/dockerfile:1
FROM gradle:latest

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /kotlin

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /kotlin/
USER automation:automation

CMD ["./run_tests.sh"]
