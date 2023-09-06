# syntax=docker/dockerfile:1
FROM php:8.2-apache
FROM composer/composer:latest
COPY . /php/
WORKDIR /php
RUN find . -name "composer.json" -not -path "*vendor*" -exec bash -c "dirname {} | xargs -I % composer install --working-dir=%" \;
CMD ["cd", "php", "&&", "./testing", "--integ"]