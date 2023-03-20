# Photo Asset Management Tech Spec

This is the Tech Spec for the Photo Asset Management cross services example. This doc details the specifics of the deliverable app.

## Reading this document

This document is broken down into the following sections

* Introduction - A summary of app functionality.
* Development / Deployment - Information on where to find CDK deployment and development instructions.
* Front-end - Describes the resources used to create the front-end, and how it interacts with the back-end.
* Back-end - Describes the resources used to create the back-end.
    * Common - Explains the concept of common resources vs language specific and describes the common resources.
    * Language specific - Explains the programming language specific resources.

* READMES - Defines the information needed for each language specific readme.
* Glossary - Lists common terms.
* Appendices
    * Appendix A - Policy details
    * Appendix B - Lambda triggers

# Introduction
The Photo Asset Management (PAM) example app uses Rekognition to categorize images, which are stored with Amazon S3 Intelligent-Tiering for cost savings. Users can upload new images. Those images are analyzed label detection and the labels are stored in a DynamoDB table. Users can later request a bundle of images matching those labels. When images are requested, they will be retrieved from s3, zipped, and the user will be sent a link to the zip.

<img width="4400" alt="PAM Diagram" src="https://user-images.githubusercontent.com/2723491/226400489-8ce85f78-fd53-4bcb-adda-42a230964a4c.png">

|Area	|Purpose	|Tools	|Per-Language?	|
|---	|---	|---	|---	|
|Front-end|Front end user interface.	|React; CloudScape; S3; CloudFront	|No	|
|Back-end|Underlying pieces that are used for any instance of the application, regardless of Lambda implementation.	|DynamoDB; S3; APIGateway	|No	|
|Lambdas|Business logic for each application specific function.	|Lambda; SDKs: S3, DDB, SNS, Rekognition	|Yes	|

The common stack is used by all deployments of the PAM app. These include the S3 buckets, S3 lifecycles, DynamoDB tables, front-end & user resources, Lambda function skeletons, and policies for IAM, SNS topics. When following the BuildOn README, these resources will be created for the customer by running the CDK script. The number of resources in this example makes it untenable to perform the actions via console.

The resources in Common are deployed for all instances of the app, and use the same CDK definitions. An implementor for the spec in a particular language should be familiar with the resources described, as they will be the resources the Lambda function implementations will interact with. Per language implementors will be creating the several Lambda function implementations, as well as “best practice” CDK layers for their language’s Lambda deployment.

All resources will share a `{NAME}` marker, chosen at CDK run time. While the CDK will truncate any generated IDs to avoid name lengths (such as exceeding the DNS length of 64 characters), it should be kept short and concise for easy legibility in the generated resource IDs. Customer must provide an `{EMAIL}` to use for account creation. This PII will only be used for the Cognito user pool and SNS notification topic.


## Audience

This spec is written for **anyone implementing the business logic** of the PAM app. This includes:

* The AWS Code Examples Cloud Sherpas who provided the common resources as well as a reference implementation of the lambdas in Java.
* The AWS Code Examples MVPeeps who provide a number of implementations in other supported languages.

While developing this example, we have kept two other personas in mind:

* Dan - a hypothetical photographer who will use this app for long-term storage of his photographs, infrequently accessing them based on the labels assigned to each image.
* Customer - an AWS customer learning about the pieces of a fully functional Serverless consumer app, with lambda back-ends, object storage, columnar key data, and managed user pools.

# Development / deployment

Refer to [README.md](../tree/main/resources/cdk/photo_asset_manager/README.md) for deployment instructions and [DEVELOPMENT.md](../tree/main/resources/cdk/photo_asset_manager/DEVELOPMENT.md) for instructions on modifying the CDK script.

# Front-end

The front end is a single React app using the Cloudscape design system. It allows users to authenticate via a Cognito flow. All requests will then be authn/authz to that token. The app is deployed to an s3 website. The front end will only be available via the S3 website bucket name. The name will be `{NAME}-sdk-code-examples-pam.s3.{REGION}.amazonaws.com`. This domain name will be necessary for CORS requests & Cognito configuration.

### Login & API

The static website will redirect to Cognito Hosted UI. The user can log in with their provided email and the
temporary password sent to them during deployment. When authenticated, all requests to the API will include the JWT id_token. The API Gateway endpoint url will be embedded in the static site. Requests to the endpoint will include the access token in the `Authorization` header.

### Upload

