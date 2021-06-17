# AWS Chalice and AWS Lambda REST API example

## Purpose

Shows how to use AWS Chalice with the AWS SDK for Python (Boto3) to 
create a serverless REST API that uses Amazon API Gateway, AWS Lambda, and 
Amazon DynamoDB. The REST API simulates a system that tracks daily cases
of COVID-19 in the United States, using fictional data. Learn how to:

* Use AWS Chalice to define routes in AWS Lambda functions that
 are called to handle REST requests that come through Amazon API Gateway.
* Use AWS Lambda functions to retrieve and store data in an Amazon DynamoDB 
table to serve REST requests.
* Define table structure and security role resources in an AWS CloudFormation template.
* Use AWS Chalice and AWS CloudFormation to package and deploy all necessary resources.
* Use AWS CloudFormation to clean up all created resources.

This example brings together some of the same information you can find in the
tutorials in the 
[AWS Chalice GitHub repository](https://aws.github.io/chalice/quickstart.html).

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.14.20 or later
- AWS Chalice 1.15.1 or later
- AWS Command Line Interface (AWS CLI) 1.18.97 or later
- Requests 2.23.0 or later
- PyTest 5.3.5 or later (to run unit tests)
- An Amazon S3 bucket

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

1. Install prerequisites by running the following at a command prompt.

    ```
    pip install -U chalice
    pip install -U awscli
    pip install -U requests
    ``` 

1. At a command prompt, navigate to the 
`[GitHub root]/python/cross_service/apigateway_covid-19_tracker` 
folder.

1. Run the following to create a deployment package in a subfolder named `out`.

    ```
    chalice package --merge-template resources.json out
    ```

1. Run the following to prepare the package for deployment. Replace the  
YOUR-BUCKET-NAME placeholder with the name of an Amazon S3 bucket that you control.

    ```
    aws cloudformation package --template-file out/sam.json \ 
   --s3-bucket YOUR-BUCKET-NAME --output-template-file out/template.yml
    ```

1. Run the following to create the resources and deploy your REST API to AWS.

    ```
    aws cloudformation deploy --template-file out\template.yml \ 
    --stack-name ChaliceRestDemo --capabilities CAPABILITY_IAM
    ```

    At this point, the REST API is available and can be called from any client
    that can issue HTTP requests.
    
1. Run the following to find the URL of the REST API in the AWS CloudFormation stack.

   ```
   aws cloudformation describe-stacks --stack-name ChaliceRestDemo \
   --query "Stacks[0].Outputs[?OutputKey=='EndpointURL'].OutputValue" --output text
   ```
   
1. Append "states" to the base URL returned by the previous step and use `curl` to
get the list of states from the API. For example, if your endpoint ID is 1234567890, 
run the following command.

    ```
    curl https://1234567890.execute-api.us-west-2.amazonaws.com/api/states
    ``` 

1. Start the client demonstration by running the following command. The client
demo uses the Requests package to send requests to the REST API.

    ```
    python client_demo.py
    ```  

1. After the demonstration completes, clean up all resources by running the following
command.

    ```
    aws cloudformation delete-stack --stack-name ChaliceRestDemo
    ```

### Example structure

The example is divided into the following files.

**app.py**

Defines the routes for the REST API. Uses Chalice to decorate route functions and
handle deserializing requests and serializing responses. Calls a custom Storage
class to handle moving data in and out of an Amazon DynamoDB table. 

**client_demo.py**

Shows how to use the Requests package to send a variety of requests to the REST API.
If the `api_url` option is not specified, the script finds the base REST URL in the 
AWS CloudFormation stack.

**requirements.txt**

Defines the minimum version of Boto3 to deploy to AWS Lambda.

**resources.json**

An additional AWS CloudFormation template that is merged into the main template when
the deployment package is created. This template defines additional resources used
by this demo, such as an Amazon DynamoDB table and IAM role.

**chalicelib/covid_data.py**

The Storage class that handles moving data in and out of Amazon DynamoDB in response
to REST requests. When a `chalicelib` module is present, Chalice automatically
deploys its contents to AWS Lambda so that it is available to the main `app.py` module.

**.chalice/config.json**

Configuration for the application. The TABLE_NAME environment variable is deployed
to AWS Lambda and is used by the Storage class to access the Amazon DynamoDB table.    

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your 
`[GitHub root]/python/example_code/lambda/chalice_examples/lambda_rest` folder.

```    
python -m pytest
```

## Additional information

- [AWS Chalice Quickstart](https://aws.github.io/chalice/quickstart.html)
- [AWS Command Line Interface User Guide](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html)
- [Requests documentation](https://requests.readthedocs.io/en/master/)
- [Boto3 Quickstart](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/quickstart.html)
- [Boto3 Amazon API Gateway service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/apigateway.html)
- [Boto3 AWS Lambda service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/lambda.html)
- Actual COVID-19 data can be found in Amazon's public [COVID-19 data lake](https://aws.amazon.com/covid-19-data-lake/)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
