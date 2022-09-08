# syntax=docker/dockerfile:1
FROM ruby:3.0
RUN bundle config --delete frozen
COPY ../ .
RUN bundle install
CMD ["bash"]
