
# Create an item tracker web app using Amazon RDS, Amazon SES, and the AWS SDK for C++

## Overview
This example code creates an HTTP server which implements the following APIs.
- `GET /items` to query all items.
- `GET /items?archived=true` to query archived items, `archived=false` to query active items.
- `GET /items/{item_id}` to query a single item. 
- `POST /items` to create a new item in the collection.
- `PUT /items/{item_id}` to update an individual item. 
- `PUT /items/{item_id}:archive` to move an individual item to the `archived` status.
- `POST /items:report` to generate a report. The body can accept an `email` field with a string email address.

The data for these APIs is stored in an Amazon Aurora Serverless database. The email report is sent using Amazon Simple Email Service (Amazon SES).

This example uses C++ code, JavaScript React code, and AWS Cloud Development Kit (AWS CDK) code.

1. The example code in this folder creates an HTTP server which accesses AWS services using the AWS SDK for C++. 

2. A client JavaScript React web app is created using the code at [resources/clients/react/elwing](../../../../resources/clients/react/elwing/).

3. The Amazon Aurora Serverless resources are created using the AWS CDK code at [resources/cdk/aurora_serverless_app](../../../../resources/cdk/aurora_serverless_app). 

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

* Install the [AWS SDK for C++](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html).
* Install the [Conan C++ package manager](https://conan.io/), which is used by this example to install dependencies. 

## Create the resources 

The Amazon RDS resources for this example can be created by running the AWS CloudFormation
`setup.yaml` script in
[resources/cdk/aurora_serverless_app](../../../../resources/cdk/aurora_serverless_app).
For instructions on how to run the script, see the [README](../../../../resources/cdk/aurora_serverless_app/README.md).

A [verified identity](https://docs.aws.amazon.com/ses/latest/dg/verify-addresses-and-domains.html) must be created in Amazon SES.

## Build the code

### Open a terminal in the code example's directory and create a build subdirectory

`mkdir build`  
`cd build`

### Install the Poco library using the Conan package manager

On Linux and Mac.

`conan install .. --build=missing`

On Windows.

`conan install .. --build=missing -o poco:shared=True -s build_type=Debug`

### Build the example using CMake and Make

`cmake ..`  
`make ..`

## Run the example

Run the HTTP server from the command line with the necessary arguments.

`./run_serverless_aurora <database> <resource_arn> <secret_arn> <email>`

The "database", "resource_arn", and "secret_arn" were obtained in the preceding "Create the resources" step.

The "email" is a [verified identity](https://docs.aws.amazon.com/ses/latest/dg/verify-addresses-and-domains.html) created in Amazon SES.

Now run the [client app](../../../../resources/clients/react/elwing/) to communicate with the "run_serverless_aurora" HTTP server. The [ReadMe](../../../../resources/clients/react/elwing/README.md) contains instructions for running the client web app.

When both the client app and the HTTP server app are running, AWS resources can be manipulated from a webpage. The client app will appear in your web browser. Select "Item Tracker" in the webpage sidebar to open the webpage which communicates with the HTTP server.

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial.
Follow the instructions in the "Destroying resources" section of the [README](../../../../resources/cdk/aurora_serverless_app/README.md) for the Aurora Serverless CDK deployment.

## Next steps
Congratulations! You have built a web application that reads, writes, and archives
work items that are stored in an Amazon Aurora Serverless database. The web application also uses
Amazon SES to send email from a verified user.

## Additional resources
* [Cross-service examples](../README.md)
* [Amazon Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
* [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
* [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
* [Amazon RDS Data Service API Reference](https://docs.aws.amazon.com/rdsdataservice/latest/APIReference/Welcome.html)
* [Amazon SES API Reference](https://docs.aws.amazon.com/ses/latest/APIReference/Welcome.html)
* [AWS SDK for C++ Developer Guide](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/welcome.html) 
