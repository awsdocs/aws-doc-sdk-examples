FROM golang AS builder
COPY . src/
RUN cd src && ls && GOPROXY=direct go build -o ../bin/worker ./cmd/worker/

ENTRYPOINT [ "/go/bin/worker" ]