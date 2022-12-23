# syntax=docker/dockerfile:1
FROM gradle:latest
RUN mkdir /src
COPY . /src/
WORKDIR /src/kotlin/services/cloudwatch
RUN gradle build
CMD ["bash"]
