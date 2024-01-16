// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[sqs.JavaScript.createclientv3]
import { SQSClient } from "@aws-sdk/client-sqs";

export const client = new SQSClient({ region: "us-east-1" });
// snippet-end:[sqs.JavaScript.createclientv3]
