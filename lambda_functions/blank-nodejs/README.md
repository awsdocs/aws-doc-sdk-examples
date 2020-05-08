# Blank function (Node.js)
This sample application is a Lambda function that calls the Lambda API. It shows the use of logging, environment variables, AWS X-Ray tracing, layers, unit tests and the AWS SDK. You can use it to learn about Lambda features or use it as a starting point for your own projects.

![Architecture](/lambda_functions/blank-nodejs/images/sample-blank-nodejs.png)

The project source includes function code and supporting resources:

- `function` - A Node.js function.
- `template.yml` - An AWS CloudFormation template that creates an application.
- `1-create-bucket.sh`, `2-deploy.sh`, etc. - Shell scripts that use the AWS CLI to deploy and manage the application.

Variants of this sample application are available for the following languages:

- Python – [blank-python](/lambda_functions/blank-python).
- Ruby – [blank-ruby](/lambda_functions/blank-ruby).
- Java – [blank-java](/lambda_functions/blank-java).
- Go – [blank-go](/lambda_functions/blank-go).
- C# – [blank-csharp](/lambda_functions/blank-csharp).
- PowerShell – [blank-powershell](/lambda_functions/blank-powershell).

Use the following instructions to deploy the sample application. For an in-depth look at its architecture and features, see [Blank Function Sample Application for AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/samples-blank-nodejs.html) in the developer guide.

# Requirements
- [Node.js 10 with npm](https://nodejs.org/en/download/releases/)
- The Bash shell. For Linux and macOS, this is included by default. In Windows 10, you can install the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to get a Windows-integrated version of Ubuntu and Bash.
- [The AWS CLI v1](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).

# Setup
Download or clone this repository.

    $ git clone https://github.com/awsdocs/aws-doc-sdk-examples.git
    $ cd aws-doc-sdk-examples/lambda_functions/blank-nodejs

To create a new bucket for deployment artifacts, run `1-create-bucket.sh`.

    blank-nodejs$ ./1-create-bucket.sh
    make_bucket: lambda-artifacts-a5e491dbb5b22e0d

To build a Lambda layer that contains the function's runtime dependencies, run `2-build-layer.sh`. Packaging dependencies in a layer reduces the size of the deployment package that you upload when you modify your code.

    blank-nodejs$ ./2-build-layer.sh

# Deploy
To deploy the application, run `3-deploy.sh`.

    blank-nodejs$ ./3-deploy.sh
    added 16 packages from 18 contributors and audited 18 packages in 0.926s
    added 17 packages from 19 contributors and audited 19 packages in 0.916s
    Uploading to e678bc216e6a0d510d661ca9ae2fd941  2737254 / 2737254.0  (100.00%)
    Successfully packaged artifacts and wrote output template to file out.yml.
    Waiting for changeset to be created..
    Waiting for stack create/update to complete
    Successfully created/updated stack - blank-nodejs

This script uses AWS CloudFormation to deploy the Lambda functions and an IAM role. If the AWS CloudFormation stack that contains the resources already exists, the script updates it with any changes to the template or function code.

# Test
To invoke the function, run `4-invoke.sh`.

    blank-nodejs$ ./4-invoke.sh
    {
        "StatusCode": 200,
        "ExecutedVersion": "$LATEST"
    }
    {"AccountLimit":{"TotalCodeSize":80530636800,"CodeSizeUnzipped":262144000,"CodeSizeZipped":52428800,"ConcurrentExecutions":1000,"UnreservedConcurrentExecutions":933},"AccountUsage":{"TotalCodeSize":303678359,"FunctionCount":75}}

Let the script invoke the function a few times and then press `CRTL+C` to exit.

The application uses AWS X-Ray to trace requests. Open the [X-Ray console](https://console.aws.amazon.com/xray/home#/service-map) to view the service map. The following service map shows the function calling Amazon S3.

![Service Map](/lambda_functions/blank-nodejs/images/blank-nodejs-servicemap.png)

Choose a node in the main function graph. Then choose **View traces** to see a list of traces. Choose any trace to view a timeline that breaks down the work done by the function.

![Trace](/lambda_functions/blank-nodejs/images/blank-nodejs-trace.png)

Finally, view the application in the Lambda console.

*To view the application*
1. Open the [applications page](https://console.aws.amazon.com/lambda/home#/applications) in the Lambda console.
2. Choose **blank-nodejs**.

  ![Application](/lambda_functions/blank-nodejs/images/blank-nodejs-application.png)

# Cleanup
To delete the application, run `5-cleanup.sh`.

    blank-nodejs$ ./5-cleanup.sh
    Deleted blank-nodejs stack.
    Delete deployment artifacts and bucket (lambda-artifacts-4475xmpl08ba7f8d)?y
    delete: s3://lambda-artifacts-4475xmpl08ba7f8d/6f2edcce52085e31a4a5ba823dba2c9d
    delete: s3://lambda-artifacts-4475xmpl08ba7f8d/3d3aee62473d249d039d2d7a37512db3
    remove_bucket: lambda-artifacts-4475xmpl08ba7f8d
    Delete function logs? (log group /aws/lambda/blank-nodejs-function-1RQTXMPLR0YSO)y

The cleanup script delete's the application stack, which includes the function and execution role, and local build artifacts. You can choose to delete the bucket and function logs as well.
