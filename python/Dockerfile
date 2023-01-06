# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4125
FROM python:3
COPY . /src/
RUN cd /src/example_code/dynamodb && \
    pip install -r requirements.txt
CMD ["bash"]
