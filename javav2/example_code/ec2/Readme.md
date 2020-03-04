# Amazon Elastic Compute Cloud (Amazon EC2) Java Readme

A README that discusses how to run and test the Java Elastic Compute Cloud (Amazon EC2)  code examples.

## Running the Amazon EC2  Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and region for which you've specified credentials, and you may incur AWS service charges by running them. 

Please visit the `AWS Pricing  

<https://aws.amazon.com/pricing/>`_ 
page for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a security group. **Be very careful** when running an operation that 
deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you'll need the AWS SDK for Java libraries in your **CLASSPATH**:

	export CLASSPATH=target/sdk-examples-1.0.jar:/path/to/aws-java-sdk/<jar-file-name>.jar

Where  **/path/to/aws-java-sdk/<jar-file-name>.jar** is the path to where you extracted or built the AWS Java SDK jar.

Once you set the **CLASSPATH**, you can run a particular example like this:

	java com.example.ec2.FindRunningInstances

For systems with bash support.

 ## Testing the Amazon EC2 Java files

You can test the Amazon EC2 Java code examples by running a test file named **AWSEC2ServiceIntegrationTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/) .

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that test 3 passed:

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulate real Amazon EC2 resources and may incur charges on your account._

 ### Properties file
Before running the Amazon EC2 JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **instanceId** - the id of an instance. You can obtain this value from the AWS Console. 
- **ami** – an ami value. You can obtain this value from the AWS Console.
- **instanceName** – an instance name. You can obtain this value from the AWS Console. 
- **keyPair** – a key pair to use. For example, **TestKeyPair**.
- **groupName** – ta group name to use. For example, **TestSecGroup**.
- **groupDesc** – a description of the group. For example, **Test Group**.
- **vpcId** – a VCP ID. You can obtain this value from the AWS Console. 

### Command line
To execute the JUnit tests from the command line, you can use the following command:

		mvn test
You will see output from the JUnit tests, as shown here:

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AWSEC2ServiceIntegrationTest
	Running Amazon EC2 Test 1
	Running Amazon EC2 Test 2
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as below. You need to double check the values that you set in the properties file and run the tests again. 

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];
