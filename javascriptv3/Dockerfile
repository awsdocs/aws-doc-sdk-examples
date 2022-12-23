# syntax=docker/dockerfile:1
FROM node:18
# copy in files
RUN mkdir /src
COPY . /src/

# resolve dependencies
WORKDIR /src/javascriptv3
RUN npm install

CMD ["bash"]
