# syntax=docker/dockerfile:1
FROM rust:1.59
# copy in files
RUN mkdir /src
COPY . /src/

# resolve dependencies
# or builld

# set work directory
WORKDIR /src

RUN cargo build --all-targets
CMD ["cargo test"]
