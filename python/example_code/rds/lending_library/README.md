# Amazon Aurora serverless REST API lending library example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Relational Database 
Service (Amazon RDS) API and AWS Chalice to create a REST API backed by an 
Amazon Aurora database. The web service is fully serverless and represents
a simple lending library where patrons can borrow and return books. Learn how to:

* Create and manage a serverless Amazon Aurora database cluster.
* Use AWS Secrets Manager to manage database credentials.
* Implement a data storage layer that uses Amazon RDS Data Service to move data into
and out of the database.  
* Use AWS Chalice to deploy a serverless REST API to Amazon API Gateway and AWS Lambda.
* Use the Requests package to send requests to the web service.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.14.47 or later
- Chalice 1.20.0 or later
- AWS CLI 1.18.147 or later
- Requests 2.23.0 or later

To run unit tests, you'll also need the following packages.
 
- PyTest 5.3.5 or later
- Hypothesis 5.36.0 or later

## Cautions

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

This example contains two deployment steps, a REST demo, and a cleanup step that
all must be run separately.

1. Database deployment

    Creates an Amazon Aurora serverless data cluster and an AWS Secrets Manager 
    secret to hold the database user credentials. It fills the database with example 
    data pulled from the [Internet Archive's Open Library](https://openlibrary.org). 
    
    Deploy and fill the database by running the following command at a command prompt.

    ```
    py library_demo.py deploy_database
    ``` 

    The `lendinglibrary` database is now ready and can be accessed through the 
    [AWS Console Query Editor](https://console.aws.amazon.com/rds/home?#query-editor:) 
    or the Boto3 `rds-data` client. Or, run the next step to deploy the REST API.

1. REST API deployment

    Uses Chalice and AWS CLI commands to deploy routing and data-handling layers 
    to AWS Lambda, set up API Gateway to handle HTTP requests, and
    establish AWS Identity and Access Management (IAM) roles and profiles to manage
    permissions. 
    
    Deploy the REST API by running the following command at a command 
    prompt.

    ```
    py library_demo.py deploy_rest
    ```
 
    The REST API is now deployed and can received HTTP requests. Try it yourself 
    using your favorite HTTP client or run the next step to see a demonstration
    of how to use the Requests package to call the web service.

1. REST API demonstration

    Uses the Requests package to send a series of HTTP requests to the web service to
    perform the following tasks.
    
    1. List the books in the library.
    1. Add a library patron.
    1. Lend a book to the new patron.
    1. Return the book from the patron.
    
    See the demo by running the following command at a command prompt.
    
    ```
    py library_demo.py demo_rest
    ```
    
1. Cleanup

    Remove all resources created during the demonstration by running the following 
    command at a command prompt.
    
    ```
    py library_demo.py cleanup
    ```
    
    Be sure to run cleanup after you're done to avoid additional charges to your 
    account.    

### Example structure

The example is subdivided into two main sections.

* The *library_demo.py* script and *rds_tools* folder are used to deploy resources 
and manage the demo.
* The *library_api* folder contains the REST API code and resource definitions that 
are deployed to AWS by Chalice.
 
The example contains the following files.

**library_demo.py**

Deploys database and REST API resources, fills the database with example books,
runs a REST request demonstration, and cleans up resources.

**rds_tools/aurora_tools.py**

Wraps parts of the Boto3 RDS and Secrets Manager API to show how to create database
clusters and secrets.  

**library_api/app.py**

Contains REST API routes that receive HTTP requests. This file and supporting 
`chalicelib` files are deployed to AWS Lambda as part of the Chalice deployment.

**library_api/requirements.txt**

Lists packages that are required in the AWS Lambda environment.

**library_api/resources.json**

Defines an IAM role and policy to grant AWS Lambda permission to perform specific
actions on RDS, Secrets Manager, RDS Data Service, and Amazon CloudWatch Logs.    

**.chalice/config.json**

Defines environment variables that define the cluster, secret, and database used
by the data-handling layer.

**chalicelib/library_data.py**

Handles calls from the REST routing layer. Runs MySQL statements through RDS
Data Service to move data into and out of the database.

**chalicelib/mysql_helper.py**

A simplified object-relational mapping (ORM) layer that translates between Python 
structures and SQL statements.  

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your 
[GitHub root]/python/example_code/rds/lending_library/rds_tools and
[GitHub root]/python/example_code/rds/lending_library/library_api
folders.

```    
py -m pytest
```

The tests in the *test_library_api_app.py* script use the `chalice.test.Client`
object to help with route testing. For details, see 
[Chalice Testing](https://aws.github.io/chalice/api.html#testing-api).

## Additional information

- [Boto3 Amazon Relation Database Service service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds.html)
- [Boto3 Amazon RDS Data Service service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds-data.html)
- [Boto3 AWS Secrets Manager service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/secretsmanager.html)
- [AWS Chalice on GitHub](https://github.com/aws/chalice)
- [Amazon Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
- [Amazon RDS Data Service API Reference](https://docs.aws.amazon.com/rdsdataservice/latest/APIReference/Welcome.html)
- [AWS Secrets Manager User Guide](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
