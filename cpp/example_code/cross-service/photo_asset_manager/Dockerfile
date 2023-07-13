FROM public.ecr.aws/amazonlinux/amazonlinux:2023

# Dependencies to build aws-lambda-cpp-runtime and aws-sdk-cpp.
RUN dnf --setopt=install_weak_deps=False -y install gcc-c++ libcurl-devel cmake3 jsoncpp-devel git make zip unzip openssl-devel libuuid-devel pulseaudio-libs-devel libzip-devel && \
    dnf clean all

# Build and install aws-lambda-cpp.
RUN git clone https://github.com/awslabs/aws-lambda-cpp.git && \
    cd aws-lambda-cpp && \
    mkdir build  && \
    cd build  && \
    cmake3 -DCMAKE_BUILD_TYPE=Debug \
      -DBUILD_SHARED_LIBS=OFF \
      -DCMAKE_INSTALL_PREFIX=~/install .. && \
    make  && \
    make install

ENV SERVICES="s3;sns;rekognition;dynamodb"

# Build aws-sdk-cpp, building only the modules listed in SERVICES using the BUILD_ONLY argument.
RUN \
  git clone --depth 1 --recurse-submodules https://github.com/aws/aws-sdk-cpp.git && \
  cd aws-sdk-cpp && \
  mkdir -p build && \
  cd build && \
  cmake3 -DCMAKE_BUILD_TYPE=Debug -DBUILD_ONLY=${SERVICES} -DCMAKE_INSTALL_PREFIX=~/install .. && \
  make --jobs=$(nproc)  install


# Build and package the AWS Lambda source code.
RUN mkdir -p /pam_lambda

COPY . /pam_lambda

RUN \
    cd /pam_lambda && \
    mkdir build && \
    cd build && \
    cmake3 -DCMAKE_BUILD_TYPE=Debug -DCMAKE_PREFIX_PATH=~/install .. && \
    make && \
    make aws-lambda-package-cpp_pam_lambdas

# Because this Dockerfile is used by the AWS CDK "lambda.Code.fromDockerBuild" api, unzip to /asset.
# The CDK will then zip the code.

RUN \
   mkdir -p /asset && \
   unzip /pam_lambda/build/cpp_pam_lambdas.zip -d /asset