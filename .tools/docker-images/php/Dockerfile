FROM php:8.2-cli

LABEL maintainer="awsdocs-sdk-examples"
LABEL language="php"

RUN apt-get update && apt-get install -y --no-install-recommends \
    git unzip \
    && rm -rf /var/lib/apt/lists/*

# Install Composer
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

# Pre-install AWS SDK and test tools
RUN mkdir /tmp/warmup && cd /tmp/warmup && \
    composer init --name=warmup/warmup --no-interaction && \
    composer require aws/aws-sdk-php:^3.320 --no-interaction && \
    composer require --dev phpunit/phpunit:^10.5 --no-interaction && \
    rm -rf /tmp/warmup

WORKDIR /repo
