# Amazon Personalize Java code examples

This README discusses how to run and test the Java code examples for Amazon Personalize.

## Running the Amazon SageMaker Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a solution by running the **DeleteSolution** example. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you'll need the AWS SDK for Java libraries in your **CLASSPATH**.

	export CLASSPATH=target/sdk-examples-1.0.jar:/path/to/aws-java-sdk/<jar-file-name>.jar

Here **/path/to/aws-java-sdk/<jar-file-name>.jar** is the path to where you extracted or built the AWS SDK for Java JAR file.

For systems with Bash support, once you set the **CLASSPATH**, you can run a particular example as follows.

	java com.example.personalize.ListSolutions


 ## Testing the Amazon Personalize Java files

You can test the Java code examples for SageMaker by running a test file named **PersonalizeServiceIntegrationTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

 ### Properties file
Before running the Personalize JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a solution name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **existingSolutionArn** - The ARN value of an existing solution.   
- **solutionVersionArn** - The ARN of the solution version that is used in the **CreateCampaign** test.
- **campaignName** - The name of a campaign to create.
- **modelName** - The model name used in various tests.
- **datasetGroupArn** - The ARN of the dataset group used to create a solution. 
- **recipeArn** - The ARN of a recipe used in the **DescribeRecipe** test.
- **solutionName** - The name of the solution to create.
- **campaignArn** - The ARN of a campaign used in the **DescribeCampaign** test.
- **userId** - The user ID to provide recommendations for and used in the **GetRecommendations** tests.


**Note**: To set up the data and other requirements needed for the unit tests, follow [Getting Started](https://docs.aws.amazon.com/personalize/latest/dg/getting-started-console.html).

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running PersonalizeServiceIntegrationTest
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as the following. You need to double-check the values that you set in the properties file and run the tests again.

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project SageMakerServiceIntegrationTest:  There are test failures.
	[ERROR];
