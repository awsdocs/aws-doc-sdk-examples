#!/bin/bash
set -eo pipefail
gem install bundler
rm -rf lib
cd function
rm -f Gemfile.lock
bundle config set path '../lib'
bundle install