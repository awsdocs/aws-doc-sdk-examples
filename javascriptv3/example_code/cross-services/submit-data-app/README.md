# TypeScript environment for the SubmitData App tutorial
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) 'Submit data' app tutorial.

The [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-submitting-data.html) contains these examples.

# Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

1. Install the dependencies listed in the [root]/javascriptv3/example_code/cross-services/submit-data-app/package.json.

**Note**: These include the AWS SDK for JavaScript v3 client modules for the AWS services required in this example, 
which are *@aws-sdk/client-cognito-identity*, *@aws-sdk/client-cognito-identity-browser*, *@aws-sdk/client-dynamodb*,
*@aws-sdk/credential-provider-cognito-identity*, *@aws-sdk/client-sns*, *@aws-sdk/client-s3*, and *@aws-sdk/client-iam*.
They also include third-party Node.js modules, fs (file-server), path, and webpack.
```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/cross-services/submit-data-app 
npm install
```

