
# Create an item tracker web app using Amazon Relational Database Service (Amazon RDS), Amazon Simple Email Service (Amazon SES), and the AWS SDK for C++.

## Overview
This example code creates an HTTP server which implements the following APIs.
- `GET /items` to query all items.
- `GET /items?archived=true` to query archived items, `archived=false` to query active items.
- `GET /items/{item_id}` to query a single item. 
- `POST /items` to create a new item in the collection.
- `PUT /items/{item_id}` to update an individual item. 
- `PUT /items/{item_id}:archive` to move an individual item to the `archived` status.
- `POST /items:report` to generate a report. The body may accept an `email` field with a string email address.

The data for these APIs is stored in an Amazon RDS database. The email report is sent using Amazon SES.

The AWS SDK code examples library contains a client react app at [resources/clients/react/elwing](../../../../resources/clients/react/elwing/). 
Run the client app by following the instructions in the [ReadMe](../../../../resources/clients/react/elwing/README.md). 
When the client app is running in your browser, select "Item Tracker" in the sidebar to open a web page that communicates with this example.

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites'

* Install the [AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html).
* Install the [Conan C++ package manager](https://conan.io/), which is used  by this example to install dependencies. 

## Create the resources 

The AWS resources for this example can be created by running the AWS CloudFormation
`setup.yaml` script in
[resources/cdk/aurora_serverless_app](../../../resources/cdk/aurora_serverless_app).
For instructions on how to run the script, see the [README](../../../resources/cdk/aurora_serverless_app/README.md).

## Build the code
This example uses the poco library.
The poco library can be installed using Conan.
conan install .. --build=missing
On Windows
conan install .. --build=missing -o poco:shared=True -s build_type=Debug

*Varies by language, and clearly illustrates the workflow of building the app. Should include descriptions of code not included as code comments in actual examples.* **<-- Delete this sentence from template**

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial.
Follow the instructions in the [Destroying resources](../../../resources/cdk/aurora_serverless_app#destroying-resources)
section of the README for the Aurora Serverless sample application.

## Next steps
Congratulations! You have built a web application that reads, writes, and archives
work items that are stored in an Amazon Aurora Serverless database, and that uses
Amazon SES to send email to a registered user.

## Additional resources
* [*Cross-service examples*](*link to cross-service level README for the language*)
* [*Relevant service developer guide(s)*](*link to developer guide(s)*)
* [*Relevant service API reference guide(s)*](*link to developer guide(s)*)
* [*Relevant SDK API reference guide*](*link to API reference guide(s)*) 
