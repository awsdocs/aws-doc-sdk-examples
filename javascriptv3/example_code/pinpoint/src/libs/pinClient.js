/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/pinpoint-examples.html.

Purpose:
pinClient.js is a helper function that creates an Amazon Pinpoint service client.

Inputs (replace in code):
- REGION
- ENDPOINT
*/
// snippet-start:[pinpoint.JavaScript.createclientv3]
import { PinpointClient } from "@aws-sdk/client-pinpoint";
// Set the AWS Region.
const REGION = "us-east-1";
//Set the MediaConvert Service Object
const pinClient = new PinpointClient({region: REGION});
export  { pinClient };
// snippet-end:[pinpoint.JavaScript.createclientv3]
