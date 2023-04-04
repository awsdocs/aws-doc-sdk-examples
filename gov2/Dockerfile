# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4129
FROM golang:1.19.2
COPY . /gov2
# If your corporate network blocks proxy.golang.org, uncomment this line.
RUN go env -w GOPROXY=direct
## Run tests in all gov2 folders.
## Pass 'integration' to run_all_tests.sh to run integration tests.
#RUN cd /gov2 && \
#  ./run_all_tests.sh
CMD ["bash"]
