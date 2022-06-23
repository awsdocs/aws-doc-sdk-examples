# Amazon Personalize Workflow with AWS Java V2 SDK

This project shows you how to complete the Amazon Personalize
workflow from start to finish with the AWS Java V2 SDK. 

The project trains
two different models with the movie-lens dataset: one with the User-Personalization (`aws-user-personalization`)
recipe for creating personalized recommendations for your users, and one with the 
item-to-item similarities (`aws-sims`) recipe to generate recommendations for items
that are similar to a given item.

Before you run the demo, set up the Java V2 SDK by following the steps here: 
https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html.
This demo assumes that you have set up your credentials in the credentials file in the .aws folder.

**IMPORTANT**

>This project performs Amazon Personalize operations for the account and AWS Region for which you've specified
credentials, and you may incur service charges by running them. 
See the Amazon Personalize Pricing page (https://aws.amazon.com/personalize/pricing/) for details about the charges you can expect for a given
service and operation.

>Some operations perform *destructive* operations on Amazon Personalize resources, such as deleting an Amazon Personalize Campaign,
or an Amazon Personalize dataset. **Be very careful** when running an operation that
may delete or modify resources in your account. It's best to create separate test-only
resources when experimenting with these examples.

## Running the demo

To run the demo, do the following

1. Install a java application server, such as Tomcat. 
2. Name your project and set your region by modifying the following variables in the PersonalizeDemoOnMovieLens20M.java file:
   - `private static final String PREFIX = "your-project-name-here";`
   - `private static final Region region = Region.US_EAST_1;`
3. Build and run PersonalizeDemoOnMovieLens20M.

This project shows you how to create the following Amazon Personalize components with the AWS Java V2 SDK:

**Data import**
- Create Amazon Personalize schemas, dataset groups, datasets and dataset import jobs.
- Import user interactions data in real time with the `PutEvents` API operation.
  
**Training** 

Create solutions and solution versions for the following recipes: 
- User-Personalization (`aws-user-personalization`) recipe.
- Item-to-item similarities (`aws-sims`) recipe.
  
**Model deployment and recommendations**
- Create campaigns to deploy each solution version
- Generate and display real-time recommendations.

## Additional Notes
  
This project does not include the following:
- Adding items and users in real-time using put APIs.
- Creating and using filters to filter recommendations.
- `personalized-ranking` and `popularity-count` recipes.
- Using contextual metadata in training or recommendations.
- Exporting datasets to an Amazon S3 bucket.

Additional features include the following:
- The code is idempotent. You can terminate and execute it repeatedly, and it will resume from its last state. 
- The code is decoupled from the DatasetProvider class. You can provide a new implementation of this class.
- The code provides two higher level APIs - createAndWaitForResource() and 
  deleteResource() - which are blocking APIs and do their work before returning the control. 
