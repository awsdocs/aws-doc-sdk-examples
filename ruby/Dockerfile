# syntax=docker/dockerfile:1
FROM ruby:3.1.2-buster

# copy in files
RUN mkdir /src
COPY /ruby/* /src/
ADD .github/linters/.ruby-lint.yml /src/.ruby-lint.yml

# resolve dependencies
WORKDIR /src
RUN bundle config --delete frozen
RUN bundle install

CMD ["/bin/bash"]
