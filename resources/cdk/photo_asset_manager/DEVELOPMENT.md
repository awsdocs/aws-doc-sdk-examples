# PAM Development

Photo Asset Management (PAM) is an AWS SDK Code Examples cross-service example.
It's one app, with a consistent architecture, implemented in several programming
languages supported by AWS SDKs. This folder has the AWS CDK resources to deploy
the entire app, including AWS resources, the React front end, and the
per-language lambda implementations.

## Adding a new language

To add a new supported implementation language, create a new project in the appropriate language folder.
Implement the lambda functions.
Edit [`./lib/backend/strategies.ts`](./lib/backend/strategies.ts) and add a new `PamLambdaStrategy` for the language.
(Copy or refer to an existing instance.)
Register this language in the `STRATEGIES` constant.
Update the current languages in the README.

### Adding a `PamLambdaStrategy`

The `PamLambdaStrategy` interface describes how the CDK will deploy the Lambdas for a language.
`runtime` uses a constant from the published Lambda [`Runtime`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-lambda.Runtime.html)s.
If a static entry is available, use it; otherwise, create a new Runtime as appropriate.
`timeout` and `memorySize` specify the limits the Lambda will impose.
The defaults might be fine, but expect to need to tune these.

```
export interface PamLambdasStrategy {
  runtime: Runtime;
  timeout: Duration; // Default is 15 minutes
  memorySize: number; // In megabytes
  codeAsset: () => Code; // See below, Bundling Code
  handlers: PamLambdasStrategyHandlers; // See below, Handlers
}
```

#### Bundling Code

The strategy `codeAsset` property is a function that returns a CDK [`Code`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-lambda.Code.html) reference.
This will be the code that Lambda will execute directly.
[`Code.fromAsset`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-lambda.Code.html#static-fromwbrassetpath-options) using the NodeJS [`resolve`](https://nodejs.org/api/path.html#pathresolvepaths) path utility can load any code from the `aws-doc-sdk-examples` repository, and is specified relative to `resources/cdk/photo_asset_manager`.
For interpreted languages, this may be all that is necessary for the code.
For compiled languages, Lambda requires the compiled assets.

CDK can compile resources during the deployment using docker.
To configure this, use the [`bundling`](https://docs.aws.amazon.com/cdk/api/v1/docs/@aws-cdk_aws-s3-assets.AssetOptions.html#bundling) option of `Code.fromAsset`.
Languages with a well-known Runtime usually have a `Runtime.image` property that has an appropriate docker base image.
Otherwise, any public docker image is suitable.

Docker will execute the `command` for the bundling step.
The assets, specified as the first argument to `Code.fromAsset`, are mounted in the container at `/asset-input/` (which is also the working directory).
The command should perform any compilation steps necessary, and move any necessary artifacts to `/asset-output/`.
The contents of `/asset-output/` are archived in the Lambda's s3 bucket, and are the executable that the Runtime will use.

#### Handlers

In this setup, the output bundle has code for all the Lambdas.
To specify which handler gets executed, specify the handler for each.
The exact format of the handler string is language dependent, and will require research for each.
For instance, the Java handler specifies the classpath to load.
The Python handler specifies the entire module and function to execute.

### Implement the Lambda Functions

When implementing Lambda functions, application AWS resources are available in these environment variables.

| Variable              | Usage                                                         |
| --------------------- | ------------------------------------------------------------- |
| `LABELS_TABLE_NAME`   | Name for the Labels Table                                     |
| `STORAGE_BUCKET_NAME` | Name for the Storage Bucket                                   |
| `WORKING_BUCKET_NAME` | Name for the Working Bucket                                   |
| `NOTIFICATION_TOPIC`  | ARN of the SNS topic to sent download ready notifications to. |

#### Upload

1. The input and output are [API Gateway Proxy Events](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format).
1. Read the `file_name` field from the body (which need to be parsed as JSON).
1. Prepend a UUID.
1. Creat a presigned PUT request for the Storage Bucket.
1. Return the presigned URL in the `url` response body (which will need to be encoded as JSON).

#### DetectLabels

1. The input is an [S3 Notification event](https://docs.aws.amazon.com/lambda/latest/dg/with-s3.html); there is no response.
1. For each record, run the s3 object through rekognition's [`detectLabels`](https://docs.aws.amazon.com/rekognition/latest/APIReference/API_DetectLabels.html).
1. Upsert each recognized label to the Labels table, incrementing the Count column and (atomically) inserting the key to the Images column.

#### GetLabels

1. The input and output are [API Gateway Proxy Events](https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format).
1. There is no input data needed by this function.
1. Read the Labels Table.
1. Prepare a response Map
   - Each key is a Label string
   - Each value is an object with a property `count`, with the integer count column.
1. Return this Map as the JSON encoded response body.

#### PrepareDownload

1. The input is a Map with the JSON request body as a string in the `body` field; there is no response.
1. Parse the `body` as JSON and get the `labels` column, an array of strings.
1. Load all Images from the Labels Table matching the selected labels into a set (no duplicates).
1. Read those images from the Storage bucket, and write them to a single zip archive in the Working bucket.
1. Send a message to the Notification Topic with a link to a presigned GET url for that zip archive.

## Deploying and Testing

1. Create a stub hello-world lambda, and use that for all the handlers.
1. Export `PAM_NAME`, `PAM_EMAIl`, and `PAM_LANG` (using your new language).
1. Deploy the three phases
   - `cdk deploy ${PAM_NAME}_FE_Infra_PAM`
   - `cdk deploy ${PAM_NAME}_${PAM_LANG}_PAM`
   - `cdk deploy ${PAM_NAME}_FE_Assets_PAM`
1. Implement the Upload endpoint.
   1. Implement the function.
   1. Update the handler.
   1. Run `cdk deploy ${PAM_NAME}_${PAM_LANG}_PAM`.
   1. Test & iterate
1. Repeat for DetectLabels, GetLabels, and PrepareDownload.
