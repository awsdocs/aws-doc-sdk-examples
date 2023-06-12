# Photo Asset Manager (PAM)

The Photo Asset Management (PAM) example app uses Amazon Rekognition to categorize images, which are stored with Amazon S3 Intelligent-Tiering for cost savings. Users can upload new images. Those images are analyzed with label detection and the labels are stored in an Amazon DynamoDB table. Users can later request a bundle of images matching those labels. When images are requested, they will be retrieved from Amazon S3, zipped, and the user is sent a link to the zip.

[Tech spec](./SPECIFICATION.md)
[Development](./DEVELOPMENT.md)
[Design](./DESIGN.md)

## Stacks & Architecture

PAM CDK has three stacks. While one stack would be ideal, CDK has some limits on accessing final resource ARNs while creating Cloud Formation stacks. The three stacks build on each other, using CFN Outputs to expose information from one to the next.

1. [`{PAM_NAME}-FE-Infra-PAM`](./lib/frontend/infra-stack.ts) creates a Cloud Front distribution. The URL of ths distribution is necessary for the Cognito User Pool in the `{PAM_LANG}` stack, as well as CORS domain headers. It also takes substantially longer to create, modify, and destroy Cloud Front distribution than other resource types in the app.
2. [`{PAM_NAME}-{PAM_LANG}-PAM`](./lib/backend/stack.ts) deploys the buckets, tables, rest APIs, and lambda functions, as well as bundling the lambda function code. Each available language will utilize the bulk of the same resources, with the only difference being their provided CodeAsset bundles. This stack has as CFN Outputs the ARNs and other details necessary for a front end to connect and access API resources.
3. [`{PAM_NAME}-FE-Assets-PAM`](./lib/frontend/asset-stack.ts) builds a static React site with the specific API Gateway, Cognito User Pool, and similar information embedded using data from the `{PAM_LANG}` CFN Outputs.

### App

The stacks are coordinated in [`app.ts`](./bin/app.ts). This looks for the CFN Outputs of each stack, including later stacks if earlier stack outputs are available. When the `FE-Infra` stack has been deployed, the `PAM_LANG` stack becomes available. Similarly, `FE-Assets` is available when the other two are deployed. They should be destroyed in the opposite order.

## `{PAM_NAME}-FE-Infra-PAM`

S3 buckets should not generally be publicly accessible. While S3 websites are a common exception to this, not every organization allows public S3 website buckets. To account for this, the website front end assets are published to a private S3 bucket, and accessed via a Cloud Front distribution. The URL of this distribution is a necessary input to Cognito and API Gateway, but is not known until after the stack has been deployed.

## `{PAM_NAME}-FE-Assets-PAM`

The Photo Asset Manager front end is a single React app used for any implementation of the back end specification. It is compiled from TypeScript into static files for a single page application using Vite. The look and feel are provided by the Cloudscape component library and design system. When compiling the static SPA, the application needs to know the URL of the API Gateway Rest API, as well as the Cognito user pool to handle authentication. These are accessed from CDK Outputs in the other two stacks.

## `{PAM_NAME}-{PAM_LANG}-PAM`

Photo Asset Manager is intended to be an educational learning app that implements a real-world product, albeit a small and understandable one! The core functionality uses Rekognition to use machine learning to detect labels within an image, and then create downloadable zip archives to bundle all images in the data set that match one or more detected labels.

This application is used intermittently and while images are stored indefinitely, they are accessed infrequently. Serverless compute and s3 retention fit these constraints well. With infrequent usage, it is expensive to maintain ec2 instances that are rarely used. S3's intelligent tiering will scale costs to meet the users' patterns, with cost reductions in months they are not accessing images. And that tiering is per object, so downloading pictures of mountains will not change the storage tier for accessing pictures of stars.

The Lambda functions are triggered by user actions, which are coordinated using API Gateway, and by S3 events, which use AWS' built in event systems between S3 and Lambda. Rekognition requires no configuration, working purely from an S3 bucket & object key to return its data. Labels are stored in DynamoDB tables, as there is a clear partition key (the label name) and generally low throughput needs. Typically, there will be a batch of uploads and detect operations, and then later a single download request.

The `PAM_LANG` stack coordinates these resources, as well as any IAM roles and policies that need to be applied to maintain minimum trust environments. Each AWS SDK will have an example implementation of the Lambda functions in that language. See [`DEVELOPMENT.md`](./DEVELOPMENT.md) for detailed instructions on how to add a new language implementation to the PAM cdk.

End user authentication is handled using Amazon Cognito. A user pool is provisioned for the app, and the `PAM_EMAIL` is used to create the first account. A complete user management system is outside the scope of this example. However, developers can follow the Cognito documentation to enable third-party sign-up flows to allow additional users.

## List of Tools & Services

- [AWS Cloud Development Kit (CDK)](https://aws.amazon.com/cdk/)
- [AWS CloudFormation (CFN)](https://aws.amazon.com/cloudformation/)
- [Amazon API Gateway](https://aws.amazon.com/api-gateway/)
- [Amazon CloudFront](https://aws.amazon.com/cloudfront/)
- [Amazon Cognito](https://aws.amazon.com/cognito/)
- [Amazon S3](https://aws.amazon.com/s3/)
- [Cloudscape](https://cloudscape.design/)
- [Vite](https://vitejs.dev/)
