# syntax=docker/dockerfile:1
FROM maven:latest
# copy in files
RUN mkdir /src
COPY . /src/

# set work directory
WORKDIR /src/javav2/example_code/cloudwatch

RUN mvn package -Dmaven.test.skip

CMD ["bash"]
