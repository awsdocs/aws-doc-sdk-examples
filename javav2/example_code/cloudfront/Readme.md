# Amazon CloudFront Java code examples

This README discusses how to run and test the Java code examples for Amazon CloudFront.

## Running the Amazon CloudFront Java files

The credential provider used in all code examples is ProfileCredentialsProvider. For more information,
see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you
may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details
about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a function. **Be very
careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate
test-only resources when experimenting with these examples.

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build
AWS SDK for Java projects. For more information,
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html)
.

## Testing the Amazon CloudFront files

You can test the Java code examples with two JUnit5 test classes.

The `CloudFrontTest` class tests the following files:

| Java file          | `config.properties` entry required | Comment                                                                       |
|--------------------|------------------------------------|-------------------------------------------------------------------------------|
| CreateFunction     |                                    | Uses provided `CF_function.js` file                                           |
| DescribeFunction   |                                    |                                                                               |
| DeleteFunction     |                                    |                                                                               |
| ListFunction       |                                    |                                                                               |
| GetDistribution    |                                    |                                                                               |
| ModifyDistribution | `distributionId`                   | This is the distributionId of an existing distribution, e.g., `EHUCVLZEPS70R` |

The `CloudFrontSigningTest` class tests the following files:

| Java file                   | `config.properties` entry required | Comment                                                                                                                                                                                                                                                                                                                                           |
|-----------------------------|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CreatePublicKey             | `publicKeyFileName`                | Put a copy of your public key file in `src/main/resources` directory. <br/>The public key is the counterpart to the private key you use to sign.<br/>See the [CloudFront Developer Guide](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/private-content-trusted-signers.html#private-content-creating-cloudfront-key-pairs). |
| CreateKeyGroup              |                                    |                                                                                                                                                                                                                                                                                                                                                   |
| CreateOriginAccessControl   |                                    |                                                                                                                                                                                                                                                                                                                                                   |
| CreateDistribution          |                                    |                                                                                                                                                                                                                                                                                                                                                   |
| CreateBucketPolicy          |                                    | Creates and uploads the bucket policy.                                                                                                                                                                                                                                                                                                            |
| CreateCannedPolicyRequest   | `privateKeyFullPath`               | `privateKeyFullPath` is the full path to private key whose public key is used by the CreatePublicKey file.                                                                                                                                                                                                                                        |
| CreateCustomPolicyRequest   | `privateKeyFullPath`               |                                                                                                                                                                                                                                                                                                                                                   |
| SigningUtilities            |                                    | Contains methods that sign URLs and cookies.                                                                                                                                                                                                                                                                                                      |
| DeleteDistribution          |                                    | Deletes the distribution.                                                                                                                                                                                                                                                                                                                         |
| DeleteDistributionResources |                                    | Deletes the origin access control, key group, and public key.                                                                                                                                                                                                                                                                                     |

`CloudFrontSigningTest` programmatically creates a CloudFront distribution along with the required setup 
to test that signed URLs/cookies are required to access S3 items through CloudFront. As part of the setup, 
the `CloudFrontSigningTest` prepares and deletes an S3 bucket along with uploading and deleting 
the provided file, `index.html`. All resources created during the run of the tests are deleted at the end.

The `CloudFrontSigningTest` tests take several minutes to run. Deploying the distribution, so that the test can run for signing URLs and cookies,
takes a couple of minutes. Undeploying the distribution, so that it can be deleted, also takes a couple of minutes.

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test
runs, messages are logged that inform you of what the tests are doing and if a test succeeds or fails. For example, the following message
informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

### Properties file

Before running the CloudFront JUnit tests, you must define values in the **config.properties** file located in the
`src/main/resources` folder. This file contains values that are required to execute the JUnit tests. 
If you do not define all values, the JUnit tests fail.

Define the following values to run the JUnit tests:

**distributionId** - The distribution id of an exisiting CloudFront distribution. Follow [these steps](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/GettingStarted.SimpleDistribution.html) to manually
set up a simple CloudFront distribution using the AWS console if you don't already have a distribution. 
Use the distribution id as the value of this property (something similar to `E1LK64FWG38EAW`)

**publicKeyFileName** - The name of the public key file that you copied to `src/main/resources`. This file is the public
key counterpart of the private key you specify with the `privateKeyFullPath` property (for example `cf_public_key.pem`)

**privateKeyFullPath** - The full system path and file name of the private key used for signing (for example, `/Users/user/cf_keypair/cf_private_key.der`).
See the developer guide for [creating a key pair](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/private-content-trusted-signers.html#private-content-creating-cloudfront-key-pairs).

### Command line

To execute the JUnit tests from the command line, you can use the following command.

		mvn test

This Maven command will run both tests. You will see output from the JUnit tests similar to the following.
```
[INFO]
[INFO] --- maven-surefire-plugin:2.22.1:test (default-test) @ CloudFrontJ2 ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running CloudFrontTest
Test 1 passed
Test 2 passed
Test 3 passed
Function name is FunctionUploadedByJava7820550d-c5d1-4923-9f55-176a497b5ee4
Function runtime is cloudfront-js-1.0
Test 4 passed
The Distribution ARN is arn:aws:cloudfront::228601887821:distribution/EHUCVLZEPS70R
Test 5 passed
Test 6 passed
FunctionUploadedByJava7820550d-c5d1-4923-9f55-176a497b5ee4 was successfully deleted.
Test 7 passed
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.569 s - in CloudFrontTest
[INFO] Running com.example.cloudfront.CloudFrontSigningTest
2022-11-30 13:24:20 [main] INFO  com.example.cloudfront.S3TestUtils:27 - [cf-test-bucket4b560a63-fb6a-4d3e-af08-7a8d4ba589c1] bucket created
2022-11-30 13:24:21 [main] INFO  com.example.cloudfront.S3TestUtils:42 - public access blocked for bucket [cf-test-bucket4b560a63-fb6a-4d3e-af08-7a8d4ba589c1]
2022-11-30 13:24:21 [main] INFO  com.example.cloudfront.S3TestUtils:51 - object owner enforced for [cf-test-bucket4b560a63-fb6a-4d3e-af08-7a8d4ba589c1]
2022-11-30 13:24:21 [main] INFO  com.example.cloudfront.S3TestUtils:61 - [/Users/user/projects/cloudfront/target/classes/index.html] uploaded to [cf-test-bucket4b560a63-fb6a-4d3e-af08-7a8d4ba589c1] bucket
2022-11-30 13:24:22 [main] INFO  com.example.cloudfront.CreatePublicKey:26 - Public key created with id: [K3819C0RW11MQY]
2022-11-30 13:24:22 [main] INFO  com.example.cloudfront.CreateKeyGroup:18 - KeyGroup created with ID: [09d6b9d3-a2cd-435f-b8d1-1a26c2b7eef0]
2022-11-30 13:24:22 [main] INFO  com.example.cloudfront.CreateOriginAccessControl:23 - Origin Access Control created. Id: [E1Q3L4DI27PLLD]
2022-11-30 13:24:23 [main] INFO  com.example.cloudfront.CreateDistribution:77 - Distribution created. DomainName: [d1rccinn8s3ac4.cloudfront.net]  Id: [E1LK64FWG38EAW]
2022-11-30 13:24:23 [main] INFO  com.example.cloudfront.CreateDistribution:78 - Waiting for distribution to be deployed ...
2022-11-30 13:42:29 [main] INFO  com.example.cloudfront.CreateDistribution:83 - Distribution deployed. DomainName: [d1rccinn8s3ac4.cloudfront.net]  Id: [E1LK64FWG38EAW]
2022-11-30 13:42:30 [main] INFO  com.example.cloudfront.CreateBucketPolicy:41 - Bucket access police created
2022-11-30 13:42:30 [main] INFO  com.example.cloudfront.CreateBucketPolicy:51 - Bucket access policy successfully uploaded
2022-11-30 13:42:30 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:128 - Test 1 passed
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:148 - Test 2 passed
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:18 - Signed URL: [https://d1rccinn8s3ac4.cloudfront.net/index.html?Expires=1670438551&Signature=krP4IZeOXJD7c4c0TWf8b2ot8gEXz9lg~S-MQmWczTdhvz8cDON-TMbwca~yu~qEGoJFbZplZzdT0TOc-vST1RoE2PeJ4r8TfdzR8WkQJ80MZ0CmkxXnu4sO1wp-dlcIISPBNkAwXVrxzsvr8KvyeL5OtyraLFs~U-o7inUdTRRnNXPMIWCBLGmDPw4pIzah8tn2sihYqKUsAZQ6PNdltE8I1TUKgLlNP4Ji11pf1BfTB2~Ylb0EF192jiH07GWtO-dlxmwYwb3vA4HuVj1si4s0iw-P7oq6SnSOtkenoldXy0WY~mg6DCbMQYwgQVDyVJSzYbh68QYRMAXoBYYvYQ__&Key-Pair-Id=K3819C0RW11MQY]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:159 - Test 3 passed
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:24 - Signed URL: [https://d1rccinn8s3ac4.cloudfront.net/index.html?Policy=eyJTdGF0ZW1lbnQiOiBbeyJSZXNvdXJjZSI6Imh0dHBzOi8vZDFyY2Npbm44czNhYzQuY2xvdWRmcm9udC5uZXQvaW5kZXguaHRtbCIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTY3MDQzODU1MX0sIkRhdGVHcmVhdGVyVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNjY5OTIwMTUxfX19XX0_&Signature=mC1EMH8ElzoleEUcrcAHm3dDjAQfKyS08YqZwpvKPH9ve~F39uf3RMX9JN-q2UwAw0Hu9xHrU4-7MKulEK2vu02dofb7Vv76tnIcuQd3cT8USBz~Rlwv7OEhPgT1ZEGjfAvP5~Oqn2rRzbRS5u7qHZ6EuHEwdrFW7aCqFUdQ5~uoB4PgXlv9WPDLmGq6cC~d7zLXY-YRyyOvqD-l0UoG~CXmsdk2PLsGcEyMUfC0sueCx1g2LjqbuwOAsxBc0cFGj5B4ClKRf88A0UDNdL82ITA6dZ1OfcP0mB1cDBOD2atNBMvW72TFazQJVolZ6fKcoNBgySBS1hunPYqT3bXZ0A__&Key-Pair-Id=K3819C0RW11MQY]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:171 - Test 4 passed
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:24 - Signed URL: [https://d1rccinn8s3ac4.cloudfront.net/index.html?Policy=eyJTdGF0ZW1lbnQiOiBbeyJSZXNvdXJjZSI6Imh0dHBzOi8vZDFyY2Npbm44czNhYzQuY2xvdWRmcm9udC5uZXQvaW5kZXguaHRtbCIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTY3MDQzODU1MX0sIkRhdGVHcmVhdGVyVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNjY5NzQ3MzUxfX19XX0_&Signature=Ye8GhSpfJzFvHQVSYprpU36U~rF4mjKGaTpfXwiLesSLpFKsb0VIPq-JesAAYgqFXHWktbe6BbYRTyJO2lXKQZW8-WJd1F1vlXG14qGGOE3wDW-HdVNP9UE2XpLB9ABpItrA8tsJBD2QfUO0Ko~JEjwL1voDjPzu3i1Mn-j8k1fD-5-9jwM0Y8NxOIoy0SMt4LWTVDZzofuxR-ZPLtzW2gSqkdtxbrTvyspG0anZZZr63K6iLaxNYQPghnGhz4QWDtXQI73Nczc7MEXY0w6THn41DlyUAZmDdxDEKxJP6~-GQBfem850W4NJw0tDoBzJObUROPATXQ6lPDJNamNi6Q__&Key-Pair-Id=K3819C0RW11MQY]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:186 - Test 5 passed
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:30 - Cookie EXPIRES header [CloudFront-Expires=1670438551]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:31 - Cookie KEYPAIR header [CloudFront-Key-Pair-Id=K3819C0RW11MQY]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:32 - Cookie SIGNATURE header [CloudFront-Signature=krP4IZeOXJD7c4c0TWf8b2ot8gEXz9lg~S-MQmWczTdhvz8cDON-TMbwca~yu~qEGoJFbZplZzdT0TOc-vST1RoE2PeJ4r8TfdzR8WkQJ80MZ0CmkxXnu4sO1wp-dlcIISPBNkAwXVrxzsvr8KvyeL5OtyraLFs~U-o7inUdTRRnNXPMIWCBLGmDPw4pIzah8tn2sihYqKUsAZQ6PNdltE8I1TUKgLlNP4Ji11pf1BfTB2~Ylb0EF192jiH07GWtO-dlxmwYwb3vA4HuVj1si4s0iw-P7oq6SnSOtkenoldXy0WY~mg6DCbMQYwgQVDyVJSzYbh68QYRMAXoBYYvYQ__]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:197 - Test 6 passed
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:38 - Cookie POLICY header [CloudFront-Policy=eyJTdGF0ZW1lbnQiOiBbeyJSZXNvdXJjZSI6Imh0dHBzOi8vZDFyY2Npbm44czNhYzQuY2xvdWRmcm9udC5uZXQvaW5kZXguaHRtbCIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTY3MDQzODU1MX0sIkRhdGVHcmVhdGVyVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNjY5OTIwMTUxfX19XX0_]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:39 - Cookie KEYPAIR header [CloudFront-Key-Pair-Id=K3819C0RW11MQY]
2022-11-30 13:42:31 [main] INFO  com.example.cloudfront.SigningUtilities:40 - Cookie SIGNATURE header [CloudFront-Signature=mC1EMH8ElzoleEUcrcAHm3dDjAQfKyS08YqZwpvKPH9ve~F39uf3RMX9JN-q2UwAw0Hu9xHrU4-7MKulEK2vu02dofb7Vv76tnIcuQd3cT8USBz~Rlwv7OEhPgT1ZEGjfAvP5~Oqn2rRzbRS5u7qHZ6EuHEwdrFW7aCqFUdQ5~uoB4PgXlv9WPDLmGq6cC~d7zLXY-YRyyOvqD-l0UoG~CXmsdk2PLsGcEyMUfC0sueCx1g2LjqbuwOAsxBc0cFGj5B4ClKRf88A0UDNdL82ITA6dZ1OfcP0mB1cDBOD2atNBMvW72TFazQJVolZ6fKcoNBgySBS1hunPYqT3bXZ0A__]
2022-11-30 13:42:32 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:208 - Test 7 passed
2022-11-30 13:42:32 [main] INFO  com.example.cloudfront.SigningUtilities:38 - Cookie POLICY header [CloudFront-Policy=eyJTdGF0ZW1lbnQiOiBbeyJSZXNvdXJjZSI6Imh0dHBzOi8vZDFyY2Npbm44czNhYzQuY2xvdWRmcm9udC5uZXQvaW5kZXguaHRtbCIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTY3MDQzODU1Mn0sIkRhdGVHcmVhdGVyVGhhbiI6eyJBV1M6RXBvY2hUaW1lIjoxNjY5NzQ3MzUyfX19XX0_]
2022-11-30 13:42:32 [main] INFO  com.example.cloudfront.SigningUtilities:39 - Cookie KEYPAIR header [CloudFront-Key-Pair-Id=K3819C0RW11MQY]
2022-11-30 13:42:32 [main] INFO  com.example.cloudfront.SigningUtilities:40 - Cookie SIGNATURE header [CloudFront-Signature=e3eKG0Du6G~PXr~2b1rtUJxo2NRMYHs0wRQr9Py7ik7vb4KMG7G~u1G-Di~i6nxLzvlb2-H~RY5YID7QMo1nUbTCaLv7q8jjFIBcV~u6RKk6uN3qTfZCHqyfvWGg1o5tUBTHy5E03E1TRNswSgw-XWCX~YVJIKB-RmDxFfxPuPZ6A5foZ8Sw0V2YIse1K7QiqyHMqCLXJkyqikRDX08T3HD10pduBcw421rVMPyXW3jUbp3baaet-kjKUmfNHhT8UT1UuO5PWh7KQ1Gvwvoj6EEU1GmhFAcEuN6GMPOsW5n7P8bgQKF0iAhL8zlrLFX0Z3OIRPhbCufHxFuVcPlrVw__]
2022-11-30 13:42:32 [main] INFO  com.example.cloudfront.CloudFrontSigningTest:223 - Test 8 passed
2022-11-30 13:42:32 [main] INFO  com.example.cloudfront.S3TestUtils:72 - index.html deleted from cf-test-bucket4b560a63-fb6a-4d3e-af08-7a8d4ba589c1
2022-11-30 13:42:32 [main] INFO  com.example.cloudfront.S3TestUtils:77 - cf-test-bucket4b560a63-fb6a-4d3e-af08-7a8d4ba589c1 bucket deleted
2022-11-30 13:42:33 [main] INFO  com.example.cloudfront.DeleteDistribution:46 - Distribution [E1LK64FWG38EAW] is DISABLED, waiting for deployment before deleting ...
2022-11-30 13:55:38 [main] INFO  com.example.cloudfront.DeleteDistribution:58 - Distribution [E1LK64FWG38EAW] DELETED
2022-11-30 13:55:38 [main] INFO  com.example.cloudfront.DeleteSigningResources:22 - Successfully deleted Origin Access Control [E1Q3L4DI27PLLD]
2022-11-30 13:55:38 [main] INFO  com.example.cloudfront.DeleteSigningResources:33 - Successfully deleted Key Group [09d6b9d3-a2cd-435f-b8d1-1a26c2b7eef0]
2022-11-30 13:55:38 [main] INFO  com.example.cloudfront.DeleteSigningResources:45 - Successfully deleted Public Key [K3819C0RW11MQY]
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1,879.548 s - in com.example.cloudfront.CloudFrontSigningTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  31:25 min
[INFO] Finished at: 2022-11-30T13:55:39-05:00
[INFO] ------------------------------------------------------------------------
```
### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an
error message such as the following. You need to double-check the values that you set in the properties file and run the
tests again.

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];

