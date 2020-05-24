#!/bin/bash
set -eo pipefail
if [ ! -d lib ]; then
  echo "Installing libraries..."
  ./2-build-layer.sh
fi
GEM_PATH=lib/ruby/2.5.0
ruby function/lambda_function.test.rb
