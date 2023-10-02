/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[eventbridge.JavaScript.events.createclientv3]
import { EventBridgeClient } from "@aws-sdk/client-eventbridge";
// Create the Amazon EventBridge client. The region is optional. If not specified,
// the SDK will look for local configurations containing a region.
export const client = new EventBridgeClient({ region: "us-east-1" });
// snippet-end:[eventbridge.JavaScript.events.createclientv3]
