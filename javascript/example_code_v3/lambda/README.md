# Typescript environment for Amazon Lambda samples
Environment for AWS SDK for JavaScript (V3) Amazon Lambda tutorial. For more information, see the [AWS documentation for this tutorial](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-functions.html).

AWS Lambda lets you run code without provisioning or managing servers. You pay only for the compute time you consume.

This is a workspace where you can find working AWS SDK for JavaScript (V3) Lambda samples. 

**NOTE:** The AWS SDK for JavaScript (V3) is written in TypeScript so, for consistency, these examples are also in TypeScript. TypeScript is
a super-set of JavaScript so these examples can also be run as JavaScript.

# Getting Started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) repo to your local environment. See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client modules for the AWS services required in these example, 
which include *@aws-sdk/client-dynamodb*, *@aws-sdk/client-lambda*, *@aws-sdk/client-iam*, 
*@aws-sdk/client-s3*, *@aws-sdk/client-dynamoDB*, *@aws-sdk/client-cognito-identity*, 
*@aws-sdk/credential-provider-cognito-identity*, and *@aws-sdk/client-lambda*.
```
npm install ts-node -g // if you prefer to use JavaScript, enter 'npm install node -g' instead
cd javascript/example_code_v3/lambda
npm install
```
