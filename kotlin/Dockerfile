# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4130
FROM gradle:latest
COPY . /kotlin/
RUN cd /kotlin/services/cloudwatch && \
    gradle build
CMD ["bash"]
