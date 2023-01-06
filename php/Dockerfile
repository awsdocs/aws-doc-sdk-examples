# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4131
FROM composer/composer:latest
COPY . /src/
RUN cd /src/php/example_code/glue && \
    composer install
CMD ["bash"]