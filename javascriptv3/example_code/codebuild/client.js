/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[javascript.v3.codebuild.client]
import { CodeBuildClient } from "@aws-sdk/client-codebuild";

// When no "region" is provided. The SDK will attempt to environment variables
// or an AWS configuration file to determine the region.
const client = new CodeBuildClient({});

export { client };
// snippet-end:[javascript.v3.codebuild.client]
