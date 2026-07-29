FROM amazoncorretto:21-al2023

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="java"

# System dependencies
RUN dnf install -y git tar gzip && dnf clean all

# Install Maven
ARG MAVEN_VERSION=3.9.9
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    | tar xz -C /opt \
    && ln -s /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/local/bin/mvn

# Pre-cache common AWS SDK dependencies by building a dummy project
COPY warm-cache-pom.xml /tmp/pom.xml
RUN cd /tmp && mvn dependency:resolve -q && rm -rf /tmp/pom.xml

WORKDIR /repo
