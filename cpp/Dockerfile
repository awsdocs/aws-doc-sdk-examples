# syntax=docker/dockerfile:1
FROM public.ecr.aws/amazonlinux/amazonlinux:2023

# Set up the dependencies.
RUN \
  dnf --setopt=install_weak_deps=False -y install gcc gcc-c++ make cmake libcurl-devel openssl-devel libuuid-devel pulseaudio-libs-devel git libzip-tools libzip-devel && \
  dnf clean all

# Copy source code
COPY . /cpp
WORKDIR /cpp

# Set non-root user
RUN useradd -m automation && \
    chown automation:automation /cpp
USER automation:automation

# Perform build steps
## Build only the services needed for example code
ENV SERVICES="acm;autoscaling;cloudtrail;codebuild;codecommit;cognito-idp;dynamodb;ec2;elasticache;elasticbeanstalk"
ENV SERVICES=${SERVICES}";elasticfilesystem;email;events;glacier;glue;guardduty;iam;kinesis;lambda;logs;mediaconvert;monitoring"
ENV SERVICES=${SERVICES}";monitoring;neptune;rds;rds-data;redshift;rekognition;s3;s3-crt;s3-encryption;secretsmanager;sesv2;sns;sqs"
ENV SERVICES=${SERVICES}";storagegateway;sts;transfer;transcribe;transcribestreaming"

## Build aws-sdk-cpp, building only the modules listed in SERVICES using the BUILD_ONLY argument
RUN \
  cd /usr/local && \
  git clone --recurse-submodules https://github.com/aws/aws-sdk-cpp.git && \
  cd aws-sdk-cpp && \
  mkdir -p build && \
  cd build && \
  cmake -DCMAKE_BUILD_TYPE=Debug -DBUILD_ONLY=${SERVICES} -DENABLE_TESTING=ON .. && \
  make --jobs=$(nproc) install &&  \
  cd /usr/local

## Install googletest
RUN \
    git clone https://github.com/google/googletest.git -b v1.13.0 && \
    cd googletest && \
    mkdir build  && \
    cd build && \
    cmake ..  -DBUILD_GMOCK=OFF && \
    make -j$(nproc) && \
    make install

CMD ["bash"]

