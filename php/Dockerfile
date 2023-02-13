# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4131
FROM composer/composer:latest
COPY . /php/
WORKDIR /php
RUN find /php -name "composer.json" -not -path "*vendor*" -exec bash -c "dirname {} | xargs -I % composer install --working-dir=%" \;
CMD ["bash"]