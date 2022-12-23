# syntax=docker/dockerfile:1
FROM swift:5.4.3
# copy in files
RUN mkdir /src
COPY . /src/

# set work directory
WORKDIR /src/swift/example_code/s3/basics

RUN swift build

CMD ["bash"]