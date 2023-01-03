# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4128
FROM maven:latest
RUN mkdir /src
COPY . /src/
WORKDIR /src/javav2/example_code/cloudwatch
RUN mvn package -Dmaven.test.skip
CMD ["bash"]
