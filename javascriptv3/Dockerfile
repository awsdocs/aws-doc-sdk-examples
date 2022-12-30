# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4127
FROM node:18
RUN mkdir /src
COPY . /src/
WORKDIR /src/javascriptv3
RUN npm install
CMD ["bash"]
