# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4127
FROM node:18
COPY . /src/
RUN cd /src/javascriptv3 && \
    npm install
CMD ["bash"]
