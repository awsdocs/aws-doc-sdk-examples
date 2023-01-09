/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[cloudwatch.JavaScript.logs.createclientv3]
import { CloudWatchLogsClient } from "@aws-sdk/client-cloudwatch-logs";
const REGION = "us-east-1"; //e.g. "us-east-1"
export const client = new CloudWatchLogsClient({ region: REGION });
// snippet-end:[cloudwatch.JavaScript.logs.createclientv3]
