# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4125
FROM python:3
RUN mkdir /src
COPY . /src/
WORKDIR /src/python/
RUN pip install -r requirements.txt
CMD ["bash"]
