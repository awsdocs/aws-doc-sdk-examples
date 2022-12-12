# Amazon S3 code examples for the AWS SDK for Java V2

## Overview
This README discusses how to run and test the Java code examples for Amazon Simple Storage Service (Amazon S3).

Amazon S3 is cloud object storage with industry-leading scalability, data availability, security, and performance.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

**Note** - The Credential Provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single action

You will find these examples that use the **S3Client** class: 

- [Abort a multipart upload to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/AbortMultipartUpload.java) (AbortMultipartUpload command)
- [Copy an object from one S3 bucket to another S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/CopyObject.java) (CopyObject command)
- [Create and delete an access point for an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/CreateAccessPoint.java) (CreateAccessPoint command)
- [Create an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/CreateBucket.java) (CreateBucket command)
- [Create an Amazon S3 batch job](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/CreateJob.java) (CreateJob command)
- [Delete a policy from an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/DeleteBucketPolicy.java) (DeleteBucketPolicy command)
- [Delete multiple objects from an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/DeleteMultiObjects.java) (DeleteObjects command)
- [Delete the website configuration for an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/DeleteWebsiteConfiguration.java) (DeleteBucketWebsite command)
- [Use the S3Presigner client to create a presigned URL and upload an object to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GeneratePresignedUrlAndUploadObject.java) (PresignPutObject command)
- [Use the S3Presigner client to create a presigned URL and upload an object that contains metadata to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GeneratePresignedUrlMetadata.java) (PresignPutObject command)
- [Use the S3Presigner client to create a presigned URL and upload a PNG image file to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GeneratePresignedUrlUploadImage.java) (PresignPutObject command)
- [Get the access control list (ACL) for an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetAcl.java) (GetObjectAcl command)
- [Get the bucket policy for an existing S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetBucketPolicy.java) (GetBucketPolicy command)
- [Get the content type of an object in an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetObjectContentType.java) (HeadObject command)
- [Read data from an S3 object](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetObjectData.java) (GetObjectAsBytes command)
- [Get an object located in an S3 bucket by using the S3Presigner client object](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetObjectPresignedUrl.java) (PresignGetObject command)
- [Read tags that belong to an object located in an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetObjectTags.java) (GetObjectTagging command)
- [Get an URL for an object located in an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/GetObjectUrl.java) (GetUrl command)
- [Use the AWS Key Management Service to encrypt data prior to placing the data into an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/KMSEncryptionExample.java) (Encrypt command)
- [Add, update, and delete a Lifecycle configuration](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/LifecycleConfiguration.java) (PutBucketLifecycleConfiguration command)
- [Retrieve a list of in-progress multipart uploads.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/ListMultipartUploads.java) (ListMultipartUploads command)
- [Get a list of objects located in a given S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/ListObjects.java) (ListObjects command)
- [Set tags for an object in an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/ManagingObjectTags.java) (putObject command)
- [Get a presigned object using a Java Swing app.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/PresignedSwing.java) (using Swing command)
- [Set the logging parameters for an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/PutBucketLogging.java) (PutObject command)
- [Upload an object to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/PutObject.java) (PutObject commands)
- [Upload an object with metadata to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/PutObjectMetadata.java) (PutObject command)
- [Upload an object with retention configuration to an S3 object](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/PutObjectRetention.java) (PutObjectRetention command)
- [Restore an archived copy of an object back into an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/RestoreObject.java) (RestoreObject command)
- [Delete an empty S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3BucketDeletion.java) (DeleteBucket command)
- [Create, list and delete an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3BucketOps.java) (various commands)
- [Manage cross-origin resource sharing (CORS) for an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3Cors.java) (PutBucketCors command)
- [Get log information](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3Log.java) (ListBuckets command)
- [Set a new access control list (ACL) for an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/SetAcl.java) (PutBucketAcl command)
- [Add a bucket policy to an existing S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/SetBucketPolicy.java) (PutBucketPolicy command)
- [Set the website configuration for an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/SetWebsiteConfiguration.java) (PutBucketWebsite command)
- [Create an S3ControlClient object using a virtual private cloud (VPC) URL](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/VPCCreateJob.java) (createJob command)
- [Set up an S3Client object using a virtual private cloud (VPC) URL](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/VPCS3Example.java) (EndpointOverride command)



You will find these examples that use the **S3AsyncClient** class: 

