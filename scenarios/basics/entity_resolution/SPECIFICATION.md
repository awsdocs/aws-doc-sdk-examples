# Specification for the AWS Entity Resolution Service Scenario

## Overview

This SDK Basics scenario demonstrates how to interact with AWS Entity Resolution
using an AWS SDK. It demonstrates various tasks such as creating a schema
mapping, creating an matching workflow, starting a workflow, and so on. Finally,
this scenario demonstrates how to clean up resources.

## Resources

This Basics scenario requires an IAM role that has permissions to work with the
AWS Entity Resolution service, an AWS Glue database and a table, and two S3
buckets.
A [CDK script](../../../resources/cdk/entityresolution_resources/README.md
) is provided to create these resources.

## Hello AWS Entity Resolution

This program is intended for users not familiar with the AWS Entity Resolution
Service to easily get up and running. The program uses a
`listMatchingWorkflowsPaginator` to demonstrate how you can read through
workflow information.

## Basics Scenario Program Flow

The AWS Entity Resolution Basics scenario executes the following operations.

1. **Create a schema mapping**:
    - Description: Creates a schema mapping by invoking the
      `createSchemaMapping` method.
    - Exception Handling: Check to see if a `ConflictException` is thrown, which
      indicates that the schema mapping already exists. If the exception is
      thrown, display the information and end the program.

2. **Create a Matching Workflow**:
    - Description: Creates a new matching workflow that defines how entities
      should be resolved and matched. The method `createMatchingWorkflow` is
      called.
    - Exception Handling: Check to see if a `ConflictException` is thrown, which
      is thrown if the matching workflow already exists. ALso check to see if a `ValidationException` is thrown. If so, display the message and end the program.

3. **Start Matching Workflow**:
    - Description: Initiates a matching workflow by calling the
      `startMatchingJob` method to process entity resolution based on predefined
      configurations.
    - Exception Handling: Check to see if an `ConflictException` is thrown,
      which indicates that the matching workflow job is already running. If the
      exception is thrown, display the message and end the program.

4. **Get Workflow Job Details**:
    - Description: Retrieves details about a specific matching workflow job by
      calling the `getMatchingJob` method.
    - Exception Handling: Check to see if an `ResourceNotFoundException` is
      thrown, which indicates that the workflow cannot be found. If the
      exception is thrown, display the message and end the program.

5. **List Matching Workflows**:
    - Description: Lists all matching workflows created within the account by
      calling the `listMatchingWorkflows` method.
    - Exception Handling: Check to see if an `CompletionException` is thrown. If
      so, display the message and end the program.

6. **Get Schema Mapping**:
    - Description: Returns the `SchemaMapping` of a given name by calling the
      `getSchemaMapping` method.
    - Exception Handling: Check to see if a `ResourceNotFoundException` is
      thrown. If so, display the message and end the program.

7. **Tag Resource**:
    - Description: Adds tags associated with an AWS Entity Resolution resource
      by calling the`tagResource` method.
    - Exception Handling: Check to see if a `ResourceNotFoundException` is
      thrown. If so, display the message and end the program
8. **Delete Matching Workflow**:
    - Description: Deletes a specified matching workflow by calling the
      `deleteMatchingWorkflow` method.
    - Exception Handling: Check to see if an `ConflictException` is thrown. If
      so, display the message and end the program.

### Program execution

The following shows the output of the AWS Entity Resolution Basics scenario in
the console.

