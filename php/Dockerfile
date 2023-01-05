# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4131
FROM composer/composer:latest
RUN mkdir /src
COPY . /src/
WORKDIR /src/php/example_code/glue
RUN composer install
CMD ["bash"]