An upload button is provided that will allow the user to select an image from their desktop. A PUT request is made
to `/upload` endpoint. The API responds with a pre-signed S3 URL that the client uses to put the image into the storage
bucket.

### View & download

A GET request is made to `/labels`. The labels are displayed to the user who can select 1 or more labels. On clicking the download button, the selected labels are sent to the `/download` endpoint. This endpoint will trigger a lambda that prepares the download, which then sends the user an email notification with a link to download the zip.

# Back-end

## Common

### ⭐ API Gateway

API Gateway provides HTTP API routes for the Lambda integrations `LabelsFn`, `UploadFn`, and `PrepareDownloadFn`. Each endpoint is configured with a Cognito authorizer. Parameters for all routes are provided in the body of the request in a JSON object. Each parameter is a top-level item in the request body JSON object.

|Method	|Route	|Parameters	|Example Response	|Lambda	|
|---	|---	|---	|---	|---	|
|PUT	|/upload	|file_name: string	|{"url": "presigned URL"}	|UploadFn
|GET	|/labels	|	|{"labels": {"maintain": {"count": 5}, "lake": {"count": 3}}|LabelsFn|
|POST	|/download	|labels: string[]	|{} (event)	|PrepareDownloadFn|

### ⭐ Cognito

A cognito user pool is created via the CDK using the email address provided during deployment. A temporary password will be
sent to that email address.

### ⭐ S3 Buckets

PAM uses two buckets. `{NAME}-sdk-code-examples-pam-storage-bucket` (the Storage Bucket) and `{NAME}-sdk-code-examples-pam-working-bucket` (the Working Bucket) provide the long-term intelligent-tiering storage and ephemeral manifest and zip download storage, respectively.

The Storage Bucket has a Notification Configuration when objects are PUT to call the DetectLabels Lambda. The Working Bucket has a Lifecycle Configuration to delete objects after 24 hours.

|Bucket	|Policies	|Use	|
|---	|---	|---	|
|`{NAME}-sdk-code-examples-pam-storage-bucket`	|DetectLabels on jpeg images	|All the images that are being stored in the application.	|
|`{NAME}-sdk-code-examples-pam-working-bucket`	|Delete after 1 day	|Working files with a short lifetime, that are not intended to be stored long term.	|

### ⭐ DynamoDB

PAM uses one DynamoDB tables to track data. The LabelsTable, `{NAME}-SDKCodeExamplesPAM-Labels`, contains the labels found by Rekognition. It has a simple primary key with an attribute `Label` of type `S`. 

|Table	|Key	|Use	|
|---	|---	|---	|
|`{NAME}-SDKCodeExamplesPAM-LabelsTable`	|Label: S	|Track the detected labels and count & list of images for that label.	|

### ⭐ Rekognition

There is no specific configuration necessary for the Rekognition uses in this example.

### ⭐ IAM

IAM roles are created for each function, via the CDK script, with permissions assigned providing each function’s role minimum access to the underlying resources. See each function for specific role permission needs.

## Language specific lambdas

Each language will implement these lambda functions. These functions handle the logic of the application, and execute in response to a user’s action or lifecycle event. Input parameters for user actions will be passed from API Gateway in the request body as a JSON document string. The shape of these documents is described with each function’s specification, below. Lifecycle events have unique formats for the functions they call. User actions have response bodies. The final response returned will be a JSON object, with keys described in each functions’s specification. Lifecycle event functions do not return data.

**Note on code structure:** To make testing easier, an implementation should have three parts. The logic should be a function which takes the specific arguments necessary, performs the work using the SDKs, and returns an object for the result. The lambda function is a handler that parses & marshals event data to the logic, and stringifies the return to a JSON document. A third middleware function, using the HTTP middleware server technology appropriate for the language, does the same as the handler but in a local environment for easy testing.

|Function	|Trigger	|Input Example	|Output Example	|Uses	|
|---	|---	|---	|---	|---	|
|Upload|APIG PUT /upload	|{"file_name": "maintain01.jpg"}	|{"url": "<presigned URL>"}	|`StorageBucket`	|
|DetectLabels|S3 PutObject jpeg	|	|	|`StorageBucket`
`LabelsTable`	|
|LabelsFn|APIG GET /labels	|	|{"labels": {"maintain": {"count": 5}, "lake": {"count": 3}}}	|`LabelsTable`	|
|PrepareDownload|APIG POST /download (event)	|{"labels": ["Mountain", "Lake"]}	|{}	|`LabelsTable`
`WorkingBucket`	|

**TIP**: The Java lambdas were created during the building of this document. Refer to those for implementation insight.

### ⭐ Upload

1. Create a pre-signed S3 URL in the Storage Bucket. The object key shall be the provided file name, prefixed with a UUIDv4 and a /. The presigned URL will allow a `PUT` method operation with `Content-Type` `image/jpeg`. The expiration will live for 5 minutes (5 * 60 * 1000 ms = 300,000 ms).
2. Return a serialized JSON object with a single key, “url”, containing the value of the pre-signed url.

### ⭐ DetectLabels

This Lambda will be triggered by uploads to the Storage Bucket.

1. Run Rekognition’s `detectLabels` on each incoming object.
2. If Rekognition’s `detectLabels` succeeds, update the Labels Table. For each Label key, add the image object key to the list in the Images column and incrementing the Count column. These must be atomic operations. If the enhanced DynamoDB client supports atomic counter fields, use them. Otherwise, the request can use update expressions to atomically update Count and Images.

>UpdateExpression: `ADD Count :one, Images :im``g
`ExpressionAttributeValues: `":one": AttributeValue::N(1), ":img": AttributeValue::S({object_key})`

### ⭐ LabelsFn

1. Load the `Label` and Count columns from the LabelsTable. 
2. Return a JSON document with a single object. The top level object has one property key for each label. Each key has a value of an object with a single property, “count”, whose value is the numeric count of images matching that label.

### ⭐ PrepareDownloadFn

1. Retrieve the keys of all images for the provided labels from `LabelsTable`.
2. Create a Zip file, as appropriate for the language. Place all images into the ZIP and place the ZIP in the Working Bucket.
3. Create a 24-hour Presigned GET URL for the archive file.
4. Send an SNS message including this presigned URL. 

SNS Topics are created using CDK. The `PrepareDownloadFn` lambda will publish messages by calling the SNS service client’s publish(). 

# README

Each implementation will include a README describing the language-specific details. While it should follow the [cross services README template](https://github.com/awsdocs/aws-doc-sdk-examples/wiki/single-cross-service-level-README-template), the content is left to the language writer in the best style for their language.

# Resources

* [In-Repo README for adding a new Lambda](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/cdk/photo_asset_manager/DEVELOPMENT.md)

# Glossary

**AWS Code Examples Cloud Sherpas** created this design & spec, and provides the underlying CDK resources & React front end that customers deploy.
**AWS Code Examples MVPeeps** have created and documented reference implementations of the lambda functions for each supported language.
**Customer** is the AWS customer following the example readme and deploying the application.
**Labels Table** is the DynamoDB table that stores the Rekognition-identified label, the count of items for that label, and the list of image keys matching that label.
**Photo Asset Management (PAM)** is a complete serverless best practices cross service example application.
`{NAME}-sdk-code-examples-pam` and `{NAME}-SDKCodeExamplesPAM` are common prefix identifiers for created resources. `{NAME}` is provided by the customer at application creation time. `{NAME}` must be 24 characters or shorter.
**Storage Bucket** is the bucket with intelligent-tiering storage policies applied. It has the uploaded images only.
**User** is the user who is using the deployed PAM application. Typically this will be the customer.
**Working Bucket** is the bucket that keeps ephemeral working data that is deleted after 24 hours.


# Appendices

## Appendix A - Policy details

**Lambda Function (Upload):**

* `s3:PutObject` on Storage Bucket.

**Lambda Function (DetectLabels):**

* `dyanmodb:PutItem` and `dynamodb:UpdateItem` on LabelsTable.
* `rekognition:DetectLabels` on `*`.
* `s3:ReadObject` on Storage bucket.

**Lambda Function (LabelsFn):**

* `dynamodb:ReadItem` on LabelsTable

**Lambda Function (PrepareDownload):**

* `dynamodb:ReadItem` on LabelsTable.
* `s3:ReadItem` on `StorageBucket`.
* `s3:ReadItem` and `s3:PutItem` on `WorkingBucket`.
* `sns:Subscribe` on `*`
* `sns:Publish` on `arn:aws:sns:{REGION}:{ACCOUNT_ID}/{NAME}-SDKCodeExamplePAM-*`

## Appendix B - Lambda triggers
* Storage bucket [OBJECT_CREATED] - When the user uploads a *.jpg/*.jpeg  to the presigned URL in the storage bucket, the DetectLabelsFn function is invoked.