FROM amazonlinux:2

# Dependencies to build aws-lambda-cpp-runtime:
RUN yum -y install gcc-c++ libcurl-devel cmake3 jsoncpp-devel git make zip

# Build and install aws-lambda-cpp-runtime.
RUN git clone https://github.com/awslabs/aws-lambda-cpp-runtime.git && \
    cd aws-lambda-cpp-runtime && \
    mkdir build  && \
    cd build  && \
    cmake3 .. -DCMAKE_BUILD_TYPE=Debug \
      -DBUILD_SHARED_LIBS=OFF \
      -DCMAKE_INSTALL_PREFIX=~/install  && \
    make  && \
    make install