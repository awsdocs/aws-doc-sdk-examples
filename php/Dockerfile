# syntax=docker/dockerfile:1
FROM composer/composer:latest
# copy in files
RUN mkdir /src
COPY . /src/

WORKDIR /src/php/example_code/glue
RUN composer install

CMD ["bash"]