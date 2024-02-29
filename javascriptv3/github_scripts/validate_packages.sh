#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
set -e
errors_found=false
packages=$(npm query --prefix javascriptv3 .workspace | jq -r '.[].name')
for package_name in $packages; do
    if [[ "$package_name" != *"@aws-doc-sdk-examples/"* ]]; then
        echo "Error: Prefix '@aws-doc-sdk-examples/' is missing in package '$package_name'"
        errors_found=true
    fi
done

if [ "$errors_found" = true ]; then
    exit 1
fi
