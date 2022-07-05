#  Creating the Amazon Aurora Serverless backend using the AWS SDK for PHP

## Overview

| Heading      | Description                                                                                                  |
| ----------- |--------------------------------------------------------------------------------------------------------------|
| Description | Discusses how to develop a dynamic web application that tracks and reports on Amazon Aurora Serverless data. |
| Audience   | Developer (beginner / intermediate)                                                                          |
| Updated   | 6/20/2022                                                                                                    |
| Required skills   | PHP, Laravel                                                                                                 |

## Purpose

You can develop a dynamic web application that tracks and reports on work items by using the following AWS services:

+ Amazon Aurora Serverless database
+ Amazon Simple Email Service (Amazon SES)

For more information on Laravel and how to use it, see [Laravel.com](laravel.com).

This AWS tutorial uses the [RDSDataServiceClient](https://docs.aws.amazon.com/aws-sdk-php/v3/api/class-Aws.RDSDataService.RDSDataServiceClient.html) object to perform CRUD operations on the Aurora Serverless database.

**Note:** You can only use the **RDSDataServiceClient** object for an Aurora Serverless DB cluster or an Amazon Aurora PostgreSQL-Compatible Edition. For more information, see [Using the Data API for Aurora Serverless](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/data-api.html).

#### Topics

+ Prerequisites
+ Understand the AWS Tracker application
+ Create an IntelliJ project named ItemTrackerRDS
+ Add the Spring POM dependencies to your project
+ Create the Java classes
+ Create the HTML files
+ Create script files
+ Run the application

## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A PHP IDE (such as PHPStorm https://www.jetbrains.com/phpstorm/download/)
+ PHP 8.0 or later (https://php.net)
+ Composer installed (https://getcomposer.org/)

### Important

+ The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
+ Running this code might result in charges to your AWS account.
+ Make sure to delete all the resources that you create during this tutorial so that you won't be charged.

### Creating the resources

This tutorial uses the AWS Cloud Development Kit (AWS CDK) to automatically build and create the required Amazon Relational Database Service (Amazon RDS) setup for you.

Follow the instructions here: https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/cdk/aurora_serverless_app

After you complete the resource scaffolding by using the AWS CDK, connect to the Amazon RDS console.
Select *Query Editor*. Then, choose the database instance created by using the AWS CDK.
For *Database username*, choose *Connect with a Secrets Manager ARN*. Put the *SecretARN* revealed by
the *describe-stacks* command from the AWS CDK instructions. Do the same for the database name.

This opens a SQL query console. You can run any raw SQL queries here that you want. Run the 
following to create the work table.

```sql
create table work_items (
  work_item_id INT AUTO_INCREMENT PRIMARY KEY,
  created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
  description TEXT, 
  guide TEXT, 
  status TEXT, 
  username VARCHAR(45), 
  archive BOOL DEFAULT 0
);
```

#### Application functionality

A user can perform the following tasks in the AWS Tracker application:

+ Enter an item into the system.
+ View active items.
+ View archived items.
+ View all items.
+ Archive active items.
+ Send a report to an email recipient.

#### Build the front end

We have provided a React (https://reactjs.org/) front end which can connect to any backend configured
with the proper REST endpoints. Follow the README at resources/clients/react/item-tracker/README.md. When
you have a local server running at http://localhost:3000, finish this section by setting up the config.json.

Open the resources/clients/react/item-tracker/src/config.json file and change the *BASE_URL* value to
"http://localhost:8000/api".

### Deploy Laravel

First, you need to set up the environment file to read from your database. Add the following key/values to the end
of the php/cross_service/aurora_item_tracker/.env file. Replace the ResourceArn and SecretArn values with
the values from *describe-stacks* from the AWS CDK section. Replace *your@email* with an email address you've
set up as a sender in Amazon SES.
```yaml
RESOURCE_ARN=*ResourceArn*
SECRET_ARN=*SecretArn*
DATABASE=auroraappdb
EMAIL=your@email
```

From a terminal, go to php/cross_service/aurora_item_tracker. Run *composer install* followed by *php
artisan serve*. This starts your Laravel application running at http://localhost:8000. This is where
the front end looks for API endpoints, and it's also where you can run API calls directly to see what
the raw output looks like.

## Run the application

Now, you're all set! Access the application by opening http://localhost:3000 in a web browser. 
You can add items, archive them, filter by state, and send out an email report. 
(Before sending an email, make sure to register the email as a sender with 
[Amazon SES](https://aws.amazon.com/ses/).)


### Next steps
Congratulations, you have created and deployed a Laravel application that interacts with Amazon RDS 
(and other AWS services).
As stated at the beginning of this tutorial, be sure to delete all the resources you created
during this tutorial so that you won't continue to be charged.

