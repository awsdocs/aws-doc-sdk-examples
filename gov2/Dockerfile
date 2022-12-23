# syntax=docker/dockerfile:1
FROM golang:1.19.2
# copy in files
RUN mkdir /src
COPY . /src/

# resolve dependencies
# or build

# set work directory
WORKDIR /src/gov2/dynamodb
RUN go build ./...

CMD ["bash"]
