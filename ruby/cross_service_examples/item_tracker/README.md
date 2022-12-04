# Deploy a sample application using the AWS SDK for Ruby, Amazon Aurora, and Amazon Simple Email Service (SES)

## Overview
This example code comprises a "real-world" reference application showcasing a [serverless Amazon Aurora database](https://aws.amazon.com/rds/aurora/) using the [AWS SDK for Ruby](https://aws.amazon.com/sdk-for-ruby/).

This code is written to be browsed in an exploratory manner.
For a more hands-on experience, you can "run" the application by invoking this example code using the [instructions below]((#invoke-this-example-code)).

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites
*List prerequisites.* **<-- Delete this sentence from template**

## Create the resources
*Writer can choose to use the CDK, console, or an SDK for their example. This section should be comprehensive and include every step. If using the console, images are helpful.* **<-- Delete this sentence from template**

## Build the code
*Varies by language, and clearly illustrates the workflow of building the app. Should include descriptions of code not included as code comments in actual examples.* **<-- Delete this sentence from template**

## Delete the resources

*This section should walk the user step-by-step through deleting the resources using the CDK. Or, if they created the resources manually, provide a reminder to delete them.* **<-- Delete this sentence from template**

To avoid charges, delete all the resources that you created for this tutorial.

If you created your resources through the AWS Management Console, or modified them by running the app, you must use the console to delete them.

If you created your resources using AWS CloudFormation, you can delete them by deleting the stack. For more information, see [Deleting a stack on the AWS CloudFormation console](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-console-delete-stack.html).

**Note**: By running the app, you modified the table and the bucket, so you must delete these resources manually through the console before you can delete the stack.

## Next steps
Congratulations! You have [*description of what example achieves*].

[*Describe other potential use cases for the skills they learned.*] **<-- Delete this sentence from template**

## Additional resources
* [*Cross-service examples*](*link to cross-service level README for the language*)
* [*Relevant service developer guide(s)*](*link to developer guide(s)*)
* [*Relevant service API reference guide(s)*](*link to developer guide(s)*)
* [*Relevant SDK API reference guide*](*link to API reference guide(s)*)

=======






# Item Tracker, a Cross-Service Example using the AWS SDK for Ruby

This example code comprises a "real-world" reference application showcasing a [serverless Amazon Aurora database](https://aws.amazon.com/rds/aurora/) using the [AWS SDK for Ruby](https://aws.amazon.com/sdk-for-ruby/).

This code is written to be browsed in an exploratory manner. 
For a more hands-on experience, you can "run" the application by invoking this example code using the [instructions below]((#invoke-this-example-code)).

***
# Table of contents
1. [About this example code](#about-this-example-code)
2. [Invoke this example code](#invoke-this-example-code)
3. [Test this example code](#test-this-example-code)
***
# ⚠️ Caution
Running this code might result in charges to your AWS account.
This code is not tested in every AWS Region. For more information, see AWS Regional Services.
***

# About this example code
The code comprises an application designed to manage fictitious work items using 3 key components.

## 1. Frontend
The frontend code is written in JavaScript and implemented using the React framework.
For more information, see the [React Elwing Client README](../../../resources/clients/react/elwing/README.md).

## 2. Backend
The centerpiece of this example is an API written exclusively in Ruby. It features the following gems:
* [AWS SDK for Ruby](https://aws.amazon.com/sdk-for-ruby/) (for communicating with Amazon Aurora)
* [Sinatra](https://sinatrarb.com/intro.html) (for a lightweight API implementation)
* [Sequel](https://sequel.jeremyevans.net/) (for Ruby-native modeling of SQL queries)

This Ruby API consists of the following API routes:

|method              | route                |action        | client             |function                |
|--------------------|----------------------|--------------|--------------------|------------------------|
|GET                 | /api/items           |List items    | RDSDataService     |execute_statement(*SQL*)|
|POST                | /api/items           |Add item      | RDSDataService     |execute_statement(*SQL*)|
|PUT                 | /api/items:archive   |Archive item  | RDSDataService     |execute_statement(*SQL*)|
|GET                 | /api/items/{item_id} |Get item      | RDSDataService     |execute_statement(*SQL*)|
|POST                | /api/items:report    |Create report | RDSDataService     |execute_statement(*SQL*)|

## 3. Database
This example relies on a MySQL 5.7 database implemented using Amazon Aurora, a serverless relational database management system (RDBMS). 
The Aurora cluster is deployed using the AWS Cloud Development Kit (AWS CDK). 
For more information, see the [Aurora Serverless App CDK script README.md](../../../resources/cdk/aurora_serverless_app/README.md).

***

# Invoke this example code

## Prerequisites
To explore this example, you must first:
1. Create account and configure credentials using [instructions in the top-level README.md](../../../README.md#invoke-example-code).
2. Install Ruby and resolve dependencies using [instructions in the Ruby README.md](../../../ruby/README.md).
3. Create AWS infrastructure using [instructions in the Aurora Serverless App README.md](../../../resources/cdk/aurora_serverless_app/README.md). 
    * NOTE: Store the following output values in the `env/config.yml` file: `CLUSTER_ARN`, `SECRET_ARN`, and `DATABASE`.
4. Populate your database with a table and data using the following commands:
     ```bash
     cd ruby/cross_service_examples/item-tracker/helpers
     ruby create_table.rb # checks for database and creates a new table
     ruby populate_data.rb # creates a table with 10 records
     ```
5. Register your email address with Amazon Simple Email Service (SES) using the instructions in [Creating and verifying identities in Amazon SES](https://docs.aws.amazon.com/ses/latest/dg/creating-identities.html)
   * NOTE: Use this email for the `sender_email` and `recipient_email` fields in your `env/config.yml` file.

## Start the application
To view this example in its entirety, you must:
1. Start the frontend React application using the following commands:
    ```bash
     cd resources/clients/elwing # located within Resources sub-directory
     npm install # first time only
     npm start # launches a browser session on localhost:3000 
    ```
2. Open a second tab in your terminal and run the following command:
    ```bash
    cd ruby/cross_service_examples/item-tracker
    ruby app.rb # starts REST API listening on port 8080
    ```
Now, visit http://localhost:3000/item_tracker to view your working application.

***

# Test this example code
Test this application manually via the frontend or automatically via the RSpec tests.

### Manual validation (via frontend React app)
Once the app is started, manually validate the following behaviors on the frontend:
* Filter items by All, Archived, and Active (not archived).
* Archive an item by clicking the Archive button.
* Add an item by submitting the form revealed by clicking the Add Item button.
* Send a report of items by entering a verified email address and clicking Submit.

As you interact with the React app, logs will appear in the command line tab where you started the REST API. 
### Automated validation (via RSpec tests)
Validate the internal logic within this example by running the following commands:
```bash
cd spec
rspec db_wrapper_spec.rb
```

