# syntax=docker/dockerfile:1
# Status: Beta
# GA updates: https://github.com/awsdocs/aws-doc-sdk-examples/issues/4124
FROM ruby:3.1.2-buster
COPY . /ruby/
RUN cd /ruby && \
    bundle config --delete frozen && \
    bundle install
WORKDIR /ruby
CMD ["rake"]
