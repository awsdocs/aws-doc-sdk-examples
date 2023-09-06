# syntax=docker/dockerfile:1

# Our examples have various minimum Swift versions, depending on what features
# of the SDK they use. Here we will just pull the most recent version of the
# official Swift Docker image.

FROM swift

# Install Python for the scripts and tools.
RUN apt-get update && \
    apt-get install -y python3.10 libssl-dev openssl

# Copy the code into place and build all the examples.
COPY . /swift/
RUN cd /swift/example_code/ && \
    python3.10 ../tools/swiftbuild.py */*
CMD ["cd", "/swift", "tools/swiftbuild.py -t */*"]
