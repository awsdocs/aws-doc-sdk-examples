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

5. **Get Schema Mapping**:
    - Description: Returns the `SchemaMapping` of a given name by calling the
      `getSchemaMapping` method.
    - Exception Handling: Check to see if a `ResourceNotFoundException` is
      thrown. If so, display the message and end the program.

6. **List Matching Workflows**:
    - Description: Lists all matching workflows created within the account by
      calling the `listMatchingWorkflows` method.
    - Exception Handling: Check to see if an `CompletionException` is thrown. If
      so, display the message and end the program.

7. **Tag Resource**:
    - Description: Adds tags associated with an AWS Entity Resolution resource
      by calling the`tagResource` method.
    - Exception Handling: Check to see if a `ResourceNotFoundException` is
      thrown. If so, display the message and end the program
8. **View the results of the AWS Entity Resolution Workflow**:
    - Description: View the workflow results by calling the
      `getMatchingJob` method.
    - Exception Handling: Check to see if an `ResourceNotFoundException` is thrown. If
      so, display the message and end the program.

9. **Delete the AWS resources**:
    - Description: Delete the AWS resouces including the workflow and schema mappings by calling the
      `deleteMatchingWorkflow` and `deleteSchemaMapping` methods.
    - Exception Handling: Check to see if an `ResourceNotFoundException` is thrown. If
      so, display the message and end the program.   
    - Finally delete the CloudFormation Stack by calling these method:
       - CloudFormationHelper.emptyS3Bucket(glueBucketName);
       - CloudFormationHelper.destroyCloudFormationStack     

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
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
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

Generating resources...
Stack creation requested, ARN is arn:aws:cloudformation:us-east-1:814548047983:stack/EntityResolutionCdkStack/858988e0-f604-11ef-916b-0affc298c80f
Stack created successfully
--------------------------------------------------------------------------------

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Upload the following JSON objects to the erbucketf684533d2680435fa99d24b1bdaf5179 S3 bucket.
{"id":"1","name":"Jane Doe","email":"jane.doe@example.com"}
{"id":"2","name":"John Doe","email":"john.doe@example.com"}
{"id":"3","name":"Jorge Souza","email":"jorge_souza@example.com"}

Upload the following CSV data to the erbucketf684533d2680435fa99d24b1bdaf5179 S3 bucket.
id,name,email,phone
1,Jane B.,Doe,jane.doe@example.com,555-876-9846
2,John Doe Jr.,john.doe@example.com,555-654-3210
3,María García,maría_garcia@company.com,555-567-1234
4,Mary Major,mary_major@company.com,555-222-3333


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The JSON and CSV objects have been uploaded to the S3 bucket.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Create Schema Mapping
Entity Resolution schema mapping aligns and integrates data from
multiple sources by identifying and matching corresponding entities
like customers or products. It unifies schemas, resolves conflicts,
and uses machine learning to link related entities, enabling a
consolidated, accurate view for improved data quality and decision-making.

In this example, the schema mapping lines up with the fields in the JSON and CSV objects. That is,
it contains these fields: id, name, and email.

[jsonschema-ef86075e-cf5e-4bb1-be50-e0f19743ddb2] schema mapping Created Successfully!
The JSON schema mapping name is jsonschema-ef86075e-cf5e-4bb1-be50-e0f19743ddb2
[csv-8d05576d-66bb-4fcf-a29c-8c3de57ce48c] schema mapping Created Successfully!
The CSV schema mapping name is csv-8d05576d-66bb-4fcf-a29c-8c3de57ce48c

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
The workflow outputs consolidated, de-duplicated data.

