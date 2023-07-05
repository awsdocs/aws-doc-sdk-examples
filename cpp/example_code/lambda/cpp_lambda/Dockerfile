FROM public.ecr.aws/amazonlinux/amazonlinux:2023

# Dependencies to build aws-lambda-cpp-runtime:
RUN dnf --setopt=install_weak_deps=False -y install gcc-c++ libcurl-devel cmake jsoncpp-devel git make zip && dnf clean all

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
