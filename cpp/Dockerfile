# This DockerFile was copied from the AWS SDK for C++.
# https://github.com/aws/aws-sdk-cpp/blob/master/CI/docker-file/Ubuntu/20.04/Dockerfile
# Additional run steps were added.

# Using official ubuntu docker image
FROM ubuntu:20.04

# Install git, zip, python-pip, cmake, g++, zlib, libssl, libcurl, java, maven via apt
# Specify DEBIAN_FRONTEND and TZ to prevent tzdata hanging
RUN apt-get update && \
    apt-get upgrade -y && \
    DEBIAN_FRONTEND="noninteractive" TZ="America/Los_Angeles" apt-get install -y git zip wget python3 python3-pip cmake g++ zlib1g-dev libssl-dev libcurl4-openssl-dev openjdk-8-jdk doxygen ninja-build

RUN update-alternatives --install /usr/bin/python python /usr/bin/python3 10

# Install maven
RUN apt-get install -y maven

# Install awscli
#RUN pip install awscli --upgrade

RUN mkdir /src
COPY . /src/

ENV SERVICES="autoscaling;ec2;iam;dynamodb;glue;lambda;monitoring;s3;sts"

RUN mkdir /src/aws && \
    cd /src/aws && \
    git clone --recurse-submodules https://github.com/aws/aws-sdk-cpp && \
    cd aws-sdk-cpp && \
    mkdir build && \
    cd build && \
    cmake .. -DCMAKE_BUILD_TYPE=Debug -DBUILD_ONLY=${SERVICES} -DENABLE_TESTING=OFF && \
    make && \
    make install

WORKDIR /src

CMD ["bash"]

