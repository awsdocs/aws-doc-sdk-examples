# To build the docker image, run the following command from the shell. This command must be run in
# the "aws-doc-sdk-examples" directory, the parent directory of "cpp", in order to access the resources folder.
#
# 'docker build -f cpp/Dockerfile -t <container_tag> .'
#
# The following command will run the docker image, copying your AWS credentials.
# 'docker run -it --volume ~/.aws/credentials:/home/tests/.aws/credentials <container_tag>'

FROM public.ecr.aws/amazonlinux/amazonlinux:2023

# Set up the dependencies.
RUN \
  dnf --setopt=install_weak_deps=False -y install gcc gcc-c++ make cmake libcurl-devel openssl-devel libuuid-devel pulseaudio-libs-devel git libzip-tools libzip-devel && \
  dnf clean all

# Build only the services needed for example code.
ENV SERVICES="acm;autoscaling;cloudtrail;codebuild;codecommit;cognito-idp;dynamodb;ec2;elasticache;elasticbeanstalk"
ENV SERVICES=${SERVICES}";elasticfilesystem;email;events;glacier;glue;guardduty;iam;kinesis;lambda;logs;mediaconvert;monitoring"
ENV SERVICES=${SERVICES}";monitoring;neptune;rds;rds-data;redshift;rekognition;s3;s3-crt;s3-encryption;secretsmanager;sesv2;sns;sqs"
ENV SERVICES=${SERVICES}";storagegateway;sts;transfer;transcribe;transcribestreaming"

# Build aws-sdk-cpp, building only the modules listed in SERVICES using the BUILD_ONLY argument.
RUN \
  cd /usr/local && \
  git clone --recurse-submodules https://github.com/aws/aws-sdk-cpp.git && \
  cd aws-sdk-cpp && \
  mkdir -p build && \
  cd build && \
  cmake -DCMAKE_BUILD_TYPE=Debug -DBUILD_ONLY=${SERVICES} -DENABLE_TESTING=ON .. && \
  make --jobs=$(nproc) install &&  \
  cd /usr/local

# Install googletest.
RUN \
    git clone https://github.com/google/googletest.git -b v1.13.0 && \
    cd googletest && \
    mkdir build  && \
    cd build && \
    cmake ..  -DBUILD_GMOCK=OFF && \
    make -j$(nproc) && \
    make install


# Copy the C++ example code.
RUN mkdir -p /src/cpp

COPY . /src/cpp/

# Commented until https://github.com/awsdocs/aws-doc-sdk-examples/issues/5454 resolved.
## The sample files are needed for some of the automated tests.
#RUN mkdir -p /src/resources/sample_files
#COPY resources/sample_files /src/resources/sample_files

WORKDIR /src/cpp/

RUN useradd -ms /bin/bash tests && \
    chown tests /src/cpp

USER tests

CMD ["python3", "/src/cpp/run_automated_tests.py "]
