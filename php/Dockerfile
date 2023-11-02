# syntax=docker/dockerfile:1
FROM php:8.2-apache
FROM composer/composer:latest

# Update image
RUN apk update && \
    apk upgrade && \
    rm -rf /var/cache/apk/*

# Copy source code
COPY . /php/
WORKDIR /php

# Perform build steps
RUN find . -name "composer.json" -not -path "*vendor*" -exec bash -c "dirname {} | xargs -I % composer install --working-dir=%" \;

# Set non-root user w/ ownership of /php
RUN addgroup -S automation && \
    adduser -S -G automation automation && \
    chown -R automation:automation /php/

USER automation:automation

CMD ["./php/testing", "--integ"]