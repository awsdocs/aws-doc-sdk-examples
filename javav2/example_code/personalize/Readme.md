# Amazon Personalize Java code examples

This README discusses how to run and test the Java code examples for Amazon Personalize.

Amazon Personalize is a fully managed machine learning service that makes it easy for developers to deliver personalized experiences to their users. It reflects the knowledge and experience that Amazon has in building personalization systems.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action
- [Create a batch inference job](src/main/java/com/example/personalize/CreateBatchInferenceJob.java) (createBatchInferenceJob command)
- [Create a campaign](src/main/java/com/example/personalize/CreateCampaign.java) (createCampaign command)
- [Create a dataset](src/main/java/com/example/personalize/CreateDataset.java) (createDataset command)
- [Create a dataset export job](src/main/java/com/example/personalize/CreateDatasetExportJob.java) (createDatasetExportJob command)
- [Create a dataset group](src/main/java/com/example/personalize/CreateDatasetGroup.java) (createDatasetGroup command)
- [Create a dataset import job](src/main/java/com/example/personalize/CreateDatasetImportJob.java) (createDatasetImportJob command)
- [Create a domain dataset group](src/main/java/com/example/personalize/CreateDatasetGroup.java) (createDatasetGroup command)
- [Create a domain schema](src/main/java/com/example/personalize/CreateDomainSchema.java) (createSchema command)
- [Create an event tracker](src/main/java/com/example/personalize/CreateEventTracker.java) (createEventTracker command)
- [Create a filter](src/main/java/com/example/personalize/CreateFilter.java) (createFilter command)
- [Create a recommender](src/main/java/com/example/personalize/CreateRecommender.java) (createRecommender command)
- [Create a schema](src/main/java/com/example/personalize/CreateSchema.java) (createSchema command)
- [Create a solution](src/main/java/com/example/personalize/CreateSolution.java) (createSolution command)
- [Create a solution version](src/main/java/com/example/personalize/CreateSolutionVersion.java) (createSolutionVersion command)
- [Delete a campaign](src/main/java/com/example/personalize/DeleteCampaign.java) (deleteCampaign command)
- [Delete a solution](src/main/java/com/example/personalize/DeleteSolution.java) (deleteSolution command)
- [Delete event tracker](src/main/java/com/example/personalize/DeleteEventTracker.java) (deleteEventTracker command)
- [Describe a campaign](src/main/java/com/example/personalize/DescribeCampaign.java) (describeCampaign command)
- [Describe a recipe](src/main/java/com/example/personalize/DescribeRecipe.java) (describeRecipe command)
- [Describe a solution](src/main/java/com/example/personalize/DescribeSolution.java) (describeSolution command)
- [Get filtered recommendations](src/main/java/com/example/personalize/FilterRecommendations.java) (filterRecommendations command)
- [Get personalized rankings](src/main/java/com/example/personalize/GetPersonalizedRanking.java) (getPersonalizedRanking command)
- [Get recommendations (custom dataset group)](src/main/java/com/example/personalize/GetRecommendations.java) (getRecommendations command)
- [Get recommendation from a recommender](src/main/java/com/example/personalize/GetRecommendationsFromRecommender.java) (getRecommendations command)
- [List campaigns](src/main/java/com/example/personalize/ListCampaigns.java) (listCampaigns command)
- [List dataset groups](src/main/java/com/example/personalize/ListDatasetGroups.java) (listDatasetGroups command)
- [List recipes](src/main/java/com/example/personalize/ListRecipes.java) (listRecipes command)
- [List solutions](src/main/java/com/example/personalize/ListSolutions.java) (listSolutions command)
- [Put events](src/main/java/com/example/personalize/PutEvents.java) (putEvents command)
- [Put items](src/main/java/com/example/personalize/PutItems.java) (putItems command)
- [Put users](src/main/java/com/example/personalize/PutUsers.java) (putUsers command)

## Running the examples
To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information,
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).

Some of these examples perform *destructive* operations on AWS resources, such as deleting a table. **Be very careful** when running an operation that deletes or modifies AWS resources in your account.


## Tests

You can test the Java code examples for Amazon Personalize by running a test file named **PersonalizeTest** to create
a custom dataset group, or **PersonalizeDomainTest*** to create a domain dataset group.
These files use JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line with Maven. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

### Properties file
Before running the Amazon Personalize JUnit tests, you must define values in either of the following .properties files
in the **resources** folder: complete the **config.properties** file for custom dataset groups or the **domain-dsg-config.properties**
for domain dataset groups. These files contain values that are required to run the JUnit tests.
For example, you specify a solution name used in the tests. If you do not define all values, the JUnit tests fail.

**Note**: To make sure you have completed permissions requirements, complete [Setting up Amazon Personalize](https://docs.aws.amazon.com/personalize/latest/dg/setup.html).

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running PersonalizeTest
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
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project PersonalizeServiceIntegrationTest:  There are test failures.
	[ERROR];

## Additional resources
* [Developer guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [Amazon Personalize Developer Guide](https://docs.aws.amazon.com/personalize/latest/dg/what-is-personalize.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
