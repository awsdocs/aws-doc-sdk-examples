# syntax=docker/dockerfile:1
FROM php:8.2-apache
FROM composer/composer:latest

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /php/
WORKDIR /php

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /php/
USER automation:automation

# Perform build steps
RUN find . -name "composer.json" -not -path "*vendor*" -exec bash -c "dirname {} | xargs -I % composer install --working-dir=%" \;

CMD ["./php/testing", "--integ"]