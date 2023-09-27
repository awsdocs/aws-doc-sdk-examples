# syntax=docker/dockerfile:1
FROM swift

# Update image
RUN apt-get update && \
    apt-get install -y python3.10 libssl-dev openssl && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /swift

# Perform build steps
RUN python3.10 /swift/tools/swiftbuild.py */*

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /swift
USER automation:automation

CMD ["python3.10", "/swift/tools/swiftbuild.py", "-t", "*/*"]

