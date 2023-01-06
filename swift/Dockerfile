# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4132
FROM swift:5.4.3
COPY . /src/
RUN cd /src/swift/example_code/s3/basics && \
    swift build
CMD ["bash"]