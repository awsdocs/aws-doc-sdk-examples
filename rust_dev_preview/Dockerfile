# syntax=docker/dockerfile:1
FROM rust:1.40 as builder

# copy in files
RUN mkdir /src
COPY . /src/
WORKDIR /src
RUN cargo install --path .

CMD ["bash"]
