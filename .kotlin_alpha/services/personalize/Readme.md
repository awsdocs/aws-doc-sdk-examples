# Amazon Personalize Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon Personalize.

## Running the Amazon Personalize Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a solution. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateCampaign** - Demonstrates how to create an Amazon Personalize campaign.
- **CreateSolution** - Demonstrates how to create an Amazon Personalize solution.
- **DeleteCampaign** - Demonstrates how to delete an Amazon Personalize campaign.
- **DeleteSolution** - Demonstrates how to delete an Amazon Personalize solution.
- **DescribeCampaign** - Demonstrates how to describe an Amazon Personalize campaign.
- **DescribeSolution** - Demonstrates how to describe an Amazon Personalize solution.
- **GetRecommendations** - Demonstrates how to return a list of recommended items.
- **ListCampaigns** - Demonstrates how to list Amazon Personalize campaigns.
- **ListDatasetGroups** - Demonstrates how to list Amazon Personalize data set groups.
- **ListRecipes** - Demonstrates how to list Amazon Personalize recipes.
- **ListSolutions** - Demonstrates how to list Amazon Personalize solutions.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
