/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[javascript.v3.cloudwatchevents.client]
import { CloudWatchEventsClient } from "@aws-sdk/client-cloudwatch-events";
import { DEFAULT_REGION } from "@aws-sdk-examples/libs/utils/util-aws-sdk.js";

export const client = new CloudWatchEventsClient({ region: DEFAULT_REGION });
// snippet-end:[javascript.v3.cloudwatchevents.client]
