# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4132

# Although some examples work on earlier versions of Swift, the
# version below will run all examples.

FROM swift:5.6

# Install Python for the scripts and tools.

RUN apt-get update && \
    apt-get install -y python3.10 libssl-dev openssl

# Copy the code into place and build all the examples.

COPY . /swift/
RUN cd /swift/example_code/ && \
    ../tools/swiftbuild */*
CMD ["bash"]
