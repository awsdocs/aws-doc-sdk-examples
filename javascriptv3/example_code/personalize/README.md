#  Amazon Personalize JavaScript SDK v3 code examples
The code examples in this directory demonstrate how to work with Amazon Personalize using the AWS SDK for JavaScript version 3 (v3).

Amazon Personalize is a fully managed machine learning service that makes it easy for developers deliver personalized experiences to their users.

## Code examples
In this workspace, you can find the following AWS SDK for JavaScript version 3 (v3) examples for Amazon Personalize:

### Single action examples
- [Create a batch inference job](src/personalize_createBatchInferenceJob.js) (CreateBatchInferenceJobCommand)
- [Create a batch segment job](src/personalize_createBatchSegmentJob.js) (CreateBatchSegmentJobCommand)
- [Create a campaign](src/personalize_createCampaign.js) (CreateCampaignCommand)
- [Create a dataset](src/personalize_createDataset.js) (CreateDatasetCommand)
- [Create a dataset export job](src/personalize_createDatasetExportJob.js) (CreateDatasetExportJobCommand)
- [Create a dataset group](src/personalize_createDatasetGroup.js) (CreateDatasetGroupCommand)
- [Create a dataset import job](src/personalize_createDatasetImportJob.js) (CreateDatasetImportJobCommand)
- [Create a domain dataset group](src/personalize_createDomainDatasetGroup.js) (CreateDatasetGroupCommand)
- [Create a domain schema](src/personalize_createDomainSchema.js) (CreateSchemaCommand)
- [Create an event tracker](src/personalize_createEventTracker.js) (CreateEventTrackerCommand)
- [Create a filter](src/personalize_createFilter.js) (CreateFilterCommand)
- [Create a recommender](src/personalize_createRecommender.js) (CreateRecommenderCommand)
- [Create a schema](src/personalize_createSchema.js) (CreateSchemaCommand)
- [Create a solution](src/personalize_createSolution.js) (CreateSolutionCommand)
- [Create a solution version](src/personalize_createSolutionVersion.js) (CreateSolutionVersion)
- [Get a personalized ranking](src/personalize_getPersonalizedRanking.js) (GetPersonalizedRankingCommand)
- [Get recommendations (custom dataset group)](src/personalize_getRecommendations.js) (GetRecommendationsCommand)
- [Get recommendations from a recommender (domain dataset group)](src/personalize_getRecommendationsFromRecommender.js) (GetRecommendationsCommand)
- [Get recommendation with a filter (custom dataset group)](src/personalize_getRecommendationsWithFilter.js) (GetRecommendationsCommand)
- [Put events](src/personalize_putEvents.js) (PutEventsCommand)
- [Put items](src/personalize_putItems.js) (PutItemsCommand)
- [Put users](src/personalize_putUsers.js) (PutUsersCommand)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

## Important

- As an AWS best practice, grant this code least privilege, or only the
  permissions required to perform a task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the *AWS Identity and Access Management User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific AWS Regions. For more information, see the
  [AWS Regional Services List](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites
- An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.
- AWS credentials. For details, see  [Setting credentials in Node.js](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/setting-credentials-node.html) in the
  *AWS SDK for Javascript (v3) Developer Guide*.

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

```
cd javascriptv3/example_code/personalize/src
npm install
```
3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js
```

## Unit tests
For more information see, the [README](../README.rst).

## Resources
- [AWS SDK for JavaScript v3 repo](https://github.com/aws/aws-sdk-js-v3)
- [Amazon Personalize Developer Guide ](https://docs.aws.amazon.com/personalize/latest/dg/what-is-personalize.html)   
- [AWS SDK for JavaScript v3 API Reference Guide - Personalize Client](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-personalize/index.html)
- [AWS SDK for JavaScript v3 API Reference Guide - Personalize Events Client](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-personalize-events/index.html)
- [AWS SDK for JavaScript v3 API Reference Guide - Personalize Runtime Client](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-personalize-events/index.html) 

 
