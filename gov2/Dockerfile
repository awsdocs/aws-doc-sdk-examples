# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4129
FROM golang:1.19.2
COPY . /gov2
RUN cd gov2/dynamodb && \
    go build ./...
CMD ["bash"]
