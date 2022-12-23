# syntax=docker/dockerfile:1
FROM ruby:3.1.2-buster

# copy in files
RUN mkdir /src
COPY . /src/

# resolve dependencies
WORKDIR /src/ruby
RUN pwd
RUN ls
RUN bundle config --delete frozen
RUN bundle install

CMD ["./entrypoint.sh"]
