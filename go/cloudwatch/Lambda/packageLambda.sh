#!/usr/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
# Build and zip up Lambda code in main.go

GOOS=linux
go build -o main main.go
zip main.zip main