We will use the machine learning-based matching technique.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Workflow created successfully.
The workflow ARN is: arn:aws:entityresolution:us-east-1:814548047983:matchingworkflow/workflow-39216b7f-f00b-4896-84ae-cd7edcfc7872

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
3. Start the matching job of the workflow-39216b7f-f00b-4896-84ae-cd7edcfc7872 workflow.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job ID: f25d2707729646a4af27874d991e22c5
The matching job was successfully started.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. While the matching job is running, let's look at other API methods. First, let's get details for job f25d2707729646a4af27874d991e22c5

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Job status: RUNNING
Job details: GetMatchingJobResponse(JobId=f25d2707729646a4af27874d991e22c5, StartTime=2025-02-28T18:49:14.921Z, Status=RUNNING)
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Get the schema mapping for the JSON data.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Attribute Name: id, Attribute Type: UNIQUE_ID
Attribute Name: name, Attribute Type: NAME
Attribute Name: email, Attribute Type: EMAIL_ADDRESS
Schema mapping ARN is arn:aws:entityresolution:us-east-1:814548047983:schemamapping/jsonschema-ef86075e-cf5e-4bb1-be50-e0f19743ddb2

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. List Schema Mappings.
Schema Mapping Name: csv-33f8e392-74e7-4a08-900a-652b94f86250
Schema Mapping Name: csv-3b68e38b-1d5c-4836-bfc7-92ac7339e5c7
Schema Mapping Name: csv-4f547deb-56c1-4923-9119-556bc43df08d
Schema Mapping Name: csv-6fe8bbc3-ebb5-4800-ab49-a89f75a87905
Schema Mapping Name: csv-812ecad3-3175-49c3-93a5-d3175396d6e7
Schema Mapping Name: csv-8d05576d-66bb-4fcf-a29c-8c3de57ce48c
Schema Mapping Name: csv-90a464e1-f050-422c-8f5f-0726541a5858
Schema Mapping Name: csv-ebad3e3d-27be-4ed4-ae35-7401265e57bd
Schema Mapping Name: csv-f752d395-857b-4106-b2f2-85e1da5e3040
Schema Mapping Name: jsonschema-363dc915-0540-406e-8d3f-4f1435e0b942
Schema Mapping Name: jsonschema-5b1ad3e1-a840-4c4f-b791-5e9e1893fe7e
Schema Mapping Name: jsonschema-8623e0ec-bb8c-4fe2-a998-609eae08d84d
Schema Mapping Name: jsonschema-93d5fd04-f10e-4274-a181-489bea7b92db
Schema Mapping Name: jsonschema-b1653c13-ce77-471d-a3d5-ae4877216a74
Schema Mapping Name: jsonschema-c09b3414-384c-4e3d-90c8-61e48abde04d
Schema Mapping Name: jsonschema-d9a6edc0-a9bd-4553-bb71-fbf0d6064ef9
Schema Mapping Name: jsonschema-ef86075e-cf5e-4bb1-be50-e0f19743ddb2
Schema Mapping Name: jsonschema-f0a259e0-f4e5-493a-bfd5-32740d2fa24d
Schema Mapping Name: schema2135
Schema Mapping Name: schema435
Schema Mapping Name: schema455
Schema Mapping Name: schema456
Schema Mapping Name: schema4648
Schema Mapping Name: schema4720
Schema Mapping Name: schema4848
Schema Mapping Name: schema6758
Schema Mapping Name: schema8775
Schema Mapping Name: schemaName100

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Tag the jsonschema-ef86075e-cf5e-4bb1-be50-e0f19743ddb2 resource.
Tags can help you organize and categorize your Entity Resolution resources.
You can also use them to scope user permissions by granting a user permission
to access or change only resources with certain tag values.
In Entity Resolution, SchemaMapping and MatchingWorkflow can be tagged. For this example,
the SchemaMapping is tagged.

Successfully tagged the resource.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. View the results of the AWS Entity Resolution Workflow.
You cannot view the result of the workflow that is in a running state.
In order to view the results, you need to wait for the workflow that we started in step 3 to complete.

If you choose not to wait, you cannot view the results. You can perform
this task manually in the AWS Management Console.

This can take up to 30 mins (y/n).

y
You selected to view the Entity Resolution Workflow results.
29:01Job status: RUNNING
28:01Job status: RUNNING
27:01Job status: RUNNING
26:01Job status: RUNNING
25:01Job status: RUNNING
24:01Job status: RUNNING
23:01Job status: RUNNING
22:01Job status: RUNNING
21:01Job status: RUNNING
20:01Job status: RUNNING
19:01Job status: RUNNING
18:01Job status: RUNNING
17:01Job status: RUNNING
16:01Job status: RUNNING
15:01Job status: RUNNING
14:01Job status: RUNNING
13:01Job status: RUNNING
12:01Job status: RUNNING
11:01Job status: RUNNING
10:01Job status: RUNNING
09:01Job status: RUNNING
08:01Job status: RUNNING
07:01Job status: SUCCEEDED

