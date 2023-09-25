# syntax=docker/dockerfile:1
FROM gradle:latest

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /kotlin
WORKDIR /kotlin

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /kotlin/
USER automation:automation

# Perform build steps
RUN gradle build

CMD ["bash"]













# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4130
FROM gradle:latest
COPY . /kotlin/
RUN cd /kotlin/services/cloudwatch && \
    gradle build
CMD ["bash"]
