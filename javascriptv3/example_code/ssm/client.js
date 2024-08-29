// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ssm.JavaScript.ssm.basics]
import { SSMClient } from "@aws-sdk/client-ssm";
// This relies on a Region being set up in your local AWS config.
const client = new SSMClient({});
export { client };
// snippet-end:[ssm.JavaScript.ssm.basics]
