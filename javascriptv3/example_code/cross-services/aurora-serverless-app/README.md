# An item tracker web application with Aurora and Amazon SES

## Overview

A REST API that uses the AWS SDK for JavaScript (v3) to track work items in Amazon Aurora
and sends email reports with Amazon Simple Email Service (Amazon SES).

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

- See [the JavaScript (v3) prerequisites](../../README.md#prerequisites)
- [Create an email address identity](https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html#verify-email-addresses-procedure) in Amazon SES.

## Create the resources

See [the steps for creating the resource stack](../../../../resources//cdk/aurora_serverless_app/README.md#deploying-with-the-aws-cdk).

## Run the code

- Populate the `env.json` file with the outputs from the preceding ["create the resources"](#create-the-resources) step.
- Run `yarn start`.
- Make calls to the API. Use a tool like Postman or run front-end client linked in the [additional resources](#additional-resources).

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial.

See [the steps for destroying the resources with the CDK](../../../../resources//cdk/aurora_serverless_app/README.md#destroying-with-the-aws-cdk).

**Note**: Running the app modifies the table and the bucket, so you must delete these resources manually through the console before you can delete the stack.

## Additional resources

- [Front-end client](../../../../resources/clients/react/elwing/README.md)
- [Amazon Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
- [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [RDS Data Client - AWS SDK for JavaScript v3](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-rds-data/index.html)
- [SES Client - AWS SDK for JavaScript v3](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-ses/index.html)
