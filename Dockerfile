# syntax=docker/dockerfile:1
FROM ruby:3.0

# copy in files
RUN mkdir /src
COPY /ruby/* /src
ADD .github/linters/.ruby-lint.yml /src/.ruby-lint.yml

# resolve dependencies
WORKDIR /src
RUN bundle config --delete frozen
# RUN bundle install

# configure AWS credentials
RUN mkdir .aws
COPY ./ruby/aws_configure.sh /
RUN chmod 755 /aws_configure.sh

# ENTRYPOINT ["/aws_configure.sh"]
CMD ["bash"]
