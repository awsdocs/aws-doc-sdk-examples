# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4130
FROM gradle:latest
RUN mkdir /src
COPY . /src/
WORKDIR /src/kotlin/services/cloudwatch
RUN gradle build
CMD ["bash"]
