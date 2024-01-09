# syntax=docker/dockerfile:1
FROM node:20

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /javascriptv3

# Set default command
# `npm i` needs to be run in the container. Otherwise it causes a dependency issue: https://github.com/evanw/esbuild/issues/1646#issuecomment-1238080595
CMD npm i --prefix /javascriptv3 && npm run --prefix /javascriptv3 integration-test