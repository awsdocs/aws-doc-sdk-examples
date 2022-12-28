FROM mcr.microsoft.com/devcontainers/universal:2-focal

RUN \
  sudo apt-get update --yes && sudo apt-get upgrade --yes
# c++
# dotnet
RUN \
  rm /usr/local/dotnet/current && \
  ln -s /usr/local/dotnet/6.0.403/ /usr/local/dotnet/current && \
  true

RUN \
  # Python
  pip install boto3 && \
  true

RUN \
  # php
  sudo apt-get install php-zip --yes && \
  true

RUN \
  # rust
  curl —proto '=https' —tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y --default-toolchain 1.62.1 && \
  echo 'source "$HOME/.cargo/env"' >> ~/.zshrc && \
  true

RUN \
  # swift 
  sudo apt-get install --yes \
  binutils \
  git \
  libc6-dev \
  libcurl4 \
  libedit2 \
  # libgcc-5-dev \
  libpython2.7 \
  libsqlite3-0 \
  # libstdc++-5-dev \
  libxml2 \
  pkg-config \
  tzdata \
  zlib1g-dev && \
  curl https://download.swift.org/swift-5.7.2-release/ubuntu1804/swift-5.7.2-RELEASE/swift-5.7.2-RELEASE-ubuntu18.04.tar.gz > ~/swift-5.7.2-RELEASE-ubuntu18.04.tar.gz && \
  tar xvf ~/swift-5.7.2-RELEASE-ubuntu18.04.tar.gz && \
  ln -s ~/swift-5.7.2-RELEASE-ubuntu18.04 swift && \
  rm ~/swift-5.7.2-RELEASE-ubuntu18.04.tar.gz && \
  echo 'export PATH="$HOME/swift/usr/bin:$PATH"'  >> ~/.zshrc
