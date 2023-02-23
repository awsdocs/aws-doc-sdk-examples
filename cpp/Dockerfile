# Build this docker file from the sdk example code root directory in order to access the resources folder.

FROM amazonlinux:2022

USER root

# Setup the dependencies.
RUN \
  yum update -y && \
  yum install -y gcc gcc-c++ make cmake libcurl-devel openssl-devel libuuid-devel pulseaudio-libs-devel git && \
  yum clean all

ENV SERVICES="acm;autoscaling;cloudtrail;codebuild;codecommit;cognito-idp;dynamodb;ec2;elasticache;elasticbeanstalk;elasticfilesystem;email;events;glacier;glue;guardduty;iam;kinesis;lambda;logs;monitoring;monitoring;neptune;rds-data;redshift;s3;s3-crt;s3-encryption;secretsmanager;sesv2;sns;sqs;storagegateway;sts;transfer;transcribe;transcribestreaming"

# Build aws-sdk-cpp, building just the modules listed in SERVICES using the BUILD_ONLY argument.
RUN \
  cd /usr/local && \
  git clone --recurse-submodules https://github.com/aws/aws-sdk-cpp.git && \
  cd aws-sdk-cpp && \
  mkdir -p build && \
  cd build && \
  cmake -DCMAKE_BUILD_TYPE=Debug -DBUILD_ONLY=${SERVICES} -DENABLE_TESTING=ON .. && \
  make --jobs=3 install &&  \
  cd /usr/local

# Install googletest.
RUN \
    git clone https://github.com/google/googletest.git -b v1.13.0 && \
    cd googletest && \
    mkdir build  && \
    cd build && \
    cmake ..  -DBUILD_GMOCK=OFF && \
    make && \
    make install

RUN mkdir -p /src/cpp
COPY cpp /src/cpp/

RUN mkdir -p /src/resources/sample_files
COPY resources/sample_files /src/resources/sample_files

WORKDIR /src/cpp/

CMD ["bash"]

