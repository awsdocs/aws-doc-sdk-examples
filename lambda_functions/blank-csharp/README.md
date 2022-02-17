# Blank function (C#)

![Architecture](/lambda_functions/blank-csharp/images/sample-blank-csharp.png)

The project source includes function code and supporting resources:

- `src/blank-csharp` - A C# .NET Core function.
- `template.yml` - An AWS CloudFormation template that creates an application.
- `1-create-bucket.sh`, `2-deploy.sh`, etc. - Shell scripts that use the AWS CLI to deploy and manage the application.

Use the following instructions to deploy the sample application. For more information on the application's architecture and implementation, see [Managing Spot Instance Requests](https://docs.aws.amazon.com/lambda/latest/dg/services-ec2-tutorial.html) in the developer guide.

# Requirements
- [.NET Core SDK 3.1](https://nodejs.org/en/download/releases/)
- [AWS extensions for .NET CLI](https://github.com/aws/aws-extensions-for-dotnet-cli)
- The Bash shell. For Linux and macOS, this is included by default. In Windows 10, you can install the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to get a Windows-integrated version of Ubuntu and Bash.
- [The AWS CLI v1](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).

# Setup
Download or clone this repository.

    $ git clone https://github.com/awsdocs/aws-doc-sdk-examples.git
    $ cd aws-doc-sdk-examples/lambda_functions/blank-csharp

To create a new bucket for deployment artifacts, run `1-create-bucket.sh`.

    blank-csharp$ ./1-create-bucket.sh
    make_bucket: lambda-artifacts-a5e491dbb5b22e0d

# Deploy
To deploy the application, run `2-deploy.sh`.

    blank-csharp$ ./2-deploy.sh
    Amazon Lambda Tools for .NET Core applications (4.0.0)
    Executing publish command
    Uploading to e678bc216e6a0d510d661ca9ae2fd941  1009985 / 1009985.0  (100.00%)
    Successfully packaged artifacts and wrote output template to file out.yml.
    Waiting for changeset to be created..
    Waiting for stack create/update to complete
    Successfully created/updated stack - blank-csharp

This script uses AWS CloudFormation to deploy the Lambda functions and an IAM role. If the AWS CloudFormation stack that contains the resources already exists, the script updates it with any changes to the template or function code.

To invoke the function, run `3-invoke.sh`.

    blank-csharp$ ./3-invoke.sh
    {
        "StatusCode": 200,
        "ExecutedVersion": "$LATEST"
    }
    {"FunctionCount":43,"TotalCodeSize":362867335}

Let the script invoke the function a few times and then press `CRTL+C` to exit.

The application uses AWS X-Ray to trace requests. Open the [X-Ray console](https://console.aws.amazon.com/xray/home#/service-map) to view the service map. The following service map shows the function managing spot instances in Amazon EC2.

![Service Map](/lambda_functions/blank-csharp/images/blank-csharp-servicemap.png)

Choose a node in the main function graph. Then choose **View traces** to see a list of traces. Choose any trace to view a timeline that breaks down the work done by the function.

![Trace](/lambda_functions/blank-csharp/images/blank-csharp-trace.png)

# Cleanup
To delete the application, run the cleanup script.

    blank-csharp$ ./4-cleanup.sh
