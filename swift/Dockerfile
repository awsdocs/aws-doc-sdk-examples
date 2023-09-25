# syntax=docker/dockerfile:1
FROM swift

# Update image
RUN apt-get update && \
    apt-get install -y python3.10 libssl-dev openssl && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /swift/
WORKDIR /swift

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /swift
USER automation:automation

# Perform build steps
RUN cd /swift/example_code/ && \
    python3.10 ../tools/swiftbuild.py */*

CMD ["python3.10", "/swift/tools/swiftbuild.py"]

