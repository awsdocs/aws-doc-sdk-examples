# Amazon Rekognition Detect Faces example
Amazon Rekognition makes it easy to add image and video analysis to your applications using  AWS SDK for JavaScript version 3 (v3).

This example demonstrates how to estimate the ages of faces in an photo.

# Running the example

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

**Note**: These include the client module for the AWS services required in these example, 
which are *@aws-sdk/client-cognito-identity*, *@aws-sdk/credential-provider-cognito-identity*, and *@aws-sdk/client-rekognition*, and Webpack for bundling your Node.js and Javascript.

```
npm install node -g
cd javascriptv3/example_code/rekognition/estimate-age-example/src
npm install
```

3. Run the following in the commandline to use the *setup.yaml* AWS CloudFormation template to create the resources for this example:

```
aws cloudformation create-stack --stack-name STACK_NAME --template-body file://setup.yaml --capabilities CAPABILITY_IAM
```

*Note*: The stack name must be unique within an AWS Region and AWS account. You can specify up to 128 characters, and numbers and hyphens are allowed.

4. Open the AWS CloudFormation Console, choose the stack, and choose the  **Resources** tab. 

5. Copy the **Physical ID** of the **CognitoDefaultUnauthenticatedRole**.

6. In the *estimate-age.js* file, replace **IDENTITY_POOL_ID** with the **Physical ID** of the **CognitoDefaultUnauthenticatedRole**.

7. In the *libs/rekognitionClient.js* file, replace **REGION** with your AWS Region.

8. Use Webpack to bundle the Node.js modules required for the example by running the following in the command line:

```
webpack estimate-age.js --mode development --target web --devtool false -o main.js
```

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-rekognition/index.html)
- [Amazon Rekognition Developer Guide - AWS SDK for JavaScript (v2) version of this example](https://docs.aws.amazon.com/rekognition/latest/dg/image-bytes-javascript.html)
