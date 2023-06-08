# PAM development

Photo Asset Management (PAM) is an AWS SDK Code Examples cross-service example.
It's one app, with a consistent architecture, implemented in several programming
languages supported by AWS SDKs. This folder has the AWS Cloud Development Kit (CDK) resources to deploy
the entire app, including AWS resources, the React front end, and the
per-language AWS Lambda implementations.

## Add a new language

To add a new supported implementation language:

1. Create a new project in the appropriate language folder.
1. Implement the Lambda functions.
1. Edit [`./lib/backend/strategies.ts`](./cdk/lib/backend/strategies.ts) and add a new `PamLambdaStrategy` for the language.
   (Copy or refer to an existing instance.)
   1. Register this language in the `STRATEGIES` constant.
   1. Update the current languages in the README.

### Add a `PamLambdaStrategy`

The `PamLambdaStrategy` interface describes how the AWS CDK will deploy the Lambdas for a language.
`runtime` uses a constant from the published Lambda [`Runtime`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-lambda.Runtime.html).
If a static entry is available, use it. Otherwise, create a new Runtime as appropriate.
`timeout` and `memorySize` specify the limits the Lambda will impose.
The defaults might be fine, but expect to need to tune these.

```
export interface PamLambdasStrategy {
  runtime: Runtime;
  timeout: Duration; // Default is 15 minutes.
  memorySize: number; // In megabytes.
  codeAsset: () => Code; // See Bundle code, following.
  handlers: PamLambdasStrategyHandlers; // See Handlers, following
}
```

#### Bundle code

The strategy `codeAsset` property is a function that returns an AWS CDK [`Code`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-lambda.Code.html) reference.
This is the code that Lambda executes directly.
[`Code.fromAsset`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-lambda.Code.html#static-fromwbrassetpath-options) using the NodeJS [`resolve`](https://nodejs.org/api/path.html#pathresolvepaths) path utility can load any code from the `aws-doc-sdk-examples` repository. It is specified relative to `resources/cdk/photo_asset_manager`.
For interpreted languages, this might be all that's necessary for the code.
For compiled languages, Lambda requires the compiled assets.

The AWS CDK can compile resources during the deployment using docker.
To configure this, use the [`bundling`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-s3-assets.AssetOptions.html#bundling) option of `Code.fromAsset`.
Languages with a well-known Runtime usually have a `Runtime.image` property that has an appropriate docker base image.
Otherwise, any public docker image is suitable.

Docker will execute the `command` for the bundling step.
The assets, specified as the first argument to `Code.fromAsset`, are mounted in the container at `/asset-input/` (which is also the working directory).
The command should perform any compilation steps necessary, and move any necessary artifacts to `/asset-output/`.
The contents of `/asset-output/` are archived in the Lambda's Amazon Simple Storage Service (Amazon S3) bucket. These contents are the executable that the Runtime uses.

#### Handlers

In this setup, the output bundle has code for all the Lambdas.
To specify which handler gets executed, specify the handler for each.
The exact format of the handler string is language dependent, and will require research for each.
For instance, the Java handler specifies the classpath to load.
The Python handler specifies the entire module and function to execute.

### Implement the Lambda functions

When implementing Lambda functions, application AWS resources are available in the following environment variables.

| Variable              | Usage                                                                                                                            |
| --------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `LABELS_TABLE_NAME`   | Name for the Labels Table                                                                                                        |
| `STORAGE_BUCKET_NAME` | Name for the Storage Bucket                                                                                                      |
| `WORKING_BUCKET_NAME` | Name for the Working Bucket                                                                                                      |
| `NOTIFICATION_TOPIC`  | Amazon Resource Name (ARN) of the Amazon Simple Notification Service (Amazon SNS) topic to send download ready notifications to. |

#### Upload

1. The input and output are [API Gateway Proxy Events](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format).
1. Read the `file_name` field from the body (which needs to be parsed as JSON).
1. Prepend a UUID.
1. Create a presigned PUT request for the Storage Bucket.
1. Return the presigned URL in the `url` response body (which will need to be encoded as JSON).

#### DetectLabels

1. The input is an [Amazon S3 notification event](https://docs.aws.amazon.com/lambda/latest/dg/with-s3.html). There is no response.
1. For each record, run the Amazon S3 object through Amazon Rekognition's [`detectLabels`](https://docs.aws.amazon.com/rekognition/latest/APIReference/API_DetectLabels.html).
1. Upsert each recognized label to the Labels table, incrementing the Count column and (atomically) inserting the key into the Images column.

#### GetLabels

1. The input and output are [Amazon API Gateway proxy events](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format).
1. There is no input data needed by this function.
1. Read the Labels Table.
1. Prepare a response map
   - Each key is a Label string.
   - Each value is an object with a property `count`, with the integer count column.
1. Return this map as the JSON encoded response body.

#### PrepareDownload

1. The input is a map with the JSON request body as a string in the `body` field. There is no response.
1. Parse the `body` as JSON and get the `labels` column, an array of strings.
1. Load all Images from the Labels Table that match the selected labels into a set (no duplicates).
1. Read those images from the Storage Bucket, and write them to a single zip archive in the Working Bucket.
1. Send a message to the SNS topic with a link to a presigned GET URL for that zip archive.
   - WARNING: Presigned URLs are often longer than email client limits, and email clients may insert newlines or spaces for formatting that breaks the URL. This is a known limitation of using SNS, and the Code Examples team are looking for alternatives. However, SNS does not send rich html, and the design goals do not allow for PAM to handle PII directly.

## Deploy and test

1. Create a stub hello-world Lambda, and use that for all the handlers.
1. Export `PAM_NAME`, `PAM_EMAIl`, and `PAM_LANG` (using your new language).
1. Deploy the three phases:
   - `cdk deploy ${PAM_NAME}_FE_Infra_PAM`
   - `cdk deploy ${PAM_NAME}_${PAM_LANG}_PAM`
   - `cdk deploy ${PAM_NAME}_FE_Assets_PAM`
1. Implement the Upload endpoint.
   1. Implement the function.
   1. Update the handler.
   1. Run `cdk deploy ${PAM_NAME}_${PAM_LANG}_PAM`.
   1. Test and iterate.
1. Repeat for DetectLabels, GetLabels, and PrepareDownload.
