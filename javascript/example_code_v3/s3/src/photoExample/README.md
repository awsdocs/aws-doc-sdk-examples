# Typescript environment for Amazon S3 examples
Environment for AWS SDK for JavaScript (V3) Amazon S3 Photo Album tutorial. For more information, see the [AWS documentation for these examples](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-photo-album.html).

This example demonstrates how to manipulate photos in albums stored in an Amazon S3 bucket.

# Getting Started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) repo to your local environment. See [here](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

1. Install the dependencies listed in the package.json.

**Note**: These include the client modules for the AWS services required in these example, 
which include *@aws-sdk/client-s3*, *@aws-sdk/client-cognito-identity*, and 
*@aws-sdk/credential-provider-cognito-identity*.
```
npm install ts-node -g // if you prefer to use JavaScript, enter 'npm install node -g' instead
cd javascript/example_code_v3/s3/src/photoExample/src
yarn
```

