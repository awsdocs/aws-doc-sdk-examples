# Amazon Athena Java Readme

A README that discusses how to run and test the Java Amazon Athena code examples.

## Running the Amazon Athena Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and region for which you've specified credentials, and you may incur AWS service charges by running them. Visit the AWS Pricing page for details about the charges you can expect for a given service and operation. For details, see https://aws.amazon.com/pricing/.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a named query example. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you'll need the AWS SDK for Java libraries in your **CLASSPATH**:

	export CLASSPATH=target/sdk-examples-1.0.jar:/path/to/aws-java-sdk/<jar-file-name>.jar

Where  **/path/to/aws-java-sdk/<jar-file-name>.jar** is the path to where you extracted or built the AWS Java SDK jar.

Once you set the **CLASSPATH**, you can run a particular example like this:

	java aws.example.athena.ListNamedQueryExample

For systems with bash support.

**Note**: These code examples depend upon values defined in the **ExampleConstants** Java file. Besure to set values that are applicable to your environment. For example, specify the database that you created by following https://docs.aws.amazon.com/athena/latest/ug/work-with-data.html.

 ## Testing the Amazon Athena files

You can test the Amazon Athena Java code examples by running a test file named **AWSAthenaServiceIntegrationTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/) .

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that test 3 passed:

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulate real Amazon resources and may incur charges on your account._

 ### Properties file
Before running the Amazon Athena JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define a named query to use in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **nameQuery** - a named query.  

### Command line
To execute the JUnit tests from the command line, you can use the following command:

		mvn test
You will see output from the JUnit tests, as shown here:

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AWSAthenaServiceIntegrationTest
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
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
