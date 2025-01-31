# Java CDK app for stacks used in Java v2 examples

This directory contains Java CDK application that produce stacks for code examples.

The following Java v2 code examples use stacks produced the application in this directory:

* [PutBucketS3EventNotificationEventBridge.java](../../javav2/example_code/s3/src/main/java/com/example/s3/PutBucketS3EventNotificationEventBridge.java)
  * uses stack named `queue-topic` 
* [ProcessS3EventNotificationTest.java](../../javav2/example_code/s3/src/test/java/com/example/s3/ProcessS3EventNotificationTest.java)
  * uses stack named `direct-target` 


The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!
