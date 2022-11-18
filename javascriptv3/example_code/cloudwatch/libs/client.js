/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[javascript.v3.cloudwatch.client]
import { CloudWatchClient } from "@aws-sdk/client-cloudwatch";
import { DEFAULT_REGION } from "../../libs/utils/util-aws-sdk.js";

export const client = new CloudWatchClient({ region: DEFAULT_REGION });
// snippet-end:[javascript.v3.cloudwatch.client]
