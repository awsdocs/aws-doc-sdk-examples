#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Run all tests with integ tag, excluding those quarantined.
rspec . --tag integ --tag ~@quarantine
