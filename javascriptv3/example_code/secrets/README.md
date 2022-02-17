# Amazon Secrets Manager JavaScript SDK v3 code examples
Amazon Secrets Manager helps you protect secrets needed to access your applications, services, and IT resources. 

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon Secrets Manager examples. 
- [Get secret value](src/secrets_getsecretvalue.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-example-javascript-syntax.html).

## Getting started

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Install the dependencies listed in the package.json.

**Note**: These dependencies include the client module for the AWS services that this example requires, 
which is the *@aws-sdk/client-secrets-manager*.
```
npm install node -g
cd javascriptv3/example_code/secrets 
npm install
```


3. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

4. Run sample code:
```
cd src
node [example name].js // For example, node secrets_getsecretvalue.js
```

## Resources
- [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3)  
- [AWS Secrets Manager User Guide](https://docs.aws.amazon.com/secretsmanager/latest/userguide/manage_retrieve-secret.html) 
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-secrets-manager/index.html) 


