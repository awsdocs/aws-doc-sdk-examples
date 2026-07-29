FROM amazoncorretto:21-al2023

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="kotlin"

RUN dnf install -y git unzip zip && dnf clean all

# Install Gradle
ARG GRADLE_VERSION=8.10
RUN curl -fsSL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o /tmp/gradle.zip && \
    unzip -d /opt /tmp/gradle.zip && \
    ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle && \
    rm /tmp/gradle.zip

# Install Kotlin compiler
ARG KOTLIN_VERSION=2.0.21
RUN curl -fsSL https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip -o /tmp/kotlin.zip && \
    unzip -d /opt /tmp/kotlin.zip && \
    ln -s /opt/kotlinc/bin/kotlin /usr/local/bin/kotlin && \
    ln -s /opt/kotlinc/bin/kotlinc /usr/local/bin/kotlinc && \
    rm /tmp/kotlin.zip

# Install ktlint
RUN curl -fsSL https://github.com/pinterest/ktlint/releases/download/1.4.1/ktlint -o /usr/local/bin/ktlint && \
    chmod +x /usr/local/bin/ktlint

WORKDIR /repo
