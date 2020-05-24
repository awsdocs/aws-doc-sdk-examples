# Blank function (Ruby)

![Architecture](/lambda_functions/blank-ruby/images/sample-blank-ruby.png)

The project source includes function code and supporting resources:

- `function` - A Ruby function.
- `template.yml` - An AWS CloudFormation template that creates an application.
- `1-create-bucket.sh`, `2-build-layer.sh`, etc. - Shell scripts that use the AWS CLI to deploy and manage the application.

Use the following instructions to deploy the sample application.

# Requirements
- [Ruby 2.5](https://www.ruby-lang.org/en/downloads/)
- The Bash shell. For Linux and macOS, this is included by default. In Windows 10, you can install the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to get a Windows-integrated version of Ubuntu and Bash.
- [The AWS CLI v1](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).

# Setup
Download or clone this repository.

    $ git clone https://github.com/awsdocs/aws-doc-sdk-examples.git
    $ cd aws-doc-sdk-examples/lambda_functions/blank-ruby

To create a new bucket for deployment artifacts, run `1-create-bucket.sh`.

    blank-ruby$ ./1-create-bucket.sh
    make_bucket: lambda-artifacts-a5e491dbb5b22e0d

To build a Lambda layer that contains the function's runtime dependencies, run `2-build-layer.sh`. The script installs Bundler and uses it to install the application's libraries in a folder named `lib`.

    blank-ruby$ ./2-build-layer.sh

The `lib` folder is used to create a Lambda layer during deployment. Packaging dependencies in a layer reduces the size of the deployment package that you upload when you modify your code.

# Deploy
To deploy the application, run `3-deploy.sh`.

    blank-ruby$ ./3-deploy.sh
    Uploading to e678bc216e6a0d510d661ca9ae2fd941  2737254 / 2737254.0  (100.00%)
    Successfully packaged artifacts and wrote output template to file out.yml.
    Waiting for changeset to be created..
    Waiting for stack create/update to complete
    Successfully created/updated stack - blank-ruby

This script uses AWS CloudFormation to deploy the Lambda functions and an IAM role. If the AWS CloudFormation stack that contains the resources already exists, the script updates it with any changes to the template or function code.

# Test
To invoke the function, run `4-invoke.sh`.

    blank-ruby$ ./4-invoke.sh
    {
        "StatusCode": 200,
        "ExecutedVersion": "$LATEST"
    }

Let the script invoke the function a few times and then press `CRTL+C` to exit.

The application uses AWS X-Ray to trace requests. Open the [X-Ray console](https://console.aws.amazon.com/xray/home#/service-map) to view the service map. The following service map shows the function calling Amazon S3.

![Service Map](/lambda_functions/blank-ruby/images/blank-ruby-servicemap.png)

Choose a node in the main function graph. Then choose **View traces** to see a list of traces. Choose any trace to view a timeline that breaks down the work done by the function.

![Trace](/lambda_functions/blank-ruby/images/blank-ruby-trace.png)

# Cleanup
To delete the application, run `5-cleanup.sh`.

    blank-ruby$ ./5-cleanup.sh
