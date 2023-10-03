# syntax=docker/dockerfile:1
FROM ruby:3.1.2-buster

# Update image
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy source code
COPY . /ruby/
WORKDIR /ruby

# Set non-root user
RUN useradd -m automation && \
    chown -R automation:automation /ruby
USER automation:automation

# Perform build steps
RUN bundle config --delete frozen && \
    bundle install

# Set default command
CMD ["rake"]