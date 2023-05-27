# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4131
FROM php:8.2-apache
FROM composer/composer:latest
COPY . /php/
WORKDIR /php
RUN find . -name "composer.json" -not -path "*vendor*" -exec bash -c "dirname {} | xargs -I % composer install --working-dir=%" \;
# Uncomment the following line to run all integration tests
# RUN ./testing --integ
CMD ["bash"]