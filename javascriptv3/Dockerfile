# syntax=docker/dockerfile:1
FROM node:18

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /javascriptv3

# Perform build steps
RUN npm install -C /javascriptv3

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /javascriptv3/
USER automation:automation

# Set default command
CMD ["./javascriptv3/scripts/run_tests.sh", "integration"]