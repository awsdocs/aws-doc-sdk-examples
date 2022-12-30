# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4124
FROM ruby:3.1.2-buster
RUN mkdir /src
COPY . /src/
WORKDIR /src/ruby
RUN bundle config --delete frozen
RUN bundle install
CMD ["bash"]
