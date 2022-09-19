#!/bin/bash

# Exec these commands after container is running:
source ~/.bashrc
type rbenv
rbenv install 3.0.2
rbenv global 3.0.2
cd /src
echo "gem: --no-document" > ~/.gemrc
gem install bundler