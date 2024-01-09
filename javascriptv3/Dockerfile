# syntax=docker/dockerfile:1
FROM node:20
COPY . /javascriptv3
RUN npm i --prefix /javascriptv3
CMD npm run --prefix /javascriptv3 integration-test