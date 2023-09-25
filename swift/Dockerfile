# syntax=docker/dockerfile:1
FROM swift

RUN apt-get update && \
    apt-get install -y python3.10 libssl-dev openssl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY . /swift/
RUN cd /swift/example_code/ && \
    python3.10 ../tools/swiftbuild.py */*

# Create a non-root user for added security
RUN useradd -m automation && \
    chown -R automation:automation /swift

# Ignored by GitHub Actions
WORKDIR /swift

USER automation

CMD ["python3.10", "/swift/tools/swiftbuild.py"]