Countdown complete: Workflow is in Completed state!
Job metrics fetched successfully for jobId: f25d2707729646a4af27874d991e22c5
Number of input records: 7
Number of match ids: 6
Number of records not processed: 0
Number of total records processed: 7
The following explains the output data generated by the Entity Resolution workflow. The output data is stored in the erbucketf684533d2680435fa99d24b1bdaf5179 bucket.

  ------------------------------------------------------------------------------ ----------------- ---- ------------------ --------------------------- -------------- ---------- --------------------------------------------------- 
  InputSourceARN                                                                 ConfidenceLevel   id   name               email                       phone          RecordId   MatchID                                            
 ------------------------------------------------------------------------------ ----------------- ---- ------------------ --------------------------- -------------- ---------- --------------------------------------------------- 
  arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable                      7    Jane E. Doe        jane_doe@company.com        111-222-3333   7          036298535ed6471ebfc358fc76e1f51200006472446402560  
  arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable    0.90523           2    Bob Smith Jr.      bob.smith@example.com       987-654-3210   2          6ae2d360d6594089837eafc31b20f31600003506806140928  
  arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/jsongluetable   0.90523           2    Bob Smith          bob.smith@example.com                      2          6ae2d360d6594089837eafc31b20f31600003506806140928  
  arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable    0.89398956        1    Alice B. Johnson   alice.johnson@example.com   746-876-9846   1          34a5075b289247efa1847ab292ed677400009137438953472  
  arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/jsongluetable   0.89398956        1    Alice Johnson      alice.johnson@example.com                  1          34a5075b289247efa1847ab292ed677400009137438953472  
  arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable    0.605295          3    Charlie Black      charlie.black@company.com   345-567-1234   3          92c8ef3f68b34948a3af998d700ed02700002146028888064  
  arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/jsongluetable   0.605295          3    Charlie Black      charlie.black@example.com                  3          92c8ef3f68b34948a3af998d700ed02700002146028888064  

Note that each of the last 3 pairs of records are considered a match even though the 'name' or 'email' differ between the records;
For example 'Bob Smith Jr.' compared to 'Bob Smith'.
The confidence level is a value between 0 and 1, where 1 indicates a perfect match. In the last pair of matched records,
the confidence level is lower for the differing email addresses.



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. Do you want to delete the resources, including the workflow? (y/n)
You cannot delete the workflow that is in a running state.
In order to delete the workflow, you need to wait for the workflow to complete.

You can delete the workflow manually in the AWS Management Console at a later time.

If you already waited for the workflow to complete in the previous step,
the workflow is completed and you can delete it.

If the workflow is not completed, this can take up to 30 mins (y/n).

y
workflow-39216b7f-f00b-4896-84ae-cd7edcfc7872 was deleted
Workflow deleted successfully!
Schema mapping 'jsonschema-ef86075e-cf5e-4bb1-be50-e0f19743ddb2' deleted successfully.
Schema mapping 'csv-8d05576d-66bb-4fcf-a29c-8c3de57ce48c' deleted successfully.
Both schema mappings were deleted successfully!

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
Now we delete the CloudFormation stack, which deletes
the resources that were created at the beginning of this scenario.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
Delete stack requested ....
Stack deleted successfully.
Resources deleted successfully!

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

| action                 | metadata file                    | metadata key                         |
|------------------------|----------------------------------|--------------------------------------|
| `createWorkflow`       | entityresolution_metadata.yaml   |entityresolution_CreateWorkflow       |
| `createSchemaMapping`  | entityresolution_metadata.yaml   |entityresolution_CreateMapping        |
| `startMatchingJob`     | entityresolution_metadata.yaml   |entityresolution_StartMatchingJob     |
| `getMatchingJob`       | entityresolution_metadata.yaml   |entityresolution_GetMatchingJob       |
| `listMatchingWorkflows`| entityresolution_metadata.yaml   |entityresolution_ListMatchingWorkflows|
| `getSchemaMapping`     | entityresolution_metadata.yaml   |entityresolution_GetSchemaMapping     |
| `listSchemaMappings`   | entityresolution_metadata.yaml   |entityresolution_ListSchemaMappings   |
| `tagResource `         | entityresolution_metadata.yaml   |entityresolution_TagResource          |
| `deleteWorkflow `      | entityresolution_metadata.yaml   |entityresolution_DeleteWorkflow       |
| `deleteMapping `       | entityresolution_metadata.yaml   |entityresolution_DeleteSchemaMapping  |
| `listMappingJobs `     | entityresolution_metadata.yaml   |entityresolution_Hello                |
| `scenario`             | entityresolution_metadata.yaml   |entityresolution_Scenario             |