- [Create an Amazon S3 object using the Async client](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/async/CreateBucketAsync.java) (CreateBucket command)
- [Read data from an Amazon S3 object using the Async client](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/async/GetObjectDataAsync.java) (GetObject command)
- [Use the asynchronous client to place an object into an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/async/S3AsyncOps.java) (PutObject command)

You will find these examples that use the **S3TransferManager** class: 

- [Download a file from an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/transfermanager/FileDownload.java) (DownloadFile command)
- [Download all objects in an S3 bucket to a local directory](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/transfermanager/DownloadToDirectory.java) (DownloadDirectory command)
- [Copy an object to another S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/transfermanager/ObjectCopy.java) (CopyRequest command)
- [Upload a file to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/transfermanager/FileUpload.java) (UploadFile command)
- [Upload a local directory to an S3 bucket](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/transfermanager/DirectoryUpload.java) (UploadDirectory command)


### Scenario

- [Perform various Amazon S3 operations](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3Scenario.java) (multiple commands)


### Cross-service

- [Build a Spring Boot web application that streams Amazon S3 content over HTTP](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/create_spring_stream_app) 
- [Detect PPE in images with Amazon Rekognition](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_lambda_ppe) 
- [Create a dynamic web application that analyzes photos using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_photo_analyzer_app) 

## Running the examples
To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 

Some of these examples perform *destructive* operations on AWS resources, such as deleting a table. **Be very careful** when running an operation that deletes or modifies AWS resources in your account.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

You can test the Amazon S3 Java code examples by running a test file named **AmazonS3Test**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon S3 resources and may incur charges on your account._

 ### Properties file
Before running the Amazon S3 JUnit tests, you must define values in the **config.properties** file located in the **src/main/resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define an object key required for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **bucketName** - The name of the bucket to use. For example, **buckettestfeb7**.
- **bucketNamePolicy** - The name of an existing bucket to which a policy is applied (used in the **setBucketPolicy** and **getBucketPolicy** tests). 
- **presignBucket** - The name of the bucket to use in presign operations. For example, **bucketpresign**.
- **objectKey** – The name of the object to use. For example, **book.pdf**.
- **presignKey** – The name of the text object to use in the presign tests. For example, **note.txt**.
- **path** – The path name used in the **GetObjectData** test. For example **/AWS/AdobePDF.pdf**.
- **objectPath** – The path where the object is located. For example, **/AWS/book2.pdf**.
- **toBucket** - The name of another bucket in your account. For example, **febbucket101**.
- **policyText** – The location of a text file that defines a policy. For example, **/AWS/bucketpolicy.txt** (an example of this file is shown below).
- **id**  - The ID of the user who owns the bucket. You can get this value from the AWS Management Console. This value appears as a GUID value (choose the *Permissions* tab, and then the *Access Control List* tab).
- **accountId** - Your account id value required for the **CreateAccessPoint** test.
- **accessPointName** - The name of the access point required for the **CreateAccessPoint** test.
- **encryptObjectName** - The name of the object to encrypt required for the **KMSEncryptionExample** test.
- **encryptObjectPath** - The path to a TXT file to encrypt and place into a Amazon S3 bucket. This value is required for the **KMSEncryptionExample** test.
- **encryptOutPath** - The path where a text file is written to after it's decrypted. This value is required for the **KMSEncryptionExample** test.
- **keyId** - The id of the AWS KMS key to use to encrypt/decrypt the data. You can obtain the key ID value from the AWS Management Console. This value is required for the **KMSEncryptionExample** test.


###  Sample policy text

For the purpose of the JUnit tests, you can use the following example content for the policy text. Be sure to specify the correct Amazon Resource Name (ARN) bucket name in the **Resource** section; otherwise, your test is not successful.

	{
   	  "Version":"2012-10-17",
   	  "Statement":[
      	{
         "Sid":"PublicRead",
         "Effect":"Allow",
         "Principal":"*",
         "Action":[
            "s3:GetObject",
            "s3:GetObjectVersion"
         ],
         "Resource":[
            "arn:aws:s3:::<change to an existing bucket>/*"
         ]
      }
   ]
}

### Command line

To execute the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AmazonS3Test
	Running Amazon S3 Test 1
	Running Amazon S3 Test 2
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as the following. You need to double-check the values that you set in the properties file and run the tests again. Also, ensure that you specify the correct resource name in the **Sample policy** text file, and the correct owner ID value (you can retrieve this value from the AWS Management Console).

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];


## Additional resources
* [Developer guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [Amazon Simple Storage Service User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html).
* [Interface S3Client](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/S3Client.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

