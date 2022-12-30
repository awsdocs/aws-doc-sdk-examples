# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4129
FROM golang:1.19.2
RUN mkdir /src
COPY . /src/
WORKDIR /src/gov2/dynamodb
RUN go build ./...
CMD ["bash"]
