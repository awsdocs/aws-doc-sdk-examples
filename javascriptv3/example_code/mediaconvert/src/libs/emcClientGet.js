/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-examples.html.

Purpose:
ec2ClientGet.js is a helper function that creates an Amazon Elastic Compute Cloud (Amazon EC2) service client.

Inputs (replace in code):
- REGION

 */
/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-examples.html.

Purpose:
emcClientGet.js is a helper function that creates an Amazon Elemental MediaConvert (EMC) service client, without requiring the account endpoint.

Inputs (replace in code):
- REGION
- ENDPOINT
*/
// snippet-start:[cloudwatch.JavaScript.createclientGetv3]
import { MediaConvertClient } from "@aws-sdk/client-mediaconvert";
// Set the AWS Region.
const REGION = "REGION";
//Set the account end point.
//Set the MediaConvert Service Object
const emcClientGet = new MediaConvertClient({region: REGION});
export { emcClientGet };
// snippet-end:[cloudwatch.JavaScript.createclientGetv3]
