# Amazon Pinpoint Java Readme

A README that discusses how to run and test the Java (v2) Amazon Pinpoint code examples.

## Running the Amazon Pinpoint Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and region for which you've specified credentials, and you may incur AWS service charges by running them. Visit the AWS Pricing page for details about the charges you can expect for a given service and operation. For details, see https://aws.amazon.com/pricing/.   

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon Pinpoint application. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you'll need the AWS SDK for Java libraries in your **CLASSPATH**:

	export CLASSPATH=target/sdk-s3-examples-1.0.jar:/path/to/aws-java-sdk/<jar-file-name>.jar

Where  **/path/to/aws-java-sdk/<jar-file-name>.jar** is the path to where you extracted or built the AWS Java SDK jar.

Once you set the **CLASSPATH**, you can run a particular example like this:

	java com.example.pinpoint.CreateApp

For systems with bash support.

 ## Testing the Amazon Pinpoint Java files

You can test the Amazon Pinpoint Java code examples by running a test file named **AmazonPinpointServiceIntegrationTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/) .

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that test 3 passed:

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulate real Amazon S3 resources and may incur charges on your account._

 ### Properties file
Before running the Amazon S3  JUnit tests, you must define values in the **config.properties** file located in the **src/main/resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define an application name required for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **appName** - the name of the Pinpoint application. For example, **TestApp2**.

- **bucket** – the name of a S3 bucket to use for the ImportSegments test. 

- **path** – the path where the JSON is located in the bucket for the ImportSegments test. For example, **imports/myjson.json**.

- **roleArn** - the ARN of the role for the ImportSegments test.


###  Sample policy text

For the purpose of the JUnit tests, the **ImportSegments** test uses JSON to import segments. You must place this JSON in the S3 bucket specified in the **bucket** value. Also, this JSON must be located in the **path** location. The following represents an example myjson.json file that you can use for the **ImportSegments** test.   

	{
   "ChannelType":"SMS",
   "Address":"2065550182",
   "Location":{
      "Country":"CAN"
   },
   "Demographic":{
      "Platform":"Android",
      "Make":"LG"
   },
   "User":{
      "UserId":"example-user-id-1"
   }
}{
   "ChannelType":"APNS",
   "Address":"1a2b3c4d5e6f7g8h9i0j1a2b3c4d5e6f",
   "Location":{
      "Country":"USA"
   },
   "Demographic":{
      "Platform":"iOS",
      "Make":"Apple"
   },
   "User":{
      "UserId":"example-user-id-2"
   }
}{
   "ChannelType":"EMAIL",
   "Address":"john.stiles@example.com",
   "Location":{
      "Country":"USA"
   },
   "Demographic":{
      "Platform":"iOS",
      "Make":"Apple"
   },
   "User":{
      "UserId":"example-user-id-2"
   }
}


### Command line

To execute the JUnit tests from the command line, you can use the following command:

		mvn test
You will see output from the JUnit tests, as shown here:

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AmazonPinpointServiceIntegrationTest
	Running Amazon Test 1
	Running Amazon Test 2
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as below. You need to double check the values that you set in the properties file and run the tests again. Also, ensure that you placed the JSON file in the correct S3 bucket location.

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];