```
Welcome to the AWS Entity Resolution Scenario. 
AWS Entity Resolution is a fully-managed machine learning service provided by
Amazon Web Services (AWS) that helps organizations extract, link, and
organize information from multiple data sources. It leverages natural
language processing and deep learning models to identify and resolve
entities, such as people, places, organizations, and products,
across structured and unstructured data.

With Entity Resolution, customers can build robust data integration
pipelines to combine and reconcile data from multiple systems, databases,
and documents. The service can handle ambiguous, incomplete, or conflicting
information, and provide a unified view of entities and their relationships.
This can be particularly valuable in applications such as customer 360,
fraud detection, supply chain management, and knowledge management, where
accurate entity identification is crucial.

The `EntityResolutionAsyncClient` interface in the AWS SDK for Java 2.x
provides a set of methods to programmatically interact with the AWS Entity
Resolution service. This allows developers to automate the entity extraction,
linking, and deduplication process as part of their data processing workflows.
With Entity Resolution, organizations can unlock the value of their data,
improve decision-making, and enhance customer experiences by having a reliable,
comprehensive view of their key entities.

Enter 'c' followed by <ENTER> to continue:
c

To prepare the AWS resources needed for this scenario application, the next step uploads
a CloudFormation template whose resulting stack creates the following resources:
 - An AWS Glue Data Catalog table
 - An AWS IAM role
 - An AWS S3 bucket
 - An AWS Entity Resolution Schema
                            
It can take a couple minutes for the Stack to finish creating the resources.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Upload the JSON to the glue-5ffb912c3d534e8493bac675c2a3196d S3 bucket if it does not exist
[
  {
    "id": "1",
    "name": "Alice Johnson",
    "email": "alice.johnson@example.com"
  },
  {
    "id": "2",
    "name": "Bob Smith",
    "email": "bob.smith@example.com"
  },
  {
    "id": "3",
    "name": "Charlie Black",
    "email": "charlie.black@example.com"
  }
]


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
The JSON exists in glue-5ffb912c3d534e8493bac675c2a3196d

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Create Schema Mapping
Entity Resolution Schema Mapping aligns and integrates data from
multiple sources by identifying and matching corresponding entities
like customers or products. It unifies schemas, resolves conflicts,
and uses machine learning to link related entities, enabling a
consolidated, accurate view for improved data quality and decision-making.

In this example, the schema mapping lines up with the fields in the JSON. That is,
it contains these fields: id, name, and email.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Schema Mapping Created Successfully!

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Create an AWS Entity Resolution Workflow. 
An Entity Resolution matching workflow identifies and links records
across datasets that represent the same real-world entity, such as
customers or products. Using techniques like schema mapping,
data profiling, and machine learning algorithms,
it evaluates attributes like names or emails to detect duplicates
or relationships, even with variations or inconsistencies.
The workflow outputs consolidated, de-duplicated data, 


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Workflow created successfully.
The workflow ARN is: arn:aws:entityresolution:us-east-1:814548047983:matchingworkflow/MyMatchingWorkflow450

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
3. Start the matching job of the MyMatchingWorkflow450 workflow.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job ID: ec2dbd1717624b2b806ed93a04c20049
The matching job was successfully started.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Get details for job ec2dbd1717624b2b806ed93a04c20049

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job status: QUEUED
Job details: GetMatchingJobResponse(JobId=ec2dbd1717624b2b806ed93a04c20049, StartTime=2025-01-30T18:37:57.475Z, Status=QUEUED)
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Get Schema Mapping.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Attribute Name: id, Attribute Type: UNIQUE_ID
Attribute Name: name, Attribute Type: STRING
Attribute Name: email, Attribute Type: STRING
Schema mapping retrieval completed.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. List Schema Mappings.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Tag the schema450resource.
Tags can help you organize and categorize your Entity Resolution resources.
You can also use them to scope user permissions by granting a user permission
to access or change only resources with certain tag values.
In Entity Resolution, SchemaMapping and MatchingWorkflow can be tagged. For this example,
the SchemaMapping is tagged.

Successfully tagged the resource.

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Delete the AWS Entity Resolution Workflow.
You cannot delete a workflow that is in a running state.
Would you like to wait for the workflow to complete.
This can take up to 30 mins (y/n).

n

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
This concludes the AWS Entity Resolution scenario.
--------------------------------------------------------------------------------


```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.

| action                  | metadata file          | metadata key                  |
|-------------------------|------------------------|-------------------------------|
| `createWorkflow`        | entity_metadata.yaml   | entity_CreateWorkflow         |
| `createSchemaMapping`   | entity_metadata.yaml   | entity_CreateMapping          |
| `startMatchingJob`      | entity_metadata.yaml   | entity_StartMatchingJob       |
| `getMatchingJob`        | entity_metadata.yaml   | entity_GetMatchingJob         |
| `listMatchingWorkflows` | entity_metadata.yaml   | entity_ListMatchingWorkflows  |
| `getSchemaMapping`      | entity_metadata.yaml   | entity_GetSchemaMapping       |
| `listSchemaMappings`    | entity_metadata.yaml   | entity_ListSchemaMappings     |
| `tagResource `          | entity_metadata.yaml   | entity_TagResource            |
| `deleteWorkflow `       | entity_metadata.yaml   | entity_DeleteWorkflow         |
| `deleteMapping `        | entity_metadata.yaml   | entity_DeleteSchemaMapping    |
| `listMappingJobs `      | entity_metadata.yaml   | entity_Hello                  |
| `scenario`              | entity_metadata.yaml   | entity_Scenario               |




