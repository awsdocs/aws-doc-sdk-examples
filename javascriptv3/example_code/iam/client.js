/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

// snippet-start:[iam.JavaScript.createclientv3]
import { IAMClient } from "@aws-sdk/client-iam";
const client = new IAMClient({ region: "us-east-1" });
export { client };
// snippet-end:[iam.JavaScript.createclientv3]
